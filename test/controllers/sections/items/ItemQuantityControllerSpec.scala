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
import forms.sections.items.ItemQuantityFormProvider
import mocks.services.{MockGetCnCodeInformationService, MockUserAnswersService}
import models.GoodsTypeModel.Wine
import models.UnitOfMeasure.Litres20
import models.requests.CnCodeInformationItem
import models.response.referenceData.CnCodeInformation
import models.{Index, NormalMode, UserAnswers}
import navigation.FakeNavigators.FakeItemsNavigator
import pages.sections.items.{ItemCommodityCodePage, ItemExciseProductCodePage, ItemQuantityPage}
import play.api.data.Form
import play.api.mvc.{AnyContentAsEmpty, Call}
import play.api.test.Helpers._
import play.api.test.{FakeRequest, Helpers}
import views.html.sections.items.ItemQuantityView

import scala.concurrent.Future

class ItemQuantityControllerSpec extends SpecBase with MockUserAnswersService with MockGetCnCodeInformationService {

  val item: CnCodeInformationItem = CnCodeInformationItem("W200", "22042111")

  //Ensures a dummy item exists in the array for testing
  val defaultUserAnswers: UserAnswers = emptyUserAnswers
    .set(ItemExciseProductCodePage(testIndex1), item.productCode)
    .set(ItemCommodityCodePage(testIndex1), item.cnCode)

  def itemQuantitySubmitAction(idx: Index = testIndex1): Call = routes.ItemQuantityController.onSubmit(testErn, testDraftId, idx, NormalMode)

  lazy val formProvider: ItemQuantityFormProvider = new ItemQuantityFormProvider()
  lazy val form: Form[BigDecimal] = formProvider()
  lazy val view: ItemQuantityView = app.injector.instanceOf[ItemQuantityView]

  class Fixture(val userAnswers: Option[UserAnswers] = Some(defaultUserAnswers)) {
    lazy val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest()

    lazy val controller = new ItemQuantityController(
      messagesApi,
      mockUserAnswersService,
      new FakeItemsNavigator(testOnwardRoute),
      fakeAuthAction,
      new FakeDataRetrievalAction(userAnswers, Some(testMinTraderKnownFacts)),
      dataRequiredAction,
      fakeUserAllowListAction,
      formProvider,
      Helpers.stubMessagesControllerComponents(),
      view,
      mockGetCnCodeInformationService
    )
  }

  "ItemQuantity Controller" - {

    "must redirect to Index of section when the idx is outside of bounds for a GET" in new Fixture() {
      val result = controller.onPageLoad(testErn, testDraftId, testIndex2, NormalMode)(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustBe routes.ItemsIndexController.onPageLoad(testErn, testDraftId).url
    }

    "must redirect to Index of section when the idx is outside of bounds for a POST" in new Fixture() {
      val result = controller.onSubmit(testErn, testDraftId, testIndex2, NormalMode)(request.withFormUrlEncodedBody(("value", "1")))

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustBe routes.ItemsIndexController.onPageLoad(testErn, testDraftId).url
    }

    "must redirect to Index of section when Commodity Code is missing" in new Fixture(
      Some(defaultUserAnswers.remove(ItemExciseProductCodePage(testIndex1)))
    ) {
      val result = controller.onPageLoad(testErn, testDraftId, testIndex1, NormalMode)(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustBe routes.ItemsIndexController.onPageLoad(testErn, testDraftId).url
    }

    "must redirect to Index of section when Excise Product Code is missing" in new Fixture(
      Some(defaultUserAnswers.remove(ItemCommodityCodePage(testIndex1)))
    ) {
      val result = controller.onPageLoad(testErn, testDraftId, testIndex1, NormalMode)(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustBe routes.ItemsIndexController.onPageLoad(testErn, testDraftId).url
    }

    "must return OK and the correct view for a GET" in new Fixture() {
      MockGetCnCodeInformationService.getCnCodeInformationWithMovementItems(Seq(item))
        .returns(Future.successful(Seq(item -> CnCodeInformation(item.cnCode, "Sparkling Wine", item.productCode, "Wine", Litres20))))

      val result = controller.onPageLoad(testErn, testDraftId, testIndex1, NormalMode)(request)

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(form, itemQuantitySubmitAction(), Wine, Litres20)(dataRequest(request), messages(request)).toString
    }

    "must populate the view correctly on a GET when the question has previously been answered" in new Fixture(
      Some(defaultUserAnswers.set(ItemQuantityPage(testIndex1), BigDecimal(1.5)))
    ) {
      MockGetCnCodeInformationService.getCnCodeInformationWithMovementItems(Seq(item))
        .returns(Future.successful(Seq(item -> CnCodeInformation(item.cnCode, "Sparkling Wine", item.productCode, "Wine", Litres20))))

      val result = controller.onPageLoad(testErn, testDraftId, testIndex1, NormalMode)(request)

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(
        form.fill(BigDecimal(1.5)),
        itemQuantitySubmitAction(),
        Wine,
        Litres20
      )(dataRequest(request, userAnswers.get), messages(request)).toString
    }

    "must redirect to the next page when valid data is submitted" in new Fixture() {
      MockUserAnswersService.set().returns(Future.successful(emptyUserAnswers))
      val result = controller.onSubmit(testErn, testDraftId, testIndex1, NormalMode)(request.withFormUrlEncodedBody(("value", "1")))

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual testOnwardRoute.url
    }

    "must render BadRequest when invalid data is submitted" in new Fixture() {
      MockGetCnCodeInformationService.getCnCodeInformationWithMovementItems(Seq(item))
        .returns(Future.successful(Seq(item -> CnCodeInformation(item.cnCode, "Sparkling Wine", item.productCode, "Wine", Litres20))))

      val result = controller.onSubmit(testErn, testDraftId, testIndex1, NormalMode)(request.withFormUrlEncodedBody(("value", "")))
      val boundForm = form.bind(Map("value" -> ""))

      status(result) mustEqual BAD_REQUEST
      contentAsString(result) mustEqual view(
        boundForm,
        itemQuantitySubmitAction(),
        Wine,
        Litres20
      )(dataRequest(request, userAnswers.get), messages(request)).toString
    }

    "must redirect when no data retrieved from Reference Data for CN Code Information" in new Fixture() {
      MockGetCnCodeInformationService.getCnCodeInformationWithMovementItems(Seq(item))
        .returns(Future.successful(Seq()))

      val result = controller.onPageLoad(testErn, testDraftId, testIndex1, NormalMode)(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.JourneyRecoveryController.onPageLoad().url
    }

    "must redirect to Journey Recovery for a GET if no existing data is found" in new Fixture(None) {
      val result = controller.onPageLoad(testErn, testDraftId, testIndex1, NormalMode)(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.JourneyRecoveryController.onPageLoad().url
    }

    "must redirect to Journey Recovery for a POST if no existing data is found" in new Fixture(None) {
      val result = controller.onSubmit(testErn, testDraftId, testIndex1, NormalMode)(request.withFormUrlEncodedBody(("value", "1")))

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.JourneyRecoveryController.onPageLoad().url
    }
  }
}
