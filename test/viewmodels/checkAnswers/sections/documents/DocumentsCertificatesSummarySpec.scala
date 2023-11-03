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

package viewmodels.checkAnswers.sections.documents

import base.SpecBase
import fixtures.messages.sections.documents.DocumentsCertificatesMessages
import models.CheckMode
import org.scalatest.matchers.must.Matchers
import pages.sections.documents.DocumentsCertificatesPage
import play.api.i18n.Messages
import play.api.test.FakeRequest
import uk.gov.hmrc.govukfrontend.views.Aliases.Value
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text
import viewmodels.govuk.summarylist._
import viewmodels.implicits._

class DocumentsCertificatesSummarySpec extends SpecBase with Matchers {

  "DocumentsCertificatesSummary" - {

    lazy val app = applicationBuilder().build()

    Seq(DocumentsCertificatesMessages.English).foreach { messagesForLanguage =>

      s"when being rendered in lang code of '${messagesForLanguage.lang.code}'" - {

        implicit lazy val msgs: Messages = messages(app, messagesForLanguage.lang)

        "when there's no answer" - {

          "must output no row" in {

            implicit lazy val request = dataRequest(FakeRequest(), emptyUserAnswers)

            DocumentsCertificatesSummary.row(request.userAnswers) mustBe None
          }
        }

        "when there's an answer" - {

          "must output the expected row when user answers yes" in {

            implicit lazy val request = dataRequest(FakeRequest(), emptyUserAnswers.set(DocumentsCertificatesPage, true))

            DocumentsCertificatesSummary.row(request.userAnswers) mustBe
              Some(
                SummaryListRowViewModel(
                  key = messagesForLanguage.cyaLabel,
                  value = Value(Text(messagesForLanguage.yes)),
                  actions = Seq(
                    ActionItemViewModel(
                      content = messagesForLanguage.change,
                      href = controllers.sections.documents.routes.DocumentsCertificatesController.onPageLoad(testErn, testDraftId, CheckMode).url,
                      id = "changeDocumentsCertificates"
                    ).withVisuallyHiddenText(messagesForLanguage.cyaChangeHidden)
                  )
                )
              )
          }

          "must output the expected row when user answers no" in {

            implicit lazy val request = dataRequest(FakeRequest(), emptyUserAnswers.set(DocumentsCertificatesPage, false))

            DocumentsCertificatesSummary.row(request.userAnswers) mustBe
              Some(
                SummaryListRowViewModel(
                  key = messagesForLanguage.cyaLabel,
                  value = Value(Text(messagesForLanguage.no)),
                  actions = Seq(
                    ActionItemViewModel(
                      content = messagesForLanguage.change,
                      href = controllers.sections.documents.routes.DocumentsCertificatesController.onPageLoad(testErn, testDraftId, CheckMode).url,
                      id = "changeDocumentsCertificates"
                    ).withVisuallyHiddenText(messagesForLanguage.cyaChangeHidden)
                  )
                )
              )
          }
        }
      }
    }
  }
}