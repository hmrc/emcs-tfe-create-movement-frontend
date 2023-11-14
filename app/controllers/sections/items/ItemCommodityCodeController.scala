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

import controllers.BaseNavigationController
import controllers.actions._
import forms.sections.items.ItemCommodityCodeFormProvider
import models.requests.DataRequest

import javax.inject.Inject
import models.{Index, Mode}
import navigation.{ItemsNavigator, Navigator}
import pages.sections.items.ItemCommodityCodePage
import play.api.data.Form
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import play.twirl.api.Html
import services.UserAnswersService
import views.html.sections.items.ItemCommodityCodeView

import scala.concurrent.Future

class ItemCommodityCodeController @Inject()(
                                             override val messagesApi: MessagesApi,
                                             override val userAnswersService: UserAnswersService,
                                             override val navigator: ItemsNavigator,
                                             override val auth: AuthAction,
                                             override val getData: DataRetrievalAction,
                                             override val requireData: DataRequiredAction,
                                             override val userAllowList: UserAllowListAction,
                                             formProvider: ItemCommodityCodeFormProvider,
                                             val controllerComponents: MessagesControllerComponents,
                                             view: ItemCommodityCodeView
                                           ) extends BaseItemsNavigationController with AuthActionHelper {

  def onPageLoad(ern: String, draftId: String, idx: Index, mode: Mode): Action[AnyContent] =
    authorisedDataRequest(ern, draftId) { implicit request =>
      Ok(view(
        form = fillForm(ItemCommodityCodePage(idx), formProvider()),
        action = routes.ItemCommodityCodeController.onSubmit(request.ern, request.draftId, idx, mode),
        goodsType = getGoodsType(idx)
      ))
    }

  def onSubmit(ern: String, draftId: String, idx: Index, mode: Mode): Action[AnyContent] =
    authorisedDataRequestAsync(ern, draftId) { implicit request =>
      formProvider().bindFromRequest().fold(
        formWithErrors => Future.successful(BadRequest(
          view(
            form = formWithErrors,
            action = routes.ItemCommodityCodeController.onSubmit(request.ern, request.draftId, idx, mode),
            goodsType = getGoodsType(idx)
          )
        )),
        value => saveAndRedirect(ItemCommodityCodePage(idx), value, mode)
      )
    }
}
