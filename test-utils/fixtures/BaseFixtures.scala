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

package fixtures

import models.{CountryModel, TraderKnownFacts, UserAddress, UserAnswers}
import models.addressLookupFrontend._
import play.api.libs.json.{JsObject, Json}
import play.api.mvc.Call

import java.time.temporal.ChronoUnit
import java.time.{Instant, LocalDate}

trait BaseFixtures {

  val testCredId: String = "credId"
  val testInternalId: String = "internalId"
  val testErn: String = "XIRC1234567890"
  val testLrn: String = "lrn"
  val testVatNumber: String = "123456789"
  val testDateOfArrival: LocalDate = LocalDate.now()
  val testConfirmationReference: String = "UYVQBLMXCYK6HAEBZI7TSWAQ6XDTXFYU"
  val testOnwardRoute: Call = Call("GET", "/foo")
  val testId: String = "123"
  val testUrl: String = "testUrl"

  val testAlfJourneyConfig: AddressLookupFrontendJourneyConfig =
    AddressLookupFrontendJourneyConfig(
      version = 2,
      options = JourneyOptions(
        continueUrl = "testContinueUrl",
        homeNavHref = None,
        accessibilityFooterUrl = None,
        deskProServiceName = None,
        showPhaseBanner = None,
        alphaPhase = None,
        showBackButtons = None,
        includeHMRCBranding = None,
        selectPageConfig = None,
        confirmPageConfig = None,
        timeoutConfig = None,
        disableTranslations = None
      ),
      labels = JourneyLabels(en = None, cy = None)
    )

  val testAlfAddressJson: JsObject =
    Json.obj(
      "auditRef" -> "bed4bd24-72da-42a7-9338-f43431b7ed72",
      "id" -> "GB990091234524",
      "address" -> Json.obj(
        "lines" -> Json.arr("10 Other Place", "Some District", "Anytown"),
        "postcode" -> "ZZ1 1ZZ",
        "country" -> Json.obj(
          "code" -> "GB",
          "name" -> "United Kingdom"
        )
      )
    )

  val testAlfAddress: Address =
    Address(
      lines = Seq("10 Other Place", "Some District", "Anytown"),
      postcode = Some("ZZ1 1ZZ"),
      country = Some(Country("GB", "United Kingdom")),
      auditRef = Some("bed4bd24-72da-42a7-9338-f43431b7ed72")
    )

  val emptyUserAnswers: UserAnswers = UserAnswers(
    ern = testErn,
    lrn = testLrn,
    lastUpdated = Instant.now().truncatedTo(ChronoUnit.MILLIS)
  )

  val testMinTraderKnownFacts: TraderKnownFacts = TraderKnownFacts(
    traderName = "testTraderName",
    addressLine1 = None,
    addressLine2 = None,
    addressLine3 = None,
    addressLine4 = None,
    addressLine5 = None,
    postcode = None
  )

  val testUserAddress = UserAddress(Some("10"), "Test Street", "Testown", "ZZ1 1ZZ")


  val countryModelAT = CountryModel(
    countryCode = "AT",
    country = "Austria"
  )

  val countryModelBE = CountryModel(
    countryCode = "BE",
    country = "Belgium"
  )

  val memberStateJsonAT = Json.obj(
    "countryCode" -> "AT",
    "country" -> "Austria"
  )

  val memberStateJsonBE = Json.obj(
    "countryCode" -> "BE",
    "country" -> "Belgium"
  )
}
