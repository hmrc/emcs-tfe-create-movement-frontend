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

package views.sections.info

import base.SpecBase
import fixtures.messages.sections.info.DestinationTypeMessages
import forms.sections.info.DestinationTypeFormProvider
import models.NormalMode
import models.requests.DataRequest
import models.sections.info.DispatchPlace.{GreatBritain, NorthernIreland}
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.i18n.Messages
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import views.html.sections.info.DestinationTypeView
import views.{BaseSelectors, ViewBehaviours}

class DestinationTypeViewSpec extends SpecBase with ViewBehaviours {

  def h1Helper(i: Int) = s"main form > div > fieldset > legend > h1:nth-of-type($i)"
  def radioButtonHintHelper(radioIndex: Int) = s"main > fieldset > div >.govuk-radios > div:nth-child($radioIndex) > .govuk-hint"

  val directDelivery = 1
  val exemptedOrganisation = 2
  val registeredConsignee = 5
  val temporaryRegisteredConsignee = 9
  val unknownDestionation = 10
  object Selectors extends BaseSelectors

  "Destination Type view" - {

    Seq(DestinationTypeMessages.English).foreach { messagesForLanguage =>

      s"when being rendered in lang code of '${messagesForLanguage.lang.code}'" - {
        Seq("GBWK", "XIWK").foreach {
          ern =>
            s"for ERN starting with $ern" - {
              implicit val msgs: Messages = messages(Seq(messagesForLanguage.lang))
              implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(), ern = s"${ern}123")

             lazy val view = app.injector.instanceOf[DestinationTypeView]
              val form = app.injector.instanceOf[DestinationTypeFormProvider].apply()

              implicit val doc: Document = Jsoup.parse(view(NorthernIreland, form, controllers.sections.info.routes.DestinationTypeController.onPreDraftSubmit(request.ern, NormalMode)).toString())
              if(ern== "XIWK") {

                behave like pageWithExpectedElementsAndMessages(Seq(
                  Selectors.radioButtonHint(directDelivery) -> messagesForLanguage.hint,
                  Selectors.radioButtonHint(exemptedOrganisation) -> messagesForLanguage.hint,
                  Selectors.radioButtonHint(registeredConsignee) -> messagesForLanguage.hint,
                  Selectors.radioButtonHint(temporaryRegisteredConsignee) -> messagesForLanguage.hint,
                  Selectors.radioButtonHint(unknownDestionation) -> messagesForLanguage.hint,

                ))
              }
              behave like pageWithExpectedElementsAndMessages(Seq(
                Selectors.title -> messagesForLanguage.titleMovement,
                Selectors.h1 -> messagesForLanguage.h1,
                h1Helper(1) -> messagesForLanguage.headingMovement,
                Selectors.h2(1) -> messagesForLanguage.movementInformationSection,
                Selectors.h2(2) -> messagesForLanguage.h2,
                Selectors.p(2) -> messagesForLanguage.p1,
                Selectors.bullet(1) -> messagesForLanguage.bullet1,
                Selectors.bullet(2) -> messagesForLanguage.bullet2,
                Selectors.bullet(3) -> messagesForLanguage.bullet3,
                Selectors.p(4) -> messagesForLanguage.p2,
                Selectors.button -> messagesForLanguage.continue
              ))
            }
        }
        Seq("GBRC", "XIRC").foreach {
          ern =>
            s"for ERN starting with $ern" - {
              implicit val msgs: Messages = messages(Seq(messagesForLanguage.lang))
              implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(), ern = s"${ern}123")

             lazy val view = app.injector.instanceOf[DestinationTypeView]
              val form = app.injector.instanceOf[DestinationTypeFormProvider].apply()

              implicit val doc: Document = Jsoup.parse(view(GreatBritain, form, controllers.sections.info.routes.DestinationTypeController.onPreDraftSubmit(request.ern, NormalMode)).toString())
               if(ern== "XIRC") {

                 behave like pageWithExpectedElementsAndMessages(Seq(
                   Selectors.radioButtonHint(directDelivery) -> messagesForLanguage.hint,
                   Selectors.radioButtonHint(exemptedOrganisation) -> messagesForLanguage.hint,
                   Selectors.radioButtonHint(registeredConsignee) -> messagesForLanguage.hint,
                   Selectors.radioButtonHint(temporaryRegisteredConsignee) -> messagesForLanguage.hint,
                   Selectors.radioButtonHint(unknownDestionation) -> messagesForLanguage.hint,

                 ))
               }
              behave like pageWithExpectedElementsAndMessages(Seq(
               // Selectors.title -> messagesForLanguage.titleImport,
                Selectors.h1 -> messagesForLanguage.h1,
                h1Helper(1) -> messagesForLanguage.headingImport,
                Selectors.h2(1) -> messagesForLanguage.movementInformationSection,
                Selectors.h2(2) -> messagesForLanguage.h2,
                Selectors.p(2) -> messagesForLanguage.p1,
                Selectors.bullet(1) -> messagesForLanguage.bullet1,
                Selectors.bullet(2) -> messagesForLanguage.bullet2,
                Selectors.bullet(3) -> messagesForLanguage.bullet3,
                Selectors.p(4) -> messagesForLanguage.p2,
                Selectors.button -> messagesForLanguage.continue
              ))
            }
        }
      }
    }
  }
}
