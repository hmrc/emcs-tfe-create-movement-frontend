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

package viewmodels.checkAnswers.sections.transportUnit

import base.SpecBase
import fixtures.TransportUnitFixtures
import fixtures.messages.sections.transportUnit.TransportSealTypeMessages
import models.CheckMode
import org.scalatest.matchers.must.Matchers
import pages.sections.transportUnit.TransportSealTypePage
import play.api.i18n.Messages
import play.api.test.FakeRequest
import uk.gov.hmrc.govukfrontend.views.Aliases.Value
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text
import viewmodels.govuk.summarylist._
import viewmodels.implicits._

class TransportSealTypeSummarySpec extends SpecBase with Matchers with TransportUnitFixtures {

  "TransportSealTypeSummary" - {

    lazy val app = applicationBuilder().build()

    Seq(TransportSealTypeMessages.English, TransportSealTypeMessages.Welsh).foreach { messagesForLanguage =>

      s"when being rendered in lang code of '${messagesForLanguage.lang.code}'" - {

        implicit lazy val msgs: Messages = messages(app, messagesForLanguage.lang)

        "when there's no answer" - {

          "must output no row" in {

            implicit lazy val request = dataRequest(FakeRequest(), emptyUserAnswers)

            TransportSealTypeSummary.row(testIndex1, request.userAnswers) mustBe None
          }
        }

        "when there's an answer" - {

          s"must output the expected row for TransportSealType" in {

            implicit lazy val request = dataRequest(FakeRequest(), emptyUserAnswers.set(TransportSealTypePage(testIndex1), transportSealTypeModelMax))

            TransportSealTypeSummary.row(testIndex1, request.userAnswers) mustBe
              Some(
                SummaryListRowViewModel(
                  key = messagesForLanguage.sealTypeCYA,
                  value = Value(Text(transportSealTypeModelMax.sealType)),
                  actions = Seq(
                    ActionItemViewModel(
                      content = messagesForLanguage.change,
                      href = controllers.sections.transportUnit.routes.TransportSealTypeController.onPageLoad(testErn, testDraftId, testIndex1, CheckMode).url,
                      id = "changeTransportSealType"
                    ).withVisuallyHiddenText(messagesForLanguage.sealTypeCyaChangeHidden)
                  )
                )
              )
          }
        }
      }
    }
  }
}