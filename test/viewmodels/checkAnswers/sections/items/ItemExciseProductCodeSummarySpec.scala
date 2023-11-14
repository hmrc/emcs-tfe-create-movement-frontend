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

package viewmodels.checkAnswers.sections.items

import base.SpecBase
import fixtures.messages.sections.items.ItemExciseProductCodeMessages
import models.NormalMode
import org.scalatest.matchers.must.Matchers
import pages.sections.items.ItemExciseProductCodePage
import play.api.i18n.Messages
import play.api.test.FakeRequest
import play.twirl.api.{Html, HtmlFormat}
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.HtmlContent
import viewmodels.govuk.summarylist._
import viewmodels.implicits._

class ItemExciseProductCodeSummarySpec extends SpecBase with Matchers {

  "ItemExciseProductCodeSummary" - {

    Seq(ItemExciseProductCodeMessages.English).foreach { messagesForLanguage =>

      s"when being rendered in lang code of '${messagesForLanguage.lang.code}'" - {

        implicit val msgs: Messages = messages(Seq(messagesForLanguage.lang))

        "when there's no answer" - {

          "must output the expected data" in {
            implicit lazy val request = dataRequest(FakeRequest(), emptyUserAnswers)

            ItemExciseProductCodeSummary.row(testIndex1) mustBe None
          }
        }

        "when there's an answer" - {

          "must output the expected row" in {
            implicit lazy val request = dataRequest(FakeRequest(), emptyUserAnswers.set(ItemExciseProductCodePage(testIndex1), testExciseProductCodeB000))

            ItemExciseProductCodeSummary.row(testIndex1) mustBe Some(
              SummaryListRowViewModel(
                key = messagesForLanguage.cyaLabel,
                value = ValueViewModel(HtmlContent(HtmlFormat.fill(Seq(
                  Html("B000" + "<br>"),
                  Html("Beer")
                )))),
                actions = Seq(
                  ActionItemViewModel(
                    content = messagesForLanguage.change,
                    href = controllers.sections.items.routes.ItemExciseProductCodeController.onPageLoad(testErn, testDraftId, testIndex1, NormalMode).url,
                    id = "changeItemExciseProductCode1"
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
