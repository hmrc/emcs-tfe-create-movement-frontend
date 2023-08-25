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
import config.AppConfig
import models.addressLookupFrontend._
import org.scalamock.scalatest.MockFactory
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.i18n.MessagesApi

class AddressLookupFrontendConfigBuilderServiceSpec() extends SpecBase with GuiceOneAppPerSuite with MockFactory {

  implicit val messages: MessagesApi = app.injector.instanceOf[MessagesApi]

  trait Test {
    val appConfig: AppConfig
    object TestService extends AddressLookupFrontendConfigBuilderService(appConfig)
  }

  "buildConfig" - {
    "return a filled AlfJourneyConfig model" in new Test {
      override val appConfig: AppConfig = app.injector.instanceOf[AppConfig]

      val result: AddressLookupFrontendJourneyConfig = TestService.buildConfig(
        handbackLocation = testOnwardRoute
      )

      val expectedConfig: AddressLookupFrontendJourneyConfig =
        AddressLookupFrontendJourneyConfig(
          version = AddressLookupFrontendJourneyConfig.defaultConfigVersion,
          options =
            JourneyOptions(
              continueUrl = "http://localhost:8314/foo",
              homeNavHref = Some("http://www.hmrc.gov.uk/"),
              accessibilityFooterUrl = Some("http://localhost:12346/emcs-tfe-create-movement-frontend"),
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
                  timeoutAmount = 9000,
                  timeoutUrl = "http://localhost:8308/gg/sign-out"
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
                manualAddressLinkText = Some("Enter the address manually")
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
            cy = Some(LanguageLabels(
              appLevelLabels = AppLevelLabels(
                navTitle = Some("Excise Movement and Control System"),
                phaseBannerHtml = None
              ),

              CountryPickerLabels(
                title = Some("Custom title - welsh"),
                heading = Some("Custom heading - welsh"),
                countryLabel = Some("Custom country label - welsh"),
                submitLabel = Some("Custom submit label - welsh")
              ),

              SelectPageLabels(
                title = Some("Choose address - welsh"),
                heading = Some("Choose address - welsh"),
                searchAgainLinkText = Some("Search again - welsh"),
                editAddressLinkText = Some("Enter address manually - welsh")
              ),

              LookupPageLabels(
                title = Some("Find address - welsh"),
                heading = Some("Find address - welsh"),
                filterLabel = Some("Property name or number (optional) - welsh"),
                submitLabel = Some("Find address - welsh"),
                manualAddressLinkText = Some("Enter the address manually - welsh")
              ),

              EditPageLabels(
                title = Some("Enter address - welsh"),
                heading = Some("Enter address - welsh"),
                line1Label = Some("Address line 1 - welsh"),
                line2Label = Some("Address line 2 (optional) - welsh"),
                line3Label = Some("Address line 3 (optional) - welsh")
              ),

              ConfirmPageLabels(
                title = Some("Confirm address - welsh"),
                heading = Some("Review and confirm - welsh"),
                submitLabel = Some("Confirm address - welsh"),
                changeLinkText = Some("Edit address - welsh")
              )
            ))
          )
        )

      result mustBe expectedConfig

    }
  }

  "continueUrl" - {
    "must handle HTTPS" in new Test {
      override val appConfig: AppConfig = mock[AppConfig]

      (appConfig.selfUrl _).expects().returns("https://example.com:433")

      TestService.continueUrl("/foo") mustBe "/foo"
    }

    "must handle HTTP" in new Test {
      override val appConfig: AppConfig = mock[AppConfig]

      (appConfig.selfUrl _).expects().returns("http://localhost:999999")

      TestService.continueUrl("/foo") mustBe "http://localhost:999999/foo"
    }
  }

}
