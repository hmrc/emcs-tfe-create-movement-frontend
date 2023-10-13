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
import mocks.services.MockUserAnswersService
import models.{NormalMode, UserAnswers}
import navigation.BaseNavigator
import navigation.FakeNavigators.FakeNavigator
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import pages.QuestionPage
import play.api.libs.json.{JsPath, __}
import play.api.mvc.{Call, MessagesControllerComponents, Result}
import play.api.test.FakeRequest
import play.api.test.Helpers.{GET, defaultAwaitTimeout, redirectLocation}
import services.UserAnswersService
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.Future

class BaseNavigationControllerSpec extends SpecBase with GuiceOneAppPerSuite with MockUserAnswersService {
  trait Test {
    implicit val hc = HeaderCarrier()

    implicit val request = userRequest(FakeRequest(GET, "/foo/bar"))

    val page = new QuestionPage[String] { override def path: JsPath = __ \ "page1" }
    val page2 = new QuestionPage[String] { override def path: JsPath = __ \ "page2" }
    val value = "foo"

    def onwardRoute = Call("GET", "/foo")

    val testNavigator = new FakeNavigator(onwardRoute)

    val controller: BaseNavigationController = new BaseNavigationController with BaseController {
      override val userAnswersService: UserAnswersService = mockUserAnswersService
      override val navigator: BaseNavigator = testNavigator

      override protected def controllerComponents: MessagesControllerComponents = app.injector.instanceOf[MessagesControllerComponents]
    }
  }

  "saveAndRedirect" - {
    "with currentAnswers" - {
      "must save the answer and redirect" - {
        "when current UserAnswers doesn't contain the input answer" in new Test {
          val newUserAnswers: UserAnswers = emptyUserAnswers.set(page, value)

          MockUserAnswersService.set().returns(Future.successful(newUserAnswers))

          val answer: Future[Result] =
            controller.saveAndRedirect(page, value, emptyUserAnswers, NormalMode)

          redirectLocation(answer) mustBe Some(onwardRoute.url)
        }
      }

      "must only redirect" - {
        "when current UserAnswers contains the input answer" in new Test {
          val newUserAnswers: UserAnswers = emptyUserAnswers.set(page, value)

          MockUserAnswersService.set().never()

          val answer: Future[Result] =
            controller.saveAndRedirect(page, value, newUserAnswers, NormalMode)

          redirectLocation(answer) mustBe Some(onwardRoute.url)
        }
      }
    }

    "without currentAnswers" - {
      "must save the answer and redirect" in new Test {
        val newUserAnswers: UserAnswers = emptyUserAnswers.set(page, value)

        MockUserAnswersService.set().returns(Future.successful(newUserAnswers))

        val answer: Future[Result] =
          controller.saveAndRedirect(page, value, NormalMode)(dataRequest(FakeRequest()), implicitly)

        redirectLocation(answer) mustBe Some(onwardRoute.url)
      }
    }
  }

  "cleanseUserAnswersIfValueHasChanged" - {

    // To make it clear, this function just gets rid of everything
    // so when the cleansing function runs, the result should be an empty UserAnswers
    // otherwise, the result should be whatever you pass in the DataRequest
    def cleansingFunction: UserAnswers = emptyUserAnswers

    "must run the cleansing function" - {
      "when current UserAnswers contains the input page but the answer is different" in new Test {
        val result: UserAnswers =
          controller.cleanseUserAnswersIfValueHasChanged(
            page = page,
            newAnswer = "bar",
            cleansingFunction = cleansingFunction
          )(dataRequest(FakeRequest(), emptyUserAnswers.set(page, value)), implicitly)

        result mustBe emptyUserAnswers
      }
    }
    "must not run the cleansing function" - {
      "when current UserAnswers contains the input page but the answer is the same" in new Test {
        val result: UserAnswers =
          controller.cleanseUserAnswersIfValueHasChanged(
            page = page,
            newAnswer = value,
            cleansingFunction = cleansingFunction
          )(dataRequest(FakeRequest(), emptyUserAnswers.set(page, value)), implicitly)

        result mustBe emptyUserAnswers.set(page, value)
      }
      "when current UserAnswers doesn't contain the input page" in new Test {

        val result: UserAnswers =
          controller.cleanseUserAnswersIfValueHasChanged(
            page = page2,
            newAnswer = value,
            cleansingFunction = cleansingFunction
          )(dataRequest(FakeRequest(), emptyUserAnswers.set(page, value)), implicitly)

        result mustBe emptyUserAnswers.set(page, value)
      }
    }
  }
}
