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
import forms.sections.items.ItemFiscalMarksChoiceFormProvider
import mocks.services.MockUserAnswersService
import models.GoodsTypeModel.Tobacco
import models.{Index, NormalMode, UserAnswers}
import navigation.FakeNavigators.FakeItemsNavigator
import navigation.ItemsNavigator
import pages.sections.items.{ItemExciseProductCodePage, ItemFiscalMarksChoicePage}
import play.api.i18n.Messages
import play.api.inject.bind
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.UserAnswersService
import views.html.sections.items.ItemFiscalMarksChoiceView

import scala.concurrent.Future

class ItemFiscalMarksChoiceControllerSpec extends SpecBase with MockUserAnswersService {

  val formProvider = new ItemFiscalMarksChoiceFormProvider()

  val baseUserAnswers = emptyUserAnswers.set(ItemExciseProductCodePage(testIndex1), "T200")

  class Fixture(userAnswers: Option[UserAnswers]) {
    val application = applicationBuilder(userAnswers = userAnswers)
      .overrides(
        bind[ItemsNavigator].toInstance(new FakeItemsNavigator(testOnwardRoute)),
        bind[UserAnswersService].toInstance(mockUserAnswersService)
      ).build()
    implicit val msgs: Messages = messages(application)
    val form = formProvider(Tobacco)
  }

  val action = controllers.sections.items.routes.ItemFiscalMarksChoiceController.onSubmit(testErn, testDraftId, testIndex1, NormalMode)

  def itemFiscalMarksChoiceRoute(index: Index = testIndex1): String =
    routes.ItemFiscalMarksChoiceController.onPageLoad(testErn, testDraftId, index, NormalMode).url

  "ItemFiscalMarksChoice Controller" - {

    "must redirect to Index of section when the idx is outside of bounds for a GET" in new Fixture(Some(baseUserAnswers)) {

      running(application) {

        val request = FakeRequest(GET, itemFiscalMarksChoiceRoute(testIndex2))
        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustBe routes.ItemsIndexController.onPageLoad(testErn, testDraftId).url
      }
    }

    "must redirect to Index of section when the idx is outside of bounds for a POST" in new Fixture(Some(baseUserAnswers)) {

      running(application) {

        val request = FakeRequest(POST, itemFiscalMarksChoiceRoute(testIndex2)).withFormUrlEncodedBody(("value", "1"))
        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustBe routes.ItemsIndexController.onPageLoad(testErn, testDraftId).url
      }
    }

    "must redirect to Index of section when Excise Product Code is missing (for GET)" in new Fixture(Some(emptyUserAnswers)) {

      running(application) {

        val request = FakeRequest(GET, itemFiscalMarksChoiceRoute(testIndex1))
        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustBe routes.ItemsIndexController.onPageLoad(testErn, testDraftId).url
      }
    }

    "must redirect to Index of section when Excise Product Code is missing (for POST)" in new Fixture(Some(emptyUserAnswers)) {

      running(application) {

        val request = FakeRequest(POST, itemFiscalMarksChoiceRoute(testIndex1))
        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustBe routes.ItemsIndexController.onPageLoad(testErn, testDraftId).url
      }
    }

    "must return OK and the correct view for a GET" in new Fixture(Some(baseUserAnswers)) {

      running(application) {
        val request = FakeRequest(GET, itemFiscalMarksChoiceRoute(testIndex1))

        val result = route(application, request).value

        val view = application.injector.instanceOf[ItemFiscalMarksChoiceView]

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form, action, Tobacco)(dataRequest(request), messages(application)).toString
      }
    }

    "must populate the view correctly on a GET when the question has previously been answered" in new Fixture(
      Some(baseUserAnswers.set(ItemFiscalMarksChoicePage(testIndex1), true))
    ) {

      running(application) {
        val request = FakeRequest(GET, itemFiscalMarksChoiceRoute(testIndex1))

        val view = application.injector.instanceOf[ItemFiscalMarksChoiceView]

        val result = route(application, request).value

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form.fill(true), action, Tobacco)(dataRequest(request), messages(application)).toString
      }
    }

    "must redirect to the next page when valid data is submitted" in new Fixture(Some(baseUserAnswers)) {

      MockUserAnswersService.set(
        baseUserAnswers.set(ItemFiscalMarksChoicePage(testIndex1), true)
      ).returns(Future.successful(
        baseUserAnswers.set(ItemFiscalMarksChoicePage(testIndex1), true)
      ))

      running(application) {
        val request =
          FakeRequest(POST, itemFiscalMarksChoiceRoute(testIndex1))
            .withFormUrlEncodedBody(("value", "true"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual testOnwardRoute.url
      }
    }

    "must return a Bad Request and errors when invalid data is submitted" in new Fixture(Some(baseUserAnswers)) {

      running(application) {
        val request =
          FakeRequest(POST, itemFiscalMarksChoiceRoute(testIndex1))
            .withFormUrlEncodedBody(("value", ""))

        val boundForm = form.bind(Map("value" -> ""))

        val view = application.injector.instanceOf[ItemFiscalMarksChoiceView]

        val result = route(application, request).value

        status(result) mustEqual BAD_REQUEST
        contentAsString(result) mustEqual view(boundForm, action, Tobacco)(dataRequest(request), messages(application)).toString
      }
    }

    "must redirect to Journey Recovery for a GET if no existing data is found" in new Fixture(None) {

      running(application) {
        val request = FakeRequest(GET, itemFiscalMarksChoiceRoute(testIndex1))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual controllers.routes.JourneyRecoveryController.onPageLoad().url
      }
    }

    "must redirect to Journey Recovery for a POST if no existing data is found" in new Fixture(None) {

      running(application) {
        val request =
          FakeRequest(POST, itemFiscalMarksChoiceRoute(testIndex1))
            .withFormUrlEncodedBody(("value", "true"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual controllers.routes.JourneyRecoveryController.onPageLoad().url
      }
    }
  }
}
