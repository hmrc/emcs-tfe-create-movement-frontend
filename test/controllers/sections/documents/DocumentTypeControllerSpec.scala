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
import forms.sections.documents.DocumentTypeFormProvider
import mocks.services.{MockGetDocumentTypesService, MockUserAnswersService}
import models.sections.documents.DocumentType
import models.{NormalMode, UserAnswers}
import navigation.DocumentsNavigator
import navigation.FakeNavigators.FakeDocumentsNavigator
import pages.sections.documents.DocumentTypePage
import play.api.inject.bind
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.{GetDocumentTypesService, UserAnswersService}
import uk.gov.hmrc.http.HeaderCarrier
import viewmodels.helpers.SelectItemHelper
import views.html.sections.documents.DocumentTypeView

import scala.concurrent.Future

class DocumentTypeControllerSpec extends SpecBase with MockUserAnswersService with MockGetDocumentTypesService {

  def onwardRoute = Call("GET", "/foo")

  lazy val documentTypeRoute = routes.DocumentTypeController.onPageLoad(testErn, testDraftId, NormalMode).url

  val formProvider = new DocumentTypeFormProvider()
  val form = formProvider()
  val testDocumentType = DocumentType("testCode", "testDocumentType")

  class Fixture(userAnswers: Option[UserAnswers]) {
    val application = applicationBuilder(userAnswers = userAnswers)
      .overrides(
        bind[DocumentsNavigator].toInstance(new FakeDocumentsNavigator(onwardRoute)),
        bind[UserAnswersService].toInstance(mockUserAnswersService),
        bind[GetDocumentTypesService].toInstance(mockGetDocumentTypesService)
      )
      .build()

    val sampleDocumentTypesSelectOptions = SelectItemHelper.constructSelectItems(
      selectOptions = Seq(testDocumentType),
      defaultTextMessageKey = "documentType.select.defaultValue")(messages(application))
  }

  implicit val hc = HeaderCarrier()

  "DocumentType Controller" - {

    "must return OK and the correct view for a GET" in new Fixture(Some(
      emptyUserAnswers
    )) {

      MockGetDocumentTypesService.getDocumentTypes().returns(Future.successful(Seq(testDocumentType)))

      running(application) {

        val request = FakeRequest(GET, documentTypeRoute)

        val result = route(application, request).value

        val view = application.injector.instanceOf[DocumentTypeView]

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form, NormalMode, sampleDocumentTypesSelectOptions)(dataRequest(request), messages(application)).toString
      }
    }

    "must populate the view correctly on a GET when the question has previously been answered" in new Fixture(Some(
      emptyUserAnswers.set(DocumentTypePage, testDocumentType.code)
    )) {

      MockGetDocumentTypesService.getDocumentTypes().returns(Future.successful(Seq(testDocumentType)))

      running(application) {

        val sampleDocumentTypesSelectOptionsWithPreFilledSelection = SelectItemHelper.constructSelectItems(
          selectOptions = Seq(testDocumentType),
          defaultTextMessageKey = "documentType.select.defaultValue",
          existingAnswer = Some(testDocumentType.code))(messages(application))

        val request = FakeRequest(GET, documentTypeRoute)

        val view = application.injector.instanceOf[DocumentTypeView]

        val result = route(application, request).value

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form.fill(testDocumentType.code), NormalMode,
          sampleDocumentTypesSelectOptionsWithPreFilledSelection)(dataRequest(request), messages(application)).toString
      }
    }

    "must redirect to the next page when valid data is submitted" in new Fixture(Some(
      emptyUserAnswers
    )) {

      MockUserAnswersService.set(
        emptyUserAnswers.set(DocumentTypePage, testDocumentType.code)
      ).returns(Future.successful(emptyUserAnswers.set(DocumentTypePage, testDocumentType.code)))

      running(application) {

        val request =
          FakeRequest(POST, documentTypeRoute)
            .withFormUrlEncodedBody(("document-type", testDocumentType.code))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual onwardRoute.url
      }
    }

    "must return a Bad Request and errors when invalid data is submitted" in new Fixture(Some(
      emptyUserAnswers
    )) {

      MockGetDocumentTypesService.getDocumentTypes().returns(Future.successful(Seq(testDocumentType)))

      running(application) {

        val request =
          FakeRequest(POST, documentTypeRoute)
            .withFormUrlEncodedBody(("document-type", ""))

        val boundForm = form.bind(Map("document-type" -> ""))

        val view = application.injector.instanceOf[DocumentTypeView]

        val result = route(application, request).value

        status(result) mustEqual BAD_REQUEST
        contentAsString(result) mustEqual view(boundForm, NormalMode, sampleDocumentTypesSelectOptions)(dataRequest(request), messages(application)).toString
      }
    }

    "must redirect to Journey Recovery for a GET if no existing data is found" in {
      val application = applicationBuilder(userAnswers = None).build()

      running(application) {
        val request = FakeRequest(GET, documentTypeRoute)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual controllers.routes.JourneyRecoveryController.onPageLoad().url
      }
    }

    "redirect to Journey Recovery for a POST if no existing data is found" in {
      val application = applicationBuilder(userAnswers = None).build()

      val testDocumentType = "testDocumentType"

      running(application) {
        val request =
          FakeRequest(POST, documentTypeRoute)
            .withFormUrlEncodedBody(("value", testDocumentType))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual controllers.routes.JourneyRecoveryController.onPageLoad().url
      }
    }
  }
}
