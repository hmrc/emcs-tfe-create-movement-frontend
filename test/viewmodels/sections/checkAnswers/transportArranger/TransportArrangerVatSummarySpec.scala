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

package viewmodels.sections.checkAnswers.transportArranger

import base.SpecBase
import fixtures.messages.sections.transportArranger.TransportArrangerVatMessages
import models.CheckMode
import models.sections.transportArranger.TransportArranger.{Consignor, GoodsOwner, Other}
import org.scalatest.matchers.must.Matchers
import pages.sections.transportArranger.{TransportArrangerPage, TransportArrangerVatPage}
import play.api.i18n.Messages
import play.api.test.FakeRequest
import uk.gov.hmrc.govukfrontend.views.Aliases.Value
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text
import viewmodels.checkAnswers.sections.transportArranger.TransportArrangerVatSummary
import viewmodels.govuk.summarylist._
import viewmodels.implicits._

class TransportArrangerVatSummarySpec extends SpecBase with Matchers {
  "TransportArrangerVatSummary" - {

    lazy val app = applicationBuilder().build()

    Seq(TransportArrangerVatMessages.English, TransportArrangerVatMessages.Welsh).foreach { messagesForLanguage =>

      s"when being rendered in lang code of '${messagesForLanguage.lang.code}'" - {

        implicit lazy val msgs: Messages = messages(app, messagesForLanguage.lang)

        "when TransportArranger is NOT GoodsOwner or Other" - {

          "must output None" in {

            implicit lazy val request = dataRequest(FakeRequest(), emptyUserAnswers.set(TransportArrangerPage, Consignor))

            TransportArrangerVatSummary.row(showActionLinks = true) mustBe None
          }
        }

        "when TransportArranger is GoodsOwner or Other" - {

          "when there's no answer" - {

            "must output the expected data" in {

              implicit lazy val request = dataRequest(FakeRequest(), emptyUserAnswers.set(TransportArrangerPage, GoodsOwner))

              TransportArrangerVatSummary.row(showActionLinks = true) mustBe None
            }
          }

          "when there's an answer" - {

            "when the show action link boolean is true" - {

              "must output the expected row" in {

                implicit lazy val request = dataRequest(FakeRequest(), emptyUserAnswers
                  .set(TransportArrangerPage, Other)
                  .set(TransportArrangerVatPage, testVatNumber)
                )

                TransportArrangerVatSummary.row(showActionLinks = true) mustBe
                  Some(
                    SummaryListRowViewModel(
                      key = messagesForLanguage.cyaLabel,
                      value = Value(Text(testVatNumber)),
                      actions = Seq(
                        ActionItemViewModel(
                          content = messagesForLanguage.change,
                          href = controllers.sections.transportArranger.routes.TransportArrangerVatController.onPageLoad(testErn, testLrn, CheckMode).url,
                          id = "changeTransportArrangerVat"
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
}