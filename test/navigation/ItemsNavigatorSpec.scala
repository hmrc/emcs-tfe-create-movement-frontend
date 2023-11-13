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
import controllers.routes
import controllers.sections.items.{routes => itemsRoutes}
import models.sections.items.ItemBrandNameModel
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

        //TODO: Route to CAM-ITM43 when implemented
        "to CAM-ITM43 page" - {
          Seq("S500", "T300", "S400", "E600", "E800", "E910").foreach(epc => {
            s"when the EPC is $epc" in {
              navigator.nextPage(
                ItemExciseProductCodePage(testIndex1),
                NormalMode,
                emptyUserAnswers.set(ItemExciseProductCodePage(testIndex1), epc)) mustBe
                testOnly.controllers.routes.UnderConstructionController.onPageLoad()
            }
          })
        }

        //TODO: Route to CAM-ITM43 when implemented
        "to CAM-ITM38 page" - {
          "when the EPC has multiple commodity codes" in {
            navigator.nextPage(
              ItemExciseProductCodePage(testIndex1),
              NormalMode,
              emptyUserAnswers.set(ItemExciseProductCodePage(testIndex1), "B000")) mustBe
              testOnly.controllers.routes.UnderConstructionController.onPageLoad()
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
          //TODO: Update routing as part of future story when next page in flow is built
          "to the Under Construction Page" in {
            val userAnswers = emptyUserAnswers
              .set(ItemExciseProductCodePage(testIndex1), "T200")

            navigator.nextPage(CommercialDescriptionPage(testIndex1), NormalMode, userAnswers) mustBe
              testOnly.controllers.routes.UnderConstructionController.onPageLoad()
          }
        }
        "when GoodsType is one type of Energy " - {
          //TODO: Update routing as part of future story when next page in flow is built
          "to the Under Construction Page" in {
            val userAnswers = emptyUserAnswers
              .set(ItemExciseProductCodePage(testIndex1), "E200")

            navigator.nextPage(CommercialDescriptionPage(testIndex1), NormalMode, userAnswers) mustBe
              testOnly.controllers.routes.UnderConstructionController.onPageLoad()
          }
        }

        "when GoodsType is other Energy code " - {
          //TODO: Update routing as part of future story when next page in flow is built
          "to the Under Construction Page" in {
            val userAnswers = emptyUserAnswers
              .set(ItemExciseProductCodePage(testIndex1), "E470")

            navigator.nextPage(CommercialDescriptionPage(testIndex1), NormalMode, userAnswers) mustBe
              testOnly.controllers.routes.UnderConstructionController.onPageLoad()
          }
        }
      }

      "must go from the Item Alcohol Strength page" - {

        "when GoodsType is Beer and XIWK or XIRC" - {

          //TODO: Redirect to CAM-ITM07
          "to the Under Construction Page" in {

            val userAnswers = emptyUserAnswers.copy(ern = testNorthernIrelandErn)
              .set(ItemExciseProductCodePage(testIndex1), "B200")
              .set(ItemAlcoholStrengthPage(testIndex1), BigDecimal(1.5))

            navigator.nextPage(ItemAlcoholStrengthPage(testIndex1), NormalMode, userAnswers) mustBe
              testOnly.controllers.routes.UnderConstructionController.onPageLoad()
          }
        }

        "when GoodsType is Beer and GBWK or GBRC and ABV < 8.5" - {

          //TODO: Redirect to CAM-ITM41
          "to the Under Construction Page" in {

            val userAnswers = emptyUserAnswers.copy(ern = testGreatBritainErn)
              .set(ItemExciseProductCodePage(testIndex1), "B200")
              .set(ItemAlcoholStrengthPage(testIndex1), BigDecimal(8.4))

            navigator.nextPage(ItemAlcoholStrengthPage(testIndex1), NormalMode, userAnswers) mustBe
              testOnly.controllers.routes.UnderConstructionController.onPageLoad()
          }
        }

        "when GoodsType is Beer and GBWK or GBRC and ABV >= 8.5" - {

          //TODO: Redirect to CAM-ITM19
          "to the Under Construction Page" in {

            val userAnswers = emptyUserAnswers.copy(ern = testGreatBritainErn)
              .set(ItemExciseProductCodePage(testIndex1), "B200")
              .set(ItemAlcoholStrengthPage(testIndex1), BigDecimal(8.5))

            navigator.nextPage(ItemAlcoholStrengthPage(testIndex1), NormalMode, userAnswers) mustBe
              testOnly.controllers.routes.UnderConstructionController.onPageLoad()
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

          //TODO: Redirect to CAM-ITM09
          "to the Under Construction Page" in {

            val userAnswers = emptyUserAnswers.copy(ern = testGreatBritainErn)
              .set(ItemExciseProductCodePage(testIndex1), "W100")
              .set(ItemAlcoholStrengthPage(testIndex1), BigDecimal(8.5))

            navigator.nextPage(ItemAlcoholStrengthPage(testIndex1), NormalMode, userAnswers) mustBe
              testOnly.controllers.routes.UnderConstructionController.onPageLoad()
          }
        }
      }
    }

    "in Check mode" - {
      "must go from the Excise Product Code page" - {

        //TODO: Route to CAM-ITM43 when implemented
        "to CAM-ITM43 page" - {
          Seq("S500", "T300", "S400", "E600", "E800", "E910").foreach(epc => {
            s"when the EPC is $epc" in {
              navigator.nextPage(
                ItemExciseProductCodePage(testIndex1),
                CheckMode,
                emptyUserAnswers.set(ItemExciseProductCodePage(testIndex1), epc)) mustBe
                testOnly.controllers.routes.UnderConstructionController.onPageLoad()
            }
          })
        }

      //TODO: Route to CAM-ITM43 when implemented
      "to CAM-ITM38 page" - {
        "when the EPC has multiple commodity codes" in {
          navigator.nextPage(
            ItemExciseProductCodePage(testIndex1),
            CheckMode,
            emptyUserAnswers.set(ItemExciseProductCodePage(testIndex1), "B000")) mustBe
            testOnly.controllers.routes.UnderConstructionController.onPageLoad()
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
          routes.CheckYourAnswersController.onPageLoad(testErn, testDraftId)
      }
    }
  }
}
