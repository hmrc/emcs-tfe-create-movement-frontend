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

package forms.sections.documents

import fixtures.messages.sections.documents.DocumentTypeMessages.English
import forms.behaviours.OptionFieldBehaviours
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.data.FormError
import play.api.i18n.{Messages, MessagesApi}

class DocumentTypeFormProviderSpec extends OptionFieldBehaviours with GuiceOneAppPerSuite {

  val form = new DocumentTypeFormProvider()()

  ".value" - {

    val fieldName = "document-type"
    val requiredKey = "documentType.error.required"

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )
  }

  "Error Messages" - {

    implicit val messages: Messages = app.injector.instanceOf[MessagesApi].preferred(Seq(English.lang))

    "have the correct error message for required" in {

      messages("documentType.error.required") mustBe
        English.errorRequired
    }
  }
}
