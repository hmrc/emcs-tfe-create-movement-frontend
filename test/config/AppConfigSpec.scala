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
import featureswitch.core.config.{FeatureSwitching, RedirectToFeedbackSurvey}
import org.scalamock.scalatest.MockFactory
import org.scalatest.BeforeAndAfterEach
import play.api.test.FakeRequest
import play.api.test.Helpers.GET

class AppConfigSpec extends SpecBase with BeforeAndAfterEach with FeatureSwitching with MockFactory {

  override val config: AppConfig = appConfig

  "AppConfig" - {

    ".deskproName must be emcstfe" in {
      config.deskproName mustBe "emcstfe"
    }

    ".feedbackFrontendSurveyUrl() must handoff to feedback frontend with the correct URL" in {
      config.feedbackFrontendSurveyUrl mustBe s"http://localhost:9514/feedback/${config.deskproName}/beta"
    }

    ".signOutUrl()" - {
      "should have the correct url" in {
        appConfig.signOutUrl mustBe "http://localhost:8308/gg/sign-out"
      }
    }

    ".emcsTfeHomeUrl" - {
      "should generate the correct url" in {
        appConfig.emcsTfeHomeUrl mustBe "http://localhost:8310/emcs/account"
      }
    }

    ".emcsTfeMessagesUrl" - {
      "should generate the correct url" in {
        appConfig.emcsTfeMessagesUrl(testErn) mustBe s"http://localhost:8310/emcs/account/trader/$testErn/messages"
      }
    }

    ".emcsTfeDraftsUrl" - {
      "should generate the correct url" in {
        appConfig.emcsTfeDraftsUrl(testErn) mustBe s"http://localhost:8310/emcs/account/trader/$testErn/draft-movements"
      }
    }

    ".emcsTfeMovementsUrl" - {
      "should generate the correct url" in {
        appConfig.emcsTfeMovementsUrl(testErn) mustBe s"http://localhost:8310/emcs/account/trader/$testErn/movements"
      }
    }

  }

}
