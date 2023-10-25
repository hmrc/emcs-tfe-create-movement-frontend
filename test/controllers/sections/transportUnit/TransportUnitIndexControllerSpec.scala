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

package controllers.sections.transportUnit

import base.SpecBase
import mocks.services.MockUserAnswersService
import models.NormalMode
import models.sections.transportUnit.TransportUnitType.Container
import pages.sections.transportUnit._
import play.api.http.Status.SEE_OTHER
import play.api.test.FakeRequest
import play.api.test.Helpers._
import queries.TransportUnitsCount

class TransportUnitIndexControllerSpec extends SpecBase with MockUserAnswersService {

  lazy val transportUnitIndexRoute = controllers.sections.transportUnit.routes.TransportUnitIndexController.onPageLoad(testErn, testDraftId).url

  "TransportUnitIndex Controller" - {

    "when TransportUnitSection.isCompleted" - {
      // TODO: update route when CYA page is built (CAM-TU07)
      "must redirect to the CYA controller" in {
        val application = applicationBuilder(userAnswers = Some(
          emptyUserAnswers
            .set(TransportUnitTypePage(testIndex1), Container)
            .set(TransportUnitIdentityPage(testIndex1), "")
            .set(TransportSealChoicePage(testIndex1), false)
            .set(TransportUnitGiveMoreInformationChoicePage(testIndex1), false)
        )).build()

        running(application) {

          val request = FakeRequest(GET, transportUnitIndexRoute)

          val result = route(application, request).value

          status(result) mustEqual SEE_OTHER
          redirectLocation(result).value mustBe
            testOnly.controllers.routes.UnderConstructionController.onPageLoad().url
        }
      }
    }

    "must redirect to the transport unit type page (CAM-TU01) when there is an empty transport unit list" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers.set(TransportUnitsCount, Seq.empty))).build()

      running(application) {
        val request = FakeRequest(GET, transportUnitIndexRoute)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustBe controllers.sections.transportUnit.routes.TransportUnitTypeController.onPageLoad(testErn, testDraftId, testIndex1, NormalMode).url
      }
    }

    "must redirect to the transport unit type page (CAM-TU01) when no transport units answered" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, transportUnitIndexRoute)
        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustBe routes.TransportUnitTypeController.onPageLoad(testErn, testDraftId, testIndex1, NormalMode).url
      }
    }
  }
}
