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

import config.AppConfig
import models.addressLookupFrontend.{Address, AddressLookupFrontendJourneyConfig}
import models.response.{ErrorResponse, UnexpectedDownstreamResponseError}
import play.api.libs.json.Reads
import uk.gov.hmrc.http._

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

trait AddressLookupFrontendConnector {
  def retrieveAddress(id: String)(implicit hc: HeaderCarrier): Future[Either[ErrorResponse, Option[Address]]]

  def initialiseJourney(config: AddressLookupFrontendJourneyConfig)(implicit hc: HeaderCarrier): Future[Either[ErrorResponse, String]]
}

@Singleton
class AddressLookupFrontendConnectorImpl @Inject()(val http: HttpClient,
                                               appConfig: AppConfig
                                              )(implicit ec: ExecutionContext) extends AddressLookupFrontendHttpParsers with AddressLookupFrontendConnector {

  override implicit val reads: Reads[Address] = Address.reads

  def retrieveAddress(id: String)(implicit hc: HeaderCarrier): Future[Either[ErrorResponse, Option[Address]]] =
    http.GET[Either[ErrorResponse, Option[Address]]](
      url = s"${appConfig.addressLookupFrontendUrl}/api/confirmed?id=$id")(RetrieveAddressReads, hc, ec)
      .recover {
        case error =>
          logger.error(error.getMessage)
          Left(UnexpectedDownstreamResponseError)
      }

  def initialiseJourney(config: AddressLookupFrontendJourneyConfig)(implicit hc: HeaderCarrier): Future[Either[ErrorResponse, String]] =
    http.POST[AddressLookupFrontendJourneyConfig, Either[ErrorResponse, String]](
      url = s"${appConfig.addressLookupFrontendUrl}/api/init",
      body = config
    )(AddressLookupFrontendJourneyConfig.format, InitialiseJourneyReads, hc, ec)
      .recover {
        case error =>
          logger.error(error.getMessage)
          Left(UnexpectedDownstreamResponseError)
      }
}
