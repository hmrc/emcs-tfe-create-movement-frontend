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
import play.api.libs.json.{JsPath, Reads}

case object DispatchAddressPage extends QuestionPage[UserAddress] {
  override val toString: String = "dispatchAddress"
  override val path: JsPath = DispatchSection.path \ toString

  // Old business name page for use in transitional period between separate and combined business name and address pages
  // TODO: remove eventually, this won't be set in new drafts
  private case object DispatchBusinessNamePage extends QuestionPage[String] {
    override val toString: String = "businessName"
    override val path: JsPath = DispatchSection.path \ toString
  }

  override def value[T >: UserAddress](implicit request: DataRequest[_], reads: Reads[T]): Option[T] =
    request.userAnswers.get(this).map {
      address =>

        val businessNameFromDispatchSection: Option[String] = address.businessName match {
          case Some(value) => Some(value)
          case None => request.userAnswers.get(DispatchBusinessNamePage)
        }

        val businessName: Option[String] = if (DispatchUseConsignorDetailsPage.value.contains(true)) {
          Seq(
            request.traderKnownFacts.map(_.traderName),
            request.userAnswers.get(ConsignorAddressPage).flatMap(_.businessName),
            businessNameFromDispatchSection
          ).collectFirst {
            case Some(value) => value
          }
        } else {
          businessNameFromDispatchSection
        }

        address.copy(businessName = businessName)
    }
}

