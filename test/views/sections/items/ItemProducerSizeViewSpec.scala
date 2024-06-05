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
import fixtures.messages.sections.items.ItemProducerSizeMessages.English
import forms.sections.items.ItemProducerSizeFormProvider
import models.GoodsType.{Beer, Spirits, Wine}
import models.NormalMode
import models.requests.DataRequest
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.i18n.Messages
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import views.html.sections.items.ItemProducerSizeView
import views.{BaseSelectors, ViewBehaviours}

class ItemProducerSizeViewSpec extends SpecBase with ViewBehaviours with ItemFixtures {

  object Selectors extends BaseSelectors{
    val returnToDraftLink: String = "#save-and-exit"

  }

  "Item Producer Size view" - {

    s"when being rendered in lang code of '${English.lang.code}'" - {

      "when the goodsType is beer and the years are 2022 to 2023" - {

        implicit val msgs: Messages = messages(Seq(English.lang))

        implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest())

        val view = app.injector.instanceOf[ItemProducerSizeView]
        val form = app.injector.instanceOf[ItemProducerSizeFormProvider].apply()

        implicit val doc: Document = Jsoup.parse(view(
          form = form,
          onSubmitAction = testOnwardRoute,
          skipQuestionAction = testOnwardRouteTwo,
          goodsType = Beer,
          startYear = "2022",
          endYear = "2023",
          index = testIndex1,
          showAlcoholProductionContent = false
        ).toString())

        behave like pageWithExpectedElementsAndMessages(Seq(
          Selectors.title -> English.titleGoodsType("beer", "2022", "2023"),
          Selectors.h1 -> English.headingGoodsType("beer", "2022", "2023"),
          Selectors.subHeadingCaptionSelector -> English.itemSection,
          Selectors.p(1) -> English.p,
          Selectors.p(2) -> English.p2GoodsType,
          Selectors.label("value") -> English.labelGoodsType,
          Selectors.inputSuffix -> English.inputSuffix,
          Selectors.button -> English.saveAndContinue,
          Selectors.link(1) -> English.link,
          Selectors.returnToDraftLink -> English.returnToDraft
        ))

        "link to the item Quantity page" in {
          doc.select(Selectors.link(1)).attr("href") mustBe testOnwardRouteTwo.url
        }
      }

      "when the goodsType is spirit and the years are 2023 to 2024" - {

        implicit val msgs: Messages = messages(Seq(English.lang))

        implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest())

        val view = app.injector.instanceOf[ItemProducerSizeView]
        val form = app.injector.instanceOf[ItemProducerSizeFormProvider].apply()

        implicit val doc: Document = Jsoup.parse(view(
          form = form,
          onSubmitAction = testOnwardRoute,
          skipQuestionAction = testOnwardRouteTwo,
          goodsType = Spirits,
          startYear = "2023",
          endYear = "2024",
          index = testIndex1,
          showAlcoholProductionContent = false
        ).toString())

        behave like pageWithExpectedElementsAndMessages(Seq(
          Selectors.title -> English.titleGoodsType("pure alcohol", "2023", "2024"),
          Selectors.h1 -> English.headingGoodsType("pure alcohol", "2023", "2024"),
          Selectors.subHeadingCaptionSelector -> English.itemSection,
          Selectors.p(1) -> English.p,
          Selectors.p(2) -> English.p2GoodsType,
          Selectors.label("value") -> English.labelGoodsType,
          Selectors.inputSuffix -> English.inputSuffix,
          Selectors.button -> English.saveAndContinue,
          Selectors.link(1) -> English.link,
          Selectors.returnToDraftLink -> English.returnToDraft
        ))

        "link to the item Quantity page" in {
          doc.select(Selectors.link(1)).attr("href") mustBe testOnwardRouteTwo.url
        }
      }

      "when the goodsType is beer and the years are 2024 to 2025" - {

        implicit val msgs: Messages = messages(Seq(English.lang))

        implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest())

        val view = app.injector.instanceOf[ItemProducerSizeView]
        val form = app.injector.instanceOf[ItemProducerSizeFormProvider].apply()

        implicit val doc: Document = Jsoup.parse(view(
          form = form,
          onSubmitAction = testOnwardRoute,
          skipQuestionAction = testOnwardRouteTwo,
          goodsType = Wine,
          startYear = "2024",
          endYear = "2025",
          index = testIndex1,
          showAlcoholProductionContent = false
        ).toString())

        behave like pageWithExpectedElementsAndMessages(Seq(
          Selectors.title -> English.titleGoodsType("this product type", "2024", "2025"),
          Selectors.h1 -> English.headingGoodsType("this product type", "2024", "2025"),
          Selectors.subHeadingCaptionSelector -> English.itemSection,
          Selectors.p(1) -> English.p,
          Selectors.p(2) -> English.p2GoodsType,
          Selectors.label("value") -> English.labelGoodsType,
          Selectors.inputSuffix -> English.inputSuffix,
          Selectors.link(1) -> English.link,
          Selectors.button -> English.saveAndContinue,
          Selectors.returnToDraftLink -> English.returnToDraft
        ))

        "link to the item Quantity page" in {
          doc.select(Selectors.link(1)).attr("href") mustBe testOnwardRouteTwo.url
        }
      }

      "when the showAlcoholProductionContent is true" - {

        implicit val msgs: Messages = messages(Seq(English.lang))

        implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest())

        val view = app.injector.instanceOf[ItemProducerSizeView]
        val form = app.injector.instanceOf[ItemProducerSizeFormProvider].apply()

        implicit val doc: Document = Jsoup.parse(view(
          form = form,
          onSubmitAction = testOnwardRoute,
          skipQuestionAction = testOnwardRouteTwo,
          goodsType = Wine,
          startYear = "2024",
          endYear = "2025",
          index = testIndex1,
          showAlcoholProductionContent = true
        ).toString())

        behave like pageWithExpectedElementsAndMessages(Seq(
          Selectors.title -> English.titlePureAlcohol("2024", "2025"),
          Selectors.h1 -> English.headingPureAlcohol("2024", "2025"),
          Selectors.subHeadingCaptionSelector -> English.itemSection,
          Selectors.p(1) -> English.p,
          Selectors.p(2) -> English.p2PureAlcohol,
          Selectors.label("value") -> English.labelPureAlcohol,
          Selectors.inputSuffix -> English.inputSuffix,
          Selectors.link(1) -> English.link,
          Selectors.button -> English.saveAndContinue,
          Selectors.returnToDraftLink -> English.returnToDraft
        ))

        "link to the item Quantity page" in {
          doc.select(Selectors.link(1)).attr("href") mustBe testOnwardRouteTwo.url
        }
      }
    }
  }
}
