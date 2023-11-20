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
import controllers.actions.{DataRequiredAction, FakeAuthAction, FakeDataRetrievalAction, FakeUserAllowListAction}
import forms.sections.transportUnit.TransportUnitsAddToListFormProvider
import mocks.services.MockUserAnswersService
import models.sections.transportUnit.TransportUnitType.Tractor
import models.sections.transportUnit.TransportUnitsAddToListModel
import models.sections.transportUnit.TransportUnitsAddToListModel.{MoreToCome, NoMoreToCome}
import models.{Index, NormalMode, UserAnswers}
import navigation.FakeNavigators.FakeTransportUnitNavigator
import pages.sections.transportUnit.{TransportUnitTypePage, TransportUnitsAddToListPage}
import play.api.Play.materializer
import play.api.data.Form
import play.api.i18n.{Messages, MessagesApi}
import play.api.mvc.{AnyContentAsEmpty, Call}
import play.api.test.Helpers._
import play.api.test.{FakeRequest, Helpers}
import viewmodels.helpers.TransportUnitsAddToListHelper
import views.html.sections.transportUnit.TransportUnitsAddToListView

import scala.concurrent.Future

class TransportUnitsAddToListControllerSpec extends SpecBase with MockUserAnswersService {

  class Test(userAnswers: Option[UserAnswers]) {
    def onwardRoute: Call = Call("GET", "/foo")

    def submitRoute: Call = routes.TransportUnitsAddToListController.onSubmit(testErn, testDraftId)

    implicit lazy val messagesApi: MessagesApi = app.injector.instanceOf[MessagesApi]

    lazy val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest()

    lazy val messages: Messages = messagesApi.preferred(request)

    lazy val formProvider = new TransportUnitsAddToListFormProvider()

    lazy val form: Form[TransportUnitsAddToListModel] = formProvider()

    lazy val view: TransportUnitsAddToListView = app.injector.instanceOf[TransportUnitsAddToListView]

    val helper = app.injector.instanceOf[TransportUnitsAddToListHelper]

    lazy val controller = new TransportUnitsAddToListController(
      messagesApi,
      mockUserAnswersService,
      new FakeUserAllowListAction,
      new FakeTransportUnitNavigator(onwardRoute),
      new FakeAuthAction(Helpers.stubPlayBodyParsers),
      new FakeDataRetrievalAction(userAnswers, Some(testMinTraderKnownFacts)),
      app.injector.instanceOf[DataRequiredAction],
      formProvider,
      Helpers.stubMessagesControllerComponents(),
      view,
      helper
    )
  }

