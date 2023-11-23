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
import forms.sections.items.ItemPackagingShippingMarksFormProvider
import models.requests.DataRequest
import models.{Index, Mode}
import navigation.ItemsNavigator
import pages.sections.items.ItemPackagingShippingMarksPage
import play.api.data.Form
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import services.{GetCnCodeInformationService, UserAnswersService}
import views.html.sections.items.ItemPackagingShippingMarksView

import javax.inject.Inject
import scala.concurrent.Future

class ItemPackagingShippingMarksController @Inject()(
                                       override val messagesApi: MessagesApi,
                                       override val userAnswersService: UserAnswersService,
                                       override val userAllowList: UserAllowListAction,
                                       override val navigator: ItemsNavigator,
                                       override val auth: AuthAction,
                                       override val getData: DataRetrievalAction,
                                       override val requireData: DataRequiredAction,
                                       formProvider: ItemPackagingShippingMarksFormProvider,
                                       val controllerComponents: MessagesControllerComponents,
                                       view: ItemPackagingShippingMarksView,
                                       override val cnCodeInformationService: GetCnCodeInformationService
                                     ) extends BaseItemsNavigationController with AuthActionHelper {

  def onPageLoad(ern: String, draftId: String, itemsIndex: Index, packagingIdx: Index, mode: Mode): Action[AnyContent] =
    authorisedDataRequestAsync(ern, draftId) { implicit request =>
      validatePackagingIndexAsync(itemsIndex, packagingIdx) {
        renderView(Ok, fillForm(ItemPackagingShippingMarksPage(itemsIndex, packagingIdx), formProvider()), itemsIndex, packagingIdx, mode)
      }
    }

  def onSubmit(ern: String, draftId: String, itemsIndex: Index, packagingIdx: Index, mode: Mode): Action[AnyContent] =
    authorisedDataRequestAsync(ern, draftId) { implicit request =>
      validatePackagingIndexAsync(itemsIndex, packagingIdx) {
        formProvider().bindFromRequest().fold(
          renderView(BadRequest, _, itemsIndex, packagingIdx, mode),
          saveAndRedirect(ItemPackagingShippingMarksPage(itemsIndex, packagingIdx), _, mode)
        )
      }
    }

  private def renderView(status: Status, form: Form[_], itemsIndex: Index, packagingIdx: Index, mode: Mode)
                        (implicit request: DataRequest[_]): Future[Result] = {
    withItemPackaging(itemsIndex, packagingIdx) { packagingDescription =>
      Future.successful(status(view(
        form = form,
        action = routes.ItemPackagingShippingMarksController.onSubmit(request.ern, request.draftId, itemsIndex, packagingIdx, mode),
        packagingDescription = packagingDescription,
        //TODO: redirect to CAM-ITM28
        skipLink = testOnly.controllers.routes.UnderConstructionController.onPageLoad()
      )))
    }
  }
}
