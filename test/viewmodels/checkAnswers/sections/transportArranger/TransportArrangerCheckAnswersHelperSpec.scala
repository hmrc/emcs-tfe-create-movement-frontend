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

package viewmodels.checkAnswers.sections.transportArranger

import base.SpecBase
import fixtures.messages.sections.transportArranger.TransportArrangerMessages
import models.VatNumberModel
import models.requests.DataRequest
import models.sections.transportArranger.TransportArranger
import models.sections.transportArranger.TransportArranger.{GoodsOwner, Other}
import org.scalamock.scalatest.MockFactory
import pages.sections.transportArranger.{TransportArrangerPage, TransportArrangerVatPage}
import play.api.i18n.{Messages, MessagesApi}
import play.api.test.FakeRequest
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryList
import viewmodels.govuk.all.CardViewModel

class TransportArrangerCheckAnswersHelperSpec extends SpecBase with MockFactory {

  trait Test {
    implicit val msgs: Messages = app.injector.instanceOf[MessagesApi].preferred(Seq(TransportArrangerMessages.English.lang))
    val helper = new TransportArrangerCheckAnswersHelper()
  }

  val vatNumberInputModel: VatNumberModel = VatNumberModel(hasVatNumber = true, Some(testVatNumber))

  "summaryList" - {
    TransportArranger.values.foreach {
      case value@(GoodsOwner | Other) =>
        // Only GoodsOwner or Other contain the VAT reg row
        "must render five rows" - {
          s"when TransportArranger value is $value" in new Test {
            implicit val request: DataRequest[_] = dataRequest(
              FakeRequest(),
              emptyUserAnswers
                .set(TransportArrangerPage, value)
                .set(TransportArrangerVatPage, vatNumberInputModel)
            )
            helper.summaryList()(request, msgs).rows.length mustBe 4
          }
        }
      case value =>
        "must render three rows" - {
          s"when TransportArranger value is $value" in new Test {
            implicit val request: DataRequest[_] = dataRequest(
              FakeRequest(),
              emptyUserAnswers
                .set(TransportArrangerPage, value)
                .set(TransportArrangerVatPage, vatNumberInputModel)
            )
            helper.summaryList()(request, msgs).rows.length mustBe 2
          }
        }
        "must render card layout" - {
          s"when TransportArranger value is $value and asCard is `true`" in new Test {
            implicit val request: DataRequest[_] = dataRequest(
              FakeRequest(),
              emptyUserAnswers
                .set(TransportArrangerPage, value)
                .set(TransportArrangerVatPage, vatNumberInputModel)
            )
            helper.summaryList(asCard = true)(request, msgs) mustBe SummaryList(
              rows = Seq(
                TransportArrangerSummary.row(),
                TransportArrangerVatChoiceSummary.row(),
                TransportArrangerVatSummary.row(),
                Some(TransportArrangerAddressSummary.row())
              ).flatten,
              card = Some(CardViewModel(
                title = "Transport arranger",
                headingLevel = 2,
                actions = None
              ))
            )
          }
        }
    }
  }
}
