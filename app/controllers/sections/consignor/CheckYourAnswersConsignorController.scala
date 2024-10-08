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

package controllers.sections.consignor

import controllers.BaseController
import controllers.actions._
import models.NormalMode
import navigation.ConsignorNavigator
import pages.sections.consignor.CheckAnswersConsignorPage
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import viewmodels.checkAnswers.sections.consignor.ConsignorCheckAnswersHelper
import views.html.sections.consignor.CheckYourAnswersConsignorView

import javax.inject.Inject

class CheckYourAnswersConsignorController @Inject()(override val messagesApi: MessagesApi,
                                                    override val auth: AuthAction,
                                                    override val getData: DataRetrievalAction,
                                                    override val requireData: DataRequiredAction,
                                                    val controllerComponents: MessagesControllerComponents,
                                                    val navigator: ConsignorNavigator,
                                                    cyaHelper: ConsignorCheckAnswersHelper,
                                                    view: CheckYourAnswersConsignorView
                                                   ) extends BaseController with AuthActionHelper {

  def onPageLoad(ern: String, draftId: String): Action[AnyContent] =
    authorisedDataRequest(ern, draftId) { implicit request =>
      Ok(view(
        cyaHelper.summaryList(),
        controllers.sections.consignor.routes.CheckYourAnswersConsignorController.onSubmit(ern, draftId)
      ))
    }

  def onSubmit(ern: String, draftId: String): Action[AnyContent] =
    authorisedDataRequest(ern, draftId) {
      implicit request =>
        Redirect(navigator.nextPage(CheckAnswersConsignorPage, NormalMode, request.userAnswers))
    }

}
