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

package models

import pages.{Page, QuestionPage}
import pages.sections.Section
import pages.sections.info.LocalReferenceNumberPage
import play.api.libs.json._
import queries.{Derivable, Gettable, Settable}
import uk.gov.hmrc.mongo.play.json.formats.MongoJavatimeFormats
import utils.SubmissionFailureErrorCodes.localReferenceNumberError

import java.time.Instant
import scala.annotation.unused

final case class UserAnswers(ern: String,
                             draftId: String,
                             data: JsObject = Json.obj(),
                             submissionFailures: Seq[MovementSubmissionFailure],
                             lastUpdated: Instant = Instant.now,
                             hasBeenSubmitted: Boolean,
                             submittedDraftId: Option[String]) {

  /**
   * @param pages a Seq of pages you want to leave in UserAnswers
   * @return this UserAnswers, where any pages not in the `pages` parameter are filtered out
   */
  def filterForPages(pages: Seq[QuestionPage[_]]): UserAnswers = {
    val pagesWithAnswersInData: Seq[(String, Json.JsValueWrapper)] = pages.flatMap {
      page =>
        data \ page match {
          case JsDefined(value) => Some(page.toString -> Json.toJsFieldJsValueWrapper(value))
          case _: JsUndefined => None
        }
    }

    val newAnswers = Json.obj(pagesWithAnswersInData: _*)

    this.copy(data = newAnswers)
  }

  def get[A](page: Gettable[A])(implicit rds: Reads[A]): Option[A] =
    Reads.optionNoError(Reads.at(page.path)).reads(data).asOpt.flatten

  def get[A, B](query: Derivable[A, B])(implicit rds: Reads[A]): Option[B] =
    get(query.asInstanceOf[Gettable[A]]).map(query.derive)

  def set[A](page: Settable[A], value: A)(implicit writes: Writes[A]): UserAnswers =
    handleResult {
      data.setObject(page.path, Json.toJson(value))
    }

  def remove[A](page: Settable[A]): UserAnswers =
    handleResult {
      data.removeObject(page.path)
    }

  /**
   * @param section section to reset to an empty object
   * @param index to ensure this method is used to reset indexed Sections
   * @return UserAnswers with the supplied section reset to an empty JSON object
   *         To be used to reset indexed Sections without deleting the index - e.g. clearing down an indexed Section in order to start it up again without losing its place in the Array
   */
  def resetIndexedSection(section: Section[JsObject], @unused index: Index): UserAnswers =
    handleResult {
      data.setObject(section.path, Json.obj())
    }

  private[models] def handleResult: JsResult[JsObject] => UserAnswers = {
    case JsSuccess(updatedAnswers, _) =>
      copy(data = updatedAnswers)
    case JsError(errors) =>
      throw JsResultException(errors)
  }

  def haveAllSubmissionErrorsBeenFixed: Boolean = submissionFailures.forall(_.hasFixed)

  def isSubmissionErrorOnPage(page: Page): Boolean =
    page match {
      case LocalReferenceNumberPage(_) => submissionFailures.exists(error => error.errorType == localReferenceNumberError && !error.hasFixed)
      case _ => false
    }

  def getOriginalAttributeValueForPage(page: Page): Option[String] =
    page match {
      case LocalReferenceNumberPage(_) => submissionFailures.find(_.errorType == localReferenceNumberError).flatMap(_.originalAttributeValue)
      case _ => None
    }

}

object UserAnswers {

  import play.api.libs.functional.syntax._

  val ern = "ern"
  val draftId = "draftId"
  val data = "data"
  val lastUpdated = "lastUpdated"
  val hasBeenSubmitted = "hasBeenSubmitted"
  val submissionFailures = "submissionFailures"
  val submittedDraftId = "submittedDraftId"

  val reads: Reads[UserAnswers] =
    (
      (__ \ ern).read[String] and
        (__ \ draftId).read[String] and
        (__ \ data).read[JsObject] and
        (__ \ submissionFailures).readNullable[Seq[MovementSubmissionFailure]].map(_.getOrElse(Seq.empty)) and
        (__ \ lastUpdated).read(MongoJavatimeFormats.instantFormat) and
        (__ \ hasBeenSubmitted).read[Boolean] and
        (__ \ submittedDraftId).readNullable[String]
      )(UserAnswers.apply _)

  val writes: OWrites[UserAnswers] =
    (
      (__ \ ern).write[String] and
        (__ \ draftId).write[String] and
        (__ \ data).write[JsObject] and
        (__ \ submissionFailures).write[Seq[MovementSubmissionFailure]] and
        (__ \ lastUpdated).write(MongoJavatimeFormats.instantFormat) and
        (__ \ hasBeenSubmitted).write[Boolean] and
        (__ \ submittedDraftId).writeNullable[String]
      )(unlift(UserAnswers.unapply))

  implicit val format: OFormat[UserAnswers] = OFormat(reads, writes)
}
