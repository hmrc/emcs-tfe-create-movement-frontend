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
import forms.sections.items.CommercialDescriptionFormProvider
import mocks.services.MockUserAnswersService
import models.GoodsTypeModel.Wine
import models.{Index, NormalMode}
import navigation.FakeNavigators.FakeItemsNavigator
import navigation.ItemsNavigator
import pages.sections.items.{CommercialDescriptionPage, ItemExciseProductCodePage}
import play.api.inject.bind
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.UserAnswersService
import views.html.sections.items.CommercialDescriptionView

import scala.concurrent.Future

class CommercialDescriptionControllerSpec extends SpecBase with MockUserAnswersService {

  def onwardRoute = Call("GET", "/foo")

  val formProvider = new CommercialDescriptionFormProvider()
  val form = formProvider()

  lazy val commercialDescriptionRoute = routes.CommercialDescriptionController.onPageLoad(testErn, testDraftId, testIndex1, NormalMode).url
  def itemCommercialDescriptionSubmitAction(idx: Index = testIndex1) = routes.CommercialDescriptionController.onSubmit(testErn, testDraftId, idx, NormalMode)

  "CommercialDescription Controller" - {

    "must return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers.set(ItemExciseProductCodePage(testIndex1), "W200"))).build()

      running(application) {
        val request = FakeRequest(GET, commercialDescriptionRoute)

        val result = route(application, request).value

        val view = application.injector.instanceOf[CommercialDescriptionView]

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form, itemCommercialDescriptionSubmitAction(), Wine)(dataRequest(request), messages(application)).toString
      }
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = emptyUserAnswers.set(CommercialDescriptionPage(testIndex1), "answer").set(ItemExciseProductCodePage(testIndex1), "W200")

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, commercialDescriptionRoute)

        val view = application.injector.instanceOf[CommercialDescriptionView]

        val result = route(application, request).value

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form.fill("answer"),itemCommercialDescriptionSubmitAction(), Wine)(dataRequest(request), messages(application)).toString
      }
    }

    "must redirect to the next page when valid data is submitted" in {

      MockUserAnswersService.set().returns(Future.successful(emptyUserAnswers))

      val application =
        applicationBuilder(userAnswers = Some(emptyUserAnswers.set(ItemExciseProductCodePage(testIndex1), "W200")))
          .overrides(
            bind[ItemsNavigator].toInstance(new FakeItemsNavigator(onwardRoute)),
            bind[UserAnswersService].toInstance(mockUserAnswersService)
          )
          .build()

      running(application) {
        val request =
          FakeRequest(POST, commercialDescriptionRoute)
            .withFormUrlEncodedBody(("value", "answer"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual onwardRoute.url
      }
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers.set(ItemExciseProductCodePage(testIndex1), "W200"))).build()

      running(application) {
        val request =
          FakeRequest(POST, commercialDescriptionRoute)
            .withFormUrlEncodedBody(("value", ""))

        val boundForm = form.bind(Map("value" -> ""))

        val view = application.injector.instanceOf[CommercialDescriptionView]

        val result = route(application, request).value

        status(result) mustEqual BAD_REQUEST
        contentAsString(result) mustEqual view(boundForm,itemCommercialDescriptionSubmitAction(), Wine)(dataRequest(request), messages(application)).toString
      }
    }

    "must redirect to Journey Recovery for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      running(application) {
        val request = FakeRequest(GET, commercialDescriptionRoute)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual controllers.routes.JourneyRecoveryController.onPageLoad().url
      }
    }

    "must redirect to Journey Recovery for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      running(application) {
        val request =
          FakeRequest(POST, commercialDescriptionRoute)
            .withFormUrlEncodedBody(("value", "answer"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual controllers.routes.JourneyRecoveryController.onPageLoad().url
      }
    }
  }
}
