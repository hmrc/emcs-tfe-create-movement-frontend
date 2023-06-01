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

import base.SpecBase
import models.addressLookupFrontend.Address
import models.response.{JsonValidationError, MissingHeaderError, UnexpectedDownstreamResponseError}
import play.api.http.{HeaderNames, Status}
import play.api.libs.json.{Json, Reads}
import uk.gov.hmrc.http.HttpResponse

class AddressLookupFrontendHttpParsersSpec extends SpecBase {

  val addressId: String = "12345"

  val testAddress: Address = Address(
    lines = Seq("testLine1", "testLine2"),
    postcode = Some("FX1 1ZZ"),
    country = None
  )


  lazy val httpParser: AddressLookupFrontendHttpParsers = new AddressLookupFrontendHttpParsers {
    override implicit val reads: Reads[Address] = Address.reads
  }

  "RetrieveAddressReads" - {
    "should return a successful response" - {
      "when valid JSON is returned that can be parsed to the model" in {

        val httpResponse = HttpResponse(Status.OK, Json.toJsObject(testAddress), Map())

        httpParser.RetrieveAddressReads.read("", "", httpResponse) mustBe Right(Some(testAddress))
      }
    }

    "should return UnexpectedDownstreamError" - {
      s"when status is not OK (${Status.OK})" in {

        val httpResponse = HttpResponse(Status.INTERNAL_SERVER_ERROR, Json.obj(), Map())

        httpParser.RetrieveAddressReads.read("", "", httpResponse) mustBe Left(UnexpectedDownstreamResponseError)
      }
    }

    "should return JsonValidationError" - {
      s"when response does not contain Json" in {

        val httpResponse = HttpResponse(Status.OK, "", Map())

        httpParser.RetrieveAddressReads.read("", "", httpResponse) mustBe Left(JsonValidationError)
      }

      s"when response contains JSON but can't be deserialized to model" in {

        val httpResponse = HttpResponse(Status.OK, Json.obj(), Map())

        httpParser.RetrieveAddressReads.read("", "", httpResponse) mustBe Left(JsonValidationError)
      }
    }
  }

  "InitialiseJourneyReads" - {
    "should return a successful response" - {
      "when valid header is returned that can be parsed to the model" in {

        val httpResponse = HttpResponse(Status.ACCEPTED, "", Map(HeaderNames.LOCATION -> Seq(testUrl)))

        httpParser.InitialiseJourneyReads.read("", "", httpResponse) mustBe Right(testUrl)
      }
    }

    "should return UnexpectedDownstreamError" - {
      s"when status is not OK (${Status.OK})" in {

        val httpResponse = HttpResponse(Status.INTERNAL_SERVER_ERROR, Json.obj(), Map())

        httpParser.InitialiseJourneyReads.read("", "", httpResponse) mustBe Left(UnexpectedDownstreamResponseError)
      }
    }

    "should return MissingHeaderError" - {
      s"when response does not contain location header" in {

        val httpResponse = HttpResponse(Status.ACCEPTED, "", Map())

        httpParser.InitialiseJourneyReads.read("", "", httpResponse) mustBe
          Left(MissingHeaderError("Missing location header to redirect to Address Lookup Frontend"))
      }
    }
  }

}