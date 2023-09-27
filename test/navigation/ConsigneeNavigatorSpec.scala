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
import models.NormalMode
import models.sections.consignee.ConsigneeExportVat
import models.sections.consignee.ConsigneeExportVatType.{No, YesEoriNumber, YesVatNumber}
import pages.Page
import pages.sections.consignee._

class ConsigneeNavigatorSpec extends SpecBase {
  val navigator = new ConsigneeNavigator

  "ConsigneeNavigator" - {

    "in Normal mode" - {

      "must go from a page that doesn't exist in the route map to Index" in {

        case object UnknownPage extends Page
        navigator.nextPage(UnknownPage, NormalMode, emptyUserAnswers) mustBe
          routes.IndexController.onPageLoad(testErn)
      }
    }

    "for the ConsigneeBusinessNamePage" - {

      "must go to CAM-NEE07" in {
        val userAnswers = emptyUserAnswers
          .set(ConsigneeBusinessNamePage, "a business name")

          navigator.nextPage(ConsigneeBusinessNamePage, NormalMode, userAnswers) mustBe
            controllers.sections.consignee.routes.ConsigneeAddressController.onPageLoad(testErn, testLrn, NormalMode)
      }

    }

    "for the ConsigneeExportPage" - {

      "must go to CAM-NEE11" - {

        "when 'YES' is answered'" in {
          val userAnswers = emptyUserAnswers
            .set(ConsigneeExportPage, true)

          navigator.nextPage(ConsigneeExportPage, NormalMode, userAnswers) mustBe
            controllers.sections.consignee.routes.ConsigneeExportVatController.onPageLoad(userAnswers.ern, userAnswers.lrn, NormalMode)
        }
      }

      // TODO update once CAM-NEE01 has been created
      "must go to CAM-NEE01" - {

        "when 'YES' is answered'" in {
          val userAnswers = emptyUserAnswers
            .set(ConsigneeExportPage, false)

          navigator.nextPage(ConsigneeExportPage, NormalMode, userAnswers) mustBe
            testOnly.controllers.routes.UnderConstructionController.onPageLoad()
        }
      }

      "must go to the journey recovery" - {

        "when there is no answer'" in {
          val userAnswers = emptyUserAnswers

          navigator.nextPage(ConsigneeExportPage, NormalMode, userAnswers) mustBe
            controllers.routes.JourneyRecoveryController.onPageLoad()
        }
      }

    }

    "for the ConsigneeExportVatPage" - {
      "must go to CAM-NEE03 business name page" - {

        "when YES - VAT Number is answered'" in {
          val userAnswers = emptyUserAnswers
            .set(ConsigneeExportVatPage, ConsigneeExportVat(YesVatNumber, Some("vatnumber"), None))

          navigator.nextPage(ConsigneeExportVatPage, NormalMode, userAnswers) mustBe
            controllers.sections.consignee.routes.ConsigneeBusinessNameController.onPageLoad(testErn, testLrn, NormalMode)
        }

        "when YES - EORI Number is answered'" in {
          val userAnswers = emptyUserAnswers
            .set(ConsigneeExportVatPage, ConsigneeExportVat(YesEoriNumber, None, Some("eorinumber")))

          navigator.nextPage(ConsigneeExportVatPage, NormalMode, userAnswers) mustBe
            controllers.sections.consignee.routes.ConsigneeBusinessNameController.onPageLoad(testErn, testLrn, NormalMode)
        }


        "when NO is answered'" in {
          val userAnswers = emptyUserAnswers
            .set(ConsigneeExportVatPage, ConsigneeExportVat(No, None, None))

          navigator.nextPage(ConsigneeExportVatPage, NormalMode, userAnswers) mustBe
            controllers.sections.consignee.routes.ConsigneeBusinessNameController.onPageLoad(testErn, testLrn, NormalMode)
        }
      }
    }


  }
}
