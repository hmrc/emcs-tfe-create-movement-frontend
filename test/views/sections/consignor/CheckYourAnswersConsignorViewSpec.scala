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

package views.sections.consignor

import base.SpecBase
import fixtures.messages.sections.consignor.CheckYourAnswersConsignorMessages
import models.CheckMode
import models.requests.DataRequest
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import pages.sections.consignor.{ConsignorAddressPage, ConsignorPaidTemporaryAuthorisationCodePage}
import play.api.i18n.Messages
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import viewmodels.checkAnswers.sections.consignor.ConsignorCheckAnswersHelper
import views.html.sections.consignor.CheckYourAnswersConsignorView
import views.{BaseSelectors, ViewBehaviours}

class CheckYourAnswersConsignorViewSpec extends SpecBase with ViewBehaviours {

  object Selectors extends BaseSelectors {
    def govukSummaryListKey(id: Int) = s".govuk-summary-list__row:nth-of-type($id) .govuk-summary-list__key"

    val changeConsignorAddressLink = "#changeConsignorAddress"

    val changeConsignorPaidTemporaryAuthorisationCodeLink = "#changeConsignorPaidTemporaryAuthorisationCode"
  }

  "CheckYourAnswersConsignor view" - {

    Seq(CheckYourAnswersConsignorMessages.English).foreach { messagesForLanguage =>

      s"when being rendered in lang code of '${messagesForLanguage.lang.code}'" - {

        implicit val msgs: Messages = messages(Seq(messagesForLanguage.lang))
        implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(), emptyUserAnswers.set(
          ConsignorAddressPage, testUserAddress
        ))

        lazy val view = app.injector.instanceOf[CheckYourAnswersConsignorView]

        val summaryListHelper = app.injector.instanceOf[ConsignorCheckAnswersHelper]

        "for a non-XIPC trader" - {
          implicit val doc: Document = Jsoup.parse(view(
            summaryListHelper.summaryList(),
            controllers.sections.consignor.routes.CheckYourAnswersConsignorController.onSubmit(testErn, testDraftId)
          ).toString())

          behave like pageWithExpectedElementsAndMessages(Seq(
            Selectors.title -> messagesForLanguage.title,
            Selectors.h1 -> messagesForLanguage.heading,
            Selectors.subHeadingCaptionSelector -> messagesForLanguage.consignorInformationSection,
            Selectors.govukSummaryListKey(1) -> messagesForLanguage.ern,
            Selectors.govukSummaryListKey(2) -> messagesForLanguage.address,
            Selectors.button -> messagesForLanguage.confirmAnswers
          ))

          "have a link to change Address details" in {

            doc.select(Selectors.changeConsignorAddressLink).attr("href") mustBe
              controllers.sections.consignor.routes.ConsignorAddressController.onPageLoad(testErn, testDraftId, CheckMode).url
          }
        }

        "for an XIPC trader" - {

          implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(), emptyUserAnswers
            .set(ConsignorPaidTemporaryAuthorisationCodePage, testPaidTemporaryAuthorisationCode)
            .set(ConsignorAddressPage, testUserAddress),
            ern = testNITemporaryCertifiedConsignorErn)

          implicit val doc: Document = Jsoup.parse(view(
            summaryListHelper.summaryList(),
            controllers.sections.consignor.routes.CheckYourAnswersConsignorController.onSubmit(testNITemporaryCertifiedConsignorErn, testDraftId)
          ).toString())

          behave like pageWithExpectedElementsAndMessages(Seq(
            Selectors.title -> messagesForLanguage.title,
            Selectors.h1 -> messagesForLanguage.heading,
            Selectors.subHeadingCaptionSelector -> messagesForLanguage.consignorInformationSection,
            Selectors.govukSummaryListKey(1) -> messagesForLanguage.paidTemporaryAuthorisationCode,
            Selectors.govukSummaryListKey(2) -> messagesForLanguage.address,
            Selectors.button -> messagesForLanguage.confirmAnswers
          ))

          "have a link to change Paid Temporary Authorisation Code" in {

            doc.select(Selectors.changeConsignorPaidTemporaryAuthorisationCodeLink).attr("href") mustBe
              controllers.sections.consignor.routes.ConsignorPaidTemporaryAuthorisationCodeController.onPageLoad(testNITemporaryCertifiedConsignorErn, testDraftId, CheckMode).url
          }

          "have a link to change Address details" in {

            doc.select(Selectors.changeConsignorAddressLink).attr("href") mustBe
              controllers.sections.consignor.routes.ConsignorAddressController.onPageLoad(testNITemporaryCertifiedConsignorErn, testDraftId, CheckMode).url
          }
        }
      }
    }
  }

}
