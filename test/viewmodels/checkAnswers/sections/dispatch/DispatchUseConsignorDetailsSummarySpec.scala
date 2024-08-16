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
import fixtures.UserAddressFixtures
import fixtures.messages.sections.dispatch.DispatchUseConsignorDetailsMessages
import models.CheckMode
import org.scalatest.matchers.must.Matchers
import pages.sections.consignor.ConsignorAddressPage
import pages.sections.dispatch.DispatchUseConsignorDetailsPage
import play.api.i18n.Messages
import play.api.test.FakeRequest
import viewmodels.govuk.summarylist._
import viewmodels.implicits._

class DispatchUseConsignorDetailsSummarySpec extends SpecBase with Matchers with UserAddressFixtures {

  "DispatchUseConsignorDetailsSummary" - {

    Seq(DispatchUseConsignorDetailsMessages.English).foreach { messagesForLanguage =>

      s"when being rendered in lang code of '${messagesForLanguage.lang.code}'" - {

        implicit val msgs: Messages = messages(Seq(messagesForLanguage.lang))

        "when there's no answer to ConsignorAddressPage" - {

          "must output no row" in {

            implicit lazy val request = dataRequest(FakeRequest(), emptyUserAnswers.set(DispatchUseConsignorDetailsPage, true))

            DispatchUseConsignorDetailsSummary.row() mustBe None
          }
        }

        "when there's an answer to ConsignorAddressPage" - {

          s"must output the expected row for DispatchUseConsignorDetailsSummary when present" in {

            Map(
              true -> messagesForLanguage.yes,
              false -> messagesForLanguage.no
            ).foreach {
              case (answer, expectedValue) =>
                withClue(s"For answer: $answer\n") {

                  implicit lazy val request = dataRequest(
                    FakeRequest(),
                    emptyUserAnswers.set(ConsignorAddressPage, testUserAddress).set(DispatchUseConsignorDetailsPage, answer)
                  )

                  DispatchUseConsignorDetailsSummary.row() mustBe
                    Some(
                      SummaryListRowViewModel(
                        key = messagesForLanguage.checkAnswersLabel,
                        value = ValueViewModel(expectedValue),
                        actions = Seq(
                          ActionItemViewModel(
                            content = messagesForLanguage.change,
                            href = controllers.sections.dispatch.routes.DispatchUseConsignorDetailsController.onPageLoad(testErn, testDraftId, CheckMode).url,
                            id = "changeDispatchUseConsignorDetails"
                          ).withVisuallyHiddenText(messagesForLanguage.checkAnswersHiddenChangeLink)
                        )
                      )
                    )
                }
            }

          }

          "must output the default row for DispatchUseConsignorDetailsSummary when answer is missing" in {
            implicit lazy val request = dataRequest(
              FakeRequest(),
              emptyUserAnswers.set(ConsignorAddressPage, testUserAddress)
            )

            DispatchUseConsignorDetailsSummary.row() mustBe
              Some(
                SummaryListRowViewModel(
                  key = messagesForLanguage.checkAnswersLabel,
                  value = ValueViewModel(messagesForLanguage.no),
                  actions = Seq(
                    ActionItemViewModel(
                      content = messagesForLanguage.change,
                      href = controllers.sections.dispatch.routes.DispatchUseConsignorDetailsController.onPageLoad(testErn, testDraftId, CheckMode).url,
                      id = "changeDispatchUseConsignorDetails"
                    ).withVisuallyHiddenText(messagesForLanguage.checkAnswersHiddenChangeLink)
                  )
                )
              )
          }
        }
      }
    }
  }
}
