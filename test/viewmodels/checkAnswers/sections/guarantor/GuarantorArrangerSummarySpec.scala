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
import models.CheckMode
import models.sections.guarantor.GuarantorArranger.Transporter
import pages.GuarantorArrangerPage
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.Aliases.Value
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text
import viewmodels.govuk.summarylist._
import viewmodels.implicits._

class GuarantorArrangerSummarySpec extends SpecBase {

  lazy val app = applicationBuilder().build()

  "row" - {
    Seq(GuarantorArrangerMessages.English, GuarantorArrangerMessages.Welsh).foreach { messagesForLanguage =>
      s"when language is set to $messagesForLanguage" - {

        implicit lazy val msgs: Messages = messages(app, messagesForLanguage.lang)

        "must return Some(SummaryListRow)" - {
          "when the GuarantorArranger page is found" in {
            GuarantorArrangerSummary.row(emptyUserAnswers.set(GuarantorArrangerPage, Transporter)) mustBe Some(SummaryListRowViewModel(
              key = messagesForLanguage.cyaLabel,
              value = Value(Text(messagesForLanguage.transporterRadioOption)),
              actions = Seq(ActionItemViewModel(
                content = messagesForLanguage.change,
                href = controllers.sections.guarantor.routes.GuarantorArrangerController.onPageLoad(testErn, testLrn, CheckMode).url,
                id = "guarantor-arranger"
              ).withVisuallyHiddenText(messagesForLanguage.cyaChangeHidden))
            ))

          }
        }
        "must return None" - {
          "when the GuarantorArranger page is not found" in {
            GuarantorArrangerSummary.row(emptyUserAnswers) mustBe None
          }
        }
      }
    }
  }
}
