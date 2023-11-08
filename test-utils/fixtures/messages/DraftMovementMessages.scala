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

package fixtures.messages

object DraftMovementMessages {
  sealed trait ViewMessages extends BaseMessages {
    _: i18n =>
    def headingGbTaxWarehouseTo(input: String): String = s"Great Britain tax warehouse to $input"
    def titleGbTaxWarehouseTo(input: String): String = titleHelper(headingGbTaxWarehouseTo(input))
    def headingDispatchPlaceTo(input1: String, input2: String): String = s"$input1 tax warehouse to $input2"
    def titleDispatchPlaceTo(input1: String, input2: String): String = titleHelper(headingDispatchPlaceTo(input1, input2))
    def headingImportFor(input1: String): String = s"Import for $input1"
    def titleImportFor(input1: String): String = titleHelper(headingImportFor(input1))
    val headingImport = "What is the destination type for this import?"
    val titleImport = titleHelper(headingImport)
    val caption = "Movement information"
    val taxWarehouseInGb = "Tax warehouse in Great Britain"
    val cyaLabel: String = "Destination type"
    val cyaChangeHidden: String = "destination type"
  }

  object English extends ViewMessages with BaseEnglish


}