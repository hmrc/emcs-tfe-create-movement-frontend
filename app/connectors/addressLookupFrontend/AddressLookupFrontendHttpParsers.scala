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

package connectors.addressLookupFrontend

import connectors.BaseConnectorUtils
import models.addressLookupFrontend.Address
import models.response.{ErrorResponse, JsonValidationError, MissingHeaderError, UnexpectedDownstreamResponseError}
import play.api.http.HeaderNames
import play.api.http.Status.{ACCEPTED, OK}
import uk.gov.hmrc.http.{HttpReads, HttpResponse}

trait AddressLookupFrontendHttpParsers extends BaseConnectorUtils[Address] {

  object RetrieveAddressReads extends HttpReads[Either[ErrorResponse, Option[Address]]] {
    override def read(method: String, url: String, response: HttpResponse): Either[ErrorResponse, Option[Address]] =
      response.status match {
        case OK =>
          response.validateJson match {
            case Some(valid) => Right(Some(valid))
            case None =>
              logger.error(s"[read] Bad JSON response from Address Lookup Frontend")
              Left(JsonValidationError)
          }
        case status =>
          logger.warn(s"[read] Unexpected status from Address Lookup Frontend: $status")
          Left(UnexpectedDownstreamResponseError)
      }
  }

  object InitialiseJourneyReads extends HttpReads[Either[ErrorResponse, String]] {
    override def read(method: String, url: String, response: HttpResponse): Either[ErrorResponse, String] =
      response.status match {
        case ACCEPTED =>
          response.header(HeaderNames.LOCATION) match {
            case Some(continueUrl) => Right(continueUrl)
            case None =>
              logger.error("[read] Location header not set in Address Lookup Frontend response")
              Left(MissingHeaderError("Missing location header to redirect to Address Lookup Frontend"))
          }
        case status =>
          logger.warn(s"[read] Unexpected status from Address Lookup Frontend: $status")
          Left(UnexpectedDownstreamResponseError)
      }
  }

}
