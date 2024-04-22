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

package fixtures.messages.sections.consignee

import fixtures.messages.{BaseEnglish, BaseMessages, i18n}

object ConsigneeBusinessNameMessages {

  sealed trait ViewMessages extends BaseMessages { _: i18n =>
    val heading = "What is the consignee’s business name?"
    val title = titleHelper(heading)
    val errorRequired = "Enter the consignee business name"
    val errorLength = "Consignee business name must be 182 characters or less"
    val errorInvalid = "Consignee business name must not contain < and > and : and ;"

    val cyaLabel: String = "Business name"
    val cyaChangeHidden: String = "consignee business name"
  }

  object English extends ViewMessages with BaseEnglish
}
