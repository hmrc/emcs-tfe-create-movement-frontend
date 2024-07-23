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

package pages.sections.journeyType

import models.requests.DataRequest
import models.sections.journeyType.HowMovementTransported.Other
import pages.sections.Section
import play.api.libs.json.{JsObject, JsPath}
import viewmodels.taskList.{Completed, InProgress, NotStarted, TaskListStatus}

case object JourneyTypeSection extends Section[JsObject] {
  override val path: JsPath = JsPath \ "journeyType"

  override def status(implicit request: DataRequest[_]): TaskListStatus = {
    val pageAnswersExist = List(
      HowMovementTransportedPage.value.isDefined,
      JourneyTimeDaysPage.value.isDefined || JourneyTimeHoursPage.value.isDefined
    ) ++ {
      if (HowMovementTransportedPage.value.contains(Other)) {
        // GiveInformationOtherTransportPage is only mandatory when Other is selected
        List(GiveInformationOtherTransportPage.value.isDefined)
      } else {
        // if Other isn't selected
        List()
      }
    }

    if (pageAnswersExist.forall(identity)) {
      Completed
    } else if (pageAnswersExist.exists(identity)) {
      InProgress
    } else {
      NotStarted
    }
  }

  override def canBeCompletedForTraderAndDestinationType(implicit request: DataRequest[_]): Boolean = true
}
