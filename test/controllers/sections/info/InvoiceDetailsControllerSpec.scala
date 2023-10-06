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
import com.ibm.icu.impl.number.AffixUtils
import controllers.routes
import forms.sections.info.InvoiceDetailsFormProvider
import mocks.services.MockUserAnswersService
import models.{NormalMode, UserAnswers}
import navigation.FakeNavigators.FakeNavigator
import navigation.Navigator
import pages.sections.info.InvoiceDetailsPage
import play.api.inject.bind
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.UserAnswersService
import views.html.sections.info.InvoiceDetailsView

import scala.concurrent.Future

class InvoiceDetailsControllerSpec extends SpecBase with MockUserAnswersService {


  class Fixture(userAnswers: Option[UserAnswers] = Some(emptyUserAnswers)) {

    def onwardRoute = Call("GET", "/foo")

    val formProvider = new InvoiceDetailsFormProvider()
    val form = formProvider()

    lazy val invoiceDetailsRoute = controllers.sections.info.routes.InvoiceDetailsController.onPageLoad(testErn, testLrn).url
    lazy val invoiceDetailsOnSubmit= controllers.sections.info.routes.InvoiceDetailsController.onSubmit(testErn, testLrn)

    val application = applicationBuilder(userAnswers = userAnswers)
      .overrides(
        bind[Navigator].toInstance(new FakeNavigator(onwardRoute)),
        bind[UserAnswersService].toInstance(mockUserAnswersService)
      )
      .build()
  }

  "InvoiceDetails Controller" - {

    "must return OK and the correct view for a GET" in new Fixture() {

      running(application) {

        val request = FakeRequest(GET, invoiceDetailsRoute)

        val result = route(application, request).value

        val view = application.injector.instanceOf[InvoiceDetailsView]

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form, invoiceDetailsOnSubmit)(dataRequest(request), messages(application)).toString
      }
    }

    "must populate the view correctly on a GET when the question has previously been answered" in new Fixture(Some(emptyUserAnswers
      .set(InvoiceDetailsPage, invoiceDetailsModel)
    )){

      running(application) {
        val request = FakeRequest(GET, invoiceDetailsRoute)

        val view = application.injector.instanceOf[InvoiceDetailsView]

        val result = route(application, request).value

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form.fill(invoiceDetailsModel), invoiceDetailsOnSubmit)(dataRequest(request), messages(application)).toString
      }
    }

    "must redirect to the next page when valid data is submitted" in new Fixture() {

      MockUserAnswersService.set().returns(Future.successful(emptyUserAnswers))

      running(application) {
        val request =
          FakeRequest(POST, invoiceDetailsRoute)
            .withFormUrlEncodedBody(
              ("invoice-reference", "answer"),
              ("invoice-date.day", "1"),
              ("invoice-date.month", "1"),
              ("invoice-date.year", "2020")
            )

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual onwardRoute.url
      }
    }

    "must return a Bad Request and errors when invalid data is submitted" in new Fixture() {

      running(application) {
        val request =
          FakeRequest(POST, invoiceDetailsRoute)
            .withFormUrlEncodedBody(("value", ""))

        val boundForm = form.bind(Map("value" -> ""))

        val view = application.injector.instanceOf[InvoiceDetailsView]

        val result = route(application, request).value

        status(result) mustEqual BAD_REQUEST
        contentAsString(result) mustEqual view(boundForm, invoiceDetailsOnSubmit)(dataRequest(request), messages(application)).toString
      }
    }

    "must redirect to Journey Recovery for a GET if no existing data is found" in new Fixture(None) {

      running(application) {
        val request = FakeRequest(GET, invoiceDetailsRoute)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad().url
      }
    }

    "must redirect to Journey Recovery for a POST if no existing data is found" in new Fixture(None) {

      running(application) {
        val request =
          FakeRequest(POST, invoiceDetailsRoute)
            .withFormUrlEncodedBody(("value", "answer"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad().url
      }
    }
  }
}
