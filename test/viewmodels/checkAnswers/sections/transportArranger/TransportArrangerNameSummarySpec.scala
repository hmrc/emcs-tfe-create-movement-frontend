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

package viewmodels.checkAnswers.sections.transportArranger

import base.SpecBase
import fixtures.messages.sections.transportArranger.TransportArrangerNameMessages
import models.CheckMode
import models.sections.transportArranger.TransportArranger.{Consignee, Consignor, GoodsOwner, Other}
import org.scalatest.matchers.must.Matchers
import pages.sections.consignee.ConsigneeBusinessNamePage
import pages.sections.transportArranger.{TransportArrangerNamePage, TransportArrangerPage}
import play.api.i18n.Messages
import play.api.test.FakeRequest
import uk.gov.hmrc.govukfrontend.views.Aliases.Value
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text
import viewmodels.govuk.summarylist._
import viewmodels.implicits._

class TransportArrangerNameSummarySpec extends SpecBase with Matchers {

  "TransportArrangerNameSummary" - {

    lazy val app = applicationBuilder().build()

    Seq(TransportArrangerNameMessages.English).foreach { messagesForLanguage =>

      s"when being rendered in lang code of '${messagesForLanguage.lang.code}'" - {

        implicit lazy val msgs: Messages = messages(app, messagesForLanguage.lang)

        "when TransportArranger is GoodsOwner or Other" - {

          "when there's no answer" - {

            "must output the Not Provided" in {

              implicit lazy val request = dataRequest(FakeRequest(), emptyUserAnswers.set(TransportArrangerPage, GoodsOwner))

              TransportArrangerNameSummary.row() mustBe
                SummaryListRowViewModel(
                  key = messagesForLanguage.cyaLabel,
                  value = Value(Text(messagesForLanguage.sectionNotComplete("Goods owner"))),
                  actions = Seq(
                    ActionItemViewModel(
                      content = messagesForLanguage.change,
                      href = controllers.sections.transportArranger.routes.TransportArrangerNameController.onPageLoad(testErn, testDraftId, CheckMode).url,
                      id = "changeTransportArrangerName"
                    ).withVisuallyHiddenText(messagesForLanguage.cyaChangeHidden)
                  )
                )
            }
          }

          "when there's an answer" - {

            "must output the expected row" in {

              implicit lazy val request = dataRequest(FakeRequest(), emptyUserAnswers
                .set(TransportArrangerPage, Other)
                .set(TransportArrangerNamePage, "Jeff")
              )

              TransportArrangerNameSummary.row() mustBe
                SummaryListRowViewModel(
                  key = messagesForLanguage.cyaLabel,
                  value = Value(Text("Jeff")),
                  actions = Seq(
                    ActionItemViewModel(
                      content = messagesForLanguage.change,
                      href = controllers.sections.transportArranger.routes.TransportArrangerNameController.onPageLoad(testErn, testDraftId, CheckMode).url,
                      id = "changeTransportArrangerName"
                    ).withVisuallyHiddenText(messagesForLanguage.cyaChangeHidden)
                  )
                )
            }
          }
        }

        "when TransportArranger is Consignee" - {

          "when there's no answer for the ConsigneeBusinessNamePage" - {

            "must output the expected data" in {

              implicit lazy val request = dataRequest(FakeRequest(), emptyUserAnswers.set(TransportArrangerPage, Consignee))

              TransportArrangerNameSummary.row() mustBe
                SummaryListRowViewModel(
                  key = messagesForLanguage.cyaLabel,
                  value = Value(Text(messagesForLanguage.sectionNotComplete("Consignee"))),
                  actions = Seq()
                )
            }
          }

          "when there's an answer for the ConsigneeBusinessNamePage" - {

            "must output the expected row" in {

              implicit lazy val request = dataRequest(FakeRequest(), emptyUserAnswers
                .set(TransportArrangerPage, Consignee)
                .set(ConsigneeBusinessNamePage, "Jeff")
              )

              TransportArrangerNameSummary.row() mustBe
                SummaryListRowViewModel(
                  key = messagesForLanguage.cyaLabel,
                  value = Value(Text("Jeff")),
                  actions = Seq()
                )
            }
          }
        }

        "transportArrangerNameValue" - {
          s"when TransportArranger is Some($Consignor)" - {
            "must return the user's trader known facts name" in {
              implicit lazy val request = dataRequest(FakeRequest(), emptyUserAnswers)

              TransportArrangerNameSummary.transportArrangerNameValue(Some(Consignor)) mustBe testMinTraderKnownFacts.traderName
            }
          }
          Seq(
            (Some(Consignee), ConsigneeBusinessNamePage, messagesForLanguage.sectionNotComplete("Consignee")),
            (Some(GoodsOwner), TransportArrangerNamePage, messagesForLanguage.sectionNotComplete("Goods owner")),
            (Some(Other), TransportArrangerNamePage, messagesForLanguage.sectionNotComplete("Other")),
            (None, TransportArrangerNamePage, messagesForLanguage.notProvided)
          ).foreach {
            case (transportArranger, page, notProvidedValue) =>
              s"when TransportArranger is $transportArranger" - {
                s"and $page has a value" - {
                  s"must return the $transportArranger name" in {
                    implicit lazy val request = dataRequest(FakeRequest(), emptyUserAnswers.set(page, "Jeff"))

                    TransportArrangerNameSummary.transportArrangerNameValue(transportArranger) mustBe "Jeff"
                  }
                }
                s"and $page has no value" - {
                  "must return the default text" in {
                    implicit lazy val request = dataRequest(FakeRequest(), emptyUserAnswers)

                    TransportArrangerNameSummary.transportArrangerNameValue(transportArranger) mustBe notProvidedValue
                  }
                }
              }
          }
        }
      }
    }
  }
}