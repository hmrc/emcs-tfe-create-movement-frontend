/*
 * Copyright 2024 HM Revenue & Customs
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

package utils

object ExciseProductCodeHelper {

  def isSpirituousBeverages(epc: String): Boolean = epc == "S200"

  def isSpiritAndNotSpirituousBeverages(epc: String): Boolean = Seq("S300", "S400", "S500", "S600").contains(epc)

  def isLiquid(epc: String): Boolean =
    Seq(
      "B000", "W200", "W300", "I000", "S200",
      "S300", "S400", "S500", "E200", "E300",
      "E410", "E420", "E430", "E440", "E450",
      "E460", "E480", "E490", "E700", "E800",
      "E910", "E920"
    ).contains(epc)
}
