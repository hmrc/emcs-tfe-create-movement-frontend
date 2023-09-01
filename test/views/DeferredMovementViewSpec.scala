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

package views

import base.ViewSpecBase
import fixtures.messages.DeferredMovementMessages
import forms.DeferredMovementFormProvider
import models.requests.UserRequest
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.i18n.Messages
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import views.html.DeferredMovementView

class DeferredMovementViewSpec extends ViewSpecBase with ViewBehaviours {

  object Selectors extends BaseSelectors

  "Deferred Movement view" - {

    Seq(DeferredMovementMessages.English, DeferredMovementMessages.Welsh).foreach { messagesForLanguage =>

      s"when being rendered in lang code of '${messagesForLanguage.lang.code}'" - {

        implicit val msgs: Messages = messages(app, messagesForLanguage.lang)
        implicit val request: UserRequest[AnyContentAsEmpty.type] = userRequest(FakeRequest())

        val view = app.injector.instanceOf[DeferredMovementView]
        val form = app.injector.instanceOf[DeferredMovementFormProvider].apply()

        implicit val doc: Document = Jsoup.parse(view(form, controllers.routes.DeferredMovementController.onSubmit(request.ern)).toString())

        behave like pageWithExpectedElementsAndMessages(Seq(
          Selectors.title -> messagesForLanguage.title,
          Selectors.h1 -> messagesForLanguage.heading,
          Selectors.h2(1) -> messagesForLanguage.caption,
          Selectors.hint -> messagesForLanguage.hint,
          Selectors.summary(1) -> messagesForLanguage.summary,
          Selectors.p(1) -> messagesForLanguage.paragraph1,
          Selectors.p(2) -> messagesForLanguage.paragraph2,
          Selectors.radioButton(1) -> messagesForLanguage.yes,
          Selectors.radioButton(2) -> messagesForLanguage.no,
          Selectors.button -> messagesForLanguage.continue
        ))
      }
    }
  }
}
