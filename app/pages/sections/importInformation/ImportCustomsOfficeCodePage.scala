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

package pages.sections.importInformation

import models.requests.DataRequest
import pages.QuestionPage
import play.api.libs.json.JsPath
import utils.SubmissionFailureErrorCodes.importCustomsOfficeCodeError

case object ImportCustomsOfficeCodePage extends QuestionPage[String] {
  override val toString: String = "importCustomsOfficeCode"
  override val path: JsPath = ImportInformationSection.path \ toString

  override def getOriginalAttributeValue(implicit request: DataRequest[_]): Option[String] =
    request.userAnswers.submissionFailures.find(_.errorType == importCustomsOfficeCodeError).flatMap(_.originalAttributeValue)

  override def isMovementSubmissionError(implicit request: DataRequest[_]): Boolean =
    request.userAnswers.submissionFailures.exists(error => error.errorType == importCustomsOfficeCodeError && !error.hasBeenFixed)

  override def indexesOfMovementSubmissionErrors(implicit request: DataRequest[_]): Seq[Int] =
    Seq(request.userAnswers.submissionFailures.indexWhere(_.errorType == importCustomsOfficeCodeError))
}
