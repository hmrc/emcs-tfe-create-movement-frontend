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

package forms.sections.transportArranger

import forms.mappings.Mappings
import models.Enumerable
import models.requests.DataRequest
import models.sections.info.movementScenario.MovementScenario.UnknownDestination
import models.sections.transportArranger.TransportArranger
import pages.sections.info.DestinationTypePage
import play.api.data.Form

import javax.inject.Inject

class TransportArrangerFormProvider @Inject() extends Mappings {

  def apply()(implicit request: DataRequest[_]): Form[TransportArranger] = {

    implicit val destinationEnumerable: Enumerable[TransportArranger] =
      if (request.userAnswers.get(DestinationTypePage).contains(UnknownDestination)) {
        TransportArranger.enumerableForUnknownDestination
      } else {
        TransportArranger.enumerable
      }

    Form(
      "value" -> enumerable[TransportArranger]("transportArranger.error.required")(destinationEnumerable)
    )
  }
}