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
import models.NormalMode
import org.scalatest.matchers.must.Matchers
import pages.sections.dispatch.DispatchUseConsignorDetailsPage
import play.api.i18n.Messages
import play.api.test.FakeRequest
import viewmodels.govuk.summarylist._
import viewmodels.implicits._

class DispatchUseConsignorDetailsSummarySpec extends SpecBase with Matchers with UserAddressFixtures {

  "DispatchUseConsignorDetailsSummary" - {

    lazy val app = applicationBuilder().build()

    Seq(DispatchUseConsignorDetailsMessages.English).foreach { messagesForLanguage =>

      s"when being rendered in lang code of '${messagesForLanguage.lang.code}'" - {

        implicit lazy val msgs: Messages = messages(app, messagesForLanguage.lang)

        "when there's no answer" - {

          "must output no row" in {

            implicit lazy val request = dataRequest(FakeRequest(), emptyUserAnswers)

            DispatchUseConsignorDetailsSummary.row() mustBe None
          }
        }

        "when there's an answer" - {

          s"must output the expected row for DispatchUseConsignorDetailsSummary" in {

            implicit lazy val request = dataRequest(FakeRequest(), emptyUserAnswers.set(DispatchUseConsignorDetailsPage, true))

            DispatchUseConsignorDetailsSummary.row() mustBe
              Some(
                SummaryListRowViewModel(
                  key = messagesForLanguage.checkAnswersLabel,
                  value = ValueViewModel(messagesForLanguage.yes),
                  actions = Seq(
                    ActionItemViewModel(
                      content = messagesForLanguage.change,
                      href = controllers.sections.dispatch.routes.DispatchUseConsignorDetailsController.onPageLoad(testErn, testDraftId, NormalMode).url,
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