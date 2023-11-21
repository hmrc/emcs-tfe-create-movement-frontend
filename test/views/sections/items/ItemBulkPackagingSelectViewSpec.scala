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
import fixtures.messages.sections.items.ItemBulkPackagingSelectMessages
import forms.sections.items.ItemBulkPackagingSelectFormProvider
import models.GoodsTypeModel.Wine
import models.requests.DataRequest
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.i18n.Messages
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import views.html.sections.items.ItemBulkPackagingSelectView
import views.{BaseSelectors, ViewBehaviours}

class ItemBulkPackagingSelectViewSpec extends SpecBase with ViewBehaviours with ItemFixtures {
  object Selectors extends BaseSelectors

  //scalastyle:off
  "Item Bulk Packaging Select view" - {
    Seq(ItemBulkPackagingSelectMessages.English).foreach { messagesForLanguage =>

      s"when being rendered in lang code of '${messagesForLanguage.lang.code}'" - {

        implicit val msgs: Messages = messages(Seq(messagesForLanguage.lang))
        implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest())

        val view = app.injector.instanceOf[ItemBulkPackagingSelectView]
        val form = app.injector.instanceOf[ItemBulkPackagingSelectFormProvider].apply(Wine)

        implicit val doc: Document = Jsoup.parse(view(form, testOnwardRoute, bulkPackagingTypesRadioOptions, Wine).toString())

        behave like pageWithExpectedElementsAndMessages(Seq(
          Selectors.title -> messagesForLanguage.title(Wine.toSingularOutput()),
          Selectors.h1 -> messagesForLanguage.heading(Wine.toSingularOutput()),
          Selectors.subHeadingCaptionSelector -> messagesForLanguage.itemSection,
          Selectors.radioButton(1) -> messagesForLanguage.radioOption1,
          Selectors.radioButton(2) -> messagesForLanguage.radioOption2,
          Selectors.radioButton(3) -> messagesForLanguage.radioOption3,
          Selectors.radioButton(4) -> messagesForLanguage.radioOption4,
          Selectors.radioButton(5) -> messagesForLanguage.radioOption5,
          Selectors.radioButton(6) -> messagesForLanguage.radioOption6,
          Selectors.radioButton(7) -> messagesForLanguage.radioOption7,
          Selectors.button -> messagesForLanguage.saveAndContinue,
          Selectors.link(1) -> messagesForLanguage.returnToDraft
        ))
      }
    }
  }
}
