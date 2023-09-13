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

package controllers.auth

import base.SpecBase
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.auth.SignedOutView

class SignedOutControllerSpec extends SpecBase {

  "SignedOut Controller" - {

    "must return OK and the correct view for a GET" - {
      "When there is no referer in the session" in {

        val application = applicationBuilder(userAnswers = None).build()

        running(application) {
          val request = FakeRequest(GET, routes.SignedOutController.onPageLoad.url)

          val result = route(application, request).value

          val view = application.injector.instanceOf[SignedOutView]

          status(result) mustEqual OK
          contentAsString(result) mustEqual view(guidance = "signedOut.guidance.saved")(request, messages(application)).toString
          await(result).session(request).get(REFERER_SESSION_KEY) mustBe None
        }
      }
      "When there is a referer in the session which is not an INFO page" in {

        val application = applicationBuilder(userAnswers = None).build()

        running(application) {
          val request = FakeRequest(GET, routes.SignedOutController.onPageLoad.url)
            .withSession("Referer" -> "my/test/url")

          val result = route(application, request).value

          val view = application.injector.instanceOf[SignedOutView]

          status(result) mustEqual OK
          contentAsString(result) mustEqual view(guidance = "signedOut.guidance.saved")(request, messages(application)).toString
          session(result).get(REFERER_SESSION_KEY) mustBe None
        }
      }
      "When there is a referer in the session which is an INFO page" in {
        val application = applicationBuilder(userAnswers = None).build()

        running(application) {
          val request = FakeRequest(GET, routes.SignedOutController.onPageLoad.url)
            .withSession("Referer" -> controllers.routes.DeferredMovementController.onPageLoad(testErn).url)

          val result = route(application, request).value

          val view = application.injector.instanceOf[SignedOutView]

          status(result) mustEqual OK
          contentAsString(result) mustEqual view(guidance = "signedOut.guidance.notSaved")(request, messages(application)).toString
          session(result).get(REFERER_SESSION_KEY) mustBe None
        }
      }
    }
  }
}
