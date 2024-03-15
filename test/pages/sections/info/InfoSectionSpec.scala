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

package pages.sections.info

import base.SpecBase
import fixtures.MovementSubmissionFailureFixtures
import models.NorthernIrelandWarehouseKeeper
import models.sections.info.DispatchPlace.GreatBritain
import models.sections.info.movementScenario.MovementScenario.ExportWithCustomsDeclarationLodgedInTheUk
import models.sections.info.{DispatchDetailsModel, InvoiceDetailsModel}
import play.api.test.FakeRequest
import utils.LocalReferenceNumberError
import viewmodels.taskList.{Completed, InProgress, NotStarted, UpdateNeeded}

import java.time.{LocalDate, LocalTime}

class InfoSectionSpec extends SpecBase with MovementSubmissionFailureFixtures {

  "status" - {

    "should return UpdateNeeded" - {

      "when the LRN is a duplicate (704 response)" in {
        InfoSection.status(dataRequest(FakeRequest(), answers = emptyUserAnswers.copy(
          submissionFailures = Seq(movementSubmissionFailure.copy(errorType = LocalReferenceNumberError.code, hasBeenFixed = false))
        ))) mustBe UpdateNeeded
      }
    }

    "should return Completed" - {

      "all the answers are completed" - {

        s"but the $DispatchPlacePage answer is populated for a $NorthernIrelandWarehouseKeeper" in {
          InfoSection.status(dataRequest(FakeRequest(),
            ern = testNorthernIrelandErn,
            answers = emptyUserAnswers.copy(ern = testNorthernIrelandErn)
              .set(DestinationTypePage, ExportWithCustomsDeclarationLodgedInTheUk)
              .set(DeferredMovementPage(), false)
              .set(LocalReferenceNumberPage(), "")
              .set(InvoiceDetailsPage(), InvoiceDetailsModel("inv ref", LocalDate.parse("2020-12-25")))
              .set(DispatchDetailsPage(), DispatchDetailsModel(LocalDate.parse("2020-10-31"), LocalTime.parse("23:59:59")))
              .set(DispatchPlacePage, GreatBritain)
          )) mustBe Completed
        }

        s"for non-XIWK users" in {
          InfoSection.status(dataRequest(FakeRequest(),
            answers = emptyUserAnswers
              .set(DestinationTypePage, ExportWithCustomsDeclarationLodgedInTheUk)
              .set(DeferredMovementPage(), false)
              .set(LocalReferenceNumberPage(), "")
              .set(InvoiceDetailsPage(), InvoiceDetailsModel("inv ref", LocalDate.parse("2020-12-25")))
              .set(DispatchDetailsPage(), DispatchDetailsModel(LocalDate.parse("2020-10-31"), LocalTime.parse("23:59:59")))
          )) mustBe Completed
        }
      }
    }

    "should return InProgress" - {

      s"when the $DispatchPlacePage answer is empty for a $NorthernIrelandWarehouseKeeper" in {
        InfoSection.status(dataRequest(FakeRequest(),
          ern = testNorthernIrelandErn,
          answers = emptyUserAnswers.copy(ern = testNorthernIrelandErn)
            .set(DestinationTypePage, ExportWithCustomsDeclarationLodgedInTheUk)
            .set(DeferredMovementPage(), false)
            .set(LocalReferenceNumberPage(), "")
            .set(InvoiceDetailsPage(), InvoiceDetailsModel("inv ref", LocalDate.parse("2020-12-25")))
            .set(DispatchDetailsPage(), DispatchDetailsModel(LocalDate.parse("2020-10-31"), LocalTime.parse("23:59:59")))
        )) mustBe InProgress
      }

      s"when all the required pages haven't been populated" in {
        InfoSection.status(dataRequest(FakeRequest(),
          answers = emptyUserAnswers
            .set(DestinationTypePage, ExportWithCustomsDeclarationLodgedInTheUk)
            .set(DeferredMovementPage(), false)
            .set(InvoiceDetailsPage(), InvoiceDetailsModel("inv ref", LocalDate.parse("2020-12-25")))
            .set(DispatchDetailsPage(), DispatchDetailsModel(LocalDate.parse("2020-10-31"), LocalTime.parse("23:59:59")))
        )) mustBe InProgress
      }
    }

    "should return NotStarted" - {

      "when no answers exist" in {

        InfoSection.status(dataRequest(FakeRequest(),
          answers = emptyUserAnswers
        )) mustBe NotStarted
      }
    }


  }
}
