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

package controllers.sections.sad

import base.SpecBase
import forms.sections.sad.SadRemoveDocumentFormProvider
import mocks.services.MockUserAnswersService
import pages.sections.sad.ImportNumberPage
import play.api.inject.bind
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.UserAnswersService
import views.html.sections.sad.SadRemoveDocumentView

import scala.concurrent.Future

class SadRemoveDocumentControllerSpec extends SpecBase with MockUserAnswersService {

  val formProvider = new SadRemoveDocumentFormProvider()
  val form = formProvider()

  lazy val sadRemoveDocumentRoute = controllers.sections.sad.routes.SadRemoveDocumentController.onPageLoad(testErn, testDraftId, testIndex1).url

  "SadRemoveDocument Controller" - {

    "must return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers =
        Some(emptyUserAnswers.set(ImportNumberPage(testIndex1), "answer"))
      ).build()

      running(application) {
        val request = FakeRequest(GET, sadRemoveDocumentRoute)

        val result = route(application, request).value

        val view = application.injector.instanceOf[SadRemoveDocumentView]

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form, testIndex1)(dataRequest(request), messages(application)).toString
      }
    }

    "must redirect to the index controller when index is out of bounds (for GET)" in {

      val application =
        applicationBuilder(userAnswers = Some(
          emptyUserAnswers
            .set(ImportNumberPage(testIndex1), "answer")
        ))
          .overrides(
            bind[UserAnswersService].toInstance(mockUserAnswersService)
          )
          .build()

      running(application) {
        val request =
          FakeRequest(POST, routes.SadRemoveDocumentController.onPageLoad(testErn, testDraftId, testIndex2).url)
            .withFormUrlEncodedBody(("value", "true"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual controllers.sections.sad.routes.SadIndexController.onPageLoad(testErn, testDraftId).url
      }
    }

    "must redirect to add to list when the user answers no" in {

      val application =
        applicationBuilder(userAnswers = Some(
          emptyUserAnswers.set(ImportNumberPage(testIndex1), "answer1")
        ))
          .overrides(
            bind[UserAnswersService].toInstance(mockUserAnswersService)
          )
          .build()

      running(application) {
        val request =
          FakeRequest(POST, sadRemoveDocumentRoute)
            .withFormUrlEncodedBody(("value", "false"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual
          controllers.sections.sad.routes.SadAddToListController.onPageLoad(testErn, testDraftId).url
      }
    }

    "must redirect to the index controller when the user answers yes (removing the Sad Document)" in {

      MockUserAnswersService.set(
        emptyUserAnswers.set(ImportNumberPage(testIndex2), "answer2")
      ).returns(Future.successful(
        emptyUserAnswers.set(ImportNumberPage(testIndex1), "answer1")
      ))

      val application =
        applicationBuilder(userAnswers = Some(
          emptyUserAnswers
            .set(ImportNumberPage(testIndex1), "answer1")
            .set(ImportNumberPage(testIndex2), "answer2")
        ))
          .overrides(
            bind[UserAnswersService].toInstance(mockUserAnswersService)
          )
          .build()

      running(application) {
        val request =
          FakeRequest(POST, sadRemoveDocumentRoute)
            .withFormUrlEncodedBody(("value", "true"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual controllers.sections.sad.routes.SadIndexController.onPageLoad(testErn, testDraftId).url
      }
    }

    "must redirect to the index controller when index is out of bounds (for POST)" in {

      val application =
        applicationBuilder(userAnswers = Some(
          emptyUserAnswers
            .set(ImportNumberPage(testIndex1), "answer")
        ))
          .overrides(
            bind[UserAnswersService].toInstance(mockUserAnswersService)
          )
          .build()

      running(application) {
        val request =
          FakeRequest(POST, routes.SadRemoveDocumentController.onPageLoad(testErn, testDraftId, testIndex2).url)
            .withFormUrlEncodedBody(("value", "true"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual controllers.sections.sad.routes.SadIndexController.onPageLoad(testErn, testDraftId).url
      }
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      val application = applicationBuilder(userAnswers = Some(
        emptyUserAnswers.set(ImportNumberPage(testIndex1), "answer")
      )).build()

      running(application) {
        val request =
          FakeRequest(POST, sadRemoveDocumentRoute)
            .withFormUrlEncodedBody(("value", ""))

        val boundForm = form.bind(Map("value" -> ""))

        val view = application.injector.instanceOf[SadRemoveDocumentView]

        val result = route(application, request).value

        status(result) mustEqual BAD_REQUEST
        contentAsString(result) mustEqual view(boundForm, testIndex1)(dataRequest(request), messages(application)).toString
      }
    }

    "must redirect to Journey Recovery for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      running(application) {
        val request = FakeRequest(GET, sadRemoveDocumentRoute)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual controllers.routes.JourneyRecoveryController.onPageLoad().url
      }
    }

    "must redirect to Journey Recovery for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      running(application) {
        val request =
          FakeRequest(POST, sadRemoveDocumentRoute)
            .withFormUrlEncodedBody(("value", "true"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual controllers.routes.JourneyRecoveryController.onPageLoad().url
      }
    }
  }
}
