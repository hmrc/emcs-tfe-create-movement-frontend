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

package forms.sections.items

import forms.mappings.Mappings
import models.sections.items.ItemNetGrossMassModel
import play.api.data.Forms.mapping
import play.api.data.{Form, Mapping}

import javax.inject.Inject


class ItemNetGrossMassFormProvider @Inject() extends Mappings {


  private def bigDecimalMapping(name: String): (String, Mapping[BigDecimal]) =
    name -> bigDecimal(s"itemNetGrossMass.$name.error.required", s"itemNetGrossMass.$name.error.invalid")
      .verifying(s"itemNetGrossMass.$name.error.high", _ < BigDecimal("9999999999999999"))
      .verifying(s"itemNetGrossMass.$name.error.low", _ > BigDecimal("0"))
      .verifying(s"itemNetGrossMass.$name.error.decimals", _.scale <= 6)

  def apply(): Form[ItemNetGrossMassModel] =
    Form(
      mapping(
        bigDecimalMapping("netMass"),
        bigDecimalMapping("grossMass")
      )(ItemNetGrossMassModel.apply)(ItemNetGrossMassModel.unapply)
        .verifying(model => model.netMass > model.grossMass)
    )
}
