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
import forms.sections.items.ItemPackagingShippingMarksChoiceFormProvider
import mocks.services.MockUserAnswersService
import models.{NormalMode, UserAnswers}
import navigation.FakeNavigators.FakeItemsNavigator
import pages.sections.items.{ItemExciseProductCodePage, ItemPackagingQuantityPage, ItemPackagingShippingMarksChoicePage, ItemSelectPackagingPage}
import play.api.mvc.{AnyContentAsEmpty, Call}
import play.api.test.Helpers._
import play.api.test.{FakeRequest, Helpers}
import views.html.sections.items.ItemPackagingShippingMarksChoiceView

import scala.concurrent.Future

class ItemPackagingShippingMarksChoiceControllerSpec extends SpecBase with MockUserAnswersService with ItemFixtures {

  lazy val formProvider = new ItemPackagingShippingMarksChoiceFormProvider()
  lazy val form = formProvider()
  lazy val view = app.injector.instanceOf[ItemPackagingShippingMarksChoiceView]

  val baseUserAnswers: UserAnswers = emptyUserAnswers
    .set(ItemExciseProductCodePage(testIndex1), testEpcWine)
    .set(ItemPackagingQuantityPage(testIndex1, testPackagingIndex1), "2")
    .set(ItemSelectPackagingPage(testIndex1, testPackagingIndex1), testPackageBag)

  val submitCall: Call = controllers.sections.items.routes.ItemPackagingShippingMarksChoiceController.onSubmit(testErn, testDraftId, testIndex1, testPackagingIndex1, NormalMode)

  class Test(val userAnswers: Option[UserAnswers] = Some(baseUserAnswers)) {
    lazy val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest()

    lazy val controller = new ItemPackagingShippingMarksChoiceController(
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

  "ItemPackagingShippingMarksChoice Controller" - {

    "must redirect to Index of section when the item packaging does not exist for a GET" in new Test(Some(
      emptyUserAnswers
        .set(ItemExciseProductCodePage(testIndex1), testEpcWine)
    )) {
      val result = controller.onPageLoad(testErn, testDraftId, testIndex1, testPackagingIndex2, NormalMode)(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustBe routes.ItemsPackagingIndexController.onPageLoad(testErn, testDraftId, testIndex1).url
    }

    "must redirect to Index of section when the packaging idx does not exist for a POST" in new Test(Some(
      emptyUserAnswers
        .set(ItemExciseProductCodePage(testIndex1), testEpcWine)
    )) {
      val result = controller.onSubmit(testErn, testDraftId, testIndex1, testPackagingIndex2, NormalMode)(request.withFormUrlEncodedBody(("value", "true")))

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustBe routes.ItemsPackagingIndexController.onPageLoad(testErn, testDraftId, testIndex1).url
    }

    "must redirect to Index of section when the packaging quantity does not exist for a GET" in new Test(Some(
      emptyUserAnswers
        .set(ItemExciseProductCodePage(testIndex1), testEpcWine)
        .set(ItemSelectPackagingPage(testIndex1, testPackagingIndex1), testPackageBag)
    )) {
      val result = controller.onPageLoad(testErn, testDraftId, testIndex1, testPackagingIndex2, NormalMode)(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustBe routes.ItemsPackagingIndexController.onPageLoad(testErn, testDraftId, testIndex1).url
    }

    "must redirect to Index of section when the packaging quantity does not exist for a POST" in new Test(Some(
      emptyUserAnswers
        .set(ItemExciseProductCodePage(testIndex1), testEpcWine)
        .set(ItemSelectPackagingPage(testIndex1, testPackagingIndex1), testPackageBag)
    )) {
      val result = controller.onSubmit(testErn, testDraftId, testIndex1, testPackagingIndex2, NormalMode)(request.withFormUrlEncodedBody(("value", "true")))

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustBe routes.ItemsPackagingIndexController.onPageLoad(testErn, testDraftId, testIndex1).url
    }

    "must redirect to Index of section when the item idx is outside of bounds for a GET (which redirects to Items index)" in new Test(Some(emptyUserAnswers)) {
      val result = controller.onPageLoad(testErn, testDraftId, testIndex2, testPackagingIndex1, NormalMode)(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustBe routes.ItemsPackagingIndexController.onPageLoad(testErn, testDraftId, testIndex2).url
    }

    "must redirect to Index of section when the item idx is outside of bounds for a POST (which redirects to Items index)" in new Test(Some(emptyUserAnswers)) {
      val result = controller.onSubmit(testErn, testDraftId, testIndex2, testPackagingIndex1, NormalMode)(request.withFormUrlEncodedBody(("value", "true")))

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustBe routes.ItemsPackagingIndexController.onPageLoad(testErn, testDraftId, testIndex2).url
    }

    "must return OK and the correct view for a GET" in new Test {
      val result = controller.onPageLoad(testErn, testDraftId, testIndex1, testPackagingIndex1, NormalMode)(request)

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(
        form,
        testIndex1,
        testPackagingIndex1,
        testPackageBag.description,
        packagingQuantity = "2",
        submitCall,
        NormalMode
      )(dataRequest(request, userAnswers.get), messages(request)).toString
    }

    "must populate the view correctly on a GET when the question has previously been answered" in new Test(Some(
      baseUserAnswers.set(ItemPackagingShippingMarksChoicePage(testIndex1, testPackagingIndex1), true)
    )) {
      val result = controller.onPageLoad(testErn, testDraftId, testIndex1, testPackagingIndex1, NormalMode)(request)

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(
        form.fill(true),
        testIndex1,
        testPackagingIndex1,
        testPackageBag.description,
        packagingQuantity = "2",
        submitCall,
        NormalMode
      )(dataRequest(request, userAnswers.get), messages(request)).toString
    }

    "must redirect to the next page when valid data is submitted" in new Test {

      val expectedUserAnswers = baseUserAnswers.set(ItemPackagingShippingMarksChoicePage(testIndex1, testPackagingIndex1), true)

      MockUserAnswersService.set(expectedUserAnswers).returns(Future.successful(expectedUserAnswers))

      val result = controller.onSubmit(testErn, testDraftId, testIndex1, testPackagingIndex1, NormalMode)(request.withFormUrlEncodedBody(("value", "true")))

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual testOnwardRoute.url
    }

    "must return a Bad Request and errors when invalid data is submitted" in new Test {
      val boundForm = form.bind(Map("value" -> ""))

      val result = controller.onSubmit(testErn, testDraftId, testIndex1, testPackagingIndex1, NormalMode)(request.withFormUrlEncodedBody(("value", "")))

      status(result) mustEqual BAD_REQUEST
      contentAsString(result) mustEqual view(
        boundForm,
        testIndex1,
        testPackagingIndex1,
        testPackageBag.description,
        packagingQuantity = "2",
        submitCall,
        NormalMode
      )(dataRequest(request, userAnswers.get), messages(request)).toString
    }

    "must redirect to Journey Recovery for a GET if no existing data is found" in new Test(None) {
      val result = controller.onPageLoad(testErn, testDraftId, testIndex1, testPackagingIndex1, NormalMode)(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.JourneyRecoveryController.onPageLoad().url
    }

    "must redirect to Journey Recovery for a POST if no existing data is found" in new Test(None) {
      val result = controller.onSubmit(testErn, testDraftId, testIndex1, testPackagingIndex1, NormalMode)(request.withFormUrlEncodedBody(("value", "true")))

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.JourneyRecoveryController.onPageLoad().url
    }
  }
}
