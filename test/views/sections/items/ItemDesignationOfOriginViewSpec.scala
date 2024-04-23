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
import fixtures.messages.sections.items.ItemDesignationOfOriginMessages
import forms.sections.items.ItemDesignationOfOriginFormProvider
import models.requests.DataRequest
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.i18n.Messages
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import views.html.sections.items.ItemDesignationOfOriginView
import views.{BaseSelectors, ViewBehaviours}

class ItemDesignationOfOriginViewSpec extends SpecBase with ViewBehaviours with ItemFixtures {

  object Selectors extends BaseSelectors {

    //This is a awkward way of getting the legends, nth-of-type returns 2 elements despite
    //specifying an index, this is due to the behaviour of nth-of-type (and its relative selection)
    val geographicalIndicationFieldset = "[aria-describedBy=\"geographicalIndication-hint\"]"
    val isSpiritMarketedAndLabelledFieldset = "[aria-describedBy=\"isSpiritMarketedAndLabelled-hint\"]"

    val geographicalIndicationLegend = s"$geographicalIndicationFieldset > legend"
    val isSpiritMarketedAndLabelledLegend = s"$isSpiritMarketedAndLabelledFieldset > legend"

    val geographicalIndicationHint = "#geographicalIndication-hint"

    val isSpiritMarketingAndLabellingHint = "#isSpiritMarketedAndLabelled-hint"

    def radioButton(section: String, radioIndex: Int) = s"$section .govuk-radios > div:nth-child($radioIndex) > label"

    def radioDividerButton(section: String, radioIndex: Int) = s"$section .govuk-radios > div:nth-child($radioIndex)"
  }

  lazy val view = app.injector.instanceOf[ItemDesignationOfOriginView]

  lazy val formProvider = app.injector.instanceOf[ItemDesignationOfOriginFormProvider]

  //scalastyle:off
  "ItemDesignationOfOriginView" - {

    Seq(ItemDesignationOfOriginMessages.English).foreach { messagesForLanguage =>

      s"when being rendered in lang code of '${messagesForLanguage.lang.code}'" - {

        implicit val msgs: Messages = messages(Seq(messagesForLanguage.lang))
        implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(), emptyUserAnswers)

        "for non-S200 EPC" - {

          val form = formProvider.apply(testEpcWine)

          implicit val doc: Document = Jsoup.parse(view(form, testOnwardRoute, testEpcWine, testIndex1).toString())

          behave like pageWithExpectedElementsAndMessages(Seq(
            Selectors.title -> messagesForLanguage.title,
            Selectors.subHeadingCaptionSelector -> messagesForLanguage.itemSection,
            Selectors.h1 -> messagesForLanguage.heading,
            Selectors.geographicalIndicationHint -> messagesForLanguage.geographicalIndicationHint,
            Selectors.radioButton(1) -> messagesForLanguage.pdoRadio,
            Selectors.label(ItemDesignationOfOriginFormProvider.protectedDesignationOfOriginTextField) -> messagesForLanguage.pdoInput,
            Selectors.radioButton(3) -> messagesForLanguage.pgiRadio,
            Selectors.label(ItemDesignationOfOriginFormProvider.protectedGeographicalIndicationTextField) -> messagesForLanguage.pgiInput,
            Selectors.radioDividerButton(5) -> messagesForLanguage.or,
            Selectors.radioButton(6) -> messagesForLanguage.noGiRadio,
            Selectors.button -> messagesForLanguage.saveAndContinue,
            Selectors.saveAndExitLink -> messagesForLanguage.returnToDraft
          ))
        }

        "for S200 EPC" - {

          val form = formProvider.apply(testEpcSpirit)

          implicit val doc: Document = Jsoup.parse(view(form, testOnwardRoute, testEpcSpirit, testIndex1).toString())

          behave like pageWithExpectedElementsAndMessages(Seq(
            Selectors.title -> messagesForLanguage.titleS200,
            Selectors.subHeadingCaptionSelector -> messagesForLanguage.itemSection,
            Selectors.geographicalIndicationLegend -> messagesForLanguage.designationOfOriginLegendS200,
            Selectors.geographicalIndicationHint -> messagesForLanguage.geographicalIndicationHint,
            Selectors.radioButton(Selectors.geographicalIndicationFieldset, 1) -> messagesForLanguage.pdoRadio,
            Selectors.label(ItemDesignationOfOriginFormProvider.protectedDesignationOfOriginTextField) -> messagesForLanguage.pdoInput,
            Selectors.radioButton(Selectors.geographicalIndicationFieldset, 3) -> messagesForLanguage.pgiRadio,
            Selectors.label(ItemDesignationOfOriginFormProvider.protectedGeographicalIndicationTextField) -> messagesForLanguage.pgiInput,
            Selectors.radioDividerButton(Selectors.geographicalIndicationFieldset, 5) -> messagesForLanguage.or,
            Selectors.radioButton(Selectors.geographicalIndicationFieldset, 6) -> messagesForLanguage.noGiRadio,
            Selectors.isSpiritMarketedAndLabelledLegend -> messagesForLanguage.spiritMarketingAndLabellingLegend,
            Selectors.isSpiritMarketingAndLabellingHint -> messagesForLanguage.spiritMarketingAndLabellingHint,
            Selectors.radioButton(Selectors.isSpiritMarketedAndLabelledFieldset, 1) -> messagesForLanguage.s200YesRadio,
            Selectors.radioDividerButton(Selectors.isSpiritMarketedAndLabelledFieldset, 2) -> messagesForLanguage.or,
            Selectors.radioButton(Selectors.isSpiritMarketedAndLabelledFieldset, 3) -> messagesForLanguage.s200NoRadio,
            Selectors.button -> messagesForLanguage.saveAndContinue,
            Selectors.saveAndExitLink -> messagesForLanguage.returnToDraft
          ))
        }
      }
    }
  }
}
