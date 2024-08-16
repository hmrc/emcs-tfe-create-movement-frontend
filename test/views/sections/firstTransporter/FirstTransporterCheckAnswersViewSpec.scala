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

package views.sections.firstTransporter

import base.SpecBase
import fixtures.messages.sections.firstTransporter.FirstTransporterCheckAnswerMessages
import models.requests.DataRequest
import models.{CheckMode, VatNumberModel}
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import pages.sections.firstTransporter.{FirstTransporterAddressPage, FirstTransporterVatPage}
import play.api.i18n.{Lang, Messages}
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import uk.gov.hmrc.govukfrontend.views.Aliases.SummaryList
import viewmodels.checkAnswers.sections.firstTransporter._
import views.html.sections.firstTransporter.FirstTransporterCheckAnswersView
import views.{BaseSelectors, ViewBehaviours}

class FirstTransporterCheckAnswersViewSpec extends SpecBase with ViewBehaviours {

  class Fixture(lang: Lang) {

    implicit val msgs: Messages = messages(Seq(lang))
    implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(),
      emptyUserAnswers
        .set(FirstTransporterVatPage, VatNumberModel(true, Some("GB123456789")))
        .set(FirstTransporterAddressPage, testUserAddress.copy(businessName = Some("Transporter name")))
    )

    lazy val view = app.injector.instanceOf[FirstTransporterCheckAnswersView]

    implicit val doc: Document = Jsoup.parse(view(
      SummaryList(Seq(
        FirstTransporterVatChoiceSummary.row(),
        FirstTransporterVatSummary.row(),
        Some(FirstTransporterAddressSummary.row())
      ).flatten),
      testOnwardRoute
    ).toString())
  }

  object Selectors extends BaseSelectors {
    def govukSummaryListKey(id: Int) = s".govuk-summary-list__row:nth-of-type($id) .govuk-summary-list__key"
  }

  "FirstTransporterCheckAnswersView" - {

    Seq(FirstTransporterCheckAnswerMessages.English).foreach { messagesForLanguage =>

      s"when being rendered in lang code of '${messagesForLanguage.lang.code}'" - new Fixture(messagesForLanguage.lang) {

        behave like pageWithExpectedElementsAndMessages(Seq(
          Selectors.title -> messagesForLanguage.title,
          Selectors.subHeadingCaptionSelector -> messagesForLanguage.firstTransporterSection,
          Selectors.h1 -> messagesForLanguage.heading,
          Selectors.govukSummaryListKey(1) -> messagesForLanguage.firstTransporterHasVat,
          Selectors.govukSummaryListKey(2) -> messagesForLanguage.firstTransporterVatNumber,
          Selectors.govukSummaryListKey(3) -> messagesForLanguage.firstTransporterAddress,
          Selectors.button -> messagesForLanguage.confirmAnswers
        ))

        "have a link to change first transporter VAT Choice" in {
          doc.getElementById("changeFirstTransporterVatChoice").attr("href") mustBe
            controllers.sections.firstTransporter.routes.FirstTransporterVatController.onPageLoad(testErn, testDraftId, CheckMode).url
        }
        "have a link to change first transporter VAT Number" in {
          doc.getElementById("changeFirstTransporterVatNumber").attr("href") mustBe
            controllers.sections.firstTransporter.routes.FirstTransporterVatController.onPageLoad(testErn, testDraftId, CheckMode).url
        }

        "have a link to change first transporter address" in {
          doc.getElementById("changeFirstTransporterAddress").attr("href") mustBe
            controllers.sections.firstTransporter.routes.FirstTransporterAddressController.onPageLoad(testErn, testDraftId, CheckMode).url
        }
      }
    }
  }
}
