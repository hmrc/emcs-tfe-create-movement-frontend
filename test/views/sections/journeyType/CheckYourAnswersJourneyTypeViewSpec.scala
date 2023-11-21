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

import base.SpecBase
import fixtures.messages.sections.journeyType.CheckYourAnswersJourneyTypeMessages
import models.requests.DataRequest
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.i18n.Messages
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import uk.gov.hmrc.govukfrontend.views.Aliases.SummaryList
import views.html.sections.journeyType.CheckYourAnswersJourneyTypeView
import views.{BaseSelectors, ViewBehaviours}

class CheckYourAnswersJourneyTypeViewSpec extends SpecBase with ViewBehaviours {

  object Selectors extends BaseSelectors

  "CheckYourAnswersJourneyType view" - {

    Seq(CheckYourAnswersJourneyTypeMessages.English).foreach { messagesForLanguage =>

      s"when being rendered in lang code of '${messagesForLanguage.lang.code}'" - {

        implicit val msgs: Messages = messages(Seq(messagesForLanguage.lang))
        implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(), emptyUserAnswers)

       lazy val view = app.injector.instanceOf[CheckYourAnswersJourneyTypeView]

        implicit val doc: Document = Jsoup.parse(view(
          SummaryList(Seq()),
          controllers.sections.journeyType.routes.CheckYourAnswersJourneyTypeController.onSubmit(testErn, testDraftId)
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
