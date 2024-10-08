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

package pages.sections.destination

import base.SpecBase
import fixtures.{MovementSubmissionFailureFixtures, UserAddressFixtures}
import models.requests.DataRequest
import models.sections.info.movementScenario.MovementScenario
import models.sections.info.movementScenario.MovementScenario._
import pages.sections.info.DestinationTypePage
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import utils.JsonOptionFormatter
import viewmodels.taskList._

class DestinationSectionSpec extends SpecBase
  with UserAddressFixtures
  with JsonOptionFormatter
  with MovementSubmissionFailureFixtures {

  val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest()

  "status" - {

    "when DestinationWarehouseExcisePage has a MovementSubmissionError" - {
      "must return UpdateNeeded" - {

        implicit val dr: DataRequest[_] = dataRequest(request,
          emptyUserAnswers
            .set(DestinationWarehouseExcisePage, destinationWarehouseExciseFailure.errorType)
            .copy(submissionFailures = Seq(destinationWarehouseExciseFailure))
        )

        DestinationSection.status mustBe UpdateNeeded
      }
    }

    "when shouldStartFlowAtDestinationWarehouseExcise" - {
      "must return Completed" - {
        "when mandatory pages have an answer" in {
          Seq(
            UkTaxWarehouse.GB,
            UkTaxWarehouse.NI,
            EuTaxWarehouse
          ).foreach {
            implicit destinationTypePageAnswer =>
              assert(
                DestinationSection.shouldStartFlowAtDestinationWarehouseExcise === true,
                s"shouldStartFlowAtDestinationWarehouseExcise returned false for MovementScenario $destinationTypePageAnswer"
              )

              implicit val dr: DataRequest[_] = dataRequest(request, emptyUserAnswers
                .set(DestinationTypePage, destinationTypePageAnswer)
                .set(DestinationWarehouseExcisePage, testErn)
                .set(DestinationAddressPage, testUserAddress)
              )

              DestinationSection.status mustBe Completed
          }
        }
      }

      "must return InProgress" - {
        "when some, but not all, mandatory pages have an answer" in {
          Seq(
            UkTaxWarehouse.GB,
            UkTaxWarehouse.NI,
            EuTaxWarehouse
          ).foreach {
            implicit destinationTypePageAnswer =>
              assert(
                DestinationSection.shouldStartFlowAtDestinationWarehouseExcise === true,
                s"shouldStartFlowAtDestinationWarehouseExcise returned false for MovementScenario $destinationTypePageAnswer"
              )

              val baseUserAnswers = emptyUserAnswers
                .set(DestinationTypePage, destinationTypePageAnswer)
                .set(DestinationWarehouseExcisePage, testErn)
                .set(DestinationAddressPage, testUserAddress)

              Seq(
                DestinationAddressPage, DestinationWarehouseExcisePage
              ).foreach {
                page =>
                  implicit val dr: DataRequest[_] = dataRequest(request, baseUserAnswers
                    .remove(page)
                  )

                  DestinationSection.status mustBe InProgress
              }
          }
        }
      }

      "must return NotStarted" - {
        "when no mandatory pages have an answer" in {
          Seq(
            UkTaxWarehouse.GB,
            UkTaxWarehouse.NI,
            EuTaxWarehouse
          ).foreach {
            implicit destinationTypePageAnswer =>
              assert(
                DestinationSection.shouldStartFlowAtDestinationWarehouseExcise === true,
                s"shouldStartFlowAtDestinationWarehouseExcise returned false for MovementScenario $destinationTypePageAnswer"
              )

              implicit val dr: DataRequest[_] = dataRequest(request, emptyUserAnswers
                .set(DestinationTypePage, destinationTypePageAnswer)
              )

              DestinationSection.status mustBe NotStarted
          }
        }
      }
    }

    "when shouldStartFlowAtDestinationWarehouseVat" - {

      "when RegisteredConsignee, TemporaryRegisteredConsignee or ExemptedOrganisation" - {

        "must return Completed" - {

          "when mandatory pages have an answer and DestinationDetailsChoicePage = true" in {
            Seq(
              TemporaryRegisteredConsignee,
              ExemptedOrganisation
            ).foreach {
              implicit destinationTypePageAnswer =>
                assert(
                  DestinationSection.shouldStartFlowAtDestinationWarehouseVat === true,
                  s"shouldStartFlowAtDestinationWarehouseVat returned false for MovementScenario $destinationTypePageAnswer"
                )

                implicit val dr: DataRequest[_] = dataRequest(request, emptyUserAnswers
                  .set(DestinationWarehouseVatPage, testVatNumber)
                  .set(DestinationTypePage, destinationTypePageAnswer)
                  .set(DestinationDetailsChoicePage, true)
                  .set(DestinationAddressPage, testUserAddress)
                )

                DestinationSection.status mustBe Completed
            }
          }

          "when mandatory pages have an answer and DestinationDetailsChoicePage = false" in {
            Seq(
              TemporaryRegisteredConsignee,
              ExemptedOrganisation
            ).foreach {
              implicit destinationTypePageAnswer =>
                assert(
                  DestinationSection.shouldStartFlowAtDestinationWarehouseVat === true,
                  s"shouldStartFlowAtDestinationWarehouseVat returned false for MovementScenario $destinationTypePageAnswer"
                )

                implicit val dr: DataRequest[_] = dataRequest(request, emptyUserAnswers
                  .set(DestinationWarehouseVatPage, testVatNumber)
                  .set(DestinationTypePage, destinationTypePageAnswer)
                  .set(DestinationDetailsChoicePage, false)
                )

                DestinationSection.status mustBe Completed
            }
          }
        }

        "must return InProgress" - {
          "when some, but not all, mandatory pages have an answer and DestinationDetailsChoicePage = true" in {
            Seq(
              TemporaryRegisteredConsignee,
              ExemptedOrganisation
            ).foreach {
              implicit destinationTypePageAnswer =>
                assert(
                  DestinationSection.shouldStartFlowAtDestinationWarehouseVat === true,
                  s"shouldStartFlowAtDestinationWarehouseVat returned false for MovementScenario $destinationTypePageAnswer"
                )

                val baseUserAnswers = emptyUserAnswers
                  .set(DestinationWarehouseVatPage, testVatNumber)
                  .set(DestinationTypePage, destinationTypePageAnswer)
                  .set(DestinationDetailsChoicePage, true)

                implicit val dr: DataRequest[_] = dataRequest(request, baseUserAnswers)

                DestinationSection.status mustBe InProgress
            }
          }
        }

        "must return NotStarted" - {
          "when no mandatory pages have an answer" in {
            Seq(
              TemporaryRegisteredConsignee,
              ExemptedOrganisation
            ).foreach {
              implicit destinationTypePageAnswer =>
                assert(
                  DestinationSection.shouldStartFlowAtDestinationWarehouseVat === true,
                  s"shouldStartFlowAtDestinationWarehouseVat returned false for MovementScenario $destinationTypePageAnswer"
                )

                implicit val dr: DataRequest[_] = dataRequest(request, emptyUserAnswers
                  .set(DestinationTypePage, destinationTypePageAnswer)
                )

                DestinationSection.status mustBe NotStarted
            }
          }
        }
      }

      "when CertifiedConsignee or TemporaryCertifiedConsignee" - {

        "must return Completed" - {

          "when mandatory pages have an answer DestinationConsigneeDetailsPage = false" in {
            Seq(
              CertifiedConsignee,
              TemporaryCertifiedConsignee
            ).foreach {
              implicit destinationTypePageAnswer =>
                assert(
                  DestinationSection.shouldStartFlowAtDestinationWarehouseVat === true,
                  s"shouldStartFlowAtDestinationWarehouseVat returned false for MovementScenario $destinationTypePageAnswer"
                )

                implicit val dr: DataRequest[_] = dataRequest(request, emptyUserAnswers
                  .set(DestinationWarehouseVatPage, testVatNumber)
                  .set(DestinationTypePage, destinationTypePageAnswer)
                  .set(DestinationAddressPage, testUserAddress)
                )

                DestinationSection.status mustBe Completed
            }
          }
        }

        "must return InProgress" - {

          "when some, but not all, mandatory pages have an answer DestinationConsigneeDetailsPage = false" in {
            Seq(
              CertifiedConsignee,
              TemporaryCertifiedConsignee
            ).foreach {
              implicit destinationTypePageAnswer =>
                assert(
                  DestinationSection.shouldStartFlowAtDestinationWarehouseVat === true,
                  s"shouldStartFlowAtDestinationWarehouseVat returned false for MovementScenario $destinationTypePageAnswer"
                )

                val baseUserAnswers = emptyUserAnswers
                  .set(DestinationWarehouseVatPage, testVatNumber)
                  .set(DestinationTypePage, destinationTypePageAnswer)
                  .set(DestinationConsigneeDetailsPage, false)

                implicit val dr: DataRequest[_] = dataRequest(request, baseUserAnswers)

                DestinationSection.status mustBe InProgress

            }
          }

          "when mandatory pages are missing but DestinationWarehouseVatPage has an answer" in {
            Seq(
              CertifiedConsignee,
              TemporaryCertifiedConsignee
            ).foreach {
              implicit destinationTypePageAnswer =>
                assert(
                  DestinationSection.shouldStartFlowAtDestinationWarehouseVat === true,
                  s"shouldStartFlowAtDestinationWarehouseVat returned false for MovementScenario $destinationTypePageAnswer"
                )
                implicit val dr: DataRequest[_] = dataRequest(request, emptyUserAnswers
                  .set(DestinationTypePage, destinationTypePageAnswer)
                  .set(DestinationWarehouseVatPage, testVatNumber)
                )

                DestinationSection.status mustBe InProgress
            }
          }
        }

        "must return NotStarted" - {

          "when no mandatory pages have an answer" in {
            Seq(
              CertifiedConsignee,
              TemporaryCertifiedConsignee
            ).foreach {
              implicit destinationTypePageAnswer =>
                assert(
                  DestinationSection.shouldStartFlowAtDestinationWarehouseVat === true,
                  s"shouldStartFlowAtDestinationWarehouseVat returned false for MovementScenario $destinationTypePageAnswer"
                )

                implicit val dr: DataRequest[_] = dataRequest(request, emptyUserAnswers
                  .set(DestinationTypePage, destinationTypePageAnswer)
                )

                DestinationSection.status mustBe NotStarted
            }
          }
        }
      }
    }

    "when shouldStartFlowAtDestinationBusinessName" - {
      "must return Completed" - {
        "when mandatory pages have an answer" in {
          Seq(
            DirectDelivery
          ).foreach {
            implicit destinationTypePageAnswer =>
              assert(
                DestinationSection.shouldStartFlowAtDestinationAddress === true,
                s"shouldStartFlowAtDestinationBusinessName returned false for MovementScenario $destinationTypePageAnswer"
              )
              implicit val dr: DataRequest[_] = dataRequest(request, emptyUserAnswers
                .set(DestinationTypePage, destinationTypePageAnswer)
                .set(DestinationAddressPage, testUserAddress)
              )

              DestinationSection.status mustBe Completed
          }
        }
      }

      "must return NotStarted" - {
        "when no mandatory pages have an answer" in {
          Seq(
            DirectDelivery
          ).foreach {
            implicit destinationTypePageAnswer =>
              assert(
                DestinationSection.shouldStartFlowAtDestinationAddress === true,
                s"shouldStartFlowAtDestinationBusinessName returned false for MovementScenario $destinationTypePageAnswer"
              )
              implicit val dr: DataRequest[_] = dataRequest(request, emptyUserAnswers
                .set(DestinationTypePage, destinationTypePageAnswer)
              )

              DestinationSection.status mustBe NotStarted
          }
        }
      }
    }

    "when canBeCompletedForTraderAndDestinationType = false" - {
      "must return NotStarted" in {
        MovementScenario.values
          .filterNot(
            Seq(
              UkTaxWarehouse.GB,
              UkTaxWarehouse.NI,
              EuTaxWarehouse,
              RegisteredConsignee,
              TemporaryRegisteredConsignee,
              CertifiedConsignee,
              TemporaryCertifiedConsignee,
              ExemptedOrganisation,
              DirectDelivery
            ).contains)
          .foreach {
            movementScenario =>
              implicit val dr: DataRequest[_] = dataRequest(request, emptyUserAnswers
                .set(DestinationTypePage, movementScenario)
                .set(DestinationAddressPage, testUserAddress)
                .set(DestinationConsigneeDetailsPage, false)
                .set(DestinationDetailsChoicePage, true)
                .set(DestinationWarehouseExcisePage, "")
                .set(DestinationWarehouseVatPage, "")
              )

              DestinationSection.status mustBe NotStarted
          }
      }
    }

    "when DestinationTypePage is missing" - {
      "must return NotStarted" in {
        DestinationSection.status(dataRequest(request)) mustBe NotStarted
      }
    }
  }
}
