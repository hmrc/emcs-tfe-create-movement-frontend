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

package views.sections.items

import base.SpecBase
import fixtures.MovementSubmissionFailureFixtures
import fixtures.messages.UnitOfMeasureMessages
import fixtures.messages.sections.items.ItemQuantityMessages
import forms.sections.items.ItemQuantityFormProvider
import models.GoodsType.Wine
import models.UnitOfMeasure.Litres15
import models.requests.DataRequest
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.data.FormError
import play.api.i18n.Messages
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import views.html.sections.items.ItemQuantityView
import views.{BaseSelectors, ViewBehaviours}

class ItemQuantityViewSpec extends SpecBase with ViewBehaviours with MovementSubmissionFailureFixtures {

  object Selectors extends BaseSelectors

  "ItemQuantity view" - {

    Seq(ItemQuantityMessages.English -> UnitOfMeasureMessages.English).foreach { case (messagesForLanguage, unitOfMeasureMessages) =>

      s"when being rendered in lang code of '${messagesForLanguage.lang.code}'" - {

        implicit val msgs: Messages = messages(Seq(messagesForLanguage.lang))
        implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest())

       lazy val view = app.injector.instanceOf[ItemQuantityView]
        val form = app.injector.instanceOf[ItemQuantityFormProvider].apply(testIndex1)

        implicit def doc(isFormError: Boolean = false)(implicit request: DataRequest[_]): Document = Jsoup.parse(view(
          if(isFormError) form.withError(FormError("key", "msg")) else form,
          testOnwardRoute,
          Wine,
          Litres15,
          testIndex1
        ).toString())

        behave like pageWithExpectedElementsAndMessages(Seq(
          Selectors.title -> messagesForLanguage.title(Wine.toSingularOutput()),
          Selectors.h1 -> messagesForLanguage.heading(Wine.toSingularOutput()),
          Selectors.subHeadingCaptionSelector -> messagesForLanguage.itemSection,
          Selectors.hint -> messagesForLanguage.hint(unitOfMeasureMessages.litres15Long),
          Selectors.inputSuffix -> unitOfMeasureMessages.litres15Short,
          Selectors.button -> messagesForLanguage.saveAndContinue,
          Selectors.link(1) -> messagesForLanguage.returnToDraft
        ))(doc())

        behave like pageWithElementsNotPresent(Seq(
          Selectors.notificationBannerTitle,
          Selectors.notificationBannerContent
        ))(doc())

        "when there is a 704 error" - {

          implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(), emptyUserAnswers
            .copy(submissionFailures = Seq(itemQuantityFailure(1))))

          behave like pageWithExpectedElementsAndMessages(Seq(
            Selectors.title -> messagesForLanguage.title(Wine.toSingularOutput()),
            Selectors.subHeadingCaptionSelector -> messagesForLanguage.itemSection,
            Selectors.h1 -> messagesForLanguage.heading(Wine.toSingularOutput()),
            Selectors.notificationBannerTitle -> messagesForLanguage.updateNeeded,
            Selectors.notificationBannerContent -> messagesForLanguage.quantitySubmissionFailure
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
