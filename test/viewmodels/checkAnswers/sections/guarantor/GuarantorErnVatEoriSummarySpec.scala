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

package viewmodels.checkAnswers.sections.guarantor

import base.SpecBase
import fixtures.messages.sections.guarantor.GuarantorErnVatEoriMessages
import fixtures.messages.sections.guarantor.GuarantorErnVatEoriMessages.ViewMessages
import models.{CheckMode, VatNumberModel}
import models.requests.DataRequest
import models.sections.consignee.ConsigneeExportInformation.{NoInformation, VatNumber}
import models.sections.guarantor.GuarantorArranger.{Consignee, Consignor, GoodsOwner, Transporter}
import org.scalatest.matchers.must.Matchers
import pages.sections.consignee.{ConsigneeExcisePage, ConsigneeExportInformationPage, ConsigneeExportPage, ConsigneeExportVatPage}
import pages.sections.guarantor.{GuarantorArrangerPage, GuarantorRequiredPage, GuarantorVatPage}
import play.api.i18n.Messages
import play.api.test.FakeRequest
import uk.gov.hmrc.govukfrontend.views.Aliases.{Key, Value}
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import viewmodels.govuk.summarylist._

class GuarantorErnVatEoriSummarySpec extends SpecBase with Matchers {

  "GuarantorErnVatEoriSummary" - {

    def expectedRow(key: String, value: String, showChangeLink: Boolean = false)(implicit messagesForLanguage: ViewMessages): SummaryListRow =
      SummaryListRowViewModel(
        key = Key(Text(key)),
        value = Value(Text(value)),
        actions = if (!showChangeLink) Seq() else Seq(ActionItemViewModel(
          content = Text(messagesForLanguage.change),
          href = controllers.sections.guarantor.routes.GuarantorVatController.onPageLoad(testErn, testDraftId, CheckMode).url,
          id = "changeGuarantorVat"
        ).withVisuallyHiddenText(messagesForLanguage.cyaVatInputLabel))
      )

    Seq(GuarantorErnVatEoriMessages.English).foreach { implicit messagesForLanguage =>

      s"when language is set to ${messagesForLanguage.lang.code}" - {
        implicit val msgs: Messages = messages(Seq(messagesForLanguage.lang))

        "and there is no answer for the GuarantorRequiredPage" - {
          "then must not return a row" in {
            implicit lazy val request: DataRequest[_] = dataRequest(FakeRequest(), emptyUserAnswers)

            GuarantorErnVatEoriSummary.rows mustBe Seq()
          }
        }

        "and there is a GuarantorRequiredPage answer of `no`" - {
          "then must not return a row" in {
            implicit lazy val request: DataRequest[_] = dataRequest(FakeRequest(), emptyUserAnswers.set(GuarantorRequiredPage, false))

            GuarantorErnVatEoriSummary.rows mustBe Seq()
          }
        }

        "and there is a GuarantorRequiredPage answer of `yes`" - {

          "and there is a GuarantorArrangerPage answer of `Consignee`" - {

            "and the goods are to be exported outside the UK or EU" - {

              "and the consignee VAT section has been answered" in {

                implicit lazy val request: DataRequest[_] = dataRequest(
                  FakeRequest(),
                  emptyUserAnswers
                    .set(ConsigneeExportPage, true)
                    .set(ConsigneeExportInformationPage, Set(VatNumber))
                    .set(ConsigneeExportVatPage, "VAT123")
                    .set(GuarantorRequiredPage, true)
                    .set(GuarantorArrangerPage, Consignee)

                )

                GuarantorErnVatEoriSummary.rows mustBe Seq(expectedRow(messagesForLanguage.cyaVatInputLabel, "VAT123"))
              }

              "and the consignee doesn't provide either the VAT or EORI number " in {

                implicit lazy val request: DataRequest[_] = dataRequest(
                  FakeRequest(),
                  emptyUserAnswers
                    .set(ConsigneeExportPage, true)
                    .set(ConsigneeExportInformationPage, Set(NoInformation))
                    .set(GuarantorRequiredPage, true)
                    .set(GuarantorArrangerPage, Consignee)

                )

                GuarantorErnVatEoriSummary.rows mustBe Seq(expectedRow(messagesForLanguage.cyaErnLabel, messagesForLanguage.consigneeErnNotProvided))
              }

            }

            "and the goods are NOT to be exported outside the UK or EU" - {

              "and the consignee ERN section hasn't been filled in yet" in {

                implicit lazy val request: DataRequest[_] = dataRequest(
                  FakeRequest(),
                  emptyUserAnswers
                    .set(ConsigneeExportPage, false)
                    .set(GuarantorRequiredPage, true)
                    .set(GuarantorArrangerPage, Consignee)
                )

                GuarantorErnVatEoriSummary.rows mustBe Seq(expectedRow(messagesForLanguage.cyaErnLabel, messagesForLanguage.consigneeErnNotProvided))
              }

              "and that section has been filled in" in {
                implicit lazy val request: DataRequest[_] = dataRequest(
                  FakeRequest(),
                  emptyUserAnswers
                    .set(ConsigneeExportPage, false)
                    .set(GuarantorRequiredPage, true)
                    .set(GuarantorArrangerPage, Consignee)
                    .set(ConsigneeExcisePage, "GB12345678901")
                )

                GuarantorErnVatEoriSummary.rows mustBe Seq(expectedRow(messagesForLanguage.cyaErnLabel, "GB12345678901"))
              }
            }
          }

          "and there is a GuarantorArrangerPage answer of `Consignor`" - {
            "then must return the consignors ERN" in {
              implicit lazy val request: DataRequest[_] = dataRequest(
                FakeRequest(),
                emptyUserAnswers
                  .set(GuarantorRequiredPage, true)
                  .set(GuarantorArrangerPage, Consignor)
              )

              GuarantorErnVatEoriSummary.rows mustBe Seq(expectedRow(messagesForLanguage.cyaErnLabel, request.ern))
            }
          }

          "and there is a GuarantorArrangerPage answer of `GoodsOwner`" - {

            "and there is no answer for GuarantorVatPage" - {
              "then must render not provided row with change link" in {
                implicit lazy val request: DataRequest[_] = dataRequest(
                  FakeRequest(),
                  emptyUserAnswers
                    .set(GuarantorRequiredPage, true)
                    .set(GuarantorArrangerPage, GoodsOwner)
                )

                GuarantorErnVatEoriSummary.rows mustBe Seq(expectedRow(messagesForLanguage.cyaVatInputLabel , messagesForLanguage.notProvided, true))
              }
            }

            "and there is a GuarantorVatPage answer" - {
              "the answer is no" - {
                "then must render row with value and change link" in {
                  implicit lazy val request: DataRequest[_] = dataRequest(
                    FakeRequest(),
                    emptyUserAnswers
                      .set(GuarantorRequiredPage, true)
                      .set(GuarantorArrangerPage, GoodsOwner)
                      .set(GuarantorVatPage, VatNumberModel(hasVatNumber = false, vatNumber = None))
                  )

                  GuarantorErnVatEoriSummary.rows mustBe Seq(expectedRow(messagesForLanguage.cyaVatChoiceLabel, messagesForLanguage.no, true))
                }
              }

              "the answer is Yes with Vat Number" - {
                "then must render row with value and change link" in {
                  implicit lazy val request: DataRequest[_] = dataRequest(
                    FakeRequest(),
                    emptyUserAnswers
                      .set(GuarantorRequiredPage, true)
                      .set(GuarantorArrangerPage, GoodsOwner)
                      .set(GuarantorVatPage, VatNumberModel(hasVatNumber = true, vatNumber = Some(testVatNumber)))
                  )

                  GuarantorErnVatEoriSummary.rows mustBe Seq(
                    expectedRow(messagesForLanguage.cyaVatChoiceLabel, messagesForLanguage.yes, true),
                    expectedRow(messagesForLanguage.cyaVatInputLabel, testVatNumber, true)
                  )
                }
              }
            }

          }

          "and there is a GuarantorArrangerPage answer of `Transporter`" - {
            "and there is no answer for GuarantorVatPage" - {
              "then must render not provided row with change link" in {
                implicit lazy val request: DataRequest[_] = dataRequest(
                  FakeRequest(),
                  emptyUserAnswers
                    .set(GuarantorRequiredPage, true)
                    .set(GuarantorArrangerPage, Transporter)
                )

                GuarantorErnVatEoriSummary.rows mustBe Seq(
                  expectedRow(messagesForLanguage.cyaVatInputLabel, messagesForLanguage.notProvided, true)
                )
              }
            }

            "and there is a GuarantorVatPage answer" - {
              "the answer is no" - {
                "then must render row with value and change link" in {
                  implicit lazy val request: DataRequest[_] = dataRequest(
                    FakeRequest(),
                    emptyUserAnswers
                      .set(GuarantorRequiredPage, true)
                      .set(GuarantorArrangerPage, Transporter)
                      .set(GuarantorVatPage, VatNumberModel(hasVatNumber = false, None))
                  )

                  GuarantorErnVatEoriSummary.rows mustBe Seq(
                    expectedRow(messagesForLanguage.cyaVatChoiceLabel, messagesForLanguage.no, true)
                  )
                }
              }
              "the answer is yes" - {
                "then must render choice row with value and vat number row and change link" in {
                  implicit lazy val request: DataRequest[_] = dataRequest(
                    FakeRequest(),
                    emptyUserAnswers
                      .set(GuarantorRequiredPage, true)
                      .set(GuarantorArrangerPage, Transporter)
                      .set(GuarantorVatPage, VatNumberModel(hasVatNumber = true, Some(testVatNumber)))
                  )

                  GuarantorErnVatEoriSummary.rows mustBe Seq(
                    expectedRow(messagesForLanguage.cyaVatChoiceLabel, messagesForLanguage.yes, true),
                    expectedRow(messagesForLanguage.cyaVatInputLabel, testVatNumber, true)
                  )
                }
              }
            }
          }
        }
      }
    }
  }
}
