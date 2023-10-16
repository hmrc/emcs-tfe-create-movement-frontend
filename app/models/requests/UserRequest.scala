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

package models.requests

import config.SessionKeys.DISPATCH_PLACE
import models._
import models.sections.info.DispatchPlace
import models.sections.info.DispatchPlace.{GreatBritain, NorthernIreland}
import play.api.mvc.{Request, WrappedRequest}
import utils.Logging

case class UserRequest[A](request: Request[A],
                          ern: String,
                          internalId: String,
                          credId: String) extends WrappedRequest[A](request) with Logging {

  val isNorthernIrelandErn: Boolean = ern.startsWith("XI")

  private val ERN_PREFIX_LENGTH = 4

  val userTypeFromErn: UserType = ern.take(ERN_PREFIX_LENGTH).toUpperCase match {
    case "GBRC" => GreatBritainRegisteredConsignor
    case "XIRC" => NorthernIrelandRegisteredConsignor
    case "GBWK" => GreatBritainWarehouseKeeper
    case "XIWK" => NorthernIrelandWarehouseKeeper
    case "XI00" => NorthernIrelandWarehouse
    case "GB00" => GreatBritainWarehouse
    case _ => Unknown
  }

  val isWarehouseKeeper: Boolean = (userTypeFromErn == GreatBritainWarehouseKeeper) || (userTypeFromErn == NorthernIrelandWarehouseKeeper)
  val isRegisteredConsignor: Boolean = (userTypeFromErn == GreatBritainRegisteredConsignor) || (userTypeFromErn == NorthernIrelandRegisteredConsignor)

  val dispatchPlace: Option[DispatchPlace] = request.session.get(DISPATCH_PLACE) match {
    case Some(dp) if dp == GreatBritain.toString => Some(GreatBritain)
    case Some(dp) if dp == NorthernIreland.toString => Some(NorthernIreland)
    case value =>
      logger.info(s"[dispatchPlace] Invalid value for DISPATCH_PLACE: $value")
      None
  }
}
