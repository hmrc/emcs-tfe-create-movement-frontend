/*
 * Copyright 2024 HM Revenue & Customs
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

package views.sections.templates

import base.SpecBase
import fixtures.messages.sections.templates.SaveTemplateMessages
import forms.sections.templates.SaveTemplateFormProvider
import models.NormalMode
import models.requests.DataRequest
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.i18n.Messages
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import views.html.sections.templates.SaveTemplateView
import views.{BaseSelectors, ViewBehaviours}

class SaveTemplateViewSpec extends SpecBase with ViewBehaviours {

  object Selectors extends BaseSelectors

  "SaveTemplate view" - {

    Seq(SaveTemplateMessages.English).foreach { messagesForLanguage =>

      s"when being rendered in lang code of '${messagesForLanguage.lang.code}'" - {

        implicit val msgs: Messages = messages(Seq(messagesForLanguage.lang))
        implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest())

        lazy val view = app.injector.instanceOf[SaveTemplateView]
        val form = app.injector.instanceOf[SaveTemplateFormProvider].apply(Seq())

        implicit val doc: Document =
          Jsoup.parse(view(form, controllers.sections.templates.routes.SaveTemplateController.onSubmit(request.ern, request.draftId), NormalMode).toString())

        behave like pageWithExpectedElementsAndMessages(Seq(
          Selectors.title -> messagesForLanguage.title,
          Selectors.h1 -> messagesForLanguage.heading,
          Selectors.radioButton(1) -> messagesForLanguage.yes,
          Selectors.label(SaveTemplateFormProvider.templateNameField) -> messagesForLanguage.templateNameLabel,
          //Note, this is radio button 2 but index is 3 due to hidden HTML conditional content for radio 1
          Selectors.radioButton(3) -> messagesForLanguage.no,
          Selectors.hint -> messagesForLanguage.saveTemplateHint,
          Selectors.button -> messagesForLanguage.continue
        ))
      }
    }
  }
}
