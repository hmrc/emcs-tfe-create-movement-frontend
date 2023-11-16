package forms.sections.items

import forms.behaviours.StringFieldBehaviours
import play.api.data.FormError

class ItemNetGrossMassFormProviderSpec extends StringFieldBehaviours {

  val requiredKey = "itemNetGrossMass.error.required"
  val lengthKey = "itemNetGrossMass.error.length"
  val maxLength = 100

  val form = new ItemNetGrossMassFormProvider()()

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
