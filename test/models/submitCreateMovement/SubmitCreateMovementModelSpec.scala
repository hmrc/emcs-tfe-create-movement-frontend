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
import models.requests.DataRequest
import models.sections.info._
import models.sections.info.movementScenario.MovementScenario.UkTaxWarehouse
import pages.sections.destination.{DestinationConsigneeDetailsPage, DestinationWarehouseExcisePage}
import pages.sections.dispatch.DispatchWarehouseExcisePage
import pages.sections.info._
import play.api.i18n.Messages
import play.api.libs.json.Json
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest

class SubmitCreateMovementModelSpec extends SpecBase with ItemFixtures {
  implicit val ac: AppConfig = appConfig

  val messagesForLanguage = ItemSmallIndependentProducerMessages.English
  implicit val msgs: Messages = messages(Seq(messagesForLanguage.lang))

  val fakeRequest: FakeRequest[AnyContentAsEmpty.type] = FakeRequest()

  "dispatchOffice" - {
    "when XIPC" - {
      s"must return OfficeModel(XI$dispatchOfficeSuffix)" in {
        implicit val dr: DataRequest[_] = dataRequest(
          request = fakeRequest,
          answers = emptyUserAnswers,
          ern = "XIPC123"
        )

        SubmitCreateMovementModel.dispatchOffice mustBe OfficeModel(s"XI$dispatchOfficeSuffix")
      }
    }

    "when XIPA" - {
      s"must return OfficeModel(XI$dispatchOfficeSuffix)" in {
        implicit val dr: DataRequest[_] = dataRequest(
          request = fakeRequest,
          answers = emptyUserAnswers,
          ern = "XIPA123"
        )

        SubmitCreateMovementModel.dispatchOffice mustBe OfficeModel(s"XI$dispatchOfficeSuffix")
      }
    }

    "when XIRC" - {
      s"must return OfficeModel(XI$dispatchOfficeSuffix)" in {
        implicit val dr: DataRequest[_] = dataRequest(
          request = fakeRequest,
          answers = emptyUserAnswers,
          ern = "XIRC123"
        )

        SubmitCreateMovementModel.dispatchOffice mustBe OfficeModel(s"XI$dispatchOfficeSuffix")
      }
    }
    "when XIWK" - {
      "when DispatchWarehouseExcisePage is present" - {
        s"must return OfficeModel(XI$dispatchOfficeSuffix) when DispatchWarehouseExcisePage starts with XI" in {
          implicit val dr: DataRequest[_] = dataRequest(
            request = fakeRequest,
            answers = emptyUserAnswers.set(DispatchWarehouseExcisePage, "XI00123456789"),
            ern = "XIWK123"
          )
          SubmitCreateMovementModel.dispatchOffice mustBe OfficeModel(s"XI$dispatchOfficeSuffix")
        }
        s"must return OfficeModel(GB$dispatchOfficeSuffix) when DispatchWarehouseExcisePage starts with GB" in {
          implicit val dr: DataRequest[_] = dataRequest(
            request = fakeRequest,
            answers = emptyUserAnswers.set(DispatchWarehouseExcisePage, "GB00123456789"),
            ern = "XIWK123"
          )
          SubmitCreateMovementModel.dispatchOffice mustBe OfficeModel(s"GB$dispatchOfficeSuffix")
        }
      }
      "when DispatchWarehouseExcisePage is missing" - {
        s"must return OfficeModel(GB$dispatchOfficeSuffix) when DispatchPlacePage is GreatBritain" in {
          implicit val dr: DataRequest[_] = dataRequest(
            request = fakeRequest,
            answers = emptyUserAnswers.set(DispatchPlacePage, DispatchPlace.GreatBritain),
            ern = "XIWK123"
          )

          SubmitCreateMovementModel.dispatchOffice mustBe OfficeModel(s"GB$dispatchOfficeSuffix")
        }
        s"must return OfficeModel(XI$dispatchOfficeSuffix) when DispatchPlacePage is NorthernIreland" in {
          implicit val dr: DataRequest[_] = dataRequest(
            request = fakeRequest,
            answers = emptyUserAnswers.set(DispatchPlacePage, DispatchPlace.NorthernIreland),
            ern = "XIWK123"
          )

          SubmitCreateMovementModel.dispatchOffice mustBe OfficeModel(s"XI$dispatchOfficeSuffix")
        }
      }
    }
    Seq("GBRC123", "GBWK123").foreach(
      ern =>
        s"when $ern" - {
          s"must return OfficeModel(GB$dispatchOfficeSuffix)" in {
            implicit val dr: DataRequest[_] = dataRequest(
              request = fakeRequest,
              answers = emptyUserAnswers,
              ern = ern
            )

            SubmitCreateMovementModel.dispatchOffice mustBe OfficeModel(s"GB$dispatchOfficeSuffix")
          }
        }
    )
  }

  "apply" - {
    "must return a model and output the correct JSON" - {
      "when XIRC" in {
        implicit val dr: DataRequest[_] = dataRequest(
          request = fakeRequest,
          answers = baseFullUserAnswers,
          ern = "XIRC123"
        )

        val submission = SubmitCreateMovementModel.apply

        submission mustBe xircSubmitCreateMovementModel
        Json.toJson(submission) mustBe xircSubmitCreateMovementJson
      }
      "when XIWK" in {
        implicit val dr: DataRequest[_] = dataRequest(
          request = fakeRequest,
          answers = baseFullUserAnswers.set(DispatchPlacePage, DispatchPlace.NorthernIreland),
          ern = "XIWK123"
        )

        val submission = SubmitCreateMovementModel.apply

        submission mustBe  xiwkSubmitCreateMovementModel
        Json.toJson(submission) mustBe xiwkSubmitCreateMovementJson
      }
      "when GBRC" in {
        implicit val dr: DataRequest[_] = dataRequest(
          request = fakeRequest,
          answers = baseFullUserAnswers
            .set(DestinationTypePage, UkTaxWarehouse.GB)
            .set(DestinationWarehouseExcisePage, testErn)
            .set(DestinationConsigneeDetailsPage, false),
          ern = "GBRC123"
        )

        val submission = SubmitCreateMovementModel.apply

        submission mustBe gbrcSubmitCreateMovementModel
        Json.toJson(submission) mustBe gbrcSubmitCreateMovementJson
      }
      "when GBWK" in {
        implicit val dr: DataRequest[_] = dataRequest(
          request = fakeRequest,
          answers = baseFullUserAnswers
            .set(DestinationTypePage, UkTaxWarehouse.GB)
            .set(DestinationWarehouseExcisePage, testErn)
            .set(DestinationConsigneeDetailsPage, false),
          ern = "GBWK123"
        )

        val submission = SubmitCreateMovementModel.apply

        submission mustBe gbwkSubmitCreateMovementModel
        Json.toJson(submission) mustBe gbwkSubmitCreateMovementJson
      }
    }
  }
}
