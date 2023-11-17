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
import models.sections.items.ItemBrandNameModel
import models.sections.items.ItemGeographicalIndicationType.{NoGeographicalIndication, ProtectedGeographicalIndication}
import models.{CheckMode, NormalMode, ReviewMode}
import pages.Page
import pages.sections.items._

class ItemsNavigatorSpec extends SpecBase {
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
              emptyUserAnswers.set(ItemExciseProductCodePage(testIndex1), "B000")
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
              .set(ItemExciseProductCodePage(testIndex1), "B200")

            navigator.nextPage(CommercialDescriptionPage(testIndex1), NormalMode, userAnswers) mustBe
              itemsRoutes.ItemAlcoholStrengthController.onPageLoad(testErn, testDraftId, testIndex1, NormalMode)
          }
        }
        "when GoodsType is Spirits" - {
          "to the  Alcohol Strength Page" in {
            val userAnswers = emptyUserAnswers
              .set(ItemExciseProductCodePage(testIndex1), "S200")

            navigator.nextPage(CommercialDescriptionPage(testIndex1), NormalMode, userAnswers) mustBe
              itemsRoutes.ItemAlcoholStrengthController.onPageLoad(testErn, testDraftId, testIndex1, NormalMode)
          }
        }
        "when GoodsType is Wine" - {
          "to the Alcohol Strength Page" in {
            val userAnswers = emptyUserAnswers
              .set(ItemExciseProductCodePage(testIndex1), "W200")

            navigator.nextPage(CommercialDescriptionPage(testIndex1), NormalMode, userAnswers) mustBe
              itemsRoutes.ItemAlcoholStrengthController.onPageLoad(testErn, testDraftId, testIndex1, NormalMode)
          }
        }
        "when GoodsType is Intermediate" - {
          "to the Alcohol Strength Page" in {
            val userAnswers = emptyUserAnswers
              .set(ItemExciseProductCodePage(testIndex1), "I200")

            navigator.nextPage(CommercialDescriptionPage(testIndex1), NormalMode, userAnswers) mustBe
              itemsRoutes.ItemAlcoholStrengthController.onPageLoad(testErn, testDraftId, testIndex1, NormalMode)
          }
        }
        "when GoodsType is one type of Tobacco " - {

          "to the Item Fiscal Marks Choice Page" in {
            val userAnswers = emptyUserAnswers
              .set(ItemExciseProductCodePage(testIndex1), "T200")

            navigator.nextPage(CommercialDescriptionPage(testIndex1), NormalMode, userAnswers) mustBe
              itemsRoutes.ItemFiscalMarksChoiceController.onPageLoad(testErn, testDraftId, testIndex1, NormalMode)
          }
        }

        "when GoodsType is Energy " - {

          Seq("E470", "E500", "E600", "E930").foreach(epc => {

            s"when the EPC is $epc" - {

              "to the Item Quantity Page" in {
                val userAnswers = emptyUserAnswers
                  .set(ItemExciseProductCodePage(testIndex1), epc)

                navigator.nextPage(CommercialDescriptionPage(testIndex1), NormalMode, userAnswers) mustBe
                  itemsRoutes.ItemQuantityController.onPageLoad(testErn, testDraftId, testIndex1, NormalMode)
              }
            }
          })

          s"when the EPC is anything else" - {

            //TODO: Route to CAM-ITM33
            "to the Under Construction Page" in {
              val userAnswers = emptyUserAnswers
                .set(ItemExciseProductCodePage(testIndex1), "E200")

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
              .set(ItemExciseProductCodePage(testIndex1), "B200")
              .set(ItemAlcoholStrengthPage(testIndex1), BigDecimal(1.5))

            navigator.nextPage(ItemAlcoholStrengthPage(testIndex1), NormalMode, userAnswers) mustBe
              itemsRoutes.ItemDegreesPlatoController.onPageLoad(testNorthernIrelandErn, testDraftId, testIndex1, NormalMode)
          }
        }

        "when GoodsType is Beer and GBWK or GBRC and ABV < 8.5" - {

          "to the Small Independent Producer Page" in {

            val userAnswers = emptyUserAnswers.copy(ern = testGreatBritainErn)
              .set(ItemExciseProductCodePage(testIndex1), "B200")
              .set(ItemAlcoholStrengthPage(testIndex1), BigDecimal(8.4))

            navigator.nextPage(ItemAlcoholStrengthPage(testIndex1), NormalMode, userAnswers) mustBe
              itemsRoutes.ItemSmallIndependentProducerController.onPageLoad(userAnswers.ern, userAnswers.draftId, testIndex1, NormalMode)
          }
        }

        "when GoodsType is Beer and GBWK or GBRC and ABV >= 8.5" - {

          "to the Item Quantity page" in {

            val userAnswers = emptyUserAnswers.copy(ern = testGreatBritainErn)
              .set(ItemExciseProductCodePage(testIndex1), "B200")
              .set(ItemAlcoholStrengthPage(testIndex1), BigDecimal(8.5))

            navigator.nextPage(ItemAlcoholStrengthPage(testIndex1), NormalMode, userAnswers) mustBe
              itemsRoutes.ItemQuantityController.onPageLoad(testGreatBritainErn, testDraftId, testIndex1, NormalMode)
          }
        }

        "when GoodsType is Spirits" - {

          //TODO: Redirect to CAM-ITM08
          "to the Under Construction Page" in {

            val userAnswers = emptyUserAnswers.copy(ern = testGreatBritainErn)
              .set(ItemExciseProductCodePage(testIndex1), "S100")
              .set(ItemAlcoholStrengthPage(testIndex1), BigDecimal(8.5))

            navigator.nextPage(ItemAlcoholStrengthPage(testIndex1), NormalMode, userAnswers) mustBe
              testOnly.controllers.routes.UnderConstructionController.onPageLoad()
          }
        }

        "when GoodsType is anything else (e.g. Wine)" - {

          "to the Geographical Indication Choice Page" in {

            val userAnswers = emptyUserAnswers.copy(ern = testGreatBritainErn)
              .set(ItemExciseProductCodePage(testIndex1), "W100")
              .set(ItemAlcoholStrengthPage(testIndex1), BigDecimal(8.5))

            navigator.nextPage(ItemAlcoholStrengthPage(testIndex1), NormalMode, userAnswers) mustBe
              itemsRoutes.ItemGeographicalIndicationChoiceController.onPageLoad(testGreatBritainErn, testDraftId, testIndex1, NormalMode)
          }
        }
      }

      "must go from the Item Small Independent Producer page" - {

        "when the answer is 'Yes'" - {

          //TODO: Redirect to CAM-ITM11
          "to the Under Construction Page" in {

            val userAnswers = emptyUserAnswers.set(ItemSmallIndependentProducerPage(testIndex1), true)

            navigator.nextPage(ItemSmallIndependentProducerPage(testIndex1), NormalMode, userAnswers) mustBe
              testOnly.controllers.routes.UnderConstructionController.onPageLoad()
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

      "must go from the Item Quantity page" - {

        //TODO: Redirect to CAM-ITM21
        "to the Under Construction Page" in {

          navigator.nextPage(ItemQuantityPage(testIndex1), NormalMode, emptyUserAnswers) mustBe
            testOnly.controllers.routes.UnderConstructionController.onPageLoad()
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

      "must go from the Item Geographical Indication Choice page" - {

        "to the Geographical Indication Page" - {
          "when the answer is Yes (any option)" in {

            navigator.nextPage(ItemGeographicalIndicationChoicePage(testIndex1), NormalMode, emptyUserAnswers
              .set(ItemGeographicalIndicationChoicePage(testIndex1), ProtectedGeographicalIndication)
              .set(ItemAlcoholStrengthPage(testIndex1), BigDecimal(8.499))
              .set(ItemExciseProductCodePage(testIndex1), "W200")
            ) mustBe itemsRoutes.ItemGeographicalIndicationController.onPageLoad(testErn, testDraftId, testIndex1, NormalMode)
          }
        }

        "to the Small Independent Producer Page" - {
          "when the answer is No (and the ABV is < 8.5)" in {

            navigator.nextPage(ItemGeographicalIndicationChoicePage(testIndex1), NormalMode, emptyUserAnswers
              .set(ItemGeographicalIndicationChoicePage(testIndex1), NoGeographicalIndication)
              .set(ItemAlcoholStrengthPage(testIndex1), BigDecimal(8.499))
              .set(ItemExciseProductCodePage(testIndex1), "W200")
            ) mustBe itemsRoutes.ItemSmallIndependentProducerController.onPageLoad(testErn, testDraftId, testIndex1, NormalMode)
          }
        }

        "to the Quantity Page" - {

          "when the answer is No (and the ABV is >= 8.5)" in {

            navigator.nextPage(ItemGeographicalIndicationChoicePage(testIndex1), NormalMode, emptyUserAnswers
              .set(ItemGeographicalIndicationChoicePage(testIndex1), NoGeographicalIndication)
              .set(ItemAlcoholStrengthPage(testIndex1), BigDecimal(8.501))
              .set(ItemExciseProductCodePage(testIndex1), "W200")
            ) mustBe itemsRoutes.ItemQuantityController.onPageLoad(testErn, testDraftId, testIndex1, NormalMode)
          }
        }

        "to the Items index page" - {
          "when the goods type is not spirits / wine / fermented / intermediate (e.g., Tobacco) and answered no" in {
            navigator.nextPage(ItemGeographicalIndicationChoicePage(testIndex1), NormalMode, emptyUserAnswers
              .set(ItemGeographicalIndicationChoicePage(testIndex1), NoGeographicalIndication)
              .set(ItemAlcoholStrengthPage(testIndex1), BigDecimal(8.501))
              .set(ItemExciseProductCodePage(testIndex1), "T200")
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
              .set(ItemExciseProductCodePage(testIndex1), "T200")
              .set(ItemFiscalMarksChoicePage(testIndex1), true)
            ) mustBe itemsRoutes.ItemFiscalMarksController.onPageLoad(testErn, testDraftId, testIndex1, NormalMode)
          }
        }

        "to the Quantity page" - {

          "when the user answers no" in {
            navigator.nextPage(ItemFiscalMarksChoicePage(testIndex1), NormalMode, emptyUserAnswers
              .set(ItemExciseProductCodePage(testIndex1), "T200")
              .set(ItemFiscalMarksChoicePage(testIndex1), false)
            ) mustBe itemsRoutes.ItemQuantityController.onPageLoad(testErn, testDraftId, testIndex1, NormalMode)
          }
        }

        "to the Items Index page" - {
          "when there is no answer for ItemFiscalMarksChoicePage" in {
            navigator.nextPage(ItemFiscalMarksChoicePage(testIndex1), NormalMode, emptyUserAnswers
              .set(ItemExciseProductCodePage(testIndex1), "T200")
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
    }

    "in Check mode" - {
      "must go from the Excise Product Code page" - {
        "to CAM-ITM38 page" - {
          "when the EPC has multiple commodity codes" in {
            navigator.nextPage(
              ItemExciseProductCodePage(testIndex1),
              CheckMode,
              emptyUserAnswers.set(ItemExciseProductCodePage(testIndex1), "B000")) mustBe
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

      "must go to CheckYourAnswersItemsController" in {
        //TODO: update to Items CYA when built
        case object UnknownPage extends Page
        navigator.nextPage(UnknownPage, CheckMode, emptyUserAnswers) mustBe
          testOnly.controllers.routes.UnderConstructionController.onPageLoad()
      }
    }

    "in Review mode" - {
      "must go to CheckYourAnswers" in {
        case object UnknownPage extends Page
        navigator.nextPage(UnknownPage, ReviewMode, emptyUserAnswers) mustBe
          controllers.routes.CheckYourAnswersController.onPageLoad(testErn, testDraftId)
      }
    }
  }
}
