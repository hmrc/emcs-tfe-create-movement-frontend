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
import controllers.sections.documents.routes
import fixtures.messages.sections.documents.DocumentReferenceMessages.English
import models.CheckMode
import org.scalatest.matchers.must.Matchers
import pages.sections.documents.DocumentReferencePage
import play.api.i18n.Messages
import play.api.test.FakeRequest
import uk.gov.hmrc.govukfrontend.views.Aliases.Value
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text
import viewmodels.govuk.summarylist._
import viewmodels.implicits._

class DocumentReferenceSummarySpec extends SpecBase with Matchers {

  "DocumentReferenceummary" - {

    lazy val app = applicationBuilder().build()

    s"when being rendered in lang code of '${English.lang.code}'" - {

      implicit lazy val msgs: Messages = messages(app, English.lang)

      "when there's no answer" - {

        "must output no row" in {

          implicit lazy val request = dataRequest(FakeRequest(), emptyUserAnswers)

          DocumentReferenceSummary.row(0) mustBe None
        }
      }

      "when there's an answer" - {

        "must output the expected row" in {

          val answer = "reference"

          implicit lazy val request = dataRequest(FakeRequest(), emptyUserAnswers.set(DocumentReferencePage(0), answer))

          DocumentReferenceSummary.row(0) mustBe
            Some(
              SummaryListRowViewModel(
                key = English.cyaLabel,
                value = Value(Text(answer)),
                actions = Seq(
                  ActionItemViewModel(
                    content = English.change,
                    href = routes.DocumentReferenceController.onPageLoad(testErn, testDraftId, 0, CheckMode).url,
                    id = "changeDocumentReference-1"
                  ).withVisuallyHiddenText(English.cyaChangeHidden)
                )
              )
            )
        }
      }
    }
  }
}