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
import fixtures.messages.sections.items.ItemNetGrossMassMessages
import forms.sections.items.ItemNetGrossMassFormProvider
import models.GoodsTypeModel.Wine
import models.NormalMode
import models.requests.DataRequest
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.i18n.Messages
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import views.html.sections.items.ItemNetGrossMassView
import views.{BaseSelectors, ViewBehaviours}

class ItemNetGrossMassViewSpec extends ViewSpecBase with ViewBehaviours {

  object Selectors extends BaseSelectors {
    def questionTitle(i: Int): String = s".govuk-form-group:nth-of-type($i) > label"
    def questionHint(i: Int): String = s".govuk-form-group:nth-of-type($i) > .govuk-hint"
    def questionSuffix(i: Int): String = s".govuk-form-group:nth-of-type($i) > * > .govuk-input__suffix"
  }

  "ItemQuantity view" - {
    val messagesForLanguage = ItemNetGrossMassMessages.English

    s"when being rendered in lang code of '${messagesForLanguage.lang.code}'" - {

      implicit val msgs: Messages = messages(app, messagesForLanguage.lang)
      implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest())

      val view = app.injector.instanceOf[ItemNetGrossMassView]
      val form = app.injector.instanceOf[ItemNetGrossMassFormProvider].form

      implicit val doc: Document = Jsoup.parse(view(form, testIndex1, Wine, NormalMode).toString())

      behave like pageWithExpectedElementsAndMessages(Seq(
        Selectors.title -> messagesForLanguage.title(Wine.toSingularOutput()),
        Selectors.h1 -> messagesForLanguage.heading(Wine.toSingularOutput()),
        Selectors.subHeadingCaptionSelector -> messagesForLanguage.itemSection,
        Selectors.questionTitle(1) -> messagesForLanguage.netMassH2,
        Selectors.questionHint(1) -> messagesForLanguage.netMassHint,
        Selectors.questionSuffix(1) -> "kg",
        Selectors.questionTitle(2) -> messagesForLanguage.grossMassH2,
        Selectors.questionHint(2) -> messagesForLanguage.grossMassHint,
        Selectors.questionSuffix(2) -> "kg",
        Selectors.button -> messagesForLanguage.saveAndContinue,
        Selectors.link(1) -> messagesForLanguage.returnToDraft
      ))
    }
  }
}
