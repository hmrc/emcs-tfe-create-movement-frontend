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

package forms.sections.templates

import config.Constants.TEMPLATE_NAME_MAX_LENGTH
import forms.behaviours.BooleanFieldBehaviours
import forms.TEMPLATE_NAME_REGEX
import models.sections.templates.SaveTemplateModel
import play.api.data.FormError

class SaveTemplateFormProviderSpec extends BooleanFieldBehaviours {

  val form = new SaveTemplateFormProvider()(Seq("template1", "template2"))

  "when binding 'Yes'" - {

    "when template name contains invalid characters" - {

      "must error when binding the form" in {

        val boundForm = form.bind(Map(
          SaveTemplateFormProvider.saveTemplateField -> "true",
          SaveTemplateFormProvider.templateNameField -> "<"
        ))

        boundForm.errors mustBe Seq(FormError(
          SaveTemplateFormProvider.templateNameField,
          SaveTemplateFormProvider.templateNameInvalid,
          Seq(TEMPLATE_NAME_REGEX)
        ))
      }
    }

    "when template name is too long" - {

      "must error when binding the form" in {

        val boundForm = form.bind(Map(
          SaveTemplateFormProvider.saveTemplateField -> "true",
          SaveTemplateFormProvider.templateNameField -> "a" * (TEMPLATE_NAME_MAX_LENGTH + 1)
        ))

        boundForm.errors mustBe Seq(FormError(
          SaveTemplateFormProvider.templateNameField,
          SaveTemplateFormProvider.templateNameTooLong,
          Seq(TEMPLATE_NAME_MAX_LENGTH)
        ))
      }
    }

    "when template name is NOT provided" - {

      "must error when binding the form" in {

        val boundForm = form.bind(Map(
          SaveTemplateFormProvider.saveTemplateField -> "true"
        ))

        boundForm.errors mustBe Seq(FormError(
          SaveTemplateFormProvider.templateNameField,
          SaveTemplateFormProvider.templateNameRequired
        ))
      }
    }

    "when template name matches one that already exists" - {

      "must error when binding the form" in {

        val boundForm = form.bind(Map(
          SaveTemplateFormProvider.saveTemplateField -> "true",
          SaveTemplateFormProvider.templateNameField -> "template1"
        ))

        boundForm.errors mustBe Seq(FormError(
          SaveTemplateFormProvider.templateNameField,
          SaveTemplateFormProvider.templateNameDuplicate
        ))
      }
    }

    "when template name is valid" - {

      "must bind the form successfully when true with value" in {

        val boundForm = form.bind(Map(
          SaveTemplateFormProvider.saveTemplateField -> "true",
          SaveTemplateFormProvider.templateNameField -> "template3"
        ))

        boundForm.errors mustBe Seq()

        boundForm.value mustBe Some(SaveTemplateModel(value = true, Some("template3")))
      }
    }
  }

  "when binding 'No'" - {

    "must bind the form successfully when false with value (should be transformed to None on bind)" in {

      val boundForm = form.bind(Map(
        SaveTemplateFormProvider.saveTemplateField -> "false",
        SaveTemplateFormProvider.templateNameField -> "template"
      ))

      boundForm.errors mustBe Seq()
      boundForm.value mustBe Some(SaveTemplateModel(value = false, None))
    }

    "must bind the form successfully when false with NO value" in {

      val boundForm = form.bind(Map(
        SaveTemplateFormProvider.saveTemplateField -> "false"
      ))

      boundForm.errors mustBe Seq()
      boundForm.value mustBe Some(SaveTemplateModel(value = false, None))
    }
  }
}
