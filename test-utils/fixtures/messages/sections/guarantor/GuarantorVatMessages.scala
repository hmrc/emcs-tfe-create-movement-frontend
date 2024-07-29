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

package fixtures.messages.sections.guarantor

import fixtures.messages.{BaseEnglish, BaseMessages, i18n}
import models.sections.guarantor.GuarantorArranger
import models.sections.guarantor.GuarantorArranger.GoodsOwner


object GuarantorVatMessages {

  sealed trait ViewMessages extends BaseMessages {
    _: i18n =>
    def title()(implicit guarantorArranger: GuarantorArranger): String = guarantorArranger match {
      case GoodsOwner => titleHelper(heading())
      case _ => titleHelper(heading())
    }

    def heading()(implicit guarantorArranger: GuarantorArranger): String = guarantorArranger match {
      case GoodsOwner => "Is the goods owner VAT registered in the UK?"
      case _ => "Is the transporter VAT registered in the UK?"
    }

    val hint = "A UK VAT registration number is 9 numbers, sometimes with ‘GB’ at the start, for example 123456789 or GB123456789."
    val label = "UK VAT registration number"

    val cyaLabel = "VAT registration number"
    val cyaChangeHidden = "VAT registration number"
  }

  object English extends ViewMessages with BaseEnglish
}