  "TransportUnitsAddToList Controller" - {

    "must return OK and the correct view for a GET" in new Test(Some(emptyUserAnswers)) {

      val result = controller.onPageLoad(testErn, testDraftId)(request)

      status(result) mustEqual OK
      contentAsString(result) mustEqual
        view(Some(form), Nil, NormalMode)(dataRequest(request), messages).toString
    }

    "must populate the view correctly on a GET when the question has previously been answered" in new Test(Some(
      emptyUserAnswers.set(TransportUnitsAddToListPage, TransportUnitsAddToListModel.values.head)
    )) {
      val result = controller.onPageLoad(testErn, testDraftId)(request)

      status(result) mustEqual OK
      contentAsString(result) mustEqual
        view(Some(form.fill(TransportUnitsAddToListModel.values.head)), Nil, NormalMode)(dataRequest(request), messages).toString
    }

    "must have no form populated if Transport units is 99 for GET" in new Test(Some(
      (0 until 99)
        .foldLeft(emptyUserAnswers)((answers, int) => answers.set(TransportUnitTypePage(Index(int)), Tractor))
        .set(TransportUnitsAddToListPage, TransportUnitsAddToListModel.values.head)
    )) {
      val fullCheckAnswers = helper.allTransportUnitsSummary()(dataRequest(request), messages)

      val result = controller.onPageLoad(testErn, testDraftId)(request)

      status(result) mustEqual OK
      contentAsString(result) mustEqual
        view(None, fullCheckAnswers, NormalMode)(dataRequest(request), messages).toString
    }

    "must redirect to task list page CAM-02 if Transport units is 99 for POST" in new Test(Some(
      (0 until 99)
        .foldLeft(emptyUserAnswers)((answers, int) => answers.set(TransportUnitTypePage(Index(int)), Tractor))
        .set(TransportUnitsAddToListPage, TransportUnitsAddToListModel.values.head)
    )) {

      val result = controller.onPageLoad(testErn, testDraftId)(request.withFormUrlEncodedBody("value" -> TransportUnitsAddToListModel.Yes.toString))

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.DraftMovementController.onPageLoad(testErn, testDraftId).url
    }

    "must redirect to transport unit type page with next index if yes selected and clear down the answer for the page" in new Test(Some(
      emptyUserAnswers
        .set(TransportUnitTypePage(testIndex1), Tractor)
        .set(TransportUnitsAddToListPage, MoreToCome)
    )) {

      MockUserAnswersService
        .set(emptyUserAnswers
          .set(TransportUnitTypePage(testIndex1), Tractor))
        .returns(
          Future.successful(
            emptyUserAnswers
              .set(TransportUnitTypePage(testIndex1), Tractor)
          ))

      val result = controller.onSubmit(testErn, testDraftId)(request.withFormUrlEncodedBody(("value", TransportUnitsAddToListModel.Yes.toString)))

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual
        controllers.sections.transportUnit.routes.TransportUnitTypeController.onPageLoad(testErn, testDraftId, testIndex2, NormalMode).url
    }

    "must redirect to task list page if NoMoreToCome is selected" in new Test(Some(
      emptyUserAnswers.set(TransportUnitTypePage(testIndex1), Tractor)
    )) {
      MockUserAnswersService
        .set()
        .returns(
          Future.successful(
            emptyUserAnswers
              .set(TransportUnitsAddToListPage, NoMoreToCome)
          ))

      val result = controller.onSubmit(testErn, testDraftId)(request.withFormUrlEncodedBody(("value", TransportUnitsAddToListModel.NoMoreToCome.toString)))

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual
        controllers.routes.DraftMovementController.onPageLoad(testErn, testDraftId).url
    }

    "must redirect to task list page if MoreToCome is selected" in new Test(Some(
      emptyUserAnswers.set(TransportUnitTypePage(testIndex1), Tractor)
    )) {
      MockUserAnswersService
        .set()
        .returns(
          Future.successful(
            emptyUserAnswers
              .set(TransportUnitsAddToListPage, MoreToCome)
          ))

      val result = controller.onSubmit(testErn, testDraftId)(request.withFormUrlEncodedBody(("value", TransportUnitsAddToListModel.MoreToCome.toString)))

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual
        controllers.routes.DraftMovementController.onPageLoad(testErn, testDraftId).url
    }

    "must return a Bad Request and errors when invalid data is submitted" in new Test(Some(emptyUserAnswers)) {

      val boundForm = form.bind(Map("value" -> "invalid value"))

      val result = controller.onSubmit(testErn, testDraftId)(request.withFormUrlEncodedBody(("value", "invalid value")))

      status(result) mustEqual BAD_REQUEST
      contentAsString(result) mustEqual view(Some(boundForm), Nil, NormalMode)(dataRequest(request), messages).toString
    }

    "must redirect to Journey Recovery for a GET if no existing data is found" in new Test(None) {

      val result = controller.onPageLoad(testErn, testDraftId)(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.JourneyRecoveryController.onPageLoad().url
    }

    "redirect to Journey Recovery for a POST if no existing data is found" in new Test(None) {

      val result = controller.onSubmit(testErn, testDraftId)(request.withFormUrlEncodedBody(("value", TransportUnitsAddToListModel.values.head.toString)))

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.JourneyRecoveryController.onPageLoad().url
    }
  }
}
