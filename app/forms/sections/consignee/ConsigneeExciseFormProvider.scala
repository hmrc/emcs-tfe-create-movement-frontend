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
import models.sections.info.movementScenario.MovementScenario.{TemporaryCertifiedConsignee, TemporaryRegisteredConsignee}
import pages.sections.consignee.ConsigneeExcisePage
import pages.sections.info.DestinationTypePage
import play.api.data.Form

import javax.inject.Inject

class ConsigneeExciseFormProvider @Inject() extends Mappings {

  def apply()(implicit request: DataRequest[_]): Form[String] = {

    val keyPrefix = request.isNorthernIrelandErn -> request.userAnswers.get(DestinationTypePage) match {
      case true -> Some(TemporaryRegisteredConsignee) => "consigneeExcise.temporaryRegisteredConsignee"
      case true -> Some(TemporaryCertifiedConsignee) => "consigneeExcise.temporaryCertifiedConsignee"
      case _ => "consigneeExcise"
    }

    Form(
      "value" -> text(s"$keyPrefix.error.noInput")
        .verifying(firstError(
          fixedLength(13, s"$keyPrefix.error.length"),
          regexpUnlessEmpty(ALPHANUMERIC_REGEX, s"$keyPrefix.error.invalidCharacters"),
          regexpUnlessEmpty(EXCISE_NUMBER_REGEX, s"$keyPrefix.error.format")
        ))
        .verifying(isNotEqualToOptExistingAnswer(
          existingAnswer = ConsigneeExcisePage.getOriginalAttributeValue,
          errorKey = "consigneeExcise.error.submissionError"
        ))
    )
  }
}
