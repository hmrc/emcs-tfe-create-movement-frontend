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

import config.SessionKeys.DEFERRED_MOVEMENT
import controllers.actions._
import forms.LocalReferenceNumberFormProvider
import models.requests.UserRequest
import models.{NormalMode, UserAnswers}
import navigation.Navigator
import pages.{DeferredMovementPage, LocalReferenceNumberPage}
import play.api.data.Form
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import services.UserAnswersService
import views.html.LocalReferenceNumberView

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
      withDeferredMovementAnswer { isDeferred =>
        renderView(Ok, formProvider(isDeferred), isDeferred)
      }
    }

  def onSubmit(ern: String): Action[AnyContent] =
    (auth(ern) andThen userAllowed).async { implicit request =>
      withDeferredMovementAnswer { isDeferred =>
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
    Future.successful(status(view(isDeferred, form, routes.LocalReferenceNumberController.onSubmit(request.ern))))

  private def withDeferredMovementAnswer(f: Boolean => Future[Result])(implicit request: UserRequest[_]): Future[Result] =
    request.session.get(DEFERRED_MOVEMENT) match {
      case Some(isDeferred) =>
        logger.debug(s"[withIsDeferredMovementAnswer] Deferred Movement answer: '$isDeferred'")
        f(isDeferred.toBoolean)
      case _ =>
        logger.warn(s"[withIsDeferredMovementAnswer] No answer for Deferred Movement question, redirecting to get answer")
        //TODO: Redirect back to the Deferred Movement question to capture answer
        Future.successful(Redirect(testOnly.controllers.routes.UnderConstructionController.onPageLoad()))
    }

  private def createDraftEntryAndRedirect(lrn: String)(implicit request: UserRequest[_]): Future[Result] =
    withDeferredMovementAnswer { isDeferred =>
      val userAnswers = UserAnswers(request.ern, lrn)
        .set(DeferredMovementPage, isDeferred)
        .set(LocalReferenceNumberPage, lrn)
      userAnswersService.set(userAnswers).map { answers =>
        Redirect(navigator.nextPage(LocalReferenceNumberPage, NormalMode, answers))
      }
    }
}
