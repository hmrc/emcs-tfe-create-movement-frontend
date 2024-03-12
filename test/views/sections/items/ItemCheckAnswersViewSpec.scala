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
import fixtures.{ItemFixtures, MovementSubmissionFailureFixtures}
import fixtures.messages.sections.items.ItemCheckAnswersMessages
import models.CheckMode
import models.requests.DataRequest
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.i18n.Messages
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import views.html.sections.items.ItemCheckAnswersView
import views.{BaseSelectors, ViewBehaviours}

class ItemCheckAnswersViewSpec extends SpecBase with ViewBehaviours with ItemFixtures with MovementSubmissionFailureFixtures {

  object Selectors extends BaseSelectors {
    val fixQuantityErrorAtIndex: Int => String = index => s"#fix-item-$index-quantity"
    val notificationBannerList = s".govuk-notification-banner .govuk-list"
  }

  val view: ItemCheckAnswersView = app.injector.instanceOf[ItemCheckAnswersView]
  implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest())

  val itemDetailsIndex = 1
  val quantityIndex = 2
  val wineDetailsIndex = 3
  val packagingIndex = 4

  "Item Check Answers view" - {
    Seq(ItemCheckAnswersMessages.English).foreach { messagesForLanguage =>

      implicit val msgs: Messages = messages(Seq(messagesForLanguage.lang))

      s"when being rendered in lang code of '${messagesForLanguage.lang.code}'" - {

        implicit val doc: Document = Jsoup.parse(view(testIndex1, testCommodityCodeWine, testOnwardRoute).toString())

        behave like pageWithExpectedElementsAndMessages(Seq(
          Selectors.title -> messagesForLanguage.title,
          Selectors.h1 -> messagesForLanguage.heading,
          Selectors.subHeadingCaptionSelector -> messagesForLanguage.itemSection,
          Selectors.h2(2) -> messagesForLanguage.subheading(testIndex1),
          Selectors.summaryCardHeading(itemDetailsIndex) -> messagesForLanguage.cardTitleItemDetails,
          Selectors.summaryCardHeading(quantityIndex) -> messagesForLanguage.cardTitleQuantity,
          Selectors.summaryCardHeading(wineDetailsIndex) -> messagesForLanguage.cardTitleWineDetails,
          Selectors.summaryCardHeading(packagingIndex) -> messagesForLanguage.cardTitlePackaging,
          Selectors.button -> messagesForLanguage.confirmAnswers
        ))
      }

      "must not render the Wine card" - {
        s"when the commodity code is not 22060010 or begins 2204 (except 22043096 / 22043098)" in {
          implicit val msgs: Messages = messages(Seq(messagesForLanguage.lang))
          implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest())

          val view = app.injector.instanceOf[ItemCheckAnswersView]

          implicit val doc: Document = Jsoup.parse(view(testIndex1, testCommodityCodeWine.copy(cnCode = testCnCodeSpirit), testOnwardRoute).toString())

          doc.selectFirst(Selectors.summaryCardHeading(wineDetailsIndex)).text() must not be messagesForLanguage.cardTitleWineDetails
        }
      }

      "when there is a 704 error" - {

        implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(), singleCompletedWineItem
          .copy(submissionFailures = Seq(itemQuantityFailure(1))))

        implicit val doc: Document = Jsoup.parse(view(testIndex1, testCommodityCodeWine, testOnwardRoute).toString())

        behave like pageWithExpectedElementsAndMessages(Seq(
          Selectors.title -> messagesForLanguage.title,
          Selectors.subHeadingCaptionSelector -> messagesForLanguage.itemSection,
          Selectors.h1 -> messagesForLanguage.heading,
          Selectors.notificationBannerTitle -> messagesForLanguage.updateNeeded,
          Selectors.notificationBannerList -> messagesForLanguage.notificationBannerContentForQuantity,
          Selectors.button -> messagesForLanguage.confirmAnswers
        ))

        "link to the item Quantity page" in {
          doc.select(Selectors.fixQuantityErrorAtIndex(1)).attr("href") mustBe controllers.sections.items.routes.ItemQuantityController.onPageLoad(testErn, testDraftId, testIndex1, CheckMode).url
        }
      }
    }
  }
}
