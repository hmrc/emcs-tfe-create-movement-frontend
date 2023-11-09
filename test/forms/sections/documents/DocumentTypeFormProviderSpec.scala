package forms.sections.documents

import forms.behaviours.OptionFieldBehaviours
import models.DocumentType
import play.api.data.FormError

class DocumentTypeFormProviderSpec extends OptionFieldBehaviours {

  val form = new DocumentTypeFormProvider()()

  ".value" - {

    val fieldName = "value"
    val requiredKey = "documentType.error.required"

    behave like optionsField[DocumentType](
      form,
      fieldName,
      validValues  = DocumentType.values,
      invalidError = FormError(fieldName, "error.invalid")
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )
  }
}
