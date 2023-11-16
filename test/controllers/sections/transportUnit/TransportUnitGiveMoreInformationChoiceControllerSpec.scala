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
import forms.sections.transportUnit.TransportUnitGiveMoreInformationChoiceFormProvider
import mocks.services.MockUserAnswersService
import models.sections.transportUnit.TransportUnitType
import models.sections.transportUnit.TransportUnitType.{Container, Tractor}
import models.{Index, NormalMode}
import navigation.FakeNavigators.FakeTransportUnitNavigator
import navigation.TransportUnitNavigator
import pages.sections.transportUnit.{TransportUnitGiveMoreInformationChoicePage, TransportUnitGiveMoreInformationPage, TransportUnitIdentityPage, TransportUnitTypePage}
import play.api.data.Form
import play.api.inject.bind
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.UserAnswersService
import views.html.sections.transportUnit.TransportUnitGiveMoreInformationChoiceView

import scala.concurrent.Future

class TransportUnitGiveMoreInformationChoiceControllerSpec extends SpecBase with MockUserAnswersService {

  val onwardRouteTU06: Call =
    controllers.sections.transportUnit.routes.TransportUnitGiveMoreInformationController.onPageLoad(testErn, testDraftId, testIndex1, NormalMode)

  val onwardRouteTU07: Call = Call("GET", s"/emcs/create-movement/trader/${testErn}/draft/$testDraftId/transport-units/add-to-list")

  val formProvider = new TransportUnitGiveMoreInformationChoiceFormProvider()

  lazy val transportUnit1GiveMoreInformationChoiceRoute =
    controllers.sections.transportUnit.routes.TransportUnitGiveMoreInformationChoiceController.onPageLoad(testErn, testDraftId, testIndex1, NormalMode).url
  lazy val transportUnit2GiveMoreInformationChoiceRoute =
    controllers.sections.transportUnit.routes.TransportUnitGiveMoreInformationChoiceController.onPageLoad(testErn, testDraftId, testIndex2, NormalMode).url

  "TransportUnitGiveMoreInformationChoice Controller" - {

    "must return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers.set(TransportUnitTypePage(testIndex1), Tractor))).build()

      def form(transportUnitType: TransportUnitType): Form[Boolean] = formProvider(transportUnitType)(messages(FakeRequest()))

      running(application) {
        val request = FakeRequest(GET, transportUnit1GiveMoreInformationChoiceRoute)

        val result = route(application, request).value

        val view = application.injector.instanceOf[TransportUnitGiveMoreInformationChoiceView]

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form(Tractor), testIndex1, NormalMode, Tractor)(dataRequest(request), messages(request)).toString
      }
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = emptyUserAnswers.set(TransportUnitGiveMoreInformationChoicePage(testIndex1), true).set(TransportUnitTypePage(testIndex1), Tractor)

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      def form(transportUnitType: TransportUnitType): Form[Boolean] = formProvider(transportUnitType)(messages(FakeRequest()))

