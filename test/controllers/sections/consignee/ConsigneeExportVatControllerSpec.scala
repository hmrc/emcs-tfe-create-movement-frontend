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

package controllers.sections.consignee

import base.SpecBase
import controllers.actions.FakeDataRetrievalAction
import controllers.routes
import forms.sections.consignee.ConsigneeExportVatFormProvider
import mocks.services.MockUserAnswersService
import models.sections.consignee.ConsigneeExportVat
import models.sections.consignee.ConsigneeExportVatType.YesEoriNumber
import models.{NormalMode, UserAnswers}
import navigation.FakeNavigators.FakeConsigneeNavigator
import pages.sections.consignee.ConsigneeExportVatPage
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.sections.consignee.ConsigneeExportVatView

import scala.concurrent.Future

class ConsigneeExportVatControllerSpec extends SpecBase with MockUserAnswersService {

  class Fixture(optUserAnswers: Option[UserAnswers] = Some(emptyUserAnswers)) {
    val onwardRoute = Call("GET", "/foo")

    lazy val consigneeExportVatRoute = controllers.sections.consignee.routes.ConsigneeExportVatController.onPageLoad(testErn, testDraftId, NormalMode).url
    lazy val consigneeExportVatRouteSubmit = controllers.sections.consignee.routes.ConsigneeExportVatController.onSubmit(testErn, testDraftId, NormalMode).url

    val formProvider = new ConsigneeExportVatFormProvider()
    val form = formProvider()
    val request = FakeRequest(GET, consigneeExportVatRoute)
    lazy val view = app.injector.instanceOf[ConsigneeExportVatView]

    object TestController extends ConsigneeExportVatController(
      messagesApi,
      mockUserAnswersService,
      new FakeConsigneeNavigator(testOnwardRoute),
      fakeAuthAction,
      new FakeDataRetrievalAction(optUserAnswers, Some(testMinTraderKnownFacts)),
      dataRequiredAction,
      fakeUserAllowListAction,
      formProvider,
      messagesControllerComponents,
      view
    )
  }

  "ConsigneeExportVat Controller" - {
    "must return OK and the correct view for a GET" in new Fixture() {
      val result = TestController.onPageLoad(testErn, testDraftId, NormalMode)(request)

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(form, NormalMode)(dataRequest(request), messages(request)).toString
    }

    "must populate the view correctly on a GET when the question has previously been answered" in new Fixture(
      Some(emptyUserAnswers.set(ConsigneeExportVatPage, ConsigneeExportVat(YesEoriNumber, None, Some("EORI1234567890"))))) {

      val result = TestController.onPageLoad(testErn, testDraftId, NormalMode)(request)

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(
        form.fill(ConsigneeExportVat(YesEoriNumber, None, Some("EORI1234567890"))),
        NormalMode
      )(dataRequest(request), messages(request)).toString
    }

    "must redirect to the next page when valid data is submitted" in new Fixture() {
      MockUserAnswersService.set().returns(
        Future.successful(
          emptyUserAnswers.set(ConsigneeExportVatPage, ConsigneeExportVat(YesEoriNumber, None, Some("EORI1234567890")))
        )
      )

      val req = FakeRequest(POST, consigneeExportVatRouteSubmit).withFormUrlEncodedBody(
        ("exportType", YesEoriNumber.toString),
        ("eoriNumber", "EORI1234567890")
      )

      val result = TestController.onSubmit(testErn, testDraftId, NormalMode)(req)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual onwardRoute.url
    }

    "must return a Bad Request and errors when invalid data is submitted" in new Fixture() {
      val req = FakeRequest(POST, consigneeExportVatRouteSubmit).withFormUrlEncodedBody(("exportType", "invalid value"))

      val boundForm = form.bind(Map("exportType" -> "invalid value"))

      val result = TestController.onSubmit(testErn, testDraftId, NormalMode)(req)

      status(result) mustEqual BAD_REQUEST
      contentAsString(result) mustEqual view(boundForm, NormalMode)(dataRequest(req), messages(req)).toString
    }

    "must redirect to Journey Recovery for a GET if no existing data is found" in new Fixture(None) {
      val result = TestController.onPageLoad(testErn, testDraftId, NormalMode)(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad().url
    }

    "redirect to Journey Recovery for a POST if no existing data is found" in new Fixture(None) {
      val req = FakeRequest(POST, consigneeExportVatRouteSubmit).withFormUrlEncodedBody(("exportType", YesEoriNumber.toString))

      val result = TestController.onSubmit(testErn, testDraftId, NormalMode)(req)

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad().url
    }
  }
}
