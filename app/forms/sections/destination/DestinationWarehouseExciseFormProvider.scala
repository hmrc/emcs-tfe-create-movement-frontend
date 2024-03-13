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
import models.requests.DataRequest
import pages.sections.destination.DestinationWarehouseExcisePage
import play.api.data.Form

import javax.inject.Inject

class DestinationWarehouseExciseFormProvider @Inject() extends Mappings {


  def apply()(implicit dataRequest: DataRequest[_]): Form[String] = {
    val optOriginalValueSentInPreviousSubmission = DestinationWarehouseExcisePage.getOriginalAttributeValue

    Form(
      "value" -> text("destinationWarehouseExcise.error.required")
        .verifying(regexpUnlessEmpty(XSS_REGEX, "destinationWarehouseExcise.error.invalidCharacter"))
        .verifying(maxLength(16, "destinationWarehouseExcise.error.length"))
        .verifying(isNotEqualToOptExistingAnswer(optOriginalValueSentInPreviousSubmission, "destinationWarehouseExcise.updateOriginalSubmission"))
    )
  }
}
