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

package mocks.services

import models.addressLookupFrontend.AddressLookupFrontendJourneyConfig
import org.scalamock.handlers.CallHandler2
import org.scalamock.scalatest.MockFactory
import play.api.i18n.MessagesApi
import play.api.mvc.Call
import services.AddressLookupFrontendConfigBuilderService

trait MockAddressLookupFrontendConfigBuilderService extends MockFactory {

  lazy val mockAddressLookupFrontendConfigBuilderService: AddressLookupFrontendConfigBuilderService = mock[AddressLookupFrontendConfigBuilderService]

  object MockAddressLookupFrontendConfigBuilderService {
    def buildConfig(handbackLocation: Call): CallHandler2[Call, MessagesApi, AddressLookupFrontendJourneyConfig] =
      (mockAddressLookupFrontendConfigBuilderService.buildConfig(_: Call)(_: MessagesApi))
        .expects(handbackLocation, *)
  }

}
