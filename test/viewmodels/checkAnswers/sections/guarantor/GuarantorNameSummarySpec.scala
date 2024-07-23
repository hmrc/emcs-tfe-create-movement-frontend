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

package viewmodels.checkAnswers.sections.guarantor

import base.SpecBase
import fixtures.messages.sections.guarantor.GuarantorNameMessages
import fixtures.messages.sections.guarantor.GuarantorNameMessages.ViewMessages
import models.requests.DataRequest
import models.sections.guarantor.GuarantorArranger.{Consignee, Consignor, GoodsOwner, Transporter}
import models.sections.info.movementScenario.MovementScenario.{ExportWithCustomsDeclarationLodgedInTheUk, UkTaxWarehouse}
import models.sections.journeyType.HowMovementTransported.FixedTransportInstallations
import models.{CheckMode, Mode, NormalMode}
import org.scalatest.matchers.must.Matchers
import pages.sections.consignee.ConsigneeBusinessNamePage
import pages.sections.guarantor.{GuarantorArrangerPage, GuarantorNamePage, GuarantorRequiredPage}
import pages.sections.info.DestinationTypePage
import pages.sections.journeyType.HowMovementTransportedPage
import play.api.i18n.Messages
import play.api.test.FakeRequest
import uk.gov.hmrc.govukfrontend.views.Aliases.{Key, Value}
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import viewmodels.govuk.summarylist._

class GuarantorNameSummarySpec extends SpecBase with Matchers {

