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

package viewmodels.checkAnswers.sections.info

import base.SpecBase
import fixtures.messages.sections.info.DispatchDetailsMessages
import fixtures.messages.sections.info.DispatchDetailsMessages.ViewMessages
import models.CheckMode
import models.requests.DataRequest
import models.sections.info.DispatchDetailsModel
import pages.sections.info.DispatchDetailsPage
import play.api.i18n.Messages
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.{HtmlContent, Text}
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.{ActionItem, Key, SummaryListRow, Value}
import viewmodels.govuk.summarylist._
import viewmodels.helpers.TagHelper
import views.html.components
import views.html.components.link

import java.time.{LocalDate, LocalTime}


class InformationDispatchTimeSummarySpec extends SpecBase {

  lazy val informationTimeOfDispatchSummary: InformationTimeOfDispatchSummary = app.injector.instanceOf[InformationTimeOfDispatchSummary]
  val link: link = app.injector.instanceOf[components.link]
  val tagHelper: TagHelper = app.injector.instanceOf[TagHelper]

  private def expectedRow(value: Option[String],
                          isPreDraft: Boolean)(implicit messagesForLanguage: ViewMessages, messages: Messages): Option[SummaryListRow] = {

    val changeLink = if (isPreDraft) {
      controllers.sections.info.routes.DispatchDetailsController.onPreDraftPageLoad(testErn, CheckMode)
    } else {
      controllers.sections.info.routes.DispatchDetailsController.onPageLoad(testErn, testDraftId, CheckMode)
    }

    value match {
      case Some(value) =>
        Some(SummaryListRowViewModel(
          key = Key(Text(messagesForLanguage.cyaDispatchTimeLabel)),
          value = Value(HtmlContent(value)),
          actions = Seq(ActionItemViewModel(
            content = Text(messagesForLanguage.change),
            href = changeLink.url,
            id = "changeTimeOfDispatch"
          ).withVisuallyHiddenText(messagesForLanguage.cyaChangeDispatchTimeHidden))
        ))
      case None => Some(SummaryListRowViewModel(
        key = Key(Text(messagesForLanguage.cyaDispatchTimeLabel)),
        value = ValueViewModel(HtmlContent(link(
          link = changeLink.url,
          messageKey = messagesForLanguage.addTime,
          id = Some("changeTimeOfDispatch")
        ))),
        actions = if (isPreDraft) Seq() else Seq(ActionItem(
          content = HtmlContent(tagHelper.incompleteTag(withNoFloat = true)),
          href = changeLink.url,
          visuallyHiddenText = Some(messagesForLanguage.addTime),
          classes = "cursor-default",
          attributes = Map("tabindex" -> "-1")
        ))
      ))
    }
  }

  Seq(DispatchDetailsMessages.English).foreach { implicit messagesForLanguage =>

    s"when language is set to ${messagesForLanguage.lang.code}" - {

      implicit val msgs: Messages = messages(Seq(messagesForLanguage.lang))

      "and there is no answer for the DispatchDetailsPage" - {
        "when on pre-draft flow" - {
          "then must return add data row, with no incomplete tag" in {
            implicit lazy val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(), emptyUserAnswers)

            informationTimeOfDispatchSummary.row() mustBe expectedRow(
              value = None,
              isPreDraft = true
            )
          }
        }

        "when on pre-draft flow" - {
          "then must return add data row, with incomplete tag" in {
            implicit lazy val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(), emptyUserAnswers.copy(createdFromTemplateId = Some(templateId)))

            informationTimeOfDispatchSummary.row() mustBe expectedRow(
              value = None,
              isPreDraft = false
            )
          }
        }
      }

      "and there is a DispatchDetailsPage answer " - {
        "when on pre-draft flow" - {
          "then must return a row with the answer" in {
            val model = DispatchDetailsModel(LocalDate.of(2023, 6, 7), LocalTime.of(7, 25))

            implicit lazy val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(), emptyUserAnswers.set(DispatchDetailsPage(), model))

            informationTimeOfDispatchSummary.row() mustBe expectedRow(
              value = Some("07:25"),
              isPreDraft = true
            )
          }
        }

        "when NOT on pre-draft flow" - {
          "then must return a row with the answer" in {
            val model = DispatchDetailsModel(LocalDate.of(2023, 6, 7), LocalTime.of(7, 25))

            implicit lazy val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(), emptyUserAnswers
              .copy(createdFromTemplateId = Some(templateId))
              .set(DispatchDetailsPage(), model)
            )

            informationTimeOfDispatchSummary.row() mustBe expectedRow(
              value = Some("07:25"),
              isPreDraft = false
            )
          }
        }
      }

    }
  }

}
