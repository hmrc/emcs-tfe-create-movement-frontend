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

package controllers.sections.guarantor

import base.SpecBase
import forms.sections.guarantor.GuarantorVatFormProvider
import mocks.services.MockUserAnswersService
import models.NormalMode
import models.sections.guarantor.GuarantorArranger.Transporter
import navigation.FakeNavigators.FakeGuarantorNavigator
import navigation.GuarantorNavigator
import pages.sections.guarantor.{GuarantorArrangerPage, GuarantorRequiredPage, GuarantorVatPage}
import play.api.inject.bind
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.UserAnswersService
import views.html.sections.guarantor.GuarantorVatView

import scala.concurrent.Future

class GuarantorVatControllerSpec extends SpecBase with MockUserAnswersService {

  def onwardRoute = Call("GET", "/foo")

  val formProvider = new GuarantorVatFormProvider()
  val form = formProvider()

  lazy val guarantorVatRoute = controllers.sections.guarantor.routes.GuarantorVatController.onPageLoad(testErn, testDraftId, NormalMode).url
  lazy val guarantorVatSubmitRoute = controllers.sections.guarantor.routes.GuarantorVatController.onSubmit(testErn, testDraftId, NormalMode).url

  "GuarantorVat Controller" - {

    "must return OK and the correct view for a GET" in {

      val userAnswersSoFar = emptyUserAnswers
        .set(GuarantorRequiredPage, true)
        .set(GuarantorArrangerPage, Transporter)

      val application = applicationBuilder(userAnswers = Some(userAnswersSoFar)).build()

      running(application) {
        val request = FakeRequest(GET, guarantorVatRoute)

        val result = route(application, request).value

        val view = application.injector.instanceOf[GuarantorVatView]

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form, Transporter, NormalMode)(dataRequest(request), messages(request)).toString
      }
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswersSoFar = emptyUserAnswers
        .set(GuarantorRequiredPage, true)
        .set(GuarantorArrangerPage, Transporter)
        .set(GuarantorVatPage, "answer")

      val application = applicationBuilder(userAnswers = Some(userAnswersSoFar)).build()

      running(application) {
        val request = FakeRequest(GET, guarantorVatRoute)

        val view = application.injector.instanceOf[GuarantorVatView]

        val result = route(application, request).value

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form.fill("answer"), Transporter, NormalMode)(dataRequest(request), messages(request)).toString
      }
    }

    "must redirect to the next page when valid data is submitted" in {

      val userAnswersSoFar = emptyUserAnswers
        .set(GuarantorRequiredPage, true)
        .set(GuarantorArrangerPage, Transporter)

      MockUserAnswersService.set().returns(Future.successful(userAnswersSoFar))

      val application =
        applicationBuilder(userAnswers = Some(userAnswersSoFar))
          .overrides(
            bind[GuarantorNavigator].toInstance(new FakeGuarantorNavigator(onwardRoute)),
            bind[UserAnswersService].toInstance(mockUserAnswersService)
          )
          .build()

      running(application) {
        val request =
          FakeRequest(POST, guarantorVatRoute)
            .withFormUrlEncodedBody(("value", "answer"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual onwardRoute.url
      }
    }

    "must redirect to the next page when the NONGBVAT link is clicked" in {
      val userAnswersSoFar = emptyUserAnswers
        .set(GuarantorRequiredPage, true)
        .set(GuarantorArrangerPage, Transporter)

      MockUserAnswersService.set().returns(Future.successful(userAnswersSoFar))

      val application =
        applicationBuilder(userAnswers = Some(userAnswersSoFar))
          .overrides(
            bind[GuarantorNavigator].toInstance(new FakeGuarantorNavigator(onwardRoute)),
            bind[UserAnswersService].toInstance(mockUserAnswersService)
          )
          .build()

      running(application) {
        val request = FakeRequest(GET, controllers.sections.guarantor.routes.GuarantorVatController.onNonGbVAT(testErn, testDraftId, NormalMode).url)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual onwardRoute.url
      }
    }

    "must redirect to the guarantor index controller for a GET if no guarantor arranger value is found" in {
      val userAnswersSoFar = emptyUserAnswers
        .set(GuarantorRequiredPage, true)

      val application = applicationBuilder(userAnswers = Some(userAnswersSoFar)).build()

      running(application) {
        val request = FakeRequest(GET, guarantorVatRoute)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual
          controllers.sections.guarantor.routes.GuarantorIndexController.onPageLoad(testErn, testDraftId).url
      }
    }

    "must redirect to the guarantor index controller for a POST if no guarantor arranger value is found" in {
      val userAnswersSoFar = emptyUserAnswers
        .set(GuarantorRequiredPage, true)

      val application = applicationBuilder(userAnswers = Some(userAnswersSoFar)).build()

      running(application) {
        val request = FakeRequest(POST, guarantorVatSubmitRoute)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual
          controllers.sections.guarantor.routes.GuarantorIndexController.onPageLoad(testErn, testDraftId).url
      }
    }

    "must return a Bad Request and errors when invalid data is submitted" in {
      val userAnswersSoFar = emptyUserAnswers
        .set(GuarantorRequiredPage, true)
        .set(GuarantorArrangerPage, Transporter)

      val application = applicationBuilder(userAnswers = Some(userAnswersSoFar)).build()

      running(application) {
        val request =
          FakeRequest(POST, guarantorVatRoute)
            .withFormUrlEncodedBody(("value", ""))

        val boundForm = form.bind(Map("value" -> ""))

        val view = application.injector.instanceOf[GuarantorVatView]

        val result = route(application, request).value

        status(result) mustEqual BAD_REQUEST
        contentAsString(result) mustEqual view(boundForm, Transporter, NormalMode)(dataRequest(request), messages(request)).toString
      }
    }

    "must redirect to Journey Recovery for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      running(application) {
        val request = FakeRequest(GET, guarantorVatRoute)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual controllers.routes.JourneyRecoveryController.onPageLoad().url
      }
    }

    "must redirect to Journey Recovery for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      running(application) {
        val request =
          FakeRequest(POST, guarantorVatRoute)
            .withFormUrlEncodedBody(("value", "answer"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual controllers.routes.JourneyRecoveryController.onPageLoad().url
      }
    }
  }
}
