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

package models.submitCreateMovement

import models.{GoodsType, Index}
import models.requests.DataRequest
import models.response.MissingMandatoryPage
import models.sections.items.ItemNetGrossMassModel
import pages.sections.items._
import play.api.i18n.Messages
import play.api.libs.json.{Json, OFormat}
import queries.ItemsCount
import utils.{Logging, ModelConstructorHelpers}
import viewmodels.helpers.ItemSmallIndependentProducerHelper

case class BodyEadEsadModel(
                             bodyRecordUniqueReference: Int,
                             exciseProductCode: String,
                             cnCode: String,
                             quantity: BigDecimal,
                             grossMass: BigDecimal,
                             netMass: BigDecimal,
                             alcoholicStrengthByVolumeInPercentage: Option[BigDecimal],
                             degreePlato: Option[BigDecimal],
                             fiscalMark: Option[String],
                             fiscalMarkUsedFlag: Option[Boolean],
                             designationOfOrigin: Option[String],
                             sizeOfProducer: Option[BigInt],
                             density: Option[BigDecimal],
                             commercialDescription: Option[String],
                             brandNameOfProducts: Option[String],
                             maturationPeriodOrAgeOfProducts: Option[String],
                             packages: Seq[PackageModel],
                             wineProduct: Option[WineProductModel]
                           )

object BodyEadEsadModel extends ModelConstructorHelpers with Logging {

  def apply(implicit request: DataRequest[_], messages: Messages): Seq[BodyEadEsadModel] = {
    request.userAnswers.get(ItemsCount) match {
      case Some(0) | None =>
        logger.error("ItemsSection should contain at least one item")
        throw MissingMandatoryPage("ItemsSection should contain at least one item")
      case Some(value) =>
        (0 until value)
          .map(Index(_))
          .map {
            idx =>
              val exciseProductCode: String = mandatoryPage(ItemExciseProductCodePage(idx))
              val netGrossMass: ItemNetGrossMassModel = mandatoryPage(ItemNetGrossMassPage(idx))

              val designationOfOrigin: Option[String] = {
                // TODO: review this
                (request.userAnswers.get(ItemGeographicalIndicationPage(idx)), request.userAnswers.get(ItemSmallIndependentProducerPage(idx))) match {
                  case (Some(value), _) => Some(value)
                  case (_, Some(true)) => Some(ItemSmallIndependentProducerHelper.yesMessageFor(GoodsType(exciseProductCode)))
                  case _ => None
                }
              }

              val packagingIsBulk = mandatoryPage(ItemBulkPackagingChoicePage(idx))

              BodyEadEsadModel(
                bodyRecordUniqueReference = idx.position,
                exciseProductCode = exciseProductCode,
                cnCode = mandatoryPage(ItemCommodityCodePage(idx)),
                quantity = mandatoryPage(ItemQuantityPage(idx)),
                grossMass = netGrossMass.grossMass,
                netMass = netGrossMass.netMass,
                alcoholicStrengthByVolumeInPercentage = request.userAnswers.get(ItemAlcoholStrengthPage(idx)),
                degreePlato = request.userAnswers.get(ItemDegreesPlatoPage(idx)).flatMap(_.degreesPlato),
                fiscalMark = request.userAnswers.get(ItemFiscalMarksPage(idx)),
                fiscalMarkUsedFlag = request.userAnswers.get(ItemFiscalMarksChoicePage(idx)),
                designationOfOrigin = designationOfOrigin,
                sizeOfProducer = request.userAnswers.get(ItemProducerSizePage(idx)),
                density = request.userAnswers.get(ItemDensityPage(idx)),
                commercialDescription = request.userAnswers.get(ItemCommercialDescriptionPage(idx)),
                brandNameOfProducts = mandatoryPage(ItemBrandNamePage(idx)).brandName,
                maturationPeriodOrAgeOfProducts = request.userAnswers.get(ItemMaturationPeriodAgePage(idx)).flatMap(_.maturationPeriodAge),
                packages = if(packagingIsBulk) PackageModel.applyBulkPackaging(idx) else PackageModel.applyIndividualPackaging(idx),
                wineProduct = WineProductModel.apply(idx)
              )
          }
    }
  }

  implicit val fmt: OFormat[BodyEadEsadModel] = Json.format
}
