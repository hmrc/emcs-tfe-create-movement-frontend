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
import fixtures.messages.sections.items.ItemMaturationPeriodAgeMessages
import forms.sections.items.ItemMaturationPeriodAgeFormProvider
import models.GoodsType.Beer
import models.requests.DataRequest
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.i18n.Messages
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import views.html.sections.items.ItemMaturationPeriodAgeView
import views.{BaseSelectors, ViewBehaviours}

class ItemMaturationPeriodAgeViewSpec extends SpecBase with ViewBehaviours {

  object Selectors extends BaseSelectors

  "ItemMaturationPeriodAgeView" - {

    Seq(ItemMaturationPeriodAgeMessages.English).foreach { messagesForLanguage =>

      s"when being rendered in lang code of '${messagesForLanguage.lang.code}'" - {

        implicit val msgs: Messages = messages(Seq(messagesForLanguage.lang))
        implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest())

       lazy val view = app.injector.instanceOf[ItemMaturationPeriodAgeView]
        val form = app.injector.instanceOf[ItemMaturationPeriodAgeFormProvider].apply(Beer)

        implicit val doc: Document = Jsoup.parse(view(form, testOnwardRoute, Beer).toString())

        behave like pageWithExpectedElementsAndMessages(Seq(
          Selectors.title -> messagesForLanguage.title(Beer.toSingularOutput()),
          Selectors.h1 -> messagesForLanguage.heading(Beer.toSingularOutput()),
          Selectors.subHeadingCaptionSelector -> messagesForLanguage.itemSection,
          Selectors.hint -> messagesForLanguage.hint,
          Selectors.radioButton(1) -> messagesForLanguage.yes,
          Selectors.label(ItemMaturationPeriodAgeFormProvider.maturationPeriodAgeField) -> messagesForLanguage.maturationPeriodAgeLabel,
          //Note, this is radio button 2 but index is 3 due to hidden HTML conditional content for radio 1
          Selectors.radioButton(3) -> messagesForLanguage.no,
          Selectors.button -> messagesForLanguage.saveAndContinue,
          Selectors.link(1) -> messagesForLanguage.returnToDraft
        ))
      }
    }
  }
}
