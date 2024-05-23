/*
 * Copyright 2024 HM Revenue & Customs
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
import forms.sections.items.ItemPackagingShippingMarksChoiceFormProvider
import models.requests.DataRequest
import models.{Index, Mode}
import navigation.ItemsNavigator
import pages.sections.items.{ItemPackagingShippingMarksChoicePage, ItemPackagingShippingMarksPage, ItemsSection}
import play.api.data.Form
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import services.UserAnswersService
import views.html.sections.items.ItemPackagingShippingMarksChoiceView

import javax.inject.Inject
import scala.concurrent.Future

class ItemPackagingShippingMarksChoiceController @Inject()(
                                                            override val messagesApi: MessagesApi,
                                                            override val userAnswersService: UserAnswersService,
                                                            override val betaAllowList: BetaAllowListAction,
                                                            override val navigator: ItemsNavigator,
                                                            override val auth: AuthAction,
                                                            override val getData: DataRetrievalAction,
                                                            override val requireData: DataRequiredAction,
                                                            formProvider: ItemPackagingShippingMarksChoiceFormProvider,
                                                            val controllerComponents: MessagesControllerComponents,
                                                            view: ItemPackagingShippingMarksChoiceView
                                                          ) extends BaseItemsNavigationController with AuthActionHelper {

  def onPageLoad(ern: String, draftId: String, itemsIdx: Index, packagingIdx: Index, mode: Mode): Action[AnyContent] =
    authorisedDataRequestAsync(ern, draftId) { implicit request =>
      withItemPackagingQuantity(itemsIdx, packagingIdx) { quantity =>
        renderView(Ok, fillForm(ItemPackagingShippingMarksChoicePage(itemsIdx, packagingIdx), formProvider()), itemsIdx, packagingIdx, quantity, mode)
      }
    }

  def onSubmit(ern: String, draftId: String, itemsIdx: Index, packagingIdx: Index, mode: Mode): Action[AnyContent] =
    authorisedDataRequestAsync(ern, draftId) { implicit request =>
      withItemPackagingQuantity(itemsIdx, packagingIdx) { quantity =>
        formProvider().bindFromRequest().fold(
          renderView(BadRequest, _, itemsIdx, packagingIdx, quantity, mode),
          cleanseSaveAndRedirect(_, itemsIdx, packagingIdx, mode)
        )
      }
    }

  private def renderView(status: Status, form: Form[_], itemsIdx: Index, packagingIdx: Index, quantity: String, mode: Mode)
                        (implicit request: DataRequest[_]): Future[Result] = {
    withItemPackaging(itemsIdx, packagingIdx) { description =>
      Future.successful(status(view(
        form,
        itemsIdx,
        packagingIdx,
        description,
        quantity,
        routes.ItemPackagingShippingMarksChoiceController.onSubmit(request.ern, request.draftId, itemsIdx, packagingIdx, mode),
        mode
      )))
    }
  }

  private def cleanseSaveAndRedirect(hasShippingMark: Boolean, itemIdx: Index, packageIdx: Index, mode: Mode)
                                    (implicit request: DataRequest[_]): Future[Result] = {
    val cleansedAnswers = if (hasShippingMark) request.userAnswers else {
      ItemsSection
        .removeAnyPackagingThatMatchesTheShippingMark(itemIdx, packageIdx)
        .remove(ItemPackagingShippingMarksPage(itemIdx, packageIdx))
    }
    saveAndRedirect(ItemPackagingShippingMarksChoicePage(itemIdx, packageIdx), hasShippingMark, cleansedAnswers, mode)
  }
}
