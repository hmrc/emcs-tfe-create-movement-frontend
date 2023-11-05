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
import fixtures.messages.sections.documents.DocumentsAddToListMessages.English
import models.{Index, UserAnswers}
import pages.sections.documents.{DocumentDescriptionPage, DocumentsCertificatesPage}
import play.api.Application
import play.api.i18n.Messages
import play.api.test.FakeRequest
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist._
import viewmodels.checkAnswers.sections.documents.{DocumentDescriptionSummary, DocumentsCertificatesSummary}
import views.html.components.link

class DocumentsAddToListHelperSpec extends SpecBase {

  class Setup(userAnswers: UserAnswers = emptyUserAnswers) {
    lazy val app: Application = applicationBuilder().build()
    implicit lazy val link = app.injector.instanceOf[link]
    implicit lazy val request = dataRequest(FakeRequest(), userAnswers)

    lazy val helper: DocumentsAddToListHelper = app.injector.instanceOf[DocumentsAddToListHelper]
  }

  "DocumentsAddToListHelper" - {

    "return nothing" - {

      s"when no answers specified for '${English.lang.code}'" in new Setup() {

        implicit lazy val msgs: Messages = messages(app, English.lang)

        helper.allDocumentsSummary() mustBe Nil
      }
    }

    "return required rows when all answers filled out" - {

      s"when all answers entered '${English.lang.code}' and single transport units" in new Setup(emptyUserAnswers
        .set(DocumentDescriptionPage(0), "description")
        .set(DocumentDescriptionPage(1), "description")

      ) {

        implicit lazy val msgs: Messages = messages(app, English.lang)

        helper.allDocumentsSummary() mustBe Seq(
          SummaryList(
            card = Some(Card(
              title = Some(CardTitle(Text(English.documentCardTitle(0)))),
              actions = Some(Actions(items = Seq(
                ActionItem(
                  href = testOnly.controllers.routes.UnderConstructionController.onPageLoad().url,
                  content = Text(English.remove),
                  visuallyHiddenText = Some(English.documentCardTitle(0)),
                  attributes = Map("id" -> "removeDocuments1")
                )
              ))))),
            rows = Seq(
              DocumentDescriptionSummary.row(0).get
            )
          ),
          SummaryList(
            card = Some(Card(
              title = Some(CardTitle(Text(English.documentCardTitle(1)))),
              actions = Some(Actions(items = Seq(
                ActionItem(
                  href = testOnly.controllers.routes.UnderConstructionController.onPageLoad().url,
                  content = Text(English.remove),
                  visuallyHiddenText = Some(English.documentCardTitle(1)),
                  attributes = Map("id" -> "removeDocuments2")
                )
              ))))),
            rows = Seq(
              DocumentDescriptionSummary.row(1).get
            )
          )
        )
      }
    }
  }
}
