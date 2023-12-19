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
import fixtures.messages.sections.transportUnit.TransportUnitTypeMessages
import models.CheckMode
import models.sections.transportUnit.TransportUnitType
import org.scalatest.matchers.must.Matchers
import pages.sections.transportUnit.TransportUnitTypePage
import play.api.i18n.Messages
import play.api.test.FakeRequest
import uk.gov.hmrc.govukfrontend.views.Aliases.Value
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text
import viewmodels.govuk.summarylist._
import viewmodels.implicits._

class TransportUnitTypeSummarySpec extends SpecBase with Matchers {

  "TransportUnitTypeSummary" - {

    Seq(TransportUnitTypeMessages.English).foreach { messagesForLanguage =>

      s"when being rendered in lang code of '${messagesForLanguage.lang.code}'" - {

        implicit val msgs: Messages = messages(Seq(messagesForLanguage.lang))

        ".checkYourAnswersRow" - {
          "when there's no answer" - {

            "must output no row" in {

              implicit lazy val request = dataRequest(FakeRequest(), emptyUserAnswers)

              TransportUnitTypeSummary.checkYourAnswersRow(testIndex1) mustBe None
            }
          }

          "when there's an answer" - {

            s"must output the expected row for FixedTransport" in {

              implicit lazy val request = dataRequest(FakeRequest(), emptyUserAnswers.set(TransportUnitTypePage(testIndex1), TransportUnitType.FixedTransport))

              TransportUnitTypeSummary.checkYourAnswersRow(testIndex1) mustBe
                Some(
                  SummaryListRowViewModel(
                    key = messagesForLanguage.cyaLabel,
                    value = Value(Text(messagesForLanguage.addToListValue(TransportUnitType.FixedTransport))),
                    actions = Seq.empty
                  )
                )
            }
          }
        }

        ".row" - {

          "when there's no answer" - {

            "must output no row" in {

              implicit lazy val request = dataRequest(FakeRequest(), emptyUserAnswers)

              TransportUnitTypeSummary.row(testIndex1) mustBe None
            }
          }

          "when there's an answer" - {

            Seq(
              ("FixedTransport", TransportUnitType.FixedTransport),
              ("Container", TransportUnitType.Container),
              ("Tractor", TransportUnitType.Tractor),
              ("Trailer", TransportUnitType.Trailer),
              ("Vehicle", TransportUnitType.Vehicle)
            ).foreach {
              case (name, transportUnitType) =>
                s"must output the expected row for $name" in {

                  implicit lazy val request = dataRequest(FakeRequest(), emptyUserAnswers.set(TransportUnitTypePage(testIndex1), transportUnitType))

                  TransportUnitTypeSummary.row(testIndex1) mustBe
                    Some(
                      SummaryListRowViewModel(
                        key = messagesForLanguage.addToListLabel,
                        value = Value(Text(messagesForLanguage.addToListValue(transportUnitType))),
                        actions = Seq(
                          ActionItemViewModel(
                            content = messagesForLanguage.change,
                            href = controllers.sections.transportUnit.routes.TransportUnitTypeController.onPageLoad(testErn, testDraftId, testIndex1, CheckMode).url,
                            id = "changeTransportUnitType1"
                          ).withVisuallyHiddenText(messagesForLanguage.addToListChangeHidden)
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
}
