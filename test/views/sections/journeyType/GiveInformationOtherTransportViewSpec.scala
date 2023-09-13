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

package views.sections.journeyType

import base.ViewSpecBase
import fixtures.messages.sections.journeyType.GiveInformationOtherTransportMessages
import forms.sections.journeyType.GiveInformationOtherTransportFormProvider
import models.NormalMode
import models.requests.DataRequest
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.i18n.Messages
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import views.html.sections.journeyType.GiveInformationOtherTransportView
import views.{BaseSelectors, ViewBehaviours}

class GiveInformationOtherTransportViewSpec extends ViewSpecBase with ViewBehaviours {

  object Selectors extends BaseSelectors

  "GiveInformationOtherTransportView" - {

    Seq(GiveInformationOtherTransportMessages.English, GiveInformationOtherTransportMessages.Welsh).foreach { messagesForLanguage =>

      s"when being rendered in lang code of '${messagesForLanguage.lang.code}'" - {

        implicit val msgs: Messages = messages(app, messagesForLanguage.lang)
        implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(), emptyUserAnswers)

        val view = app.injector.instanceOf[GiveInformationOtherTransportView]
        val form = app.injector.instanceOf[GiveInformationOtherTransportFormProvider].apply()

        implicit val doc: Document = Jsoup.parse(view(form, NormalMode).toString())

        val subHeadingCaptionSelector: String = "main .govuk-caption-l"

        behave like pageWithExpectedElementsAndMessages(Seq(
          Selectors.title -> messagesForLanguage.title,
          subHeadingCaptionSelector -> messagesForLanguage.subHeading,
          Selectors.h1 -> messagesForLanguage.heading,
          Selectors.button -> messagesForLanguage.saveAndContinue
        ))
      }
    }
  }
}
