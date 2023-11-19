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

import base.SpecBase
import fixtures.messages.sections.transportArranger.TransportArrangerCheckAnswersMessages
import models.requests.DataRequest
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.i18n.{Messages, MessagesApi}
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import uk.gov.hmrc.govukfrontend.views.Aliases.SummaryList
import views.html.sections.transportArranger.TransportArrangerCheckAnswersView
import views.{BaseSelectors, ViewBehaviours}

class TransportArrangerCheckAnswersViewSpec extends SpecBase with ViewBehaviours {

  object Selectors extends BaseSelectors

  "TransportArrangerCheckAnswers view" - {

    Seq(TransportArrangerCheckAnswersMessages.English).foreach { messagesForLanguage =>

      s"when being rendered in lang code of '${messagesForLanguage.lang.code}'" - {

        implicit val msgs: Messages = app.injector.instanceOf[MessagesApi].preferred(Seq(messagesForLanguage.lang))
        implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(), emptyUserAnswers)

        val view = app.injector.instanceOf[TransportArrangerCheckAnswersView]

        implicit val doc: Document = Jsoup.parse(view(
          SummaryList(Seq()),
          controllers.sections.transportArranger.routes.TransportArrangerCheckAnswersController.onSubmit(testErn, testDraftId)
        ).toString())

        behave like pageWithExpectedElementsAndMessages(Seq(
          Selectors.title -> messagesForLanguage.title,
          Selectors.h2(1) -> messagesForLanguage.sectionSubheading,
          Selectors.h1 -> messagesForLanguage.heading,
          Selectors.button -> messagesForLanguage.confirmAnswers
        ))
      }
    }
  }
}
