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
import fixtures.MovementSubmissionFailureFixtures
import fixtures.messages.sections.info.DispatchDetailsMessages
import fixtures.messages.sections.info.DispatchDetailsMessages.ViewMessages
import models.CheckMode
import models.sections.info.DispatchDetailsModel
import pages.sections.info.DispatchDetailsPage
import play.api.i18n.Messages
import play.api.test.FakeRequest
import play.twirl.api.{Html, HtmlFormat}
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.{HtmlContent, Text}
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.{ActionItem, Key, SummaryListRow}
import viewmodels.govuk.summarylist._
import viewmodels.helpers.TagHelper

import java.time.{LocalDate, LocalTime}
import views.html.components

class InformationDispatchDateSummarySpec extends SpecBase with MovementSubmissionFailureFixtures {

  val informationDateOfDispatchSummary = app.injector.instanceOf[InformationDateOfDispatchSummary]
  val link = app.injector.instanceOf[components.link]
  val tagHelper = app.injector.instanceOf[TagHelper]

  private def expectedRow(value: Option[String],
                          withErrorTag: Boolean,
                          isPreDraft: Boolean)(implicit messagesForLanguage: ViewMessages, messages: Messages): Option[SummaryListRow] = {

    val changeLink = if (isPreDraft) {
      controllers.sections.info.routes.DispatchDetailsController.onPreDraftPageLoad(testErn, CheckMode)
    } else {
      controllers.sections.info.routes.DispatchDetailsController.onPageLoad(testErn, testDraftId, CheckMode)
    }

    value match {
      case Some(value) =>
        Some(SummaryListRowViewModel(
          key = Key(Text(messagesForLanguage.cyaDispatchDateLabel)),
          value = ValueViewModel(HtmlContent(HtmlFormat.fill(Seq(
            Some(Html(value)),
            if (withErrorTag) Some(tagHelper.updateNeededTag(withNoFloat = false)) else None
          ).flatten))),
          actions = Seq(ActionItemViewModel(
            content = Text(messagesForLanguage.change),
            href = changeLink.url,
            id = "changeDateOfDispatch"
          ).withVisuallyHiddenText(messagesForLanguage.cyaChangeDispatchDateHidden))
        ))
      case None => Some(SummaryListRowViewModel(
        key = Key(Text(messagesForLanguage.cyaDispatchDateLabel)),
        value = ValueViewModel(HtmlContent(link(
          link = changeLink.url,
          messageKey = messagesForLanguage.addDate,
          id = Some("changeDateOfDispatch")
        ))),
        actions = if (isPreDraft) Seq() else Seq(ActionItem(
          content = HtmlContent(tagHelper.incompleteTag(withNoFloat = true)),
          href = changeLink.url,
          visuallyHiddenText = Some(messagesForLanguage.addDate),
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

        "when the user is on the pre-draft journey" - {

          "then must return not provided row" in {
            implicit lazy val request = dataRequest(FakeRequest(), emptyUserAnswers)

            informationDateOfDispatchSummary.row mustBe expectedRow(
              value = None,
              withErrorTag = false,
              isPreDraft = true
            )
          }
        }

        "when the user is NOT on the pre-draft journey" - {

          "then must return not provided row" in {
            implicit lazy val request = dataRequest(FakeRequest(), emptyUserAnswers.copy(createdFromTemplateId = Some(templateId)))

            informationDateOfDispatchSummary.row mustBe expectedRow(
              value = None,
              withErrorTag = false,
              isPreDraft = false
            )
          }
        }
      }

      "and there is a DispatchDetailsPage answer " - {

        "when viewing in pre-draft" - {

          "then must return a row with the answer" in {
            val model = DispatchDetailsModel(LocalDate.of(2023, 6, 7), LocalTime.of(7, 25))

            implicit lazy val request = dataRequest(FakeRequest(), emptyUserAnswers.set(DispatchDetailsPage(), model))

            informationDateOfDispatchSummary.row mustBe expectedRow(
              value = Some("7 June 2023"),
              withErrorTag = false,
              isPreDraft = true
            )
          }
        }

        "when viewing NOT on pre-draft flow" - {

          "then must return a row with the answer" in {
            val model = DispatchDetailsModel(LocalDate.of(2023, 6, 7), LocalTime.of(7, 25))

            implicit lazy val request = dataRequest(FakeRequest(), emptyUserAnswers
              .copy(createdFromTemplateId = Some(templateId))
              .set(DispatchDetailsPage(), model)
            )

            informationDateOfDispatchSummary.row mustBe expectedRow(
              value = Some("7 June 2023"),
              withErrorTag = false,
              isPreDraft = false
            )
          }
        }
      }

      "and there is a DispatchDetailsPage answer which needs fixing due to an error" - {

        "then must return a row with the answer" in {
          val model = DispatchDetailsModel(LocalDate.of(2023, 6, 7), LocalTime.of(7, 25))

          implicit lazy val request = dataRequest(FakeRequest(), emptyUserAnswers.set(DispatchDetailsPage(), model).copy(
            submissionFailures = Seq(dispatchDateInPastValidationError()),
            createdFromTemplateId = Some(templateId)
          ))

          informationDateOfDispatchSummary.row mustBe expectedRow(
            value = Some("7 June 2023"),
            withErrorTag = true,
            isPreDraft = false
          )
        }
      }
    }
  }
}
