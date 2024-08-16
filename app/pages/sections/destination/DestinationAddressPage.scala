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

package pages.sections.destination

import models.UserAddress
import models.requests.DataRequest
import pages.QuestionPage
import play.api.libs.json.{JsPath, Reads}

case object DestinationAddressPage extends QuestionPage[UserAddress] {
  override val toString: String = "destinationAddress"
  override val path: JsPath = DestinationSection.path \ toString

  // Old business name page for use in transitional period between separate and combined business name and address pages
  // TODO: remove eventually, this won't be set in new drafts
  private case object DestinationBusinessNamePage extends QuestionPage[String] {
    override val toString: String = "destinationBusinessName"
    override val path: JsPath = DestinationSection.path \ toString
  }

  override def value[T >: UserAddress](implicit request: DataRequest[_], reads: Reads[T]): Option[T] = {
    request.userAnswers.get(this).map {
      case address@UserAddress(None, _, _, _, _) => address.copy(businessName = request.userAnswers.get(DestinationBusinessNamePage))
      case address => address
    }
  }
}
