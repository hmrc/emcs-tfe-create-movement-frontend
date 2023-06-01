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

package config

import base.SpecBase
import featureswitch.core.config.{FeatureSwitching, StubAddressLookupJourney}
import org.scalatest.BeforeAndAfterEach
import play.api.{Application, Configuration}
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig

class AppConfigSpec extends SpecBase with BeforeAndAfterEach with FeatureSwitching {

  override def afterEach(): Unit = {
    disable(StubAddressLookupJourney)
    super.afterEach()
  }

  lazy val app: Application = applicationBuilder().build()
  lazy val servicesConfig: ServicesConfig = app.injector.instanceOf[ServicesConfig]
  lazy val configuration: Configuration = app.injector.instanceOf[Configuration]

  "AppConfig" - {
    object TestConfig extends AppConfig(servicesConfig, configuration)

    ".addressLookupFrontendUrl" - {
      "should generate the correct url" - {
        s"when the $StubAddressLookupJourney feature switch is enabled" in {
          enable(StubAddressLookupJourney)

          TestConfig.addressLookupFrontendUrl mustBe "http://localhost:8308"
        }
        s"when the $StubAddressLookupJourney feature switch is disabled" in {
          disable(StubAddressLookupJourney)

          TestConfig.addressLookupFrontendUrl mustBe "http://localhost:9028"
        }
      }
    }
  }

}
