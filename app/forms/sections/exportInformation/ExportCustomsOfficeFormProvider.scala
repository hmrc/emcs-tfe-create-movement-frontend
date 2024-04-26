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

package forms.sections.exportInformation

import forms.mappings.Mappings
import forms.{CUSTOMS_OFFICE_CODE_REGEX, XSS_REGEX}
import models.NorthernIrelandRegisteredConsignor
import models.requests.DataRequest
import models.response.InvalidCustomsOfficeValidationException
import models.sections.info.DispatchPlace.{GreatBritain, NorthernIreland}
import pages.sections.exportInformation.ExportCustomsOfficePage
import play.api.data.Form
import play.api.data.validation.Constraint
import utils.Logging

import javax.inject.Inject

class ExportCustomsOfficeFormProvider @Inject() extends Mappings with Logging {

  def apply()(implicit dataRequest: DataRequest[_]): Form[String] = {
    val optOriginalValueSentInPreviousSubmission = ExportCustomsOfficePage.getOriginalAttributeValue
    Form(
      "value" -> text("exportCustomsOffice.error.required")
        .verifying(
          firstError(
            fixedLength(8, "exportCustomsOffice.error.length"),
            regexp(XSS_REGEX, s"exportCustomsOffice.error.invalidCharacter"),
            regexp(CUSTOMS_OFFICE_CODE_REGEX, s"exportCustomsOffice.error.customOfficeRegex"),
            isNotEqualToOptExistingAnswer(optOriginalValueSentInPreviousSubmission, "errors.704.exportOffice.input"),
            validateOfficePrefix
          )
        )
    )
  }

  private def validateOfficePrefix(implicit request: DataRequest[_]): Constraint[String] =
    (request.isGreatBritainErn, request.isNorthernIrelandErn, request.dispatchPlace) match {
      case (true, false, _) =>
        startsWith("GB", "exportCustomsOffice.error.mustStartWithGB")
      case (false, true, Some(GreatBritain)) =>
        startsWith("GB", "exportCustomsOffice.error.mustStartWithGB")
      case (false, true, Some(NorthernIreland)) =>
        doesNotStartWith("GB", "exportCustomsOffice.error.mustNotStartWithGBAsDispatchedFromNorthernIreland")
      case (false, true, _) if request.userTypeFromErn == NorthernIrelandRegisteredConsignor =>
        doesNotStartWith("GB", "exportCustomsOffice.error.mustNotStartWithGBAsNorthernIrelandRegisteredConsignor")
      case (isGB, isNI, dispatchPlace) =>
        val msg = s"[validateOfficePrefix] unexpected scenario: isGreatBritainErn=$isGB, isNorthernIrelandErn=$isNI, dispatchPlace=$dispatchPlace"
        logger.error(msg)
        throw InvalidCustomsOfficeValidationException(msg)
    }

}