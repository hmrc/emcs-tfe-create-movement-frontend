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
import fixtures.messages.sections.info.InvoiceDetailsMessages
import fixtures.messages.sections.info.InvoiceDetailsMessages.ViewMessages
import models.CheckMode
import models.sections.info.InvoiceDetailsModel
import pages.sections.info.InvoiceDetailsPage
import play.api.i18n.Messages
import play.api.test.FakeRequest
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.{HtmlContent, Text}
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.{ActionItem, Key, SummaryListRow, Value}
import viewmodels.govuk.summarylist._
import viewmodels.helpers.TagHelper
import views.html.components

import java.time.LocalDate

class InformationInvoiceReferenceSummarySpec extends SpecBase {

  lazy val link = app.injector.instanceOf[components.link]
  lazy val tagHelper = app.injector.instanceOf[TagHelper]
  lazy val informationInvoiceReferenceSummary = app.injector.instanceOf[InformationInvoiceReferenceSummary]

  private def expectedRow(value: Option[String],
                          isPreDraft: Boolean)(implicit messagesForLanguage: ViewMessages, messages: Messages): Option[SummaryListRow] = {

    val changeLink = if (isPreDraft) {
      controllers.sections.info.routes.InvoiceDetailsController.onPreDraftPageLoad(testErn, CheckMode)
    } else {
      controllers.sections.info.routes.InvoiceDetailsController.onPageLoad(testErn, testDraftId, CheckMode)
    }

    value match {
      case Some(value) =>
        Some(SummaryListRowViewModel(
          key = Key(Text(messagesForLanguage.cyaInvoiceReferenceLabel)),
          value = Value(HtmlContent(value)),
          actions = Seq(ActionItemViewModel(
            content = Text(messagesForLanguage.change),
            href = changeLink.url,
            id = "changeInvoiceReference"
          ).withVisuallyHiddenText(messagesForLanguage.cyaChangeInvoiceReferenceHidden))
        ))
      case None => Some(SummaryListRowViewModel(
        key = Key(Text(messagesForLanguage.cyaInvoiceReferenceLabel)),
        value = ValueViewModel(HtmlContent(link(
          link = changeLink.url,
          messageKey = messagesForLanguage.addReference,
          id = Some("changeInvoiceReference")
        ))),
        actions = if (isPreDraft) Seq() else Seq(ActionItem(
          content = HtmlContent(tagHelper.incompleteTag(withNoFloat = true)),
          href = changeLink.url,
          visuallyHiddenText = Some(messagesForLanguage.addReference),
          classes = "cursor-default",
          attributes = Map("tabindex" -> "-1")
        ))
      ))
    }
  }

  Seq(InvoiceDetailsMessages.English).foreach { implicit messagesForLanguage =>

    s"when language is set to ${messagesForLanguage.lang.code}" - {

      implicit val msgs: Messages = messages(Seq(messagesForLanguage.lang))

      "and there is no answer for the InvoiceDetailsPage" - {

        "when on pre-draft flow" - {
          "then must return Add details row with NO incomplete tag" in {
            implicit lazy val request = dataRequest(FakeRequest(), emptyUserAnswers)

            informationInvoiceReferenceSummary.row mustBe expectedRow(
              value = None,
              isPreDraft = true
            )
          }
        }

        "when NOT on pre-draft flow" - {
          "then must return Add details row with incomplete tag" in {
            implicit lazy val request = dataRequest(FakeRequest(), emptyUserAnswers.copy(createdFromTemplateId = Some(templateId)))

            informationInvoiceReferenceSummary.row mustBe expectedRow(
              value = None,
              isPreDraft = false
            )
          }
        }
      }

      "and there is a InvoiceDetailsPage answer " - {
        "when on pre-draft flow" - {
          "then must return a row with the answer" in {
            val model = InvoiceDetailsModel("inv reference", LocalDate.now)

            implicit lazy val request = dataRequest(FakeRequest(), emptyUserAnswers.set(InvoiceDetailsPage(), model))

            informationInvoiceReferenceSummary.row mustBe expectedRow(
              value = Some("inv reference"),
              isPreDraft = true
            )
          }
        }
        "when NOT on pre-draft flow" - {
          "then must return a row with the answer" in {
            val model = InvoiceDetailsModel("inv reference", LocalDate.now)

            implicit lazy val request = dataRequest(FakeRequest(), emptyUserAnswers
              .copy(createdFromTemplateId = Some(templateId))
              .set(InvoiceDetailsPage(), model)
            )

            informationInvoiceReferenceSummary.row mustBe expectedRow(
              value = Some("inv reference"),
              isPreDraft = false
            )
          }
        }
      }

    }
  }

}
