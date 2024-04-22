/*
 * Copyright 2024 HM Revenue & Customs
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

package viewmodels.checkAnswers.sections.consignee

import base.SpecBase
import fixtures.MovementSubmissionFailureFixtures
import fixtures.messages.sections.consignee.ConsigneeExciseMessages.English
import models.CheckMode
import models.sections.info.movementScenario.MovementScenario
import models.sections.info.movementScenario.MovementScenario._
import org.scalatest.matchers.must.Matchers
import pages.sections.consignee.ConsigneeExcisePage
import pages.sections.info.DestinationTypePage
import play.api.i18n.Messages
import play.api.test.FakeRequest
import play.twirl.api.{Html, HtmlFormat}
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.{HtmlContent, Text}
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.{Key, SummaryListRow, Value}
import utils.InvalidOrMissingConsigneeError
import viewmodels.govuk.summarylist._
import views.html.components.tag


class ConsigneeExciseSummarySpec extends SpecBase with Matchers with MovementSubmissionFailureFixtures {

  lazy val summary: ConsigneeExciseSummary = app.injector.instanceOf[ConsigneeExciseSummary]
  lazy val tag: tag = app.injector.instanceOf[tag]

  "ConsigneeExciseSummary" - {

    s"when being rendered in lang code of '${English.lang.code}'" - {

      implicit val msgs: Messages = messages(Seq(English.lang))

      def expectedRow(hasUpdateNeededTag: Boolean, movementScenario: MovementScenario): Option[SummaryListRow] = {
        Some(SummaryListRowViewModel(
          key = Key(Text(English.cyaLabel(movementScenario.destinationType))),
          value = Value(HtmlContent(HtmlFormat.fill(Seq(
            Some(Html(testErn)),
            if (hasUpdateNeededTag) Some(tag("taskListStatus.updateNeeded", "orange", "float-none govuk-!-margin-left-1")) else None
          ).flatten))),
          actions = Seq(
            ActionItemViewModel(
              content = Text(English.change),
              href = controllers.sections.consignee.routes.ConsigneeExciseController.onPageLoad(testErn, testDraftId, CheckMode).url,
              id = "changeConsigneeExcise"
            ).withVisuallyHiddenText(English.cyaChangeHidden(movementScenario.destinationType))
          )
        ))
      }


      Seq(GbTaxWarehouse, EuTaxWarehouse, DirectDelivery, RegisteredConsignee, TemporaryRegisteredConsignee, TemporaryCertifiedConsignee).foreach {
        movementScenario =>
          s"with a $movementScenario movement scenario" - {

            "when there's no answer" - {

              "must output the Not Provided" in {

                implicit lazy val request = dataRequest(FakeRequest(), emptyUserAnswers)

                summary.row(showActionLinks = true) mustBe None
              }
            }

            "when there's an answer" - {

              "when the show action link boolean is true" - {

                "must output the expected row" in {

                  implicit lazy val request = dataRequest(FakeRequest(), emptyUserAnswers
                    .set(ConsigneeExcisePage, testErn)
                    .set(DestinationTypePage, movementScenario)
                  )

                  summary.row(showActionLinks = true) mustBe expectedRow(hasUpdateNeededTag = false, movementScenario)
                }
              }
            }

            "when there is a 704 error" - {

              "must render an 'Update Needed' tag against the ERN - when the error has not been fixed" in {

                implicit lazy val request = dataRequest(FakeRequest(),
                  emptyUserAnswers
                    .copy(submissionFailures = Seq(movementSubmissionFailure.copy(errorType = InvalidOrMissingConsigneeError.code, hasBeenFixed = false)))
                    .set(ConsigneeExcisePage, testErn)
                    .set(DestinationTypePage, movementScenario)
                )

                summary.row(showActionLinks = true) mustBe expectedRow(hasUpdateNeededTag = true, movementScenario)
              }

              "must not render an 'Update Needed' tag against the ERN - when the error has been fixed" in {

                implicit lazy val request = dataRequest(FakeRequest(),
                  emptyUserAnswers
                    .copy(submissionFailures = Seq(movementSubmissionFailure.copy(errorType = InvalidOrMissingConsigneeError.code, hasBeenFixed = true)))
                    .set(ConsigneeExcisePage, testErn)
                    .set(DestinationTypePage, movementScenario)
                )

                summary.row(showActionLinks = true) mustBe expectedRow(hasUpdateNeededTag = false, movementScenario)
              }
            }
          }
      }
    }
  }
}
