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

package viewmodels.checkAnswers.sections.consignee

import base.SpecBase
import models.UserAnswers
import models.requests.DataRequest
import org.scalamock.scalatest.MockFactory
import pages.sections.consignee.ConsigneeExportEoriPage
import play.api.i18n.Messages
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.{SummaryList, SummaryListRow}

class ConsigneeCheckAnswersHelperSpec extends SpecBase with MockFactory {

  class Setup(userAnswers: UserAnswers) {
    implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(), userAnswers)
    implicit val msgs: Messages = messages(FakeRequest())
    lazy val helper = new ConsigneeCheckAnswersHelper
  }

  "ConsigneeCheckAnswersHelper" - {

    ".summaryList" - {

      "output a list when the EORI number has been supplied" in
        new Setup(
          emptyUserAnswers
            .set(ConsigneeExportEoriPage, "GB123456123456")
        ) {

          val maybeSummaryRow: Option[SummaryListRow] = ConsigneeExportEoriSummary.row(true)
          maybeSummaryRow.isDefined mustBe true

          helper.summaryList()(implicitly, messages(request)) mustBe SummaryList(
            rows = Seq(maybeSummaryRow.get)
          )
        }

      "output an empty list when there is no EORI number" in
        new Setup(emptyUserAnswers) {

          helper.summaryList()(implicitly, messages(request)) mustBe SummaryList(
            rows = Seq.empty
          )
        }

    }

  }

}
