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

package viewmodels.helpers.draftMovement

import models._
import models.requests.DataRequest
import models.sections.info.movementScenario.MovementScenario._
import models.response.{InvalidUserTypeException, MissingMandatoryPage}
import pages.sections.info.{DestinationTypePage, DispatchPlacePage}
import play.api.i18n.Messages
import utils.Logging

import javax.inject.Inject

class DraftMovementHelper @Inject()() extends Logging {
  def heading(implicit request: DataRequest[_], messages: Messages): String =
    (request.userTypeFromErn, request.userAnswers.get(DestinationTypePage).get) match {
      case (GreatBritainWarehouseKeeper, GbTaxWarehouse) =>
        messages("draftMovement.heading.gbTaxWarehouseTo", messages(s"destinationType.$GbTaxWarehouse"))

      case (NorthernIrelandWarehouseKeeper, destinationType@(GbTaxWarehouse | EuTaxWarehouse | DirectDelivery | RegisteredConsignee | TemporaryRegisteredConsignee | ExemptedOrganisation | UnknownDestination)) =>
        request.userAnswers.get(DispatchPlacePage) match {
          case Some(value) =>
            messages("draftMovement.heading.dispatchPlaceTo", messages(s"dispatchPlace.$value"), messages(s"destinationType.$destinationType"))
          case None =>
            logger.error(s"[heading] Missing mandatory page $DispatchPlacePage for $NorthernIrelandWarehouseKeeper")
            throw MissingMandatoryPage(s"[heading] Missing mandatory page $DispatchPlacePage for $NorthernIrelandWarehouseKeeper")
        }

      case (GreatBritainRegisteredConsignor | NorthernIrelandRegisteredConsignor, destinationType) =>
        messages("draftMovement.heading.importFor", messages(s"destinationType.$destinationType"))

      case (GreatBritainWarehouseKeeper | NorthernIrelandRegisteredConsignor, destinationType@(ExportWithCustomsDeclarationLodgedInTheUk | ExportWithCustomsDeclarationLodgedInTheEu)) =>
        messages(s"destinationType.$destinationType")

      case (userType, destinationType) =>
        logger.error(s"[heading] invalid UserType and destinationType combination for CAM journey: $userType | $destinationType")
        throw InvalidUserTypeException(s"[DraftMovementHelper][heading] invalid UserType and destinationType combination for CAM journey: $userType | $destinationType")
    }

}
