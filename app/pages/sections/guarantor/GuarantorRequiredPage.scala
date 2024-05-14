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

package pages.sections.guarantor

import config.Constants.{GB_PREFIX, NI_PREFIX}
import models.GoodsType
import models.requests.DataRequest
import models.sections.info.movementScenario.DestinationType.Export
import models.sections.info.movementScenario.MovementScenario.{DirectDelivery, EuTaxWarehouse, ExemptedOrganisation, RegisteredConsignee, TemporaryRegisteredConsignee, UkTaxWarehouse, UnknownDestination}
import models.sections.journeyType.HowMovementTransported.FixedTransportInstallations
import models.sections.transportUnit.TransportUnitType.FixedTransport
import pages.QuestionPage
import pages.sections.info.DestinationTypePage
import pages.sections.items.ItemExciseProductCodePage
import pages.sections.journeyType.HowMovementTransportedPage
import pages.sections.transportUnit.TransportUnitTypePage
import play.api.libs.json.JsPath
import queries.{ItemsCount, TransportUnitsCount}

case object GuarantorRequiredPage extends QuestionPage[Boolean] {

  override val toString: String = "guarantorRequired"
  override val path: JsPath = GuarantorSection.path \ toString

  def guarantorAlwaysRequired()(implicit request: DataRequest[_]): Boolean =
    destinationTypeUkTaxWarehouse || destinationTypeExport || ernNotIsGBorXI

  def guarantorAlwaysRequiredNIToEU()(implicit request: DataRequest[_]): Boolean =
    destinationTypeToEU && (goodsTypeAlcoholOrTobacco || isNotFixedMovementTransportType || isNotFixedTransportUnitType)

  private def destinationTypeExport(implicit request: DataRequest[_]): Boolean =
    request.userAnswers.get(DestinationTypePage).exists(_.destinationType == Export)

  private def ernNotIsGBorXI(implicit request: DataRequest[_]): Boolean =
    !(request.ern.startsWith(GB_PREFIX) || request.ern.startsWith(NI_PREFIX))

  private def destinationTypeUkTaxWarehouse(implicit request: DataRequest[_]): Boolean = {

    val isUkTaxWarehouse = request.userAnswers.get(DestinationTypePage)
      .exists(UkTaxWarehouse.toList.contains(_))

    val releventGoodsTypes: Seq[GoodsType] = Seq(
      GoodsType.Spirits,
      GoodsType.Intermediate,
      GoodsType.Energy,
      GoodsType.Tobacco
    )

    val itemCount = request.userAnswers.get(ItemsCount).getOrElse(0)

    val hasReleventGoodsTypes = (for (idx <- 0 until itemCount) yield request.userAnswers
      .get(ItemExciseProductCodePage(idx))
      .exists(x => releventGoodsTypes.contains(GoodsType.apply(x)))
    ).contains(true)

    isUkTaxWarehouse && hasReleventGoodsTypes
  }

  private def destinationTypeToEU(implicit request: DataRequest[_]): Boolean = {

    val releventDestinationTypes = Seq(
      EuTaxWarehouse,
      ExemptedOrganisation,
      UnknownDestination,
      TemporaryRegisteredConsignee,
      RegisteredConsignee,
      DirectDelivery
    )

    request.userAnswers.get(DestinationTypePage)
      .exists(releventDestinationTypes.contains(_))
  }

  private def goodsTypeAlcoholOrTobacco(implicit request: DataRequest[_]): Boolean = {

    val releventGoodsTypes: Seq[GoodsType] = Seq(
      GoodsType.Wine,
      GoodsType.Beer,
      GoodsType.Spirits,
      GoodsType.Intermediate,
      GoodsType.Tobacco
    )

    val itemCount = request.userAnswers.get(ItemsCount).getOrElse(0)

    (for (idx <- 0 until itemCount) yield request.userAnswers
      .get(ItemExciseProductCodePage(idx))
      .exists(x => releventGoodsTypes.contains(GoodsType.apply(x)))
    ).contains(true)

  }

  private def isNotFixedMovementTransportType(implicit request: DataRequest[_]): Boolean =
    !request.userAnswers.get(HowMovementTransportedPage).contains(FixedTransportInstallations)

  private def isNotFixedTransportUnitType(implicit request: DataRequest[_]): Boolean = {

    val transportUnitsCount = request.userAnswers.get(TransportUnitsCount).getOrElse(0)

    val transportUnitIsFixedList = for (idx <- 0 until transportUnitsCount) yield request.userAnswers
      .get(TransportUnitTypePage(idx))
      .contains(FixedTransport)

    !transportUnitIsFixedList.contains(true)
  }
}
