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

package fixtures

import models.UnitOfMeasure.{Kilograms, Litres20}
import models.response.referenceData.{BulkPackagingType, CnCodeInformation, ItemPackaging, WineOperations}
import models.sections.items.{ItemBrandNameModel, ItemNetGrossMassModel}
import models.sections.items.ItemBulkPackagingCode._
import models.sections.items.ItemGeographicalIndicationType.NoGeographicalIndication
import models.{ExciseProductCode, GoodsTypeModel}
import pages.sections.items.{ItemAlcoholStrengthPage, ItemBrandNamePage, ItemBulkPackagingChoicePage, ItemCommercialDescriptionPage, ItemCommodityCodePage, ItemExciseProductCodePage, ItemGeographicalIndicationChoicePage, ItemImportedWineChoicePage, ItemNetGrossMassPage, ItemPackagingProductTypePage, ItemPackagingQuantityPage, ItemPackagingSealChoicePage, ItemQuantityPage, ItemSelectPackagingPage, ItemWineMoreInformationChoicePage}
import play.api.libs.json.{JsObject, Json}
import uk.gov.hmrc.govukfrontend.views.Aliases.HtmlContent
import uk.gov.hmrc.govukfrontend.views.viewmodels.radios.RadioItem

trait ItemFixtures { _: BaseFixtures =>

  val beerExciseProductCode = ExciseProductCode(
    code = "B000",
    description = "Beer",
    category = "B",
    categoryDescription = "Beer"
  )

  val wineExciseProductCode = ExciseProductCode(
    code = "W200",
    description = "Still wine and still fermented beverages other than wine and beer",
    category = "W",
    categoryDescription = "Wine and fermented beverages other than wine and beer"
  )

  val beerExciseProductCodeJson = Json.obj(
    "code" -> "B000",
    "description" -> "Beer",
    "category" -> "B",
    "categoryDescription" -> "Beer"
  )

  val wineExciseProductCodeJson = Json.obj(
    "code" -> "W200",
    "description" -> "Still wine and still fermented beverages other than wine and beer",
    "category" -> "W",
    "categoryDescription" -> "Wine and fermented beverages other than wine and beer"
  )

  val bulkPackagingTypesJson = Json.obj(
    "VG" -> "Bulk, gas (at 1031 mbar and 15°C)",
    "VQ" -> "Bulk, liquefied gas (abn.temp/press)",
    "VL" -> "Bulk, liquid",
    "VY" -> "Bulk, solid, fine (powders)",
    "VR" -> "Bulk, solid, granular (grains)",
    "VO" -> "Bulk, solid, large (nodules)",
    "NE" -> "Unpacked or unpackaged"
  )

  val bulkPackagingTypes: Seq[BulkPackagingType] = Seq(
    BulkPackagingType(BulkGas, "Bulk, gas (at 1031 mbar and 15°C)"),
    BulkPackagingType(BulkLiquefiedGas, "Bulk, liquefied gas (abn.temp/press)"),
    BulkPackagingType(BulkLiquid, "Bulk, liquid"),
    BulkPackagingType(BulkSolidPowders, "Bulk, solid, fine (powders)"),
    BulkPackagingType(BulkSolidGrains, "Bulk, solid, granular (grains)"),
    BulkPackagingType(BulkSolidNodules, "Bulk, solid, large (nodules)"),
    BulkPackagingType(Unpacked, "Unpacked or unpackaged")
  )

  val bulkPackagingTypesRadioOptions: Seq[RadioItem] = Seq(
    RadioItem(content = HtmlContent("Bulk, gas (at 1031 mbar and 15°C) (VG)"), value = Some("VG"), id = Some("value_0")),
    RadioItem(content = HtmlContent("Bulk, liquefied gas (abn.temp/press) (VQ)"), value = Some("VQ"), id = Some("value_1")),
    RadioItem(content = HtmlContent("Bulk, liquid (VL)"), value = Some("VL"), id = Some("value_2")),
    RadioItem(content = HtmlContent("Bulk, solid, fine (powders) (VY)"), value = Some("VY"), id = Some("value_3")),
    RadioItem(content = HtmlContent("Bulk, solid, granular (grains) (VR)"), value = Some("VR"), id = Some("value_4")),
    RadioItem(content = HtmlContent("Bulk, solid, large (nodules) (VO)"), value = Some("VO"), id = Some("value_5")),
    RadioItem(content = HtmlContent("Unpacked or unpackaged (NE)"), value = Some("NE"), id = Some("value_6"))
  )

