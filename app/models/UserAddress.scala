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

import play.api.libs.json.{Json, OFormat}
import play.twirl.api.{Html, HtmlFormat}
import uk.gov.hmrc.govukfrontend.views.Aliases.HtmlContent

case class UserAddress(businessName: Option[String],
                       property: Option[String],
                       street: Option[String],
                       town: Option[String],
                       postcode: Option[String]) {
  def toCheckYourAnswersFormat: HtmlContent = HtmlContent(
    HtmlFormat.fill(
      Seq(
        businessName.map(Html(_)),
        if(businessName.nonEmpty) Some(Html("<br>")) else None,
        Some(Html(property.map(_ + " ").getOrElse("") + street.getOrElse(""))),
        if(property.nonEmpty || street.nonEmpty) Some(Html("<br>")) else None,
        town.map(Html(_)),
        if(town.nonEmpty && postcode.nonEmpty) Some(Html("<br>")) else None,
        postcode.map(Html(_))
      ).flatten
    )
  )

}


object UserAddress {

  implicit lazy val format: OFormat[UserAddress] = Json.format[UserAddress]
}
