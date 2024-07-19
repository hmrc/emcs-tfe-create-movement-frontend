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

import models.GoodsType
import models.GoodsType.{Beer, Energy, Wine}
import models.requests.DataRequest
import models.sections.info.movementScenario.MovementScenario
import pages.sections.guarantor.GuarantorRequiredPage
import pages.sections.info.DestinationTypePage

sealed trait ExciseProductCodeRules {
  def shouldDisplayInset()(implicit request: DataRequest[_]): Boolean
  def shouldResetGuarantorSectionOnSubmission(exciseProductCode: String)(implicit request: DataRequest[_]): Boolean
}

object ExciseProductCodeRules {
  object UKNoGuarantorRules extends ExciseProductCodeRules {
    def shouldDisplayInset()(implicit request: DataRequest[_]): Boolean =
      GuarantorRequiredPage.guarantorIsOptionalUKtoUK && GuarantorRequiredPage.is(false)

    def shouldResetGuarantorSectionOnSubmission(exciseProductCode: String)(implicit request: DataRequest[_]): Boolean =
      shouldDisplayInset() && !Seq(Beer, Wine).contains(GoodsType(exciseProductCode))
  }

  object NINoGuarantorRules extends ExciseProductCodeRules {
    def shouldDisplayInset()(implicit request: DataRequest[_]): Boolean =
      GuarantorRequiredPage.guarantorIsOptionalNIToEU && GuarantorRequiredPage.is(false)

    def shouldResetGuarantorSectionOnSubmission(exciseProductCode: String)(implicit request: DataRequest[_]): Boolean =
      shouldDisplayInset() && GoodsType(exciseProductCode) != Energy
  }

  object UnknownDestinationRules extends ExciseProductCodeRules {
    def shouldDisplayInset()(implicit request: DataRequest[_]): Boolean =
      DestinationTypePage.is(MovementScenario.UnknownDestination)

    def shouldResetGuarantorSectionOnSubmission(exciseProductCode: String)(implicit request: DataRequest[_]): Boolean = false
  }
}