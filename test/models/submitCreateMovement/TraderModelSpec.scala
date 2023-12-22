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

import base.SpecBase
import models.requests.DataRequest
import models.sections.consignee.{ConsigneeExportVat, ConsigneeExportVatType}
import models.sections.guarantor.GuarantorArranger
import models.sections.info.movementScenario.MovementScenario
import models.sections.info.movementScenario.MovementScenario._
import models.sections.transportArranger.TransportArranger
import pages.sections.consignee._
import pages.sections.consignor._
import pages.sections.destination._
import pages.sections.dispatch._
import pages.sections.firstTransporter._
import pages.sections.guarantor._
import pages.sections.info.DestinationTypePage
import pages.sections.transportArranger._
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest

class TraderModelSpec extends SpecBase {

  lazy val fakeRequest: FakeRequest[AnyContentAsEmpty.type] = FakeRequest()

  val consigneeTraderWithErn: TraderModel = TraderModel(
    traderExciseNumber = Some("consignee ern"),
    traderName = Some("consignee name"),
    address = Some(AddressModel.fromUserAddress(testUserAddress.copy(street = "consignee street"))),
    vatNumber = None,
    eoriNumber = Some("consignee eori")
  )
  val consigneeTraderWithVatNo: TraderModel = TraderModel(
    traderExciseNumber = Some("vat no"),
    traderName = Some("consignee name"),
    address = Some(AddressModel.fromUserAddress(testUserAddress.copy(street = "consignee street"))),
    vatNumber = None,
    eoriNumber = None
  )
  val consigneeTraderWithNeitherErnNorVatNo: TraderModel = TraderModel(
    traderExciseNumber = None,
    traderName = Some("consignee name"),
    address = Some(AddressModel.fromUserAddress(testUserAddress.copy(street = "consignee street"))),
    vatNumber = None,
    eoriNumber = None
  )
  val consignorTrader: TraderModel = TraderModel(
    traderExciseNumber = Some(testErn),
    traderName = Some(testMinTraderKnownFacts.traderName),
    address = Some(AddressModel.fromUserAddress(testUserAddress.copy(street = "consignor street"))),
    vatNumber = None,
    eoriNumber = None
  )
  val placeOfDispatchTrader: TraderModel = TraderModel(
    traderExciseNumber = Some("dispatch ern"),
    traderName = Some("dispatch name"),
    address = Some(AddressModel.fromUserAddress(testUserAddress.copy(street = "dispatch street"))),
    vatNumber = None,
    eoriNumber = None
  )
  val deliveryPlaceTrader: TraderModel = TraderModel(
    traderExciseNumber = Some("destination ern"),
    traderName = Some("destination name"),
    address = Some(AddressModel.fromUserAddress(testUserAddress.copy(street = "destination street"))),
    vatNumber = None,
    eoriNumber = None
  )
  val transportArrangerTrader: TraderModel = TraderModel(
    traderExciseNumber = None,
    traderName = Some("arranger name"),
    address = Some(AddressModel.fromUserAddress(testUserAddress.copy(street = "arranger street"))),
    vatNumber = Some("arranger vat"),
    eoriNumber = None
  )
  val firstTransporterTrader: TraderModel = TraderModel(
    traderExciseNumber = None,
    traderName = Some("first name"),
    address = Some(AddressModel.fromUserAddress(testUserAddress.copy(street = "first street"))),
    vatNumber = Some("first vat"),
    eoriNumber = None
  )
  val guarantorTrader: TraderModel = TraderModel(
    traderExciseNumber = None,
    traderName = Some("guarantor name"),
    address = Some(AddressModel.fromUserAddress(testUserAddress.copy(street = "guarantor street"))),
    vatNumber = Some("guarantor vat"),
    eoriNumber = None
  )
  val guarantorTraderWithConsigneeInfo: TraderModel = TraderModel(
    traderExciseNumber = None,
    traderName = Some("consignee name"),
    address = Some(AddressModel.fromUserAddress(testUserAddress.copy(street = "consignee street"))),
    vatNumber = Some("vat no"),
    eoriNumber = None
  )

