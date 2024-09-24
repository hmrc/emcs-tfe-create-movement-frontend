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
import models.UserAnswers
import models.requests.DataRequest
import pages.sections.dispatch.{DispatchAddressPage, DispatchUseConsignorDetailsPage, DispatchWarehouseExcisePage}
import play.api.i18n.Messages
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryList
import viewmodels.govuk.all.{CardViewModel, FluentSummaryList, SummaryListViewModel}

class DispatchCheckAnswersHelperSpec extends SpecBase with UserAddressFixtures {

  class Setup(userAnswers: UserAnswers) {
    implicit val fakeDataRequest: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(), userAnswers)
    implicit val msgs: Messages = messages(FakeRequest())
    lazy val dispatchWarehouseExciseSummary: DispatchWarehouseExciseSummary = app.injector.instanceOf[DispatchWarehouseExciseSummary]
    lazy val dispatchCheckAnswersSummary = new DispatchCheckAnswersHelper(dispatchWarehouseExciseSummary)
  }

  "CheckAnswersDispatchHelper" - {

    ".buildSummaryRows" - {

      "should return the correct rows" in new Setup(emptyUserAnswers
        .set(DispatchWarehouseExcisePage, testErn)
        .set(DispatchUseConsignorDetailsPage, false)
        .set(DispatchAddressPage, userAddressModelMax)
      ) {

        val expectedResult: SummaryList = SummaryList(Seq(
          DispatchUseConsignorDetailsSummary.row()(fakeDataRequest, msgs),
          dispatchWarehouseExciseSummary.row()(fakeDataRequest, msgs),
          Some(DispatchAddressSummary.row()(fakeDataRequest, msgs))
        ).flatten).withCssClass("govuk-!-margin-bottom-9")


        dispatchCheckAnswersSummary.summaryList() mustBe expectedResult
      }

      "should render as card layout when asCard is 'true'" in new Setup(emptyUserAnswers
        .set(DispatchWarehouseExcisePage, testErn)
        .set(DispatchUseConsignorDetailsPage, false)
        .set(DispatchAddressPage, userAddressModelMax)
      ) {

        val expectedResult: SummaryList = SummaryListViewModel(
          Seq(
          DispatchUseConsignorDetailsSummary.row()(fakeDataRequest, msgs),
          dispatchWarehouseExciseSummary.row()(fakeDataRequest, msgs),
          Some(DispatchAddressSummary.row()(fakeDataRequest, msgs))
        ).flatten,
          card = Some(CardViewModel(
            title = "Place of dispatch",
            headingLevel = 2,
            actions = None
          ))
        )

        dispatchCheckAnswersSummary.summaryList(asCard = true) mustBe expectedResult
      }
    }
  }
}
