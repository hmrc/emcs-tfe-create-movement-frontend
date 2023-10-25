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

package forms.sections.transportUnit

import forms.mappings.Mappings
import forms.{ALPHANUMERIC_REGEX, XSS_REGEX}
import models.sections.transportUnit.TransportUnitType
import play.api.data.Form
import play.api.i18n.Messages

import javax.inject.Inject

class TransportUnitGiveMoreInformationFormProvider @Inject() extends Mappings {

  def apply(transportUnitType: TransportUnitType)(implicit messages: Messages): Form[String] =
    Form(
      "value" -> text("transportUnitGiveMoreInformation.error.required", args =
        Seq(messages(s"transportUnitGiveMoreInformation.transportUnitType.$transportUnitType")))
        .transform[String](
          _.replace("\n", " ")
            .replace("\r", " ")
            .replaceAll(" +", " ")
            .trim,
          identity
        )
        .verifying(maxLength(350, "transportUnitGiveMoreInformation.error.length"))
        .verifying(regexpUnlessEmpty(ALPHANUMERIC_REGEX, s"transportUnitGiveMoreInformation.error.character"))
        .verifying(regexpUnlessEmpty(XSS_REGEX, "transportUnitGiveMoreInformation.error.xss"))
    )
}
