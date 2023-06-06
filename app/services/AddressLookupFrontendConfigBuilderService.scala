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

import config.AppConfig
import models.addressLookupFrontend._
import play.api.i18n.{Lang, MessagesApi}
import play.api.mvc.Call
import utils.MessagesUtil.optionalMessage

import javax.inject.{Inject, Singleton}

@Singleton
class AddressLookupFrontendConfigBuilderService @Inject()(appConfig: AppConfig) {

  private val english = Lang("en")
  private val welsh = Lang("cy")

  // scalastyle:off
  def buildConfig(handbackLocation: Call)(implicit messagesApi: MessagesApi): AddressLookupFrontendJourneyConfig = {

    val selectPageConfig = SelectPageConfig(
      proposalListLimit = 30,
      showSearchAgainLink = true
    )

    val confirmPageConfig = ConfirmPageConfig(
      showSubHeadingAndInfo = false,
      showSearchAgainLink = false,
      showChangeLink = true
    )

    val timeoutConfig = TimeoutConfig(
      timeoutAmount = appConfig.timeout,
      timeoutUrl = appConfig.signOutUrl
    )

    val journeyOptions = JourneyOptions(
      continueUrl = s"${appConfig.selfUrl}$handbackLocation",
      homeNavHref = Some("http://www.hmrc.gov.uk/"),
      accessibilityFooterUrl = Some(appConfig.accessibilityStatementUrl),
      showPhaseBanner = Some(true),
      alphaPhase = Some(false),
      includeHMRCBranding = Some(true),
      showBackButtons = Some(true),
      deskProServiceName = Some(appConfig.deskproName),
      selectPageConfig = Some(selectPageConfig),
      confirmPageConfig = Some(confirmPageConfig),
      timeoutConfig = Some(timeoutConfig),
      disableTranslations = Some(!appConfig.languageTranslationEnabled)
    )

    def appLevelLabels(lang: Lang) =
      AppLevelLabels(
        navTitle = optionalMessage("service.name", lang)(messagesApi),
        phaseBannerHtml = optionalMessage("This is a new service. " +
          "Help us improve it - send your" +
          " <a href='https://www.tax.service.gov.uk/contact/beta-feedback?service=emcstfe'>feedback</a>.", lang
        )(messagesApi))

    def countryPickerLabels(lang: Lang) =
      CountryPickerLabels(
        title = optionalMessage("addressLookup.countryPicker.title", lang)(messagesApi),
        heading = optionalMessage("addressLookup.countryPicker.heading", lang)(messagesApi),
        countryLabel = optionalMessage("addressLookup.countryPicker.country", lang)(messagesApi),
        submitLabel = optionalMessage("addressLookup.countryPicker.submit", lang)(messagesApi)
      )

    def lookupPageLabels(lang: Lang) =
      LookupPageLabels(
        title = optionalMessage("addressLookup.lookup.title", lang)(messagesApi),
        heading = optionalMessage("addressLookup.lookup.heading", lang)(messagesApi),
        filterLabel = optionalMessage("addressLookup.lookup.filter", lang)(messagesApi),
        submitLabel = optionalMessage("addressLookup.lookup.submit", lang)(messagesApi),
        manualAddressLinkText = optionalMessage("addressLookup.lookup.manual", lang)(messagesApi)
      )

    def selectPageLabels(lang: Lang) =
      SelectPageLabels(
        title = optionalMessage("addressLookup.select.title", lang)(messagesApi),
        heading = optionalMessage("addressLookup.select.heading", lang)(messagesApi),
        searchAgainLinkText = optionalMessage("addressLookup.select.searchAgain", lang)(messagesApi),
        editAddressLinkText = optionalMessage("addressLookup.select.editAddress", lang)(messagesApi)
      )

    def editPageLabels(lang: Lang) =
      EditPageLabels(
        title = optionalMessage("addressLookup.edit.title", lang)(messagesApi),
        heading = optionalMessage("addressLookup.edit.heading", lang)(messagesApi),
        line1Label = optionalMessage("addressLookup.edit.line1", lang)(messagesApi),
        line2Label = optionalMessage("addressLookup.edit.line2", lang)(messagesApi),
        line3Label = optionalMessage("addressLookup.edit.line3", lang)(messagesApi)
      )

    def confirmPageLabels(lang: Lang) =
      ConfirmPageLabels(
        title = optionalMessage("addressLookup.confirm.title", lang)(messagesApi),
        heading = optionalMessage("addressLookup.confirm.heading", lang)(messagesApi),
        submitLabel = optionalMessage("addressLookup.confirm.continue", lang)(messagesApi),
        changeLinkText = optionalMessage("addressLookup.confirm.change", lang)(messagesApi)
      )

    val journeyLabels = JourneyLabels(
      en = Some(LanguageLabels(
        appLevelLabels(english),
        countryPickerLabels(english),
        selectPageLabels(english),
        lookupPageLabels(english),
        editPageLabels(english),
        confirmPageLabels(english)
      )),
      cy = Some(LanguageLabels(
        appLevelLabels(welsh),
        countryPickerLabels(welsh),
        selectPageLabels(welsh),
        lookupPageLabels(welsh),
        editPageLabels(welsh),
        confirmPageLabels(welsh)
      ))
    )

    AddressLookupFrontendJourneyConfig(
      version = AddressLookupFrontendJourneyConfig.defaultConfigVersion,
      options = journeyOptions,
      labels = journeyLabels
    )

  }
  // scalastyle:on
}
