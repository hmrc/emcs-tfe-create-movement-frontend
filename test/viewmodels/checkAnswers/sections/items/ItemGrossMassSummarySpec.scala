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
import fixtures.messages.sections.items.ItemNetGrossMassMessages
import models.CheckMode
import models.requests.DataRequest
import models.sections.items.ItemNetGrossMassModel
import org.scalatest.matchers.must.Matchers
import pages.sections.items.ItemNetGrossMassPage
import play.api.i18n.Messages
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import uk.gov.hmrc.govukfrontend.views.Aliases.{Text, Value}
import viewmodels.govuk.summarylist._
import viewmodels.implicits._

class ItemGrossMassSummarySpec extends SpecBase with Matchers {

  "ItemGrossMassSummary" - {

    lazy val app = applicationBuilder().build()

    val messagesForLanguage = ItemNetGrossMassMessages.English


    s"when being rendered in lang code of 'en'" - {

      implicit lazy val msgs: Messages = messages(app, messagesForLanguage.lang)

      "when there's no answer" - {

        "must output None" in {
          implicit lazy val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(), emptyUserAnswers)

          ItemGrossMassSummary.row(testIndex1) mustBe None
        }
      }

      "when there's an answer" - {

        "must output the expected row" in {

          implicit lazy val request: DataRequest[AnyContentAsEmpty.type] =
            dataRequest(FakeRequest(), emptyUserAnswers.set(ItemNetGrossMassPage(testIndex1), ItemNetGrossMassModel(BigDecimal("999.736"), BigDecimal("71.736"))))

          ItemGrossMassSummary.row(testIndex1) mustBe Some(
            SummaryListRowViewModel(
              key = messagesForLanguage.cyaGrossMassLabel,
              value = Value(Text(s"71.736 kg")),
              actions = Seq(
                ActionItemViewModel(
                  content = messagesForLanguage.change,
                  href = controllers.sections.items.routes.ItemNetGrossMassController.onPageLoad(testErn, testDraftId, testIndex1, CheckMode).url,
                  id = "changeGrossMass"
                ).withVisuallyHiddenText(messagesForLanguage.cyaGrossMassChangeHidden)
              )
            )
          )
        }
      }
    }
  }
}
