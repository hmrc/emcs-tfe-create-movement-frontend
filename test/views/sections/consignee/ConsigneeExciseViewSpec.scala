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

package views.sections.consignee

import base.SpecBase
import fixtures.MovementSubmissionFailureFixtures
import fixtures.messages.sections.consignee.ConsigneeExciseMessages.English
import forms.sections.consignee.ConsigneeExciseFormProvider
import models.requests.DataRequest
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import pages.sections.consignee.ConsigneeExcisePage
import play.api.data.FormError
import play.api.i18n.Messages
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import views.html.sections.consignee.ConsigneeExciseView
import views.{BaseSelectors, ViewBehaviours}

class ConsigneeExciseViewSpec extends SpecBase with ViewBehaviours with MovementSubmissionFailureFixtures {
  object Selectors extends BaseSelectors

  "Consignee Excise view" - {

    s"when being rendered in lang code of '${English.lang.code}'" - {

      implicit val msgs: Messages = messages(Seq(English.lang))
      implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(), emptyUserAnswers)

      lazy val view = app.injector.instanceOf[ConsigneeExciseView]
      val form = app.injector.instanceOf[ConsigneeExciseFormProvider].apply(None)

      implicit val doc: Document = Jsoup.parse(
        view(
          form,
          testOnwardRoute,
          isNorthernIrishTemporaryRegisteredConsignee = false,
          isNorthernIrishTemporaryCertifiedConsignee = false
        ).toString()
      )

      behave like pageWithExpectedElementsAndMessages(Seq(
        Selectors.title -> English.title,
        Selectors.h1 -> English.heading,
        Selectors.h2(1) -> English.consigneeInformationSection,
        Selectors.hint -> English.hint,
        Selectors.button -> English.saveAndContinue
      ))

      behave like pageWithElementsNotPresent(Seq(
        Selectors.notificationBannerTitle,
        Selectors.notificationBannerContent
      ))
    }

    s"when ERN needs updating and there is NO form error" - {

      implicit val msgs: Messages = messages(Seq(English.lang))

      implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(),
        emptyUserAnswers.copy(submissionFailures =
          ConsigneeExcisePage.possibleErrors.map(error => consigneeExciseFailure.copy(error.code))
        )
      )

      lazy val view = app.injector.instanceOf[ConsigneeExciseView]
      val form = app.injector.instanceOf[ConsigneeExciseFormProvider].apply(None)

      implicit val doc: Document = Jsoup.parse(
        view(
          form = form,
          testOnwardRoute,
          isNorthernIrishTemporaryRegisteredConsignee = false,
          isNorthernIrishTemporaryCertifiedConsignee = false
        ).toString()
      )

      behave like pageWithExpectedElementsAndMessages(Seq(
        Selectors.title -> English.title,
        Selectors.h1 -> English.heading,
        Selectors.notificationBannerTitle -> English.updateNeeded,
        Selectors.notificationBannerError(1) -> English.invalidOrMissingConsignee,
        Selectors.notificationBannerError(2) -> English.linkIsPending,
        Selectors.notificationBannerError(3) -> English.linkIsAlreadyUsed,
        Selectors.notificationBannerError(4) -> English.linkIsWithdrawn,
        Selectors.notificationBannerError(5) -> English.linkIsCancelled,
        Selectors.notificationBannerError(6) -> English.linkIsExpired,
        Selectors.notificationBannerError(7) -> English.linkMissingOrInvalid,
        Selectors.notificationBannerError(8) -> English.directDeliveryNotAllowed,
        Selectors.notificationBannerError(9) -> English.consignorNotAuthorised,
        Selectors.notificationBannerError(10) -> English.registeredConsignorToRegisteredConsignee,
        Selectors.notificationBannerError(11) -> English.consigneeRoleInvalid,
        Selectors.subHeadingCaptionSelector -> English.consigneeInformationSection,
        Selectors.hint -> English.hint,
        Selectors.button -> English.saveAndContinue
      ))
    }

    s"when ERN needs updating and there is a form error" - {

      implicit val msgs: Messages = messages(Seq(English.lang))
      implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(),
        emptyUserAnswers.copy(submissionFailures = Seq(consigneeExciseFailure))
      )

      lazy val view = app.injector.instanceOf[ConsigneeExciseView]
      val form = app.injector.instanceOf[ConsigneeExciseFormProvider].apply(None)

      implicit val doc: Document = Jsoup.parse(
        view(
          form = form.withError(FormError("key", "msg")),
          testOnwardRoute,
          isNorthernIrishTemporaryRegisteredConsignee = false,
          isNorthernIrishTemporaryCertifiedConsignee = false
        ).toString()
      )

      "not show the notification banner when there is an error" in {
        doc.select(".govuk-error-summary").isEmpty mustBe false
        doc.select(".govuk-notification-banner").isEmpty mustBe true
      }
    }

    s"when being rendered in lang code of '${English.lang.code}' when isNorthernIrishTemporaryRegisteredConsignee is true" - {

      implicit val msgs: Messages = messages(Seq(English.lang))
      implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(), emptyUserAnswers)

      lazy val view = app.injector.instanceOf[ConsigneeExciseView]
      val form = app.injector.instanceOf[ConsigneeExciseFormProvider].apply(None)

      implicit val doc: Document = Jsoup.parse(
        view(
          form,
          testOnwardRoute,
          isNorthernIrishTemporaryRegisteredConsignee = true,
          isNorthernIrishTemporaryCertifiedConsignee = false
        ).toString()
      )

      behave like pageWithExpectedElementsAndMessages(Seq(
        Selectors.title -> English.temporaryRegisteredConsigneeTitle,
        Selectors.h1 -> English.temporaryRegisteredConsigneeHeading,
        Selectors.h2(1) -> English.consigneeInformationSection,
        Selectors.hint -> English.temporaryRegisteredConsigneeHint,
        Selectors.button -> English.saveAndContinue
      ))

      behave like pageWithElementsNotPresent(Seq(
        Selectors.notificationBannerTitle,
        Selectors.notificationBannerContent
      ))
    }

    s"when being rendered in lang code of '${English.lang.code}' when isNorthernIrishTemporaryCertifiedConsignee is true" - {

      implicit val msgs: Messages = messages(Seq(English.lang))
      implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(), emptyUserAnswers)

      lazy val view = app.injector.instanceOf[ConsigneeExciseView]
      val form = app.injector.instanceOf[ConsigneeExciseFormProvider].apply(None)

      implicit val doc: Document = Jsoup.parse(
        view(
          form,
          testOnwardRoute,
          isNorthernIrishTemporaryRegisteredConsignee = false,
          isNorthernIrishTemporaryCertifiedConsignee = true
        ).toString()
      )

      behave like pageWithExpectedElementsAndMessages(Seq(
        Selectors.title -> English.temporaryCertifiedConsigneeTitle,
        Selectors.h1 -> English.temporaryCertifiedConsigneeHeading,
        Selectors.h2(1) -> English.consigneeInformationSection,
        Selectors.hint -> English.temporaryCertifiedConsigneeHint,
        Selectors.button -> English.saveAndContinue
      ))

      behave like pageWithElementsNotPresent(Seq(
        Selectors.notificationBannerTitle,
        Selectors.notificationBannerContent
      ))
    }
  }
}
