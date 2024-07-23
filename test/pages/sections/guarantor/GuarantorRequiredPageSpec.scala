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

package pages.sections.guarantor

import base.SpecBase
import models.GoodsType._
import models.requests.DataRequest
import models.sections.info.movementScenario.MovementScenario
import models.sections.info.movementScenario.MovementScenario.{EuTaxWarehouse, _}
import models.sections.journeyType.HowMovementTransported._
import models.sections.transportUnit.TransportUnitType._
import pages.sections.info.DestinationTypePage
import pages.sections.items.ItemExciseProductCodePage
import pages.sections.journeyType.HowMovementTransportedPage
import pages.sections.transportUnit.TransportUnitTypePage
import play.api.test.FakeRequest

class GuarantorRequiredPageSpec extends SpecBase {

  "guarantorRequired()" - {

    "when guarantorIsOptionalUKtoUK returns false" - {

      "must return true" in {

        implicit val dr: DataRequest[_] = dataRequest(
          request = FakeRequest(),
          answers = emptyUserAnswers
            .set(DestinationTypePage, UkTaxWarehouse.GB)
            .set(ItemExciseProductCodePage(0), Tobacco.code),
          ern = testGreatBritainErn
        )

        GuarantorRequiredPage.isRequired() mustBe true
      }
    }

    "when guarantorIsOptionalUKtoUK returns true" - {

      "must return false" in {

        implicit val dr: DataRequest[_] = dataRequest(
          request = FakeRequest(),
          answers = emptyUserAnswers.set(DestinationTypePage, UkTaxWarehouse.GB),
          ern = testGreatBritainWarehouseKeeperErn
        )

        GuarantorRequiredPage.isRequired() mustBe false
      }
    }

    "when guarantorIsOptionalNIToEU returns false" - {

      "must return true" in {

        implicit val dr: DataRequest[_] = dataRequest(
          request = FakeRequest(),
          answers = emptyUserAnswers
            .set(DestinationTypePage, EuTaxWarehouse)
            .set(ItemExciseProductCodePage(0), Tobacco.code)
            .set(HowMovementTransportedPage, FixedTransportInstallations)
        )

        GuarantorRequiredPage.isRequired() mustBe true
      }
    }

    "when guarantorIsOptionalNIToEU returns true" - {

      "must return false" in {

        implicit val dr: DataRequest[_] = dataRequest(
          request = FakeRequest(),
          answers = emptyUserAnswers.set(DestinationTypePage, EuTaxWarehouse)
        )

        GuarantorRequiredPage.isRequired() mustBe false
      }
    }
  }

  "guarantorIsOptionalUKtoUK" - {

    "when the movement is UK to UK" - {

      UkTaxWarehouse.values.foreach { destinationType =>

        s"when destinationType is $destinationType" - {

          "when no items have been added yet" - {

            "must return true" in {

              implicit val dr: DataRequest[_] = dataRequest(
                request = FakeRequest(),
                answers = emptyUserAnswers.set(DestinationTypePage, UkTaxWarehouse.GB),
                ern = testGreatBritainWarehouseKeeperErn
              )

              GuarantorRequiredPage.guarantorIsOptionalUKtoUK mustBe true
            }
          }

          "when items added only include beer or wine" - {

            "must return true" in {

              implicit val dr: DataRequest[_] = dataRequest(
                request = FakeRequest(),
                answers = emptyUserAnswers
                  .set(DestinationTypePage, UkTaxWarehouse.GB)
                  .set(ItemExciseProductCodePage(testIndex1), "B000")
                  .set(ItemExciseProductCodePage(testIndex2), "W200"),
                ern = testGreatBritainWarehouseKeeperErn
              )

              GuarantorRequiredPage.guarantorIsOptionalUKtoUK mustBe true
            }
          }

          "when items added include any other type of product" - {

            "must return false" in {

              implicit val dr: DataRequest[_] = dataRequest(
                request = FakeRequest(),
                answers = emptyUserAnswers
                  .set(DestinationTypePage, UkTaxWarehouse.GB)
                  .set(ItemExciseProductCodePage(testIndex1), "B000")
                  .set(ItemExciseProductCodePage(testIndex2), "W200")
                  .set(ItemExciseProductCodePage(testIndex3), "T200"),
                ern = testGreatBritainWarehouseKeeperErn
              )

              GuarantorRequiredPage.guarantorIsOptionalUKtoUK mustBe false
            }
          }
        }
      }
    }

    "when the movement is NOT UK to UK" - {

      MovementScenario.values
        .filterNot(UkTaxWarehouse.values.contains)
        .filterNot(valuesForDutyPaidTraders.contains)
        .foreach { destinationType =>

        s"when destinationType is $destinationType" - {

          "must return false" in {

            implicit val dr: DataRequest[_] = dataRequest(
              request = FakeRequest(),
              answers = emptyUserAnswers.set(DestinationTypePage, destinationType),
              ern = testGreatBritainWarehouseKeeperErn
            )

            GuarantorRequiredPage.guarantorIsOptionalUKtoUK mustBe false
          }
        }
      }
    }
  }

