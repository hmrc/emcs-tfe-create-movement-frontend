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

package forms.sections.templates

import config.Constants.TEMPLATE_NAME_MAX_LENGTH
import forms.TEMPLATE_NAME_REGEX
import forms.mappings.Mappings
import forms.sections.templates.SaveTemplateFormProvider._
import models.sections.templates.SaveTemplateModel
import play.api.data.Form
import play.api.data.Forms.mapping
import uk.gov.voa.play.form.ConditionalMappings.mandatoryIfTrue

import javax.inject.Inject

class SaveTemplateFormProvider @Inject() extends Mappings {

  def apply(existingTemplateNames: Seq[String]): Form[SaveTemplateModel] =
    Form(
      mapping(
        saveTemplateField -> boolean("saveTemplate.error.required"),
        templateNameField -> mandatoryIfTrue(
          fieldName = saveTemplateField,
          mapping = text(templateNameRequired)
            .verifying(
              firstError(
                maxLength(TEMPLATE_NAME_MAX_LENGTH, templateNameTooLong),
                regexp(TEMPLATE_NAME_REGEX, templateNameInvalid),
                valueNotInList(existingTemplateNames, templateNameDuplicate)
              )
            )
        )
      )(SaveTemplateModel.apply)(SaveTemplateModel.unapply)
    )
}

object SaveTemplateFormProvider {
  val saveTemplateField = "value"
  val templateNameField = "name"

  val templateNameInvalid = "saveTemplate.error.name.invalid"
  val templateNameTooLong = "saveTemplate.error.name.length"
  val templateNameRequired = "saveTemplate.error.name.required"
  val templateNameDuplicate = "saveTemplate.error.name.duplicate"
}
