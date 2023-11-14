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
import forms.sections.items.ItemGeographicalIndicationChoiceFormProvider
import mocks.services.MockUserAnswersService
import models.GoodsTypeModel.Beer
import models.NormalMode
import models.sections.items.ItemGeographicalIndicationType.{ProtectedDesignationOfOrigin, ProtectedGeographicalIndication}
import navigation.FakeNavigators.FakeItemsNavigator
import navigation.ItemsNavigator
import pages.sections.items.{ItemExciseProductCodePage, ItemGeographicalIndicationChoicePage}
import play.api.inject.bind
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.UserAnswersService
import views.html.sections.items.ItemGeographicalIndicationChoiceView

import scala.concurrent.Future

class ItemGeographicalIndicationChoiceControllerSpec extends SpecBase with MockUserAnswersService {

  val formProvider = new ItemGeographicalIndicationChoiceFormProvider()
  val form = formProvider()
  val baseUserAnswers = emptyUserAnswers.set(ItemExciseProductCodePage(testIndex1), "B000")

  val action = controllers.sections.items.routes.ItemGeographicalIndicationChoiceController.onSubmit(testErn, testDraftId, testIndex1, NormalMode)

  lazy val itemGeographicalIndicationChoiceRoute = routes.ItemGeographicalIndicationChoiceController.onPageLoad(testErn, testDraftId, testIndex1, NormalMode).url

  "ItemGeographicalIndicationChoice Controller" - {

    "must return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(baseUserAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, itemGeographicalIndicationChoiceRoute)

        val result = route(application, request).value

        val view = application.injector.instanceOf[ItemGeographicalIndicationChoiceView]

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form, action, Beer)(dataRequest(request), messages(application)).toString
      }
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = baseUserAnswers.set(ItemGeographicalIndicationChoicePage(testIndex1), ProtectedDesignationOfOrigin)

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, itemGeographicalIndicationChoiceRoute)

        val view = application.injector.instanceOf[ItemGeographicalIndicationChoiceView]

        val result = route(application, request).value

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form.fill(ProtectedDesignationOfOrigin), action, Beer)(dataRequest(request), messages(application)).toString
      }
    }

    "must redirect to the next page when valid data is submitted" in {

      MockUserAnswersService.set(
        baseUserAnswers.set(ItemGeographicalIndicationChoicePage(testIndex1), ProtectedGeographicalIndication)
      ).returns(Future.successful(baseUserAnswers))

      val application =
        applicationBuilder(userAnswers = Some(baseUserAnswers))
          .overrides(
            bind[ItemsNavigator].toInstance(new FakeItemsNavigator(testOnwardRoute)),
            bind[UserAnswersService].toInstance(mockUserAnswersService)
          )
          .build()

      running(application) {
        val request =
          FakeRequest(POST, itemGeographicalIndicationChoiceRoute)
            .withFormUrlEncodedBody(("value", "PGI"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual testOnwardRoute.url
      }
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      val application = applicationBuilder(userAnswers = Some(baseUserAnswers)).build()

      running(application) {
        val request =
          FakeRequest(POST, itemGeographicalIndicationChoiceRoute)
            .withFormUrlEncodedBody(("value", ""))

        val boundForm = form.bind(Map("value" -> ""))

        val view = application.injector.instanceOf[ItemGeographicalIndicationChoiceView]

        val result = route(application, request).value

        status(result) mustEqual BAD_REQUEST
        contentAsString(result) mustEqual view(boundForm, action, Beer)(dataRequest(request), messages(application)).toString
      }
    }

    "must redirect to Journey Recovery for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      running(application) {
        val request = FakeRequest(GET, itemGeographicalIndicationChoiceRoute)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual controllers.routes.JourneyRecoveryController.onPageLoad().url
      }
    }

    "must redirect to Journey Recovery for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      running(application) {
        val request =
          FakeRequest(POST, itemGeographicalIndicationChoiceRoute)
            .withFormUrlEncodedBody(("value", "true"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual controllers.routes.JourneyRecoveryController.onPageLoad().url
      }
    }
  }
}
