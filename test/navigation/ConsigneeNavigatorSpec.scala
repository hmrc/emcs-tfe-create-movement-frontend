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
import models.sections.consignee.ConsigneeExportInformation.{EoriNumber, NoInformation, VatNumber}
import models.{CheckMode, NormalMode, ReviewMode}
import pages.Page
import pages.sections.consignee._

class ConsigneeNavigatorSpec extends SpecBase {
  val navigator = new ConsigneeNavigator

  "ConsigneeNavigator" - {

    "in Normal mode" - {

      "must go from a page that doesn't exist in the route map to Consignee CYA" in {

        case object UnknownPage extends Page
        navigator.nextPage(UnknownPage, NormalMode, emptyUserAnswers) mustBe
          controllers.sections.consignee.routes.CheckYourAnswersConsigneeController.onPageLoad(testErn, testDraftId)
      }

      "for the ConsigneeAddress page" - {

        "must go to the Consignee Check Your Answers page" in {

          navigator.nextPage(ConsigneeAddressPage, NormalMode, emptyUserAnswers) mustBe
            controllers.sections.consignee.routes.CheckYourAnswersConsigneeController.onPageLoad(testErn, testDraftId)
        }
      }

      "for the ConsigneeExcise page" - {

        "must go to the ConsigneeAddress page" in {

          navigator.nextPage(ConsigneeExcisePage, NormalMode, emptyUserAnswers) mustBe
            controllers.sections.consignee.routes.ConsigneeAddressController.onPageLoad(testErn, testDraftId, NormalMode)
        }
      }

      "for the ConsigneeExportInformationPage" - {

        "must go to CAM-NEE12 export-vat page" - {
          "when VAT has been selected" in {
            val userAnswers = emptyUserAnswers
              .set(ConsigneeExportInformationPage, Set(VatNumber))

            navigator.nextPage(ConsigneeExportInformationPage, NormalMode, userAnswers) mustBe
              controllers.sections.consignee.routes.ConsigneeExportVatController.onPageLoad(testErn, testDraftId, NormalMode)
          }
        }

        "must go to CAM-NEE13 export-eori page" - {
          "when EORI has been selected" in {
            val userAnswers = emptyUserAnswers
              .set(ConsigneeExportInformationPage, Set(EoriNumber))

            navigator.nextPage(ConsigneeExportInformationPage, NormalMode, userAnswers) mustBe
              controllers.sections.consignee.routes.ConsigneeExportEoriController.onPageLoad(testErn, testDraftId, NormalMode)
          }
        }

        "must go to address page" - {
          "when neither VAT or EORI are selected" in {
            val userAnswers = emptyUserAnswers
              .set(ConsigneeExportInformationPage, Set(NoInformation))

            navigator.nextPage(ConsigneeExportInformationPage, NormalMode, userAnswers) mustBe
              controllers.sections.consignee.routes.ConsigneeAddressController.onPageLoad(testErn, testDraftId, NormalMode)
          }
        }

      }

      "for the ConsigneeExportVatPage" - {

        "must go to CAM-NEE13: consignee-export-EORI" - {
          "when the user has selected VAT and EORI on CAM-NEE11: consignee-export-information" in {

            navigator.nextPage(ConsigneeExportVatPage, NormalMode,
              emptyUserAnswers
                .set(ConsigneeExportInformationPage, Set(VatNumber, EoriNumber))
                .set(ConsigneeExportVatPage, "GB123456789")
            ) mustBe controllers.sections.consignee.routes.ConsigneeExportEoriController.onPageLoad(testErn, testDraftId, NormalMode)
          }
        }

        "must go to address page" - {
          "when the user has selected only VAT on CAM-NEE11: consignee-export-information" in {

            navigator.nextPage(ConsigneeExportVatPage, NormalMode,
              emptyUserAnswers
                .set(ConsigneeExportInformationPage, Set(VatNumber))
                .set(ConsigneeExportVatPage, "GB123456789")
            ) mustBe controllers.sections.consignee.routes.ConsigneeAddressController.onPageLoad(testErn, testDraftId, NormalMode)
          }
        }

      }

      "for the ConsigneeExportEoriPage" - {
        "must go to address page" in {
          val userAnswers = emptyUserAnswers
            .set(ConsigneeExportInformationPage, Set(EoriNumber))
            .set(ConsigneeExportEoriPage, testEori)

          navigator.nextPage(ConsigneeExportEoriPage, NormalMode, userAnswers) mustBe
            controllers.sections.consignee.routes.ConsigneeAddressController.onPageLoad(testErn, testDraftId, NormalMode)
        }
      }

      "for the CheckAnswersConsignee page" - {
        "must go to the tasklist" in {
          navigator.nextPage(CheckAnswersConsigneePage, NormalMode, emptyUserAnswers) mustBe
            routes.DraftMovementController.onPageLoad(testErn, testDraftId)
        }
      }
    }

    "in Check mode" - {

      "for the ConsigneeExcise page" - {

        "when there is no consignee address" - {
          "must go to the ConsigneeAddress page" in {
            navigator.nextPage(ConsigneeExcisePage, CheckMode, emptyUserAnswers) mustBe
              controllers.sections.consignee.routes.ConsigneeAddressController.onPageLoad(testErn, testDraftId, NormalMode)
          }
        }

        "when there is a consignee address" - {
          "must go to the CheckYourAnswersConsigneeController" in {
            val currentUserAnswers = emptyUserAnswers
              .set(ConsigneeAddressPage, testUserAddress)

            navigator.nextPage(ConsigneeExcisePage, CheckMode, currentUserAnswers) mustBe
              controllers.sections.consignee.routes.CheckYourAnswersConsigneeController.onPageLoad(testErn, testDraftId)
          }
        }
      }

      "must go to CheckYourAnswersConsigneeController" in {
        case object UnknownPage extends Page
        navigator.nextPage(UnknownPage, CheckMode, emptyUserAnswers) mustBe
          controllers.sections.consignee.routes.CheckYourAnswersConsigneeController.onPageLoad(testErn, testDraftId)
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
