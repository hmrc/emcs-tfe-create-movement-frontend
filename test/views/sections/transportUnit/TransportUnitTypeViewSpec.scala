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

import base.SpecBase
import fixtures.messages.sections.transportUnit.TransportUnitTypeMessages
import forms.sections.transportUnit.TransportUnitTypeFormProvider
import models.NormalMode
import models.requests.DataRequest
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.i18n.Messages
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import views.html.sections.transportUnit.TransportUnitTypeView
import views.{BaseSelectors, ViewBehaviours}

class TransportUnitTypeViewSpec extends SpecBase with ViewBehaviours {
  object Selectors extends BaseSelectors

  "TransportUnitTypeView" - {

    Seq(TransportUnitTypeMessages.English).foreach { messagesForLanguage =>

      s"when being rendered in lang code of '${messagesForLanguage.lang.code}'" - {

        implicit val msgs: Messages = messages(Seq(messagesForLanguage.lang))
        implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(), emptyUserAnswers)

        val view = app.injector.instanceOf[TransportUnitTypeView]
        val form = app.injector.instanceOf[TransportUnitTypeFormProvider].apply()

        implicit val doc: Document = Jsoup.parse(
          view(
            form = form,
            idx = testIndex1,
            mode = NormalMode
          ).toString())

        behave like pageWithExpectedElementsAndMessages(Seq(
          Selectors.title -> messagesForLanguage.title,
          Selectors.h1 -> messagesForLanguage.heading,
          Selectors.radioButton(1) -> messagesForLanguage.containerRadioOption,
          Selectors.radioButton(2) -> messagesForLanguage.fixedTransportRadioOption,
          Selectors.radioButton(3) -> messagesForLanguage.tractorRadioOption,
          Selectors.radioButton(4) -> messagesForLanguage.trailerRadioOption,
          Selectors.radioButton(5) -> messagesForLanguage.vehicleRadioOption,
          Selectors.button -> messagesForLanguage.saveAndContinue,
          Selectors.link(1) -> messagesForLanguage.returnToDraft
        ))
      }
    }
  }
}

