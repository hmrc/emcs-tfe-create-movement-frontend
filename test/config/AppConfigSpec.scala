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
import play.api.Application

class AppConfigSpec extends SpecBase with BeforeAndAfterEach with FeatureSwitching {

  override def afterEach(): Unit = {
    disable(StubAddressLookupJourney)
    super.afterEach()
  }

  lazy val app: Application = applicationBuilder().build()
  lazy val appConfig: AppConfig = app.injector.instanceOf[AppConfig]
  override val config: AppConfig = appConfig

  "AppConfig" - {

    ".deskproName must be emcstfe" in {
      config.deskproName mustBe "emcstfe"
    }

    ".feedbackFrontendSurveyUrl() must handoff to feddback frontend with the correct URL" in {
      config.feedbackFrontendSurveyUrl mustBe s"http://localhost:9514/feedback/${config.deskproName}/beta"
    }

    ".addressLookupFrontendUrl" - {
      "should generate the correct url" - {
        s"when the $StubAddressLookupJourney feature switch is enabled" in {
          enable(StubAddressLookupJourney)

          appConfig.addressLookupFrontendUrl mustBe "http://localhost:8308"
        }
        s"when the $StubAddressLookupJourney feature switch is disabled" in {
          disable(StubAddressLookupJourney)

          appConfig.addressLookupFrontendUrl mustBe "http://localhost:9028"
        }
      }
    }
  }

}
