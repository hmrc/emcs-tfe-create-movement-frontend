package forms.sections.items

import forms.behaviours.OptionFieldBehaviours
import models.ItemDesignationOfOrigin
import play.api.data.FormError

class ItemDesignationOfOriginFormProviderSpec extends OptionFieldBehaviours {

  val form = new ItemDesignationOfOriginFormProvider()()

  ".value" - {

    val fieldName = "value"
    val requiredKey = "itemDesignationOfOrigin.error.required"

    behave like optionsField[ItemDesignationOfOrigin](
      form,
      fieldName,
      validValues  = ItemDesignationOfOrigin.values,
      invalidError = FormError(fieldName, "error.invalid")
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )
  }
}
