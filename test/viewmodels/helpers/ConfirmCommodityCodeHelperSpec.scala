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
import fixtures.ItemFixtures
import models.requests.DataRequest
import pages.sections.items.{ItemCommodityCodePage, ItemExciseProductCodePage}
import play.api.i18n.Messages
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryList
import viewmodels.checkAnswers.sections.items.{ItemCommodityCodeSummary, ItemExciseProductCodeSummary}

class ConfirmCommodityCodeHelperSpec extends SpecBase with ItemFixtures {

  val answers = emptyUserAnswers
    .set(ItemExciseProductCodePage(testIndex1), testCommodityCodeWine.exciseProductCode)
    .set(ItemCommodityCodePage(testIndex1), testCommodityCodeWine.cnCode)

  lazy val itemExciseProductCodeSummary: ItemExciseProductCodeSummary = app.injector.instanceOf[ItemExciseProductCodeSummary]
  lazy val itemCommodityCodeSummary: ItemCommodityCodeSummary = app.injector.instanceOf[ItemCommodityCodeSummary]

  lazy val confirmCommodityCodeHelper = new ConfirmCommodityCodeHelper(itemExciseProductCodeSummary, itemCommodityCodeSummary)
  implicit val fakeDataRequest: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(), answers)
  implicit val testUserRequest = userRequest(fakeDataRequest)
  implicit val msgs: Messages = messagesApi.preferred(fakeDataRequest)

  "ConfirmCommodityCodeHelper" - {
    ".summaryList should render row" in {
      confirmCommodityCodeHelper.summaryList(testIndex1, testCommodityCodeWine) mustBe SummaryList(
        rows = Seq(
          itemExciseProductCodeSummary.row(idx = testIndex1, cnCodeInformation = testCommodityCodeWine),
          itemCommodityCodeSummary.row(idx = testIndex1, cnCodeInformation = testCommodityCodeWine)
        )
      )
    }
  }
}
