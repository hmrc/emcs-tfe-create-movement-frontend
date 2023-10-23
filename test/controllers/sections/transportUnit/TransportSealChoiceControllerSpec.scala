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
import controllers.routes
import forms.sections.transportUnit.TransportSealChoiceFormProvider
import mocks.services.MockUserAnswersService
import models.TransportUnitType.{Container, Tractor}
import models.sections.transportUnit.TransportSealTypeModel
import models.{NormalMode, UserAnswers}
import navigation.FakeNavigators.FakeTransportUnitNavigator
import navigation.TransportUnitNavigator
import pages.sections.transportUnit._
import play.api.inject.bind
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.UserAnswersService
import views.html.sections.transportUnit.TransportSealChoiceView

import scala.concurrent.Future

class TransportSealChoiceControllerSpec extends SpecBase with MockUserAnswersService {
 class Setup(userAnswers: Option[UserAnswers] = Some(emptyUserAnswers)) {

   val application = applicationBuilder(userAnswers)
     .overrides(
       bind[TransportUnitNavigator].toInstance(new FakeTransportUnitNavigator(onwardRoute)),
       bind[UserAnswersService].toInstance(mockUserAnswersService)
     )
     .build()

   val formProvider = new TransportSealChoiceFormProvider()

   val form = formProvider(Container)(messages(application))

   def onwardRoute = Call("GET", "/foo")

   lazy val transportUnit1SealChoiceRoute =
     controllers.sections.transportUnit.routes.TransportSealChoiceController.onPageLoad(testErn, testLrn, testIndex1, NormalMode).url

   lazy val transportUnit2SealChoiceRoute =
     controllers.sections.transportUnit.routes.TransportSealChoiceController.onPageLoad(testErn, testLrn, testIndex2, NormalMode).url

   lazy val transportSealChoiceOnSubmit =
     controllers.sections.transportUnit.routes.TransportSealChoiceController.onSubmit(testErn, testLrn, testIndex1, NormalMode)

   val view = application.injector.instanceOf[TransportSealChoiceView]

 }

  "TransportSealChoice Controller" - {

    "must return OK and the correct view for a GET" in new Setup(Some(emptyUserAnswers
      .set(TransportUnitTypePage(testIndex1), Container)
    )) {

      running(application) {

        val request = FakeRequest(GET, transportUnit1SealChoiceRoute)

        val result = route(application, request).value

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(
          form = form,
          mode = NormalMode,
          transportUnitType = Container,
          onSubmitCall = transportSealChoiceOnSubmit
        )(dataRequest(request), messages(application)).toString
      }
    }

    "must populate the view correctly on a GET when the question has previously been answered" in new Setup(Some(emptyUserAnswers
      .set(TransportUnitTypePage(testIndex1), Container)
      .set(TransportSealChoicePage(testIndex1), true)
    )) {

      running(application) {

        val request = FakeRequest(GET, transportUnit1SealChoiceRoute)

        val result = route(application, request).value

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(
          form = form.fill(true),
          mode = NormalMode,
          transportUnitType = Container,
          onSubmitCall = transportSealChoiceOnSubmit
        )(dataRequest(request), messages(application)).toString
      }
    }

    "must redirect to the transport unit index controller when missing the transport unit type answer" in new Setup(
      Some(emptyUserAnswers.set(TransportUnitIdentityPage(testIndex1), "answer"))) {

      running(application) {

        val request = FakeRequest(GET, transportUnit1SealChoiceRoute)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual
          controllers.sections.transportUnit.routes.TransportUnitIndexController.onPageLoad(testErn, testLrn).url
      }
    }

    "must redirect to the next page when valid data is submitted" in new Setup(Some(emptyUserAnswers
      .set(TransportUnitTypePage(testIndex1), Container)
    )) {

      MockUserAnswersService.set().returns(Future.successful(emptyUserAnswers))

      running(application) {
        val request =
          FakeRequest(POST, transportUnit1SealChoiceRoute)
            .withFormUrlEncodedBody(("value", "true"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual onwardRoute.url
      }
    }

    "must cleanse the transport seal type (TU04) when answering no" in new Setup(Some(
      emptyUserAnswers
        .set(TransportSealChoicePage(testIndex1), true)
        .set(TransportSealTypePage(testIndex1), TransportSealTypeModel("SEAL1", Some("xyz")))
        .set(TransportUnitTypePage(testIndex1), Container)
    )) {

      val expectedAnswers = emptyUserAnswers
        .set(TransportSealChoicePage(testIndex1), false)
        .set(TransportUnitTypePage(testIndex1), Container)

      MockUserAnswersService.set(expectedAnswers).returns(Future.successful(expectedAnswers))

      running(application) {

        val request = FakeRequest(POST, transportUnit1SealChoiceRoute).withFormUrlEncodedBody(("value", "false"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

      }
    }

    "must return a Bad Request and errors when invalid data is submitted" in new Setup(Some(emptyUserAnswers
      .set(TransportUnitTypePage(testIndex1), Container)
    )) {

      running(application) {
        val request =
          FakeRequest(POST, transportUnit1SealChoiceRoute)
            .withFormUrlEncodedBody(("value", ""))

        val boundForm = form.bind(Map("value" -> ""))

        val result = route(application, request).value

        status(result) mustEqual BAD_REQUEST
        contentAsString(result) mustEqual view(
          form = boundForm,
          mode = NormalMode,
          transportUnitType = Container,
          onSubmitCall = transportSealChoiceOnSubmit
        )(dataRequest(request), messages(application)).toString
      }
    }

    "must redirect to the transport unit index controller when transport unit type has not been answered" in new Setup(
      Some(emptyUserAnswers.set(TransportUnitIdentityPage(testIndex1), "answer"))) {

      running(application) {
        val request =
          FakeRequest(POST, transportUnit1SealChoiceRoute)
            .withFormUrlEncodedBody(("value", "true"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual
          controllers.sections.transportUnit.routes.TransportUnitIndexController.onPageLoad(testErn, testLrn).url
      }
    }

    "must redirect to Journey Recovery for a GET if no existing data is found" in new Setup(None) {

      running(application) {

        val request = FakeRequest(GET, transportUnit1SealChoiceRoute)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad().url
      }
    }

    "must redirect to Journey Recovery for a POST if no existing data is found" in new Setup(None) {

      running(application) {

        val request =
          FakeRequest(POST, transportUnit1SealChoiceRoute)
            .withFormUrlEncodedBody(("value", "true"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad().url
      }
    }

    "must redirect to transport unit index controller for a GET if the index in the url is not valid" in new Setup(Some(emptyUserAnswers
      .set(TransportSealTypePage(testIndex1), TransportSealTypeModel("some", None))
      .set(TransportUnitTypePage(testIndex1), Tractor))) {

      running(application) {
        val request = FakeRequest(GET, transportUnit2SealChoiceRoute)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual controllers.sections.transportUnit.routes.TransportUnitIndexController.onPageLoad(testErn, testLrn).url
      }
    }

    "must redirect to transport unit index controller for a POST if the index in the url is not valid" in new Setup(Some(emptyUserAnswers
      .set(TransportSealTypePage(testIndex1), TransportSealTypeModel("some", None))
      .set(TransportUnitTypePage(testIndex1), Tractor))) {

      running(application) {
        val request = FakeRequest(POST, transportUnit2SealChoiceRoute)
          .withFormUrlEncodedBody("value" -> "true")

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual controllers.sections.transportUnit.routes.TransportUnitIndexController.onPageLoad(testErn, testLrn).url
      }
    }
  }
}
