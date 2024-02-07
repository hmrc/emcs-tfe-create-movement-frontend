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
import controllers.actions.FakeDataRetrievalAction
import fixtures.DocumentTypeFixtures
import forms.sections.documents.ReferenceAvailableFormProvider
import mocks.services.MockUserAnswersService
import models.{Index, NormalMode, UserAnswers}
import navigation.FakeNavigators.FakeDocumentsNavigator
import pages.sections.documents._
import play.api.data.Form
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.sections.documents.ReferenceAvailableView

import scala.concurrent.Future

class ReferenceAvailableControllerSpec extends SpecBase with MockUserAnswersService with DocumentTypeFixtures {

  lazy val formProvider: ReferenceAvailableFormProvider = new ReferenceAvailableFormProvider()
  lazy val form: Form[Boolean] = formProvider()
  lazy val view: ReferenceAvailableView = app.injector.instanceOf[ReferenceAvailableView]

  def referenceAvailableRoute(idx: Index): String =
    routes.ReferenceAvailableController.onPageLoad(testErn, testDraftId, idx, NormalMode).url

  def onSubmitCall(idx: Index): Call =
    routes.ReferenceAvailableController.onSubmit(testErn, testDraftId, idx, NormalMode)

  class Setup(startingUserAnswers: Option[UserAnswers] = Some(emptyUserAnswers)) {
    val request = FakeRequest(GET, referenceAvailableRoute(0))

    lazy val testController = new ReferenceAvailableController(
      messagesApi,
      mockUserAnswersService,
      fakeBetaAllowListAction,
      new FakeDocumentsNavigator(testOnwardRoute),
      fakeAuthAction,
      new FakeDataRetrievalAction(startingUserAnswers, Some(testMinTraderKnownFacts)),
      dataRequiredAction,
      formProvider,
      messagesControllerComponents,
      view
    )

  }


