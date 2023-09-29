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

package views.sections.transportArranger

import base.ViewSpecBase
import fixtures.messages.sections.transportArranger.TransportArrangerNameMessages
import forms.sections.transportArranger.TransportArrangerNameFormProvider
import models.NormalMode
import models.requests.DataRequest
import models.sections.transportArranger.TransportArranger.{GoodsOwner, Other}
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import pages.sections.transportArranger.TransportArrangerPage
import play.api.i18n.Messages
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import views.html.sections.transportArranger.TransportArrangerNameView
import views.{BaseSelectors, ViewBehaviours}

class TransportArrangerNameViewSpec extends ViewSpecBase with ViewBehaviours {
  object Selectors extends BaseSelectors

  "TransportArrangerNameView" - {

    Seq(TransportArrangerNameMessages.English, TransportArrangerNameMessages.Welsh).foreach { messagesForLanguage =>

      s"when being rendered in lang code of '${messagesForLanguage.lang.code}'" - {

        Seq(GoodsOwner, Other).foreach { implicit validTransportArranger =>
          s"with a transport arranger of `${validTransportArranger.getClass.getSimpleName}`" - {

            implicit val msgs: Messages = messages(app, messagesForLanguage.lang)
            implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(), emptyUserAnswers)

            val view = app.injector.instanceOf[TransportArrangerNameView]
            val form = app.injector.instanceOf[TransportArrangerNameFormProvider].apply()

            implicit val doc: Document = Jsoup.parse(
              view(
                form = form,
                mode = NormalMode,
                transportArranger = validTransportArranger
              ).toString())

            behave like pageWithExpectedElementsAndMessages(Seq(
              Selectors.title -> messagesForLanguage.title,
              Selectors.h1 -> messagesForLanguage.heading,

              Selectors.button -> messagesForLanguage.saveAndContinue,
              Selectors.link(1) -> messagesForLanguage.returnToDraft
            ))
          }
        }
      }
    }
  }
}

