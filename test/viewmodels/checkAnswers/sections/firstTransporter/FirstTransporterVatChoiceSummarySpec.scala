/*
 * Copyright 2024 HM Revenue & Customs
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

package viewmodels.checkAnswers.sections.firstTransporter

import base.SpecBase
import fixtures.messages.sections.firstTransporter.FirstTransporterVatMessages
import models.{CheckMode, VatNumberModel}
import org.scalatest.matchers.must.Matchers
import pages.sections.firstTransporter.FirstTransporterVatPage
import play.api.i18n.{Messages, MessagesApi}
import play.api.test.FakeRequest
import uk.gov.hmrc.govukfrontend.views.Aliases.Value
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text
import viewmodels.govuk.summarylist._
import viewmodels.implicits._


class FirstTransporterVatChoiceSummarySpec extends SpecBase with Matchers {
  "FirstTransporterVatChoiceSummary" - {
    Seq(FirstTransporterVatMessages.English).foreach { messagesForLanguage =>

      s"when being rendered in lang code of '${messagesForLanguage.lang.code}'" - {

        implicit val msgs: Messages = app.injector.instanceOf[MessagesApi].preferred(Seq(messagesForLanguage.lang))

        "when the answer is 'No'" in {
          implicit lazy val request = dataRequest(FakeRequest(), emptyUserAnswers.set(FirstTransporterVatPage, VatNumberModel(false, None)))

          FirstTransporterVatChoiceSummary.row() mustBe Some(SummaryListRowViewModel(
            key = messagesForLanguage.vatChoiceCyaLabel,
            value = Value(Text("No")),
            actions = Seq(
              ActionItemViewModel(
                content = messagesForLanguage.change,
                href = controllers.sections.firstTransporter.routes.FirstTransporterVatController.onPageLoad(testErn, testDraftId, CheckMode).url,
                id = "changeFirstTransporterVatChoice"
              ).withVisuallyHiddenText(messagesForLanguage.cyaChangeChoiceHidden)
            )
          ))
        }

        "when the answer is 'Yes'" in {
          implicit lazy val request = dataRequest(FakeRequest(), emptyUserAnswers.set(FirstTransporterVatPage, VatNumberModel(true, Some(testVatNumber))))

          FirstTransporterVatChoiceSummary.row() mustBe Some(SummaryListRowViewModel(
            key = messagesForLanguage.vatChoiceCyaLabel,
            value = Value(Text("Yes")),
            actions = Seq(
              ActionItemViewModel(
                content = messagesForLanguage.change,
                href = controllers.sections.firstTransporter.routes.FirstTransporterVatController.onPageLoad(testErn, testDraftId, CheckMode).url,
                id = "changeFirstTransporterVatChoice"
              ).withVisuallyHiddenText(messagesForLanguage.cyaChangeChoiceHidden)
            )
          ))
        }

        "when there is no answer" in {
          implicit lazy val request = dataRequest(FakeRequest(), emptyUserAnswers)

          FirstTransporterVatChoiceSummary.row() mustBe None
        }
      }
    }
  }
}
