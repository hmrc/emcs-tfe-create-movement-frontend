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

import base.SpecBase
import fixtures.UserAddressFixtures
import play.api.libs.json.{JsSuccess, Json}
import play.twirl.api.{Html, HtmlFormat}
import uk.gov.hmrc.govukfrontend.views.Aliases.HtmlContent

class UserAddressSpec extends SpecBase with UserAddressFixtures {

  "UserAddress with MAX values" - {

    "should read from json" in {
      Json.fromJson[UserAddress](userAddressJsonMax) mustBe JsSuccess(userAddressModelMax)
    }
    "should write to json" in {
      Json.toJson(userAddressModelMax) mustBe userAddressJsonMax
    }
  }

  "UserAddress with MIN values" - {

    "should read from json" in {
      Json.fromJson[UserAddress](userAddressJsonMin) mustBe JsSuccess(userAddressModelMin)
    }
    "should write to json" in {
      Json.toJson(userAddressModelMin) mustBe userAddressJsonMin
    }
  }

  ".toCheckYourAnswersFormat" - {
    "must return the correct HTML content" - {
      "when max model" in {
        userAddressModelMax.toCheckYourAnswersFormat mustBe HtmlContent(HtmlFormat.fill(
          Seq(
            Html(userAddressModelMax.businessName.value),
            Html("<br>"),
            Html(userAddressModelMax.property.value + " " + userAddressModelMax.street.value),
            Html("<br>"),
            Html(userAddressModelMax.town.value),
            Html("<br>"),
            Html(userAddressModelMax.postcode.value)
          )
        ))
      }

      "when min model" in {
        userAddressModelMin.toCheckYourAnswersFormat mustBe HtmlContent(HtmlFormat.fill(
          Seq()
        ))
      }

      "when businessName is None" in {
        val userAddressModel = userAddressModelMax.copy(businessName = None)
        userAddressModel.toCheckYourAnswersFormat mustBe HtmlContent(HtmlFormat.fill(
          Seq(
            Html(userAddressModel.property.value + " " + userAddressModel.street.value),
            Html("<br>"),
            Html(userAddressModel.town.value),
            Html("<br>"),
            Html(userAddressModel.postcode.value)
          )
        ))
      }

      "when property is None" in {
        val userAddressModel = userAddressModelMax.copy(property = None)
        userAddressModel.toCheckYourAnswersFormat mustBe HtmlContent(HtmlFormat.fill(
          Seq(
            Html(userAddressModelMax.businessName.value),
            Html("<br>"),
            Html(userAddressModel.street.value),
            Html("<br>"),
            Html(userAddressModel.town.value),
            Html("<br>"),
            Html(userAddressModel.postcode.value)
          )
        ))
      }

      "when street is None" in {
        val userAddressModel = userAddressModelMax.copy(street = None)
        userAddressModel.toCheckYourAnswersFormat mustBe HtmlContent(HtmlFormat.fill(
          Seq(
            Html(userAddressModelMax.businessName.value),
            Html("<br>"),
            Html(userAddressModel.property.value + " "),
            Html("<br>"),
            Html(userAddressModel.town.value),
            Html("<br>"),
            Html(userAddressModel.postcode.value)
          )
        ))
      }

      "when town is None" in {
        val userAddressModel = userAddressModelMax.copy(town = None)
        userAddressModel.toCheckYourAnswersFormat mustBe HtmlContent(HtmlFormat.fill(
          Seq(
            Html(userAddressModelMax.businessName.value),
            Html("<br>"),
            Html(userAddressModel.property.value + " " + userAddressModel.street.value),
            Html("<br>"),
            Html(userAddressModel.postcode.value)
          )
        ))
      }

      "when postcode is None" in {
        val userAddressModel = userAddressModelMax.copy(postcode = None)
        userAddressModel.toCheckYourAnswersFormat mustBe HtmlContent(HtmlFormat.fill(
          Seq(
            Html(userAddressModelMax.businessName.value),
            Html("<br>"),
            Html(userAddressModel.property.value + " " + userAddressModel.street.value),
            Html("<br>"),
            Html(userAddressModel.town.value)
          )
        ))
      }
    }
  }
}
