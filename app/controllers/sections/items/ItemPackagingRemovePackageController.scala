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
import forms.sections.items.ItemPackagingRemovePackageFormProvider
import models.Index
import models.requests.DataRequest
import navigation.ItemsNavigator
import pages.sections.items.{ItemPackagingShippingMarksPage, ItemsPackagingSectionItems, ItemsSection}
import play.api.data.Form
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import services.UserAnswersService
import views.html.sections.items.ItemPackagingRemovePackageView

import javax.inject.Inject
import scala.concurrent.Future

class ItemPackagingRemovePackageController @Inject()(
                                                      override val messagesApi: MessagesApi,
                                                      override val userAnswersService: UserAnswersService,
                                                                     override val navigator: ItemsNavigator,
                                                      override val auth: AuthAction,
                                                      override val getData: DataRetrievalAction,
                                                      override val requireData: DataRequiredAction,
                                                      formProvider: ItemPackagingRemovePackageFormProvider,
                                                      val controllerComponents: MessagesControllerComponents,
                                                      view: ItemPackagingRemovePackageView
                                                    ) extends BaseItemsNavigationController with AuthActionHelper {

  def onPageLoad(ern: String, draftId: String, itemsIdx: Index, packagingIdx: Index): Action[AnyContent] =
    authorisedDataRequestAsync(ern, draftId) { implicit request =>
      validatePackagingIndexAsync(itemsIdx, packagingIdx) {
        renderView(Ok, formProvider())(ern, draftId, itemsIdx, packagingIdx)
      }
    }

  def onSubmit(ern: String, draftId: String, itemsIdx: Index, packagingIdx: Index): Action[AnyContent] =
    authorisedDataRequestAsync(ern, draftId) { implicit request =>
      validatePackagingIndexAsync(itemsIdx, packagingIdx) {
        formProvider().bindFromRequest().fold(
          renderView(BadRequest, _)(ern, draftId, itemsIdx, packagingIdx),
          handleAnswerRemovalAndRedirect(_, itemsIdx, packagingIdx)(ern, draftId)
        )
      }
    }

  private def renderView(status: Status, form: Form[_])(ern: String, draftId: String, itemsIdx: Index, packagingIdx: Index)
                        (implicit request: DataRequest[_]): Future[Result] = {
    withItemPackaging(itemsIdx, packagingIdx) { packaging =>
      Future.successful(
        status(view(
          form = form,
          action = routes.ItemPackagingRemovePackageController.onSubmit(ern, draftId, itemsIdx, packagingIdx),
          packaging,
          itemsIdx,
          packagingIdx
        ))
      )
    }
  }

  private def handleAnswerRemovalAndRedirect(shouldRemoveItem: Boolean, itemIdx: Index, packageIdx: Index)(ern: String, draftId: String)
                                            (implicit request: DataRequest[_]): Future[Result] = {
    if (shouldRemoveItem) {

      val existingShippingMark = ItemPackagingShippingMarksPage(itemIdx, packageIdx).value
      val updatedAnswers = request.userAnswers.remove(ItemsPackagingSectionItems(itemIdx, packageIdx))

      val cleansedAnswers =
        (existingShippingMark, ItemsSection.shippingMarkForItemIsUsedOnOtherItems(itemIdx, packageIdx)) match {
          case (Some(mark), true) =>
            ItemsSection.removeAnyPackagingThatMatchesTheShippingMark(mark)(request.copy(userAnswers = updatedAnswers))
          case _ =>
            updatedAnswers
        }

      userAnswersService.set(cleansedAnswers).map {
        _ => Redirect(routes.ItemsPackagingIndexController.onPageLoad(ern, draftId, itemIdx))
      }
    } else {
      Future(Redirect(routes.ItemCheckAnswersController.onPageLoad(ern, draftId, itemIdx)))
    }
  }
}
