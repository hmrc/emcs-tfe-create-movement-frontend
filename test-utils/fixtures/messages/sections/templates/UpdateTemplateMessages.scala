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

import fixtures.messages.{BaseEnglish, BaseMessages, i18n}

object UpdateTemplateMessages {

  sealed trait ViewMessages extends BaseMessages { _: i18n =>
    val heading = "Update template"
    val title = titleHelper(heading)
    val p1: String => String = name => s"This draft movement was created from template ($name)"
    val label: String = "Do you want to update the template with new information entered in this movement?"

    val requiredError = "Select yes if you would like to update this template"
  }

  object English extends ViewMessages with BaseEnglish
}
