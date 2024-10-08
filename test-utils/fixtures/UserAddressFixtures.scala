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

package fixtures

import models.UserAddress
import play.api.libs.json.{JsObject, Json}

trait UserAddressFixtures {

  val userAddressModelMax: UserAddress = UserAddress(
    businessName = Some("Name"),
    property = Some("1"),
    street = Some("Street"),
    town = Some("Town"),
    postcode = Some("aa11aa")
  )

  val userAddressJsonMax: JsObject = Json.obj(
    "businessName" -> "Name",
    "property" -> "1",
    "street" -> "Street",
    "town" -> "Town",
    "postcode" -> "aa11aa"
  )

  val userAddressModelMin: UserAddress = UserAddress(
    businessName = None,
    property = None,
    street = None,
    town = None,
    postcode = None
  )

  val userAddressJsonMin: JsObject = Json.obj()
}
