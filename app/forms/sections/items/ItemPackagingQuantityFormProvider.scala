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
import models.Index
import models.requests.DataRequest
import pages.sections.items.{ItemPackagingQuantityPage, ItemPackagingShippingMarksPage, ItemsSection}
import play.api.data.Form
import play.api.data.validation.{Constraint, Invalid, Valid}
import play.api.i18n.Messages

import javax.inject.Inject

class ItemPackagingQuantityFormProvider @Inject() extends Mappings {

  import ItemPackagingQuantityFormProvider._

  def apply(itemIndex: Index, packagingIndex: Index)(implicit messages: Messages, request: DataRequest[_]): Form[String] =
    Form(
      fieldName -> bigInt(
        requiredKey = requiredErrorKey(itemIndex),
        wholeNumberKey = decimalPlacesErrorKey,
        nonNumericKey = invalidCharactersErrorKey
      ).transform[String](_.toString(), BigInt(_))
        .verifying(maxLength(maxDigits, maxLengthErrorKey))
        .verifying(validateZeroEntry)
        .verifying(validateChangedValue(itemIndex, packagingIndex))
    )

  private def validateZeroEntry(implicit request: DataRequest[_]): Constraint[String] =
    Constraint {
      case str if BigInt(str) == 0 =>
        val allShippingMarks = ItemsSection.retrieveAllShippingMarks()
        allShippingMarks match {
          case Nil => Invalid(cannotBeZeroNoShippingMarksExistErrorKey)
          case _ => Valid
        }
      case _ =>
        Valid
    }

  private def validateChangedValue(itemIndex: Index, packagingIndex: Index)(implicit request: DataRequest[_]): Constraint[String] =
    Constraint {
      //If a shipping mark is defined for this packaging and the original quantity > 0 (i.e., this is the lead shipping mark)
      //show an error (user can't set a lead shipping mark quantity to 0)
      case str if BigInt(str) == 0 && request.userAnswers.get(ItemPackagingQuantityPage(itemIndex, packagingIndex)).exists(BigInt(_) > 0) =>
        if(request.userAnswers.get(ItemPackagingShippingMarksPage(itemIndex, packagingIndex)).isDefined) {
          Invalid(cannotBeZeroMustBeMoreThanZeroErrorKey)
        } else {
          Valid
        }
      case _ =>
        Valid
    }
}

object ItemPackagingQuantityFormProvider {

  val maxDigits = 15
  val fieldName = "value"
  val maxDecimalPlacesValue = 0

  def requiredErrorKey(itemIndex: Index)(implicit messages: Messages): String =
    messages("itemPackagingQuantity.error.required", itemIndex.displayIndex)

  val invalidCharactersErrorKey = "itemPackagingQuantity.error.invalidCharacters"
  val decimalPlacesErrorKey = "itemPackagingQuantity.error.decimalPlaces"
  val maxLengthErrorKey = "itemPackagingQuantity.error.tooLong"

  val cannotBeZeroNoShippingMarksExistErrorKey = "itemQuantity.error.mustNotBeZero.noShippingMarksExist"
  val cannotBeZeroMustBeMoreThanZeroErrorKey = "itemQuantity.error.mustNotBeZero.atLeastOnePackageWithQuantityMoreThanZero"

}
