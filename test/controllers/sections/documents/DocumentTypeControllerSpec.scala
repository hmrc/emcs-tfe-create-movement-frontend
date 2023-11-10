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
import forms.sections.documents.DocumentTypeFormProvider
import mocks.services.MockUserAnswersService
import models.sections.documents.DocumentType
import models.{NormalMode, UserAnswers}
import navigation.FakeNavigators.{FakeDocumentsNavigator, FakeNavigator}
import navigation.{DocumentsNavigator, Navigator}
import pages.sections.documents.DocumentTypePage
import play.api.inject.bind
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.{GetDocumentTypesService, UserAnswersService}
import uk.gov.hmrc.http.HeaderCarrier
import views.html.sections.documents.DocumentTypeView

import scala.concurrent.Future

class DocumentTypeControllerSpec extends SpecBase with MockUserAnswersService {

  def onwardRoute = Call("GET", "/foo")

  lazy val documentTypeRoute = routes.DocumentTypeController.onPageLoad(testErn, testDraftId, NormalMode).url

  val formProvider = new DocumentTypeFormProvider()
  val form = formProvider()
  val testDocumentType = DocumentType("testCode", "testDocumentType")

  implicit val hc = HeaderCarrier()

  "DocumentType Controller" - {

    "must return OK and the correct view for a GET" in {
      val mockGetDocumentTypesService = stub[GetDocumentTypesService]

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers))
        .overrides(bind[GetDocumentTypesService].to(mockGetDocumentTypesService))
        .build()

      running(application) {
        (mockGetDocumentTypesService.getDocumentTypes()(_: HeaderCarrier)).when(*).returns(Future.successful(Seq(testDocumentType)))

        val request = FakeRequest(GET, documentTypeRoute)

        val result = route(application, request).value

        val view = application.injector.instanceOf[DocumentTypeView]

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form, NormalMode, Seq(testDocumentType))(dataRequest(request), messages(application)).toString
      }
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {
      val userAnswers = emptyUserAnswers.set(DocumentTypePage, testDocumentType.code)

      val mockGetDocumentTypesService = stub[GetDocumentTypesService]
      val application = applicationBuilder(userAnswers = Some(userAnswers)).overrides(bind[GetDocumentTypesService].to(mockGetDocumentTypesService)).build()

      running(application) {
        (mockGetDocumentTypesService.getDocumentTypes()(_: HeaderCarrier)).when(*).returns(Future.successful(Seq(testDocumentType)))

        val request = FakeRequest(GET, documentTypeRoute)

        val view = application.injector.instanceOf[DocumentTypeView]

        val result = route(application, request).value

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form.fill(testDocumentType.code), NormalMode, Seq(testDocumentType))(dataRequest(request), messages(application)).toString
      }
    }

    "must redirect to the next page when valid data is submitted" in {
      MockUserAnswersService.set().returns(Future.successful(emptyUserAnswers))
      val mockGetDocumentTypesService = stub[GetDocumentTypesService]

      val application =
        applicationBuilder(userAnswers = Some(emptyUserAnswers))
          .overrides(
            bind[DocumentsNavigator].toInstance(new FakeDocumentsNavigator(onwardRoute)),
            bind[UserAnswersService].toInstance(mockUserAnswersService),
            bind[GetDocumentTypesService].to(mockGetDocumentTypesService)
          )
          .build()

      running(application) {
        (mockGetDocumentTypesService.getDocumentTypes()(_: HeaderCarrier)).when(*).returns(Future.successful(Seq(testDocumentType)))

        val request =
          FakeRequest(POST, documentTypeRoute)
            .withFormUrlEncodedBody(("document-type", testDocumentType.code))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual onwardRoute.url
      }
    }

    "must return a Bad Request and errors when invalid data is submitted" in {
      val mockGetDocumentTypesService = stub[GetDocumentTypesService]

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers))
        .overrides(bind[GetDocumentTypesService].to(mockGetDocumentTypesService))
        .build()

      running(application) {
        (mockGetDocumentTypesService.getDocumentTypes()(_: HeaderCarrier)).when(*).returns(Future.successful(Seq(testDocumentType)))

        val request =
          FakeRequest(POST, documentTypeRoute)
            .withFormUrlEncodedBody(("document-type", ""))

        val boundForm = form.bind(Map("document-type" -> ""))

        val view = application.injector.instanceOf[DocumentTypeView]

        val result = route(application, request).value

        status(result) mustEqual BAD_REQUEST
        contentAsString(result) mustEqual view(boundForm, NormalMode, Seq(testDocumentType))(dataRequest(request), messages(application)).toString
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
