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

import play.api.i18n.Messages


object GoodsTypeModel {

  sealed trait GoodsType {
    val code: String
    def toPluralOutput()(implicit messages: Messages): String = messages(s"goodsType.$code.plural")
    def toSingularOutput()(implicit messages: Messages): String = messages(s"goodsType.$code.singular")
  }
  case object Beer extends GoodsType {
    override val code: String = "B"
  }
  case object Wine extends GoodsType {
    override val code: String = "W"
  }
  case object Energy extends GoodsType {
    override val code: String = "E"
  }
  case object Spirits extends GoodsType {
    override val code: String = "S"
  }
  case object Tobacco extends GoodsType {
    override val code: String = "T"
  }
  case object Intermediate extends GoodsType {
    override val code: String = "I"
  }

  def apply(epc: String): GoodsType = epc.take(1) match {
    case Beer.code => Beer
    case Wine.code => Wine
    case Energy.code => Energy
    case Spirits.code => Spirits
    case Tobacco.code => Tobacco
    case Intermediate.code => Intermediate
    case invalid => throw new IllegalArgumentException(s"Invalid argument of '$invalid' received which can not be mapped to a GoodsType")
  }
}
