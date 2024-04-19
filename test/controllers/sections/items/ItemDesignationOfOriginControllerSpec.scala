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

package controllers.sections.items

import base.SpecBase
import controllers.actions.FakeDataRetrievalAction
import fixtures.ItemFixtures
import forms.sections.items.ItemDesignationOfOriginFormProvider
import mocks.services.MockUserAnswersService
import models.{NormalMode, UserAnswers}
import navigation.FakeNavigators.FakeItemsNavigator
import play.api.mvc.{AnyContentAsEmpty, Call}
import play.api.test.{FakeRequest, Helpers}
import views.html.sections.items.ItemDesignationOfOriginView

class ItemDesignationOfOriginControllerSpec extends SpecBase with MockUserAnswersService with ItemFixtures {

  lazy val formProvider = new ItemDesignationOfOriginFormProvider()

  lazy val view: ItemDesignationOfOriginView = app.injector.instanceOf[ItemDesignationOfOriginView]

  val submitCall: Call = controllers.sections.items.routes.ItemDesignationOfOriginController.onSubmit(testErn, testDraftId, testIndex1, NormalMode)

  class Test(val userAnswers: Option[UserAnswers], epc: String = "W200") {

    lazy val form = formProvider(epc)

    lazy val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest()

    lazy val controller = new ItemDesignationOfOriginController(
      messagesApi,
      mockUserAnswersService,
      fakeBetaAllowListAction,
      new FakeItemsNavigator(testOnwardRoute),
      fakeAuthAction,
      new FakeDataRetrievalAction(userAnswers, Some(testMinTraderKnownFacts)),
      dataRequiredAction,
      formProvider,
      Helpers.stubMessagesControllerComponents(),
      view
    )
  }
  //TODO: ETFE-3703
//  "ItemDesignationOfOrigin Controller" - {
//
//    "must return OK and the correct view for a GET" in new Test(Some(emptyUserAnswers)) {
//      val result = controller.onPageLoad(testErn, testDraftId, testIndex1, NormalMode)(request)
//
//      status(result) mustEqual OK
//      contentAsString(result) mustEqual view(form, submitCall, testEpcWine, testIndex1)(dataRequest(request, userAnswers.get), messages).toString
//    }
//
//    "must populate the view correctly on a GET when the question has previously been answered" in new Test(Some(
//      emptyUserAnswers.set(ItemDesignationOfOriginPage(testIndex1), ItemDesignationOfOrigin.values.head)
//    )) {
//      val result = controller.onPageLoad(testErn, testDraftId, testIndex1, NormalMode)(request)
//
//      status(result) mustEqual OK
//      contentAsString(result) mustEqual view(form.fill(ItemDesignationOfOrigin.values.head), NormalMode)(dataRequest(request, userAnswers.get), messages).toString
//    }
//
//    "must redirect to the next page when valid data is submitted" in new Test(Some(emptyUserAnswers)) {
//
//      MockUserAnswersService.set().returns(Future.successful(emptyUserAnswers))
//
//      val result = controller.onSubmit(testErn, testDraftId, testIndex1, NormalMode)(request.withFormUrlEncodedBody(("value", ItemDesignationOfOrigin.values.head.toString)))
//
//      status(result) mustEqual SEE_OTHER
//      redirectLocation(result).value mustEqual testOnwardRoute.url
//    }
//
//    "must return a Bad Request and errors when invalid data is submitted" in new Test(Some(emptyUserAnswers)) {
//      val boundForm = form.bind(Map("value" -> ""))
//
//      val result = controller.onSubmit(testErn, testDraftId, testIndex1, NormalMode)(request.withFormUrlEncodedBody(("value", "")))
//
//      status(result) mustEqual BAD_REQUEST
//      contentAsString(result) mustEqual view(boundForm, NormalMode)(dataRequest(request, userAnswers.get), messages).toString
//    }
//
//    "must redirect to Journey Recovery for a GET if no existing data is found" in new Test(None) {
//      val result = controller.onPageLoad(testErn, testDraftId, testIndex1, NormalMode)(request)
//
//      status(result) mustEqual SEE_OTHER
//      redirectLocation(result).value mustEqual controllers.routes.JourneyRecoveryController.onPageLoad().url
//    }
//
//    "must redirect to Journey Recovery for a POST if no existing data is found" in new Test(None) {
//      val result = controller.onSubmit(testErn, testDraftId, testIndex1, NormalMode)(request.withFormUrlEncodedBody(("value", ItemDesignationOfOrigin.values.head.toString)))
//
//      status(result) mustEqual SEE_OTHER
//      redirectLocation(result).value mustEqual controllers.routes.JourneyRecoveryController.onPageLoad().url
//    }
//  }
}
