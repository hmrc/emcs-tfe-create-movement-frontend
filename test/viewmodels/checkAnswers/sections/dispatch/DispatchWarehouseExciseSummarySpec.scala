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

package viewmodels.checkAnswers.sections.dispatch

import base.SpecBase
import fixtures.MovementSubmissionFailureFixtures
import fixtures.messages.sections.dispatch.DispatchWarehouseExciseMessages
import models.CheckMode
import org.scalatest.matchers.must.Matchers
import pages.sections.dispatch.DispatchWarehouseExcisePage
import play.api.i18n.Messages
import play.api.test.FakeRequest
import uk.gov.hmrc.govukfrontend.views.Aliases.{HtmlContent, Value}
import utils._
import viewmodels.govuk.summarylist._
import viewmodels.implicits._
import views.html.components.tag

class DispatchWarehouseExciseSummarySpec extends SpecBase with Matchers with MovementSubmissionFailureFixtures {

  lazy val dispatchWarehouseExciseSummary: DispatchWarehouseExciseSummary = app.injector.instanceOf[DispatchWarehouseExciseSummary]
  lazy val tag: tag = app.injector.instanceOf[tag]

  "DispatchWarehouseExciseSummary" - {

    Seq(DispatchWarehouseExciseMessages.English).foreach { messagesForLanguage =>

      s"when being rendered in lang code of '${messagesForLanguage.lang.code}'" - {

        implicit val msgs: Messages = messages(Seq(messagesForLanguage.lang))

        "when there's no answer" - {

          "must output no row" in {

            implicit lazy val request = dataRequest(FakeRequest(), emptyUserAnswers)

            dispatchWarehouseExciseSummary.row() mustBe None
          }
        }

        "when there's an answer" - {

          s"must output the expected row for DispatchWarehouseExcise" in {

            implicit lazy val request = dataRequest(FakeRequest(), emptyUserAnswers.set(DispatchWarehouseExcisePage, "GB123456789"))

            dispatchWarehouseExciseSummary.row() mustBe
              Some(
                SummaryListRowViewModel(
                  key = messagesForLanguage.cyaLabel,
                  value = Value(HtmlContent("GB123456789")),
                  actions = Seq(
                    ActionItemViewModel(
                      content = messagesForLanguage.change,
                      href = controllers.sections.dispatch.routes.DispatchWarehouseExciseController.onPageLoad(testErn, testDraftId, CheckMode).url,
                      id = "dispatchWarehouseExcise"
                    ).withVisuallyHiddenText(messagesForLanguage.cyaChangeHidden)
                  )
                )
              )
          }

          "and there is a 704 error" - {

            "must render an 'Update Needed' tag against the ERN - when the error has not been fixed" in {

              implicit lazy val request = dataRequest(FakeRequest(),
                emptyUserAnswers
                  .copy(submissionFailures = Seq(movementSubmissionFailure.copy(errorType = dispatchWarehouseInvalidOrMissingOnSeedError.errorType, hasBeenFixed = false)))
                  .set(DispatchWarehouseExcisePage, testErn))

              dispatchWarehouseExciseSummary.row() mustBe Some(
                SummaryListRowViewModel(
                  key = messagesForLanguage.cyaLabel,
                  value = Value(HtmlContent(testErn + tag("taskListStatus.updateNeeded", "orange", "float-none govuk-!-margin-left-1").toString())),
                  actions = Seq(
                    ActionItemViewModel(
                      content = messagesForLanguage.change,
                      href = controllers.sections.dispatch.routes.DispatchWarehouseExciseController.onPageLoad(testErn, testDraftId, CheckMode).url,
                      id = "dispatchWarehouseExcise"
                    ).withVisuallyHiddenText(messagesForLanguage.cyaChangeHidden)
                  )
                )
              )
            }

            "must not render an 'Update Needed' tag against the ERN - when the error has been fixed" in {

              implicit lazy val request = dataRequest(FakeRequest(),
                emptyUserAnswers
                  .copy(submissionFailures = Seq(movementSubmissionFailure.copy(errorType = dispatchWarehouseInvalidOrMissingOnSeedError.errorType, hasBeenFixed = true)))
                  .set(DispatchWarehouseExcisePage, testErn))

              dispatchWarehouseExciseSummary.row() mustBe Some(
                SummaryListRowViewModel(
                  key = messagesForLanguage.cyaLabel,
                  value = Value(HtmlContent(testErn)),
                  actions = Seq(
                    ActionItemViewModel(
                      content = messagesForLanguage.change,
                      href = controllers.sections.dispatch.routes.DispatchWarehouseExciseController.onPageLoad(testErn, testDraftId, CheckMode).url,
                      id = "dispatchWarehouseExcise"
                    ).withVisuallyHiddenText(messagesForLanguage.cyaChangeHidden)
                  )
                )
              )
            }
          }
        }
      }
    }
  }
}
