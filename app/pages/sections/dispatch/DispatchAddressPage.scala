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

package pages.sections.dispatch

import models.UserAddress
import models.requests.DataRequest
import pages.QuestionPage
import pages.sections.consignor.ConsignorAddressPage
import play.api.libs.json.JsPath

case object DispatchAddressPage extends QuestionPage[UserAddress] {
  override val toString: String = "dispatchAddress"
  override val path: JsPath = DispatchSection.path \ toString

  def businessName(implicit request: DataRequest[_]): Option[String] = {
    val address = this.value
    if(DispatchUseConsignorDetailsPage.value.contains(true)) {
      Seq(
        address.flatMap(_.businessName),
        request.userAnswers.get(ConsignorAddressPage).flatMap(_.businessName),
        request.traderKnownFacts.map(_.traderName)
      ).collectFirst {
        case Some(value) => value
      }
    } else {
      address.flatMap(_.businessName)
    }
  }
}

