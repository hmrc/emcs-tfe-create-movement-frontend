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
import forms.sections.items.ItemBulkPackagingSelectFormProvider
import models.GoodsType
import models.requests.DataRequest
import models.response.referenceData.BulkPackagingType
import models.sections.items.ItemBulkPackagingCode
import models.{Index, Mode}
import navigation.ItemsNavigator
import pages.sections.items.ItemBulkPackagingSelectPage
import play.api.data.Form
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import services.{GetPackagingTypesService, UserAnswersService}
import views.html.sections.items.ItemBulkPackagingSelectView

import javax.inject.Inject
import scala.concurrent.Future

class ItemBulkPackagingSelectController @Inject()(
                                                   override val messagesApi: MessagesApi,
                                                   override val userAnswersService: UserAnswersService,
                                                   override val userAllowList: UserAllowListAction,
                                                   override val navigator: ItemsNavigator,
                                                   override val auth: AuthAction,
                                                   override val getData: DataRetrievalAction,
                                                   override val requireData: DataRequiredAction,
                                                   formProvider: ItemBulkPackagingSelectFormProvider,
                                                   getPackagingTypesService: GetPackagingTypesService,
                                                   val controllerComponents: MessagesControllerComponents,
                                                   view: ItemBulkPackagingSelectView
                                                 ) extends BaseItemsNavigationController with AuthActionHelper {

  def onPageLoad(ern: String, draftId: String, idx: Index, mode: Mode): Action[AnyContent] =
    authorisedDataRequestAsync(ern, draftId) { implicit request =>
      validateIndexAsync(idx) {
        withGoodsTypeAsync(idx) { goodsType =>
          getPackagingTypesService.getBulkPackagingTypes(ItemBulkPackagingCode.values).flatMap { bulkPackagingTypes =>
            renderView(Ok, fillForm(ItemBulkPackagingSelectPage(idx), formProvider(goodsType, bulkPackagingTypes)), idx,
              goodsType, bulkPackagingTypes, mode)
          }
        }
      }
    }

  def onSubmit(ern: String, draftId: String, idx: Index, mode: Mode): Action[AnyContent] =
    authorisedDataRequestAsync(ern, draftId) { implicit request =>
      validateIndexAsync(idx) {
        withGoodsTypeAsync(idx) { goodsType =>
          getPackagingTypesService.getBulkPackagingTypes(ItemBulkPackagingCode.values).flatMap { bulkPackagingTypes =>
            formProvider(goodsType, bulkPackagingTypes).bindFromRequest().fold(
              renderView(BadRequest, _, idx, goodsType, bulkPackagingTypes, mode),
              saveAndRedirect(ItemBulkPackagingSelectPage(idx), _, mode)
            )
          }
        }
      }
    }

  private def renderView(status: Status, form: Form[_], idx: Index, goodsType: GoodsType, bulkPackagingTypes: Seq[BulkPackagingType], mode: Mode)
                        (implicit request: DataRequest[_]): Future[Result] = {
    val radioOptions = BulkPackagingType.options(bulkPackagingTypes)
    Future.successful(status(view(
      form = form,
      action = routes.ItemBulkPackagingSelectController.onSubmit(request.ern, request.draftId, idx, mode),
      options = radioOptions,
      goodsType = goodsType
    )))
  }
}
