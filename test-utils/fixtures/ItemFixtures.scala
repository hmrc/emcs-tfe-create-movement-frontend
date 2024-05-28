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
import models.sections.consignee.ConsigneeExportInformation.NoInformation
import models.sections.documents.{DocumentType, DocumentsAddToList}
import models.sections.guarantor.GuarantorArranger
import models.sections.info._
import models.sections.info.movementScenario.{DestinationType, MovementScenario, MovementType, OriginType}
import models.sections.items.ItemBulkPackagingCode._
import models.sections.items.ItemGeographicalIndicationType.{NoGeographicalIndication, ProtectedDesignationOfOrigin}
import models.sections.items.ItemSmallIndependentProducerType.SelfCertifiedIndependentSmallProducerAndNotConsignor
import models.sections.items.ItemWineProductCategory.{ImportedWine, Other}
import models.sections.items._
import models.sections.journeyType.HowMovementTransported
import models.sections.sad.SadAddToListModel
import models.sections.transportArranger._
import models.sections.transportUnit.{TransportSealTypeModel, TransportUnitType, TransportUnitsAddToListModel}
import models.submitCreateMovement._
import models.{ExciseProductCode, ExemptOrganisationDetailsModel, GoodsType, UserAnswers, VatNumberModel}
import pages.sections.consignee._
import pages.sections.consignor._
import pages.sections.destination._
import pages.sections.dispatch._
import pages.sections.documents._
import pages.sections.exportInformation._
import pages.sections.firstTransporter._
import pages.sections.guarantor._
import pages.sections.importInformation._
import pages.sections.info._
import pages.sections.items._
import pages.sections.journeyType._
import pages.sections.sad.{ImportNumberPage, SadAddToListPage}
import pages.sections.transportArranger._
import pages.sections.transportUnit._
import play.api.libs.json.{JsObject, Json}
import uk.gov.hmrc.govukfrontend.views.Aliases.HtmlContent
import uk.gov.hmrc.govukfrontend.views.viewmodels.radios.RadioItem

import java.time.{LocalDate, LocalTime}

trait ItemFixtures {
  _: BaseFixtures =>

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

  val wineExciseProductCode300 = ExciseProductCode(
    code = "W300",
    description = "Still wine and still fermented beverages other than wine and beer",
    category = "W",
    categoryDescription = "Wine and fermented beverages other than wine and beer"
  )

  val energyExciseProductCode = ExciseProductCode(
    code = "E430",
    description = "Energy Products",
    category = "E",
    categoryDescription = "Energy products"
  )

  val spiritExciseProductCode = ExciseProductCode(
    code = "S500",
    description = "Spirit",
    category = "S",
    categoryDescription = "Spirit"
  )

