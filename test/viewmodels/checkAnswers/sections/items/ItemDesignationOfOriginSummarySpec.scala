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

package viewmodels.checkAnswers.sections.items

import base.SpecBase
import controllers.sections.items.routes
import fixtures.messages.sections.items.ItemDesignationOfOriginMessages
import models.CheckMode
import models.sections.items.ItemDesignationOfOriginModel
import models.sections.items.ItemGeographicalIndicationType.{NoGeographicalIndication, ProtectedDesignationOfOrigin, ProtectedGeographicalIndication}
import pages.sections.items.ItemDesignationOfOriginPage
import play.api.i18n.Messages
import play.api.test.FakeRequest
import uk.gov.hmrc.govukfrontend.views.Aliases.{HtmlContent, Text, Value}
import viewmodels.govuk.summarylist._
import viewmodels.implicits._
import views.html.components.p

class ItemDesignationOfOriginSummarySpec extends SpecBase {

  lazy val summary: ItemDesignationOfOriginSummary = app.injector.instanceOf[ItemDesignationOfOriginSummary]
  lazy val p: p = app.injector.instanceOf[p]

  "ItemDesignationOfOriginSummary" - {

    Seq(ItemDesignationOfOriginMessages.English).foreach { messagesForLanguage =>

      s"when being rendered in lang code of '${messagesForLanguage.lang.code}'" - {

        implicit lazy val msgs: Messages = messages(Seq(messagesForLanguage.lang))

        "when there's no answer" - {

          "must output the expected data" in {
            implicit lazy val request = dataRequest(FakeRequest(), emptyUserAnswers)

            summary.row(testIndex1) mustBe None
          }
        }

        "when there's an answer" - {

          "must output the expected row (has name/register number but not S200)" - {

            Seq(
              ProtectedDesignationOfOrigin -> "The product has a Protected Designation of Origin (PDO)",
              ProtectedGeographicalIndication -> "The product has a Protected Geographical Indication (PGI)",
              NoGeographicalIndication -> "None"
            ).foreach { geographicalIndicationAndExpectedMessage =>

              s"for ${geographicalIndicationAndExpectedMessage._1}" in {
                implicit lazy val request = dataRequest(FakeRequest(), emptyUserAnswers
                  .set(ItemDesignationOfOriginPage(testIndex1), ItemDesignationOfOriginModel(geographicalIndicationAndExpectedMessage._1, Some("Name/Register number"), None))
                )

                summary.row(testIndex1) mustBe Some(
                  SummaryListRowViewModel(
                    key = messagesForLanguage.cyaLabel,
                    value = Value(HtmlContent(Seq(
                      p()(Text(geographicalIndicationAndExpectedMessage._2).asHtml).toString(),
                      p()(Text("Name/Register number").asHtml).toString()
                    ).mkString)),
                    actions = Seq(
                      ActionItemViewModel(
                        content = messagesForLanguage.change,
                        href = routes.ItemDesignationOfOriginController.onPageLoad(testErn, testDraftId, testIndex1, CheckMode).url,
                        id = "changeItemDesignationOfOrigin1"
                      ).withVisuallyHiddenText(messagesForLanguage.cyaChangeHidden)
                    )
                  )
                )
              }
            }
          }

          "must output the expected row (has name/register number and has marketing and labelling)" - {

            Seq(
              ProtectedDesignationOfOrigin -> "The product has a Protected Designation of Origin (PDO)",
              ProtectedGeographicalIndication -> "The product has a Protected Geographical Indication (PGI)",
              NoGeographicalIndication -> "None"
            ).foreach { geographicalIndicationAndExpectedMessage =>

              s"for ${geographicalIndicationAndExpectedMessage._1}" in {
                implicit lazy val request = dataRequest(FakeRequest(), emptyUserAnswers
                  .set(ItemDesignationOfOriginPage(testIndex1), ItemDesignationOfOriginModel(geographicalIndicationAndExpectedMessage._1, Some("Name/Register number"), Some(true)))
                )

                summary.row(testIndex1) mustBe Some(
                  SummaryListRowViewModel(
                    key = messagesForLanguage.cyaLabelS200,
                    value = Value(HtmlContent(Seq(
                      p()(Text(geographicalIndicationAndExpectedMessage._2).asHtml).toString(),
                      p()(Text("Name/Register number").asHtml).toString(),
                      p()(Text("It is hereby certified that the product described is marketed and labelled in compliance with Regulation (EU) 2019/787").asHtml).toString()
                    ).mkString)),
                    actions = Seq(
                      ActionItemViewModel(
                        content = messagesForLanguage.change,
                        href = routes.ItemDesignationOfOriginController.onPageLoad(testErn, testDraftId, testIndex1, CheckMode).url,
                        id = "changeItemDesignationOfOrigin1"
                      ).withVisuallyHiddenText(messagesForLanguage.cyaChangeHiddenS200)
                    )
                  )
                )
              }
            }
          }

          "must output the expected row (no name/register number but hasn't got marketing and labelling)" - {

            Seq(
              ProtectedDesignationOfOrigin -> "The product has a Protected Designation of Origin (PDO)",
              ProtectedGeographicalIndication -> "The product has a Protected Geographical Indication (PGI)",
              NoGeographicalIndication -> "None"
            ).foreach { geographicalIndicationAndExpectedMessage =>

              s"for ${geographicalIndicationAndExpectedMessage._1}" in {
                implicit lazy val request = dataRequest(FakeRequest(), emptyUserAnswers
                  .set(ItemDesignationOfOriginPage(testIndex1), ItemDesignationOfOriginModel(geographicalIndicationAndExpectedMessage._1, None, Some(false)))
                )

                summary.row(testIndex1) mustBe Some(
                  SummaryListRowViewModel(
                    key = messagesForLanguage.cyaLabelS200,
                    value = Value(HtmlContent(Seq(
                      p()(Text(geographicalIndicationAndExpectedMessage._2).asHtml).toString(),
                      p()(Text("I don’t want to provide a statement about the marketing and labelling of the spirit").asHtml).toString()
                    ).mkString)),
                    actions = Seq(
                      ActionItemViewModel(
                        content = messagesForLanguage.change,
                        href = routes.ItemDesignationOfOriginController.onPageLoad(testErn, testDraftId, testIndex1, CheckMode).url,
                        id = "changeItemDesignationOfOrigin1"
                      ).withVisuallyHiddenText(messagesForLanguage.cyaChangeHiddenS200)
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
