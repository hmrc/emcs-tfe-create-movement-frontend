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

package viewmodels.helpers

import base.SpecBase
import controllers.sections.documents.routes
import fixtures.DocumentTypeFixtures
import fixtures.messages.sections.documents.DocumentsAddToListMessages
import models.requests.DataRequest
import models.{CheckMode, NormalMode, UserAnswers}
import pages.sections.documents.{DocumentReferencePage, DocumentTypePage}
import play.api.i18n.Messages
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.{Empty, HtmlContent, Text}
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist._
import viewmodels.checkAnswers.sections.documents.{DocumentReferenceSummary, DocumentTypeSummary}
import viewmodels.govuk.all.{ActionItemViewModel, CardViewModel, SummaryListRowViewModel}
import views.html.components.{link, span, tag}

class DocumentsAddToListHelperSpec extends SpecBase with DocumentTypeFixtures {

  implicit lazy val link: link = app.injector.instanceOf[link]
  implicit lazy val span: span = app.injector.instanceOf[span]
  implicit lazy val tag: tag = app.injector.instanceOf[tag]

  class Setup(userAnswers: UserAnswers = emptyUserAnswers) {
    implicit lazy val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(), userAnswers)

    lazy val helper: DocumentsAddToListHelper = app.injector.instanceOf[DocumentsAddToListHelper]
  }

  "DocumentsAddToListHelper" - {

    Seq(DocumentsAddToListMessages.English).foreach { messagesForLanguage =>

      implicit lazy val msgs: Messages = messages(Seq(messagesForLanguage.lang))

      s"when rendered for language of '${messagesForLanguage.lang.code}'" - {

        ".allDocumentsSummary()" - {

          "return nothing" - {

            s"when no answers specified" in new Setup() {

              helper.allDocumentsSummary() mustBe Nil
            }
          }

          "return required rows when all answers filled out" - {

            s"when the row is Complete" in new Setup(emptyUserAnswers
              .set(DocumentTypePage(0), documentTypeModel)
              .set(DocumentReferencePage(0), "reference")
            ) {

              helper.allDocumentsSummary() mustBe Seq(
                SummaryList(
                  card = Some(Card(
                    title = Some(CardTitle(HtmlContent(span(messagesForLanguage.documentCardTitle(0))))),
                    actions = Some(Actions(items = Seq(
                      ActionItem(
                        href = routes.DocumentsRemoveFromListController.onPageLoad(testErn, testDraftId, 0).url,
                        content = Text(messagesForLanguage.remove),
                        visuallyHiddenText = Some(messagesForLanguage.documentCardTitle(0)),
                        attributes = Map("id" -> "removeDocuments-1")
                      )
                    )))
                  )),
                  rows = Seq(
                    DocumentTypeSummary.row(0).get,
                    DocumentReferenceSummary.row(0).get
                  )
                )
              )
            }


            s"when all answers entered and there is both a Completed and an InProgress row" in new Setup(emptyUserAnswers
              .set(DocumentTypePage(0), documentTypeModel)
              .set(DocumentReferencePage(0), "reference")
              .set(DocumentTypePage(1), documentTypeModel)
            ) {

              helper.allDocumentsSummary() mustBe Seq(
                SummaryList(
                  card = Some(Card(
                    title = Some(CardTitle(HtmlContent(span(messagesForLanguage.documentCardTitle(0))))),
                    actions = Some(Actions(items = Seq(
                      ActionItem(
                        href = routes.DocumentsRemoveFromListController.onPageLoad(testErn, testDraftId, 0).url,
                        content = Text(messagesForLanguage.remove),
                        visuallyHiddenText = Some(messagesForLanguage.documentCardTitle(0)),
                        attributes = Map("id" -> "removeDocuments-1")
                      )
                    )))
                  )),
                  rows = Seq(
                    DocumentTypeSummary.row(0).get,
                    DocumentReferenceSummary.row(0).get
                  )
                ),
                SummaryList(
                  card = Some(Card(
                    title = Some(CardTitle(HtmlContent(HtmlFormat.fill(Seq(
                      span(messagesForLanguage.documentCardTitle(1), Some("govuk-!-margin-right-2")),
                      tag(messagesForLanguage.incomplete, "red")
                    ))))),
                    actions = Some(Actions(items = Seq(
                      ActionItem(
                        href = routes.DocumentTypeController.onPageLoad(testErn, testDraftId, 1, NormalMode).url,
                        content = Text(messagesForLanguage.continueEditing),
                        visuallyHiddenText = Some(messagesForLanguage.documentCardTitle(1)),
                        attributes = Map("id" -> "editDocuments-2")
                      ),
                      ActionItem(
                        href = routes.DocumentsRemoveFromListController.onPageLoad(testErn, testDraftId, 1).url,
                        content = Text(messagesForLanguage.remove),
                        visuallyHiddenText = Some(messagesForLanguage.documentCardTitle(1)),
                        attributes = Map("id" -> "removeDocuments-2")
                      )
                    )))
                  )),
                  rows = Seq(
                    DocumentTypeSummary.row(1).get
                  )
                )
              )
            }
          }
        }

        ".finalCyaSummary()" - {

          "return No Documents" - {

            s"when no documents added" in new Setup() {
              helper.finalCyaSummary() mustBe SummaryList(
                card = Some(CardViewModel(
                  title = messagesForLanguage.finalCyaCardTitle,
                  actions = Some(Actions(items = Seq(
                    ActionItemViewModel(
                      href = routes.DocumentsCertificatesController.onPageLoad(testErn, testDraftId, CheckMode).url,
                      content = Text(messagesForLanguage.change),
                      id = "changeDocuments"
                    )
                  ))),
                  headingLevel = 2
                )),
                rows = Seq(SummaryListRowViewModel(
                  key = Key(Text(messagesForLanguage.finalCyaNoDocuments)),
                  value = Value(Empty)
                ))
              )
            }
          }

          "return required rows when all answers filled out" - {

            s"when single document has been added" in new Setup(emptyUserAnswers
              .set(DocumentTypePage(testIndex1), documentTypeModel)
              .set(DocumentReferencePage(testIndex1), "reference")
            ) {

              helper.finalCyaSummary() mustBe
                SummaryList(
                  card = Some(CardViewModel(
                    title = messagesForLanguage.finalCyaCardTitle,
                    actions = Some(Actions(items = Seq(
                      ActionItemViewModel(
                        href = routes.DocumentsAddToListController.onPageLoad(testErn, testDraftId).url,
                        content = Text(messagesForLanguage.change),
                        id = "changeDocuments"
                      )
                    ))),
                    headingLevel = 2
                  )),
                  rows = Seq(SummaryListRowViewModel(
                    key = Key(Text(messagesForLanguage.finalCyaValue(1))),
                    value = Value(Text(documentTypeModel.description))
                  ))
                )
            }
          }

          s"when multiple documents have been added" in new Setup(emptyUserAnswers
            .set(DocumentTypePage(testIndex1), documentTypeModel)
            .set(DocumentReferencePage(testIndex1), "reference")
            .set(DocumentTypePage(testIndex2), documentTypeModel)
            .set(DocumentReferencePage(testIndex2), "reference")
          ) {

            helper.finalCyaSummary() mustBe
              SummaryList(
                card = Some(CardViewModel(
                  title = messagesForLanguage.finalCyaCardTitle,
                  actions = Some(Actions(items = Seq(
                    ActionItemViewModel(
                      href = routes.DocumentsAddToListController.onPageLoad(testErn, testDraftId).url,
                      content = Text(messagesForLanguage.change),
                      id = "changeDocuments"
                    )
                  ))),
                  headingLevel = 2
                )),
                rows = Seq(
                  SummaryListRowViewModel(
                    key = Key(Text(messagesForLanguage.finalCyaValue(1))),
                    value = Value(Text(documentTypeModel.description))
                  ),
                  SummaryListRowViewModel(
                    key = Key(Text(messagesForLanguage.finalCyaValue(2))),
                    value = Value(Text(documentTypeModel.description))
                  )
                )
              )
          }
        }
      }
    }
  }
}
