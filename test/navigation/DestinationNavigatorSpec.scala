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
import pages.Page
import pages.sections.destination._

class DestinationNavigatorSpec extends SpecBase {
  val navigator = new DestinationNavigator

  "DestinationNavigator" - {

    "in Normal mode" - {

      "must go from a page that doesn't exist in the route map to Index" in {
        case object UnknownPage extends Page
        navigator.nextPage(UnknownPage, NormalMode, emptyUserAnswers) mustBe
          routes.IndexController.onPageLoad(testErn)
      }

      "for the DestinationWarehouseExcisePage" - {
        "must go to Destination Consignee details page (CAM-DES04) if answer is no" in {
          val userAnswers = emptyUserAnswers.set(DestinationWarehouseExcisePage, "Answer")

          navigator.nextPage(DestinationWarehouseExcisePage, NormalMode, userAnswers) mustBe
            controllers.sections.destination.routes.DestinationConsigneeDetailsController.onPageLoad(testErn, testLrn, NormalMode)

        }

      }

      "for the DestinationConsigneeDetailsPage" - {
        "must go to Destination Business Name page (CAM-DES04) if answer is no" in {
          val userAnswers = emptyUserAnswers.set(DestinationConsigneeDetailsPage, false)

          navigator.nextPage(DestinationConsigneeDetailsPage, NormalMode, userAnswers) mustBe
            controllers.sections.destination.routes.DestinationBusinessNameController.onPageLoad(testErn, testLrn, NormalMode)

        }

        "must go to Destination Check Answers page (CAM-DES06) if answer is yes" in {
          //TODO change when CAM-DES06 check answers page built
          val userAnswers = emptyUserAnswers.set(DestinationConsigneeDetailsPage, true)

          navigator.nextPage(DestinationConsigneeDetailsPage, NormalMode, userAnswers) mustBe
            testOnly.controllers.routes.UnderConstructionController.onPageLoad()
        }

        "must go to Journey Recovery Controller if no answer is present" in {
          navigator.nextPage(DestinationConsigneeDetailsPage, NormalMode, emptyUserAnswers) mustBe
            controllers.routes.JourneyRecoveryController.onPageLoad()
        }
      }

      "for the DestinationBusinessNamePage" - {

        "must go to Destination Address page" in {

          navigator.nextPage(DestinationBusinessNamePage, NormalMode, emptyUserAnswers) mustBe
            controllers.sections.destination.routes.DestinationAddressController.onPageLoad(testErn, testLrn, NormalMode)
        }
      }

      "for the DestinationAddressPage" - {

        "must go to test Only page" in {

          navigator.nextPage(DestinationAddressPage, NormalMode, emptyUserAnswers) mustBe
            testOnly.controllers.routes.UnderConstructionController.onPageLoad()
        }
      }

      "for the DestinationDetailsChoicePage" - {
        "must go to CAM-DES03 when user selects yes" in {
          val userAnswers = emptyUserAnswers.set(DestinationDetailsChoicePage, true)

          navigator.nextPage(DestinationDetailsChoicePage, NormalMode, userAnswers) mustBe
            controllers.sections.destination.routes.DestinationConsigneeDetailsController.onPageLoad(testErn, testLrn, NormalMode)
        }

        "must go to CAM-02 when user selects no" in {
          //TODO change when CAM-02 check answers page built
          val userAnswers = emptyUserAnswers.set(DestinationDetailsChoicePage, false)

          navigator.nextPage(DestinationDetailsChoicePage, NormalMode, userAnswers) mustBe
            testOnly.controllers.routes.UnderConstructionController.onPageLoad()
        }

        "must go to test Only page" in {

          navigator.nextPage(DestinationDetailsChoicePage, NormalMode, emptyUserAnswers) mustBe
            testOnly.controllers.routes.UnderConstructionController.onPageLoad()
        }
      }

      "for the DestinationWarehouseVatPage" - {
        "must go to test Only page" in {

          navigator.nextPage(DestinationWarehouseVatPage, NormalMode, emptyUserAnswers) mustBe
            controllers.sections.destination.routes.DestinationDetailsChoiceController.onPageLoad(testErn, testLrn, NormalMode)
        }
      }

    }
  }
}
