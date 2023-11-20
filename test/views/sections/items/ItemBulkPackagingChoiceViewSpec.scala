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
import fixtures.messages.sections.items.ItemBulkPackagingChoiceMessages
import forms.sections.items.ItemBulkPackagingChoiceFormProvider
import models.GoodsTypeModel
import models.requests.DataRequest
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.i18n.Messages
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import views.html.sections.items.ItemBulkPackagingChoiceView
import views.{BaseSelectors, ViewBehaviours}

class ItemBulkPackagingChoiceViewSpec extends ViewSpecBase with ViewBehaviours {

  object Selectors extends BaseSelectors

  "ItemBulkPackagingChoice view" - {

    Seq(ItemBulkPackagingChoiceMessages.English).foreach { messagesForLanguage =>

      s"when being rendered in lang code of '${messagesForLanguage.lang.code}'" - {

        GoodsTypeModel.values.foreach(
          goodsType =>
            s"when being rendered for GoodsType code ${goodsType.code}" - {


              implicit val msgs: Messages = messages(app, messagesForLanguage.lang)
              implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest())

              val view = app.injector.instanceOf[ItemBulkPackagingChoiceView]
              val form = app.injector.instanceOf[ItemBulkPackagingChoiceFormProvider].apply(goodsType)

              implicit val doc: Document = Jsoup.parse(view(form, testOnwardRoute, goodsType).toString())

              behave like pageWithExpectedElementsAndMessages(Seq(
                Selectors.title -> messagesForLanguage.title(goodsType.toSingularOutput()),
                Selectors.h1 -> messagesForLanguage.heading(goodsType.toSingularOutput()),
                Selectors.h2(1) -> messagesForLanguage.caption,
                Selectors.hint -> messagesForLanguage.hint,
                Selectors.radioButton(1) -> messagesForLanguage.yes,
                Selectors.radioButton(2) -> messagesForLanguage.no,
                Selectors.button -> messagesForLanguage.saveAndContinue,
                Selectors.link(1) -> messagesForLanguage.returnToDraft
              ))
            }
        )
      }
    }
  }
}
