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
import mocks.services.{MockAddressLookupFrontendService, MockUserAnswersService}
import models.response.UnexpectedDownstreamResponseError
import play.api.inject.bind
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.{AddressLookupFrontendService, UserAnswersService}

import scala.concurrent.Future

class IndexControllerSpec extends SpecBase with MockUserAnswersService with MockAddressLookupFrontendService {

  "Index Controller" - {
    "when existing UserAnswers don't exist" - {
      "must Initialise the UserAnswers and redirect to DateOfArrival" - {
        "when the call to initialise the ALF journey fails" in {

          MockUserAnswersService.set().returns(Future.successful(emptyUserAnswers))
          MockAddressLookupFrontendService.initialiseJourney(routes.CheckYourAnswersController.onPageLoad(testErn, testLrn))
            .returns(Future.successful(Left(UnexpectedDownstreamResponseError)))

          val application = applicationBuilder(userAnswers = None).overrides(
            bind[UserAnswersService].toInstance(mockUserAnswersService),
            bind[AddressLookupFrontendService].toInstance(mockAddressLookupFrontendService)
          ).build()

          running(application) {

            val request = FakeRequest(GET, routes.IndexController.onPageLoad(testErn, testLrn).url)

            val result = route(application, request).value

            status(result) mustEqual SEE_OTHER
            redirectLocation(result) mustBe Some(routes.CheckYourAnswersController.onPageLoad(testErn, testLrn).url)
          }
        }
        "when the call to initialise ALF journey is successful" - {
          "should redirect to the provided URL" in {

            MockUserAnswersService.set().returns(Future.successful(emptyUserAnswers))
            MockAddressLookupFrontendService.initialiseJourney(routes.CheckYourAnswersController.onPageLoad(testErn, testLrn))
              .returns(Future.successful(Right(testUrl)))

            val application = applicationBuilder(userAnswers = None).overrides(
              bind[UserAnswersService].toInstance(mockUserAnswersService),
              bind[AddressLookupFrontendService].toInstance(mockAddressLookupFrontendService)
            ).build()

            running(application) {


              val request = FakeRequest(GET, routes.IndexController.onPageLoad(testErn, testLrn).url)

              val result = route(application, request).value

              status(result) mustEqual SEE_OTHER
              redirectLocation(result) mustBe Some(testUrl)
            }
          }
        }
        "when existing UserAnswers exist" - {
          "must use the existing answers and redirect to DateOfArrival" - {
            "when the call to initialise the ALF journey fails" in {

              MockUserAnswersService.set(emptyUserAnswers).returns(Future.successful(emptyUserAnswers))
              MockAddressLookupFrontendService.initialiseJourney(routes.CheckYourAnswersController.onPageLoad(testErn, testLrn))
                .returns(Future.successful(Left(UnexpectedDownstreamResponseError)))

              val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).overrides(
                bind[UserAnswersService].toInstance(mockUserAnswersService),
                bind[AddressLookupFrontendService].toInstance(mockAddressLookupFrontendService)
              ).build()

              running(application) {


                val request = FakeRequest(GET, routes.IndexController.onPageLoad(testErn, testLrn).url)

                val result = route(application, request).value

                status(result) mustEqual SEE_OTHER
                redirectLocation(result) mustBe Some(routes.CheckYourAnswersController.onPageLoad(testErn, testLrn).url)
              }
            }
          }
        }

      }
    }
  }
}
