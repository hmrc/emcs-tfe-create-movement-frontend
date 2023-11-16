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

import base.ViewSpecBase
import fixtures.messages.sections.items.ItemCommodityCodeMessages.English
import forms.sections.items.ItemCommodityCodeFormProvider
import models.GoodsTypeModel.Beer
import models.NormalMode
import models.UnitOfMeasure.Kilograms
import models.requests.DataRequest
import models.response.referenceData.CnCodeInformation
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.i18n.Messages
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import views.html.sections.items.ItemCommodityCodeView
import views.{BaseSelectors, ViewBehaviours}

class ItemCommodityCodeViewSpec extends ViewSpecBase with ViewBehaviours {
  object Selectors extends BaseSelectors {
    def selectOption(nthChild: Int) = s"#item-commodity-code > option:nth-child($nthChild)"
  }

  "Dispatch Business Name view" - {

    s"when being rendered in lang code of '${English.lang.code}'" - {

      implicit val msgs: Messages = messages(app, English.lang)
      implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(), emptyUserAnswers)

      val view = app.injector.instanceOf[ItemCommodityCodeView]
      val form = app.injector.instanceOf[ItemCommodityCodeFormProvider].apply()
      val submitRoute = controllers.sections.items.routes.ItemCommodityCodeController.onPageLoad(testErn, testDraftId, testIndex1, NormalMode)
      val testEpc = "testEpcTobacco"

      val testCommodityCode1 = CnCodeInformation(
        cnCode = "testCnCode1",
        cnCodeDescription = "testCnCodeDescription1",
        exciseProductCode = testEpc,
        exciseProductCodeDescription = "testExciseProductCodeDescription",
        unitOfMeasure = Kilograms
      )

      val testCommodityCode2 = CnCodeInformation(
        cnCode = "testCnCodeWine",
        cnCodeDescription = "testCnCodeDescription2",
        exciseProductCode = testEpc,
        exciseProductCodeDescription = "testExciseProductCodeDescription",
        unitOfMeasure = Kilograms
      )


      implicit val doc: Document = Jsoup.parse(view(form, submitRoute, Beer, Seq(testCommodityCode1, testCommodityCode2)).toString())
      val main = doc.getElementsByTag("main").first()
      val links = main.getElementsByTag("a")
      links.get(0).text mustBe English.lookUpCommodityCode
      links.get(1).text mustBe English.returnToDraft

      behave like pageWithExpectedElementsAndMessages(Seq(
        Selectors.h2(1) -> English.itemSection,
        Selectors.hiddenText -> English.hiddenSectionContent,
        Selectors.title -> English.title,
        Selectors.h1 -> English.heading,
        Selectors.selectOption(1) -> English.defaultItem,
        Selectors.selectOption(2) -> s"${testCommodityCode1.cnCode}: ${testCommodityCode1.cnCodeDescription}",
        Selectors.selectOption(3) -> s"${testCommodityCode2.cnCode}: ${testCommodityCode2.cnCodeDescription}",
        Selectors.button -> English.saveAndContinue
      ))
    }
  }
}

