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

package fixtures.messages

object DeclarationMessages {

  sealed trait ViewMessages extends BaseMessages {
    _: i18n =>
    val heading = "Declaration"
    val title: String = titleHelper(heading)
    val content = "By submitting this draft movement you are confirming that to the best of your knowledge, the details you are providing are correct."
    val templateContent = "This movement was created from a draft template. It is therefore important that all details have been checked for accuracy."
    val submit = "Submit draft movement"
    val maxTemplatesReachedP1: Int => String = max => s"You have reached the maximum limit of $max templates. If you would like to save this draft as a new template you must first delete an existing template."
    val maxTemplatesReachedP2: Int => String = max => s"If you exit this draft your answers will be saved and you can return to it later. Once you have managed your templates and are below the maximum limit of $max you will be able to save this draft as a template before submitting."
    val saveAndExitToTemplates = "Save and exit to manage my templates"
  }

  object English extends ViewMessages with BaseEnglish
}
