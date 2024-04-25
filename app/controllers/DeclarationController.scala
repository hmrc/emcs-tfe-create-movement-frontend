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
import models.response.MissingMandatoryPage
import models.submitCreateMovement.SubmitCreateMovementModel
import navigation.Navigator
import pages.DeclarationPage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import services.{SubmitCreateMovementService, UserAnswersService, ValidationService}
import utils.Logging
import views.html.DeclarationView

import java.time.LocalDateTime
import javax.inject.Inject
import scala.concurrent.Future
import scala.util.{Failure, Success, Try}

class DeclarationController @Inject()(
                                       override val messagesApi: MessagesApi,
                                       override val auth: AuthAction,
                                       override val betaAllowList: BetaAllowListAction,
                                       override val getData: DataRetrievalAction,
                                       override val requireData: DataRequiredAction,
                                       val controllerComponents: MessagesControllerComponents,
                                       val userAnswersService: UserAnswersService,
                                       val navigator: Navigator,
                                       service: SubmitCreateMovementService,
                                       view: DeclarationView,
                                       val validationService: ValidationService,
                                       errorHandler: ErrorHandler
                                     )(implicit appConfig: AppConfig) extends BaseNavigationController with I18nSupport with AuthActionHelper with Logging {

  def onPageLoad(ern: String, draftId: String): Action[AnyContent] =
    authorisedDataRequestAsync(ern, draftId) { implicit request =>
      withSubmitCreateMovementModel { _ =>
        validationService.validate().map { validatedAnswers =>
          if(validatedAnswers.haveAllSubmissionErrorsBeenFixed) {
            Ok(view(submitAction = routes.DeclarationController.onSubmit(ern, draftId)))
          } else {
            logger.info("[onPageLoad] Validation Error was triggered, redirect to task list for User to correct")
            Redirect(controllers.routes.DraftMovementController.onPageLoad(ern, draftId))
          }
        }
      }
    }

  def onSubmit(ern: String, draftId: String): Action[AnyContent] =
    authorisedDataRequestAsync(ern, draftId) { implicit request =>
      withSubmitCreateMovementModel { submitCreateMovementModel =>
        service.submit(submitCreateMovementModel).flatMap {
          response =>
            logger.debug(s"[onSubmit] response received from downstream service ${response.downstreamService}: ${response.receipt}")
            val updatedAnswers = request.userAnswers.copy(hasBeenSubmitted = true, submittedDraftId = Some(response.submittedDraftId))
            saveAndRedirect(DeclarationPage, LocalDateTime.now(), updatedAnswers, NormalMode)
        }.recover {
          case exception =>
            logger.error(s"Error thrown when calling Submit Create Movement: ${exception.getMessage}")
            InternalServerError(errorHandler.internalServerErrorTemplate)
        }
      }
    }

  private def withSubmitCreateMovementModel(onSuccess: SubmitCreateMovementModel => Future[Result])(implicit request: DataRequest[_]): Future[Result] =
    Try(SubmitCreateMovementModel.apply) match {
      case Failure(exception: MissingMandatoryPage) =>
        logger.warn(s"[withSubmitCreateMovementModel] MissingMandatoryPage error thrown: ${exception.message}")
        Future.successful(Redirect(routes.DraftMovementController.onPageLoad(request.ern, request.draftId)))

      //TODO: add in when ETFE-3340 frontend has been merged (impossible to fix error as of: 13/03/24)
      //      case Failure(_: UnfixedSubmissionFailuresException) =>
      //        Future.successful(Redirect(routes.DraftMovementController.onPageLoad(request.ern, request.draftId)))

      case Failure(exception) =>
        logger.error(s"[withSubmitCreateMovementModel] Error thrown when creating request model to submit: ${exception.getMessage}")
        Future.successful(InternalServerError(errorHandler.internalServerErrorTemplate))

      case Success(submitCreateMovementModel) => onSuccess(submitCreateMovementModel)
    }
}
