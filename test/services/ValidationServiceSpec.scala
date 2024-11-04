/*
 * Copyright 2024 HM Revenue & Customs
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

package services

import base.SpecBase
import fixtures.MovementSubmissionFailureFixtures
import fixtures.messages.sections.info.DispatchDetailsMessages
import mocks.config.MockAppConfig
import mocks.services.MockUserAnswersService
import models.UserAnswers
import models.requests.DataRequest
import models.sections.info.DispatchDetailsModel
import models.sections.info.movementScenario.MovementScenario.DirectDelivery
import models.sections.journeyType.HowMovementTransported.FixedTransportInstallations
import models.sections.transportUnit.TransportUnitType.{FixedTransport, Tractor}
import pages.sections.guarantor.{GuarantorRequiredPage, GuarantorSection}
import pages.sections.info.{DeferredMovementPage, DestinationTypePage, DispatchDetailsPage}
import pages.sections.journeyType.HowMovementTransportedPage
import pages.sections.transportUnit.TransportUnitTypePage
import play.api.mvc.AnyContentAsEmpty
import play.api.test.Helpers.await
import play.api.test.{DefaultAwaitTimeout, FakeRequest}
import uk.gov.hmrc.http.HeaderCarrier
import utils.TimeMachine

import java.time.{Instant, LocalDate, LocalDateTime, LocalTime}
import scala.concurrent.{ExecutionContext, Future}

class ValidationServiceSpec extends SpecBase
  with MockUserAnswersService
  with MockAppConfig
  with MovementSubmissionFailureFixtures
  with DefaultAwaitTimeout {

  implicit val hc: HeaderCarrier = HeaderCarrier()
  implicit val ec: ExecutionContext = ExecutionContext.global

  val dateTime: LocalDateTime = LocalDateTime.now()
  val dispatchTime = LocalTime.of(3, 0)

  val mockTimeMachine = new TimeMachine {
    override def now(): LocalDateTime = dateTime
    override def instant(): Instant = Instant.now
  }

  class Test(val userAnswers: UserAnswers, ern: String = testErn) {

    MockAppConfig.maxDispatchDateFutureDays.returns(7).anyNumberOfTimes()
    MockAppConfig.earliestDispatchDate.returns(LocalDate.of(2000,1,1)).anyNumberOfTimes()

    implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(), userAnswers, ern)

    val service = new ValidationService(mockAppConfig, mockTimeMachine, mockUserAnswersService)
  }

  "ValidationService" - {

    ".validate" - {

      Seq(DispatchDetailsMessages.English) foreach { messagesForLanguage =>

        s"when language is set to code '${messagesForLanguage.lang.code}'" - {

          implicit val msgs = messages(Seq(messagesForLanguage.lang))

          "when dispatch date is invalid because it has become in the past for a non-deferred movement" - {

            "must add a SubmissionError to the UserAnswers for DispatchDateValidationError" in new Test(
              emptyUserAnswers
                .set(DeferredMovementPage(isOnPreDraftFlow = false), false)
                .set(DispatchDetailsPage(isOnPreDraftFlow = false), DispatchDetailsModel(
                  date = dateTime.minusDays(1).toLocalDate,
                  time = dispatchTime
                ))
            ) {

              val answersWithError = userAnswers.copy(submissionFailures = Seq(
                dispatchDateInPastValidationError(messagesForLanguage.tooFarInPastError, dateTime.minusDays(1).toLocalDate)
              ))

              MockUserAnswersService.set(answersWithError).returns(Future.successful(answersWithError))

              await(service.validate()) mustBe answersWithError
            }
          }

          "when dispatch date is invalid because it is in the future for a deferred movement" - {

            "must add a SubmissionError to the UserAnswers for DispatchDateValidationError" in new Test(
              emptyUserAnswers
                .set(DeferredMovementPage(isOnPreDraftFlow = false), true)
                .set(DispatchDetailsPage(isOnPreDraftFlow = false), DispatchDetailsModel(
                  date = dateTime.plusDays(1).toLocalDate,
                  time = dispatchTime
                ))
            ) {

              val answersWithError = userAnswers.copy(submissionFailures = Seq(
                dispatchDateInFutureValidationError(messagesForLanguage.deferredTooFarFutureError, dateTime.plusDays(1).toLocalDate)
              ))

              MockUserAnswersService.set(answersWithError).returns(Future.successful(answersWithError))

              await(service.validate()) mustBe answersWithError
            }
          }

          "when dispatch date submission already exists in the submission errors" - {

            "must add the new SubmissionError to the UserAnswers, replacing the old" in new Test(
              emptyUserAnswers
                .set(DeferredMovementPage(isOnPreDraftFlow = false), false)
                .set(DispatchDetailsPage(isOnPreDraftFlow = false), DispatchDetailsModel(
                  date = dateTime.minusDays(1).toLocalDate,
                  time = dispatchTime
                ))
                .copy(submissionFailures = Seq(
                  dispatchDateInPastValidationError(),
                  dispatchWarehouseInvalidOrMissingOnSeedError,
                  movementSubmissionFailure
                ))
            ) {

              val answersWithError = userAnswers.copy(submissionFailures = Seq(
                dispatchWarehouseInvalidOrMissingOnSeedError,
                movementSubmissionFailure,
                dispatchDateInPastValidationError(messagesForLanguage.tooFarInPastError, dateTime.minusDays(1).toLocalDate)
              ))

              MockUserAnswersService.set(answersWithError).returns(Future.successful(answersWithError))

              await(service.validate()) mustBe answersWithError
            }
          }

          "when the movement has changed in a way that the guarantor section has become mandatory" - {

            "when the guarantor section has been saved as 'No Guarantor Required'" - {

              "must reset the guarantor section" in new Test(
                emptyUserAnswers
                  .copy(ern = testNorthernIrelandErn)
                  .set(DestinationTypePage, DirectDelivery)
                  .set(GuarantorRequiredPage, false)
                  .set(HowMovementTransportedPage, FixedTransportInstallations)
                  .set(TransportUnitTypePage(testIndex1), FixedTransport)
                  .set(TransportUnitTypePage(testIndex2), Tractor),
                testNorthernIrelandErn
              ) {

                val expectedUpdatedAnswers = userAnswers.remove(GuarantorSection)

                MockUserAnswersService.set(expectedUpdatedAnswers).returns(Future.successful(expectedUpdatedAnswers))

                await(service.validate()) mustBe expectedUpdatedAnswers
              }
            }

            "when the guarantor section has been saved as 'Yes a Guarantor is Required'" - {

              "must NOT reset the guarantor section" in new Test(
                emptyUserAnswers
                  .copy(ern = testNorthernIrelandErn)
                  .set(DestinationTypePage, DirectDelivery)
                  .set(GuarantorRequiredPage, true)
                  .set(HowMovementTransportedPage, FixedTransportInstallations)
                  .set(TransportUnitTypePage(testIndex1), FixedTransport)
                  .set(TransportUnitTypePage(testIndex2), Tractor),
                testNorthernIrelandErn
              ) {

                MockUserAnswersService.set(userAnswers).returns(Future.successful(userAnswers))

                await(service.validate()) mustBe userAnswers
              }
            }
          }
        }
      }
    }
  }
}
