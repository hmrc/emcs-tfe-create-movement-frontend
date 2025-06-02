/*
 * Copyright 2024 HM Revenue & Customs
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

package controllers

import base.SpecBase
import config.Constants.TFE_DELETED_DRAFT_LRN
import controllers.actions.FakeDataRetrievalAction
import forms.DeleteDraftMovementFormProvider
import mocks.services.{MockDeleteDraftMovementService, MockUserAnswersService}
import models.UserAnswers
import navigation.FakeNavigators.FakeNavigator
import pages.sections.info.LocalReferenceNumberPage
import play.api.data.Form
import play.api.mvc.{AnyContentAsEmpty, Call, Result}
import play.api.test.Helpers._
import play.api.test.{FakeRequest, Helpers}
import views.html.DeleteDraftMovementView

import scala.concurrent.Future

class DeleteDraftMovementControllerSpec extends SpecBase with MockUserAnswersService with MockDeleteDraftMovementService {

  lazy val formProvider = new DeleteDraftMovementFormProvider()
  lazy val form: Form[Boolean] = formProvider()
  lazy val view: DeleteDraftMovementView = app.injector.instanceOf[DeleteDraftMovementView]

  val submitCall: Call = controllers.routes.DeleteDraftMovementController.onSubmit(testErn, testDraftId)

  val baseUserAnswers: UserAnswers = emptyUserAnswers.set(LocalReferenceNumberPage(isOnPreDraftFlow = false), testLrn)

  class Test(val userAnswers: Option[UserAnswers]) {
    lazy val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest()

    lazy val controller = new DeleteDraftMovementController(
      messagesApi,
      mockUserAnswersService,
      new FakeNavigator(testOnwardRoute),
      fakeAuthAction,
      new FakeDataRetrievalAction(userAnswers, Some(testMinTraderKnownFacts)),
      dataRequiredAction,
      formProvider,
      mockDeleteDraftMovementService,
      Helpers.stubMessagesControllerComponents(),
      view,
      appConfig,
      errorHandler
    )
  }

  "DeleteDraftMovement Controller" - {

    "must return OK and the correct view for a GET" in new Test(Some(baseUserAnswers)) {
      val result: Future[Result] = controller.onPageLoad(testErn, testDraftId)(request)

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(form, submitCall)(dataRequest(request, userAnswers.get), messages(request)).toString
    }

    "must redirect to drafts when yes is selected (and the deletion is successful)" in new Test(Some(baseUserAnswers)) {

      MockDeleteDraftMovementService.deleteDraft().returns(Future.successful(true))

      val result: Future[Result] = controller.onSubmit(testErn, testDraftId)(request.withFormUrlEncodedBody("value" -> "true"))

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual appConfig.emcsTfeDraftsUrl(testErn)
      flash(result).get(TFE_DELETED_DRAFT_LRN).get mustBe testLrn
    }

    "must redirect to task list when no is selected" in new Test(Some(baseUserAnswers)) {

      val result: Future[Result] = controller.onSubmit(testErn, testDraftId)(request.withFormUrlEncodedBody("value" -> "false"))

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.DraftMovementController.onPageLoad(testErn, testDraftId).url
    }

    "must throw an exception when the local reference number of the draft is missing" in new Test(Some(emptyUserAnswers)) {

      val result: Future[Result] = controller.onSubmit(testErn, testDraftId)(request.withFormUrlEncodedBody("value" -> "true"))

      status(result) mustEqual INTERNAL_SERVER_ERROR
    }

    "must return a Bad Request and errors when invalid data is submitted" in new Test(Some(baseUserAnswers)) {
      val boundForm: Form[Boolean] = form.bind(Map("value" -> ""))

      val result: Future[Result] = controller.onSubmit(testErn, testDraftId)(request.withFormUrlEncodedBody("value" -> ""))

      status(result) mustEqual BAD_REQUEST
      contentAsString(result) mustEqual view(boundForm, submitCall)(dataRequest(request, userAnswers.get), messages(request)).toString
    }

    "must redirect to Journey Recovery for a GET if no existing data is found" in new Test(None) {
      val result: Future[Result] = controller.onPageLoad(testErn, testDraftId)(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.JourneyRecoveryController.onPageLoad().url
    }

    "must redirect to Journey Recovery for a POST if no existing data is found" in new Test(None) {
      val result: Future[Result] = controller.onSubmit(testErn, testDraftId)(request.withFormUrlEncodedBody("value" -> "true"))

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.JourneyRecoveryController.onPageLoad().url
    }
  }
}
