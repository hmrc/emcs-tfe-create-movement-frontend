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

package viewmodels.helpers

import base.SpecBase
import fixtures.ItemFixtures
import forms.sections.items.ItemDesignationOfOriginFormProvider
import forms.sections.items.ItemDesignationOfOriginFormProvider.{protectedDesignationOfOriginTextField, protectedGeographicalIndicationTextField}
import models.sections.items.ItemGeographicalIndicationType.{NoGeographicalIndication, ProtectedDesignationOfOrigin, ProtectedGeographicalIndication}
import models.sections.items.{ItemDesignationOfOriginModel, ItemGeographicalIndicationType}
import play.api.data.Form
import play.api.i18n.Messages
import play.api.test.FakeRequest
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.govukfrontend.views.Aliases.{RadioItem, Text}
import uk.gov.hmrc.govukfrontend.views.html.components.GovukCharacterCount
import uk.gov.hmrc.govukfrontend.views.viewmodels.charactercount.CharacterCount
import viewmodels.govuk.LabelFluency

class ItemDesignationOfOriginHelperSpec extends SpecBase with ItemFixtures with LabelFluency {

  val govukCharacterCount: GovukCharacterCount = app.injector.instanceOf[GovukCharacterCount]
  val helper: ItemDesignationOfOriginHelper = app.injector.instanceOf[ItemDesignationOfOriginHelper]
  val form: Form[ItemDesignationOfOriginModel] = new ItemDesignationOfOriginFormProvider()(testEpcSpirit)

  implicit val msgs: Messages = messages(FakeRequest())

  ".geographicalIndicationIdentificationTextBox" - {

    Seq(
      ProtectedDesignationOfOrigin -> protectedDesignationOfOriginTextField,
      ProtectedGeographicalIndication -> protectedGeographicalIndicationTextField
    ).foreach { geographicalIndicationAndFormField =>

      s"should render the correct character count component for ${geographicalIndicationAndFormField._1}" in {

        helper.geographicalIndicationIdentificationTextBox(geographicalIndicationAndFormField._1, form) mustBe govukCharacterCount(CharacterCount(
          id = geographicalIndicationAndFormField._2,
          name = geographicalIndicationAndFormField._2,
          maxLength = Some(50),
          label = LabelViewModel(Text(msgs(s"itemDesignationOfOrigin.${geographicalIndicationAndFormField._1}.input"))),
          value = form(geographicalIndicationAndFormField._2).value,
          errorMessage = None
        ))

      }
    }
  }

  ".geographicalIndicationRadioOptions" - {

    "should render the correct radio options" in {

      def characterCountComponent(field: String, geographicalIndication: ItemGeographicalIndicationType): HtmlFormat.Appendable = {
        govukCharacterCount(CharacterCount(
          id = field,
          name = field,
          maxLength = Some(50),
          label = LabelViewModel(Text(msgs(s"itemDesignationOfOrigin.$geographicalIndication.input"))),
          value = form(field).value,
          errorMessage = None
        ))
      }

      helper.geographicalIndicationRadioOptions(form) mustBe Seq(
        RadioItem(
          id      = Some(s"${ItemDesignationOfOriginFormProvider.geographicalIndicationField}-pdo"),
          value   = Some(ProtectedDesignationOfOrigin.toString),
          content = Text(msgs(s"itemDesignationOfOrigin.$ProtectedDesignationOfOrigin")),
          conditionalHtml = Some(
            characterCountComponent(ItemDesignationOfOriginFormProvider.protectedDesignationOfOriginTextField, ProtectedDesignationOfOrigin)
          )
        ),
        RadioItem(
          id      = Some(s"${form(ItemDesignationOfOriginFormProvider.geographicalIndicationField).id}-pgi"),
          value   = Some(ProtectedGeographicalIndication.toString),
          content = Text(msgs(s"itemDesignationOfOrigin.$ProtectedGeographicalIndication")),
          conditionalHtml = Some(
            characterCountComponent(ItemDesignationOfOriginFormProvider.protectedGeographicalIndicationTextField, ProtectedGeographicalIndication)
          )
        ),
        RadioItem(
          divider = Some(msgs(s"site.divider"))
        ),
        RadioItem(
          id      = Some(s"${form(ItemDesignationOfOriginFormProvider.geographicalIndicationField).id}-unprovided"),
          value   = Some(NoGeographicalIndication.toString),
          content = Text(msgs(s"itemDesignationOfOrigin.$NoGeographicalIndication"))
        )
      )
    }
  }

  ".spiritMarketingAndLabellingRadioOptions" - {

    "should render the correct radio options" in {

      helper.spiritMarketingAndLabellingRadioOptions(form) mustBe Seq(
        RadioItem(
          id      = Some(s"${form(ItemDesignationOfOriginFormProvider.isSpiritMarketedAndLabelledField).id}"),
          value   = Some("true"),
          content = Text(msgs("itemDesignationOfOrigin.s200.radio.yes"))
        ),
        RadioItem(
          divider = Some(msgs(s"site.divider"))
        ),
        RadioItem(
          id      = Some(s"${form(ItemDesignationOfOriginFormProvider.isSpiritMarketedAndLabelledField).id}-no"),
          value   = Some("false"),
          content = Text(msgs("itemDesignationOfOrigin.s200.radio.unprovided"))
        )
      )
    }
  }
}
