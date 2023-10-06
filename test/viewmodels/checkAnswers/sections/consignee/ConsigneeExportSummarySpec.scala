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

package viewmodels.checkAnswers.sections.consignee

import base.SpecBase
import fixtures.messages.sections.consignee.ConsigneeExportMessages
import models.CheckMode
import org.scalatest.matchers.must.Matchers
import pages.sections.consignee.ConsigneeExportPage
import play.api.i18n.Messages
import play.api.test.FakeRequest
import uk.gov.hmrc.govukfrontend.views.Aliases.Value
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text
import viewmodels.checkAnswers.sections.consignee.ConsigneeExportSummary
import viewmodels.govuk.summarylist._
import viewmodels.implicits._

class ConsigneeExportSummarySpec extends SpecBase with Matchers {
  "ConsigneeExportSummary" - {

    lazy val app = applicationBuilder().build()

    Seq(ConsigneeExportMessages.English, ConsigneeExportMessages.Welsh).foreach { messagesForLanguage =>

      s"when being rendered in lang code of '${messagesForLanguage.lang.code}'" - {

        implicit lazy val msgs: Messages = messages(app, messagesForLanguage.lang)

        "when there's no answer" - {

          "must output the expected data" in {

            implicit lazy val request = dataRequest(FakeRequest(), emptyUserAnswers)

            ConsigneeExportSummary.row(showActionLinks = true) mustBe None
          }
        }

        "when there's an answer" - {

          "when the show action link boolean is true" - {

            "must output the expected row" in {

              implicit lazy val request = dataRequest(FakeRequest(), emptyUserAnswers.set(ConsigneeExportPage, true))

              ConsigneeExportSummary.row(showActionLinks = true) mustBe
                Some(
                  SummaryListRowViewModel(
                    key = messagesForLanguage.cyaLabel,
                    value = Value(Text("true")),
                    actions = Seq(
                      ActionItemViewModel(
                        content = messagesForLanguage.change,
                        href = controllers.sections.consignee.routes.ConsigneeExportController.onPageLoad(testErn, testLrn, CheckMode).url,
                        id = ConsigneeExportPage
                      ).withVisuallyHiddenText(messagesForLanguage.cyaChangeHidden)
                    )
                  )
                )
            }
          }

          "when the show action link boolean is false" - {

            "must output the expected row without action links" in {

              implicit lazy val request = dataRequest(FakeRequest(), emptyUserAnswers.set(ConsigneeExportPage, true))

              ConsigneeExportSummary.row(showActionLinks = false) mustBe
                Some(
                  SummaryListRowViewModel(
                    key = messagesForLanguage.cyaLabel,
                    value = Value(Text("true")),
                    actions = Seq()
                  )
                )
            }
          }
        }
      }
    }
  }
}