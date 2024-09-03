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
import forms.sections.items.ItemDesignationOfOriginFormProvider.{geographicalIndicationField, isSpiritMarketedAndLabelledField, protectedDesignationOfOriginTextField, protectedGeographicalIndicationTextField}
import mocks.services.MockUserAnswersService
import models.sections.items.ItemDesignationOfOriginModel
import models.sections.items.ItemGeographicalIndicationType.ProtectedGeographicalIndication
import models.{NormalMode, UserAnswers}
import navigation.FakeNavigators.FakeItemsNavigator
import pages.sections.items.{ItemDesignationOfOriginPage, ItemExciseProductCodePage}
import play.api.mvc.{AnyContentAsEmpty, Call}
import play.api.test.Helpers._
import play.api.test.{FakeRequest, Helpers}
import views.html.sections.items.ItemDesignationOfOriginView

import scala.concurrent.Future

class ItemDesignationOfOriginControllerSpec extends SpecBase with MockUserAnswersService with ItemFixtures {

  lazy val formProvider = new ItemDesignationOfOriginFormProvider()

  lazy val view: ItemDesignationOfOriginView = app.injector.instanceOf[ItemDesignationOfOriginView]

  val submitCall: Call = controllers.sections.items.routes.ItemDesignationOfOriginController.onSubmit(testErn, testDraftId, testIndex1, NormalMode)

  val baseUserAnswersWine: UserAnswers = emptyUserAnswers
    .set(ItemExciseProductCodePage(testIndex1), testEpcWine)

  val baseUserAnswersSpirit: UserAnswers = emptyUserAnswers
    .set(ItemExciseProductCodePage(testIndex1), testEpcSpirit)

  class Test(val userAnswers: Option[UserAnswers], epc: String = testEpcWine) {

    lazy val form = formProvider(epc)

    lazy val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest()

    lazy val controller = new ItemDesignationOfOriginController(
      messagesApi,
      mockUserAnswersService,
      new FakeItemsNavigator(testOnwardRoute),
      fakeAuthAction,
      new FakeDataRetrievalAction(userAnswers, Some(testMinTraderKnownFacts)),
      dataRequiredAction,
      formProvider,
      Helpers.stubMessagesControllerComponents(),
      view
    )
  }

