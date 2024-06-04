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
import fixtures.ItemFixtures
import forms.sections.items.ItemPackagingQuantityFormProvider
import mocks.services.MockUserAnswersService
import models.response.referenceData.ItemPackaging
import models.{NormalMode, UserAnswers}
import navigation.FakeNavigators.FakeItemsNavigator
import pages.sections.items._
import play.api.data.Form
import play.api.mvc.{AnyContentAsEmpty, Call}
import play.api.test.Helpers._
import play.api.test.{FakeRequest, Helpers}
import views.html.sections.items.ItemPackagingQuantityView

import scala.concurrent.Future

class ItemPackagingQuantityControllerSpec extends SpecBase with MockUserAnswersService with ItemFixtures {

  //Ensures a dummy item exists in the array for testing
  val defaultUserAnswers: UserAnswers = emptyUserAnswers
    .set(ItemExciseProductCodePage(testIndex1), testEpcWine)
    .set(ItemSelectPackagingPage(testIndex1, testPackagingIndex1), ItemPackaging("VA", "Vat"))

  val itemPackagingQuantitySubmitAction: Call = routes.ItemPackagingQuantityController.onSubmit(testErn, testDraftId, testIndex1, testPackagingIndex1, NormalMode)
  lazy val formProvider: ItemPackagingQuantityFormProvider = new ItemPackagingQuantityFormProvider()
  lazy val view: ItemPackagingQuantityView = app.injector.instanceOf[ItemPackagingQuantityView]

  class Fixture(val userAnswers: Option[UserAnswers] = Some(defaultUserAnswers)) {
    lazy val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest()
    lazy val form: Form[String] = formProvider(testIndex1, testPackagingIndex2)(messages(request), dataRequest(request, userAnswers.getOrElse(defaultUserAnswers)))

    lazy val controller = new ItemPackagingQuantityController(
      messagesApi,
      mockUserAnswersService,
      new FakeItemsNavigator(testOnwardRoute),
      fakeAuthAction,
      new FakeDataRetrievalAction(userAnswers, Some(testMinTraderKnownFacts)),
      dataRequiredAction,
      fakeBetaAllowListAction,
      formProvider,
      Helpers.stubMessagesControllerComponents(),
      view
    )
  }