  "guarantorIsOptionalNItoEU" - {

    "when the movement is NI to EU" - {

      "when not UnknownDestination or ExemptedOrganisation" - {

        Seq(
          DirectDelivery,
          RegisteredConsignee,
          EuTaxWarehouse,
          TemporaryRegisteredConsignee,
          CertifiedConsignee,
          TemporaryCertifiedConsignee
        ).foreach { destinationType =>

          s"where destination type is $destinationType" - {

            "when no items have been added yet" - {

              "when no Journey Type has been answered yet" - {

                "when no Transport Units have been added" - {

                  "must return true" - {

                    implicit val dr: DataRequest[_] = dataRequest(
                      request = FakeRequest(),
                      answers = emptyUserAnswers.set(DestinationTypePage, destinationType),
                      ern = testNorthernIrelandErn
                    )

                    GuarantorRequiredPage.guarantorIsOptionalNIToEU mustBe true
                  }
                }

                "when Transport Units have been added (only Fixed Transport)" - {

                  "must return true" - {

                    implicit val dr: DataRequest[_] = dataRequest(
                      request = FakeRequest(),
                      answers = emptyUserAnswers
                        .set(DestinationTypePage, destinationType)
                        .set(TransportUnitTypePage(testIndex1), FixedTransport)
                        .set(TransportUnitTypePage(testIndex2), FixedTransport)
                      ,
                      ern = testNorthernIrelandErn
                    )

                    GuarantorRequiredPage.guarantorIsOptionalNIToEU mustBe true
                  }
                }

                "when Transport Units have been added (including something other than FixedTransport)" - {

                  "must return false" - {

                    implicit val dr: DataRequest[_] = dataRequest(
                      request = FakeRequest(),
                      answers = emptyUserAnswers
                        .set(DestinationTypePage, destinationType)
                        .set(TransportUnitTypePage(testIndex1), FixedTransport)
                        .set(TransportUnitTypePage(testIndex2), Tractor)
                      ,
                      ern = testNorthernIrelandErn
                    )

                    GuarantorRequiredPage.guarantorIsOptionalNIToEU mustBe false
                  }
                }
              }

              "when Journey Type has been answered" - {

                "when Journey Type is fixed" - {

                  "must return true" - {

                    implicit val dr: DataRequest[_] = dataRequest(
                      request = FakeRequest(),
                      answers = emptyUserAnswers
                        .set(DestinationTypePage, destinationType)
                        .set(HowMovementTransportedPage, FixedTransportInstallations),
                      ern = testNorthernIrelandErn
                    )

                    GuarantorRequiredPage.guarantorIsOptionalNIToEU mustBe true
                  }
                }

                "when Journey Type is not fixed" - {

                  "must return false" - {

                    implicit val dr: DataRequest[_] = dataRequest(
                      request = FakeRequest(),
                      answers = emptyUserAnswers
                        .set(DestinationTypePage, destinationType)
                        .set(HowMovementTransportedPage, AirTransport),
                      ern = testNorthernIrelandErn
                    )

                    GuarantorRequiredPage.guarantorIsOptionalNIToEU mustBe false
                  }
                }
              }
            }

            "when items have been added" - {

              "when items are only Energy" - {

                "must return true" in {

                  implicit val dr: DataRequest[_] = dataRequest(
                    request = FakeRequest(),
                    answers = emptyUserAnswers
                      .set(DestinationTypePage, destinationType)
                      .set(ItemExciseProductCodePage(testIndex1), "E450")
                      .set(ItemExciseProductCodePage(testIndex2), "E500"),
                    ern = testNorthernIrelandErn
                  )

                  GuarantorRequiredPage.guarantorIsOptionalNIToEU mustBe true
                }
              }

              "when items include things other than Energy" - {

                "must return false" in {

                  implicit val dr: DataRequest[_] = dataRequest(
                    request = FakeRequest(),
                    answers = emptyUserAnswers
                      .set(DestinationTypePage, destinationType)
                      .set(ItemExciseProductCodePage(testIndex1), "B200"),
                    ern = testNorthernIrelandErn
                  )

                  GuarantorRequiredPage.guarantorIsOptionalNIToEU mustBe false
                }
              }
            }
          }
        }
      }

      "when UnknownDestination or ExemptedOrganisation" - {

        Seq(
          UnknownDestination,
          ExemptedOrganisation
        ).foreach { destinationType =>

          s"where destination type is $destinationType" - {

            "must return false" - {

              implicit val dr: DataRequest[_] = dataRequest(
                request = FakeRequest(),
                answers = emptyUserAnswers.set(DestinationTypePage, destinationType),
                ern = testNorthernIrelandErn
              )

              GuarantorRequiredPage.guarantorIsOptionalNIToEU mustBe false
            }
          }
        }
      }
    }

    "when the movement is not NI to EU" - {

      MovementScenario.valuesExportUkAndUkTaxWarehouse.foreach { destinationType =>

        s"where destination type is $destinationType" - {

          "must return false" - {

            implicit val dr: DataRequest[_] = dataRequest(
              request = FakeRequest(),
              answers = emptyUserAnswers.set(DestinationTypePage, destinationType),
              ern = testNorthernIrelandErn
            )

            GuarantorRequiredPage.guarantorIsOptionalNIToEU mustBe false
          }
        }
      }
    }
  }
}
