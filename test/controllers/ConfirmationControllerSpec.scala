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
import controllers.actions.{AuthAction, DataRetrievalAction, FakeAuthAction}
import handlers.ErrorHandler
import models.UserAnswers
import models.requests.{OptionalDataRequest, UserRequest}
import navigation.FakeNavigators.FakeNavigator
import navigation.Navigator
import org.scalamock.scalatest.MockFactory
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import pages.DeclarationPage
import pages.sections.info.LocalReferenceNumberPage
import play.api.Application
import play.api.i18n.Messages
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.mvc.{ActionTransformer, AnyContentAsEmpty}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.ConfirmationView

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{ExecutionContext, Future}

class ConfirmationControllerSpec extends SpecBase with GuiceOneAppPerSuite with MockFactory {
  val testUserAnswers: UserAnswers = emptyUserAnswers
    .set(DeclarationPage, testSubmissionDate)
    .set(LocalReferenceNumberPage(), testConfirmationReference)

  val mockDataRetrievalAction: DataRetrievalAction = mock[DataRetrievalAction]
  val testExciseEnquiriesLink = "testExciseEnquiriesLink"
  val testReturnToAccountLink = "testReturnToAccountLink"
  val testFeedbackBaseUrl = "testFeedbackBaseUrl"
  val testDeskproName = "testDeskproName"

  def testRetrievalAction(userAnswers: UserAnswers): ActionTransformer[UserRequest, OptionalDataRequest] = {
    new ActionTransformer[UserRequest, OptionalDataRequest] {
      override def transform[A](request: UserRequest[A]): Future[OptionalDataRequest[A]] =
        Future(OptionalDataRequest(request, testDraftId, Some(userAnswers), Some(testMinTraderKnownFacts)))

      override protected def executionContext: ExecutionContext = global
    }
  }

  override def fakeApplication(): Application =
    new GuiceApplicationBuilder()
      .configure(
        "urls.exciseGuidance" -> testExciseEnquiriesLink,
        "urls.emcsTfeHome" -> testReturnToAccountLink,
        "feedback-frontend.host" -> testFeedbackBaseUrl,
        "deskproName" -> testDeskproName
      )
      .overrides(
        bind[AuthAction].to[FakeAuthAction],
        bind[Navigator].toInstance(new FakeNavigator(testOnwardRoute)),
        bind[DataRetrievalAction].toInstance(mockDataRetrievalAction),
      ).build()

  val view: ConfirmationView = app.injector.instanceOf[ConfirmationView]
  val errorHandler: ErrorHandler = app.injector.instanceOf[ErrorHandler]
  val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest(GET, routes.ConfirmationController.onPageLoad(testErn, testDraftId).url)
  val controller: ConfirmationController = app.injector.instanceOf[ConfirmationController]
  implicit lazy val messagesInstance: Messages = messages(app)


  "Confirmation Controller" - {

    "when the confirmation receipt reference is held in session" - {

      "must return OK and the correct view for a GET" in {
        (mockDataRetrievalAction.apply _).expects(*).returns(testRetrievalAction(testUserAnswers))

        implicit val req = dataRequest(
          request = request,
          answers = testUserAnswers
        )

        val result = controller.onPageLoad(testErn, testDraftId)(req)

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(
          reference = testConfirmationReference,
          dateOfSubmission = testSubmissionDate.toLocalDate,
          exciseEnquiriesLink = testExciseEnquiriesLink,
          returnToAccountLink = testReturnToAccountLink,
          feedbackLink = s"$testFeedbackBaseUrl/feedback/$testDeskproName/beta"
        ).toString()
      }
    }

    "when no local reference or submission date is found" - {
      "must redirect to Journey Recovery" in {
        (mockDataRetrievalAction.apply _).expects(*).returns(testRetrievalAction(emptyUserAnswers))

        val result = controller.onPageLoad(testErn, testDraftId)(request)

        status(result) mustBe SEE_OTHER
        redirectLocation(result).value mustBe routes.JourneyRecoveryController.onPageLoad().url
      }
    }
  }
}
