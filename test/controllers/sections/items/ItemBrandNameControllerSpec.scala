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
import fixtures.messages.sections.items.ItemBrandNameMessages
import forms.sections.items.ItemBrandNameFormProvider
import forms.sections.items.ItemBrandNameFormProvider.{brandNameField, hasBrandNameField}
import mocks.services.MockUserAnswersService
import models.GoodsTypeModel.Wine
import models.sections.items.ItemBrandNameModel
import models.{Index, NormalMode, UserAnswers}
import navigation.FakeNavigators.FakeItemsNavigator
import navigation.ItemsNavigator
import pages.sections.items.{ItemBrandNamePage, ItemExciseProductCodePage}
import play.api.inject.bind
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.UserAnswersService
import views.html.sections.items.ItemBrandNameView

import scala.concurrent.Future

class ItemBrandNameControllerSpec extends SpecBase with MockUserAnswersService {

  //Ensures a dummy item exists in the array for testing
  val defaultUserAnswers = emptyUserAnswers.set(ItemExciseProductCodePage(testIndex1), "W200")

  val formProvider = new ItemBrandNameFormProvider()
  val form = formProvider()

  def itemBrandNameRoute(idx: Index = testIndex1) = routes.ItemBrandNameController.onPageLoad(testErn, testDraftId, idx, NormalMode).url
  def itemBrandNameSubmitAction(idx: Index = testIndex1) = routes.ItemBrandNameController.onSubmit(testErn, testDraftId, idx, NormalMode)

  class Fixture(val userAnswers: Option[UserAnswers] = Some(defaultUserAnswers)) {

    val application = applicationBuilder(userAnswers)
      .overrides(
        bind[ItemsNavigator].toInstance(new FakeItemsNavigator(testOnwardRoute)),
        bind[UserAnswersService].toInstance(mockUserAnswersService)
      )
      .build()

    val view = application.injector.instanceOf[ItemBrandNameView]
  }

  "ItemBrandName Controller" - {

    "must redirect to Index of section when the idx is outside of bounds for a GET" in new Fixture() {
      running(application) {

        val request = FakeRequest(GET, itemBrandNameRoute(testIndex2))
        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result) mustBe Some(routes.ItemsIndexController.onPageLoad(testErn, testDraftId).url)
      }
    }

    "must redirect to Index of section when the idx is outside of bounds for a POST" in new Fixture() {
      running(application) {

        val request = FakeRequest(POST, itemBrandNameRoute(testIndex2)).withFormUrlEncodedBody((hasBrandNameField, "false"))
        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result) mustBe Some(routes.ItemsIndexController.onPageLoad(testErn, testDraftId).url)
      }
    }

    "must return OK and the correct view for a GET" in new Fixture() {
      running(application) {

        val request = FakeRequest(GET, itemBrandNameRoute())
        val result = route(application, request).value

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form, itemBrandNameSubmitAction(), Some(Wine))(dataRequest(request), messages(application)).toString
      }
    }

    "must populate the view correctly on a GET when the question has previously been answered" in new Fixture(
      Some(defaultUserAnswers.set(ItemBrandNamePage(testIndex1), ItemBrandNameModel(hasBrandName = true, Some("brand"))))
    ) {
      running(application) {

        val request = FakeRequest(GET, itemBrandNameRoute())
        val result = route(application, request).value

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(
          form.fill(ItemBrandNameModel(hasBrandName = true, Some("brand"))),
          itemBrandNameSubmitAction(),
          Some(Wine)
        )(dataRequest(request), messages(application)).toString
      }
    }

    "must redirect to the next page when valid data is submitted" in new Fixture() {
      running(application) {

        MockUserAnswersService.set().returns(Future.successful(emptyUserAnswers))

        val request = FakeRequest(POST, itemBrandNameRoute()).withFormUrlEncodedBody((hasBrandNameField, "false"))
        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual testOnwardRoute.url
      }
    }

    "must return a Bad Request and errors when invalid data is submitted (no brand name supplied)" in new Fixture() {
      running(application) {

        val request = FakeRequest(POST, itemBrandNameRoute()).withFormUrlEncodedBody((hasBrandNameField, "true"))
        val boundForm =
          form
            .bind(Map(hasBrandNameField -> "true"))
            .withError(brandNameField, ItemBrandNameMessages.English.errorBrandNameRequired)
        val result = route(application, request).value

        status(result) mustEqual BAD_REQUEST
        contentAsString(result) mustEqual view(boundForm, itemBrandNameSubmitAction(), Some(Wine))(dataRequest(request), messages(application)).toString
      }
    }

    "must redirect to Journey Recovery for a GET if no existing data is found" in new Fixture(None) {
      running(application) {

        val request = FakeRequest(GET, itemBrandNameRoute())
        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual controllers.routes.JourneyRecoveryController.onPageLoad().url
      }
    }

    "must redirect to Journey Recovery for a POST if no existing data is found" in new Fixture(None) {
      running(application) {

        val request = FakeRequest(POST, itemBrandNameRoute()).withFormUrlEncodedBody(("value", "true"))
        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual controllers.routes.JourneyRecoveryController.onPageLoad().url
      }
    }
  }
}
