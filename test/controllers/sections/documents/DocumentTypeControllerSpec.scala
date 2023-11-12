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
import fixtures.DocumentTypeFixtures
import forms.sections.documents.DocumentTypeFormProvider
import mocks.services.{MockGetDocumentTypesService, MockUserAnswersService}
import models.{Index, NormalMode, UserAnswers}
import models.sections.documents.DocumentType
import navigation.DocumentsNavigator
import navigation.FakeNavigators.FakeDocumentsNavigator
import pages.sections.documents.{DocumentReferencePage, DocumentTypePage, ReferenceAvailablePage}
import play.api.inject.bind
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.{GetDocumentTypesService, UserAnswersService}
import uk.gov.hmrc.http.HeaderCarrier
import views.html.sections.documents.DocumentTypeView

import scala.concurrent.Future

class DocumentTypeControllerSpec extends SpecBase with MockUserAnswersService with DocumentTypeFixtures with MockGetDocumentTypesService {

  class Setup(userAnswers: Option[UserAnswers] = Some(emptyUserAnswers)) {

    def onwardRoute = Call("GET", "/foo")

    val mockGetDocumentTypesService = stub[GetDocumentTypesService]

    val application = applicationBuilder(userAnswers = userAnswers)
      .overrides(
        bind[DocumentsNavigator].toInstance(new FakeDocumentsNavigator(onwardRoute)),
        bind[UserAnswersService].toInstance(mockUserAnswersService),
        bind[GetDocumentTypesService].to(mockGetDocumentTypesService),
    )
      .build()

    def documentTypeRoute(idx: Index = 0): String =
      routes.DocumentTypeController.onPageLoad(testErn, testDraftId, idx, NormalMode).url

    def onSubmitCall(idx: Index = 0) =
      routes.DocumentTypeController.onSubmit(testErn, testDraftId, idx, NormalMode)

    val formProvider = new DocumentTypeFormProvider()
    val form = formProvider()

    val documentTypes = Seq(documentTypeModel, documentTypeOtherModel)

    val view = application.injector.instanceOf[DocumentTypeView]

//    val sampleDocumentTypesSelectOptions = SelectItemHelper.constructSelectItems(
//      selectOptions = Seq(testDocumentType),
//      defaultTextMessageKey = "documentType.select.defaultValue")(messages(application))
  }

