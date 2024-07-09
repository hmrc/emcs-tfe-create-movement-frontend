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

package controllers.sections.items

import base.SpecBase
import controllers.actions.FakeDataRetrievalAction
import fixtures.{ItemFixtures, MovementSubmissionFailureFixtures}
import forms.sections.items.ItemRemoveItemFormProvider
import mocks.services.MockUserAnswersService
import models.UserAnswers
import navigation.FakeNavigators.FakeItemsNavigator
import pages.sections.items.{ItemExciseProductCodePage, ItemPackagingQuantityPage, ItemPackagingShippingMarksPage}
import play.api.mvc.{AnyContentAsEmpty, Call}
import play.api.test.Helpers._
import play.api.test.{FakeRequest, Helpers}
import views.html.sections.items.ItemRemoveItemView

import scala.concurrent.Future

class ItemRemoveItemControllerSpec extends SpecBase with MockUserAnswersService with ItemFixtures with MovementSubmissionFailureFixtures {

  lazy val formProvider = new ItemRemoveItemFormProvider()
  lazy val form = formProvider()
  lazy val view = app.injector.instanceOf[ItemRemoveItemView]

  val baseUserAnswers: UserAnswers = emptyUserAnswers
    .set(ItemExciseProductCodePage(testIndex1), testEpcTobacco)
    .set(ItemExciseProductCodePage(testIndex2), testEpcWine)

  val action: Call = routes.ItemRemoveItemController.onSubmit(testErn, testDraftId, testIndex1)

  class Test(val userAnswers: Option[UserAnswers]) {
    lazy val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest()

    lazy val controller = new ItemRemoveItemController(
      messagesApi,
      mockUserAnswersService,
      fakeBetaAllowListAction,
      new FakeItemsNavigator(testOnwardRoute),
      fakeAuthAction,
      new FakeDataRetrievalAction(userAnswers, Some(testMinTraderKnownFacts), Some(testMessageStatistics)),
      dataRequiredAction,
      formProvider,
      Helpers.stubMessagesControllerComponents(),
      view
    )
  }

  "ItemRemoveItem Controller" - {

    "must redirect to Index of section when the idx is outside of bounds for a GET" in new Test(Some(
      emptyUserAnswers.set(ItemExciseProductCodePage(testIndex1), testEpcTobacco)
    )) {
      val result = controller.onPageLoad(testErn, testDraftId, testIndex2)(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustBe routes.ItemsIndexController.onPageLoad(testErn, testDraftId).url
    }

    "must redirect to Index of section when the idx is outside of bounds for a POST" in new Test(Some(
      emptyUserAnswers.set(ItemExciseProductCodePage(testIndex1), testEpcTobacco)
    )) {
      val result = controller.onSubmit(testErn, testDraftId, testIndex2)(request.withFormUrlEncodedBody(("value", "answer")))

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustBe routes.ItemsIndexController.onPageLoad(testErn, testDraftId).url
    }

    "must return OK and the correct view for a GET" in new Test(Some(baseUserAnswers)) {
      val result = controller.onPageLoad(testErn, testDraftId, testIndex1)(request)

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(form, action, testIndex1)(dataRequest(request, userAnswers.get), messages(request)).toString
    }

    "must redirect to the Index Controller when yes is selected (removing the section) AND cleanse any submissionFailureMessages" in new Test(
      Some(baseUserAnswers.copy(submissionFailures = Seq(
        itemQuantityFailure(1).copy(originalAttributeValue = Some("1")),
        itemQuantityFailure(2)
      )))
    ) {

      MockUserAnswersService.set(
        emptyUserAnswers
          .set(ItemExciseProductCodePage(testIndex1), testEpcWine)
          .copy(submissionFailures = Seq(itemQuantityFailure(1)))
      ).returns(
        Future.successful(emptyUserAnswers.set(ItemExciseProductCodePage(testIndex1), testEpcWine))
      )

      val result = controller.onSubmit(testErn, testDraftId, testIndex1)(request.withFormUrlEncodedBody(("value", "true")))

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.ItemsIndexController.onPageLoad(testErn, testDraftId).url
    }

    "must redirect to the Index Controller when yes is selected (removing the section) AND cleanse any packages that have the same shippingMark" +
      "where this items shipping mark is used on packages linked to this or other items" in new Test(
      Some(baseUserAnswers
        //Lead item, with shipping mark
        .set(ItemPackagingShippingMarksPage(testIndex1, testPackagingIndex1), "MarkA")
        .set(ItemPackagingQuantityPage(testIndex1, testPackagingIndex1), "1")
        //Second item, has same shipping mark as lead item. That package 1 should be removed, package 2 should re-index and become package 1
        .set(ItemPackagingShippingMarksPage(testIndex2, testPackagingIndex1), "MarkA")
        .set(ItemPackagingQuantityPage(testIndex2, testPackagingIndex1), "0")
        .set(ItemPackagingShippingMarksPage(testIndex2, testPackagingIndex2), "MarkB")
        .set(ItemPackagingQuantityPage(testIndex2, testPackagingIndex2), "1")
        //Third item, doesn't reference the shipping mark, so should be unchanged
        .set(ItemPackagingShippingMarksPage(testIndex3, testPackagingIndex1), "MarkB")
        .set(ItemPackagingQuantityPage(testIndex3, testPackagingIndex1), "1")
      )
    ) {

      val expectedSavedAnswers = emptyUserAnswers
        .set(ItemExciseProductCodePage(testIndex1), testEpcWine)
        .set(ItemPackagingShippingMarksPage(testIndex1, testPackagingIndex1), "MarkB")
        .set(ItemPackagingQuantityPage(testIndex1, testPackagingIndex1), "1")
        .set(ItemPackagingShippingMarksPage(testIndex2, testPackagingIndex1), "MarkB")
        .set(ItemPackagingQuantityPage(testIndex2, testPackagingIndex1), "1")

      MockUserAnswersService.set(expectedSavedAnswers).returns(Future.successful(expectedSavedAnswers))

      val result = controller.onSubmit(testErn, testDraftId, testIndex1)(request.withFormUrlEncodedBody(("value", "true")))

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.ItemsIndexController.onPageLoad(testErn, testDraftId).url
    }

    "must redirect to the Item CYA page when no is selected" in new Test(Some(baseUserAnswers)) {

      val result = controller.onSubmit(testErn, testDraftId, testIndex1)(request.withFormUrlEncodedBody(("value", "false")))

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.ItemsAddToListController.onPageLoad(testErn, testDraftId).url
    }

    "must return a Bad Request and errors when invalid data is submitted" in new Test(Some(baseUserAnswers)) {
      val boundForm = form.bind(Map("value" -> ""))

      val result = controller.onSubmit(testErn, testDraftId, testIndex1)(request.withFormUrlEncodedBody(("value", "")))

      status(result) mustEqual BAD_REQUEST
      contentAsString(result) mustEqual view(boundForm, action, testIndex1)(dataRequest(request, userAnswers.get), messages(request)).toString
    }

    "must redirect to Journey Recovery for a GET if no existing data is found" in new Test(None) {
      val result = controller.onPageLoad(testErn, testDraftId, testIndex1)(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.JourneyRecoveryController.onPageLoad().url
    }

    "must redirect to Journey Recovery for a POST if no existing data is found" in new Test(None) {
      val result = controller.onSubmit(testErn, testDraftId, testIndex1)(request.withFormUrlEncodedBody(("value", "true")))

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.JourneyRecoveryController.onPageLoad().url
    }
  }
}
