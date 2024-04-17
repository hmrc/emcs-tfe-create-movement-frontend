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

import fixtures.messages.sections.consignor.CheckYourAnswersConsignorMessages
import base.SpecBase
import models.CheckMode
import org.scalatest.matchers.must.Matchers
import pages.sections.consignor.ConsignorAddressPage
import play.api.i18n.Messages
import play.api.test.FakeRequest
import play.twirl.api.{Html, HtmlFormat}
import uk.gov.hmrc.govukfrontend.views.Aliases.Value
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.{HtmlContent, Text}
import viewmodels.govuk.summarylist._
import viewmodels.implicits._

class ConsignorAddressSummarySpec extends SpecBase with Matchers {

  "ConsignorAddressSummary" - {

    Seq(CheckYourAnswersConsignorMessages.English).foreach { messagesForLanguage =>

      s"when being rendered in lang code of '${messagesForLanguage.lang.code}'" - {

        implicit val msgs: Messages = messages(Seq(messagesForLanguage.lang))

        "when there's no answer" - {

          "must output the expected data" in {

            implicit lazy val request = dataRequest(FakeRequest(), emptyUserAnswers)

            ConsignorAddressSummary.row() mustBe None
          }
        }

        "when there's an answer" - {

          "must output the expected row" in {

            implicit lazy val request = dataRequest(FakeRequest(), emptyUserAnswers.set(ConsignorAddressPage, testUserAddress))

            val expectedValue = HtmlContent(
              HtmlFormat.fill(
                Seq(
                  Html(testUserAddress.property.fold("")(_ + " ") + testUserAddress.street + "<br>"),
                  Html(testUserAddress.town + "<br>"),
                  Html(testUserAddress.postcode),
                )
              )
            )

            ConsignorAddressSummary.row() mustBe
              Some(
                SummaryListRowViewModel(
                  key = messagesForLanguage.address,
                  value = Value(expectedValue),
                  actions = Seq(ActionItemViewModel(
                    content = Text(messagesForLanguage.change),
                    href = controllers.sections.consignor.routes.ConsignorAddressController.onPageLoad(testErn, testDraftId, CheckMode).url,
                    id = "changeConsignorAddress"
                  ).withVisuallyHiddenText(messagesForLanguage.addressChangeHidden))
                )
              )
          }
        }
      }
    }
  }
}
