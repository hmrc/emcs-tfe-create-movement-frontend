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
import pages.sections.documents.{DocumentReferencePage, DocumentTypePage, DocumentsSection, ReferenceAvailablePage}
import play.api.inject.bind
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.{GetDocumentTypesService, UserAnswersService}
import viewmodels.helpers.SelectItemHelper
import views.html.sections.documents.DocumentTypeView

import scala.concurrent.Future

class DocumentTypeControllerSpec extends SpecBase with MockUserAnswersService with DocumentTypeFixtures with MockGetDocumentTypesService {

  class Setup(userAnswers: Option[UserAnswers] = Some(emptyUserAnswers)) {

    def onwardRoute = Call("GET", "/foo")

    val application = applicationBuilder(userAnswers = userAnswers)
      .overrides(
        bind[DocumentsNavigator].toInstance(new FakeDocumentsNavigator(onwardRoute)),
        bind[UserAnswersService].toInstance(mockUserAnswersService),
        bind[GetDocumentTypesService].to(mockGetDocumentTypesService),
      )
      .build()

    implicit val msg = messages(application)

    def documentTypeRoute(idx: Index = 0): String =
      routes.DocumentTypeController.onPageLoad(testErn, testDraftId, idx, NormalMode).url

    def onSubmitCall(idx: Index = 0) =
      routes.DocumentTypeController.onSubmit(testErn, testDraftId, idx, NormalMode)

    val documentTypes = Seq(documentTypeModel, documentTypeOtherModel)

    val formProvider = new DocumentTypeFormProvider()
    val form = formProvider(documentTypes)

    val view = application.injector.instanceOf[DocumentTypeView]

    val sampleDocumentTypesSelectOptions = SelectItemHelper.constructSelectItems(
      selectOptions = documentTypes,
      defaultTextMessageKey = "documentType.select.defaultValue"
    )
  }

