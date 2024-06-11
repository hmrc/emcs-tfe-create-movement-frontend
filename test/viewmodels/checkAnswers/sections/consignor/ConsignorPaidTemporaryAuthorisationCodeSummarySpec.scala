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

package viewmodels.checkAnswers.sections.consignor

import base.SpecBase
import fixtures.messages.sections.consignor.CheckYourAnswersConsignorMessages
import models.CheckMode
import org.scalatest.matchers.must.Matchers
import pages.sections.consignor.ConsignorPaidTemporaryAuthorisationCodePage
import play.api.i18n.Messages
import play.api.test.FakeRequest
import uk.gov.hmrc.govukfrontend.views.Aliases.Value
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text
import viewmodels.govuk.summarylist._
import viewmodels.implicits._

class ConsignorPaidTemporaryAuthorisationCodeSummarySpec extends SpecBase with Matchers {

  "ConsignorPaidTemporaryAuthorisationCodeSummary" - {

    Seq(CheckYourAnswersConsignorMessages.English).foreach { messagesForLanguage =>

      s"when being rendered in lang code of '${messagesForLanguage.lang.code}'" - {

        implicit val msgs: Messages = messages(Seq(messagesForLanguage.lang))

        "when the user is a non-XIPA trader" - {

          "must output the expected data" in {

            implicit lazy val request = dataRequest(FakeRequest(), emptyUserAnswers, ern = testNITemporaryCertifiedConsignorErn)

            ConsignorPaidTemporaryAuthorisationCodeSummary.row() mustBe None
          }
        }

        "when the user is an XIPA trader but there is no answer for the page" - {

          "must output the expected data" in {

            implicit lazy val request = dataRequest(FakeRequest(), emptyUserAnswers, ern = testNICertifiedConsignorErn)

            ConsignorPaidTemporaryAuthorisationCodeSummary.row() mustBe None
          }
        }

        "when the user is an XIPA trader" - {

          "must output the expected row" in {

            implicit lazy val request = dataRequest(FakeRequest(), emptyUserAnswers
              .set(ConsignorPaidTemporaryAuthorisationCodePage, testNICertifiedConsignorErn),
              ern = testNICertifiedConsignorErn
            )

            ConsignorPaidTemporaryAuthorisationCodeSummary.row() mustBe
              Some(
                SummaryListRowViewModel(
                  key = messagesForLanguage.paidTemporaryAuthorisationCode,
                  value = Value(Text(testNICertifiedConsignorErn)),
                  actions = Seq(ActionItemViewModel(
                    content = Text(messagesForLanguage.change),
                    href = controllers.sections.consignor.routes.ConsignorPaidTemporaryAuthorisationCodeController.onPageLoad(testNICertifiedConsignorErn, testDraftId, CheckMode).url,
                    id = "changeConsignorPaidTemporaryAuthorisationCode"
                  ).withVisuallyHiddenText(messagesForLanguage.paidTemporaryAuthorisationCodeChangeHidden))
                )
              )
          }
        }
      }
    }
  }
}
