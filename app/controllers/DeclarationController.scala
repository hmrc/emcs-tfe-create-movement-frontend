/*
 * Copyright 2023 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package controllers

import config.AppConfig
import controllers.actions._
import handlers.ErrorHandler
import models.NormalMode
import models.requests.DataRequest
import models.response.{MissingMandatoryPage, SubmitCreateMovementException, UnexpectedDownstreamDraftSubmissionResponseError}
import models.submitCreateMovement.SubmitCreateMovementModel
import navigation.Navigator
import pages.DeclarationPage
import pages.sections.AllSections
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import services.{MovementTemplatesService, SubmitCreateMovementService, UserAnswersService, ValidationService}
import utils.Logging
import views.html.DeclarationView

import java.time.LocalDateTime
import javax.inject.Inject
import scala.concurrent.Future
import scala.util.{Failure, Success, Try}

class DeclarationController @Inject()(
                                       override val messagesApi: MessagesApi,
                                       override val auth: AuthAction,
                                       override val getData: DataRetrievalAction,
                                       override val requireData: DataRequiredAction,
                                       val controllerComponents: MessagesControllerComponents,
                                       val userAnswersService: UserAnswersService,
                                       val navigator: Navigator,
                                       service: SubmitCreateMovementService,
                                       view: DeclarationView,
                                       val validationService: ValidationService,
                                       movementTemplatesService: MovementTemplatesService,
                                       errorHandler: ErrorHandler
                                     )(implicit appConfig: AppConfig) extends BaseNavigationController with I18nSupport with AuthActionHelper with Logging {

  def onPageLoad(ern: String, draftId: String): Action[AnyContent] =
    authorisedDataRequestAsync(ern, draftId) { implicit request =>
      withSubmitCreateMovementModel { _ =>
        validationService.validate().flatMap { validatedAnswers =>
          if (AllSections.isCompleted(request.copy(userAnswers = validatedAnswers))) {
            movementTemplatesService.getList(ern).map { templates =>
              Ok(view(
                submitAction = routes.DeclarationController.onSubmit(ern, draftId),
                templates.count
              ))
            }
          } else {
            logger.info("[onPageLoad] Validation Error was triggered, redirect to task list for User to correct")
            Future.successful(Redirect(controllers.routes.DraftMovementController.onPageLoad(ern, draftId)))
          }
        }
      }
    }

  def onSubmit(ern: String, draftId: String): Action[AnyContent] =
    authorisedDataRequestAsync(ern, draftId) { implicit request =>
      withSubmitCreateMovementModel { submitCreateMovementModel =>
        service.submit(submitCreateMovementModel, ern).flatMap {
          case Right(response) =>
            logger.debug(s"[onSubmit] response received from downstream service ${response.downstreamService}: ${response.receipt}")
            val updatedAnswers = request.userAnswers.copy(hasBeenSubmitted = true, submittedDraftId = Some(response.submittedDraftId))
            saveAndRedirect(DeclarationPage, LocalDateTime.now(), updatedAnswers, NormalMode)
          case Left(UnexpectedDownstreamDraftSubmissionResponseError(UNPROCESSABLE_ENTITY)) =>
            // emcs-tfe returns an UNPROCESSABLE_ENTITY when downstream responds with RIM validation errors,
            // so we redirect to DraftMovementController to display these errors
            logger.warn(s"Received UnexpectedDownstreamDraftSubmissionResponseError(UNPROCESSABLE_ENTITY) from SubmitCreateMovementService, " +
              s"redirecting to DraftMovementController")
            Future.successful(Redirect(routes.DraftMovementController.onPageLoad(ern, draftId)))
          case Left(value) =>
            logger.warn(s"Received Left from SubmitCreateMovementService: $value")
            throw SubmitCreateMovementException(s"Failed to submit Create Movement to emcs-tfe for ern: '${request.ern}' & draftId: '${request.draftId}'")
        }.recoverWith {
          case exception =>
            logger.error(s"Error thrown when calling Submit Create Movement: ${exception.getMessage}")
            errorHandler.internalServerErrorTemplate.map(res => InternalServerError(res))
        }
      }
    }

  private def withSubmitCreateMovementModel(onSuccess: SubmitCreateMovementModel => Future[Result])(implicit request: DataRequest[_]): Future[Result] =
    Try(SubmitCreateMovementModel.apply) match {
      case Failure(exception: MissingMandatoryPage) =>
        logger.warn(s"[withSubmitCreateMovementModel] MissingMandatoryPage error thrown: ${exception.message}")
        Future.successful(Redirect(routes.DraftMovementController.onPageLoad(request.ern, request.draftId)))

      case Failure(exception) =>
        logger.error(s"[withSubmitCreateMovementModel] Error thrown when creating request model to submit: ${exception.getMessage}")
        errorHandler.internalServerErrorTemplate.map(res => InternalServerError(res))

      case Success(submitCreateMovementModel) => onSuccess(submitCreateMovementModel)
    }
}
