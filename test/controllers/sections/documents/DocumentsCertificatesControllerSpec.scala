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
import forms.sections.documents.DocumentsCertificatesFormProvider
import mocks.services.MockUserAnswersService
import models.sections.documents.DocumentsAddToList
import models.{NormalMode, UserAnswers}
import navigation.DocumentsNavigator
import navigation.FakeNavigators.FakeDocumentsNavigator
import pages.sections.documents.{DocumentDescriptionPage, DocumentReferencePage, DocumentsAddToListPage, DocumentsCertificatesPage, ReferenceAvailablePage}
import play.api.inject.bind
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.UserAnswersService
import views.html.sections.documents.DocumentsCertificatesView

import scala.concurrent.Future

class DocumentsCertificatesControllerSpec extends SpecBase with MockUserAnswersService {

  class Setup(val startingUserAnswers: Option[UserAnswers] = Some(emptyUserAnswers)) {

    def onwardRoute = Call("GET", "/foo")

    val application = applicationBuilder(userAnswers = startingUserAnswers)
      .overrides(
        bind[DocumentsNavigator].toInstance(new FakeDocumentsNavigator(onwardRoute)),
        bind[UserAnswersService].toInstance(mockUserAnswersService)
      )
      .build()

    val formProvider = new DocumentsCertificatesFormProvider()
    val form = formProvider()

    lazy val documentsCertificatesRoute =
      routes.DocumentsCertificatesController.onPageLoad(testErn, testDraftId, NormalMode).url

    lazy val onSubmitCall =
      routes.DocumentsCertificatesController.onSubmit(testErn, testDraftId, NormalMode)

    val view = application.injector.instanceOf[DocumentsCertificatesView]
  }

  "DocumentsCertificates Controller" - {

    "GET onPageLoad" - {

      "must return OK and the correct view for a GET" in new Setup() {

        running(application) {

          val request = FakeRequest(GET, documentsCertificatesRoute)

          val result = route(application, request).value

          status(result) mustEqual OK
          contentAsString(result) mustEqual view(
            form = form,
            onSubmitCall = onSubmitCall
          )(dataRequest(request), messages(request)).toString
        }
      }

      "must populate the view correctly on a GET when the question has previously been answered" in new Setup(Some(emptyUserAnswers
        .set(DocumentsCertificatesPage, true)
      )) {

        running(application) {

          val request = FakeRequest(GET, documentsCertificatesRoute)

          val result = route(application, request).value

          status(result) mustEqual OK
          contentAsString(result) mustEqual view(
            form = form.fill(true),
            onSubmitCall = onSubmitCall
          )(dataRequest(request), messages(request)).toString
        }
      }

      "must redirect to Journey Recovery for a GET if no existing data is found" in new Setup(None) {

        running(application) {

          val request = FakeRequest(GET, documentsCertificatesRoute)

          val result = route(application, request).value

          status(result) mustEqual SEE_OTHER
          redirectLocation(result).value mustEqual controllers.routes.JourneyRecoveryController.onPageLoad().url
        }
      }
    }

    "POST onSubmit" - {

      "must redirect to the next page when valid answer is submitted" in new Setup() {

        val updatedAnswers = startingUserAnswers.value
          .set(DocumentsCertificatesPage, true)

        MockUserAnswersService.set(updatedAnswers).returns(Future.successful(updatedAnswers))

        running(application) {

          val request = FakeRequest(POST, documentsCertificatesRoute)
            .withFormUrlEncodedBody(("value", "true"))

          val result = route(application, request).value

          status(result) mustEqual SEE_OTHER
          redirectLocation(result).value mustEqual onwardRoute.url
        }
      }

      "must redirect to the next page and not update answers when the same answer submitted again" in new Setup(Some(
        emptyUserAnswers
          .set(DocumentsCertificatesPage, true)
          .set(ReferenceAvailablePage(0), true)
          .set(DocumentReferencePage(0), "reference")
          .set(ReferenceAvailablePage(1), false)
          .set(DocumentDescriptionPage(1), "description")
          .set(DocumentsAddToListPage, DocumentsAddToList.No)
      )) {

        running(application) {

          val request = FakeRequest(POST, documentsCertificatesRoute)
            .withFormUrlEncodedBody(("value", "true"))

          val result = route(application, request).value

          status(result) mustEqual SEE_OTHER
          redirectLocation(result).value mustEqual onwardRoute.url
        }
      }

      "must redirect to the next page and wipe answers when a new answer is submitted" in new Setup(Some(
        emptyUserAnswers
          .set(DocumentsCertificatesPage, true)
          .set(ReferenceAvailablePage(0), true)
          .set(DocumentReferencePage(0), "reference")
          .set(ReferenceAvailablePage(1), false)
          .set(DocumentDescriptionPage(1), "description")
          .set(DocumentsAddToListPage, DocumentsAddToList.No)
      )) {

        val updatedAnswers = emptyUserAnswers
          .set(DocumentsCertificatesPage, false)

        MockUserAnswersService.set(updatedAnswers).returns(Future.successful(updatedAnswers))

        running(application) {

          val request = FakeRequest(POST, documentsCertificatesRoute)
            .withFormUrlEncodedBody(("value", "false"))

          val result = route(application, request).value

          status(result) mustEqual SEE_OTHER
          redirectLocation(result).value mustEqual onwardRoute.url
        }
      }

      "must return a Bad Request and errors when invalid data is submitted" in new Setup() {

        running(application) {

          val request = FakeRequest(POST, documentsCertificatesRoute)
            .withFormUrlEncodedBody(("value", ""))

          val boundForm = form.bind(Map("value" -> ""))

          val view = application.injector.instanceOf[DocumentsCertificatesView]

          val result = route(application, request).value

          status(result) mustEqual BAD_REQUEST
          contentAsString(result) mustEqual view(
            form = boundForm,
            onSubmitCall = onSubmitCall
          )(dataRequest(request), messages(request)).toString
        }
      }

      "must redirect to Journey Recovery for a POST if no existing data is found" in new Setup(None) {

        running(application) {

          val request = FakeRequest(POST, documentsCertificatesRoute)
            .withFormUrlEncodedBody(("value", "true"))

          val result = route(application, request).value

          status(result) mustEqual SEE_OTHER
          redirectLocation(result).value mustEqual controllers.routes.JourneyRecoveryController.onPageLoad().url
        }
      }
    }
  }
}
