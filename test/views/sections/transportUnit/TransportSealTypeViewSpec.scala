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
import fixtures.messages.sections.transportUnit.TransportSealTypeMessages
import forms.sections.transportUnit.TransportSealTypeFormProvider
import models.requests.DataRequest
import models.sections.transportUnit.TransportUnitType
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.i18n.Messages
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import views.html.sections.transportUnit.TransportSealTypeView
import views.{BaseSelectors, ViewBehaviours}

class TransportSealTypeViewSpec extends ViewSpecBase with ViewBehaviours {
  object Selectors extends BaseSelectors

  "TransportSealTypeView" - {

    Seq(TransportSealTypeMessages.English).foreach { messagesForLanguage =>

      s"when being rendered in lang code of '${messagesForLanguage.lang.code}'" - {

        TransportUnitType.values foreach { transportUnitType =>

          s"when transport unit type is $transportUnitType" - {

            implicit val msgs: Messages = messages(app, messagesForLanguage.lang)
            implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(), emptyUserAnswers)

            val view = app.injector.instanceOf[TransportSealTypeView]
            val form = app.injector.instanceOf[TransportSealTypeFormProvider].apply()

            implicit val doc: Document = Jsoup.parse(
              view(
                form = form,
                transportUnitType = transportUnitType,
                testOnwardRoute
              ).toString())

            behave like pageWithExpectedElementsAndMessages(Seq(
              Selectors.h2(1) -> messagesForLanguage.transportUnitsSection,
              Selectors.title -> messagesForLanguage.title(transportUnitType),
              Selectors.h1 -> messagesForLanguage.heading(transportUnitType),
              Selectors.label("value") -> messagesForLanguage.sealType,
              Selectors.label("moreInfo") -> messagesForLanguage.moreInfo,
              Selectors.hint -> messagesForLanguage.moreInfoHint,
              Selectors.button -> messagesForLanguage.saveAndContinue,
              Selectors.link(1) -> messagesForLanguage.returnToDraft
            ))
          }
        }
      }
    }
  }
}

