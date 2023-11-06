package forms.sections.items

import forms.behaviours.StringFieldBehaviours
import play.api.data.FormError

class CommercialDescriptionFormProviderSpec extends StringFieldBehaviours {

  val requiredKey = "commercialDescription.error.required"
  val lengthKey = "commercialDescription.error.length"
  val maxLength = 350

  val form = new CommercialDescriptionFormProvider()()

  ".value" - {

    val fieldName = "value"

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      "0" * maxLength
    )

    behave like fieldWithMaxLength(
      form,
      fieldName,
      maxLength = maxLength,
      lengthError = FormError(fieldName, lengthKey, Seq(maxLength))
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )
  }
}
