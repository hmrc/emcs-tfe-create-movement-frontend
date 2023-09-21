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

package models

import models.UserAddress.normalisedSeq
import play.api.libs.json.{Json, OFormat}
import play.twirl.api.{Html, HtmlFormat}

case class UserAddress(property: Option[String],
                       street: String,
                       town: String,
                       postcode: String) {
  def toCheckYourAnswersFormat: Html = Html(normalisedSeq(this).map(line => HtmlFormat.escape(line)).mkString("<br>"))

}

object UserAddress {

  private sealed trait AddressLineOrPostcode

  private final case class AddressLine(line: String) extends AddressLineOrPostcode

  private final case class Postcode(postcode: String) extends AddressLineOrPostcode

  private def normalisedSeq(address: UserAddress): Seq[String] = {
    Seq[Option[AddressLineOrPostcode]](
      address.property.map(AddressLine),
      Option(AddressLine(address.street)),
      Option(AddressLine(address.town)),
      Option(Postcode(address.postcode))
    ).collect {
      case Some(AddressLine(line)) => line.split("\\s").map(_.capitalize).mkString(" ")
      case Some(Postcode(postcode)) => postcode.toUpperCase()
    }
  }

  implicit lazy val format: OFormat[UserAddress] = Json.format[UserAddress]
}
