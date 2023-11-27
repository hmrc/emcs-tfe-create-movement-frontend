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
import forms.sections.items.ItemPackagingProductTypeFormProvider
import models.requests.DataRequest
import models.response.referenceData.ItemPackaging
import models.{Index, Mode}
import navigation.ItemsNavigator
import pages.sections.items.{ItemPackagingProductTypePage, ItemSelectPackagingPage}
import play.api.data.Form
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import services.UserAnswersService
import views.html.sections.items.ItemPackagingProductTypeView

import javax.inject.Inject
import scala.concurrent.Future

class ItemPackagingProductTypeController @Inject()(
                                                    override val messagesApi: MessagesApi,
                                                    override val userAnswersService: UserAnswersService,
                                                    override val userAllowList: UserAllowListAction,
                                                    override val navigator: ItemsNavigator,
                                                    override val auth: AuthAction,
                                                    override val getData: DataRetrievalAction,
                                                    override val requireData: DataRequiredAction,
                                                    formProvider: ItemPackagingProductTypeFormProvider,
                                                    val controllerComponents: MessagesControllerComponents,
                                                    view: ItemPackagingProductTypeView
                                     ) extends BaseItemsNavigationController with AuthActionHelper {

  def onPageLoad(ern: String, draftId: String, itemsIdx: Index, packagingIdx: Index, mode: Mode): Action[AnyContent] =
    authorisedDataRequest(ern, draftId) { implicit request =>
      validatePackagingIndex(itemsIdx, packagingIdx){
        renderView(Ok, fillForm(ItemPackagingProductTypePage(itemsIdx, packagingIdx), formProvider()), itemsIdx, packagingIdx, mode)
      }
    }

  def onSubmit(ern: String, draftId: String, itemsIdx: Index, packagingIdx: Index, mode: Mode): Action[AnyContent] =
    authorisedDataRequestAsync(ern, draftId) { implicit request =>
      validatePackagingIndexAsync(itemsIdx, packagingIdx) {
        formProvider().bindFromRequest().fold(
          formWithErrors =>
            Future.successful(renderView(BadRequest, formWithErrors, itemsIdx, packagingIdx, mode)),
          value =>
            saveAndRedirect(ItemPackagingProductTypePage(itemsIdx, packagingIdx), value, mode)
        )
      }
    }

  def renderView(status: Status, form: Form[_], itemsIdx: Index, packagingIdx: Index, mode: Mode)(implicit request: DataRequest[_]): Result = {
    withAnswer(
      ItemSelectPackagingPage(itemsIdx, packagingIdx),
      routes.ItemsIndexController.onPageLoad(request.ern, request.draftId)
    ) { packageType: ItemPackaging =>
      status(view(
        form = form,
        packageType = packageType,
        onSubmitAction = routes.ItemPackagingProductTypeController.onSubmit(request.ern, request.draftId, itemsIdx, packagingIdx, mode)
      ))
    }
  }


}
