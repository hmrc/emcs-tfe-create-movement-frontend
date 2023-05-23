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

package connectors.userAllowList

import base.SpecBase
import models.response.UnexpectedDownstreamResponseError
import play.api.http.Status
import uk.gov.hmrc.http.HttpResponse

class UserAllowListHttpParserSpec extends SpecBase with UserAllowListHttpParser {

  "UserAllowListReads.read(method: String, url: String, response: HttpResponse)" - {

    "should return 'true'" - {

      s"when an OK (${Status.OK}) response is retrieved" in {
        UserAllowListReads.read("", "", HttpResponse(Status.OK, "")) mustBe Right(true)
      }
    }

    "should return 'false'" - {

      s"when a NOT_FOUND (${Status.NOT_FOUND}) response is retrieved" in {
        UserAllowListReads.read("", "", HttpResponse(Status.NOT_FOUND, "")) mustBe Right(false)
      }
    }

    "should return UnexpectedDownstreamError" - {

      s"when status is anything else" in {
        UserAllowListReads.read("", "", HttpResponse(Status.INTERNAL_SERVER_ERROR, "")) mustBe Left(UnexpectedDownstreamResponseError)
      }
    }
  }
}
