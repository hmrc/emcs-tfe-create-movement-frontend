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

package generators

import models.UserAddress
import models.sections.consignee.ConsigneeExportVat
import models.sections.journeyType.HowMovementTransported
import org.scalacheck.Arbitrary
import org.scalacheck.Arbitrary._
import pages._
import pages.sections.consignee._
import pages.sections.journeyType.HowMovementTransportedPage
import play.api.libs.json.{JsValue, Json}

trait UserAnswersEntryGenerators extends PageGenerators with ModelGenerators {

  implicit lazy val arbitraryConsigneeExportVatUserAnswersEntry: Arbitrary[(ConsigneeExportVatPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[ConsigneeExportVatPage.type]
        value <- arbitrary[ConsigneeExportVat].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryConsigneeExportUserAnswersEntry: Arbitrary[(ConsigneeExportPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[ConsigneeExportPage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryConsigneeBusinessNameUserAnswersEntry: Arbitrary[(ConsigneeBusinessNamePage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[ConsigneeBusinessNamePage.type]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryConsigneeAddressUserAnswersEntry: Arbitrary[(ConsigneeAddressPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[ConsigneeAddressPage.type]
        value <- arbitrary[UserAddress].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryConsignorAddressUserAnswersEntry: Arbitrary[(ConsignorAddressPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[ConsignorAddressPage.type]
        value <- arbitrary[UserAddress].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryDeferredMovementUserAnswersEntry: Arbitrary[(DeferredMovementPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[DeferredMovementPage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryLocalReferenceNumberUserAnswersEntry: Arbitrary[(LocalReferenceNumberPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[LocalReferenceNumberPage.type]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryHowMovementTransportedUserAnswersEntry: Arbitrary[(HowMovementTransportedPage.type, JsValue)] =
    Arbitrary {
      for {
        page <- arbitrary[HowMovementTransportedPage.type]
        value <- arbitrary[HowMovementTransported].map(Json.toJson(_))
      } yield (page, value)
    }
}
