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

package pages.sections.dispatch

import base.SpecBase
import fixtures.MovementSubmissionFailureFixtures
import models.requests.DataRequest
import models.sections.info.movementScenario.MovementScenario
import models.sections.info.movementScenario.MovementScenario.CertifiedConsignee
import pages.sections.info.DestinationTypePage
import play.api.test.FakeRequest
import viewmodels.taskList.{Completed, InProgress, NotStarted, UpdateNeeded}

class DispatchSectionSpec extends SpecBase with MovementSubmissionFailureFixtures {

  "status" - {

    "must return UpdateNeeded" - {

      "when there is a Dispatch Submission Error" in {
        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(),
          emptyUserAnswers
            .set(DispatchWarehouseExcisePage, "beans")
            .set(DispatchUseConsignorDetailsPage, false)
            .set(DispatchAddressPage, testUserAddress)
            .copy(submissionFailures = Seq(dispatchWarehouseInvalidOrMissingOnSeedError))
        )

        DispatchSection.status mustBe UpdateNeeded
      }
    }

    "must return Completed" - {

      "when duty paid and all required fields are completed" in {
        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(),
          emptyUserAnswers
            .set(DestinationTypePage, CertifiedConsignee)
            .set(DispatchUseConsignorDetailsPage, true)
            .set(DispatchAddressPage, testUserAddress)
        )

        DispatchSection.status mustBe Completed
      }

      "when duty suspended and DispatchWarehouseExcisePage is completed" in {
        MovementScenario.values.filterNot(MovementScenario.valuesForDutyPaidTraders.contains(_)).foreach { movementScenario =>
          implicit val dr: DataRequest[_] = dataRequest(
            FakeRequest(),
            emptyUserAnswers.set(DispatchWarehouseExcisePage, "beans").set(DestinationTypePage, movementScenario)
          )

          DispatchSection.status mustBe Completed
        }
      }
    }

    "must return InProgress" - {

      "when duty paid and only DispatchUseConsignorDetailsPage is completed" in {
        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(),
          emptyUserAnswers
            .set(DestinationTypePage, CertifiedConsignee)
            .set(DispatchUseConsignorDetailsPage, true)
        )

        DispatchSection.status mustBe InProgress
      }

      "when duty paid and only DispatchAddressPage is completed" in {
        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(),
          emptyUserAnswers
            .set(DestinationTypePage, CertifiedConsignee)
            .set(DispatchAddressPage, testUserAddress)
        )

        DispatchSection.status mustBe InProgress
      }
    }

    "must return NotStarted" - {

      "when duty paid and no fields are completed" in {
        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), emptyUserAnswers.set(DestinationTypePage, CertifiedConsignee))

        DispatchSection.status mustBe NotStarted
      }

      "when duty suspended and DispatchWarehouseExcisePage is not completed" in {
        MovementScenario.values.filterNot(MovementScenario.valuesForDutyPaidTraders.contains(_)).foreach { movementScenario =>
          implicit val dr: DataRequest[_] = dataRequest(
            FakeRequest(),
            emptyUserAnswers.set(DestinationTypePage, movementScenario)
          )

          DispatchSection.status mustBe NotStarted
        }
      }
    }
  }
}
