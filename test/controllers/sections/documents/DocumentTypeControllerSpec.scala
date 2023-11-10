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
import models.{NormalMode, UserAnswers}
import models.sections.documents.DocumentType
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


    lazy val documentTypeRoute = routes.DocumentTypeController.onPageLoad(testErn, testDraftId, 0, NormalMode).url
    lazy val onSubmitCall = routes.DocumentTypeController.onSubmit(testErn, testDraftId, 0, NormalMode)

    val formProvider = new DocumentTypeFormProvider()
    val form = formProvider()

    val view = application.injector.instanceOf[DocumentTypeView]

//    val sampleDocumentTypesSelectOptions = SelectItemHelper.constructSelectItems(
//      selectOptions = Seq(testDocumentType),
//      defaultTextMessageKey = "documentType.select.defaultValue")(messages(application))
  }

  "DocumentType Controller" - {

    "must return OK and the correct view for a GET" in new Setup() {

      running(application) {

        (mockGetDocumentTypesService.getDocumentTypes()(_: HeaderCarrier)).when(*).returns(Future.successful(Seq(documentTypeModel)))

        val request = FakeRequest(GET, documentTypeRoute)

        val result = route(application, request).value

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(
          form = form,
          onSubmitCall = onSubmitCall,
          documentTypes = Seq(documentTypeModel)
        )(dataRequest(request), messages(application)).toString
      }
    }

    "must populate the view correctly on a GET when the question has previously been answered" in new Setup(Some(
      emptyUserAnswers.set(DocumentTypePage(0), documentTypeModel.code)
    )) {

      running(application) {

        val sampleDocumentTypesSelectOptionsWithPreFilledSelection = SelectItemHelper.constructSelectItems(
          selectOptions = Seq(documentTypeModel),
          defaultTextMessageKey = "documentType.select.defaultValue",
          existingAnswer = Some(testDocumentType.code))(messages(application))

        val request = FakeRequest(GET, documentTypeRoute)

        val result = route(application, request).value

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(
          form = form.fill(documentTypeModel.code),
          onSubmitCall = onSubmitCall,
          documentTypes = Seq(documentTypeModel)
        )(dataRequest(request), messages(application)).toString
      }
    }

    "must redirect to the next page when valid data is submitted" in new Setup() {

      MockUserAnswersService.set().returns(Future.successful(emptyUserAnswers))

      running(application) {

        (mockGetDocumentTypesService.getDocumentTypes()(_: HeaderCarrier)).when(*).returns(Future.successful(Seq(documentTypeModel)))

        val request = FakeRequest(POST, documentTypeRoute)
          .withFormUrlEncodedBody(("document-type", documentTypeModel.code))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual onwardRoute.url
      }
    }

    "must return a Bad Request and errors when invalid data is submitted" in new Setup() {

      running(application) {
        (mockGetDocumentTypesService.getDocumentTypes()(_: HeaderCarrier)).when(*).returns(Future.successful(Seq(documentTypeModel)))

        val request = FakeRequest(POST, documentTypeRoute)
          .withFormUrlEncodedBody(("document-type", ""))

        val boundForm = form.bind(Map("document-type" -> ""))

        val result = route(application, request).value

        status(result) mustEqual BAD_REQUEST
        contentAsString(result) mustEqual view(
          form = boundForm,
          onSubmitCall = onSubmitCall,
          documentTypes = Seq(documentTypeModel)
        )(dataRequest(request), messages(application)).toString
      }
    }

    "must redirect to Journey Recovery for a GET if no existing data is found" in new Setup(None) {

      running(application) {

        val request = FakeRequest(GET, documentTypeRoute)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual controllers.routes.JourneyRecoveryController.onPageLoad().url
      }
    }

    "redirect to Journey Recovery for a POST if no existing data is found" in new Setup(None) {

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
