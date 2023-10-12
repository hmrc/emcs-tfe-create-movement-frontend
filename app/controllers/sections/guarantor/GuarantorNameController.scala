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

package controllers.sections.guarantor

import controllers.BaseNavigationController
import controllers.actions._
import forms.sections.guarantor.GuarantorNameFormProvider
import models.requests.DataRequest
import models.sections.guarantor.GuarantorArranger
import models.sections.guarantor.GuarantorArranger.{GoodsOwner, Transporter}
import models.{Mode, NormalMode}
import navigation.GuarantorNavigator
import pages.GuarantorArrangerPage
import pages.sections.guarantor.GuarantorNamePage
import play.api.data.Form
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import services.UserAnswersService
import views.html.sections.guarantor.GuarantorNameView

import javax.inject.Inject
import scala.concurrent.Future

class GuarantorNameController @Inject()(
                                         override val messagesApi: MessagesApi,
                                         override val userAnswersService: UserAnswersService,
                                         override val navigator: GuarantorNavigator,
                                         override val auth: AuthAction,
                                         override val getData: DataRetrievalAction,
                                         override val requireData: DataRequiredAction,
                                         override val userAllowList: UserAllowListAction,
                                         formProvider: GuarantorNameFormProvider,
                                         val controllerComponents: MessagesControllerComponents,
                                         view: GuarantorNameView
                                       ) extends BaseNavigationController with AuthActionHelper {

  def onPageLoad(ern: String, lrn: String, mode: Mode): Action[AnyContent] =
    authorisedDataRequestAsync(ern, lrn) { implicit request =>
      withGuarantorArrangerAnswer { guarantorArranger =>
        renderView(Ok, fillForm(GuarantorNamePage, formProvider()), guarantorArranger, mode)
      }
    }

  def onSubmit(ern: String, lrn: String, mode: Mode): Action[AnyContent] =
    authorisedDataRequestAsync(ern, lrn) { implicit request =>
      withGuarantorArrangerAnswer { guarantorArranger =>
        formProvider().bindFromRequest().fold(
          formWithErrors =>
            renderView(BadRequest, formWithErrors, guarantorArranger, mode),
          value =>
            saveAndRedirect(GuarantorNamePage, value, mode)
        )
      }
    }

  private def withGuarantorArrangerAnswer(f: GuarantorArranger => Future[Result])(implicit request: DataRequest[_]): Future[Result] = {
    request.userAnswers.get(GuarantorArrangerPage) match {
      case Some(guarantorArranger) if guarantorArranger == GoodsOwner | guarantorArranger == Transporter => f(guarantorArranger)
      case Some(guarantorArranger) =>
        logger.warn(s"[withGuarantorArrangerAnswer] Invalid answer of $guarantorArranger for this controller/page")
        Future.successful(
          Redirect(
            controllers.routes.JourneyRecoveryController.onPageLoad()
          )
        )
      case None =>
        logger.warn(s"[withGuarantorArrangerAnswer] No answer, redirecting to get the answer")
        Future.successful(
          Redirect(
            controllers.sections.guarantor.routes.GuarantorArrangerController.onPageLoad(request.ern, request.lrn, NormalMode)
          )
        )
    }
  }

  private def renderView(status: Status, form: Form[_], guarantorArranger: GuarantorArranger, mode: Mode)(implicit request: DataRequest[_]): Future[Result] = {
    Future.successful(
      status(view(
        form = form,
        guarantorArranger = guarantorArranger,
        mode = mode
      ))
    )
  }
}
