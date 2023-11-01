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
import controllers.routes
import forms.sections.consignee.ConsigneeExportFormProvider
import mocks.services.MockUserAnswersService
import models.NormalMode
import models.sections.consignee.{ConsigneeExportVat, ConsigneeExportVatType}
import navigation.ConsigneeNavigator
import navigation.FakeNavigators.FakeConsigneeNavigator
import pages.sections.consignee.{ConsigneeExportPage, ConsigneeExportVatPage}
import play.api.inject.bind
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.UserAnswersService
import views.html.sections.consignee.ConsigneeExportView

import scala.concurrent.Future

class ConsigneeExportControllerSpec extends SpecBase with MockUserAnswersService {

  def onwardRoute = Call("GET", "/foo")

  val formProvider = new ConsigneeExportFormProvider()
  val form = formProvider()

  lazy val consigneeExportRoute = controllers.sections.consignee.routes.ConsigneeExportController.onPageLoad(testErn, testDraftId, NormalMode).url

  "ConsigneeExport Controller" - {

    "onPageLoad" - {
      "must return OK and the correct view for a GET" in {

        val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

        running(application) {
          val request = FakeRequest(GET, consigneeExportRoute)

          val result = route(application, request).value

          val view = application.injector.instanceOf[ConsigneeExportView]

          status(result) mustEqual OK
          contentAsString(result) mustEqual view(form, NormalMode)(dataRequest(request), messages(application)).toString
        }
      }

      "must populate the view correctly on a GET when the question has previously been answered" in {

        val userAnswers = emptyUserAnswers.set(ConsigneeExportPage, true)

        val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

        running(application) {
          val request = FakeRequest(GET, consigneeExportRoute)

          val view = application.injector.instanceOf[ConsigneeExportView]

          val result = route(application, request).value

          status(result) mustEqual OK
          contentAsString(result) mustEqual view(form.fill(true), NormalMode)(dataRequest(request), messages(application)).toString
        }
      }
    }

    "onSubmit" - {

      "must redirect to the next page when valid data is submitted - data is new" in {

        val application =
          applicationBuilder(userAnswers = Some(emptyUserAnswers))
            .overrides(
              bind[ConsigneeNavigator].toInstance(new FakeConsigneeNavigator(onwardRoute)),
              bind[UserAnswersService].toInstance(mockUserAnswersService)
            )
            .build()

        running(application) {

          MockUserAnswersService.set().returns(Future.successful(emptyUserAnswers))

          val request =
            FakeRequest(POST, consigneeExportRoute)
              .withFormUrlEncodedBody(("value", "true"))

          val result = route(application, request).value

          status(result) mustEqual SEE_OTHER
          redirectLocation(result).value mustEqual onwardRoute.url
        }
      }

      "must redirect to the next page when valid data is submitted - data has changed" in {

        val userAnswers = emptyUserAnswers
          .set(ConsigneeExportPage, true)
          .set(ConsigneeExportVatPage, ConsigneeExportVat(ConsigneeExportVatType.No, None, None))

        val application =
          applicationBuilder(userAnswers = Some(userAnswers))
            .overrides(
              bind[ConsigneeNavigator].toInstance(new FakeConsigneeNavigator(onwardRoute)),
              bind[UserAnswersService].toInstance(mockUserAnswersService)
            )
            .build()

        running(application) {

          MockUserAnswersService.set(emptyUserAnswers.set(ConsigneeExportPage, false)).returns(Future.successful(emptyUserAnswers))

          val request =
            FakeRequest(POST, consigneeExportRoute)
              .withFormUrlEncodedBody(("value", "false"))

          val result = route(application, request).value

          status(result) mustEqual SEE_OTHER
          redirectLocation(result).value mustEqual onwardRoute.url
        }
      }

      "must redirect to the next page when valid data is submitted - data has not changed" in {

        val userAnswers = emptyUserAnswers.set(ConsigneeExportPage, true)

        val application =
          applicationBuilder(userAnswers = Some(userAnswers))
            .overrides(
              bind[ConsigneeNavigator].toInstance(new FakeConsigneeNavigator(onwardRoute)),
              bind[UserAnswersService].toInstance(mockUserAnswersService)
            )
            .build()

        running(application) {

          val request =
            FakeRequest(POST, consigneeExportRoute)
              .withFormUrlEncodedBody(("value", "true"))

          val result = route(application, request).value

          status(result) mustEqual SEE_OTHER
          redirectLocation(result).value mustEqual onwardRoute.url
        }
      }

      "must return a Bad Request and errors when invalid data is submitted" in {

        val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

        running(application) {
          val request =
            FakeRequest(POST, consigneeExportRoute)
              .withFormUrlEncodedBody(("value", ""))

          val boundForm = form.bind(Map("value" -> ""))

          val view = application.injector.instanceOf[ConsigneeExportView]

          val result = route(application, request).value

          status(result) mustEqual BAD_REQUEST
          contentAsString(result) mustEqual view(boundForm, NormalMode)(dataRequest(request), messages(application)).toString
        }
      }

    }

    "must redirect to Journey Recovery" - {

      "for a GET if no existing data is found" in {

        val application = applicationBuilder(userAnswers = None).build()

        running(application) {
          val request = FakeRequest(GET, consigneeExportRoute)

          val result = route(application, request).value

          status(result) mustEqual SEE_OTHER
          redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad().url
        }
      }

      "for a POST if no existing data is found" in {

        val application = applicationBuilder(userAnswers = None).build()

        running(application) {
          val request =
            FakeRequest(POST, consigneeExportRoute)
              .withFormUrlEncodedBody(("value", "true"))

          val result = route(application, request).value

          status(result) mustEqual SEE_OTHER
          redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad().url
        }
      }

    }
  }
}
