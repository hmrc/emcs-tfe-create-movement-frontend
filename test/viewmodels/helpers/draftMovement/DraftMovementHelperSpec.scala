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

package viewmodels.helpers.draftMovement

import base.SpecBase
import fixtures.messages.DraftMovementMessages
import models._
import models.requests.DataRequest
import models.response.{InvalidUserTypeException, MissingMandatoryPage}
import models.sections.info.DispatchPlace
import models.sections.info.DispatchPlace.{GreatBritain, NorthernIreland}
import models.sections.info.movementScenario.MovementScenario
import models.sections.info.movementScenario.MovementScenario._
import pages.sections.info.{DestinationTypePage, DispatchPlacePage}
import play.api.test.FakeRequest
import viewmodels.taskList.{NotStarted, TaskListSection, TaskListSectionRow}
import views.ViewUtils.titleNoForm
import views.html.components.taskList

class DraftMovementHelperSpec extends SpecBase {
  lazy val app = applicationBuilder().build()

  lazy val taskList = app.injector.instanceOf[taskList]
  lazy val helper = new DraftMovementHelper(taskList)

  Seq(DraftMovementMessages.English).foreach { messagesForLanguage =>
    s"when being rendered in lang code of ${messagesForLanguage.lang.code}" - {
      lazy val app = applicationBuilder().build()
      implicit lazy val msgs = messages(app, messagesForLanguage.lang)

      def dataRequest(ern: String, userAnswers: UserAnswers) = DataRequest(
        userRequest(FakeRequest(), ern), testDraftId, userAnswers, testMinTraderKnownFacts
      )

      "heading" - {
        "when user answers are valid" - {
          "must return the draftMovement.heading.gbTaxWarehouseTo message" in {
            Seq[(String, MovementScenario)](
              ("GBWK123", GbTaxWarehouse)
            ).foreach {
              case (ern, movementScenario) =>
                implicit val request: DataRequest[_] = dataRequest(ern = ern, userAnswers = emptyUserAnswers.set(DestinationTypePage, movementScenario))

                val input1 = msgs(s"destinationType.$movementScenario")

                helper.heading mustBe messagesForLanguage.headingGbTaxWarehouseTo(input1)
                titleNoForm(helper.heading) mustBe messagesForLanguage.titleGbTaxWarehouseTo(input1)
            }
          }
          "must return the draftMovement.heading.dispatchPlaceTo message" in {
            Seq[(String, DispatchPlace, MovementScenario)](
              ("XIWK123", GreatBritain, GbTaxWarehouse),
              ("XIWK123", GreatBritain, EuTaxWarehouse),
              ("XIWK123", GreatBritain, RegisteredConsignee),
              ("XIWK123", GreatBritain, TemporaryRegisteredConsignee),
              ("XIWK123", GreatBritain, ExemptedOrganisation),
              ("XIWK123", GreatBritain, UnknownDestination),
              ("XIWK123", GreatBritain, DirectDelivery),
              ("XIWK123", NorthernIreland, GbTaxWarehouse),
              ("XIWK123", NorthernIreland, EuTaxWarehouse),
              ("XIWK123", NorthernIreland, RegisteredConsignee),
              ("XIWK123", NorthernIreland, TemporaryRegisteredConsignee),
              ("XIWK123", NorthernIreland, ExemptedOrganisation),
              ("XIWK123", NorthernIreland, UnknownDestination),
              ("XIWK123", NorthernIreland, DirectDelivery),
            ).foreach {
              case (ern, dispatchPlace, movementScenario) =>
                implicit val request: DataRequest[_] =
                  dataRequest(ern = ern, userAnswers = emptyUserAnswers.set(DispatchPlacePage, dispatchPlace).set(DestinationTypePage, movementScenario))

                val input1 = msgs(s"dispatchPlace.$dispatchPlace")
                val input2 = msgs(s"destinationType.$movementScenario")

                helper.heading mustBe messagesForLanguage.headingDispatchPlaceTo(input1, input2)
                titleNoForm(helper.heading) mustBe messagesForLanguage.titleDispatchPlaceTo(input1, input2)
            }
          }
          "must return the draftMovement.heading.importFor message" in {
            Seq[String](
              "GBRC123",
              "XIRC123"
            ).foreach(
              ern =>
                MovementScenario.values.foreach {
                  movementScenario =>
                    implicit val request: DataRequest[_] = dataRequest(ern = ern, userAnswers = emptyUserAnswers.set(DestinationTypePage, movementScenario))

                    val input1 = msgs(s"destinationType.$movementScenario")

                    helper.heading mustBe messagesForLanguage.headingImportFor(input1)
                    titleNoForm(helper.heading) mustBe messagesForLanguage.titleImportFor(input1)
                }
            )
          }
          "must return the destination type" in {
            Seq[(String, MovementScenario)](
              ("GBWK123", ExportWithCustomsDeclarationLodgedInTheUk),
              ("GBWK123", ExportWithCustomsDeclarationLodgedInTheEu),
              ("XIWK123", ExportWithCustomsDeclarationLodgedInTheUk),
              ("XIWK123", ExportWithCustomsDeclarationLodgedInTheEu)
            ).foreach {
              case (ern, movementScenario) =>
                implicit val request: DataRequest[_] = dataRequest(ern = ern, userAnswers = emptyUserAnswers.set(DestinationTypePage, movementScenario))

                val input1 = msgs(s"destinationType.$movementScenario")

                helper.heading mustBe input1
                titleNoForm(helper.heading) mustBe messagesForLanguage.titleHelper(input1)
            }
          }
        }
        "when inputs are invalid" - {
          "must throw an error when the ERN is invalid" in {
            implicit val request: DataRequest[_] = dataRequest(ern = "GB00123", userAnswers = emptyUserAnswers)

            val response = intercept[InvalidUserTypeException](helper.heading)

            response.message mustBe s"[DraftMovementHelper][heading] invalid UserType and destinationType combination for CAM journey: $GreatBritainWarehouse | $None"
          }
          "must throw an error when the ERN/destinationType combo is invalid" in {
            implicit val request: DataRequest[_] = dataRequest(ern = "GBWK123", userAnswers = emptyUserAnswers.set(DestinationTypePage, UnknownDestination))

            val response = intercept[InvalidUserTypeException](helper.heading)

            response.message mustBe s"[DraftMovementHelper][heading] invalid UserType and destinationType combination for CAM journey: $GreatBritainWarehouseKeeper | ${Some(UnknownDestination)}"
          }
          "must throw an error when the ERN is XIWK and DispatchPlace is missing" in {
            implicit val request: DataRequest[_] = dataRequest(ern = "XIWK123", userAnswers = emptyUserAnswers.set(DestinationTypePage, GbTaxWarehouse))

            val response = intercept[MissingMandatoryPage](helper.heading)

            response.message mustBe s"[heading] Missing mandatory page $DispatchPlacePage for $NorthernIrelandWarehouseKeeper"
          }
        }
      }

      "movementSection" - {
        "should render the Movement details section" in {
          implicit val request: DataRequest[_] = dataRequest(ern = testErn, userAnswers = emptyUserAnswers)
          helper.movementSection mustBe TaskListSection(
            messagesForLanguage.movementSectionHeading,
            Seq(
              TaskListSectionRow(
                messagesForLanguage.movementDetails,
                "movementDetails",
                Some(controllers.sections.info.routes.InfoIndexController.onPageLoad(testErn, testDraftId).url),
                NotStarted
              )
            )
          )
        }
      }

      "deliverySection" - {
        def importRow(ern: String) = TaskListSectionRow(
          messagesForLanguage.`import`,
          "import",
          Some(controllers.sections.importInformation.routes.ImportInformationIndexController.onPageLoad(ern, testDraftId).url),
          NotStarted
        )

        def dispatchRow(ern: String) = TaskListSectionRow(
          messagesForLanguage.dispatch,
          "dispatch",
          Some(controllers.sections.dispatch.routes.DispatchIndexController.onPageLoad(ern, testDraftId).url),
          NotStarted
        )

        def consigneeRow(ern: String) = TaskListSectionRow(
          messagesForLanguage.consignee,
          "consignee",
          Some(controllers.sections.consignee.routes.ConsigneeIndexController.onPageLoad(ern, testDraftId).url),
          NotStarted
        )

        def destinationRow(ern: String) = TaskListSectionRow(
          messagesForLanguage.destination,
          "destination",
          Some(controllers.sections.destination.routes.DestinationIndexController.onPageLoad(ern, testDraftId).url),
          NotStarted
        )

        def exportRow(ern: String) = TaskListSectionRow(
          messagesForLanguage.export,
          "export",
          Some(controllers.sections.exportInformation.routes.ExportInformationIndexController.onPageLoad(ern, testDraftId).url),
          NotStarted
        )

        "should have the correct heading" in {
          implicit val request: DataRequest[_] = dataRequest(ern = testErn, userAnswers = emptyUserAnswers)
          helper.deliverySection.sectionHeading mustBe messagesForLanguage.deliverySectionHeading
        }
        "should render the consignor row" in {
          implicit val request: DataRequest[_] = dataRequest(ern = testErn, userAnswers = emptyUserAnswers)
          helper.deliverySection.rows must contain(TaskListSectionRow(
            messagesForLanguage.consignor,
            "consignor",
            Some(controllers.sections.consignor.routes.ConsignorIndexController.onPageLoad(testErn, testDraftId).url),
            NotStarted
          ))
        }
        "should render the import row" - {
          "when UserType is valid" in {
            Seq("GBRC123", "XIRC123").foreach {
              ern =>
                implicit val request: DataRequest[_] = dataRequest(ern = ern, userAnswers = emptyUserAnswers)
                helper.deliverySection.rows must contain(importRow(ern))
            }
          }
        }
        "should not render the import row" - {
          "when UserType is invalid" in {
            Seq("GBWK123", "XIWK123").foreach {
              ern =>
                implicit val request: DataRequest[_] = dataRequest(ern = ern, userAnswers = emptyUserAnswers)
                helper.deliverySection.rows must not contain importRow(ern)
            }
          }
        }
        "should render the dispatch row" - {
          "when UserType is valid" in {
            Seq("GBWK123", "XIWK123").foreach {
              ern =>
                implicit val request: DataRequest[_] = dataRequest(ern = ern, userAnswers = emptyUserAnswers)
                helper.deliverySection.rows must contain(dispatchRow(ern))
            }
          }
        }
        "should not render the dispatch row" - {
          "when UserType is invalid" in {
            Seq("GBRC123", "XIRC123").foreach {
              ern =>
                implicit val request: DataRequest[_] = dataRequest(ern = ern, userAnswers = emptyUserAnswers)
                helper.deliverySection.rows must not contain dispatchRow(ern)
            }
          }
        }
        "should render the consignee row" - {
          "when MovementScenario is valid" in {
            MovementScenario.values.filterNot(_ == UnknownDestination).foreach {
              scenario =>
                implicit val request: DataRequest[_] = dataRequest(ern = testErn, userAnswers = emptyUserAnswers.set(DestinationTypePage, scenario))
                helper.deliverySection.rows must contain(consigneeRow(testErn))
            }
          }
        }
        "should not render the consignee row" - {
          "when MovementScenario is invalid" in {
            Seq(UnknownDestination).foreach {
              scenario =>
                implicit val request: DataRequest[_] = dataRequest(ern = testErn, userAnswers = emptyUserAnswers.set(DestinationTypePage, scenario))
                helper.deliverySection.rows must not contain consigneeRow(testErn)
            }
          }
          "when MovementScenario is missing" in {
            implicit val request: DataRequest[_] = dataRequest(ern = testErn, userAnswers = emptyUserAnswers)
            helper.deliverySection.rows must not contain consigneeRow(testErn)
          }
        }
        "should render the destination row" - {
          "when MovementScenario is valid" in {
            Seq(
              GbTaxWarehouse,
              EuTaxWarehouse,
              RegisteredConsignee,
              TemporaryRegisteredConsignee,
              ExemptedOrganisation,
              DirectDelivery
            ).foreach {
              scenario =>
                implicit val request: DataRequest[_] = dataRequest(ern = testErn, userAnswers = emptyUserAnswers.set(DestinationTypePage, scenario))
                helper.deliverySection.rows must contain(destinationRow(testErn))
            }
          }
        }
        "should not render the destination row" - {
          "when MovementScenario is invalid" in {
            MovementScenario.values.filterNot(Seq(
              GbTaxWarehouse,
              EuTaxWarehouse,
              RegisteredConsignee,
              TemporaryRegisteredConsignee,
              ExemptedOrganisation,
              DirectDelivery
            ).contains).foreach {
              scenario =>
                implicit val request: DataRequest[_] = dataRequest(ern = testErn, userAnswers = emptyUserAnswers.set(DestinationTypePage, scenario))
                helper.deliverySection.rows must not contain destinationRow(testErn)
            }
          }
          "when MovementScenario is missing" in {
            implicit val request: DataRequest[_] = dataRequest(ern = testErn, userAnswers = emptyUserAnswers)
            helper.deliverySection.rows must not contain destinationRow(testErn)
          }
        }
        "should render the export row" - {
          "when MovementScenario is valid" in {
            Seq(
              ExportWithCustomsDeclarationLodgedInTheUk,
              ExportWithCustomsDeclarationLodgedInTheEu
            ).foreach {
              scenario =>
                implicit val request: DataRequest[_] = dataRequest(ern = testErn, userAnswers = emptyUserAnswers.set(DestinationTypePage, scenario))
                helper.deliverySection.rows must contain(exportRow(testErn))
            }
          }
        }
        "should not render the export row" - {
          "when MovementScenario is invalid" in {
            MovementScenario.values.filterNot(Seq(
              ExportWithCustomsDeclarationLodgedInTheUk,
              ExportWithCustomsDeclarationLodgedInTheEu
            ).contains).foreach {
              scenario =>
                implicit val request: DataRequest[_] = dataRequest(ern = testErn, userAnswers = emptyUserAnswers.set(DestinationTypePage, scenario))
                helper.deliverySection.rows must not contain exportRow(testErn)
            }
          }
          "when MovementScenario is missing" in {
            implicit val request: DataRequest[_] = dataRequest(ern = testErn, userAnswers = emptyUserAnswers)
            helper.deliverySection.rows must not contain exportRow(testErn)
          }
        }
      }
    }
  }
}