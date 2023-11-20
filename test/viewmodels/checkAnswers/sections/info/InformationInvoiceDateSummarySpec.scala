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

package viewmodels.checkAnswers.sections.info

import base.SpecBase
import fixtures.messages.sections.info.InvoiceDetailsMessages
import fixtures.messages.sections.info.InvoiceDetailsMessages.ViewMessages
import models.CheckMode
import models.sections.info.InvoiceDetailsModel
import pages.sections.info.InvoiceDetailsPage
import play.api.i18n.{Messages, MessagesApi}
import play.api.test.FakeRequest
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.{Key, SummaryListRow, Value}
import viewmodels.govuk.summarylist._

import java.time.LocalDate


class InformationInvoiceDateSummarySpec extends SpecBase {

  private def expectedRow(value: String)(implicit messagesForLanguage: ViewMessages): Option[SummaryListRow] = {
    Some(
      SummaryListRowViewModel(
        key = Key(Text(messagesForLanguage.cyaInvoiceDateLabel)),
        value = Value(Text(value)),
        actions = Seq(ActionItemViewModel(
          content = Text(messagesForLanguage.change),
          href = controllers.sections.info.routes.InvoiceDetailsController.onPreDraftPageLoad(testErn, CheckMode).url,
          id = "changeInvoiceDate"
        ).withVisuallyHiddenText(messagesForLanguage.cyaChangeInvoiceDateHidden))
      )
    )
  }

  Seq(InvoiceDetailsMessages.English).foreach { implicit messagesForLanguage =>

    s"when language is set to ${messagesForLanguage.lang.code}" - {

      implicit val msgs: Messages = app.injector.instanceOf[MessagesApi].preferred(Seq(messagesForLanguage.lang))

      "and there is no answer for the InvoiceDetailsPage" - {
        "then must return not provided row" in {
          implicit lazy val request = dataRequest(FakeRequest(), emptyUserAnswers)

          InformationInvoiceDateSummary.row mustBe expectedRow(value = messagesForLanguage.notProvided)
        }
      }

      "and there is a InvoiceDetailsPage answer " - {
        "then must return a row with the answer" in {
          val model = InvoiceDetailsModel("inv reference", LocalDate.of(2023,6,7))

          implicit lazy val request = dataRequest(FakeRequest(), emptyUserAnswers.set(InvoiceDetailsPage(), model))

          InformationInvoiceDateSummary.row mustBe expectedRow(value = "7 June 2023")
        }
      }

    }
  }

}
