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
import fixtures.messages.sections.items.ItemSmallIndependentProducerMessages
import forms.sections.items.ItemSmallIndependentProducerFormProvider
import models.requests.DataRequest
import models.sections.info.movementScenario.MovementScenario.UkTaxWarehouse
import models.sections.items.ItemSmallIndependentProducerModel
import models.sections.items.ItemSmallIndependentProducerType.SelfCertifiedIndependentSmallProducerAndNotConsignor
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import pages.sections.info.DestinationTypePage
import pages.sections.items.{ItemCommodityCodePage, ItemExciseProductCodePage, ItemSmallIndependentProducerPage}
import play.api.i18n.Messages
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import views.html.sections.items.ItemSmallIndependentProducerView
import views.{BaseSelectors, ViewBehaviours}

class ItemSmallIndependentProducerViewSpec extends SpecBase with ViewBehaviours with ItemFixtures {

  object Selectors extends BaseSelectors

  implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(),
    emptyUserAnswers
      .set(DestinationTypePage, UkTaxWarehouse.GB)
      .set(ItemExciseProductCodePage(testIndex1), testEpcWine)
      .set(ItemCommodityCodePage(testIndex1), testCnCodeWine)
      .set(ItemSmallIndependentProducerPage(testIndex1), ItemSmallIndependentProducerModel(SelfCertifiedIndependentSmallProducerAndNotConsignor, Some(testErn)))
  )

  lazy val view = app.injector.instanceOf[ItemSmallIndependentProducerView]
  val form = app.injector.instanceOf[ItemSmallIndependentProducerFormProvider].apply()

  "ItemSmallIndependentProducer view" - {

    Seq(ItemSmallIndependentProducerMessages.English).foreach { messagesForLanguage =>

      s"when being rendered in lang code of '${messagesForLanguage.lang.code}'" - {

        implicit val msgs: Messages = messages(Seq(messagesForLanguage.lang))

        implicit val doc: Document = Jsoup.parse(view(form, testOnwardRoute, testIndex1).toString())

        behave like pageWithExpectedElementsAndMessages(Seq(
          Selectors.title -> messagesForLanguage.title,
          Selectors.h1 -> messagesForLanguage.heading,
          Selectors.subHeadingCaptionSelector -> messagesForLanguage.itemSection,
          Selectors.radioButton(1) -> messagesForLanguage.certifiedIndependentSmallProducer,
          Selectors.radioButtonHint(1) -> messagesForLanguage.certifiedIndependentSmallProducerHint,
          Selectors.radioButton(2) -> messagesForLanguage.selfCertifiedIndependentSmallProducerAndConsignor,
          Selectors.radioButton(3) -> messagesForLanguage.selfCertifiedIndependentSmallProducerNotConsignor,
          Selectors.label(ItemSmallIndependentProducerFormProvider.producerIdField) -> messagesForLanguage.selfCertifiedIndependentSmallProducerNotConsignorInput,
          Selectors.radioDividerButton(5) -> messagesForLanguage.or,
          Selectors.radioButton(6) -> messagesForLanguage.notAIndependentSmallProducer,
          Selectors.button -> messagesForLanguage.saveAndContinue,
          Selectors.saveAndExitLink -> messagesForLanguage.returnToDraft
        ))
      }
    }
  }
}
