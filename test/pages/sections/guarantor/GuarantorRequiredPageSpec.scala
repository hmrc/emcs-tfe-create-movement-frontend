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
import models.GoodsType
import models.GoodsType._
import models.requests.DataRequest
import models.sections.info.movementScenario.MovementScenario._
import models.sections.journeyType.HowMovementTransported._
import models.sections.transportUnit.TransportUnitType._
import pages.sections.consignee.ConsigneeExcisePage
import pages.sections.info.DestinationTypePage
import pages.sections.items.ItemExciseProductCodePage
import pages.sections.journeyType.HowMovementTransportedPage
import pages.sections.transportUnit.TransportUnitTypePage
import play.api.test.FakeRequest

class GuarantorRequiredPageSpec extends SpecBase {

  "guarantorRequired()" - {

    "when guarantorAlwaysRequiredUk() returns true" - {

      "must return true" in {

        implicit val dr: DataRequest[_] = dataRequest(
          request = FakeRequest(),
          answers = emptyUserAnswers
            .set(DestinationTypePage, UkTaxWarehouse.GB)
            .set(ItemExciseProductCodePage(0), Tobacco.code)
        )

        GuarantorRequiredPage.isRequired() mustBe true
      }
    }

    "when guarantorAlwaysRequiredNIToEU() returns true" - {

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

    "when neither guarantorAlwaysRequiredNIToEU() or guarantorAlwaysRequiredUk() return true" - {

      "must return false" in {

        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), emptyUserAnswers)

        GuarantorRequiredPage.isRequired() mustBe false
      }
    }
  }

  "guarantorAlwaysRequiredUk()" - {

    Seq(UkTaxWarehouse.GB, UkTaxWarehouse.NI).foreach{ destinationType =>

      s"when the Destination Type is UKTaxWarehouse: ${destinationType.toString}" - {

        Seq(Spirits, Intermediate, Energy, Tobacco).foreach { goodsType =>

          s"there is a GoodsType of ${goodsType.code}" - {

            "return true" in {

              implicit val dr: DataRequest[_] = dataRequest(
                request = FakeRequest(),
                answers = emptyUserAnswers
                  .set(DestinationTypePage, destinationType)
                  .set(ItemExciseProductCodePage(0), goodsType.code)
              )

              GuarantorRequiredPage.guarantorAlwaysRequiredUk() mustBe true
            }
          }
        }

        Seq(Wine, Beer).foreach { goodsType =>

          s"there is a GoodsType of ${goodsType.code}" - {

            "return false" in {

              implicit val dr: DataRequest[_] = dataRequest(
                request = FakeRequest(),
                answers = emptyUserAnswers
                  .set(DestinationTypePage, destinationType)
                  .set(ItemExciseProductCodePage(0), goodsType.code)
              )

              GuarantorRequiredPage.guarantorAlwaysRequiredUk() mustBe false
            }
          }
        }


        "there are ONLY items with GoodsTypes Spirit, Intermediate, Energy or Tobacco" - {

          "return true" in {

            implicit val dr: DataRequest[_] = dataRequest(
              request = FakeRequest(),
              answers = emptyUserAnswers
                .set(DestinationTypePage, destinationType)
                .set(ItemExciseProductCodePage(0), GoodsType.Spirits.code)
                .set(ItemExciseProductCodePage(1), GoodsType.Intermediate.code)
                .set(ItemExciseProductCodePage(2), GoodsType.Energy.code)
                .set(ItemExciseProductCodePage(3), GoodsType.Tobacco.code)
            )

            GuarantorRequiredPage.guarantorAlwaysRequiredUk() mustBe true
          }
        }

        "there is a mix of GoodsTypes including Spirit, Intermediate, Energy or Tobacco" - {

          "return true" in {

            implicit val dr: DataRequest[_] = dataRequest(
              request = FakeRequest(),
              answers = emptyUserAnswers
                .set(DestinationTypePage, destinationType)
                .set(ItemExciseProductCodePage(0), GoodsType.Beer.code)
                .set(ItemExciseProductCodePage(1), GoodsType.Spirits.code)
                .set(ItemExciseProductCodePage(2), GoodsType.Wine.code)
                .set(ItemExciseProductCodePage(3), GoodsType.Intermediate.code)
            )

            GuarantorRequiredPage.guarantorAlwaysRequiredUk() mustBe true
          }
        }

        "there are NO Items with the GoodsType Spirit, Intermediate, Energy or Tobacco" - {

          "return false" in {

            implicit val dr: DataRequest[_] = dataRequest(
              request = FakeRequest(),
              answers = emptyUserAnswers
                .set(DestinationTypePage, destinationType)
                .set(ItemExciseProductCodePage(0), GoodsType.Beer.code)
                .set(ItemExciseProductCodePage(1), GoodsType.Wine.code)
            )

            GuarantorRequiredPage.guarantorAlwaysRequiredUk() mustBe false
          }
        }
      }
    }

    Seq(ExportWithCustomsDeclarationLodgedInTheUk, ExportWithCustomsDeclarationLodgedInTheEu).foreach { destinationType =>

      s"when the Destination Type is Export: ${destinationType.toString}" - {

        "return true" in {

          implicit val dr: DataRequest[_] = dataRequest(
            request = FakeRequest(),
            answers = emptyUserAnswers.set(DestinationTypePage, destinationType)
          )

          GuarantorRequiredPage.guarantorAlwaysRequiredUk() mustBe true
        }
      }
    }

    Seq(DirectDelivery, ExemptedOrganisation, RegisteredConsignee, EuTaxWarehouse, TemporaryRegisteredConsignee, UnknownDestination).foreach{ destinationType =>

      s"when the Destination Type is NOT UKTaxWarehouse or Export: ${destinationType.toString}" - {

        "when the Consignee ERN is NOT GB or XI" - {

          "return true" in {

            val nonGBorXIErn = "AA1234567890"

            implicit val dr: DataRequest[_] = dataRequest(
              request = FakeRequest(),
              answers = emptyUserAnswers
                .set(DestinationTypePage, DirectDelivery)
                .set(ConsigneeExcisePage, nonGBorXIErn)
            )

            GuarantorRequiredPage.guarantorAlwaysRequiredUk() mustBe true
          }
        }

        Seq(testGreatBritainErn, testNorthernIrelandErn).foreach { ern =>

          s"when the Consignee ERN is GB or XI: $ern" - {

            "return true" in {

              implicit val dr: DataRequest[_] = dataRequest(
                request = FakeRequest(),
                answers = emptyUserAnswers
                  .set(DestinationTypePage, DirectDelivery)
                  .set(ConsigneeExcisePage, ern)
              )

              GuarantorRequiredPage.guarantorAlwaysRequiredUk() mustBe false
            }
          }
        }

        Seq(Intermediate, Energy, Tobacco).foreach { goodsType =>

          s"there is a GoodsType of ${goodsType.code}" - {

            "return false" in {

              implicit val dr: DataRequest[_] = dataRequest(
                request = FakeRequest(),
                answers = emptyUserAnswers
                  .set(DestinationTypePage, destinationType)
                  .set(ItemExciseProductCodePage(0), goodsType.code)
              )

              GuarantorRequiredPage.guarantorAlwaysRequiredUk() mustBe false
            }
          }
        }
      }
    }
  }

  "guarantorAlwaysRequiredNIToEU()" - {

    Seq(
      EuTaxWarehouse,
      ExemptedOrganisation,
      UnknownDestination,
      TemporaryRegisteredConsignee,
      RegisteredConsignee,
      DirectDelivery,
      CertifiedConsignee,
      TemporaryCertifiedConsignee
    ).foreach { destinationType =>

      s"when Destination Type is to EU: ${destinationType.toString}" - {

        Seq(Wine, Beer, Spirits, Intermediate, Tobacco).foreach { goodsType =>

          s"when GoodsType is Alcohol or Tobacco: ${goodsType.code}" - {

            s"when MovementTransportType is ${FixedTransportInstallations.toString}" - {

              "return true" in {

                implicit val dr: DataRequest[_] = dataRequest(
                  request = FakeRequest(),
                  answers = emptyUserAnswers
                    .set(DestinationTypePage, destinationType)
                    .set(ItemExciseProductCodePage(0), goodsType.code)
                    .set(HowMovementTransportedPage, FixedTransportInstallations)

                )

                GuarantorRequiredPage.guarantorAlwaysRequiredNIToEU() mustBe true
              }
            }

            s"when TransportUnitType is ${FixedTransport.toString}" - {

              "return true" in {

                implicit val dr: DataRequest[_] = dataRequest(
                  request = FakeRequest(),
                  answers = emptyUserAnswers
                    .set(DestinationTypePage, destinationType)
                    .set(ItemExciseProductCodePage(0), goodsType.code)
                    .set(TransportUnitTypePage(0), FixedTransport)
                )

                GuarantorRequiredPage.guarantorAlwaysRequiredNIToEU() mustBe true
              }
            }

            s"when neither MovementTransportType or TransportUnitType are Fixed" - {

              "return true" in {

                implicit val dr: DataRequest[_] = dataRequest(
                  request = FakeRequest(),
                  answers = emptyUserAnswers
                    .set(DestinationTypePage, destinationType)
                    .set(ItemExciseProductCodePage(0), goodsType.code)
                    .set(HowMovementTransportedPage, InlandWaterwayTransport)
                    .set(TransportUnitTypePage(0), Tractor)

                )

                GuarantorRequiredPage.guarantorAlwaysRequiredNIToEU() mustBe true
              }
            }
          }
        }

        s"when GoodsType is NOT Alcohol or Tobacco: ${Energy.code}" - {

          Seq(AirTransport, InlandWaterwayTransport, PostalConsignment, RailTransport, RoadTransport, SeaTransport, Other).foreach { howMovementTransported =>

            s"when MovementTransportType is NOT FixedMovement: ${howMovementTransported.toString}" - {

              "when no TransportUnitType has been selected" - {

                "return true" in {

                  implicit val dr: DataRequest[_] = dataRequest(
                    request = FakeRequest(),
                    answers = emptyUserAnswers
                      .set(DestinationTypePage, destinationType)
                      .set(ItemExciseProductCodePage(0), Energy.code)
                      .set(HowMovementTransportedPage, howMovementTransported)
                  )

                  GuarantorRequiredPage.guarantorAlwaysRequiredNIToEU() mustBe true
                }
              }

              Seq(Container, FixedTransport, Tractor, Trailer, Vehicle).foreach { transportUnitType =>

                s"when ANY TransportUnitType has been selected: ${transportUnitType.toString}" - {

                  "return true" in {

                    implicit val dr: DataRequest[_] = dataRequest(
                      request = FakeRequest(),
                      answers = emptyUserAnswers
                        .set(DestinationTypePage, destinationType)
                        .set(ItemExciseProductCodePage(0), Energy.code)
                        .set(HowMovementTransportedPage, howMovementTransported)
                    )

                    GuarantorRequiredPage.guarantorAlwaysRequiredNIToEU() mustBe true
                  }
                }
              }
            }
          }

          s"when MovementTransportType is ${FixedTransportInstallations.toString}" - {

            Seq(Container, Tractor, Trailer, Vehicle).foreach{ transportUnitType =>

              s"when a NON fixed TransportUnitType is selected: ${transportUnitType.toString}" - {

                "return true" in {

                  implicit val dr: DataRequest[_] = dataRequest(
                    request = FakeRequest(),
                    answers = emptyUserAnswers
                      .set(DestinationTypePage, destinationType)
                      .set(ItemExciseProductCodePage(0), Energy.code)
                      .set(HowMovementTransportedPage, FixedTransportInstallations)
                      .set(TransportUnitTypePage(0), transportUnitType)
                  )

                  GuarantorRequiredPage.guarantorAlwaysRequiredNIToEU() mustBe true
                }
              }
            }

            s"when TransportUnitType is ${FixedTransport.toString}" - {

              "return false" in {

                implicit val dr: DataRequest[_] = dataRequest(
                  request = FakeRequest(),
                  answers = emptyUserAnswers
                    .set(DestinationTypePage, destinationType)
                    .set(ItemExciseProductCodePage(0), Energy.code)
                    .set(HowMovementTransportedPage, FixedTransportInstallations)
                    .set(TransportUnitTypePage(0), FixedTransport)
                )

                GuarantorRequiredPage.guarantorAlwaysRequiredNIToEU() mustBe false
              }
            }

            "when NO TransportUnitType" - {

              "return true" in {

                implicit val dr: DataRequest[_] = dataRequest(
                  request = FakeRequest(),
                  answers = emptyUserAnswers
                    .set(DestinationTypePage, destinationType)
                    .set(ItemExciseProductCodePage(0), Energy.code)
                    .set(HowMovementTransportedPage, FixedTransportInstallations)
                )

                GuarantorRequiredPage.guarantorAlwaysRequiredNIToEU() mustBe false
              }
            }
          }

          s"when MovementTransportType is not answered" - {

            "when no TransportUnitType has been selected" - {

              "return true" in {

                implicit val dr: DataRequest[_] = dataRequest(
                  request = FakeRequest(),
                  answers = emptyUserAnswers
                    .set(DestinationTypePage, destinationType)
                    .set(ItemExciseProductCodePage(0), Energy.code)
                )

                GuarantorRequiredPage.guarantorAlwaysRequiredNIToEU() mustBe false
              }
            }

            Seq(Container, FixedTransport, Tractor, Trailer, Vehicle).foreach { transportUnitType =>

              s"when ANY TransportUnitType has been selected: ${transportUnitType.toString}" - {

                "return true" in {

                  implicit val dr: DataRequest[_] = dataRequest(
                    request = FakeRequest(),
                    answers = emptyUserAnswers
                      .set(DestinationTypePage, destinationType)
                      .set(ItemExciseProductCodePage(0), Energy.code)
                  )

                  GuarantorRequiredPage.guarantorAlwaysRequiredNIToEU() mustBe false
                }
              }
            }
          }
        }
      }
    }
  }
}
