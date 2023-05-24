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

package viewmodels

import base.SpecBase
import uk.gov.hmrc.govukfrontend.views.Aliases.SummaryList
import viewmodels.checkAnswers.CheckAnswersHelper
import viewmodels.govuk.summarylist._

class CheckAnswersHelperSpec extends SpecBase {

  lazy val checkAnswersHelper = new CheckAnswersHelper()

  lazy val app = applicationBuilder().build()
  implicit lazy val msgs = messages(app)

  "CheckAnswersHelper" - {

    s"must return the expected SummaryList" in {

      checkAnswersHelper.summaryList() mustBe
        SummaryList(Seq()).withCssClass("govuk-!-margin-bottom-9")
    }
  }
}
