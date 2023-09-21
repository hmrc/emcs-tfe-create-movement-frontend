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
import mocks.services.MockAddressLookupFrontendService
import mocks.viewmodels.MockCheckAnswersHelper
import models.UserAnswers
import models.response.UnexpectedDownstreamResponseError
import navigation.FakeNavigators.FakeNavigator
import navigation.Navigator
import play.api.{Application, inject}
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.AddressLookupFrontendService
import viewmodels.checkAnswers.CheckAnswersHelper
import viewmodels.govuk.SummaryListFluency
import views.html.CheckYourAnswersView

import scala.concurrent.Future

class CheckYourAnswersControllerSpec extends SpecBase with SummaryListFluency with MockCheckAnswersHelper with MockAddressLookupFrontendService {

  class Fixture(userAnswers: Option[UserAnswers]) {
    val application: Application =
      applicationBuilder(userAnswers)
        .overrides(
          inject.bind[CheckAnswersHelper].toInstance(mockCheckAnswersHelper),
          inject.bind[Navigator].toInstance(new FakeNavigator(testOnwardRoute)),
          inject.bind[AddressLookupFrontendService].toInstance(mockAddressLookupFrontendService)
        )
        .build()

    lazy val errorHandler: ErrorHandler = application.injector.instanceOf[ErrorHandler]
    val view: CheckYourAnswersView = application.injector.instanceOf[CheckYourAnswersView]
  }

  "Check Your Answers Controller" - {

    ".onPageLoad" - {

      def request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest(GET, routes.CheckYourAnswersController.onPageLoad(testErn, testLrn).url)

      "must return OK and the correct view for a GET when there is no ALF ID query parameter" in new Fixture(Some(emptyUserAnswers)) {

        running(application) {

          val list = SummaryListViewModel(Seq.empty)

          MockCheckAnswersHelper.summaryList(Seq()).returns(list)

          val result = route(application, request).value

          status(result) mustBe OK
          contentAsString(result) mustBe
            view(routes.CheckYourAnswersController.onSubmit(testErn, testLrn), list)(dataRequest(request), messages(application)).toString
        }
      }

      "must return OK and the correct view for a GET when there is an ALF ID query parameter" - {
        def requestWithAlfId: FakeRequest[AnyContentAsEmpty.type] =
          FakeRequest(GET, routes.CheckYourAnswersController.onPageLoad(testErn, testLrn, Some(testId)).url)

        "when the call to retrieve the address is successful" in new Fixture(Some(emptyUserAnswers)) {
          running(application) {

            val list = SummaryListViewModel(Seq.empty)

            MockCheckAnswersHelper.summaryList(Seq()).returns(list)
            MockAddressLookupFrontendService.retrieveAddress(testId).returns(Future.successful(Right(Some(testAlfAddress))))

            val result = route(application, requestWithAlfId).value

            status(result) mustBe OK
            contentAsString(result) must contain
            view(
              routes.CheckYourAnswersController.onSubmit(testErn, testLrn),
              list,
              Some(testAlfAddress)
            )(dataRequest(request), messages(application)).toString
          }
        }

        "when the call to retrieve the address fails" in new Fixture(Some(emptyUserAnswers)) {
          running(application) {

            val list = SummaryListViewModel(Seq.empty)

            MockCheckAnswersHelper.summaryList(Seq()).returns(list)
            MockAddressLookupFrontendService.retrieveAddress(testId).returns(Future.successful(Left(UnexpectedDownstreamResponseError)))

            val result = route(application, requestWithAlfId).value

            status(result) mustBe OK
            contentAsString(result) must contain
            view(
              routes.CheckYourAnswersController.onSubmit(testErn, testLrn),
              list
            )(dataRequest(request), messages(application)).toString
          }
        }
      }

      "must redirect to Journey Recovery for a GET if no existing data is found when there is no ALF ID query parameter" in new Fixture(None) {

        running(application) {

          val result = route(application, request).value

          status(result) mustBe SEE_OTHER
          redirectLocation(result).value mustBe routes.JourneyRecoveryController.onPageLoad().url
        }
      }
    }

    ".onSubmit" - {

      def request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest(POST, routes.CheckYourAnswersController.onSubmit(testErn, testLrn).url)

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
