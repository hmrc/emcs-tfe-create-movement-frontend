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

package fixtures.messages.sections.transportUnit

import fixtures.messages.{BaseEnglish, BaseMessages, SectionMessages, i18n}
import models.Index

object TransportUnitGiveMoreInformationMessages {

  sealed trait ViewMessages extends BaseMessages { _: i18n =>
    def heading(transportType: String): String = s"Give more information about this $transportType (optional)"
    def title(transportType: String): String = titleHelper(heading(transportType), Some(SectionMessages.English.transportUnitSubHeading))
    val hintText = "You must give more information about the transport if you know it. For example, the identity of the subsequent transporter or information about subsequent transport units."
    val cyaLabel = "More information"
    val valueWhenAnswerNotPresent = "Enter more information about the transport unit (optional)"
    def cyaChangeHidden(idx: Index) = s"more information for transport unit ${idx.displayIndex}"
  }

  object English extends ViewMessages with BaseEnglish


}
