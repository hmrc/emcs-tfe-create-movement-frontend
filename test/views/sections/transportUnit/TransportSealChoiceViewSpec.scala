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
import fixtures.messages.sections.transportUnit.TransportSealChoiceMessages
import forms.TransportSealChoiceFormProvider
import models.NormalMode
import models.TransportUnitType._
import models.requests.DataRequest
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import pages.TransportUnitTypePage
import play.api.i18n.Messages
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import views.html.sections.transportUnit.TransportSealChoiceView
import views.{BaseSelectors, ViewBehaviours}

class TransportSealChoiceViewSpec extends ViewSpecBase with ViewBehaviours {

  object Selectors extends BaseSelectors

  "TransportSealChoice view" - {

    Seq(TransportSealChoiceMessages.English, TransportSealChoiceMessages.Welsh).foreach { messagesForLanguage =>

      s"when being rendered in lang code of '${messagesForLanguage.lang.code}' for Container" - {

        implicit val msgs: Messages = messages(app, messagesForLanguage.lang)

        implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(), emptyUserAnswers.set(TransportUnitTypePage, Tractor))
        val view = app.injector.instanceOf[TransportSealChoiceView]
        val form = app.injector.instanceOf[TransportSealChoiceFormProvider].apply(Container)

        implicit val doc: Document = Jsoup.parse(view(form, NormalMode, Container
        ).toString())

        behave like pageWithExpectedElementsAndMessages(Seq(
          Selectors.title -> messagesForLanguage.titleContainer,
          Selectors.h1 -> messagesForLanguage.headingContainer,
          Selectors.button -> messagesForLanguage.saveAndContinue,
        ))

      }

      s"when being rendered in lang code of '${messagesForLanguage.lang.code}' for Tractor" - {

        implicit val msgs: Messages = messages(app, messagesForLanguage.lang)

        implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(), emptyUserAnswers.set(TransportUnitTypePage, Tractor))
        val view = app.injector.instanceOf[TransportSealChoiceView]
        val form = app.injector.instanceOf[TransportSealChoiceFormProvider].apply(Tractor)

        implicit val doc: Document = Jsoup.parse(view(form, NormalMode, Tractor
        ).toString())

        behave like pageWithExpectedElementsAndMessages(Seq(
          Selectors.title -> messagesForLanguage.titleTractor,
          Selectors.h1 -> messagesForLanguage.headingTractor,
          Selectors.button -> messagesForLanguage.saveAndContinue,
        ))
      }
      s"when being rendered in lang code of '${messagesForLanguage.lang.code}' for Trailer" - {

        implicit val msgs: Messages = messages(app, messagesForLanguage.lang)

        implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(), emptyUserAnswers.set(TransportUnitTypePage, Trailer))
        val view = app.injector.instanceOf[TransportSealChoiceView]
        val form = app.injector.instanceOf[TransportSealChoiceFormProvider].apply(Trailer)

        implicit val doc: Document = Jsoup.parse(view(form, NormalMode, Trailer
        ).toString())

        behave like pageWithExpectedElementsAndMessages(Seq(
          Selectors.title -> messagesForLanguage.titleTrailer,
          Selectors.h1 -> messagesForLanguage.headingTrailer,
          Selectors.button -> messagesForLanguage.saveAndContinue,
        ))
      }

      s"when being rendered in lang code of '${messagesForLanguage.lang.code}' for FixedTransport" - {

        implicit val msgs: Messages = messages(app, messagesForLanguage.lang)

        implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(), emptyUserAnswers.set(TransportUnitTypePage, FixedTransport))
        val view = app.injector.instanceOf[TransportSealChoiceView]
        val form = app.injector.instanceOf[TransportSealChoiceFormProvider].apply(FixedTransport)

        implicit val doc: Document = Jsoup.parse(view(form, NormalMode, FixedTransport
        ).toString())

        behave like pageWithExpectedElementsAndMessages(Seq(
          Selectors.title -> messagesForLanguage.titleFixed,
          Selectors.h1 -> messagesForLanguage.headingFixed,
          Selectors.button -> messagesForLanguage.saveAndContinue,
        ))
      }

      s"when being rendered in lang code of '${messagesForLanguage.lang.code} for Vehicle'" - {

        implicit val msgs: Messages = messages(app, messagesForLanguage.lang)

        implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(), emptyUserAnswers.set(TransportUnitTypePage, Vehicle))
        val view = app.injector.instanceOf[TransportSealChoiceView]
        val form = app.injector.instanceOf[TransportSealChoiceFormProvider].apply(Vehicle)

        implicit val doc: Document = Jsoup.parse(view(form, NormalMode, Vehicle
        ).toString())

        behave like pageWithExpectedElementsAndMessages(Seq(
          Selectors.title -> messagesForLanguage.titleVehicle,
          Selectors.h1 -> messagesForLanguage.headingVehicle,
          Selectors.button -> messagesForLanguage.saveAndContinue,
        ))
      }

    }
  }
}
