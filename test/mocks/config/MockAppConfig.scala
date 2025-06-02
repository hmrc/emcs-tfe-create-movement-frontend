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

package mocks.config

import config.AppConfig
import featureswitch.core.models.FeatureSwitch
import org.scalamock.handlers.{CallHandler0, CallHandler1}
import org.scalamock.scalatest.MockFactory

import java.time.LocalDate

trait MockAppConfig extends MockFactory {
  lazy val mockAppConfig: AppConfig = mock[AppConfig]

  object MockAppConfig {
    def loginUrl: CallHandler0[String] = (() => mockAppConfig.loginUrl).expects()
    def loginContinueUrl: CallHandler1[String, String] = (mockAppConfig.loginContinueUrl(_: String)).expects(*)
    def destinationOfficeSuffix: CallHandler0[String] = (() => mockAppConfig.destinationOfficeSuffix).expects()
    def maxDispatchDateFutureDays: CallHandler0[Int] = (() => mockAppConfig.maxDispatchDateFutureDays).expects()
    def earliestDispatchDate: CallHandler0[LocalDate] = (() => mockAppConfig.earliestDispatchDate).expects()
    def nrsBrokerBaseUrl: CallHandler0[String] = (() => mockAppConfig.nrsBrokerBaseUrl()).expects()

    def emcsTfeHomeUrl: CallHandler0[String] = (() => mockAppConfig.emcsTfeHomeUrl).expects()

    def getFeatureSwitchValue(feature: FeatureSwitch): CallHandler1[String, Boolean] = {
      val featureSwitchName = feature.configName
      (mockAppConfig.getFeatureSwitchValue(_: String)).expects(featureSwitchName)
    }

    def maxTemplates: CallHandler0[Int] = (() => mockAppConfig.maxTemplates).expects()
  }
}
