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
import config.AppConfig
import fixtures.ItemFixtures
import fixtures.messages.sections.items.ItemSmallIndependentProducerMessages
import models.ExemptOrganisationDetailsModel
import models.requests.DataRequest
import models.response.referenceData.WineOperations
import models.sections.consignee._
import models.sections.documents.DocumentType
import models.sections.guarantor.GuarantorArranger
import models.sections.info._
import models.sections.info.movementScenario.{DestinationType, MovementScenario, MovementType, OriginType}
import models.sections.items._
import models.sections.journeyType.HowMovementTransported
import models.sections.transportArranger._
import models.sections.transportUnit.{TransportSealTypeModel, TransportUnitType}
import pages.sections.consignee._
import pages.sections.consignor._
import pages.sections.destination._
import pages.sections.dispatch._
import pages.sections.documents._
import pages.sections.exportInformation._
import pages.sections.firstTransporter._
import pages.sections.guarantor._
import pages.sections.importInformation._
import pages.sections.info._
import pages.sections.items._
import pages.sections.journeyType._
import pages.sections.sad.ImportNumberPage
import pages.sections.transportArranger._
import pages.sections.transportUnit._
import play.api.i18n.Messages
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest

import java.time.{LocalDate, LocalTime}

class SubmitCreateMovementModelSpec extends SpecBase with ItemFixtures {
  implicit val ac: AppConfig = appConfig

  val messagesForLanguage = ItemSmallIndependentProducerMessages.English
  implicit val msgs: Messages = messages(Seq(messagesForLanguage.lang))

  val fakeRequest: FakeRequest[AnyContentAsEmpty.type] = FakeRequest()

  val suffix = "004098"

