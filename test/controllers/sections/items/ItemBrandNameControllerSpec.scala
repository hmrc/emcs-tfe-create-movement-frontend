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
import forms.sections.items.ItemBrandNameFormProvider
import mocks.services.MockUserAnswersService
import models.{BrandNameModel, NormalMode}
import navigation.FakeNavigators.FakeNavigator
import navigation.Navigator
import pages.sections.items.ItemBrandNamePage
import play.api.inject.bind
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.UserAnswersService
import views.html.sections.items.ItemBrandNameView

import scala.concurrent.Future

class ItemBrandNameControllerSpec extends SpecBase with MockUserAnswersService {

  def onwardRoute = Call("GET", "/foo")

  val formProvider = new ItemBrandNameFormProvider()
  val form = formProvider()

  lazy val itemBrandNameRoute = routes.ItemBrandNameController.onPageLoad(testErn, testDraftId, testIndex1, NormalMode).url
  lazy val itemBrandNameSubmitAction = routes.ItemBrandNameController.onSubmit(testErn, testDraftId, testIndex1, NormalMode)

  "ItemBrandName Controller" - {

    "must return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, itemBrandNameRoute)

        val result = route(application, request).value

        val view = application.injector.instanceOf[ItemBrandNameView]

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form, itemBrandNameSubmitAction, None)(dataRequest(request), messages(application)).toString
      }
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = emptyUserAnswers.set(ItemBrandNamePage(testIndex1), BrandNameModel(hasBrandName = true, Some("brand")))

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, itemBrandNameRoute)

        val view = application.injector.instanceOf[ItemBrandNameView]

        val result = route(application, request).value

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(
          form.fill(BrandNameModel(hasBrandName = true, Some("brand"))),
          itemBrandNameSubmitAction,
          None
        )(dataRequest(request), messages(application)).toString
      }
    }

    "must redirect to the next page when valid data is submitted" in {

      MockUserAnswersService.set().returns(Future.successful(emptyUserAnswers))

      val application =
        applicationBuilder(userAnswers = Some(emptyUserAnswers))
          .overrides(
            bind[Navigator].toInstance(new FakeNavigator(onwardRoute)),
            bind[UserAnswersService].toInstance(mockUserAnswersService)
          )
          .build()

      running(application) {
        val request =
          FakeRequest(POST, itemBrandNameRoute)
            .withFormUrlEncodedBody(("value", "true"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual onwardRoute.url
      }
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      running(application) {
        val request =
          FakeRequest(POST, itemBrandNameRoute)
            .withFormUrlEncodedBody(("value", ""))

        val boundForm = form.bind(Map("value" -> ""))

        val view = application.injector.instanceOf[ItemBrandNameView]

        val result = route(application, request).value

        status(result) mustEqual BAD_REQUEST
        contentAsString(result) mustEqual view(boundForm, itemBrandNameSubmitAction, None)(dataRequest(request), messages(application)).toString
      }
    }

    "must redirect to Journey Recovery for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      running(application) {
        val request = FakeRequest(GET, itemBrandNameRoute)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual controllers.routes.JourneyRecoveryController.onPageLoad().url
      }
    }

    "must redirect to Journey Recovery for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      running(application) {
        val request =
          FakeRequest(POST, itemBrandNameRoute)
            .withFormUrlEncodedBody(("value", "true"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual controllers.routes.JourneyRecoveryController.onPageLoad().url
      }
    }
  }
}
