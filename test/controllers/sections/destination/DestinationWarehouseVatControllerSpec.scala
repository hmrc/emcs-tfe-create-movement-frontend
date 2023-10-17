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

package controllers

import base.SpecBase
import forms.sections.destination.DestinationWarehouseVatFormProvider
import mocks.services.MockUserAnswersService
import models.DispatchPlace.GreatBritain
import models.sections.info.movementScenario.MovementScenario._
import models.{DispatchPlace, NormalMode, UserAnswers}
import navigation.FakeNavigators.FakeDestinationNavigator
import navigation.DestinationNavigator
import pages.{DestinationWarehouseVatPage, DispatchPlacePage}
import pages.sections.info.DestinationTypePage
import play.api.inject.bind
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.UserAnswersService
import views.html.sections.destination.DestinationWarehouseVatView

import scala.concurrent.Future

class DestinationWarehouseVatControllerSpec extends SpecBase with MockUserAnswersService {

  class Fixture(userAnswers: Option[UserAnswers] = Some(emptyUserAnswers)) {
    def onwardRoute = Call("GET", "/foo")

    lazy val destinationWarehouseVatRoute = controllers.sections.destination.routes.DestinationWarehouseVatController.onPageLoad(testErn, testLrn, NormalMode).url
    lazy val destinationWarehouseVatOnSubmit = controllers.sections.destination.routes.DestinationWarehouseVatController.onSubmit(testErn, testLrn, NormalMode)
    lazy val destinationDetailsChoiceRoute = controllers.sections.destination.routes.DestinationDetailsChoiceController.onPageLoad(testErn, testLrn, NormalMode)

    val application = applicationBuilder(userAnswers = userAnswers)
      .overrides(
        bind[DestinationNavigator].toInstance(new FakeDestinationNavigator(onwardRoute)),
        bind[UserAnswersService].toInstance(mockUserAnswersService)
      )
      .build()

    val formProvider = new DestinationWarehouseVatFormProvider()

    val form = formProvider(RegisteredConsignee)(messages(application))

    val view = application.injector.instanceOf[DestinationWarehouseVatView]


  }

    "DestinationWarehouseVat Controller" - {

      "must return OK and the correct view for a GET" in new Fixture(Some(emptyUserAnswers
        .set(DestinationTypePage, RegisteredConsignee))) {

        running(application) {
          val request = FakeRequest(GET, destinationWarehouseVatRoute)

          val result = route(application, request).value

          status(result) mustEqual OK
          contentAsString(result) mustEqual view(form,
            destinationWarehouseVatOnSubmit,
            RegisteredConsignee,
            destinationDetailsChoiceRoute)(dataRequest(request), messages(application)).toString
        }
      }

      "must populate the view correctly on a GET when the question has previously been answered" in new Fixture(Some(emptyUserAnswers
        .set(DestinationWarehouseVatPage, "answer").set(DestinationTypePage, RegisteredConsignee)
      )) {

        running(application) {
          val request = FakeRequest(GET, destinationWarehouseVatRoute)

          val result = route(application, request).value

          val expectedView = view(
            form.fill("answer"),
            destinationWarehouseVatOnSubmit,
            RegisteredConsignee,
            destinationDetailsChoiceRoute)(dataRequest(request), messages(application)).toString

          status(result) mustEqual OK
          contentAsString(result) mustEqual expectedView
        }
      }

      "must redirect to the next page when valid data is submitted" in new Fixture(Some(emptyUserAnswers
        .set(DestinationTypePage, RegisteredConsignee)
      )) {

        MockUserAnswersService.set().returns(Future.successful(emptyUserAnswers))


        running(application) {
          val request =
            FakeRequest(POST, destinationWarehouseVatRoute)
              .withFormUrlEncodedBody(("value", "answer"))

          val result = route(application, request).value

          status(result) mustEqual SEE_OTHER
          redirectLocation(result).value mustEqual onwardRoute.url
        }
      }

      "must return a Bad Request and errors when invalid data is submitted" in new Fixture(Some(emptyUserAnswers
        .set(DestinationTypePage, RegisteredConsignee)
      )) {

        running(application) {
          val request =
            FakeRequest(POST, destinationWarehouseVatRoute)
              .withFormUrlEncodedBody(("value", ""))

          val boundForm = form.bind(Map("value" -> ""))

          val result = route(application, request).value

          status(result) mustEqual BAD_REQUEST
          contentAsString(result) mustEqual view(boundForm, destinationWarehouseVatOnSubmit,
            RegisteredConsignee, destinationDetailsChoiceRoute
          )(dataRequest(request), messages(application)).toString
        }
      }

      "must redirect to destination type Page for a GET if the destination type value is invalid/none for this controller/page" in new Fixture(Some(emptyUserAnswers
        .set(DispatchPlacePage, GreatBritain))) {

        running(application) {
          val request = FakeRequest(GET, destinationWarehouseVatRoute)

          val result = route(application, request).value

          status(result) mustEqual SEE_OTHER
          redirectLocation(result).value mustEqual controllers.sections.info.routes.DestinationTypeController.onSubmit(testErn).url
        }
      }

      "must redirect to destination type Page for a POST if the destination type value is invalid/none for this controller/page" in new Fixture(Some(emptyUserAnswers
        .set(DispatchPlacePage, GreatBritain))) {

        running(application) {
          val request = FakeRequest(POST, destinationWarehouseVatRoute)
            .withFormUrlEncodedBody(("value", "answer"))

          val result = route(application, request).value

          status(result) mustEqual SEE_OTHER
          redirectLocation(result).value mustEqual controllers.sections.info.routes.DestinationTypeController.onSubmit(testErn).url
        }
      }

      "must redirect to Journey Recovery for a GET if no existing data is found" in new Fixture(None) {

        running(application) {
          val request = FakeRequest(GET, destinationWarehouseVatRoute)

          val result = route(application, request).value

          status(result) mustEqual SEE_OTHER
          redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad().url
        }
      }

      "must redirect to Journey Recovery for a POST if no existing data is found" in new Fixture(None)  {


        running(application) {
          val request =
            FakeRequest(POST, destinationWarehouseVatRoute)
              .withFormUrlEncodedBody(("value", "answer"))

          val result = route(application, request).value

          status(result) mustEqual SEE_OTHER
          redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad().url
        }
      }
    }
  }

