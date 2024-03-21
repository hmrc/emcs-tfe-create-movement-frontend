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

package views.sections.destination

import base.SpecBase
import fixtures.MovementSubmissionFailureFixtures
import fixtures.messages.TaskListStatusMessages
import fixtures.messages.sections.destination.DestinationCheckAnswersMessages.English
import models.requests.DataRequest
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import pages.sections.destination.DestinationWarehouseExcisePage
import play.api.i18n.Messages
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryList
import utils.{PlaceOfDestinationExciseIdForTaxWarehouseInvalidError, PlaceOfDestinationExciseIdInvalidError, PlaceOfDestinationNoLinkBetweenConsigneeAndPlaceOfDeliveryError}
import viewmodels.checkAnswers.sections.destination.DestinationWarehouseExciseSummary
import views.html.sections.destination.DestinationCheckAnswersView
import views.{BaseSelectors, ViewBehaviours}

class DestinationCheckAnswersViewSpec extends SpecBase with ViewBehaviours with MovementSubmissionFailureFixtures {
  object Selectors extends BaseSelectors {
    val tag = ".govuk-tag--orange"
  }

  implicit val msgs: Messages = messages(Seq(English.lang))
  lazy val destinationWarehouseExciseSummary: DestinationWarehouseExciseSummary = app.injector.instanceOf[DestinationWarehouseExciseSummary]
  lazy val view = app.injector.instanceOf[DestinationCheckAnswersView]

  "Destination Business Name view" - {

    s"when being rendered in lang code of '${English.lang.code}'" - {

      implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(), emptyUserAnswers)
      implicit val doc: Document = Jsoup.parse(view(SummaryList(Seq.empty), testOnwardRoute).toString())

      behave like pageWithExpectedElementsAndMessages(Seq(
        Selectors.subHeadingCaptionSelector -> English.destinationSection,
        Selectors.title -> English.title,
        Selectors.h1 -> English.heading,
        Selectors.button -> English.confirmAnswers
      ))
    }

    s"when being rendered with Destination Warehouse Excise errors in lang code of '${English.lang.code}'" - {

      implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(),
        emptyUserAnswers.copy(submissionFailures =
          DestinationWarehouseExcisePage.possibleErrors.map(error => destinationWarehouseExciseFailure.copy(error.code))
        )
          .set(DestinationWarehouseExcisePage, testErn)
      )

      implicit val doc: Document = Jsoup.parse(view(SummaryList(Seq(destinationWarehouseExciseSummary.row().get)), testOnwardRoute).toString())

      behave like pageWithExpectedElementsAndMessages(Seq(
        Selectors.subHeadingCaptionSelector -> English.destinationSection,
        Selectors.title -> English.title,
        Selectors.h1 -> English.heading,
        Selectors.button -> English.confirmAnswers,
        Selectors.notificationBannerTitle -> English.updateNeeded,
        Selectors.tag -> TaskListStatusMessages.English.updateNeededTag,
        Selectors.submissionError(PlaceOfDestinationExciseIdInvalidError) -> English.placeOfDestinationExciseIdInvalidError,
        Selectors.submissionError(PlaceOfDestinationNoLinkBetweenConsigneeAndPlaceOfDeliveryError) -> English.placeOfDestinationNoLinkBetweenConsigneeAndPlaceOfDeliveryError,
        Selectors.submissionError(PlaceOfDestinationExciseIdForTaxWarehouseInvalidError) -> English.placeOfDestinationExciseIdForTaxWarehouseInvalidError,
      ))

    }
  }
}

