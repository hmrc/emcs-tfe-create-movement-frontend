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

package viewmodels.helpers

import base.SpecBase
import fixtures.messages.sections.transportArranger.TransportArrangerMessages.English.{consigneeRadioOption, consignorRadioOption, goodsOwnerRadioOption, otherRadioOption}
import models.requests.DataRequest
import models.sections.info.movementScenario.MovementScenario
import models.sections.info.movementScenario.MovementScenario.UnknownDestination
import pages.sections.info.DestinationTypePage
import play.api.i18n.Messages
import play.api.test.FakeRequest
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text
import uk.gov.hmrc.govukfrontend.views.viewmodels.radios.RadioItem

class TransportArrangerHelperSpec extends SpecBase {
  implicit val msgs: Messages = messages(FakeRequest())

  lazy val helper = new TransportArrangerHelper()

  "should render 3 radio options" - {
    s"when the movement scenario destination is ${simpleName(UnknownDestination)}" in {
      implicit val dr: DataRequest[_] = dataRequest(
        request = FakeRequest(),
        answers = emptyUserAnswers.set(DestinationTypePage, UnknownDestination)
      )

      helper.radioItems() mustBe Seq(
        RadioItem(
          content = Text(consignorRadioOption),
          value = Some("1"),
          id = Some(s"value_0")
        ),
        RadioItem(
          content = Text(goodsOwnerRadioOption),
          value = Some("3"),
          id = Some(s"value_1")
        ),
        RadioItem(
          content = Text(otherRadioOption),
          value = Some("4"),
          id = Some(s"value_2")
        )
      )
    }
  }

  "should render 4 radio options" - {
    MovementScenario.values.filterNot(_ == UnknownDestination).foreach { scenario =>
      s"when the movement scenario destination is ${simpleName(scenario)}" in {
        implicit val dr: DataRequest[_] = dataRequest(FakeRequest())

        helper.radioItems() mustBe Seq(
          RadioItem(
            content = Text(consignorRadioOption),
            value = Some("1"),
            id = Some(s"value_0")
          ),
          RadioItem(
            content = Text(consigneeRadioOption),
            value = Some("2"),
            id = Some(s"value_1"),
          ),
          RadioItem(
            content = Text(goodsOwnerRadioOption),
            value = Some("3"),
            id = Some(s"value_2")
          ),
          RadioItem(
            content = Text(otherRadioOption),
            value = Some("4"),
            id = Some(s"value_3")
          )
        )
      }
    }
  }

}
