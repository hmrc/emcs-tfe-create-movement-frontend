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

package models.sections.items

import models.requests.DataRequest
import models.sections.info.DispatchPlace
import models.sections.info.movementScenario.MovementScenario
import pages.sections.guarantor.GuarantorRequiredPage
import pages.sections.info.{DestinationTypePage, DispatchPlacePage}

sealed trait ExciseProductCodeRules {
  def shouldDisplayInset()(implicit request: DataRequest[_]): Boolean
  def shouldResetGuarantorSectionOnSubmission(exciseProductCode: String)(implicit request: DataRequest[_]): Boolean
}

object ExciseProductCodeRules {
  object GBNoGuarantorRules extends ExciseProductCodeRules {
    def shouldDisplayInset()(implicit request: DataRequest[_]): Boolean = {
      request.userAnswers.get(GuarantorRequiredPage) match {
        case Some(false) if request.isGreatBritainErn => true
        case _ => false
      }
    }

    def shouldResetGuarantorSectionOnSubmission(exciseProductCode: String)(implicit request: DataRequest[_]): Boolean = {
      shouldDisplayInset() && (!Set("B000", "W200", "W300").contains(exciseProductCode))
    }
  }

  object NINoGuarantorRules extends ExciseProductCodeRules {
    def shouldDisplayInset()(implicit request: DataRequest[_]): Boolean = {
      (request.userAnswers.get(GuarantorRequiredPage), request.userAnswers.get(DispatchPlacePage)) match {
        case (Some(false), Some(DispatchPlace.NorthernIreland)) if request.isNorthernIrelandErn => true
        case _ => false
      }
    }

    def shouldResetGuarantorSectionOnSubmission(exciseProductCode: String)(implicit request: DataRequest[_]): Boolean = {
      shouldDisplayInset() && (exciseProductCode.toUpperCase.head != 'E')
    }
  }

  object UnknownDestinationRules extends ExciseProductCodeRules {
    def shouldDisplayInset()(implicit request: DataRequest[_]): Boolean = {
      request.userAnswers.get(DestinationTypePage) match {
        case Some(MovementScenario.UnknownDestination) => true
        case _ => false
      }
    }

    def shouldResetGuarantorSectionOnSubmission(exciseProductCode: String)(implicit request: DataRequest[_]): Boolean = false
  }
}