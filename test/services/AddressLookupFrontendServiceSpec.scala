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
import mocks.connectors.MockAddressLookupFrontendConnector
import mocks.services.MockAddressLookupFrontendConfigBuilderService
import models.addressLookupFrontend._
import models.response.ErrorResponse
import org.scalatest.EitherValues
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.i18n.MessagesApi
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers.{GET, await, defaultAwaitTimeout}
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.Future

class AddressLookupFrontendServiceSpec() extends SpecBase
  with GuiceOneAppPerSuite
  with EitherValues
  with MockAddressLookupFrontendConnector
  with MockAddressLookupFrontendConfigBuilderService {


  class Setup {
    val messagesApi: MessagesApi = app.injector.instanceOf[MessagesApi]

    object TestService extends AddressLookupFrontendService(
      mockAddressLookupFrontendConnector,
      mockAddressLookupFrontendConfigBuilderService,
      messagesApi
    ) {
      lazy val createMovementUrl = "testUrl"
      lazy val timeoutInSeconds = 15
    }

  }

  implicit val hc: HeaderCarrier = HeaderCarrier()

  "getAddress" - {
    val id = "testID"
    val address = Address(Seq("testLine 1", "testLine 2"), None, None)

    "return an address" in new Setup {
      MockAddressLookupFrontendConnector.retrieveAddress(id).returns(Future.successful(Right(Some(address))))

      val result: Either[ErrorResponse, Option[Address]] = await(TestService.retrieveAddress(id)(hc))

      result.right.value mustBe Some(address)
    }
  }

  "initialiseAlfJourney" - {
    "return a url" in new Setup {
      val url = "testID"
      val testHandbackLocation: Call = Call("", "/testUrl")

      implicit val request = FakeRequest(GET, "/foo/bar")

      val config: AddressLookupFrontendJourneyConfig =
        AddressLookupFrontendJourneyConfig(
          version = AddressLookupFrontendJourneyConfig.defaultConfigVersion,
          options =
            JourneyOptions(
              continueUrl = "testUrl/foo",
              homeNavHref = Some("http://www.hmrc.gov.uk/"),
              accessibilityFooterUrl = Some("testAccessibilityUrl"),
              deskProServiceName = Some("emcstfe"),
              showPhaseBanner = Some(true),
              alphaPhase = Some(false),
              showBackButtons = Some(true),
              includeHMRCBranding = Some(true),
              disableTranslations = Some(true),

              selectPageConfig = Some(
                SelectPageConfig(
                  proposalListLimit = 30,
                  showSearchAgainLink = true
                )
              ),

              confirmPageConfig = Some(
                ConfirmPageConfig(
                  showSearchAgainLink = false,
                  showSubHeadingAndInfo = false,
                  showChangeLink = true
                )
              ),

              timeoutConfig = Some(
                TimeoutConfig(
                  timeoutAmount = 15,
                  timeoutUrl = "testUrl"
                )
              )
            ),
          labels = JourneyLabels(
            en = Some(LanguageLabels(
              appLevelLabels = AppLevelLabels(
                navTitle = Some("Excise Movement and Control System"),
                phaseBannerHtml = None
              ),

              CountryPickerLabels(
                title = Some("Custom title"),
                heading = Some("Custom heading"),
                countryLabel = Some("Custom country label"),
                submitLabel = Some("Custom submit label")
              ),

              SelectPageLabels(
                title = Some("Choose address"),
                heading = Some("Choose address"),
                searchAgainLinkText = Some("Search again"),
                editAddressLinkText = Some("Enter address manually")
              ),

              LookupPageLabels(
                title = Some("Find address"),
                heading = Some("Find address"),
                filterLabel = Some("Property name or number (optional)"),
                submitLabel = Some("Find address"),
                manualAddressLinkText = Some("Enter address manually")
              ),

              EditPageLabels(
                title = Some("Enter address"),
                heading = Some("Enter address"),
                line1Label = Some("Address line 1"),
                line2Label = Some("Address line 2 (optional)"),
                line3Label = Some("Address line 3 (optional)")
              ),

              ConfirmPageLabels(
                title = Some("Confirm address"),
                heading = Some("Review and confirm"),
                submitLabel = Some("Confirm address"),
                changeLinkText = Some("Edit address")
              )
            )),
            cy = None
          )
        )

      MockAddressLookupFrontendConfigBuilderService.buildConfig(testHandbackLocation).returns(config)
      MockAddressLookupFrontendConnector.initialiseJourney(config).returns(Future.successful(Right(url)))

      val res: Either[ErrorResponse, String] = await(TestService.initialiseJourney(handbackLocation = testHandbackLocation))

      res.right.value mustBe url
    }
  }

}
