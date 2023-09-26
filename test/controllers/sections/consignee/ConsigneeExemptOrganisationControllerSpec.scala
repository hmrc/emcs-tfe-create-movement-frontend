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
import fixtures.OrganisationDetailsFixtures
import forms.sections.consignee.ConsigneeExemptOrganisationFormProvider
import mocks.services.{MockGetMemberStatesService, MockUserAnswersService}
import models.{NormalMode, UserAnswers}
import navigation.FakeNavigators.{FakeConsigneeNavigator, FakeNavigator}
import navigation.{ConsigneeNavigator, Navigator}
import pages.sections.consignee.ConsigneeExemptOrganisationPage
import play.api.inject.bind
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.{GetMemberStatesService, UserAnswersService}
import uk.gov.hmrc.govukfrontend.views.viewmodels.select.SelectItem
import uk.gov.hmrc.http.HeaderCarrier
import views.html.sections.consignee.ConsigneeExemptOrganisationView

import scala.concurrent.{ExecutionContext, Future}

class ConsigneeExemptOrganisationControllerSpec extends SpecBase with MockUserAnswersService with OrganisationDetailsFixtures with MockGetMemberStatesService {


  class Fixture(userAnswers: Option[UserAnswers] = Some(emptyUserAnswers)) {

    val testSelectItems: Seq[SelectItem] = Seq(
      SelectItem(
        value = Some("one"),
        text = "one"
      ),
      SelectItem(
        value = Some("two"),
        text = "two"
      ),
      SelectItem(
        value = Some("three"),
        text = "three"
      )
    )

    implicit val hc = HeaderCarrier()
    implicit val ec = ExecutionContext.global

    def onwardRoute = Call("GET", "/foo")

    val formProvider = new ConsigneeExemptOrganisationFormProvider()
    val form = formProvider()

    lazy val consigneeExemptOrganisationRoute =
      controllers.sections.consignee.routes.ConsigneeExemptOrganisationController.onPageLoad(testErn, testLrn, NormalMode).url

    lazy val onSubmitCall = controllers.sections.consignee.routes.ConsigneeExemptOrganisationController.onSubmit(testErn, testLrn, NormalMode)

    val application = applicationBuilder(userAnswers)
      .overrides(
        bind[ConsigneeNavigator].toInstance(new FakeConsigneeNavigator(onwardRoute)),
        bind[UserAnswersService].toInstance(mockUserAnswersService),
        bind[GetMemberStatesService].toInstance(mockGetMemberStatesService)
      )
      .build()

    val view = application.injector.instanceOf[ConsigneeExemptOrganisationView]
  }


  "ConsigneeExemptOrganisation Controller" - {

    "must return OK and the correct view for a GET" in new Fixture() {

      running(application) {

        val request = FakeRequest(GET, consigneeExemptOrganisationRoute)

        MockGetMemberStatesService.getMemberStates().returns(Future(testSelectItems))

        val result = route(application, request).value

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(
          form = form,
          items = testSelectItems,
          call = onSubmitCall
        )(dataRequest(request), messages(application)).toString
      }
    }

    "must populate the view correctly on a GET when the question has previously been answered" in new Fixture(Some(emptyUserAnswers
      .set(ConsigneeExemptOrganisationPage, exemptOrganisationDetailsModel)
    )) {

      running(application) {

        val request = FakeRequest(GET, consigneeExemptOrganisationRoute)

        MockGetMemberStatesService.getMemberStates().returns(Future(testSelectItems))

        val result = route(application, request).value

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(
          form = form.fill(exemptOrganisationDetailsModel),
          items = testSelectItems,
          call = onSubmitCall
        )(dataRequest(request), messages(application)).toString
      }
    }

    "must redirect to the next page when valid data is submitted" in new Fixture() {

      MockUserAnswersService.set().returns(Future.successful(emptyUserAnswers))

      running(application) {
        val request =
          FakeRequest(POST, consigneeExemptOrganisationRoute)
            .withFormUrlEncodedBody(
              ("memberState", "answer"),
              ("certificateSerialNumber", "answer")
            )

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual onwardRoute.url
      }
    }

    "must return a Bad Request and errors when invalid data is submitted" in new Fixture() {

      running(application) {
        val request =
          FakeRequest(POST, consigneeExemptOrganisationRoute)
            .withFormUrlEncodedBody(("value", ""))

        val boundForm = form.bind(Map("value" -> ""))

        MockGetMemberStatesService.getMemberStates().returns(Future(testSelectItems))

        val result = route(application, request).value

        status(result) mustEqual BAD_REQUEST
        contentAsString(result) mustEqual view(
          form = boundForm,
          items = testSelectItems,
          call = onSubmitCall
        )(dataRequest(request), messages(application)).toString
      }
    }

    "must redirect to Journey Recovery for a GET if no existing data is found" in new Fixture(None) {

      running(application) {
        val request = FakeRequest(GET, consigneeExemptOrganisationRoute)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad().url
      }
    }

    "must redirect to Journey Recovery for a POST if no existing data is found" in new Fixture(None) {

      running(application) {
        val request =
          FakeRequest(POST, consigneeExemptOrganisationRoute)
            .withFormUrlEncodedBody(("value", "answer"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad().url
      }
    }
  }
}
