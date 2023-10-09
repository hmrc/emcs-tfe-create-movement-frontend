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

package controllers.sections.transportArranger

import base.SpecBase
import forms.sections.transportArranger.TransportArrangerVatFormProvider
import mocks.services.MockUserAnswersService
import models.sections.transportArranger.TransportArranger.GoodsOwner
import models.{NormalMode, UserAnswers}
import navigation.FakeNavigators.FakeTransportArrangerNavigator
import navigation.TransportArrangerNavigator
import pages.sections.transportArranger.{TransportArrangerPage, TransportArrangerVatPage}
import play.api.inject.bind
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.UserAnswersService
import views.html.sections.transportArranger.TransportArrangerVatView

import scala.concurrent.Future

class TransportArrangerVatControllerSpec extends SpecBase with MockUserAnswersService {

  val goodsOwnerUserAnswers = emptyUserAnswers.set(TransportArrangerPage, GoodsOwner)

  class Fixture(val userAnswers: Option[UserAnswers] = Some(goodsOwnerUserAnswers)) {

    val formProvider = new TransportArrangerVatFormProvider()
    val form = formProvider()

    lazy val transportArrangerVatRoute = routes.TransportArrangerVatController.onPageLoad(testErn, testLrn, NormalMode).url
    lazy val transportArrangerNonGbVatRoute = routes.TransportArrangerVatController.onNonGbVAT(testErn, testLrn).url
    lazy val transportArrangerVatSubmitAction = routes.TransportArrangerVatController.onSubmit(testErn, testLrn, NormalMode)

    val application = applicationBuilder(userAnswers)
      .overrides(
        bind[TransportArrangerNavigator].toInstance(new FakeTransportArrangerNavigator(testOnwardRoute)),
        bind[UserAnswersService].toInstance(mockUserAnswersService)
      )
      .build()

    val view = application.injector.instanceOf[TransportArrangerVatView]
  }

  "TransportArrangerVat Controller" - {

    "must return OK and the correct view for a GET" in new Fixture() {
      running(application) {

        val request = FakeRequest(GET, transportArrangerVatRoute)
        val result = route(application, request).value

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form, transportArrangerVatSubmitAction, GoodsOwner)(dataRequest(request), messages(application)).toString
      }
    }

    "must redirect for a GET onNonGbVAT" in new Fixture() {
      running(application) {

        MockUserAnswersService.set().returns(Future.successful(goodsOwnerUserAnswers))

        val request = FakeRequest(GET, transportArrangerNonGbVatRoute)
        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual testOnwardRoute.url
      }
    }

    "must populate the view correctly on a GET when the question has previously been answered" in new Fixture(
      Some(goodsOwnerUserAnswers.set(TransportArrangerVatPage, "answer"))
    ) {
      running(application) {

        val request = FakeRequest(GET, transportArrangerVatRoute)
        val result = route(application, request).value

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form.fill("answer"), transportArrangerVatSubmitAction, GoodsOwner)(dataRequest(request), messages(application)).toString
      }
    }

    "must redirect to the next page when valid data is submitted" in new Fixture() {
      running(application) {

        MockUserAnswersService.set().returns(Future.successful(goodsOwnerUserAnswers))

        val request = FakeRequest(POST, transportArrangerVatRoute).withFormUrlEncodedBody(("value", "answer"))
        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual testOnwardRoute.url
      }
    }

    "must return a Bad Request and errors when invalid data is submitted" in new Fixture() {
      running(application) {

        val request = FakeRequest(POST, transportArrangerVatRoute).withFormUrlEncodedBody(("value", ""))
        val boundForm = form.bind(Map("value" -> ""))
        val result = route(application, request).value

        status(result) mustEqual BAD_REQUEST
        contentAsString(result) mustEqual view(boundForm, transportArrangerVatSubmitAction, GoodsOwner)(dataRequest(request), messages(application)).toString
      }
    }

    "must redirect to Journey Recovery for a GET if no existing data is found" in new Fixture(None) {
      running(application) {

        val request = FakeRequest(GET, transportArrangerVatRoute)
        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual controllers.routes.JourneyRecoveryController.onPageLoad().url
      }
    }

    "must redirect to Journey Recovery for a POST if no existing data is found" in new Fixture(None) {
      running(application) {

        val request = FakeRequest(POST, transportArrangerVatRoute).withFormUrlEncodedBody(("value", "answer"))
        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual controllers.routes.JourneyRecoveryController.onPageLoad().url
      }
    }
  }
}
