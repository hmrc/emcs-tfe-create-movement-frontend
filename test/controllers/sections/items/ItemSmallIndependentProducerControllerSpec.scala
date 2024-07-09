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
import forms.sections.items.ItemSmallIndependentProducerFormProvider
import forms.sections.items.ItemSmallIndependentProducerFormProvider.{producerField, producerIdField}
import mocks.services.MockUserAnswersService
import models.sections.info.movementScenario.MovementScenario.UkTaxWarehouse
import models.sections.items.ItemSmallIndependentProducerModel
import models.sections.items.ItemSmallIndependentProducerType.{SelfCertifiedIndependentSmallProducerAndConsignor, SelfCertifiedIndependentSmallProducerAndNotConsignor}
import models.{Index, NormalMode, UserAnswers}
import navigation.FakeNavigators.FakeItemsNavigator
import pages.sections.info.DestinationTypePage
import pages.sections.items.{ItemCommodityCodePage, ItemExciseProductCodePage, ItemProducerSizePage, ItemSmallIndependentProducerPage}
import play.api.data.Form
import play.api.mvc.{AnyContentAsEmpty, Call}
import play.api.test.Helpers._
import play.api.test.{FakeRequest, Helpers}
import views.html.sections.items.ItemSmallIndependentProducerView

import scala.concurrent.Future

class ItemSmallIndependentProducerControllerSpec extends SpecBase with MockUserAnswersService with ItemFixtures {

  val defaultUserAnswers: UserAnswers = {
    emptyUserAnswers
      .set(DestinationTypePage, UkTaxWarehouse.GB)
      .set(ItemExciseProductCodePage(testIndex1), testEpcWine)
      .set(ItemCommodityCodePage(testIndex1), testCnCodeWine)
  }

  def itemSmallIndependentProducerSubmitAction(idx: Index = testIndex1): Call =
    routes.ItemSmallIndependentProducerController.onSubmit(testErn, testDraftId, idx, NormalMode)

  val model: ItemSmallIndependentProducerModel = ItemSmallIndependentProducerModel(SelfCertifiedIndependentSmallProducerAndNotConsignor, Some(testVatNumber))

  val validFormBody: Seq[(String, String)] = Seq(
    producerField -> SelfCertifiedIndependentSmallProducerAndNotConsignor.toString,
    producerIdField -> testVatNumber
  )

  lazy val formProvider = new ItemSmallIndependentProducerFormProvider()
  lazy val form: Form[ItemSmallIndependentProducerModel] = formProvider()

  lazy val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest()

  lazy val view: ItemSmallIndependentProducerView = app.injector.instanceOf[ItemSmallIndependentProducerView]

  class Test(val userAnswers: Option[UserAnswers] = Some(defaultUserAnswers)) {

    lazy val controller = new ItemSmallIndependentProducerController(
      messagesApi,
      mockUserAnswersService,
      new FakeItemsNavigator(testOnwardRoute),
      fakeAuthAction,
      new FakeDataRetrievalAction(userAnswers, Some(testMinTraderKnownFacts), Some(testMessageStatistics)),
      dataRequiredAction,
      fakeBetaAllowListAction,
      formProvider,
      Helpers.stubMessagesControllerComponents(),
      view
    )
  }

