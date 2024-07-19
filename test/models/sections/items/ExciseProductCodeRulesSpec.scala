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

package models.sections.items

import base.SpecBase
import models.sections.info.DispatchPlace
import models.sections.info.movementScenario.MovementScenario
import models.sections.info.movementScenario.MovementScenario.{EuTaxWarehouse, TemporaryCertifiedConsignee, UkTaxWarehouse}
import models.sections.items.ExciseProductCodeRules.{NINoGuarantorRules, UKNoGuarantorRules, UnknownDestinationRules}
import models.sections.journeyType.HowMovementTransported.AirTransport
import pages.sections.guarantor.GuarantorRequiredPage
import pages.sections.info.{DestinationTypePage, DispatchPlacePage}
import pages.sections.journeyType.HowMovementTransportedPage
import play.api.test.FakeRequest

class ExciseProductCodeRulesSpec extends SpecBase {

  "UKNoGuarantorRules" - {
    ".shouldDisplayInset" - {
      "must return true" - {
        "when GuarantorRequiredPage is false and Guarantor is Optional (UK to UK)" in {
          val request = dataRequest(
            FakeRequest(),
            ern = testGreatBritainWarehouseKeeperErn,
            answers = emptyUserAnswers
              .set(GuarantorRequiredPage, false)
              .set(DestinationTypePage, UkTaxWarehouse.GB)
          )
          UKNoGuarantorRules.shouldDisplayInset()(request) mustBe true
        }
      }
      "must return false" - {

        "when GuarantorRequiredPage is not answered yet" in {
          val request = dataRequest(
            FakeRequest(),
            ern = testGreatBritainWarehouseKeeperErn,
            answers = emptyUserAnswers.set(DestinationTypePage, UkTaxWarehouse.GB)
          )
          UKNoGuarantorRules.shouldDisplayInset()(request) mustBe false
        }

        "when GuarantorRequiredPage is true" in {
          val request = dataRequest(
            FakeRequest(),
            ern = testGreatBritainWarehouseKeeperErn,
            answers = emptyUserAnswers
              .set(GuarantorRequiredPage, true)
              .set(DestinationTypePage, UkTaxWarehouse.GB)
          )
          UKNoGuarantorRules.shouldDisplayInset()(request) mustBe false
        }

        "when Guarantor is always required (UK to UK)" in {
          val request = dataRequest(
            FakeRequest(),
            ern = testGreatBritainWarehouseKeeperErn,
            answers = emptyUserAnswers.set(DestinationTypePage, EuTaxWarehouse)
          )
          UKNoGuarantorRules.shouldDisplayInset()(request) mustBe false
        }

        "when Guarantor is always required (NI to EU)" in {
          val request = dataRequest(
            FakeRequest(),
            ern = testNorthernIrelandErn,
            answers = emptyUserAnswers
              .set(DestinationTypePage, EuTaxWarehouse)
              .set(HowMovementTransportedPage, AirTransport)
          )
          UKNoGuarantorRules.shouldDisplayInset()(request) mustBe false
        }

        "when Guarantor is NOT required (NI to EU)" in {
          val request = dataRequest(
            FakeRequest(),
            ern = testNorthernIrelandErn,
            answers = emptyUserAnswers.set(DestinationTypePage, EuTaxWarehouse)
          )
          UKNoGuarantorRules.shouldDisplayInset()(request) mustBe false
        }
      }
    }

    ".shouldResetGuarantorSectionOnSubmission" - {
      "must return true" - {
        "when shouldDisplayInset is true and exciseProductCode is not Beer or Wine" - {
          Seq("T400", "E500", "S200").foreach { exciseProductCode =>

            s"when excise product code is $exciseProductCode" in {

              val request = dataRequest(
                FakeRequest(),
                ern = testGreatBritainWarehouseKeeperErn,
                answers = emptyUserAnswers
                  .set(GuarantorRequiredPage, false)
                  .set(DestinationTypePage, MovementScenario.UkTaxWarehouse.GB)
              )
              UKNoGuarantorRules.shouldResetGuarantorSectionOnSubmission(exciseProductCode)(request) mustBe true
            }
          }
        }
      }

      "must return false" - {
        "when shouldDisplayInset is false" in {
          val request = dataRequest(FakeRequest(), ern = testGreatBritainWarehouseKeeperErn, answers = emptyUserAnswers.set(GuarantorRequiredPage, false))
          UKNoGuarantorRules.shouldResetGuarantorSectionOnSubmission("B000")(request) mustBe false
        }
        "when exciseProductCode is B000" in {
          val request = dataRequest(FakeRequest(), ern = testGreatBritainWarehouseKeeperErn, answers = emptyUserAnswers.set(GuarantorRequiredPage, false))
          UKNoGuarantorRules.shouldResetGuarantorSectionOnSubmission("B000")(request) mustBe false
        }
        "when exciseProductCode is W200" in {
          val request = dataRequest(FakeRequest(), ern = testGreatBritainWarehouseKeeperErn, answers = emptyUserAnswers.set(GuarantorRequiredPage, false))
          UKNoGuarantorRules.shouldResetGuarantorSectionOnSubmission("W200")(request) mustBe false
        }
        "when exciseProductCode is W300" in {
          val request = dataRequest(FakeRequest(), ern = testGreatBritainWarehouseKeeperErn, answers = emptyUserAnswers.set(GuarantorRequiredPage, false))
          UKNoGuarantorRules.shouldResetGuarantorSectionOnSubmission("W300")(request) mustBe false
        }
        "when shouldDisplayInset is true" in {
          val request = dataRequest(FakeRequest(), ern = testGreatBritainWarehouseKeeperErn, answers = emptyUserAnswers.set(GuarantorRequiredPage, true))
          UKNoGuarantorRules.shouldResetGuarantorSectionOnSubmission("B000")(request) mustBe false
        }
      }
    }
  }

