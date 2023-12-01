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

import base.SpecBase
import fixtures.ItemFixtures
import models.requests.DataRequest
import models.response.referenceData.BulkPackagingType
import models.sections.items.ItemBulkPackagingCode.BulkLiquid
import models.sections.items.ItemGeographicalIndicationType.{NoGeographicalIndication, ProtectedDesignationOfOrigin}
import models.sections.items.ItemWineGrowingZone.CIII_A
import models.sections.items.{ItemBrandNameModel, ItemMaturationPeriodAgeModel, ItemNetGrossMassModel, ItemPackagingSealTypeModel}
import play.api.test.FakeRequest

class ItemsSectionItemSpec extends SpecBase with ItemFixtures {

  "isCompleted" - {

    "must return true" - {

      "when Bulk Wine, Imported from EU, with all mandatory and optional pages complete" in {

        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), emptyUserAnswers
          .set(ItemExciseProductCodePage(testIndex1), testEpcWine)
          .set(ItemCommodityCodePage(testIndex1), testCnCodeWine)
          .set(ItemBrandNamePage(testIndex1), ItemBrandNameModel(hasBrandName = true, Some("brand")))
          .set(ItemCommercialDescriptionPage(testIndex1), "Wine from grapes")
          .set(ItemAlcoholStrengthPage(testIndex1), BigDecimal(12.5))
          .set(ItemGeographicalIndicationChoicePage(testIndex1), ProtectedDesignationOfOrigin)
          .set(ItemGeographicalIndicationPage(testIndex1), "Italy - DOCG")
          .set(ItemQuantityPage(testIndex1), BigDecimal("1000"))
          .set(ItemNetGrossMassPage(testIndex1), ItemNetGrossMassModel(BigDecimal("2000"), BigDecimal("2105")))
          .set(ItemBulkPackagingChoicePage(testIndex1), true)
          .set(ItemBulkPackagingSelectPage(testIndex1), BulkPackagingType(BulkLiquid, "bulk"))
          .set(ItemWineOperationsChoicePage(testIndex1), testWineOperations)
          .set(ItemImportedWineChoicePage(testIndex1), true)
          .set(ItemWineGrowingZonePage(testIndex1), CIII_A)
          .set(ItemWineMoreInformationChoicePage(testIndex1), true)
          .set(ItemWineMoreInformationPage(testIndex1), Some("Info"))
          .set(ItemBulkPackagingSealChoicePage(testIndex1), true)
          .set(ItemBulkPackagingSealTypePage(testIndex1), ItemPackagingSealTypeModel("Seal", Some("Seal Info")))
        )

        ItemsSectionItem(testIndex1).isCompleted mustBe true
      }

