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

import config.Constants.NONGBVAT
import controllers.actions._
import forms.sections.guarantor.GuarantorVatFormProvider
import models.requests.DataRequest
import models.sections.guarantor.GuarantorArranger
import models.{Mode, NormalMode}
import navigation.GuarantorNavigator
import pages.sections.guarantor.GuarantorVatPage
import play.api.data.Form
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import services.UserAnswersService
import views.html.sections.guarantor.GuarantorVatView

import javax.inject.Inject
import scala.concurrent.Future

class GuarantorVatController @Inject()(
                                        override val messagesApi: MessagesApi,
                                        override val userAnswersService: UserAnswersService,
                                        override val navigator: GuarantorNavigator,
                                        override val auth: AuthAction,
                                        override val getData: DataRetrievalAction,
                                        override val requireData: DataRequiredAction,
                                        override val userAllowList: UserAllowListAction,
                                        formProvider: GuarantorVatFormProvider,
                                        val controllerComponents: MessagesControllerComponents,
                                        view: GuarantorVatView
                                      ) extends GuarantorBaseController with AuthActionHelper {

  def onPageLoad(ern: String, lrn: String, mode: Mode): Action[AnyContent] =
    authorisedDataRequestAsync(ern, lrn) { implicit request =>
      withGuarantorArrangerAnswer { guarantorArranger =>
        renderView(Ok, fillForm(GuarantorVatPage, formProvider()), guarantorArranger, mode)
      }
    }

  def onSubmit(ern: String, lrn: String, mode: Mode): Action[AnyContent] =
    authorisedDataRequestAsync(ern, lrn) { implicit request =>
      withGuarantorArrangerAnswer { guarantorArranger =>
        formProvider().bindFromRequest().fold(
          formWithErrors =>
            renderView(BadRequest, formWithErrors, guarantorArranger, mode),
          value =>
            saveAndRedirect(GuarantorVatPage, value, mode)
        )
      }
    }

  def onNonGbVAT(ern: String, lrn: String): Action[AnyContent] =
    authorisedDataRequestAsync(ern, lrn) { implicit request =>
      saveAndRedirect(GuarantorVatPage, NONGBVAT, NormalMode)
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