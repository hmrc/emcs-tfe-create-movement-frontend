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

package viewmodels.checkAnswers.sections.destination

import base.SpecBase
import fixtures.MovementSubmissionFailureFixtures
import fixtures.messages.sections.destination.DestinationWarehouseExciseMessages.English
import models.CheckMode
import models.requests.DataRequest
import org.scalatest.matchers.must.Matchers
import pages.sections.destination.DestinationWarehouseExcisePage
import play.api.i18n.Messages
import play.api.test.FakeRequest
import play.twirl.api.{Html, HtmlFormat}
import uk.gov.hmrc.govukfrontend.views.Aliases.Value
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.{HtmlContent, Text}
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.{Key, SummaryListRow}
import utils.{ExciseIdForTaxWarehouseInvalid, ExciseIdForTaxWarehouseOfDestinationInvalidError, ExciseIdForTaxWarehouseOfDestinationNeedsConsigneeError}
import viewmodels.govuk.summarylist._
import views.html.components.tag


class DestinationWarehouseExciseSummarySpec extends SpecBase with Matchers with MovementSubmissionFailureFixtures {

  lazy val destinationWarehouseExciseSummary = app.injector.instanceOf[DestinationWarehouseExciseSummary]
  lazy val tag: tag = app.injector.instanceOf[tag]

  "DestinationWarehouseExciseSummary" - {

    s"when being rendered in lang code of '${English.lang.code}'" - {

      implicit val msgs: Messages = messages(Seq(English.lang))

      "when there's no answer" - {

        "must output no row" in {

          implicit lazy val request = dataRequest(FakeRequest(), emptyUserAnswers)

          destinationWarehouseExciseSummary.row() mustBe None
        }
      }

      "when there's an answer" - {

        "must output the expected row" in {

          implicit lazy val request = dataRequest(FakeRequest(), emptyUserAnswers.set(DestinationWarehouseExcisePage, "XIRC123456789"))

          destinationWarehouseExciseSummary.row() mustBe expectedRow(hasUpdateNeededTag = false)
        }

        "and there is a 704 error" - {

          def requestWithTestErnSet(errorCode: String, hasBeenFixed: Boolean): DataRequest[_] = {
            dataRequest(FakeRequest(),
              emptyUserAnswers.copy(submissionFailures = Seq(
                movementSubmissionFailure.copy(
                  errorType = errorCode,
                  hasBeenFixed = hasBeenFixed
                )
              )).set(DestinationWarehouseExcisePage, testErn))
          }

          "must render an 'Update Needed' tag against the Destination Section, " +
            "when an ExciseIdForTaxWarehouseOfDestinationInvalidError has not been fixed" in {
            implicit lazy val request =
              requestWithTestErnSet(ExciseIdForTaxWarehouseOfDestinationInvalidError.code, hasBeenFixed = false)

            destinationWarehouseExciseSummary.row() mustBe expectedRow(hasUpdateNeededTag = true)
          }

          "must render an 'Update Needed' tag against the Destination Section, " +
            "when an ExciseIdForTaxWarehouseOfDestinationNeedsConsigneeError has not been fixed" in {
            implicit lazy val request =
              requestWithTestErnSet(ExciseIdForTaxWarehouseOfDestinationNeedsConsigneeError.code, hasBeenFixed = false)

            destinationWarehouseExciseSummary.row() mustBe expectedRow(hasUpdateNeededTag = true)
          }

          "must render an 'Update Needed' tag against the Destination Section, " +
            "when an ExciseIdForTaxWarehouseInvalid has not been fixed" in {
            implicit lazy val request =
              requestWithTestErnSet(ExciseIdForTaxWarehouseInvalid.code, hasBeenFixed = false)

            destinationWarehouseExciseSummary.row() mustBe expectedRow(hasUpdateNeededTag = true)
          }

          "must not render an 'Update Needed' tag against the Destination Section, " +
            "when an error relevant to the section has been fixed" in {
            implicit lazy val request =
              requestWithTestErnSet(ExciseIdForTaxWarehouseInvalid.code, hasBeenFixed = true)

            destinationWarehouseExciseSummary.row() mustBe expectedRow(hasUpdateNeededTag = false)
          }
        }
      }
    }
  }

  private def expectedRow(hasUpdateNeededTag: Boolean)(implicit messages: Messages): Option[SummaryListRow] = {
    Some(SummaryListRowViewModel(
      key = Key(Text(English.cyaLabel)),
      value = Value(HtmlContent(HtmlFormat.fill(Seq(
        Some(Html(testErn)),
        if (hasUpdateNeededTag) Some(tag("taskListStatus.updateNeeded", "orange", "float-none govuk-!-margin-left-1")) else None
      ).flatten))),
      actions = Seq(
        ActionItemViewModel(
          content = Text(English.change),
          href = controllers.sections.destination.routes.DestinationWarehouseExciseController.onPageLoad(testErn, testDraftId, CheckMode).url,
          id = "changeDestinationWarehouseExcise"
        ).withVisuallyHiddenText(English.cyaChangeHidden)
      )
    ))
  }
}
