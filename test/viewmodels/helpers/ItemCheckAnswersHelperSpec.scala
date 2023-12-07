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
import fixtures.messages.sections.items._
import models.UserAnswers
import models.requests.DataRequest
import models.sections.items._
import pages.sections.items._
import play.api.i18n.Messages
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.{Actions, SummaryList}
import viewmodels.checkAnswers.sections.items._
import viewmodels.govuk.summarylist._
import viewmodels.implicits._

class ItemCheckAnswersHelperSpec extends SpecBase with ItemFixtures {
  val messagesForLanguage: ItemCheckAnswersMessages.English.type = ItemCheckAnswersMessages.English

  val baseUserAnswers: UserAnswers = emptyUserAnswers
    .set(ItemExciseProductCodePage(testIndex1), testEpcWine)

  class Test(val userAnswers: UserAnswers) {
    lazy implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(), userAnswers, testErn)
    lazy implicit val msgs: Messages = messages(Seq(messagesForLanguage.lang))
    lazy val itemExciseProductCodeSummary: ItemExciseProductCodeSummary = app.injector.instanceOf[ItemExciseProductCodeSummary]
    lazy val itemCommodityCodeSummary: ItemCommodityCodeSummary = app.injector.instanceOf[ItemCommodityCodeSummary]
    lazy val itemWineOperationsChoiceSummary: ItemWineOperationsChoiceSummary = app.injector.instanceOf[ItemWineOperationsChoiceSummary]
    lazy val itemWineMoreInformationSummary: ItemWineMoreInformationSummary = app.injector.instanceOf[ItemWineMoreInformationSummary]
    lazy val itemBulkPackagingSealTypeSummary: ItemBulkPackagingSealTypeSummary = app.injector.instanceOf[ItemBulkPackagingSealTypeSummary]

    lazy val helper = new ItemCheckAnswersHelper(
      itemExciseProductCodeSummary = itemExciseProductCodeSummary,
      itemCommodityCodeSummary = itemCommodityCodeSummary,
      itemWineOperationsChoiceSummary = itemWineOperationsChoiceSummary,
      itemWineMoreInformationSummary = itemWineMoreInformationSummary,
      itemBulkPackagingSealTypeSummary = itemBulkPackagingSealTypeSummary
    )
  }

  "ItemCheckAnswersHelper" - {
    "constructItemDetailsCard" - {
      "must return rows" in new Test(baseUserAnswers) {
        val card: SummaryList = helper.constructItemDetailsCard(testIndex1, testCommodityCodeWine)

        card.card mustBe Some(CardViewModel(messagesForLanguage.cardTitleItemDetails, 3, None))
        card.rows must not be empty
      }
    }

    "constructQuantityCard" - {
      "must return all rows" in new Test(
        baseUserAnswers
          .set(ItemQuantityPage(testIndex1), BigDecimal(1.23))
          .set(ItemNetGrossMassPage(testIndex1), ItemNetGrossMassModel(BigDecimal(4.56), BigDecimal(7.89)))
      ) {
        val card: SummaryList = helper.constructQuantityCard(testIndex1, testCommodityCodeWine)

        card.card mustBe Some(CardViewModel(messagesForLanguage.cardTitleQuantity, 3, None))
        card.rows.length mustBe 3
      }
    }

    "constructWineDetailsCard" - {
      "must return rows" in new Test(
        baseUserAnswers
          .set(ItemWineOperationsChoicePage(testIndex1), Seq())
      ) {
        val card: SummaryList = helper.constructWineDetailsCard(testIndex1)

        card.card mustBe Some(CardViewModel(messagesForLanguage.cardTitleWineDetails, 3, None))
        card.rows must not be empty
      }
    }

    "constructPackagingCard" - {
      "must return rows" - {
        "when bulk" in new Test(
          baseUserAnswers
            .set(ItemBulkPackagingChoicePage(testIndex1), true)
        ) {
          val card: SummaryList = helper.constructPackagingCard(testIndex1, testCommodityCodeTobacco)

          card.card mustBe Some(CardViewModel(messagesForLanguage.cardTitlePackaging, 3, None))
          card.rows must not be empty
        }
        "when not bulk" in new Test(
          baseUserAnswers
            .set(ItemBulkPackagingChoicePage(testIndex1), false)
        ) {
          val card: SummaryList = helper.constructPackagingCard(testIndex1, testCommodityCodeTobacco)

          card.card mustBe Some(CardViewModel(messagesForLanguage.cardTitlePackaging, 3, Some(Actions(items = Seq(ActionItemViewModel(
            messagesForLanguage.change,
            href = controllers.sections.items.routes.ItemsPackagingAddToListController.onPageLoad(request.ern, request.draftId, testIndex1).url,
            s"changeItemBulkPackagingChoice${testIndex1.displayIndex}"
          ).withVisuallyHiddenText(ItemsPackagingAddToListMessages.English.cyaChangeHidden))))))
          card.rows must not be empty
        }
      }
    }
  }
}