  "NINoGuarantorRules" - {
    ".shouldDisplayInset" - {
      "must return true" - {
        "when GuarantorRequiredPage is false and XIPA" in {
          val request = dataRequest(FakeRequest(), ern = "XIPA123", answers = emptyUserAnswers
            .set(DestinationTypePage, TemporaryCertifiedConsignee)
            .set(GuarantorRequiredPage, false)
          )
          NINoGuarantorRules.shouldDisplayInset()(request) mustBe true
        }
        "when GuarantorRequiredPage is false and XIWK and destination is not Export, UK Tax Warehouse, Unknown or Exempted" - {
          val movementScenarios = MovementScenario.values.filterNot(
            Seq(
              MovementScenario.ExportWithCustomsDeclarationLodgedInTheUk,
              MovementScenario.ExportWithCustomsDeclarationLodgedInTheEu,
              MovementScenario.UkTaxWarehouse.GB,
              MovementScenario.UkTaxWarehouse.NI,
              MovementScenario.UnknownDestination,
              MovementScenario.ExemptedOrganisation
            ).contains
          )
          movementScenarios.foreach { movementScenario =>

            s"when movement scenario is $movementScenario" in {

              val request = dataRequest(
                FakeRequest(),
                ern = testNorthernIrelandErn,
                answers = emptyUserAnswers
                  .set(DestinationTypePage, movementScenario)
                  .set(GuarantorRequiredPage, false)
              )
              NINoGuarantorRules.shouldDisplayInset()(request) mustBe true
            }
          }
        }
      }

      "must return false" - {
        "when GuarantorRequiredPage is true" - {
          Seq("XIPA123", "XIWK123", "XIRC123").foreach { ern =>

            s"when ERN is $ern" - {

              val movementScenarios = MovementScenario.values.filterNot(
                Seq(
                  MovementScenario.ExportWithCustomsDeclarationLodgedInTheUk,
                  MovementScenario.ExportWithCustomsDeclarationLodgedInTheEu,
                  MovementScenario.UkTaxWarehouse.GB,
                  MovementScenario.UkTaxWarehouse.NI,
                  MovementScenario.UnknownDestination,
                  MovementScenario.ExemptedOrganisation
                ).contains
              )

              movementScenarios.foreach { movementScenario =>

                s"when movement scenario is $movementScenario" in {

                  val request = dataRequest(
                    FakeRequest(),
                    ern = ern,
                    answers = emptyUserAnswers.set(GuarantorRequiredPage, true).set(DestinationTypePage, movementScenario)
                  )
                  NINoGuarantorRules.shouldDisplayInset()(request) mustBe false
                }
              }
            }
          }
        }
        "when not XIPA or XIWK or XIRC" - {
          Seq("XIPC123", testGreatBritainWarehouseKeeperErn, "GBRC123").foreach { ern =>

            s"when ERN is $ern" - {

              val movementScenarios = MovementScenario.values.filterNot(
                Seq(
                  MovementScenario.ExportWithCustomsDeclarationLodgedInTheUk,
                  MovementScenario.ExportWithCustomsDeclarationLodgedInTheEu,
                  MovementScenario.UkTaxWarehouse.GB,
                  MovementScenario.UkTaxWarehouse.NI,
                  MovementScenario.UnknownDestination,
                  MovementScenario.ExemptedOrganisation
                ).contains
              )

              movementScenarios.foreach { movementScenario =>

                s"when movement scenario is $movementScenario" in {

                  val request = dataRequest(
                    FakeRequest(),
                    ern = ern,
                    answers = emptyUserAnswers.set(GuarantorRequiredPage, false).set(DestinationTypePage, movementScenario)
                  )
                  NINoGuarantorRules.shouldDisplayInset()(request) mustBe false
                }
              }
            }
          }
        }
        "when XIWK and destination is Export, UK Tax Warehouse or Unknown" - {
          val movementScenarios = Seq(
            MovementScenario.ExportWithCustomsDeclarationLodgedInTheUk,
            MovementScenario.ExportWithCustomsDeclarationLodgedInTheEu,
            MovementScenario.UkTaxWarehouse.GB,
            MovementScenario.UkTaxWarehouse.NI,
            MovementScenario.UnknownDestination,
            MovementScenario.ExemptedOrganisation
          )
          movementScenarios.foreach { movementScenario =>

            s"when movement scenario is $movementScenario" in {

              val request = dataRequest(
                FakeRequest(),
                ern = "XIWK123",
                answers = emptyUserAnswers.set(GuarantorRequiredPage, false).set(DestinationTypePage, movementScenario)
              )
              NINoGuarantorRules.shouldDisplayInset()(request) mustBe false
            }
          }
        }
        "when GuarantorRequiredPage is missing" in {
          Seq("XIWK123", "XIRC123").foreach { ern =>
            val request = dataRequest(FakeRequest(), ern = ern)
            NINoGuarantorRules.shouldDisplayInset()(request) mustBe false
          }
        }
      }
    }

    ".shouldResetGuarantorSectionOnSubmission" - {
      "must return true" - {
        "when shouldDisplayInset is true and exciseProductCode is not Energy" in {
          val request = dataRequest(
            FakeRequest(),
            ern = "XIWK123",
            answers = emptyUserAnswers.set(GuarantorRequiredPage, false).set(DestinationTypePage, MovementScenario.DirectDelivery)
          )
          NINoGuarantorRules.shouldResetGuarantorSectionOnSubmission("B123")(request) mustBe true
        }
      }

      "must return false" - {
        "when shouldDisplayInset is false" in {
          val request = dataRequest(FakeRequest(), ern = "XIWK123")
          NINoGuarantorRules.shouldResetGuarantorSectionOnSubmission("B123")(request) mustBe false
        }
        "when exciseProductCode starts with E" in {
          val request = dataRequest(FakeRequest(), ern = "XIWK123", answers = emptyUserAnswers.set(GuarantorRequiredPage, false).set(DispatchPlacePage, DispatchPlace.NorthernIreland))
          NINoGuarantorRules.shouldResetGuarantorSectionOnSubmission("E123")(request) mustBe false
        }
      }
    }
  }

  "UnknownDestinationRules" - {
    ".shouldDisplayInset" - {
      "must return true" - {
        "when DestinationTypePage is UnknownDestination" in {
          val request = dataRequest(FakeRequest(), answers = emptyUserAnswers.set(DestinationTypePage, MovementScenario.UnknownDestination))
          UnknownDestinationRules.shouldDisplayInset()(request) mustBe true
        }
      }

      "must return false" - {
        "when DestinationTypePage is not UnknownDestination" in {
          MovementScenario.values.filterNot(_ == MovementScenario.UnknownDestination).foreach { scenario =>
            val request = dataRequest(FakeRequest(), answers = emptyUserAnswers.set(DestinationTypePage, scenario))
            UnknownDestinationRules.shouldDisplayInset()(request) mustBe false
          }
        }
        "when DestinationTypePage is missing" in {
          val request = dataRequest(FakeRequest())
          UnknownDestinationRules.shouldDisplayInset()(request) mustBe false
        }
      }
    }

    ".shouldResetGuarantorSectionOnSubmission" - {
      "must return false" in {
        val request = dataRequest(FakeRequest())
        UnknownDestinationRules.shouldResetGuarantorSectionOnSubmission("B123")(request) mustBe false
      }
    }
  }
}