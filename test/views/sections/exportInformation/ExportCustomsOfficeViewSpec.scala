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

package views.sections.exportInformation

import base.SpecBase
import fixtures.MovementSubmissionFailureFixtures
import fixtures.messages.sections.exportInformation.ExportCustomsOfficeMessages
import forms.sections.exportInformation.ExportCustomsOfficeFormProvider
import models.requests.DataRequest
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.data.FormError
import play.api.i18n.{Lang, Messages}
import play.api.test.FakeRequest
import utils.ExportCustomsOfficeNumberError
import views.html.sections.exportInformation.ExportCustomsOfficeView
import views.{BaseSelectors, ViewBehaviours}

class ExportCustomsOfficeViewSpec extends SpecBase with ViewBehaviours with MovementSubmissionFailureFixtures {

  class Fixture(lang: Lang, euExport: Boolean) {

    implicit lazy val msgs: Messages = messages(Seq(lang))
    implicit lazy val request: DataRequest[_] = dataRequest(FakeRequest(), emptyUserAnswers)

    lazy val view = app.injector.instanceOf[ExportCustomsOfficeView]
    lazy val form = app.injector.instanceOf[ExportCustomsOfficeFormProvider].apply()

    implicit def doc(isFormError: Boolean = false)(implicit request: DataRequest[_]): Document = Jsoup.parse(view(
      form = if(isFormError) form.withError(FormError("key", "msg")) else form,
      action = testOnwardRoute,
      euExport
    ).toString())
  }

  object Selectors extends BaseSelectors

  "ExportCustomsOfficeView" - {

    Seq(ExportCustomsOfficeMessages.English).foreach { messagesForLanguage =>

      s"when being rendered in lang code of '${messagesForLanguage.lang.code}'" - {

        Seq(true, false).foreach { euExport =>

          s"when EU export is '$euExport'" - new Fixture(messagesForLanguage.lang, euExport) {

            behave like pageWithExpectedElementsAndMessages(Seq(
              Selectors.title -> messagesForLanguage.title,
              Selectors.h1 -> messagesForLanguage.heading,
              Selectors.subHeadingCaptionSelector -> messagesForLanguage.exportInformationSection,
              Selectors.hint -> messagesForLanguage.hint(euExport),
              Selectors.button -> messagesForLanguage.saveAndContinue,
              Selectors.saveAndExitLink -> messagesForLanguage.returnToDraft
            ))(doc())

            behave like pageWithElementsNotPresent(Seq(
              Selectors.notificationBannerTitle,
              Selectors.notificationBannerContent
            ))(doc())
          }
        }

        "when there is a 704 error" - new Fixture(messagesForLanguage.lang, true) {

          override implicit lazy val request: DataRequest[_] = dataRequest(FakeRequest(), emptyUserAnswers
            .copy(submissionFailures = Seq(movementSubmissionFailure.copy(errorType = ExportCustomsOfficeNumberError.code, hasBeenFixed = false))))

          behave like pageWithExpectedElementsAndMessages(Seq(
            Selectors.title -> messagesForLanguage.title,
            Selectors.subHeadingCaptionSelector -> messagesForLanguage.exportInformationSection,
            Selectors.h1 -> messagesForLanguage.heading,
            Selectors.notificationBannerTitle -> messagesForLanguage.updateNeeded,
            Selectors.notificationBannerContent -> messagesForLanguage.submissionFailureError
          ))(doc())

          "not show the notification banner when there is an error" - {
            doc(isFormError = true).select(".govuk-error-summary").isEmpty mustBe false
            doc(isFormError = true).select(".govuk-notification-banner").isEmpty mustBe true
          }
        }
      }
    }
  }
}

