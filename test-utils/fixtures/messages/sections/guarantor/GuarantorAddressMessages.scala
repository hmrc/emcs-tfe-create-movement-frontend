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

object GuarantorAddressMessages {

  sealed trait ViewMessages extends BaseMessages { _: i18n =>
    def cyaLabel(arranger: GuarantorArranger): String =
      arranger match {
        case GuarantorArranger.Consignor => "Consignor’s details"
        case GuarantorArranger.Consignee => "Consignee’s details"
        case GuarantorArranger.GoodsOwner => "Goods owner’s details"
        case GuarantorArranger.Transporter => "Transporter’s details"
      }

    def cyaChangeHidden(arranger: GuarantorArranger): String =
      arranger match {
        case GuarantorArranger.Consignor => "consignor’s details"
        case GuarantorArranger.Consignee => "consignee’s details"
        case GuarantorArranger.GoodsOwner => "goods owner’s details"
        case GuarantorArranger.Transporter => "transporter’s details"
      }
  }

  object English extends ViewMessages with BaseEnglish
}
