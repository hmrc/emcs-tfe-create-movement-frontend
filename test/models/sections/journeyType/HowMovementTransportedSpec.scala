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

package models.sections.journeyType

import base.SpecBase
import fixtures.messages.sections.journeyType.HowMovementTransportedMessages
import models.sections.info.movementScenario.MovementScenario.UnknownDestination
import models.sections.journeyType.HowMovementTransported._
import org.scalatest.OptionValues
import org.scalatest.matchers.must.Matchers
import pages.sections.info.DestinationTypePage
import play.api.libs.json.{JsError, JsString, Json}
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text
import uk.gov.hmrc.govukfrontend.views.viewmodels.radios.RadioItem

class HowMovementTransportedSpec extends SpecBase with Matchers with OptionValues {

  implicit val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest()

  "HowMovementTransported" - {

    "must deserialise valid values" in {
      val howMovementTransported = HowMovementTransported.values.head

      JsString(howMovementTransported.toString).validate[HowMovementTransported].asOpt.value mustEqual howMovementTransported
    }

    "must fail to deserialise invalid values" in {
      val invalidValue = "beans"

      JsString(invalidValue).validate[HowMovementTransported] mustEqual JsError("error.invalid")
    }

    "must serialise" in {
      val howMovementTransported = HowMovementTransported.values.head

      Json.toJson(howMovementTransported) mustEqual JsString(howMovementTransported.toString)
    }

    "options" - {

      val messagesForLanguage = HowMovementTransportedMessages.English

      "must render the correct radio options when the destination type = Unknown Destination" in {

        HowMovementTransported.options(dataRequest(request, emptyUserAnswers.set(DestinationTypePage, UnknownDestination)), messages(request)) mustBe Seq(
          RadioItem(
            content = Text(messagesForLanguage.radioOption3),
            value = Some(InlandWaterwayTransport.toString),
            id = Some(s"value_${InlandWaterwayTransport.toString}")
          ),
          RadioItem(
            content = Text(messagesForLanguage.radioOption7),
            value = Some(SeaTransport.toString),
            id = Some(s"value_${SeaTransport.toString}")
          )
        )
      }

      "must render the correct radio options when the destination type != Unknown Destination" in {

        val optionsAndMessages = Seq(
          AirTransport -> messagesForLanguage.radioOption1,
          FixedTransportInstallations -> messagesForLanguage.radioOption2,
          InlandWaterwayTransport -> messagesForLanguage.radioOption3,
          PostalConsignment -> messagesForLanguage.radioOption4,
          RailTransport -> messagesForLanguage.radioOption5,
          RoadTransport -> messagesForLanguage.radioOption6,
          SeaTransport -> messagesForLanguage.radioOption7,
          Other -> messagesForLanguage.radioOption8
        )

        HowMovementTransported.options(dataRequest(request), messages(request)) mustBe optionsAndMessages.map { journeyTypeAndMessage =>
          RadioItem(
            content = Text(journeyTypeAndMessage._2),
            value = Some(journeyTypeAndMessage._1.toString),
            id = Some(s"value_${journeyTypeAndMessage._1.toString}")
          )
        }
      }
    }
  }
}
