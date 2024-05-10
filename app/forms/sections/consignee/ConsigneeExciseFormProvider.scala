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

package forms.sections.consignee

import forms.mappings.Mappings
import forms.{ALPHANUMERIC_REGEX, EXCISE_NUMBER_REGEX}
import models.requests.DataRequest
import pages.sections.consignee.ConsigneeExcisePage
import play.api.data.Form

import javax.inject.Inject

class ConsigneeExciseFormProvider @Inject() extends Mappings {


  def apply(isNorthernIrishTemporaryRegisteredConsignee: Boolean)(implicit request: DataRequest[_]): Form[String] = {

    val noInputErrorKey = if (isNorthernIrishTemporaryRegisteredConsignee) {
      "consigneeExcise.temporaryRegisteredConsignee.error.noInput"
    } else {
      "consigneeExcise.error.noInput"
    }

    val not13CharactersErrorKey = if (isNorthernIrishTemporaryRegisteredConsignee) {
      "consigneeExcise.temporaryRegisteredConsignee.error.length"
    }
    else {
      "consigneeExcise.error.length"
    }

    val invalidCharactersErrorKey = if (isNorthernIrishTemporaryRegisteredConsignee) {
      "consigneeExcise.temporaryRegisteredConsignee.error.invalidCharacters"
    }
    else {
      "consigneeExcise.error.invalidCharacters"
    }

    val formatErrorKey = if(isNorthernIrishTemporaryRegisteredConsignee) {
      "consigneeExcise.temporaryRegisteredConsignee.error.format"
    } else {
      "consigneeExcise.error.format"
    }

    Form(
      "value" -> text(noInputErrorKey)
        .verifying(firstError(
          fixedLength(13, not13CharactersErrorKey),
          regexpUnlessEmpty(ALPHANUMERIC_REGEX, invalidCharactersErrorKey),
          regexpUnlessEmpty(EXCISE_NUMBER_REGEX, formatErrorKey)
        ))
        .verifying(isNotEqualToOptExistingAnswer(
          existingAnswer = ConsigneeExcisePage.getOriginalAttributeValue,
          errorKey = "consigneeExcise.error.submissionError"
        ))
    )
  }
}
