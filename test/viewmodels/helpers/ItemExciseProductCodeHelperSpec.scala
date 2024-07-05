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
import fixtures.messages.sections.items.ItemExciseProductCodeMessages
import models.requests.DataRequest
import models.sections.info.movementScenario.MovementScenario
import pages.sections.guarantor.GuarantorRequiredPage
import pages.sections.info.DestinationTypePage
import play.api.i18n.Messages
import play.api.test.FakeRequest
import viewmodels.govuk.LabelFluency

class ItemExciseProductCodeHelperSpec extends SpecBase with LabelFluency {

  val helper: ItemExciseProductCodeHelper = app.injector.instanceOf[ItemExciseProductCodeHelper]

  "ItemExciseProductCodeHelper" - {
    Seq(ItemExciseProductCodeMessages.English).foreach { messagesForLanguage =>

      s"when being rendered in lang code of '${messagesForLanguage.lang.code}'" - {

        "insetText" - {
          "must render" - {
            "when request is UK to UK and no guarantor" in {
              implicit val request: DataRequest[_] = dataRequest(
                request = FakeRequest(),
                ern = "GBWK123456789",
                answers = emptyUserAnswers
                  .set(DestinationTypePage, MovementScenario.UkTaxWarehouse.GB)
                  .set(GuarantorRequiredPage, false)
              )
              implicit val msgs: Messages = messages(Seq(messagesForLanguage.lang))
              helper.insetText().value mustBe messagesForLanguage.insetTextGBNoGuarantor
            }
            "when request is UK to EU and no guarantor" in {
              implicit val request: DataRequest[_] = dataRequest(
                request = FakeRequest(),
                ern = "GBWK123456789",
                answers = emptyUserAnswers
                  .set(DestinationTypePage, MovementScenario.DirectDelivery)
                  .set(GuarantorRequiredPage, false)
              )
              implicit val msgs: Messages = messages(Seq(messagesForLanguage.lang))
              helper.insetText().value mustBe messagesForLanguage.insetTextEuNoGuarantor
            }
            "when request is unknown destination" in {
              implicit val request: DataRequest[_] = dataRequest(
                request = FakeRequest(),
                ern = "GBWK123456789",
                answers = emptyUserAnswers
                  .set(DestinationTypePage, MovementScenario.UnknownDestination)
              )
              implicit val msgs: Messages = messages(Seq(messagesForLanguage.lang))
              helper.insetText().value mustBe messagesForLanguage.insetTextUnknownDestination
            }
          }

          "must not render" - {
            "when request is anything else" in {
              implicit val request: DataRequest[_] = dataRequest(
                request = FakeRequest(),
                ern = "GBWK123456789",
                answers = emptyUserAnswers
                  .set(DestinationTypePage, MovementScenario.DirectDelivery)
              )
              implicit val msgs: Messages = messages(Seq(messagesForLanguage.lang))
              helper.insetText() mustBe None
            }
          }
        }
      }
    }
  }
}