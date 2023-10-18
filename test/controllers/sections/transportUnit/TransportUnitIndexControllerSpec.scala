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
import models.{Index, NormalMode, TransportUnitType}
import pages.sections.transportUnit.TransportUnitTypePage
import play.api.test.FakeRequest
import play.api.test.Helpers._

class TransportUnitIndexControllerSpec extends SpecBase with MockUserAnswersService {

  lazy val transportUnitIndexRoute = controllers.sections.transportUnit.routes.TransportUnitIndexController.onPageLoad(testErn, testLrn).url

  "transportUnitIndex Controller" - {

    "must redirect to the transport unit type page (CAM-TU01) when no transport units answered" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, transportUnitIndexRoute)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result) mustBe
          Some(routes.TransportUnitTypeController.onPageLoad(testErn, testLrn, Index(0), NormalMode).url)
      }
    }

    "must redirect to the add to list page (CAM-TU07) when any answer is present" in {

      val userAnswers = emptyUserAnswers.set(TransportUnitTypePage(Index(0)), TransportUnitType.Vehicle)

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, transportUnitIndexRoute)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        //TODO: when page implemented redirect to CAM-TU07
        redirectLocation(result) mustBe
          Some(testOnly.controllers.routes.UnderConstructionController.onPageLoad().url)
      }
    }


  }
}
