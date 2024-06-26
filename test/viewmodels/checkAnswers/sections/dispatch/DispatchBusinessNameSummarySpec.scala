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

package viewmodels.checkAnswers.sections.dispatch

import base.SpecBase
import fixtures.messages.sections.dispatch.DispatchCheckAnswersMessages
import models.requests.DataRequest
import models.{CheckMode, UserAnswers}
import org.scalatest.matchers.must.Matchers
import pages.sections.dispatch.{DispatchBusinessNamePage, DispatchUseConsignorDetailsPage}
import play.api.i18n.Messages
import play.api.test.FakeRequest
import uk.gov.hmrc.govukfrontend.views.Aliases.Value
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.{Key, SummaryListRow}
import viewmodels.govuk.summarylist._

class DispatchBusinessNameSummarySpec extends SpecBase with Matchers {

  class Test(userAnswers: UserAnswers) {
    implicit lazy val request: DataRequest[_] = dataRequest(FakeRequest(), userAnswers)

    def expectedRow(value: String, withChangeLinks: Boolean = true)
                   (implicit messagesForLanguage: DispatchCheckAnswersMessages.ViewMessages): SummaryListRow =
      SummaryListRowViewModel(
        key = Key(Text(messagesForLanguage.traderNameLabel)),
        value = Value(Text(value)),
        actions = if(!withChangeLinks) Seq() else Seq(
          ActionItemViewModel(
            content = Text(messagesForLanguage.change),
            href = controllers.sections.dispatch.routes.DispatchBusinessNameController.onPageLoad(testErn, testDraftId, CheckMode).url,
            id = "changeDispatchBusinessName"
          ).withVisuallyHiddenText(messagesForLanguage.traderNameChangeHidden)
        )
      )
  }

  "DispatchBusinessAddressSummary" - {

    Seq(DispatchCheckAnswersMessages.English).foreach { implicit messagesForLanguage =>

      s"when being rendered in lang code of '${messagesForLanguage.lang.code}'" - {

        implicit val msgs: Messages = messages(Seq(messagesForLanguage.lang))

        "must output 'Not Provided' row" - {
          "when there's no answer for DispatchBusinessNamePage" in new Test(emptyUserAnswers) {
            DispatchBusinessNameSummary.row() mustBe expectedRow(messagesForLanguage.notProvided)
          }
        }

        s"must output the expected row for DispatchBusinessName" - {

          "when DispatchUseConsignorDetailsPage is true" in new Test(emptyUserAnswers.set(DispatchUseConsignorDetailsPage, true)) {
            DispatchBusinessNameSummary.row() mustBe expectedRow(testMinTraderKnownFacts.traderName, withChangeLinks = false)
          }

          "when DispatchUseConsignorDetailsPage is false and there's an answer" in new Test(
            emptyUserAnswers
              .set(DispatchUseConsignorDetailsPage, false)
              .set(DispatchBusinessNamePage, testBusinessName)
          ) {
            DispatchBusinessNameSummary.row() mustBe expectedRow(testBusinessName)
          }

          "when DispatchUseConsignorDetailsPage is None and there's an answer" in new Test(
            emptyUserAnswers
              .set(DispatchBusinessNamePage, testBusinessName)
          ) {
            DispatchBusinessNameSummary.row() mustBe expectedRow(testBusinessName)
          }
        }
      }
    }
  }
}
