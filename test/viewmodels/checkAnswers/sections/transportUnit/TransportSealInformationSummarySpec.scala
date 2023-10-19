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

package viewmodels.checkAnswers.sections.transportUnit

import base.SpecBase
import fixtures.TransportUnitFixtures
import fixtures.messages.sections.transportUnit.TransportSealTypeMessages
import models.CheckMode
import org.scalatest.matchers.must.Matchers
import pages.sections.transportUnit.TransportSealTypePage
import play.api.Application
import play.api.i18n.Messages
import play.api.test.FakeRequest
import uk.gov.hmrc.govukfrontend.views.Aliases.Value
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.{HtmlContent, Text}
import viewmodels.govuk.summarylist._
import viewmodels.implicits._
import views.html.components.link

class TransportSealInformationSummarySpec extends SpecBase with Matchers with TransportUnitFixtures {

  lazy val app: Application = applicationBuilder().build()
  lazy val link: link = app.injector.instanceOf[link]

  "TransportSealInformationSummary" - {

    lazy val app = applicationBuilder().build()

    Seq(TransportSealTypeMessages.English, TransportSealTypeMessages.Welsh).foreach { messagesForLanguage =>

      s"when being rendered in lang code of '${messagesForLanguage.lang.code}'" - {

        implicit lazy val msgs: Messages = messages(app, messagesForLanguage.lang)

        "when there's no answer" - {

          "must output no row" in {

            implicit lazy val request = dataRequest(FakeRequest(), emptyUserAnswers)

            TransportSealInformationSummary.row(request.userAnswers, link) mustBe None
          }
        }

        "when there's an answer" - {

          s"must output the expected row for TransportSealInformation" in {

            implicit lazy val request = dataRequest(FakeRequest(), emptyUserAnswers.set(TransportSealTypePage, transportSealTypeModelMax))

            TransportSealInformationSummary.row(request.userAnswers, link) mustBe
              Some(
                SummaryListRowViewModel(
                  key = messagesForLanguage.moreInfoCYA,
                  value = Value(Text(transportSealTypeModelMax.moreInfo.value)),
                  actions = Seq(
                    ActionItemViewModel(
                      content = messagesForLanguage.change,
                      href = controllers.sections.transportUnit.routes.TransportSealTypeController.onPageLoad(testErn, testLrn, CheckMode).url,
                      id = "changeTransportSealInformation"
                    ).withVisuallyHiddenText(messagesForLanguage.moreInfoCyaChangeHidden)
                  )
                )
              )
          }
        }

        "when there's an answer but no more information has been added" - {

          s"must output a row with a link to add more information" in {

            implicit lazy val request = dataRequest(FakeRequest(), emptyUserAnswers.set(TransportSealTypePage, transportSealTypeModelMin))

            TransportSealInformationSummary.row(request.userAnswers, link) mustBe
              Some(
                SummaryListRowViewModel(
                  key = messagesForLanguage.moreInfoCYA,
                  value = ValueViewModel(HtmlContent(link(
                    link = controllers.sections.transportUnit.routes.TransportSealTypeController.onPageLoad(testErn, testLrn, CheckMode).url,
                    messageKey = messagesForLanguage.moreInfoCYAAddInfo
                  )))
                )
              )
          }
        }
      }
    }
  }
}