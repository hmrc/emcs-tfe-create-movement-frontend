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

import config.AppConfig
import forms.sections.info.DispatchDateValidation
import models.UserAnswers
import models.requests.DataRequest
import models.sections.info.DispatchDetailsModel
import models.validation.UIErrorModel
import pages.sections.info.{DeferredMovementPage, DispatchDetailsPage}
import play.api.i18n.Messages
import uk.gov.hmrc.http.HeaderCarrier
import utils._

import java.time.LocalDate
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ValidationService @Inject()(override val appConfig: AppConfig,
                                  override val timeMachine: TimeMachine,
                                  userAnswersService: UserAnswersService)(implicit ec: ExecutionContext) extends DispatchDateValidation with Logging {

  def validate()(implicit request: DataRequest[_], hc: HeaderCarrier, messages: Messages): Future[UserAnswers] = {
    val validatedUserAnswers = Seq[Seq[UIErrorModel[_, _]]](
      validateDateOfDispatch()
    ).flatten.foldLeft(request.userAnswers) { case (updatedAnswers, error) =>
      logger.info(s"[validate] Validation Error of type ${error.errorType} was triggered")
      updatedAnswers.copy(submissionFailures = updatedAnswers.submissionFailures.filterNot(_.errorType == error.errorType.code) :+ error.asSubmissionFailure)
    }
    userAnswersService.set(validatedUserAnswers) map identity
  }

  private def validateDateOfDispatch()(implicit request: DataRequest[_], messages: Messages): Seq[UIErrorModel[DispatchDetailsModel, LocalDate]] =
    DispatchDetailsPage(isOnPreDraftFlow = false).value -> DeferredMovementPage(isOnPreDraftFlow = false).value match {
      case (Some(dispatchDetails), Some(isDeferred)) =>
        Seq(
          UIErrorModel(DispatchDateInPastValidationError, minDateCheck(isDeferred), dispatchDetails.date),
          UIErrorModel(DispatchDateInFutureValidationError, maxDateCheck(isDeferred), dispatchDetails.date)
        ).flatten
      case _ =>
        Seq()
    }
}