  val testEpcTobacco: String = "T200"
  val testGoodsTypeTobacco: GoodsTypeModel.GoodsType = GoodsTypeModel.apply(testEpcTobacco)
  val testCnCodeTobacco: String = "24022090"
  val testCnCodeTobacco2: String = "24029000"
  val testCommodityCodeTobacco: CnCodeInformation = CnCodeInformation(
    cnCode = testCnCodeTobacco,
    cnCodeDescription = "Cigarettes containing tobacco / other",
    exciseProductCode = testEpcTobacco,
    exciseProductCodeDescription = "Cigarettes",
    unitOfMeasure = Kilograms
  )

  val testEpcOtherProductsContainingEthylAlcohol: String = "S500"
  val testCommodityCodeS500: CnCodeInformation = CnCodeInformation(
    cnCode = "10000000",
    cnCodeDescription = "Other products containing ethyl alcohol",
    exciseProductCode = testEpcOtherProductsContainingEthylAlcohol,
    exciseProductCodeDescription = "Other products containing ethyl alcohol",
    unitOfMeasure = Litres20
  )

  val testEpcBeer: String = "B200"
  val testCnCodeBeer: String = "22030001"

  val testEpcSpirit: String = "S200"
  val testCnCodeSpirit: String = "22060031"

  val testEpcEnergy: String = "E500"
  val testCnCodeEnergy: String = "29011000"

  val testEpcEnergyWithDensity: String = "E200"
  val testCnCodeEnergyWithDensity: String = "29011000"

  val testEpcWine: String = "W200"
  val testGoodsTypeWine: GoodsTypeModel.GoodsType = GoodsTypeModel.apply(testEpcWine)
  val testCnCodeWine: String = "22060031"
  val testCommodityCodeWine: CnCodeInformation = CnCodeInformation(
    cnCode = testCnCodeWine,
    cnCodeDescription = "Sparkling cider and perry",
    exciseProductCode = testEpcWine,
    exciseProductCodeDescription = "Still wine and still fermented beverages other than wine and beer",
    unitOfMeasure = Litres20
  )

  val testItemPackagingTypes: Seq[ItemPackaging] = Seq(
    ItemPackaging("AE", "Aerosol"),
    ItemPackaging("AM", "Ampoule, non protected"),
    ItemPackaging("BG", "Bag"),
    ItemPackaging("VA", "Vat")
  )

  val testItemPackagingTypesJson: JsObject = Json.obj(
    "AE" -> "Aerosol",
    "AM" -> "Ampoule, non protected",
    "BG" -> "Bag",
    "VA" -> "Vat"
  )
  val testExciseProductCodeB000: ExciseProductCode =
    ExciseProductCode(
      "B000",
      "Beer",
      "B",
      "Beer"
    )
  val testExciseProductCodeE200: ExciseProductCode =
    ExciseProductCode(
      "E200",
      "Vegetable and animal oils Products falling within CN codes 1507 to 1518, if these are intended for use as heating fuel or motor fuel (Article 20(1)(a))",
      "E",
      "Energy Products"
    )
  val testExciseProductCodeE470: ExciseProductCode =
    ExciseProductCode(
      "E470",
      "Heavy fuel oil",
      "E",
      "Energy Products"
    )
  val testExciseProductCodeE500: ExciseProductCode =
    ExciseProductCode(
      "E500",
      "Liquified Petroleum gases (LPG) Products falling within CN codes 2711 (except 2711 11, 2711 21 and 2711 29)",
      "E",
      "Energy Products"
    )
  val testExciseProductCodeE600: ExciseProductCode =
    ExciseProductCode(
      "E600",
      "Saturated acyclic hydrocarbons Products falling within CN code 2901 10",
      "E",
      "Energy Products"
    )
  val testExciseProductCodeE800: ExciseProductCode =
    ExciseProductCode(
      "E800",
      "Methanol (methyl alcohol) Products falling within CN code 2905 11 00, which are not of synthetic origin, if these are intended for use as heating fuel or motor fuel",
      "E",
      "Energy Products"
    )
  val testExciseProductCodeE910: ExciseProductCode =
    ExciseProductCode(
      "E910",
      "Fatty-acid mono-alkyl esters, containing by volume 96,5 % or more of esters (FAMAE) falling within CN code 3824 90 99",
      "E",
      "Energy Products"
    )
  val testExciseProductCodeE930: ExciseProductCode =
    ExciseProductCode(
      "E930",
      "Additives falling within CN codes 3811 11, 3811 19 00 and 3811 90 00",
      "E",
      "Energy Products"
    )
  val testExciseProductCodeI200: ExciseProductCode =
    ExciseProductCode(
      "I200",
      "Test Description",
      "I",
      "Test Description"
    )
  val testExciseProductCodeS100: ExciseProductCode =
    ExciseProductCode(
      "S100",
      "Test Description",
      "S",
      "Test Description"
    )
  val testExciseProductCodeS200: ExciseProductCode =
    ExciseProductCode(
      "S200",
      "Spirituous beverages",
      "S",
      "Ethyl alcohol and spirits"
    )
  val testExciseProductCodeS400: ExciseProductCode =
    ExciseProductCode(
      "S400",
      "Partially denatured alcohol",
      "S",
      "Ethyl alcohol and spirits"
    )
  val testExciseProductCodeS500: ExciseProductCode =
    ExciseProductCode(
      "S500",
      "Other products containing ethyl alcohol",
      "S",
      "Ethyl alcohol and spirits"
    )
  val testExciseProductCodeT200: ExciseProductCode =
    ExciseProductCode(
      "T200",
      "Cigarettes",
      "T",
      "Manufactured tobacco products"
    )
  val testExciseProductCodeT300: ExciseProductCode =
    ExciseProductCode(
      "T300",
      "Cigars &amp; cigarillos",
      "T",
      "Manufactured tobacco products"
    )
  val testExciseProductCodeW100: ExciseProductCode =
    ExciseProductCode(
      "W100",
      "Test Description",
      "W",
      "Test Description"
    )
  val testExciseProductCodeW200: ExciseProductCode =
    ExciseProductCode(
      "W200",
      "Still wine and still fermented beverages other than wine and beer",
      "W",
      "Wine and fermented beverages other than wine and beer"
    )
  val testExciseProductCodeW300: ExciseProductCode =
    ExciseProductCode(
      "W300",
      "Sparkling wine and sparkling fermented beverages other than wine and beer",
      "W",
      "Wine and fermented beverages other than wine and beer"
    )

