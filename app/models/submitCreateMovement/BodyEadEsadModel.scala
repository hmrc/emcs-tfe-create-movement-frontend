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

import models.Index
import models.requests.DataRequest
import models.response.MissingMandatoryPage
import models.sections.items.ItemGeographicalIndicationType.NoGeographicalIndication
import models.sections.items.ItemSmallIndependentProducerType.NotProvided
import models.sections.items.{ItemDesignationOfOriginModel, ItemNetGrossMassModel, ItemSmallIndependentProducerModel, ItemSmallIndependentProducerType}
import pages.sections.items._
import play.api.i18n.Messages
import play.api.libs.functional.syntax.{toFunctionalBuilderOps, unlift}
import play.api.libs.json.{Json, OFormat, OWrites, __}
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
                             independentSmallProducersDeclaration: Option[String],
                             packages: Seq[PackageModel],
                             wineProduct: Option[WineProductModel]
                           )

object BodyEadEsadModel extends ModelConstructorHelpers with Logging {

  private[submitCreateMovement] def smallIndependentProducerAnswer(idx: Index, smallIndependentProducer: ItemSmallIndependentProducerModel)
                                                                  (implicit request: DataRequest[_], messages: Messages): String = {
    val declaration = ItemSmallIndependentProducerHelper.constructDeclarationPrefix(idx).dropRight(1)

    val answer = smallIndependentProducer.producerType match {
      case NotProvided => messages(s"itemSmallIndependentProducer.$NotProvided.downstream")
      case _ => messages(s"itemSmallIndependentProducer.${smallIndependentProducer.producerType}")
    }

    val optSeedNumber = smallIndependentProducer.producerId.map(messages("itemSmallIndependentProducer.cya.identification", _))

    Seq(
      Option.when(!ItemSmallIndependentProducerType.notProvidedValues.contains(smallIndependentProducer.producerType))(declaration),
      Some(answer),
      optSeedNumber
    ).flatten.mkString(". ")
  }

  private[submitCreateMovement] def designationOfOriginAnswer(answer: ItemDesignationOfOriginModel)(implicit messages: Messages): String = {
    val marketingAndLabellingAnswer = answer.isSpiritMarketedAndLabelled.map(isSpiritMarketedAndLabelled =>
      if(isSpiritMarketedAndLabelled) "itemDesignationOfOrigin.s200.radio.yes" else "itemDesignationOfOrigin.s200.radio.unprovided.downstream"
    )

    val designationOfOriginSelection = if(answer.geographicalIndication == NoGeographicalIndication) {
      "itemDesignationOfOrigin.None.downstream"
    } else {
      s"itemDesignationOfOrigin.${answer.geographicalIndication}"
    }

    Seq(
      Some(messages(designationOfOriginSelection)),
      answer.geographicalIndicationIdentification,
      marketingAndLabellingAnswer.map(messages(_))
    ).flatten.mkString(". ")
  }

  private[submitCreateMovement] def designationOfOrigin(idx: Index)(implicit request: DataRequest[_], messages: Messages): Option[String] =
    ItemDesignationOfOriginPage(idx).value match {
      case Some(designationOfOrigin) => Some(designationOfOriginAnswer(answer = designationOfOrigin))
      case _ => None
    }

  private[submitCreateMovement] def smallIndependentProducer(idx: Index)(implicit request: DataRequest[_], messages: Messages): Option[String] =
    ItemSmallIndependentProducerPage(idx).value match {
      case Some(smallIndependentProducer) => Some(smallIndependentProducerAnswer(idx, smallIndependentProducer))
      case _ => None
    }

  def apply(implicit request: DataRequest[_], messages: Messages): Seq[BodyEadEsadModel] = {
    request.userAnswers.getCount(ItemsCount) match {
      case Some(0) | None =>
        logger.error("ItemsSection should contain at least one item")
        throw MissingMandatoryPage("ItemsSection should contain at least one item")
      case Some(value) =>
        (0 until value)
          .map(Index(_))
          .map {
            idx =>
              val exciseProductCode: String = mandatoryPage(ItemExciseProductCodePage(idx))
              val commodityCode: String = mandatoryPage(ItemCommodityCodePage(idx))
              val netGrossMass: ItemNetGrossMassModel = mandatoryPage(ItemNetGrossMassPage(idx))

              val packagingIsBulk = mandatoryPage(ItemBulkPackagingChoicePage(idx))

              BodyEadEsadModel(
                bodyRecordUniqueReference = idx.position + 1, // "0" is an invalid value, so assumption is this is 1-indexed
                exciseProductCode = exciseProductCode,
                cnCode = commodityCode,
                quantity = mandatoryPage(ItemQuantityPage(idx)),
                grossMass = netGrossMass.grossMass,
                netMass = netGrossMass.netMass,
                alcoholicStrengthByVolumeInPercentage = ItemAlcoholStrengthPage(idx).value,
                degreePlato = ItemDegreesPlatoPage(idx).value.flatMap(_.degreesPlato),
                fiscalMark = ItemFiscalMarksPage(idx).value,
                fiscalMarkUsedFlag = ItemFiscalMarksChoicePage(idx).value,
                designationOfOrigin = designationOfOrigin(idx),
                independentSmallProducersDeclaration = smallIndependentProducer(idx),
                sizeOfProducer = ItemProducerSizePage(idx).value,
                density = ItemDensityPage(idx).value,
                commercialDescription = ItemCommercialDescriptionPage(idx).value,
                brandNameOfProducts = mandatoryPage(ItemBrandNamePage(idx)).brandName,
                maturationPeriodOrAgeOfProducts = ItemMaturationPeriodAgePage(idx).value.flatMap(_.maturationPeriodAge),
                packages = if (packagingIsBulk) PackageModel.applyBulkPackaging(idx) else PackageModel.applyIndividualPackaging(idx),
                wineProduct = WineProductModel.applyAtIdx(idx)
              )
          }
    }
  }

  implicit val fmt: OFormat[BodyEadEsadModel] = Json.format

  val auditWrites: OWrites[BodyEadEsadModel] = (
    (__ \ "bodyRecordUniqueReference").write[Int] and
    (__ \ "exciseProductCode").write[String] and
    (__ \ "cnCode").write[String] and
    (__ \ "quantity").write[BigDecimal] and
    (__ \ "grossMass").write[BigDecimal] and
    (__ \ "netMass").write[BigDecimal] and
    (__ \ "alcoholicStrengthByVolumeInPercentage").writeNullable[BigDecimal] and
    (__ \ "degreePlato").writeNullable[BigDecimal] and
    (__ \ "fiscalMark").writeNullable[String] and
    (__ \ "fiscalMarkUsedFlag").writeNullable[Boolean] and
    (__ \ "designationOfOrigin").writeNullable[String] and
    (__ \ "sizeOfProducer").writeNullable[BigInt] and
    (__ \ "density").writeNullable[BigDecimal] and
    (__ \ "commercialDescription").writeNullable[String] and
    (__ \ "brandNameOfProducts").writeNullable[String] and
    (__ \ "maturationPeriodOrAgeOfProducts").writeNullable[String] and
    (__ \ "independentSmallProducersDeclaration").writeNullable[String] and
    (__ \ "packages").write[Seq[PackageModel]] and
    (__ \ "wineProduct").writeNullable[WineProductModel](WineProductModel.auditWrites)
  )(unlift(BodyEadEsadModel.unapply))
}
