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

object DeferredMovementMessages {

  sealed trait ViewMessages {
    _: i18n =>
    val title: String
    val heading: String
    val caption: String
    val hint: String
    val summary: String
    val paragraph1: String
    val paragraph2: String
    val errorRequired: String
  }

  object English extends ViewMessages with BaseEnglish {
    override val title = title("Is this a deferred movement?")
    override val heading = "Is this a deferred movement?"
    override val caption = "Movement information"
    override val hint = "A deferred movement is one that was originally made using fallback procedures due to EMCS being unavailable."
    override val summary = "Help with fallback procedures"
    override val paragraph1 = "Fallback is used when goods need to be dispatched and EMCS is unavailable. When this happens the Fallback Accompanying Document (FAD) is completed and printed to travel with the goods."
    override val paragraph2 = "Any information entered on EMCS for a deferred movement must match the information on the FAD."
    override val errorRequired = "Select yes if this is a deferred movement"
  }

  object Welsh extends ViewMessages with BaseWelsh {
    override val title = title("Is this a deferred movement?")
    override val heading = "Is this a deferred movement?"
    override val caption = "Movement information"
    override val hint = "A deferred movement is one that was originally made using fallback procedures due to EMCS being unavailable."
    override val summary = "Help with fallback procedures"
    override val paragraph1 = "Fallback is used when goods need to be dispatched and EMCS is unavailable. When this happens the Fallback Accompanying Document (FAD) is completed and printed to travel with the goods."
    override val paragraph2 = "Any information entered on EMCS for a deferred movement must match the information on the FAD."
    override val errorRequired = "Select yes if this is a deferred movement"
  }
}
