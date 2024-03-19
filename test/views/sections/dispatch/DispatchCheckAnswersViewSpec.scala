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

package views.sections.dispatch

import base.SpecBase
import fixtures.MovementSubmissionFailureFixtures
import fixtures.messages.sections.dispatch.DispatchCheckAnswersMessages
import models.requests.DataRequest
import fixtures.messages.TaskListStatusMessages
import models.CheckMode
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import pages.sections.dispatch.DispatchWarehouseExcisePage
import play.api.i18n.Messages
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryList
import utils.{DispatchWarehouseConsignorDoesNotManageWarehouseError, DispatchWarehouseInvalidError, DispatchWarehouseInvalidOrMissingOnSeedError}
import viewmodels.checkAnswers.sections.dispatch.DispatchWarehouseExciseSummary
import views.html.sections.dispatch.DispatchCheckAnswersView
import views.{BaseSelectors, ViewBehaviours}

class DispatchCheckAnswersViewSpec extends SpecBase with ViewBehaviours with MovementSubmissionFailureFixtures {
  object Selectors extends BaseSelectors {
    def govukSummaryListKey(id: Int) = s".govuk-summary-list__row:nth-of-type($id) .govuk-summary-list__key"

    val govukSummaryListChangeLink = ".govuk-summary-list__actions .govuk-link"
    val tag = ".govuk-tag--orange"
  }

  "Dispatch Business Name view" - {


    Seq(DispatchCheckAnswersMessages.English).foreach { messagesForLanguage =>

      lazy val dispatchWarehouseExciseSummary: DispatchWarehouseExciseSummary = app.injector.instanceOf[DispatchWarehouseExciseSummary]
      lazy val view = app.injector.instanceOf[DispatchCheckAnswersView]

      s"when being rendered in lang code of '${messagesForLanguage.lang.code}'" - {

        implicit val msgs: Messages = messages(Seq(messagesForLanguage.lang))
        implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(), emptyUserAnswers)
        implicit val doc: Document = Jsoup.parse(view(SummaryList(Seq.empty), testOnwardRoute).toString())

        behave like pageWithExpectedElementsAndMessages(Seq(
          Selectors.subHeadingCaptionSelector -> messagesForLanguage.dispatchSection,
          Selectors.title -> messagesForLanguage.title,
          Selectors.h1 -> messagesForLanguage.heading,
          Selectors.button -> messagesForLanguage.confirmAnswers
        ))
      }

      s"when being rendered in with an Excise Submission Error'" - {

        implicit val msgs: Messages = messages(Seq(messagesForLanguage.lang))

        implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(),
          emptyUserAnswers.copy(submissionFailures =
            DispatchWarehouseExcisePage.possibleErrors.map(error => dispatchWarehouseInvalidOrMissingOnSeedError.copy(error.code))
          )
            .set(DispatchWarehouseExcisePage, testErn)
        )

        implicit val doc: Document = Jsoup.parse(view(SummaryList(Seq(dispatchWarehouseExciseSummary.row().get)), testOnwardRoute).toString())

        behave like pageWithExpectedElementsAndMessages(Seq(
          Selectors.subHeadingCaptionSelector -> messagesForLanguage.dispatchSection,
          Selectors.title -> messagesForLanguage.title,
          Selectors.h1 -> messagesForLanguage.heading,
          Selectors.notificationBannerTitle -> messagesForLanguage.updateNeeded,
          Selectors.govukSummaryListKey(1) -> messagesForLanguage.ern,
          Selectors.tag -> TaskListStatusMessages.English.updateNeededTag,
          Selectors.submissionError(DispatchWarehouseInvalidOrMissingOnSeedError) -> messagesForLanguage.dispatchWarehouseInvalidOrMissingOnSeedError,
          Selectors.submissionError(DispatchWarehouseInvalidError) -> messagesForLanguage.dispatchWarehouseInvalidError,
          Selectors.submissionError(DispatchWarehouseConsignorDoesNotManageWarehouseError) -> messagesForLanguage.dispatchWarehouseConsignorDoesNotManageWarehouseError,
          Selectors.button -> messagesForLanguage.confirmAnswers,
        ))

        "link to the consignee excise page" in {
          val route = controllers.sections.dispatch.routes.DispatchWarehouseExciseController.onPageLoad(testErn, testDraftId, CheckMode).url
          DispatchWarehouseExcisePage.possibleErrors.foreach(
            error => doc.select(Selectors.submissionError(error)).attr("href") mustBe route
          )
        }
      }
    }
  }
}

