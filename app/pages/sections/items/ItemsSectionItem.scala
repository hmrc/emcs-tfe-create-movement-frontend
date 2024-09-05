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

package pages.sections.items

import models.GoodsType._
import models.requests.DataRequest
import models.sections.items.ItemWineProductCategory.ImportedWine
import models.{GoodsType, Index}
import pages.sections.Section
import play.api.libs.json.{JsObject, JsPath}
import queries.ItemsPackagingCount
import utils.{ItemHelper, ExciseProductCodeHelper, JsonOptionFormatter, SubmissionError}
import viewmodels.taskList._

case class ItemsSectionItem(idx: Index) extends Section[JsObject] with JsonOptionFormatter {

  override val path: JsPath = ItemsSectionItems.path \ idx.position

  override def status(implicit request: DataRequest[_]): TaskListStatus =
    ItemExciseProductCodePage(idx).value match {
      case None => NotStarted
      case Some(epc) =>
        implicit val goodsType: GoodsType = GoodsType(epc)

        if (isMovementSubmissionError) {
          UpdateNeeded
        } else if (itemPagesWithoutPackagingComplete(epc) && packagingPagesComplete) {
          Completed
        } else {
          InProgress
        }
    }

  def itemPagesWithoutPackagingComplete(epc: String)(implicit goodsType: GoodsType, request: DataRequest[_]): Boolean =
    (commonMandatoryAnswers ++
      degreesPlatoAnswer ++
      independentProducerAnswers ++
      maturationAgeAnswer(epc) ++
      wineCountryOfOriginAnswers ++
      wineMoreInformationAnswers ++
      designationOfOriginAnswers(epc) ++
      alcoholStrengthAnswer ++
      itemDensityAnswer(epc) ++
      fiscalMarksAnswers
      ).forall(_.isDefined)

  private[items] def packagingPagesComplete(implicit request: DataRequest[_]): Boolean =
    (ItemExciseProductCodePage(idx).value, ItemBulkPackagingChoicePage(idx).value) match {
      case (Some(_), Some(false)) =>
        ItemsPackagingSection(idx).isCompleted
      case (Some(_), Some(true)) =>
        bulkPackagingPagesComplete
      case _ =>
        false
    }

  def bulkPackagingPagesComplete(implicit request: DataRequest[_]): Boolean =
    (wineBulkOperationAnswer ++
      wineBulkGrowingZoneAnswer ++
      bulkCommercialSeals :+
      ItemBulkPackagingSelectPage(idx).value
      ).forall(_.isDefined)

  private def commonMandatoryAnswers(implicit request: DataRequest[_]): Seq[Option[_]] =
    Seq(
      ItemExciseProductCodePage(idx).value,
      ItemCommodityCodePage(idx).value,
      ItemBrandNamePage(idx).value,
      ItemCommercialDescriptionPage(idx).value,
      ItemQuantityPage(idx).value,
      ItemNetGrossMassPage(idx).value,
      ItemBulkPackagingChoicePage(idx).value
    )

  private[items] def itemDensityAnswer(epc: String)(implicit goodsType: GoodsType, request: DataRequest[_]): Seq[Option[_]] =
    mandatoryIf(goodsType == Energy && !Seq("E470", "E500", "E600", "E930").contains(epc)) {
      Seq(ItemDensityPage(idx).value)
    }

  private[items] def fiscalMarksAnswers(implicit goodsType: GoodsType, request: DataRequest[_]): Seq[Option[_]] =
    mandatoryIf(goodsType == Tobacco) {
      ItemFiscalMarksChoicePage(idx).value match {
        case fiscalMarksChoice@Some(true) => Seq(fiscalMarksChoice, ItemFiscalMarksPage(idx).value)
        case fiscalMarksChoice => Seq(fiscalMarksChoice)
      }
    }

  private[items] def wineMoreInformationAnswers(implicit request: DataRequest[_]): Seq[Option[_]] =
    mandatoryIf(
      ItemHelper.isWine(idx)(request.userAnswers)
    ) {
      ItemWineMoreInformationChoicePage(idx).value match {
        case wineInfoChoice@Some(true) => Seq(wineInfoChoice, ItemWineMoreInformationPage(idx).value)
        case wineInfoChoice => Seq(wineInfoChoice)
      }
    }

  private[items] def wineCountryOfOriginAnswers(implicit request: DataRequest[_]): Seq[Option[_]] =
    mandatoryIf(
      ItemHelper.isWine(idx)(request.userAnswers)
    ) {
      ItemWineProductCategoryPage(idx).value match {
        case wineImportedChoice@Some(ImportedWine) => Seq(wineImportedChoice, ItemWineOriginPage(idx).value)
        case wineImportedChoice => Seq(wineImportedChoice)
      }
    }

  private[items] def designationOfOriginAnswers(epc: String)(implicit goodsType: GoodsType, request: DataRequest[_]): Seq[Option[_]] =
    mandatoryIf(goodsType.isAlcohol && (goodsType == Wine || goodsType == Intermediate || ExciseProductCodeHelper.isSpirituousBeverages(epc))) {
      //Whilst Statement of spirit marketing and labelling is required for S200 EPC's, the page (or specifically, the form) should
      //prevent the user from continuing if they hadn't selected an option
      Seq(ItemDesignationOfOriginPage(idx).value)
    }

  private[items] def maturationAgeAnswer(epc: String)(implicit goodsType: GoodsType, request: DataRequest[_]): Seq[Option[_]] =
    mandatoryIf(goodsType == Spirits && ExciseProductCodeHelper.isSpirituousBeverages(epc))(Seq(ItemMaturationPeriodAgePage(idx).value))

  private[items] def alcoholStrengthAnswer(implicit goodsType: GoodsType, request: DataRequest[_]): Seq[Option[_]] =
    mandatoryIf(goodsType.isAlcohol)(Seq(ItemAlcoholStrengthPage(idx).value))

  private[items] def independentProducerAnswers(implicit goodsType: GoodsType, request: DataRequest[_]): Seq[Option[_]] =
    mandatoryIf(goodsType.isAlcohol && ItemAlcoholStrengthPage(idx).value.exists(_ < 8.5))(
      Seq(ItemSmallIndependentProducerPage(idx).value)
    )

  private[items] def degreesPlatoAnswer(implicit goodsType: GoodsType, request: DataRequest[_]): Seq[Option[_]] =
    mandatoryIf(request.isNorthernIrelandErn && goodsType == Beer)(Seq(ItemDegreesPlatoPage(idx).value))

  private[items] def bulkCommercialSeals(implicit request: DataRequest[_]): Seq[Option[_]] =
    ItemBulkPackagingSealChoicePage(idx).value match {
      case sealChoice@Some(true) => Seq(sealChoice, ItemBulkPackagingSealTypePage(idx).value)
      case sealChoice => Seq(sealChoice)
    }

  private[items] def wineBulkGrowingZoneAnswer(implicit request: DataRequest[_]): Seq[Option[_]] =
    mandatoryIf(
      ItemHelper.isWine(idx)(request.userAnswers)
    )((ItemQuantityPage(idx).value, ItemWineProductCategoryPage(idx).value) match {
      case (Some(quantity), Some(productCategory)) if productCategory != ImportedWine && quantity > 60 => Seq(ItemWineGrowingZonePage(idx).value)
      case _ => Seq()
    })

  private[items] def wineBulkOperationAnswer(implicit request: DataRequest[_]): Seq[Option[_]] =
    mandatoryIf(
      ItemHelper.isWine(idx)(request.userAnswers)
    )(ItemQuantityPage(idx).value match {
      case Some(quantity) if quantity > 60 =>
        Seq(ItemWineOperationsChoicePage(idx).value)
      case _ => Seq()
    })

  private[items] def mandatoryIf(condition: Boolean)(f: => Seq[Option[_]]): Seq[Option[_]] = if (condition) f else Seq()


  def getSubmissionFailuresForItem(isOnAddToList: Boolean = false)(implicit request: DataRequest[_]): Seq[SubmissionError] = {
    Seq(
      ItemQuantityPage(idx).getSubmissionErrorCode(isOnAddToList),
      ItemDegreesPlatoPage(idx).getSubmissionErrorCode(isOnAddToList)
    ).flatten
  }

  /**
   * Constructs a list of item pages (which could have submission failures) and checks if there are
   * submission failures (that have not been fixed).
   *
   * @return true/false depending on if there is an outstanding submission failure within this item
   */
  override def isMovementSubmissionError(implicit request: DataRequest[_]): Boolean = getSubmissionFailuresForItem().nonEmpty

  // $COVERAGE-OFF$
  override def canBeCompletedForTraderAndDestinationType(implicit request: DataRequest[_]): Boolean =
    ItemsSection.canBeCompletedForTraderAndDestinationType

  def packagingIndexes(implicit request: DataRequest[_]): Seq[Index] =
    request.userAnswers.getCount(ItemsPackagingCount(idx)) match {
      case Some(count) =>
        0 until count map Index.apply
      case None =>
        Seq()
    }

}
