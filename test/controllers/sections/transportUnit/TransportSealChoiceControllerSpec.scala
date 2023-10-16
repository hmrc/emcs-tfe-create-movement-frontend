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
import forms.TransportSealChoiceFormProvider
import mocks.services.MockUserAnswersService
import models.NormalMode
import models.TransportUnitType.Container
import navigation.FakeNavigators.FakeNavigator
import navigation.Navigator
import pages.{TransportSealChoicePage, TransportUnitTypePage}
import play.api.Application
import play.api.inject.bind
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.UserAnswersService
import views.html.sections.transportUnit.TransportSealChoiceView

import scala.concurrent.Future

class TransportSealChoiceControllerSpec extends SpecBase with MockUserAnswersService {
 class Setup(application: Application) {
   val formProvider = new TransportSealChoiceFormProvider()
   val form = formProvider(Container)(messages(application))

 }
  def onwardRoute = Call("GET", "/foo")
  lazy val transportSealChoiceRoute = controllers.sections.transportUnit.routes.TransportSealChoiceController.onPageLoad(testErn, testLrn, NormalMode).url
  "TransportSealChoice Controller" - {
    val application = applicationBuilder(userAnswers = Some(emptyUserAnswers.set(TransportUnitTypePage, Container))).build()

    "must return OK and the correct view for a GET" in new Setup(application) {

      running(application) {
        val request = FakeRequest(GET, transportSealChoiceRoute)

        val result = route(application, request).value

        val view = application.injector.instanceOf[TransportSealChoiceView]

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form, NormalMode, Container)(dataRequest(request), messages(application)).toString
      }
    }
    val userAnswers = emptyUserAnswers.set(TransportUnitTypePage, Container).set(TransportSealChoicePage, true)
    val applicationAnswers = applicationBuilder(userAnswers = Some(userAnswers)).build()
    "must populate the view correctly on a GET when the question has previously been answered" in new Setup(applicationAnswers)  {

      running(applicationAnswers) {
        val request = FakeRequest(GET, transportSealChoiceRoute)

        val view = applicationAnswers.injector.instanceOf[TransportSealChoiceView]

        val result = route(applicationAnswers, request).value

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form.fill(true), NormalMode, Container)(dataRequest(request), messages(application)).toString
      }
    }

    val applicationMocked =
      applicationBuilder(userAnswers = Some(emptyUserAnswers.set(TransportUnitTypePage, Container)))
        .overrides(
          bind[Navigator].toInstance(new FakeNavigator(onwardRoute)),
          bind[UserAnswersService].toInstance(mockUserAnswersService)
        )
        .build()

    "must redirect to the next page when valid data is submitted" in new Setup(applicationMocked){

      MockUserAnswersService.set().returns(Future.successful(emptyUserAnswers))

      running(applicationMocked) {
        val request =
          FakeRequest(POST, transportSealChoiceRoute)
            .withFormUrlEncodedBody(("value", "true"))

        val result = route(applicationMocked, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual onwardRoute.url
      }
    }

    "must return a Bad Request and errors when invalid data is submitted" in new Setup(application) {

      running(application) {
        val request =
          FakeRequest(POST, transportSealChoiceRoute)
            .withFormUrlEncodedBody(("value", ""))

        val boundForm = form.bind(Map("value" -> ""))

        val view = application.injector.instanceOf[TransportSealChoiceView]

        val result = route(application, request).value

        status(result) mustEqual BAD_REQUEST
        contentAsString(result) mustEqual view(boundForm, NormalMode, Container)(dataRequest(request), messages(application)).toString
      }
    }

    val applicationNone = applicationBuilder(userAnswers = None).build()

    "must redirect to Journey Recovery for a GET if no existing data is found" in new Setup(applicationNone){

      running(applicationNone) {
        val request = FakeRequest(GET, transportSealChoiceRoute)

        val result = route(applicationNone, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad().url
      }
    }

    "must redirect to Journey Recovery for a POST if no existing data is found" in new Setup(applicationNone){

      running(applicationNone) {
        val request =
          FakeRequest(POST, transportSealChoiceRoute)
            .withFormUrlEncodedBody(("value", "true"))

        val result = route(applicationNone, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad().url
      }
    }
  }
}
