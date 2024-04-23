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
import fixtures.UserAddressFixtures
import fixtures.messages.sections.dispatch.DispatchCheckAnswersMessages
import models.requests.DataRequest
import models.{CheckMode, UserAnswers}
import org.scalatest.matchers.must.Matchers
import pages.sections.dispatch.DispatchAddressPage
import play.api.i18n.Messages
import play.api.test.FakeRequest
import uk.gov.hmrc.govukfrontend.views.Aliases.Value
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.{Key, SummaryListRow}
import viewmodels.govuk.summarylist._

class DispatchAddressSummarySpec extends SpecBase with Matchers with UserAddressFixtures {

  class Test(userAnswers: UserAnswers) {
    implicit lazy val request: DataRequest[_] = dataRequest(FakeRequest(), userAnswers)

    def expectedRow(value: Value)(implicit messagesForLanguage: DispatchCheckAnswersMessages.ViewMessages): SummaryListRow =
      SummaryListRowViewModel(
        key = Key(Text(messagesForLanguage.addressLabel)),
        value = value,
        actions = Seq(
          ActionItemViewModel(
            content = Text(messagesForLanguage.change),
            href = controllers.sections.dispatch.routes.DispatchAddressController.onPageLoad(testErn, testDraftId, CheckMode).url,
            id = "changeDispatchAddress"
          ).withVisuallyHiddenText(messagesForLanguage.addressChangeHidden)
        )
      )
  }

  "DispatchBusinessAddressSummary" - {

    Seq(DispatchCheckAnswersMessages.English).foreach { implicit messagesForLanguage =>

      s"when being rendered in lang code of '${messagesForLanguage.lang.code}'" - {

        implicit val msgs: Messages = messages(Seq(messagesForLanguage.lang))

        "must output 'Not Provided' row" - {
          "when there's no answer for DispatchAddressPage" in new Test(emptyUserAnswers) {
            DispatchAddressSummary.row() mustBe expectedRow(ValueViewModel(Text(messagesForLanguage.notProvided)))
          }
        }

        s"must output the expected row for DispatchAddress" - {

          "when there's an answer for DispatchAddressPage" in new Test(
            emptyUserAnswers.set(DispatchAddressPage, userAddressModelMax)
          ) {
            DispatchAddressSummary.row() mustBe expectedRow(ValueViewModel(userAddressModelMax.toCheckYourAnswersFormat))
          }
        }
      }
    }
  }
}
