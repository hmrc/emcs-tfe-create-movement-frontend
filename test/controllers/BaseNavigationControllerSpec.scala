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
import fixtures.MovementSubmissionFailureFixtures
import mocks.services.MockUserAnswersService
import models.requests.{DataRequest, UserRequest}
import models.{Index, NormalMode, UserAnswers}
import navigation.BaseNavigator
import navigation.FakeNavigators.FakeNavigator
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import pages.sections.info.LocalReferenceNumberPage
import pages.{DeclarationPage, QuestionPage}
import play.api.libs.json.{JsObject, JsPath, __}
import play.api.mvc.{AnyContentAsEmpty, MessagesControllerComponents, Result}
import play.api.test.FakeRequest
import play.api.test.Helpers.{GET, defaultAwaitTimeout, redirectLocation}
import queries.Derivable
import services.UserAnswersService
import uk.gov.hmrc.http.HeaderCarrier
import utils.SubmissionFailureErrorCodes.LocalReferenceNumberError

import scala.concurrent.Future

class BaseNavigationControllerSpec extends SpecBase with GuiceOneAppPerSuite with MockUserAnswersService with MovementSubmissionFailureFixtures {
  trait Test {
    implicit val hc: HeaderCarrier = HeaderCarrier()

    implicit val request: UserRequest[AnyContentAsEmpty.type] = userRequest(FakeRequest(GET, "/foo/bar"))

    val page = new QuestionPage[String] {
      override val path: JsPath = __ \ "page1"
    }
    val page2 = new QuestionPage[String] {
      override val path: JsPath = __ \ "page2"
    }
    val value = "foo"

    case class TestIndexPage(index: Index) extends QuestionPage[String] {
      override val path: JsPath = TestDerivable.path \ index.position \ "test"
    }

    object TestDerivable extends Derivable[Seq[JsObject], Int] {
      override val derive: Seq[JsObject] => Int = _.size

      override val path: JsPath = JsPath \ "something"
    }

    val testNavigator = new FakeNavigator(testOnwardRoute)

    lazy val testController = new BaseNavigationController with BaseController {
      override val userAnswersService: UserAnswersService = mockUserAnswersService
      override val navigator: BaseNavigator = testNavigator

      override protected def controllerComponents: MessagesControllerComponents = messagesControllerComponents
    }
  }

