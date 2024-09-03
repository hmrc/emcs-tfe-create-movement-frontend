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

package controllers.sections.consignee

import controllers.BaseController
import controllers.actions._
import models.NormalMode
import models.sections.info.movementScenario.DestinationType
import navigation.ConsigneeNavigator
import pages.sections.consignee.{CheckAnswersConsigneePage, ConsigneeAddressPage}
import pages.sections.info.DestinationTypePage
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import viewmodels.checkAnswers.sections.consignee.ConsigneeCheckAnswersHelper
import views.html.sections.consignee.CheckYourAnswersConsigneeView

import javax.inject.Inject

class CheckYourAnswersConsigneeController @Inject()(override val messagesApi: MessagesApi,
                                                    override val auth: AuthAction,
                                                                 override val getData: DataRetrievalAction,
                                                    override val requireData: DataRequiredAction,
                                                    val controllerComponents: MessagesControllerComponents,
                                                    val navigator: ConsigneeNavigator,
                                                    val checkYourAnswersConsigneeHelper: ConsigneeCheckAnswersHelper,
                                                    view: CheckYourAnswersConsigneeView
                                                   ) extends BaseController with AuthActionHelper {

  def onPageLoad(ern: String, draftId: String): Action[AnyContent] =
    authorisedDataRequest(ern, draftId) {
      implicit request =>
        withAnswer(ConsigneeAddressPage, controllers.sections.consignee.routes.ConsigneeIndexController.onPageLoad(ern, draftId)) {
          _ =>
            withAnswer(DestinationTypePage) {
              destinationType =>
                Ok(view(
                  controllers.sections.consignee.routes.CheckYourAnswersConsigneeController.onSubmit(ern, draftId),
                  ern,
                  draftId,
                  if (destinationType.destinationType == DestinationType.Export) {
                    Seq(checkYourAnswersConsigneeHelper.summaryList(true), checkYourAnswersConsigneeHelper.summaryList(consigneeReviewBusinessName = true))
                  } else {
                    Seq(checkYourAnswersConsigneeHelper.summaryList())
                  },
                  destinationType.destinationType == DestinationType.Export
                ))
            }
        }
    }


  def onSubmit(ern: String, draftId: String): Action[AnyContent] =
    authorisedDataRequest(ern, draftId) {
      implicit request =>
        Redirect(navigator.nextPage(CheckAnswersConsigneePage, NormalMode, request.userAnswers))
    }

}
