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
import pages.sections.transportUnit.{TransportSealChoicePage, TransportSealTypePage}
import play.api.i18n.Messages
import play.api.test.FakeRequest
import uk.gov.hmrc.govukfrontend.views.Aliases.Value
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text
import viewmodels.govuk.summarylist._
import viewmodels.implicits._

class TransportSealTypeSummarySpec extends SpecBase with Matchers with TransportUnitFixtures {

  "TransportSealTypeSummary" - {

    Seq(TransportSealTypeMessages.English).foreach { messagesForLanguage =>

      s"when being rendered in lang code of '${messagesForLanguage.lang.code}'" - {

        implicit val msgs: Messages = messages(Seq(messagesForLanguage.lang))

        "when there's no answer" - {

          "must output no row if TransportSealChoicePage is not answered" in {

            implicit lazy val request = dataRequest(FakeRequest(), emptyUserAnswers)

            TransportSealTypeSummary.row(testIndex1, false) mustBe None
          }

          "must output no row if TransportSealChoicePage is false" in {

            implicit lazy val request = dataRequest(FakeRequest(), emptyUserAnswers
              .set(TransportSealChoicePage(testIndex1), false)
            )

            TransportSealTypeSummary.row(testIndex1, false) mustBe None
          }

          "must output row with answer not provide if TransportSealChoicePage is true" in {

            implicit lazy val request = dataRequest(FakeRequest(), emptyUserAnswers
              .set(TransportSealChoicePage(testIndex1), true)
            )

            TransportSealTypeSummary.row(testIndex1, true) mustBe
              Some(
                SummaryListRowViewModel(
                  key = messagesForLanguage.sealTypeCYA,
                  value = Value(Text(messagesForLanguage.notProvided)),
                  actions = Seq(
                    ActionItemViewModel(
                      content = messagesForLanguage.change,
                      href = controllers.sections.transportUnit.routes.TransportSealTypeController.onPageLoad(testErn, testDraftId, testIndex1, CheckMode).url,
                      id = "changeTransportSealType1"
                    ).withVisuallyHiddenText(messagesForLanguage.sealTypeCyaChangeHidden)
                  )
                )
              )
          }
        }

        "when there's an answer" - {

          "must output no row if TransportSealChoicePage is false" in {

            implicit lazy val request = dataRequest(FakeRequest(), emptyUserAnswers
              .set(TransportSealTypePage(testIndex1), transportSealTypeModelMax)
              .set(TransportSealChoicePage(testIndex1), false)
            )

            TransportSealTypeSummary.row(testIndex1, false) mustBe None
          }

          "must output no row if TransportSealChoicePage is not answered" in {

            implicit lazy val request = dataRequest(FakeRequest(), emptyUserAnswers
              .set(TransportSealTypePage(testIndex1), transportSealTypeModelMax)
            )

            TransportSealTypeSummary.row(testIndex1, false) mustBe None
          }

          s"must output the expected row for TransportSealType and TransportSealChoicePage is true" in {

            implicit lazy val request = dataRequest(FakeRequest(), emptyUserAnswers
              .set(TransportSealTypePage(testIndex1), transportSealTypeModelMax)
              .set(TransportSealChoicePage(testIndex1), true)
            )

            TransportSealTypeSummary.row(testIndex1, true) mustBe
              Some(
                SummaryListRowViewModel(
                  key = messagesForLanguage.sealTypeCYA,
                  value = Value(Text(transportSealTypeModelMax.sealType)),
                  actions = Seq(
                    ActionItemViewModel(
                      content = messagesForLanguage.change,
                      href = controllers.sections.transportUnit.routes.TransportSealTypeController.onPageLoad(testErn, testDraftId, testIndex1, CheckMode).url,
                      id = "changeTransportSealType1"
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
