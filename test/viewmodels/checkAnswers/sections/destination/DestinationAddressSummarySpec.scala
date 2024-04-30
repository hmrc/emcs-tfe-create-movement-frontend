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

package viewmodels.checkAnswers.sections.destination

import base.SpecBase
import fixtures.UserAddressFixtures
import fixtures.messages.sections.destination.DestinationAddressMessages
import models.CheckMode
import org.scalatest.matchers.must.Matchers
import pages.sections.destination.{DestinationAddressPage, DestinationConsigneeDetailsPage}
import play.api.i18n.Messages
import play.api.test.FakeRequest
import uk.gov.hmrc.govukfrontend.views.Aliases.{Text, Value}
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.HtmlContent
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.{Key, SummaryListRow}
import viewmodels.govuk.summarylist._

class DestinationAddressSummarySpec extends SpecBase with Matchers with UserAddressFixtures {

  "DestinationAddressSummary" - {

    Seq(DestinationAddressMessages.English).foreach { implicit messagesForLanguage =>

      s"when being rendered in lang code of '${messagesForLanguage.lang.code}'" - {

        implicit val msgs: Messages = messages(Seq(messagesForLanguage.lang))

        "when Destination Address has NOT been answered" - {

          "must output a Not Provided row" in {

            implicit lazy val request = dataRequest(FakeRequest(), emptyUserAnswers)

            DestinationAddressSummary.row() mustBe expectedSummary(Value(HtmlContent(messagesForLanguage.cyaDestinationNotProvided)))
          }
        }

        "when Destination Address has been answered" - {

          "must output the expected row" in {

            implicit lazy val request = dataRequest(FakeRequest(), emptyUserAnswers
              .set(DestinationConsigneeDetailsPage, false)
              .set(DestinationAddressPage, userAddressModelMax)
            )

            DestinationAddressSummary.row() mustBe expectedSummary(Value(userAddressModelMax.toCheckYourAnswersFormat))
          }
        }
      }
    }
  }

  private def expectedSummary(value: Value)(implicit messagesForLanguage: DestinationAddressMessages.ViewMessages): SummaryListRow =
    SummaryListRowViewModel(
      key = Key(Text(messagesForLanguage.cyaLabel)),
      value = value,
      actions = Seq(
        ActionItemViewModel(
          content = Text(messagesForLanguage.change),
          href = controllers.sections.destination.routes.DestinationAddressController.onPageLoad(testErn, testDraftId, CheckMode).url,
          id = "changeDestinationAddress"
        ).withVisuallyHiddenText(messagesForLanguage.cyaChangeHidden)
      )
    )
}
