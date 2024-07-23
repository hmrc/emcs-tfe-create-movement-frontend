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

package viewmodels.helpers

import forms.sections.items.ItemSmallIndependentProducerFormProvider.{producerField, producerIdField}
import models.GoodsType._
import models.requests.DataRequest
import models.sections.info.movementScenario.MovementScenario
import models.sections.info.movementScenario.MovementScenario._
import models.sections.items.ItemSmallIndependentProducerType._
import models.{GoodsType, Index}
import pages.sections.info.DestinationTypePage
import pages.sections.items.{ItemCommodityCodePage, ItemExciseProductCodePage}
import play.api.data.Form
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.Aliases.{ErrorMessage, HtmlContent, Input, Text}
import uk.gov.hmrc.govukfrontend.views.html.components.GovukInput
import uk.gov.hmrc.govukfrontend.views.viewmodels.radios.{RadioItem, Radios}
import viewmodels.LegendSize
import viewmodels.govuk.all._

import javax.inject.Inject

class ItemSmallIndependentProducerHelper @Inject()(govukInput: GovukInput) {

  def radios(form: Form[_])(implicit messages: Messages): Radios =
    RadiosViewModel.apply(
      form(producerField),
      items = Seq(
        RadioItem(
          id = Some(s"${form(producerField).id}-${CertifiedIndependentSmallProducer.toString}"),
          value = Some(CertifiedIndependentSmallProducer.toString),
          content = Text(messages(s"itemSmallIndependentProducer.${CertifiedIndependentSmallProducer.toString}")),
          hint = Some(HintViewModel(Text(messages(s"itemSmallIndependentProducer.${CertifiedIndependentSmallProducer.toString}.hint"))))
        ),
        RadioItem(
          id = Some(s"${form(producerField).id}-${SelfCertifiedIndependentSmallProducerAndConsignor.toString}"),
          value = Some(SelfCertifiedIndependentSmallProducerAndConsignor.toString),
          content = Text(messages(s"itemSmallIndependentProducer.${SelfCertifiedIndependentSmallProducerAndConsignor.toString}"))
        ),
        RadioItem(
          id = Some(s"${form(producerField).id}-${SelfCertifiedIndependentSmallProducerAndNotConsignor.toString}"),
          value = Some(SelfCertifiedIndependentSmallProducerAndNotConsignor.toString),
          content = Text(messages(s"itemSmallIndependentProducer.${SelfCertifiedIndependentSmallProducerAndNotConsignor.toString}")),
          conditionalHtml = Some(
            govukInput(Input(
              id = producerIdField,
              name = producerIdField,
              label = LabelViewModel(Text(messages(s"itemSmallIndependentProducer.$SelfCertifiedIndependentSmallProducerAndNotConsignor.input"))),
              value = form(producerIdField).value,
              errorMessage = form.errors(producerIdField) match {
                case Nil => None
                case errors => Some(ErrorMessage(content = HtmlContent(errors.map(err => messages(err.message)).mkString("<br>"))))
              }
            ))
          )
        ),
        RadioItem(
          divider = Some(messages("site.divider"))
        ),
        RadioItem(
          id = Some(s"${form(producerField).id}-${NotAIndependentSmallProducer.toString}"),
          value = Some(NotAIndependentSmallProducer.toString),
          content = Text(messages(s"itemSmallIndependentProducer.${NotAIndependentSmallProducer.toString}"))
        ),
        RadioItem(
          id = Some(s"${form(producerField).id}-${NotProvided.toString}"),
          value = Some(NotProvided.toString),
          content = Text(messages(s"itemSmallIndependentProducer.${NotProvided.toString}"))
        )
      ),
      LegendViewModel(Text(messages("itemSmallIndependentProducer.legend"))).withCssClass(LegendSize.Small.toString)
    )

}

object ItemSmallIndependentProducerHelper {

  def constructDeclarationPrefix(itemIndex: Index)(implicit request: DataRequest[_], messages: Messages): String =
    (
      DestinationTypePage.value,
      ItemExciseProductCodePage(itemIndex).value,
      ItemCommodityCodePage(itemIndex).value
    ) match {
      case (Some(UkTaxWarehouse.GB | UkTaxWarehouse.NI | ExportWithCustomsDeclarationLodgedInTheUk | ExportWithCustomsDeclarationLodgedInTheEu), _, _) =>
        messages("itemSmallIndependentProducer.certifiedStatement.smallProducer")
      case (Some(movementScenario), Some(epc), Some(cnCode)) if isNiToEUMovement(movementScenario) =>
        handleNiToEuMovementDeclaration(epc, cnCode)
      case (movementScenario, epc, cnCode) => throw new IllegalStateException(s"Invalid scenario for small independent producer. Destination type: $movementScenario & EPC: $epc & CN code: $cnCode")
    }

  private[helpers] def handleNiToEuMovementDeclaration(epc: String, cnCode: String)(implicit messages: Messages): String = {
    val goodsType = GoodsType(epc, Some(cnCode))
    goodsType match {
      case Beer => messages("itemSmallIndependentProducer.certifiedStatement.smallBrewery")
      case Spirits => messages("itemSmallIndependentProducer.certifiedStatement.smallDistillery")
      case Wine => messages("itemSmallIndependentProducer.certifiedStatement.wineProducer")
      case Fermented(_) => messages("itemSmallIndependentProducer.certifiedStatement.fermentedBeveragesProducer")
      case Intermediate => messages("itemSmallIndependentProducer.certifiedStatement.intermediateBeveragesProducer")
      case _ => throw new IllegalStateException(s"Invalid goods type for small independent producer: $goodsType")
    }
  }

  private[helpers] def isNiToEUMovement(movementScenario: MovementScenario)(implicit request: DataRequest[_]): Boolean =
    Seq(
      EuTaxWarehouse,
      TemporaryRegisteredConsignee,
      RegisteredConsignee,
      DirectDelivery,
      CertifiedConsignee,
      TemporaryCertifiedConsignee,
      ExemptedOrganisation,
      ExportWithCustomsDeclarationLodgedInTheEu,
      ExportWithCustomsDeclarationLodgedInTheUk
    ).contains(movementScenario) && request.isNorthernIrelandErn
}
