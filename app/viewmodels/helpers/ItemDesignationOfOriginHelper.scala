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

import forms.sections.items.ItemDesignationOfOriginFormProvider
import forms.sections.items.ItemDesignationOfOriginFormProvider._
import models.sections.items.ItemGeographicalIndicationType
import models.sections.items.ItemGeographicalIndicationType._
import play.api.data.Form
import play.api.i18n.Messages
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.govukfrontend.views.Aliases._
import uk.gov.hmrc.govukfrontend.views.html.components.{GovukCharacterCount, GovukRadios}
import viewmodels.govuk.LabelFluency

import javax.inject.Inject

class ItemDesignationOfOriginHelper @Inject()(govukCharacterCount: GovukCharacterCount,
                                              govukRadios: GovukRadios
                                             ) extends LabelFluency {

  def geographicalIndicationIdentificationTextBox(geographicalIndication: ItemGeographicalIndicationType, form: Form[_])
                                                 (implicit messages: Messages): HtmlFormat.Appendable = {
    val fieldName = if(geographicalIndication == ProtectedDesignationOfOrigin) protectedDesignationOfOriginTextField else protectedGeographicalIndicationTextField
    govukCharacterCount(CharacterCount(
      id = fieldName,
      name = fieldName,
      maxLength = Some(50),
      label = LabelViewModel(Text(messages(s"itemDesignationOfOrigin.$geographicalIndication.input"))),
      value = form(fieldName).value,
      errorMessage = form.errors(fieldName) match {
        case Nil => None
        case errors => Some(ErrorMessage(content = HtmlContent(errors.map(err => messages(err.message)).mkString("<br>"))))
      }
    ))
  }

  def geographicalIndicationRadioOptions(form: Form[_])(implicit messages: Messages): Seq[RadioItem] = {
    Seq(
      RadioItem(
        id      = Some(s"${form(ItemDesignationOfOriginFormProvider.geographicalIndicationField).id}-pdo"),
        value   = Some(ProtectedDesignationOfOrigin.toString),
        content = Text(messages(s"itemDesignationOfOrigin.$ProtectedDesignationOfOrigin")),
        conditionalHtml = Some(geographicalIndicationIdentificationTextBox(ProtectedDesignationOfOrigin, form))
      ),
      RadioItem(
        id      = Some(s"${form(ItemDesignationOfOriginFormProvider.geographicalIndicationField).id}-pgi"),
        value   = Some(ProtectedGeographicalIndication.toString),
        content = Text(messages(s"itemDesignationOfOrigin.$ProtectedGeographicalIndication")),
        conditionalHtml = Some(geographicalIndicationIdentificationTextBox(ProtectedGeographicalIndication, form))
      ),
      RadioItem(
        divider = Some(messages(s"site.divider"))
      ),
      RadioItem(
        id      = Some(s"${form(ItemDesignationOfOriginFormProvider.geographicalIndicationField).id}-unprovided"),
        value   = Some(NoGeographicalIndication.toString),
        content = Text(messages("itemDesignationOfOrigin.unprovided"))
      )
    )
  }

  def spiritMarketingAndLabellingRadioOptions(form: Form[_])(implicit messages: Messages): Seq[RadioItem] = {
    Seq(
      RadioItem(
        id      = Some(s"${form(ItemDesignationOfOriginFormProvider.isSpiritMarketedAndLabelledField).id}"),
        value   = Some("true"),
        content = Text(messages("itemDesignationOfOrigin.s200.radio.yes")),
      ),
      RadioItem(
        divider = Some(messages(s"site.divider"))
      ),
      RadioItem(
        id      = Some(s"${form(ItemDesignationOfOriginFormProvider.isSpiritMarketedAndLabelledField).id}-no"),
        value   = Some("false"),
        content = Text(messages("itemDesignationOfOrigin.s200.radio.unprovided"))
      )
    )
  }

}
