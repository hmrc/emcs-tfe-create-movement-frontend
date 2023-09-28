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

package models.sections.consignee

import models.{Enumerable, WithName}
import play.api.data.Form
import play.api.i18n.Messages
import play.api.libs.json.{Json, OFormat}
import play.twirl.api.Html
import uk.gov.hmrc.govukfrontend.views.Aliases.Text
import uk.gov.hmrc.govukfrontend.views.html.components.{GovukErrorMessage, GovukHint, GovukInput, GovukLabel}
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.HtmlContent
import uk.gov.hmrc.govukfrontend.views.viewmodels.errormessage.ErrorMessage
import uk.gov.hmrc.govukfrontend.views.viewmodels.input.Input
import uk.gov.hmrc.govukfrontend.views.viewmodels.label.Label
import uk.gov.hmrc.govukfrontend.views.viewmodels.radios.RadioItem

case class ConsigneeExportVat(exportType: ConsigneeExportVatType, vatNumber: Option[String], eoriNumber: Option[String])

object ConsigneeExportVat {
  implicit val format: OFormat[ConsigneeExportVat] = Json.format[ConsigneeExportVat]
}

sealed trait ConsigneeExportVatType

object ConsigneeExportVatType extends Enumerable.Implicits {

  case object YesVatNumber extends WithName("yesVatNumber") with ConsigneeExportVatType

  case object YesEoriNumber extends WithName("yesEoriNumber") with ConsigneeExportVatType

  case object No extends WithName("no") with ConsigneeExportVatType

  val values: Seq[ConsigneeExportVatType] = Seq(YesVatNumber, YesEoriNumber, No)

  def options(form: Form[_])(implicit messages: Messages): Seq[RadioItem] = {

    def createConditionalField(fieldName: String)(implicit messages: Messages) =
      new GovukInput(
        new GovukErrorMessage(),
        new GovukHint(),
        new GovukLabel())(
        Input(
          id = fieldName,
          value = form(fieldName).value,
          name = fieldName,
          label = Label(content = Text(messages(s"consigneeExportVat.$fieldName.label")), isPageHeading = false),
          errorMessage = {
            form.error(fieldName).flatMap { error =>
              Some(
                ErrorMessage(content = HtmlContent(Html( messages(error.message))))
              )
            }
          }
        )
      )

    values.map {
      value =>
        RadioItem(
          content = Text(messages(s"consigneeExportVat.${value.toString}")),
          value = Some(value.toString),
          id = Some(s"value_$value"),
          conditionalHtml = value match {
            case YesVatNumber => Some(createConditionalField(fieldName = "vatNumber"))
            case YesEoriNumber => Some(createConditionalField(fieldName = "eoriNumber"))
            case _ => None
          }
        )
    }
  }

  implicit val enumerable: Enumerable[ConsigneeExportVatType] =
    Enumerable(values.map(v => v.toString -> v): _*)
}
