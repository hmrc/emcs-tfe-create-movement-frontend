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

import models.GoodsType._
import models.{NorthernIrelandCertifiedConsignor, NorthernIrelandRegisteredConsignor, NorthernIrelandWarehouseKeeper}
import models.requests.DataRequest
import models.sections.info.movementScenario.MovementScenario._
import models.sections.journeyType.HowMovementTransported.FixedTransportInstallations
import models.sections.transportUnit.TransportUnitType.FixedTransport
import pages.QuestionPage
import pages.sections.info.DestinationTypePage
import pages.sections.items.ItemsSectionItems
import pages.sections.journeyType.HowMovementTransportedPage
import pages.sections.transportUnit.TransportUnitsSectionUnits
import play.api.libs.json.JsPath

case object GuarantorRequiredPage extends QuestionPage[Boolean] {

  override val toString: String = "guarantorRequired"
  override val path: JsPath = GuarantorSection.path \ toString

  def isRequired()(implicit request: DataRequest[_]): Boolean =
    !(guarantorIsOptionalUKtoUK || guarantorIsOptionalNIToEU)

  def guarantorIsOptionalUKtoUK(implicit request: DataRequest[_]): Boolean =
    DestinationTypePage.isUKtoUKMovement && ItemsSectionItems.onlyContainsOrIsEmpty(Beer, Wine)

  def guarantorIsOptionalNIToEU(implicit request: DataRequest[_]): Boolean = {
    DestinationTypePage.isNItoEuMovement && destinationTypeIsNotUnknownOrExempted && onlyFixedTransport &&
      Seq(NorthernIrelandRegisteredConsignor, NorthernIrelandCertifiedConsignor, NorthernIrelandWarehouseKeeper).contains(request.userTypeFromErn) &&
      ItemsSectionItems.onlyContainsOrIsEmpty(Energy)
  }

  private def destinationTypeIsNotUnknownOrExempted(implicit request: DataRequest[_]): Boolean =
    !Seq(UnknownDestination, ExemptedOrganisation).exists(DestinationTypePage.is)

  private def onlyFixedTransport(implicit request: DataRequest[_]): Boolean =
    (HowMovementTransportedPage.is(FixedTransportInstallations) || HowMovementTransportedPage.isEmpty) &&
      TransportUnitsSectionUnits.onlyContainsOrIsEmpty(FixedTransport)
}
