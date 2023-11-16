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
import forms.sections.items.ItemCommodityCodeFormProvider
import mocks.services.MockUserAnswersService
import models.UnitOfMeasure.Kilograms
import models.response.referenceData.CnCodeInformation
import models.{GoodsTypeModel, NormalMode, UserAnswers}
import navigation.FakeNavigators.FakeItemsNavigator
import navigation.ItemsNavigator
import pages.sections.items.{ItemCommodityCodePage, ItemExciseProductCodePage}
import play.api.Application
import play.api.inject.bind
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.{GetCommodityCodesService, UserAnswersService}
import uk.gov.hmrc.http.HeaderCarrier
import views.html.sections.items.ItemCommodityCodeView

import scala.concurrent.Future

class ItemCommodityCodeControllerSpec extends SpecBase with MockUserAnswersService {

  val defaultUserAnswers: UserAnswers = emptyUserAnswers.set(ItemExciseProductCodePage(testIndex1), "W200")

  def onwardRoute = Call("GET", "/foo")

  val formProvider = new ItemCommodityCodeFormProvider()
  val form = formProvider()

  lazy val itemCommodityCodeRoute = routes.ItemCommodityCodeController.onPageLoad(testErn, testDraftId, testIndex1, NormalMode).url
  lazy val itemIndexRoute = routes.ItemsIndexController.onPageLoad(testErn, testDraftId).url
  lazy val submitCall = routes.ItemCommodityCodeController.onSubmit(testErn, testDraftId, testIndex1, NormalMode)
  implicit val hc: HeaderCarrier = HeaderCarrier()
  val testEpc: String = "T"
  val testGoodsType: GoodsTypeModel.GoodsType = GoodsTypeModel.apply(testEpc)

  val testCommodityCode1 = CnCodeInformation(
    cnCode = "testCnCode1",
    cnCodeDescription = "testCnCodeDescription1",
    exciseProductCode = testEpc,
    exciseProductCodeDescription = "testExciseProductCodeDescription",
    unitOfMeasure = Kilograms
  )

  val testCommodityCode2 = CnCodeInformation(
    cnCode = "testCnCode2",
    cnCodeDescription = "testCnCodeDescription2",
    exciseProductCode = testEpc,
    exciseProductCodeDescription = "testExciseProductCodeDescription",
    unitOfMeasure = Kilograms
  )

  class Fixture(val userAnswers: Option[UserAnswers] = Some(defaultUserAnswers)) {
    val mockGetCommodityCodesService: GetCommodityCodesService = mock[GetCommodityCodesService]

    val application: Application = applicationBuilder(userAnswers)
      .overrides(
        bind[ItemsNavigator].toInstance(new FakeItemsNavigator(testOnwardRoute)),
        bind[UserAnswersService].toInstance(mockUserAnswersService),
        bind[GetCommodityCodesService].toInstance(mockGetCommodityCodesService)
      )
      .build()

    val view: ItemCommodityCodeView = application.injector.instanceOf[ItemCommodityCodeView]
  }

  "ItemCommodityCode Controller" - {
    "must return OK and the correct view for a GET when a list of commodity codes are returned" in new Fixture(
      userAnswers = Some(emptyUserAnswers.set(ItemExciseProductCodePage(testIndex1), testEpc))
    ) {
      running(application) {
        (mockGetCommodityCodesService.getCommodityCodes(_: String)(_: HeaderCarrier)).expects(testEpc, *).returns(Future.successful(Seq(
          testCommodityCode1,
          testCommodityCode2
        )))

        val request = FakeRequest(GET, itemCommodityCodeRoute)

        val result = route(application, request).value

        val view = application.injector.instanceOf[ItemCommodityCodeView]

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form, submitCall, testGoodsType, Seq(testCommodityCode1, testCommodityCode2))(dataRequest(request), messages(application)).toString
      }
    }

