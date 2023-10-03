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

import models._
import play.api.mvc.WrappedRequest

case class DataRequest[A](request: UserRequest[A],
                          lrn: String,
                          userAnswers: UserAnswers,
                          traderKnownFacts: TraderKnownFacts) extends WrappedRequest[A](request) {

  val internalId: String = request.internalId
  val ern: String = request.ern

  val userTypeFromErn: UserType = ern.take(4).toUpperCase match {
    case "GBRC" => GreatBritainRegisteredConsignor
    case "XIRC" => NorthernIrelandRegisteredConsignor
    case "GBWK" => GreatBritainWarehouseKeeper
    case "XIWK" => NorthernIrelandWarehouseKeeper
    case "XI00" => NorthernIrelandWarehouse
    case "GB00" => GreatBritainWarehouse
    case _ => Unknown
  }

}
