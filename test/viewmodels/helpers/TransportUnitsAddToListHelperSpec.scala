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
import controllers.sections.transportUnit.{routes => transportUnitRoutes}
import fixtures.messages.sections.transportUnit.TransportUnitAddToListMessages
import models.{NormalMode, UserAnswers}
import models.requests.DataRequest
import models.sections.transportUnit.TransportSealTypeModel
import models.sections.transportUnit.TransportUnitType.{Container, FixedTransport, Tractor}
import pages.sections.transportUnit._
import play.api.i18n.Messages
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.{HtmlContent, Text}
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist._
import viewmodels.checkAnswers.sections.transportUnit._
import views.html.components.{link, span, tag}

class TransportUnitsAddToListHelperSpec extends SpecBase {

  implicit lazy val link: link = app.injector.instanceOf[link]
  implicit lazy val tag: tag = app.injector.instanceOf[tag]
  implicit lazy val span: span = app.injector.instanceOf[span]

  class Setup(userAnswers: UserAnswers = emptyUserAnswers) {
    implicit lazy val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(), userAnswers)

    lazy val helper: TransportUnitsAddToListHelper = app.injector.instanceOf[TransportUnitsAddToListHelper]
  }

  "TransportUnitsAddToListHelper" - {
    Seq(TransportUnitAddToListMessages.English).foreach { msg =>
      "return nothing" - {
        s"when no answers specified for '${msg.lang.code}'" in new Setup() {
          implicit val msgs: Messages = messages(Seq(msg.lang))

          helper.allTransportUnitsSummary() mustBe Nil
        }
      }
      "return required rows when all answers filled out" - {

        s"when all answers entered '${msg.lang.code}' and single transport units" in new Setup(
          emptyUserAnswers
            .set(TransportUnitTypePage(testIndex1), Tractor)
            .set(TransportUnitIdentityPage(testIndex1), "wee")
            .set(TransportSealChoicePage(testIndex1), true)
            .set(TransportSealTypePage(testIndex1), TransportSealTypeModel("seal Type", Some("more seal info")))
            .set(TransportUnitGiveMoreInformationChoicePage(testIndex1), true)
            .set(TransportUnitGiveMoreInformationPage(testIndex1), Some("more information for transport unit"))) {
          implicit lazy val msgs: Messages = messages(Seq(msg.lang))

          helper.allTransportUnitsSummary() mustBe Seq(
            SummaryList(
              card = Some(Card(
                title = Some(CardTitle(Text(msg.transportUnit1))),
                actions = Some(Actions(items = Seq(
                  ActionItem(
                    href = transportUnitRoutes.TransportUnitRemoveUnitController.onPageLoad(testErn, testDraftId, testIndex1).url,
                    content = Text(msg.remove),
                    visuallyHiddenText = Some(msg.transportUnit1),
                    attributes = Map("id" -> "removeTransportUnit1")
                  )
                ))))),
              rows = Seq(
                TransportUnitTypeSummary.row(testIndex1, true).get,
                TransportUnitIdentitySummary.row(testIndex1, true).get,
                TransportSealChoiceSummary.row(testIndex1, true).get,
                TransportSealTypeSummary.row(testIndex1, true).get,
                TransportSealInformationSummary.row(testIndex1, true).get,
                TransportUnitGiveMoreInformationSummary.row(testIndex1, true).get
              )
            )
          )
        }

        s"when incomplete answers entered '${msg.lang.code}' for container unit type" in new Setup(
          emptyUserAnswers
            .set(TransportUnitTypePage(testIndex1), Container)
            .set(TransportUnitIdentityPage(testIndex1), "1234")) {
          implicit lazy val msgs: Messages = messages(Seq(msg.lang))

          helper.allTransportUnitsSummary() mustBe Seq(
            SummaryList(
              card = Some(Card(
                title = Some(
                  CardTitle(
                    HtmlContent(HtmlFormat.fill(
                      Seq(
                        span(msg.transportUnit1, Some("govuk-!-margin-right-2")),
                        tag(
                          message = "Incomplete",
                          colour = "red"
                        )
                      )
                    ))
                  )
                ),
                actions = Some(Actions(items = Seq(
                  ActionItem(
                    href = transportUnitRoutes.TransportUnitTypeController.onPageLoad(testErn, testDraftId, testIndex1, NormalMode).url,
                    content = Text(msg.continueEditing),
                    visuallyHiddenText = Some(msg.transportUnit1),
                    attributes = Map("id" -> "editTransportUnit1")
                  ),
                  ActionItem(
                    href = transportUnitRoutes.TransportUnitRemoveUnitController.onPageLoad(testErn, testDraftId, testIndex1).url,
                    content = Text(msg.remove),
                    visuallyHiddenText = Some(msg.transportUnit1),
                    attributes = Map("id" -> "removeTransportUnit1")
                  )
                ))))),
              rows = Seq(
                TransportUnitTypeSummary.row(testIndex1, false).get,
                TransportUnitIdentitySummary.row(testIndex1, false).get,
                TransportSealChoiceSummary.row(testIndex1, false).get,
                TransportUnitGiveMoreInformationSummary.row(testIndex1, false).get
              )
            )
          )
        }

        s"when all answers entered '${msg.lang.code}' and multiple transport units" in new Setup(emptyUserAnswers
          .set(TransportUnitTypePage(testIndex1), Tractor)
          .set(TransportUnitIdentityPage(testIndex1), "wee")
          .set(TransportSealChoicePage(testIndex1), true)
          .set(TransportSealTypePage(testIndex1), TransportSealTypeModel("seal Type", Some("more seal info")))
          .set(TransportUnitGiveMoreInformationChoicePage(testIndex1), true)
          .set(TransportUnitGiveMoreInformationPage(testIndex1), Some("more information for transport unit"))
          .set(TransportUnitTypePage(testIndex2), FixedTransport)
          .set(TransportUnitIdentityPage(testIndex2), "wee2")
          .set(TransportSealChoicePage(testIndex2), true)
          .set(TransportSealTypePage(testIndex2), TransportSealTypeModel("seal Type", Some("more seal info 2")))
          .set(TransportUnitGiveMoreInformationChoicePage(testIndex2), true)
          .set(TransportUnitGiveMoreInformationPage(testIndex2), Some("more information for transport unit 2"))) {
          implicit lazy val msgs: Messages = messages(Seq(msg.lang))

          helper.allTransportUnitsSummary() mustBe Seq(
            SummaryList(
              card = Some(Card(
                title = Some(CardTitle(Text(msg.transportUnit1))),
                actions = Some(Actions(items = Seq(
                  ActionItem(
                    href = transportUnitRoutes.TransportUnitRemoveUnitController.onPageLoad(testErn, testDraftId, testIndex1).url,
                    content = Text(msg.remove),
                    visuallyHiddenText = Some(msg.transportUnit1),
                    attributes = Map("id" -> "removeTransportUnit1")
                  )
                ))))),
              rows = Seq(
                TransportUnitTypeSummary.row(testIndex1, true).get,
                TransportUnitIdentitySummary.row(testIndex1, true).get,
                TransportSealChoiceSummary.row(testIndex1, true).get,
                TransportSealTypeSummary.row(testIndex1, true).get,
                TransportSealInformationSummary.row(testIndex1, true).get,
                TransportUnitGiveMoreInformationSummary.row(testIndex1, true).get
              )
            ),
            SummaryList(
              card = Some(Card(
                title = Some(CardTitle(Text(msg.transportUnit2))),
                actions = Some(Actions(items = Seq(
                  ActionItem(
                    href = transportUnitRoutes.TransportUnitRemoveUnitController.onPageLoad(testErn, testDraftId, testIndex2).url,
                    content = Text(msg.remove),
                    visuallyHiddenText = Some(msg.transportUnit2),
                    attributes = Map("id" -> "removeTransportUnit2")
                  )
                ))))),
              rows = Seq(
                TransportUnitTypeSummary.row(testIndex2, true).get
              )
            )
          )
        }
      }

    }
  }

}