    "must return OK and the correct view with the previous answer for a GET when a list of commodity codes are returned" in new Fixture(
      userAnswers = Some(
        emptyUserAnswers
          .set(ItemExciseProductCodePage(testIndex1), testEpc)
          .set(ItemCommodityCodePage(testIndex1), testCommodityCode1.cnCode)
      )
    ) {
      running(application) {
        (mockGetCommodityCodesService.getCommodityCodes(_: String)(_: HeaderCarrier)).expects(testEpc, *).returns(Future.successful(Seq(
          testCommodityCode1,
          testCommodityCode2
        )))

        val request = FakeRequest(GET, itemCommodityCodeRoute)

        val result = route(application, request).value

        val view = application.injector.instanceOf[ItemCommodityCodeView]

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form.fill(testCommodityCode1.cnCode), submitCall, testGoodsType, Seq(testCommodityCode1, testCommodityCode2))(dataRequest(request), messages(application)).toString
      }
    }

    "must redirect to the confirmation page for a GET when a single commodity code is returned" in new Fixture(
      userAnswers = Some(emptyUserAnswers.set(ItemExciseProductCodePage(testIndex1), testEpc))
    ) {
      running(application) {
        MockUserAnswersService.set().returns(Future.successful(emptyUserAnswers))

        (mockGetCommodityCodesService.getCommodityCodes(_: String)(_: HeaderCarrier)).expects(testEpc, *).returns(Future.successful(Seq(
          testCommodityCode1,
        )))

        val request = FakeRequest(GET, itemCommodityCodeRoute)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual onwardRoute.url
      }
    }

    "must redirect to the confirmation page for a GET when no commodity codes are returned" in new Fixture(
      userAnswers = Some(emptyUserAnswers.set(ItemExciseProductCodePage(testIndex1), testEpc))
    ) {
      running(application) {
        MockUserAnswersService.set().returns(Future.successful(emptyUserAnswers))

        (mockGetCommodityCodesService.getCommodityCodes(_: String)(_: HeaderCarrier)).expects(testEpc, *).returns(Future.successful(Nil))

        val request = FakeRequest(GET, itemCommodityCodeRoute)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual onwardRoute.url
      }
    }

    "must redirect to the next page when valid data is submitted" in new Fixture(
      userAnswers = Some(emptyUserAnswers.set(ItemExciseProductCodePage(testIndex1), testEpc))
    ) {
      MockUserAnswersService.set().returns(Future.successful(emptyUserAnswers))

      running(application) {
        val request =
          FakeRequest(POST, itemCommodityCodeRoute)
            .withFormUrlEncodedBody(("item-commodity-code", "answer"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual onwardRoute.url
      }
    }

    "must return a Bad Request and errors when invalid data is submitted" in new Fixture(
      userAnswers = Some(emptyUserAnswers.set(ItemExciseProductCodePage(testIndex1), testEpc))
    ) {
      (mockGetCommodityCodesService.getCommodityCodes(_: String)(_: HeaderCarrier)).expects(testEpc, *).returns(Future.successful(Seq(
        testCommodityCode1,
        testCommodityCode2
      )))

      running(application) {
        val request =
          FakeRequest(POST, itemCommodityCodeRoute)
            .withFormUrlEncodedBody(("item-commodity-code", ""))

        val boundForm = form.bind(Map("item-commodity-code" -> ""))

        val view = application.injector.instanceOf[ItemCommodityCodeView]

        val result = route(application, request).value

        status(result) mustEqual BAD_REQUEST
        contentAsString(result) mustEqual view(boundForm, submitCall, testGoodsType, Seq(testCommodityCode1, testCommodityCode2))(dataRequest(request), messages(application)).toString
      }
    }

    "must redirect to Item Index Controller for a GET if no existing data is found" in new Fixture(
      userAnswers = Some(emptyUserAnswers)
    ) {
      running(application) {
        val request = FakeRequest(GET, itemCommodityCodeRoute)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual itemIndexRoute
      }
    }
  }
}