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

import forms.XSS_REGEX
import forms.mappings.Mappings
import play.api.data.Form
import play.api.data.Forms.optional

import javax.inject.Inject


class DestinationWarehouseVatFormProvider @Inject() extends Mappings {

  private val VAT_NUMBER_MAX_LENGTH = 14

  def apply(): Form[Option[String]] = {
    Form(
      "value" -> optional(
        text("destinationWarehouseVat.error.required")
          .verifying(regexpUnlessEmpty(XSS_REGEX, "destinationWarehouseVat.error.invalidCharacters"))
          .verifying(maxLength(VAT_NUMBER_MAX_LENGTH, "destinationWarehouseVat.error.length"))
      )
    )
  }
}
