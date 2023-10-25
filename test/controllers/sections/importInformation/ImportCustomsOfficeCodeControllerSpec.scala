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

package controllers.sections.importInformation

import base.SpecBase
import forms.sections.importInformation.ImportCustomsOfficeCodeFormProvider
import mocks.services.MockUserAnswersService
import models.{NormalMode, NorthernIrelandRegisteredConsignor, UserAnswers}
import navigation.FakeNavigators.FakeImportInformationNavigator
import navigation.ImportInformationNavigator
import pages.sections.importInformation.ImportCustomsOfficeCodePage
import play.api.inject.bind
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.UserAnswersService
import views.html.sections.importInformation.ImportCustomsOfficeCodeView

import scala.concurrent.Future

class ImportCustomsOfficeCodeControllerSpec extends SpecBase with MockUserAnswersService {

  val formProvider = new ImportCustomsOfficeCodeFormProvider()
  val form = formProvider()

  lazy val importCustomsOfficeRoute = controllers.sections.importInformation.routes.ImportCustomsOfficeCodeController.onPageLoad(testErn, testDraftId, NormalMode).url
  lazy val importCustomsOfficeSubmitAction = controllers.sections.importInformation.routes.ImportCustomsOfficeCodeController.onSubmit(testErn, testDraftId, NormalMode)


  class Fixture(val userAnswers: Option[UserAnswers] = Some(emptyUserAnswers)) {
    lazy val application =
      applicationBuilder(userAnswers)
        .overrides(
          bind[ImportInformationNavigator].toInstance(new FakeImportInformationNavigator(testOnwardRoute)),
          bind[UserAnswersService].toInstance(mockUserAnswersService)
        )
        .build()

    lazy val view = application.injector.instanceOf[ImportCustomsOfficeCodeView]
  }

  "Import Customs Office Code Controller" - {
    "must return OK and the correct view for a GET" in new Fixture() {
      running(application) {

        val request = FakeRequest(GET, importCustomsOfficeRoute)
        val result = route(application, request).value

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form, importCustomsOfficeSubmitAction, NorthernIrelandRegisteredConsignor)(dataRequest(request), messages(application)).toString
      }
    }

    "must populate the view correctly on a GET when the question has previously been answered" in new Fixture(
      Some(emptyUserAnswers.set(ImportCustomsOfficeCodePage, "answer"))
    ) {
      running(application) {

        val request = FakeRequest(GET, importCustomsOfficeRoute)
        val result = route(application, request).value

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form.fill("answer"), importCustomsOfficeSubmitAction, NorthernIrelandRegisteredConsignor)(dataRequest(request), messages(application)).toString
      }
    }

    "must redirect to the next page when valid data is submitted" in new Fixture() {
      running(application) {

        MockUserAnswersService.set().returns(Future.successful(emptyUserAnswers))

        val request = FakeRequest(POST, importCustomsOfficeRoute).withFormUrlEncodedBody(("value", "AB123456"))
        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual testOnwardRoute.url
      }
    }

    "must return a Bad Request and errors when invalid data is submitted" in new Fixture() {
      running(application) {

        val request = FakeRequest(POST, importCustomsOfficeRoute).withFormUrlEncodedBody(("value", ""))
        val boundForm = form.bind(Map("value" -> ""))
        val result = route(application, request).value

        status(result) mustEqual BAD_REQUEST
        contentAsString(result) mustEqual view(boundForm, importCustomsOfficeSubmitAction, NorthernIrelandRegisteredConsignor)(dataRequest(request), messages(application)).toString
      }
    }

    "must redirect to Journey Recovery for a GET if no existing data is found" in new Fixture(None) {
      running(application) {

        val request = FakeRequest(GET, importCustomsOfficeRoute)
        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual controllers.routes.JourneyRecoveryController.onPageLoad().url
      }
    }

    "must redirect to Journey Recovery for a POST if no existing data is found" in new Fixture(None) {
      running(application) {

        val request = FakeRequest(POST, importCustomsOfficeRoute).withFormUrlEncodedBody(("value", "answer"))
        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual controllers.routes.JourneyRecoveryController.onPageLoad().url
      }
    }
  }
}
