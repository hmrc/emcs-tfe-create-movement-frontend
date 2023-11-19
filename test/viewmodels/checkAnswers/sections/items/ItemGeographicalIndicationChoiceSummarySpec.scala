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
import fixtures.messages.sections.items.ItemGeographicalIndicationChoiceMessages
import models.CheckMode
import models.sections.items.ItemGeographicalIndicationType._
import org.scalatest.matchers.must.Matchers
import pages.sections.items.ItemGeographicalIndicationChoicePage
import play.api.i18n.Messages
import play.api.test.FakeRequest
import uk.gov.hmrc.govukfrontend.views.Aliases.{Text, Value}
import viewmodels.govuk.summarylist._
import viewmodels.implicits._

class ItemGeographicalIndicationChoiceSummarySpec extends SpecBase with Matchers {

  "ItemGeographicalIndicationChoiceSummary" - {

    Seq(ItemGeographicalIndicationChoiceMessages.English).foreach { messagesForLanguage =>

      s"when being rendered in lang code of '${messagesForLanguage.lang.code}'" - {

        implicit val msgs: Messages = messages(Seq(messagesForLanguage.lang))

        "when there's no answer" - {

          "must output None" in {
            implicit lazy val request = dataRequest(FakeRequest(), emptyUserAnswers)

            ItemGeographicalIndicationChoiceSummary.row(testIndex1) mustBe None
          }

        }

        "when there's an answer" - {

          def sampleSummaryRow(text: String) = SummaryListRowViewModel(
            key = messagesForLanguage.cyaLabel,
            value = Value(Text(text)),
            actions = Seq(
              ActionItemViewModel(
                content = messagesForLanguage.change,
                href = controllers.sections.items.routes.ItemGeographicalIndicationChoiceController.onPageLoad(testErn, testDraftId, testIndex1, CheckMode).url,
                id = "changeItemGeographicalIndicationChoice1"
              ).withVisuallyHiddenText(messagesForLanguage.cyaChangeHidden)
            )
          )

          "must output None - when 'No' is selected" in {
            implicit lazy val request = dataRequest(FakeRequest(),
              emptyUserAnswers.set(ItemGeographicalIndicationChoicePage(testIndex1), NoGeographicalIndication))
            ItemGeographicalIndicationChoiceSummary.row(testIndex1) mustBe None
          }

          "must output the expected row" - {

            Seq(ProtectedDesignationOfOrigin -> "Protected Designation of Origin (PDO)",
              ProtectedGeographicalIndication -> "Protected Geographical Indication (PGI)",
              GeographicalIndication -> "Geographical Indication (GI)"
            ).foreach( geographicalIndicationTypeAndDisplayText =>
              s"when the answer is ${geographicalIndicationTypeAndDisplayText._1}" in {
                implicit lazy val request = dataRequest(FakeRequest(),
                  emptyUserAnswers.set(ItemGeographicalIndicationChoicePage(testIndex1), geographicalIndicationTypeAndDisplayText._1))
                ItemGeographicalIndicationChoiceSummary.row(testIndex1) mustBe Some(sampleSummaryRow(geographicalIndicationTypeAndDisplayText._2))
              }
            )
          }
        }
      }
    }
  }
}
