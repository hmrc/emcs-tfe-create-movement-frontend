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

package controllers.sections.destination

import base.SpecBase
import controllers.routes
import forms.sections.destination.DestinationDetailsChoiceFormProvider
import mocks.services.MockUserAnswersService
import models.sections.info.DispatchPlace.GreatBritain
import models.sections.info.movementScenario.MovementScenario.RegisteredConsignee
import models.{NormalMode, UserAnswers}
import navigation.DestinationNavigator
import navigation.FakeNavigators.FakeDestinationNavigator
import pages.sections.destination.DestinationDetailsChoicePage
import pages.sections.info.{DestinationTypePage, DispatchPlacePage}
import play.api.Application
import play.api.data.Form
import play.api.inject.bind
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.UserAnswersService
import views.html.sections.destination.DestinationDetailsChoiceView

import scala.concurrent.Future

class DestinationDetailsChoiceControllerSpec extends SpecBase with MockUserAnswersService {

  class Setup(optUserAnswers: Option[UserAnswers] = Some(emptyUserAnswers)) {
    val application: Application = applicationBuilder(optUserAnswers).build()
    def onwardRoute: Call = Call("GET", "/test-route")


    lazy val destinationDetailsChoiceRoute: String =
      controllers.sections.destination.routes.DestinationDetailsChoiceController.onPageLoad(testErn, testDraftId, NormalMode).url

    lazy val destinationDetailsChoiceSubmit: Call =
      controllers.sections.destination.routes.DestinationDetailsChoiceController.onSubmit(testErn, testDraftId, NormalMode)

    val formProvider: DestinationDetailsChoiceFormProvider = new DestinationDetailsChoiceFormProvider()
    lazy val form: Form[Boolean] = formProvider(RegisteredConsignee)(messages(application))

  }

  "DestinationDetailsChoice Controller" - {
    "must return OK and the correct view for a GET" in new Setup(Some(emptyUserAnswers.set(DestinationTypePage, RegisteredConsignee))) {
      running(application) {
        val request = FakeRequest(GET, destinationDetailsChoiceRoute)

        val result = route(application, request).value

        val view = application.injector.instanceOf[DestinationDetailsChoiceView]

        val expectedView = view(form, destinationDetailsChoiceSubmit, RegisteredConsignee)(dataRequest(request), messages(application)).toString

        status(result) mustEqual OK
        contentAsString(result) mustEqual expectedView
      }
    }

    "must populate the view correctly on a GET when the question has previously been answered" in
      new Setup(Some(emptyUserAnswers.set(DestinationDetailsChoicePage, true).set(DestinationTypePage, RegisteredConsignee))) {

        running(application) {
          val request = FakeRequest(GET, destinationDetailsChoiceRoute)

          val view = application.injector.instanceOf[DestinationDetailsChoiceView]

          val result = route(application, request).value

          val expectedView = view(form.fill(true), destinationDetailsChoiceSubmit, RegisteredConsignee)(dataRequest(request), messages(application)).toString

          status(result) mustEqual OK
          contentAsString(result) mustEqual expectedView
        }
      }

    "must redirect to the next page when valid data is submitted" in new Setup(Some(emptyUserAnswers.set(DestinationTypePage, RegisteredConsignee))) {
      MockUserAnswersService.set().returns(Future.successful(emptyUserAnswers))

      override val application: Application =
        applicationBuilder(userAnswers = Some(emptyUserAnswers.set(DestinationTypePage, RegisteredConsignee)))
          .overrides(
            bind[DestinationNavigator].toInstance(new FakeDestinationNavigator(onwardRoute)),
            bind[UserAnswersService].toInstance(mockUserAnswersService)
          )
          .build()

      running(application) {
        val request = FakeRequest(POST, destinationDetailsChoiceRoute).withFormUrlEncodedBody(("value", "true"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual onwardRoute.url
      }
    }

    "must return a Bad Request and errors when invalid data is submitted" in new Setup(Some(emptyUserAnswers.set(DestinationTypePage, RegisteredConsignee))) {
      running(application) {
        val request = FakeRequest(POST, destinationDetailsChoiceRoute).withFormUrlEncodedBody(("value", "invalid value"))

        val boundForm = form.bind(Map("value" -> "invalid value"))

        val view = application.injector.instanceOf[DestinationDetailsChoiceView]

        val result = route(application, request).value

        val expectedView = view(boundForm, destinationDetailsChoiceSubmit, RegisteredConsignee)(dataRequest(request), messages(application)).toString

        status(result) mustEqual BAD_REQUEST
        contentAsString(result) mustEqual expectedView
      }
    }

    "must redirect to journey recovery for a GET if the destination type value is invalid/none for this controller/page" in new Setup(Some(emptyUserAnswers
      .set(DispatchPlacePage, GreatBritain))) {

      running(application) {
        val request = FakeRequest(GET, destinationDetailsChoiceRoute)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad().url
      }
    }

    "must redirect to journey recovery for a POST if the destination type value is invalid/none for this controller/page" in new Setup(Some(emptyUserAnswers
      .set(DispatchPlacePage, GreatBritain))) {

      running(application) {
        val request = FakeRequest(POST, destinationDetailsChoiceRoute)
          .withFormUrlEncodedBody(("value", "answer"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad().url
      }
    }

    "must redirect to Journey Recovery for a GET if no existing data is found" in new Setup(None) {
      running(application) {
        val request = FakeRequest(GET, destinationDetailsChoiceRoute)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad().url
      }
    }

    "redirect to Journey Recovery for a POST if no existing data is found" in new Setup(None) {
      running(application) {
        val request = FakeRequest(POST, destinationDetailsChoiceRoute).withFormUrlEncodedBody(("value", "true"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad().url
      }
    }
  }

}
