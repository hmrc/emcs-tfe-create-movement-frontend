package forms.sections.items

import javax.inject.Inject

import forms.mappings.Mappings
import play.api.data.Form

class CommercialDescriptionFormProvider @Inject() extends Mappings {

  def apply(): Form[String] =
    Form(
      "value" -> text("commercialDescription.error.required")
        .verifying(maxLength(350, "commercialDescription.error.length"))
    )
}
