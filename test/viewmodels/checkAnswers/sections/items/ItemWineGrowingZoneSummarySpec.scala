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
import fixtures.messages.sections.items.ItemWineGrowingZoneMessages
import models.CheckMode
import models.sections.items.ItemWineGrowingZone.CIII_A
import org.scalatest.matchers.must.Matchers
import pages.sections.items.ItemWineGrowingZonePage
import play.api.i18n.Messages
import play.api.test.FakeRequest
import uk.gov.hmrc.govukfrontend.views.Aliases.Value
import viewmodels.govuk.summarylist._
import viewmodels.implicits._

class ItemWineGrowingZoneSummarySpec extends SpecBase with Matchers {

  "ItemWineGrowingZoneSummary" - {

    Seq(ItemWineGrowingZoneMessages.English).foreach { messagesForLanguage =>

      s"when being rendered in lang code of '${messagesForLanguage.lang.code}'" - {

        implicit val msgs: Messages = messages(Seq(messagesForLanguage.lang))

        "when there's no answer" - {

          "must output None" in {
            implicit lazy val request = dataRequest(FakeRequest(), emptyUserAnswers)

            ItemWineGrowingZoneSummary.row(testIndex1) mustBe None
          }

        }

        "when there's an answer" - {

          "must output the expected row" in {
            implicit lazy val request = dataRequest(FakeRequest(), emptyUserAnswers.set(
              ItemWineGrowingZonePage(testIndex1), CIII_A)
            )

            ItemWineGrowingZoneSummary.row(testIndex1) mustBe
              Some(
                SummaryListRowViewModel(
                  key = messagesForLanguage.cyaLabel,
                  value = Value(messagesForLanguage.ciii_a),
                  actions = Seq(
                    ActionItemViewModel(
                      content = messagesForLanguage.change,
                      href = controllers.sections.items.routes.ItemWineGrowingZoneController.onPageLoad(testErn, testDraftId, testIndex1, CheckMode).url,
                      id = "changeItemWineGrowingZone1"
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
