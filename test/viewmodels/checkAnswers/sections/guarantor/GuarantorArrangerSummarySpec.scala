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
import fixtures.messages.sections.guarantor.GuarantorArrangerMessages
import fixtures.messages.sections.guarantor.GuarantorArrangerMessages.ViewMessages
import models.CheckMode
import models.sections.guarantor.GuarantorArranger.{Consignee, Consignor, GoodsOwner, Transporter}
import models.sections.info.movementScenario.MovementScenario.{ExportWithCustomsDeclarationLodgedInTheUk, UkTaxWarehouse}
import pages.sections.guarantor.{GuarantorArrangerPage, GuarantorRequiredPage}
import pages.sections.info.DestinationTypePage
import play.api.i18n.Messages
import play.api.test.FakeRequest
import uk.gov.hmrc.govukfrontend.views.Aliases.{Key, Value}
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import viewmodels.govuk.summarylist._

class GuarantorArrangerSummarySpec extends SpecBase {
  private def expectedRow(value: String)(implicit messagesForLanguage: ViewMessages): Option[SummaryListRow] = {
    Some(
      SummaryListRowViewModel(
        key = Key(Text(messagesForLanguage.cyaLabel)),
        value = Value(Text(value)),
        actions = Seq(ActionItemViewModel(
          content = Text(messagesForLanguage.change),
          href = controllers.sections.guarantor.routes.GuarantorArrangerController.onPageLoad(testGreatBritainWarehouseKeeperErn, testDraftId, CheckMode).url,
          id = "changeGuarantorArranger"
        ).withVisuallyHiddenText(messagesForLanguage.cyaChangeHidden))
      )
    )
  }

  Seq(GuarantorArrangerMessages.English).foreach { implicit messagesForLanguage =>

    s"when language is set to ${messagesForLanguage.lang.code}" - {

      implicit val msgs: Messages = messages(Seq(messagesForLanguage.lang))

      "Guarantor is always required (not optional)" - {

        "then must return expected row" in {

          implicit lazy val request = dataRequest(
            FakeRequest(),
            emptyUserAnswers
              .set(GuarantorArrangerPage, Consignee)
              .set(DestinationTypePage, ExportWithCustomsDeclarationLodgedInTheUk),
            testGreatBritainWarehouseKeeperErn
          )

          GuarantorArrangerSummary.row mustBe expectedRow(messagesForLanguage.consigneeRadioOption)
        }
      }

      "Guarantor is optional" - {

        "and there is a GuarantorRequiredPage answer of `no`" - {

          "then must not return a row" in {

            implicit lazy val request = dataRequest(
              FakeRequest(),
              emptyUserAnswers
                .set(DestinationTypePage, UkTaxWarehouse.GB)
                .set(GuarantorRequiredPage, false),
              testGreatBritainWarehouseKeeperErn
            )

            GuarantorArrangerSummary.row mustBe None
          }
        }

        "and there is a GuarantorRequiredPage answer of `yes`" - {

          "and there is no answer for the GuarantorArrangerPage" in {

            implicit lazy val request = dataRequest(
              FakeRequest(),
              emptyUserAnswers
                .set(DestinationTypePage, UkTaxWarehouse.GB)
                .set(GuarantorRequiredPage, true),
              testGreatBritainWarehouseKeeperErn
            )

            GuarantorArrangerSummary.row mustBe expectedRow(messagesForLanguage.notProvided)
          }

          Seq(
            (Consignee, messagesForLanguage.consigneeRadioOption),
            (Consignor, messagesForLanguage.consignorRadioOption),
            (GoodsOwner, messagesForLanguage.goodsOwnerRadioOption),
            (Transporter, messagesForLanguage.transporterRadioOption)
          ).foreach { case (arranger, expectedMessage) =>

            s"and there is a GuarantorArrangerPage answer of `${arranger.getClass.getSimpleName.stripSuffix("$")}`" in {

              implicit lazy val request = dataRequest(
                FakeRequest(),
                emptyUserAnswers
                  .set(DestinationTypePage, UkTaxWarehouse.GB)
                  .set(GuarantorRequiredPage, true)
                  .set(GuarantorArrangerPage, arranger),
                testGreatBritainWarehouseKeeperErn
              )

              GuarantorArrangerSummary.row mustBe expectedRow(expectedMessage)
            }
          }
        }
      }
    }
  }
}
