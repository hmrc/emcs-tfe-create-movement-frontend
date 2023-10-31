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

package controllers.sections.journeyType

import base.SpecBase
import models.NormalMode
import models.sections.journeyType.HowMovementTransported.SeaTransport
import pages.sections.journeyType.{GiveInformationOtherTransportPage, HowMovementTransportedPage, JourneyTimeDaysPage}
import play.api.http.Status.SEE_OTHER
import play.api.test.FakeRequest
import play.api.test.Helpers._

class JourneyTypeIndexControllerSpec extends SpecBase {
  "JourneyTypeIndexController" - {

    "when JourneyTypeSection.isCompleted" - {
      "must redirect to the CYA controller" in {
        val completedUserAnswers = emptyUserAnswers
          .set(HowMovementTransportedPage, SeaTransport)
          .set(GiveInformationOtherTransportPage, "information")
          .set(JourneyTimeDaysPage, 1)
        val application = applicationBuilder(userAnswers = Some(completedUserAnswers)).build()

        running(application) {

          val request = FakeRequest(GET, controllers.sections.journeyType.routes.JourneyTypeIndexController.onPageLoad(testErn, testDraftId).url)
          val result = route(application, request).value

          status(result) mustEqual SEE_OTHER
          redirectLocation(result) mustBe
            Some(controllers.sections.journeyType.routes.CheckYourAnswersJourneyTypeController.onPageLoad(testErn, testDraftId).url)
        }
      }
    }

    "must redirect to the how movement transported controller" in {
      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      running(application) {

        val request = FakeRequest(GET, controllers.sections.journeyType.routes.JourneyTypeIndexController.onPageLoad(testErn, testDraftId).url)
        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result) mustBe
          Some(controllers.sections.journeyType.routes.HowMovementTransportedController.onPageLoad(testErn, testDraftId, NormalMode).url)
      }
    }
  }
}