  val baseUserAnswers = emptyUserAnswers
    // movementType
    .set(DestinationTypePage, MovementScenario.DirectDelivery)
    // consignee
    .set(ConsigneeBusinessNamePage, "consignee name")
    .set(ConsigneeExcisePage, "consignee ern")
    .set(ConsigneeExportVatPage, ConsigneeExportVat(ConsigneeExportVatType.YesEoriNumber, Some("vat no"), Some("consignee eori")))
    .set(ConsigneeAddressPage, testUserAddress.copy(street = "consignee street"))
    // consignor
    .set(ConsignorAddressPage, testUserAddress.copy(street = "consignor street"))
    // placeOfDispatch
    .set(DispatchUseConsignorDetailsPage, false)
    .set(DispatchBusinessNamePage, "dispatch name")
    .set(DispatchAddressPage, testUserAddress.copy(street = "dispatch street"))
    .set(DispatchWarehouseExcisePage, "dispatch ern")
    // dispatchImportOffice
    .set(ImportCustomsOfficeCodePage, "dispatch import office")
    // complementConsigneeTrader
    .set(ConsigneeExemptOrganisationPage, ExemptOrganisationDetailsModel("state", "number"))
    // deliveryPlaceTrader
    .set(DestinationWarehouseVatPage, "destination ern")
    .set(DestinationBusinessNamePage, "destination name")
    .set(DestinationAddressPage, testUserAddress.copy(street = "destination street"))
    // deliveryPlaceCustomsOffice
    .set(ExportCustomsOfficePage, "delivery place customs office")
    // transportArrangerTrader
    .set(TransportArrangerPage, TransportArranger.GoodsOwner)
    .set(TransportArrangerNamePage, "arranger name")
    .set(TransportArrangerAddressPage, testUserAddress.copy(street = "arranger street"))
    .set(TransportArrangerVatPage, "arranger vat")
    // firstTransporterTrader
    .set(FirstTransporterNamePage, "first name")
    .set(FirstTransporterAddressPage, testUserAddress.copy(street = "first street"))
    .set(FirstTransporterVatPage, "first vat")
    // documentCertificate
    .set(DocumentsCertificatesPage, true)
    .set(DocumentTypePage(testIndex1), DocumentType("0", "0 type desc"))
    .set(DocumentReferencePage(testIndex1), "0 reference")
    .set(DocumentDescriptionPage(testIndex1), "0 description")
    // headerEadEsad
    .set(JourneyTimeHoursPage, 2)
    // transportMode
    .set(HowMovementTransportedPage, HowMovementTransported.AirTransport)
    .set(GiveInformationOtherTransportPage, "info")
    // movementGuarantee
    .set(GuarantorRequiredPage, true)
    .set(GuarantorNamePage, "guarantor name")
    .set(GuarantorAddressPage, testUserAddress.copy(street = "guarantor street"))
    .set(GuarantorVatPage, "guarantor vat")
    .set(GuarantorArrangerPage, GuarantorArranger.GoodsOwner)
    .set(GuarantorNamePage, "guarantor name")
    .set(GuarantorAddressPage, testUserAddress.copy(street = "guarantor street"))
    .set(GuarantorVatPage, "guarantor vat")
    // bodyEadEsad
    .set(ItemExciseProductCodePage(testIndex1), testEpcWine)
    .set(ItemCommodityCodePage(testIndex1), testCnCodeWine)
    .set(ItemQuantityPage(testIndex1), BigDecimal(1))
    .set(ItemNetGrossMassPage(testIndex1), ItemNetGrossMassModel(netMass = BigDecimal(2), grossMass = BigDecimal(3)))
    .set(ItemAlcoholStrengthPage(testIndex1), BigDecimal(1.23))
    .set(ItemDegreesPlatoPage(testIndex1), ItemDegreesPlatoModel(hasDegreesPlato = true, Some(4.56)))
    .set(ItemFiscalMarksPage(testIndex1), "fiscal marks")
    .set(ItemFiscalMarksChoicePage(testIndex1), true)
    .set(ItemGeographicalIndicationPage(testIndex1), "talkin' 'bout my deeeeeesignation")
    .set(ItemProducerSizePage(testIndex1), BigInt(4))
    .set(ItemDensityPage(testIndex1), BigDecimal(7.89))
    .set(ItemCommercialDescriptionPage(testIndex1), "beans")
    .set(ItemBrandNamePage(testIndex1), ItemBrandNameModel(hasBrandName = true, Some("name")))
    .set(ItemMaturationPeriodAgePage(testIndex1), ItemMaturationPeriodAgeModel(hasMaturationPeriodAge = true, Some("really old")))
    .set(ItemBulkPackagingChoicePage(testIndex1), true)
    .set(ItemBulkPackagingSelectPage(testIndex1), bulkPackagingTypes.head)
    .set(ItemBulkPackagingSealTypePage(testIndex1), ItemPackagingSealTypeModel("seal type", Some("seal info")))
    .set(ItemImportedWineFromEuChoicePage(testIndex1), false)
    .set(ItemWineGrowingZonePage(testIndex1), ItemWineGrowingZone.CII)
    .set(ItemWineOriginPage(testIndex1), countryModelGB)
    .set(ItemWineMoreInformationPage(testIndex1), Some("more wine info"))
    .set(ItemWineOperationsChoicePage(testIndex1), Seq(WineOperations("op code", "choice desc")))
    // eadEsadDraft
    .set(LocalReferenceNumberPage(), testLrn)
    .set(InvoiceDetailsPage(), InvoiceDetailsModel("inv ref", LocalDate.parse("2020-12-25")))
    .set(DispatchDetailsPage(), DispatchDetailsModel(LocalDate.parse("2020-10-31"), LocalTime.parse("23:59:59.123")))
    .set(ImportNumberPage(testIndex1), "sad 1")
    .set(ImportNumberPage(testIndex2), "sad 2")
    .set(ImportNumberPage(testIndex3), "sad 3")
    // transportDetails
    .set(TransportUnitTypePage(testIndex1), TransportUnitType.FixedTransport)
    .set(TransportUnitIdentityPage(testIndex1), "identity")
    .set(TransportSealTypePage(testIndex1), TransportSealTypeModel("seal type", Some("seal info")))
    .set(TransportUnitGiveMoreInformationPage(testIndex1), Some("more info"))

