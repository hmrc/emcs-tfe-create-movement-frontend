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

import config.AppConfig
import models.audit.Auditable
import models.requests.DataRequest
import models.sections.info.DispatchPlace
import models.sections.info.movementScenario.{MovementScenario, MovementType}
import models.{NorthernIrelandCertifiedConsignor, NorthernIrelandRegisteredConsignor, NorthernIrelandTemporaryCertifiedConsignor, NorthernIrelandWarehouseKeeper, UserType}
import pages.sections.exportInformation.ExportCustomsOfficePage
import pages.sections.importInformation.ImportCustomsOfficeCodePage
import pages.sections.info.{DestinationTypePage, DispatchPlacePage}
import play.api.i18n.Messages
import play.api.libs.functional.syntax.{toFunctionalBuilderOps, unlift}
import play.api.libs.json.{Json, OFormat, OWrites, Writes, __}
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

  val auditWrites: OWrites[SubmitCreateMovementModel] = (
    (__ \ "movementType").write[MovementType](Auditable.writes[MovementType]) and
    (__ \ "attributes").write[AttributesModel](AttributesModel.auditWrites) and
    (__ \ "consigneeTrader").writeNullable[TraderModel] and
    (__ \ "consignorTrader").write[TraderModel] and
    (__ \ "placeOfDispatchTrader").writeNullable[TraderModel] and
    (__ \ "dispatchImportOffice").writeNullable[OfficeModel] and
    (__ \ "complementConsigneeTrader").writeNullable[ComplementConsigneeTraderModel] and
    (__ \ "deliveryPlaceTrader").writeNullable[TraderModel] and
    (__ \ "deliveryPlaceCustomsOffice").writeNullable[OfficeModel] and
    (__ \ "competentAuthorityDispatchOffice").write[OfficeModel] and
    (__ \ "transportArrangerTrader").writeNullable[TraderModel] and
    (__ \ "firstTransporterTrader").writeNullable[TraderModel] and
    (__ \ "documentCertificate").writeNullable[Seq[DocumentCertificateModel]](Writes.seq(DocumentCertificateModel.auditWrites)) and
    (__ \ "headerEadEsad").write[HeaderEadEsadModel](HeaderEadEsadModel.auditWrites) and
    (__ \ "transportMode").write[TransportModeModel](TransportModeModel.auditWrites) and
    (__ \ "movementGuarantee").write[MovementGuaranteeModel](MovementGuaranteeModel.auditWrites) and
    (__ \ "bodyEadEsad").write[Seq[BodyEadEsadModel]](Writes.seq(BodyEadEsadModel.auditWrites)) and
    (__ \ "eadEsadDraft").write[EadEsadDraftModel](EadEsadDraftModel.auditWrites) and
    (__ \ "transportDetails").write[Seq[TransportDetailsModel]](Writes.seq(TransportDetailsModel.auditWrites))
    )(unlift(SubmitCreateMovementModel.unapply))

  private[submitCreateMovement] def dispatchOffice(implicit request: DataRequest[_], appConfig: AppConfig): OfficeModel = {

    val referenceNumber = UserType(request.ern) match {
      case NorthernIrelandRegisteredConsignor | NorthernIrelandTemporaryCertifiedConsignor | NorthernIrelandCertifiedConsignor =>
        DispatchPlace.NorthernIreland + appConfig.destinationOfficeSuffix
      case NorthernIrelandWarehouseKeeper =>
        mandatoryPage(DispatchPlacePage) + appConfig.destinationOfficeSuffix
      case _ =>
        DispatchPlace.GreatBritain + appConfig.destinationOfficeSuffix
    }

    OfficeModel(referenceNumber)
  }

  def apply(implicit request: DataRequest[_], messages: Messages, appConfig: AppConfig): SubmitCreateMovementModel = {

    val movementScenario: MovementScenario = mandatoryPage(DestinationTypePage)

    SubmitCreateMovementModel(
      movementType = movementScenario.movementType,
      attributes = AttributesModel.apply,
      consigneeTrader = TraderModel.applyConsignee,
      consignorTrader = TraderModel.applyConsignor,
      placeOfDispatchTrader = TraderModel.applyPlaceOfDispatch,
      dispatchImportOffice = request.userAnswers.get(ImportCustomsOfficeCodePage).map(OfficeModel(_)),
      complementConsigneeTrader = ComplementConsigneeTraderModel.apply,
      deliveryPlaceTrader = TraderModel.applyDeliveryPlace(movementScenario),
      deliveryPlaceCustomsOffice = request.userAnswers.get(ExportCustomsOfficePage).map(OfficeModel(_)),
      competentAuthorityDispatchOffice = dispatchOffice,
      transportArrangerTrader = TraderModel.applyTransportArranger,
      firstTransporterTrader = TraderModel.applyFirstTransporter,
      documentCertificate = DocumentCertificateModel.applyFromRequest,
      headerEadEsad = HeaderEadEsadModel.apply(movementScenario.destinationType),
      transportMode = TransportModeModel.apply,
      movementGuarantee = MovementGuaranteeModel.apply,
      bodyEadEsad = BodyEadEsadModel.apply,
      eadEsadDraft = EadEsadDraftModel.apply,
      transportDetails = TransportDetailsModel.apply
    )
  }
}
