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

package pages

import models.requests.DataRequest
import play.api.libs.json.Reads
import queries.{Gettable, Settable}
import utils.SubmissionError

trait QuestionPage[+A] extends Page with Gettable[A] with Settable[A] {

  val possibleErrors: Seq[SubmissionError] = Seq.empty

  def isMovementSubmissionError(implicit request: DataRequest[_]): Boolean =
    request.userAnswers.submissionFailures.exists(error =>
      possibleErrors.map(_.code).contains(error.errorType) && !error.hasBeenFixed
    )

  def getOriginalAttributeValue(implicit request: DataRequest[_]): Option[String] =
    request.userAnswers.submissionFailures.find(error =>
      possibleErrors.map(_.code).contains(error.errorType)
    ).flatMap(_.originalAttributeValue)

  def indexesOfMovementSubmissionErrors(implicit request: DataRequest[_]): Seq[Int] =
    request.userAnswers.submissionFailures.zipWithIndex.collect {
      case (error, index) if possibleErrors.map(_.code).contains(error.errorType) => index
    }

  def getMovementSubmissionErrors(implicit request: DataRequest[_]): Seq[SubmissionError] =
    request.userAnswers.submissionFailures.collect {
      case error if possibleErrors.map(_.code).contains(error.errorType) && !error.hasBeenFixed => SubmissionError(error.errorType)
    }

  def is[T >: A](t: T)(implicit request: DataRequest[_], reads: Reads[T]): Boolean = value[T].contains(t)

  def value[T >: A](implicit request: DataRequest[_], reads: Reads[T]): Option[T] = request.userAnswers.get[T](this)

  def isEmpty[T >: A](implicit request: DataRequest[_], reads: Reads[T]): Boolean = value[T].isEmpty
}