      "when Bulk Wine, NOT Imported from EU, optional pages not supplied" in {

        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), emptyUserAnswers
          .set(ItemExciseProductCodePage(testIndex1), testEpcWine)
          .set(ItemCommodityCodePage(testIndex1), testCnCodeWine)
          .set(ItemBrandNamePage(testIndex1), ItemBrandNameModel(hasBrandName = true, Some("brand")))
          .set(ItemCommercialDescriptionPage(testIndex1), "Wine from grapes")
          .set(ItemAlcoholStrengthPage(testIndex1), BigDecimal(12.5))
          .set(ItemGeographicalIndicationChoicePage(testIndex1), NoGeographicalIndication)
          .set(ItemQuantityPage(testIndex1), BigDecimal("1000"))
          .set(ItemNetGrossMassPage(testIndex1), ItemNetGrossMassModel(BigDecimal("2000"), BigDecimal("2105")))
          .set(ItemBulkPackagingChoicePage(testIndex1), true)
          .set(ItemBulkPackagingSelectPage(testIndex1), BulkPackagingType(BulkLiquid, "bulk"))
          .set(ItemWineOperationsChoicePage(testIndex1), testWineOperations)
          .set(ItemImportedWineChoicePage(testIndex1), false)
          .set(ItemWineOriginPage(testIndex1), countryModelAU)
          .set(ItemWineMoreInformationChoicePage(testIndex1), false)
          .set(ItemBulkPackagingSealChoicePage(testIndex1), false)
        )

        ItemsSectionItem(testIndex1).isCompleted mustBe true
      }

      "when Wine delivered in individual packages, all packages complete" in {

        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), emptyUserAnswers
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
        )

        ItemsSectionItem(testIndex1).isCompleted mustBe true
      }

      "when Spirit with maturation age and all other mandatory pages" in {

        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), emptyUserAnswers
          .set(ItemExciseProductCodePage(testIndex1), testEpcSpirit)
          .set(ItemCommodityCodePage(testIndex1), testCnCodeSpirit)
          .set(ItemBrandNamePage(testIndex1), ItemBrandNameModel(hasBrandName = true, Some("brand")))
          .set(ItemCommercialDescriptionPage(testIndex1), "Cider")
          .set(ItemAlcoholStrengthPage(testIndex1), BigDecimal(12.5))
          .set(ItemMaturationPeriodAgePage(testIndex1), ItemMaturationPeriodAgeModel(hasMaturationPeriodAge = true, Some("40 years")))
          .set(ItemGeographicalIndicationChoicePage(testIndex1), NoGeographicalIndication)
          .set(ItemQuantityPage(testIndex1), BigDecimal("1000"))
          .set(ItemNetGrossMassPage(testIndex1), ItemNetGrossMassModel(BigDecimal("2000"), BigDecimal("2105")))
          .set(ItemBulkPackagingChoicePage(testIndex1), false)
          .set(ItemSelectPackagingPage(testIndex1, testPackagingIndex1), testPackageBag)
          .set(ItemPackagingQuantityPage(testIndex1, testPackagingIndex1), "400")
          .set(ItemPackagingProductTypePage(testIndex1, testPackagingIndex1), true)
          .set(ItemPackagingSealChoicePage(testIndex1, testPackagingIndex1), false)
        )

        ItemsSectionItem(testIndex1).isCompleted mustBe true
      }

      "when Energy with density and all other mandatory pages" in {

        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), emptyUserAnswers
          .set(ItemExciseProductCodePage(testIndex1), testEpcEnergyWithDensity)
          .set(ItemCommodityCodePage(testIndex1), testCnCodeEnergyWithDensity)
          .set(ItemBrandNamePage(testIndex1), ItemBrandNameModel(hasBrandName = true, Some("brand")))
          .set(ItemCommercialDescriptionPage(testIndex1), "Petrol")
          .set(ItemDensityPage(testIndex1), BigDecimal("251"))
          .set(ItemQuantityPage(testIndex1), BigDecimal("1000"))
          .set(ItemNetGrossMassPage(testIndex1), ItemNetGrossMassModel(BigDecimal("2000"), BigDecimal("2105")))
          .set(ItemBulkPackagingChoicePage(testIndex1), false)
          .set(ItemSelectPackagingPage(testIndex1, testPackagingIndex1), testPackageBag)
          .set(ItemPackagingQuantityPage(testIndex1, testPackagingIndex1), "400")
          .set(ItemPackagingProductTypePage(testIndex1, testPackagingIndex1), true)
          .set(ItemPackagingSealChoicePage(testIndex1, testPackagingIndex1), false)
        )

        ItemsSectionItem(testIndex1).isCompleted mustBe true
      }

      "when Energy that does not require density and all other mandatory pages" in {

        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), emptyUserAnswers
          .set(ItemExciseProductCodePage(testIndex1), testEpcEnergy)
          .set(ItemCommodityCodePage(testIndex1), testCnCodeEnergy)
          .set(ItemBrandNamePage(testIndex1), ItemBrandNameModel(hasBrandName = true, Some("brand")))
          .set(ItemCommercialDescriptionPage(testIndex1), "Petrol")
          .set(ItemQuantityPage(testIndex1), BigDecimal("1000"))
          .set(ItemNetGrossMassPage(testIndex1), ItemNetGrossMassModel(BigDecimal("2000"), BigDecimal("2105")))
          .set(ItemBulkPackagingChoicePage(testIndex1), false)
          .set(ItemSelectPackagingPage(testIndex1, testPackagingIndex1), testPackageBag)
          .set(ItemPackagingQuantityPage(testIndex1, testPackagingIndex1), "400")
          .set(ItemPackagingProductTypePage(testIndex1, testPackagingIndex1), true)
          .set(ItemPackagingSealChoicePage(testIndex1, testPackagingIndex1), false)
        )

        ItemsSectionItem(testIndex1).isCompleted mustBe true
      }

      "when Tobacco with fiscal marks and all other mandatory pages" in {

        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), emptyUserAnswers
          .set(ItemExciseProductCodePage(testIndex1), testEpcTobacco)
          .set(ItemCommodityCodePage(testIndex1), testCnCodeTobacco)
          .set(ItemBrandNamePage(testIndex1), ItemBrandNameModel(hasBrandName = true, Some("brand")))
          .set(ItemCommercialDescriptionPage(testIndex1), "Petrol")
          .set(ItemFiscalMarksChoicePage(testIndex1), true)
          .set(ItemFiscalMarksPage(testIndex1), "Fiscal Marks")
          .set(ItemQuantityPage(testIndex1), BigDecimal("1000"))
          .set(ItemNetGrossMassPage(testIndex1), ItemNetGrossMassModel(BigDecimal("2000"), BigDecimal("2105")))
          .set(ItemBulkPackagingChoicePage(testIndex1), false)
          .set(ItemSelectPackagingPage(testIndex1, testPackagingIndex1), testPackageBag)
          .set(ItemPackagingQuantityPage(testIndex1, testPackagingIndex1), "400")
          .set(ItemPackagingProductTypePage(testIndex1, testPackagingIndex1), true)
          .set(ItemPackagingSealChoicePage(testIndex1, testPackagingIndex1), false)
        )

        ItemsSectionItem(testIndex1).isCompleted mustBe true
      }

      "when Tobacco with fiscal marks as false and all other mandatory pages" in {

        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), emptyUserAnswers
          .set(ItemExciseProductCodePage(testIndex1), testEpcTobacco)
          .set(ItemCommodityCodePage(testIndex1), testCnCodeTobacco)
          .set(ItemBrandNamePage(testIndex1), ItemBrandNameModel(hasBrandName = true, Some("brand")))
          .set(ItemCommercialDescriptionPage(testIndex1), "Petrol")
          .set(ItemFiscalMarksChoicePage(testIndex1), false)
          .set(ItemQuantityPage(testIndex1), BigDecimal("1000"))
          .set(ItemNetGrossMassPage(testIndex1), ItemNetGrossMassModel(BigDecimal("2000"), BigDecimal("2105")))
          .set(ItemBulkPackagingChoicePage(testIndex1), false)
          .set(ItemSelectPackagingPage(testIndex1, testPackagingIndex1), testPackageBag)
          .set(ItemPackagingQuantityPage(testIndex1, testPackagingIndex1), "400")
          .set(ItemPackagingProductTypePage(testIndex1, testPackagingIndex1), true)
          .set(ItemPackagingSealChoicePage(testIndex1, testPackagingIndex1), false)
        )

        ItemsSectionItem(testIndex1).isCompleted mustBe true
      }
    }

    "must return false" - {

      "when Tobacco and fiscal marks is true but not answer exists missing" in {

        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), emptyUserAnswers
          .set(ItemExciseProductCodePage(testIndex1), testEpcTobacco)
          .set(ItemCommodityCodePage(testIndex1), testCnCodeTobacco)
          .set(ItemBrandNamePage(testIndex1), ItemBrandNameModel(hasBrandName = true, Some("brand")))
          .set(ItemCommercialDescriptionPage(testIndex1), "Petrol")
          .set(ItemFiscalMarksChoicePage(testIndex1), true)
          .set(ItemQuantityPage(testIndex1), BigDecimal("1000"))
          .set(ItemNetGrossMassPage(testIndex1), ItemNetGrossMassModel(BigDecimal("2000"), BigDecimal("2105")))
          .set(ItemBulkPackagingChoicePage(testIndex1), false)
          .set(ItemSelectPackagingPage(testIndex1, testPackagingIndex1), testPackageBag)
          .set(ItemPackagingQuantityPage(testIndex1, testPackagingIndex1), "400")
          .set(ItemPackagingProductTypePage(testIndex1, testPackagingIndex1), true)
          .set(ItemPackagingSealChoicePage(testIndex1, testPackagingIndex1), false)
        )

        ItemsSectionItem(testIndex1).isCompleted mustBe false
      }

      "when Tobacco and fiscal marks is missing all together" in {

        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), emptyUserAnswers
          .set(ItemExciseProductCodePage(testIndex1), testEpcTobacco)
          .set(ItemCommodityCodePage(testIndex1), testCnCodeTobacco)
          .set(ItemBrandNamePage(testIndex1), ItemBrandNameModel(hasBrandName = true, Some("brand")))
          .set(ItemCommercialDescriptionPage(testIndex1), "Petrol")
          .set(ItemFiscalMarksChoicePage(testIndex1), true)
          .set(ItemQuantityPage(testIndex1), BigDecimal("1000"))
          .set(ItemNetGrossMassPage(testIndex1), ItemNetGrossMassModel(BigDecimal("2000"), BigDecimal("2105")))
          .set(ItemBulkPackagingChoicePage(testIndex1), false)
          .set(ItemSelectPackagingPage(testIndex1, testPackagingIndex1), testPackageBag)
          .set(ItemPackagingQuantityPage(testIndex1, testPackagingIndex1), "400")
          .set(ItemPackagingProductTypePage(testIndex1, testPackagingIndex1), true)
          .set(ItemPackagingSealChoicePage(testIndex1, testPackagingIndex1), false)
        )

        ItemsSectionItem(testIndex1).isCompleted mustBe false
      }

      "when Energy that requires density but has page missing - all other mandatory pages exist" in {

        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), emptyUserAnswers
          .set(ItemExciseProductCodePage(testIndex1), testEpcEnergyWithDensity)
          .set(ItemCommodityCodePage(testIndex1), testCnCodeEnergyWithDensity)
          .set(ItemBrandNamePage(testIndex1), ItemBrandNameModel(hasBrandName = true, Some("brand")))
          .set(ItemCommercialDescriptionPage(testIndex1), "Petrol")
          .set(ItemQuantityPage(testIndex1), BigDecimal("1000"))
          .set(ItemNetGrossMassPage(testIndex1), ItemNetGrossMassModel(BigDecimal("2000"), BigDecimal("2105")))
          .set(ItemBulkPackagingChoicePage(testIndex1), false)
          .set(ItemSelectPackagingPage(testIndex1, testPackagingIndex1), testPackageBag)
          .set(ItemPackagingQuantityPage(testIndex1, testPackagingIndex1), "400")
          .set(ItemPackagingProductTypePage(testIndex1, testPackagingIndex1), true)
          .set(ItemPackagingSealChoicePage(testIndex1, testPackagingIndex1), false)
        )

        ItemsSectionItem(testIndex1).isCompleted mustBe false
      }

      "when Spirit with maturation age missing - all other mandatory pages exist" in {

        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), emptyUserAnswers
          .set(ItemExciseProductCodePage(testIndex1), testEpcSpirit)
          .set(ItemCommodityCodePage(testIndex1), testCnCodeSpirit)
          .set(ItemBrandNamePage(testIndex1), ItemBrandNameModel(hasBrandName = true, Some("brand")))
          .set(ItemCommercialDescriptionPage(testIndex1), "Cider")
          .set(ItemAlcoholStrengthPage(testIndex1), BigDecimal(12.5))
          .set(ItemGeographicalIndicationChoicePage(testIndex1), NoGeographicalIndication)
          .set(ItemQuantityPage(testIndex1), BigDecimal("1000"))
          .set(ItemNetGrossMassPage(testIndex1), ItemNetGrossMassModel(BigDecimal("2000"), BigDecimal("2105")))
          .set(ItemBulkPackagingChoicePage(testIndex1), false)
          .set(ItemSelectPackagingPage(testIndex1, testPackagingIndex1), testPackageBag)
          .set(ItemPackagingQuantityPage(testIndex1, testPackagingIndex1), "400")
          .set(ItemPackagingProductTypePage(testIndex1, testPackagingIndex1), true)
          .set(ItemPackagingSealChoicePage(testIndex1, testPackagingIndex1), false)
        )

        ItemsSectionItem(testIndex1).isCompleted mustBe false
      }

      "when item packaging exists which is incomplete" in {

        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), emptyUserAnswers
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
          .set(ItemSelectPackagingPage(testIndex1, testPackagingIndex2), testPackageBag)
        )

        ItemsSectionItem(testIndex1).isCompleted mustBe false
      }

      "when bulk item where seal type is missing" in {

        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), emptyUserAnswers
          .set(ItemExciseProductCodePage(testIndex1), testEpcWine)
          .set(ItemCommodityCodePage(testIndex1), testCnCodeWine)
          .set(ItemBrandNamePage(testIndex1), ItemBrandNameModel(hasBrandName = true, Some("brand")))
          .set(ItemCommercialDescriptionPage(testIndex1), "Wine from grapes")
          .set(ItemAlcoholStrengthPage(testIndex1), BigDecimal(12.5))
          .set(ItemGeographicalIndicationChoicePage(testIndex1), ProtectedDesignationOfOrigin)
          .set(ItemGeographicalIndicationPage(testIndex1), "Italy - DOCG")
          .set(ItemQuantityPage(testIndex1), BigDecimal("1000"))
          .set(ItemNetGrossMassPage(testIndex1), ItemNetGrossMassModel(BigDecimal("2000"), BigDecimal("2105")))
          .set(ItemBulkPackagingChoicePage(testIndex1), true)
          .set(ItemBulkPackagingSelectPage(testIndex1), BulkPackagingType(BulkLiquid, "bulk"))
          .set(ItemWineOperationsChoicePage(testIndex1), testWineOperations)
          .set(ItemImportedWineChoicePage(testIndex1), true)
          .set(ItemWineGrowingZonePage(testIndex1), CIII_A)
          .set(ItemWineMoreInformationChoicePage(testIndex1), true)
          .set(ItemWineMoreInformationPage(testIndex1), Some("Info"))
          .set(ItemBulkPackagingSealChoicePage(testIndex1), true)
        )

        ItemsSectionItem(testIndex1).isCompleted mustBe false
      }

      "when bulk item where seal choice is missing" in {

        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), emptyUserAnswers
          .set(ItemExciseProductCodePage(testIndex1), testEpcWine)
          .set(ItemCommodityCodePage(testIndex1), testCnCodeWine)
          .set(ItemBrandNamePage(testIndex1), ItemBrandNameModel(hasBrandName = true, Some("brand")))
          .set(ItemCommercialDescriptionPage(testIndex1), "Wine from grapes")
          .set(ItemAlcoholStrengthPage(testIndex1), BigDecimal(12.5))
          .set(ItemGeographicalIndicationChoicePage(testIndex1), ProtectedDesignationOfOrigin)
          .set(ItemGeographicalIndicationPage(testIndex1), "Italy - DOCG")
          .set(ItemQuantityPage(testIndex1), BigDecimal("1000"))
          .set(ItemNetGrossMassPage(testIndex1), ItemNetGrossMassModel(BigDecimal("2000"), BigDecimal("2105")))
          .set(ItemBulkPackagingChoicePage(testIndex1), true)
          .set(ItemBulkPackagingSelectPage(testIndex1), BulkPackagingType(BulkLiquid, "bulk"))
          .set(ItemWineOperationsChoicePage(testIndex1), testWineOperations)
          .set(ItemImportedWineChoicePage(testIndex1), true)
          .set(ItemWineGrowingZonePage(testIndex1), CIII_A)
          .set(ItemWineMoreInformationChoicePage(testIndex1), true)
          .set(ItemWineMoreInformationPage(testIndex1), Some("Info"))
        )

        ItemsSectionItem(testIndex1).isCompleted mustBe false
      }

      "when bulk wine where more information is missing" in {

        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), emptyUserAnswers
          .set(ItemExciseProductCodePage(testIndex1), testEpcWine)
          .set(ItemCommodityCodePage(testIndex1), testCnCodeWine)
          .set(ItemBrandNamePage(testIndex1), ItemBrandNameModel(hasBrandName = true, Some("brand")))
          .set(ItemCommercialDescriptionPage(testIndex1), "Wine from grapes")
          .set(ItemAlcoholStrengthPage(testIndex1), BigDecimal(12.5))
          .set(ItemGeographicalIndicationChoicePage(testIndex1), ProtectedDesignationOfOrigin)
          .set(ItemGeographicalIndicationPage(testIndex1), "Italy - DOCG")
          .set(ItemQuantityPage(testIndex1), BigDecimal("1000"))
          .set(ItemNetGrossMassPage(testIndex1), ItemNetGrossMassModel(BigDecimal("2000"), BigDecimal("2105")))
          .set(ItemBulkPackagingChoicePage(testIndex1), true)
          .set(ItemBulkPackagingSelectPage(testIndex1), BulkPackagingType(BulkLiquid, "bulk"))
          .set(ItemWineOperationsChoicePage(testIndex1), testWineOperations)
          .set(ItemImportedWineChoicePage(testIndex1), true)
          .set(ItemWineGrowingZonePage(testIndex1), CIII_A)
          .set(ItemWineMoreInformationChoicePage(testIndex1), true)
          .set(ItemBulkPackagingSealChoicePage(testIndex1), false)
        )

        ItemsSectionItem(testIndex1).isCompleted mustBe false
      }

      "when bulk wine where more information choice is missing" in {

        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), emptyUserAnswers
          .set(ItemExciseProductCodePage(testIndex1), testEpcWine)
          .set(ItemCommodityCodePage(testIndex1), testCnCodeWine)
          .set(ItemBrandNamePage(testIndex1), ItemBrandNameModel(hasBrandName = true, Some("brand")))
          .set(ItemCommercialDescriptionPage(testIndex1), "Wine from grapes")
          .set(ItemAlcoholStrengthPage(testIndex1), BigDecimal(12.5))
          .set(ItemGeographicalIndicationChoicePage(testIndex1), ProtectedDesignationOfOrigin)
          .set(ItemGeographicalIndicationPage(testIndex1), "Italy - DOCG")
          .set(ItemQuantityPage(testIndex1), BigDecimal("1000"))
          .set(ItemNetGrossMassPage(testIndex1), ItemNetGrossMassModel(BigDecimal("2000"), BigDecimal("2105")))
          .set(ItemBulkPackagingChoicePage(testIndex1), true)
          .set(ItemBulkPackagingSelectPage(testIndex1), BulkPackagingType(BulkLiquid, "bulk"))
          .set(ItemWineOperationsChoicePage(testIndex1), testWineOperations)
          .set(ItemImportedWineChoicePage(testIndex1), true)
          .set(ItemWineGrowingZonePage(testIndex1), CIII_A)
          .set(ItemBulkPackagingSealChoicePage(testIndex1), false)
        )

        ItemsSectionItem(testIndex1).isCompleted mustBe false
      }

      "when bulk wine where imported from EU and wine growing zone is missing" in {

        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), emptyUserAnswers
          .set(ItemExciseProductCodePage(testIndex1), testEpcWine)
          .set(ItemCommodityCodePage(testIndex1), testCnCodeWine)
          .set(ItemBrandNamePage(testIndex1), ItemBrandNameModel(hasBrandName = true, Some("brand")))
          .set(ItemCommercialDescriptionPage(testIndex1), "Wine from grapes")
          .set(ItemAlcoholStrengthPage(testIndex1), BigDecimal(12.5))
          .set(ItemGeographicalIndicationChoicePage(testIndex1), ProtectedDesignationOfOrigin)
          .set(ItemGeographicalIndicationPage(testIndex1), "Italy - DOCG")
          .set(ItemQuantityPage(testIndex1), BigDecimal("1000"))
          .set(ItemNetGrossMassPage(testIndex1), ItemNetGrossMassModel(BigDecimal("2000"), BigDecimal("2105")))
          .set(ItemBulkPackagingChoicePage(testIndex1), true)
          .set(ItemBulkPackagingSelectPage(testIndex1), BulkPackagingType(BulkLiquid, "bulk"))
          .set(ItemWineOperationsChoicePage(testIndex1), testWineOperations)
          .set(ItemImportedWineChoicePage(testIndex1), true)
          .set(ItemWineMoreInformationChoicePage(testIndex1), false)
          .set(ItemBulkPackagingSealChoicePage(testIndex1), false)
        )

        ItemsSectionItem(testIndex1).isCompleted mustBe false
      }

      "when bulk wine where wine operations choice is missing" in {

        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), emptyUserAnswers
          .set(ItemExciseProductCodePage(testIndex1), testEpcWine)
          .set(ItemCommodityCodePage(testIndex1), testCnCodeWine)
          .set(ItemBrandNamePage(testIndex1), ItemBrandNameModel(hasBrandName = true, Some("brand")))
          .set(ItemCommercialDescriptionPage(testIndex1), "Wine from grapes")
          .set(ItemAlcoholStrengthPage(testIndex1), BigDecimal(12.5))
          .set(ItemGeographicalIndicationChoicePage(testIndex1), ProtectedDesignationOfOrigin)
          .set(ItemGeographicalIndicationPage(testIndex1), "Italy - DOCG")
          .set(ItemQuantityPage(testIndex1), BigDecimal("1000"))
          .set(ItemNetGrossMassPage(testIndex1), ItemNetGrossMassModel(BigDecimal("2000"), BigDecimal("2105")))
          .set(ItemBulkPackagingChoicePage(testIndex1), true)
          .set(ItemBulkPackagingSelectPage(testIndex1), BulkPackagingType(BulkLiquid, "bulk"))
          .set(ItemImportedWineChoicePage(testIndex1), false)
          .set(ItemWineMoreInformationChoicePage(testIndex1), false)
          .set(ItemBulkPackagingSealChoicePage(testIndex1), false)
        )

        ItemsSectionItem(testIndex1).isCompleted mustBe false
      }

      "when bulk wine where bulk packaging select is missing" in {

        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), emptyUserAnswers
          .set(ItemExciseProductCodePage(testIndex1), testEpcWine)
          .set(ItemCommodityCodePage(testIndex1), testCnCodeWine)
          .set(ItemBrandNamePage(testIndex1), ItemBrandNameModel(hasBrandName = true, Some("brand")))
          .set(ItemCommercialDescriptionPage(testIndex1), "Wine from grapes")
          .set(ItemAlcoholStrengthPage(testIndex1), BigDecimal(12.5))
          .set(ItemGeographicalIndicationChoicePage(testIndex1), ProtectedDesignationOfOrigin)
          .set(ItemGeographicalIndicationPage(testIndex1), "Italy - DOCG")
          .set(ItemQuantityPage(testIndex1), BigDecimal("1000"))
          .set(ItemNetGrossMassPage(testIndex1), ItemNetGrossMassModel(BigDecimal("2000"), BigDecimal("2105")))
          .set(ItemBulkPackagingChoicePage(testIndex1), true)
          .set(ItemWineOperationsChoicePage(testIndex1), testWineOperations)
          .set(ItemImportedWineChoicePage(testIndex1), false)
          .set(ItemWineMoreInformationChoicePage(testIndex1), false)
          .set(ItemBulkPackagingSealChoicePage(testIndex1), false)
        )

        ItemsSectionItem(testIndex1).isCompleted mustBe false
      }

      "when bulk wine where bulk packaging choice is missing" in {

        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), emptyUserAnswers
          .set(ItemExciseProductCodePage(testIndex1), testEpcWine)
          .set(ItemCommodityCodePage(testIndex1), testCnCodeWine)
          .set(ItemBrandNamePage(testIndex1), ItemBrandNameModel(hasBrandName = true, Some("brand")))
          .set(ItemCommercialDescriptionPage(testIndex1), "Wine from grapes")
          .set(ItemAlcoholStrengthPage(testIndex1), BigDecimal(12.5))
          .set(ItemGeographicalIndicationChoicePage(testIndex1), ProtectedDesignationOfOrigin)
          .set(ItemGeographicalIndicationPage(testIndex1), "Italy - DOCG")
          .set(ItemQuantityPage(testIndex1), BigDecimal("1000"))
          .set(ItemNetGrossMassPage(testIndex1), ItemNetGrossMassModel(BigDecimal("2000"), BigDecimal("2105")))
          .set(ItemWineOperationsChoicePage(testIndex1), testWineOperations)
          .set(ItemImportedWineChoicePage(testIndex1), false)
          .set(ItemWineMoreInformationChoicePage(testIndex1), false)
          .set(ItemBulkPackagingSealChoicePage(testIndex1), false)
        )

        ItemsSectionItem(testIndex1).isCompleted mustBe false
      }

      "when Excise Product Code is missing" in {

        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), emptyUserAnswers
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
        )

        ItemsSectionItem(testIndex1).isCompleted mustBe false
      }

      "when Commodity Code is missing" in {

        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), emptyUserAnswers
          .set(ItemExciseProductCodePage(testIndex1), testEpcWine)
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
        )

        ItemsSectionItem(testIndex1).isCompleted mustBe false
      }

      "when Brand Name is missing" in {

        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), emptyUserAnswers
          .set(ItemExciseProductCodePage(testIndex1), testEpcWine)
          .set(ItemCommodityCodePage(testIndex1), testCnCodeWine)
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
        )

        ItemsSectionItem(testIndex1).isCompleted mustBe false
      }

      "when Commercial Description is missing" in {

        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), emptyUserAnswers
          .set(ItemExciseProductCodePage(testIndex1), testEpcWine)
          .set(ItemCommodityCodePage(testIndex1), testCnCodeWine)
          .set(ItemBrandNamePage(testIndex1), ItemBrandNameModel(hasBrandName = true, Some("brand")))
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
        )

        ItemsSectionItem(testIndex1).isCompleted mustBe false
      }

      "when Quantity is missing" in {

        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), emptyUserAnswers
          .set(ItemExciseProductCodePage(testIndex1), testEpcWine)
          .set(ItemCommodityCodePage(testIndex1), testCnCodeWine)
          .set(ItemBrandNamePage(testIndex1), ItemBrandNameModel(hasBrandName = true, Some("brand")))
          .set(ItemCommercialDescriptionPage(testIndex1), "Wine from grapes")
          .set(ItemAlcoholStrengthPage(testIndex1), BigDecimal(12.5))
          .set(ItemGeographicalIndicationChoicePage(testIndex1), NoGeographicalIndication)
          .set(ItemNetGrossMassPage(testIndex1), ItemNetGrossMassModel(BigDecimal("2000"), BigDecimal("2105")))
          .set(ItemBulkPackagingChoicePage(testIndex1), false)
          .set(ItemImportedWineChoicePage(testIndex1), true)
          .set(ItemWineMoreInformationChoicePage(testIndex1), false)
          .set(ItemSelectPackagingPage(testIndex1, testPackagingIndex1), testPackageBag)
          .set(ItemPackagingQuantityPage(testIndex1, testPackagingIndex1), "400")
          .set(ItemPackagingProductTypePage(testIndex1, testPackagingIndex1), true)
          .set(ItemPackagingSealChoicePage(testIndex1, testPackagingIndex1), false)
        )

        ItemsSectionItem(testIndex1).isCompleted mustBe false
      }

      "when Net and Gross Mass is missing" in {

        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), emptyUserAnswers
          .set(ItemExciseProductCodePage(testIndex1), testEpcWine)
          .set(ItemCommodityCodePage(testIndex1), testCnCodeWine)
          .set(ItemBrandNamePage(testIndex1), ItemBrandNameModel(hasBrandName = true, Some("brand")))
          .set(ItemCommercialDescriptionPage(testIndex1), "Wine from grapes")
          .set(ItemAlcoholStrengthPage(testIndex1), BigDecimal(12.5))
          .set(ItemGeographicalIndicationChoicePage(testIndex1), NoGeographicalIndication)
          .set(ItemQuantityPage(testIndex1), BigDecimal("1000"))
          .set(ItemBulkPackagingChoicePage(testIndex1), false)
          .set(ItemImportedWineChoicePage(testIndex1), true)
          .set(ItemWineMoreInformationChoicePage(testIndex1), false)
          .set(ItemSelectPackagingPage(testIndex1, testPackagingIndex1), testPackageBag)
          .set(ItemPackagingQuantityPage(testIndex1, testPackagingIndex1), "400")
          .set(ItemPackagingProductTypePage(testIndex1, testPackagingIndex1), true)
          .set(ItemPackagingSealChoicePage(testIndex1, testPackagingIndex1), false)
        )

        ItemsSectionItem(testIndex1).isCompleted mustBe false
      }

      "when low alcohol and Small Independent Producer annual production is missing" in {

        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), emptyUserAnswers
          .set(ItemExciseProductCodePage(testIndex1), testEpcBeer)
          .set(ItemCommodityCodePage(testIndex1), testCnCodeBeer)
          .set(ItemBrandNamePage(testIndex1), ItemBrandNameModel(hasBrandName = true, Some("brand")))
          .set(ItemCommercialDescriptionPage(testIndex1), "Beer from hops")
          .set(ItemAlcoholStrengthPage(testIndex1), BigDecimal(8.4))
          .set(ItemSmallIndependentProducerPage(testIndex1), true)
          .set(ItemProducerSizePage(testIndex1), BigInt("300"))
          .set(ItemQuantityPage(testIndex1), BigDecimal("1000"))
          .set(ItemBulkPackagingChoicePage(testIndex1), false)
          .set(ItemImportedWineChoicePage(testIndex1), true)
          .set(ItemWineMoreInformationChoicePage(testIndex1), false)
          .set(ItemSelectPackagingPage(testIndex1, testPackagingIndex1), testPackageBag)
          .set(ItemPackagingQuantityPage(testIndex1, testPackagingIndex1), "400")
          .set(ItemPackagingProductTypePage(testIndex1, testPackagingIndex1), true)
          .set(ItemPackagingSealChoicePage(testIndex1, testPackagingIndex1), false)
        )

        ItemsSectionItem(testIndex1).isCompleted mustBe false
      }

      "when low alcohol and Small Independent Producer choice is missing" in {

        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), emptyUserAnswers
          .set(ItemExciseProductCodePage(testIndex1), testEpcBeer)
          .set(ItemCommodityCodePage(testIndex1), testCnCodeBeer)
          .set(ItemBrandNamePage(testIndex1), ItemBrandNameModel(hasBrandName = true, Some("brand")))
          .set(ItemCommercialDescriptionPage(testIndex1), "Beer from hops")
          .set(ItemAlcoholStrengthPage(testIndex1), BigDecimal(8.4))
          .set(ItemQuantityPage(testIndex1), BigDecimal("1000"))
          .set(ItemBulkPackagingChoicePage(testIndex1), false)
          .set(ItemImportedWineChoicePage(testIndex1), true)
          .set(ItemWineMoreInformationChoicePage(testIndex1), false)
          .set(ItemSelectPackagingPage(testIndex1, testPackagingIndex1), testPackageBag)
          .set(ItemPackagingQuantityPage(testIndex1, testPackagingIndex1), "400")
          .set(ItemPackagingProductTypePage(testIndex1, testPackagingIndex1), true)
          .set(ItemPackagingSealChoicePage(testIndex1, testPackagingIndex1), false)
        )

        ItemsSectionItem(testIndex1).isCompleted mustBe false
      }

      "when Beer and User is Northern Ireland (XI) and Degrees Plato is missing" in {

        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), emptyUserAnswers.copy(ern = testNorthernIrelandErn)
          .set(ItemExciseProductCodePage(testIndex1), testEpcBeer)
          .set(ItemCommodityCodePage(testIndex1), testCnCodeBeer)
          .set(ItemBrandNamePage(testIndex1), ItemBrandNameModel(hasBrandName = true, Some("brand")))
          .set(ItemCommercialDescriptionPage(testIndex1), "Beer from hops")
          .set(ItemAlcoholStrengthPage(testIndex1), BigDecimal(12.5))
          .set(ItemQuantityPage(testIndex1), BigDecimal("1000"))
          .set(ItemBulkPackagingChoicePage(testIndex1), false)
          .set(ItemImportedWineChoicePage(testIndex1), true)
          .set(ItemWineMoreInformationChoicePage(testIndex1), false)
          .set(ItemSelectPackagingPage(testIndex1, testPackagingIndex1), testPackageBag)
          .set(ItemPackagingQuantityPage(testIndex1, testPackagingIndex1), "400")
          .set(ItemPackagingProductTypePage(testIndex1, testPackagingIndex1), true)
          .set(ItemPackagingSealChoicePage(testIndex1, testPackagingIndex1), false)
        )

        ItemsSectionItem(testIndex1).isCompleted mustBe false
      }
    }
  }
}
