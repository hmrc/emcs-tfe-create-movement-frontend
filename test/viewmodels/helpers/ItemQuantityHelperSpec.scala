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

import base.SpecBase
import fixtures.ItemFixtures
import models.GoodsType
import models.UnitOfMeasure.Litres20
import models.response.referenceData.CnCodeInformation
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar.mock
import play.api.i18n.Messages

class ItemQuantityHelperSpec extends SpecBase with ItemFixtures {

  "should return the correct heading when a specific excise product code message is defined" in {
    val cnCodeInfo = CnCodeInformation("specificCode", "specificDescription", "specificEpc", "specificEpcDescription", Litres20)
    implicit val messages: Messages = mock[Messages]

    when(messages.isDefinedAt("itemQuantity.heading.specificEpc")).thenReturn(true)
    when(messages("itemQuantity.heading.specificEpc")).thenReturn("Specific Heading")

    val helper = new ItemQuantityHelper
    val result = helper.heading(cnCodeInfo)

    result mustBe Some("Specific Heading")
  }

  "should return None when a specific excise product code heading is not defined" in {
    val cnCodeInfo = CnCodeInformation("unknownCode", "unknownCodeDescription", "undefinedEpc", "specificEpcDescription", Litres20)
    implicit val messages: Messages = mock[Messages]

    when(messages.isDefinedAt("itemQuantity.heading.undefinedEpc")).thenReturn(false)

    val helper = new ItemQuantityHelper
    val result = helper.heading(cnCodeInfo)

    result mustBe None
  }

  "should return the correct paragraph when a specific excise product code message is defined" in {
    val cnCodeInfo = CnCodeInformation("specificCode", "specificDescription", "specificEpc", "specificEpcDescription", Litres20)
    implicit val messages: Messages = mock[Messages]

    when(messages.isDefinedAt("itemQuantity.paragraph.specificEpc")).thenReturn(true)
    when(messages("itemQuantity.paragraph.specificEpc")).thenReturn("Specific Paragraph")

    val helper = new ItemQuantityHelper
    val result = helper.paragraph(cnCodeInfo)

    result mustBe Some("Specific Paragraph")
  }

  "should return None when a specific excise product code paragraph is not defined" in {
    val cnCodeInfo = CnCodeInformation("unknownCode", "unknownCodeDescription", "unknownEpc", "specificEpcDescription", Litres20)
    implicit val messages: Messages = mock[Messages]

    when(messages.isDefinedAt("itemQuantity.paragraph.unknownEpc")).thenReturn(false)

    val helper = new ItemQuantityHelper
    val result = helper.paragraph(cnCodeInfo)

    result mustBe None
  }

  "should return the correct label when a specific excise product code message is defined" in {
    val goodsType = GoodsType("someGoodsType")
    val cnCodeInfo = CnCodeInformation("specificCode", "specificDescription", "specificEpc", "specificEpcDescription", Litres20)
    implicit val messages: Messages = mock[Messages]

    when(messages.isDefinedAt("itemQuantity.label.specificEpc")).thenReturn(true)
    when(messages("itemQuantity.label.specificEpc")).thenReturn("Specific Label")

    val helper = new ItemQuantityHelper
    val result = helper.label(goodsType, cnCodeInfo)

    result mustBe "Specific Label"
  }

  "should return the default label when a specific excise product code label is not defined" in {
    val goodsType = GoodsType("someGoodsType")
    val cnCodeInfo = CnCodeInformation("unknownCode", "unknownCodeDescription", "unknownEpc", "specificEpcDescription", Litres20)
    implicit val messages: Messages = mock[Messages]

    when(messages.isDefinedAt("itemQuantity.label.unknownEpc")).thenReturn(false)
    when(messages("itemQuantity.label", goodsType.toSingularOutput())).thenReturn("Default Label")

    val helper = new ItemQuantityHelper
    val result = helper.label(goodsType, cnCodeInfo)

    result mustBe "Default Label"
  }

  "should return the correct hint for liquid products" in {
    val cnCodeInfo = CnCodeInformation("liquidCode", "specificDescription", "B000", "specificEpcDescription", Litres20)
    implicit val messages: Messages = mock[Messages]

    when(messages("unitOfMeasure.litres20.long")).thenReturn("litres")
    when(messages("itemQuantity.hint.liquid", "litres")).thenReturn("Liquid Hint")

    val helper = new ItemQuantityHelper
    val result = helper.hint(cnCodeInfo)

    result mustBe Some("Liquid Hint")
  }

  "should return the correct hint when a specific excise product code message is defined" in {
    val cnCodeInfo = CnCodeInformation("specificCode", "specificDescription", "specificEpc", "specificEpcDescription", Litres20)
    implicit val messages: Messages = mock[Messages]

    when(messages.isDefinedAt("itemQuantity.hint.specificEpc")).thenReturn(true)
    when(messages("itemQuantity.hint.specificEpc")).thenReturn("Specific Hint")

    val helper = new ItemQuantityHelper
    val result = helper.hint(cnCodeInfo)

    result mustBe Some("Specific Hint")
  }

  "should return a None when a specific excise product code hint is not defined and it is not a liquid" in {
    val cnCodeInfo = CnCodeInformation("unknownCode", "unknownCodeDescription", "E600", "specificEpcDescription", Litres20)
    implicit val messages: Messages = mock[Messages]

    when(messages.isDefinedAt("itemQuantity.hint.E600")).thenReturn(false)

    val helper = new ItemQuantityHelper
    val result = helper.hint(cnCodeInfo)

    result mustBe None
  }
}
