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

package controllers.sections.info

import base.SpecBase
import controllers.actions.FakeDataRetrievalAction
import controllers.actions.predraft.FakePreDraftRetrievalAction
import forms.sections.info.InvoiceDetailsFormProvider
import mocks.services.{MockPreDraftService, MockUserAnswersService}
import models.sections.info.InvoiceDetailsModel
import models.{NormalMode, UserAnswers}
import navigation.FakeNavigators.FakeInfoNavigator
import pages.sections.info.InvoiceDetailsPage
import play.api.mvc.AnyContentAsEmpty
import play.api.test.Helpers._
import play.api.test.{FakeRequest, Helpers}
import utils.{DateTimeUtils, TimeMachine}
import views.html.sections.info.InvoiceDetailsView

import java.time.{LocalDate, LocalDateTime}
import scala.concurrent.Future

class InvoiceDetailsControllerSpec extends SpecBase with MockUserAnswersService with MockPreDraftService with DateTimeUtils {

  val testLocalDate = LocalDate.of(2023, 2, 9)

  val timeMachine = new TimeMachine {
    override def now(): LocalDateTime = testLocalDate.atStartOfDay()
  }

  class Fixture(val userAnswers: Option[UserAnswers] = Some(emptyUserAnswers)) {

    lazy val invoiceDetailsPreDraftSubmitRoute = controllers.sections.info.routes.InvoiceDetailsController.onPreDraftSubmit(testErn, NormalMode)
    lazy val invoiceDetailsSubmitRoute = controllers.sections.info.routes.InvoiceDetailsController.onSubmit(testErn, testDraftId)

    lazy val formProvider = new InvoiceDetailsFormProvider()
    lazy val form = formProvider()

    lazy val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest()

    lazy val view = app.injector.instanceOf[InvoiceDetailsView]

    lazy val controller = new InvoiceDetailsController(
      messagesApi,
      mockPreDraftService,
      new FakeInfoNavigator(testOnwardRoute),
      fakeAuthAction,
      new FakePreDraftRetrievalAction(userAnswers, Some(testMinTraderKnownFacts)),
      preDraftDataRequiredAction,
      new FakeDataRetrievalAction(userAnswers, Some(testMinTraderKnownFacts)),
      dataRequiredAction,
      fakeUserAllowListAction,
      formProvider,
      mockUserAnswersService,
      Helpers.stubMessagesControllerComponents(),
      view,
      timeMachine
    )
  }

  "InvoiceDetails Controller" - {

    "pre-draft" - {

      "must return OK and the correct view for a GET" in new Fixture() {

        val result = controller.onPreDraftPageLoad(testErn, NormalMode)(request)

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(
          form = form,
          currentDate = testLocalDate.formatDateNumbersOnly(),
          onSubmitCall = invoiceDetailsPreDraftSubmitRoute,
          skipQuestionCall = testOnwardRoute
        )(dataRequest(request), messages(request)).toString
      }

      "must redirect to the next page when valid data is submitted" in new Fixture() {

        val expectedToSaveAnswers = emptyUserAnswers
          .set(InvoiceDetailsPage(), InvoiceDetailsModel("answer", LocalDate.of(2020, 1, 1)))

        MockPreDraftService.set(expectedToSaveAnswers).returns(Future.successful(true))

        val result = controller.onPreDraftSubmit(testErn, NormalMode)(request.withFormUrlEncodedBody(
          ("invoice-reference", "answer"),
          ("value.day", "1"),
          ("value.month", "1"),
          ("value.year", "2020")
        ))

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual testOnwardRoute.url
      }

      "must return a Bad Request and errors when invalid data is submitted" in new Fixture() {

        val boundForm = form.bind(Map("value" -> ""))

        val result = controller.onPreDraftSubmit(testErn, NormalMode)(request.withFormUrlEncodedBody(("value", "")))

        status(result) mustEqual BAD_REQUEST
        contentAsString(result) mustEqual view(
          form = boundForm,
          currentDate = testLocalDate.formatDateNumbersOnly(),
          onSubmitCall = invoiceDetailsPreDraftSubmitRoute,
          skipQuestionCall = testOnwardRoute
        )(dataRequest(request), messages(request)).toString
      }
    }

    "post-draft" - {

      "must return OK and the correct view for a GET" in new Fixture() {

        val result = controller.onPageLoad(testErn, testDraftId)(request)

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(
          form = form,
          currentDate = testLocalDate.formatDateNumbersOnly(),
          onSubmitCall = invoiceDetailsSubmitRoute,
          skipQuestionCall = testOnwardRoute
        )(dataRequest(request), messages(request)).toString
      }

      "must redirect to the next page when valid data is submitted" in new Fixture() {

        val expectedToSaveAnswers = emptyUserAnswers
          .set(InvoiceDetailsPage(), InvoiceDetailsModel("answer", LocalDate.of(2020, 1, 1)))

        MockUserAnswersService.set(expectedToSaveAnswers).returns(Future.successful(emptyUserAnswers))

        val result = controller.onSubmit(testErn, testDraftId)(request.withFormUrlEncodedBody(
          ("invoice-reference", "answer"),
          ("value.day", "1"),
          ("value.month", "1"),
          ("value.year", "2020")
        ))

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual testOnwardRoute.url
      }

      "must return a Bad Request and errors when invalid data is submitted" in new Fixture() {

        val boundForm = form.bind(Map("value" -> ""))

        val result = controller.onSubmit(testErn, testDraftId)(request.withFormUrlEncodedBody(("value", "")))

        status(result) mustEqual BAD_REQUEST
        contentAsString(result) mustEqual view(
          form = boundForm,
          currentDate = testLocalDate.formatDateNumbersOnly(),
          onSubmitCall = invoiceDetailsSubmitRoute,
          skipQuestionCall = testOnwardRoute
        )(dataRequest(request), messages(request)).toString
      }
    }
  }
}