  "ItemDesignationOfOrigin Controller" - {

    "must return OK and the correct view for a GET" in new Test(Some(baseUserAnswersWine)) {
      val result = controller.onPageLoad(testErn, testDraftId, testIndex1, NormalMode)(request)

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(form, submitCall, testEpcWine, testIndex1)(dataRequest(request, userAnswers.get), messages(request)).toString
    }

    "must populate the view correctly on a GET when the question has previously been answered (non-S200)" in new Test(Some(
      baseUserAnswersWine
        .set(ItemDesignationOfOriginPage(testIndex1), ItemDesignationOfOriginModel(ProtectedGeographicalIndication, Some("Geographical Indication Number"), None))
    )) {
      val result = controller.onPageLoad(testErn, testDraftId, testIndex1, NormalMode)(request)

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(
        form.fill(ItemDesignationOfOriginModel(ProtectedGeographicalIndication, Some("Geographical Indication Number"), None)),
        submitCall,
        testEpcWine,
        testIndex1)(dataRequest(request, userAnswers.get), messages(request)).toString
    }

    "must populate the view correctly on a GET when the question has previously been answered (for S200)" in new Test(Some(
      baseUserAnswersSpirit
        .set(ItemDesignationOfOriginPage(testIndex1), ItemDesignationOfOriginModel(ProtectedGeographicalIndication, Some("Geographical Indication Number"), Some(true)))
    ), epc = "S200") {

      val result = controller.onPageLoad(testErn, testDraftId, testIndex1, NormalMode)(request)

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(
        form.fill(ItemDesignationOfOriginModel(ProtectedGeographicalIndication, Some("Geographical Indication Number"), Some(true))),
        submitCall,
        testEpcSpirit,
        testIndex1)(dataRequest(request, userAnswers.get), messages(request)).toString
    }

    "must redirect to the next page when valid data is submitted" in new Test(Some(baseUserAnswersWine)) {

      val expectedAnswers: UserAnswers = baseUserAnswersWine
        .set(ItemDesignationOfOriginPage(testIndex1), ItemDesignationOfOriginModel(ProtectedGeographicalIndication, Some("Geographical Indication Number"), None))

      MockUserAnswersService.set(expectedAnswers).returns(Future.successful(expectedAnswers))

      val result = controller.onSubmit(testErn, testDraftId, testIndex1, NormalMode)(request.withFormUrlEncodedBody(
        geographicalIndicationField -> ProtectedGeographicalIndication.toString,
        protectedDesignationOfOriginTextField -> "don't use this",
        protectedGeographicalIndicationTextField -> "Geographical Indication Number"
      ))

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual testOnwardRoute.url
    }

    "must redirect to the next page when valid data is submitted (for S200)" in new Test(Some(baseUserAnswersSpirit)) {

      val expectedAnswers: UserAnswers = baseUserAnswersSpirit
        .set(ItemDesignationOfOriginPage(testIndex1), ItemDesignationOfOriginModel(ProtectedGeographicalIndication, Some("Geographical Indication Number"), Some(true)))

      MockUserAnswersService.set(expectedAnswers).returns(Future.successful(expectedAnswers))

      val result = controller.onSubmit(testErn, testDraftId, testIndex1, NormalMode)(request.withFormUrlEncodedBody(
        geographicalIndicationField -> ProtectedGeographicalIndication.toString,
        protectedDesignationOfOriginTextField -> "don't use this",
        protectedGeographicalIndicationTextField -> "Geographical Indication Number",
        isSpiritMarketedAndLabelledField -> "true"
      ))

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual testOnwardRoute.url
    }

    "must return a Bad Request and errors when invalid data is submitted" in new Test(Some(baseUserAnswersSpirit)) {
      val boundForm = form.bind(Map(
        geographicalIndicationField -> ProtectedGeographicalIndication.toString,
        protectedDesignationOfOriginTextField -> "don't use this",
        protectedGeographicalIndicationTextField -> ";;;;Geographical Indication Number",
        isSpiritMarketedAndLabelledField -> "true"
      ))

      val result = controller.onSubmit(testErn, testDraftId, testIndex1, NormalMode)(request.withFormUrlEncodedBody(
        geographicalIndicationField -> ProtectedGeographicalIndication.toString,
        protectedDesignationOfOriginTextField -> "don't use this",
        protectedGeographicalIndicationTextField -> ";;;;Geographical Indication Number",
        isSpiritMarketedAndLabelledField -> "true"
      ))

      status(result) mustEqual BAD_REQUEST
      contentAsString(result) mustEqual view(
        boundForm,
        submitCall,
        testEpcSpirit,
        testIndex1)(dataRequest(request, userAnswers.get), messages(request)).toString
    }


    "must redirect to Index of section when the idx is outside of bounds for a GET" in new Test(Some(baseUserAnswersSpirit)) {
      val result = controller.onPageLoad(testErn, testDraftId, testIndex2, NormalMode)(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustBe routes.ItemsIndexController.onPageLoad(testErn, testDraftId).url
    }

    "must redirect to Index of section when the idx is outside of bounds for a POST" in new Test(Some(baseUserAnswersSpirit)) {
      val result = controller.onSubmit(testErn, testDraftId, testIndex2, NormalMode)(request.withFormUrlEncodedBody(
        geographicalIndicationField -> ProtectedGeographicalIndication.toString,
        protectedDesignationOfOriginTextField -> "don't use this",
        protectedGeographicalIndicationTextField -> "Geographical Indication Number",
        isSpiritMarketedAndLabelledField -> "true"
      ))

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustBe routes.ItemsIndexController.onPageLoad(testErn, testDraftId).url
    }

    "must redirect to Index of section when Excise Product Code is missing (for GET)" in new Test(Some(emptyUserAnswers)) {
      val result = controller.onPageLoad(testErn, testDraftId, testIndex1, NormalMode)(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustBe routes.ItemsIndexController.onPageLoad(testErn, testDraftId).url
    }

    "must redirect to Index of section when Excise Product Code is missing (for POST)" in new Test(Some(emptyUserAnswers)) {
      val result = controller.onSubmit(testErn, testDraftId, testIndex1, NormalMode)(request.withFormUrlEncodedBody(
        geographicalIndicationField -> ProtectedGeographicalIndication.toString,
        protectedDesignationOfOriginTextField -> "don't use this",
        protectedGeographicalIndicationTextField -> "Geographical Indication Number",
        isSpiritMarketedAndLabelledField -> "true"
      ))

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustBe routes.ItemsIndexController.onPageLoad(testErn, testDraftId).url
    }

    "must redirect to Journey Recovery for a GET if no existing data is found" in new Test(None) {
      val result = controller.onPageLoad(testErn, testDraftId, testIndex1, NormalMode)(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.JourneyRecoveryController.onPageLoad().url
    }

    "must redirect to Journey Recovery for a POST if no existing data is found" in new Test(None) {
      val result = controller.onSubmit(testErn, testDraftId, testIndex1, NormalMode)(request.withFormUrlEncodedBody(
        geographicalIndicationField -> ProtectedGeographicalIndication.toString,
        protectedDesignationOfOriginTextField -> "don't use this",
        protectedGeographicalIndicationTextField -> "Geographical Indication Number",
        isSpiritMarketedAndLabelledField -> "true"
      ))

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.JourneyRecoveryController.onPageLoad().url
    }
  }
}
