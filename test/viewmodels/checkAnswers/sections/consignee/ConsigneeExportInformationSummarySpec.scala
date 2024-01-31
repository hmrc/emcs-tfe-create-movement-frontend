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

package viewmodels.checkAnswers.sections.consignee

import base.SpecBase
import fixtures.messages.sections.consignee.ConsigneeExportInformationMessages
import models.NormalMode
import models.sections.consignee.ConsigneeExportInformation
import org.scalatest.matchers.must.Matchers
import pages.sections.consignee.ConsigneeExportInformationPage
import play.api.i18n.Messages
import play.api.test.FakeRequest
import play.twirl.api.Html
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.HtmlContent
import viewmodels.govuk.summarylist._
import viewmodels.implicits._
import views.html.components.list

class ConsigneeExportInformationSummarySpec extends SpecBase with Matchers {
  lazy val list: list = app.injector.instanceOf[list]

  "ConsigneeExportInformationSummary" - {

    Seq(ConsigneeExportInformationMessages.English).foreach { messagesForLanguage =>

      s"when being rendered in lang code of '${messagesForLanguage.lang.code}'" - {
        implicit val msgs: Messages = messages(Seq(messagesForLanguage.lang))

        "when there is no answer" - {
          "must output no summary row" in {
            implicit lazy val request = dataRequest(FakeRequest(), emptyUserAnswers)
            ConsigneeExportInformationSummary(list).row() mustBe None
          }
        }

        "when there are multiple answers" - {
          "must output the expected row" in {
            implicit lazy val request = dataRequest(
              FakeRequest(),
              emptyUserAnswers
                .set(ConsigneeExportInformationPage, ConsigneeExportInformation.values)
            )

            ConsigneeExportInformationSummary(list).row() mustBe
              Some(
                SummaryListRowViewModel(
                  key = messagesForLanguage.cyaLabel,
                  value = ValueViewModel(
                    HtmlContent(list(Seq(
                      Html(messagesForLanguage.cyaValueVatNumber),
                      Html(messagesForLanguage.cyaValueEoriNumber),
                      Html(messagesForLanguage.cyaValueNoInformation)
                    )))
                  ),
                  actions = Seq(
                    ActionItemViewModel(
                      content = messagesForLanguage.change,
                      href = controllers.sections.consignee.routes.ConsigneeExportInformationController.onPageLoad(testErn, testDraftId, NormalMode).url,
                      id = "changeConsigneeExportInformation"
                    ).withVisuallyHiddenText(messagesForLanguage.cyaChangeHidden)
                  )
                )
              )
          }
        }

        Seq(
          ConsigneeExportInformation.VatNumber -> messagesForLanguage.cyaValueVatNumber,
          ConsigneeExportInformation.EoriNumber -> messagesForLanguage.cyaValueEoriNumber,
          ConsigneeExportInformation.NoInformation -> messagesForLanguage.cyaValueNoInformation,
        ).foreach {
          case (identification, text) =>
            s"when there is only one answer of $identification" - {

              "must output the expected row" in {
                implicit lazy val request = dataRequest(
                  FakeRequest(),
                  emptyUserAnswers
                    .set(ConsigneeExportInformationPage, Set(identification))
                )

                ConsigneeExportInformationSummary(list).row() mustBe
                  Some(
                    SummaryListRowViewModel(
                      key = messagesForLanguage.cyaLabel,
                      value = ValueViewModel(
                        HtmlContent(list(Seq(
                          Html(text)
                        )))
                      ),
                      actions = Seq(
                        ActionItemViewModel(
                          content = messagesForLanguage.change,
                          href = controllers.sections.consignee.routes.ConsigneeExportInformationController.onPageLoad(testErn, testDraftId, NormalMode).url,
                          id = "changeConsigneeExportInformation"
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