  "dispatchOffice" - {
    "when XIRC" - {
      s"must return OfficeModel(XI$suffix)" in {
        implicit val dr: DataRequest[_] = dataRequest(
          request = fakeRequest,
          answers = emptyUserAnswers,
          ern = "XIRC123"
        )

        SubmitCreateMovementModel.dispatchOffice mustBe OfficeModel(s"XI$suffix")
      }
    }
    "when XIWK" - {
      s"must return OfficeModel(GB$suffix) when DispatchPlacePage is GreatBritain" in {
        implicit val dr: DataRequest[_] = dataRequest(
          request = fakeRequest,
          answers = emptyUserAnswers.set(DispatchPlacePage, DispatchPlace.GreatBritain),
          ern = "XIWK123"
        )

        SubmitCreateMovementModel.dispatchOffice mustBe OfficeModel(s"GB$suffix")
      }
      s"must return OfficeModel(XI$suffix) when DispatchPlacePage is NorthernIreland" in {
        implicit val dr: DataRequest[_] = dataRequest(
          request = fakeRequest,
          answers = emptyUserAnswers.set(DispatchPlacePage, DispatchPlace.NorthernIreland),
          ern = "XIWK123"
        )

        SubmitCreateMovementModel.dispatchOffice mustBe OfficeModel(s"XI$suffix")
      }
    }
    Seq("GBRC123", "GBWK123").foreach(
      ern =>
        s"when $ern" - {
          s"must return OfficeModel(GB$suffix)" in {
            implicit val dr: DataRequest[_] = dataRequest(
              request = fakeRequest,
              answers = emptyUserAnswers,
              ern = ern
            )

            SubmitCreateMovementModel.dispatchOffice mustBe OfficeModel(s"GB$suffix")
          }
        }
    )
  }

