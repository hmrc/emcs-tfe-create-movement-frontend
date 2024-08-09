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
import forms.sections.items.ItemPackagingEnterShippingMarksFormProvider.maxLengthOfField
import forms.{ALPHANUMERIC_REGEX, XSS_REGEX}
import models.Index
import models.requests.DataRequest
import pages.sections.items.{ItemPackagingShippingMarksPage, ItemsSection}
import play.api.data.Form
import play.api.data.validation.{Constraint, Invalid, Valid}
import play.api.i18n.Messages

import javax.inject.Inject

class ItemPackagingEnterShippingMarksFormProvider @Inject() extends Mappings {

  def apply(currentItemsIdx: Index, currentPackagingIdx: Index)(implicit dataRequest: DataRequest[_], messages: Messages): Form[String] =
    Form(
      "value" -> normalisedSpaceText("itemPackagingEnterShippingMarks.error.required")
        .verifying(maxLength(maxLengthOfField, "itemPackagingEnterShippingMarks.error.length"))
        .verifying(regexpUnlessEmpty(ALPHANUMERIC_REGEX, "itemPackagingEnterShippingMarks.error.character"))
        .verifying(regexpUnlessEmpty(XSS_REGEX, "itemPackagingEnterShippingMarks.error.invalid"))
        .verifying(shippingMarkUnique(currentItemsIdx, currentPackagingIdx))
    )


  private def shippingMarkUnique(currentItemsIdx: Index, currentPackagingIdx: Index)
                                (implicit request: DataRequest[_], messages: Messages): Constraint[String] =
    Constraint {
      shippingMarkEntered => {
        val shippingMarkValueFromDB = ItemPackagingShippingMarksPage(currentItemsIdx, currentPackagingIdx).value

        if (shippingMarkValueFromDB.contains(shippingMarkEntered)) {
          Valid
        } else {
          ItemsSection
            .retrieveShippingMarkLocationsMatching(valueToMatch = shippingMarkEntered)
            .filterNot(_ == (currentItemsIdx, currentPackagingIdx)) match {
            case Nil => Valid
            case _ => Invalid(messages("itemPackagingEnterShippingMarks.error.not.unique", currentItemsIdx.displayIndex))
          }
        }
      }
    }
}

object ItemPackagingEnterShippingMarksFormProvider {
  val maxLengthOfField: Int = 999
}
