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

package models.submitCreateMovement

import models.requests.DataRequest
import models.sections.info.movementScenario.{MovementScenario, MovementType}
import pages.sections.exportInformation.ExportCustomsOfficePage
import pages.sections.importInformation.ImportCustomsOfficeCodePage
import pages.sections.info.DestinationTypePage
import play.api.libs.json.{Json, OFormat}
import utils.ModelConstructorHelpers

case class SubmitCreateMovementModel(
                                movementType: MovementType,
                                attributes: AttributesModel,
                                consigneeTrader: Option[TraderModel],
                                consignorTrader: TraderModel,
                                placeOfDispatchTrader: Option[TraderModel],
                                dispatchImportOffice: Option[OfficeModel],
                                complementConsigneeTrader: Option[ComplementConsigneeTraderModel],
                                deliveryPlaceTrader: Option[TraderModel],
                                deliveryPlaceCustomsOffice: Option[OfficeModel],
                                competentAuthorityDispatchOffice: OfficeModel,
                                transportArrangerTrader: Option[TraderModel],
                                firstTransporterTrader: Option[TraderModel],
                                documentCertificate: Option[Seq[DocumentCertificateModel]],
                                headerEadEsad: HeaderEadEsadModel,
                                transportMode: TransportModeModel,
                                movementGuarantee: MovementGuaranteeModel,
                                bodyEadEsad: Seq[BodyEadEsadModel],
                                eadEsadDraft: EadEsadDraftModel,
                                transportDetails: Seq[TransportDetailsModel]
                              )

object SubmitCreateMovementModel extends ModelConstructorHelpers {
  implicit val fmt: OFormat[SubmitCreateMovementModel] = Json.format

  def apply(implicit request: DataRequest[_]): SubmitCreateMovementModel = {

    val movementScenario: MovementScenario = mandatoryPage(DestinationTypePage)

    SubmitCreateMovementModel(
      movementType = movementScenario.movementType,
      attributes = AttributesModel.apply(movementScenario.destinationType),
      consigneeTrader = TraderModel.applyConsignee,
      consignorTrader = TraderModel.applyConsignor,
      placeOfDispatchTrader = TraderModel.applyPlaceOfDispatch,
      dispatchImportOffice = request.userAnswers.get(ImportCustomsOfficeCodePage).map(OfficeModel(_)),
      complementConsigneeTrader = ComplementConsigneeTraderModel.apply,
      deliveryPlaceTrader = TraderModel.applyDeliveryPlace(movementScenario),
      deliveryPlaceCustomsOffice = request.userAnswers.get(ExportCustomsOfficePage).map(OfficeModel(_)),
      competentAuthorityDispatchOffice = ???,
      transportArrangerTrader = TraderModel.applyTransportArranger,
      firstTransporterTrader = TraderModel.applyFirstTransporter,
      documentCertificate = DocumentCertificateModel.apply,
      headerEadEsad = HeaderEadEsadModel.apply(movementScenario.destinationType),
      transportMode = ???,
      movementGuarantee = ???,
      bodyEadEsad = ???,
      eadEsadDraft = ???,
      transportDetails = ???
    )
  }
}
