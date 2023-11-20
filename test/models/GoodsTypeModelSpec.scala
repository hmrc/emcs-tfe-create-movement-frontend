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

package models

import base.SpecBase
import fixtures.messages.sections.documents.DocumentsCertificatesMessages
import models.GoodsTypeModel._
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.i18n.Messages

class GoodsTypeModelSpec extends SpecBase with GuiceOneAppPerSuite {

  "GoodsTypeModel" - {

    "can be constructed from EPC for all valid codes" - {

      GoodsTypeModel.apply("W200") mustBe Wine
      GoodsTypeModel.apply("S100") mustBe Spirits
      GoodsTypeModel.apply("I100") mustBe Intermediate
      GoodsTypeModel.apply("T300") mustBe Tobacco
      GoodsTypeModel.apply("E400") mustBe Energy
      GoodsTypeModel.apply("B300") mustBe Beer
    }

    "throws illegal argument error when EPC can't be mapped to GoodsType" in {
      intercept[IllegalArgumentException](GoodsTypeModel.apply("OHNO")).getMessage mustBe
        s"Invalid argument of 'O' received which can not be mapped to a GoodsType"
    }

    Seq(DocumentsCertificatesMessages.English).foreach { messagesForLanguage =>

      s"when being rendered in lang code of '${messagesForLanguage.lang.code}'" - {

        implicit val msgs: Messages = messages(Seq(messagesForLanguage.lang))

        "must output the correct messages" in {

          //TODO: Content to be confirmed by Lexi
          Wine.toSingularOutput() mustBe "wine"
          Wine.toPluralOutput() mustBe "wines"
          Spirits.toSingularOutput() mustBe "spirit"
          Spirits.toPluralOutput() mustBe "spirits"
          Intermediate.toSingularOutput() mustBe "intermediate product"
          Intermediate.toPluralOutput() mustBe "intermediate products"
          Tobacco.toSingularOutput() mustBe "tobacco"
          Tobacco.toPluralOutput() mustBe "tobaccos"
          Energy.toSingularOutput() mustBe "energy"
          Energy.toPluralOutput() mustBe "energies"
          Beer.toSingularOutput() mustBe "beer"
          Beer.toPluralOutput() mustBe "beers"
        }
      }
    }
  }
}
