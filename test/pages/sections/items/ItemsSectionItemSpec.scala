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
import fixtures.{ItemFixtures, MovementSubmissionFailureFixtures}
import models.GoodsType.{Beer, Energy, Spirits}
import models.requests.DataRequest
import models.response.referenceData.BulkPackagingType
import models.sections.items.ItemBulkPackagingCode.BulkLiquid
import models.sections.items.ItemGeographicalIndicationType.{NoGeographicalIndication, ProtectedDesignationOfOrigin}
import models.sections.items.ItemSmallIndependentProducerType.{NotApplicable, SelfCertifiedIndependentSmallProducerAndNotConsignor}
import models.sections.items.ItemWineGrowingZone.CIII_A
import models.sections.items.ItemWineProductCategory.{ImportedWine, Other}
import models.sections.items._
import models.{GoodsType, UserAnswers}
import play.api.test.FakeRequest

class ItemsSectionItemSpec extends SpecBase with ItemFixtures with MovementSubmissionFailureFixtures {

  val section: ItemsSectionItem = ItemsSectionItem(testIndex1)

  "isCompleted" - {

    "must return true" - {

      "when Bulk Wine, Imported from EU, with all mandatory and optional pages complete" in {

        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), emptyUserAnswers
          .set(ItemExciseProductCodePage(testIndex1), testEpcWine)
          .set(ItemCommodityCodePage(testIndex1), testCnCodeWine)
          .set(ItemBrandNamePage(testIndex1), ItemBrandNameModel(hasBrandName = true, Some("brand")))
          .set(ItemCommercialDescriptionPage(testIndex1), "Wine from grapes")
          .set(ItemAlcoholStrengthPage(testIndex1), BigDecimal(12.5))
          .set(ItemDesignationOfOriginPage(testIndex1), ItemDesignationOfOriginModel(ProtectedDesignationOfOrigin, Some("Italy - DOCG"), None))
          .set(ItemQuantityPage(testIndex1), BigDecimal("1000"))
          .set(ItemNetGrossMassPage(testIndex1), ItemNetGrossMassModel(BigDecimal("2000"), BigDecimal("2105")))
          .set(ItemBulkPackagingChoicePage(testIndex1), true)
          .set(ItemBulkPackagingSelectPage(testIndex1), BulkPackagingType(BulkLiquid, "bulk"))
          .set(ItemWineOperationsChoicePage(testIndex1), testWineOperations)
          .set(ItemWineProductCategoryPage(testIndex1), Other)
          .set(ItemWineGrowingZonePage(testIndex1), CIII_A)
          .set(ItemWineMoreInformationChoicePage(testIndex1), true)
          .set(ItemWineMoreInformationPage(testIndex1), Some("Info"))
          .set(ItemBulkPackagingSealChoicePage(testIndex1), true)
          .set(ItemBulkPackagingSealTypePage(testIndex1), ItemPackagingSealTypeModel("Seal", Some("Seal Info")))
        )

        section.isCompleted mustBe true
      }