  val s600ExciseProductCode = ExciseProductCode(
    code = "S600",
    description = "Spirit",
    category = "S",
    categoryDescription = "Spirit"
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
  val testGoodsTypeTobacco: GoodsType = GoodsType.apply(testEpcTobacco)
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
  val testGoodsTypeWine: GoodsType = GoodsType.apply(testEpcWine)
  val testCnCodeWine: String = "22060010"
  val testCommodityCodeWine: CnCodeInformation = CnCodeInformation(
    cnCode = testCnCodeWine,
    cnCodeDescription = "Piquette",
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
  val testExciseProductCodeS300: ExciseProductCode =
    ExciseProductCode(
      "S300",
      "Ethyl alcohol",
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
  val testExciseProductCodeS600: ExciseProductCode =
    ExciseProductCode(
      "S600",
      "Completely denatured alcohol, falling within Article 20 of Directive 92/83/EEC, being alcohol which has been denatured and fulfils the conditions to benefit from the exemption provided for in Article 27(1)(a) of that Directive",
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
    .set(ItemDesignationOfOriginPage(testIndex1), ItemDesignationOfOriginModel(NoGeographicalIndication, None, None))
    .set(ItemQuantityPage(testIndex1), BigDecimal("1000"))
    .set(ItemNetGrossMassPage(testIndex1), ItemNetGrossMassModel(BigDecimal("2000"), BigDecimal("2105")))
    .set(ItemBulkPackagingChoicePage(testIndex1), false)
    .set(ItemWineProductCategoryPage(testIndex1), Other)
    .set(ItemWineMoreInformationChoicePage(testIndex1), false)
    .set(ItemSelectPackagingPage(testIndex1, testPackagingIndex1), testPackageBag)
    .set(ItemPackagingQuantityPage(testIndex1, testPackagingIndex1), "400")
    .set(ItemPackagingShippingMarksChoicePage(testIndex1, testPackagingIndex1), false)
    .set(ItemPackagingSealChoicePage(testIndex1, testPackagingIndex1), false)

  val dispatchOfficeSuffix = "004098"

  val baseFullUserAnswers: UserAnswers = emptyUserAnswers
    // movementType (info)
    .set(DestinationTypePage, MovementScenario.DirectDelivery)
    .set(DeferredMovementPage(), false)
    .set(LocalReferenceNumberPage(), testLrn)
    .set(InvoiceDetailsPage(), InvoiceDetailsModel("inv ref", LocalDate.parse("2020-12-25")))
    .set(DispatchDetailsPage(), DispatchDetailsModel(LocalDate.parse("2020-10-31"), LocalTime.parse("23:59:59")))
    // consignee
    .set(ConsigneeBusinessNamePage, "consignee name")
    .set(ConsigneeExcisePage, "consignee ern")
    .set(ConsigneeExportInformationPage, Set(NoInformation))
    .set(ConsigneeAddressPage, testUserAddress.copy(street = "consignee street"))
    // consignor
    .set(ConsignorAddressPage, testUserAddress.copy(street = "consignor street"))
    // placeOfDispatch
    .set(DispatchUseConsignorDetailsPage, false)
    .set(DispatchBusinessNamePage, "dispatch name")
    .set(DispatchAddressPage, testUserAddress.copy(street = "dispatch street"))
    .set(DispatchWarehouseExcisePage, "dispatch ern")
    // dispatchImportOffice
    .set(ImportCustomsOfficeCodePage, "dispatch import office")
    // complementConsigneeTrader
    .set(ConsigneeExemptOrganisationPage, ExemptOrganisationDetailsModel("state", "number"))
    // deliveryPlaceTrader
    .set(DestinationWarehouseVatPage, "destination ern")
    .set(DestinationBusinessNamePage, "destination name")
    .set(DestinationAddressPage, testUserAddress.copy(street = "destination street"))
    // deliveryPlaceCustomsOffice
    .set(ExportCustomsOfficePage, "delivery place customs office")
    // transportArrangerTrader
    .set(TransportArrangerPage, TransportArranger.GoodsOwner)
    .set(TransportArrangerNamePage, "arranger name")
    .set(TransportArrangerAddressPage, testUserAddress.copy(street = "arranger street"))
    .set(TransportArrangerVatPage, VatNumberModel(hasVatNumber = true, Some("arranger vat")))
    // firstTransporterTrader
    .set(FirstTransporterNamePage, "first name")
    .set(FirstTransporterAddressPage, testUserAddress.copy(street = "first street"))
    .set(FirstTransporterVatPage, VatNumberModel(true, Some("first vat")))
    // documentCertificate
    .set(DocumentsCertificatesPage, true)
    .set(DocumentTypePage(testIndex1), DocumentType("0", "0 type desc"))
    .set(DocumentReferencePage(testIndex1), "0 reference")
    .set(DocumentsAddToListPage, DocumentsAddToList.No)
    // headerEadEsad
    .set(JourneyTimeHoursPage, 2)
    // transportMode
    .set(HowMovementTransportedPage, HowMovementTransported.AirTransport)
    .set(GiveInformationOtherTransportPage, "info")
    // movementGuarantee
    .set(GuarantorRequiredPage, true)
    .set(GuarantorNamePage, "guarantor name")
    .set(GuarantorAddressPage, testUserAddress.copy(street = "guarantor street"))
    .set(GuarantorVatPage, VatNumberModel(hasVatNumber = true, Some("guarantor vat")))
    .set(GuarantorArrangerPage, GuarantorArranger.GoodsOwner)
    // bodyEadEsad (items)
    .set(ItemExciseProductCodePage(testIndex1), testEpcWine)
    .set(ItemCommodityCodePage(testIndex1), testCnCodeWine)
    .set(ItemQuantityPage(testIndex1), BigDecimal(1))
    .set(ItemNetGrossMassPage(testIndex1), ItemNetGrossMassModel(netMass = BigDecimal(2), grossMass = BigDecimal(3)))
    .set(ItemAlcoholStrengthPage(testIndex1), BigDecimal(1.23))
    .set(ItemDegreesPlatoPage(testIndex1), ItemDegreesPlatoModel(hasDegreesPlato = true, Some(4.56)))
    .set(ItemFiscalMarksPage(testIndex1), "fiscal marks")
    .set(ItemFiscalMarksChoicePage(testIndex1), true)
    .set(ItemDesignationOfOriginPage(testIndex1), ItemDesignationOfOriginModel(ProtectedDesignationOfOrigin, Some("talkin' 'bout my deeeeeesignation"), None))
    .set(ItemSmallIndependentProducerPage(testIndex1), ItemSmallIndependentProducerModel(SelfCertifiedIndependentSmallProducerAndNotConsignor, Some(testErn)))
    .set(ItemProducerSizePage(testIndex1), BigInt(4))
    .set(ItemDensityPage(testIndex1), BigDecimal(7.89))
    .set(ItemCommercialDescriptionPage(testIndex1), "beans")
    .set(ItemBrandNamePage(testIndex1), ItemBrandNameModel(hasBrandName = true, Some("name")))
    .set(ItemMaturationPeriodAgePage(testIndex1), ItemMaturationPeriodAgeModel(hasMaturationPeriodAge = true, Some("really old")))
    .set(ItemBulkPackagingChoicePage(testIndex1), true)
    .set(ItemBulkPackagingSealChoicePage(testIndex1), true)
    .set(ItemBulkPackagingSelectPage(testIndex1), bulkPackagingTypes.head)
    .set(ItemBulkPackagingSealTypePage(testIndex1), ItemPackagingSealTypeModel("seal type", Some("seal info")))
    .set(ItemWineProductCategoryPage(testIndex1), ImportedWine)
    .set(ItemWineGrowingZonePage(testIndex1), ItemWineGrowingZone.CII)
    .set(ItemWineOriginPage(testIndex1), countryModelGB)
    .set(ItemWineMoreInformationChoicePage(testIndex1), true)
    .set(ItemWineMoreInformationPage(testIndex1), Some("more wine info"))
    .set(ItemWineOperationsChoicePage(testIndex1), Seq(WineOperations("op code", "choice desc")))
    .set(ItemsAddToListPage, ItemsAddToList.No)
    // sad
    .set(ImportNumberPage(testIndex1), "sad 1")
    .set(ImportNumberPage(testIndex2), "sad 2")
    .set(ImportNumberPage(testIndex3), "sad 3")
    .set(SadAddToListPage, SadAddToListModel.NoMoreToCome)
    // transportDetails
    .set(TransportUnitTypePage(testIndex1), TransportUnitType.FixedTransport)
    .set(TransportUnitIdentityPage(testIndex1), "identity")
    .set(TransportSealTypePage(testIndex1), TransportSealTypeModel("seal type", Some("seal info")))
    .set(TransportUnitGiveMoreInformationPage(testIndex1), Some("more info"))
    .set(TransportUnitsAddToListPage, TransportUnitsAddToListModel.NoMoreToCome)

  val xircSubmitCreateMovementJson = Json.parse(
    """{
      |  "movementType": "4",
      |  "attributes": {
      |    "submissionMessageType": "1",
      |    "deferredSubmissionFlag": false
      |  },
      |  "consigneeTrader": {
      |    "traderExciseNumber": "consignee ern",
      |    "traderName": "consignee name",
      |    "address": {
      |      "streetNumber": "10",
      |      "street": "consignee street",
      |      "postcode": "ZZ1 1ZZ",
      |      "city": "Testown"
      |    }
      |  },
      |  "consignorTrader": {
      |    "traderExciseNumber": "XIRC123",
      |    "traderName": "testTraderName",
      |    "address": {
      |      "streetNumber": "10",
      |      "street": "consignor street",
      |      "postcode": "ZZ1 1ZZ",
      |      "city": "Testown"
      |    }
      |  },
      |  "dispatchImportOffice": {
      |    "referenceNumber": "dispatch import office"
      |  },
      |  "complementConsigneeTrader": {
      |    "memberStateCode": "state",
      |    "serialNumberOfCertificateOfExemption": "number"
      |  },
      |  "deliveryPlaceTrader": {
      |    "traderName": "destination name",
      |    "address": {
      |      "streetNumber": "10",
      |      "street": "destination street",
      |      "postcode": "ZZ1 1ZZ",
      |      "city": "Testown"
      |    }
      |  },
      |  "deliveryPlaceCustomsOffice": {
      |    "referenceNumber": "delivery place customs office"
      |  },
      |  "competentAuthorityDispatchOffice": {
      |    "referenceNumber": "XI004098"
      |  },
      |  "transportArrangerTrader": {
      |    "traderName": "arranger name",
      |    "address": {
      |      "streetNumber": "10",
      |      "street": "arranger street",
      |      "postcode": "ZZ1 1ZZ",
      |      "city": "Testown"
      |    },
      |    "vatNumber": "arranger vat"
      |  },
      |  "firstTransporterTrader": {
      |    "traderName": "first name",
      |    "address": {
      |      "streetNumber": "10",
      |      "street": "first street",
      |      "postcode": "ZZ1 1ZZ",
      |      "city": "Testown"
      |    },
      |    "vatNumber": "first vat"
      |  },
      |  "documentCertificate": [
      |    {
      |      "documentType": "0",
      |      "documentReference": "0 reference"
      |    }
      |  ],
      |  "headerEadEsad": {
      |    "destinationType": "4",
      |    "journeyTime": "2 hours",
      |    "transportArrangement": "3"
      |  },
      |  "transportMode": {
      |    "transportModeCode": "4",
      |    "complementaryInformation": "info"
      |  },
      |  "movementGuarantee": {
      |    "guarantorTypeCode": "3",
      |    "guarantorTrader": [
      |      {
      |        "traderName": "guarantor name",
      |        "address": {
      |          "streetNumber": "10",
      |          "street": "guarantor street",
      |          "postcode": "ZZ1 1ZZ",
      |          "city": "Testown"
      |        },
      |        "vatNumber": "guarantor vat"
      |      }
      |    ]
      |  },
      |  "bodyEadEsad": [
      |    {
      |      "bodyRecordUniqueReference": 1,
      |      "exciseProductCode": "W200",
      |      "cnCode": "22060010",
      |      "quantity": 1,
      |      "grossMass": 3,
      |      "netMass": 2,
      |      "alcoholicStrengthByVolumeInPercentage": 1.23,
      |      "degreePlato": 4.56,
      |      "fiscalMark": "fiscal marks",
      |      "fiscalMarkUsedFlag": true,
      |      "designationOfOrigin": "The product has a Protected Designation of Origin (PDO). talkin' 'bout my deeeeeesignation",
      |      "sizeOfProducer": 4,
      |      "density": 7.89,
      |      "commercialDescription": "beans",
      |      "brandNameOfProducts": "name",
      |      "maturationPeriodOrAgeOfProducts": "really old",
      |      "independentSmallProducersDeclaration": "It is hereby certified that the alcoholic product described has been produced by an independent wine producer. The producer is a self-certified independent small producer and not the consignor. Identification: XIRC123456789",
      |      "packages": [
      |        {
      |          "kindOfPackages": "VG",
      |          "commercialSealIdentification": "seal type",
      |          "sealInformation": "seal info"
      |        }
      |      ],
      |      "wineProduct": {
      |        "wineProductCategory": "4",
      |        "wineGrowingZoneCode": "4",
      |        "thirdCountryOfOrigin": "GB",
      |        "otherInformation": "more wine info",
      |        "wineOperations": [
      |          "op code"
      |        ]
      |      }
      |    }
      |  ],
      |  "eadEsadDraft": {
      |    "localReferenceNumber": "1234567890",
      |    "invoiceNumber": "inv ref",
      |    "invoiceDate": "2020-12-25",
      |    "originTypeCode": "2",
      |    "dateOfDispatch": "2020-10-31",
      |    "timeOfDispatch": "23:59:59",
      |    "importSad": [
      |      {
      |        "importSadNumber": "sad 1"
      |      },
      |      {
      |        "importSadNumber": "sad 2"
      |      },
      |      {
      |        "importSadNumber": "sad 3"
      |      }
      |    ]
      |  },
      |  "transportDetails": [
      |    {
      |      "transportUnitCode": "5",
      |      "identityOfTransportUnits": "identity",
      |      "commercialSealIdentification": "seal type",
      |      "complementaryInformation": "more info",
      |      "sealInformation": "seal info"
      |    }
      |  ]
      |}""".stripMargin)

  val xircSubmitCreateMovementModel: SubmitCreateMovementModel = SubmitCreateMovementModel(
    movementType = MovementType.ImportEu,
    attributes = AttributesModel(SubmissionMessageType.Standard, Some(false)),
    consigneeTrader = Some(TraderModel(
      traderExciseNumber = Some("consignee ern"),
      traderName = Some("consignee name"),
      address = Some(AddressModel.fromUserAddress(testUserAddress.copy(street = "consignee street"))),
      vatNumber = None,
      eoriNumber = None
    )),
    consignorTrader = TraderModel(
      traderExciseNumber = Some("XIRC123"),
      traderName = Some(testMinTraderKnownFacts.traderName),
      address = Some(AddressModel.fromUserAddress(testUserAddress.copy(street = "consignor street"))),
      vatNumber = None,
      eoriNumber = None
    ),
    placeOfDispatchTrader = None,
    dispatchImportOffice = Some(OfficeModel("dispatch import office")),
    complementConsigneeTrader = Some(ComplementConsigneeTraderModel("state", Some("number"))),
    deliveryPlaceTrader = Some(TraderModel(
      traderExciseNumber = None,
      traderName = Some("destination name"),
      address = Some(AddressModel.fromUserAddress(testUserAddress.copy(street = "destination street"))),
      vatNumber = None,
      eoriNumber = None
    )),
    deliveryPlaceCustomsOffice = Some(OfficeModel("delivery place customs office")),
    competentAuthorityDispatchOffice = OfficeModel(s"XI$dispatchOfficeSuffix"),
    transportArrangerTrader = Some(TraderModel(
      traderExciseNumber = None,
      traderName = Some("arranger name"),
      address = Some(AddressModel.fromUserAddress(testUserAddress.copy(street = "arranger street"))),
      vatNumber = Some("arranger vat"),
      eoriNumber = None
    )),
    firstTransporterTrader = Some(TraderModel(
      traderExciseNumber = None,
      traderName = Some("first name"),
      address = Some(AddressModel.fromUserAddress(testUserAddress.copy(street = "first street"))),
      vatNumber = Some("first vat"),
      eoriNumber = None
    )),
    documentCertificate = Some(Seq(
      DocumentCertificateModel(
        documentType = Some("0"),
        documentReference = Some("0 reference"),
        documentDescription = None,
        referenceOfDocument = None
      )
    )),
    headerEadEsad = HeaderEadEsadModel(
      destinationType = DestinationType.DirectDelivery,
      journeyTime = "2 hours",
      transportArrangement = TransportArranger.GoodsOwner
    ),
    transportMode = TransportModeModel(
      transportModeCode = HowMovementTransported.AirTransport,
      complementaryInformation = Some("info")
    ),
    movementGuarantee = MovementGuaranteeModel(
      guarantorTypeCode = GuarantorArranger.GoodsOwner,
      guarantorTrader = Some(Seq(TraderModel(
        traderExciseNumber = None,
        traderName = Some("guarantor name"),
        address = Some(AddressModel.fromUserAddress(testUserAddress.copy(street = "guarantor street"))),
        vatNumber = Some("guarantor vat"),
        eoriNumber = None
      )))
    ),
    bodyEadEsad = Seq(BodyEadEsadModel(
      bodyRecordUniqueReference = 1,
      exciseProductCode = testEpcWine,
      cnCode = testCnCodeWine,
      quantity = BigDecimal(1),
      grossMass = BigDecimal(3),
      netMass = BigDecimal(2),
      alcoholicStrengthByVolumeInPercentage = Some(BigDecimal(1.23)),
      degreePlato = Some(4.56),
      fiscalMark = Some("fiscal marks"),
      fiscalMarkUsedFlag = Some(true),
      designationOfOrigin = Some("The product has a Protected Designation of Origin (PDO). talkin' 'bout my deeeeeesignation"),
      independentSmallProducersDeclaration = Some("It is hereby certified that the alcoholic product described has been produced by an independent wine producer. The producer is a self-certified independent small producer and not the consignor. Identification: XIRC123456789"),
      sizeOfProducer = Some(BigInt(4)),
      density = Some(BigDecimal(7.89)),
      commercialDescription = Some("beans"),
      brandNameOfProducts = Some("name"),
      maturationPeriodOrAgeOfProducts = Some("really old"),
      packages = Seq(
        PackageModel(
          kindOfPackages = bulkPackagingTypes.head.packagingType.toString,
          numberOfPackages = None,
          shippingMarks = None,
          commercialSealIdentification = Some("seal type"),
          sealInformation = Some("seal info")
        )
      ),
      wineProduct = Some(
        WineProductModel(
          wineProductCategory = ItemWineCategory.ImportedWine,
          wineGrowingZoneCode = Some(ItemWineGrowingZone.CII),
          thirdCountryOfOrigin = Some(countryModelGB.code),
          otherInformation = Some("more wine info"),
          wineOperations = Some(Seq(WineOperations("op code", "choice desc")))
        )
      )
    )),
    eadEsadDraft = EadEsadDraftModel(
      localReferenceNumber = testLrn,
      invoiceNumber = "inv ref",
      invoiceDate = Some("2020-12-25"),
      originTypeCode = OriginType.Imports,
      dateOfDispatch = "2020-10-31",
      timeOfDispatch = Some("23:59:59"),
      importSad = Some(Seq(ImportSadModel("sad 1"), ImportSadModel("sad 2"), ImportSadModel("sad 3")))
    ),
    transportDetails = Seq(
      TransportDetailsModel(
        transportUnitCode = TransportUnitType.FixedTransport,
        identityOfTransportUnits = Some("identity"),
        commercialSealIdentification = Some("seal type"),
        complementaryInformation = Some("more info"),
        sealInformation = Some("seal info")
      )
    )
  )

  val xiwkSubmitCreateMovementJson = Json.parse(
    """
      |{
      |  "movementType": "2",
      |  "attributes": {
      |    "submissionMessageType": "1",
      |    "deferredSubmissionFlag": false
      |  },
      |  "consigneeTrader": {
      |    "traderExciseNumber": "consignee ern",
      |    "traderName": "consignee name",
      |    "address": {
      |      "streetNumber": "10",
      |      "street": "consignee street",
      |      "postcode": "ZZ1 1ZZ",
      |      "city": "Testown"
      |    }
      |  },
      |  "consignorTrader": {
      |    "traderExciseNumber": "XIWK123",
      |    "traderName": "testTraderName",
      |    "address": {
      |      "streetNumber": "10",
      |      "street": "consignor street",
      |      "postcode": "ZZ1 1ZZ",
      |      "city": "Testown"
      |    }
      |  },
      |  "placeOfDispatchTrader": {
      |    "traderExciseNumber": "dispatch ern",
      |    "traderName": "dispatch name",
      |    "address": {
      |      "streetNumber": "10",
      |      "street": "dispatch street",
      |      "postcode": "ZZ1 1ZZ",
      |      "city": "Testown"
      |    }
      |  },
      |  "dispatchImportOffice": {
      |    "referenceNumber": "dispatch import office"
      |  },
      |  "complementConsigneeTrader": {
      |    "memberStateCode": "state",
      |    "serialNumberOfCertificateOfExemption": "number"
      |  },
      |  "deliveryPlaceTrader": {
      |    "traderName": "destination name",
      |    "address": {
      |      "streetNumber": "10",
      |      "street": "destination street",
      |      "postcode": "ZZ1 1ZZ",
      |      "city": "Testown"
      |    }
      |  },
      |  "deliveryPlaceCustomsOffice": {
      |    "referenceNumber": "delivery place customs office"
      |  },
      |  "competentAuthorityDispatchOffice": {
      |    "referenceNumber": "XI004098"
      |  },
      |  "transportArrangerTrader": {
      |    "traderName": "arranger name",
      |    "address": {
      |      "streetNumber": "10",
      |      "street": "arranger street",
      |      "postcode": "ZZ1 1ZZ",
      |      "city": "Testown"
      |    },
      |    "vatNumber": "arranger vat"
      |  },
      |  "firstTransporterTrader": {
      |    "traderName": "first name",
      |    "address": {
      |      "streetNumber": "10",
      |      "street": "first street",
      |      "postcode": "ZZ1 1ZZ",
      |      "city": "Testown"
      |    },
      |    "vatNumber": "first vat"
      |  },
      |  "documentCertificate": [
      |    {
      |      "documentType": "0",
      |      "documentReference": "0 reference"
      |    }
      |  ],
      |  "headerEadEsad": {
      |    "destinationType": "4",
      |    "journeyTime": "2 hours",
      |    "transportArrangement": "3"
      |  },
      |  "transportMode": {
      |    "transportModeCode": "4",
      |    "complementaryInformation": "info"
      |  },
      |  "movementGuarantee": {
      |    "guarantorTypeCode": "3",
      |    "guarantorTrader": [
      |      {
      |        "traderName": "guarantor name",
      |        "address": {
      |          "streetNumber": "10",
      |          "street": "guarantor street",
      |          "postcode": "ZZ1 1ZZ",
      |          "city": "Testown"
      |        },
      |        "vatNumber": "guarantor vat"
      |      }
      |    ]
      |  },
      |  "bodyEadEsad": [
      |    {
      |      "bodyRecordUniqueReference": 1,
      |      "exciseProductCode": "W200",
      |      "cnCode": "22060010",
      |      "quantity": 1,
      |      "grossMass": 3,
      |      "netMass": 2,
      |      "alcoholicStrengthByVolumeInPercentage": 1.23,
      |      "degreePlato": 4.56,
      |      "fiscalMark": "fiscal marks",
      |      "fiscalMarkUsedFlag": true,
      |      "designationOfOrigin": "The product has a Protected Designation of Origin (PDO). talkin' 'bout my deeeeeesignation",
      |      "sizeOfProducer": 4,
      |      "density": 7.89,
      |      "commercialDescription": "beans",
      |      "brandNameOfProducts": "name",
      |      "maturationPeriodOrAgeOfProducts": "really old",
      |      "independentSmallProducersDeclaration": "It is hereby certified that the alcoholic product described has been produced by an independent wine producer. The producer is a self-certified independent small producer and not the consignor. Identification: XIRC123456789",
      |      "packages": [
      |        {
      |          "kindOfPackages": "VG",
      |          "commercialSealIdentification": "seal type",
      |          "sealInformation": "seal info"
      |        }
      |      ],
      |      "wineProduct": {
      |        "wineProductCategory": "4",
      |        "wineGrowingZoneCode": "4",
      |        "thirdCountryOfOrigin": "GB",
      |        "otherInformation": "more wine info",
      |        "wineOperations": [
      |          "op code"
      |        ]
      |      }
      |    }
      |  ],
      |  "eadEsadDraft": {
      |    "localReferenceNumber": "1234567890",
      |    "invoiceNumber": "inv ref",
      |    "invoiceDate": "2020-12-25",
      |    "originTypeCode": "1",
      |    "dateOfDispatch": "2020-10-31",
      |    "timeOfDispatch": "23:59:59"
      |  },
      |  "transportDetails": [
      |    {
      |      "transportUnitCode": "5",
      |      "identityOfTransportUnits": "identity",
      |      "commercialSealIdentification": "seal type",
      |      "complementaryInformation": "more info",
      |      "sealInformation": "seal info"
      |    }
      |  ]
      |}""".stripMargin)

  val xiwkSubmitCreateMovementModel: SubmitCreateMovementModel = SubmitCreateMovementModel(
    movementType = MovementType.UkToEu,
    attributes = AttributesModel(SubmissionMessageType.Standard, Some(false)),
    consigneeTrader = Some(TraderModel(
      traderExciseNumber = Some("consignee ern"),
      traderName = Some("consignee name"),
      address = Some(AddressModel.fromUserAddress(testUserAddress.copy(street = "consignee street"))),
      vatNumber = None,
      eoriNumber = None
    )),
    consignorTrader = TraderModel(
      traderExciseNumber = Some("XIWK123"),
      traderName = Some(testMinTraderKnownFacts.traderName),
      address = Some(AddressModel.fromUserAddress(testUserAddress.copy(street = "consignor street"))),
      vatNumber = None,
      eoriNumber = None
    ),
    placeOfDispatchTrader = Some(TraderModel(
      traderExciseNumber = Some("dispatch ern"),
      traderName = Some("dispatch name"),
      address = Some(AddressModel.fromUserAddress(testUserAddress.copy(street = "dispatch street"))),
      vatNumber = None,
      eoriNumber = None
    )),
    dispatchImportOffice = Some(OfficeModel("dispatch import office")),
    complementConsigneeTrader = Some(ComplementConsigneeTraderModel("state", Some("number"))),
    deliveryPlaceTrader = Some(TraderModel(
      traderExciseNumber = None,
      traderName = Some("destination name"),
      address = Some(AddressModel.fromUserAddress(testUserAddress.copy(street = "destination street"))),
      vatNumber = None,
      eoriNumber = None
    )),
    deliveryPlaceCustomsOffice = Some(OfficeModel("delivery place customs office")),
    competentAuthorityDispatchOffice = OfficeModel(s"XI$dispatchOfficeSuffix"),
    transportArrangerTrader = Some(TraderModel(
      traderExciseNumber = None,
      traderName = Some("arranger name"),
      address = Some(AddressModel.fromUserAddress(testUserAddress.copy(street = "arranger street"))),
      vatNumber = Some("arranger vat"),
      eoriNumber = None
    )),
    firstTransporterTrader = Some(TraderModel(
      traderExciseNumber = None,
      traderName = Some("first name"),
      address = Some(AddressModel.fromUserAddress(testUserAddress.copy(street = "first street"))),
      vatNumber = Some("first vat"),
      eoriNumber = None
    )),
    documentCertificate = Some(Seq(
      DocumentCertificateModel(
        documentType = Some("0"),
        documentReference = Some("0 reference"),
        documentDescription = None,
        referenceOfDocument = None
      )
    )),
    headerEadEsad = HeaderEadEsadModel(
      destinationType = DestinationType.DirectDelivery,
      journeyTime = "2 hours",
      transportArrangement = TransportArranger.GoodsOwner
    ),
    transportMode = TransportModeModel(
      transportModeCode = HowMovementTransported.AirTransport,
      complementaryInformation = Some("info")
    ),
    movementGuarantee = MovementGuaranteeModel(
      guarantorTypeCode = GuarantorArranger.GoodsOwner,
      guarantorTrader = Some(Seq(TraderModel(
        traderExciseNumber = None,
        traderName = Some("guarantor name"),
        address = Some(AddressModel.fromUserAddress(testUserAddress.copy(street = "guarantor street"))),
        vatNumber = Some("guarantor vat"),
        eoriNumber = None
      )))
    ),
    bodyEadEsad = Seq(BodyEadEsadModel(
      bodyRecordUniqueReference = 1,
      exciseProductCode = testEpcWine,
      cnCode = testCnCodeWine,
      quantity = BigDecimal(1),
      grossMass = BigDecimal(3),
      netMass = BigDecimal(2),
      alcoholicStrengthByVolumeInPercentage = Some(BigDecimal(1.23)),
      degreePlato = Some(4.56),
      fiscalMark = Some("fiscal marks"),
      fiscalMarkUsedFlag = Some(true),
      designationOfOrigin = Some("The product has a Protected Designation of Origin (PDO). talkin' 'bout my deeeeeesignation"),
      independentSmallProducersDeclaration = Some("It is hereby certified that the alcoholic product described has been produced by an independent wine producer. The producer is a self-certified independent small producer and not the consignor. Identification: XIRC123456789"),
      sizeOfProducer = Some(BigInt(4)),
      density = Some(BigDecimal(7.89)),
      commercialDescription = Some("beans"),
      brandNameOfProducts = Some("name"),
      maturationPeriodOrAgeOfProducts = Some("really old"),
      packages = Seq(
        PackageModel(
          kindOfPackages = bulkPackagingTypes.head.packagingType.toString,
          numberOfPackages = None,
          shippingMarks = None,
          commercialSealIdentification = Some("seal type"),
          sealInformation = Some("seal info")
        )
      ),
      wineProduct = Some(
        WineProductModel(
          wineProductCategory = ItemWineCategory.ImportedWine,
          wineGrowingZoneCode = Some(ItemWineGrowingZone.CII),
          thirdCountryOfOrigin = Some(countryModelGB.code),
          otherInformation = Some("more wine info"),
          wineOperations = Some(Seq(WineOperations("op code", "choice desc")))
        )
      )
    )),
    eadEsadDraft = EadEsadDraftModel(
      localReferenceNumber = testLrn,
      invoiceNumber = "inv ref",
      invoiceDate = Some("2020-12-25"),
      originTypeCode = OriginType.TaxWarehouse,
      dateOfDispatch = "2020-10-31",
      timeOfDispatch = Some("23:59:59"),
      importSad = None
    ),
    transportDetails = Seq(
      TransportDetailsModel(
        transportUnitCode = TransportUnitType.FixedTransport,
        identityOfTransportUnits = Some("identity"),
        commercialSealIdentification = Some("seal type"),
        complementaryInformation = Some("more info"),
        sealInformation = Some("seal info")
      )
    )
  )


  val gbrcSubmitCreateMovementJson = Json.parse(
    """
      |{
      |  "movementType": "5",
      |  "attributes": {
      |    "submissionMessageType": "1",
      |    "deferredSubmissionFlag": false
      |  },
      |  "consigneeTrader": {
      |    "traderExciseNumber": "consignee ern",
      |    "traderName": "consignee name",
      |    "address": {
      |      "streetNumber": "10",
      |      "street": "consignee street",
      |      "postcode": "ZZ1 1ZZ",
      |      "city": "Testown"
      |    }
      |  },
      |  "consignorTrader": {
      |    "traderExciseNumber": "GBRC123",
      |    "traderName": "testTraderName",
      |    "address": {
      |      "streetNumber": "10",
      |      "street": "consignor street",
      |      "postcode": "ZZ1 1ZZ",
      |      "city": "Testown"
      |    }
      |  },
      |  "dispatchImportOffice": {
      |    "referenceNumber": "dispatch import office"
      |  },
      |  "complementConsigneeTrader": {
      |    "memberStateCode": "state",
      |    "serialNumberOfCertificateOfExemption": "number"
      |  },
      |  "deliveryPlaceTrader": {
      |    "traderExciseNumber": "XIRC123456789",
      |    "traderName": "destination name",
      |    "address": {
      |      "streetNumber": "10",
      |      "street": "destination street",
      |      "postcode": "ZZ1 1ZZ",
      |      "city": "Testown"
      |    }
      |  },
      |  "deliveryPlaceCustomsOffice": {
      |    "referenceNumber": "delivery place customs office"
      |  },
      |  "competentAuthorityDispatchOffice": {
      |    "referenceNumber": "GB004098"
      |  },
      |  "transportArrangerTrader": {
      |    "traderName": "arranger name",
      |    "address": {
      |      "streetNumber": "10",
      |      "street": "arranger street",
      |      "postcode": "ZZ1 1ZZ",
      |      "city": "Testown"
      |    },
      |    "vatNumber": "arranger vat"
      |  },
      |  "firstTransporterTrader": {
      |    "traderName": "first name",
      |    "address": {
      |      "streetNumber": "10",
      |      "street": "first street",
      |      "postcode": "ZZ1 1ZZ",
      |      "city": "Testown"
      |    },
      |    "vatNumber": "first vat"
      |  },
      |  "documentCertificate": [
      |    {
      |      "documentType": "0",
      |      "documentReference": "0 reference"
      |    }
      |  ],
      |  "headerEadEsad": {
      |    "destinationType": "1",
      |    "journeyTime": "2 hours",
      |    "transportArrangement": "3"
      |  },
      |  "transportMode": {
      |    "transportModeCode": "4",
      |    "complementaryInformation": "info"
      |  },
      |  "movementGuarantee": {
      |    "guarantorTypeCode": "3",
      |    "guarantorTrader": [
      |      {
      |        "traderName": "guarantor name",
      |        "address": {
      |          "streetNumber": "10",
      |          "street": "guarantor street",
      |          "postcode": "ZZ1 1ZZ",
      |          "city": "Testown"
      |        },
      |        "vatNumber": "guarantor vat"
      |      }
      |    ]
      |  },
      |  "bodyEadEsad": [
      |    {
      |      "bodyRecordUniqueReference": 1,
      |      "exciseProductCode": "W200",
      |      "cnCode": "22060010",
      |      "quantity": 1,
      |      "grossMass": 3,
      |      "netMass": 2,
      |      "alcoholicStrengthByVolumeInPercentage": 1.23,
      |      "degreePlato": 4.56,
      |      "fiscalMark": "fiscal marks",
      |      "fiscalMarkUsedFlag": true,
      |      "designationOfOrigin": "The product has a Protected Designation of Origin (PDO). talkin' 'bout my deeeeeesignation",
      |      "sizeOfProducer": 4,
      |      "density": 7.89,
      |      "commercialDescription": "beans",
      |      "brandNameOfProducts": "name",
      |      "maturationPeriodOrAgeOfProducts": "really old",
      |      "independentSmallProducersDeclaration": "It is hereby certified that the alcoholic product described has been produced by an independent small producer. The producer is a self-certified independent small producer and not the consignor. Identification: XIRC123456789",
      |      "packages": [
      |        {
      |          "kindOfPackages": "VG",
      |          "commercialSealIdentification": "seal type",
      |          "sealInformation": "seal info"
      |        }
      |      ],
      |      "wineProduct": {
      |        "wineProductCategory": "4",
      |        "wineGrowingZoneCode": "4",
      |        "thirdCountryOfOrigin": "GB",
      |        "otherInformation": "more wine info",
      |        "wineOperations": [
      |          "op code"
      |        ]
      |      }
      |    }
      |  ],
      |  "eadEsadDraft": {
      |    "localReferenceNumber": "1234567890",
      |    "invoiceNumber": "inv ref",
      |    "invoiceDate": "2020-12-25",
      |    "originTypeCode": "2",
      |    "dateOfDispatch": "2020-10-31",
      |    "timeOfDispatch": "23:59:59",
      |    "importSad": [
      |      {
      |        "importSadNumber": "sad 1"
      |      },
      |      {
      |        "importSadNumber": "sad 2"
      |      },
      |      {
      |        "importSadNumber": "sad 3"
      |      }
      |    ]
      |  },
      |  "transportDetails": [
      |    {
      |      "transportUnitCode": "5",
      |      "identityOfTransportUnits": "identity",
      |      "commercialSealIdentification": "seal type",
      |      "complementaryInformation": "more info",
      |      "sealInformation": "seal info"
      |    }
      |  ]
      |}""".stripMargin)

  val gbrcSubmitCreateMovementModel: SubmitCreateMovementModel = SubmitCreateMovementModel(
    movementType = MovementType.ImportUk,
    attributes = AttributesModel(SubmissionMessageType.Standard, Some(false)),
    consigneeTrader = Some(TraderModel(
      traderExciseNumber = Some("consignee ern"),
      traderName = Some("consignee name"),
      address = Some(AddressModel.fromUserAddress(testUserAddress.copy(street = "consignee street"))),
      vatNumber = None,
      eoriNumber = None
    )),
    consignorTrader = TraderModel(
      traderExciseNumber = Some("GBRC123"),
      traderName = Some(testMinTraderKnownFacts.traderName),
      address = Some(AddressModel.fromUserAddress(testUserAddress.copy(street = "consignor street"))),
      vatNumber = None,
      eoriNumber = None
    ),
    placeOfDispatchTrader = None,
    dispatchImportOffice = Some(OfficeModel("dispatch import office")),
    complementConsigneeTrader = Some(ComplementConsigneeTraderModel("state", Some("number"))),
    deliveryPlaceTrader = Some(TraderModel(
      traderExciseNumber = Some(testErn),
      traderName = Some("destination name"),
      address = Some(AddressModel.fromUserAddress(testUserAddress.copy(street = "destination street"))),
      vatNumber = None,
      eoriNumber = None
    )),
    deliveryPlaceCustomsOffice = Some(OfficeModel("delivery place customs office")),
    competentAuthorityDispatchOffice = OfficeModel(s"GB$dispatchOfficeSuffix"),
    transportArrangerTrader = Some(TraderModel(
      traderExciseNumber = None,
      traderName = Some("arranger name"),
      address = Some(AddressModel.fromUserAddress(testUserAddress.copy(street = "arranger street"))),
      vatNumber = Some("arranger vat"),
      eoriNumber = None
    )),
    firstTransporterTrader = Some(TraderModel(
      traderExciseNumber = None,
      traderName = Some("first name"),
      address = Some(AddressModel.fromUserAddress(testUserAddress.copy(street = "first street"))),
      vatNumber = Some("first vat"),
      eoriNumber = None
    )),
    documentCertificate = Some(Seq(
      DocumentCertificateModel(
        documentType = Some("0"),
        documentReference = Some("0 reference"),
        documentDescription = None,
        referenceOfDocument = None
      )
    )),
    headerEadEsad = HeaderEadEsadModel(
      destinationType = DestinationType.TaxWarehouse,
      journeyTime = "2 hours",
      transportArrangement = TransportArranger.GoodsOwner
    ),
    transportMode = TransportModeModel(
      transportModeCode = HowMovementTransported.AirTransport,
      complementaryInformation = Some("info")
    ),
    movementGuarantee = MovementGuaranteeModel(
      guarantorTypeCode = GuarantorArranger.GoodsOwner,
      guarantorTrader = Some(Seq(TraderModel(
        traderExciseNumber = None,
        traderName = Some("guarantor name"),
        address = Some(AddressModel.fromUserAddress(testUserAddress.copy(street = "guarantor street"))),
        vatNumber = Some("guarantor vat"),
        eoriNumber = None
      )))
    ),
    bodyEadEsad = Seq(BodyEadEsadModel(
      bodyRecordUniqueReference = 1,
      exciseProductCode = testEpcWine,
      cnCode = testCnCodeWine,
      quantity = BigDecimal(1),
      grossMass = BigDecimal(3),
      netMass = BigDecimal(2),
      alcoholicStrengthByVolumeInPercentage = Some(BigDecimal(1.23)),
      degreePlato = Some(4.56),
      fiscalMark = Some("fiscal marks"),
      fiscalMarkUsedFlag = Some(true),
      designationOfOrigin = Some("The product has a Protected Designation of Origin (PDO). talkin' 'bout my deeeeeesignation"),
      independentSmallProducersDeclaration = Some("It is hereby certified that the alcoholic product described has been produced by an independent small producer. The producer is a self-certified independent small producer and not the consignor. Identification: XIRC123456789"),
      sizeOfProducer = Some(BigInt(4)),
      density = Some(BigDecimal(7.89)),
      commercialDescription = Some("beans"),
      brandNameOfProducts = Some("name"),
      maturationPeriodOrAgeOfProducts = Some("really old"),
      packages = Seq(
        PackageModel(
          kindOfPackages = bulkPackagingTypes.head.packagingType.toString,
          numberOfPackages = None,
          shippingMarks = None,
          commercialSealIdentification = Some("seal type"),
          sealInformation = Some("seal info")
        )
      ),
      wineProduct = Some(
        WineProductModel(
          wineProductCategory = ItemWineCategory.ImportedWine,
          wineGrowingZoneCode = Some(ItemWineGrowingZone.CII),
          thirdCountryOfOrigin = Some(countryModelGB.code),
          otherInformation = Some("more wine info"),
          wineOperations = Some(Seq(WineOperations("op code", "choice desc")))
        )
      )
    )),
    eadEsadDraft = EadEsadDraftModel(
      localReferenceNumber = testLrn,
      invoiceNumber = "inv ref",
      invoiceDate = Some("2020-12-25"),
      originTypeCode = OriginType.Imports,
      dateOfDispatch = "2020-10-31",
      timeOfDispatch = Some("23:59:59"),
      importSad = Some(Seq(ImportSadModel("sad 1"), ImportSadModel("sad 2"), ImportSadModel("sad 3")))
    ),
    transportDetails = Seq(
      TransportDetailsModel(
        transportUnitCode = TransportUnitType.FixedTransport,
        identityOfTransportUnits = Some("identity"),
        commercialSealIdentification = Some("seal type"),
        complementaryInformation = Some("more info"),
        sealInformation = Some("seal info")
      )
    )
  )

  val gbwkSubmitCreateMovementJson = Json.parse(
    """{
      |  "movementType": "1",
      |  "attributes": {
      |    "submissionMessageType": "1",
      |    "deferredSubmissionFlag": false
      |  },
      |  "consigneeTrader": {
      |    "traderExciseNumber": "consignee ern",
      |    "traderName": "consignee name",
      |    "address": {
      |      "streetNumber": "10",
      |      "street": "consignee street",
      |      "postcode": "ZZ1 1ZZ",
      |      "city": "Testown"
      |    }
      |  },
      |  "consignorTrader": {
      |    "traderExciseNumber": "GBWK123",
      |    "traderName": "testTraderName",
      |    "address": {
      |      "streetNumber": "10",
      |      "street": "consignor street",
      |      "postcode": "ZZ1 1ZZ",
      |      "city": "Testown"
      |    }
      |  },
      |  "placeOfDispatchTrader": {
      |    "traderExciseNumber": "dispatch ern",
      |    "traderName": "dispatch name",
      |    "address": {
      |      "streetNumber": "10",
      |      "street": "dispatch street",
      |      "postcode": "ZZ1 1ZZ",
      |      "city": "Testown"
      |    }
      |  },
      |  "dispatchImportOffice": {
      |    "referenceNumber": "dispatch import office"
      |  },
      |  "complementConsigneeTrader": {
      |    "memberStateCode": "state",
      |    "serialNumberOfCertificateOfExemption": "number"
      |  },
      |  "deliveryPlaceTrader": {
      |    "traderExciseNumber": "XIRC123456789",
      |    "traderName": "destination name",
      |    "address": {
      |      "streetNumber": "10",
      |      "street": "destination street",
      |      "postcode": "ZZ1 1ZZ",
      |      "city": "Testown"
      |    }
      |  },
      |  "deliveryPlaceCustomsOffice": {
      |    "referenceNumber": "delivery place customs office"
      |  },
      |  "competentAuthorityDispatchOffice": {
      |    "referenceNumber": "GB004098"
      |  },
      |  "transportArrangerTrader": {
      |    "traderName": "arranger name",
      |    "address": {
      |      "streetNumber": "10",
      |      "street": "arranger street",
      |      "postcode": "ZZ1 1ZZ",
      |      "city": "Testown"
      |    },
      |    "vatNumber": "arranger vat"
      |  },
      |  "firstTransporterTrader": {
      |    "traderName": "first name",
      |    "address": {
      |      "streetNumber": "10",
      |      "street": "first street",
      |      "postcode": "ZZ1 1ZZ",
      |      "city": "Testown"
      |    },
      |    "vatNumber": "first vat"
      |  },
      |  "documentCertificate": [
      |    {
      |      "documentType": "0",
      |      "documentReference": "0 reference"
      |    }
      |  ],
      |  "headerEadEsad": {
      |    "destinationType": "1",
      |    "journeyTime": "2 hours",
      |    "transportArrangement": "3"
      |  },
      |  "transportMode": {
      |    "transportModeCode": "4",
      |    "complementaryInformation": "info"
      |  },
      |  "movementGuarantee": {
      |    "guarantorTypeCode": "3",
      |    "guarantorTrader": [
      |      {
      |        "traderName": "guarantor name",
      |        "address": {
      |          "streetNumber": "10",
      |          "street": "guarantor street",
      |          "postcode": "ZZ1 1ZZ",
      |          "city": "Testown"
      |        },
      |        "vatNumber": "guarantor vat"
      |      }
      |    ]
      |  },
      |  "bodyEadEsad": [
      |    {
      |      "bodyRecordUniqueReference": 1,
      |      "exciseProductCode": "W200",
      |      "cnCode": "22060010",
      |      "quantity": 1,
      |      "grossMass": 3,
      |      "netMass": 2,
      |      "alcoholicStrengthByVolumeInPercentage": 1.23,
      |      "degreePlato": 4.56,
      |      "fiscalMark": "fiscal marks",
      |      "fiscalMarkUsedFlag": true,
      |      "designationOfOrigin": "The product has a Protected Designation of Origin (PDO). talkin' 'bout my deeeeeesignation",
      |      "sizeOfProducer": 4,
      |      "density": 7.89,
      |      "commercialDescription": "beans",
      |      "brandNameOfProducts": "name",
      |      "maturationPeriodOrAgeOfProducts": "really old",
      |      "independentSmallProducersDeclaration": "It is hereby certified that the alcoholic product described has been produced by an independent small producer. The producer is a self-certified independent small producer and not the consignor. Identification: XIRC123456789",
      |      "packages": [
      |        {
      |          "kindOfPackages": "VG",
      |          "commercialSealIdentification": "seal type",
      |          "sealInformation": "seal info"
      |        }
      |      ],
      |      "wineProduct": {
      |        "wineProductCategory": "4",
      |        "wineGrowingZoneCode": "4",
      |        "thirdCountryOfOrigin": "GB",
      |        "otherInformation": "more wine info",
      |        "wineOperations": [
      |          "op code"
      |        ]
      |      }
      |    }
      |  ],
      |  "eadEsadDraft": {
      |    "localReferenceNumber": "1234567890",
      |    "invoiceNumber": "inv ref",
      |    "invoiceDate": "2020-12-25",
      |    "originTypeCode": "1",
      |    "dateOfDispatch": "2020-10-31",
      |    "timeOfDispatch": "23:59:59"
      |  },
      |  "transportDetails": [
      |    {
      |      "transportUnitCode": "5",
      |      "identityOfTransportUnits": "identity",
      |      "commercialSealIdentification": "seal type",
      |      "complementaryInformation": "more info",
      |      "sealInformation": "seal info"
      |    }
      |  ]
      |}""".stripMargin)

  val gbwkSubmitCreateMovementModel: SubmitCreateMovementModel = SubmitCreateMovementModel(
    movementType = MovementType.UkToUk,
    attributes = AttributesModel(SubmissionMessageType.Standard, Some(false)),
    consigneeTrader = Some(TraderModel(
      traderExciseNumber = Some("consignee ern"),
      traderName = Some("consignee name"),
      address = Some(AddressModel.fromUserAddress(testUserAddress.copy(street = "consignee street"))),
      vatNumber = None,
      eoriNumber = None
    )),
    consignorTrader = TraderModel(
      traderExciseNumber = Some("GBWK123"),
      traderName = Some(testMinTraderKnownFacts.traderName),
      address = Some(AddressModel.fromUserAddress(testUserAddress.copy(street = "consignor street"))),
      vatNumber = None,
      eoriNumber = None
    ),
    placeOfDispatchTrader = Some(TraderModel(
      traderExciseNumber = Some("dispatch ern"),
      traderName = Some("dispatch name"),
      address = Some(AddressModel.fromUserAddress(testUserAddress.copy(street = "dispatch street"))),
      vatNumber = None,
      eoriNumber = None
    )),
    dispatchImportOffice = Some(OfficeModel("dispatch import office")),
    complementConsigneeTrader = Some(ComplementConsigneeTraderModel("state", Some("number"))),
    deliveryPlaceTrader = Some(TraderModel(
      traderExciseNumber = Some(testErn),
      traderName = Some("destination name"),
      address = Some(AddressModel.fromUserAddress(testUserAddress.copy(street = "destination street"))),
      vatNumber = None,
      eoriNumber = None
    )),
    deliveryPlaceCustomsOffice = Some(OfficeModel("delivery place customs office")),
    competentAuthorityDispatchOffice = OfficeModel(s"GB$dispatchOfficeSuffix"),
    transportArrangerTrader = Some(TraderModel(
      traderExciseNumber = None,
      traderName = Some("arranger name"),
      address = Some(AddressModel.fromUserAddress(testUserAddress.copy(street = "arranger street"))),
      vatNumber = Some("arranger vat"),
      eoriNumber = None
    )),
    firstTransporterTrader = Some(TraderModel(
      traderExciseNumber = None,
      traderName = Some("first name"),
      address = Some(AddressModel.fromUserAddress(testUserAddress.copy(street = "first street"))),
      vatNumber = Some("first vat"),
      eoriNumber = None
    )),
    documentCertificate = Some(Seq(
      DocumentCertificateModel(
        documentType = Some("0"),
        documentReference = Some("0 reference"),
        documentDescription = None,
        referenceOfDocument = None
      )
    )),
    headerEadEsad = HeaderEadEsadModel(
      destinationType = DestinationType.TaxWarehouse,
      journeyTime = "2 hours",
      transportArrangement = TransportArranger.GoodsOwner
    ),
    transportMode = TransportModeModel(
      transportModeCode = HowMovementTransported.AirTransport,
      complementaryInformation = Some("info")
    ),
    movementGuarantee = MovementGuaranteeModel(
      guarantorTypeCode = GuarantorArranger.GoodsOwner,
      guarantorTrader = Some(Seq(TraderModel(
        traderExciseNumber = None,
        traderName = Some("guarantor name"),
        address = Some(AddressModel.fromUserAddress(testUserAddress.copy(street = "guarantor street"))),
        vatNumber = Some("guarantor vat"),
        eoriNumber = None
      )))
    ),
    bodyEadEsad = Seq(BodyEadEsadModel(
      bodyRecordUniqueReference = 1,
      exciseProductCode = testEpcWine,
      cnCode = testCnCodeWine,
      quantity = BigDecimal(1),
      grossMass = BigDecimal(3),
      netMass = BigDecimal(2),
      alcoholicStrengthByVolumeInPercentage = Some(BigDecimal(1.23)),
      degreePlato = Some(4.56),
      fiscalMark = Some("fiscal marks"),
      fiscalMarkUsedFlag = Some(true),
      designationOfOrigin = Some("The product has a Protected Designation of Origin (PDO). talkin' 'bout my deeeeeesignation"),
      independentSmallProducersDeclaration = Some(s"It is hereby certified that the alcoholic product described has been produced by an independent small producer. The producer is a self-certified independent small producer and not the consignor. Identification: $testErn"),
      sizeOfProducer = Some(BigInt(4)),
      density = Some(BigDecimal(7.89)),
      commercialDescription = Some("beans"),
      brandNameOfProducts = Some("name"),
      maturationPeriodOrAgeOfProducts = Some("really old"),
      packages = Seq(
        PackageModel(
          kindOfPackages = bulkPackagingTypes.head.packagingType.toString,
          numberOfPackages = None,
          shippingMarks = None,
          commercialSealIdentification = Some("seal type"),
          sealInformation = Some("seal info")
        )
      ),
      wineProduct = Some(
        WineProductModel(
          wineProductCategory = ItemWineCategory.ImportedWine,
          wineGrowingZoneCode = Some(ItemWineGrowingZone.CII),
          thirdCountryOfOrigin = Some(countryModelGB.code),
          otherInformation = Some("more wine info"),
          wineOperations = Some(Seq(WineOperations("op code", "choice desc")))
        )
      )
    )),
    eadEsadDraft = EadEsadDraftModel(
      localReferenceNumber = testLrn,
      invoiceNumber = "inv ref",
      invoiceDate = Some("2020-12-25"),
      originTypeCode = OriginType.TaxWarehouse,
      dateOfDispatch = "2020-10-31",
      timeOfDispatch = Some("23:59:59"),
      importSad = None
    ),
    transportDetails = Seq(
      TransportDetailsModel(
        transportUnitCode = TransportUnitType.FixedTransport,
        identityOfTransportUnits = Some("identity"),
        commercialSealIdentification = Some("seal type"),
        complementaryInformation = Some("more info"),
        sealInformation = Some("seal info")
      )
    )
  )
}
