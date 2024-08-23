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

package featureswitch.core.config

import featureswitch.core.models.FeatureSwitch
import play.api.inject.{Binding, Module}
import play.api.{Configuration, Environment}

import javax.inject.Singleton

@Singleton
class FeatureSwitchingModule extends Module with FeatureSwitchRegistry {

  val switches: Seq[FeatureSwitch] = Seq(
    CheckBetaAllowList,
    StubGetTraderKnownFacts,
    RedirectToFeedbackSurvey,
    EnableXIPCInCaM,
    MessageStatisticsNotification,
    EnableNRS
  )

  override def bindings(environment: Environment, configuration: Configuration): Seq[Binding[_]] = {
    Seq(
      bind[FeatureSwitchRegistry].to(this).eagerly()
    )
  }
}

case object CheckBetaAllowList extends FeatureSwitch {
  override val configName: String = "features.checkBetaAllowList"
  override val displayName: String = "Check beta allow list"
}

case object StubGetTraderKnownFacts extends FeatureSwitch {
  override val configName: String = "features.stub-get-trader-known-facts"
  override val displayName: String = "Use stub to get trader known facts"
}

case object RedirectToFeedbackSurvey extends FeatureSwitch {
  override val configName: String = "features.redirectToFeedbackSurvey"
  override val displayName: String = "Enable redirecting to feedback survey"
}

case object EnableXIPCInCaM extends FeatureSwitch {
  override val configName: String = "features.enableXIPCInCaM"
  override val displayName: String = "Enables XIPC users in CaM"
}

case object MessageStatisticsNotification extends FeatureSwitch {
  override val configName: String = "features.messageStatisticsNotification"
  override val displayName: String = "Show the message statistics red notification badge (new messages count)"
}

case object EnableNRS extends FeatureSwitch {
  override val configName: String = "features.enableNRS"
  override val displayName: String = "Enables sending submissions to NRS"
}
