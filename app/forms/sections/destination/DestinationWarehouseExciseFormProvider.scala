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

package forms.sections.destination

import config.Constants
import forms.mappings.Mappings
import forms.{GB_00_EXCISE_NUMBER_REGEX, XI_00_EXCISE_NUMBER_REGEX, XI_OR_GB_00_EXCISE_NUMBER_REGEX, XSS_REGEX}
import models.CountryModel
import models.requests.DataRequest
import models.sections.info.movementScenario.MovementScenario
import pages.sections.destination.DestinationWarehouseExcisePage
import play.api.data.Form
import play.api.data.validation.{Constraint, Invalid, Valid}

import javax.inject.Inject

class DestinationWarehouseExciseFormProvider @Inject() extends Mappings {

  //noinspection ScalaStyle
  private[forms] def inputIsValidForDestinationType(movementScenario: MovementScenario,
                                                    memberStates: Option[Seq[CountryModel]] = None
                                                   ): Constraint[String] =
    Constraint {
      case answer if movementScenario == MovementScenario.UkTaxWarehouse.GB =>
        regexp(GB_00_EXCISE_NUMBER_REGEX, "destinationWarehouseExcise.error.invalidGB00").apply(answer)
      case answer if movementScenario == MovementScenario.UkTaxWarehouse.NI =>
        regexp(XI_00_EXCISE_NUMBER_REGEX, "destinationWarehouseExcise.error.invalidXI00").apply(answer)
      case answer if movementScenario == MovementScenario.EuTaxWarehouse =>
        if (answer.startsWith(Constants.NI_PREFIX) || answer.startsWith(Constants.GB_PREFIX)) Invalid("destinationWarehouseExcise.error.invalidXIOrGB") else {
          memberStates.map(_.map(_.code)) match {
            case Some(codes) if codes.contains(answer.take(2)) => Valid
            case Some(_) => Invalid("destinationWarehouseExcise.error.invalidMemberState")
            case _ => Valid
          }
        }
      case answer =>
        regexpToNotMatch(XI_OR_GB_00_EXCISE_NUMBER_REGEX, "destinationWarehouseExcise.error.invalidXIOrGB").apply(answer)
    }


  def apply(movementScenario: MovementScenario,
            memberStates: Option[Seq[CountryModel]])
           (implicit dataRequest: DataRequest[_]): Form[String] = {
    val optOriginalValueSentInPreviousSubmission = DestinationWarehouseExcisePage.getOriginalAttributeValue

    Form(
      "value" -> text("destinationWarehouseExcise.error.required")
        .transform[String](_.toUpperCase.replace(" ", ""), identity)
        .verifying(
          firstError(
            regexpUnlessEmpty(XSS_REGEX, "destinationWarehouseExcise.error.invalidCharacter"),
            maxLength(16, "destinationWarehouseExcise.error.length"),
            isNotEqualToOptExistingAnswer(optOriginalValueSentInPreviousSubmission, "destinationWarehouseExcise.error.submissionError"),
            inputIsValidForDestinationType(movementScenario, memberStates)
          )
        )
    )
  }
}
