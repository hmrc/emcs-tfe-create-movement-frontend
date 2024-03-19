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

package views.sections.info

import base.SpecBase
import fixtures.MovementSubmissionFailureFixtures
import fixtures.messages.sections.info.InformationCheckAnswersMessages
import models.requests.DataRequest
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.i18n.Messages
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import uk.gov.hmrc.govukfrontend.views.Aliases.SummaryList
import utils.LocalReferenceNumberError
import views.html.sections.info.InformationCheckAnswersView
import views.{BaseSelectors, ViewBehaviours}

class InformationCheckAnswersViewSpec extends SpecBase with ViewBehaviours with MovementSubmissionFailureFixtures {

  object Selectors extends BaseSelectors {
    val fixLRNLink = "#fix-local-reference-number"
  }

  "InformationArrangerCheckAnswers view" - {

    Seq(InformationCheckAnswersMessages.English).foreach { messagesForLanguage =>

      s"when being rendered in lang code of '${messagesForLanguage.lang.code}'" - {

        implicit val msgs: Messages = messages(Seq(messagesForLanguage.lang))
        implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(), emptyUserAnswers)

        lazy val view = app.injector.instanceOf[InformationCheckAnswersView]

        implicit def doc(implicit request: DataRequest[_]): Document = Jsoup.parse(view(
          SummaryList(Seq()),
          controllers.sections.info.routes.InformationCheckAnswersController.onPreDraftSubmit(testErn)
        ).toString())

        behave like pageWithExpectedElementsAndMessages(Seq(
          Selectors.title -> messagesForLanguage.title,
          Selectors.h2(1) -> messagesForLanguage.sectionSubheading,
          Selectors.h1 -> messagesForLanguage.heading,
          Selectors.button -> messagesForLanguage.confirmAnswers
        ))

        "when there is a 704 error" - {

          implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(), emptyUserAnswers
          .copy(submissionFailures = Seq(movementSubmissionFailure.copy(errorType = LocalReferenceNumberError.code, hasBeenFixed = false))))

          behave like pageWithExpectedElementsAndMessages(Seq(
            Selectors.title -> messagesForLanguage.title,
            Selectors.subHeadingCaptionSelector -> messagesForLanguage.sectionSubheading,
            Selectors.h1 -> messagesForLanguage.heading,
            Selectors.notificationBannerTitle -> messagesForLanguage.updateNeeded,
            Selectors.notificationBannerContent -> messagesForLanguage.lrnSubmissionFailure,
            Selectors.button -> messagesForLanguage.confirmAnswers
          ))

          "link to the LRN page" in {
            doc.select(Selectors.fixLRNLink).attr("href") mustBe controllers.sections.info.routes.LocalReferenceNumberController.onPageLoad(testErn, testDraftId).url
          }
        }
      }
    }
  }
}
