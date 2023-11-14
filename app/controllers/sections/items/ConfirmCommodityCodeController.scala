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

package controllers.sections.items

import controllers.actions._
import models.{Index, Mode}
import navigation.ItemsNavigator
import pages.sections.items.ConfirmCommodityCodePage
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.{GetCnCodeInformationService, UserAnswersService}
import viewmodels.helpers.ConfirmCommodityCodeHelper
import views.html.sections.items.ConfirmCommodityCodeView

import javax.inject.Inject
import scala.concurrent.Future

class ConfirmCommodityCodeController @Inject()(override val messagesApi: MessagesApi,
                                               override val auth: AuthAction,
                                               override val userAllowList: UserAllowListAction,
                                               override val getData: DataRetrievalAction,
                                               override val userAnswersService: UserAnswersService,
                                               override val requireData: DataRequiredAction,
                                               override val cnCodeInformationService: GetCnCodeInformationService,
                                               val controllerComponents: MessagesControllerComponents,
                                               val navigator: ItemsNavigator,
                                               val confirmCommodityCodeHelper: ConfirmCommodityCodeHelper,
                                               view: ConfirmCommodityCodeView
                                              ) extends BaseItemsNavigationController with AuthActionHelper {

  def onPageLoad(ern: String, draftId: String, idx: Index, mode: Mode): Action[AnyContent] =
    authorisedDataRequestAsync(ern, draftId) {
      implicit request =>
        validateIndexAsync(idx) {
          withGoodsTypeAsync(idx) {
            goodsType =>
              Future.successful(Ok(view(
                controllers.sections.items.routes.ConfirmCommodityCodeController.onSubmit(request.ern, request.draftId, idx, mode),
                confirmCommodityCodeHelper.summaryList(idx, goodsType, request.userAnswers)
              )))
          }
        }
    }


  def onSubmit(ern: String, draftId: String, idx: Index, mode: Mode): Action[AnyContent] =
    authorisedDataRequestAsync(ern, draftId) {
      implicit request =>
        validateIndexAsync(idx) {
          Future.successful(Redirect(navigator.nextPage(ConfirmCommodityCodePage(idx), mode, request.userAnswers)))
        }
    }
}
