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
import fixtures.UserAddressFixtures
import models.UserAnswers
import models.requests.DataRequest
import pages.sections.consignor.{ConsignorAddressPage, ConsignorPaidTemporaryAuthorisationCodePage}
import play.api.i18n.Messages
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryList
import viewmodels.govuk.summarylist._

class ConsignorCheckAnswersHelperSpec extends SpecBase with UserAddressFixtures {

  val baseUserAnswers: UserAnswers = emptyUserAnswers.set(ConsignorAddressPage, testUserAddress)

  class Setup(userAnswers: UserAnswers, ern: String = testErn) {
    implicit val fakeDataRequest: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(), userAnswers, ern)
    implicit val msgs: Messages = messages(FakeRequest())
    lazy val checkAnswersHelper: ConsignorCheckAnswersHelper = app.injector.instanceOf[ConsignorCheckAnswersHelper]
  }

  "ConsignorCheckAnswersHelper" - {

    ".summaryList" - {

      "should return the correct rows for an XIPA user" in new Setup(
        baseUserAnswers.set(ConsignorPaidTemporaryAuthorisationCodePage, testNICertifiedConsignorErn), ern = testNICertifiedConsignorErn
      ) {

        val expectedResult: SummaryList = SummaryList(Seq(
          Some(ConsignorTraderNameSummary.row),
          ConsignorPaidTemporaryAuthorisationCodeSummary.row,
          ConsignorAddressSummary.row,
        ).flatten).withCssClass("govuk-!-margin-bottom-9")

        checkAnswersHelper.summaryList() mustBe expectedResult
      }

      "should return the correct rows for a non-XIPA user" in new Setup(baseUserAnswers) {

        val expectedResult: SummaryList = SummaryList(Seq(
          Some(ConsignorTraderNameSummary.row),
          ConsignorERNSummary.row,
          ConsignorAddressSummary.row,
        ).flatten).withCssClass("govuk-!-margin-bottom-9")

        checkAnswersHelper.summaryList() mustBe expectedResult
      }
    }
  }

}
