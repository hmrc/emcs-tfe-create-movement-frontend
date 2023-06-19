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

package featureswitch.frontend.config

import config.AppConfig
import featureswitch.frontend.models.FeatureSwitchProvider
import play.api.Configuration
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig

import javax.inject.{Inject, Singleton}

@Singleton
class FeatureSwitchProviderConfig @Inject()(configuration: Configuration, appConfig: AppConfig) {

  val servicesConfig = new ServicesConfig(configuration)

  lazy val selfBaseUrl: String = appConfig.selfUrl

  lazy val selfFeatureSwitchUrl = s"$selfBaseUrl/emcs-tfe-create-movement/test-only/api/feature-switches"

  lazy val emcsTfeReportAReceiptFeatureSwitchUrl =
    s"${servicesConfig.baseUrl("emcs-tfe-report-a-receipt-frontend")}/emcs-tfe-report-receipt/test-only/api/feature-switches"

  lazy val emcsTfeFeatureSwitchUrl =
    s"${servicesConfig.baseUrl("emcs-tfe")}/emcs-tfe/test-only/api/feature-switches"

  lazy val selfFeatureSwitchProvider: FeatureSwitchProvider = FeatureSwitchProvider(
    id = "emcs-tfe-create-movement-frontend",
    appName = "EMCS-TFE Create Movement Frontend",
    url = selfFeatureSwitchUrl
  )

  lazy val emcsTfeFeatureSwitchProvider: FeatureSwitchProvider = FeatureSwitchProvider(
    id = "emcs-tfe",
    appName = "EMCS TFE Backend",
    url = emcsTfeFeatureSwitchUrl
  )

  lazy val emcsTfeReportAReceiptFeatureSwitchProvider: FeatureSwitchProvider = FeatureSwitchProvider(
    id = "emcs-tfe-report-a-receipt-frontend",
    appName = "EMCS TFE Report A Receipt Frontend",
    url = emcsTfeReportAReceiptFeatureSwitchUrl
  )

  lazy val featureSwitchProviders: Seq[FeatureSwitchProvider] =
    Seq(selfFeatureSwitchProvider, emcsTfeFeatureSwitchProvider)

}
