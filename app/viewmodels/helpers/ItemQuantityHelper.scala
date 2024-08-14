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

package viewmodels.helpers

import models.GoodsType
import models.response.referenceData.CnCodeInformation
import play.api.i18n.Messages
import utils.ExciseProductCodeHelper

class ItemQuantityHelper {

  def heading(goodsType: GoodsType, cnCodeInfo: CnCodeInformation)(implicit messages: Messages): String = {
    val key: String = s"itemQuantity.heading.${cnCodeInfo.exciseProductCode}"
    if (messages.isDefinedAt(key)) {
      messages(key)
    } else {
      messages("itemQuantity.heading", goodsType.toSingularOutput())
    }
  }

  def paragraph(cnCodeInfo: CnCodeInformation)(implicit messages: Messages): Option[String] = {
    val key: String = s"itemQuantity.paragraph.${cnCodeInfo.exciseProductCode}"
    Option.when(messages.isDefinedAt(key))(messages(key))
  }

  def label(cnCodeInfo: CnCodeInformation)(implicit messages: Messages): String = {
    val key: String = s"itemQuantity.label.${cnCodeInfo.exciseProductCode}"
    if (messages.isDefinedAt(key)) {
      messages(key)
    } else {
      ""
    }
  }

  def hint(cnCodeInfo: CnCodeInformation)(implicit messages: Messages): String = {
    if (ExciseProductCodeHelper.isLiquid(cnCodeInfo.exciseProductCode)) {
      messages("itemQuantity.hint.liquid", cnCodeInfo.unitOfMeasure.toLongFormatMessage())
    } else {
      val key = s"itemQuantity.hint.${cnCodeInfo.exciseProductCode}"
      if (messages.isDefinedAt(key)) {
        messages(key)
      } else {
        ""
      }
    }
  }

}
