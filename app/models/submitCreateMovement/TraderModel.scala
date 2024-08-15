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

package models.submitCreateMovement

import config.Constants.NONGBVAT
import models.requests.DataRequest
import models.sections.guarantor.GuarantorArranger
import models.sections.info.movementScenario.MovementScenario
import models.sections.transportArranger.TransportArranger
import models.{NorthernIrelandTemporaryCertifiedConsignor, UserAddress}
import pages.sections.consignee._
import pages.sections.consignor._
import pages.sections.destination._
import pages.sections.dispatch._
import pages.sections.firstTransporter._
import pages.sections.guarantor._
import pages.sections.transportArranger._
import play.api.libs.json.{Format, Json}
import utils.ModelConstructorHelpers

case class TraderModel(traderExciseNumber: Option[String] = None,
                       traderName: Option[String] = None,
                       address: Option[AddressModel] = None,
                       vatNumber: Option[String] = None,
                       eoriNumber: Option[String] = None)

object TraderModel extends ModelConstructorHelpers {

  def applyConsignee(implicit request: DataRequest[_]): Option[TraderModel] = {
    if (ConsigneeSection.canBeCompletedForTraderAndDestinationType) {
      val consigneeAddress: UserAddress = mandatoryPage(ConsigneeAddressPage)

      Some(TraderModel(
        // Consignee section has multiple entry points.
        // If the ConsigneeExcisePage is defined, use that, otherwise use the VAT number entered on the ConsigneeExportVatPage.
        traderExciseNumber = (ConsigneeExcisePage.value, ConsigneeExportVatPage.value) match {
          case (Some(ern), _) => Some(ern)
          case (_, Some(vatNumber)) => Some(vatNumber)
          case _ => None
        },
        traderName = consigneeAddress.businessName,
        address = Some(AddressModel.fromUserAddress(consigneeAddress)),
        vatNumber = None,
        eoriNumber = ConsigneeExportEoriPage.value
      ))
    } else {
      None
    }
  }

  def applyConsignor(implicit request: DataRequest[_]): TraderModel = {
    val consignorAddress: UserAddress = mandatoryPage(ConsignorAddressPage)

    val ern = if (request.userTypeFromErn == NorthernIrelandTemporaryCertifiedConsignor) {
      mandatoryPage(ConsignorPaidTemporaryAuthorisationCodePage)
    } else {
      request.ern
    }

    TraderModel(
      traderExciseNumber = Some(ern),
      traderName = consignorAddress.businessName,
      address = Some(AddressModel.fromUserAddress(consignorAddress)),
      vatNumber = None,
      eoriNumber = None
    )
  }

  def applyPlaceOfDispatch(implicit request: DataRequest[_]): Option[TraderModel] = {
    if (DispatchSection.canBeCompletedForTraderAndDestinationType) {
      Some(TraderModel(
        traderExciseNumber = DispatchWarehouseExcisePage.value,
        traderName = DispatchAddressPage.value.flatMap(_.businessName),
        address = if (request.isCertifiedConsignor) {
          Some(AddressModel.fromUserAddress(mandatoryPage(DispatchAddressPage)))
        } else {
          DispatchAddressPage.value.map(AddressModel.fromUserAddress)
        },
        vatNumber = None,
        eoriNumber = None
      ))
    } else {
      None
    }
  }

  def applyDeliveryPlace(movementScenario: MovementScenario)(implicit request: DataRequest[_]): Option[TraderModel] = {
    if (DestinationSection.canBeCompletedForTraderAndDestinationType) {
      DestinationSection.shouldStartFlowAtDestinationWarehouseExcise(movementScenario) ->
        DestinationSection.shouldStartFlowAtDestinationWarehouseVat(movementScenario) match {
        case (true, _) =>
          val address = mandatoryPage(DestinationAddressPage)
          Some(TraderModel(
            traderExciseNumber = Some(mandatoryPage(DestinationWarehouseExcisePage)),
            traderName = address.businessName,
            address = Some(AddressModel.fromUserAddress(address))
          ))
        case (_, true) =>

          val giveAddressAndBusinessName: Boolean =
            if (DestinationSection.shouldSkipDestinationDetailsChoice(movementScenario)) true else mandatoryPage(DestinationDetailsChoicePage)

          Some(TraderModel(
            traderExciseNumber = DestinationWarehouseVatPage.value,
            traderName = Option.when(giveAddressAndBusinessName)(DestinationAddressPage.value.flatMap(_.businessName)).flatten,
            address = Option.when(giveAddressAndBusinessName)(DestinationAddressPage.value.map(AddressModel.fromUserAddress)).flatten
          ))
        case _ =>
          Some(TraderModel(
            traderExciseNumber = None,
            traderName = DestinationAddressPage.value.flatMap(_.businessName),
            address = DestinationAddressPage.value.map(AddressModel.fromUserAddress)
          ))
      }
    } else {
      None
    }
  }

  def applyTransportArranger(implicit request: DataRequest[_]): Option[TraderModel] = {
    val transportArranger: TransportArranger = mandatoryPage(TransportArrangerPage)

    transportArranger match {
      case TransportArranger.Consignor => None
      case TransportArranger.Consignee => None
      case _ =>
        val address = mandatoryPage(TransportArrangerAddressPage)
        Some(TraderModel(
          traderExciseNumber = None,
          traderName = address.businessName,
          address = Some(AddressModel.fromUserAddress(address)),
          /*
            On the TransportArrangerVatPage when the user clicks No we set the `vatNumber` to None
            We need to default this to NONGBVAT hence the getOrElse.
           */
          vatNumber = Some(mandatoryPage(TransportArrangerVatPage).vatNumber.getOrElse(NONGBVAT)),
          eoriNumber = None
        ))
    }
  }

  def applyFirstTransporter(implicit request: DataRequest[_]): Option[TraderModel] = {
    val address = mandatoryPage(FirstTransporterAddressPage)
    Some(TraderModel(
      traderExciseNumber = None,
      traderName = address.businessName,
      address = Some(AddressModel.fromUserAddress(address)),
      vatNumber = Some(mandatoryPage(FirstTransporterVatPage).vatNumber.getOrElse(NONGBVAT)),
      eoriNumber = None
    ))
  }

  def applyGuarantor(guarantorArranger: GuarantorArranger)(implicit request: DataRequest[_]): Option[TraderModel] = {
    guarantorArranger match {
      case GuarantorArranger.Consignor => None
      case GuarantorArranger.Consignee => None
      case GuarantorArranger.NoGuarantorRequired => None
      case GuarantorArranger.NoGuarantorRequiredUkToEu => None
      case _ =>
        val address = mandatoryPage(GuarantorAddressPage)
        Some(TraderModel(
          traderExciseNumber = None,
          traderName = address.businessName,
          address = Some(AddressModel.fromUserAddress(address)),
          vatNumber = Some(mandatoryPage(GuarantorVatPage).vatNumber.getOrElse(NONGBVAT)),
          eoriNumber = None
        ))
    }
  }

  implicit val fmt: Format[TraderModel] = Json.format
}
