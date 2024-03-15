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

package utils

import models.{CheckMode, Index}
import models.requests.DataRequest
import play.api.mvc.Call

object SubmissionFailureErrorCodes {

  //TODO: replace with case objects
  val localReferenceNumberError = "4402"
  val importCustomsOfficeCodeError = "4451"
  val exportCustomsOfficeNumberError = "4425"
  val itemQuantityError = "4407"
  val itemDegreesPlatoError = "4445"

  sealed trait ErrorCode {
    val code: String
    val messageKey: String
    val id: String

    def route()(implicit request: DataRequest[_]): Call

    val index: Option[Index] = None
  }

  case class ItemQuantityError(idx: Index, isForAddToList: Boolean) extends ErrorCode {
    override val code = itemQuantityError
    override val messageKey = s"errors.704.items.quantity${if (isForAddToList) ".addToList" else ""}"
    override val id = s"fix-item-${idx.displayIndex}-quantity"

    override def route()(implicit request: DataRequest[_]): Call =
      controllers.sections.items.routes.ItemQuantityController.onPageLoad(request.ern, request.draftId, idx, CheckMode)

    override val index = Some(idx)

  }

  case class ItemDegreesPlatoError(idx: Index, isForAddToList: Boolean) extends ErrorCode {
    override val code = itemDegreesPlatoError
    override val messageKey = s"errors.704.items.degreesPlato${if (isForAddToList) ".addToList" else ""}"
    override val id = s"fix-item-${idx.displayIndex}-degrees-plato"

    override def route()(implicit request: DataRequest[_]): Call =
      controllers.sections.items.routes.ItemDegreesPlatoController.onPageLoad(request.ern, request.draftId, idx, CheckMode)

    override val index = Some(idx)
  }

  object ErrorCode {

    //scalastyle:off cyclomatic.complexity
    def apply(errorType: String, idx: Index, isForAddToList: Boolean = false): ErrorCode = errorType match {
      case "4407" => ItemQuantityError(idx, isForAddToList)
      case "4445" => ItemDegreesPlatoError(idx, isForAddToList)
    }
  }
}