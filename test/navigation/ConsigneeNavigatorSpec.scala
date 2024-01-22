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
import models.sections.consignee.ConsigneeExportInformation
import models.sections.consignee.ConsigneeExportInformationType.{No, YesEoriNumber, YesVatNumber}
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

        "must go to the ConsigneeBusinessName page" in {

          navigator.nextPage(ConsigneeExcisePage, NormalMode, emptyUserAnswers) mustBe
            controllers.sections.consignee.routes.ConsigneeBusinessNameController.onPageLoad(testErn, testDraftId, NormalMode)
        }
      }

      "for the ConsigneeExportVatPage" - {

        "must go to CAM-NEE13: consignee-export-EORI" - {

          //TODO replace ignore to in when ETFE-3153 has been completed
          "when the user has selected VAT and EORI on CAM-NEE11: consignee-export-information" ignore {

            navigator.nextPage(ConsigneeExportVatPage, NormalMode,
              emptyUserAnswers
                .set(ConsigneeExportInformationPage, ConsigneeExportInformation(YesVatNumber, Some("vatnumber"), Some("eorinumber")))
                .set(ConsigneeExportVatPage, "GB123456789")
            ) mustBe testOnly.controllers.routes.UnderConstructionController.onPageLoad()
          }
        }

        "must go to CAM-NEE03: consignee-business-name" - {

          "when the user has selected only VAT on CAM-NEE11: consignee-export-information" in {

            navigator.nextPage(ConsigneeExportVatPage, NormalMode,
              emptyUserAnswers
                .set(ConsigneeExportInformationPage, ConsigneeExportInformation(YesVatNumber, Some("vatnumber"), None))
                .set(ConsigneeExportVatPage, "GB123456789")
            ) mustBe controllers.sections.consignee.routes.ConsigneeBusinessNameController.onPageLoad(testErn, testDraftId, NormalMode)
          }
        }

        "must go to the journey recovery" - {

          "when the user neither both options or VAT option have been selected" in {

            navigator.nextPage(ConsigneeExportVatPage, NormalMode,
              emptyUserAnswers
                .set(ConsigneeExportInformationPage, ConsigneeExportInformation(No, None, None))
                .set(ConsigneeExportVatPage, "GB123456789")
            ) mustBe controllers.routes.JourneyRecoveryController.onPageLoad()
          }

          "when no answer exists for ConsigneeExportVatPage" in {

            navigator.nextPage(ConsigneeExportVatPage, NormalMode,
              emptyUserAnswers
                .set(ConsigneeExportInformationPage, ConsigneeExportInformation(YesVatNumber, None, None))
            ) mustBe controllers.routes.JourneyRecoveryController.onPageLoad()
          }

          "when no answer exists for ConsigneeExportInformationPage" in {

            navigator.nextPage(ConsigneeExportVatPage, NormalMode,
              emptyUserAnswers
                .set(ConsigneeExportVatPage, "GB123456789")
            ) mustBe controllers.routes.JourneyRecoveryController.onPageLoad()
          }
        }
      }

      "for the ConsigneeBusinessNamePage" - {

        "must go to CAM-NEE07" in {
          val userAnswers = emptyUserAnswers
            .set(ConsigneeBusinessNamePage, "a business name")

          navigator.nextPage(ConsigneeBusinessNamePage, NormalMode, userAnswers) mustBe
            controllers.sections.consignee.routes.ConsigneeAddressController.onPageLoad(testErn, testDraftId, NormalMode)
        }

      }

      "for the ConsigneeExportPage" - {

        "must go to CAM-NEE11" - {

          "when 'YES' is answered'" in {
            val userAnswers = emptyUserAnswers
              .set(ConsigneeExportPage, true)

            navigator.nextPage(ConsigneeExportPage, NormalMode, userAnswers) mustBe
              controllers.sections.consignee.routes.ConsigneeExportInformationController.onPageLoad(userAnswers.ern, userAnswers.draftId, NormalMode)
          }
        }

        "must go to ConsigneeExcise page" - {

          "when 'NO' is answered'" in {
            val userAnswers = emptyUserAnswers
              .set(ConsigneeExportPage, false)

            navigator.nextPage(ConsigneeExportPage, NormalMode, userAnswers) mustBe
              controllers.sections.consignee.routes.ConsigneeExciseController.onPageLoad(testErn, testDraftId, NormalMode)
          }
        }

        "from ConsigneeExemptOrganisationPage to ConsigneeBusinessName" in {

          navigator.nextPage(ConsigneeExemptOrganisationPage, NormalMode, emptyUserAnswers) mustBe
            controllers.sections.consignee.routes.ConsigneeBusinessNameController.onPageLoad(testErn, testDraftId, NormalMode)
        }

        "must go to the journey recovery" - {

          "when there is no answer'" in {
            val userAnswers = emptyUserAnswers

            navigator.nextPage(ConsigneeExportPage, NormalMode, userAnswers) mustBe
              controllers.routes.JourneyRecoveryController.onPageLoad()
          }
        }
      }

      "for the ConsigneeExportInformationPage" - {

        "must go to CAM-NEE03 business name page" - {

          "when YES - VAT Number is answered'" in {
            val userAnswers = emptyUserAnswers
              .set(ConsigneeExportInformationPage, ConsigneeExportInformation(YesVatNumber, Some("vatnumber"), None))

            navigator.nextPage(ConsigneeExportInformationPage, NormalMode, userAnswers) mustBe
              controllers.sections.consignee.routes.ConsigneeBusinessNameController.onPageLoad(testErn, testDraftId, NormalMode)
          }

          "when YES - EORI Number is answered'" in {
            val userAnswers = emptyUserAnswers
              .set(ConsigneeExportInformationPage, ConsigneeExportInformation(YesEoriNumber, None, Some("eorinumber")))

            navigator.nextPage(ConsigneeExportInformationPage, NormalMode, userAnswers) mustBe
              controllers.sections.consignee.routes.ConsigneeBusinessNameController.onPageLoad(testErn, testDraftId, NormalMode)
          }


          "when NO is answered'" in {
            val userAnswers = emptyUserAnswers
              .set(ConsigneeExportInformationPage, ConsigneeExportInformation(No, None, None))

            navigator.nextPage(ConsigneeExportInformationPage, NormalMode, userAnswers) mustBe
              controllers.sections.consignee.routes.ConsigneeBusinessNameController.onPageLoad(testErn, testDraftId, NormalMode)
          }
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
