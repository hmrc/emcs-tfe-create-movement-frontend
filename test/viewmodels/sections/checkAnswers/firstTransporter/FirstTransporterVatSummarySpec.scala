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

package viewmodels.sections.checkAnswers.firstTransporter

import base.SpecBase
import fixtures.messages.sections.firstTransporter.FirstTransporterVatMessages
import models.CheckMode
import org.scalatest.matchers.must.Matchers
import pages.sections.firstTransporter.FirstTransporterVatPage
import play.api.i18n.Messages
import play.api.test.FakeRequest
import uk.gov.hmrc.govukfrontend.views.Aliases.Value
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text
import viewmodels.checkAnswers.sections.firstTransporter.FirstTransporterVatSummary
import viewmodels.govuk.summarylist._
import viewmodels.implicits._


class FirstTransporterVatSummarySpec extends SpecBase with Matchers {
  "FirstTransporterVatSummary" - {

    lazy val app = applicationBuilder().build()

    Seq(FirstTransporterVatMessages.English, FirstTransporterVatMessages.Welsh).foreach { messagesForLanguage =>

      s"when being rendered in lang code of '${messagesForLanguage.lang.code}'" - {

        implicit lazy val msgs: Messages = messages(app, messagesForLanguage.lang)

        "when the show action link boolean is true" - {

          "when there is no answer" in {
            implicit lazy val request = dataRequest(FakeRequest(), emptyUserAnswers)

            FirstTransporterVatSummary.row(showActionLinks = true) mustBe
              SummaryListRowViewModel(
                key = messagesForLanguage.cyaLabel,
                value = Value(Text(messagesForLanguage.notProvided)),
                actions = Seq(
                  ActionItemViewModel(
                    content = messagesForLanguage.change,
                    href = controllers.sections.firstTransporter.routes.FirstTransporterVatController.onPageLoad(testErn, testDraftId, CheckMode).url,
                    id = "changeFirstTransporterVat"
                  ).withVisuallyHiddenText(messagesForLanguage.cyaChangeHidden)
                )
              )
          }

          "when there is an answer" in {
            implicit lazy val request = dataRequest(FakeRequest(), emptyUserAnswers.set(FirstTransporterVatPage, testVatNumber))

            FirstTransporterVatSummary.row(showActionLinks = true) mustBe SummaryListRowViewModel(
              key = messagesForLanguage.cyaLabel,
              value = Value(Text(testVatNumber)),
              actions = Seq(
                ActionItemViewModel(
                  content = messagesForLanguage.change,
                  href = controllers.sections.firstTransporter.routes.FirstTransporterVatController.onPageLoad(testErn, testDraftId, CheckMode).url,
                  id = "changeFirstTransporterVat"
                ).withVisuallyHiddenText(messagesForLanguage.cyaChangeHidden)
              )
            )
          }
        }

        "when the show action link boolean is false" - {

          "when there is no answer" in {
            implicit lazy val request = dataRequest(FakeRequest(), emptyUserAnswers)

            FirstTransporterVatSummary.row(showActionLinks = false) mustBe
              SummaryListRowViewModel(
                key = messagesForLanguage.cyaLabel,
                value = Value(Text(messagesForLanguage.notProvided)),
                actions = Seq()
              )
          }

          "when there is an answer" in {
            implicit lazy val request = dataRequest(FakeRequest(), emptyUserAnswers.set(FirstTransporterVatPage, testVatNumber))

            FirstTransporterVatSummary.row(showActionLinks = false) mustBe SummaryListRowViewModel(
              key = messagesForLanguage.cyaLabel,
              value = Value(Text(testVatNumber)),
              actions = Seq()
            )
          }
        }
      }
    }
  }
}