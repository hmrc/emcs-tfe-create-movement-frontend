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
import fixtures.messages.sections.items.ItemPackagingRemovePackageMessages
import forms.sections.items.ItemPackagingRemovePackageFormProvider
import models.requests.DataRequest
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import pages.sections.items.{ItemPackagingQuantityPage, ItemPackagingShippingMarksPage}
import play.api.i18n.Messages
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import views.html.sections.items.ItemPackagingRemovePackageView
import views.{BaseSelectors, ViewBehaviours}

class ItemPackagingRemovePackageViewSpec extends SpecBase with ViewBehaviours {

  lazy val view = app.injector.instanceOf[ItemPackagingRemovePackageView]
  lazy val form = app.injector.instanceOf[ItemPackagingRemovePackageFormProvider].apply()

  object Selectors extends BaseSelectors

  "ItemPackagingRemovePackageView view" - {

    Seq(ItemPackagingRemovePackageMessages.English).foreach { messagesForLanguage =>

      s"when being rendered in lang code of '${messagesForLanguage.lang.code}'" - {

        implicit val msgs: Messages = messages(Seq(messagesForLanguage.lang))

        "when removing a package that has a shipping mark that is linked to another package" - {

          implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(), emptyUserAnswers
            .set(ItemPackagingShippingMarksPage(testIndex1, testPackagingIndex1), "Mark1")
            .set(ItemPackagingQuantityPage(testIndex1, testPackagingIndex1), "1")
            .set(ItemPackagingShippingMarksPage(testIndex2, testPackagingIndex1), "Mark1")
            .set(ItemPackagingQuantityPage(testIndex2, testPackagingIndex1), "0")
          )

          implicit val doc: Document = Jsoup.parse(view(form, testOnwardRoute, testPackageBag.description, testIndex1, testPackagingIndex1).toString())

          behave like pageWithExpectedElementsAndMessages(Seq(
            Selectors.title -> messagesForLanguage.title,
            Selectors.subHeadingCaptionSelector -> messagesForLanguage.itemSection,
            Selectors.h1 -> messagesForLanguage.heading,
            Selectors.inset -> messagesForLanguage.inset(testIndex1.displayIndex),
            Selectors.p(1) -> messagesForLanguage.p1(testPackageBag.description),
            Selectors.radioButton(1) -> messagesForLanguage.yes,
            Selectors.radioButton(2) -> messagesForLanguage.no,
            Selectors.button -> messagesForLanguage.saveAndContinue,
            Selectors.link(1) -> messagesForLanguage.returnToDraft
          ))
        }

        "when removing a package that DOES NOT have a shipping mark that is linked to another package" - {

          implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest())

          implicit val doc: Document = Jsoup.parse(view(form, testOnwardRoute, testPackageBag.description, testIndex1, testPackagingIndex1).toString())

          behave like pageWithElementsNotPresent(Seq(
            Selectors.inset
          ))
        }
      }
    }
  }
}
