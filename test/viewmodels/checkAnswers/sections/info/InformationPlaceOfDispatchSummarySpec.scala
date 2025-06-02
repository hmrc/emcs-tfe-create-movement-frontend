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

package viewmodels.checkAnswers.sections.info

import base.SpecBase
import fixtures.messages.sections.info.DispatchPlaceMessages
import fixtures.messages.sections.info.DispatchPlaceMessages.ViewMessages
import models.CheckMode
import models.requests.DataRequest
import models.sections.info.DispatchPlace.{GreatBritain, NorthernIreland}
import pages.sections.info.{DispatchPlacePage, InformationCheckAnswersPage}
import play.api.i18n.Messages
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.{Key, SummaryListRow, Value}
import viewmodels.govuk.summarylist._

class InformationPlaceOfDispatchSummarySpec extends SpecBase {

  private def expectedRow(value: String, withChangeLinks: Boolean = true)
                         (implicit messagesForLanguage: ViewMessages): Option[SummaryListRow] = {
    Some(
      SummaryListRowViewModel(
        key = Key(Text(messagesForLanguage.cyaLabel)),
        value = Value(Text(value)),
        actions = if(withChangeLinks) {
          Seq(ActionItemViewModel(
            content = Text(messagesForLanguage.change),
            href = controllers.sections.info.routes.DispatchPlaceController.onPreDraftPageLoad(testErn, CheckMode).url,
            id = "changeDispatchPlace"
          ).withVisuallyHiddenText(messagesForLanguage.cyaChangeHidden))
        } else {
          Seq()
        }
      )
    )
  }

  Seq(DispatchPlaceMessages.English).foreach { implicit messagesForLanguage =>

    s"when language is set to ${messagesForLanguage.lang.code}" - {

      implicit val msgs: Messages = messages(Seq(messagesForLanguage.lang))

      "and there is no answer for the DispatchPlacePage" - {
        "then must return no row" in {
          implicit lazy val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(), emptyUserAnswers)

          InformationPlaceOfDispatchSummary.row() mustBe None
        }
      }

      "and there is a DispatchPlacePage answer" - {

        Seq(
          GreatBritain -> messagesForLanguage.greatBritainRadioOption,
          NorthernIreland -> messagesForLanguage.northernIrelandRadioOption
        ).foreach { answerAndExpectedValue =>

          s"and the answer is ${answerAndExpectedValue._1}" in {

            implicit lazy val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(), emptyUserAnswers.set(DispatchPlacePage, answerAndExpectedValue._1))

            InformationPlaceOfDispatchSummary.row() mustBe expectedRow(value = answerAndExpectedValue._2)
          }
        }

        "when the user is on the pre-draft flow" - {

          "show the change links" in {

            implicit lazy val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(), emptyUserAnswers
              .set(DispatchPlacePage, GreatBritain)
            )

            InformationPlaceOfDispatchSummary.row() mustBe expectedRow(value = messagesForLanguage.greatBritainRadioOption)
          }
        }

        "when the user is not on the pre-draft flow" - {

          "do NOT show the change links" in {

            implicit lazy val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(), emptyUserAnswers
              .set(DispatchPlacePage, GreatBritain)
              .set(InformationCheckAnswersPage, true)
            )

            InformationPlaceOfDispatchSummary.row() mustBe expectedRow(value = messagesForLanguage.greatBritainRadioOption, withChangeLinks = false)
          }
        }
      }

    }
  }
}
