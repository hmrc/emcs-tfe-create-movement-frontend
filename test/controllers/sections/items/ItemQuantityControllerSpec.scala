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
import forms.sections.items.ItemQuantityFormProvider
import handlers.ErrorHandler
import mocks.services.{MockGetCnCodeInformationService, MockUserAnswersService}
import models.GoodsTypeModel.Wine
import models.UnitOfMeasure.Litres20
import models.requests.CnCodeInformationItem
import models.response.referenceData.CnCodeInformation
import models.{Index, NormalMode, UserAnswers}
import navigation.FakeNavigators.FakeItemsNavigator
import navigation.ItemsNavigator
import pages.sections.items.{ItemCommodityCodePage, ItemExciseProductCodePage, ItemQuantityPage}
import play.api.inject.bind
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.{GetCnCodeInformationService, UserAnswersService}
import views.html.sections.items.ItemQuantityView

import scala.concurrent.Future

class ItemQuantityControllerSpec extends SpecBase with MockUserAnswersService with MockGetCnCodeInformationService {

  val item = CnCodeInformationItem("W200", "22042111")

  //Ensures a dummy item exists in the array for testing
  val defaultUserAnswers = emptyUserAnswers
    .set(ItemExciseProductCodePage(testIndex1), item.productCode)
    .set(ItemCommodityCodePage(testIndex1), item.cnCode)

  val formProvider = new ItemQuantityFormProvider()
  val form = formProvider()


  def itemQuantityRoute(idx: Index = testIndex1) = routes.ItemQuantityController.onPageLoad(testErn, testDraftId, idx, NormalMode).url
  def itemQuantitySubmitAction(idx: Index = testIndex1) = routes.ItemQuantityController.onSubmit(testErn, testDraftId, idx, NormalMode)

  class Fixture(val userAnswers: Option[UserAnswers] = Some(defaultUserAnswers)) {

    val application = applicationBuilder(userAnswers)
      .overrides(
        bind[ItemsNavigator].toInstance(new FakeItemsNavigator(testOnwardRoute)),
        bind[UserAnswersService].toInstance(mockUserAnswersService),
        bind[GetCnCodeInformationService].toInstance(mockGetCnCodeInformationService)
      )
      .build()

    val view = application.injector.instanceOf[ItemQuantityView]
    val errorHandler = application.injector.instanceOf[ErrorHandler]
  }

  "ItemQuantity Controller" - {

    "must redirect to Index of section when the idx is outside of bounds for a GET" in new Fixture() {
      running(application) {

        val request = FakeRequest(GET, itemQuantityRoute(testIndex2))
        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustBe routes.ItemsIndexController.onPageLoad(testErn, testDraftId).url
      }
    }

    "must redirect to Index of section when the idx is outside of bounds for a POST" in new Fixture() {
      running(application) {

        val request = FakeRequest(POST, itemQuantityRoute(testIndex2)).withFormUrlEncodedBody(("value", "1"))
        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustBe routes.ItemsIndexController.onPageLoad(testErn, testDraftId).url
      }
    }

    "must redirect to Index of section when Excise Product Code is missing" in new Fixture(
      Some(defaultUserAnswers.remove(ItemExciseProductCodePage(testIndex1)))
    ) {
      running(application) {

        val request = FakeRequest(GET, itemQuantityRoute(testIndex1))
        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustBe routes.ItemsIndexController.onPageLoad(testErn, testDraftId).url
      }
    }

    "must redirect to Index of section when Commodity Code is missing" in new Fixture(
      Some(defaultUserAnswers.remove(ItemCommodityCodePage(testIndex1)))
    ) {
      running(application) {

        val request = FakeRequest(GET, itemQuantityRoute(testIndex1))
        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustBe routes.ItemsIndexController.onPageLoad(testErn, testDraftId).url
      }
    }

    "must return OK and the correct view for a GET" in new Fixture() {
      running(application) {

        MockGetCnCodeInformationService.getCnCodeInformationWithMovementItems(Seq(item))
          .returns(Future.successful(Seq(item -> CnCodeInformation(item.cnCode, "Sparkling Wine", item.productCode, "Wine", Litres20))))

        val request = FakeRequest(GET, itemQuantityRoute())
        val result = route(application, request).value

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form, itemQuantitySubmitAction(), Wine, Litres20)(dataRequest(request), messages(application)).toString
      }
    }

    "must populate the view correctly on a GET when the question has previously been answered" in new Fixture(
      Some(defaultUserAnswers.set(ItemQuantityPage(testIndex1), BigDecimal(1.5)))
    ) {
      running(application) {

        MockGetCnCodeInformationService.getCnCodeInformationWithMovementItems(Seq(item))
          .returns(Future.successful(Seq(item -> CnCodeInformation(item.cnCode, "Sparkling Wine", item.productCode, "Wine", Litres20))))

        val request = FakeRequest(GET, itemQuantityRoute())
        val result = route(application, request).value

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(
          form.fill(BigDecimal(1.5)),
          itemQuantitySubmitAction(),
          Wine,
          Litres20
        )(dataRequest(request), messages(application)).toString
      }
    }

    "must redirect to the next page when valid data is submitted" in new Fixture() {
      running(application) {

        MockUserAnswersService.set().returns(Future.successful(emptyUserAnswers))

        val request = FakeRequest(POST, itemQuantityRoute()).withFormUrlEncodedBody(("value", "1"))
        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual testOnwardRoute.url
      }
    }

    "must render BadRequest when invalid data is submitted" in new Fixture() {
      running(application) {

        MockGetCnCodeInformationService.getCnCodeInformationWithMovementItems(Seq(item))
          .returns(Future.successful(Seq(item -> CnCodeInformation(item.cnCode, "Sparkling Wine", item.productCode, "Wine", Litres20))))

        val request = FakeRequest(POST, itemQuantityRoute()).withFormUrlEncodedBody(("value", ""))
        val result = route(application, request).value
        val boundForm = form.bind(Map("value" -> ""))

        status(result) mustEqual BAD_REQUEST
        contentAsString(result) mustEqual view(
          boundForm,
          itemQuantitySubmitAction(),
          Wine,
          Litres20
        )(dataRequest(request), messages(application)).toString
      }
    }

    "must render Ise when no data retrieved from Reference Data for CN Code Information" in new Fixture() {
      running(application) {

        MockGetCnCodeInformationService.getCnCodeInformationWithMovementItems(Seq(item))
          .returns(Future.successful(Seq()))

        val request = FakeRequest(POST, itemQuantityRoute()).withFormUrlEncodedBody(("value", ""))
        val result = route(application, request).value

        status(result) mustEqual INTERNAL_SERVER_ERROR
        contentAsString(result) mustEqual errorHandler.internalServerErrorTemplate(request).toString
      }
    }

    "must redirect to Journey Recovery for a GET if no existing data is found" in new Fixture(None) {
      running(application) {

        val request = FakeRequest(GET, itemQuantityRoute())
        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual controllers.routes.JourneyRecoveryController.onPageLoad().url
      }
    }

    "must redirect to Journey Recovery for a POST if no existing data is found" in new Fixture(None) {
      running(application) {

        val request = FakeRequest(POST, itemQuantityRoute()).withFormUrlEncodedBody(("value", ""))
        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual controllers.routes.JourneyRecoveryController.onPageLoad().url
      }
    }
  }
}
