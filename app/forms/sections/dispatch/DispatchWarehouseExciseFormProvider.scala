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

package forms.sections.dispatch

import forms.mappings.Mappings
import forms.{GB_00_EXCISE_NUMBER_REGEX, XI_00_EXCISE_NUMBER_REGEX, XI_OR_GB_00_EXCISE_NUMBER_REGEX, XSS_REGEX}
import models.requests.DataRequest
import models.response.MissingMandatoryPage
import models.sections.info.DispatchPlace
import pages.sections.dispatch.DispatchWarehouseExcisePage
import pages.sections.info.DispatchPlacePage
import play.api.data.Form
import play.api.data.validation.Constraint

import javax.inject.Inject

class DispatchWarehouseExciseFormProvider @Inject() extends Mappings {

  def apply()(implicit request: DataRequest[_]): Form[String] = {
    Form(
      "value" -> text("dispatchWarehouseExcise.error.required")
        .transform[String](_.toUpperCase.replace(" ", ""), identity)
        .verifying(firstError(
          fixedLength(13, "dispatchWarehouseExcise.error.length"),
          regexpUnlessEmpty(XSS_REGEX, "dispatchWarehouseExcise.error.xss"),
          validationForERNBasedOnConsignor
        ))
        .verifying(isNotEqualToOptExistingAnswer(
          existingAnswer = DispatchWarehouseExcisePage.getOriginalAttributeValue,
          errorKey = "dispatchWarehouseExcise.error.submissionError"
        ))
    )
  }

  private[dispatch] def validationForERNBasedOnConsignor(implicit request: DataRequest[_]): Constraint[String] = {
    if(request.isNorthernIrelandErn) {
      if(request.isWarehouseKeeper) {
        DispatchPlacePage.value.map {
          case DispatchPlace.GreatBritain => regexpUnlessEmpty(GB_00_EXCISE_NUMBER_REGEX, "dispatchWarehouseExcise.error.mustStartWithGB00")
          case DispatchPlace.NorthernIreland => regexpUnlessEmpty(XI_00_EXCISE_NUMBER_REGEX, "dispatchWarehouseExcise.error.mustStartWithXI00")
        }.getOrElse(throw MissingMandatoryPage(s"Missing mandatory page ${DispatchPlacePage.toString} for Northern Ireland Warehouse Keeper ${request.ern}"))
      } else {
        regexpUnlessEmpty(XI_OR_GB_00_EXCISE_NUMBER_REGEX, "dispatchWarehouseExcise.error.mustStartWithGBOrXI00")
      }
    } else {
      regexpUnlessEmpty(GB_00_EXCISE_NUMBER_REGEX, "dispatchWarehouseExcise.error.mustStartWithGB00")
    }
  }
}