  "ItemSmallIndependentProducer Controller" - {

    "must redirect to Index of section when the idx is outside of bounds for a GET" in new Test() {
      val result = controller.onPageLoad(testErn, testDraftId, testIndex2, NormalMode)(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustBe routes.ItemsIndexController.onPageLoad(testErn, testDraftId).url
    }

    "must redirect to Index of section when the idx is outside of bounds for a POST" in new Test() {
      val result = controller.onSubmit(testErn, testDraftId, testIndex2, NormalMode)(request.withFormUrlEncodedBody(("value", "true")))

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustBe routes.ItemsIndexController.onPageLoad(testErn, testDraftId).url
    }

    "must return OK and the correct view for a GET" in new Test() {
      val result = controller.onPageLoad(testErn, testDraftId, testIndex1, NormalMode)(request)

      status(result) mustEqual OK
      contentAsString(result) mustEqual
        view(
          form,
          itemSmallIndependentProducerSubmitAction(),
          testIndex1
        )(dataRequest(request, userAnswers.get), messages(request)).toString
    }

    "must populate the view correctly on a GET when the question has previously been answered" in new Test(Some(
      defaultUserAnswers.set(ItemSmallIndependentProducerPage(testIndex1), model)
    )) {
      val result = controller.onPageLoad(testErn, testDraftId, testIndex1, NormalMode)(request)

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(
        form.fill(model),
        itemSmallIndependentProducerSubmitAction(),
        testIndex1
      )(dataRequest(request, userAnswers.get), messages(request)).toString
    }

    "must redirect to the next page when valid data is submitted" - {
      "when the answer has changed" in new Test(Some(
        defaultUserAnswers
          .set(ItemSmallIndependentProducerPage(testIndex1), model.copy(SelfCertifiedIndependentSmallProducerAndConsignor, producerId = None))
          .set(ItemProducerSizePage(testIndex1), BigInt(1))
      )) {
        MockUserAnswersService
          .set(defaultUserAnswers.set(ItemSmallIndependentProducerPage(testIndex1), model.copy(SelfCertifiedIndependentSmallProducerAndNotConsignor, producerId = Some(testVatNumber))))
          .returns(Future.successful(emptyUserAnswers))

        val result = controller.onSubmit(testErn, testDraftId, testIndex1, NormalMode)(request.withFormUrlEncodedBody(validFormBody: _*))

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual testOnwardRoute.url
      }
      "when the answer has not changed" in new Test(Some(
        defaultUserAnswers
          .set(ItemSmallIndependentProducerPage(testIndex1), model)
          .set(ItemProducerSizePage(testIndex1), BigInt(1))
      )) {
        val result = controller.onSubmit(testErn, testDraftId, testIndex1, NormalMode)(request.withFormUrlEncodedBody(validFormBody: _*))

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual testOnwardRoute.url
      }
      "when the answer is new" in new Test(Some(
        defaultUserAnswers
      )) {
        MockUserAnswersService.set().returns(Future.successful(emptyUserAnswers))

        val result = controller.onSubmit(testErn, testDraftId, testIndex1, NormalMode)(request.withFormUrlEncodedBody(validFormBody: _*))

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual testOnwardRoute.url
      }
    }

    s"must render BadRequest when $SelfCertifiedIndependentSmallProducerAndNotConsignor is selected but no identifier is entered" in new Test() {
      val result = controller.onSubmit(testErn, testDraftId, testIndex1, NormalMode)(request.withFormUrlEncodedBody(validFormBody.head))
      val boundForm = form.bind(Map(validFormBody.head))

      status(result) mustEqual BAD_REQUEST
      contentAsString(result) mustEqual view(
        boundForm,
        itemSmallIndependentProducerSubmitAction(),
        testIndex1
      )(dataRequest(request, userAnswers.get), messages(request)).toString
    }

    "must render BadRequest when no option is selected" in new Test() {
      val result = controller.onSubmit(testErn, testDraftId, testIndex1, NormalMode)(request.withFormUrlEncodedBody(("value", "")))
      val boundForm = form.bind(Map(producerField -> ""))

      status(result) mustEqual BAD_REQUEST
      contentAsString(result) mustEqual view(
        boundForm,
        itemSmallIndependentProducerSubmitAction(),
        testIndex1
      )(dataRequest(request, userAnswers.get), messages(request)).toString
    }

    "must redirect to Journey Recovery for a GET if no existing data is found" in new Test(None) {
      val result = controller.onPageLoad(testErn, testDraftId, testIndex1, NormalMode)(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.JourneyRecoveryController.onPageLoad().url
    }

    "must redirect to Journey Recovery for a POST if no existing data is found" in new Test(None) {
      val result = controller.onSubmit(testErn, testDraftId, testIndex1, NormalMode)(request.withFormUrlEncodedBody(("value", "true")))

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.JourneyRecoveryController.onPageLoad().url
    }
  }
}
