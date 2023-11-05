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

package views.sections.documents

import base.ViewSpecBase
import fixtures.messages.sections.documents.DocumentsAddToListMessages.English
import forms.sections.documents.DocumentsAddToListFormProvider
import models.requests.DataRequest
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import pages.sections.documents.{DocumentDescriptionPage, DocumentsCertificatesPage}
import play.api.i18n.Messages
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import viewmodels.helpers.DocumentsAddToListHelper
import views.html.sections.documents.DocumentsAddToListView
import views.{BaseSelectors, ViewBehaviours}

class DocumentsAddToListViewSpec extends ViewSpecBase with ViewBehaviours {

  object Selectors extends BaseSelectors {
    val returnToDraftLink: String = "#save-and-exit"
    val cardTitle: String = ".govuk-summary-card__title-wrapper > .govuk-summary-card__title"
    val legendQuestion = ".govuk-fieldset__legend.govuk-fieldset__legend--m"
    val errorSummary: Int => String = index => s".govuk-error-summary__list > li:nth-child($index)"
    val errorField: String = "p.govuk-error-message"
    val removeItemLink: Int => String = index => s"#removeDocuments$index"
  }

  "DocumentsAddToListView" - {

    s"when being rendered in lang code of '${English.lang.code}' for singular item" - {

      implicit val msgs: Messages = messages(app, English.lang)

      val userAnswers = emptyUserAnswers
        .set(DocumentDescriptionPage(0), "description")

      implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(), userAnswers)

      val view = app.injector.instanceOf[DocumentsAddToListView]
      val form = app.injector.instanceOf[DocumentsAddToListFormProvider].apply()
      val helper = app.injector.instanceOf[DocumentsAddToListHelper].allDocumentsSummary()

      implicit val doc: Document = Jsoup.parse(view(
        form = form,
        onSubmitCall = testOnwardRoute,
        documents = helper
      ).toString())

      behave like pageWithExpectedElementsAndMessages(Seq(
        Selectors.title -> English.title(1),
        Selectors.h1 -> English.heading(1),
        Selectors.cardTitle -> English.documentCardTitle(0),
        Selectors.removeItemLink(1) -> English.removeDocument(0),
        Selectors.legendQuestion -> English.h2,
        Selectors.radioButton(1) -> English.yes,
        Selectors.radioButton(2) -> English.no1,
        Selectors.radioButton(4) -> English.moreLater,
        Selectors.button -> English.saveAndContinue,
        Selectors.returnToDraftLink -> English.returnToDraft
      ))
    }

    s"when being rendered in lang code of '${English.lang.code}' for multiple items" - {

      implicit val msgs: Messages = messages(app, English.lang)

      val userAnswers = emptyUserAnswers
        .set(DocumentDescriptionPage(0), "description1")
        .set(DocumentDescriptionPage(1), "description2")

      implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(), userAnswers)

      val view = app.injector.instanceOf[DocumentsAddToListView]
      val form = app.injector.instanceOf[DocumentsAddToListFormProvider].apply()
      val helper = app.injector.instanceOf[DocumentsAddToListHelper].allDocumentsSummary()

      implicit val doc: Document = Jsoup.parse(view(
        form = form,
        onSubmitCall = testOnwardRoute,
        documents = helper
      ).toString())

      behave like pageWithExpectedElementsAndMessages(Seq(
        Selectors.title -> English.title(2),
        Selectors.h1 -> English.heading(2),
        Selectors.cardTitle -> English.documentCardTitle(0),
        Selectors.removeItemLink(1) -> English.removeDocument(0),
        Selectors.legendQuestion -> English.h2,
        Selectors.radioButton(1) -> English.yes,
        Selectors.radioButton(2) -> English.no2,
        Selectors.radioButton(4) -> English.moreLater,
        Selectors.button -> English.saveAndContinue,
        Selectors.returnToDraftLink -> English.returnToDraft
      ))
    }
  }
}

