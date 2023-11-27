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
import forms.sections.items.ItemPackagingSealChoiceFormProvider
import models.requests.DataRequest
import models.{Index, Mode}
import navigation.ItemsNavigator
import pages.sections.items.ItemPackagingSealChoicePage
import play.api.data.Form
import play.api.i18n.MessagesApi
import play.api.mvc._
import services.UserAnswersService
import views.html.sections.items.ItemPackagingSealChoiceView

import javax.inject.Inject
import scala.concurrent.Future

class ItemPackagingSealChoiceController @Inject()(
                                                   override val messagesApi: MessagesApi,
                                                   override val userAnswersService: UserAnswersService,
                                                   override val userAllowList: UserAllowListAction,
                                                   override val navigator: ItemsNavigator,
                                                   override val auth: AuthAction,
                                                   override val getData: DataRetrievalAction,
                                                   override val requireData: DataRequiredAction,
                                                   formProvider: ItemPackagingSealChoiceFormProvider,
                                                   val controllerComponents: MessagesControllerComponents,
                                                   view: ItemPackagingSealChoiceView
                                                 ) extends BaseItemsNavigationController with AuthActionHelper {

  def onPageLoad(ern: String, draftId: String, itemsIdx: Index, packagingIdx: Index, mode: Mode): Action[AnyContent] =
    authorisedDataRequestAsync(ern, draftId) { implicit request =>
      validatePackagingIndexAsync(itemsIdx, packagingIdx) {
        withItemPackaging(itemsIdx, packagingIdx) { description =>
          renderView(Ok, fillForm(ItemPackagingSealChoicePage(itemsIdx, packagingIdx), formProvider()), itemsIdx, packagingIdx, mode, description)
        }
      }
    }

  def onSubmit(ern: String, draftId: String, itemsIdx: Index, packagingIdx: Index, mode: Mode): Action[AnyContent] =
    authorisedDataRequestAsync(ern, draftId) { implicit request =>
      validatePackagingIndexAsync(itemsIdx, packagingIdx) {
        withItemPackaging(itemsIdx, packagingIdx) { description =>
          formProvider().bindFromRequest().fold(
            renderView(BadRequest, _, itemsIdx, packagingIdx, mode, description),
            saveAndRedirect(ItemPackagingSealChoicePage(itemsIdx, packagingIdx), _, mode)
          )
        }
      }
    }

  private def renderView(status: Status, form: Form[_], itemsIndex: Index, packagingIndex: Index, mode: Mode,
                         description: String)(implicit request: DataRequest[_]): Future[Result] =
    Future.successful(status(view(
      form = form,
      action = controllers.sections.items.routes.ItemPackagingSealChoiceController.onSubmit(request.ern, request.draftId, itemsIndex, packagingIndex, mode),
      packagingDescription = description
    )))
}
