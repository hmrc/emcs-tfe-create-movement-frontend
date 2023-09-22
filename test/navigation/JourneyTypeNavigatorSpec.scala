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
import models._
import models.sections.journeyType.HowMovementTransported.{AirTransport, Other}
import pages._
import pages.sections.journeyType.{CheckYourAnswersJourneyTypePage, GiveInformationOtherTransportPage, HowMovementTransportedPage}

class JourneyTypeNavigatorSpec extends SpecBase {

  val navigator = new JourneyTypeNavigator

  "JourneyTypeNavigator" - {

    "in Normal mode" - {

      "must go from a page that doesn't exist in the route map to Index" in {

        case object UnknownPage extends Page
        navigator.nextPage(UnknownPage, NormalMode, emptyUserAnswers) mustBe
          routes.IndexController.onPageLoad(testErn)
      }

      "for the HowMovementTransported page" - {

        "must go to the GiveInformationOtherTransport page" - {

          "when the option selected is 'Other'" in {
            val userAnswers = emptyUserAnswers
              .set(LocalReferenceNumberPage, "123")
              .set(HowMovementTransportedPage, Other)

            navigator.nextPage(HowMovementTransportedPage, NormalMode, userAnswers) mustBe
              controllers.sections.journeyType.routes.GiveInformationOtherTransportController.onPageLoad(testErn, testLrn, NormalMode)
          }
        }

        // TODO update once CAM-JT03 has been created
        "must go to the Journey Type CYA page" - {

          "when the option selected is not `Other`" in {
            val userAnswers = emptyUserAnswers
              .set(LocalReferenceNumberPage, "123")
              .set(HowMovementTransportedPage, AirTransport)

            navigator.nextPage(HowMovementTransportedPage, NormalMode, userAnswers) mustBe
              controllers.sections.journeyType.routes.CheckYourAnswersJourneyTypeController.onPageLoad(testErn, testLrn)
          }
        }
      }

      "for the GiveInformationOtherTransport page" - {

        // TODO update once CAM-JT03 has been created
        "must go to the Journey Type CYA page" - {
          val userAnswers = emptyUserAnswers
            .set(LocalReferenceNumberPage, "123")
            .set(HowMovementTransportedPage, Other)
            .set(GiveInformationOtherTransportPage, "some information text")

          navigator.nextPage(GiveInformationOtherTransportPage, NormalMode, userAnswers) mustBe
            controllers.sections.journeyType.routes.CheckYourAnswersJourneyTypeController.onPageLoad(testErn, testLrn)

        }
      }

      "for the CheckYourAnswers page" - {

        // TODO update to confirmation page when created
        "must go to the construction page" - {
          val userAnswers = emptyUserAnswers
            .set(LocalReferenceNumberPage, "123")
            .set(HowMovementTransportedPage, Other)
            .set(GiveInformationOtherTransportPage, "some information text")

          navigator.nextPage(CheckYourAnswersJourneyTypePage, NormalMode, userAnswers) mustBe
            testOnly.controllers.routes.UnderConstructionController.onPageLoad()
        }
      }
    }

    "in Check mode" - {
    }


  }
}
