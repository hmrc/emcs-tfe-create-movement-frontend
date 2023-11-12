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

package controllers.sections.documents

import base.SpecBase
import forms.sections.documents.ReferenceAvailableFormProvider
import mocks.services.MockUserAnswersService
import models.{NormalMode, UserAnswers}
import navigation.DocumentsNavigator
import navigation.FakeNavigators.FakeDocumentsNavigator
import pages.sections.documents.{DocumentDescriptionPage, DocumentReferencePage, ReferenceAvailablePage}
import play.api.inject.bind
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.UserAnswersService
import views.html.sections.documents.ReferenceAvailableView

import scala.concurrent.Future

class ReferenceAvailableControllerSpec extends SpecBase with MockUserAnswersService {

  class Setup(startingUserAnswers: Option[UserAnswers] = Some(emptyUserAnswers)) {

    def onwardRoute = Call("GET", "/foo")

    val application = applicationBuilder(userAnswers = startingUserAnswers)
      .overrides(
        bind[DocumentsNavigator].toInstance(new FakeDocumentsNavigator(onwardRoute)),
        bind[UserAnswersService].toInstance(mockUserAnswersService)
      )
      .build()

    val formProvider = new ReferenceAvailableFormProvider()
    val form = formProvider()

    lazy val referenceAvailableRoute =
      routes.ReferenceAvailableController.onPageLoad(testErn, testDraftId, 0, NormalMode).url

    lazy val onSubmitCall =
      routes.ReferenceAvailableController.onSubmit(testErn, testDraftId, 0, NormalMode)

    val view = application.injector.instanceOf[ReferenceAvailableView]
  }


  "ReferenceAvailable Controller" - {

    "GET onPageLoad" - {

      "must return OK and the correct view for a GET" in new Setup() {

        running(application) {

          val request = FakeRequest(GET, referenceAvailableRoute)

          val result = route(application, request).value

          status(result) mustEqual OK
          contentAsString(result) mustEqual view(
            form = form,
            onSubmitCall = onSubmitCall
          )(dataRequest(request), messages(application)).toString
        }
      }

      "must populate the view correctly on a GET when the question has previously been answered" in new Setup(Some(
        emptyUserAnswers.set(ReferenceAvailablePage(0), true)
      )) {

        running(application) {

          val request = FakeRequest(GET, referenceAvailableRoute)

          val result = route(application, request).value

          status(result) mustEqual OK
          contentAsString(result) mustEqual view(
            form = form.fill(true),
            onSubmitCall = onSubmitCall
          )(dataRequest(request), messages(application)).toString
        }
      }

      "must redirect to Journey Recovery for a GET if no existing data is found" in new Setup(None) {

        running(application) {

          val request = FakeRequest(GET, referenceAvailableRoute)

          val result = route(application, request).value

          status(result) mustEqual SEE_OTHER
          redirectLocation(result).value mustEqual controllers.routes.JourneyRecoveryController.onPageLoad().url
        }
      }
    }

    "POST onSubmit" - {

      "must redirect to the next page valid answer is submitted" in new Setup() {

        val expectedAnswers = emptyUserAnswers
          .set(ReferenceAvailablePage(0), true)

        MockUserAnswersService.set(expectedAnswers).returns(Future.successful(expectedAnswers))

        running(application) {

          val request = FakeRequest(POST, referenceAvailableRoute)
            .withFormUrlEncodedBody(("value", "true"))

          val result = route(application, request).value

          status(result) mustEqual SEE_OTHER
          redirectLocation(result).value mustEqual onwardRoute.url
        }
      }

      "must redirect to the next page and not update answers the same answer is submitted again" in new Setup(Some(
        emptyUserAnswers
          .set(ReferenceAvailablePage(0), false)
          .set(DocumentDescriptionPage(0), "description")
      )) {

        running(application) {

          val request = FakeRequest(POST, referenceAvailableRoute)
            .withFormUrlEncodedBody(("value", "false"))

          val result = route(application, request).value

          status(result) mustEqual SEE_OTHER
          redirectLocation(result).value mustEqual onwardRoute.url
        }
      }

      "must redirect to the next page and wipe UserAnswers when new answer is submitted" in new Setup(Some(
        emptyUserAnswers
          .set(ReferenceAvailablePage(0), true)
          .set(DocumentReferencePage(0), "reference")
          .set(DocumentDescriptionPage(0), "description")
      )) {

        val expectedAnswers = emptyUserAnswers
          .set(ReferenceAvailablePage(0), false)

        MockUserAnswersService.set(expectedAnswers).returns(Future.successful(expectedAnswers))

        running(application) {

          val request = FakeRequest(POST, referenceAvailableRoute)
            .withFormUrlEncodedBody(("value", "false"))

          val result = route(application, request).value

          status(result) mustEqual SEE_OTHER
          redirectLocation(result).value mustEqual onwardRoute.url
        }
      }

      "must return a Bad Request and errors when invalid data is submitted" in new Setup() {

        running(application) {

          val request = FakeRequest(POST, referenceAvailableRoute)
            .withFormUrlEncodedBody(("value", ""))

          val boundForm = form.bind(Map("value" -> ""))

          val result = route(application, request).value

          status(result) mustEqual BAD_REQUEST
          contentAsString(result) mustEqual view(
            form = boundForm,
            onSubmitCall = onSubmitCall
          )(dataRequest(request), messages(application)).toString
        }
      }

      "must redirect to Journey Recovery for a POST if no existing data is found" in new Setup(None) {

        running(application) {

          val request = FakeRequest(POST, referenceAvailableRoute)
            .withFormUrlEncodedBody(("value", "true"))

          val result = route(application, request).value

          status(result) mustEqual SEE_OTHER
          redirectLocation(result).value mustEqual controllers.routes.JourneyRecoveryController.onPageLoad().url
        }
      }
    }
  }
}
