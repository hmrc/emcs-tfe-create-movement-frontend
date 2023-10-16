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

import fixtures.messages.{BaseEnglish, BaseMessages, BaseWelsh, i18n}

object TransportSealChoiceMessages {

  sealed trait ViewMessages extends BaseMessages { _: i18n =>
    val titleContainer = titleHelper("Is there a commercial seal on this container?")
    val titleVehicle = titleHelper("Is there a commercial seal on this vehicle?")
    val titleTrailer = titleHelper("Is there a commercial seal on this trailer?")
    val titleTractor = titleHelper("Is there a commercial seal on this tractor?")
    val titleFixed = titleHelper("Is there a commercial seal on this fixed transport installation?")
    val headingContainer = "Is there a commercial seal on this container?"
    val headingVehicle = "Is there a commercial seal on this vehicle?"
    val headingTrailer = "Is there a commercial seal on this trailer?"
    val headingTractor = "Is there a commercial seal on this tractor?"
    val headingFixed = "Is there a commercial seal on this fixed transport installation?"
    val errorRequiredContainer = "Select yes if there is a commercial seal on this container?"
    val errorRequiredVehicle = "Select yes if there is a commercial seal on this vehicle?"
    val errorRequiredTrailer = "Select yes if there is a commercial seal on this trailer?"
    val errorRequiredTractor = "Select yes if there is a commercial seal on this tractor?"
    val errorRequiredFixed = "Select yes if there is a commercial seal on this fixed transport installation?"
    val hint = "his is a seal that prevents items being removed or added. For example, a seal on the outermost packaging, or a seal on the transport."
  }

  object English extends ViewMessages with BaseEnglish

  object Welsh extends ViewMessages with BaseWelsh
}
