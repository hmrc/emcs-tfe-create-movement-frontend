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

package views.sections.transportUnit

import base.SpecBase
import fixtures.messages.sections.transportUnit.TransportUnitTypeMessages
import forms.sections.transportUnit.TransportUnitTypeFormProvider
import models.NormalMode
import models.requests.DataRequest
import models.sections.info.movementScenario.MovementScenario.DirectDelivery
import models.sections.journeyType.HowMovementTransported.FixedTransportInstallations
import models.sections.transportUnit.TransportUnitType.FixedTransport
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import pages.sections.guarantor.GuarantorRequiredPage
import pages.sections.info.DestinationTypePage
import pages.sections.journeyType.HowMovementTransportedPage
import pages.sections.transportUnit.TransportUnitTypePage
import play.api.i18n.Messages
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import views.html.sections.transportUnit.TransportUnitTypeView
import views.{BaseSelectors, ViewBehaviours}

class TransportUnitTypeViewSpec extends SpecBase with ViewBehaviours {

  lazy val view = app.injector.instanceOf[TransportUnitTypeView]
  lazy val form = app.injector.instanceOf[TransportUnitTypeFormProvider].apply()

  object Selectors extends BaseSelectors

  "TransportUnitTypeView" - {

    Seq(TransportUnitTypeMessages.English).foreach { messagesForLanguage =>

      s"when being rendered in lang code of '${messagesForLanguage.lang.code}'" - {

        implicit val msgs: Messages = messages(Seq(messagesForLanguage.lang))

        "when adding a second transport unit, when currently only fixed transport and NI to EU" - {

          "should render the complex question pattern with inset text" - {

            implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(
              FakeRequest(),
              emptyUserAnswers
                .copy(ern = testNorthernIrelandErn)
                .set(DestinationTypePage, DirectDelivery)
                .set(GuarantorRequiredPage, false)
                .set(HowMovementTransportedPage, FixedTransportInstallations)
                .set(TransportUnitTypePage(testIndex1), FixedTransport),
              testNorthernIrelandErn
            )

            implicit val doc: Document = Jsoup.parse(
              view(
                form = form,
                idx = testIndex1,
                mode = NormalMode
              ).toString())

            behave like pageWithExpectedElementsAndMessages(Seq(
              Selectors.title -> messagesForLanguage.title,
              Selectors.h1 -> messagesForLanguage.addGuarantorHeading,
              Selectors.inset -> messagesForLanguage.addGuarantorInset,
              Selectors.radioButton(1) -> messagesForLanguage.containerRadioOption,
              Selectors.radioButton(2) -> messagesForLanguage.fixedTransportRadioOption,
              Selectors.radioButton(3) -> messagesForLanguage.tractorRadioOption,
              Selectors.radioButtonHint(3) -> messagesForLanguage.tractorRadioOptionHint,
              Selectors.radioButton(4) -> messagesForLanguage.trailerRadioOption,
              Selectors.radioButton(5) -> messagesForLanguage.vehicleRadioOption,
              Selectors.radioButtonHint(5) -> messagesForLanguage.vehicleRadioOptionHint,
              Selectors.button -> messagesForLanguage.saveAndContinue,
              Selectors.saveAndExitLink -> messagesForLanguage.returnToDraft
            ))
          }
        }

        "in any other scenario" - {

          "should render the simple question pattern with hint text" - {

            implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(), emptyUserAnswers)

            implicit val doc: Document = Jsoup.parse(
              view(
                form = form,
                idx = testIndex1,
                mode = NormalMode
              ).toString())

            behave like pageWithExpectedElementsAndMessages(Seq(
              Selectors.title -> messagesForLanguage.title,
              Selectors.h1 -> messagesForLanguage.heading,
              Selectors.hint -> messagesForLanguage.hint,
              Selectors.radioButton(1) -> messagesForLanguage.containerRadioOption,
              Selectors.radioButton(2) -> messagesForLanguage.fixedTransportRadioOption,
              Selectors.radioButton(3) -> messagesForLanguage.tractorRadioOption,
              Selectors.radioButtonHint(3) -> messagesForLanguage.tractorRadioOptionHint,
              Selectors.radioButton(4) -> messagesForLanguage.trailerRadioOption,
              Selectors.radioButton(5) -> messagesForLanguage.vehicleRadioOption,
              Selectors.radioButtonHint(5) -> messagesForLanguage.vehicleRadioOptionHint,
              Selectors.button -> messagesForLanguage.saveAndContinue,
              Selectors.saveAndExitLink -> messagesForLanguage.returnToDraft
            ))
          }
        }
      }
    }
  }
}

