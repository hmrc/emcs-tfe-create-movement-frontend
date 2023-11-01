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

package controllers.sections.destination

import base.SpecBase
import fixtures.UserAddressFixtures
import forms.sections.destination.DestinationConsigneeDetailsFormProvider
import mocks.services.MockUserAnswersService
import models.{CheckMode, NormalMode, UserAnswers}
import navigation.DestinationNavigator
import navigation.FakeNavigators.FakeDestinationNavigator
import pages.sections.destination._
import play.api.inject.bind
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.UserAnswersService
import views.html.sections.destination.DestinationConsigneeDetailsView

import scala.concurrent.Future

class DestinationConsigneeDetailsControllerSpec extends SpecBase with MockUserAnswersService with UserAddressFixtures {

  class Test(userAnswers: Option[UserAnswers] = Some(emptyUserAnswers)) {

    def onwardRoute = Call("GET", "/foo")

    val application =
      applicationBuilder(userAnswers = userAnswers)
        .overrides(
          bind[DestinationNavigator].toInstance(new FakeDestinationNavigator(onwardRoute)),
          bind[UserAnswersService].toInstance(mockUserAnswersService)
        )
        .build()

    val formProvider = new DestinationConsigneeDetailsFormProvider()

    val form = formProvider()

    val view = application.injector.instanceOf[DestinationConsigneeDetailsView]

    lazy val destinationConsigneeDetailsRoute = routes.DestinationConsigneeDetailsController.onPageLoad(testErn, testDraftId, NormalMode).url
    lazy val destinationConsigneeDetailsRouteCheckMode = routes.DestinationConsigneeDetailsController.onPageLoad(testErn, testDraftId, CheckMode).url
  }


  "DestinationConsigneeDetails Controller" - {

    "must return OK and the correct view for a GET" in new Test() {

      running(application) {

        val request = FakeRequest(GET, destinationConsigneeDetailsRoute)

        val result = route(application, request).value

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form, NormalMode)(dataRequest(request), messages(application)).toString
      }
    }

    "must populate the view correctly on a GET when the question has previously been answered" in new Test(Some(emptyUserAnswers
      .set(DestinationConsigneeDetailsPage, true)
    )) {

      running(application) {
        val request = FakeRequest(GET, destinationConsigneeDetailsRoute)

        val view = application.injector.instanceOf[DestinationConsigneeDetailsView]

        val result = route(application, request).value

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form.fill(true), NormalMode)(dataRequest(request), messages(application)).toString
      }
    }

    "must redirect to the next page when valid data is submitted" in new Test() {

      MockUserAnswersService.set().returns(Future.successful(emptyUserAnswers))

      running(application) {
        val request =
          FakeRequest(POST, destinationConsigneeDetailsRoute)
            .withFormUrlEncodedBody(("value", "true"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual onwardRoute.url
      }
    }

    "must return a Bad Request and errors when invalid data is submitted" in new Test() {

      running(application) {
        val request =
          FakeRequest(POST, destinationConsigneeDetailsRoute)
            .withFormUrlEncodedBody(("value", ""))

        val boundForm = form.bind(Map("value" -> ""))

        val view = application.injector.instanceOf[DestinationConsigneeDetailsView]

        val result = route(application, request).value

        status(result) mustEqual BAD_REQUEST
        contentAsString(result) mustEqual view(boundForm, NormalMode)(dataRequest(request), messages(application)).toString
      }
    }

    "must redirect to Journey Recovery for a GET if no existing data is found" in new Test(None) {

      running(application) {
        val request = FakeRequest(GET, destinationConsigneeDetailsRoute)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual controllers.routes.JourneyRecoveryController.onPageLoad().url
      }
    }

    "must redirect to Journey Recovery for a POST if no existing data is found" in new Test(None) {

      running(application) {
        val request =
          FakeRequest(POST, destinationConsigneeDetailsRoute)
            .withFormUrlEncodedBody(("value", "true"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual controllers.routes.JourneyRecoveryController.onPageLoad().url
      }
    }

    s"must redirect without changing any answers when this page is submitted with the same answer it already has" - {

      "when for a user with WarehouseExcise answered" in new Test(Some(emptyUserAnswers
        .set(DestinationWarehouseExcisePage, "excise")
        .set(DestinationConsigneeDetailsPage, false)
        .set(DestinationBusinessNamePage, "business name")
        .set(DestinationAddressPage, userAddressModelMax)
      )) {

        val request = FakeRequest(POST, destinationConsigneeDetailsRouteCheckMode)
          .withFormUrlEncodedBody(("value", "false"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
      }

      "when for a user with WarehouseVat answered" in new Test(Some(emptyUserAnswers
        .set(DestinationWarehouseVatPage, Some("vat"))
        .set(DestinationDetailsChoicePage, true)
        .set(DestinationConsigneeDetailsPage, true)
        .set(DestinationBusinessNamePage, "business name")
        .set(DestinationAddressPage, userAddressModelMax)
      )) {

        val request = FakeRequest(POST, destinationConsigneeDetailsRouteCheckMode)
          .withFormUrlEncodedBody(("value", "true"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
      }
    }

    s"must cleanse further Destination section answers answering Yes to using consignee details" - {

      "when for a user with WarehouseExcise answered" in new Test(Some(emptyUserAnswers
        .set(DestinationWarehouseExcisePage, "excise")
        .set(DestinationConsigneeDetailsPage, false)
        .set(DestinationBusinessNamePage, "business name")
        .set(DestinationAddressPage, userAddressModelMax)
      )) {

        val expectedAnswers = emptyUserAnswers
          .set(DestinationWarehouseExcisePage, "excise")
          .set(DestinationConsigneeDetailsPage, true)

        MockUserAnswersService.set(expectedAnswers).returns(Future.successful(expectedAnswers))

        val request = FakeRequest(POST, destinationConsigneeDetailsRouteCheckMode)
          .withFormUrlEncodedBody(("value", "true"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
      }

      "when for a user with WarehouseVat answered" in new Test(Some(emptyUserAnswers
        .set(DestinationWarehouseVatPage, Some("vat"))
        .set(DestinationDetailsChoicePage, true)
        .set(DestinationConsigneeDetailsPage, false)
        .set(DestinationBusinessNamePage, "business name")
        .set(DestinationAddressPage, userAddressModelMax)
      )) {

        val expectedAnswers = emptyUserAnswers
          .set(DestinationWarehouseVatPage, Some("vat"))
          .set(DestinationDetailsChoicePage, true)
          .set(DestinationConsigneeDetailsPage, true)

        MockUserAnswersService.set(expectedAnswers).returns(Future.successful(expectedAnswers))

        val request = FakeRequest(POST, destinationConsigneeDetailsRouteCheckMode)
          .withFormUrlEncodedBody(("value", "true"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
      }
    }
  }
}
