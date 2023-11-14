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

package viewmodels.checkAnswers.sections.items

import base.SpecBase
import fixtures.ItemFixtures
import fixtures.messages.sections.items.ItemCommodityCodeMessages
import models.{GoodsTypeModel, NormalMode}
import org.scalatest.matchers.must.Matchers
import pages.sections.items.{ItemCommodityCodePage, ItemExciseProductCodePage}
import play.api.i18n.Messages
import play.api.test.FakeRequest
import play.twirl.api.{Html, HtmlFormat}
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.HtmlContent
import viewmodels.govuk.summarylist._
import viewmodels.implicits._

class ItemCommodityCodeSummarySpec extends SpecBase with Matchers with ItemFixtures {

  "ItemCommodityCodeSummary" - {

    Seq(testExciseProductCodeS500, testExciseProductCodeT300, testExciseProductCodeS400, testExciseProductCodeE600, testExciseProductCodeE800, testExciseProductCodeE910).foreach { exciseProductCode =>

      Seq(ItemCommodityCodeMessages.English).foreach { messagesForLanguage =>

        s"when being rendered in lang code of '${messagesForLanguage.lang.code}' with EPC of '${exciseProductCode.code}'" - {

          implicit lazy val msgs: Messages = messages(Seq(messagesForLanguage.lang))

          "when there's an answer" - {

            "must output the expected row" in {
              implicit lazy val request = dataRequest(FakeRequest(), emptyUserAnswers.set(ItemExciseProductCodePage(testIndex1), exciseProductCode).set(ItemCommodityCodePage(testIndex1), testCommodityCodeWine))

              ItemCommodityCodeSummary.row(testIndex1,GoodsTypeModel(exciseProductCode.code), request.userAnswers) mustBe Some(
                SummaryListRowViewModel(
                  key = messagesForLanguage.cyaLabel,
                  value = ValueViewModel(HtmlContent(HtmlFormat.fill(Seq(
                    Html("22060031" + "<br>"),
                    Html("Sparkling cider and perry"))))),
                  actions = Seq()
                )
              )
            }
          }
        }
      }
    }

    Seq(ItemCommodityCodeMessages.English).foreach { messagesForLanguage =>

      s"when being rendered in lang code of '${messagesForLanguage.lang.code}'" - {

        implicit lazy val msgs: Messages = messages(Seq(messagesForLanguage.lang))

        "when there's no answer" - {

          "must output the expected data" in {
            implicit lazy val request = dataRequest(FakeRequest(), emptyUserAnswers)

            ItemCommodityCodeSummary.row(testIndex1,GoodsTypeModel(testExciseProductCodeB000.code),request.userAnswers) mustBe None
          }
        }

        "when there's an answer" - {

          "must output the expected row" in {
            implicit lazy val request = dataRequest(FakeRequest(), emptyUserAnswers.set(ItemExciseProductCodePage(testIndex1), testExciseProductCodeB000).set(ItemCommodityCodePage(testIndex1), testCommodityCodeWine))

            ItemCommodityCodeSummary.row(testIndex1, GoodsTypeModel(testExciseProductCodeB000.code),request.userAnswers) mustBe Some(
              SummaryListRowViewModel(
                key = messagesForLanguage.cyaLabel,
                value = ValueViewModel(HtmlContent(HtmlFormat.fill(Seq(
                  Html("22060031" + "<br>"),
                  Html("Sparkling cider and perry"))))),
                actions = Seq(
                  ActionItemViewModel(
                    content = messagesForLanguage.change,
                    href = controllers.sections.items.routes.ItemCommodityCodeController.onPageLoad(testErn, testDraftId, testIndex1, NormalMode).url,
                    id = "changeItemCommodityCode1"
                  ).withVisuallyHiddenText(messagesForLanguage.cyaChangeHidden)
                )
              )
            )
          }
        }
      }
    }
  }
}
