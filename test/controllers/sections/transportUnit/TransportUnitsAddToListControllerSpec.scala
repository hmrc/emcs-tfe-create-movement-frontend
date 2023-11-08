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
import controllers.sections.transportUnit.{routes => transportUnitRoutes}
import forms.sections.transportUnit.TransportUnitsAddToListFormProvider
import mocks.services.MockUserAnswersService
import models.sections.transportUnit.TransportUnitType.Tractor
import models.sections.transportUnit.TransportUnitsAddToListModel
import models.sections.transportUnit.TransportUnitsAddToListModel.{MoreToCome, NoMoreToCome, Yes}
import models.{Index, NormalMode}
import navigation.FakeNavigators.FakeNavigator
import navigation.Navigator
import pages.sections.transportUnit.{TransportUnitTypePage, TransportUnitsAddToListPage}
import play.api.data.Form
import play.api.inject.bind
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.UserAnswersService
import viewmodels.helpers.TransportUnitsAddToListHelper
import views.html.sections.transportUnit.TransportUnitsAddToListView

import scala.concurrent.Future

class TransportUnitsAddToListControllerSpec extends SpecBase with MockUserAnswersService {

  def onwardRoute: Call = Call("GET", "/foo")

  lazy val transportUnitsAddToListRoute: String = transportUnitRoutes.TransportUnitsAddToListController.onPageLoad(testErn, testDraftId).url

  val formProvider = new TransportUnitsAddToListFormProvider()
  val form: Form[TransportUnitsAddToListModel] = formProvider()

