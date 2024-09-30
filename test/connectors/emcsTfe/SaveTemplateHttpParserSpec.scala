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

package connectors.emcsTfe

import base.SpecBase
import models.response.UnexpectedDownstreamResponseError
import play.api.http.Status
import play.api.libs.json.Json
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse}

import scala.concurrent.ExecutionContext

class SaveTemplateHttpParserSpec extends SpecBase with SaveTemplateHttpParser {

  implicit val ec: ExecutionContext = ExecutionContext.global
  implicit val hc: HeaderCarrier = HeaderCarrier()

  "SaveTemplateReads.read(method: String, url: String, response: HttpResponse)" - {

    "should return a successful response" - {

      "when OK is returned" in {
        val httpResponse = HttpResponse(
          status = Status.OK,
          json = Json.obj(),
          headers = Map()
        )
        SaveTemplateReads.read("", "", httpResponse) mustBe Right(true)
      }
    }

    "should return UnexpectedDownstreamDraftSubmissionResponseError" - {

      s"when status is not OK (${Status.OK})" in {
        val httpResponse = HttpResponse(Status.INTERNAL_SERVER_ERROR, Json.obj(), Map())
        SaveTemplateReads.read("", "", httpResponse) mustBe Left(UnexpectedDownstreamResponseError)
      }
    }
  }
}
