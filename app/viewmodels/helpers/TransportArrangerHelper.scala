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

package viewmodels.helpers

import models.requests.DataRequest
import models.sections.info.movementScenario.MovementScenario.UnknownDestination
import models.sections.transportArranger.TransportArranger
import pages.sections.info.DestinationTypePage
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text
import uk.gov.hmrc.govukfrontend.views.viewmodels.radios.RadioItem

import javax.inject.Inject

class TransportArrangerHelper @Inject() {

  def radioItems()(implicit request: DataRequest[_], messages: Messages): Seq[RadioItem] = {
    val values: Seq[TransportArranger] = if (DestinationTypePage.value.contains(UnknownDestination)) {
      TransportArranger.valuesForUnknownDestination
    } else {
      TransportArranger.values
    }

    values.zipWithIndex.map {
      case (value: TransportArranger, index: Int) =>
        RadioItem(
          content = Text(messages(key = s"transportArranger.${value.toString}")),
          value = Some(value.toString),
          id = Some(s"value_$index")
        )
    }

  }
}
