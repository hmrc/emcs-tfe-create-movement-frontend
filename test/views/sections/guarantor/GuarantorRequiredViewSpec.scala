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

package views.sections.guarantor

import base.SpecBase
import fixtures.messages.sections.guarantor.GuarantorRequiredMessages.English
import forms.sections.guarantor.GuarantorRequiredFormProvider
import models.requests.DataRequest
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.i18n.Messages
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import views.html.sections.guarantor.GuarantorRequiredView
import views.{BaseSelectors, ViewBehaviours}

class GuarantorRequiredViewSpec extends SpecBase with ViewBehaviours {

  object Selectors extends BaseSelectors {
    val insetText = ".govuk-inset-text"
    val enterDetailsButton = "#enter-details"
  }

  "Guarantor Required view" - {

    s"when the guarantor always required" - {

      implicit val msgs: Messages = messages(Seq(English.lang))
      implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest())

     lazy val view = app.injector.instanceOf[GuarantorRequiredView]
      val form = app.injector.instanceOf[GuarantorRequiredFormProvider].apply()

      implicit val doc: Document = Jsoup.parse(view(form, testOnwardRoute, requiredGuarantee = true).toString())

      behave like pageWithExpectedElementsAndMessages(Seq(
        Selectors.title -> English.isRequiredTitle,
        Selectors.h1 -> English.isRequiredHeading,
        Selectors.h2(1) -> English.guarantorSection,
        Selectors.p(1) -> English.isRequiredP1,
        Selectors.p(2) -> English.p2,
        Selectors.p(3) -> English.p3Link,
        Selectors.insetText -> English.inset,
        Selectors.enterDetailsButton -> English.isRequiredEnterDetails
      ))

      "have links with the correct url" in {
        doc.select(Selectors.link(1)).attr("href") mustBe appConfig.exciseWarehouseGuidanceUrl
        doc.select(Selectors.enterDetailsButton).attr("href") mustBe testOnwardRoute.url
      }
    }

    s"when the guarantor always required for NI to EU" - {

      implicit val msgs: Messages = messages(Seq(English.lang))
      implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest())

     lazy val view = app.injector.instanceOf[GuarantorRequiredView]
      val form = app.injector.instanceOf[GuarantorRequiredFormProvider].apply()

      implicit val doc: Document = Jsoup.parse(view(form, testOnwardRoute, requiredGuaranteeNIToEU = true).toString())

      behave like pageWithExpectedElementsAndMessages(Seq(
        Selectors.title -> English.isRequiredTitle,
        Selectors.h1 -> English.isRequiredHeading,
        Selectors.h2(1) -> English.guarantorSection,
        Selectors.p(1) -> English.isRequiredNIToEUP1,
        Selectors.p(2) -> English.p2,
        Selectors.p(3) -> English.p3Link,
        Selectors.insetText -> English.inset,
        Selectors.enterDetailsButton -> English.isRequiredEnterDetails
      ))

      "have links with the correct url" in {
        doc.select(Selectors.link(1)).attr("href") mustBe appConfig.exciseWarehouseGuidanceUrl
        doc.select(Selectors.enterDetailsButton).attr("href") mustBe testOnwardRoute.url
      }
    }

    s"when the guarantor is not always required" - {

      implicit val msgs: Messages = messages(Seq(English.lang))
      implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest())

     lazy val view = app.injector.instanceOf[GuarantorRequiredView]
      val form = app.injector.instanceOf[GuarantorRequiredFormProvider].apply()

      implicit val doc: Document = Jsoup.parse(view(form, testOnwardRoute).toString())

      behave like pageWithExpectedElementsAndMessages(Seq(
        Selectors.title -> English.title,
        Selectors.h1 -> English.heading,
        Selectors.h2(1) -> English.guarantorSection,
        Selectors.p(1) -> English.p1,
        Selectors.p(2) -> English.p2,
        Selectors.p(3) -> English.p3Link,
        Selectors.insetText -> English.inset,
        Selectors.h2(2) -> English.h2,
        Selectors.p(4) -> English.p4,
        Selectors.bullet(1) -> English.bullet1,
        Selectors.bullet(2) -> English.bullet2,
        Selectors.h2(3) -> English.question,
        Selectors.radioButton(1) -> English.yes,
        Selectors.radioButton(2) -> English.no,
        Selectors.button -> English.saveAndContinue,
        Selectors.saveAndExitLink -> English.returnToDraft
      ))

      "have links with the correct url" in {
        doc.select(Selectors.link(1)).attr("href") mustBe appConfig.exciseWarehouseGuidanceUrl
      }
    }
  }
}
