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
import forms.sections.documents.DocumentReferenceFormProvider
import mocks.services.MockUserAnswersService
import models.{NormalMode, UserAnswers}
import navigation.DocumentsNavigator
import navigation.FakeNavigators.FakeDocumentsNavigator
import pages.sections.documents.DocumentReferencePage
import play.api.inject.bind
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.UserAnswersService
import views.html.sections.documents.DocumentReferenceView

import scala.concurrent.Future

class DocumentReferenceControllerSpec extends SpecBase with MockUserAnswersService {

  class Setup(userAnswers: Option[UserAnswers] = Some(emptyUserAnswers)) {

    def onwardRoute = Call("GET", "/foo")

    val application = applicationBuilder(userAnswers = userAnswers)
      .overrides(
        bind[DocumentsNavigator].toInstance(new FakeDocumentsNavigator(onwardRoute)),
        bind[UserAnswersService].toInstance(mockUserAnswersService)
      )
      .build()

    val formProvider = new DocumentReferenceFormProvider()
    val form = formProvider()

    lazy val controllerRoute = routes.DocumentReferenceController.onPageLoad(testErn, testDraftId, 0, NormalMode).url
    lazy val onSubmitCall = routes.DocumentReferenceController.onSubmit(testErn, testDraftId, 0, NormalMode)

    val view = application.injector.instanceOf[DocumentReferenceView]
  }

  "DocumentReference Controller" - {

    "for GET onPageLoad" - {

      "must return OK and the correct view for a GET" in new Setup() {

        running(application) {

          val request = FakeRequest(GET, controllerRoute)

          val result = route(application, request).value

          status(result) mustEqual OK
          contentAsString(result) mustEqual view(
            form = form,
            onSubmitCall = onSubmitCall
          )(dataRequest(request), messages(application)).toString
        }
      }

      "must populate the view correctly on a GET when the question has previously been answered" in new Setup(Some(emptyUserAnswers
        .set(DocumentReferencePage(0), "answer")
      )){

        running(application) {

          val request = FakeRequest(GET, controllerRoute)

          val result = route(application, request).value

          status(result) mustEqual OK
          contentAsString(result) mustEqual view(
            form = form.fill("answer"),
            onSubmitCall = onSubmitCall
          )(dataRequest(request), messages(application)).toString
        }
      }

      "must redirect to Journey Recovery for a GET if no existing data is found" in new Setup(None){

        running(application) {

          val request = FakeRequest(GET, controllerRoute)

          val result = route(application, request).value

          status(result) mustEqual SEE_OTHER
          redirectLocation(result).value mustEqual controllers.routes.JourneyRecoveryController.onPageLoad().url
        }
      }
    }

    "must redirect to the next page when valid data is submitted" in new Setup() {

      MockUserAnswersService.set().returns(Future.successful(emptyUserAnswers))

      running(application) {
        val request =
          FakeRequest(POST, controllerRoute)
            .withFormUrlEncodedBody(("value", "answer"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual onwardRoute.url
      }
    }

    "must return a Bad Request and errors when invalid data is submitted" in new Setup() {

      running(application) {
        val request =
          FakeRequest(POST, controllerRoute)
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
        val request =
          FakeRequest(POST, controllerRoute)
            .withFormUrlEncodedBody(("value", "answer"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual controllers.routes.JourneyRecoveryController.onPageLoad().url
      }
    }
  }
}
