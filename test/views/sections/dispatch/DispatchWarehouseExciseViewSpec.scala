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

package views.sections.dispatch

import base.SpecBase
import fixtures.MovementSubmissionFailureFixtures
import fixtures.messages.sections.dispatch.DispatchWarehouseExciseMessages
import forms.sections.dispatch.DispatchWarehouseExciseFormProvider
import models.NormalMode
import models.requests.DataRequest
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import pages.sections.dispatch.DispatchWarehouseExcisePage
import play.api.data.FormError
import play.api.i18n.Messages
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import views.html.sections.dispatch.DispatchWarehouseExciseView
import views.{BaseSelectors, ViewBehaviours}

class DispatchWarehouseExciseViewSpec extends SpecBase with ViewBehaviours with MovementSubmissionFailureFixtures {
  object Selectors extends BaseSelectors

  "DispatchWarehouseExciseView" - {

    Seq(DispatchWarehouseExciseMessages.English).foreach { messagesForLanguage =>

      lazy val view = app.injector.instanceOf[DispatchWarehouseExciseView]

      s"when being rendered in lang code of '${messagesForLanguage.lang.code}'" - {

        implicit val msgs: Messages = messages(Seq(messagesForLanguage.lang))
        implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(), emptyUserAnswers)

        val form = app.injector.instanceOf[DispatchWarehouseExciseFormProvider].apply()

        implicit val doc: Document = Jsoup.parse(view(form, NormalMode).toString())

        behave like pageWithExpectedElementsAndMessages(Seq(
          Selectors.subHeadingCaptionSelector -> messagesForLanguage.dispatchSection,
          Selectors.title -> messagesForLanguage.title,
          Selectors.h1 -> messagesForLanguage.heading,
          Selectors.hint -> messagesForLanguage.hintText,
          Selectors.button -> messagesForLanguage.saveAndContinue,
          Selectors.saveAndExitLink -> messagesForLanguage.returnToDraft
        ))
      }

      s"when there is a submission failure and there is NO form error '${messagesForLanguage.lang.code}'" - {

        implicit val msgs: Messages = messages(Seq(messagesForLanguage.lang))


        implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(),
          emptyUserAnswers.copy(submissionFailures =
            DispatchWarehouseExcisePage.possibleErrors.map(error => dispatchWarehouseInvalidOrMissingOnSeedError.copy(error.code))
          )
        )

        val form = app.injector.instanceOf[DispatchWarehouseExciseFormProvider].apply()

        implicit val doc: Document = Jsoup.parse(view(form, NormalMode).toString())

        behave like pageWithExpectedElementsAndMessages(Seq(
          Selectors.title -> messagesForLanguage.title,
          Selectors.h1 -> messagesForLanguage.heading,
          Selectors.notificationBannerTitle -> messagesForLanguage.updateNeeded,
          Selectors.notificationBannerError(1) -> messagesForLanguage.dispatchWarehouseInvalidOrMissingOnSeedError,
          Selectors.notificationBannerError(2) -> messagesForLanguage.dispatchWarehouseInvalidError,
          Selectors.notificationBannerError(3) -> messagesForLanguage.dispatchWarehouseConsignorDoesNotManageWarehouseError,
          Selectors.subHeadingCaptionSelector -> messagesForLanguage.dispatchSection,
          Selectors.hint -> messagesForLanguage.hintText,
          Selectors.button -> messagesForLanguage.saveAndContinue
        ))
      }

      "when there is a submission failure but there is a form error" - {

        implicit val msgs: Messages = messages(Seq(messagesForLanguage.lang))
        implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(),
          emptyUserAnswers.copy(submissionFailures = Seq(dispatchWarehouseInvalidOrMissingOnSeedError))
        )

        val form = app.injector.instanceOf[DispatchWarehouseExciseFormProvider].apply()

        implicit val doc: Document = Jsoup.parse(view(form.withError(FormError("key", "msg")), NormalMode).toString())

        "not show the notification banner when there is an error" in {
          doc.select(".govuk-error-summary").isEmpty mustBe false
          doc.select(".govuk-notification-banner").isEmpty mustBe true
        }
      }

    }
  }
}