  "saveAndRedirect" - {
    "with currentAnswers" - {
      "must save the answer and redirect" - {
        "when current UserAnswers doesn't contain the input answer" in new Test {
          val newUserAnswers: UserAnswers = emptyUserAnswers.set(page, value)

          MockUserAnswersService.set().returns(Future.successful(newUserAnswers))

          val answer: Future[Result] =
            testController.saveAndRedirect(page, value, emptyUserAnswers, NormalMode)

          redirectLocation(answer) mustBe Some(testOnwardRoute.url)
        }
      }

      "must only redirect" - {
        "when current UserAnswers contains the input answer" in new Test {
          val newUserAnswers: UserAnswers = emptyUserAnswers.set(page, value)

          MockUserAnswersService.set().never()

          val answer: Future[Result] =
            testController.saveAndRedirect(page, value, newUserAnswers, NormalMode)

          redirectLocation(answer) mustBe Some(testOnwardRoute.url)
        }
      }
    }

    "without currentAnswers" - {
      "must save the answer and redirect" in new Test {
        val newUserAnswers: UserAnswers = emptyUserAnswers.set(page, value)

        MockUserAnswersService.set().returns(Future.successful(newUserAnswers))

        val answer: Future[Result] =
          testController.saveAndRedirect(page, value, NormalMode)(dataRequest(FakeRequest()), implicitly)

        redirectLocation(answer) mustBe Some(testOnwardRoute.url)
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
          testController.cleanseUserAnswersIfValueHasChanged(
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
          testController.cleanseUserAnswersIfValueHasChanged(
            page = page,
            newAnswer = value,
            cleansingFunction = cleansingFunction
          )(dataRequest(FakeRequest(), emptyUserAnswers.set(page, value)), implicitly)

        result mustBe emptyUserAnswers.set(page, value)
      }
      "when current UserAnswers doesn't contain the input page" in new Test {

        val result: UserAnswers =
          testController.cleanseUserAnswersIfValueHasChanged(
            page = page2,
            newAnswer = value,
            cleansingFunction = cleansingFunction
          )(dataRequest(FakeRequest(), emptyUserAnswers.set(page, value)), implicitly)

        result mustBe emptyUserAnswers.set(page, value)
      }
    }
  }

  "validateIndexForJourneyEntry" - {
    "must run the onSuccess function" - {
      "when no user answers present and value is Index(0)" in new Test {
        val result: Boolean =
          testController.validateIndexForJourneyEntry(TestDerivable, Index(0))(true, false)(dataRequest(FakeRequest(), emptyUserAnswers), implicitly)

        result mustBe true
      }

      "when index is for a new page where index exists in previous items" in new Test {
        val result: Boolean =
          testController.validateIndexForJourneyEntry(
            TestDerivable, Index(2)
          )(
            true, false
          )(
            dataRequest(FakeRequest(), emptyUserAnswers
              .set(TestIndexPage(Index(0)), "answer")
              .set(TestIndexPage(Index(1)), "answer")
              .set(TestIndexPage(Index(2)), "answer")
              .set(TestIndexPage(Index(3)), "answer")
            ), implicitly
          )

        result mustBe true
      }

      "when index is for a new page where index is one more than previous page" in new Test {
        val result: Boolean =
          testController.validateIndexForJourneyEntry(
            TestDerivable, Index(1)
          )(
            true, false
          )(
            dataRequest(FakeRequest(), emptyUserAnswers.set(TestIndexPage(Index(0)), "answer")), implicitly
          )

        result mustBe true
      }
    }
    "must run the onFailure function" - {
      "when no user answers present and index position is more than 0" in new Test {
        val result: Boolean =
          testController.validateIndexForJourneyEntry(TestDerivable, Index(2))(true, false)(dataRequest(FakeRequest(), emptyUserAnswers), implicitly)

        result mustBe false
      }

      "when index larger than max value - 1" in new Test {
        val result: Boolean =
          testController.validateIndexForJourneyEntry(
            TestDerivable, Index(2), max = 2
          )(
            true, false
          )(
            dataRequest(FakeRequest(), emptyUserAnswers
              .set(TestIndexPage(Index(0)), "answer")
              .set(TestIndexPage(Index(1)), "answer")
            ), implicitly
          )

        result mustBe false
      }

      "when index is for a new page where index is greater than one more than previous page" in new Test {
        val result: Boolean =
          testController.validateIndexForJourneyEntry(
            TestDerivable, Index(2)
          )(
            true, false
          )(
            dataRequest(FakeRequest(), emptyUserAnswers.set(TestIndexPage(Index(0)), "answer")), implicitly
          )

        result mustBe false
      }
    }
  }

  "validateIndex" - {

    "must run the onSuccess function" - {
      "when index is for a new page where index exists in previous items" in new Test {
        val result: Boolean =
          testController.validateIndex(
            TestDerivable, Index(2)
          )(
            true, false
          )(
            dataRequest(FakeRequest(), emptyUserAnswers
              .set(TestIndexPage(Index(0)), "answer")
              .set(TestIndexPage(Index(1)), "answer")
              .set(TestIndexPage(Index(2)), "answer")
              .set(TestIndexPage(Index(3)), "answer")
            ), implicitly
          )

        result mustBe true
      }
    }
    "must run the onFailure function" - {
      "when no user answers present and value is Index(0)" in new Test {
        val result: Boolean =
          testController.validateIndex(TestDerivable, Index(0))(true, false)(dataRequest(FakeRequest(), emptyUserAnswers), implicitly)

        result mustBe false
      }

      "when index is greater than last index in user answers" in new Test {
        val result: Boolean =
          testController.validateIndex(
            TestDerivable, Index(2)
          )(
            true, false
          )(
            dataRequest(FakeRequest(), emptyUserAnswers
              .set(TestIndexPage(Index(0)), "answer")
              .set(TestIndexPage(Index(1)), "answer")
            ), implicitly
          )

        result mustBe false
      }
    }
  }

  "markErrorAsFixedIfPresent" - {

    "return the same user answers" - {

      "when there are no errors" in new Test {

        val result = testController.markErrorAsFixedIfPresent(LocalReferenceNumberPage())(dataRequest(answers = emptyUserAnswers, request = FakeRequest()))
        result mustBe emptyUserAnswers
      }

      "when all errors have been fixed" in new Test {

        val expectedUserAnswers: UserAnswers = emptyUserAnswers.copy(submissionFailures = Seq(movementSubmissionFailure.copy(hasBeenFixed = true)))

        val result: UserAnswers = testController.markErrorAsFixedIfPresent(LocalReferenceNumberPage())(dataRequest(answers =
          expectedUserAnswers, request = FakeRequest()))
        result mustBe expectedUserAnswers
      }

      "when the error (in the submission failure list) doesn't exist for the page" in new Test {

        val expectedUserAnswers: UserAnswers = emptyUserAnswers.copy(submissionFailures = Seq(movementSubmissionFailure.copy(errorType = "0000")))

        val result: UserAnswers = testController.markErrorAsFixedIfPresent(LocalReferenceNumberPage())(dataRequest(answers =
          expectedUserAnswers, request = FakeRequest()))
        result mustBe expectedUserAnswers
      }

      "there is no mapping for an error" in new Test {

        val expectedUserAnswers: UserAnswers = emptyUserAnswers.copy(submissionFailures = Seq(movementSubmissionFailure))

        val result: UserAnswers = testController.markErrorAsFixedIfPresent(DeclarationPage)(dataRequest(answers =
          expectedUserAnswers, request = FakeRequest()))
        result mustBe expectedUserAnswers
      }
    }

    "return updated user answers" - {

      "when there is a mapping for the page and all the errors have not been fixed" in new Test {

        val originalUserAnswers: UserAnswers = emptyUserAnswers.copy(submissionFailures = Seq(movementSubmissionFailure.copy(errorType = LocalReferenceNumberError.code)))

        val expectedUserAnswers: UserAnswers = emptyUserAnswers.copy(submissionFailures = Seq(movementSubmissionFailure.copy(errorType = LocalReferenceNumberError.code, hasBeenFixed = true)))

        val result: UserAnswers = testController.markErrorAsFixedIfPresent(LocalReferenceNumberPage())(dataRequest(answers =
          originalUserAnswers, request = FakeRequest()))
        result mustBe expectedUserAnswers
      }

      "when there is multiple mapping for the page and all the errors have not been fixed" in new Test {

        case object TestPage extends QuestionPage[String] {
          override val path: JsPath = JsPath

          override def indexesOfMovementSubmissionErrors(implicit request: DataRequest[_]): Seq[Int] = {
            Seq(0, 1)
          }
        }

        val originalUserAnswers: UserAnswers = emptyUserAnswers.copy(submissionFailures = Seq(
          movementSubmissionFailure.copy(errorType = "0001"),
          movementSubmissionFailure.copy(errorType = "0002"),
          movementSubmissionFailure.copy(errorType = "0003")
        ))

        val expectedUserAnswers: UserAnswers = emptyUserAnswers.copy(submissionFailures = Seq(
          movementSubmissionFailure.copy(errorType = "0001", hasBeenFixed = true),
          movementSubmissionFailure.copy(errorType = "0002", hasBeenFixed = true),
          movementSubmissionFailure.copy(errorType = "0003")
        ))

        val result: UserAnswers = testController.markErrorAsFixedIfPresent(TestPage)(dataRequest(answers =
          originalUserAnswers, request = FakeRequest()))
        result mustBe expectedUserAnswers
      }
    }
  }
}
