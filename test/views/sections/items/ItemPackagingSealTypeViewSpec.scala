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
import fixtures.messages.sections.items.ItemPackagingSealTypeMessages
import forms.sections.items.ItemPackagingSealTypeFormProvider
import models.requests.DataRequest
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.i18n.Messages
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import views.html.sections.items.ItemPackagingSealTypeView
import views.{BaseSelectors, ViewBehaviours}

class ItemPackagingSealTypeViewSpec extends SpecBase with ViewBehaviours {

  object Selectors extends BaseSelectors {

    val packagingSealTypeHint = "#packaging-seal-type-hint"

    val packagingSealInformationHint = "#packaging-seal-information-hint"
  }

  lazy val view = app.injector.instanceOf[ItemPackagingSealTypeView]
  val form = app.injector.instanceOf[ItemPackagingSealTypeFormProvider].apply()

  implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest())

  "ItemPackagingSealType view" - {

    Seq(ItemPackagingSealTypeMessages.English).foreach { messagesForLanguage =>

      s"when being rendered in lang code of '${messagesForLanguage.lang.code}'" - {

        implicit val msgs: Messages = messages(Seq(messagesForLanguage.lang))

        "for the individual packaging page variation" - {

          implicit val doc: Document = Jsoup.parse(view(
            form,
            testOnwardRoute,
            itemIndex = Some(testIndex2),
            packagingIndex = Some(testPackagingIndex1),
            packagingTypeDescription = "Aerosol",
            optPackagingQuantity = Some("2")
          ).toString())

          behave like pageWithExpectedElementsAndMessages(Seq(
            Selectors.title -> messagesForLanguage.title,
            Selectors.h1 -> messagesForLanguage.heading,
            Selectors.subHeadingCaptionSelector -> messagesForLanguage.itemSection,
            Selectors.p(1) -> messagesForLanguage.p("Aerosol", "2", testIndex2, testPackagingIndex1),
            Selectors.label("packaging-seal-type") -> messagesForLanguage.textAreaLabel,
            Selectors.packagingSealTypeHint -> messagesForLanguage.sealTypeHint,
            Selectors.label("packaging-seal-information") -> messagesForLanguage.p2,
            Selectors.packagingSealInformationHint -> messagesForLanguage.sealInformationHint,
            Selectors.button -> messagesForLanguage.saveAndContinue,
            Selectors.link(1) -> messagesForLanguage.returnToDraft
          ))
        }

        "for the bulk packaging page variation" - {

          implicit val doc: Document = Jsoup.parse(view(
            form,
            testOnwardRoute,
            itemIndex = None,
            packagingIndex = None,
            packagingTypeDescription = "Aerosol",
            optPackagingQuantity = None
          ).toString())

          behave like pageWithExpectedElementsAndMessages(Seq(
            Selectors.title -> messagesForLanguage.title,
            Selectors.h1 -> messagesForLanguage.heading,
            Selectors.subHeadingCaptionSelector -> messagesForLanguage.itemSection,
            Selectors.label("packaging-seal-type") -> messagesForLanguage.textAreaLabel,
            Selectors.packagingSealTypeHint -> messagesForLanguage.sealTypeHint,
            Selectors.label("packaging-seal-information") -> messagesForLanguage.p2,
            Selectors.packagingSealInformationHint -> messagesForLanguage.sealInformationHint,
            Selectors.button -> messagesForLanguage.saveAndContinue,
            Selectors.link(1) -> messagesForLanguage.returnToDraft
          ))

          behave like pageWithElementsNotPresent(Seq(
            Selectors.p(1)
          ))
        }
      }
    }
  }
}
