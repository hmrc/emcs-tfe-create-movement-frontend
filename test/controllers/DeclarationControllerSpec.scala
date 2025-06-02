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
import controllers.actions.{DataRequiredAction, FakeDataRetrievalAction}
import fixtures.{ItemFixtures, MovementSubmissionFailureFixtures}
import mocks.config.MockAppConfig
import mocks.services.{MockMovementTemplatesService, MockSubmitCreateMovementService, MockUserAnswersService, MockValidationService}
import models.UserAnswers
import models.requests.DataRequest
import models.response.UnexpectedDownstreamDraftSubmissionResponseError
import models.response.templates.MovementTemplates
import navigation.FakeNavigators.FakeNavigator
import pages.DeclarationPage
import play.api.i18n.Messages
import play.api.mvc.{AnyContentAsEmpty, Result}
import play.api.test.Helpers._
import play.api.test.{FakeRequest, Helpers}
import views.html.DeclarationView

import java.time.LocalDateTime
import scala.concurrent.Future


class DeclarationControllerSpec extends SpecBase
  with MockUserAnswersService
  with MockSubmitCreateMovementService
  with MockAppConfig
  with ItemFixtures
  with MovementSubmissionFailureFixtures
  with MockMovementTemplatesService
  with MockValidationService {

  lazy val view: DeclarationView = app.injector.instanceOf[DeclarationView]
  val ern: String = "XIRC123"
  lazy val submitRoute = routes.DeclarationController.onSubmit(ern, testDraftId)


  class Test(val userAnswers: UserAnswers = baseFullUserAnswers) {
    implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(
      FakeRequest(),
      userAnswers,
      ern
    )
    implicit lazy val messagesInstance: Messages = messages(request)
    val controller: DeclarationController = new DeclarationController(
      messagesApi,
      fakeAuthAction,
      new FakeDataRetrievalAction(Some(userAnswers), Some(testMinTraderKnownFacts)),
      app.injector.instanceOf[DataRequiredAction],
      Helpers.stubMessagesControllerComponents(),
      mockUserAnswersService,
      new FakeNavigator(testOnwardRoute),
      mockSubmitCreateMovementService,
      view,
      mockValidationService,
      mockMovementTemplatesService,
      errorHandler
    )(mockAppConfig)
  }


  "DeclarationController" - {
    "for GET onPageLoad" - {
      "must return the declaration page (no submission failure and journey complete)" in new Test() {

        MockMovementTemplatesService.getList(ern).returns(Future.successful(MovementTemplates(Seq(), 0)))

        MockAppConfig.destinationOfficeSuffix.returns("004098")
        MockValidationService.validate().returns(Future.successful(userAnswers))

        val res: Future[Result] = controller.onPageLoad(ern, testDraftId)(request)

        status(res) mustBe OK
        contentAsString(res) mustBe view(submitAction = submitRoute, countOfTemplates = 0).toString()
      }

      "must redirect to Task List when validation service triggers a validation error to be added to the UserAnswers" in new Test() {

        MockAppConfig.destinationOfficeSuffix.returns("004098")
        MockValidationService.validate().returns(Future.successful(userAnswers.copy(
          submissionFailures = Seq(dispatchDateInPastValidationError())
        )))

        val res: Future[Result] = controller.onPageLoad(ern, testDraftId)(request)

        status(res) mustBe SEE_OTHER
        redirectLocation(res).value mustBe routes.DraftMovementController.onPageLoad(ern, testDraftId).url
      }

      "must redirect to Task List when not all IE704 errors have been fixed" in new Test(
        baseFullUserAnswers.copy(submissionFailures = Seq(
          movementSubmissionFailure,
          importCustomsOfficeCodeFailure.copy(hasBeenFixed = true)
        ))
      ) {

        MockAppConfig.destinationOfficeSuffix.returns("004098")
        MockValidationService.validate().returns(Future.successful(userAnswers))

        val res: Future[Result] = controller.onPageLoad(ern, testDraftId)(request)

        status(res) mustBe SEE_OTHER
        redirectLocation(res).value mustBe routes.DraftMovementController.onPageLoad(ern, testDraftId).url
      }

      "must return the declaration page (all submission failures have been fixed (including any UIErrorModels))" in new Test(
        baseFullUserAnswers.copy(submissionFailures = Seq(
          movementSubmissionFailure.copy(hasBeenFixed = true),
          dispatchDateInPastValidationError().copy(hasBeenFixed = true)
        ))
      ) {

        MockMovementTemplatesService.getList(ern).returns(Future.successful(MovementTemplates(Seq(), 0)))

        MockAppConfig.destinationOfficeSuffix.returns("004098")
        MockValidationService.validate().returns(Future.successful(userAnswers))

        val res: Future[Result] = controller.onPageLoad(ern, testDraftId)(request)

        status(res) mustBe OK
        contentAsString(res) mustBe view(submitAction = submitRoute, countOfTemplates = 0).toString()
      }

      "when creating a request model fails" - {
        "must return a BadRequest when MissingMandatoryPage" in new Test(emptyUserAnswers) {
          val res: Future[Result] = controller.onPageLoad(ern, testDraftId)(request)

          status(res) mustBe SEE_OTHER
          redirectLocation(res) mustBe Some(routes.DraftMovementController.onPageLoad(ern, testDraftId).url)
        }

        "must return a InternalServerError when something else goes wrong" in new Test() {
          MockAppConfig.destinationOfficeSuffix.throws(new Exception("test error"))

          val res: Future[Result] = controller.onPageLoad(ern, testDraftId)(request)

          status(res) mustBe INTERNAL_SERVER_ERROR
        }
      }
    }

    "for POST submit" - {
      "when downstream call is successful" - {
        "must save the timestamp and redirect" in new Test() {
          MockAppConfig.destinationOfficeSuffix.returns("004098")
          MockSubmitCreateMovementService.submit(xircSubmitCreateMovementModel, ern).returns(Future.successful(Right(submitCreateMovementResponseEIS)))
          MockUserAnswersService.set(baseFullUserAnswers.copy(hasBeenSubmitted = true, submittedDraftId = Some(testDraftId))).returns(Future.successful(
            baseFullUserAnswers.copy(hasBeenSubmitted = true, submittedDraftId = Some(testDraftId))
              .set(DeclarationPage, LocalDateTime.now())
          ))

          val res: Future[Result] = controller.onSubmit(ern, testDraftId)(request)

          status(res) mustBe SEE_OTHER
          redirectLocation(res) must contain(testOnwardRoute.url)
        }

        "must save the timestamp and redirect (when all submission failures have been fixed)" in new Test(
          baseFullUserAnswers.copy(
            submissionFailures = Seq(
              movementSubmissionFailure.copy(hasBeenFixed = true)
            )
          )
        ) {
          val expectedUserAnswers: UserAnswers = baseFullUserAnswers.copy(
            hasBeenSubmitted = true,
            submittedDraftId = Some(testDraftId),
            submissionFailures = Seq(
              movementSubmissionFailure.copy(hasBeenFixed = true)
            ))
          MockAppConfig.destinationOfficeSuffix.returns("004098")
          MockSubmitCreateMovementService.submit(xircSubmitCreateMovementModel, ern).returns(Future.successful(Right(submitCreateMovementResponseEIS)))
          MockUserAnswersService.set(expectedUserAnswers).returns(Future.successful(expectedUserAnswers))

          val res: Future[Result] = controller.onSubmit(ern, testDraftId)(request)

          status(res) mustBe SEE_OTHER
          redirectLocation(res) must contain(testOnwardRoute.url)
        }
      }

      "when downstream call is unsuccessful" - {
        "when downstream returns a 422" - {
          "must redirect to the DraftMovementController" in new Test() {
            MockAppConfig.destinationOfficeSuffix.returns("004098")
            MockSubmitCreateMovementService.submit(xircSubmitCreateMovementModel, ern)
              .returns(Future.successful(Left(UnexpectedDownstreamDraftSubmissionResponseError(UNPROCESSABLE_ENTITY))))

            val res: Future[Result] = controller.onSubmit(ern, testDraftId)(request)

            status(res) mustBe SEE_OTHER
            redirectLocation(res) mustBe Some(routes.DraftMovementController.onPageLoad(ern, testDraftId).url)
          }
        }
        "when downstream returns an unexpected response status" - {
          "must return an InternalServerError" in new Test() {
            // arbitrary 5xx status codes
            Seq(INTERNAL_SERVER_ERROR, BAD_GATEWAY, SERVICE_UNAVAILABLE, GATEWAY_TIMEOUT).foreach { responseStatus =>
              MockAppConfig.destinationOfficeSuffix.returns("004098")
              MockSubmitCreateMovementService.submit(xircSubmitCreateMovementModel, ern)
                .returns(Future.successful(Left(UnexpectedDownstreamDraftSubmissionResponseError(responseStatus))))

              val res = controller.onSubmit(ern, testDraftId)(request)

              status(res) mustBe INTERNAL_SERVER_ERROR
            }
          }
        }
      }

      "when creating a request model fails" - {
        "must return a BadRequest when MissingMandatoryPage" in new Test(emptyUserAnswers) {
          val res: Future[Result] = controller.onSubmit(ern, testDraftId)(request)

          status(res) mustBe SEE_OTHER
          redirectLocation(res) mustBe Some(routes.DraftMovementController.onPageLoad(ern, testDraftId).url)
        }

        "must return a InternalServerError when something else goes wrong" in new Test() {
          MockAppConfig.destinationOfficeSuffix.throws(new Exception("test error"))

          val res: Future[Result] = controller.onSubmit(ern, testDraftId)(request)

          status(res) mustBe INTERNAL_SERVER_ERROR
        }
      }
    }
  }
}