  "ReferenceAvailable Controller" - {
    "GET onPageLoad" - {
      "must return OK and the correct view" in new Setup(
        Some(emptyUserAnswers.set(DocumentTypePage(0), documentTypeOtherModel))) {

        val result = testController.onPageLoad(testErn, testDraftId, 0, NormalMode)(request)

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(
          form = form,
          onSubmitCall = onSubmitCall(0)
        )(dataRequest(request), messages(request)).toString
      }

      "must populate the view correctly when the question has previously been answered" in new Setup(
        Some(emptyUserAnswers
          .set(ReferenceAvailablePage(0), true)
          .set(DocumentTypePage(0), documentTypeOtherModel))) {

        val result = testController.onPageLoad(testErn, testDraftId, 0, NormalMode)(request)

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(
          form = form.fill(true),
          onSubmitCall = onSubmitCall(0)
        )(dataRequest(request), messages(request)).toString
      }

      "must redirect to DocumentsIndexController when there are no current documents in UserAnswers" in new Setup() {
        val req = FakeRequest(GET, referenceAvailableRoute(1))

        val result = testController.onPageLoad(testErn, testDraftId, 1, NormalMode)(req)

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.DocumentsIndexController.onPageLoad(testErn, testDraftId).url
      }

      "must redirect to DocumentsIndexController when the idx is greater than the next valid document idx" in new Setup(
        Some(emptyUserAnswers.set(DocumentTypePage(0), documentTypeOtherModel))) {

        val req = FakeRequest(GET, referenceAvailableRoute(1))

        val result = testController.onPageLoad(testErn, testDraftId, 1, NormalMode)(req)

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.DocumentsIndexController.onPageLoad(testErn, testDraftId).url
      }

      "must redirect to DocumentsIndexController when the idx is less than 0" in new Setup(
        Some(emptyUserAnswers.set(DocumentTypePage(0), documentTypeOtherModel))) {

        val req = FakeRequest(GET, referenceAvailableRoute(-1))

        val result = testController.onPageLoad(testErn, testDraftId, -1, NormalMode)(req)

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.DocumentsIndexController.onPageLoad(testErn, testDraftId).url
      }

      "must redirect to Journey Recovery if no existing data is found" in new Setup(None) {
        val result = testController.onPageLoad(testErn, testDraftId, 0, NormalMode)(request)

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual controllers.routes.JourneyRecoveryController.onPageLoad().url
      }
    }

    "POST onSubmit" - {
      "must redirect to the next page valid answer is submitted" in new Setup(
        Some(emptyUserAnswers.set(DocumentTypePage(0), documentTypeModel))) {

        val expectedAnswers = emptyUserAnswers
          .set(DocumentTypePage(0), documentTypeModel)
          .set(ReferenceAvailablePage(0), true)

        MockUserAnswersService.set(expectedAnswers).returns(Future.successful(expectedAnswers))

        val req = FakeRequest(POST, referenceAvailableRoute(0)).withFormUrlEncodedBody(("value", "true"))

        val result = testController.onSubmit(testErn, testDraftId, 0, NormalMode)(req)

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual testOnwardRoute.url
      }

      "must redirect to the next page and not update answers the same answer is submitted again" in new Setup(Some(
        emptyUserAnswers
          .set(DocumentTypePage(0), documentTypeOtherModel)
          .set(ReferenceAvailablePage(0), false)
          .set(DocumentDescriptionPage(0), "description")
      )) {

        val req = FakeRequest(POST, referenceAvailableRoute(0)).withFormUrlEncodedBody(("value", "false"))

        val result = testController.onSubmit(testErn, testDraftId, 0, NormalMode)(req)

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual testOnwardRoute.url
      }

      "must redirect to the next page and wipe UserAnswers when new answer is submitted" in new Setup(Some(
        emptyUserAnswers
          .set(ReferenceAvailablePage(0), true)
          .set(DocumentReferencePage(0), "reference")
          .set(DocumentDescriptionPage(0), "description")
      )) {

        val expectedAnswers = emptyUserAnswers.set(ReferenceAvailablePage(0), false)

        MockUserAnswersService.set(expectedAnswers).returns(Future.successful(expectedAnswers))

        val req = FakeRequest(POST, referenceAvailableRoute(0)).withFormUrlEncodedBody(("value", "false"))

        val result = testController.onSubmit(testErn, testDraftId, 0, NormalMode)(req)

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual testOnwardRoute.url
      }

      "must return a Bad Request and errors when invalid data is submitted" in new Setup(
        Some(emptyUserAnswers.set(DocumentTypePage(0), documentTypeOtherModel))) {

        val req = FakeRequest(POST, referenceAvailableRoute(0)).withFormUrlEncodedBody(("value", ""))

        val boundForm = form.bind(Map("value" -> ""))

        val result = testController.onSubmit(testErn, testDraftId, 0, NormalMode)(req)

        status(result) mustEqual BAD_REQUEST
        contentAsString(result) mustEqual view(
          form = boundForm,
          onSubmitCall = onSubmitCall(0)
        )(dataRequest(request), messages(request)).toString
      }

      "must redirect to DocumentsIndexController when there are no current documents in UserAnswers" in new Setup() {
        val req = FakeRequest(POST, referenceAvailableRoute(0)).withFormUrlEncodedBody(("value", "true"))

        val result = testController.onSubmit(testErn, testDraftId, 0, NormalMode)(req)

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.DocumentsIndexController.onPageLoad(testErn, testDraftId).url
      }

      "must redirect to DocumentsIndexController when the idx is greater than the next valid document idx" in new Setup(
        Some(emptyUserAnswers.set(DocumentTypePage(0), documentTypeOtherModel))) {

        val req = FakeRequest(POST, referenceAvailableRoute(1)).withFormUrlEncodedBody(("value", "true"))

        val result = testController.onSubmit(testErn, testDraftId, 1, NormalMode)(req)

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.DocumentsIndexController.onPageLoad(testErn, testDraftId).url
      }

      "must redirect to DocumentsIndexController when the idx is less than 0" in new Setup(
        Some(emptyUserAnswers.set(DocumentTypePage(0), documentTypeOtherModel))) {
        val req = FakeRequest(POST, referenceAvailableRoute(-1)).withFormUrlEncodedBody(("value", "true"))

        val result = testController.onSubmit(testErn, testDraftId, -1, NormalMode)(req)

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.DocumentsIndexController.onPageLoad(testErn, testDraftId).url
      }

      "must redirect to Journey Recovery if no existing data is found" in new Setup(None) {
        val req = FakeRequest(POST, referenceAvailableRoute(0)).withFormUrlEncodedBody(("value", "true"))

        val result = testController.onSubmit(testErn, testDraftId, 0, NormalMode)(req)

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual controllers.routes.JourneyRecoveryController.onPageLoad().url
      }
    }
  }
}
