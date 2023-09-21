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
import handlers.ErrorHandler
import models.UserAnswers
import navigation.FakeNavigators.FakeNavigator
import navigation.Navigator
import pages.ConsignorAddressPage
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.api.{Application, inject}
import views.html.CheckYourAnswersConsignorView

class CheckYourAnswersConsignorControllerSpec extends SpecBase {

  class Fixture(userAnswers: Option[UserAnswers]) {
    val application: Application =
      applicationBuilder(userAnswers)
        .overrides(inject.bind[Navigator].toInstance(new FakeNavigator(testOnwardRoute)))
        .build()

    lazy val errorHandler: ErrorHandler = application.injector.instanceOf[ErrorHandler]
    val view: CheckYourAnswersConsignorView = application.injector.instanceOf[CheckYourAnswersConsignorView]
  }

  "Check Your Answers Consignor Controller" - {
    ".onPageLoad" - {

      def request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest(GET, routes.CheckYourAnswersConsignorController.onPageLoad(testErn, testLrn).url)

      "must return OK and the correct view" in new Fixture(Some(emptyUserAnswers.set(ConsignorAddressPage, testUserAddress))) {

        running(application) {

          val result = route(application, request).value

          val viewAsString = view(routes.CheckYourAnswersConsignorController.onSubmit(testErn, testLrn),
            testErn,
            testLrn,
            testUserAddress,
            testMinTraderKnownFacts
          )(dataRequest(request), messages(application)).toString

          status(result) mustBe OK
          contentAsString(result) mustBe viewAsString
        }
      }

      "must redirect to Journey Recovery if no existing data is found" in new Fixture(None) {

        running(application) {

          val result = route(application, request).value

          status(result) mustBe SEE_OTHER
          redirectLocation(result).value mustBe routes.JourneyRecoveryController.onPageLoad().url
        }
      }
    }

    ".onSubmit" - {

      def request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest(POST, routes.CheckYourAnswersConsignorController.onSubmit(testErn, testLrn).url)

      "must redirect to the onward route" in new Fixture(Some(emptyUserAnswers)) {

        running(application) {

          val result = route(application, request).value

          status(result) mustBe SEE_OTHER
          redirectLocation(result).value mustBe testOnwardRoute.url
        }
      }
    }
  }
}

