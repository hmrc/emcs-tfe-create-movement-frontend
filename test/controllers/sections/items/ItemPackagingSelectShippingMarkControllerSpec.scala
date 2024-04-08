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
import fixtures.messages.sections.items.ItemPackagingSelectShippingMarkMessages
import forms.sections.items.ItemPackagingSelectShippingMarkFormProvider
import mocks.services.MockUserAnswersService
import models.{NormalMode, ShippingMarkOption, UserAnswers}
import navigation.FakeNavigators.FakeItemsNavigator
import pages.sections.items.{ItemExciseProductCodePage, ItemPackagingProductTypePage, ItemPackagingShippingMarksPage}
import play.api.i18n.Messages
import play.api.mvc.{AnyContentAsEmpty, Call}
import play.api.test.Helpers._
import play.api.test.{FakeRequest, Helpers}
import viewmodels.helpers.SelectItemHelper
import views.html.sections.items.ItemPackagingSelectShippingMarkView

class ItemPackagingSelectShippingMarkControllerSpec extends SpecBase with MockUserAnswersService with ItemFixtures {

  private val existingShippingMarks = Seq("beans", "eggs")

  implicit val msgs: Messages = messages(Seq(ItemPackagingSelectShippingMarkMessages.English.lang))
  private def shippingMarkOptions(existingAnswer: Option[String] = None) = SelectItemHelper.constructSelectItems(
    selectOptions = existingShippingMarks.map(ShippingMarkOption(_)),
    defaultTextMessageKey = "itemPackagingSelectShippingMark.select.defaultValue",
    existingAnswer = existingAnswer
  )

  lazy val formProvider = new ItemPackagingSelectShippingMarkFormProvider()
  lazy val form = formProvider(existingShippingMarks)
  lazy val view = app.injector.instanceOf[ItemPackagingSelectShippingMarkView]
  val action: Call = routes.ItemPackagingSelectShippingMarkController.onSubmit(testErn, testDraftId, testIndex1, testPackagingIndex1, NormalMode)

  val baseUserAnswers: UserAnswers = emptyUserAnswers
    .set(ItemPackagingProductTypePage(testIndex1, testPackagingIndex1), true)

  class Test(val userAnswers: Option[UserAnswers]) {
    lazy val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest()

    lazy val controller = new ItemPackagingSelectShippingMarkController(
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

  "ItemPackagingSelectShippingMark Controller" - {

    "must redirect to Index of section when the packaging idx is outside of bounds for a GET" in new Test(Some(
      emptyUserAnswers.set(ItemExciseProductCodePage(testIndex1), testEpcWine)
    )) {
      val result = controller.onPageLoad(testErn, testDraftId, testIndex1, testPackagingIndex2, NormalMode)(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustBe routes.ItemsPackagingIndexController.onPageLoad(testErn, testDraftId, testIndex1).url
    }

    "must redirect to Index of section when the packaging idx is outside of bounds for a POST" in new Test(Some(
      emptyUserAnswers.set(ItemExciseProductCodePage(testIndex1), testEpcWine)
    )) {
      val result = controller.onSubmit(testErn, testDraftId, testIndex1, testPackagingIndex2, NormalMode)(request.withFormUrlEncodedBody(("value", "answer")))

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustBe routes.ItemsPackagingIndexController.onPageLoad(testErn, testDraftId, testIndex1).url
    }

    "must redirect to Index of section when the item idx is outside of bounds for a GET (which redirects to Items index)" in new Test(Some(emptyUserAnswers)) {
      val result = controller.onPageLoad(testErn, testDraftId, testIndex2, testPackagingIndex1, NormalMode)(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustBe routes.ItemsPackagingIndexController.onPageLoad(testErn, testDraftId, testIndex2).url
    }

    "must redirect to Index of section when the item idx is outside of bounds for a POST (which redirects to Items index)" in new Test(Some(emptyUserAnswers)) {
      val result = controller.onSubmit(testErn, testDraftId, testIndex2, testPackagingIndex1, NormalMode)(request.withFormUrlEncodedBody(("value", "answer")))

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustBe routes.ItemsPackagingIndexController.onPageLoad(testErn, testDraftId, testIndex2).url
    }

    "must return OK and the correct view for a GET" in new Test(Some(
      baseUserAnswers
        .set(ItemPackagingProductTypePage(testIndex1, testPackagingIndex1), true)
        .set(ItemPackagingShippingMarksPage(testIndex1, testPackagingIndex2), "beans")
        .set(ItemPackagingShippingMarksPage(testIndex1, testPackagingIndex3), "eggs")
    )) {
      val result = controller.onPageLoad(testErn, testDraftId, testIndex1, testPackagingIndex1, NormalMode)(request)

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(
        form = form,
        action = action,
        itemIdx = testIndex1,
        packagingIdx = testPackagingIndex1,
        selectOptions = shippingMarkOptions()
      )(dataRequest(request, userAnswers.get), messages(request)).toString
    }

    "must populate the view correctly on a GET when the question has previously been answered" in new Test(Some(
      baseUserAnswers
        .set(ItemPackagingShippingMarksPage(testIndex1, testPackagingIndex1), "beans")
        .set(ItemPackagingShippingMarksPage(testIndex1, testPackagingIndex2), "eggs")
    )) {
      val result = controller.onPageLoad(testErn, testDraftId, testIndex1, testPackagingIndex1, NormalMode)(request)

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(
        form = form,
        action = action,
        itemIdx = testIndex1,
        packagingIdx = testPackagingIndex1,
        selectOptions = shippingMarkOptions(Some("beans"))
      )(dataRequest(request, userAnswers.get), messages(request)).toString
    }

    "must redirect to the next page when valid data is submitted" in new Test(Some(
      baseUserAnswers
        .set(ItemPackagingShippingMarksPage(testIndex1, testPackagingIndex1), "beans")
    )) {

      val result = controller.onSubmit(testErn, testDraftId, testIndex1, testPackagingIndex1, NormalMode)(request.withFormUrlEncodedBody(("value", "beans")))

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual testOnwardRoute.url
    }

    "must return a Bad Request and errors when invalid data is submitted" in new Test(Some(
      baseUserAnswers
        .set(ItemPackagingShippingMarksPage(testIndex1, testPackagingIndex1), "beans")
        .set(ItemPackagingShippingMarksPage(testIndex1, testPackagingIndex2), "eggs")
    )) {
      val boundForm = form.bind(Map("value" -> "toast"))

      val result = controller.onSubmit(testErn, testDraftId, testIndex1, testPackagingIndex1, NormalMode)(request.withFormUrlEncodedBody(("value", "toast")))

      status(result) mustEqual BAD_REQUEST
      contentAsString(result) mustEqual view(
        form = boundForm,
        action = action,
        itemIdx = testIndex1,
        packagingIdx = testPackagingIndex1,
        selectOptions = shippingMarkOptions(Some("beans"))
      )(dataRequest(request, userAnswers.get), messages(request)).toString
    }

    "must redirect to Journey Recovery for a GET if no existing data is found" in new Test(None) {
      val result = controller.onPageLoad(testErn, testDraftId, testIndex1, testPackagingIndex1, NormalMode)(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.JourneyRecoveryController.onPageLoad().url
    }

    "must redirect to Journey Recovery for a POST if no existing data is found" in new Test(None) {
      val result = controller.onSubmit(testErn, testDraftId, testIndex1, testPackagingIndex1, NormalMode)(request.withFormUrlEncodedBody(("value", "answer")))

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.JourneyRecoveryController.onPageLoad().url
    }
  }
}
