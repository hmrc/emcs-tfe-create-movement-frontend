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
import forms.sections.guarantor.GuarantorNameFormProvider
import mocks.services.MockUserAnswersService
import models.NormalMode
import models.sections.guarantor.GuarantorArranger.{Consignee, GoodsOwner}
import navigation.FakeNavigators.FakeGuarantorNavigator
import navigation.GuarantorNavigator
import pages.sections.guarantor.{GuarantorArrangerPage, GuarantorNamePage}
import play.api.inject.bind
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.UserAnswersService
import views.html.sections.guarantor.GuarantorNameView

import scala.concurrent.Future

class GuarantorNameControllerSpec extends SpecBase with MockUserAnswersService {

  def onwardRoute = Call("GET", "/foo")

  val formProvider = new GuarantorNameFormProvider()
  val form = formProvider()

  lazy val GuarantorNameRoute = controllers.sections.guarantor.routes.GuarantorNameController.onPageLoad(testErn, testDraftId, NormalMode).url

  "GuarantorArrangerName Controller" - {

    "must return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers.set(GuarantorArrangerPage, GoodsOwner))).build()

      running(application) {
        val request = FakeRequest(GET, GuarantorNameRoute)

        val result = route(application, request).value

        val view = application.injector.instanceOf[GuarantorNameView]

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form, GoodsOwner, NormalMode)(dataRequest(request), messages(request)).toString
      }
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = emptyUserAnswers
        .set(GuarantorArrangerPage, GoodsOwner)
        .set(GuarantorNamePage, "answer")

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, GuarantorNameRoute)

        val view = application.injector.instanceOf[GuarantorNameView]

        val result = route(application, request).value

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form.fill("answer"), GoodsOwner, NormalMode)(dataRequest(request), messages(request)).toString
      }
    }

    "must redirect to the next page when valid data is submitted" in {

      val userAnswer = emptyUserAnswers.set(GuarantorArrangerPage, GoodsOwner)

      MockUserAnswersService.set().returns(Future.successful(userAnswer))

      val application =
        applicationBuilder(userAnswers = Some(userAnswer))
          .overrides(
            bind[GuarantorNavigator].toInstance(new FakeGuarantorNavigator(onwardRoute)),
            bind[UserAnswersService].toInstance(mockUserAnswersService)
          )
          .build()

      running(application) {
        val request =
          FakeRequest(POST, GuarantorNameRoute)
            .withFormUrlEncodedBody(("value", "answer"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual onwardRoute.url
      }
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers.set(GuarantorArrangerPage, GoodsOwner))).build()

      running(application) {
        val request =
          FakeRequest(POST, GuarantorNameRoute)
            .withFormUrlEncodedBody(("value", ""))

        val boundForm = form.bind(Map("value" -> ""))

        val view = application.injector.instanceOf[GuarantorNameView]

        val result = route(application, request).value

        status(result) mustEqual BAD_REQUEST
        contentAsString(result) mustEqual view(boundForm, GoodsOwner, NormalMode)(dataRequest(request), messages(request)).toString
      }
    }

    "must redirect to Journey Recovery for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      running(application) {
        val request = FakeRequest(GET, GuarantorNameRoute)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual controllers.routes.JourneyRecoveryController.onPageLoad().url
      }
    }

    "must redirect to the guarantor index controller for a GET if no guarantor arranger value is found" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, GuarantorNameRoute)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual
          controllers.sections.guarantor.routes.GuarantorIndexController.onPageLoad(testErn, testDraftId).url
      }
    }

    "must redirect to CYA for a GET if the guarantor arranger value is invalid for this controller/page" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers.set(GuarantorArrangerPage, Consignee))).build()

      running(application) {
        val request = FakeRequest(GET, GuarantorNameRoute)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual controllers.sections.guarantor.routes.GuarantorCheckAnswersController.onPageLoad(testErn, testDraftId).url
      }
    }


    "must redirect to Journey Recovery for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      running(application) {
        val request =
          FakeRequest(POST, GuarantorNameRoute)
            .withFormUrlEncodedBody(("value", "answer"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual controllers.routes.JourneyRecoveryController.onPageLoad().url
      }
    }
  }
}
