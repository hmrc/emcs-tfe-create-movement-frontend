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

package services

import connectors.addressLookupFrontend.AddressLookupFrontendConnector
import models.addressLookupFrontend.Address
import models.response.ErrorResponse
import play.api.i18n.MessagesApi
import play.api.mvc.Call
import uk.gov.hmrc.http.HeaderCarrier

import javax.inject.{Inject, Singleton}
import scala.concurrent.Future

@Singleton
class AddressLookupFrontendService @Inject()(addressLookupFrontendConnector: AddressLookupFrontendConnector,
                                             addressLookupConfigBuilderService: AddressLookupFrontendConfigBuilderService,
                                             messagesApi: MessagesApi) {

  def initialiseJourney(handbackLocation: Call)(implicit hc: HeaderCarrier): Future[Either[ErrorResponse, String]] = {

    val config = addressLookupConfigBuilderService.buildConfig(handbackLocation = handbackLocation)(messagesApi)

    addressLookupFrontendConnector.initialiseJourney(config)
  }

  def retrieveAddress(id: String)(implicit hc: HeaderCarrier): Future[Either[ErrorResponse, Option[Address]]] =
    addressLookupFrontendConnector.retrieveAddress(id)

}
