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
        traderExciseNumber = (request.userAnswers.get(ConsigneeExcisePage), request.userAnswers.get(ConsigneeExportVatPage)) match {
          case (Some(ern), _) => Some(ern)
          case (_, Some(vatNumber)) => Some(vatNumber)
          case _ => None
        },
        traderName = Some(mandatoryPage(ConsigneeBusinessNamePage)),
        address = Some(AddressModel.fromUserAddress(consigneeAddress)),
        vatNumber = None,
        eoriNumber = request.userAnswers.get(ConsigneeExportEoriPage)
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
      traderName = Some(request.traderKnownFacts.traderName),
      address = Some(AddressModel.fromUserAddress(consignorAddress)),
      vatNumber = None,
      eoriNumber = None
    )
  }

  def applyPlaceOfDispatch(implicit request: DataRequest[_]): Option[TraderModel] = {
    if (DispatchSection.canBeCompletedForTraderAndDestinationType) {
      val name = if(request.userAnswers.get(DispatchUseConsignorDetailsPage).contains(true)) {
        Some(request.traderKnownFacts.traderName)
      } else {
        request.userAnswers.get(DispatchBusinessNamePage)
      }
      Some(TraderModel(
        traderExciseNumber = request.userAnswers.get(DispatchWarehouseExcisePage),
        traderName = name,
        address = Some(AddressModel.fromUserAddress(mandatoryPage(DispatchAddressPage))),
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
          Some(TraderModel(
            traderExciseNumber = Some(mandatoryPage(DestinationWarehouseExcisePage)),
            traderName = Some(mandatoryPage(DestinationBusinessNamePage)),
            address = Some(AddressModel.fromUserAddress(mandatoryPage(DestinationAddressPage)))
          ))
        case (_, true) =>

          val giveAddressAndBusinessName: Boolean =
            if (DestinationSection.shouldSkipDestinationDetailsChoice(movementScenario)) true else mandatoryPage(DestinationDetailsChoicePage)

          Some(TraderModel(
            traderExciseNumber = request.userAnswers.get(DestinationWarehouseVatPage),
            traderName = Option.when(giveAddressAndBusinessName)(request.userAnswers.get(DestinationBusinessNamePage)).flatten,
            address = Option.when(giveAddressAndBusinessName)(request.userAnswers.get(DestinationAddressPage).map(AddressModel.fromUserAddress)).flatten
          ))
        case _ =>
          Some(TraderModel(
            traderExciseNumber = None,
            traderName = request.userAnswers.get(DestinationBusinessNamePage),
            address = request.userAnswers.get(DestinationAddressPage).map(AddressModel.fromUserAddress)
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
      case _ => Some(TraderModel(
        traderExciseNumber = None,
        traderName = Some(mandatoryPage(TransportArrangerNamePage)),
        address = Some(AddressModel.fromUserAddress(mandatoryPage(TransportArrangerAddressPage))),
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
    Some(TraderModel(
      traderExciseNumber = None,
      traderName = Some(mandatoryPage(FirstTransporterNamePage)),
      address = Some(AddressModel.fromUserAddress(mandatoryPage(FirstTransporterAddressPage))),
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
      case _ => Some(TraderModel(
        traderExciseNumber = None,
        traderName = Some(mandatoryPage(GuarantorNamePage)),
        address = Some(AddressModel.fromUserAddress(mandatoryPage(GuarantorAddressPage))),
        vatNumber = Some(mandatoryPage(GuarantorVatPage).vatNumber.getOrElse(NONGBVAT)),
        eoriNumber = None
      ))
    }
  }

  implicit val fmt: Format[TraderModel] = Json.format
}
