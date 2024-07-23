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
import fixtures.messages.sections.guarantor.GuarantorRequiredMessages
import fixtures.messages.sections.guarantor.GuarantorRequiredMessages.ViewMessages
import models.CheckMode
import models.sections.info.movementScenario.MovementScenario.{EuTaxWarehouse, ExportWithCustomsDeclarationLodgedInTheUk}
import pages.sections.guarantor.GuarantorRequiredPage
import pages.sections.info.DestinationTypePage
import play.api.i18n.Messages
import play.api.test.FakeRequest
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.{Key, SummaryListRow, Value}
import viewmodels.govuk.summarylist._


class GuarantorRequiredSummarySpec extends SpecBase {

  private def expectedRow(value: String, ern: String)(implicit messagesForLanguage: ViewMessages): Option[SummaryListRow] = {
    Some(
      SummaryListRowViewModel(
        key = Key(Text(messagesForLanguage.cyaLabel)),
        value = Value(Text(value)),
        actions = Seq(ActionItemViewModel(
          content = Text(messagesForLanguage.change),
          href = controllers.sections.guarantor.routes.GuarantorRequiredController.onPageLoad(ern, testDraftId, CheckMode).url,
          id = "changeGuarantorRequired"
        ).withVisuallyHiddenText(messagesForLanguage.cyaChangeHidden))
      )
    )
  }

  Seq(GuarantorRequiredMessages.English).foreach { implicit messagesForLanguage =>

    s"when language is set to ${messagesForLanguage.lang.code}" - {

      implicit val msgs: Messages = messages(Seq(messagesForLanguage.lang))

      "when Guarantor is optional (not always required)" - {

        "and there is no answer for the GuarantorRequiredPage" - {

          "then must return a not provided row" in {

            implicit lazy val request = dataRequest(
              FakeRequest(),
              emptyUserAnswers.set(DestinationTypePage, EuTaxWarehouse),
              testNorthernIrelandErn
            )

            GuarantorRequiredSummary.row mustBe expectedRow(value = messagesForLanguage.notProvided, testNorthernIrelandErn)
          }
        }

        "and there is a GuarantorRequiredPage answer of yes" - {

          "then must return a row with the answer of yes " in {

            implicit lazy val request = dataRequest(
              FakeRequest(),
              emptyUserAnswers
                .set(DestinationTypePage, EuTaxWarehouse)
                .set(GuarantorRequiredPage, true)
              ,
              testNorthernIrelandErn
            )

            GuarantorRequiredSummary.row mustBe expectedRow(value = messagesForLanguage.yes, testNorthernIrelandErn)
          }
        }

        "and there is a GuarantorRequiredPage answer of no" - {

          "then must return a row with the answer " in {

            implicit lazy val request = dataRequest(
              FakeRequest(),
              emptyUserAnswers
                .set(DestinationTypePage, EuTaxWarehouse)
                .set(GuarantorRequiredPage, false)
              ,
              testNorthernIrelandErn
            )

            GuarantorRequiredSummary.row mustBe expectedRow(value = messagesForLanguage.no, testNorthernIrelandErn)
          }
        }
      }

      "when Guarantor is Always required (not optional)" - {

        "then must return None" in {

          implicit lazy val request = dataRequest(
            FakeRequest(),
            emptyUserAnswers.set(DestinationTypePage, ExportWithCustomsDeclarationLodgedInTheUk),
            testGreatBritainWarehouseKeeperErn
          )

          GuarantorRequiredSummary.row mustBe None
        }
      }
    }
  }
}
