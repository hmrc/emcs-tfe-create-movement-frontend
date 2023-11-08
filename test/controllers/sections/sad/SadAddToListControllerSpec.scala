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
import forms.sections.sad.SadAddToListFormProvider
import mocks.services.MockUserAnswersService
import models.{Index, NormalMode}
import models.sections.sad.SadAddToListModel
import navigation.FakeNavigators.FakeNavigator
import navigation.Navigator
import pages.sections.sad.{ImportNumberPage, SadAddToListPage}
import play.api.inject.bind
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.UserAnswersService
import views.html.sections.sad.SadAddToListView

import scala.concurrent.Future

class SadAddToListControllerSpec extends SpecBase with MockUserAnswersService {

  def onwardRoute = Call("GET", "/foo")

  val formProvider = new SadAddToListFormProvider()
  val form = formProvider()

  lazy val sadAddToListRoute = routes.SadAddToListController.onPageLoad(testErn, testDraftId).url

  "SadAddToList Controller" - {

    "must return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, sadAddToListRoute)

        val result = route(application, request).value

        val view = application.injector.instanceOf[SadAddToListView]

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(Some(form), Nil, NormalMode)(dataRequest(request), messages(application)).toString
      }
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = emptyUserAnswers.set(SadAddToListPage, SadAddToListModel.values.head)

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, sadAddToListRoute)

        val view = application.injector.instanceOf[SadAddToListView]

        val result = route(application, request).value

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(Some(form.fill(SadAddToListModel.values.head)), Nil, NormalMode)(dataRequest(request), messages(application)).toString
      }
    }

    "must redirect to the next page when valid data is submitted" in {

      MockUserAnswersService.set().returns(Future.successful(emptyUserAnswers))

      val application =
        applicationBuilder(userAnswers = Some(emptyUserAnswers))
          .overrides(
            bind[Navigator].toInstance(new FakeNavigator(onwardRoute)),
            bind[UserAnswersService].toInstance(mockUserAnswersService)
          )
          .build()

      running(application) {
        val request =
          FakeRequest(POST, sadAddToListRoute)
            .withFormUrlEncodedBody(("value", SadAddToListModel.NoMoreToCome.toString))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual
          controllers.routes.DraftMovementController.onPageLoad(testErn, testDraftId).url
      }
    }

    "must redirect to task list page CAM-02 if Transport units is 99 for POST" in {
      val fullUserAnswers = (0 until 99).foldLeft(emptyUserAnswers)((answers, int) => answers.set(ImportNumberPage(Index(int)), ""))

      val userAnswers = fullUserAnswers.set(SadAddToListPage, SadAddToListModel.Yes)

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      running(application) {
        val request = FakeRequest(POST, sadAddToListRoute)
          .withFormUrlEncodedBody("value" -> SadAddToListModel.Yes.toString)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual controllers.routes.DraftMovementController.onPageLoad(testErn, testDraftId).url
      }
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      running(application) {
        val request =
          FakeRequest(POST, sadAddToListRoute)
            .withFormUrlEncodedBody(("value", "invalid value"))

        val boundForm = form.bind(Map("value" -> "invalid value"))

        val view = application.injector.instanceOf[SadAddToListView]

        val result = route(application, request).value

        status(result) mustEqual BAD_REQUEST
        contentAsString(result) mustEqual view(Some(boundForm), Nil,  NormalMode)(dataRequest(request), messages(application)).toString
      }
    }

    "must redirect to Journey Recovery for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      running(application) {
        val request = FakeRequest(GET, sadAddToListRoute)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual controllers.routes.JourneyRecoveryController.onPageLoad().url
      }
    }

    "must redirect to Journey Recovery for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      running(application) {
        val request =
          FakeRequest(POST, sadAddToListRoute)
            .withFormUrlEncodedBody(("value", "answer"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual controllers.routes.JourneyRecoveryController.onPageLoad().url
      }
    }
  }
}
