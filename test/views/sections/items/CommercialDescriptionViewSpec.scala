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

package views.sections.items

import base.ViewSpecBase
import fixtures.messages.sections.items.CommercialDescriptionMessages
import forms.sections.items.CommercialDescriptionFormProvider
import models.GoodsTypeModel.{Beer, Energy, Wine}
import models.requests.DataRequest
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.i18n.{Messages, MessagesApi}
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import views.html.sections.items.CommercialDescriptionView
import views.{BaseSelectors, ViewBehaviours}

class CommercialDescriptionViewSpec extends ViewSpecBase with ViewBehaviours {

  object Selectors extends BaseSelectors

  "CommercialDescriptionView for Beer" - {

    Seq(CommercialDescriptionMessages.English).foreach { messagesForLanguage =>

      s"when being rendered in lang code of '${messagesForLanguage.lang.code}'" - {

        implicit val msgs: Messages = app.injector.instanceOf[MessagesApi].preferred(Seq(messagesForLanguage.lang))
        implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(), emptyUserAnswers)

        val view = app.injector.instanceOf[CommercialDescriptionView]
        val form = app.injector.instanceOf[CommercialDescriptionFormProvider].apply()

        implicit val doc: Document = Jsoup.parse(view(form, testOnwardRoute, Beer).toString())

        behave like pageWithExpectedElementsAndMessages(Seq(
          Selectors.title -> messagesForLanguage.title(Beer.toSingularOutput()),
          Selectors.hiddenText -> messagesForLanguage.hiddenSectionContent,
          Selectors.h1 -> messagesForLanguage.heading(Beer.toSingularOutput()),
          Selectors.hint -> messagesForLanguage.hintb,
          Selectors.summary(1) -> messagesForLanguage.summary,
          Selectors.p(1) -> messagesForLanguage.detailsBeerP,
          Selectors.bullet(1) -> messagesForLanguage.detailsBeerB1,
          Selectors.bullet(2) -> messagesForLanguage.detailsBeerB2,
          Selectors.button -> messagesForLanguage.saveAndContinue,
          Selectors.saveAndExitLink -> messagesForLanguage.returnToDraft
        ))
      }
    }
  }
  "CommercialDescriptionView for Wine" - {

    Seq(CommercialDescriptionMessages.English).foreach { messagesForLanguage =>

      s"when being rendered in lang code of '${messagesForLanguage.lang.code}'" - {

        implicit val msgs: Messages = messages(app, messagesForLanguage.lang)
        implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(), emptyUserAnswers)

        val view = app.injector.instanceOf[CommercialDescriptionView]
        val form = app.injector.instanceOf[CommercialDescriptionFormProvider].apply()

        implicit val doc: Document = Jsoup.parse(view(form, testOnwardRoute, Wine).toString())

        behave like pageWithExpectedElementsAndMessages(Seq(
          Selectors.title -> messagesForLanguage.title(Wine.toSingularOutput()),
          Selectors.hiddenText -> messagesForLanguage.hiddenSectionContent,
          Selectors.h1 -> messagesForLanguage.heading(Wine.toSingularOutput()),
          Selectors.hint -> messagesForLanguage.hintw,
          Selectors.summary(1) -> messagesForLanguage.summary,
          Selectors.p(1) -> messagesForLanguage.detailsWineP,
          Selectors.bullet(1) -> messagesForLanguage.detailsWineB1,
          Selectors.bullet(2) -> messagesForLanguage.detailsWineB2,
          Selectors.bullet(3) -> messagesForLanguage.detailsWineB3,
          Selectors.bullet(4) -> messagesForLanguage.detailsWineB4,
          Selectors.bullet(5) -> messagesForLanguage.detailsWineB5,
          Selectors.button -> messagesForLanguage.saveAndContinue,
          Selectors.saveAndExitLink -> messagesForLanguage.returnToDraft
        ))
      }
    }
  }

  "CommercialDescriptionView for Energy" - {

    Seq(CommercialDescriptionMessages.English).foreach { messagesForLanguage =>

      s"when being rendered in lang code of '${messagesForLanguage.lang.code}'" - {

        implicit val msgs: Messages = messages(app, messagesForLanguage.lang)
        implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(), emptyUserAnswers)

        val view = app.injector.instanceOf[CommercialDescriptionView]
        val form = app.injector.instanceOf[CommercialDescriptionFormProvider].apply()

        implicit val doc: Document = Jsoup.parse(view(form, testOnwardRoute, Energy).toString())

        behave like pageWithExpectedElementsAndMessages(Seq(
          Selectors.title -> messagesForLanguage.title(Energy.toSingularOutput()),
          Selectors.hiddenText -> messagesForLanguage.hiddenSectionContent,
          Selectors.h1 -> messagesForLanguage.heading(Energy.toSingularOutput()),
          Selectors.hint -> messagesForLanguage.hinte,
          Selectors.button -> messagesForLanguage.saveAndContinue,
          Selectors.saveAndExitLink -> messagesForLanguage.returnToDraft
        ))
      }
    }
  }
}
