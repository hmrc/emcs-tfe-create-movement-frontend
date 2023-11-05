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
import forms.sections.documents.DocumentsAddToListFormProvider
import mocks.services.MockUserAnswersService
import models.sections.documents.DocumentsAddToList
import models.{NormalMode, UserAnswers}
import navigation.FakeNavigators.FakeNavigator
import navigation.Navigator
import play.api.inject.bind
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.UserAnswersService
import views.html.sections.documents.DocumentsAddToListView

import scala.concurrent.Future

class DocumentsAddToListControllerSpec extends SpecBase with MockUserAnswersService {

  class Setup(userAnswers: Option[UserAnswers] = Some(emptyUserAnswers)) {

    def onwardRoute = Call("GET", "/foo")

    val application = applicationBuilder(userAnswers = userAnswers)
      .overrides(
        bind[Navigator].toInstance(new FakeNavigator(onwardRoute)),
        bind[UserAnswersService].toInstance(mockUserAnswersService)
      )
      .build()

    lazy val controllerRoute = routes.DocumentsAddToListController.onPageLoad(testErn, testDraftId, NormalMode).url
    lazy val onSubmitCall = routes.DocumentsAddToListController.onSubmit(testErn, testDraftId, NormalMode)

    val formProvider = new DocumentsAddToListFormProvider()
    val form = formProvider()

    val view = application.injector.instanceOf[DocumentsAddToListView]
  }

  "DocumentsAddToList Controller" - {

    "GET onPageLoad" - {

      "must return OK and the correct view for a GET" in new Setup() {

        running(application) {

          val request = FakeRequest(GET, controllerRoute)

          val result = route(application, request).value

          status(result) mustEqual OK
          contentAsString(result) mustEqual view(
            form = form,
            onSubmitCall = onSubmitCall,
            documents = Seq.empty
          )(dataRequest(request), messages(application)).toString
        }
      }

      "must redirect to Journey Recovery for a GET if no existing data is found" in new Setup(None) {

        running(application) {

          val request = FakeRequest(GET, controllerRoute)

          val result = route(application, request).value

          status(result) mustEqual SEE_OTHER
          redirectLocation(result).value mustEqual controllers.routes.JourneyRecoveryController.onPageLoad().url
        }
      }
    }

    "POST onSubmit" - {

      "must redirect to the next page when valid data is submitted" in new Setup() {

        MockUserAnswersService.set().returns(Future.successful(emptyUserAnswers))

        running(application) {

          val request = FakeRequest(POST, controllerRoute)
            .withFormUrlEncodedBody(("value", DocumentsAddToList.values.head.toString))

          val result = route(application, request).value

          status(result) mustEqual SEE_OTHER
          redirectLocation(result).value mustEqual onwardRoute.url
        }
      }

      "must return a Bad Request and errors when invalid data is submitted" in new Setup() {

        running(application) {

          val request = FakeRequest(POST, controllerRoute)
            .withFormUrlEncodedBody(("value", "invalid value"))

          val boundForm = form.bind(Map("value" -> "invalid value"))

          val result = route(application, request).value

          status(result) mustEqual BAD_REQUEST
          contentAsString(result) mustEqual view(
            form = boundForm,
            onSubmitCall = onSubmitCall,
            documents = Seq.empty
          )(dataRequest(request), messages(application)).toString
        }
      }

      "redirect to Journey Recovery for a POST if no existing data is found" in new Setup(None) {

        running(application) {

          val request = FakeRequest(POST, controllerRoute)
            .withFormUrlEncodedBody(("value", DocumentsAddToList.values.head.toString))

          val result = route(application, request).value

          status(result) mustEqual SEE_OTHER

          redirectLocation(result).value mustEqual controllers.routes.JourneyRecoveryController.onPageLoad().url
        }
      }
    }
  }
}