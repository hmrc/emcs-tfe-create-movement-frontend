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

package controllers.sections.dispatch

import base.SpecBase
import controllers.actions.FakeDataRetrievalAction
import forms.sections.dispatch.DispatchWarehouseExciseFormProvider
import mocks.services.MockUserAnswersService
import models.requests.DataRequest
import models.sections.info.DispatchPlace
import models.{NormalMode, UserAnswers}
import navigation.FakeNavigators.FakeDispatchNavigator
import pages.sections.dispatch.{DispatchAddressPage, DispatchWarehouseExcisePage}
import pages.sections.info.DispatchPlacePage
import play.api.data.Form
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.sections.dispatch.DispatchWarehouseExciseView

import scala.concurrent.Future

class DispatchWarehouseExciseControllerSpec extends SpecBase with MockUserAnswersService {

  lazy val formProvider: DispatchWarehouseExciseFormProvider = new DispatchWarehouseExciseFormProvider()

  lazy val view: DispatchWarehouseExciseView = app.injector.instanceOf[DispatchWarehouseExciseView]

  lazy val dispatchWarehouseExciseRoute: String =
    controllers.sections.dispatch.routes.DispatchWarehouseExciseController.onPageLoad(testErn, testDraftId, NormalMode).url

  class Fixture(val optUserAnswers: Option[UserAnswers] = Some(emptyUserAnswers)) {
    val request = FakeRequest(GET, dispatchWarehouseExciseRoute)
    implicit val dr: DataRequest[AnyContentAsEmpty.type] = dataRequest(request, optUserAnswers.getOrElse(emptyUserAnswers))
    lazy val form: Form[String] = formProvider()

    lazy val testController = new DispatchWarehouseExciseController(
      messagesApi,
      mockUserAnswersService,
      new FakeDispatchNavigator(testOnwardRoute),
      fakeAuthAction,
      new FakeDataRetrievalAction(optUserAnswers, Some(testMinTraderKnownFacts)),
      dataRequiredAction,
      fakeBetaAllowListAction,
      formProvider,
      messagesControllerComponents,
      view
    )
  }

  "DispatchWarehouseExcise Controller" - {
    "must return OK and the correct view for a GET" in new Fixture() {
      val result = testController.onPageLoad(testErn, testDraftId, NormalMode)(request)

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(form, NormalMode)(dataRequest(request), messages(request)).toString
    }

    "must populate the view correctly on a GET when the question has previously been answered" in new Fixture(
      Some(emptyUserAnswers.set(DispatchWarehouseExcisePage, "answer"))) {
      val result = testController.onPageLoad(testErn, testDraftId, NormalMode)(request)

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(form.fill("answer"), NormalMode)(dataRequest(request), messages(request)).toString
    }

    "must redirect to the next page when valid data is submitted" in new Fixture() {
      MockUserAnswersService.set().returns(Future.successful(emptyUserAnswers))

      val req = FakeRequest(POST, dispatchWarehouseExciseRoute).withFormUrlEncodedBody(("value", "GB00123456789"))

      val result = testController.onSubmit(testErn, testDraftId, NormalMode)(req)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual testOnwardRoute.url
    }

    "must redirect to the next page when valid data is submitted (page already answered, and should remove Dispatch Address as GB -> XI" in new Fixture(
      Some(emptyUserAnswers
        .set(DispatchPlacePage, DispatchPlace.NorthernIreland)
        .set(DispatchWarehouseExcisePage, testGreatBritainErn)
        .set(DispatchAddressPage, testUserAddress)
      )
    ) {

      val savedAnswers = optUserAnswers.get
        .remove(DispatchAddressPage)
        .set(DispatchWarehouseExcisePage, "XI00123456789")

      MockUserAnswersService.set(savedAnswers).returns(Future.successful(savedAnswers))

      val req = FakeRequest(POST, dispatchWarehouseExciseRoute).withFormUrlEncodedBody(("value", "XI00123456789"))

      val result = testController.onSubmit(testNorthernIrelandErn, testDraftId, NormalMode)(req)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual testOnwardRoute.url
    }

    "must return a Bad Request and errors when invalid data is submitted" in new Fixture() {
      val req = FakeRequest(POST, dispatchWarehouseExciseRoute).withFormUrlEncodedBody(("value", ""))

      val boundForm = form.bind(Map("value" -> ""))

      val result = testController.onSubmit(testErn, testDraftId, NormalMode)(req)

      status(result) mustEqual BAD_REQUEST
      contentAsString(result) mustEqual view(boundForm, NormalMode)(dataRequest(request), messages(request)).toString
    }

    "must return a Bad Request and errors when more than 13 chars are submitted" in new Fixture() {
      val req = FakeRequest(POST, dispatchWarehouseExciseRoute).withFormUrlEncodedBody(("value", "GB123456789011"))

      val boundForm = form.bind(Map("value" -> "GB123456789011"))

      val result = testController.onSubmit(testErn, testDraftId, NormalMode)(req)


      status(result) mustEqual BAD_REQUEST
      contentAsString(result) mustEqual view(boundForm, NormalMode)(dataRequest(request), messages(request)).toString
    }

    "must return a Bad Request and errors when an invalid prefix is entered" in new Fixture() {
      val req = FakeRequest(POST, dispatchWarehouseExciseRoute).withFormUrlEncodedBody(("value", "FR00123456789"))

      val boundForm = form.bind(Map("value" -> "FR00123456789"))

      val result = testController.onSubmit(testErn, testDraftId, NormalMode)(req)


      status(result) mustEqual BAD_REQUEST
      contentAsString(result) mustEqual view(boundForm, NormalMode)(dataRequest(request), messages(request)).toString
    }

    "must redirect to Journey Recovery for a GET if no existing data is found" in new Fixture(None) {
      val result = testController.onPageLoad(testErn, testDraftId, NormalMode)(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.JourneyRecoveryController.onPageLoad().url
    }

    "must redirect to Journey Recovery for a POST if no existing data is found" in new Fixture(None) {
      val req = FakeRequest(POST, dispatchWarehouseExciseRoute).withFormUrlEncodedBody(("value", "answer"))

      val result = testController.onSubmit(testErn, testDraftId, NormalMode)(req)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.JourneyRecoveryController.onPageLoad().url
    }
  }
}
