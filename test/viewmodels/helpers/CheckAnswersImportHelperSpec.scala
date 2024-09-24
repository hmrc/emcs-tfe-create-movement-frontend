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

package viewmodels.helpers

import base.SpecBase
import models.UserAnswers
import models.requests.DataRequest
import pages.sections.importInformation.ImportCustomsOfficeCodePage
import play.api.i18n.Messages
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.{SummaryList, SummaryListRow}
import viewmodels.checkAnswers.sections.importInformation.ImportCustomsOfficeCodeSummary
import viewmodels.govuk.all.CardViewModel

class CheckAnswersImportHelperSpec extends SpecBase {

  class Setup(ern: String = testErn) {
    lazy val importCustomsOfficeCodeSummary = app.injector.instanceOf[ImportCustomsOfficeCodeSummary]
    lazy val checkAnswersImportHelper = app.injector.instanceOf[CheckYourAnswersImportHelper]
    val userAnswers: UserAnswers = emptyUserAnswers.set(ImportCustomsOfficeCodePage, "AB123456")
    implicit val fakeDataRequest: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(), userAnswers)
    implicit val msgs: Messages = messages(fakeDataRequest)
  }

  "CheckAnswersConsigneeHelper" - {

    ".buildSummaryRows" - {

      "should return the correct rows" - {

        "when rendering as a card" in new Setup() {

          val expectedSummaryListRows: Seq[SummaryListRow] = Seq(
            importCustomsOfficeCodeSummary.row(true)(fakeDataRequest, msgs),
          ).flatten

          checkAnswersImportHelper.summaryList(asCard = true) mustBe SummaryList(
            rows = expectedSummaryListRows,
            card = Some(CardViewModel(
              title = "Import",
              headingLevel = 2,
              actions = None
            ))
          )
        }

        "when given a customs office import code" in new Setup() {

          val expectedSummaryListRows: Seq[SummaryListRow] = Seq(
            importCustomsOfficeCodeSummary.row(true)(fakeDataRequest, msgs),
          ).flatten

          checkAnswersImportHelper.summaryList() mustBe SummaryList(rows = expectedSummaryListRows)
        }
      }
    }
  }
}
