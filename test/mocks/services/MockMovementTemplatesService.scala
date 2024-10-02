/*
 * Copyright 2024 HM Revenue & Customs
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

import models.requests.DataRequest
import models.response.ErrorResponse
import models.response.templates.MovementTemplates
import org.scalamock.handlers.{CallHandler2, CallHandler4}
import org.scalamock.scalatest.MockFactory
import services.MovementTemplatesService
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.Future

trait MockMovementTemplatesService extends MockFactory {

  val mockMovementTemplatesService: MovementTemplatesService = mock[MovementTemplatesService]

  object MockMovementTemplatesService {

    def userHasTemplates(ern: String): CallHandler2[String, HeaderCarrier, Future[Boolean]] =
      (mockMovementTemplatesService.userHasTemplates(_: String)(_: HeaderCarrier)).expects(ern, *)

    def saveTemplate(templateName: String,
                     existingTemplateName: Option[String] = None): CallHandler4[String, Option[String], DataRequest[_], HeaderCarrier, Future[Either[ErrorResponse, Boolean]]] =
      (mockMovementTemplatesService.saveTemplate(_: String, _: Option[String])(_: DataRequest[_], _: HeaderCarrier))
        .expects(templateName, existingTemplateName, *, *)

    def getExistingTemplateNames(ern: String): CallHandler2[String, HeaderCarrier, Future[Seq[String]]] =
      (mockMovementTemplatesService.getExistingTemplateNames(_: String)(_: HeaderCarrier)).expects(ern, *)

    def getList(ern: String): CallHandler2[String, HeaderCarrier, Future[MovementTemplates]] =
      (mockMovementTemplatesService.getList(_: String)(_: HeaderCarrier)).expects(ern, *)
  }

}