  "GuarantorNameSummary" - {

    def expectedRow(value: String, showChangeLink: Boolean, mode: Mode = CheckMode)(implicit messagesForLanguage: ViewMessages): Option[SummaryListRow] = {
      Some(
        SummaryListRowViewModel(
          key = Key(Text(messagesForLanguage.cyaLabel)),
          value = Value(Text(value)),
          actions = if (!showChangeLink) Seq() else Seq(ActionItemViewModel(
            content = Text(messagesForLanguage.change),
            href = controllers.sections.guarantor.routes.GuarantorNameController.onPageLoad(testGreatBritainWarehouseKeeperErn, testDraftId, mode).url,
            id = "changeGuarantorName"
          ).withVisuallyHiddenText(messagesForLanguage.cyaChangeHidden))
        )
      )
    }

    Seq(GuarantorNameMessages.English).foreach { implicit messagesForLanguage =>

      s"when language is set to ${messagesForLanguage.lang.code}" - {
        implicit val msgs: Messages = messages(Seq(messagesForLanguage.lang))

        "when a Guarantor is always required (not optional)" - {

          "GuarantorArranger is answered" - {

            "then must return expected row" in {

              implicit lazy val request = dataRequest(
                FakeRequest(),
                emptyUserAnswers
                  .set(GuarantorArrangerPage, Consignee)
                  .set(DestinationTypePage, ExportWithCustomsDeclarationLodgedInTheUk)
                  .set(HowMovementTransportedPage, FixedTransportInstallations),
                testGreatBritainWarehouseKeeperErn
              )

              GuarantorNameSummary.row mustBe expectedRow(messagesForLanguage.consigneeNameNotProvided, false)
            }
          }

          "GuarantorArranger is NOT answered" - {

            "then must return expected row" in {

              implicit lazy val request = dataRequest(FakeRequest(), emptyUserAnswers
                .set(DestinationTypePage, ExportWithCustomsDeclarationLodgedInTheUk),
                testGreatBritainWarehouseKeeperErn
              )

              GuarantorNameSummary.row mustBe None
            }
          }
        }

        "when a Guarantor is optional" - {

          "and there is a GuarantorRequiredPage answer of `no`" - {
            "then must not return a row" in {
              implicit lazy val request: DataRequest[_] = dataRequest(
                FakeRequest(),
                emptyUserAnswers
                  .set(DestinationTypePage, UkTaxWarehouse.GB)
                  .set(GuarantorRequiredPage, false),
                testGreatBritainWarehouseKeeperErn
              )

              GuarantorNameSummary.row mustBe None
            }
          }

          "and there is a GuarantorRequiredPage answer of `yes`" - {

            "and there is a GuarantorArrangerPage answer of `Consignee`" - {
              "and that section hasn't been filled in yet" in {

                implicit lazy val request: DataRequest[_] = dataRequest(
                  FakeRequest(),
                  emptyUserAnswers
                    .set(DestinationTypePage, UkTaxWarehouse.GB)
                    .set(GuarantorRequiredPage, true)
                    .set(GuarantorArrangerPage, Consignee),
                  testGreatBritainWarehouseKeeperErn
                )

                GuarantorNameSummary.row mustBe expectedRow(messagesForLanguage.consigneeNameNotProvided, false)
              }

              "and that section has been filled in" in {
                implicit lazy val request: DataRequest[_] = dataRequest(
                  FakeRequest(),
                  emptyUserAnswers
                    .set(DestinationTypePage, UkTaxWarehouse.GB)
                    .set(GuarantorRequiredPage, true)
                    .set(GuarantorArrangerPage, Consignee)
                    .set(ConsigneeBusinessNamePage, "consignee name here"),
                  testGreatBritainWarehouseKeeperErn
                )

                GuarantorNameSummary.row mustBe expectedRow("consignee name here", false)
              }
            }

            "and there is a GuarantorArrangerPage answer of `Consignor`" - {
              "and that section has been filled in" in {
                implicit lazy val request: DataRequest[_] = dataRequest(
                  FakeRequest(),
                  emptyUserAnswers
                    .set(DestinationTypePage, UkTaxWarehouse.GB)
                    .set(GuarantorRequiredPage, true)
                    .set(GuarantorArrangerPage, Consignor),
                  testGreatBritainWarehouseKeeperErn
                )

                GuarantorNameSummary.row mustBe expectedRow(testMinTraderKnownFacts.traderName, false)
              }
            }

            "and there is a GuarantorArrangerPage answer of `GoodsOwner`" - {
              "and that section hasn't been filled in yet" in {

                implicit lazy val request: DataRequest[_] = dataRequest(
                  FakeRequest(),
                  emptyUserAnswers
                    .set(DestinationTypePage, UkTaxWarehouse.GB)
                    .set(GuarantorRequiredPage, true)
                    .set(GuarantorArrangerPage, GoodsOwner),
                  testGreatBritainWarehouseKeeperErn
                )

                GuarantorNameSummary.row mustBe expectedRow(messagesForLanguage.notProvided, showChangeLink = true, NormalMode)
              }

              "and that section has been filled in" in {
                implicit lazy val request: DataRequest[_] = dataRequest(
                  FakeRequest(),
                  emptyUserAnswers
                    .set(DestinationTypePage, UkTaxWarehouse.GB)
                    .set(GuarantorRequiredPage, true)
                    .set(GuarantorArrangerPage, GoodsOwner)
                    .set(GuarantorNamePage, "guarantor name here"),
                  testGreatBritainWarehouseKeeperErn
                )

                GuarantorNameSummary.row mustBe expectedRow("guarantor name here", true)
              }
            }

            "and there is a GuarantorArrangerPage answer of `Transporter`" - {
              "and that section hasn't been filled in yet" in {

                implicit lazy val request: DataRequest[_] = dataRequest(
                  FakeRequest(),
                  emptyUserAnswers
                    .set(DestinationTypePage, UkTaxWarehouse.GB)
                    .set(GuarantorRequiredPage, true)
                    .set(GuarantorArrangerPage, Transporter),
                  testGreatBritainWarehouseKeeperErn
                )

                GuarantorNameSummary.row mustBe expectedRow(messagesForLanguage.notProvided, showChangeLink = true, NormalMode)
              }

              "and that section has been filled in" in {
                implicit lazy val request: DataRequest[_] = dataRequest(
                  FakeRequest(),
                  emptyUserAnswers
                    .set(DestinationTypePage, UkTaxWarehouse.GB)
                    .set(GuarantorRequiredPage, true)
                    .set(GuarantorArrangerPage, Transporter)
                    .set(GuarantorNamePage, "transporter name here"),
                  testGreatBritainWarehouseKeeperErn
                )

                GuarantorNameSummary.row mustBe expectedRow("transporter name here", true)
              }
            }
          }
        }
      }
    }
  }
}
