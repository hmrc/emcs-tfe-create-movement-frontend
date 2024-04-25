/*
 * Copyright 2024 HM Revenue & Customs
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

package forms.sections.info

import config.AppConfig
import forms.mappings.Constraints
import play.api.data.validation.Constraint
import utils.TimeMachine

import java.time.LocalDate

trait DispatchDateValidation extends Constraints {

  val appConfig: AppConfig
  val timeMachine: TimeMachine

  def maxDateCheck(isDeferredMovement: Boolean): Constraint[LocalDate] = {
    val maxDateValue = if (isDeferredMovement) timeMachine.now() else timeMachine.now().plusDays(appConfig.maxDispatchDateFutureDays)
    maxDate(maxDateValue.toLocalDate, deferredSuffix(isDeferredMovement, s"dispatchDetails.value.error.latestDate"), appConfig.maxDispatchDateFutureDays)
  }

  def minDateCheck(isDeferredMovement: Boolean): Constraint[LocalDate] = {
    val minDateValue = if (isDeferredMovement) appConfig.earliestDispatchDate else timeMachine.now().toLocalDate
    minDate(minDateValue, deferredSuffix(isDeferredMovement, s"dispatchDetails.value.error.earliestDate"))
  }

  private def deferredSuffix(isDeferredMovement: Boolean, msgKey: String) =
    msgKey + (if (isDeferredMovement) ".deferred" else "")

}
