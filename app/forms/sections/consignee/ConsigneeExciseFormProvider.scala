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
import forms.{ALPHANUMERIC_REGEX, EXCISE_NUMBER_REGEX, NOT_STARTS_WITH_GBWK_OR_XIWK, NOT_STARTS_WITH_GB_OR_XI, STARTS_WITH_GBWK_OR_XIWK, STARTS_WITH_GB_OR_XI}
import models.requests.DataRequest
import models.sections.info.DispatchPlace.{GreatBritain, NorthernIreland}
import models.sections.info.movementScenario.{DestinationType, MovementScenario, MovementType}
import models.sections.info.movementScenario.MovementScenario.{EuTaxWarehouse, RegisteredConsignee, TemporaryRegisteredConsignee, UkTaxWarehouse}
import pages.sections.consignee.ConsigneeExcisePage
import pages.sections.info.{DestinationTypePage, DispatchPlacePage}
import play.api.data.Form
import play.api.data.validation.{Constraint, Valid}
import play.api.i18n.Messages

import javax.inject.Inject

class ConsigneeExciseFormProvider @Inject() extends Mappings {


  def apply(isNorthernIrishTemporaryRegisteredConsignee: Boolean)(implicit request: DataRequest[_], messages: Messages): Form[String] = {

    val noInputErrorKey = if (isNorthernIrishTemporaryRegisteredConsignee) {
      "consigneeExcise.temporaryConsignee.error.noInput"
    } else {
      "consigneeExcise.error.noInput"
    }

    val not13CharactersErrorKey = if (isNorthernIrishTemporaryRegisteredConsignee) {
      "consigneeExcise.temporaryConsignee.error.length"
    }
    else {
      "consigneeExcise.error.length"
    }

    val invalidCharactersErrorKey = if (isNorthernIrishTemporaryRegisteredConsignee) {
      "consigneeExcise.temporaryConsignee.error.invalidCharacters"
    }
    else {
      "consigneeExcise.error.invalidCharacters"
    }

    val formatErrorKey = if(isNorthernIrishTemporaryRegisteredConsignee) {
      "consigneeExcise.temporaryConsignee.error.format"
    } else {
      "consigneeExcise.error.format"
    }

    Form(
      "value" -> text(noInputErrorKey)
        .verifying(firstError(
          fixedLength(13, not13CharactersErrorKey),
          regexpUnlessEmpty(ALPHANUMERIC_REGEX, invalidCharactersErrorKey),
          regexpUnlessEmpty(EXCISE_NUMBER_REGEX, formatErrorKey),
          rimRule1,
          rimRule2,
          rimRule3,
          rimRule4,
          rimRule5,
          rimRule6,
          rimRule7,
          rimRule8
        ))
        .verifying(isNotEqualToOptExistingAnswer(
          existingAnswer = ConsigneeExcisePage.getOriginalAttributeValue,
          errorKey = "consigneeExcise.error.submissionError"
        ))
    )
  }

  /**
   * If [DESTINATIONTYPECODE] eq 1, 2, 3, 4, 9, 10 or 11
   * and [RECIPIENT] does not end with "GB" or "XI",
   * then [TRADERID] must not start with "GB" or "XI".
   *
   * consignee ERN must not start with GB or XI
   * when the place of destination is either a EU warehouse, Registered Consignee or Temporary Registered Consignee
   *
   * @param request
   * @return
   */
  private def rimRule1(implicit request: DataRequest[_], messages: Messages): Constraint[String] = {
    request.userAnswers.get(DestinationTypePage) match {
      case Some(destination) if Seq(EuTaxWarehouse, RegisteredConsignee, TemporaryRegisteredConsignee).contains(destination) =>
        regexp(NOT_STARTS_WITH_GB_OR_XI, messages("consigneeExcise.error.rimRule1", destination.stringValue))
      case _ =>
        Constraint(_ => Valid)
    }
  }

  private def rimRule2(implicit request: DataRequest[_]): Constraint[String] = {
    request.userAnswers.get(DispatchPlacePage) match {
      case Some(GreatBritain | NorthernIreland) =>
        regexp(STARTS_WITH_GB_OR_XI, "consigneeExcise.error.rimRule2")
      case _ =>
        Constraint(_ => Valid)
    }
  }

  private def rimRule3(implicit request: DataRequest[_]): Constraint[String] = {
    if (request.ern.startsWith("GBRC")) {
      regexp(STARTS_WITH_GB_OR_XI, "consigneeExcise.error.rimRule3")
    } else {
      Constraint(_ => Valid)
    }
  }

  private def rimRule4(implicit request: DataRequest[_]): Constraint[String] = {
    request.userAnswers.get(DestinationTypePage) match {
      case Some(UkTaxWarehouse.GB) =>
        regexp(STARTS_WITH_GBWK_OR_XIWK, "consigneeExcise.error.rimRule4")
      case _ =>
        Constraint(_ => Valid)
    }
  }

  private def rimRule5(implicit request: DataRequest[_]): Constraint[String] = {
    request.userAnswers.get(DestinationTypePage) match {
      case Some(destination) if destination != UkTaxWarehouse.GB =>
        regexp(NOT_STARTS_WITH_GBWK_OR_XIWK, "consigneeExcise.error.rimRule5")
      case _ =>
        Constraint(_ => Valid)
    }
  }

    private def rimRule6(implicit request: DataRequest[_]): Constraint[String] = {
      request.userAnswers.get(DestinationTypePage) match {
        case Some(UkTaxWarehouse.GB) =>
          regexp("(XIWK).*", "If ERN starts with XI it must be followed by the capital letters WK, and 9 numbers")
        case _ =>
          Constraint(_ => Valid)
      }
    }

  private def rimRule7(implicit request: DataRequest[_]): Constraint[String] = {
    (
      request.userAnswers.get(DestinationTypePage),
      request.userAnswers.get(DispatchPlacePage)
    ) match {
      case (Some(UkTaxWarehouse.GB), _) => // TODO need to check place of destination starts with GB00
        regexp("(?!(GB)).*", "ERN must start with GB, followed by 11 numbers or mixed letters and numbers")
      case _ =>
        Constraint(_ => Valid)
    }
  }

  private def rimRule8(implicit request: DataRequest[_]): Constraint[String] = {
    (
      request.userAnswers.get(DestinationTypePage),
      request.userAnswers.get(DispatchPlacePage)
    ) match {
      case (Some(UkTaxWarehouse.GB), _) => // TODO need to check place of destination starts with XI00
        regexp("(?!(XI)).*", "ERN must start with XI, followed by 11 numbers or mixed letters and numbers")
      case _ =>
        Constraint(_ => Valid)
    }
  }


}
