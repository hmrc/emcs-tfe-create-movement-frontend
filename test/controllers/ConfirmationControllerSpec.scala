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
import handlers.ErrorHandler
import models.UserAnswers
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.ConfirmationView

class ConfirmationControllerSpec extends SpecBase {

  class Fixture(userAnswers: Option[UserAnswers]) {
    val application = applicationBuilder(userAnswers).build()
    val view = application.injector.instanceOf[ConfirmationView]
    val errorHandler = application.injector.instanceOf[ErrorHandler]
    val request = FakeRequest(GET, routes.ConfirmationController.onPageLoad(testErn, testLrn).url)
  }

  "Confirmation Controller" - {

    "when the confirmation receipt reference is held in session" - {

      "must return OK and the correct view for a GET" in new Fixture(Some(emptyUserAnswers)) {

        running(application) {
          val req = dataRequest(
            request = request.withSession(SessionKeys.SUBMISSION_RECEIPT_REFERENCE -> testConfirmationReference),
            answers = emptyUserAnswers
          )

          val result = route(application, req).value

          status(result) mustEqual OK
          contentAsString(result) mustEqual view(testConfirmationReference)(req, messages(application)).toString
        }
      }
    }

    "when NO confirmation receipt reference is held in session" - {

      "must return BadRequests" in new Fixture(Some(emptyUserAnswers)) {

        running(application) {
          val req = dataRequest(request, emptyUserAnswers)

          val result = route(application, request).value

          status(result) mustEqual BAD_REQUEST
          contentAsString(result) mustEqual errorHandler.badRequestTemplate(req).toString
        }
      }
    }
  }
}
