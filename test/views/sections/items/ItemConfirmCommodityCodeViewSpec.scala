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

package views.sections.items

import base.SpecBase
import fixtures.ItemFixtures
import fixtures.messages.sections.items.ConfirmCommodityCodeMessages
import models.NormalMode
import models.requests.DataRequest
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import pages.sections.items.{ItemCommodityCodePage, ItemExciseProductCodePage}
import play.api.i18n.Messages
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import uk.gov.hmrc.govukfrontend.views.Aliases.SummaryList
import viewmodels.checkAnswers.sections.items.{ItemCommodityCodeSummary, ItemExciseProductCodeSummary}
import views.html.sections.items.ItemConfirmCommodityCodeView
import views.{BaseSelectors, ViewBehaviours}

class ItemConfirmCommodityCodeViewSpec extends SpecBase with ViewBehaviours with ItemFixtures {

  object Selectors extends BaseSelectors {
    def govukSummaryListKey(id: Int) = s".govuk-summary-list__row:nth-of-type($id) .govuk-summary-list__key"

    val govukSummaryListChangeLink = ".govuk-summary-list__actions .govuk-link"

  }

  lazy val itemExciseProductCodeSummary: ItemExciseProductCodeSummary = app.injector.instanceOf[ItemExciseProductCodeSummary]
  lazy val itemCommodityCodeSummary: ItemCommodityCodeSummary = app.injector.instanceOf[ItemCommodityCodeSummary]
  lazy val view: ItemConfirmCommodityCodeView = app.injector.instanceOf[ItemConfirmCommodityCodeView]

  "ConfirmCommodityCode view" - {

    Seq(ConfirmCommodityCodeMessages.English).foreach { messagesForLanguage =>

      s"when being rendered in lang code of '${messagesForLanguage.lang.code}'" - {

        implicit val msgs: Messages = messages(Seq(messagesForLanguage.lang))

        implicit val request: DataRequest[AnyContentAsEmpty.type] =
          dataRequest(FakeRequest(), emptyUserAnswers
            .set(ItemExciseProductCodePage(testIndex1), testEpcTobacco)
            .set(ItemCommodityCodePage(testIndex1), testCnCodeTobacco)
          )

        implicit val doc: Document = Jsoup.parse(view(
          controllers.sections.items.routes.ItemConfirmCommodityCodeController.onSubmit(testErn, testDraftId, testIndex1),
          SummaryList(Seq(
            itemExciseProductCodeSummary.row(testIndex1, testCommodityCodeTobacco),
            itemCommodityCodeSummary.row(testIndex1, testCommodityCodeTobacco)
          ))
        ).toString())

        behave like pageWithExpectedElementsAndMessages(Seq(
          Selectors.title -> messagesForLanguage.title,
          Selectors.h1 -> messagesForLanguage.heading,
          Selectors.h2(1) -> messagesForLanguage.caption,
          Selectors.govukSummaryListKey(1) -> messagesForLanguage.exciseProductCode,
          Selectors.govukSummaryListKey(2) -> messagesForLanguage.commodityCode,
          Selectors.button -> messagesForLanguage.confirmCodes,
        ))

        "have a link to change excise product code" in {
          doc.getElementById("changeItemExciseProductCode1").attr("href") mustBe
            controllers.sections.items.routes.ItemExciseProductCodeController.onPageLoad(testErn, testDraftId, testIndex1, NormalMode).url
        }

        "have a link to change commodity code" in {
          doc.getElementById("changeItemCommodityCode1").attr("href") mustBe
            controllers.sections.items.routes.ItemCommodityCodeController.onPageLoad(testErn, testDraftId, testIndex1, NormalMode).url
        }
      }
    }

    Seq(testExciseProductCodeS500,
      testExciseProductCodeT300,
      testExciseProductCodeS400,
      testExciseProductCodeE600,
      testExciseProductCodeE800,
      testExciseProductCodeE910).foreach { exciseProductCode =>

      val commodityCode = testCommodityCodeWine.copy(exciseProductCode = exciseProductCode.code)

      Seq(ConfirmCommodityCodeMessages.English).foreach { messagesForLanguage =>

        s"when being rendered in lang code of '${messagesForLanguage.lang.code}' for EPC code ${exciseProductCode}'" - {

          implicit val msgs: Messages = messages(Seq(messagesForLanguage.lang))

          implicit val request: DataRequest[AnyContentAsEmpty.type] =
            dataRequest(FakeRequest(), emptyUserAnswers
              .set(ItemExciseProductCodePage(testIndex1), exciseProductCode.code)
              .set(ItemCommodityCodePage(testIndex1), commodityCode.cnCode)
            )

          implicit val doc: Document = Jsoup.parse(view(
            controllers.sections.items.routes.ItemConfirmCommodityCodeController.onSubmit(testErn, testDraftId, testIndex1),
            SummaryList(Seq(
              itemExciseProductCodeSummary.row(testIndex1, commodityCode),
              itemCommodityCodeSummary.row(testIndex1, commodityCode)
            ))
          ).toString())

          behave like pageWithExpectedElementsAndMessages(Seq(
            Selectors.title -> messagesForLanguage.title,
            Selectors.h1 -> messagesForLanguage.heading,
            Selectors.h2(1) -> messagesForLanguage.caption,
            Selectors.govukSummaryListKey(1) -> messagesForLanguage.exciseProductCode,
            Selectors.govukSummaryListKey(2) -> messagesForLanguage.commodityCode,
            Selectors.button -> messagesForLanguage.confirmCodes,
          ))

          "have a link to change excise product code" in {
            doc.getElementById("changeItemExciseProductCode1").attr("href") mustBe
              controllers.sections.items.routes.ItemExciseProductCodeController.onPageLoad(testErn, testDraftId, testIndex1, NormalMode).url
          }

          "does not have a link to change commodity code" in {
            intercept[NullPointerException] {
              doc.getElementById("changeItemCommodityCode1").attr("href")
            }
          }
        }
      }
    }
  }
}