  "ItemPackagingQuantity Controller" - {

    "must redirect to Index of section when the idx is outside of bounds for a GET" in new Fixture() {
      val result = controller.onPageLoad(testErn, testDraftId, testIndex1, testPackagingIndex2, NormalMode)(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustBe routes.ItemsPackagingIndexController.onPageLoad(testErn, testDraftId, testIndex1).url
    }

    "must redirect to Index of section when the idx is outside of bounds for a POST" in new Fixture() {
      val result = controller.onSubmit(testErn, testDraftId, testIndex1, testPackagingIndex2, NormalMode)(request.withFormUrlEncodedBody(("value", "1")))

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustBe routes.ItemsPackagingIndexController.onPageLoad(testErn, testDraftId, testIndex1).url
    }

    "must redirect to Index of section when Select Packaging Type is missing" in new Fixture(
      Some(emptyUserAnswers.set(ItemExciseProductCodePage(testIndex1), testEpcWine))) {
      val result = controller.onPageLoad(testErn, testDraftId, testIndex1, testPackagingIndex1, NormalMode)(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustBe routes.ItemsPackagingIndexController.onPageLoad(testErn, testDraftId, testIndex1).url
    }

    "must return OK and the correct view for a GET" in new Fixture() {

      val result = controller.onPageLoad(testErn, testDraftId, testIndex1, testPackagingIndex1, NormalMode)(request)

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(
        form,
        itemPackagingQuantitySubmitAction,
        ItemPackaging("VA", "Vat"),
        testPackagingIndex1,
        testIndex1
      )(dataRequest(request), messages(request)).toString
    }

    "must populate the view correctly on a GET when the question has previously been answered" in new Fixture(
      Some(defaultUserAnswers.set(ItemPackagingQuantityPage(testIndex1, testPackagingIndex1), "1"))
    ) {
      val result = controller.onPageLoad(testErn, testDraftId, testIndex1, testPackagingIndex1, NormalMode)(request)

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(
        form.fill("1"),
        itemPackagingQuantitySubmitAction,
        ItemPackaging("VA", "Vat"),
        testPackagingIndex1,
        testIndex1
      )(dataRequest(request, userAnswers.get), messages(request)).toString
    }

    "must redirect to the next page when valid data is submitted" in new Fixture() {
      MockUserAnswersService.set().returns(Future.successful(emptyUserAnswers))
      val result = controller.onSubmit(testErn, testDraftId, testIndex1, testPackagingIndex1, NormalMode)(request.withFormUrlEncodedBody(("value", "1")))

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual testOnwardRoute.url
    }

    "must remove the packaging shipping marks choice and answer page when the user goes from 0 (existing answer) to > 0 (new answer)" in new Fixture(
      Some(emptyUserAnswers
        .set(ItemPackagingQuantityPage(testIndex1, testPackagingIndex1), "0")
        .set(ItemPackagingShippingMarksPage(testIndex1, testPackagingIndex1), "xyz")
        .set(ItemPackagingShippingMarksChoicePage(testIndex1, testPackagingIndex1), false)
      )
    ) {
      val expectedAnswers: UserAnswers = emptyUserAnswers.set(ItemPackagingQuantityPage(testIndex1, testPackagingIndex1), "1")

      MockUserAnswersService.set(expectedAnswers).returns(Future.successful(expectedAnswers))

      val result = controller.onSubmit(testErn, testDraftId, testIndex1, testPackagingIndex1, NormalMode)(request.withFormUrlEncodedBody(("value", "1")))

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual testOnwardRoute.url
    }

    "must remove the packaging shipping marks choice and answer page when the user goes from > 0 (existing answer) to 0 (new answer)" in new Fixture(
      Some(emptyUserAnswers
        .set(ItemPackagingQuantityPage(testIndex1, testPackagingIndex1), "1")
        .set(ItemPackagingShippingMarksChoicePage(testIndex1, testPackagingIndex1), false)
        .set(ItemPackagingQuantityPage(testIndex2, testPackagingIndex1), "1")
        .set(ItemPackagingShippingMarksChoicePage(testIndex2, testPackagingIndex1), true)
        .set(ItemPackagingShippingMarksPage(testIndex2, testPackagingIndex1), "xyz")
      )
    ) {
      val expectedAnswers: UserAnswers = emptyUserAnswers
        .set(ItemPackagingQuantityPage(testIndex1, testPackagingIndex1), "0")
        .set(ItemPackagingQuantityPage(testIndex2, testPackagingIndex1), "1")
        .set(ItemPackagingShippingMarksChoicePage(testIndex2, testPackagingIndex1), true)
        .set(ItemPackagingShippingMarksPage(testIndex2, testPackagingIndex1), "xyz")

      MockUserAnswersService.set(expectedAnswers).returns(Future.successful(expectedAnswers))

      val result = controller.onSubmit(testErn, testDraftId, testIndex1, testPackagingIndex1, NormalMode)(request.withFormUrlEncodedBody(("value", "0")))

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual testOnwardRoute.url
    }

    "must render BadRequest when invalid data is submitted" in new Fixture() {

      val result = controller.onSubmit(testErn, testDraftId, testIndex1, testPackagingIndex1, NormalMode)(request.withFormUrlEncodedBody(("value", "")))
      val boundForm = form.bind(Map("value" -> ""))

      status(result) mustEqual BAD_REQUEST
      contentAsString(result) mustEqual view(
        boundForm,
        itemPackagingQuantitySubmitAction,
        ItemPackaging("VA", "Vat"),
        testPackagingIndex1,
        testIndex1
      )(dataRequest(request, userAnswers.get), messages(request)).toString
    }

    "must redirect to Journey Recovery for a GET if no existing data is found" in new Fixture(None) {
      val result = controller.onPageLoad(testErn, testDraftId, testIndex1, testPackagingIndex1, NormalMode)(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.JourneyRecoveryController.onPageLoad().url
    }

    "must redirect to Journey Recovery for a POST if no existing data is found" in new Fixture(None) {
      val result = controller.onSubmit(testErn, testDraftId, testIndex1, testPackagingIndex1, NormalMode)(request.withFormUrlEncodedBody(("value", "1")))

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.JourneyRecoveryController.onPageLoad().url
    }
  }
}
