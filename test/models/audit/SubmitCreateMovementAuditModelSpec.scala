/*
 * Copyright 2024 HM Revenue & Customs
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

package models.audit

import base.SpecBase
import config.AppConfig
import fixtures.ItemFixtures
import fixtures.messages.sections.items.ItemSmallIndependentProducerMessages
import models.requests.DataRequest
import play.api.i18n.Messages
import play.api.libs.json.Json
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest

import java.time.LocalDate

class SubmitCreateMovementAuditModelSpec extends SpecBase with ItemFixtures {
  implicit val ac: AppConfig = appConfig

  val messagesForLanguage = ItemSmallIndependentProducerMessages.English
  implicit val msgs: Messages = messages(Seq(messagesForLanguage.lang))

  val fakeRequest: FakeRequest[AnyContentAsEmpty.type] = FakeRequest()

  "Audit details" - {
    "must output as expected (with templateId and name)" in {

      val date = LocalDate.now()

      implicit val dr: DataRequest[_] = dataRequest(
        request = fakeRequest,
        answers = baseFullUserAnswers,
        ern = "XIRC123"
      )

      val auditJson = SubmitCreateMovementAudit(
        ern = dr.ern,
        submissionRequest = xircSubmitCreateMovementModel,
        submissionResponse = Right(submitCreateMovementResponseEIS),
        receiptDate = date.toString,
        templateName = Some(templateName),
        templateId = Some(templateId)
      ).detail

      auditJson mustBe
        Json.parse(
          s"""{
             |  "exciseRegistrationNumber": "${dr.ern}",
             |  "templateName": "$templateName",
             |  "templateId": "$templateId",
             |  "movementType": {
             |    "code": "4",
             |    "description": "ImportEu"
             |  },
             |  "attributes": {
             |    "submissionMessageType": {
             |      "code": "1",
             |      "description": "Standard"
             |    },
             |    "deferredSubmissionFlag": false
             |  },
             |  "consigneeTrader": {
             |    "traderExciseNumber": "consignee ern",
             |    "traderName": "testName",
             |    "address": {
             |      "streetNumber": "10",
             |      "street": "consignee street",
             |      "postcode": "ZZ1 1ZZ",
             |      "city": "Testown"
             |    }
             |  },
             |  "consignorTrader": {
             |    "traderExciseNumber": "XIRC123",
             |    "traderName": "testTraderName",
             |    "address": {
             |      "streetNumber": "10",
             |      "street": "consignor street",
             |      "postcode": "ZZ1 1ZZ",
             |      "city": "Testown"
             |    }
             |  },
             |  "dispatchImportOffice": {
             |    "referenceNumber": "dispatch import office"
             |  },
             |  "complementConsigneeTrader": {
             |    "memberStateCode": "state",
             |    "serialNumberOfCertificateOfExemption": "number"
             |  },
             |  "deliveryPlaceTrader": {
             |    "traderName": "testName",
             |    "address": {
             |      "streetNumber": "10",
             |      "street": "destination street",
             |      "postcode": "ZZ1 1ZZ",
             |      "city": "Testown"
             |    }
             |  },
             |  "deliveryPlaceCustomsOffice": {
             |    "referenceNumber": "delivery place customs office"
             |  },
             |  "competentAuthorityDispatchOffice": {
             |    "referenceNumber": "XI004098"
             |  },
             |  "transportArrangerTrader": {
             |    "traderName": "testName",
             |    "address": {
             |      "streetNumber": "10",
             |      "street": "arranger street",
             |      "postcode": "ZZ1 1ZZ",
             |      "city": "Testown"
             |    },
             |    "vatNumber": "arranger vat"
             |  },
             |  "firstTransporterTrader": {
             |    "traderName": "testName",
             |    "address": {
             |      "streetNumber": "10",
             |      "street": "first street",
             |      "postcode": "ZZ1 1ZZ",
             |      "city": "Testown"
             |    },
             |    "vatNumber": "first vat"
             |  },
             |  "documentCertificate": [
             |    {
             |      "documentType": {
             |        "code": "0",
             |        "description": "0 type desc"
             |      },
             |      "documentReference": "0 reference"
             |    }
             |  ],
             |  "headerEadEsad": {
             |    "destinationType": {
             |      "code": "4",
             |      "description": "DirectDelivery"
             |    },
             |    "journeyTime": "2 hours",
             |    "transportArrangement": {
             |      "code": "3",
             |      "description": "GoodsOwner"
             |    }
             |  },
             |  "transportMode": {
             |    "transportModeCode": {
             |      "code": "4",
             |      "description": "AirTransport"
             |    },
             |    "complementaryInformation": "info"
             |  },
             |  "movementGuarantee": {
             |    "guarantorTypeCode": {
             |      "code": "3",
             |      "description": "GoodsOwner"
             |    },
             |    "guarantorTrader": [
             |      {
             |        "traderName": "testName",
             |        "address": {
             |          "streetNumber": "10",
             |          "street": "guarantor street",
             |          "postcode": "ZZ1 1ZZ",
             |          "city": "Testown"
             |        },
             |        "vatNumber": "guarantor vat"
             |      }
             |    ]
             |  },
             |  "bodyEadEsad": [
             |    {
             |      "bodyRecordUniqueReference": 1,
             |      "exciseProductCode": "W200",
             |      "cnCode": "22060010",
             |      "quantity": 1,
             |      "grossMass": 3,
             |      "netMass": 2,
             |      "alcoholicStrengthByVolumeInPercentage": 1.23,
             |      "degreePlato": 4.56,
             |      "fiscalMark": "fiscal marks",
             |      "fiscalMarkUsedFlag": true,
             |      "designationOfOrigin": "The product has a Protected Designation of Origin (PDO). talkin' 'bout my deeeeeesignation",
             |      "sizeOfProducer": 4,
             |      "density": 7.89,
             |      "commercialDescription": "beans",
             |      "brandNameOfProducts": "name",
             |      "maturationPeriodOrAgeOfProducts": "really old",
             |      "independentSmallProducersDeclaration": "It is hereby certified that the alcoholic product described has been produced by an independent wine producer. The producer is a self-certified independent small producer and not the consignor. Identification: XIRC123456789",
             |      "packages": [
             |        {
             |          "kindOfPackages": "VG",
             |          "commercialSealIdentification": "seal type",
             |          "sealInformation": "seal info"
             |        }
             |      ],
             |      "wineProduct": {
             |        "wineProductCategory": {
             |          "code": "4",
             |          "description": "ImportedWine"
             |        },
             |        "wineGrowingZoneCode": {
             |          "code": "4",
             |          "description": "ZoneCII"
             |        },
             |        "thirdCountryOfOrigin": "GB",
             |        "otherInformation": "more wine info",
             |        "wineOperations": [
             |          {
             |            "code": "op code",
             |            "description": "choice desc"
             |          }
             |        ]
             |      }
             |    }
             |  ],
             |  "eadEsadDraft": {
             |    "localReferenceNumber": "1234567890",
             |    "invoiceNumber": "inv ref",
             |    "invoiceDate": "2020-12-25",
             |    "originTypeCode": {
             |      "code": "2",
             |      "description": "Imports"
             |    },
             |    "dateOfDispatch": "2020-10-31",
             |    "timeOfDispatch": "23:59:59",
             |    "importSad": [
             |      {
             |        "importSadNumber": "sad 1"
             |      },
             |      {
             |        "importSadNumber": "sad 2"
             |      },
             |      {
             |        "importSadNumber": "sad 3"
             |      }
             |    ]
             |  },
             |  "transportDetails": [
             |    {
             |      "transportUnitCode": {
             |        "code": "5",
             |        "description": "FixedTransport"
             |      },
             |      "identityOfTransportUnits": "identity",
             |      "commercialSealIdentification": "seal type",
             |      "complementaryInformation": "more info",
             |      "sealInformation": "seal info"
             |    }
             |  ],
             |  "status": "success",
             |  "receipt": "UYVQBLMXCYK6HAEBZI7TSWAQ6XDTXFYU",
             |  "receiptDate": "${date.toString}",
             |  "responseCode": 200
             |}""".stripMargin)
    }

    "must output as expected (without templateId and name)" in {

      val date = LocalDate.now()

      implicit val dr: DataRequest[_] = dataRequest(
        request = fakeRequest,
        answers = baseFullUserAnswers,
        ern = "XIRC123"
      )

      val auditJson = SubmitCreateMovementAudit(
        ern = dr.ern,
        submissionRequest = xircSubmitCreateMovementModel,
        submissionResponse = Right(submitCreateMovementResponseEIS),
        receiptDate = date.toString,
        templateName = None,
        templateId = None
      ).detail

      auditJson mustBe
        Json.parse(
          s"""{
             |  "exciseRegistrationNumber": "${dr.ern}",
             |  "movementType": {
             |    "code": "4",
             |    "description": "ImportEu"
             |  },
             |  "attributes": {
             |    "submissionMessageType": {
             |      "code": "1",
             |      "description": "Standard"
             |    },
             |    "deferredSubmissionFlag": false
             |  },
             |  "consigneeTrader": {
             |    "traderExciseNumber": "consignee ern",
             |    "traderName": "testName",
             |    "address": {
             |      "streetNumber": "10",
             |      "street": "consignee street",
             |      "postcode": "ZZ1 1ZZ",
             |      "city": "Testown"
             |    }
             |  },
             |  "consignorTrader": {
             |    "traderExciseNumber": "XIRC123",
             |    "traderName": "testTraderName",
             |    "address": {
             |      "streetNumber": "10",
             |      "street": "consignor street",
             |      "postcode": "ZZ1 1ZZ",
             |      "city": "Testown"
             |    }
             |  },
             |  "dispatchImportOffice": {
             |    "referenceNumber": "dispatch import office"
             |  },
             |  "complementConsigneeTrader": {
             |    "memberStateCode": "state",
             |    "serialNumberOfCertificateOfExemption": "number"
             |  },
             |  "deliveryPlaceTrader": {
             |    "traderName": "testName",
             |    "address": {
             |      "streetNumber": "10",
             |      "street": "destination street",
             |      "postcode": "ZZ1 1ZZ",
             |      "city": "Testown"
             |    }
             |  },
             |  "deliveryPlaceCustomsOffice": {
             |    "referenceNumber": "delivery place customs office"
             |  },
             |  "competentAuthorityDispatchOffice": {
             |    "referenceNumber": "XI004098"
             |  },
             |  "transportArrangerTrader": {
             |    "traderName": "testName",
             |    "address": {
             |      "streetNumber": "10",
             |      "street": "arranger street",
             |      "postcode": "ZZ1 1ZZ",
             |      "city": "Testown"
             |    },
             |    "vatNumber": "arranger vat"
             |  },
             |  "firstTransporterTrader": {
             |    "traderName": "testName",
             |    "address": {
             |      "streetNumber": "10",
             |      "street": "first street",
             |      "postcode": "ZZ1 1ZZ",
             |      "city": "Testown"
             |    },
             |    "vatNumber": "first vat"
             |  },
             |  "documentCertificate": [
             |    {
             |      "documentType": {
             |        "code": "0",
             |        "description": "0 type desc"
             |      },
             |      "documentReference": "0 reference"
             |    }
             |  ],
             |  "headerEadEsad": {
             |    "destinationType": {
             |      "code": "4",
             |      "description": "DirectDelivery"
             |    },
             |    "journeyTime": "2 hours",
             |    "transportArrangement": {
             |      "code": "3",
             |      "description": "GoodsOwner"
             |    }
             |  },
             |  "transportMode": {
             |    "transportModeCode": {
             |      "code": "4",
             |      "description": "AirTransport"
             |    },
             |    "complementaryInformation": "info"
             |  },
             |  "movementGuarantee": {
             |    "guarantorTypeCode": {
             |      "code": "3",
             |      "description": "GoodsOwner"
             |    },
             |    "guarantorTrader": [
             |      {
             |        "traderName": "testName",
             |        "address": {
             |          "streetNumber": "10",
             |          "street": "guarantor street",
             |          "postcode": "ZZ1 1ZZ",
             |          "city": "Testown"
             |        },
             |        "vatNumber": "guarantor vat"
             |      }
             |    ]
             |  },
             |  "bodyEadEsad": [
             |    {
             |      "bodyRecordUniqueReference": 1,
             |      "exciseProductCode": "W200",
             |      "cnCode": "22060010",
             |      "quantity": 1,
             |      "grossMass": 3,
             |      "netMass": 2,
             |      "alcoholicStrengthByVolumeInPercentage": 1.23,
             |      "degreePlato": 4.56,
             |      "fiscalMark": "fiscal marks",
             |      "fiscalMarkUsedFlag": true,
             |      "designationOfOrigin": "The product has a Protected Designation of Origin (PDO). talkin' 'bout my deeeeeesignation",
             |      "sizeOfProducer": 4,
             |      "density": 7.89,
             |      "commercialDescription": "beans",
             |      "brandNameOfProducts": "name",
             |      "maturationPeriodOrAgeOfProducts": "really old",
             |      "independentSmallProducersDeclaration": "It is hereby certified that the alcoholic product described has been produced by an independent wine producer. The producer is a self-certified independent small producer and not the consignor. Identification: XIRC123456789",
             |      "packages": [
             |        {
             |          "kindOfPackages": "VG",
             |          "commercialSealIdentification": "seal type",
             |          "sealInformation": "seal info"
             |        }
             |      ],
             |      "wineProduct": {
             |        "wineProductCategory": {
             |          "code": "4",
             |          "description": "ImportedWine"
             |        },
             |        "wineGrowingZoneCode": {
             |          "code": "4",
             |          "description": "ZoneCII"
             |        },
             |        "thirdCountryOfOrigin": "GB",
             |        "otherInformation": "more wine info",
             |        "wineOperations": [
             |          {
             |            "code": "op code",
             |            "description": "choice desc"
             |          }
             |        ]
             |      }
             |    }
             |  ],
             |  "eadEsadDraft": {
             |    "localReferenceNumber": "1234567890",
             |    "invoiceNumber": "inv ref",
             |    "invoiceDate": "2020-12-25",
             |    "originTypeCode": {
             |      "code": "2",
             |      "description": "Imports"
             |    },
             |    "dateOfDispatch": "2020-10-31",
             |    "timeOfDispatch": "23:59:59",
             |    "importSad": [
             |      {
             |        "importSadNumber": "sad 1"
             |      },
             |      {
             |        "importSadNumber": "sad 2"
             |      },
             |      {
             |        "importSadNumber": "sad 3"
             |      }
             |    ]
             |  },
             |  "transportDetails": [
             |    {
             |      "transportUnitCode": {
             |        "code": "5",
             |        "description": "FixedTransport"
             |      },
             |      "identityOfTransportUnits": "identity",
             |      "commercialSealIdentification": "seal type",
             |      "complementaryInformation": "more info",
             |      "sealInformation": "seal info"
             |    }
             |  ],
             |  "status": "success",
             |  "receipt": "UYVQBLMXCYK6HAEBZI7TSWAQ6XDTXFYU",
             |  "receiptDate": "${date.toString}",
             |  "responseCode": 200
             |}""".stripMargin)
    }
  }
}
