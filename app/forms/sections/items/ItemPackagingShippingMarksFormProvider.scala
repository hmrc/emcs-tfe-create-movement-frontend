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

package forms.sections.items

import forms.mappings.Mappings
import forms.{ALPHANUMERIC_REGEX, XSS_REGEX}
import models.Index
import models.requests.DataRequest
import pages.sections.items.ItemsSection
import play.api.data.Form
import play.api.data.validation.{Constraint, Invalid, Valid}
import play.api.i18n.Messages

import javax.inject.Inject

class ItemPackagingShippingMarksFormProvider @Inject() extends Mappings {

  def apply(currentItemsIdx: Index, currentPackagingIdx: Index)(implicit dataRequest: DataRequest[_], messages: Messages): Form[String] =
    Form(
      "value" -> normalisedSpaceText("itemPackagingShippingMarks.error.required")
        .verifying(maxLength(999, "itemPackagingShippingMarks.error.length"))
        .verifying(regexpUnlessEmpty(ALPHANUMERIC_REGEX, "itemPackagingShippingMarks.error.character"))
        .verifying(regexpUnlessEmpty(XSS_REGEX, "itemPackagingShippingMarks.error.invalid"))
        .verifying(shippingMarkUnique(currentItemsIdx, currentPackagingIdx))
    )


  private def shippingMarkUnique(currentItemsIdx: Index, currentPackagingIdx: Index)
                                       (implicit request: DataRequest[_], messages: Messages): Constraint[String] = {
        Constraint {
      shippingMarkEntered =>
        ItemsSection.retrieveShippingMarkLocationsMatching(valueToMatch = shippingMarkEntered).filterNot(_ == (currentItemsIdx, currentPackagingIdx)) match {
          case Nil => Valid
          case _ => Invalid(messages("itemPackagingShippingMarks.error.not.unique", currentItemsIdx.displayIndex))
        }
    }
  }

}
