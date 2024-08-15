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

package pages.sections.consignee

import base.SpecBase
import fixtures.MovementSubmissionFailureFixtures
import models.ExemptOrganisationDetailsModel
import models.requests.DataRequest
import models.sections.consignee.ConsigneeExportInformation.{EoriNumber, NoInformation, VatNumber}
import play.api.test.FakeRequest
import viewmodels.taskList.UpdateNeeded

class ConsigneeSectionSpec extends SpecBase with MovementSubmissionFailureFixtures {
  "isCompleted" - {
    "must return true" - {
      "when user starts on ConsigneeExportInformation and selects only VAT number (and provided it)" in {
        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(),
          emptyUserAnswers
            .set(ConsigneeExportInformationPage, Set(VatNumber))
            .set(ConsigneeExportVatPage, testEori)
            .set(ConsigneeAddressPage, testUserAddress)
        )
        ConsigneeSection.isCompleted mustBe true
      }
      "when user starts on ConsigneeExportInformation and selects only EORI number (and provided it)" in {
        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(),
          emptyUserAnswers
            .set(ConsigneeExportInformationPage, Set(EoriNumber))
            .set(ConsigneeExportEoriPage, testEori)
            .set(ConsigneeAddressPage, testUserAddress)
        )
        ConsigneeSection.isCompleted mustBe true
      }
      "when user starts on ConsigneeExportInformation and selects both VAT / EORI number (and provided then)" in {
        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(),
          emptyUserAnswers
            .set(ConsigneeExportInformationPage, Set(VatNumber, EoriNumber))
            .set(ConsigneeExportVatPage, testVat)
            .set(ConsigneeExportEoriPage, testEori)
            .set(ConsigneeAddressPage, testUserAddress)
        )
        ConsigneeSection.isCompleted mustBe true
      }
      "when user starts on ConsigneeExportInformation and selects Not provided" in {
        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(),
          emptyUserAnswers
            .set(ConsigneeExportInformationPage, Set(NoInformation))
            .set(ConsigneeAddressPage, testUserAddress)
        )
        ConsigneeSection.isCompleted mustBe true
      }
      "when user starts on ConsigneeExcise" in {
        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(),
          emptyUserAnswers
            .set(ConsigneeExcisePage, "")
            .set(ConsigneeAddressPage, testUserAddress)
        )
        ConsigneeSection.isCompleted mustBe true
      }
      "when user starts on ConsigneeExemptOrganisation" in {
        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(),
          emptyUserAnswers
            .set(ConsigneeExemptOrganisationPage, ExemptOrganisationDetailsModel("", ""))
            .set(ConsigneeAddressPage, testUserAddress)
        )
        ConsigneeSection.isCompleted mustBe true
      }
    }

    "must return false" - {
      "when user answers doesn't contain ConsigneeExportInformation, ConsigneeExcise or ConsigneeExemptOrganisation" in {
        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), emptyUserAnswers)
        ConsigneeSection.isCompleted mustBe false
      }
      "when user starts on ConsigneeExportInformation and selects only VAT number (and has NOT provided it)" in {
        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(),
          emptyUserAnswers
            .set(ConsigneeExportInformationPage, Set(VatNumber))
            .set(ConsigneeAddressPage, testUserAddress)
        )
        ConsigneeSection.isCompleted mustBe false
      }
      "when user starts on ConsigneeExportInformation and selects only EORI number (and has NOT provided it)" in {
        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(),
          emptyUserAnswers
            .set(ConsigneeExportInformationPage, Set(EoriNumber))
            .set(ConsigneeAddressPage, testUserAddress)
        )
        ConsigneeSection.isCompleted mustBe false
      }
      "when user starts on ConsigneeExportInformation and selects both VAT / EORI number (and has NOT provided VAT)" in {
        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(),
          emptyUserAnswers
            .set(ConsigneeExportInformationPage, Set(VatNumber, EoriNumber))
            .set(ConsigneeExportEoriPage, testEori)
            .set(ConsigneeAddressPage, testUserAddress)
        )
        ConsigneeSection.isCompleted mustBe false
      }
      "when user starts on ConsigneeExportInformation and selects both VAT / EORI number (and has NOT provided EORI)" in {
        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(),
          emptyUserAnswers
            .set(ConsigneeExportInformationPage, Set(VatNumber, EoriNumber))
            .set(ConsigneeExportVatPage, testVat)
            .set(ConsigneeAddressPage, testUserAddress)
        )
        ConsigneeSection.isCompleted mustBe false
      }
      "when user starts on ConsigneeExportInformation and selects both VAT / EORI number (and has NOT provided either)" in {
        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(),
          emptyUserAnswers
            .set(ConsigneeExportInformationPage, Set(VatNumber, EoriNumber))
            .set(ConsigneeAddressPage, testUserAddress)
        )
        ConsigneeSection.isCompleted mustBe false
      }
      "when user starts on ConsigneeExcise, answers that page and doesn't finish the flow" in {
        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), emptyUserAnswers.set(ConsigneeExcisePage, ""))
        ConsigneeSection.isCompleted mustBe false
      }
      "when user starts on ConsigneeExemptOrganisation, answers that page and doesn't finish the flow" in {
        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), emptyUserAnswers.set(ConsigneeExemptOrganisationPage, ExemptOrganisationDetailsModel("", "")))
        ConsigneeSection.isCompleted mustBe false
      }
    }
  }

  "must return UpdateNeeded" - {
    "when there is a Consignee Submission Error" in {
      implicit val dr: DataRequest[_] = dataRequest(FakeRequest(),
        emptyUserAnswers
          .set(ConsigneeExcisePage, "")
          .set(ConsigneeAddressPage, testUserAddress)
          .copy(submissionFailures = Seq(consigneeExciseFailure))
      )
      ConsigneeSection.status mustBe UpdateNeeded
    }
  }
}
