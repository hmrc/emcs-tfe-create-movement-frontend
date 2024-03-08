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

package pages.sections.exportInformation

import models.requests.DataRequest
import pages.QuestionPage
import play.api.libs.json.JsPath
import utils.SubmissionFailureErrorCodes.exportCustomsOfficeNumberError

case object ExportCustomsOfficePage extends QuestionPage[String] {
  override val toString: String = "exportCustomsOffice"
  override val path: JsPath = ExportInformationSection.path \ toString

  override def isMovementSubmissionError(implicit request: DataRequest[_]): Boolean =
    request.userAnswers.submissionFailures.exists(error => error.errorType == exportCustomsOfficeNumberError && !error.hasBeenFixed)

  override def getOriginalAttributeValue(implicit request: DataRequest[_]): Option[String] =
    request.userAnswers.submissionFailures.find(_.errorType == exportCustomsOfficeNumberError).flatMap(_.originalAttributeValue)

  override def indexesOfMovementSubmissionErrors(implicit request: DataRequest[_]): Seq[Int] =
    Seq(request.userAnswers.submissionFailures.indexWhere(_.errorType == exportCustomsOfficeNumberError))
}
