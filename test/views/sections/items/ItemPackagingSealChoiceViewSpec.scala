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
import fixtures.messages.sections.items.ItemPackagingSealChoiceMessages
import forms.sections.items.ItemPackagingSealChoiceFormProvider
import models.requests.DataRequest
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.i18n.Messages
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import views.html.sections.items.ItemPackagingSealChoiceView
import views.{BaseSelectors, ViewBehaviours}

class ItemPackagingSealChoiceViewSpec extends SpecBase with ViewBehaviours {

  object Selectors extends BaseSelectors

  "ItemPackagingSealChoice view" - {

    Seq(ItemPackagingSealChoiceMessages.English).foreach { messagesForLanguage =>

      implicit val msgs: Messages = messages(Seq(messagesForLanguage.lang))
      implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest())

      val view = app.injector.instanceOf[ItemPackagingSealChoiceView]
      val form = app.injector.instanceOf[ItemPackagingSealChoiceFormProvider].apply()

      s"when the item packaging is not bulk and being rendered in '${messagesForLanguage.lang.code}'" - {

        implicit val doc: Document = Jsoup.parse(view(form, testOnwardRoute, "Aerosol", testIndex1, testPackagingIndex1, packagingQuantity = Some("1"), false).toString())

        behave like pageWithExpectedElementsAndMessages(Seq(
          Selectors.title -> messagesForLanguage.title,
          Selectors.subHeadingCaptionSelector -> messagesForLanguage.itemSection,
          Selectors.h1 -> messagesForLanguage.heading,
          Selectors.p(1) -> messagesForLanguage.p1(packagingIndex = "1",itemIndex = "1",packagingDescription = "Aerosol",packagingQuantity = "1"),
          Selectors.p(2) -> messagesForLanguage.p2,
          Selectors.strong(1) -> "Aerosol: 1",
          Selectors.legend -> messagesForLanguage.label,
          Selectors.radioButton(1) -> messagesForLanguage.yes,
          Selectors.radioButton(2) -> messagesForLanguage.no,
          Selectors.button -> messagesForLanguage.saveAndContinue,
          Selectors.link(1) -> messagesForLanguage.returnToDraft
        ))
      }

      s"when the item packaging is bulk and being rendered in '${messagesForLanguage.lang.code}'" - {

        implicit val doc: Document = Jsoup.parse(view(form, testOnwardRoute, "Bulk, solid, fine (powders)", testIndex1, testPackagingIndex1, packagingQuantity = None, true).toString())

        behave like pageWithExpectedElementsAndMessages(Seq(
          Selectors.title -> messagesForLanguage.title,
          Selectors.subHeadingCaptionSelector -> messagesForLanguage.itemSection,
          Selectors.h1 -> messagesForLanguage.heading,
          Selectors.p(1) -> messagesForLanguage.p2,
          Selectors.legend -> messagesForLanguage.label,
          Selectors.radioButton(1) -> messagesForLanguage.yes,
          Selectors.radioButton(2) -> messagesForLanguage.no,
          Selectors.button -> messagesForLanguage.saveAndContinue,
          Selectors.link(1) -> messagesForLanguage.returnToDraft
        ))

        behave like pageWithElementsNotPresent(Seq(
          Selectors.p(2)
        ))
      }
    }
  }
}
