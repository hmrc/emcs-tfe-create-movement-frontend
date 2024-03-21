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

package views.sections.destination

import base.SpecBase
import fixtures.MovementSubmissionFailureFixtures
import fixtures.messages.sections.destination.DestinationWarehouseExciseMessages.English
import forms.sections.destination.DestinationWarehouseExciseFormProvider
import models.NormalMode
import models.requests.DataRequest
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import pages.sections.destination.DestinationWarehouseExcisePage
import play.api.i18n.Messages
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import views.html.sections.destination.DestinationWarehouseExciseView
import views.{BaseSelectors, ViewBehaviours}
import controllers.sections.destination.routes
import play.api.data.FormError

class DestinationWarehouseExciseViewSpec extends SpecBase with ViewBehaviours with MovementSubmissionFailureFixtures {

  lazy val view = app.injector.instanceOf[DestinationWarehouseExciseView]

  object Selectors extends BaseSelectors

  "Destination Warehouse Excise view" - {

    s"when being rendered in lang code of '${English.lang.code}'" - {

      implicit val msgs: Messages = messages(Seq(English.lang))
      implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest())

      val form = app.injector.instanceOf[DestinationWarehouseExciseFormProvider].apply()

      implicit val doc: Document =
        Jsoup.parse(view(
          form = form,
          onSubmitCall = routes.DestinationWarehouseExciseController.onSubmit(request.ern, request.draftId, NormalMode)
        ).toString())

      behave like pageWithExpectedElementsAndMessages(Seq(
        Selectors.title -> English.title,
        Selectors.h1 -> English.heading,
        Selectors.h2(1) -> English.destinationSection,
        Selectors.hint -> English.text,
        Selectors.button -> English.saveAndContinue,
        Selectors.link(1) -> English.returnToDraft
      ))

      behave like pageWithElementsNotPresent(Seq(
        Selectors.notificationBannerTitle,
        Selectors.notificationBannerContent
      ))
    }

    "when ERN needs updating and there is NO form error" - {

      implicit val msgs: Messages = messages(Seq(English.lang))
      implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(),
        emptyUserAnswers.copy(submissionFailures =
          DestinationWarehouseExcisePage.possibleErrors.map(error => destinationWarehouseExciseFailure.copy(error.code))
        )
      )

      val form = app.injector.instanceOf[DestinationWarehouseExciseFormProvider].apply()


      implicit val doc: Document =
        Jsoup.parse(view(
          form = form,
          onSubmitCall = routes.DestinationWarehouseExciseController.onSubmit(request.ern, request.draftId, NormalMode)
        ).toString())

      behave like pageWithExpectedElementsAndMessages(Seq(
        Selectors.title -> English.title,
        Selectors.h1 -> English.heading,
        Selectors.hint -> English.text,
        Selectors.button -> English.saveAndContinue,
        Selectors.link(1) -> English.returnToDraft,
        Selectors.notificationBannerTitle -> English.updateNeeded,
        Selectors.notificationBannerError(1) -> English.exciseIdForTaxWarehouseOfDestinationInvalid,
        Selectors.notificationBannerError(2) -> English.exciseIdForTaxWarehouseOfDestinationNeedsConsignee,
        Selectors.notificationBannerError(3) -> English.exciseIdForTaxWarehouseInvalid,
      ))

    }

    s"when ERN needs updating and there is a form error" - {
      implicit val msgs: Messages = messages(Seq(English.lang))
      implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(),
        emptyUserAnswers.copy(submissionFailures = Seq(destinationWarehouseExciseFailure)
        )
      )

      val form = app.injector.instanceOf[DestinationWarehouseExciseFormProvider].apply()

      implicit val doc: Document =
        Jsoup.parse(view(
          form = form.withError(FormError("key", "msg")),
          onSubmitCall = routes.DestinationWarehouseExciseController.onSubmit(request.ern, request.draftId, NormalMode)
        ).toString())

      "not show the notification banner when there is an error" in {
        doc.select(".govuk-error-summary").isEmpty mustBe false
        doc.select(".govuk-notification-banner").isEmpty mustBe true
      }
    }
  }
}
