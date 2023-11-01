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
import controllers.routes
import forms.sections.guarantor.GuarantorRequiredFormProvider
import mocks.services.MockUserAnswersService
import models.NormalMode
import models.sections.guarantor.GuarantorArranger.Transporter
import navigation.FakeNavigators.FakeGuarantorNavigator
import navigation.GuarantorNavigator
import pages.sections.guarantor.{GuarantorArrangerPage, GuarantorNamePage, GuarantorRequiredPage}
import play.api.inject.bind
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.UserAnswersService
import views.html.sections.guarantor.GuarantorRequiredView

import scala.concurrent.Future

class GuarantorRequiredControllerSpec extends SpecBase with MockUserAnswersService {

  def onwardRoute = Call("GET", "/foo")

  val formProvider = new GuarantorRequiredFormProvider()
  val form = formProvider()

  lazy val guarantorRequiredRoute = controllers.sections.guarantor.routes.GuarantorRequiredController.onPageLoad(testErn, testDraftId, NormalMode).url

  "GuarantorRequired Controller" - {

    "must return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, guarantorRequiredRoute)

        val result = route(application, request).value

        val view = application.injector.instanceOf[GuarantorRequiredView]

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form, NormalMode)(dataRequest(request), messages(application)).toString
      }
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = emptyUserAnswers.set(GuarantorRequiredPage, true)

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, guarantorRequiredRoute)

        val view = application.injector.instanceOf[GuarantorRequiredView]

        val result = route(application, request).value

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form.fill(true), NormalMode)(dataRequest(request), messages(application)).toString
      }
    }

    "must redirect to the next page when valid data is submitted" in {

      MockUserAnswersService.set().returns(Future.successful(emptyUserAnswers))

      val application =
        applicationBuilder(userAnswers = Some(emptyUserAnswers))
          .overrides(
            bind[GuarantorNavigator].toInstance(new FakeGuarantorNavigator(onwardRoute)),
            bind[UserAnswersService].toInstance(mockUserAnswersService)
          )
          .build()

      running(application) {
        val request =
          FakeRequest(POST, guarantorRequiredRoute)
            .withFormUrlEncodedBody(("value", "true"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual onwardRoute.url
      }
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      running(application) {
        val request =
          FakeRequest(POST, guarantorRequiredRoute)
            .withFormUrlEncodedBody(("value", ""))

        val boundForm = form.bind(Map("value" -> ""))

        val view = application.injector.instanceOf[GuarantorRequiredView]

        val result = route(application, request).value

        status(result) mustEqual BAD_REQUEST
        contentAsString(result) mustEqual view(boundForm, NormalMode)(dataRequest(request), messages(application)).toString
      }
    }

    "must redirect to Journey Recovery for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      running(application) {
        val request = FakeRequest(GET, guarantorRequiredRoute)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad().url
      }
    }

    "must redirect to Journey Recovery for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      running(application) {
        val request =
          FakeRequest(POST, guarantorRequiredRoute)
            .withFormUrlEncodedBody(("value", "true"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad().url
      }
    }

    "must cleanse the guarantor section when answering no" in {
      val expectedAnswers = emptyUserAnswers.set(GuarantorRequiredPage, false)
      MockUserAnswersService.set(expectedAnswers).returns(Future.successful(expectedAnswers))

      val application = applicationBuilder(
        userAnswers = Some(
          emptyUserAnswers
            .set(GuarantorRequiredPage, true)
            .set(GuarantorArrangerPage, Transporter)
            .set(GuarantorNamePage, "a name")
        )
      )
        .overrides(
          bind[GuarantorNavigator].toInstance(new FakeGuarantorNavigator(onwardRoute)),
          bind[UserAnswersService].toInstance(mockUserAnswersService)
        )
        .build()


      val request = FakeRequest(POST, guarantorRequiredRoute).withFormUrlEncodedBody(("value", "false"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
    }

    "must cleanse the guarantor section when answering yes" in {
      val expectedAnswers = emptyUserAnswers.set(GuarantorRequiredPage, true)
      MockUserAnswersService.set(expectedAnswers).returns(Future.successful(expectedAnswers))

      val application = applicationBuilder(
        userAnswers = Some(
          emptyUserAnswers
            .set(GuarantorRequiredPage, false)
            .set(GuarantorArrangerPage, Transporter)
            .set(GuarantorNamePage, "a name")
        )
      )
        .overrides(
          bind[GuarantorNavigator].toInstance(new FakeGuarantorNavigator(onwardRoute)),
          bind[UserAnswersService].toInstance(mockUserAnswersService)
        )
        .build()


      val request = FakeRequest(POST, guarantorRequiredRoute).withFormUrlEncodedBody(("value", "true"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
    }

  }
}