  "applyConsignee" - {
    "must return a TraderModel" - {
      "when an ERN is provided" in {
        MovementScenario.values.filterNot(_ == UnknownDestination).map {
          movementScenario =>
            implicit val dr: DataRequest[_] = dataRequest(fakeRequest,
              emptyUserAnswers
                .set(DestinationTypePage, movementScenario)
                .set(ConsigneeBusinessNamePage, "consignee name")
                .set(ConsigneeExcisePage, "consignee ern")
                .set(ConsigneeExportVatPage, ConsigneeExportVat(ConsigneeExportVatType.YesEoriNumber, Some("vat no"), Some("consignee eori")))
                .set(ConsigneeAddressPage, testUserAddress.copy(street = "consignee street"))
            )

            TraderModel.applyConsignee mustBe Some(consigneeTraderWithErn)
        }
      }
      "when an VAT number is provided" in {
        MovementScenario.values.filterNot(_ == UnknownDestination).map {
          movementScenario =>
            implicit val dr: DataRequest[_] = dataRequest(fakeRequest,
              emptyUserAnswers
                .set(DestinationTypePage, movementScenario)
                .set(ConsigneeBusinessNamePage, "consignee name")
                .set(ConsigneeExportVatPage, ConsigneeExportVat(ConsigneeExportVatType.YesVatNumber, Some("vat no"), None))
                .set(ConsigneeAddressPage, testUserAddress.copy(street = "consignee street"))
            )

            TraderModel.applyConsignee mustBe Some(consigneeTraderWithVatNo)
        }
      }
      "when neither ERN nor VAT number is provided" in {
        MovementScenario.values.filterNot(_ == UnknownDestination).map {
          movementScenario =>
            implicit val dr: DataRequest[_] = dataRequest(fakeRequest,
              emptyUserAnswers
                .set(DestinationTypePage, movementScenario)
                .set(ConsigneeBusinessNamePage, "consignee name")
                .set(ConsigneeAddressPage, testUserAddress.copy(street = "consignee street"))
            )

            TraderModel.applyConsignee mustBe Some(consigneeTraderWithNeitherErnNorVatNo)
        }
      }
    }
    "must return None" - {
      "when DestinationType is UnknownDestination" in {
        implicit val dr: DataRequest[_] = dataRequest(fakeRequest,
          emptyUserAnswers
            .set(DestinationTypePage, UnknownDestination)
            .set(ConsigneeExcisePage, "consignee ern")
            .set(ConsigneeExportVatPage, ConsigneeExportVat(ConsigneeExportVatType.YesEoriNumber, Some("vat no"), Some("consignee eori")))
            .set(ConsigneeAddressPage, testUserAddress.copy(street = "consignee street"))
        )

        TraderModel.applyConsignee mustBe None
      }
      "when DestinationType is missing" in {

      }
      implicit val dr: DataRequest[_] = dataRequest(fakeRequest,
        emptyUserAnswers
          .set(ConsigneeExcisePage, "consignee ern")
          .set(ConsigneeExportVatPage, ConsigneeExportVat(ConsigneeExportVatType.YesEoriNumber, Some("vat no"), Some("consignee eori")))
          .set(ConsigneeAddressPage, testUserAddress.copy(street = "consignee street"))
      )

      TraderModel.applyConsignee mustBe None
    }
  }

  "applyConsignor" - {
    "must return a TraderModel" in {
      implicit val dr: DataRequest[_] = dataRequest(fakeRequest, emptyUserAnswers.set(ConsignorAddressPage, testUserAddress.copy(street = "consignor street")))

      TraderModel.applyConsignor mustBe consignorTrader
    }
  }

  "applyPlaceOfDispatch" - {
    "must return a TraderModel" - {
      "when __WK and use consignor details = true" in {
        Seq("GBWK123", "XIWK123").foreach {
          ern =>
            implicit val dr: DataRequest[_] = dataRequest(
              fakeRequest,
              emptyUserAnswers
                .set(DispatchUseConsignorDetailsPage, true)
                .set(ConsignorAddressPage, testUserAddress.copy(street = "consignor street"))
                .set(DispatchWarehouseExcisePage, "dispatch ern"),
              ern
            )

            TraderModel.applyPlaceOfDispatch mustBe Some(consignorTrader.copy(traderExciseNumber = Some("dispatch ern")))
        }
      }
      "when __WK and use consignor details = false" in {
        Seq("GBWK123", "XIWK123").foreach {
          ern =>
            implicit val dr: DataRequest[_] = dataRequest(
              fakeRequest,
              emptyUserAnswers
                .set(DispatchUseConsignorDetailsPage, false)
                .set(DispatchBusinessNamePage, "dispatch name")
                .set(DispatchAddressPage, testUserAddress.copy(street = "dispatch street"))
                .set(DispatchWarehouseExcisePage, "dispatch ern"),
              ern
            )

            TraderModel.applyPlaceOfDispatch mustBe Some(placeOfDispatchTrader)
        }
      }
    }
    "must return None" - {
      "when __RC" in {
        Seq("GBRC123", "XIRC123").foreach {
          ern =>
            implicit val dr: DataRequest[_] = dataRequest(
              fakeRequest,
              emptyUserAnswers
                .set(DispatchUseConsignorDetailsPage, true)
                .set(ConsignorAddressPage, testUserAddress.copy(street = "consignor street"))
                .set(DispatchWarehouseExcisePage, "dispatch ern"),
              ern
            )

            TraderModel.applyPlaceOfDispatch mustBe None
        }
      }
    }
  }