  val testWineOperationsJson = Json.obj(
    "12" -> "Other operations",
    "8" -> "A product harvested during a year other than that indicated in the description has been added to the product",
    "4" -> "The product has been sweetened",
    "5" -> "The product has been fortified for distillation",
    "10" -> "The product has been made on the basis of experimental use of a new oenological practice",
    "0" -> "The product has undergone none of the following operations",
    "2" -> "The product has been acidified",
    "7" -> "A product obtained from a vine variety other than that indicated in the description has been added to the product",
    "3" -> "The product has been de-acidified",
    "11" -> "The product has been partially dealcoholised",
    "9" -> "The product has been made using oak chips",
    "6" -> "A product originating in a geographical unit other than that indicated in the description has been added to the product",
    "1" -> "The product has been enriched"
  )

  val testWineOperations: Seq[WineOperations] = Seq(
    WineOperations("12", "Other operations"),
    WineOperations("8", "A product harvested during a year other than that indicated in the description has been added to the product"),
    WineOperations("4", "The product has been sweetened"),
    WineOperations("5", "The product has been fortified for distillation"),
    WineOperations("10", "The product has been made on the basis of experimental use of a new oenological practice"),
    WineOperations("0", "The product has undergone none of the following operations"),
    WineOperations("2", "The product has been acidified"),
    WineOperations("7", "A product obtained from a vine variety other than that indicated in the description has been added to the product"),
    WineOperations("3", "The product has been de-acidified"),
    WineOperations("11", "The product has been partially dealcoholised"),
    WineOperations("9", "The product has been made using oak chips"),
    WineOperations("6", "A product originating in a geographical unit other than that indicated in the description has been added to the product"),
    WineOperations("1", "The product has been enriched")
  )

  val singleCompletedWineItem = emptyUserAnswers
    .set(ItemExciseProductCodePage(testIndex1), testEpcWine)
    .set(ItemCommodityCodePage(testIndex1), testCnCodeWine)
    .set(ItemBrandNamePage(testIndex1), ItemBrandNameModel(hasBrandName = true, Some("brand")))
    .set(ItemCommercialDescriptionPage(testIndex1), "Wine from grapes")
    .set(ItemAlcoholStrengthPage(testIndex1), BigDecimal(12.5))
    .set(ItemGeographicalIndicationChoicePage(testIndex1), NoGeographicalIndication)
    .set(ItemQuantityPage(testIndex1), BigDecimal("1000"))
    .set(ItemNetGrossMassPage(testIndex1), ItemNetGrossMassModel(BigDecimal("2000"), BigDecimal("2105")))
    .set(ItemBulkPackagingChoicePage(testIndex1), false)
    .set(ItemImportedWineChoicePage(testIndex1), true)
    .set(ItemWineMoreInformationChoicePage(testIndex1), false)
    .set(ItemSelectPackagingPage(testIndex1, testPackagingIndex1), testPackageBag)
    .set(ItemPackagingQuantityPage(testIndex1, testPackagingIndex1), "400")
    .set(ItemPackagingProductTypePage(testIndex1, testPackagingIndex1), true)
    .set(ItemPackagingSealChoicePage(testIndex1, testPackagingIndex1), false)
}
