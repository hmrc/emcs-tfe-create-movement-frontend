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
import org.scalatest.matchers.must.Matchers
import play.api.i18n.Messages
import play.api.test.FakeRequest
import uk.gov.hmrc.govukfrontend.views.Aliases.Value
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text
import viewmodels.govuk.summarylist._
import viewmodels.implicits._

class ConsignorTraderNameSummarySpec extends SpecBase with Matchers {

  "ConsignorTraderNameSummary" - {

    Seq(CheckYourAnswersConsignorMessages.English).foreach { messagesForLanguage =>

      s"when being rendered in lang code of '${messagesForLanguage.lang.code}'" - {

        implicit val msgs: Messages = messages(Seq(messagesForLanguage.lang))

        "must output the expected row" in {

          implicit lazy val request = dataRequest(FakeRequest(), emptyUserAnswers)

          ConsignorTraderNameSummary.row() mustBe
            SummaryListRowViewModel(
              key = messagesForLanguage.traderName,
              value = Value(Text(request.traderKnownFacts.traderName)),
              actions = Seq()
            )
        }
      }
    }
  }
}