  "DocumentType Controller" - {

    "GET onPageLoad" - {

      "must return OK and the correct view" in new Setup() {

        running(application) {

          (mockGetDocumentTypesService.getDocumentTypes()(_: HeaderCarrier)).when(*)
            .returns(Future.successful(documentTypes))

          val request = FakeRequest(GET, documentTypeRoute())

          val result = route(application, request).value

          status(result) mustEqual OK
          contentAsString(result) mustEqual view(
            form = form,
            onSubmitCall = onSubmitCall(),
            documentTypes = documentTypes
          )(dataRequest(request), messages(application)).toString
        }
      }

      "must populate the view correctly when the question has previously been answered" in new Setup(Some(
        emptyUserAnswers.set(DocumentTypePage(0), documentTypeModel.code)
      )) {

        running(application) {

          (mockGetDocumentTypesService.getDocumentTypes()(_: HeaderCarrier)).when(*)
            .returns(Future.successful(documentTypes))
//        val sampleDocumentTypesSelectOptionsWithPreFilledSelection = SelectItemHelper.constructSelectItems(
//          selectOptions = Seq(documentTypeModel),
//          defaultTextMessageKey = "documentType.select.defaultValue",
//          existingAnswer = Some(testDocumentType.code))(messages(application))

          val request = FakeRequest(GET, documentTypeRoute())

          val result = route(application, request).value

          status(result) mustEqual OK
          contentAsString(result) mustEqual view(
            form = form.fill(documentTypeModel.code),
            onSubmitCall = onSubmitCall(),
            documentTypes = documentTypes
          )(dataRequest(request), messages(application)).toString
        }
      }

      "must redirect to DocumentsIndexController when the idx is greater 0 and there are no current documents in UserAnswers" in new Setup() {

        running(application) {

          (mockGetDocumentTypesService.getDocumentTypes()(_: HeaderCarrier)).when(*)
            .returns(Future.successful(documentTypes))

          val request = FakeRequest(POST, documentTypeRoute(1))
            .withFormUrlEncodedBody(("document-type", documentTypeModel.code))

          val result = route(application, request).value

          status(result) mustEqual SEE_OTHER
          redirectLocation(result).value mustEqual routes.DocumentsIndexController.onPageLoad(testErn, testDraftId).url
        }
      }

      "must redirect to DocumentsIndexController when the idx is greater than the next document to valid document idx" in new Setup(Some(
        emptyUserAnswers
          .set(DocumentTypePage(0), documentTypeOtherModel.code)
          .set(ReferenceAvailablePage(0), true)
          .set(DocumentReferencePage(0), "reference")
      )) {

        running(application) {

          (mockGetDocumentTypesService.getDocumentTypes()(_: HeaderCarrier)).when(*)
            .returns(Future.successful(documentTypes))

          val request = FakeRequest(POST, documentTypeRoute(2))
            .withFormUrlEncodedBody(("document-type", documentTypeModel.code))

          val result = route(application, request).value

          status(result) mustEqual SEE_OTHER
          redirectLocation(result).value mustEqual routes.DocumentsIndexController.onPageLoad(testErn, testDraftId).url
        }
      }

      "must redirect to DocumentsIndexController when the idx is less than 0" in new Setup(Some(
        emptyUserAnswers
          .set(DocumentTypePage(0), documentTypeOtherModel.code)
          .set(ReferenceAvailablePage(0), true)
          .set(DocumentReferencePage(0), "reference")
      )) {

        running(application) {

          (mockGetDocumentTypesService.getDocumentTypes()(_: HeaderCarrier)).when(*)
            .returns(Future.successful(documentTypes))

          val request = FakeRequest(POST, documentTypeRoute(-1))
            .withFormUrlEncodedBody(("document-type", documentTypeModel.code))

          val result = route(application, request).value

          status(result) mustEqual SEE_OTHER
          redirectLocation(result).value mustEqual routes.DocumentsIndexController.onPageLoad(testErn, testDraftId).url
        }
      }

      "must redirect to Journey Recovery if no existing data is found" in new Setup(None) {

        running(application) {

          val request = FakeRequest(GET, documentTypeRoute())

          val result = route(application, request).value

          status(result) mustEqual SEE_OTHER
          redirectLocation(result).value mustEqual controllers.routes.JourneyRecoveryController.onPageLoad().url
        }
      }
    }

    "POST onSubmit" - {

      "must redirect to the next page when valid data is submitted" in new Setup() {

        MockUserAnswersService.set().returns(Future.successful(emptyUserAnswers))

        running(application) {

          (mockGetDocumentTypesService.getDocumentTypes()(_: HeaderCarrier)).when(*)
            .returns(Future.successful(documentTypes))

          val request = FakeRequest(POST, documentTypeRoute())
            .withFormUrlEncodedBody(("document-type", documentTypeModel.code))

          val result = route(application, request).value

          status(result) mustEqual SEE_OTHER
          redirectLocation(result).value mustEqual onwardRoute.url
        }
      }

      "must redirect to the next page without changing the UserAnswers when the same answer is submitted" in new Setup(Some(
        emptyUserAnswers
          .set(DocumentTypePage(0), documentTypeOtherModel.code)
          .set(ReferenceAvailablePage(0), true)
          .set(DocumentReferencePage(0), "reference")
      )) {

        running(application) {

          (mockGetDocumentTypesService.getDocumentTypes()(_: HeaderCarrier)).when(*)
            .returns(Future.successful(documentTypes))

          val request = FakeRequest(POST, documentTypeRoute())
            .withFormUrlEncodedBody(("document-type", documentTypeOtherModel.code))

          val result = route(application, request).value

          status(result) mustEqual SEE_OTHER
          redirectLocation(result).value mustEqual onwardRoute.url
        }
      }

      "must redirect to the next page update the UserAnswers when the answer is changed" in new Setup(Some(
        emptyUserAnswers
          .set(DocumentTypePage(0), documentTypeOtherModel.code)
          .set(ReferenceAvailablePage(0), true)
          .set(DocumentReferencePage(0), "reference")
      )) {

        val expectedUserAnswers = emptyUserAnswers.set(DocumentTypePage(0), documentTypeModel.code)

        MockUserAnswersService.set(expectedUserAnswers).returns(Future.successful(expectedUserAnswers))

        running(application) {

          (mockGetDocumentTypesService.getDocumentTypes()(_: HeaderCarrier)).when(*)
            .returns(Future.successful(documentTypes))

          val request = FakeRequest(POST, documentTypeRoute())
            .withFormUrlEncodedBody(("document-type", documentTypeModel.code))

          val result = route(application, request).value

          status(result) mustEqual SEE_OTHER
          redirectLocation(result).value mustEqual onwardRoute.url
        }
      }

      "must return a Bad Request and errors when invalid data is submitted" in new Setup() {

        running(application) {
          (mockGetDocumentTypesService.getDocumentTypes()(_: HeaderCarrier)).when(*)
            .returns(Future.successful(documentTypes))

          val request = FakeRequest(POST, documentTypeRoute())
            .withFormUrlEncodedBody(("document-type", ""))

          val boundForm = form.bind(Map("document-type" -> ""))

          val result = route(application, request).value

          status(result) mustEqual BAD_REQUEST
          contentAsString(result) mustEqual view(
            form = boundForm,
            onSubmitCall = onSubmitCall(),
            documentTypes = documentTypes
          )(dataRequest(request), messages(application)).toString
        }
      }

      "must redirect to DocumentsIndexController when the idx is greater 0 and there are no current documents in UserAnswers" in new Setup() {

        running(application) {

          (mockGetDocumentTypesService.getDocumentTypes()(_: HeaderCarrier)).when(*)
            .returns(Future.successful(documentTypes))

          val request = FakeRequest(POST, documentTypeRoute(1))
            .withFormUrlEncodedBody(("document-type", documentTypeModel.code))

          val result = route(application, request).value

          status(result) mustEqual SEE_OTHER
          redirectLocation(result).value mustEqual routes.DocumentsIndexController.onPageLoad(testErn, testDraftId).url
        }
      }

      "must redirect to DocumentsIndexController when the idx is greater than the next valid document idx" in new Setup(Some(
        emptyUserAnswers
          .set(DocumentTypePage(0), documentTypeOtherModel.code)
          .set(ReferenceAvailablePage(0), true)
          .set(DocumentReferencePage(0), "reference")
      )) {

        running(application) {

          (mockGetDocumentTypesService.getDocumentTypes()(_: HeaderCarrier)).when(*)
            .returns(Future.successful(documentTypes))

          val request = FakeRequest(POST, documentTypeRoute(2))
            .withFormUrlEncodedBody(("document-type", documentTypeModel.code))

          val result = route(application, request).value

          status(result) mustEqual SEE_OTHER
          redirectLocation(result).value mustEqual routes.DocumentsIndexController.onPageLoad(testErn, testDraftId).url
        }
      }

      "must redirect to DocumentsIndexController when the idx is less than 0" in new Setup(Some(
        emptyUserAnswers
          .set(DocumentTypePage(0), documentTypeOtherModel.code)
          .set(ReferenceAvailablePage(0), true)
          .set(DocumentReferencePage(0), "reference")
      )) {

        running(application) {

          (mockGetDocumentTypesService.getDocumentTypes()(_: HeaderCarrier)).when(*)
            .returns(Future.successful(documentTypes))

          val request = FakeRequest(POST, documentTypeRoute(-1))
            .withFormUrlEncodedBody(("document-type", documentTypeModel.code))

          val result = route(application, request).value

          status(result) mustEqual SEE_OTHER
          redirectLocation(result).value mustEqual routes.DocumentsIndexController.onPageLoad(testErn, testDraftId).url
        }
      }

      "redirect to Journey Recovery if no existing data is found" in new Setup(None) {

        val testDocumentType = "testDocumentType"

        running(application) {
          val request =
            FakeRequest(POST, documentTypeRoute())
              .withFormUrlEncodedBody(("value", testDocumentType))

          val result = route(application, request).value

          status(result) mustEqual SEE_OTHER

          redirectLocation(result).value mustEqual controllers.routes.JourneyRecoveryController.onPageLoad().url
        }
      }
    }
  }
}