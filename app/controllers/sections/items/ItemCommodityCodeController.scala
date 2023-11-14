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
import forms.sections.items.ItemCommodityCodeFormProvider
import models.UnitOfMeasure.Kilograms
import models.response.referenceData.CnCodeInformation
import models.response.referenceData.CnCodeInformation._
import models.{ExciseProductCode, GoodsTypeModel, Index, Mode}
import navigation.ItemsNavigator
import pages.sections.items.{ItemCommodityCodePage, ItemExciseProductCodePage}
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.{GetCnCodeInformationService, GetCommodityCodesService, UserAnswersService}
import views.html.sections.items.ItemCommodityCodeView

import javax.inject.Inject
import scala.concurrent.Future

class ItemCommodityCodeController @Inject()(
                                             override val messagesApi: MessagesApi,
                                             override val userAnswersService: UserAnswersService,
                                             override val navigator: ItemsNavigator,
                                             override val auth: AuthAction,
                                             override val getData: DataRetrievalAction,
                                             override val requireData: DataRequiredAction,
                                             override val userAllowList: UserAllowListAction,
                                             getCommodityCodesService: GetCommodityCodesService,
                                             formProvider: ItemCommodityCodeFormProvider,
                                             val controllerComponents: MessagesControllerComponents,
                                             view: ItemCommodityCodeView,
                                             override val cnCodeInformationService: GetCnCodeInformationService
                                           ) extends BaseItemsNavigationController with AuthActionHelper {

  def onPageLoad(ern: String, draftId: String, idx: Index, mode: Mode): Action[AnyContent] =
    authorisedDataRequestAsync(ern, draftId) { implicit request =>
      withAnswerAsync(
        ItemExciseProductCodePage(idx),
        redirectRoute = routes.ItemsIndexController.onPageLoad(request.ern, request.draftId)
      ) {
        itemExciseProductCode =>
          getCommodityCodesService.getCommodityCodes(itemExciseProductCode).flatMap {
            case Nil =>{
              val s500Code = CnCodeInformation(CnCodeInformation.defaultCnCode, "", itemExciseProductCode.code, itemExciseProductCode.description, Kilograms)
              saveAndRedirect(ItemCommodityCodePage(idx), s500Code, mode)
            }
            case singleCommodityCode :: Nil =>
              saveAndRedirect(ItemCommodityCodePage(idx), singleCommodityCode, mode)
            case commodityCodes =>
              Future.successful(Ok(
                view(
                  form = request.userAnswers.get(ItemCommodityCodePage(idx)).fold(formProvider())(pageInfo => formProvider().fill(pageInfo.cnCode)),
                  action = routes.ItemCommodityCodeController.onSubmit(request.ern, request.draftId, idx, mode),
                  goodsType = GoodsTypeModel(itemExciseProductCode.code),
                  commodityCodes
                )
              ))
          }
      }
    }

  def onSubmit(ern: String, draftId: String, idx: Index, mode: Mode): Action[AnyContent] =
    authorisedDataRequestAsync(ern, draftId) { implicit request =>
      withAnswerAsync(
        ItemExciseProductCodePage(idx),
        redirectRoute = routes.ItemsIndexController.onPageLoad(request.ern, request.draftId)
      ) {
        itemExciseProductCode =>
          getCommodityCodesService.getCommodityCodes(itemExciseProductCode).flatMap {
            commodityCodes =>
              formProvider().bindFromRequest().fold(
                formWithErrors =>
                  Future.successful(BadRequest(
                    view(
                      form = formWithErrors,
                      action = routes.ItemCommodityCodeController.onSubmit(request.ern, request.draftId, idx, mode),
                      goodsType = GoodsTypeModel(itemExciseProductCode.code),
                      commodityCodes
                    )
                  )),
                value =>{
                  val cnCodeInfo = commodityCodes.filter(_.cnCode == value) match {
                    case singleCommodityCode :: Nil => singleCommodityCode
                    case _ => defaultCnCodeInformation(itemExciseProductCode)
                  }

                  saveAndRedirect(ItemCommodityCodePage(idx), cnCodeInfo, mode)
                }

              )
          }
      }
    }

  private def defaultCnCodeInformation(exciseProductCode: ExciseProductCode) =
    CnCodeInformation(CnCodeInformation.defaultCnCode, "", exciseProductCode.code, exciseProductCode.description, Kilograms)
}
