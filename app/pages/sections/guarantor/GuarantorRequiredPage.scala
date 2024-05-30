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

import config.Constants
import models.GoodsType
import models.requests.DataRequest
import models.sections.info.movementScenario.DestinationType.Export
import models.sections.info.movementScenario.MovementScenario._
import models.sections.journeyType.HowMovementTransported.FixedTransportInstallations
import models.sections.transportUnit.TransportUnitType.FixedTransport
import pages.QuestionPage
import pages.sections.consignee.ConsigneeExcisePage
import pages.sections.info.DestinationTypePage
import pages.sections.items.ItemsSectionItems
import pages.sections.journeyType.HowMovementTransportedPage
import pages.sections.transportUnit.TransportUnitsSectionUnits
import play.api.libs.json.JsPath

case object GuarantorRequiredPage extends QuestionPage[Boolean] {

  override val toString: String = "guarantorRequired"
  override val path: JsPath = GuarantorSection.path \ toString

  def isRequired()(implicit request: DataRequest[_]): Boolean =
    guarantorAlwaysRequiredUk() || guarantorAlwaysRequiredNIToEU()

  def guarantorAlwaysRequiredUk()(implicit request: DataRequest[_]): Boolean =
    destinationTypeUkTaxWarehouseCondition || destinationTypeExportCondition || notGBOrXIConsigneeCondition

  def guarantorAlwaysRequiredNIToEU()(implicit request: DataRequest[_]): Boolean =
    destinationTypeToEUCondition && (isAlcoholOrTobaccoCondition || nonFixedMovementTransportTypeCondition || nonFixedTransportUnitTypeCondition)

  private def destinationTypeExportCondition(implicit request: DataRequest[_]): Boolean =
    request.userAnswers.get(DestinationTypePage).exists(_.destinationType == Export)

  private def notGBOrXIConsigneeCondition(implicit request: DataRequest[_]): Boolean =
    request.userAnswers.get(ConsigneeExcisePage) match {
      case Some(value) => !value.startsWith(Constants.GB_PREFIX) && !value.startsWith(Constants.NI_PREFIX)
      case None => false
    }

  private def destinationTypeUkTaxWarehouseCondition(implicit request: DataRequest[_]): Boolean = {

    val isUkTaxWarehouse = request.userAnswers.get(DestinationTypePage)
      .exists(UkTaxWarehouse.toList.contains(_))

    val hasReleventGoodsTypes = ItemsSectionItems.checkGoodsType(Seq(
      GoodsType.Spirits,
      GoodsType.Intermediate,
      GoodsType.Energy,
      GoodsType.Tobacco
    ))

    isUkTaxWarehouse && hasReleventGoodsTypes
  }

  private def destinationTypeToEUCondition(implicit request: DataRequest[_]): Boolean = {

    val releventDestinationTypes = Seq(
      EuTaxWarehouse,
      ExemptedOrganisation,
      UnknownDestination,
      TemporaryRegisteredConsignee,
      RegisteredConsignee,
      TemporaryCertifiedConsignee,
      CertifiedConsignee,
      DirectDelivery
    )

    request.userAnswers.get(DestinationTypePage).exists(releventDestinationTypes.contains(_))
  }

  private def isAlcoholOrTobaccoCondition(implicit request: DataRequest[_]): Boolean =
    ItemsSectionItems.checkGoodsType(Seq(
      GoodsType.Wine,
      GoodsType.Beer,
      GoodsType.Spirits,
      GoodsType.Intermediate,
      GoodsType.Tobacco
    ))

  private def nonFixedMovementTransportTypeCondition(implicit request: DataRequest[_]): Boolean =
    request.userAnswers.get(HowMovementTransportedPage).exists(_ != FixedTransportInstallations)

  private def nonFixedTransportUnitTypeCondition(implicit request: DataRequest[_]): Boolean =
    !TransportUnitsSectionUnits.containsTransportUnitType(FixedTransport).getOrElse(true)
}
