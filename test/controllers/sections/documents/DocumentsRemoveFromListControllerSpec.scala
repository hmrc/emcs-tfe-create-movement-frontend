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

package controllers.sections.documents

import base.SpecBase
import forms.sections.documents.DocumentsRemoveFromListFormProvider
import mocks.services.MockUserAnswersService
import models.{Index, UserAnswers}
import play.api.inject.bind
import play.api.libs.json.Json
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.UserAnswersService
import views.html.sections.documents.DocumentsRemoveFromListView

import scala.concurrent.Future

class DocumentsRemoveFromListControllerSpec extends SpecBase with MockUserAnswersService {

  class Fixture(userAnswers: Option[UserAnswers] = Some(userAnswersWithDocuments())) {

    val application = applicationBuilder(userAnswers)
      .overrides(bind[UserAnswersService].toInstance(mockUserAnswersService))
      .build()

    val view = application.injector.instanceOf[DocumentsRemoveFromListView]
  }

  val formProvider = new DocumentsRemoveFromListFormProvider()
  val form = formProvider(testIndex1)

  //TODO: Dummy documents to simulate documents being added to the array
  def userAnswersWithDocuments(nDocuments: Int = 1) = {
    emptyUserAnswers.copy(data = Json.obj("documents" -> (1 to nDocuments).map(_ => Json.obj())))
  }

  def documentsRemoveUnitRoute(idx: Index = testIndex1) =
    controllers.sections.documents.routes.DocumentsRemoveFromListController.onPageLoad(testErn, testDraftId, idx).url

  "DocumentsRemoveFromList Controller" - {

    "must return OK and the correct view for a GET" in new Fixture() {
      running(application) {

        val request = FakeRequest(GET, documentsRemoveUnitRoute())
        val result = route(application, request).value

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form, testIndex1)(dataRequest(request), messages(application)).toString
      }
    }

    "must redirect to the index controller when index is out of bounds (for GET)" in new Fixture() {
      running(application) {

        val request = FakeRequest(GET, documentsRemoveUnitRoute(testIndex2))
        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual controllers.sections.documents.routes.DocumentsIndexController.onPageLoad(testErn, testDraftId).url
      }
    }

    "must redirect to Add to List when the user answers POSTs an answer of no" in new Fixture() {
      running(application) {

        val request = FakeRequest(POST, documentsRemoveUnitRoute(testIndex1)).withFormUrlEncodedBody(("value", "false"))
        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual
          //TODO: Update to route to Add To List
          testOnly.controllers.routes.UnderConstructionController.onPageLoad().url
      }
    }

    "must redirect to the index controller when the user answers yes (removing the document)" in new Fixture(
      Some(userAnswersWithDocuments(nDocuments = 2))
    ) {
      running(application) {

        MockUserAnswersService.set(userAnswersWithDocuments())
          .returns(Future.successful(userAnswersWithDocuments()))

        val request = FakeRequest(POST, documentsRemoveUnitRoute()).withFormUrlEncodedBody(("value", "true"))
        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual controllers.sections.documents.routes.DocumentsIndexController.onPageLoad(testErn, testDraftId).url
      }
    }

    "must redirect to the index controller when index is out of bounds (for POST)" in new Fixture() {
      running(application) {

        val request =
          FakeRequest(POST, routes.DocumentsRemoveFromListController.onPageLoad(testErn, testDraftId, testIndex2).url)
            .withFormUrlEncodedBody(("value", "true"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual controllers.sections.documents.routes.DocumentsIndexController.onPageLoad(testErn, testDraftId).url
      }
    }

    "must return a Bad Request and errors when invalid data is submitted" in new Fixture() {
      running(application) {

        val request = FakeRequest(POST, documentsRemoveUnitRoute()).withFormUrlEncodedBody(("value", ""))
        val boundForm = form.bind(Map("value" -> ""))
        val result = route(application, request).value

        status(result) mustEqual BAD_REQUEST
        contentAsString(result) mustEqual view(boundForm, testIndex1)(dataRequest(request), messages(application)).toString
      }
    }

    "must redirect to Journey Recovery for a GET if no existing data is found" in new Fixture(None) {
      running(application) {

        val request = FakeRequest(GET, documentsRemoveUnitRoute())
        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual controllers.routes.JourneyRecoveryController.onPageLoad().url
      }
    }

    "must redirect to Journey Recovery for a POST if no existing data is found" in new Fixture(None) {
      running(application) {

        val request = FakeRequest(POST, documentsRemoveUnitRoute()).withFormUrlEncodedBody(("value", "true"))
        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual controllers.routes.JourneyRecoveryController.onPageLoad().url
      }
    }
  }
}
