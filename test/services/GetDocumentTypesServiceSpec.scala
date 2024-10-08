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

import base.SpecBase
import fixtures.DocumentTypeFixtures
import mocks.connectors.MockGetDocumentTypesConnector
import models.response.{DocumentTypesException, UnexpectedDownstreamResponseError}
import models.sections.documents.DocumentType
import play.api.test.Helpers._
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

class GetDocumentTypesServiceSpec extends SpecBase with MockGetDocumentTypesConnector with DocumentTypeFixtures {

  implicit val hc: HeaderCarrier = HeaderCarrier()
  implicit val ec: ExecutionContext = ExecutionContext.global

  lazy val testService = new GetDocumentTypesService(mockGetDocumentTypesConnector)

  ".getDocumentTypes" - {

    "should return a Seq[DocumentType]" - {
      "when Connector returns success from downstream" in {

        val expectedResult = Seq(
          documentTypeModel,
          documentTypeModel,
          documentTypeModel
        )

        MockGetDocumentTypesConnector.getDocumentTypes().returns(Future(Right(
          Seq(
            documentTypeModel,
            documentTypeModel,
            documentTypeModel
          )
        )))

        val actualResults = testService.getDocumentTypes().futureValue

        actualResults mustBe expectedResult
      }
    }

    "should return a Seq[DocumentType] sorted by numeric and alphanumeric codes and description" - {
      "when Connector returns document types with mixed codes" in {

        val documentTypes = Seq(
          DocumentType("C1", "B"),
          DocumentType("0", "A"),
          DocumentType("A1", "A"),
          DocumentType("C1", "A"),
          DocumentType("1", "A"),
          DocumentType("0", "B"),
          DocumentType("A2", "B")
        )

        val expectedResult = Seq(
          DocumentType("0", "A"),
          DocumentType("0", "B"),
          DocumentType("1", "A"),
          DocumentType("A1", "A"),
          DocumentType("A2", "B"),
          DocumentType("C1", "A"),
          DocumentType("C1", "B")
        )

        MockGetDocumentTypesConnector.getDocumentTypes().returns(Future(Right(documentTypes)))

        val actualResults = testService.getDocumentTypes().futureValue

        actualResults mustBe expectedResult
      }
    }

    "should throw DocumentTypesException" - {

      "when Connector returns failure from downstream" in {

        val expectedResult = "No document types retrieved"

        MockGetDocumentTypesConnector.getDocumentTypes().returns(Future(Left(UnexpectedDownstreamResponseError)))

        val actualResult = intercept[DocumentTypesException](await(testService.getDocumentTypes())).getMessage

        actualResult mustBe expectedResult
      }
    }
  }
}
