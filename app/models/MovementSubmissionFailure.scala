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

package models

import models.requests.DataRequest
import play.api.libs.json.{Format, Json}
import utils.SubmissionError

case class MovementSubmissionFailure(
                                      errorType: String,
                                      errorReason: String,
                                      errorLocation: Option[String],
                                      originalAttributeValue: Option[String],
                                      hasBeenFixed: Boolean
                                    ) {

  lazy val asSubmissionError: SubmissionError = SubmissionError.apply(errorType)
  def isFixable(implicit request: DataRequest[_]): Boolean = asSubmissionError.isFixable
}

object MovementSubmissionFailure {
  implicit val format: Format[MovementSubmissionFailure] = Json.format[MovementSubmissionFailure]
}
