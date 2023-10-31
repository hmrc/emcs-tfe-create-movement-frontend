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
import forms.sections.info.InvoiceDetailsFormProvider
import mocks.services.{MockPreDraftService, MockUserAnswersService}
import models.sections.info.InvoiceDetailsModel
import models.{NormalMode, UserAnswers}
import navigation.FakeNavigators.FakeInfoNavigator
import navigation.InformationNavigator
import pages.sections.info.InvoiceDetailsPage
import play.api.inject.bind
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.{PreDraftService, UserAnswersService}
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

    val formProvider = new InvoiceDetailsFormProvider()
    val form = formProvider()

    lazy val invoiceDetailsRoute = controllers.sections.info.routes.InvoiceDetailsController.onPreDraftPageLoad(testErn, NormalMode).url
    lazy val invoiceDetailsOnSubmit = controllers.sections.info.routes.InvoiceDetailsController.onPreDraftSubmit(testErn, NormalMode)

    val application = applicationBuilder(userAnswers)
      .overrides(
        bind[InformationNavigator].toInstance(new FakeInfoNavigator(testOnwardRoute)),
        bind[UserAnswersService].toInstance(mockUserAnswersService),
        bind[PreDraftService].toInstance(mockPreDraftService),
        bind[TimeMachine].toInstance(timeMachine)
      )
      .build()

    val view = application.injector.instanceOf[InvoiceDetailsView]
  }

  "InvoiceDetails Controller" - {

    "must return OK and the correct view for a GET" in new Fixture() {

      running(application) {

        val request = FakeRequest(GET, invoiceDetailsRoute)

        val result = route(application, request).value

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(
          form = form,
          currentDate = testLocalDate.formatDateNumbersOnly(),
          onSubmitCall = invoiceDetailsOnSubmit,
          skipQuestionCall = testOnwardRoute
        )(dataRequest(request), messages(application)).toString
      }
    }

    "must redirect to the next page when valid data is submitted" in new Fixture() {

      running(application) {

        val expectedToSaveAnswers = emptyUserAnswers
          .set(InvoiceDetailsPage, InvoiceDetailsModel("answer", LocalDate.of(2020,1,1)))

        MockPreDraftService.set(expectedToSaveAnswers).returns(Future.successful(true))

        val request =
          FakeRequest(POST, invoiceDetailsRoute)
            .withFormUrlEncodedBody(
              ("invoice-reference", "answer"),
              ("value.day", "1"),
              ("value.month", "1"),
              ("value.year", "2020")
            )

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual testOnwardRoute.url
      }
    }

    "must return a Bad Request and errors when invalid data is submitted" in new Fixture() {

      running(application) {
        val request =
          FakeRequest(POST, invoiceDetailsRoute)
            .withFormUrlEncodedBody(("value", ""))

        val boundForm = form.bind(Map("value" -> ""))

        val result = route(application, request).value

        status(result) mustEqual BAD_REQUEST
        contentAsString(result) mustEqual view(
          form = boundForm,
          currentDate = testLocalDate.formatDateNumbersOnly(),
          onSubmitCall = invoiceDetailsOnSubmit,
          skipQuestionCall = testOnwardRoute
        )(dataRequest(request), messages(application)).toString
      }
    }
  }
}