      running(application) {
        val request = FakeRequest(GET, transportUnit1GiveMoreInformationChoiceRoute)

        val view = application.injector.instanceOf[TransportUnitGiveMoreInformationChoiceView]

        val result = route(application, request).value

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form(Tractor).fill(true), testIndex1, NormalMode, Tractor)(dataRequest(request), messages(request)).toString
      }
    }

    "must redirect to transport unit index controller for a GET if the index in the url is not valid" in {
      val userAnswers = emptyUserAnswers.set(TransportUnitGiveMoreInformationChoicePage(testIndex1), true).set(TransportUnitTypePage(testIndex1), Tractor)

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, transportUnit2GiveMoreInformationChoiceRoute)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual controllers.sections.transportUnit.routes.TransportUnitIndexController.onPageLoad(testErn, testDraftId).url
      }
    }

    "must redirect to transport unit index controller for a POST if the index in the url is not valid" in {
      val userAnswers = emptyUserAnswers.set(TransportUnitGiveMoreInformationChoicePage(testIndex1), true).set(TransportUnitTypePage(testIndex1), Tractor)

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      running(application) {
        val request = FakeRequest(POST, transportUnit2GiveMoreInformationChoiceRoute)
          .withFormUrlEncodedBody("value" -> "true")

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual controllers.sections.transportUnit.routes.TransportUnitIndexController.onPageLoad(testErn, testDraftId).url
      }
    }

    "must redirect to journey recovery for a GET if there is not a transport unit type found in the users answers" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers.set(TransportUnitIdentityPage(testIndex1), "answer"))).build()

      running(application) {
        val request = FakeRequest(GET, transportUnit1GiveMoreInformationChoiceRoute)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual controllers.routes.JourneyRecoveryController.onPageLoad().url
      }
    }

    "must redirect to the next page (TU06) when valid data is submitted (answer is Yes)" in {

      MockUserAnswersService
        .set()
        .returns(
          Future.successful(
            emptyUserAnswers
              .set(TransportUnitTypePage(testIndex1), Tractor)
              .set(TransportUnitGiveMoreInformationChoicePage(testIndex1), true)))

      val application =
        applicationBuilder(userAnswers = Some(emptyUserAnswers.set(TransportUnitTypePage(testIndex1), Tractor)))
          .overrides(
            bind[TransportUnitNavigator].toInstance(new FakeTransportUnitNavigator(onwardRouteTU06)),
            bind[UserAnswersService].toInstance(mockUserAnswersService)
          )
          .build()

      running(application) {
        val request =
          FakeRequest(POST, transportUnit1GiveMoreInformationChoiceRoute)
            .withFormUrlEncodedBody(("value", "true"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual onwardRouteTU06.url
      }
    }

    "must redirect to the next page (TU07) when valid data is submitted (answer is No)" in {

      MockUserAnswersService
        .set()
        .returns(
          Future.successful(
            emptyUserAnswers
              .set(TransportUnitTypePage(testIndex1), Tractor)
              .set(TransportUnitGiveMoreInformationChoicePage(testIndex1), true)))

      val application =
        applicationBuilder(userAnswers = Some(emptyUserAnswers.set(TransportUnitTypePage(testIndex1), Tractor)))
          .overrides(
            bind[TransportUnitNavigator].toInstance(new FakeTransportUnitNavigator(onwardRouteTU07)),
            bind[UserAnswersService].toInstance(mockUserAnswersService)
          )
          .build()

      running(application) {
        val request =
          FakeRequest(POST, transportUnit1GiveMoreInformationChoiceRoute)
            .withFormUrlEncodedBody(("value", "false"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual onwardRouteTU07.url
      }
    }

    "must cleanse the give more information page (TU06) when answering no" in {
      val expectedAnswers = emptyUserAnswers
        .set(TransportUnitGiveMoreInformationChoicePage(testIndex1), false)
        .set(TransportUnitTypePage(testIndex1), Container)

      MockUserAnswersService.set(expectedAnswers).returns(Future.successful(expectedAnswers))

      val application = applicationBuilder(
        userAnswers = Some(
          emptyUserAnswers
            .set(TransportUnitGiveMoreInformationChoicePage(testIndex1), true)
            .set(TransportUnitGiveMoreInformationPage(testIndex1), Some("beans"))
            .set(TransportUnitTypePage(testIndex1), Container)
        )
      )
        .overrides(
          bind[TransportUnitNavigator].toInstance(new FakeTransportUnitNavigator(onwardRouteTU07)),
          bind[UserAnswersService].toInstance(mockUserAnswersService)
        )
        .build()


      val request = FakeRequest(POST, transportUnit1GiveMoreInformationChoiceRoute).withFormUrlEncodedBody(("value", "false"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers.set(TransportUnitTypePage(testIndex1), Tractor))).build()

      def form(transportUnitType: TransportUnitType): Form[Boolean] = formProvider(transportUnitType)(messages(FakeRequest()))

      running(application) {
        val request =
          FakeRequest(POST, transportUnit1GiveMoreInformationChoiceRoute)
            .withFormUrlEncodedBody(("value", ""))

        val boundForm = form(Tractor).bind(Map("value" -> ""))

        val view = application.injector.instanceOf[TransportUnitGiveMoreInformationChoiceView]

        val result = route(application, request).value

        status(result) mustEqual BAD_REQUEST
        contentAsString(result) mustEqual view(boundForm, testIndex1, NormalMode, Tractor)(dataRequest(request), messages(request)).toString
      }
    }

    "must redirect to journey recovery controller for a POST if there is not a transport unit type found" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers.set(TransportUnitIdentityPage(Index(0)), "answer"))).build()

      running(application) {
        val request =
          FakeRequest(POST, transportUnit1GiveMoreInformationChoiceRoute)
            .withFormUrlEncodedBody(("value", "true"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual controllers.routes.JourneyRecoveryController.onPageLoad().url
      }
    }

    "must redirect to Journey Recovery for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      running(application) {
        val request = FakeRequest(GET, transportUnit1GiveMoreInformationChoiceRoute)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual controllers.routes.JourneyRecoveryController.onPageLoad().url
      }
    }

    "must redirect to Journey Recovery for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      running(application) {
        val request =
          FakeRequest(POST, transportUnit1GiveMoreInformationChoiceRoute)
            .withFormUrlEncodedBody(("value", "true"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual controllers.routes.JourneyRecoveryController.onPageLoad().url
      }
    }
  }
}
