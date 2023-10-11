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

package controllers.sections.info

import config.SessionKeys.{DEFERRED_MOVEMENT, DESTINATION_TYPE}
import controllers.BaseNavigationController
import controllers.actions._
import forms.LocalReferenceNumberFormProvider
import models.requests.UserRequest
import models.sections.info.movementScenario.MovementScenario
import models.{NormalMode, UserAnswers}
import navigation.Navigator
import pages.sections.info.{DeferredMovementPage, DestinationTypePage, LocalReferenceNumberPage}
import play.api.data.Form
import play.api.i18n.MessagesApi
import play.api.libs.json.JsString
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import services.UserAnswersService
import views.html.sections.info.LocalReferenceNumberView

import javax.inject.Inject
import scala.concurrent.Future

class LocalReferenceNumberController @Inject()(
                                                override val messagesApi: MessagesApi,
                                                override val userAnswersService: UserAnswersService,
                                                override val navigator: Navigator,
                                                val auth: AuthAction,
                                                val userAllowed: UserAllowListAction,
                                                val getData: DataRetrievalAction,
                                                formProvider: LocalReferenceNumberFormProvider,
                                                val controllerComponents: MessagesControllerComponents,
                                                view: LocalReferenceNumberView
                                              ) extends BaseNavigationController {

  def onPageLoad(ern: String): Action[AnyContent] =
    (auth(ern) andThen userAllowed).async { implicit request =>
      withGuard {
        case (_, isDeferred) =>
          renderView(Ok, formProvider(isDeferred), isDeferred)
      }
    }

  def onSubmit(ern: String): Action[AnyContent] =
    (auth(ern) andThen userAllowed).async { implicit request =>
      withGuard {
        case (_, isDeferred) =>
          formProvider(isDeferred).bindFromRequest().fold(
            renderView(BadRequest, _, isDeferred),
            lrn =>
              userAnswersService.get(ern, lrn).flatMap {
                case Some(_) =>
                  //TODO: Redirect to LRN already exists page when designed and built
                  logger.debug("[onSubmit] Draft already exists with this LRN")
                  Future.successful(Redirect(testOnly.controllers.routes.UnderConstructionController.onPageLoad()))
                case _ =>
                  logger.debug("[onSubmit] No Draft exists with this LRN, creating a new draft entry in the UserAnswers repo")
                  createDraftEntryAndRedirect(lrn)
              }
          )
      }
    }

  private def renderView(status: Status, form: Form[_], isDeferred: Boolean)(implicit request: UserRequest[_]): Future[Result] =
    Future.successful(status(view(isDeferred, form, controllers.sections.info.routes.LocalReferenceNumberController.onSubmit(request.ern))))

  private def withDeferredMovementAnswer(f: Boolean => Future[Result])(implicit request: UserRequest[_]): Future[Result] =
    request.session.get(DEFERRED_MOVEMENT) match {
      case Some(isDeferred) =>
        logger.debug(s"[withIsDeferredMovementAnswer] Deferred Movement answer: '$isDeferred'")
        f(isDeferred.toBoolean)
      case _ =>
        logger.warn(s"[withIsDeferredMovementAnswer] No answer for Deferred Movement question, redirecting to get answer")
        Future.successful(Redirect(controllers.sections.info.routes.DeferredMovementController.onPageLoad(request.ern)))
    }

  private def withDestinationTypePageAnswer(f: MovementScenario => Future[Result])(implicit request: UserRequest[_]): Future[Result] =
    request.session.get(DESTINATION_TYPE) match {
      case Some(destinationTypeAnswer) =>
        logger.debug(s"[withDestinationTypeAnswer] Destination Type answer: '$destinationTypeAnswer'")

        JsString(destinationTypeAnswer).asOpt[MovementScenario] match {
          case Some(movementScenario) => f(movementScenario)
          case _ =>
            logger.warn(s"[withDestinationTypeAnswer] Unable to parse answer for Destination Type question, redirecting to get answer")
            Future.successful(Redirect(controllers.sections.info.routes.DestinationTypeController.onPageLoad(request.ern)))
        }
      case _ =>
        logger.warn(s"[withDestinationTypeAnswer] No answer for Destination Type question, redirecting to get answer")
        Future.successful(Redirect(controllers.sections.info.routes.DestinationTypeController.onPageLoad(request.ern)))
    }

  private def createDraftEntryAndRedirect(lrn: String)(implicit request: UserRequest[_]): Future[Result] =
    withGuard {
      case (destinationTypePageAnswer, isDeferred) =>
        val userAnswers = UserAnswers(request.ern, lrn)
          .set(DestinationTypePage, destinationTypePageAnswer)
          .set(DeferredMovementPage, isDeferred)
          .set(LocalReferenceNumberPage, lrn)

        userAnswersService.set(userAnswers).map { answers =>
          Redirect(navigator.nextPage(LocalReferenceNumberPage, NormalMode, answers))
        }
    }

  private def withGuard(f: (MovementScenario, Boolean) => Future[Result])(implicit request: UserRequest[_]): Future[Result] = {
    withDestinationTypePageAnswer { destinationTypePageAnswer =>
      withDeferredMovementAnswer { isDeferred =>
        f(destinationTypePageAnswer, isDeferred)
      }
    }
  }
}