  "applyDeliveryPlace" - {
    "must return a TraderModel" - {
      "when DestinationTypePage means shouldStartFlowAtDestinationWarehouseExcise" - {
        "when useConsigneeDetails = true" in {
          Seq(GbTaxWarehouse, EuTaxWarehouse).foreach {
            movementScenario =>
              implicit val dr: DataRequest[_] = dataRequest(
                fakeRequest,
                emptyUserAnswers
                  .set(DestinationTypePage, movementScenario)
                  .set(DestinationWarehouseExcisePage, "destination ern")
                  .set(DestinationConsigneeDetailsPage, true)
                  .set(DestinationBusinessNamePage, "destination name")
                  .set(DestinationAddressPage, testUserAddress.copy(street = "destination street"))
                  .set(ConsigneeBusinessNamePage, "consignee name")
                  .set(ConsigneeExcisePage, "consignee ern")
                  .set(ConsigneeExportVatPage, ConsigneeExportVat(ConsigneeExportVatType.YesEoriNumber, Some("vat no"), Some("consignee eori")))
                  .set(ConsigneeAddressPage, testUserAddress.copy(street = "consignee street"))
              )

              TraderModel.applyDeliveryPlace(movementScenario) mustBe
                Some(consigneeTraderWithErn.copy(traderExciseNumber = Some("destination ern"), eoriNumber = None))
          }
        }
        "when useConsigneeDetails = false" in {
          Seq(GbTaxWarehouse, EuTaxWarehouse).foreach {
            movementScenario =>
              implicit val dr: DataRequest[_] = dataRequest(
                fakeRequest,
                emptyUserAnswers
                  .set(DestinationTypePage, movementScenario)
                  .set(DestinationWarehouseExcisePage, "destination ern")
                  .set(DestinationConsigneeDetailsPage, false)
                  .set(DestinationBusinessNamePage, "destination name")
                  .set(DestinationAddressPage, testUserAddress.copy(street = "destination street"))
              )

              TraderModel.applyDeliveryPlace(movementScenario) mustBe Some(deliveryPlaceTrader)
          }
        }
      }
      "when DestinationTypePage means shouldStartFlowAtDestinationWarehouseVat" - {
        "when giveAddressAndBusinessName = true" in {
          Seq(RegisteredConsignee, TemporaryRegisteredConsignee, ExemptedOrganisation).foreach {
            movementScenario =>
              implicit val dr: DataRequest[_] = dataRequest(
                fakeRequest,
                emptyUserAnswers
                  .set(DestinationTypePage, movementScenario)
                  .set(DestinationWarehouseVatPage, "destination ern")
                  .set(DestinationDetailsChoicePage, true)
                  .set(DestinationBusinessNamePage, "destination name")
                  .set(DestinationAddressPage, testUserAddress.copy(street = "destination street"))
              )

              TraderModel.applyDeliveryPlace(movementScenario) mustBe Some(deliveryPlaceTrader)
          }
        }
        "when giveAddressAndBusinessName = false" in {
          Seq(RegisteredConsignee, TemporaryRegisteredConsignee, ExemptedOrganisation).foreach {
            movementScenario =>
              implicit val dr: DataRequest[_] = dataRequest(
                fakeRequest,
                emptyUserAnswers
                  .set(DestinationTypePage, movementScenario)
                  .set(DestinationWarehouseVatPage, "destination ern")
                  .set(DestinationDetailsChoicePage, false)
                  .set(DestinationBusinessNamePage, "destination name")
                  .set(DestinationAddressPage, testUserAddress.copy(street = "destination street"))
              )

              TraderModel.applyDeliveryPlace(movementScenario) mustBe Some(deliveryPlaceTrader.copy(traderName = None, address = None))
          }
        }
      }
      "when DestinationTypePage means shouldStartFlowAtDestinationBusinessName" in {
        Seq(DirectDelivery).foreach {
          movementScenario =>
            implicit val dr: DataRequest[_] = dataRequest(
              fakeRequest,
              emptyUserAnswers
                .set(DestinationTypePage, movementScenario)
                .set(DestinationWarehouseVatPage, "destination ern")
                .set(DestinationBusinessNamePage, "destination name")
                .set(DestinationAddressPage, testUserAddress.copy(street = "destination street"))
            )

            TraderModel.applyDeliveryPlace(movementScenario) mustBe Some(deliveryPlaceTrader.copy(traderExciseNumber = None))
        }
      }
    }
    "must return None" - {
      "DestinationType is invalid" in {
        MovementScenario
          .values
          .filterNot(Seq(GbTaxWarehouse, EuTaxWarehouse, RegisteredConsignee, TemporaryRegisteredConsignee, CertifiedConsignee, TemporaryCertifiedConsignee, ExemptedOrganisation, DirectDelivery).contains)
          .foreach {
            movementScenario =>
              implicit val dr: DataRequest[_] = dataRequest(
                fakeRequest,
                emptyUserAnswers
                  .set(DestinationTypePage, movementScenario)
                  .set(DestinationWarehouseVatPage, "destination ern")
                  .set(DestinationDetailsChoicePage, true)
                  .set(DestinationBusinessNamePage, "destination name")
                  .set(DestinationAddressPage, testUserAddress.copy(street = "destination street"))
              )

              TraderModel.applyDeliveryPlace(movementScenario) mustBe None
          }
      }
    }
  }

