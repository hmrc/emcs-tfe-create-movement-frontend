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
import play.twirl.api.{Html, HtmlFormat}
import uk.gov.hmrc.govukfrontend.views.Aliases.HtmlContent
import viewmodels.taskList._
import views.ViewUtils.titleNoForm
import views.html.components.{list, p}

//scalastyle:off magic.number
class DraftMovementHelperSpec extends SpecBase {

  lazy val list: list = app.injector.instanceOf[list]
  lazy val p: p = app.injector.instanceOf[p]
  lazy val helper: DraftMovementHelper = new DraftMovementHelper(list, p)

  Seq(DraftMovementMessages.English).foreach { messagesForLanguage =>
    s"when being rendered in lang code of ${messagesForLanguage.lang.code}" - {
      implicit val msgs: Messages = app.injector.instanceOf[MessagesApi].preferred(Seq(messagesForLanguage.lang))

      def dataRequest(ern: String, userAnswers: UserAnswers) = DataRequest(
        userRequest(FakeRequest(), ern), testDraftId, userAnswers, Some(testMinTraderKnownFacts)
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

                    val input1 = if (Seq(EuTaxWarehouse, UkTaxWarehouse.GB, UkTaxWarehouse.NI).contains(movementScenario)) {
                      msgs(s"draftMovement.heading.$movementScenario")
                    } else {
                      msgs(s"destinationType.$movementScenario")
                    }

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

      "validationFailureContent" - {
        "when errorType is 12 or 13" - {
          "must return the correct content for a validation failure" in {
            Seq(12, 13).foreach {
              errorType =>
                val failure = MovementValidationFailure(Some(errorType), Some("This is an error. Please amend your entry and resubmit."))
                val result = helper.validationFailureContent(Seq(failure))
                result mustBe HtmlContent(HtmlFormat.fill(Seq(
                  p("govuk-notification-banner__heading")(Html(messagesForLanguage.notificationBannerValidationFailuresContent)),
                  list(Seq(p()(Html("This is an error."))))
                )))
            }
          }
        }

        "when errorType is not 12 or 13" - {
          "must return the correct content for a validation failure" in {
            Seq(14, 9999, 123456).foreach {
              errorType =>
                val failure = MovementValidationFailure(Some(errorType), Some("This is an error. Please amend your entry and resubmit."))
                val result = helper.validationFailureContent(Seq(failure))
                result mustBe HtmlContent(HtmlFormat.fill(Seq(
                  p("govuk-notification-banner__heading")(Html(messagesForLanguage.notificationBannerValidationFailuresContent)),
                  list(Seq(p()(Html(s"errors.validation.notificationBanner.$errorType.content"))))
                )))
            }
          }
        }

        "when errorType is missing" - {
          "must return an empty list" in {
            val failure = MovementValidationFailure(None, Some("This is an error. Please amend your entry and resubmit."))
            val result = helper.validationFailureContent(Seq(failure))
            result mustBe HtmlContent(HtmlFormat.fill(Seq(
              p("govuk-notification-banner__heading")(Html(messagesForLanguage.notificationBannerValidationFailuresContent)),
              list(Seq())
            )))
          }
        }

        "when errorReason is missing" - {
          "for errorType 12 or 13" - {
            "must return an empty list" in {
              Seq(12, 13).foreach {
                errorType =>
                  val failure = MovementValidationFailure(Some(errorType), None)
                  val result = helper.validationFailureContent(Seq(failure))
                  result mustBe HtmlContent(HtmlFormat.fill(Seq(
                    p("govuk-notification-banner__heading")(Html(messagesForLanguage.notificationBannerValidationFailuresContent)),
                    list(Seq())
                  )))
              }
            }
          }

          "for other errorTypes" - {
            "must return a non-empty list" in {
              Seq(14, 9999, 123456).foreach {
                errorType =>
                  val failure = MovementValidationFailure(Some(errorType), None)
                  val result = helper.validationFailureContent(Seq(failure))
                  result mustBe HtmlContent(HtmlFormat.fill(Seq(
                    p("govuk-notification-banner__heading")(Html(messagesForLanguage.notificationBannerValidationFailuresContent)),
                    list(Seq(p()(Html(s"errors.validation.notificationBanner.$errorType.content"))))
                  )))
              }
            }
          }
        }

        "for all valid errors" - {
          "must return the correct content" in {
            val validationFailures = Seq(
              MovementValidationFailure(Some(8033), Some("beans")),
              MovementValidationFailure(Some(8034), Some("beans")),
              MovementValidationFailure(Some(8035), Some("beans")),
              MovementValidationFailure(Some(8036), Some("beans")),
              MovementValidationFailure(Some(8037), Some("beans")),
              MovementValidationFailure(Some(8038), Some("beans")),
              MovementValidationFailure(Some(8039), Some("beans")),
              MovementValidationFailure(Some(8040), Some("beans")),
              MovementValidationFailure(Some(8041), Some("beans")),
              MovementValidationFailure(Some(8042), Some("beans")),
              MovementValidationFailure(Some(8043), Some("beans")),
              MovementValidationFailure(Some(8130), Some("beans")),
              MovementValidationFailure(Some(8712), Some("beans")),
              MovementValidationFailure(Some(8044), Some("beans")),
              MovementValidationFailure(Some(8714), Some("beans")),
              MovementValidationFailure(Some(8715), Some("beans")),
              MovementValidationFailure(Some(8045), Some("beans")),
              MovementValidationFailure(Some(12), Some("The consignee trader information must not be present where the destination is unknown. Please amend your entry and resubmit.")),
              MovementValidationFailure(Some(8046), Some("beans")),
              MovementValidationFailure(Some(8047), Some("beans")),
              MovementValidationFailure(Some(8048), Some("beans")),
              MovementValidationFailure(Some(8550), Some("beans")),
              MovementValidationFailure(Some(8602), Some("beans")),
              MovementValidationFailure(Some(8551), Some("beans")),
              MovementValidationFailure(Some(8610), Some("beans")),
              MovementValidationFailure(Some(8611), Some("beans")),
              MovementValidationFailure(Some(8612), Some("beans")),
              MovementValidationFailure(Some(8613), Some("beans")),
              MovementValidationFailure(Some(8163), Some("beans")),
              MovementValidationFailure(Some(8552), Some("beans")),
              MovementValidationFailure(Some(8553), Some("beans")),
              MovementValidationFailure(Some(8716), Some("beans")),
              MovementValidationFailure(Some(8717), Some("beans")),
              MovementValidationFailure(Some(8614), Some("beans")),
              MovementValidationFailure(Some(8615), Some("beans")),
              MovementValidationFailure(Some(8131), Some("beans")),
              MovementValidationFailure(Some(12), Some("The place of dispatch trader information must not be present where the origin type code is 'Import'. Please amend your entry and resubmit.")),
              MovementValidationFailure(Some(8616), Some("beans")),
              MovementValidationFailure(Some(8617), Some("beans")),
              MovementValidationFailure(Some(8718), Some("beans")),
              MovementValidationFailure(Some(8618), Some("beans")),
              MovementValidationFailure(Some(8619), Some("beans")),
              MovementValidationFailure(Some(8719), Some("beans")),
              MovementValidationFailure(Some(8720), Some("beans")),
              MovementValidationFailure(Some(8721), Some("beans")),
              MovementValidationFailure(Some(8722), Some("beans")),
              MovementValidationFailure(Some(12), Some("The dispatch import office must not be present where the origin type code is 'Tax Warehouse'. Please amend your entry and resubmit.")),
              MovementValidationFailure(Some(8700), Some("beans")),
              MovementValidationFailure(Some(8701), Some("beans")),
              MovementValidationFailure(Some(8132), Some("beans")),
              MovementValidationFailure(Some(12), Some("The complement consignee trader information must be present if the destination is an Exempted Consignee. Please amend your entry and resubmit.")),
              MovementValidationFailure(Some(8133), Some("beans")),
              MovementValidationFailure(Some(8134), Some("beans")),
              MovementValidationFailure(Some(8052), Some("beans")),
              MovementValidationFailure(Some(8053), Some("beans")),
              MovementValidationFailure(Some(8054), Some("beans")),
              MovementValidationFailure(Some(8055), Some("beans")),
              MovementValidationFailure(Some(8149), Some("beans")),
              MovementValidationFailure(Some(8150), Some("beans")),
              MovementValidationFailure(Some(8151), Some("beans")),
              MovementValidationFailure(Some(8056), Some("beans")),
              MovementValidationFailure(Some(12), Some("You must provide a valid Trader ID if the destination is 'Tax Warehouse'. Please amend your entry and resubmit.")),
              MovementValidationFailure(Some(8555), Some("beans")),
              MovementValidationFailure(Some(8556), Some("beans")),
              MovementValidationFailure(Some(8605), Some("beans")),
              MovementValidationFailure(Some(8606), Some("beans")),
              MovementValidationFailure(Some(8607), Some("beans")),
              MovementValidationFailure(Some(12), Some("The delivery place customs office must be present if the destination is 'Export'.")),
              MovementValidationFailure(Some(8620), Some("beans")),
              MovementValidationFailure(Some(8621), Some("beans")),
              MovementValidationFailure(Some(8622), Some("beans")),
              MovementValidationFailure(Some(8623), Some("beans")),
              MovementValidationFailure(Some(8702), Some("beans")),
              MovementValidationFailure(Some(8709), Some("beans")),
              MovementValidationFailure(Some(8710), Some("beans")),
              MovementValidationFailure(Some(8711), Some("beans")),
              MovementValidationFailure(Some(12), Some("The transport arranger information is required if they are not the consignee or the consignor. Please amend your entry and resubmit.")),
              MovementValidationFailure(Some(8500), Some("beans")),
              MovementValidationFailure(Some(8501), Some("beans")),
              MovementValidationFailure(Some(8502), Some("beans")),
              MovementValidationFailure(Some(8503), Some("beans")),
              MovementValidationFailure(Some(8140), Some("beans")),
              MovementValidationFailure(Some(8059), Some("beans")),
              MovementValidationFailure(Some(8624), Some("beans")),
              MovementValidationFailure(Some(8723), Some("beans")),
              MovementValidationFailure(Some(8152), Some("beans")),
              MovementValidationFailure(Some(8061), Some("beans")),
              MovementValidationFailure(Some(8135), Some("beans")),
              MovementValidationFailure(Some(8216), Some("beans")),
              MovementValidationFailure(Some(8062), Some("beans")),
              MovementValidationFailure(Some(8063), Some("beans")),
              MovementValidationFailure(Some(8088), Some("beans")),
              MovementValidationFailure(Some(8064), Some("beans")),
              MovementValidationFailure(Some(8158), Some("beans")),
              MovementValidationFailure(Some(8159), Some("beans")),
              MovementValidationFailure(Some(13), Some("You must provide the trader name or the Trader ID. Please amend your entry and resubmit.")),
              MovementValidationFailure(Some(13), Some("You must provide the street name or the Trader ID. Please amend your entry and resubmit.")),
              MovementValidationFailure(Some(13), Some("You must provide the city or the Trader ID. Please amend your entry and resubmit.")),
              MovementValidationFailure(Some(13), Some("You must provide the postcode or the Trader ID. Please amend your entry and resubmit.")),
              MovementValidationFailure(Some(8136), Some("beans")),
              MovementValidationFailure(Some(8065), Some("beans")),
              MovementValidationFailure(Some(8097), Some("beans")),
              MovementValidationFailure(Some(8087), Some("beans")),
              MovementValidationFailure(Some(8067), Some("beans")),
              MovementValidationFailure(Some(8068), Some("beans")),
              MovementValidationFailure(Some(8153), Some("beans")),
              MovementValidationFailure(Some(8724), Some("beans")),
              MovementValidationFailure(Some(8725), Some("beans")),
              MovementValidationFailure(Some(8086), Some("beans")),
              MovementValidationFailure(Some(8504), Some("beans")),
              MovementValidationFailure(Some(8217), Some("beans")),
              MovementValidationFailure(Some(8169), Some("beans")),
              MovementValidationFailure(Some(8218), Some("beans")),
              MovementValidationFailure(Some(8505), Some("beans")),
              MovementValidationFailure(Some(8506), Some("beans")),
              MovementValidationFailure(Some(8073), Some("beans")),
              MovementValidationFailure(Some(8074), Some("beans")),
              MovementValidationFailure(Some(8072), Some("beans")),
              MovementValidationFailure(Some(8704), Some("beans")),
              MovementValidationFailure(Some(8076), Some("beans")),
              MovementValidationFailure(Some(8077), Some("beans")),
              MovementValidationFailure(Some(13), Some("The import Single Administrative Document (SAD) must be present if the origin type code is 'Import'. Please amend your entry and resubmit.")),
              MovementValidationFailure(Some(8137), Some("beans")),
              MovementValidationFailure(Some(8713), Some("beans")),
              MovementValidationFailure(Some(8726), Some("beans")),
              MovementValidationFailure(Some(8083), Some("beans")),
              MovementValidationFailure(Some(8084), Some("beans")),
              MovementValidationFailure(Some(8085), Some("beans")),
              MovementValidationFailure(Some(8219), Some("beans")),
              MovementValidationFailure(Some(12), Some("The import Single Administrative Document (SAD) must not be present if the origin type code is 'Tax Warehouse'. Please amend your entry and resubmit.")),
              MovementValidationFailure(Some(8081), Some("beans")),
              MovementValidationFailure(Some(8082), Some("beans"))
            )

            val result = helper.validationFailureContent(validationFailures)

            result mustBe HtmlContent(HtmlFormat.fill(Seq(
              p("govuk-notification-banner__heading")(Html(messagesForLanguage.notificationBannerValidationFailuresContent)),
              list(Seq(
                p()(Html("You must provide the delivery information of the trader if the destination is to either a Tax Warehouse or Direct Delivery.")),
                p()(Html("The Consignee Trader information must be present unless the movement is to an unknown destination.")),
                p()(Html("The Trader ID for the Place of Dispatch must be present if the destination is Tax Warehouse or Duty Paid.")),
                p()(Html("The Dispatch Import Office ID must be present where the destination is import.")),
                p()(Html("The Complement Consignee Trader ID must be present if the destination is Exempt Organisation.")),
                p()(Html("The Trader ID must be present if the destination is to a Tax Warehouse, or a Certified Consignee, or a Temporary Certified Consignee.")),
                p()(Html("The Delivery Place Customs Office ID must be present if the destination is Export.")),
                p()(Html("You must provide the Transport Arranger Trader ID if you are not the consignor or the consignee.")),
                p()(Html("The transport details can repeat up to a maximum of 30 times.")),
                p()(Html("The body items can repeat up to a maximum of 999 times.")),
                p()(Html("The body item reference number is not incremental.")),
                p()(Html("You must provide the first transporter information.")),
                p()(Html("The submission type must be either a Standard submission, Submission for export (local clearance) or Submission for Duty Paid B2B.")),
                p()(Html("The submission message type must start at 1 or 3.")),
                p()(Html("If the submission type is Submission for Duty Paid B2B, then the Destination must be to either a Certified Consignee, or Temporary Certified Consignee.")),
                p()(Html("If the submission type is Submission for Duty Paid B2B, then the Destination can not be any of the following: Tax warehouse, Registered consignee, Temporary registered consignee, Direct delivery, Exempted consignee, Export, or Unknown destination.")),
                p()(Html("You must provide a Trader ID if the Destination is to any of the following: Tax Warehouse, Registered Consignee, Temporary Registered Consignee , Direct Delivery, Certified Consignee, Temporary Certified Consignee, or Return to the place of dispatch of the consignor.")),
                p()(Html("The consignee trader information must not be present where the destination is unknown.")),
                p()(Html("You do not need to provide a Trader ID if the Destination is not to any of the following: Tax Warehouse, Registered Consignee, Temporary Registered Consignee, Direct Delivery, Export, Certified Consignee, Temporary Certified Consignee, or Return to the place of dispatch of the consignor.")),
                p()(Html("The Trader ID must be a valid Excise Number if the destination is to a Tax Warehouse, Registered Consignee, Temporary Registered Consignee, Direct Delivery, Certified Consignee, Temporary Certified Consignee or for Return to the place of dispatch of the consignor.")),
                p()(Html("If a guarantor is not required then the Trader ID must start with GB or XI.")),
                p()(Html("The UK consignee Trader ID must start with GBWK or XIWK where the place of destination is a UK excise warehouse.")),
                p()(Html("The consignee trader ID must start with XIWK where the place of destination is a NI excise warehouse.")),
                p()(Html("The consignee Trader ID must not start with GB or XI when the place of destination is a non-UK excise warehouse, Registered Consignee, Temporary Registered Consignee,  Direct Delivery, Certified Consignee, Temporary Certified Consignee or Return to the place of dispatch of the consignor.")),
                p()(Html("If the place of dispatch excise warehouse ID starts with GB then the consignee trader ID must start with GB or XI.")),
                p()(Html("If the Registered Consignor ID starts with GB then the consignee trader ID must start with GB or XI.")),
                p()(Html("If the consignee Trader ID starts with XI then the postcode must start with “BT” (case insensitive).")),
                p()(Html("If the consignee Trader ID starts with GB then the postcode must not start with “BT” (case insensitive).")),
                p()(Html("The consignee EORI number must only be present for an export movement.")),
                p()(Html("The UK consignor Trader Excise Number must start with GBWK or XIWK where the place of dispatch is a UK excise warehouse.")),
                p()(Html("The UK consignor Trader Excise Number must start with GBRC or XIRC where the place of dispatch is a UK place of import.")),
                p()(Html("The UK consignor Trader Excise Number must start with XIPA or XIPC where the place of dispatch is for Duty Paid.")),
                p()(Html("The UK consignor Trader Excise Number must start with XIPA or XIPC where the submission message type is for Duty Paid.")),
                p()(Html("If the consignor excise ID starts with XI then the postcode must start with “BT” (case insensitive).")),
                p()(Html("If the consignor excise ID starts with GB then the postcode must not start with “BT”.")),
                p()(Html("The language code for the place of dispatch trader on the draft movement must be provided.")),
                p()(Html("The place of dispatch trader information must not be present where the origin type code is ‘Import’.")),
                p()(Html("If the consignor trader ID starts with GB then the place of dispatch excise ID must start with GB00.")),
                p()(Html("If the consignor trader ID starts with XI then the place of dispatch excise ID must start with GB00 or XI00.")),
                p()(Html("The Reference of Tax Warehouse must not be present where the Submission Message Type is ‘Duty Paid’.")),
                p()(Html("The place trader postcode must not start with ‘BT’ where the place of dispatch is from an excise warehouse in Great Britain.")),
                p()(Html("The place trader postcode must start with ‘BT’ where the place of dispatch is from an excise warehouse in Northern Ireland.")),
                p()(Html("The Post Code must be entered where the Submission Message Type is ‘Duty Paid’.")),
                p()(Html("The Trader Name must be entered where the Submission Message Type is ‘Duty Paid’.")),
                p()(Html("The Street Name must be entered where the Submission Message Type is ‘Duty Paid’.")),
                p()(Html("The City must be entered where the Submission Message Type is ‘Duty Paid’.")),
                p()(Html("The dispatch import office must not be present where the origin type code is ‘Tax Warehouse’ or ‘Duty Paid’.")),
                p()(Html("If the consignor trader ID starts with GB then the dispatch import office ID must start with GB.")),
                p()(Html("If the consignor trader ID starts with XI then the dispatch import office ID must start with XI.")),
                p()(Html("You must provide the serial number of the certificate of exemption.")),
                p()(Html("The complement consignee trader information must be present if the destination is an Exempted Consignee.")),
                p()(Html("The member state code must not be ‘GB’ or ‘XI’.")),
                p()(Html("The language code for the place of delivery on the draft movement must be provided.")),
                p()(Html("You must provide a trader name for the selected movement type.")),
                p()(Html("You must provide a Street Name for the selected movement type.")),
                p()(Html("You must provide a Postcode for the selected movement type.")),
                p()(Html("You must provide a City Name for the selected movement type.")),
                p()(Html("The place of delivery information must not be present where the destination is to an Unknown Destination.")),
                p()(Html("The place of delivery information must not be present where the destination is for Export.")),
                p()(Html("The place of delivery information must not be present where the destination is to Registered Consignee.")),
                p()(Html("The Trader ID is not required for Direct Deliveries.")),
                p()(Html("You must provide a valid excise ID for the place of destination tax warehouse.")),
                p()(Html("The UK place of delivery excise warehouse ID must start with GB00 where the place of destination is a UK excise warehouse.")),
                p()(Html("The consignee delivery place trader ID must not start with GB or XI where the place of destination is either a non-UK excise warehouse or Temporary Registered Consignee.")),
                p()(Html("The consignee delivery place Trader ID must start with XI00 where the place of destination is to an excise warehouse in Northern Ireland.")),
                p()(Html("The consignee delivery place trader postcode must start with ‘BT’ where the place of destination is to an excise warehouse in Northern Ireland.")),
                p()(Html("The consignee delivery place trader postcode must not start with ‘BT’ where the place of destination is to an excise warehouse in Great Britain.")),
                p()(Html("The delivery place customs office must be present if the destination is ‘Export’.")),
                p()(Html("If the consignee place of dispatch excise ID starts with GB then the delivery place customs office must also start with GB.")),
                p()(Html("If the consignee place of dispatch excise ID starts with XI then the delivery place customs office must not start with GB.")),
                p()(Html("If the Registered Consignor excise ID starts with GB then the delivery place customs office must also start with GB.")),
                p()(Html("If the Registered Consignor excise ID starts with XI then the delivery place customs office must not start with GB.")),
                p()(Html("If the consignor trader ID starts with GB then the competent authority dispatch office ID must start with GB.")),
                p()(Html("If reference of tax warehouse has been submitted and starts with “XI”, then the competent authority dispatch office ID must start with XI.")),
                p()(Html("If reference of tax warehouse has been submitted and starts with “GB”, then the competent authority dispatch office ID must start with GB.")),
                p()(Html("If reference of tax warehouse has not been submitted and the consignor trader ID starts with “XI”, then the competent authority dispatch office ID must start with XI.")),
                p()(Html("The transport arranger information is required if they are not the consignee or the consignor.")),
                p()(Html("The document type must be present.")),
                p()(Html("The document reference must be present.")),
                p()(Html("The document description is no longer valid.")),
                p()(Html("The reference of document is no longer valid.")),
                p()(Html("The destination type must be one of the following: Tax warehouse, Registered consignee, Temporary registered consignee, Direct delivery, Exempted consignee, Export, Unknown destination, Certified consignee, Temporary certified consignee, or Return to the place of dispatch of the consignor.")),
                p()(Html("If the Trader ID and Trader Excise Number starts with GB or XI and the movement originates from a Tax Warehouse then the destination must be to another Tax Warehouse or for export.")),
                p()(Html("If the Trader ID and Trader Excise Number starts with ‘GB’ or ‘XI’ and the movement is imported then the destination must be to a Tax Warehouse, for export or to an unknown destination (for example, for energy products).")),
                p()(Html("If the Trader ID and Trader Excise Number start with ‘XI’ and the movement is duty paid then the destination must be to a Certified Consignee or to a Temporary Certified Consignee.")),
                p()(Html("If Guarantor Type Code equals ‘5’ Guarantor not required for qualifying UK to EU Movements then the mode of transport must be Sea Transport or  Fixed Transport Installation.")),
                p()(Html("If the movement is to an unknown destination (for energy products) then the transport mode must be sea transport or inland waterway transport.")),
                p()(Html("If ‘Other’ has been selected for the transport mode code you must provide the complementary information.")),
                p()(Html("The complementary information can only be provided if the transport mode equals ‘Other’.")),
                p()(Html("You must provide a guarantor if the guarantor type is either the transporter or the owner of the excise products.")),
                p()(Html("You can only provide one guarantor.")),
                p()(Html("The Guarantor Type Code is invalid.")),
                p()(Html("If a guarantor is not required then the destination type must be a tax warehouse.")),
                p()(Html("The consignee can be a guarantor for a movement to a UK warehouse or to an EU Certified Consignee.")),
                p()(Html("The consignee can only be a guarantor for a movement to a UK warehouse.")),
                p()(Html("You must provide the trader name or the Trader ID.")),
                p()(Html("You must provide the street name or the Trader ID.")),
                p()(Html("You must provide the city or the Trader ID.")),
                p()(Html("You must provide the postcode or the Trader ID.")),
                p()(Html("The language code for the guarantor trader type on the draft movement must be provided.")),
                p()(Html("A guarantor Trader ID is not required where the guarantor is a consignor or a consignee, or if a guarantor is not required for the movement.")),
                p()(Html("The Excise Product Code you have entered is not in the correct format. It should be alphanumeric starting with the letters E, T, W, B, I or S followed by three numeric characters.")),
                p()(Html("The Excise Product Category Code is not valid for this CN Code.")),
                p()(Html("The only excise products where a guarantor is not required are B000, W200 and W300.")),
                p()(Html("If the destination type code is for unknown destination (for energy products), then the Excise Product Code must start with an ‘E’.")),
                p()(Html("If Guarantor Type Code equals ‘5’, Guarantor not required for qualifying UK to EU Movements then all Excise Product Codes must be Energy Products.")),
                p()(Html("The Excise Product Code of S600 can only be used for Duty paid.")),
                p()(Html("The Excise Product Code is not valid for this submission message type (movement in duty suspension).")),
                p()(Html("You have entered an Excise Product Code of S500. The correct CN Code to be input is 10000000.")),
                p()(Html("The CN Code must be greater than zero.")),
                p()(Html("The gross mass can not be less than the net mass.")),
                p()(Html("The alcoholic strength must not exceed 100.")),
                p()(Html("The alcoholic strength must not be less than 0.5.")),
                p()(Html("The number of packages is ‘0’ so there must be at least one other package with the same Shipping Mark value where the number of packages has been supplied.")),
                p()(Html("A Shipping Mark must be present if the number of packages equals 0.")),
                p()(Html("The Wine Operation Code can only be option ‘0’ if no other Wine Operation Codes have been selected.")),
                p()(Html("The Third Country of Origin Code must be provided for imported wine.")),
                p()(Html("The Wine Product can only be present if the Excise Product Code starts with a ‘W’.")),
                p()(Html("If the consignor trader ID starts with GB then the wine product category must not be 1, 2, or 3.")),
                p()(Html("The Third Country of Origin Code is not required for non-imported wine.")),
                p()(Html("Each Wine Operation Code must be unique.")),
                p()(Html("The import Single Administrative Document (SAD) must be present if the origin type code is ‘Import’.")),
                p()(Html("You must enter a time of dispatch.")),
                p()(Html("The origin type must be one of the following: Tax warehouse, Import or Duty Paid.")),
                p()(Html("The origin type must be Duty Paid.")),
                p()(Html("You must enter a Date of Dispatch which is either today or within the next 7 days following today.")),
                p()(Html("The Date of Dispatch you entered is incorrect. It must be today or later.")),
                p()(Html("You have selected the deferred indicator. The Date of Dispatch must be on or before the date of submission.")),
                p()(Html("The time of dispatch should not specify milliseconds.")),
                p()(Html("The import Single Administrative Document (SAD) must not be present if the origin type code is ‘Tax Warehouse’ or ‘Duty Paid’.")),
                p()(Html("The Identity of Transport Units must be entered if Transport Unit Code is not fixed transport installation.")),
                p()(Html("The Identity of transport Units field is not applicable as you have entered a Transport Unit Code of fixed transport installation.")),
              ))
            )))
          }
        }
      }

      "removeAmendEntryMessageFromErrorReason" - {

        "must change the ChRIS response for error message 12.e" in {
          val result = helper.removeAmendEntryMessageFromErrorReason("You must provide a valid Trader ID if the destination is 'Tax Warehouse'. Please amend your entry and resubmit.")
          result mustBe "You must provide a valid excise ID for the place of destination tax warehouse."
        }

        "must remove 'Please amend your entry and resubmit.' from the error reason" in {
          Seq(
            "This is an error. Please amend your entry and resubmit.",
            "This is an error.       Please amend your entry and resubmit.",
            "This is an error.Please amend your entry and resubmit.",
            "This is an error. Please amend your entry and resubmit",
            "This is an error.        Please amend your entry and resubmit",
            "This is an error.Please amend your entry and resubmit"
          ).foreach { errorMessage =>
            val result = helper.removeAmendEntryMessageFromErrorReason(errorMessage)
            result mustBe "This is an error."
          }
        }

        "must replace single quotes around 'Import', 'Tax Warehouse', 'Duty Paid', and 'Export', with smart quotes" in {
          Seq("Import", "Tax Warehouse", "Duty Paid", "Export").foreach {
            text =>
              val result = helper.removeAmendEntryMessageFromErrorReason(s"This is an error. '$text'.")
              result mustBe s"This is an error. ‘$text’."
          }
        }

        "must replace 'origin type code is 'Tax Warehouse'.' with 'origin type code is 'Tax Warehouse' or 'Duty Paid'.'" in {
          val result = helper.removeAmendEntryMessageFromErrorReason("This is an error. origin type code is 'Tax Warehouse'.")
          result mustBe "This is an error. origin type code is ‘Tax Warehouse’ or ‘Duty Paid’."
        }

        "must not modify the error reason if 'Please amend your entry and resubmit.' is not present, 'origin type code is 'Tax Warehouse'.' is not present, and there are no quotes to be replaced with smart quotes" in {
          val result = helper.removeAmendEntryMessageFromErrorReason("This is an error.")
          result mustBe "This is an error."
        }
      }
    }
  }
}
//scalastyle:on magic.number
