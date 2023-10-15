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
import forms.sections.guarantor.GuarantorRequiredFormProvider
import models.{Mode, NormalMode}
import navigation.GuarantorNavigator
import pages.sections.guarantor.{GuarantorRequiredPage, GuarantorSection}
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.UserAnswersService
import views.html.sections.guarantor.GuarantorRequiredView

import javax.inject.Inject
import scala.concurrent.Future

class GuarantorRequiredController @Inject()(
                                             override val messagesApi: MessagesApi,
                                             override val userAnswersService: UserAnswersService,
                                             override val userAllowList: UserAllowListAction,
                                             override val navigator: GuarantorNavigator,
                                             override val auth: AuthAction,
                                             override val getData: DataRetrievalAction,
                                             override val requireData: DataRequiredAction,
                                             formProvider: GuarantorRequiredFormProvider,
                                             val controllerComponents: MessagesControllerComponents,
                                             view: GuarantorRequiredView
                                           ) extends BaseNavigationController with AuthActionHelper {

  def onPageLoad(ern: String, lrn: String, mode: Mode): Action[AnyContent] =
    authorisedDataRequest(ern, lrn) { implicit request =>
      Ok(view(fillForm(GuarantorRequiredPage, formProvider()), mode))
    }

  def onSubmit(ern: String, lrn: String, mode: Mode): Action[AnyContent] =
    authorisedDataRequestAsync(ern, lrn) { implicit request =>
      formProvider().bindFromRequest().fold(
        formWithErrors =>
          Future.successful(BadRequest(view(formWithErrors, mode))),
        {
          case value@true =>
            saveAndRedirect(GuarantorRequiredPage, value, mode)
          case value@false =>
            val cleansedSection = request.userAnswers.remove(GuarantorSection)
            saveAndRedirect(GuarantorRequiredPage, value, cleansedSection, NormalMode)
        }
      )
    }

}