  "applyTransportArranger" - {
    "must return a TraderModel" - {
      "when Transport Arranger is Consignor" in {
        implicit val dr: DataRequest[_] = dataRequest(
          fakeRequest,
          emptyUserAnswers
            .set(TransportArrangerPage, TransportArranger.Consignor)
            .set(ConsignorAddressPage, testUserAddress.copy(street = "consignor street"))
        )

        TraderModel.applyTransportArranger mustBe None
      }
      "when Transport Arranger is Consignee" in {
        implicit val dr: DataRequest[_] = dataRequest(
          fakeRequest,
          emptyUserAnswers
            .set(TransportArrangerPage, TransportArranger.Consignee)
            .set(DestinationTypePage, MovementScenario.GbTaxWarehouse)
            .set(ConsigneeBusinessNamePage, "consignee name")
            .set(ConsigneeExcisePage, "consignee ern")
            .set(ConsigneeExportVatPage, ConsigneeExportVat(ConsigneeExportVatType.YesEoriNumber, Some("vat no"), Some("consignee eori")))
            .set(ConsigneeAddressPage, testUserAddress.copy(street = "consignee street"))
        )

        TraderModel.applyTransportArranger mustBe None
      }
      Seq(TransportArranger.GoodsOwner, TransportArranger.Other).foreach(
        transportArranger =>
          s"when Transport Arranger is $transportArranger" in {
            implicit val dr: DataRequest[_] = dataRequest(
              fakeRequest,
              emptyUserAnswers
                .set(TransportArrangerPage, transportArranger)
                .set(TransportArrangerNamePage, "arranger name")
                .set(TransportArrangerAddressPage, testUserAddress.copy(street = "arranger street"))
                .set(TransportArrangerVatPage, "arranger vat")
            )

            TraderModel.applyTransportArranger mustBe Some(transportArrangerTrader)
          }
      )
    }
  }

  "applyFirstTransporter" - {
    "must return a TraderModel" in {
      implicit val dr: DataRequest[_] = dataRequest(
        fakeRequest,
        emptyUserAnswers
          .set(FirstTransporterNamePage, "first name")
          .set(FirstTransporterAddressPage, testUserAddress.copy(street = "first street"))
          .set(FirstTransporterVatPage, "first vat")
      )

      TraderModel.applyFirstTransporter mustBe Some(firstTransporterTrader)
    }
  }

  "applyGuarantor" - {

    "must return a None" - {
      Seq(
        GuarantorArranger.Consignor,
        GuarantorArranger.Consignee,
        GuarantorArranger.NoGuarantorRequired,
        GuarantorArranger.NoGuarantorRequiredUkToEu
      ).foreach { guarantorArranger =>
        s"when Guarantor Arranger is $guarantorArranger" in {
          implicit val dr: DataRequest[_] = dataRequest(
            fakeRequest,
            emptyUserAnswers
          )

          TraderModel.applyGuarantor(guarantorArranger) mustBe None
        }
      }
    }

    "must return a TraderModel" - {
      Seq(GuarantorArranger.GoodsOwner, GuarantorArranger.Transporter).foreach(
        guarantorArranger =>
          s"when Guarantor Arranger is $guarantorArranger" in {
            implicit val dr: DataRequest[_] = dataRequest(
              fakeRequest,
              emptyUserAnswers
                .set(GuarantorNamePage, "guarantor name")
                .set(GuarantorAddressPage, testUserAddress.copy(street = "guarantor street"))
                .set(GuarantorVatPage, "guarantor vat")
            )

            TraderModel.applyGuarantor(guarantorArranger) mustBe Some(guarantorTrader)
          }
      )
    }
  }
}
