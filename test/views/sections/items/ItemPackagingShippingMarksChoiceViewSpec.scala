/*
 * Copyright 2024 HM Revenue & Customs
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
import fixtures.ItemFixtures
import fixtures.messages.sections.items.ItemPackagingShippingMarksChoiceMessages
import forms.sections.items.ItemPackagingShippingMarksChoiceFormProvider
import models.requests.DataRequest
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import pages.sections.items.ItemPackagingQuantityPage
import play.api.i18n.Messages
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import views.html.sections.items.ItemPackagingShippingMarksChoiceView
import views.{BaseSelectors, ViewBehaviours}

class ItemPackagingShippingMarksChoiceViewSpec extends SpecBase with ViewBehaviours with ItemFixtures {

  object Selectors extends BaseSelectors {

    def detailsP(pIndex: Int) = s"main details p:nth-of-type($pIndex)"

    def detailsBullet(bulletIndex: Int) = s"main details .govuk-list--bullet li:nth-of-type($bulletIndex)"
  }

  lazy val view = app.injector.instanceOf[ItemPackagingShippingMarksChoiceView]

  lazy val form = new ItemPackagingShippingMarksChoiceFormProvider()()

  "Item Packaging Shipping Marks Choice view" - {

    Seq(ItemPackagingShippingMarksChoiceMessages.English).foreach { messagesForLanguage =>

      s"when being rendered in lang code of '${messagesForLanguage.lang.code}'" - {

        "when the quantity is 0" - {

          implicit val msgs: Messages = messages(Seq(messagesForLanguage.lang))
          implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(),
            emptyUserAnswers.set(ItemPackagingQuantityPage(testIndex1, testPackagingIndex1), "0"))

          implicit val doc: Document = Jsoup.parse(view(
            form,
            testIndex1,
            testPackagingIndex1,
            testPackageBag.description,
            packagingQuantity = "0",
            testOnwardRoute
          ).toString())

          behave like pageWithExpectedElementsAndMessages(Seq(
            Selectors.h2(1) -> messagesForLanguage.itemSection,
            Selectors.hiddenText -> messagesForLanguage.hiddenSectionContent,
            Selectors.title -> messagesForLanguage.title,
            Selectors.h1 -> messagesForLanguage.heading,
            Selectors.p(1) -> messagesForLanguage.p1(quantity = "0"),
            Selectors.p(2) -> messagesForLanguage.p2,
            Selectors.summary(1) -> messagesForLanguage.detailsSummary,
            Selectors.detailsP(1) -> messagesForLanguage.detailsP1,
            Selectors.detailsBullet(1) -> messagesForLanguage.detailsBullet1,
            Selectors.detailsBullet(2) -> messagesForLanguage.detailsBullet2,
            Selectors.detailsBullet(3) -> messagesForLanguage.detailsBullet3,
            Selectors.legend -> messagesForLanguage.legend,
            Selectors.radioButton(1) -> messagesForLanguage.yesSelectExistingShippingMark,
            Selectors.radioButton(2) -> messagesForLanguage.no,
            Selectors.radioButtonHint(2) -> messagesForLanguage.noHint(testIndex1.displayIndex.toInt),
            Selectors.button -> messagesForLanguage.saveAndContinue,
            Selectors.link(1) -> messagesForLanguage.returnToDraft
          ))
        }

        "when the quantity is > 0" - {

          implicit val msgs: Messages = messages(Seq(messagesForLanguage.lang))
          implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(),
            emptyUserAnswers.set(ItemPackagingQuantityPage(testIndex1, testPackagingIndex1), "1"))

          implicit val doc: Document = Jsoup.parse(view(
            form,
            testIndex1,
            testPackagingIndex1,
            testPackageBag.description,
            packagingQuantity = "1",
            testOnwardRoute
          ).toString())

          behave like pageWithExpectedElementsAndMessages(Seq(
            Selectors.h2(1) -> messagesForLanguage.itemSection,
            Selectors.hiddenText -> messagesForLanguage.hiddenSectionContent,
            Selectors.title -> messagesForLanguage.title,
            Selectors.h1 -> messagesForLanguage.heading,
            Selectors.p(1) -> messagesForLanguage.p1(quantity = "1"),
            Selectors.p(2) -> messagesForLanguage.p2,
            Selectors.summary(1) -> messagesForLanguage.detailsSummary,
            Selectors.detailsP(1) -> messagesForLanguage.detailsP1,
            Selectors.detailsBullet(1) -> messagesForLanguage.detailsBullet1,
            Selectors.detailsBullet(2) -> messagesForLanguage.detailsBullet2,
            Selectors.detailsBullet(3) -> messagesForLanguage.detailsBullet3,
            Selectors.legend -> messagesForLanguage.legend,
            Selectors.radioButton(1) -> messagesForLanguage.yes,
            Selectors.radioButton(2) -> messagesForLanguage.no,
            Selectors.button -> messagesForLanguage.saveAndContinue,
            Selectors.link(1) -> messagesForLanguage.returnToDraft
          ))

          "must not have a hint on the 'No' radio button" - {

            behave like pageWithElementsNotPresent(Seq(Selectors.radioButtonHint(2)))
          }
        }
      }
    }
  }
}
