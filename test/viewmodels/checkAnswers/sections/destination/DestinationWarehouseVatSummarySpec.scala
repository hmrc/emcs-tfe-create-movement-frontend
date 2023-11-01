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
import fixtures.messages.sections.destination.DestinationWarehouseVatMessages
import models.CheckMode
import models.sections.info.movementScenario.MovementScenario.{ExemptedOrganisation, RegisteredConsignee, TemporaryRegisteredConsignee}
import org.scalatest.matchers.must.Matchers
import pages.sections.destination.DestinationWarehouseVatPage
import pages.sections.info.DestinationTypePage
import play.api.i18n.Messages
import play.api.test.FakeRequest
import uk.gov.hmrc.govukfrontend.views.Aliases.Value
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text
import utils.JsonOptionFormatter
import viewmodels.checkAnswers.DestinationWarehouseVatSummary
import viewmodels.govuk.summarylist._
import viewmodels.implicits._

class DestinationWarehouseVatSummarySpec extends SpecBase with Matchers with JsonOptionFormatter {

  "DestinationWarehouseVatSummary" - {

    lazy val app = applicationBuilder().build()

    Seq(DestinationWarehouseVatMessages.English).foreach { messagesForLanguage =>

      s"when being rendered in lang code of '${messagesForLanguage.lang.code}'" - {

        implicit lazy val msgs: Messages = messages(app, messagesForLanguage.lang)

        "when there's no answer" - {


          "DestinationType is not one of the VAT flow destinations" - {

            "must output no row" in {

              implicit lazy val request = dataRequest(FakeRequest(), emptyUserAnswers)

              DestinationWarehouseVatSummary.row() mustBe None
            }
          }
        }

        "when there's an answer" - {

          "must output the expected row when vat page is answered" in {

            implicit lazy val request = dataRequest(FakeRequest(), emptyUserAnswers.set(DestinationWarehouseVatPage, Some("vat")))

            DestinationWarehouseVatSummary.row() mustBe
              Some(
                SummaryListRowViewModel(
                  key = messagesForLanguage.cyaLabel,
                  value = Value(Text("vat")),
                  actions = Seq(
                    ActionItemViewModel(
                      content = messagesForLanguage.change,
                      href = controllers.sections.destination.routes.DestinationWarehouseVatController.onPageLoad(testErn, testDraftId, CheckMode).url,
                      id = "changeDestinationWarehouseVat"
                    ).withVisuallyHiddenText(messagesForLanguage.cyaChangeHidden)
                  )
                )
              )
          }

          "when the answer has been not been provided" - {

            "must output the expected row with Not Provided" in {

              implicit lazy val request = dataRequest(FakeRequest(), emptyUserAnswers.set(DestinationWarehouseVatPage, None))

              DestinationWarehouseVatSummary.row() mustBe
                Some(
                  SummaryListRowViewModel(
                    key = messagesForLanguage.cyaLabel,
                    value = Value(Text(messagesForLanguage.notProvided)),
                    actions = Seq(
                      ActionItemViewModel(
                        content = messagesForLanguage.change,
                        href = controllers.sections.destination.routes.DestinationWarehouseVatController.onPageLoad(testErn, testDraftId, CheckMode).url,
                        id = "changeDestinationWarehouseVat"
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