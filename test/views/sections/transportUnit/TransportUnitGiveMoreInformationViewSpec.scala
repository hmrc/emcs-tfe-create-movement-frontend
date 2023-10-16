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
import fixtures.messages.sections.transportUnit.TransportUnitGiveMoreInformationMessages
import forms.sections.transportUnit.TransportUnitGiveMoreInformationFormProvider
import models.NormalMode
import models.requests.DataRequest
import models.sections.transportUnit.TransportUnitType._
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.i18n.Messages
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import views.html.sections.transportUnit.TransportUnitGiveMoreInformationView
import views.{BaseSelectors, ViewBehaviours}

class TransportUnitGiveMoreInformationViewSpec extends ViewSpecBase with ViewBehaviours {
  object Selectors extends BaseSelectors

  "TransportUnitGiveMoreInformationView" - {

    Seq(TransportUnitGiveMoreInformationMessages.English, TransportUnitGiveMoreInformationMessages.Welsh).foreach { messagesForLanguage =>

      Seq(
        Container -> "container",
        FixedTransport -> "fixed transport installation",
        Tractor -> "tractor",
        Trailer -> "trailer",
        Vehicle -> "vehicle"
      ).foreach { transportUnitTypeAndMessage =>

        s"when being rendered in lang code of '${messagesForLanguage.lang.code}' - for transport unit type: ${transportUnitTypeAndMessage._2}" - {

          implicit val msgs: Messages = messages(app, messagesForLanguage.lang)
          implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(), emptyUserAnswers)

          val view = app.injector.instanceOf[TransportUnitGiveMoreInformationView]
          val form = app.injector.instanceOf[TransportUnitGiveMoreInformationFormProvider].apply(transportUnitTypeAndMessage._1)

          implicit val doc: Document = Jsoup.parse(
            view(
              form = form,
              idx = testIndex1,
              mode = NormalMode,
              transportUnitType = transportUnitTypeAndMessage._1
            ).toString())

          behave like pageWithExpectedElementsAndMessages(Seq(
            Selectors.title -> messagesForLanguage.title(transportUnitTypeAndMessage._2),
            Selectors.h1 -> messagesForLanguage.heading(transportUnitTypeAndMessage._2),
            Selectors.hint -> messagesForLanguage.hintText,
            Selectors.button -> messagesForLanguage.saveAndContinue,
            Selectors.link(1) -> messagesForLanguage.returnToDraft
          ))
        }
      }
    }
  }
}

