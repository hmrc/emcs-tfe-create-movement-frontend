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
import forms.sections.items.ItemPackagingQuantityFormProvider
import models.requests.DataRequest
import models.{Index, Mode}
import navigation.ItemsNavigator
import pages.sections.items.{ItemPackagingQuantityPage, ItemSelectPackagingPage}
import play.api.data.Form
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import services.UserAnswersService
import views.html.sections.items.ItemPackagingQuantityView

import javax.inject.Inject
import scala.concurrent.Future

class ItemPackagingQuantityController @Inject()(override val messagesApi: MessagesApi,
                                                override val userAnswersService: UserAnswersService,
                                                override val navigator: ItemsNavigator,
                                                override val auth: AuthAction,
                                                override val getData: DataRetrievalAction,
                                                override val requireData: DataRequiredAction,
                                                override val betaAllowList: BetaAllowListAction,
                                                formProvider: ItemPackagingQuantityFormProvider,
                                                override val controllerComponents: MessagesControllerComponents,
                                                view: ItemPackagingQuantityView
                                               ) extends BaseItemsNavigationController with AuthActionHelper {

  def onPageLoad(ern: String, draftId: String, itemsIndex: Index, packagingIdx: Index, mode: Mode): Action[AnyContent] =
    authorisedDataRequestAsync(ern, draftId) {
      implicit request =>
        validatePackagingIndexAsync(itemsIndex, packagingIdx) {
          renderView(
            Ok,
            fillForm(ItemPackagingQuantityPage(itemsIndex, packagingIdx), formProvider(itemsIndex)),
            itemsIndex,
            packagingIdx,
            mode
          )
        }
    }

  def onSubmit(ern: String, draftId: String, itemsIndex: Index, packagingIdx: Index, mode: Mode): Action[AnyContent] =
    authorisedDataRequestAsync(ern, draftId) {
      implicit request =>
        validatePackagingIndexAsync(itemsIndex, packagingIdx) {
          formProvider(itemsIndex).bindFromRequest().fold(
            renderView(BadRequest, _, itemsIndex, packagingIdx, mode),
            saveAndRedirect(ItemPackagingQuantityPage(itemsIndex, packagingIdx), _, mode)
          )
        }
    }

  private def renderView(status: Status,
                         form: Form[_],
                         itemIndex: Index,
                         packagingIndex: Index,
                         mode: Mode
                        )(implicit request: DataRequest[_]): Future[Result] =
    withAnswerAsync(ItemSelectPackagingPage(itemIndex, packagingIndex)) {
      packagingType =>
        Future.successful(
          status(
            view(
              form,
              routes.ItemPackagingQuantityController.onSubmit(request.ern, request.draftId, itemIndex, packagingIndex, mode),
              packagingType,
              packagingIndex = packagingIndex,
              itemIndex = itemIndex
            )
          )
        )
    }

}
