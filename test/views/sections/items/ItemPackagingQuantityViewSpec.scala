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
import fixtures.ItemFixtures
import fixtures.messages.sections.items.ItemPackagingQuantityMessages
import forms.sections.items.ItemPackagingQuantityFormProvider
import models.requests.DataRequest
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.i18n.Messages
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import views.html.sections.items.ItemPackagingQuantityView
import views.{BaseSelectors, ViewBehaviours}

class ItemPackagingQuantityViewSpec extends SpecBase with ViewBehaviours with ItemFixtures {

  object Selectors extends BaseSelectors {

    def detailsP(pIndex: Int) = s"main details p:nth-of-type($pIndex)"

    def detailsBullet(bulletIndex: Int) = s"main details .govuk-list--bullet li:nth-of-type($bulletIndex)"
  }

  "ItemPackagingQuantity view" - {

    Seq(ItemPackagingQuantityMessages.English).foreach { messagesForLanguage =>

      s"when being rendered in lang code of '${messagesForLanguage.lang.code}'" - {

        implicit val msgs: Messages = messages(Seq(messagesForLanguage.lang))
        implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest())

        lazy val view = app.injector.instanceOf[ItemPackagingQuantityView]
        val form = app.injector.instanceOf[ItemPackagingQuantityFormProvider].apply(testIndex1, testPackagingIndex2)

        implicit val doc: Document = Jsoup.parse(view(form, testOnwardRoute, testItemPackagingTypes.head, testPackagingIndex2, testIndex1).toString())

        behave like pageWithExpectedElementsAndMessages(Seq(
          Selectors.title -> messagesForLanguage.title(testIndex1.displayIndex),
          Selectors.subHeadingCaptionSelector -> messagesForLanguage.itemSection,
          Selectors.h1 -> messagesForLanguage.heading(testIndex1.displayIndex),
          Selectors.hint -> messagesForLanguage.hint(testPackagingIndex2.displayIndex.toInt, testIndex1.displayIndex.toInt, testItemPackagingTypes.head.description),
          Selectors.summary(1) -> messagesForLanguage.summary,
          Selectors.detailsP(1) -> messagesForLanguage.detailsP1,
          Selectors.detailsP(2) -> messagesForLanguage.detailsP2,
          Selectors.detailsBullet(1) -> messagesForLanguage.detailsBullet1,
          Selectors.detailsBullet(2) -> messagesForLanguage.detailsBullet2,
          Selectors.button -> messagesForLanguage.saveAndContinue,
          Selectors.link(1) -> messagesForLanguage.returnToDraft
        ))
      }
    }
  }
}
