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

package mocks.services

import models.addressLookupFrontend.Address
import models.response.ErrorResponse
import org.scalamock.handlers.{CallHandler2, CallHandler3}
import org.scalamock.scalatest.MockFactory
import play.api.mvc.{Call, RequestHeader}
import services.AddressLookupFrontendService
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.Future

trait MockAddressLookupFrontendService extends MockFactory {


  lazy val mockAddressLookupFrontendService: AddressLookupFrontendService = mock[AddressLookupFrontendService]

  object MockAddressLookupFrontendService {
    def retrieveAddress(id: String): CallHandler2[String, HeaderCarrier, Future[Either[ErrorResponse, Option[Address]]]] =
      (mockAddressLookupFrontendService.retrieveAddress(_: String)(_: HeaderCarrier)).expects(id, *)

    def initialiseJourney(handbackLocation: Call): CallHandler3[Call, HeaderCarrier, RequestHeader, Future[Either[ErrorResponse, String]]] =
      (mockAddressLookupFrontendService.initialiseJourney(_: Call)(_: HeaderCarrier, _: RequestHeader))
        .expects(handbackLocation, *, *)
  }

}
