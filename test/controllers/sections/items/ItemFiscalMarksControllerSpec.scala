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
import forms.sections.items.ItemFiscalMarksFormProvider
import mocks.services.MockUserAnswersService
import models.{Index, NormalMode, UserAnswers}
import navigation.FakeNavigators.FakeItemsNavigator
import navigation.ItemsNavigator
import pages.sections.items.{ItemExciseProductCodePage, ItemFiscalMarksPage}
import play.api.inject.bind
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.UserAnswersService
import views.html.sections.items.ItemFiscalMarksView

import scala.concurrent.Future

class ItemFiscalMarksControllerSpec extends SpecBase with MockUserAnswersService {
  
  val formProvider = new ItemFiscalMarksFormProvider()
  val form = formProvider()

  val baseUserAnswers = emptyUserAnswers.set(ItemExciseProductCodePage(testIndex1), "T200")

  class Fixture(userAnswers: Option[UserAnswers]) {
    val application = applicationBuilder(userAnswers = userAnswers)
      .overrides(
        bind[ItemsNavigator].toInstance(new FakeItemsNavigator(testOnwardRoute)),
        bind[UserAnswersService].toInstance(mockUserAnswersService)
      ).build()
  }
  
  val action = controllers.sections.items.routes.ItemFiscalMarksController.onSubmit(testErn, testDraftId, testIndex1, NormalMode)

  def itemFiscalMarksRoute(index: Index = testIndex1): String =
    routes.ItemFiscalMarksController.onPageLoad(testErn, testDraftId, index, NormalMode).url

  "ItemFiscalMarks Controller" - {

    "must redirect to Index of section when the idx is outside of bounds for a GET" in new Fixture(Some(baseUserAnswers)) {

      running(application) {

        val request = FakeRequest(GET, itemFiscalMarksRoute(testIndex2))
        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustBe routes.ItemsIndexController.onPageLoad(testErn, testDraftId).url
      }
    }

    "must redirect to Index of section when the idx is outside of bounds for a POST" in new Fixture(Some(baseUserAnswers)) {

      running(application) {

        val request = FakeRequest(POST, itemFiscalMarksRoute(testIndex2)).withFormUrlEncodedBody(("value", "1"))
        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustBe routes.ItemsIndexController.onPageLoad(testErn, testDraftId).url
      }
    }

    "must redirect to Index of section when Excise Product Code is missing (for GET)" in new Fixture(Some(emptyUserAnswers)) {

      running(application) {

        val request = FakeRequest(GET, itemFiscalMarksRoute(testIndex1))
        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustBe routes.ItemsIndexController.onPageLoad(testErn, testDraftId).url
      }
    }

    "must redirect to Index of section when Excise Product Code is missing (for POST)" in new Fixture(Some(emptyUserAnswers)) {

      running(application) {

        val request = FakeRequest(POST, itemFiscalMarksRoute(testIndex1))
        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustBe routes.ItemsIndexController.onPageLoad(testErn, testDraftId).url
      }
    }

    "must return OK and the correct view for a GET" in new Fixture(Some(baseUserAnswers)) {

      running(application) {
        val request = FakeRequest(GET, itemFiscalMarksRoute(testIndex1))

        val result = route(application, request).value

        val view = application.injector.instanceOf[ItemFiscalMarksView]

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form, action)(dataRequest(request), messages(application)).toString
      }
    }

    "must populate the view correctly on a GET when the question has previously been answered" in new Fixture(
      Some(baseUserAnswers.set(ItemFiscalMarksPage(testIndex1), "answer"))
    ) {

      running(application) {
        val request = FakeRequest(GET, itemFiscalMarksRoute(testIndex1))

        val view = application.injector.instanceOf[ItemFiscalMarksView]

        val result = route(application, request).value

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form.fill("answer"), action)(dataRequest(request), messages(application)).toString
      }
    }

    "must redirect to the next page when valid data is submitted" in new Fixture(Some(baseUserAnswers)) {

      MockUserAnswersService.set(
        baseUserAnswers.set(ItemFiscalMarksPage(testIndex1), "answer")
      ).returns(Future.successful(
        baseUserAnswers.set(ItemFiscalMarksPage(testIndex1), "answer")
      ))

      running(application) {
        val request =
          FakeRequest(POST, itemFiscalMarksRoute(testIndex1))
            .withFormUrlEncodedBody(("value", "answer"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual testOnwardRoute.url
      }
    }

    "must return a Bad Request and errors when invalid data is submitted" in new Fixture(Some(baseUserAnswers)) {

      running(application) {
        val request =
          FakeRequest(POST, itemFiscalMarksRoute(testIndex1))
            .withFormUrlEncodedBody(("value", ""))

        val boundForm = form.bind(Map("value" -> ""))

        val view = application.injector.instanceOf[ItemFiscalMarksView]

        val result = route(application, request).value

        status(result) mustEqual BAD_REQUEST
        contentAsString(result) mustEqual view(boundForm, action)(dataRequest(request), messages(application)).toString
      }
    }

    "must redirect to Journey Recovery for a GET if no existing data is found" in new Fixture(None) {

      running(application) {
        val request = FakeRequest(GET, itemFiscalMarksRoute(testIndex1))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual controllers.routes.JourneyRecoveryController.onPageLoad().url
      }
    }

    "must redirect to Journey Recovery for a POST if no existing data is found" in new Fixture(None) {

      running(application) {
        val request =
          FakeRequest(POST, itemFiscalMarksRoute(testIndex1))
            .withFormUrlEncodedBody(("value", "answer"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual controllers.routes.JourneyRecoveryController.onPageLoad().url
      }
    }
  }
}
