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
import models.requests.DataRequest
import models.sections.info.DispatchPlace
import models.sections.info.movementScenario.{MovementScenario, MovementType}
import models.{NorthernIrelandCertifiedConsignor, NorthernIrelandRegisteredConsignor, NorthernIrelandTemporaryCertifiedConsignor, NorthernIrelandWarehouseKeeper, UserType}
import pages.sections.exportInformation.ExportCustomsOfficePage
import pages.sections.importInformation.ImportCustomsOfficeCodePage
import pages.sections.info.{DestinationTypePage, DispatchPlacePage}
import play.api.i18n.Messages
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

    //Prevent user manually accessing the declaration page when unfixed submission failures exist
    //TODO: add in when ETFE-3340 frontend has been merged (impossible to fix error as of: 13/03/24)
    //if(!request.userAnswers.haveAllSubmissionErrorsBeenFixed) {
    //  logger.warn("[SubmitCreateMovementModel][apply] - User attempted to submit movement but there are still unfixed submission failures")
    //  throw UnfixedSubmissionFailuresException("Failed to create SubmitCreateMovementModel due to unfixed submission failures")
    //}

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
      documentCertificate = DocumentCertificateModel.apply,
      headerEadEsad = HeaderEadEsadModel.apply(movementScenario.destinationType),
      transportMode = TransportModeModel.apply,
      movementGuarantee = MovementGuaranteeModel.apply,
      bodyEadEsad = BodyEadEsadModel.apply,
      eadEsadDraft = EadEsadDraftModel.apply,
      transportDetails = TransportDetailsModel.apply
    )
  }
}
