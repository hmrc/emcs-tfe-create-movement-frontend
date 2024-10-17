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

package fixtures.messages.sections.templates

import fixtures.messages.{BaseEnglish, BaseMessages, SectionMessages, i18n}

object UseTemplateMessages {

  sealed trait ViewMessages extends BaseMessages { _: i18n =>
    val heading = "How would you like to create this movement?"
    val title = titleHelper(heading, Some(SectionMessages.English.draftMovementSubHeading))
    val useTemplateNo = "Create a new draft movement"
    val useTemplateNoHint = "You will enter all required information. You can choose to save the draft as a template when submitting."
    val useTemplateYes = "Select a saved draft template"
    val useTemplateYesHint = "You can edit and reuse the movement information saved in the template."
    val errorRequired = "Select how you would like to create this movement"
  }

  object English extends ViewMessages with BaseEnglish
}