  "apply" - {
    "must return a model" - {
      "when XIRC" in {
        implicit val dr: DataRequest[_] = dataRequest(
          request = fakeRequest,
          answers = baseUserAnswers,
          ern = "XIRC123"
        )

        SubmitCreateMovementModel.apply mustBe SubmitCreateMovementModel(
          movementType = MovementType.ImportEu,
          attributes = AttributesModel(SubmissionMessageType.Standard, None),
          consigneeTrader = Some(TraderModel(
            traderExciseNumber = Some("consignee ern"),
            traderName = Some("consignee name"),
            address = Some(AddressModel.fromUserAddress(testUserAddress.copy(street = "consignee street"))),
            vatNumber = None,
            eoriNumber = Some("consignee eori")
          )),
          consignorTrader = TraderModel(
            traderExciseNumber = Some("XIRC123"),
            traderName = Some(testMinTraderKnownFacts.traderName),
            address = Some(AddressModel.fromUserAddress(testUserAddress.copy(street = "consignor street"))),
            vatNumber = None,
            eoriNumber = None
          ),
          placeOfDispatchTrader = None,
          dispatchImportOffice = Some(OfficeModel("dispatch import office")),
          complementConsigneeTrader = Some(ComplementConsigneeTraderModel("state", Some("number"))),
          deliveryPlaceTrader = Some(TraderModel(
            traderExciseNumber = None,
            traderName = Some("destination name"),
            address = Some(AddressModel.fromUserAddress(testUserAddress.copy(street = "destination street"))),
            vatNumber = None,
            eoriNumber = None
          )),
          deliveryPlaceCustomsOffice = Some(OfficeModel("delivery place customs office")),
          competentAuthorityDispatchOffice = OfficeModel(s"XI$suffix"),
          transportArrangerTrader = Some(TraderModel(
            traderExciseNumber = None,
            traderName = Some("arranger name"),
            address = Some(AddressModel.fromUserAddress(testUserAddress.copy(street = "arranger street"))),
            vatNumber = Some("arranger vat"),
            eoriNumber = None
          )),
          firstTransporterTrader = Some(TraderModel(
            traderExciseNumber = None,
            traderName = Some("first name"),
            address = Some(AddressModel.fromUserAddress(testUserAddress.copy(street = "first street"))),
            vatNumber = Some("first vat"),
            eoriNumber = None
          )),
          documentCertificate = Some(Seq(
            DocumentCertificateModel(
              documentType = Some("0"),
              documentReference = Some("0 reference"),
              documentDescription = Some("0 description"),
              referenceOfDocument = None
            )
          )),
          headerEadEsad = HeaderEadEsadModel(
            destinationType = DestinationType.DirectDelivery,
            journeyTime = "2 hours",
            transportArrangement = TransportArranger.GoodsOwner
          ),
          transportMode = TransportModeModel(
            transportModeCode = HowMovementTransported.AirTransport.toString,
            complementaryInformation = Some("info")
          ),
          movementGuarantee = MovementGuaranteeModel(
            guarantorTypeCode = GuarantorArranger.GoodsOwner,
            guarantorTrader = Some(Seq(TraderModel(
              traderExciseNumber = None,
              traderName = Some("guarantor name"),
              address = Some(AddressModel.fromUserAddress(testUserAddress.copy(street = "guarantor street"))),
              vatNumber = Some("guarantor vat"),
              eoriNumber = None
            )))
          ),
          bodyEadEsad = Seq(BodyEadEsadModel(
            bodyRecordUniqueReference = 0,
            exciseProductCode = testEpcWine,
            cnCode = testCnCodeWine,
            quantity = BigDecimal(1),
            grossMass = BigDecimal(3),
            netMass = BigDecimal(2),
            alcoholicStrengthByVolumeInPercentage = Some(BigDecimal(1.23)),
            degreePlato = Some(4.56),
            fiscalMark = Some("fiscal marks"),
            fiscalMarkUsedFlag = Some(true),
            designationOfOrigin = Some("talkin' 'bout my deeeeeesignation"),
            sizeOfProducer = Some(BigInt(4)),
            density = Some(BigDecimal(7.89)),
            commercialDescription = Some("beans"),
            brandNameOfProducts = Some("name"),
            maturationPeriodOrAgeOfProducts = Some("really old"),
            packages = Seq(
              PackageModel(
                kindOfPackages = bulkPackagingTypes.head.packagingType.toString,
                numberOfPackages = None,
                shippingMarks = None,
                commercialSealIdentification = Some("seal type"),
                sealInformation = Some("seal info")
              )
            ),
            wineProduct = Some(
              WineProductModel(
                wineProductCategory = ItemWineCategory.ImportedWine.toString,
                wineGrowingZoneCode = Some(ItemWineGrowingZone.CII.toString),
                thirdCountryOfOrigin = Some(countryModelGB.code),
                otherInformation = Some("more wine info"),
                wineOperations = Some(Seq("op code"))
              )
            )
          )),
          eadEsadDraft = EadEsadDraftModel(
            localReferenceNumber = testLrn,
            invoiceNumber = "inv ref",
            invoiceDate = Some("2020-12-25"),
            originTypeCode = OriginType.Imports,
            dateOfDispatch = "2020-10-31",
            timeOfDispatch = Some("23:59:59.123"),
            importSad = Some(Seq(ImportSadModel("sad 1"), ImportSadModel("sad 2"), ImportSadModel("sad 3")))
          ),
          transportDetails = Seq(
            TransportDetailsModel(
              transportUnitCode = TransportUnitType.FixedTransport.toString,
              identityOfTransportUnits = Some("identity"),
              commercialSealIdentification = Some("seal type"),
              complementaryInformation = Some("more info"),
              sealInformation = Some("seal info")
            )
          )
        )
      }
      "when XIWK" in {
        implicit val dr: DataRequest[_] = dataRequest(
          request = fakeRequest,
          answers = baseUserAnswers.set(DispatchPlacePage, DispatchPlace.NorthernIreland),
          ern = "XIWK123"
        )

        SubmitCreateMovementModel.apply mustBe SubmitCreateMovementModel(
          movementType = MovementType.UkToEu,
          attributes = AttributesModel(SubmissionMessageType.Standard, None),
          consigneeTrader = Some(TraderModel(
            traderExciseNumber = Some("consignee ern"),
            traderName = Some("consignee name"),
            address = Some(AddressModel.fromUserAddress(testUserAddress.copy(street = "consignee street"))),
            vatNumber = None,
            eoriNumber = Some("consignee eori")
          )),
          consignorTrader = TraderModel(
            traderExciseNumber = Some("XIWK123"),
            traderName = Some(testMinTraderKnownFacts.traderName),
            address = Some(AddressModel.fromUserAddress(testUserAddress.copy(street = "consignor street"))),
            vatNumber = None,
            eoriNumber = None
          ),
          placeOfDispatchTrader = Some(TraderModel(
            traderExciseNumber = Some("dispatch ern"),
            traderName = Some("dispatch name"),
            address = Some(AddressModel.fromUserAddress(testUserAddress.copy(street = "dispatch street"))),
            vatNumber = None,
            eoriNumber = None
          )),
          dispatchImportOffice = Some(OfficeModel("dispatch import office")),
          complementConsigneeTrader = Some(ComplementConsigneeTraderModel("state", Some("number"))),
          deliveryPlaceTrader = Some(TraderModel(
            traderExciseNumber = None,
            traderName = Some("destination name"),
            address = Some(AddressModel.fromUserAddress(testUserAddress.copy(street = "destination street"))),
            vatNumber = None,
            eoriNumber = None
          )),
          deliveryPlaceCustomsOffice = Some(OfficeModel("delivery place customs office")),
          competentAuthorityDispatchOffice = OfficeModel(s"XI$suffix"),
          transportArrangerTrader = Some(TraderModel(
            traderExciseNumber = None,
            traderName = Some("arranger name"),
            address = Some(AddressModel.fromUserAddress(testUserAddress.copy(street = "arranger street"))),
            vatNumber = Some("arranger vat"),
            eoriNumber = None
          )),
          firstTransporterTrader = Some(TraderModel(
            traderExciseNumber = None,
            traderName = Some("first name"),
            address = Some(AddressModel.fromUserAddress(testUserAddress.copy(street = "first street"))),
            vatNumber = Some("first vat"),
            eoriNumber = None
          )),
          documentCertificate = Some(Seq(
            DocumentCertificateModel(
              documentType = Some("0"),
              documentReference = Some("0 reference"),
              documentDescription = Some("0 description"),
              referenceOfDocument = None
            )
          )),
          headerEadEsad = HeaderEadEsadModel(
            destinationType = DestinationType.DirectDelivery,
            journeyTime = "2 hours",
            transportArrangement = TransportArranger.GoodsOwner
          ),
          transportMode = TransportModeModel(
            transportModeCode = HowMovementTransported.AirTransport.toString,
            complementaryInformation = Some("info")
          ),
          movementGuarantee = MovementGuaranteeModel(
            guarantorTypeCode = GuarantorArranger.GoodsOwner,
            guarantorTrader = Some(Seq(TraderModel(
              traderExciseNumber = None,
              traderName = Some("guarantor name"),
              address = Some(AddressModel.fromUserAddress(testUserAddress.copy(street = "guarantor street"))),
              vatNumber = Some("guarantor vat"),
              eoriNumber = None
            )))
          ),
          bodyEadEsad = Seq(BodyEadEsadModel(
            bodyRecordUniqueReference = 0,
            exciseProductCode = testEpcWine,
            cnCode = testCnCodeWine,
            quantity = BigDecimal(1),
            grossMass = BigDecimal(3),
            netMass = BigDecimal(2),
            alcoholicStrengthByVolumeInPercentage = Some(BigDecimal(1.23)),
            degreePlato = Some(4.56),
            fiscalMark = Some("fiscal marks"),
            fiscalMarkUsedFlag = Some(true),
            designationOfOrigin = Some("talkin' 'bout my deeeeeesignation"),
            sizeOfProducer = Some(BigInt(4)),
            density = Some(BigDecimal(7.89)),
            commercialDescription = Some("beans"),
            brandNameOfProducts = Some("name"),
            maturationPeriodOrAgeOfProducts = Some("really old"),
            packages = Seq(
              PackageModel(
                kindOfPackages = bulkPackagingTypes.head.packagingType.toString,
                numberOfPackages = None,
                shippingMarks = None,
                commercialSealIdentification = Some("seal type"),
                sealInformation = Some("seal info")
              )
            ),
            wineProduct = Some(
              WineProductModel(
                wineProductCategory = ItemWineCategory.ImportedWine.toString,
                wineGrowingZoneCode = Some(ItemWineGrowingZone.CII.toString),
                thirdCountryOfOrigin = Some(countryModelGB.code),
                otherInformation = Some("more wine info"),
                wineOperations = Some(Seq("op code"))
              )
            )
          )),
          eadEsadDraft = EadEsadDraftModel(
            localReferenceNumber = testLrn,
            invoiceNumber = "inv ref",
            invoiceDate = Some("2020-12-25"),
            originTypeCode = OriginType.TaxWarehouse,
            dateOfDispatch = "2020-10-31",
            timeOfDispatch = Some("23:59:59.123"),
            importSad = None
          ),
          transportDetails = Seq(
            TransportDetailsModel(
              transportUnitCode = TransportUnitType.FixedTransport.toString,
              identityOfTransportUnits = Some("identity"),
              commercialSealIdentification = Some("seal type"),
              complementaryInformation = Some("more info"),
              sealInformation = Some("seal info")
            )
          )
        )
      }
      "when GBRC" in {
        implicit val dr: DataRequest[_] = dataRequest(
          request = fakeRequest,
          answers = baseUserAnswers,
          ern = "GBRC123"
        )

        SubmitCreateMovementModel.apply mustBe SubmitCreateMovementModel(
          movementType = MovementType.ImportEu,
          attributes = AttributesModel(SubmissionMessageType.Standard, None),
          consigneeTrader = Some(TraderModel(
            traderExciseNumber = Some("consignee ern"),
            traderName = Some("consignee name"),
            address = Some(AddressModel.fromUserAddress(testUserAddress.copy(street = "consignee street"))),
            vatNumber = None,
            eoriNumber = Some("consignee eori")
          )),
          consignorTrader = TraderModel(
            traderExciseNumber = Some("GBRC123"),
            traderName = Some(testMinTraderKnownFacts.traderName),
            address = Some(AddressModel.fromUserAddress(testUserAddress.copy(street = "consignor street"))),
            vatNumber = None,
            eoriNumber = None
          ),
          placeOfDispatchTrader = None,
          dispatchImportOffice = Some(OfficeModel("dispatch import office")),
          complementConsigneeTrader = Some(ComplementConsigneeTraderModel("state", Some("number"))),
          deliveryPlaceTrader = Some(TraderModel(
            traderExciseNumber = None,
            traderName = Some("destination name"),
            address = Some(AddressModel.fromUserAddress(testUserAddress.copy(street = "destination street"))),
            vatNumber = None,
            eoriNumber = None
          )),
          deliveryPlaceCustomsOffice = Some(OfficeModel("delivery place customs office")),
          competentAuthorityDispatchOffice = OfficeModel(s"GB$suffix"),
          transportArrangerTrader = Some(TraderModel(
            traderExciseNumber = None,
            traderName = Some("arranger name"),
            address = Some(AddressModel.fromUserAddress(testUserAddress.copy(street = "arranger street"))),
            vatNumber = Some("arranger vat"),
            eoriNumber = None
          )),
          firstTransporterTrader = Some(TraderModel(
            traderExciseNumber = None,
            traderName = Some("first name"),
            address = Some(AddressModel.fromUserAddress(testUserAddress.copy(street = "first street"))),
            vatNumber = Some("first vat"),
            eoriNumber = None
          )),
          documentCertificate = Some(Seq(
            DocumentCertificateModel(
              documentType = Some("0"),
              documentReference = Some("0 reference"),
              documentDescription = Some("0 description"),
              referenceOfDocument = None
            )
          )),
          headerEadEsad = HeaderEadEsadModel(
            destinationType = DestinationType.DirectDelivery,
            journeyTime = "2 hours",
            transportArrangement = TransportArranger.GoodsOwner
          ),
          transportMode = TransportModeModel(
            transportModeCode = HowMovementTransported.AirTransport.toString,
            complementaryInformation = Some("info")
          ),
          movementGuarantee = MovementGuaranteeModel(
            guarantorTypeCode = GuarantorArranger.GoodsOwner,
            guarantorTrader = Some(Seq(TraderModel(
              traderExciseNumber = None,
              traderName = Some("guarantor name"),
              address = Some(AddressModel.fromUserAddress(testUserAddress.copy(street = "guarantor street"))),
              vatNumber = Some("guarantor vat"),
              eoriNumber = None
            )))
          ),
          bodyEadEsad = Seq(BodyEadEsadModel(
            bodyRecordUniqueReference = 0,
            exciseProductCode = testEpcWine,
            cnCode = testCnCodeWine,
            quantity = BigDecimal(1),
            grossMass = BigDecimal(3),
            netMass = BigDecimal(2),
            alcoholicStrengthByVolumeInPercentage = Some(BigDecimal(1.23)),
            degreePlato = Some(4.56),
            fiscalMark = Some("fiscal marks"),
            fiscalMarkUsedFlag = Some(true),
            designationOfOrigin = Some("talkin' 'bout my deeeeeesignation"),
            sizeOfProducer = Some(BigInt(4)),
            density = Some(BigDecimal(7.89)),
            commercialDescription = Some("beans"),
            brandNameOfProducts = Some("name"),
            maturationPeriodOrAgeOfProducts = Some("really old"),
            packages = Seq(
              PackageModel(
                kindOfPackages = bulkPackagingTypes.head.packagingType.toString,
                numberOfPackages = None,
                shippingMarks = None,
                commercialSealIdentification = Some("seal type"),
                sealInformation = Some("seal info")
              )
            ),
            wineProduct = Some(
              WineProductModel(
                wineProductCategory = ItemWineCategory.ImportedWine.toString,
                wineGrowingZoneCode = Some(ItemWineGrowingZone.CII.toString),
                thirdCountryOfOrigin = Some(countryModelGB.code),
                otherInformation = Some("more wine info"),
                wineOperations = Some(Seq("op code"))
              )
            )
          )),
          eadEsadDraft = EadEsadDraftModel(
            localReferenceNumber = testLrn,
            invoiceNumber = "inv ref",
            invoiceDate = Some("2020-12-25"),
            originTypeCode = OriginType.Imports,
            dateOfDispatch = "2020-10-31",
            timeOfDispatch = Some("23:59:59.123"),
            importSad = Some(Seq(ImportSadModel("sad 1"), ImportSadModel("sad 2"), ImportSadModel("sad 3")))
          ),
          transportDetails = Seq(
            TransportDetailsModel(
              transportUnitCode = TransportUnitType.FixedTransport.toString,
              identityOfTransportUnits = Some("identity"),
              commercialSealIdentification = Some("seal type"),
              complementaryInformation = Some("more info"),
              sealInformation = Some("seal info")
            )
          )
        )
      }
      "when GBWK" in {
        implicit val dr: DataRequest[_] = dataRequest(
          request = fakeRequest,
          answers = baseUserAnswers,
          ern = "GBWK123"
        )

        SubmitCreateMovementModel.apply mustBe SubmitCreateMovementModel(
          movementType = MovementType.UkToEu,
          attributes = AttributesModel(SubmissionMessageType.Standard, None),
          consigneeTrader = Some(TraderModel(
            traderExciseNumber = Some("consignee ern"),
            traderName = Some("consignee name"),
            address = Some(AddressModel.fromUserAddress(testUserAddress.copy(street = "consignee street"))),
            vatNumber = None,
            eoriNumber = Some("consignee eori")
          )),
          consignorTrader = TraderModel(
            traderExciseNumber = Some("GBWK123"),
            traderName = Some(testMinTraderKnownFacts.traderName),
            address = Some(AddressModel.fromUserAddress(testUserAddress.copy(street = "consignor street"))),
            vatNumber = None,
            eoriNumber = None
          ),
          placeOfDispatchTrader = Some(TraderModel(
            traderExciseNumber = Some("dispatch ern"),
            traderName = Some("dispatch name"),
            address = Some(AddressModel.fromUserAddress(testUserAddress.copy(street = "dispatch street"))),
            vatNumber = None,
            eoriNumber = None
          )),
          dispatchImportOffice = Some(OfficeModel("dispatch import office")),
          complementConsigneeTrader = Some(ComplementConsigneeTraderModel("state", Some("number"))),
          deliveryPlaceTrader = Some(TraderModel(
            traderExciseNumber = None,
            traderName = Some("destination name"),
            address = Some(AddressModel.fromUserAddress(testUserAddress.copy(street = "destination street"))),
            vatNumber = None,
            eoriNumber = None
          )),
          deliveryPlaceCustomsOffice = Some(OfficeModel("delivery place customs office")),
          competentAuthorityDispatchOffice = OfficeModel(s"GB$suffix"),
          transportArrangerTrader = Some(TraderModel(
            traderExciseNumber = None,
            traderName = Some("arranger name"),
            address = Some(AddressModel.fromUserAddress(testUserAddress.copy(street = "arranger street"))),
            vatNumber = Some("arranger vat"),
            eoriNumber = None
          )),
          firstTransporterTrader = Some(TraderModel(
            traderExciseNumber = None,
            traderName = Some("first name"),
            address = Some(AddressModel.fromUserAddress(testUserAddress.copy(street = "first street"))),
            vatNumber = Some("first vat"),
            eoriNumber = None
          )),
          documentCertificate = Some(Seq(
            DocumentCertificateModel(
              documentType = Some("0"),
              documentReference = Some("0 reference"),
              documentDescription = Some("0 description"),
              referenceOfDocument = None
            )
          )),
          headerEadEsad = HeaderEadEsadModel(
            destinationType = DestinationType.DirectDelivery,
            journeyTime = "2 hours",
            transportArrangement = TransportArranger.GoodsOwner
          ),
          transportMode = TransportModeModel(
            transportModeCode = HowMovementTransported.AirTransport.toString,
            complementaryInformation = Some("info")
          ),
          movementGuarantee = MovementGuaranteeModel(
            guarantorTypeCode = GuarantorArranger.GoodsOwner,
            guarantorTrader = Some(Seq(TraderModel(
              traderExciseNumber = None,
              traderName = Some("guarantor name"),
              address = Some(AddressModel.fromUserAddress(testUserAddress.copy(street = "guarantor street"))),
              vatNumber = Some("guarantor vat"),
              eoriNumber = None
            )))
          ),
          bodyEadEsad = Seq(BodyEadEsadModel(
            bodyRecordUniqueReference = 0,
            exciseProductCode = testEpcWine,
            cnCode = testCnCodeWine,
            quantity = BigDecimal(1),
            grossMass = BigDecimal(3),
            netMass = BigDecimal(2),
            alcoholicStrengthByVolumeInPercentage = Some(BigDecimal(1.23)),
            degreePlato = Some(4.56),
            fiscalMark = Some("fiscal marks"),
            fiscalMarkUsedFlag = Some(true),
            designationOfOrigin = Some("talkin' 'bout my deeeeeesignation"),
            sizeOfProducer = Some(BigInt(4)),
            density = Some(BigDecimal(7.89)),
            commercialDescription = Some("beans"),
            brandNameOfProducts = Some("name"),
            maturationPeriodOrAgeOfProducts = Some("really old"),
            packages = Seq(
              PackageModel(
                kindOfPackages = bulkPackagingTypes.head.packagingType.toString,
                numberOfPackages = None,
                shippingMarks = None,
                commercialSealIdentification = Some("seal type"),
                sealInformation = Some("seal info")
              )
            ),
            wineProduct = Some(
              WineProductModel(
                wineProductCategory = ItemWineCategory.ImportedWine.toString,
                wineGrowingZoneCode = Some(ItemWineGrowingZone.CII.toString),
                thirdCountryOfOrigin = Some(countryModelGB.code),
                otherInformation = Some("more wine info"),
                wineOperations = Some(Seq("op code"))
              )
            )
          )),
          eadEsadDraft = EadEsadDraftModel(
            localReferenceNumber = testLrn,
            invoiceNumber = "inv ref",
            invoiceDate = Some("2020-12-25"),
            originTypeCode = OriginType.TaxWarehouse,
            dateOfDispatch = "2020-10-31",
            timeOfDispatch = Some("23:59:59.123"),
            importSad = None
          ),
          transportDetails = Seq(
            TransportDetailsModel(
              transportUnitCode = TransportUnitType.FixedTransport.toString,
              identityOfTransportUnits = Some("identity"),
              commercialSealIdentification = Some("seal type"),
              complementaryInformation = Some("more info"),
              sealInformation = Some("seal info")
            )
          )
        )
      }
    }
  }
}
