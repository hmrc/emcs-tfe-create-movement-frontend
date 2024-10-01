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
import fixtures.messages.sections.templates.UpdateTemplateMessages
import forms.sections.templates.UpdateTemplateFormProvider
import models.requests.DataRequest
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.i18n.Messages
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import views.html.sections.templates.UpdateTemplateView
import views.{BaseSelectors, ViewBehaviours}

class UpdateTemplateViewSpec extends SpecBase with ViewBehaviours {

  object Selectors extends BaseSelectors

  "UpdateTemplate view" - {

    Seq(UpdateTemplateMessages.English).foreach { messagesForLanguage =>

      s"when being rendered in lang code of '${messagesForLanguage.lang.code}'" - {

        implicit val msgs: Messages = messages(Seq(messagesForLanguage.lang))
        implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(
          request = FakeRequest(),
          answers = emptyUserAnswersFromTemplate
        )

        lazy val view = app.injector.instanceOf[UpdateTemplateView]
        val form = app.injector.instanceOf[UpdateTemplateFormProvider].apply()

        implicit val doc: Document =
          Jsoup.parse(view(
            form = form,
            submitAction = controllers.sections.templates.routes.UpdateTemplateController.onSubmit(request.ern, request.draftId)
          ).toString())

        behave like pageWithExpectedElementsAndMessages(Seq(
          Selectors.title -> messagesForLanguage.title,
          Selectors.h1 -> messagesForLanguage.heading,
          Selectors.p(1) -> messagesForLanguage.p1(templateName),
          Selectors.radioButton(1) -> messagesForLanguage.yes,
          Selectors.radioButton(2) -> messagesForLanguage.no,
          Selectors.button -> messagesForLanguage.continue
        ))
      }
    }
  }
}
