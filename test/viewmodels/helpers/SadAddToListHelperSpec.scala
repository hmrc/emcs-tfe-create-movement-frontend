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
import controllers.sections.sad.{routes => sadRoutes}
import fixtures.messages.sections.sad.SadAddToListMessages
import models.UserAnswers
import models.requests.DataRequest
import pages.sections.sad._
import play.api.i18n.Messages
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist._
import viewmodels.checkAnswers.sections.sad._
import viewmodels.govuk.all.{ActionItemViewModel, CardViewModel, SummaryListRowViewModel}
import views.html.components.link

class SadAddToListHelperSpec extends SpecBase {

  implicit lazy val link: link = app.injector.instanceOf[link]

  class Setup(userAnswers: UserAnswers = emptyUserAnswers) {
    implicit lazy val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(), userAnswers)
    lazy val helper: SadAddToListHelper = app.injector.instanceOf[SadAddToListHelper]
  }

  "SadAddToListHelper" - {

    Seq(SadAddToListMessages.English).foreach { msg =>

      s"when rendered in language code of '${msg.lang.code}'" - {

        implicit lazy val msgs: Messages = messages(Seq(msg.lang))

        ".allSadSummary()" - {

          "return nothing" - {
            s"when no answers specified for" in new Setup() {
              helper.allSadSummary() mustBe Nil
            }
          }

          "return required rows when all answers filled out" - {

            s"when all answers entered and single Sad" in new Setup(
              emptyUserAnswers
                .set(ImportNumberPage(testIndex1), "wee")) {

              helper.allSadSummary() mustBe Seq(
                SummaryList(
                  card = Some(Card(
                    title = Some(CardTitle(Text(msg.sad1))),
                    actions = Some(Actions(items = Seq(
                      ActionItem(
                        href = sadRoutes.SadRemoveDocumentController.onPageLoad(testErn, testDraftId, testIndex1).url,
                        content = Text(msg.remove),
                        visuallyHiddenText = None,
                        attributes = Map("id" -> "removeSad1")
                      )
                    ))))),
                  rows = Seq(
                    ImportNumberSummary.row(testIndex1).get
                  )
                )
              )
            }

            s"when all answers entered and multiple Customs Declarations" in new Setup(emptyUserAnswers
              .set(ImportNumberPage(testIndex1), "wee")
              .set(ImportNumberPage(testIndex2), "wee2")) {

              helper.allSadSummary() mustBe Seq(
                SummaryList(
                  card = Some(Card(
                    title = Some(CardTitle(Text(msg.sad1))),
                    actions = Some(Actions(items = Seq(
                      ActionItem(
                        href = sadRoutes.SadRemoveDocumentController.onPageLoad(testErn, testDraftId, testIndex1).url,
                        content = Text(msg.remove),
                        visuallyHiddenText = None,
                        attributes = Map("id" -> "removeSad1")
                      )
                    ))))),
                  rows = Seq(
                    ImportNumberSummary.row(testIndex1).get
                  )
                ),
                SummaryList(
                  card = Some(Card(
                    title = Some(CardTitle(Text(msg.sad2))),
                    actions = Some(Actions(items = Seq(
                      ActionItem(
                        href = sadRoutes.SadRemoveDocumentController.onPageLoad(testErn, testDraftId, testIndex2).url,
                        content = Text(msg.remove),
                        visuallyHiddenText = None,
                        attributes = Map("id" -> "removeSad2")
                      )
                    ))))),
                  rows = Seq(
                    ImportNumberSummary.row(testIndex2).get
                  )
                )
              )
            }
          }
        }

        ".finalCyaSummary()" - {

          "return nothing" - {
            s"when no answers specified for" in new Setup() {
              helper.finalCyaSummary() mustBe None
            }
          }

          "return required rows when all answers filled out" - {

            s"when all answers entered and single Sad" in new Setup(emptyUserAnswers
              .set(ImportNumberPage(testIndex1), "wee")
            ) {

              helper.finalCyaSummary() mustBe Some(
                SummaryList(
                  card = Some(CardViewModel(
                    title = msg.finalCyaCardTitle,
                    actions = Some(Actions(items = Seq(
                      ActionItemViewModel(
                        href = sadRoutes.SadAddToListController.onPageLoad(testErn, testDraftId).url,
                        content = Text(msg.change),
                        id = "changeSAD"
                      )
                    ))),
                    headingLevel = 2
                  )),
                  rows = Seq(
                    SummaryListRowViewModel(
                      Key(Text(msg.finalCyaKey(1))),
                      Value(Text("wee"))
                    )
                  )
                )
              )
            }

            s"when all answers entered and multiple Sads" in new Setup(emptyUserAnswers
              .set(ImportNumberPage(testIndex1), "wee")
              .set(ImportNumberPage(testIndex2), "wee2")) {

              helper.finalCyaSummary() mustBe Some(
                SummaryList(
                  card = Some(CardViewModel(
                    title = msg.finalCyaCardTitle,
                    actions = Some(Actions(items = Seq(
                      ActionItemViewModel(
                        href = sadRoutes.SadAddToListController.onPageLoad(testErn, testDraftId).url,
                        content = Text(msg.change),
                        id = "changeSAD"
                      )
                    ))),
                    headingLevel = 2
                  )),
                  rows = Seq(
                    SummaryListRowViewModel(
                      Key(Text(msg.finalCyaKey(1))),
                      Value(Text("wee"))
                    ),
                    SummaryListRowViewModel(
                      Key(Text(msg.finalCyaKey(2))),
                      Value(Text("wee2"))
                    )
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
