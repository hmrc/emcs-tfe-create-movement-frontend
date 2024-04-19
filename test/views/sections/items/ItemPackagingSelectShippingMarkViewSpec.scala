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
import fixtures.messages.sections.items.ItemPackagingSelectShippingMarkMessages
import forms.sections.items.ItemPackagingSelectShippingMarkFormProvider
import models.ShippingMarkOption
import models.requests.DataRequest
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.data.FormError
import play.api.i18n.Messages
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import viewmodels.helpers.SelectItemHelper
import views.html.sections.items.ItemPackagingSelectShippingMarkView
import views.{BaseSelectors, ViewBehaviours}

class ItemPackagingSelectShippingMarkViewSpec extends SpecBase
  with ViewBehaviours
  with ItemFixtures {

  object Selectors extends BaseSelectors {
    def selectOption(nthChild: Int) = s"#value > option:nth-child($nthChild)"
  }

  "Item Packaging Select Shipping Mark view" - {

    Seq(ItemPackagingSelectShippingMarkMessages.English).foreach { messagesForLanguage =>

      s"when being rendered in lang code of '${messagesForLanguage.lang.code}'" - {

        implicit val msgs: Messages = messages(Seq(messagesForLanguage.lang))
        implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest())

        lazy val view = app.injector.instanceOf[ItemPackagingSelectShippingMarkView]
        val selectOptions = SelectItemHelper.constructSelectItems(
          selectOptions = Seq(ShippingMarkOption("beans"), ShippingMarkOption("eggs")),
          defaultTextMessageKey = "itemPackagingSelectShippingMark.select.defaultValue"
        )
        val form = app.injector.instanceOf[ItemPackagingSelectShippingMarkFormProvider].apply(Seq("beans", "eggs"))

        implicit def doc(isFormError: Boolean = false)(implicit request: DataRequest[_]): Document = Jsoup.parse(view(
          if (isFormError) form.withError(FormError("key", "msg")) else form,
          testOnwardRoute,
          testIndex1,
          testPackagingIndex1,
          selectOptions
        ).toString())

        behave like pageWithExpectedElementsAndMessages(Seq(
          Selectors.h2(1) -> messagesForLanguage.itemSection,
          Selectors.hiddenText -> messagesForLanguage.hiddenSectionContent,
          Selectors.title -> messagesForLanguage.title(testIndex1),
          Selectors.h1 -> messagesForLanguage.heading(testIndex1),
          Selectors.hint -> messagesForLanguage.hint(testIndex1),
          Selectors.selectOption(1) -> messagesForLanguage.defaultValue,
          Selectors.selectOption(2) -> "beans",
          Selectors.selectOption(3) -> "eggs",
          Selectors.button -> messagesForLanguage.saveAndContinue,
          Selectors.link(1) -> messagesForLanguage.returnToDraft
        ))(doc())
      }
    }
  }
}