      "when Bulk Wine, NOT ImportedWine from EU, optional pages not supplied" in {

        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), emptyUserAnswers
          .set(ItemExciseProductCodePage(testIndex1), testEpcWine)
          .set(ItemCommodityCodePage(testIndex1), testCnCodeWine)
          .set(ItemBrandNamePage(testIndex1), ItemBrandNameModel(hasBrandName = true, Some("brand")))
          .set(ItemCommercialDescriptionPage(testIndex1), "Wine from grapes")
          .set(ItemAlcoholStrengthPage(testIndex1), BigDecimal(12.5))
          .set(ItemDesignationOfOriginPage(testIndex1), ItemDesignationOfOriginModel(NoGeographicalIndication, None, None))
          .set(ItemQuantityPage(testIndex1), BigDecimal("1000"))
          .set(ItemNetGrossMassPage(testIndex1), ItemNetGrossMassModel(BigDecimal("2000"), BigDecimal("2105")))
          .set(ItemBulkPackagingChoicePage(testIndex1), true)
          .set(ItemBulkPackagingSelectPage(testIndex1), BulkPackagingType(BulkLiquid, "bulk"))
          .set(ItemWineOperationsChoicePage(testIndex1), testWineOperations)
          .set(ItemWineProductCategoryPage(testIndex1), ImportedWine)
          .set(ItemWineOriginPage(testIndex1), countryModelAU)
          .set(ItemWineMoreInformationChoicePage(testIndex1), false)
          .set(ItemBulkPackagingSealChoicePage(testIndex1), false)
        )

        section.isCompleted mustBe true
      }

      "when Wine delivered in individual packages, all packages complete" in {

        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), emptyUserAnswers
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
        )

        section.isCompleted mustBe true
      }

      "when Spirituous Beverages (S200) with maturation age and all other mandatory pages" in {

        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), emptyUserAnswers
          .set(ItemExciseProductCodePage(testIndex1), testEpcSpirit)
          .set(ItemCommodityCodePage(testIndex1), testCnCodeSpirit)
          .set(ItemBrandNamePage(testIndex1), ItemBrandNameModel(hasBrandName = true, Some("brand")))
          .set(ItemCommercialDescriptionPage(testIndex1), "Cider")
          .set(ItemAlcoholStrengthPage(testIndex1), BigDecimal(12.5))
          .set(ItemMaturationPeriodAgePage(testIndex1), ItemMaturationPeriodAgeModel(hasMaturationPeriodAge = true, Some("40 years")))
          .set(ItemDesignationOfOriginPage(testIndex1), ItemDesignationOfOriginModel(NoGeographicalIndication, None, None))
          .set(ItemQuantityPage(testIndex1), BigDecimal("1000"))
          .set(ItemNetGrossMassPage(testIndex1), ItemNetGrossMassModel(BigDecimal("2000"), BigDecimal("2105")))
          .set(ItemBulkPackagingChoicePage(testIndex1), false)
          .set(ItemSelectPackagingPage(testIndex1, testPackagingIndex1), testPackageBag)
          .set(ItemPackagingQuantityPage(testIndex1, testPackagingIndex1), "400")
          .set(ItemPackagingShippingMarksChoicePage(testIndex1, testPackagingIndex1), false)
          .set(ItemPackagingSealChoicePage(testIndex1, testPackagingIndex1), false)
        )

        section.isCompleted mustBe true
      }

      "when Spirit but not Spirituous Beverages (S200) and all other mandatory pages" in {

        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), emptyUserAnswers
          .set(ItemExciseProductCodePage(testIndex1), "S500")
          .set(ItemCommodityCodePage(testIndex1), testCnCodeSpirit)
          .set(ItemBrandNamePage(testIndex1), ItemBrandNameModel(hasBrandName = true, Some("brand")))
          .set(ItemCommercialDescriptionPage(testIndex1), "Cider")
          .set(ItemAlcoholStrengthPage(testIndex1), BigDecimal(12.5))
          .set(ItemDesignationOfOriginPage(testIndex1), ItemDesignationOfOriginModel(NoGeographicalIndication, None, None))
          .set(ItemQuantityPage(testIndex1), BigDecimal("1000"))
          .set(ItemNetGrossMassPage(testIndex1), ItemNetGrossMassModel(BigDecimal("2000"), BigDecimal("2105")))
          .set(ItemBulkPackagingChoicePage(testIndex1), false)
          .set(ItemSelectPackagingPage(testIndex1, testPackagingIndex1), testPackageBag)
          .set(ItemPackagingQuantityPage(testIndex1, testPackagingIndex1), "400")
          .set(ItemPackagingShippingMarksChoicePage(testIndex1, testPackagingIndex1), false)
          .set(ItemPackagingSealChoicePage(testIndex1, testPackagingIndex1), false)
        )

        section.isCompleted mustBe true
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
          .set(ItemPackagingShippingMarksChoicePage(testIndex1, testPackagingIndex1), false)
          .set(ItemPackagingSealChoicePage(testIndex1, testPackagingIndex1), false)
        )

        section.isCompleted mustBe true
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
          .set(ItemPackagingShippingMarksChoicePage(testIndex1, testPackagingIndex1), false)
          .set(ItemPackagingSealChoicePage(testIndex1, testPackagingIndex1), false)
        )

        section.isCompleted mustBe true
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
          .set(ItemPackagingShippingMarksChoicePage(testIndex1, testPackagingIndex1), false)
          .set(ItemPackagingSealChoicePage(testIndex1, testPackagingIndex1), false)
        )

        section.isCompleted mustBe true
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
          .set(ItemPackagingShippingMarksChoicePage(testIndex1, testPackagingIndex1), false)
          .set(ItemPackagingSealChoicePage(testIndex1, testPackagingIndex1), false)
        )

        section.isCompleted mustBe true
      }
    }

    "must return false" - {

      "when Tobacco and fiscal marks is true but no fiscal marks provided" in {

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
          .set(ItemPackagingShippingMarksChoicePage(testIndex1, testPackagingIndex1), false)
          .set(ItemPackagingSealChoicePage(testIndex1, testPackagingIndex1), false)
        )

        section.isCompleted mustBe false
      }

      "when Tobacco and fiscal marks is missing all together" in {

        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), emptyUserAnswers
          .set(ItemExciseProductCodePage(testIndex1), testEpcTobacco)
          .set(ItemCommodityCodePage(testIndex1), testCnCodeTobacco)
          .set(ItemBrandNamePage(testIndex1), ItemBrandNameModel(hasBrandName = true, Some("brand")))
          .set(ItemCommercialDescriptionPage(testIndex1), "Petrol")
          .set(ItemQuantityPage(testIndex1), BigDecimal("1000"))
          .set(ItemNetGrossMassPage(testIndex1), ItemNetGrossMassModel(BigDecimal("2000"), BigDecimal("2105")))
          .set(ItemBulkPackagingChoicePage(testIndex1), false)
          .set(ItemSelectPackagingPage(testIndex1, testPackagingIndex1), testPackageBag)
          .set(ItemPackagingQuantityPage(testIndex1, testPackagingIndex1), "400")
          .set(ItemPackagingShippingMarksChoicePage(testIndex1, testPackagingIndex1), false)
          .set(ItemPackagingSealChoicePage(testIndex1, testPackagingIndex1), false)
        )

        section.isCompleted mustBe false
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
          .set(ItemPackagingShippingMarksChoicePage(testIndex1, testPackagingIndex1), false)
          .set(ItemPackagingSealChoicePage(testIndex1, testPackagingIndex1), false)
        )

        section.isCompleted mustBe false
      }

      "when Spirituous Beverages (S200) with maturation age missing - all other mandatory pages exist" in {

        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), emptyUserAnswers
          .set(ItemExciseProductCodePage(testIndex1), testEpcSpirit)
          .set(ItemCommodityCodePage(testIndex1), testCnCodeSpirit)
          .set(ItemBrandNamePage(testIndex1), ItemBrandNameModel(hasBrandName = true, Some("brand")))
          .set(ItemCommercialDescriptionPage(testIndex1), "Cider")
          .set(ItemAlcoholStrengthPage(testIndex1), BigDecimal(12.5))
          .set(ItemDesignationOfOriginPage(testIndex1), ItemDesignationOfOriginModel(NoGeographicalIndication, None, None))
          .set(ItemQuantityPage(testIndex1), BigDecimal("1000"))
          .set(ItemNetGrossMassPage(testIndex1), ItemNetGrossMassModel(BigDecimal("2000"), BigDecimal("2105")))
          .set(ItemBulkPackagingChoicePage(testIndex1), false)
          .set(ItemSelectPackagingPage(testIndex1, testPackagingIndex1), testPackageBag)
          .set(ItemPackagingQuantityPage(testIndex1, testPackagingIndex1), "400")
          .set(ItemPackagingShippingMarksChoicePage(testIndex1, testPackagingIndex1), false)
          .set(ItemPackagingSealChoicePage(testIndex1, testPackagingIndex1), false)
        )

        section.isCompleted mustBe false
      }

      "when item packaging exists which is incomplete" in {

        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), emptyUserAnswers
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
          .set(ItemSelectPackagingPage(testIndex1, testPackagingIndex2), testPackageBag)
        )

        section.isCompleted mustBe false
      }

      "when bulk item where seal type is missing" in {

        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), emptyUserAnswers
          .set(ItemExciseProductCodePage(testIndex1), testEpcWine)
          .set(ItemCommodityCodePage(testIndex1), testCnCodeWine)
          .set(ItemBrandNamePage(testIndex1), ItemBrandNameModel(hasBrandName = true, Some("brand")))
          .set(ItemCommercialDescriptionPage(testIndex1), "Wine from grapes")
          .set(ItemAlcoholStrengthPage(testIndex1), BigDecimal(12.5))
          .set(ItemDesignationOfOriginPage(testIndex1), ItemDesignationOfOriginModel(ProtectedDesignationOfOrigin, Some("Italy - DOCG"), None))
          .set(ItemQuantityPage(testIndex1), BigDecimal("1000"))
          .set(ItemNetGrossMassPage(testIndex1), ItemNetGrossMassModel(BigDecimal("2000"), BigDecimal("2105")))
          .set(ItemBulkPackagingChoicePage(testIndex1), true)
          .set(ItemBulkPackagingSelectPage(testIndex1), BulkPackagingType(BulkLiquid, "bulk"))
          .set(ItemWineOperationsChoicePage(testIndex1), testWineOperations)
          .set(ItemWineProductCategoryPage(testIndex1), Other)
          .set(ItemWineGrowingZonePage(testIndex1), CIII_A)
          .set(ItemWineMoreInformationChoicePage(testIndex1), true)
          .set(ItemWineMoreInformationPage(testIndex1), Some("Info"))
          .set(ItemBulkPackagingSealChoicePage(testIndex1), true)
        )

        section.isCompleted mustBe false
      }

      "when bulk item where seal choice is missing" in {

        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), emptyUserAnswers
          .set(ItemExciseProductCodePage(testIndex1), testEpcWine)
          .set(ItemCommodityCodePage(testIndex1), testCnCodeWine)
          .set(ItemBrandNamePage(testIndex1), ItemBrandNameModel(hasBrandName = true, Some("brand")))
          .set(ItemCommercialDescriptionPage(testIndex1), "Wine from grapes")
          .set(ItemAlcoholStrengthPage(testIndex1), BigDecimal(12.5))
          .set(ItemDesignationOfOriginPage(testIndex1), ItemDesignationOfOriginModel(ProtectedDesignationOfOrigin, Some("Italy - DOCG"), None))
          .set(ItemQuantityPage(testIndex1), BigDecimal("1000"))
          .set(ItemNetGrossMassPage(testIndex1), ItemNetGrossMassModel(BigDecimal("2000"), BigDecimal("2105")))
          .set(ItemBulkPackagingChoicePage(testIndex1), true)
          .set(ItemBulkPackagingSelectPage(testIndex1), BulkPackagingType(BulkLiquid, "bulk"))
          .set(ItemWineOperationsChoicePage(testIndex1), testWineOperations)
          .set(ItemWineProductCategoryPage(testIndex1), Other)
          .set(ItemWineGrowingZonePage(testIndex1), CIII_A)
          .set(ItemWineMoreInformationChoicePage(testIndex1), true)
          .set(ItemWineMoreInformationPage(testIndex1), Some("Info"))
        )

        section.isCompleted mustBe false
      }

      "when bulk wine where more information is missing" in {

        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), emptyUserAnswers
          .set(ItemExciseProductCodePage(testIndex1), testEpcWine)
          .set(ItemCommodityCodePage(testIndex1), testCnCodeWine)
          .set(ItemBrandNamePage(testIndex1), ItemBrandNameModel(hasBrandName = true, Some("brand")))
          .set(ItemCommercialDescriptionPage(testIndex1), "Wine from grapes")
          .set(ItemAlcoholStrengthPage(testIndex1), BigDecimal(12.5))
          .set(ItemDesignationOfOriginPage(testIndex1), ItemDesignationOfOriginModel(ProtectedDesignationOfOrigin, Some("Italy - DOCG"), None))
          .set(ItemQuantityPage(testIndex1), BigDecimal("1000"))
          .set(ItemNetGrossMassPage(testIndex1), ItemNetGrossMassModel(BigDecimal("2000"), BigDecimal("2105")))
          .set(ItemBulkPackagingChoicePage(testIndex1), true)
          .set(ItemBulkPackagingSelectPage(testIndex1), BulkPackagingType(BulkLiquid, "bulk"))
          .set(ItemWineOperationsChoicePage(testIndex1), testWineOperations)
          .set(ItemWineProductCategoryPage(testIndex1), Other)
          .set(ItemWineGrowingZonePage(testIndex1), CIII_A)
          .set(ItemWineMoreInformationChoicePage(testIndex1), true)
          .set(ItemBulkPackagingSealChoicePage(testIndex1), false)
        )

        section.isCompleted mustBe false
      }

      "when bulk wine where more information choice is missing" in {

        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), emptyUserAnswers
          .set(ItemExciseProductCodePage(testIndex1), testEpcWine)
          .set(ItemCommodityCodePage(testIndex1), testCnCodeWine)
          .set(ItemBrandNamePage(testIndex1), ItemBrandNameModel(hasBrandName = true, Some("brand")))
          .set(ItemCommercialDescriptionPage(testIndex1), "Wine from grapes")
          .set(ItemAlcoholStrengthPage(testIndex1), BigDecimal(12.5))
          .set(ItemDesignationOfOriginPage(testIndex1), ItemDesignationOfOriginModel(ProtectedDesignationOfOrigin, Some("Italy - DOCG"), None))
          .set(ItemQuantityPage(testIndex1), BigDecimal("1000"))
          .set(ItemNetGrossMassPage(testIndex1), ItemNetGrossMassModel(BigDecimal("2000"), BigDecimal("2105")))
          .set(ItemBulkPackagingChoicePage(testIndex1), true)
          .set(ItemBulkPackagingSelectPage(testIndex1), BulkPackagingType(BulkLiquid, "bulk"))
          .set(ItemWineOperationsChoicePage(testIndex1), testWineOperations)
          .set(ItemWineProductCategoryPage(testIndex1), Other)
          .set(ItemWineGrowingZonePage(testIndex1), CIII_A)
          .set(ItemBulkPackagingSealChoicePage(testIndex1), false)
        )

        section.isCompleted mustBe false
      }

      "when bulk wine where imported from EU and wine growing zone is missing" in {

        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), emptyUserAnswers
          .set(ItemExciseProductCodePage(testIndex1), testEpcWine)
          .set(ItemCommodityCodePage(testIndex1), testCnCodeWine)
          .set(ItemBrandNamePage(testIndex1), ItemBrandNameModel(hasBrandName = true, Some("brand")))
          .set(ItemCommercialDescriptionPage(testIndex1), "Wine from grapes")
          .set(ItemAlcoholStrengthPage(testIndex1), BigDecimal(12.5))
          .set(ItemDesignationOfOriginPage(testIndex1), ItemDesignationOfOriginModel(ProtectedDesignationOfOrigin, Some("Italy - DOCG"), None))
          .set(ItemQuantityPage(testIndex1), BigDecimal("1000"))
          .set(ItemNetGrossMassPage(testIndex1), ItemNetGrossMassModel(BigDecimal("2000"), BigDecimal("2105")))
          .set(ItemBulkPackagingChoicePage(testIndex1), true)
          .set(ItemBulkPackagingSelectPage(testIndex1), BulkPackagingType(BulkLiquid, "bulk"))
          .set(ItemWineOperationsChoicePage(testIndex1), testWineOperations)
          .set(ItemWineProductCategoryPage(testIndex1), Other)
          .set(ItemWineMoreInformationChoicePage(testIndex1), false)
          .set(ItemBulkPackagingSealChoicePage(testIndex1), false)
        )

        section.isCompleted mustBe false
      }

      "when bulk wine where wine operations choice is missing" in {

        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), emptyUserAnswers
          .set(ItemExciseProductCodePage(testIndex1), testEpcWine)
          .set(ItemCommodityCodePage(testIndex1), testCnCodeWine)
          .set(ItemBrandNamePage(testIndex1), ItemBrandNameModel(hasBrandName = true, Some("brand")))
          .set(ItemCommercialDescriptionPage(testIndex1), "Wine from grapes")
          .set(ItemAlcoholStrengthPage(testIndex1), BigDecimal(12.5))
          .set(ItemDesignationOfOriginPage(testIndex1), ItemDesignationOfOriginModel(ProtectedDesignationOfOrigin, Some("Italy - DOCG"), None))
          .set(ItemQuantityPage(testIndex1), BigDecimal("1000"))
          .set(ItemNetGrossMassPage(testIndex1), ItemNetGrossMassModel(BigDecimal("2000"), BigDecimal("2105")))
          .set(ItemBulkPackagingChoicePage(testIndex1), true)
          .set(ItemBulkPackagingSelectPage(testIndex1), BulkPackagingType(BulkLiquid, "bulk"))
          .set(ItemWineProductCategoryPage(testIndex1), ImportedWine)
          .set(ItemWineMoreInformationChoicePage(testIndex1), false)
          .set(ItemBulkPackagingSealChoicePage(testIndex1), false)
        )

        section.isCompleted mustBe false
      }

      "when bulk wine where bulk packaging select is missing" in {

        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), emptyUserAnswers
          .set(ItemExciseProductCodePage(testIndex1), testEpcWine)
          .set(ItemCommodityCodePage(testIndex1), testCnCodeWine)
          .set(ItemBrandNamePage(testIndex1), ItemBrandNameModel(hasBrandName = true, Some("brand")))
          .set(ItemCommercialDescriptionPage(testIndex1), "Wine from grapes")
          .set(ItemAlcoholStrengthPage(testIndex1), BigDecimal(12.5))
          .set(ItemDesignationOfOriginPage(testIndex1), ItemDesignationOfOriginModel(ProtectedDesignationOfOrigin, Some("Italy - DOCG"), None))
          .set(ItemQuantityPage(testIndex1), BigDecimal("1000"))
          .set(ItemNetGrossMassPage(testIndex1), ItemNetGrossMassModel(BigDecimal("2000"), BigDecimal("2105")))
          .set(ItemBulkPackagingChoicePage(testIndex1), true)
          .set(ItemWineOperationsChoicePage(testIndex1), testWineOperations)
          .set(ItemWineProductCategoryPage(testIndex1), ImportedWine)
          .set(ItemWineMoreInformationChoicePage(testIndex1), false)
          .set(ItemBulkPackagingSealChoicePage(testIndex1), false)
        )

        section.isCompleted mustBe false
      }

      "when bulk wine where bulk packaging choice is missing" in {

        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), emptyUserAnswers
          .set(ItemExciseProductCodePage(testIndex1), testEpcWine)
          .set(ItemCommodityCodePage(testIndex1), testCnCodeWine)
          .set(ItemBrandNamePage(testIndex1), ItemBrandNameModel(hasBrandName = true, Some("brand")))
          .set(ItemCommercialDescriptionPage(testIndex1), "Wine from grapes")
          .set(ItemAlcoholStrengthPage(testIndex1), BigDecimal(12.5))
          .set(ItemDesignationOfOriginPage(testIndex1), ItemDesignationOfOriginModel(ProtectedDesignationOfOrigin, Some("Italy - DOCG"), None))
          .set(ItemQuantityPage(testIndex1), BigDecimal("1000"))
          .set(ItemNetGrossMassPage(testIndex1), ItemNetGrossMassModel(BigDecimal("2000"), BigDecimal("2105")))
          .set(ItemWineOperationsChoicePage(testIndex1), testWineOperations)
          .set(ItemWineProductCategoryPage(testIndex1), ImportedWine)
          .set(ItemWineMoreInformationChoicePage(testIndex1), false)
          .set(ItemBulkPackagingSealChoicePage(testIndex1), false)
        )

        section.isCompleted mustBe false
      }

      "when Excise Product Code is missing" in {

        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), emptyUserAnswers
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
        )

        section.isCompleted mustBe false
      }

      "when Commodity Code is missing" in {

        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), emptyUserAnswers
          .set(ItemExciseProductCodePage(testIndex1), testEpcWine)
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
        )

        section.isCompleted mustBe false
      }

      "when Brand Name is missing" in {

        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), emptyUserAnswers
          .set(ItemExciseProductCodePage(testIndex1), testEpcWine)
          .set(ItemCommodityCodePage(testIndex1), testCnCodeWine)
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
        )

        section.isCompleted mustBe false
      }

      "when Commercial Description is missing" in {

        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), emptyUserAnswers
          .set(ItemExciseProductCodePage(testIndex1), testEpcWine)
          .set(ItemCommodityCodePage(testIndex1), testCnCodeWine)
          .set(ItemBrandNamePage(testIndex1), ItemBrandNameModel(hasBrandName = true, Some("brand")))
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
        )

        section.isCompleted mustBe false
      }

      "when Quantity is missing" in {

        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), emptyUserAnswers
          .set(ItemExciseProductCodePage(testIndex1), testEpcWine)
          .set(ItemCommodityCodePage(testIndex1), testCnCodeWine)
          .set(ItemBrandNamePage(testIndex1), ItemBrandNameModel(hasBrandName = true, Some("brand")))
          .set(ItemCommercialDescriptionPage(testIndex1), "Wine from grapes")
          .set(ItemAlcoholStrengthPage(testIndex1), BigDecimal(12.5))
          .set(ItemDesignationOfOriginPage(testIndex1), ItemDesignationOfOriginModel(NoGeographicalIndication, None, None))
          .set(ItemNetGrossMassPage(testIndex1), ItemNetGrossMassModel(BigDecimal("2000"), BigDecimal("2105")))
          .set(ItemBulkPackagingChoicePage(testIndex1), false)
          .set(ItemWineProductCategoryPage(testIndex1), Other)
          .set(ItemWineMoreInformationChoicePage(testIndex1), false)
          .set(ItemSelectPackagingPage(testIndex1, testPackagingIndex1), testPackageBag)
          .set(ItemPackagingQuantityPage(testIndex1, testPackagingIndex1), "400")
          .set(ItemPackagingShippingMarksChoicePage(testIndex1, testPackagingIndex1), false)
          .set(ItemPackagingSealChoicePage(testIndex1, testPackagingIndex1), false)
        )

        section.isCompleted mustBe false
      }

      "when Net and Gross Mass is missing" in {

        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), emptyUserAnswers
          .set(ItemExciseProductCodePage(testIndex1), testEpcWine)
          .set(ItemCommodityCodePage(testIndex1), testCnCodeWine)
          .set(ItemBrandNamePage(testIndex1), ItemBrandNameModel(hasBrandName = true, Some("brand")))
          .set(ItemCommercialDescriptionPage(testIndex1), "Wine from grapes")
          .set(ItemAlcoholStrengthPage(testIndex1), BigDecimal(12.5))
          .set(ItemDesignationOfOriginPage(testIndex1), ItemDesignationOfOriginModel(NoGeographicalIndication, None, None))
          .set(ItemQuantityPage(testIndex1), BigDecimal("1000"))
          .set(ItemBulkPackagingChoicePage(testIndex1), false)
          .set(ItemWineProductCategoryPage(testIndex1), Other)
          .set(ItemWineMoreInformationChoicePage(testIndex1), false)
          .set(ItemSelectPackagingPage(testIndex1, testPackagingIndex1), testPackageBag)
          .set(ItemPackagingQuantityPage(testIndex1, testPackagingIndex1), "400")
          .set(ItemPackagingShippingMarksChoicePage(testIndex1, testPackagingIndex1), false)
          .set(ItemPackagingSealChoicePage(testIndex1, testPackagingIndex1), false)
        )

        section.isCompleted mustBe false
      }

      "when low alcohol and Small Independent Producer annual production is missing" in {

        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), emptyUserAnswers
          .set(ItemExciseProductCodePage(testIndex1), testEpcBeer)
          .set(ItemCommodityCodePage(testIndex1), testCnCodeBeer)
          .set(ItemBrandNamePage(testIndex1), ItemBrandNameModel(hasBrandName = true, Some("brand")))
          .set(ItemCommercialDescriptionPage(testIndex1), "Beer from hops")
          .set(ItemAlcoholStrengthPage(testIndex1), BigDecimal(8.4))
          .set(ItemSmallIndependentProducerPage(testIndex1), ItemSmallIndependentProducerModel(SelfCertifiedIndependentSmallProducerAndNotConsignor, Some(testErn)))
          .set(ItemProducerSizePage(testIndex1), BigInt("300"))
          .set(ItemQuantityPage(testIndex1), BigDecimal("1000"))
          .set(ItemBulkPackagingChoicePage(testIndex1), false)
          .set(ItemWineProductCategoryPage(testIndex1), Other)
          .set(ItemWineMoreInformationChoicePage(testIndex1), false)
          .set(ItemSelectPackagingPage(testIndex1, testPackagingIndex1), testPackageBag)
          .set(ItemPackagingQuantityPage(testIndex1, testPackagingIndex1), "400")
          .set(ItemPackagingShippingMarksChoicePage(testIndex1, testPackagingIndex1), false)
          .set(ItemPackagingSealChoicePage(testIndex1, testPackagingIndex1), false)
        )

        section.isCompleted mustBe false
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
          .set(ItemWineProductCategoryPage(testIndex1), Other)
          .set(ItemWineMoreInformationChoicePage(testIndex1), false)
          .set(ItemSelectPackagingPage(testIndex1, testPackagingIndex1), testPackageBag)
          .set(ItemPackagingQuantityPage(testIndex1, testPackagingIndex1), "400")
          .set(ItemPackagingShippingMarksChoicePage(testIndex1, testPackagingIndex1), false)
          .set(ItemPackagingSealChoicePage(testIndex1, testPackagingIndex1), false)
        )

        section.isCompleted mustBe false
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
          .set(ItemWineProductCategoryPage(testIndex1), Other)
          .set(ItemWineMoreInformationChoicePage(testIndex1), false)
          .set(ItemSelectPackagingPage(testIndex1, testPackagingIndex1), testPackageBag)
          .set(ItemPackagingQuantityPage(testIndex1, testPackagingIndex1), "400")
          .set(ItemPackagingShippingMarksChoicePage(testIndex1, testPackagingIndex1), false)
          .set(ItemPackagingSealChoicePage(testIndex1, testPackagingIndex1), false)
        )

        section.isCompleted mustBe false
      }
    }
  }

  "packagingPagesComplete" - {
    "must return true" - {
      "when EPC is defined, ItemBulkPackagingChoicePage is false and ItemsPackagingSection is completed" in {
        val userAnswers = emptyUserAnswers
          .set(ItemExciseProductCodePage(testIndex1), testEpcBeer)
          .set(ItemCommodityCodePage(testIndex1), testCnCodeBeer)
          .set(ItemBulkPackagingChoicePage(testIndex1), false)
          .set(ItemSelectPackagingPage(testIndex1, testPackagingIndex1), testPackageBag)
          .set(ItemPackagingQuantityPage(testIndex1, testPackagingIndex1), "")
          .set(ItemPackagingShippingMarksChoicePage(testIndex1, testPackagingIndex1), false)
          .set(ItemPackagingSealChoicePage(testIndex1, testPackagingIndex1), false)

        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), userAnswers)

        section.packagingPagesComplete mustBe true
      }
      "when EPC is defined, ItemBulkPackagingChoicePage is true and bulkPackagingPagesComplete is completed" in {
        val userAnswers = emptyUserAnswers
          .set(ItemExciseProductCodePage(testIndex1), testEpcBeer)
          .set(ItemCommodityCodePage(testIndex1), testCnCodeBeer)
          .set(ItemBulkPackagingChoicePage(testIndex1), true)
          .set(ItemBulkPackagingSealChoicePage(testIndex1), false)
          .set(ItemBulkPackagingSelectPage(testIndex1), bulkPackagingTypes.head)

        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), userAnswers)

        section.packagingPagesComplete mustBe true
      }
    }

    "must return false" - {
      "when EPC is not defined" - {
        val userAnswers = emptyUserAnswers
          .set(ItemBulkPackagingChoicePage(testIndex1), false)
          .set(ItemSelectPackagingPage(testIndex1, testPackagingIndex1), testPackageBag)
          .set(ItemPackagingQuantityPage(testIndex1, testPackagingIndex1), "")
          .set(ItemPackagingShippingMarksChoicePage(testIndex1, testPackagingIndex1), false)
          .set(ItemPackagingSealChoicePage(testIndex1, testPackagingIndex1), false)

        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), userAnswers)

        section.packagingPagesComplete mustBe false
      }
      "when ItemBulkPackagingChoicePage is not defined" - {
        val userAnswers = emptyUserAnswers
          .set(ItemCommodityCodePage(testIndex1), testCnCodeBeer)
          .set(ItemSelectPackagingPage(testIndex1, testPackagingIndex1), testPackageBag)
          .set(ItemPackagingQuantityPage(testIndex1, testPackagingIndex1), "")
          .set(ItemPackagingShippingMarksChoicePage(testIndex1, testPackagingIndex1), false)
          .set(ItemPackagingSealChoicePage(testIndex1, testPackagingIndex1), false)

        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), userAnswers)

        section.packagingPagesComplete mustBe false
      }
      "when EPC is defined, ItemBulkPackagingChoicePage is false and ItemsPackagingSection is not completed" in {
        val userAnswers = emptyUserAnswers
          .set(ItemExciseProductCodePage(testIndex1), testEpcBeer)
          .set(ItemCommodityCodePage(testIndex1), testCnCodeBeer)
          .set(ItemBulkPackagingChoicePage(testIndex1), false)

        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), userAnswers)

        section.packagingPagesComplete mustBe false
      }
      "when EPC is defined, ItemBulkPackagingChoicePage is true and bulkPackagingPagesComplete is not completed" in {
        val userAnswers = emptyUserAnswers
          .set(ItemExciseProductCodePage(testIndex1), testEpcBeer)
          .set(ItemCommodityCodePage(testIndex1), testCnCodeBeer)
          .set(ItemBulkPackagingChoicePage(testIndex1), true)

        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), userAnswers)

        section.packagingPagesComplete mustBe false
      }
    }
  }

  "itemDensityAnswer" - {
    "must return a non-empty Seq" - {
      "when energy and not in Seq('E470', 'E500', 'E600', 'E930')" in {
        val epc = ""

        section.itemDensityAnswer(epc)(GoodsType.Energy, dataRequest(FakeRequest())).length must not be 0
      }
    }

    "must return an empty Seq" - {
      Seq("E470", "E500", "E600", "E930").foreach(
        epc =>
          s"when energy and EPC is $epc" in {
            section.itemDensityAnswer(epc)(GoodsType.Energy, dataRequest(FakeRequest())) mustBe Nil
          }
      )

      "when not energy" in {
        GoodsType.values.filterNot(_ == Energy).foreach {
          goodsType =>
            val epc = ""

            section.itemDensityAnswer(epc)(goodsType, dataRequest(FakeRequest())) mustBe Nil
        }
      }
    }
  }

  "fiscalMarksAnswers" - {
    "must return two items" - {
      "when tobacco and more info = true" in {
        val userAnswers: UserAnswers = emptyUserAnswers
          .set(ItemFiscalMarksChoicePage(testIndex1), true)

        val dr: DataRequest[_] = dataRequest(FakeRequest(), userAnswers)

        section.fiscalMarksAnswers(GoodsType.Tobacco, dr).length mustBe 2
      }
    }

    "must return one item" - {
      "when tobacco and more info = false" in {
        val userAnswers: UserAnswers = emptyUserAnswers
          .set(ItemFiscalMarksChoicePage(testIndex1), false)

        val dr: DataRequest[_] = dataRequest(FakeRequest(), userAnswers)

        section.fiscalMarksAnswers(GoodsType.Tobacco, dr).length mustBe 1
      }
    }

    "must return an empty Seq" - {
      "when not tobacco" in {
        GoodsType.values.filterNot(_ == GoodsType.Tobacco).foreach {
          goodsType =>
            val userAnswers: UserAnswers = emptyUserAnswers
              .set(ItemFiscalMarksChoicePage(testIndex1), true)

            val dr: DataRequest[_] = dataRequest(FakeRequest(), userAnswers)

            section.fiscalMarksAnswers(goodsType, dr) mustBe Nil
        }
      }
    }
  }

  "wineMoreInformationAnswers" - {
    "must return two items" - {
      "when EPC code and CN code is wine and more info = true" in {
        val userAnswers: UserAnswers = emptyUserAnswers
          .set(ItemWineMoreInformationChoicePage(testIndex1), true)
          .set(ItemExciseProductCodePage(testIndex1), testEpcWine)
          .set(ItemCommodityCodePage(testIndex1), testCnCodeWine)

        val dr: DataRequest[_] = dataRequest(FakeRequest(), userAnswers)

        section.wineMoreInformationAnswers(dr).length mustBe 2
      }
    }

    "must return one item" - {
      "when EPC code and CN code is wine and more info = false" in {
        val userAnswers: UserAnswers = emptyUserAnswers
          .set(ItemWineMoreInformationChoicePage(testIndex1), false)
          .set(ItemExciseProductCodePage(testIndex1), testEpcWine)
          .set(ItemCommodityCodePage(testIndex1), testCnCodeWine)

        val dr: DataRequest[_] = dataRequest(FakeRequest(), userAnswers)

        section.wineMoreInformationAnswers(dr).length mustBe 1
      }
    }

    "must return an empty Seq" - {
      "when CN code is wine but EPC is not" in {
        val userAnswers: UserAnswers = emptyUserAnswers
          .set(ItemWineMoreInformationChoicePage(testIndex1), true)
          .set(ItemExciseProductCodePage(testIndex1), testEpcSpirit)
          .set(ItemCommodityCodePage(testIndex1), testCnCodeWine)

        val dr: DataRequest[_] = dataRequest(FakeRequest(), userAnswers)

        section.wineMoreInformationAnswers(dr) mustBe Nil
      }

      "when CN code and EPC are not wine" in {
        val userAnswers: UserAnswers = emptyUserAnswers
          .set(ItemWineMoreInformationChoicePage(testIndex1), true)
          .set(ItemExciseProductCodePage(testIndex1), testEpcSpirit)
          .set(ItemCommodityCodePage(testIndex1), testCnCodeSpirit)

        val dr: DataRequest[_] = dataRequest(FakeRequest(), userAnswers)

        section.wineMoreInformationAnswers(dr) mustBe Nil
      }
    }
  }

  "wineCountryOfOriginAnswers" - {
    "must return two items" - {
      "when EPC code and CN code are wine and imported = false" in {
        val userAnswers: UserAnswers = emptyUserAnswers
          .set(ItemWineProductCategoryPage(testIndex1), ImportedWine)
          .set(ItemExciseProductCodePage(testIndex1), testEpcWine)
          .set(ItemCommodityCodePage(testIndex1), testCnCodeWine)

        val dr: DataRequest[_] = dataRequest(FakeRequest(), userAnswers)

        section.wineCountryOfOriginAnswers(dr).length mustBe 2
      }
    }

    "must return one item" - {
      "when EPC code and CN code are wine and imported = true" in {
        val userAnswers: UserAnswers = emptyUserAnswers
          .set(ItemWineProductCategoryPage(testIndex1), Other)
          .set(ItemExciseProductCodePage(testIndex1), testEpcWine)
          .set(ItemCommodityCodePage(testIndex1), testCnCodeWine)

        val dr: DataRequest[_] = dataRequest(FakeRequest(), userAnswers)

        section.wineCountryOfOriginAnswers(dr).length mustBe 1
      }
    }

    "must return an empty Seq" - {
      "when CN code is wine but EPC is not" in {
        val userAnswers: UserAnswers =
          emptyUserAnswers
            .set(ItemQuantityPage(testIndex1), BigDecimal(61))
            .set(ItemWineProductCategoryPage(testIndex1), Other)
            .set(ItemExciseProductCodePage(testIndex1), testEpcSpirit)
            .set(ItemCommodityCodePage(testIndex1), testCnCodeWine)

        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), userAnswers)

        section.wineCountryOfOriginAnswers(dr) mustBe Nil
      }
      "when CN code and EPC are not wine" in {
        val userAnswers: UserAnswers =
          emptyUserAnswers
            .set(ItemQuantityPage(testIndex1), BigDecimal(61))
            .set(ItemWineProductCategoryPage(testIndex1), Other)
            .set(ItemExciseProductCodePage(testIndex1), testEpcSpirit)
            .set(ItemCommodityCodePage(testIndex1), testCnCodeSpirit)

        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), userAnswers)

        section.wineCountryOfOriginAnswers(dr) mustBe Nil
      }
    }
  }

  "designationOfOriginAnswers" - {

    "must return one item" - {
      "when alcohol, not beer, ItemDesignationOfOriginPage is NoGeographicalIndication" in {
        GoodsType.values.filter(gt => gt.isAlcohol && gt != Beer && gt != Spirits).foreach {
          goodsType =>
            val userAnswers: UserAnswers = emptyUserAnswers
              .set(ItemDesignationOfOriginPage(testIndex1), ItemDesignationOfOriginModel(NoGeographicalIndication, None, None))

            val dr: DataRequest[_] = dataRequest(FakeRequest(), userAnswers)

            section.designationOfOriginAnswers(goodsType.code)(goodsType, dr).length mustBe 1
        }
      }

      "when Spirits, not beer, ItemDesignationOfOriginPage is NoGeographicalIndication (S200)" in {
          val userAnswers: UserAnswers = emptyUserAnswers
            .set(ItemDesignationOfOriginPage(testIndex1), ItemDesignationOfOriginModel(NoGeographicalIndication, None, isSpiritMarketedAndLabelled = Some(true)))

          val dr: DataRequest[_] = dataRequest(FakeRequest(), userAnswers)

          section.designationOfOriginAnswers(testEpcSpirit)(Spirits, dr).length mustBe 1
      }
    }

    "must return an empty Seq" - {
      "when not alcohol" in {
        GoodsType.values.filterNot(_.isAlcohol).foreach {
          goodsType =>
            val userAnswers: UserAnswers = emptyUserAnswers
              .set(ItemDesignationOfOriginPage(testIndex1), ItemDesignationOfOriginModel(NoGeographicalIndication, None, None))

            val dr: DataRequest[_] = dataRequest(FakeRequest(), userAnswers)

            section.designationOfOriginAnswers(goodsType.code)(goodsType, dr) mustBe Nil
        }
      }

      "when beer" in {
        val userAnswers: UserAnswers = emptyUserAnswers
          .set(ItemDesignationOfOriginPage(testIndex1), ItemDesignationOfOriginModel(NoGeographicalIndication, None, None))

        val dr: DataRequest[_] = dataRequest(FakeRequest(), userAnswers)

        section.designationOfOriginAnswers(testEpcBeer)(Beer, dr) mustBe Nil
      }

      "when Spirits (non-S200)" in {
        val userAnswers: UserAnswers = emptyUserAnswers
          .set(ItemDesignationOfOriginPage(testIndex1), ItemDesignationOfOriginModel(NoGeographicalIndication, None, None))

        val dr: DataRequest[_] = dataRequest(FakeRequest(), userAnswers)

        section.designationOfOriginAnswers(testExciseProductCodeS300.code)(Spirits, dr) mustBe Nil
      }
    }
  }

  "maturationAgeAnswer" - {
    "must return a non-empty Seq" - {
      "if spirits and is Spirituous Beverages" in {
        GoodsType.values.filter(_ == GoodsType.Spirits).foreach {
          goodsType =>
            val dr: DataRequest[_] = dataRequest(FakeRequest())

            section.maturationAgeAnswer(testEpcSpirit)(goodsType, dr).length must not be 0
        }
      }
    }

    "must return an empty Seq" - {
      "if not spirits" in {
        GoodsType.values.filterNot(_ == GoodsType.Spirits).foreach {
          goodsType =>
            val dr: DataRequest[_] = dataRequest(FakeRequest())

            section.maturationAgeAnswer(testEpcBeer)(goodsType, dr) mustBe Nil
        }
      }
      "if spirits but not Spirituous Beverages" in {
        val dr: DataRequest[_] = dataRequest(FakeRequest())

        section.maturationAgeAnswer("S500")(GoodsType.Spirits, dr) mustBe Nil
      }

    }
  }

  "alcoholStrengthAnswer" - {
    "must return a non-empty Seq" - {
      "if alcohol" in {
        GoodsType.values.filter(_.isAlcohol).foreach {
          goodsType =>
            val dr: DataRequest[_] = dataRequest(FakeRequest())

            section.alcoholStrengthAnswer(goodsType, dr).length must not be 0
        }
      }
    }

    "must return an empty Seq" - {
      "if not alcohol" in {
        GoodsType.values.filterNot(_.isAlcohol).foreach {
          goodsType =>
            val dr: DataRequest[_] = dataRequest(FakeRequest())

            section.alcoholStrengthAnswer(goodsType, dr) mustBe Nil
        }
      }
    }
  }

  "independentProducerAnswers" - {
    "must return one item" - {
      "when alcohol, strength < 8.5, and small producer" in {
        GoodsType.values.filter(_.isAlcohol).foreach {
          goodsType =>
            val userAnswers = emptyUserAnswers
              .set(ItemAlcoholStrengthPage(testIndex1), BigDecimal(8.49))
              .set(ItemSmallIndependentProducerPage(testIndex1), ItemSmallIndependentProducerModel(SelfCertifiedIndependentSmallProducerAndNotConsignor, Some(testErn)))

            val dr: DataRequest[_] = dataRequest(FakeRequest(), userAnswers)

            section.independentProducerAnswers(goodsType, dr).length mustBe 1
        }
      }
    }

    "must return one item" - {
      "when alcohol, strength < 8.5, and not small producer" in {
        GoodsType.values.filter(_.isAlcohol).foreach {
          goodsType =>
            val userAnswers = emptyUserAnswers
              .set(ItemAlcoholStrengthPage(testIndex1), BigDecimal(8.49))
              .set(ItemSmallIndependentProducerPage(testIndex1), ItemSmallIndependentProducerModel(NotApplicable, None))

            val dr: DataRequest[_] = dataRequest(FakeRequest(), userAnswers)

            section.independentProducerAnswers(goodsType, dr).length mustBe 1
        }
      }
    }

    "must return an empty Seq" - {
      "when not alcohol" in {
        GoodsType.values.filterNot(_.isAlcohol).foreach {
          goodsType =>
            val userAnswers = emptyUserAnswers
              .set(ItemAlcoholStrengthPage(testIndex1), BigDecimal(8.49))
              .set(ItemSmallIndependentProducerPage(testIndex1), ItemSmallIndependentProducerModel(SelfCertifiedIndependentSmallProducerAndNotConsignor, Some(testErn)))

            val dr: DataRequest[_] = dataRequest(FakeRequest(), userAnswers)

            section.independentProducerAnswers(goodsType, dr) mustBe Nil
        }
      }
      "strength >= 8.5" in {
        GoodsType.values.filter(_.isAlcohol).foreach {
          goodsType =>
            val userAnswers = emptyUserAnswers
              .set(ItemAlcoholStrengthPage(testIndex1), BigDecimal(8.5))
              .set(ItemSmallIndependentProducerPage(testIndex1), ItemSmallIndependentProducerModel(SelfCertifiedIndependentSmallProducerAndNotConsignor, Some(testErn)))

            val dr: DataRequest[_] = dataRequest(FakeRequest(), userAnswers)

            section.independentProducerAnswers(goodsType, dr) mustBe Nil
        }
      }
      "strength is not present" in {
        GoodsType.values.filter(_.isAlcohol).foreach {
          goodsType =>
            val userAnswers = emptyUserAnswers
              .set(ItemSmallIndependentProducerPage(testIndex1), ItemSmallIndependentProducerModel(SelfCertifiedIndependentSmallProducerAndNotConsignor, Some(testErn)))

            val dr: DataRequest[_] = dataRequest(FakeRequest(), userAnswers)

            section.independentProducerAnswers(goodsType, dr) mustBe Nil
        }
      }
    }
  }

  "degreesPlatoAnswer" - {
    "must return a non-empty Seq" - {
      "when XI and Beer" - {
        val goodsType: GoodsType = GoodsType.Beer
        val dr: DataRequest[_] = dataRequest(FakeRequest(), ern = testNorthernIrelandErn)

        section.degreesPlatoAnswer(goodsType, dr).length must not be 0
      }
    }
    "must return an empty Seq" - {
      "when not XI" - {
        val goodsType: GoodsType = GoodsType.Beer
        val dr: DataRequest[_] = dataRequest(FakeRequest(), ern = testGreatBritainErn)

        section.degreesPlatoAnswer(goodsType, dr) mustBe Nil
      }
      "when not Beer" - {
        GoodsType.values.filterNot(_ == Beer).foreach {
          goodsType =>
            val dr: DataRequest[_] = dataRequest(FakeRequest(), ern = testGreatBritainErn)

            section.degreesPlatoAnswer(goodsType, dr) mustBe Nil
        }
      }
    }
  }

  "bulkCommercialSeals" - {
    "must return two items" - {
      "when true" in {
        val userAnswers: UserAnswers =
          emptyUserAnswers
            .set(ItemBulkPackagingSealChoicePage(testIndex1), true)

        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), userAnswers)

        section.bulkCommercialSeals.length mustBe 2
      }
    }
    "must return one item" - {
      "when false" in {
        val userAnswers: UserAnswers =
          emptyUserAnswers
            .set(ItemBulkPackagingSealChoicePage(testIndex1), false)

        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), userAnswers)

        section.bulkCommercialSeals.length mustBe 1
      }
    }
  }

  "wineBulkGrowingZoneAnswer" - {
    "must return a non-empty Seq" - {
      "when EPC and CN are wine, quantity > 60 and not imported from the EU" in {
        val userAnswers: UserAnswers =
          emptyUserAnswers
            .set(ItemQuantityPage(testIndex1), BigDecimal(61))
            .set(ItemWineProductCategoryPage(testIndex1), Other)
            .set(ItemExciseProductCodePage(testIndex1), testEpcWine)
            .set(ItemCommodityCodePage(testIndex1), testCnCodeWine)

        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), userAnswers)

        section.wineBulkGrowingZoneAnswer.length must not be 0
      }
    }

    "must return an empty Seq" - {
      "when CN code is wine but EPC is not" in {
        val userAnswers: UserAnswers =
          emptyUserAnswers
            .set(ItemQuantityPage(testIndex1), BigDecimal(61))
            .set(ItemWineProductCategoryPage(testIndex1), Other)
            .set(ItemExciseProductCodePage(testIndex1), testEpcSpirit)
            .set(ItemCommodityCodePage(testIndex1), testCnCodeWine)

        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), userAnswers)

        section.wineBulkGrowingZoneAnswer mustBe Nil
      }
      "when CN code and EPC are not wine" in {
        val userAnswers: UserAnswers =
          emptyUserAnswers
            .set(ItemQuantityPage(testIndex1), BigDecimal(61))
            .set(ItemWineProductCategoryPage(testIndex1), Other)
            .set(ItemExciseProductCodePage(testIndex1), testEpcSpirit)
            .set(ItemCommodityCodePage(testIndex1), testCnCodeSpirit)

        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), userAnswers)

        section.wineBulkGrowingZoneAnswer mustBe Nil
      }
      "when quantity <= 60" in {
        val userAnswers: UserAnswers =
          emptyUserAnswers
            .set(ItemQuantityPage(testIndex1), BigDecimal(60))
            .set(ItemWineProductCategoryPage(testIndex1), Other)
            .set(ItemCommodityCodePage(testIndex1), testCnCodeWine)

        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), userAnswers)

        section.wineBulkGrowingZoneAnswer mustBe Nil
      }
      "when imported outside the EU" in {
        val userAnswers: UserAnswers =
          emptyUserAnswers
            .set(ItemQuantityPage(testIndex1), BigDecimal(61))
            .set(ItemWineProductCategoryPage(testIndex1), ImportedWine)
            .set(ItemCommodityCodePage(testIndex1), testCnCodeWine)

        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), userAnswers)

        section.wineBulkGrowingZoneAnswer mustBe Nil
      }
      "when quantity is missing" in {
        val userAnswers: UserAnswers =
          emptyUserAnswers
            .set(ItemWineProductCategoryPage(testIndex1), Other)
            .set(ItemCommodityCodePage(testIndex1), testCnCodeWine)

        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), userAnswers)

        section.wineBulkGrowingZoneAnswer mustBe Nil
      }
      "when imported choice is missing" in {
        val userAnswers: UserAnswers =
          emptyUserAnswers
            .set(ItemQuantityPage(testIndex1), BigDecimal(61))
            .set(ItemCommodityCodePage(testIndex1), testCnCodeWine)

        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), userAnswers)

        section.wineBulkGrowingZoneAnswer mustBe Nil
      }
    }
  }

  "wineBulkOperationAnswer" - {
    "must return a non-empty Seq" - {
      "when commodity code and EPC is wine with quantity > 60" in {
        val userAnswers: UserAnswers =
          emptyUserAnswers.set(ItemQuantityPage(testIndex1), BigDecimal(61))
            .set(ItemExciseProductCodePage(testIndex1), testEpcWine)
            .set(ItemCommodityCodePage(testIndex1), testCnCodeWine)

        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), userAnswers)

        section.wineBulkOperationAnswer.length must not be 0
      }
    }

    "must return an empty Seq" - {
      "when wine with quantity <= 60" in {
        val userAnswers: UserAnswers =
          emptyUserAnswers.set(ItemQuantityPage(testIndex1), BigDecimal(60))
            .set(ItemCommodityCodePage(testIndex1), testCnCodeWine)

        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), userAnswers)

        section.wineBulkOperationAnswer mustBe Nil
      }
      "when CN code is wine but EPC is not" in {
        val userAnswers: UserAnswers =
          emptyUserAnswers.set(ItemQuantityPage(testIndex1), BigDecimal(61))
            .set(ItemExciseProductCodePage(testIndex1), testEpcSpirit)
            .set(ItemCommodityCodePage(testIndex1), testCnCodeWine)

        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), userAnswers)

        section.wineBulkOperationAnswer mustBe Nil
      }
      "when CN code and EPC are not wine" in {
        val userAnswers: UserAnswers =
          emptyUserAnswers.set(ItemQuantityPage(testIndex1), BigDecimal(61))
            .set(ItemExciseProductCodePage(testIndex1), testEpcSpirit)
            .set(ItemCommodityCodePage(testIndex1), testCnCodeSpirit)

        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), userAnswers)

        section.wineBulkOperationAnswer mustBe Nil
      }
      "when quantity is missing" in {
        val userAnswers: UserAnswers =
          emptyUserAnswers.set(ItemCommodityCodePage(testIndex1), testCnCodeWine)

        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), userAnswers)

        section.wineBulkOperationAnswer mustBe Nil
      }
    }
  }

  "mandatoryIf" - {
    val fRes: Seq[Option[_]] = Seq(Some(1))

    "must return the function which returns a Seq" - {
      "if true" in {
        section.mandatoryIf(condition = true)(fRes) mustBe fRes
      }
    }
    "must return an empty Seq" - {
      "if false" in {
        section.mandatoryIf(condition = false)(fRes) mustBe Nil
      }
    }
  }

  "packagingIndexes" - {

    "must return all the packaging indexes that are linked to an item" in {

      implicit val request = dataRequest(FakeRequest(), emptyUserAnswers
        .set(ItemPackagingQuantityPage(testIndex1, testPackagingIndex1), "1")
        .set(ItemPackagingQuantityPage(testIndex1, testPackagingIndex2), "1")
        .set(ItemPackagingQuantityPage(testIndex1, testPackagingIndex3), "1")
        .set(ItemPackagingQuantityPage(testIndex2, testPackagingIndex1), "1")
        .set(ItemPackagingQuantityPage(testIndex2, testPackagingIndex2), "1")
        .set(ItemPackagingQuantityPage(testIndex3, testPackagingIndex1), "1")
      )

      ItemsSectionItem(testIndex1).packagingIndexes mustBe Seq(
        testPackagingIndex1,
        testPackagingIndex2,
        testPackagingIndex3
      )
    }
  }
}
