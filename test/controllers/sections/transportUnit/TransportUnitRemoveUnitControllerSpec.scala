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
import forms.sections.transportUnit.TransportUnitRemoveUnitFormProvider
import mocks.services.MockUserAnswersService
import models.sections.transportUnit.TransportUnitType
import pages.sections.transportUnit.TransportUnitTypePage
import play.api.inject.bind
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.UserAnswersService
import views.html.sections.transportUnit.TransportUnitRemoveUnitView

import scala.concurrent.Future

class TransportUnitRemoveUnitControllerSpec extends SpecBase with MockUserAnswersService {

  val formProvider = new TransportUnitRemoveUnitFormProvider()
  val form = formProvider()

  lazy val transportUnitRemoveUnitRoute = routes.TransportUnitRemoveUnitController.onPageLoad(testErn, testLrn, testIndex1).url

  "TransportUnitRemoveUnit Controller" - {

    "must return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers =
        Some(emptyUserAnswers.set(TransportUnitTypePage(testIndex1), TransportUnitType.FixedTransport))
      ).build()

      running(application) {
        val request = FakeRequest(GET, transportUnitRemoveUnitRoute)

        val result = route(application, request).value

        val view = application.injector.instanceOf[TransportUnitRemoveUnitView]

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form, testIndex1)(dataRequest(request), messages(application)).toString
      }
    }

    "must redirect to the index controller when index is out of bounds (for GET)" in {

      val application =
        applicationBuilder(userAnswers = Some(
          emptyUserAnswers
            .set(TransportUnitTypePage(testIndex1), TransportUnitType.FixedTransport)
        ))
          .overrides(
            bind[UserAnswersService].toInstance(mockUserAnswersService)
          )
          .build()

      running(application) {
        val request =
          FakeRequest(POST, routes.TransportUnitRemoveUnitController.onPageLoad(testErn, testLrn, testIndex2).url)
            .withFormUrlEncodedBody(("value", "true"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual controllers.sections.transportUnit.routes.TransportUnitIndexController.onPageLoad(testErn, testLrn).url
      }
    }

    "must redirect to TU07 when the user answers no" in {

      val application =
        applicationBuilder(userAnswers = Some(
          emptyUserAnswers.set(TransportUnitTypePage(testIndex1), TransportUnitType.FixedTransport)
        ))
          .overrides(
            bind[UserAnswersService].toInstance(mockUserAnswersService)
          )
          .build()

      running(application) {
        val request =
          FakeRequest(POST, transportUnitRemoveUnitRoute)
            .withFormUrlEncodedBody(("value", "false"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        //TODO redirect to TU07 when implemented
        redirectLocation(result).value mustEqual testOnly.controllers.routes.UnderConstructionController.onPageLoad().url
      }
    }

    "must redirect to the index controller when the user answers yes (removing the transport unit)" in {

      MockUserAnswersService.set(
        emptyUserAnswers.set(TransportUnitTypePage(testIndex1), TransportUnitType.Container)
      ).returns(Future.successful(
        emptyUserAnswers.set(TransportUnitTypePage(testIndex1), TransportUnitType.Container)
      ))

      val application =
        applicationBuilder(userAnswers = Some(
          emptyUserAnswers
            .set(TransportUnitTypePage(testIndex1), TransportUnitType.FixedTransport)
            .set(TransportUnitTypePage(testIndex2), TransportUnitType.Container)
        ))
          .overrides(
            bind[UserAnswersService].toInstance(mockUserAnswersService)
          )
          .build()

      running(application) {
        val request =
          FakeRequest(POST, transportUnitRemoveUnitRoute)
            .withFormUrlEncodedBody(("value", "true"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual controllers.sections.transportUnit.routes.TransportUnitIndexController.onPageLoad(testErn, testLrn).url
      }
    }

    "must redirect to the index controller when index is out of bounds (for POST)" in {

      val application =
        applicationBuilder(userAnswers = Some(
          emptyUserAnswers
            .set(TransportUnitTypePage(testIndex1), TransportUnitType.FixedTransport)
        ))
          .overrides(
            bind[UserAnswersService].toInstance(mockUserAnswersService)
          )
          .build()

      running(application) {
        val request =
          FakeRequest(POST, routes.TransportUnitRemoveUnitController.onPageLoad(testErn, testLrn, testIndex2).url)
            .withFormUrlEncodedBody(("value", "true"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual controllers.sections.transportUnit.routes.TransportUnitIndexController.onPageLoad(testErn, testLrn).url
      }
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      val application = applicationBuilder(userAnswers = Some(
        emptyUserAnswers.set(TransportUnitTypePage(testIndex1), TransportUnitType.FixedTransport)
      )).build()

      running(application) {
        val request =
          FakeRequest(POST, transportUnitRemoveUnitRoute)
            .withFormUrlEncodedBody(("value", ""))

        val boundForm = form.bind(Map("value" -> ""))

        val view = application.injector.instanceOf[TransportUnitRemoveUnitView]

        val result = route(application, request).value

        status(result) mustEqual BAD_REQUEST
        contentAsString(result) mustEqual view(boundForm, testIndex1)(dataRequest(request), messages(application)).toString
      }
    }

    "must redirect to Journey Recovery for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      running(application) {
        val request = FakeRequest(GET, transportUnitRemoveUnitRoute)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual controllers.routes.JourneyRecoveryController.onPageLoad().url
      }
    }

    "must redirect to Journey Recovery for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      running(application) {
        val request =
          FakeRequest(POST, transportUnitRemoveUnitRoute)
            .withFormUrlEncodedBody(("value", "true"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual controllers.routes.JourneyRecoveryController.onPageLoad().url
      }
    }
  }
}