  "TransportUnitsAddToList Controller" - {

    "must return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, transportUnitsAddToListRoute)

        val result = route(application, request).value

        val view = application.injector.instanceOf[TransportUnitsAddToListView]

        status(result) mustEqual OK
        contentAsString(result) mustEqual
          view(Some(form), Nil, NormalMode)(dataRequest(request), messages(application)).toString
      }
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = emptyUserAnswers.set(TransportUnitsAddToListPage, TransportUnitsAddToListModel.values.head)

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, transportUnitsAddToListRoute)

        val view = application.injector.instanceOf[TransportUnitsAddToListView]

        val result = route(application, request).value

        status(result) mustEqual OK
        contentAsString(result) mustEqual
          view(Some(form.fill(TransportUnitsAddToListModel.values.head)), Nil, NormalMode)(dataRequest(request), messages(application)).toString
      }
    }

    "must have no form populated if Transport units is 99 for GET" in {

      val fullUserAnswers = (0 until 99).foldLeft(emptyUserAnswers)((answers, int) => answers.set(TransportUnitTypePage(Index(int)), Tractor))

      val userAnswers = fullUserAnswers.set(TransportUnitsAddToListPage, TransportUnitsAddToListModel.values.head)

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      running(application) {
        lazy val fakeDataRequest = dataRequest(FakeRequest(), userAnswers)

        val fullCheckAnswers = application.injector.instanceOf[TransportUnitsAddToListHelper].allTransportUnitsSummary()(fakeDataRequest, messages(application))

        val request = FakeRequest(GET, transportUnitsAddToListRoute)

        val view = application.injector.instanceOf[TransportUnitsAddToListView]

        val result = route(application, request).value

        status(result) mustEqual OK
        contentAsString(result) mustEqual
          view(None, fullCheckAnswers, NormalMode)(dataRequest(request), messages(application)).toString
      }
    }

    "must redirect to task list page CAM-02 if Transport units is 99 for POST" in {
      val fullUserAnswers = (0 until 99).foldLeft(emptyUserAnswers)((answers, int) => answers.set(TransportUnitTypePage(Index(int)), Tractor))

      val userAnswers = fullUserAnswers.set(TransportUnitsAddToListPage, TransportUnitsAddToListModel.values.head)

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      running(application) {
        val request = FakeRequest(POST, transportUnitsAddToListRoute)
          .withFormUrlEncodedBody("value" -> TransportUnitsAddToListModel.Yes.toString)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual controllers.routes.DraftMovementController.onPageLoad(testErn, testDraftId).url
      }
    }

    "must redirect to transport unit type page with next index if yes selected and clear down the answer for the page" in {

      MockUserAnswersService
        .set(emptyUserAnswers
          .set(TransportUnitTypePage(testIndex1), Tractor))
        .returns(
          Future.successful(
            emptyUserAnswers
              .set(TransportUnitTypePage(testIndex1), Tractor)
          ))

      val application =
        applicationBuilder(userAnswers = Some(emptyUserAnswers
          .set(TransportUnitTypePage(testIndex1), Tractor)
          .set(TransportUnitsAddToListPage, MoreToCome)
        ))
          .overrides(
            bind[Navigator].toInstance(new FakeNavigator(onwardRoute)),
            bind[UserAnswersService].toInstance(mockUserAnswersService)
          )
          .build()

      running(application) {
        val request =
          FakeRequest(POST, transportUnitsAddToListRoute)
            .withFormUrlEncodedBody(("value", TransportUnitsAddToListModel.Yes.toString))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual
          controllers.sections.transportUnit.routes.TransportUnitTypeController.onPageLoad(testErn, testDraftId, testIndex2, NormalMode).url
      }
    }

    "must redirect to task list page if NoMoreToCome is selected" in {
      MockUserAnswersService
        .set()
        .returns(
          Future.successful(
            emptyUserAnswers
              .set(TransportUnitsAddToListPage, NoMoreToCome)
          ))

      val application =
        applicationBuilder(userAnswers = Some(emptyUserAnswers.set(TransportUnitTypePage(testIndex1), Tractor)))
          .overrides(
            bind[Navigator].toInstance(new FakeNavigator(onwardRoute)),
            bind[UserAnswersService].toInstance(mockUserAnswersService)
          )
          .build()

      running(application) {
        val request =
          FakeRequest(POST, transportUnitsAddToListRoute)
            .withFormUrlEncodedBody(("value", TransportUnitsAddToListModel.NoMoreToCome.toString))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual
          controllers.routes.DraftMovementController.onPageLoad(testErn, testDraftId).url
      }
    }

    "must redirect to task list page if MoreToCome is selected" in {
      MockUserAnswersService
        .set()
        .returns(
          Future.successful(
            emptyUserAnswers
              .set(TransportUnitsAddToListPage, MoreToCome)
          ))

      val application =
        applicationBuilder(userAnswers = Some(emptyUserAnswers.set(TransportUnitTypePage(testIndex1), Tractor)))
          .overrides(
            bind[Navigator].toInstance(new FakeNavigator(onwardRoute)),
            bind[UserAnswersService].toInstance(mockUserAnswersService)
          )
          .build()

      running(application) {
        val request =
          FakeRequest(POST, transportUnitsAddToListRoute)
            .withFormUrlEncodedBody(("value", TransportUnitsAddToListModel.MoreToCome.toString))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual
          controllers.routes.DraftMovementController.onPageLoad(testErn, testDraftId).url
      }
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      running(application) {
        val request =
          FakeRequest(POST, transportUnitsAddToListRoute)
            .withFormUrlEncodedBody(("value", "invalid value"))

        val boundForm = form.bind(Map("value" -> "invalid value"))

        val view = application.injector.instanceOf[TransportUnitsAddToListView]

        val result = route(application, request).value

        status(result) mustEqual BAD_REQUEST
        contentAsString(result) mustEqual view(Some(boundForm), Nil, NormalMode)(dataRequest(request), messages(application)).toString
      }
    }

    "must redirect to Journey Recovery for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      running(application) {
        val request = FakeRequest(GET, transportUnitsAddToListRoute)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual controllers.routes.JourneyRecoveryController.onPageLoad().url
      }
    }

    "redirect to Journey Recovery for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      running(application) {
        val request =
          FakeRequest(POST, transportUnitsAddToListRoute)
            .withFormUrlEncodedBody(("value", TransportUnitsAddToListModel.values.head.toString))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual controllers.routes.JourneyRecoveryController.onPageLoad().url
      }
    }
  }
}
