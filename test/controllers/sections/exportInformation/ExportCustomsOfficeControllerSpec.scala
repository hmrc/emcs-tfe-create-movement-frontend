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

package controllers.sections.exportInformation

import base.SpecBase
import forms.sections.exportInformation.ExportCustomsOfficeFormProvider
import mocks.services.MockUserAnswersService
import models.requests.UserRequest
import models.sections.info.movementScenario.MovementScenario.ExportWithCustomsDeclarationLodgedInTheEu
import models.{NormalMode, UserAnswers}
import navigation.ExportInformationNavigator
import navigation.FakeNavigators.FakeExportInformationNavigator
import pages.sections.exportInformation.ExportCustomsOfficePage
import pages.sections.info.DestinationTypePage
import play.api.inject.bind
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.UserAnswersService
import views.html.sections.exportInformation.ExportCustomsOfficeView

import scala.concurrent.Future

class ExportCustomsOfficeControllerSpec extends SpecBase with MockUserAnswersService {

  val formProvider = new ExportCustomsOfficeFormProvider()
  val form = formProvider()

  lazy val exportCustomsOfficeRoute = routes.ExportCustomsOfficeController.onPageLoad(testErn, testLrn, NormalMode).url
  lazy val exportCustomsOfficeSubmitAction = routes.ExportCustomsOfficeController.onSubmit(testErn, testLrn, NormalMode)

  implicit val ur: UserRequest[_] = userRequest(FakeRequest())

  val defaultUserAnswers = emptyUserAnswers.set(DestinationTypePage, ExportWithCustomsDeclarationLodgedInTheEu())

  class Fixture(val userAnswers: Option[UserAnswers] = Some(defaultUserAnswers)) {
    val application =
      applicationBuilder(userAnswers)
        .overrides(
          bind[ExportInformationNavigator].toInstance(new FakeExportInformationNavigator(testOnwardRoute)),
          bind[UserAnswersService].toInstance(mockUserAnswersService)
        )
        .build()

    val view = application.injector.instanceOf[ExportCustomsOfficeView]
  }

  "ExportCustomsOffice Controller" - {

    "must return OK and the correct view for a GET" in new Fixture() {
      running(application) {

        val request = FakeRequest(GET, exportCustomsOfficeRoute)
        val result = route(application, request).value

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form, exportCustomsOfficeSubmitAction, euExport = true)(dataRequest(request), messages(application)).toString
      }
    }

    "must populate the view correctly on a GET when the question has previously been answered" in new Fixture(
      Some(defaultUserAnswers.set(ExportCustomsOfficePage, "answer"))
    ) {
      running(application) {

        val request = FakeRequest(GET, exportCustomsOfficeRoute)
        val result = route(application, request).value

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form.fill("answer"), exportCustomsOfficeSubmitAction, euExport = true)(dataRequest(request), messages(application)).toString
      }
    }

    "must redirect to the next page when valid data is submitted" in new Fixture() {
      running(application) {

        MockUserAnswersService.set().returns(Future.successful(emptyUserAnswers))

        val request = FakeRequest(POST, exportCustomsOfficeRoute).withFormUrlEncodedBody(("value", "AB123456"))
        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual testOnwardRoute.url
      }
    }

    "must return a Bad Request and errors when invalid data is submitted" in new Fixture() {
      running(application) {

        val request = FakeRequest(POST, exportCustomsOfficeRoute).withFormUrlEncodedBody(("value", ""))
        val boundForm = form.bind(Map("value" -> ""))
        val result = route(application, request).value

        status(result) mustEqual BAD_REQUEST
        contentAsString(result) mustEqual view(boundForm, exportCustomsOfficeSubmitAction, euExport = true)(dataRequest(request), messages(application)).toString
      }
    }

    "must redirect to Journey Recovery for a GET if no existing data is found" in new Fixture(None) {
      running(application) {

        val request = FakeRequest(GET, exportCustomsOfficeRoute)
        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual controllers.routes.JourneyRecoveryController.onPageLoad().url
      }
    }

    "must redirect to Journey Recovery for a POST if no existing data is found" in new Fixture(None) {
      running(application) {

        val request = FakeRequest(POST, exportCustomsOfficeRoute).withFormUrlEncodedBody(("value", "answer"))
        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual controllers.routes.JourneyRecoveryController.onPageLoad().url
      }
    }
  }
}