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

package controllers

import base.SpecBase
import config.SessionKeys
import forms.LocalReferenceNumberFormProvider
import mocks.services.MockUserAnswersService
import models.UserAnswers
import navigation.{FakeNavigator, Navigator}
import pages.{DeferredMovementPage, LocalReferenceNumberPage}
import play.api.inject.bind
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.UserAnswersService
import views.html.LocalReferenceNumberView

import scala.concurrent.Future

class LocalReferenceNumberControllerSpec extends SpecBase with MockUserAnswersService {

  class Fixture(val userAnswers: Option[UserAnswers] = Some(emptyUserAnswers)) {
    val application =
      applicationBuilder(userAnswers)
        .overrides(
          bind[Navigator].toInstance(new FakeNavigator(testOnwardRoute)),
          bind[UserAnswersService].toInstance(mockUserAnswersService)
        )
        .build()

    val view = application.injector.instanceOf[LocalReferenceNumberView]
  }

  val formProvider = new LocalReferenceNumberFormProvider()
  val form = formProvider(isDeferred = false)

  lazy val localReferenceNumberRoute = routes.LocalReferenceNumberController.onPageLoad(testErn).url
  lazy val localReferenceNumberSubmitAction = routes.LocalReferenceNumberController.onSubmit(testErn)

  "LocalReferenceNumberController" - {

    ".onPageLoad()" - {

      "when the Deferred Movement answer exists in session" - {

        "must return OK and the correct view for a GET" in new Fixture() {
          running(application) {

            val request = FakeRequest(GET, localReferenceNumberRoute).withSession(SessionKeys.DEFERRED_MOVEMENT -> "false")
            val result = route(application, request).value

            status(result) mustEqual OK
            contentAsString(result) mustEqual view(isDeferred = false, form, localReferenceNumberSubmitAction)(userRequest(request), messages(application)).toString
          }
        }
      }

      "when the Deferred Movement answer DOES NOT exist in session" - {

        //TODO: Update when page built to capture the Deferred Movement answer
        "must return SEE_OTHER and redirect to the Deferred Movement page" in new Fixture() {
          running(application) {

            val request = FakeRequest(GET, localReferenceNumberRoute)
            val result = route(application, request).value

            status(result) mustEqual SEE_OTHER
            redirectLocation(result) mustBe Some(testOnly.controllers.routes.UnderConstructionController.onPageLoad().url)
          }
        }
      }
    }

    ".onSubmit()" - {

      "must redirect to the LRN already used page when valid LRN is submitted but a draft already exists" in new Fixture() {
        running(application) {

          MockUserAnswersService.get(testErn, testLrn).returns(Future.successful(Some(emptyUserAnswers)))

          val request =
            FakeRequest(POST, localReferenceNumberRoute)
            .withFormUrlEncodedBody(("value", testLrn))
            .withSession(SessionKeys.DEFERRED_MOVEMENT -> "false")

          val result = route(application, request).value

          status(result) mustEqual SEE_OTHER
          redirectLocation(result).value mustEqual testOnly.controllers.routes.UnderConstructionController.onPageLoad().url
        }
      }

      "must redirect to the next page when valid data is submitted and initialise (No Existing LRN found)" in new Fixture() {
        running(application) {

          val expectedAnswers =
            emptyUserAnswers
              .set(DeferredMovementPage, false)
              .set(LocalReferenceNumberPage, testLrn)

          MockUserAnswersService.get(testErn, testLrn).returns(Future.successful(None))
          MockUserAnswersService.set(expectedAnswers).returns(Future.successful(expectedAnswers))

          val request =
            FakeRequest(POST, localReferenceNumberRoute)
              .withFormUrlEncodedBody(("value", testLrn))
              .withSession(SessionKeys.DEFERRED_MOVEMENT -> "false")

          val result = route(application, request).value

          status(result) mustEqual SEE_OTHER
          redirectLocation(result).value mustEqual testOnwardRoute.url
        }
      }

      "must return a Bad Request and errors when invalid data is submitted" in new Fixture() {
        running(application) {

          val request =
            FakeRequest(POST, localReferenceNumberRoute)
              .withFormUrlEncodedBody(("value", ""))
              .withSession(SessionKeys.DEFERRED_MOVEMENT -> "false")

          val boundForm = form.bind(Map("value" -> ""))
          val result = route(application, request).value

          status(result) mustEqual BAD_REQUEST
          contentAsString(result) mustEqual view(isDeferred = false, boundForm, localReferenceNumberSubmitAction)(userRequest(request), messages(application)).toString
        }
      }
    }
  }
}