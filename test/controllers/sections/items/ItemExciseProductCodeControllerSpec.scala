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
import fixtures.BaseFixtures
import forms.sections.items.ItemExciseProductCodeFormProvider
import mocks.services.{MockGetExciseProductCodesService, MockUserAnswersService}
import models.{Index, NormalMode, UserAnswers}
import navigation.FakeNavigators.FakeItemsNavigator
import navigation.ItemsNavigator
import pages.sections.items.ItemExciseProductCodePage
import play.api.inject.bind
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.{GetExciseProductCodesService, UserAnswersService}
import viewmodels.helpers.SelectItemHelper
import views.html.sections.items.ItemExciseProductCodeView

import scala.concurrent.Future

class ItemExciseProductCodeControllerSpec extends SpecBase with MockUserAnswersService with MockGetExciseProductCodesService with BaseFixtures {

  def onwardRoute = Call("GET", "/foo")

  val action = controllers.sections.items.routes.ItemExciseProductCodeController.onSubmit(testErn, testDraftId, testIndex1, NormalMode)

  val sampleEPCs = Seq(beerExciseProductCode, wineExciseProductCode)

  val form = new ItemExciseProductCodeFormProvider().apply(sampleEPCs)

  def exciseProductCodeRoute(index: Index = testIndex1): String = routes.ItemExciseProductCodeController.onPageLoad(testErn, testDraftId, index, NormalMode).url

  class Fixture(userAnswers: Option[UserAnswers]) {
    val application = applicationBuilder(userAnswers = userAnswers)
      .overrides(
        bind[ItemsNavigator].toInstance(new FakeItemsNavigator(onwardRoute)),
        bind[UserAnswersService].toInstance(mockUserAnswersService),
        bind[GetExciseProductCodesService].toInstance(mockGetExciseProductCodesService)
      )
      .build()

    val sampleEPCsSelectOptions = SelectItemHelper.constructSelectItems(
      selectOptions = sampleEPCs,
      defaultTextMessageKey = "itemExciseProductCode.select.defaultValue")(messages(FakeRequest()))
  }

  "ItemExciseProductCode Controller" - {

    "must redirect to Index of section when the idx is outside of bounds for a GET" in new Fixture(
      Some(emptyUserAnswers)
    ) {
      running(application) {

        val request = FakeRequest(GET, exciseProductCodeRoute(testIndex2))
        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result) mustBe Some(routes.ItemsIndexController.onPageLoad(testErn, testDraftId).url)
      }
    }

    "must redirect to Index of section when the idx is outside of bounds for a POST" in new Fixture(
      Some(emptyUserAnswers)
    ) {
      running(application) {

        val request = FakeRequest(POST, exciseProductCodeRoute(testIndex2)).withFormUrlEncodedBody("excise-product-code" -> "B000")
        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result) mustBe Some(routes.ItemsIndexController.onPageLoad(testErn, testDraftId).url)
      }
    }

    "must return OK and the correct view for a GET" in new Fixture(Some(emptyUserAnswers)) {

      MockGetExciseProductCodesService.getExciseProductCodes().returns(Future.successful(sampleEPCs))

      running(application) {
        val request = FakeRequest(GET, exciseProductCodeRoute())

        val result = route(application, request).value

        val view = application.injector.instanceOf[ItemExciseProductCodeView]

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form, action, sampleEPCsSelectOptions, NormalMode)(dataRequest(request), messages(request)).toString
      }
    }

    "must populate the view correctly on a GET when the question has previously been answered" in new Fixture(
      Some(emptyUserAnswers.set(ItemExciseProductCodePage(testIndex1), "B000"))
    ) {

      MockGetExciseProductCodesService.getExciseProductCodes().returns(Future.successful(sampleEPCs))

      running(application) {

        val request = FakeRequest(GET, exciseProductCodeRoute())

        val sampleEPCsSelectOptionsWithBeerSelected = SelectItemHelper.constructSelectItems(
          selectOptions = sampleEPCs,
          defaultTextMessageKey = "itemExciseProductCode.select.defaultValue",
          existingAnswer = Some("B000"))(messages(request))

        val view = application.injector.instanceOf[ItemExciseProductCodeView]

        val result = route(application, request).value

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form.fill("B000"), action, sampleEPCsSelectOptionsWithBeerSelected, NormalMode)(dataRequest(request), messages(request)).toString
      }
    }

    "must redirect to the next page when valid data is submitted" in new Fixture(Some(emptyUserAnswers)) {

      MockGetExciseProductCodesService.getExciseProductCodes().returns(Future.successful(sampleEPCs))

      MockUserAnswersService.set(
        emptyUserAnswers.set(ItemExciseProductCodePage(testIndex1), "W200")
      ).returns(Future.successful(emptyUserAnswers.set(ItemExciseProductCodePage(testIndex1), "W200")))

      running(application) {
        val request =
          FakeRequest(POST, exciseProductCodeRoute())
            .withFormUrlEncodedBody(("excise-product-code", "W200"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual onwardRoute.url
      }
    }

    "must return a Bad Request and errors when invalid data is submitted" in new Fixture(Some(emptyUserAnswers)) {

      MockGetExciseProductCodesService.getExciseProductCodes().returns(Future.successful(sampleEPCs))
      
      running(application) {
        val request =
          FakeRequest(POST, exciseProductCodeRoute())
            .withFormUrlEncodedBody(("excise-product-code", ""))

        val boundForm = form.bind(Map("excise-product-code" -> ""))

        val view = application.injector.instanceOf[ItemExciseProductCodeView]

        val result = route(application, request).value

        status(result) mustEqual BAD_REQUEST
        contentAsString(result) mustEqual view(boundForm, action, sampleEPCsSelectOptions, NormalMode)(dataRequest(request), messages(request)).toString
      }
    }

    "must redirect to Journey Recovery for a GET if no existing data is found" in {
      
      val application = applicationBuilder(userAnswers = None).build()

      running(application) {
        val request = FakeRequest(GET, exciseProductCodeRoute())

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual controllers.routes.JourneyRecoveryController.onPageLoad().url
      }
    }

    "must redirect to Journey Recovery for a POST if no existing data is found" in {
      
      val application = applicationBuilder(userAnswers = None).build()

      running(application) {
        val request =
          FakeRequest(POST, exciseProductCodeRoute())
            .withFormUrlEncodedBody(("excise-product-code", "answer"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual controllers.routes.JourneyRecoveryController.onPageLoad().url
      }
    }
  }
}
