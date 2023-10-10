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
import models.CheckMode
import pages.sections.guarantor.GuarantorRequiredPage
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.Aliases.Value
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text
import viewmodels.govuk.summarylist._
import viewmodels.implicits._

class GuarantorRequiredSummarySpec extends SpecBase {

  lazy val app = applicationBuilder().build()
  "row" - {
    Seq(GuarantorRequiredMessages.English, GuarantorRequiredMessages.Welsh).foreach { messagesForLanguage =>
      s"when language is set to $messagesForLanguage" - {

        implicit lazy val msgs: Messages = messages(app, messagesForLanguage.lang)

        "must return Some(SummaryListRow)" - {
          "when the GuarantorRequired page is found" - {
            "and the answer is Yes" in {
              GuarantorRequiredSummary.row(emptyUserAnswers.set(GuarantorRequiredPage, true)) mustBe Some(SummaryListRowViewModel(
                key = messagesForLanguage.cyaLabel,
                value = Value(Text(messagesForLanguage.yes)),
                actions = Seq(ActionItemViewModel(
                  content = messagesForLanguage.change,
                  href = controllers.sections.guarantor.routes.GuarantorRequiredController.onPageLoad(testErn, testLrn, CheckMode).url,
                  id = "guarantor-required"
                ).withVisuallyHiddenText(messagesForLanguage.cyaChangeHidden))
              ))
            }
            "and the answer is No" in {
              GuarantorRequiredSummary.row(emptyUserAnswers.set(GuarantorRequiredPage, false)) mustBe Some(SummaryListRowViewModel(
                key = messagesForLanguage.cyaLabel,
                value = Value(Text(messagesForLanguage.no)),
                actions = Seq(ActionItemViewModel(
                  content = messagesForLanguage.change,
                  href = controllers.sections.guarantor.routes.GuarantorRequiredController.onPageLoad(testErn, testLrn, CheckMode).url,
                  id = "guarantor-required"
                ).withVisuallyHiddenText(messagesForLanguage.cyaChangeHidden))
              ))
            }
          }
        }
        "must return None" - {
          "when the GuarantorRequired page is not found" in {
            GuarantorRequiredSummary.row(emptyUserAnswers) mustBe None
          }
        }
      }
    }
  }
}
