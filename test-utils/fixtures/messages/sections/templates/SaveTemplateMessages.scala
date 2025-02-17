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

package fixtures.messages.sections.templates

import fixtures.messages.{BaseEnglish, BaseMessages, SectionMessages, i18n}

object SaveTemplateMessages {

  sealed trait ViewMessages extends BaseMessages { _: i18n =>
    val heading = "Do you want to save this draft as a template for future use?"
    val title = titleHelper(heading, Some(SectionMessages.English.draftMovementSubHeading))
    val saveTemplateHint = "Templates allow you to edit and reuse the draft for another movement."
    val errorRequired = "Select how you would like to create this movement"
    val templateNameLabel = "Enter a template name to identify this movement"
  }

  object English extends ViewMessages with BaseEnglish
}
