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
import forms.sections.items.ItemCommodityCodeFormProvider
import mocks.services.{MockGetCnCodeInformationService, MockGetCommodityCodesService, MockUserAnswersService}
import models.UnitOfMeasure.Kilograms
import models.response.referenceData.CnCodeInformation
import models.{GoodsTypeModel, NormalMode, UserAnswers}
import navigation.FakeNavigators.FakeItemsNavigator
import pages.sections.items.{ItemCommodityCodePage, ItemExciseProductCodePage}
import play.api.data.Form
import play.api.mvc.{AnyContentAsEmpty, Call}
import play.api.test.Helpers._
import play.api.test.{FakeRequest, Helpers}
import views.html.sections.items.ItemCommodityCodeView

import scala.concurrent.Future

class ItemCommodityCodeControllerSpec extends SpecBase with MockUserAnswersService with MockGetCommodityCodesService with MockGetCnCodeInformationService {

  val defaultUserAnswers: UserAnswers = emptyUserAnswers.set(ItemExciseProductCodePage(testIndex1), "W200")

  lazy val itemIndexRoute: String = routes.ItemsIndexController.onPageLoad(testErn, testDraftId).url
  lazy val submitCall: Call = routes.ItemCommodityCodeController.onSubmit(testErn, testDraftId, testIndex1, NormalMode)

  val testEpc: String = "T200"
  val testGoodsType: GoodsTypeModel.GoodsType = GoodsTypeModel.apply(testEpc)

  val testCommodityCode1: CnCodeInformation = CnCodeInformation(
    cnCode = "testCnCode1",
    cnCodeDescription = "testCnCodeDescription1",
    exciseProductCode = testEpc,
    exciseProductCodeDescription = "testExciseProductCodeDescription",
    unitOfMeasure = Kilograms
  )

  val testCommodityCode2: CnCodeInformation = CnCodeInformation(
    cnCode = "testCnCode2",
    cnCodeDescription = "testCnCodeDescription2",
    exciseProductCode = testEpc,
    exciseProductCodeDescription = "testExciseProductCodeDescription",
    unitOfMeasure = Kilograms
  )

  lazy val formProvider: ItemCommodityCodeFormProvider = new ItemCommodityCodeFormProvider()
  lazy val form: Form[String] = formProvider()
  lazy val view: ItemCommodityCodeView = app.injector.instanceOf[ItemCommodityCodeView]

  class Fixture(val userAnswers: Option[UserAnswers] = Some(defaultUserAnswers)) {
    lazy val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest()

    lazy val controller = new ItemCommodityCodeController(
      messagesApi,
      mockUserAnswersService,
      new FakeItemsNavigator(testOnwardRoute),
      fakeAuthAction,
      new FakeDataRetrievalAction(userAnswers, Some(testMinTraderKnownFacts)),
      dataRequiredAction,
      fakeUserAllowListAction,
      mockGetCommodityCodesService,
      formProvider,
      Helpers.stubMessagesControllerComponents(),
      view,
      mockGetCnCodeInformationService
    )
  }

  "ItemCommodityCode Controller" - {
    "must return OK and the correct view for a GET when a list of commodity codes are returned" in new Fixture(
      userAnswers = Some(emptyUserAnswers.set(ItemExciseProductCodePage(testIndex1), testEpcTobacco))
    ) {
      MockGetCommodityCodesService.getCommodityCodes(testEpc).returns(Future.successful(Seq(
        testCommodityCode1,
        testCommodityCode2
      )))

      val result = controller.onPageLoad(testErn, testDraftId, testIndex1, NormalMode)(request)

      status(result) mustEqual OK
      contentAsString(result) mustEqual
        view(form, submitCall, testGoodsType, Seq(testCommodityCode1, testCommodityCode2))(dataRequest(request, userAnswers.get), messages(request)).toString
    }

    "must return OK and the correct view with the previous answer for a GET when a list of commodity codes are returned" in new Fixture(
      userAnswers = Some(
        emptyUserAnswers
          .set(ItemExciseProductCodePage(testIndex1), testEpcTobacco)
          .set(ItemCommodityCodePage(testIndex1), testCommodityCodeTobacco.cnCode)
      )
    ) {
      MockGetCommodityCodesService.getCommodityCodes(testEpc).returns(Future.successful(Seq(
        testCommodityCode1,
        testCommodityCode2
      )))

      val result = controller.onPageLoad(testErn, testDraftId, testIndex1, NormalMode)(request)

      status(result) mustEqual OK
      contentAsString(result) mustEqual
        view(
          form.fill(testCommodityCode1.cnCode),
          submitCall,
          testGoodsType,
          Seq(testCommodityCode1, testCommodityCode2)
        )(dataRequest(request, userAnswers.get), messages(request)).toString
    }

    "must redirect to the confirmation page for a GET when a single commodity code is returned" in new Fixture(
      userAnswers = Some(emptyUserAnswers.set(ItemExciseProductCodePage(testIndex1), testEpcTobacco))
    ) {
      MockUserAnswersService.set().returns(Future.successful(emptyUserAnswers))

      MockGetCommodityCodesService.getCommodityCodes(testEpc).returns(Future.successful(Seq(
        testCommodityCode1,
      )))

      val result = controller.onPageLoad(testErn, testDraftId, testIndex1, NormalMode)(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual testOnwardRoute.url
    }

    "must redirect to the confirmation page for a GET when no commodity codes are returned" in new Fixture(
      userAnswers = Some(emptyUserAnswers.set(ItemExciseProductCodePage(testIndex1), testEpcTobacco))
    ) {
      MockUserAnswersService.set().returns(Future.successful(emptyUserAnswers))

      MockGetCommodityCodesService.getCommodityCodes(testEpc).returns(Future.successful(Nil))

      val result = controller.onPageLoad(testErn, testDraftId, testIndex1, NormalMode)(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual testOnwardRoute.url
    }

    "must redirect to the next page when valid data is submitted" in new Fixture(
      userAnswers = Some(emptyUserAnswers.set(ItemExciseProductCodePage(testIndex1), testEpcTobacco))
    ) {
      MockUserAnswersService.set().returns(Future.successful(emptyUserAnswers))

      val result = controller.onSubmit(testErn, testDraftId, testIndex1, NormalMode)(request.withFormUrlEncodedBody(("item-commodity-code", "answer")))

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual testOnwardRoute.url
    }

    "must return a Bad Request and errors when invalid data is submitted" in new Fixture(
      userAnswers = Some(emptyUserAnswers.set(ItemExciseProductCodePage(testIndex1), testEpcTobacco))
    ) {
      MockGetCommodityCodesService.getCommodityCodes(testEpc).returns(Future.successful(Seq(
        testCommodityCode1,
        testCommodityCode2
      )))

      val boundForm = form.bind(Map("item-commodity-code" -> ""))

      val result = controller.onSubmit(testErn, testDraftId, testIndex1, NormalMode)(request.withFormUrlEncodedBody(("item-commodity-code", "")))

      status(result) mustEqual BAD_REQUEST
      contentAsString(result) mustEqual
        view(boundForm, submitCall, testGoodsType, Seq(testCommodityCode1, testCommodityCode2))(dataRequest(request, userAnswers.get), messages(request)).toString
    }

    "must redirect to Item Index Controller for a GET if no existing data is found" in new Fixture(
      userAnswers = Some(emptyUserAnswers)
    ) {
      val result = controller.onPageLoad(testErn, testDraftId, testIndex1, NormalMode)(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual itemIndexRoute
    }
  }
}
