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
import forms.sections.items.ItemPackagingEnterShippingMarksFormProvider
import models.requests.DataRequest
import models.{Index, Mode, UserAnswers}
import navigation.ItemsNavigator
import pages.sections.items.{ItemPackagingShippingMarksPage, ItemsSection}
import play.api.data.Form
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import services.UserAnswersService
import views.html.sections.items.ItemPackagingEnterShippingMarksView

import javax.inject.Inject
import scala.concurrent.Future

class ItemPackagingEnterShippingMarksController @Inject()(
                                                      override val messagesApi: MessagesApi,
                                                      override val userAnswersService: UserAnswersService,
                                                      override val betaAllowList: BetaAllowListAction,
                                                      override val navigator: ItemsNavigator,
                                                      override val auth: AuthAction,
                                                      override val getData: DataRetrievalAction,
                                                      override val requireData: DataRequiredAction,
                                                      formProvider: ItemPackagingEnterShippingMarksFormProvider,
                                                      val controllerComponents: MessagesControllerComponents,
                                                      view: ItemPackagingEnterShippingMarksView
                                                    ) extends BaseItemsNavigationController with AuthActionHelper {

  def onPageLoad(ern: String, draftId: String, itemsIdx: Index, packagingIdx: Index, mode: Mode): Action[AnyContent] =
    authorisedDataRequestAsync(ern, draftId) { implicit request =>
      validatePackagingIndexAsync(itemsIdx, packagingIdx) {
        renderView(Ok, fillForm(ItemPackagingShippingMarksPage(itemsIdx, packagingIdx), formProvider(itemsIdx, packagingIdx)), itemsIdx, packagingIdx, mode)
      }
    }

  def onSubmit(ern: String, draftId: String, itemsIdx: Index, packagingIdx: Index, mode: Mode): Action[AnyContent] =
    authorisedDataRequestAsync(ern, draftId) { implicit request =>
      validatePackagingIndexAsync(itemsIdx, packagingIdx) {
        formProvider(itemsIdx, packagingIdx).bindFromRequest().fold(
          renderView(BadRequest, _, itemsIdx, packagingIdx, mode),
          value => {
            val newUserAnswers = updateAllShippingMarksToNewValueAndReturnUpdatedUserAnswers(itemsIdx, packagingIdx, value)
            saveAndRedirect(ItemPackagingShippingMarksPage(itemsIdx, packagingIdx), value, newUserAnswers, mode)
          }
        )
      }
    }

  private def renderView(status: Status, form: Form[_], itemsIndex: Index, packagingIdx: Index, mode: Mode)
                        (implicit request: DataRequest[_]): Future[Result] =
    withItemPackaging(itemsIndex, packagingIdx) { packagingDescription =>
      withItemPackagingQuantity(itemsIndex, packagingIdx) { packagingQuantity =>
        Future.successful(status(view(
          form = form,
          action = routes.ItemPackagingEnterShippingMarksController.onSubmit(request.ern, request.draftId, itemsIndex, packagingIdx, mode),
          packagingDescription = packagingDescription,
          packagingQuantity = packagingQuantity,
          packagingIndex = packagingIdx,
          itemIndex = itemsIndex
        )))
      }
    }

  private[items] def updateAllShippingMarksToNewValueAndReturnUpdatedUserAnswers(itemsIdx: Index, packagingIdx: Index, newValue: String)
                                                                             (implicit request: DataRequest[_]): UserAnswers =
    request.userAnswers.get(ItemPackagingShippingMarksPage(itemsIdx, packagingIdx)) match {
      case Some(currentAnswer) => {
        ItemsSection.retrieveShippingMarkLocationsMatching(currentAnswer).foldLeft(request.userAnswers) {
          case (currentUserAnswers, (ii, pi)) =>
            if((ii == itemsIdx) && (pi == packagingIdx)) {
              // if indexes match the page we're on, don't update
              // otherwise saveAndRedirect won't call userAnswersService.set since currentAnswers.get[A](page).contains(answer) == true
              currentUserAnswers
            } else {
              currentUserAnswers.set(ItemPackagingShippingMarksPage(itemsIndex = ii, itemsPackagingIndex = pi), newValue)
            }
        }
      }
      case None => request.userAnswers
    }
}
