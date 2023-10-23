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

package views.sections.transportUnit

import base.ViewSpecBase
import fixtures.messages.sections.transportUnit.TransportUnitRemoveUnitMessages
import forms.sections.transportUnit.{TransportUnitGiveMoreInformationChoiceFormProvider, TransportUnitRemoveUnitFormProvider}
import models.NormalMode
import models.TransportUnitType._
import models.requests.DataRequest
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.i18n.Messages
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import views.html.sections.transportUnit.{TransportUnitGiveMoreInformationChoiceView, TransportUnitRemoveUnitView}
import views.{BaseSelectors, ViewBehaviours}

class TransportUnitRemoveUnitViewSpec extends ViewSpecBase with ViewBehaviours {
  object Selectors extends BaseSelectors

  "TransportUnitRemoveUnitView" - {

    Seq(TransportUnitRemoveUnitMessages.English, TransportUnitRemoveUnitMessages.Welsh).foreach { messagesForLanguage =>

      s"when being rendered in lang code of '${messagesForLanguage.lang.code}' - for transport unit type: Container" - {

        implicit val msgs: Messages = messages(app, messagesForLanguage.lang)
        implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(), emptyUserAnswers)

        val view = app.injector.instanceOf[TransportUnitRemoveUnitView]
        val form = app.injector.instanceOf[TransportUnitRemoveUnitFormProvider].apply()

        implicit val doc: Document = Jsoup.parse(
          view(
            form = form,
            indexOfTransportUnit = testIndex1
          ).toString())

        behave like pageWithExpectedElementsAndMessages(Seq(
          Selectors.title -> messagesForLanguage.title(1),
          Selectors.subHeadingCaptionSelector -> messagesForLanguage.transportUnitsSection,
          Selectors.h1 -> messagesForLanguage.heading(1),
          Selectors.radioButton(1) -> messagesForLanguage.yes,
          Selectors.radioButton(2) -> messagesForLanguage.no,
          Selectors.button -> messagesForLanguage.saveAndContinue,
          Selectors.link(1) -> messagesForLanguage.returnToDraft
        ))
      }
    }
  }
}

