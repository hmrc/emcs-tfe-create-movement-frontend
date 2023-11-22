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

package navigation

import base.SpecBase
import controllers.sections.items.{routes => itemsRoutes}
import fixtures.ItemFixtures
import models.response.referenceData.{BulkPackagingType, ItemPackaging}
import models.sections.items.ItemBrandNameModel
import models.sections.items.ItemBulkPackagingCode.BulkLiquid
import models.sections.items.ItemGeographicalIndicationType.{NoGeographicalIndication, ProtectedGeographicalIndication}
import models.{CheckMode, GoodsTypeModel, NormalMode, ReviewMode}
import pages.Page
import pages.sections.items._

class ItemsNavigatorSpec extends SpecBase with ItemFixtures {
  val navigator = new ItemsNavigator

  "ItemsNavigator" - {
    "in Normal mode" - {
      "must go from a page that doesn't exist in the route map to Items CYA" in {
        case object UnknownPage extends Page
        navigator.nextPage(UnknownPage, NormalMode, emptyUserAnswers) mustBe
          testOnly.controllers.routes.UnderConstructionController.onPageLoad()
      }

      "must go from the Excise Product Code page" - {
        "to CAM-ITM38 page" - {
          "when the EPC has multiple commodity codes" in {
            navigator.nextPage(
              ItemExciseProductCodePage(testIndex1),
              NormalMode,
              emptyUserAnswers.set(ItemExciseProductCodePage(testIndex1), testExciseProductCodeB000.code)
            ) mustBe controllers.sections.items.routes.ItemCommodityCodeController.onPageLoad(testErn, testDraftId, testIndex1, NormalMode)
          }
        }

        "to the Items index page" - {
          "when there is no answer" in {
            navigator.nextPage(ItemExciseProductCodePage(testIndex1),
              NormalMode, emptyUserAnswers) mustBe itemsRoutes.ItemsIndexController.onPageLoad(testErn, testDraftId)
          }
        }
      }

      "must go from the Item Commodity Code page" - {
        "to the Confirm Commodity Code page" in {
          navigator.nextPage(
            ItemCommodityCodePage(testIndex1),
            NormalMode,
            emptyUserAnswers
          ) mustBe controllers.sections.items.routes.ItemConfirmCommodityCodeController.onPageLoad(testErn, testDraftId, testIndex1)
        }
      }

      "must go from the Confirm Commodity Code page" - {
        "to the Item Brand Name page" in {
          navigator.nextPage(
            ItemConfirmCommodityCodePage(testIndex1),
            NormalMode,
            emptyUserAnswers
          ) mustBe controllers.sections.items.routes.ItemBrandNameController.onPageLoad(testErn, testDraftId, testIndex1, NormalMode)
        }
      }

      "must go from the Item Brand Name page" - {

        "to the Commercial Description Page" in {
          val userAnswers = emptyUserAnswers.set(ItemBrandNamePage(testIndex1), ItemBrandNameModel(hasBrandName = true, Some("brand")))

          navigator.nextPage(ItemBrandNamePage(testIndex1), NormalMode, userAnswers) mustBe
            itemsRoutes.CommercialDescriptionController.onPageLoad(testErn, testDraftId, testIndex1, NormalMode)
        }
      }

      "must go from the Commercial Description page" - {
        "when GoodsType is Beer" - {
          "to the Alcohol Strength Page" in {
            val userAnswers = emptyUserAnswers
              .set(ItemExciseProductCodePage(testIndex1), testExciseProductCodeB000.code)

            navigator.nextPage(CommercialDescriptionPage(testIndex1), NormalMode, userAnswers) mustBe
              itemsRoutes.ItemAlcoholStrengthController.onPageLoad(testErn, testDraftId, testIndex1, NormalMode)
          }
        }
        "when GoodsType is Spirits" - {
          "to the  Alcohol Strength Page" in {
            val userAnswers = emptyUserAnswers
              .set(ItemExciseProductCodePage(testIndex1), testExciseProductCodeS200.code)

            navigator.nextPage(CommercialDescriptionPage(testIndex1), NormalMode, userAnswers) mustBe
              itemsRoutes.ItemAlcoholStrengthController.onPageLoad(testErn, testDraftId, testIndex1, NormalMode)
          }
        }
        "when GoodsType is Wine" - {
          "to the Alcohol Strength Page" in {
            val userAnswers = emptyUserAnswers
              .set(ItemExciseProductCodePage(testIndex1), testExciseProductCodeW200.code)

            navigator.nextPage(CommercialDescriptionPage(testIndex1), NormalMode, userAnswers) mustBe
              itemsRoutes.ItemAlcoholStrengthController.onPageLoad(testErn, testDraftId, testIndex1, NormalMode)
          }
        }
        "when GoodsType is Intermediate" - {
          "to the Alcohol Strength Page" in {
            val userAnswers = emptyUserAnswers
              .set(ItemExciseProductCodePage(testIndex1), testExciseProductCodeI200.code)

            navigator.nextPage(CommercialDescriptionPage(testIndex1), NormalMode, userAnswers) mustBe
              itemsRoutes.ItemAlcoholStrengthController.onPageLoad(testErn, testDraftId, testIndex1, NormalMode)
          }
        }
        "when GoodsType is one type of Tobacco " - {

          "to the Item Fiscal Marks Choice Page" in {
            val userAnswers = emptyUserAnswers
              .set(ItemExciseProductCodePage(testIndex1), testExciseProductCodeT200.code)

            navigator.nextPage(CommercialDescriptionPage(testIndex1), NormalMode, userAnswers) mustBe
              itemsRoutes.ItemFiscalMarksChoiceController.onPageLoad(testErn, testDraftId, testIndex1, NormalMode)
          }
        }

        "when GoodsType is Energy " - {

          Seq(testExciseProductCodeE470, testExciseProductCodeE500, testExciseProductCodeE600, testExciseProductCodeE930).foreach(epc => {
            s"when the EPC is $epc" - {
              "to the Item Quantity Page" in {
                val userAnswers = emptyUserAnswers.set(ItemExciseProductCodePage(testIndex1), epc.code)

                navigator.nextPage(CommercialDescriptionPage(testIndex1), NormalMode, userAnswers) mustBe
                  itemsRoutes.ItemQuantityController.onPageLoad(testErn, testDraftId, testIndex1, NormalMode)
              }
            }
          })

          s"when the EPC is anything else" - {

            //TODO: Route to CAM-ITM33
            "to the Under Construction Page" in {
              val userAnswers = emptyUserAnswers
                .set(ItemExciseProductCodePage(testIndex1), testExciseProductCodeE200.code)

              navigator.nextPage(CommercialDescriptionPage(testIndex1), NormalMode, userAnswers) mustBe
                testOnly.controllers.routes.UnderConstructionController.onPageLoad()
            }
          }
        }
      }

      "must go from the Item Alcohol Strength page" - {

        "when GoodsType is Beer and XIWK or XIRC" - {

          "to the Item Degrees Plato Page" in {

            val userAnswers = emptyUserAnswers.copy(ern = testNorthernIrelandErn)
              .set(ItemExciseProductCodePage(testIndex1), testExciseProductCodeB000.code)
              .set(ItemAlcoholStrengthPage(testIndex1), BigDecimal(1.5))

            navigator.nextPage(ItemAlcoholStrengthPage(testIndex1), NormalMode, userAnswers) mustBe
              itemsRoutes.ItemDegreesPlatoController.onPageLoad(testNorthernIrelandErn, testDraftId, testIndex1, NormalMode)
          }
        }

        "when GoodsType is Beer and GBWK or GBRC and ABV < 8.5" - {

          "to the Small Independent Producer Page" in {

            val userAnswers = emptyUserAnswers.copy(ern = testGreatBritainErn)
              .set(ItemExciseProductCodePage(testIndex1), testExciseProductCodeB000.code)
              .set(ItemAlcoholStrengthPage(testIndex1), BigDecimal(8.4))

            navigator.nextPage(ItemAlcoholStrengthPage(testIndex1), NormalMode, userAnswers) mustBe
              itemsRoutes.ItemSmallIndependentProducerController.onPageLoad(userAnswers.ern, userAnswers.draftId, testIndex1, NormalMode)
          }
        }

        "when GoodsType is Beer and GBWK or GBRC and ABV >= 8.5" - {

          "to the Item Quantity page" in {

            val userAnswers = emptyUserAnswers.copy(ern = testGreatBritainErn)
              .set(ItemExciseProductCodePage(testIndex1), testExciseProductCodeB000.code)
              .set(ItemAlcoholStrengthPage(testIndex1), BigDecimal(8.5))

            navigator.nextPage(ItemAlcoholStrengthPage(testIndex1), NormalMode, userAnswers) mustBe
              itemsRoutes.ItemQuantityController.onPageLoad(testGreatBritainErn, testDraftId, testIndex1, NormalMode)
          }
        }

        "when GoodsType is Spirits" - {

          "to the Item Maturation Period Age Page" in {

            val userAnswers = emptyUserAnswers.copy(ern = testGreatBritainErn)
              .set(ItemExciseProductCodePage(testIndex1), testExciseProductCodeS100.code)
              .set(ItemAlcoholStrengthPage(testIndex1), BigDecimal(8.5))

            navigator.nextPage(ItemAlcoholStrengthPage(testIndex1), NormalMode, userAnswers) mustBe
              itemsRoutes.ItemMaturationPeriodAgeController.onPageLoad(userAnswers.ern, userAnswers.draftId, testIndex1, NormalMode)
          }
        }

        "when GoodsType is anything else (e.g. Wine)" - {

          "to the Geographical Indication Choice Page" in {

            val userAnswers = emptyUserAnswers.copy(ern = testGreatBritainErn)
              .set(ItemExciseProductCodePage(testIndex1), testExciseProductCodeW100.code)
              .set(ItemAlcoholStrengthPage(testIndex1), BigDecimal(8.5))

            navigator.nextPage(ItemAlcoholStrengthPage(testIndex1), NormalMode, userAnswers) mustBe
              itemsRoutes.ItemGeographicalIndicationChoiceController.onPageLoad(testGreatBritainErn, testDraftId, testIndex1, NormalMode)
          }
        }
      }

      "must go from the Item Small Independent Producer page" - {

        "when the answer is 'Yes'" - {

          "to the Under Construction Page" in {

            val userAnswers = emptyUserAnswers.set(ItemSmallIndependentProducerPage(testIndex1), true)

            navigator.nextPage(ItemSmallIndependentProducerPage(testIndex1), NormalMode, userAnswers) mustBe
              itemsRoutes.ItemProducerSizeController.onPageLoad(testErn, testDraftId, 0, NormalMode)
          }
        }

        "when the answer is 'No'" - {

          "to the Item Quantity Page" in {

            val userAnswers = emptyUserAnswers.set(ItemSmallIndependentProducerPage(testIndex1), false)

            navigator.nextPage(ItemSmallIndependentProducerPage(testIndex1), NormalMode, userAnswers) mustBe
              itemsRoutes.ItemQuantityController.onPageLoad(testErn, testDraftId, testIndex1, NormalMode)
          }
        }
      }

      "must go from the ItemProducerSize page" - {

        "to the quantity page" in {

          navigator.nextPage(ItemProducerSizePage(0), NormalMode, emptyUserAnswers) mustBe
            itemsRoutes.ItemQuantityController.onPageLoad(testErn, testDraftId, testIndex1, NormalMode)
        }
      }

      "must go from the Item Quantity page" - {

        "to the Item Net Gross Mass Page" in {
          navigator.nextPage(ItemQuantityPage(testIndex1), NormalMode, emptyUserAnswers) mustBe
            itemsRoutes.ItemNetGrossMassController.onPageLoad(testErn, testDraftId, testIndex1, NormalMode)
        }
      }

      "must go from the Item Net Gross Mass Page" - {

        "to the bulk-packaging-choice page" in {
          navigator.nextPage(ItemNetGrossMassPage(testIndex1), NormalMode, emptyUserAnswers) mustBe
            itemsRoutes.ItemBulkPackagingChoiceController.onPageLoad(testErn, testDraftId, testIndex1, NormalMode)
        }
      }

      "must go from the Item Degrees Plato page" - {

        "when Alcohol Strength is < 8.5 abv" - {

          "to the Small Independent Producer Page" in {

            val userAnswers = emptyUserAnswers.copy(ern = testNorthernIrelandErn)
              .set(ItemAlcoholStrengthPage(testIndex1), BigDecimal(8.4))

            navigator.nextPage(ItemDegreesPlatoPage(testIndex1), NormalMode, userAnswers) mustBe
              itemsRoutes.ItemSmallIndependentProducerController.onPageLoad(userAnswers.ern, userAnswers.draftId, testIndex1, NormalMode)
          }
        }

        "when Alcohol Strength is >= 8.5 abv" - {

          "to the Item Quantity Page" in {

            val userAnswers = emptyUserAnswers.copy(ern = testNorthernIrelandErn)
              .set(ItemAlcoholStrengthPage(testIndex1), BigDecimal(8.5))

            navigator.nextPage(ItemDegreesPlatoPage(testIndex1), NormalMode, userAnswers) mustBe
              itemsRoutes.ItemQuantityController.onPageLoad(testNorthernIrelandErn, testDraftId, testIndex1, NormalMode)
          }
        }
      }

      "must go from the Item Maturation Period Age page" - {

        "to the Geographical Indication Choice Page" in {

          navigator.nextPage(ItemMaturationPeriodAgePage(testIndex1), NormalMode, emptyUserAnswers) mustBe
            itemsRoutes.ItemGeographicalIndicationChoiceController.onPageLoad(testErn, testDraftId, testIndex1, NormalMode)
        }
      }

      "must go from the Item Geographical Indication Choice page" - {

        "to the Geographical Indication Page" - {
          "when the answer is Yes (any option)" in {

            navigator.nextPage(ItemGeographicalIndicationChoicePage(testIndex1), NormalMode, emptyUserAnswers
              .set(ItemGeographicalIndicationChoicePage(testIndex1), ProtectedGeographicalIndication)
              .set(ItemAlcoholStrengthPage(testIndex1), BigDecimal(8.499))
              .set(ItemExciseProductCodePage(testIndex1), testExciseProductCodeW200.code)
            ) mustBe itemsRoutes.ItemGeographicalIndicationController.onPageLoad(testErn, testDraftId, testIndex1, NormalMode)
          }
        }

        "to the Small Independent Producer Page" - {
          "when the answer is No (and the ABV is < 8.5)" in {

            navigator.nextPage(ItemGeographicalIndicationChoicePage(testIndex1), NormalMode, emptyUserAnswers
              .set(ItemGeographicalIndicationChoicePage(testIndex1), NoGeographicalIndication)
              .set(ItemAlcoholStrengthPage(testIndex1), BigDecimal(8.499))
              .set(ItemExciseProductCodePage(testIndex1), testExciseProductCodeW200.code)
            ) mustBe itemsRoutes.ItemSmallIndependentProducerController.onPageLoad(testErn, testDraftId, testIndex1, NormalMode)
          }
        }

        "to the Quantity Page" - {

          "when the answer is No (and the ABV is >= 8.5)" in {

            navigator.nextPage(ItemGeographicalIndicationChoicePage(testIndex1), NormalMode, emptyUserAnswers
              .set(ItemGeographicalIndicationChoicePage(testIndex1), NoGeographicalIndication)
              .set(ItemAlcoholStrengthPage(testIndex1), BigDecimal(8.501))
              .set(ItemExciseProductCodePage(testIndex1), testExciseProductCodeW200.code)
            ) mustBe itemsRoutes.ItemQuantityController.onPageLoad(testErn, testDraftId, testIndex1, NormalMode)
          }
        }

        "to the Items index page" - {
          "when the goods type is not spirits / wine / fermented / intermediate (e.g., Tobacco) and answered no" in {
            navigator.nextPage(ItemGeographicalIndicationChoicePage(testIndex1), NormalMode, emptyUserAnswers
              .set(ItemGeographicalIndicationChoicePage(testIndex1), NoGeographicalIndication)
              .set(ItemAlcoholStrengthPage(testIndex1), BigDecimal(8.501))
              .set(ItemExciseProductCodePage(testIndex1), testExciseProductCodeT200.code)
            ) mustBe itemsRoutes.ItemsIndexController.onPageLoad(testErn, testDraftId)
          }

          "when there is no answers" in {
            navigator.nextPage(ItemGeographicalIndicationChoicePage(testIndex1), NormalMode, emptyUserAnswers
            ) mustBe itemsRoutes.ItemsIndexController.onPageLoad(testErn, testDraftId)
          }
        }
      }

      "must go from the Item Geographical Indication page" - {

        "to the Small Independent Producer Page" - {

          "when the alcoholic strength is < 8.5" in {

            navigator.nextPage(ItemGeographicalIndicationPage(testIndex1), NormalMode, emptyUserAnswers
              .set(ItemAlcoholStrengthPage(testIndex1), BigDecimal(8.499))
            ) mustBe itemsRoutes.ItemSmallIndependentProducerController.onPageLoad(testErn, testDraftId, testIndex1, NormalMode)
          }
        }

        "to the Quantity Page" - {

          "when the alcoholic strength is >= 8.5" in {

            navigator.nextPage(ItemGeographicalIndicationPage(testIndex1), NormalMode, emptyUserAnswers
              .set(ItemAlcoholStrengthPage(testIndex1), BigDecimal(8.5))
            ) mustBe itemsRoutes.ItemQuantityController.onPageLoad(testErn, testDraftId, testIndex1, NormalMode)
          }
        }

        "to the Items index page" - {

          "when there is no answers" in {
            navigator.nextPage(ItemGeographicalIndicationPage(testIndex1), NormalMode, emptyUserAnswers
            ) mustBe itemsRoutes.ItemsIndexController.onPageLoad(testErn, testDraftId)
          }
        }
      }

      "must go from the ItemFiscalMarksChoicePage" - {
        "to the Fiscal Marks page" - {

          "when the user answers yes" in {
            navigator.nextPage(ItemFiscalMarksChoicePage(testIndex1), NormalMode, emptyUserAnswers
              .set(ItemExciseProductCodePage(testIndex1), testExciseProductCodeT200.code)
              .set(ItemFiscalMarksChoicePage(testIndex1), true)
            ) mustBe itemsRoutes.ItemFiscalMarksController.onPageLoad(testErn, testDraftId, testIndex1, NormalMode)
          }
        }

        "to the Quantity page" - {

          "when the user answers no" in {
            navigator.nextPage(ItemFiscalMarksChoicePage(testIndex1), NormalMode, emptyUserAnswers
              .set(ItemExciseProductCodePage(testIndex1), testExciseProductCodeT200.code)
              .set(ItemFiscalMarksChoicePage(testIndex1), false)
            ) mustBe itemsRoutes.ItemQuantityController.onPageLoad(testErn, testDraftId, testIndex1, NormalMode)
          }
        }

        "to the Items Index page" - {
          "when there is no answer for ItemFiscalMarksChoicePage" in {
            navigator.nextPage(ItemFiscalMarksChoicePage(testIndex1), NormalMode, emptyUserAnswers
              .set(ItemExciseProductCodePage(testIndex1), testExciseProductCodeT200.code)
            ) mustBe itemsRoutes.ItemsIndexController.onPageLoad(testErn, testDraftId)
          }
        }

      }

      "must go from the ItemFiscalMarksPage" - {

        "to the Quantity page" in {
          navigator.nextPage(ItemFiscalMarksPage(testIndex1), NormalMode, emptyUserAnswers
            .set(ItemFiscalMarksPage(testIndex1), "some fiscal mark")
          ) mustBe itemsRoutes.ItemQuantityController.onPageLoad(testErn, testDraftId, testIndex1, NormalMode)
        }
      }

      "must go from the ItemBulkPackagingChoicePage" - {
        "to the Packaging Bulk Select page" - {

          "when the user answers yes" in {
            navigator.nextPage(ItemBulkPackagingChoicePage(testIndex1), NormalMode, emptyUserAnswers
              .set(ItemBulkPackagingChoicePage(testIndex1), true)
            ) mustBe itemsRoutes.ItemBulkPackagingSelectController.onPageLoad(testErn, testDraftId, testIndex1, NormalMode)
          }
        }

        "to the Imported Wine Choice page" - {
          //TODO: Redirect to CAM-ITM15
          "when the user answers no and EPC is wine" in {
            navigator.nextPage(ItemBulkPackagingChoicePage(testIndex1), NormalMode, emptyUserAnswers
              .set(ItemExciseProductCodePage(testIndex1), testExciseProductCodeW200.code)
              .set(ItemBulkPackagingChoicePage(testIndex1), false)
            ) mustBe testOnly.controllers.routes.UnderConstructionController.onPageLoad()
          }
        }

        "to the Packaging Select (Items Packaging Index) page" - {
          "when the user answers no and EPC is not wine" in {
            GoodsTypeModel.values.filterNot(_ == GoodsTypeModel.Wine).foreach(
              goodsType =>
                navigator.nextPage(ItemBulkPackagingChoicePage(testIndex1), NormalMode, emptyUserAnswers
                  .set(ItemExciseProductCodePage(testIndex1), s"${goodsType.code}200")
                  .set(ItemBulkPackagingChoicePage(testIndex1), false)
                ) mustBe itemsRoutes.ItemsPackagingIndexController.onPageLoad(testErn, testDraftId, testIndex1)
            )
          }
        }

      }

      "must go from the ItemBulkPackagingSelectPage" - {

        //TODO: redirect to CAM-ITM12
        "to the Wine Operations Choice page" - {
          "when the wine quantity is equal or over 60 litres" in {
            navigator.nextPage(ItemBulkPackagingSelectPage(testIndex1), NormalMode, emptyUserAnswers
              .set(ItemBulkPackagingSelectPage(testIndex1), BulkPackagingType(BulkLiquid, "Bulk, liquid"))
              .set(ItemExciseProductCodePage(testIndex1), testExciseProductCodeW200.code)
              .set(ItemQuantityPage(testIndex1), BigDecimal(60))
            ) mustBe testOnly.controllers.routes.UnderConstructionController.onPageLoad()
          }
        }

        //TODO: redirect to CAM-ITM15
        "to the Imported Wine Choice page" - {
          "when the wine quantity is under 60 litres" in {
            navigator.nextPage(ItemBulkPackagingSelectPage(testIndex1), NormalMode, emptyUserAnswers
              .set(ItemBulkPackagingSelectPage(testIndex1), BulkPackagingType(BulkLiquid, "Bulk, liquid"))
              .set(ItemExciseProductCodePage(testIndex1), testExciseProductCodeW200.code)
              .set(ItemQuantityPage(testIndex1), BigDecimal(59.99))
            ) mustBe testOnly.controllers.routes.UnderConstructionController.onPageLoad()
          }
        }

        //TODO: redirect to CAM-ITM28
        "to the Packaging Seal Choice page" - {
          "when goods type is not Wine" in {
            GoodsTypeModel.values.filterNot(_ == GoodsTypeModel.Wine).foreach { goodsType =>
              navigator.nextPage(ItemBulkPackagingSelectPage(testIndex1), NormalMode, emptyUserAnswers
                .set(ItemBulkPackagingSelectPage(testIndex1), BulkPackagingType(BulkLiquid, "Bulk, liquid"))
                .set(ItemExciseProductCodePage(testIndex1), s"${goodsType.code}300")
              ) mustBe testOnly.controllers.routes.UnderConstructionController.onPageLoad()
            }
          }
        }

        "to the Items Index page" - {
          "when there is no answer for ItemExciseProductCodePage" in {
            navigator.nextPage(ItemBulkPackagingSelectPage(testIndex1), NormalMode, emptyUserAnswers
              .set(ItemBulkPackagingSelectPage(testIndex1), BulkPackagingType(BulkLiquid, "Bulk, liquid"))
            ) mustBe itemsRoutes.ItemsIndexController.onPageLoad(testErn, testDraftId)
          }

          "when there is no answer for ItemQuantityPage when the EPC is Wine" in {
            navigator.nextPage(ItemBulkPackagingSelectPage(testIndex1), NormalMode, emptyUserAnswers
              .set(ItemExciseProductCodePage(testIndex1), testExciseProductCodeW200.code)
              .set(ItemBulkPackagingSelectPage(testIndex1), BulkPackagingType(BulkLiquid, "Bulk, liquid"))
            ) mustBe itemsRoutes.ItemsIndexController.onPageLoad(testErn, testDraftId)
          }
        }
      }

      "must go from the ItemSelectPackagingPage" - {
        "to the Packaging Quantity page" in {
          navigator.nextPage(ItemSelectPackagingPage(testIndex1, testPackagingIndex1), NormalMode, emptyUserAnswers
            .set(ItemExciseProductCodePage(testIndex1), testExciseProductCodeW200.code)
            .set(ItemSelectPackagingPage(testIndex1, testPackagingIndex1), ItemPackaging("AE", "Aerosol"))
          ) mustBe itemsRoutes.ItemPackagingQuantityController.onPageLoad(testErn, testDraftId, testIndex1, testPackagingIndex1, NormalMode)
        }
      }

      "must go from the ItemPackagingQuantityPage" - {
        "to the Packaging Product Type page" in {
          navigator.nextPage(ItemPackagingQuantityPage(testIndex1, testPackagingIndex1), NormalMode, emptyUserAnswers
            .set(ItemPackagingQuantityPage(testIndex1, testPackagingIndex1), "1")
          ) mustBe testOnly.controllers.routes.UnderConstructionController.onPageLoad()
        }
      }

      "must go from the ItemWineGrowingZonePage" - {

        "to the Wine More Information Choice page" in {

          navigator.nextPage(ItemWineGrowingZonePage(testIndex1), NormalMode, emptyUserAnswers) mustBe
            itemsRoutes.ItemWineMoreInformationChoiceController.onPageLoad(testErn, testDraftId, testIndex1, NormalMode)
        }
      }

      "must go from the ItemWineMoreInformationChoicePage" - {
        "to the Wine More Information page" - {
          "when the user answers yes" in {
            navigator.nextPage(ItemWineMoreInformationChoicePage(testIndex1), NormalMode, emptyUserAnswers
              .set(ItemExciseProductCodePage(testIndex1), testExciseProductCodeW200.code)
              .set(ItemWineMoreInformationChoicePage(testIndex1), true)
            ) mustBe itemsRoutes.ItemWineMoreInformationController.onPageLoad(testErn, testDraftId, testIndex1, NormalMode)
          }
        }

        "to the Select Packaging (Items Packaging Index) page" - {
          "when the user answers no and the item is not classed as bulk" in {
            navigator.nextPage(ItemWineMoreInformationChoicePage(testIndex1), NormalMode, emptyUserAnswers
              .set(ItemExciseProductCodePage(testIndex1), testExciseProductCodeW200.code)
              .set(ItemWineMoreInformationChoicePage(testIndex1), false)
              .set(ItemBulkPackagingChoicePage(testIndex1), false)
            ) mustBe itemsRoutes.ItemsPackagingIndexController.onPageLoad(testErn, testDraftId, testIndex1)
          }
        }

        //TODO: Redirect to CAM-ITM28
        "to the Packaging Seal Choice page" - {
          "when the user answers no and the item is classed as bulk" in {
            navigator.nextPage(ItemWineMoreInformationChoicePage(testIndex1), NormalMode, emptyUserAnswers
              .set(ItemExciseProductCodePage(testIndex1), testExciseProductCodeW200.code)
              .set(ItemWineMoreInformationChoicePage(testIndex1), false)
              .set(ItemBulkPackagingChoicePage(testIndex1), true)
            ) mustBe testOnly.controllers.routes.UnderConstructionController.onPageLoad()
          }
        }

        "to the Items Index page" - {
          "when the user has no answer for ItemWineMoreInformationChoicePage" in {
            navigator.nextPage(ItemWineMoreInformationChoicePage(testIndex1), NormalMode, emptyUserAnswers
              .set(ItemExciseProductCodePage(testIndex1), testExciseProductCodeW200.code)
              .set(ItemBulkPackagingChoicePage(testIndex1), true)
            ) mustBe itemsRoutes.ItemsIndexController.onPageLoad(testErn, testDraftId)
          }

          "when the user has no answer for ItemBulkPackagingChoicePage (when clicking no)" in {
            navigator.nextPage(ItemWineMoreInformationChoicePage(testIndex1), NormalMode, emptyUserAnswers
              .set(ItemExciseProductCodePage(testIndex1), testExciseProductCodeW200.code)
              .set(ItemWineMoreInformationChoicePage(testIndex1), false)
            ) mustBe itemsRoutes.ItemsIndexController.onPageLoad(testErn, testDraftId)
          }
        }
      }

      "must go from the ItemWineMoreInformationPage" - {

        "to the Select Packaging (Items Packaging Index) page" - {

          "when the user answers no and the item is not classed as bulk" in {
            navigator.nextPage(ItemWineMoreInformationPage(testIndex1), NormalMode, emptyUserAnswers
              .set(ItemBulkPackagingChoicePage(testIndex1), false)
            ) mustBe itemsRoutes.ItemsPackagingIndexController.onPageLoad(testErn, testDraftId, testIndex1)
          }
        }

        //TODO: Redirect to CAM-ITM28
        "to the Packaging Seal Choice page" - {

          "when the user answers no and the item is classed as bulk" in {
            navigator.nextPage(ItemWineMoreInformationPage(testIndex1), NormalMode, emptyUserAnswers
              .set(ItemBulkPackagingChoicePage(testIndex1), true)
            ) mustBe testOnly.controllers.routes.UnderConstructionController.onPageLoad()
          }
        }

        "to the Items Index page" - {

          "when the user has no answer for ItemBulkPackagingChoicePage (when clicking no)" in {
            navigator.nextPage(ItemWineMoreInformationChoicePage(testIndex1), NormalMode, emptyUserAnswers) mustBe
              itemsRoutes.ItemsIndexController.onPageLoad(testErn, testDraftId)
          }
        }
      }
    }

    "in Check mode" - {
      "must go from the Excise Product Code page" - {
        "to CAM-ITM38 page" - {
          "when the EPC has multiple commodity codes" in {
            navigator.nextPage(
              ItemExciseProductCodePage(testIndex1),
              CheckMode,
              emptyUserAnswers.set(ItemExciseProductCodePage(testIndex1), testExciseProductCodeB000.code)) mustBe
              controllers.sections.items.routes.ItemCommodityCodeController.onPageLoad(testErn, testDraftId, testIndex1, CheckMode)
          }
        }

        "to the Items index page" - {
          "when there is no answer" in {
            navigator.nextPage(ItemExciseProductCodePage(testIndex1),
              CheckMode, emptyUserAnswers) mustBe itemsRoutes.ItemsIndexController.onPageLoad(testErn, testDraftId)
          }
        }
      }

      "must go from the ItemWineMoreInformationChoice page" - {
        "to ItemWineMoreInformation page" - {
          "when the answer is 'Yes'" in {
            navigator.nextPage(
              ItemWineMoreInformationChoicePage(testIndex1), CheckMode, emptyUserAnswers.set(ItemWineMoreInformationChoicePage(testIndex1), true)
            ) mustBe itemsRoutes.ItemWineMoreInformationController.onPageLoad(testErn, testDraftId, testIndex1, CheckMode)
          }
        }

        //TODO: Update when CYA page is built CAM-ITM40
        "to CYA page" - {
          "when the answer is 'No'" in {
            navigator.nextPage(
              ItemWineMoreInformationChoicePage(testIndex1), CheckMode, emptyUserAnswers.set(ItemWineMoreInformationChoicePage(testIndex1), false)
            ) mustBe testOnly.controllers.routes.UnderConstructionController.onPageLoad()
          }
        }
      }

      "must go to CheckYourAnswersItemsController" in {
        //TODO: update to Items CYA when built
        case object UnknownPage extends Page
        navigator.nextPage(UnknownPage, CheckMode, emptyUserAnswers) mustBe
          testOnly.controllers.routes.UnderConstructionController.onPageLoad()
      }
    }

    "in Review mode" - {
      "must go from the Item Excise Product Code page" - {
        "to the Item Commodity Code page" - {
          "when ItemCommodityCodePage is empty" in {
            navigator.nextPage(
              ItemExciseProductCodePage(testIndex1),
              ReviewMode,
              emptyUserAnswers
            ) mustBe controllers.sections.items.routes.ItemCommodityCodeController.onPageLoad(testErn, testDraftId, testIndex1, ReviewMode)
          }
        }
        "to the Item Confirm Commodity Code page" - {
          "when ItemCommodityCodePage is not empty" in {
            navigator.nextPage(
              ItemExciseProductCodePage(testIndex1),
              ReviewMode,
              emptyUserAnswers.set(ItemCommodityCodePage(testIndex1), testCnCodeTobacco)
            ) mustBe controllers.sections.items.routes.ItemConfirmCommodityCodeController.onPageLoad(testErn, testDraftId, testIndex1)
          }
        }
      }
      "must go from the Item Commodity Code page" - {
        "to the Item Confirm Commodity Code page" in {
          navigator.nextPage(
            ItemCommodityCodePage(testIndex1),
            ReviewMode,
            emptyUserAnswers
          ) mustBe controllers.sections.items.routes.ItemConfirmCommodityCodeController.onPageLoad(testErn, testDraftId, testIndex1)
        }
      }

      "must go to CheckYourAnswers" in {
        case object UnknownPage extends Page
        navigator.nextPage(UnknownPage, ReviewMode, emptyUserAnswers) mustBe
          controllers.routes.CheckYourAnswersController.onPageLoad(testErn, testDraftId)
      }
    }
  }
}