  "DocumentType Controller" - {

    "GET onPageLoad" - {

      "must return OK and the correct view" in new Setup() {

        running(application) {

          MockGetDocumentTypesService.getDocumentTypes()
            .returns(Future.successful(documentTypes))

          val request = FakeRequest(GET, documentTypeRoute())

          val result = route(application, request).value

          status(result) mustEqual OK
          contentAsString(result) mustEqual view(
            form = form,
            onSubmitCall = onSubmitCall(),
            selectOptions = sampleDocumentTypesSelectOptions
          )(dataRequest(request), msg).toString
        }
      }

      "must populate the view correctly when the question has previously been answered" in new Setup(Some(
        emptyUserAnswers.set(DocumentTypePage(0), documentTypeModel)
      )) {

        running(application) {

          MockGetDocumentTypesService.getDocumentTypes()
            .returns(Future.successful(documentTypes))

          val sampleDocumentTypesSelectOptionsWithPreFilledSelection = SelectItemHelper.constructSelectItems(
            selectOptions = documentTypes,
            defaultTextMessageKey = "documentType.select.defaultValue",
            existingAnswer = Some(documentTypeModel.code)
          )

          val request = FakeRequest(GET, documentTypeRoute())

          val result = route(application, request).value

          status(result) mustEqual OK
          contentAsString(result) mustEqual view(
            form = form.fill(documentTypeModel),
            onSubmitCall = onSubmitCall(),
            selectOptions = sampleDocumentTypesSelectOptionsWithPreFilledSelection
          )(dataRequest(request), msg).toString
        }
      }

      "must redirect to DocumentsIndexController when the idx is greater 0 and there are no current documents in UserAnswers" in new Setup() {

        running(application) {

          val request = FakeRequest(POST, documentTypeRoute(1))
            .withFormUrlEncodedBody(("document-type", documentTypeModel.code))

          val result = route(application, request).value

          status(result) mustEqual SEE_OTHER
          redirectLocation(result).value mustEqual routes.DocumentsIndexController.onPageLoad(testErn, testDraftId).url
        }
      }

      "must redirect to DocumentsIndexController when the idx is greater than the next document to valid document idx" in new Setup(Some(
        emptyUserAnswers
          .set(DocumentTypePage(0), documentTypeOtherModel)
          .set(ReferenceAvailablePage(0), true)
          .set(DocumentReferencePage(0), "reference")
      )) {

        running(application) {

          val request = FakeRequest(POST, documentTypeRoute(2))
            .withFormUrlEncodedBody(("document-type", documentTypeModel.code))

          val result = route(application, request).value

          status(result) mustEqual SEE_OTHER
          redirectLocation(result).value mustEqual routes.DocumentsIndexController.onPageLoad(testErn, testDraftId).url
        }
      }

      "must redirect to DocumentsIndexController when the idx is less than 0" in new Setup(Some(
        emptyUserAnswers
          .set(DocumentTypePage(0), documentTypeOtherModel)
          .set(ReferenceAvailablePage(0), true)
          .set(DocumentReferencePage(0), "reference")
      )) {

        running(application) {

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

          MockGetDocumentTypesService.getDocumentTypes()
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
          .set(DocumentTypePage(0), documentTypeOtherModel)
          .set(ReferenceAvailablePage(0), true)
          .set(DocumentReferencePage(0), "reference")
      )) {

        running(application) {

          MockGetDocumentTypesService.getDocumentTypes()
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
          .set(DocumentTypePage(0), documentTypeOtherModel)
          .set(ReferenceAvailablePage(0), true)
          .set(DocumentReferencePage(0), "reference")
      )) {

        val expectedUserAnswers = emptyUserAnswers.set(DocumentTypePage(0), documentTypeModel)

        MockUserAnswersService.set(expectedUserAnswers).returns(Future.successful(expectedUserAnswers))

        running(application) {

          MockGetDocumentTypesService.getDocumentTypes()
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

          MockGetDocumentTypesService.getDocumentTypes()
            .returns(Future.successful(documentTypes))

          val request = FakeRequest(POST, documentTypeRoute())
            .withFormUrlEncodedBody(("document-type", ""))

          val boundForm = form.bind(Map("document-type" -> ""))

          val result = route(application, request).value

          status(result) mustEqual BAD_REQUEST
          contentAsString(result) mustEqual view(
            form = boundForm,
            onSubmitCall = onSubmitCall(),
            selectOptions = sampleDocumentTypesSelectOptions
          )(dataRequest(request), msg).toString
        }
      }

      "must redirect to DocumentsIndexController when the idx is greater 0 and there are no current documents in UserAnswers" in new Setup() {

        running(application) {

          val request = FakeRequest(POST, documentTypeRoute(1))
            .withFormUrlEncodedBody(("document-type", documentTypeModel.code))

          val result = route(application, request).value

          status(result) mustEqual SEE_OTHER
          redirectLocation(result).value mustEqual routes.DocumentsIndexController.onPageLoad(testErn, testDraftId).url
        }
      }

      "must redirect to DocumentsIndexController when the idx is greater than the next valid document idx" in new Setup(Some(
        emptyUserAnswers
          .set(DocumentTypePage(0), documentTypeOtherModel)
          .set(ReferenceAvailablePage(0), true)
          .set(DocumentReferencePage(0), "reference")
      )) {

        running(application) {

          val request = FakeRequest(POST, documentTypeRoute(2))
            .withFormUrlEncodedBody(("document-type", documentTypeModel.code))

          val result = route(application, request).value

          status(result) mustEqual SEE_OTHER
          redirectLocation(result).value mustEqual routes.DocumentsIndexController.onPageLoad(testErn, testDraftId).url
        }
      }

      "must redirect to DocumentsIndexController when the idx is greater than the MAX index for documents" in new Setup(Some(
        emptyUserAnswers
          .set(DocumentTypePage(0), documentTypeModel).set(DocumentReferencePage(0), "reference")
          .set(DocumentTypePage(1), documentTypeModel).set(DocumentReferencePage(1), "reference")
          .set(DocumentTypePage(2), documentTypeModel).set(DocumentReferencePage(2), "reference")
          .set(DocumentTypePage(3), documentTypeModel).set(DocumentReferencePage(3), "reference")
          .set(DocumentTypePage(4), documentTypeModel).set(DocumentReferencePage(4), "reference")
          .set(DocumentTypePage(5), documentTypeModel).set(DocumentReferencePage(5), "reference")
          .set(DocumentTypePage(6), documentTypeModel).set(DocumentReferencePage(6), "reference")
          .set(DocumentTypePage(7), documentTypeModel).set(DocumentReferencePage(7), "reference")
          .set(DocumentTypePage(8), documentTypeModel).set(DocumentReferencePage(8), "reference")
      )) {

        running(application) {

          val request = FakeRequest(POST, documentTypeRoute(DocumentsSection.MAX + 1))
            .withFormUrlEncodedBody(("document-type", documentTypeModel.code))

          val result = route(application, request).value

          status(result) mustEqual SEE_OTHER
          redirectLocation(result).value mustEqual routes.DocumentsIndexController.onPageLoad(testErn, testDraftId).url
        }
      }

      "must redirect to DocumentsIndexController when the idx is less than 0" in new Setup(Some(
        emptyUserAnswers
          .set(DocumentTypePage(0), documentTypeOtherModel)
          .set(ReferenceAvailablePage(0), true)
          .set(DocumentReferencePage(0), "reference")
      )) {

        running(application) {

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