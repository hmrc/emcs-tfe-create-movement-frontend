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
import pages.sections.Section
import pages.sections.consignee.ConsigneeSection
import pages.sections.consignor.ConsignorSection
import pages.sections.destination.DestinationSection
import pages.sections.dispatch.DispatchSection
import pages.sections.documents.DocumentsSection
import pages.sections.exportInformation.ExportInformationSection
import pages.sections.firstTransporter.FirstTransporterSection
import pages.sections.guarantor.GuarantorSection
import pages.sections.importInformation.ImportInformationSection
import pages.sections.info.{DestinationTypePage, DispatchPlacePage, InfoSection}
import pages.sections.items.ItemsSection
import pages.sections.journeyType.JourneyTypeSection
import pages.sections.sad.SadSection
import pages.sections.transportArranger.TransportArrangerSection
import pages.sections.transportUnit.TransportUnitsSection
import play.api.i18n.{Messages, MessagesApi}
import play.api.libs.json.{JsObject, JsPath}
import play.api.test.FakeRequest
import viewmodels.taskList._
import views.ViewUtils.titleNoForm

class DraftMovementHelperSpec extends SpecBase {

  lazy val helper = new DraftMovementHelper()

  Seq(DraftMovementMessages.English).foreach { messagesForLanguage =>
    s"when being rendered in lang code of ${messagesForLanguage.lang.code}" - {
      implicit val msgs: Messages = app.injector.instanceOf[MessagesApi].preferred(Seq(messagesForLanguage.lang))

      def dataRequest(ern: String, userAnswers: UserAnswers) = DataRequest(
        userRequest(FakeRequest(), ern), testDraftId, userAnswers, testMinTraderKnownFacts
      )

      "heading" - {
        "when user answers are valid" - {
          "must return the draftMovement.heading.gbTaxWarehouseTo message" in {
            Seq[(String, MovementScenario)](
              ("GBWK123", UkTaxWarehouse.GB),
              ("GBWK123", UkTaxWarehouse.NI)
            ).foreach {
              case (ern, movementScenario) =>
                implicit val request: DataRequest[_] = dataRequest(ern = ern, userAnswers = emptyUserAnswers.set(DestinationTypePage, movementScenario))

                val input1 = msgs(s"draftMovement.heading.$movementScenario")

                helper.heading mustBe messagesForLanguage.headingGbTaxWarehouseTo(input1)
                titleNoForm(helper.heading) mustBe messagesForLanguage.titleGbTaxWarehouseTo(input1)
            }
          }
          "must return the draftMovement.heading.dispatchPlaceTo message" in {
            Seq[(String, DispatchPlace, MovementScenario)](
              ("XIWK123", GreatBritain, UkTaxWarehouse.GB),
              ("XIWK123", GreatBritain, UkTaxWarehouse.NI),
              ("XIWK123", GreatBritain, EuTaxWarehouse),
              ("XIWK123", GreatBritain, RegisteredConsignee),
              ("XIWK123", GreatBritain, TemporaryRegisteredConsignee),
              ("XIWK123", GreatBritain, ExemptedOrganisation),
              ("XIWK123", GreatBritain, UnknownDestination),
              ("XIWK123", GreatBritain, DirectDelivery),
              ("XIWK123", NorthernIreland, UkTaxWarehouse.GB),
              ("XIWK123", NorthernIreland, UkTaxWarehouse.NI),
              ("XIWK123", NorthernIreland, EuTaxWarehouse),
              ("XIWK123", NorthernIreland, RegisteredConsignee),
              ("XIWK123", NorthernIreland, TemporaryRegisteredConsignee),
              ("XIPA123", NorthernIreland, CertifiedConsignee),
              ("XIPC123", NorthernIreland, TemporaryCertifiedConsignee),
              ("XIWK123", NorthernIreland, ExemptedOrganisation),
              ("XIWK123", NorthernIreland, UnknownDestination),
              ("XIWK123", NorthernIreland, DirectDelivery),
            ).foreach {
              case (ern, dispatchPlace, movementScenario) =>
                implicit val request: DataRequest[_] =
                  dataRequest(ern = ern, userAnswers = emptyUserAnswers.set(DispatchPlacePage, dispatchPlace).set(DestinationTypePage, movementScenario))

                val input1 = msgs(s"dispatchPlace.$dispatchPlace")
                val input2 = msgs(Seq(s"draftMovement.heading.$movementScenario", s"destinationType.$movementScenario"))

                helper.heading mustBe messagesForLanguage.headingDispatchPlaceTo(input1, input2)
                titleNoForm(helper.heading) mustBe messagesForLanguage.titleDispatchPlaceTo(input1, input2)
            }
          }
          "must return the draftMovement.heading.importFor message" in {
            Seq[String](
              "GBRC123",
              "XIRC123",
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
            implicit val request: DataRequest[_] = dataRequest(ern = "XIWK123", userAnswers = emptyUserAnswers.set(DestinationTypePage, UkTaxWarehouse.GB))

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
                Some(InfoSection),
                Some(NotStarted)
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
          Some(ImportInformationSection),
          Some(NotStarted)
        )

        def dispatchRow(ern: String) = TaskListSectionRow(
          messagesForLanguage.dispatch,
          "dispatch",
          Some(controllers.sections.dispatch.routes.DispatchIndexController.onPageLoad(ern, testDraftId).url),
          Some(DispatchSection),
          Some(NotStarted)
        )

        def consigneeRow(ern: String) = TaskListSectionRow(
          messagesForLanguage.consignee,
          "consignee",
          Some(controllers.sections.consignee.routes.ConsigneeIndexController.onPageLoad(ern, testDraftId).url),
          Some(ConsigneeSection),
          Some(NotStarted)
        )

        def destinationRow(ern: String) = TaskListSectionRow(
          messagesForLanguage.destination,
          "destination",
          Some(controllers.sections.destination.routes.DestinationIndexController.onPageLoad(ern, testDraftId).url),
          Some(DestinationSection),
          Some(NotStarted)
        )

        def exportRow(ern: String) = TaskListSectionRow(
          messagesForLanguage.export,
          "export",
          Some(controllers.sections.exportInformation.routes.ExportInformationIndexController.onPageLoad(ern, testDraftId).url),
          Some(ExportInformationSection),
          Some(NotStarted)
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
            Some(ConsignorSection),
            Some(NotStarted)
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
              UkTaxWarehouse.GB,
              UkTaxWarehouse.NI,
              EuTaxWarehouse,
              RegisteredConsignee,
              TemporaryRegisteredConsignee,
              CertifiedConsignee,
              TemporaryCertifiedConsignee,
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
              UkTaxWarehouse.GB,
              UkTaxWarehouse.NI,
              EuTaxWarehouse,
              RegisteredConsignee,
              TemporaryRegisteredConsignee,
              CertifiedConsignee,
              TemporaryCertifiedConsignee,
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

      "guarantorSection" - {
        "should render the Guarantor section" in {
          implicit val request: DataRequest[_] = dataRequest(ern = testErn, userAnswers = emptyUserAnswers)
          helper.guarantorSection mustBe TaskListSection(
            messagesForLanguage.guarantorSectionHeading,
            Seq(
              TaskListSectionRow(
                messagesForLanguage.guarantor,
                "guarantor",
                Some(controllers.sections.guarantor.routes.GuarantorIndexController.onPageLoad(testErn, testDraftId).url),
                Some(GuarantorSection),
                Some(NotStarted)
              )
            )
          )
        }
      }

      "transportSection" - {

        "should have the correct heading" in {
          implicit val request: DataRequest[_] = dataRequest(ern = testErn, userAnswers = emptyUserAnswers)
          helper.transportSection.sectionHeading mustBe messagesForLanguage.transportSectionHeading
        }
        "should render the journeyType row" in {
          implicit val request: DataRequest[_] = dataRequest(ern = testErn, userAnswers = emptyUserAnswers)
          helper.transportSection.rows must contain(TaskListSectionRow(
            messagesForLanguage.journeyType,
            "journeyType",
            Some(controllers.sections.journeyType.routes.JourneyTypeIndexController.onPageLoad(testErn, testDraftId).url),
            Some(JourneyTypeSection),
            Some(NotStarted)
          ))
        }
        "should render the transportArranger row" in {
          implicit val request: DataRequest[_] = dataRequest(ern = testErn, userAnswers = emptyUserAnswers)
          helper.transportSection.rows must contain(TaskListSectionRow(
            messagesForLanguage.transportArranger,
            "transportArranger",
            Some(controllers.sections.transportArranger.routes.TransportArrangerIndexController.onPageLoad(testErn, testDraftId).url),
            Some(TransportArrangerSection),
            Some(NotStarted)
          ))
        }
        "should render the firstTransporter row" in {
          implicit val request: DataRequest[_] = dataRequest(ern = testErn, userAnswers = emptyUserAnswers)
          helper.transportSection.rows must contain(TaskListSectionRow(
            messagesForLanguage.firstTransporter,
            "firstTransporter",
            Some(controllers.sections.firstTransporter.routes.FirstTransporterIndexController.onPageLoad(testErn, testDraftId).url),
            Some(FirstTransporterSection),
            Some(NotStarted)
          ))
        }
        "should render the units row" in {
          implicit val request: DataRequest[_] = dataRequest(ern = testErn, userAnswers = emptyUserAnswers)
          helper.transportSection.rows must contain(TaskListSectionRow(
            messagesForLanguage.units,
            "units",
            Some(controllers.sections.transportUnit.routes.TransportUnitIndexController.onPageLoad(testErn, testDraftId).url),
            Some(TransportUnitsSection),
            Some(NotStarted)
          ))
        }
      }

      "itemsSection" - {
        "should render the Items section" in {
          implicit val request: DataRequest[_] = dataRequest(ern = testErn, userAnswers = emptyUserAnswers)
          helper.itemsSection mustBe TaskListSection(
            messagesForLanguage.itemsSectionHeading,
            Seq(
              TaskListSectionRow(
                messagesForLanguage.items,
                "items",
                Some(controllers.sections.items.routes.ItemsIndexController.onPageLoad(testErn, testDraftId).url),
                Some(ItemsSection),
                Some(NotStarted)
              )
            )
          )
        }
      }

      "documentsSection" - {
        def sadRow(ern: String) = TaskListSectionRow(
          messagesForLanguage.sad,
          "sad",
          Some(controllers.sections.sad.routes.SadIndexController.onPageLoad(ern, testDraftId).url),
          Some(SadSection),
          Some(NotStarted)
        )

        "should have the correct heading" in {
          implicit val request: DataRequest[_] = dataRequest(ern = testErn, userAnswers = emptyUserAnswers)
          helper.documentsSection.sectionHeading mustBe messagesForLanguage.documentsSectionHeading
        }
        "should render the sad row" - {
          "when UserType is valid" in {
            Seq("GBRC123", "XIRC123").foreach {
              ern =>
                implicit val request: DataRequest[_] = dataRequest(ern = ern, userAnswers = emptyUserAnswers)
                helper.documentsSection.rows must contain(sadRow(ern))
            }
          }
        }
        "should not render the sad row" - {
          "when UserType is invalid" in {
            Seq("GBWK123", "XIWK123").foreach {
              ern =>
                implicit val request: DataRequest[_] = dataRequest(ern = ern, userAnswers = emptyUserAnswers)
                helper.documentsSection.rows must not contain sadRow(ern)
            }
          }
        }
        "should render the documents row" in {
          implicit val request: DataRequest[_] = dataRequest(ern = testErn, userAnswers = emptyUserAnswers)
          helper.documentsSection.rows must contain(TaskListSectionRow(
            messagesForLanguage.documents,
            "documents",
            Some(controllers.sections.documents.routes.DocumentsIndexController.onPageLoad(testErn, testDraftId).url),
            Some(DocumentsSection),
            Some(NotStarted)
          ))
        }
      }

      "submitSection" - {
        implicit val request: DataRequest[_] = dataRequest(ern = testErn, userAnswers = emptyUserAnswers)

        "must return a TaskList with a link" - {
          "when all sections are completed" in {
            object TestSection extends Section[JsObject] {
              override def status(implicit request: DataRequest[_]): TaskListStatus = Completed

              override def canBeCompletedForTraderAndDestinationType(implicit request: DataRequest[_]): Boolean = true

              override val path: JsPath = JsPath
            }

            val testSections: Seq[TaskListSection] = Seq(
              TaskListSection(
                sectionHeading = "testHeading1",
                rows = Seq(
                  TaskListSectionRow(
                    taskName = "testName1",
                    id = "testId1",
                    link = None,
                    section = Some(TestSection),
                    status = Some(Completed)
                  ),
                  TaskListSectionRow(
                    taskName = "testName2",
                    id = "testId2",
                    link = None,
                    section = Some(TestSection),
                    status = Some(Completed)
                  )
                )
              ),
              TaskListSection(
                sectionHeading = "testHeading2",
                rows = Seq(
                  TaskListSectionRow(
                    taskName = "testName3",
                    id = "testId3",
                    link = None,
                    section = Some(TestSection),
                    status = Some(Completed)
                  ),
                  TaskListSectionRow(
                    taskName = "testName4",
                    id = "testId4",
                    link = None,
                    section = Some(TestSection),
                    status = Some(Completed)
                  )
                )
              )
            )

            helper.submitSection(testSections) mustBe TaskListSection(
              sectionHeading = messagesForLanguage.submitSectionHeading,
              rows = Seq(
                TaskListSectionRow(
                  taskName = messagesForLanguage.reviewAndSubmit,
                  id = "submit",
                  link = Some(controllers.routes.DeclarationController.onPageLoad(request.ern, request.draftId).url),
                  section = None,
                  status = None
                )
              )
            )
          }
        }

        "must return a TaskList without a link" - {
          "when some sections are incomplete" in {
            val testSections: Seq[TaskListSection] = Seq(
              TaskListSection(
                sectionHeading = "testHeading1",
                rows = Seq(
                  TaskListSectionRow(
                    taskName = "testName1",
                    id = "testId1",
                    link = None,
                    section = None,
                    status = Some(InProgress)
                  ),
                  TaskListSectionRow(
                    taskName = "testName2",
                    id = "testId2",
                    link = None,
                    section = None,
                    status = Some(NotStarted)
                  )
                )
              ),
              TaskListSection(
                sectionHeading = "testHeading2",
                rows = Seq(
                  TaskListSectionRow(
                    taskName = "testName3",
                    id = "testId3",
                    link = None,
                    section = None,
                    status = Some(Completed)
                  ),
                  TaskListSectionRow(
                    taskName = "testName4",
                    id = "testId4",
                    link = None,
                    section = None,
                    status = Some(Completed)
                  )
                )
              )
            )

            helper.submitSection(testSections) mustBe TaskListSection(
              sectionHeading = messagesForLanguage.submitSectionHeading,
              rows = Seq(
                TaskListSectionRow(
                  taskName = messagesForLanguage.reviewAndSubmit,
                  id = "submit",
                  link = None,
                  section = None,
                  status = Some(CannotStartYet)
                )
              )
            )
          }
          "when all sections are incomplete" in {
            val testSections: Seq[TaskListSection] = Seq(
              TaskListSection(
                sectionHeading = "testHeading1",
                rows = Seq(
                  TaskListSectionRow(
                    taskName = "testName1",
                    id = "testId1",
                    link = None,
                    section = None,
                    status = Some(NotStarted)
                  ),
                  TaskListSectionRow(
                    taskName = "testName2",
                    id = "testId2",
                    link = None,
                    section = None,
                    status = Some(NotStarted)
                  )
                )
              ),
              TaskListSection(
                sectionHeading = "testHeading2",
                rows = Seq(
                  TaskListSectionRow(
                    taskName = "testName3",
                    id = "testId3",
                    link = None,
                    section = None,
                    status = Some(NotStarted)
                  ),
                  TaskListSectionRow(
                    taskName = "testName4",
                    id = "testId4",
                    link = None,
                    section = None,
                    status = Some(NotStarted)
                  )
                )
              )
            )

            helper.submitSection(testSections) mustBe TaskListSection(
              sectionHeading = messagesForLanguage.submitSectionHeading,
              rows = Seq(
                TaskListSectionRow(
                  taskName = messagesForLanguage.reviewAndSubmit,
                  id = "submit",
                  link = None,
                  section = None,
                  status = Some(CannotStartYet)
                )
              )
            )
          }
          "when all sections are empty" in {
            val testSections: Seq[TaskListSection] = Seq(
              TaskListSection(
                sectionHeading = "testHeading1",
                rows = Seq()
              ),
              TaskListSection(
                sectionHeading = "testHeading2",
                rows = Seq()
              )
            )


            helper.submitSection(testSections) mustBe TaskListSection(
              sectionHeading = messagesForLanguage.submitSectionHeading,
              rows = Seq(
                TaskListSectionRow(
                  taskName = messagesForLanguage.reviewAndSubmit,
                  id = "submit",
                  link = None,
                  section = None,
                  status = Some(CannotStartYet)
                )
              )
            )
          }
          "when no sections are provided" in {
            val testSections: Seq[TaskListSection] = Seq()

            helper.submitSection(testSections) mustBe TaskListSection(
              sectionHeading = messagesForLanguage.submitSectionHeading,
              rows = Seq(
                TaskListSectionRow(
                  taskName = messagesForLanguage.reviewAndSubmit,
                  id = "submit",
                  link = None,
                  section = None,
                  status = Some(CannotStartYet)
                )
              )
            )
          }
        }

        "must filter out sections which cannot be filled out for that user/destination type, even ifSome(Completed)" in {
          object TestSection extends Section[JsObject] {
            override def status(implicit request: DataRequest[_]): TaskListStatus = Completed

            override def canBeCompletedForTraderAndDestinationType(implicit request: DataRequest[_]): Boolean = false

            override val path: JsPath = JsPath
          }

          val testSections: Seq[TaskListSection] = Seq(
            TaskListSection(
              sectionHeading = "testHeading",
              rows = Seq(
                TaskListSectionRow(
                  taskName = "testName",
                  id = "testid",
                  None,
                  Some(TestSection),
                  Some(Completed)
                )
              )
            )
          )

          helper.submitSection(testSections) mustBe TaskListSection(
            sectionHeading = messagesForLanguage.submitSectionHeading,
            rows = Seq(
              TaskListSectionRow(
                taskName = messagesForLanguage.reviewAndSubmit,
                id = "submit",
                link = None,
                section = None,
                status = Some(CannotStartYet)
              )
            )
          )
        }
      }

      "sections" - {
        "should return all sections" in {
          implicit val request: DataRequest[_] = dataRequest(ern = testErn, userAnswers = emptyUserAnswers)
          helper.sections.map(_.sectionHeading) mustBe
            Seq(
              messagesForLanguage.movementSectionHeading,
              messagesForLanguage.deliverySectionHeading,
              messagesForLanguage.guarantorSectionHeading,
              messagesForLanguage.transportSectionHeading,
              messagesForLanguage.itemsSectionHeading,
              messagesForLanguage.documentsSectionHeading,
              messagesForLanguage.submitSectionHeading,
            )
        }
      }
    }
  }
}
