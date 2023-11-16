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

import fixtures.messages.sections.items.ItemFiscalMarksChoiceMessages
import forms.behaviours.BooleanFieldBehaviours
import models.GoodsTypeModel.Tobacco
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.data.FormError
import play.api.i18n.{Messages, MessagesApi}

class ItemFiscalMarksChoiceFormProviderSpec extends BooleanFieldBehaviours with GuiceOneAppPerSuite {

  val requiredKey = "itemFiscalMarksChoice.error.required"
  val invalidKey = "error.boolean"

  implicit val messages: Messages = app.injector.instanceOf[MessagesApi].preferred(Seq(ItemFiscalMarksChoiceMessages.English.lang))

  val form = new ItemFiscalMarksChoiceFormProvider().apply(Tobacco)

  ".value" - {

    val fieldName = "value"

    behave like booleanField(
      form,
      fieldName,
      invalidError = FormError(fieldName, invalidKey, Seq(Tobacco.toSingularOutput()))
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey, Seq(Tobacco.toSingularOutput()))
    )
  }
}