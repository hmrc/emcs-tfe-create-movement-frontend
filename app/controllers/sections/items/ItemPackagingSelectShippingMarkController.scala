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
import forms.sections.items.{ItemPackagingSelectShippingMarkFormProvider, ItemPackagingShippingMarksFormProvider}
import models.requests.DataRequest
import models.{Index, Mode, ShippingMarkOption}
import navigation.ItemsNavigator
import pages.sections.items.{ItemPackagingProductTypePage, ItemPackagingShippingMarksPage, ItemsSection}
import play.api.data.Form
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import services.UserAnswersService
import viewmodels.helpers.SelectItemHelper
import views.html.sections.items.{ItemPackagingSelectShippingMarkView, ItemPackagingShippingMarksView}

import javax.inject.Inject
import scala.concurrent.Future

class ItemPackagingSelectShippingMarkController @Inject()(
                                       override val messagesApi: MessagesApi,
                                       override val userAnswersService: UserAnswersService,
                                       override val betaAllowList: BetaAllowListAction,
                                       override val navigator: ItemsNavigator,
                                       override val auth: AuthAction,
                                       override val getData: DataRetrievalAction,
                                       override val requireData: DataRequiredAction,
                                       formProvider: ItemPackagingSelectShippingMarkFormProvider,
                                       val controllerComponents: MessagesControllerComponents,
                                       view: ItemPackagingSelectShippingMarkView
                                     ) extends BaseItemsNavigationController with AuthActionHelper {

  def onPageLoad(ern: String, draftId: String, itemsIdx: Index, packagingIdx: Index, mode: Mode): Action[AnyContent] =
    authorisedDataRequestAsync(ern, draftId) { implicit request =>
      validatePackagingIndexAsync(itemsIdx, packagingIdx) {
        renderView(Ok, fillForm(ItemPackagingShippingMarksPage(itemsIdx, packagingIdx), formProvider()), itemsIdx, packagingIdx, mode)
      }
    }

  def onSubmit(ern: String, draftId: String, itemsIdx: Index, packagingIdx: Index, mode: Mode): Action[AnyContent] =
    authorisedDataRequestAsync(ern, draftId) { implicit request =>
      validatePackagingIndexAsync(itemsIdx, packagingIdx) {
        formProvider().bindFromRequest().fold(
          renderView(BadRequest, _, itemsIdx, packagingIdx, mode),
          saveAndRedirect(ItemPackagingShippingMarksPage(itemsIdx, packagingIdx), _, mode)
        )
      }
    }


  private def renderView(status: Status, form: Form[_], itemsIndex: Index, packagingIdx: Index, mode: Mode)
                        (implicit request: DataRequest[_]): Future[Result] = {
    val allShippingMarks: Seq[ShippingMarkOption] = ItemsSection.retrieveAllShippingMarks().map(ShippingMarkOption(_))
    val selectItems = SelectItemHelper.constructSelectItems(
      selectOptions = allShippingMarks,
      defaultTextMessageKey = "itemExciseProductCode.select.defaultValue",
      existingAnswer = request.userAnswers.get(ItemPackagingShippingMarksPage(itemsIndex, packagingIdx))
    )
      Future.successful(status(view(
        form = form,
        action = routes.ItemPackagingSelectShippingMarkController.onSubmit(request.ern, request.draftId, itemsIndex, packagingIdx, mode),
        itemIdx = itemsIndex,
        packagingIdx = packagingIdx,
        selectOptions = selectItems
      )))
  }
}
