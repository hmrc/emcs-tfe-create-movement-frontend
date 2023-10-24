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

package views.sections.destination

import base.ViewSpecBase
import fixtures.messages.sections.destination.DestinationWarehouseExciseMessages
import forms.sections.destination.DestinationWarehouseExciseFormProvider
import models.NormalMode
import models.requests.DataRequest
import models.sections.info.movementScenario.MovementScenario.RegisteredConsignee
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.i18n.Messages
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import views.html.sections.destination.DestinationWarehouseExciseView
import views.{BaseSelectors, ViewBehaviours}

class DestinationWarehouseExciseViewSpec extends ViewSpecBase with ViewBehaviours {

  object Selectors extends BaseSelectors

  "Destination Warehouse Excise view" - {

    Seq(DestinationWarehouseExciseMessages.English, DestinationWarehouseExciseMessages.Welsh).foreach { messagesForLanguage =>

      s"when being rendered in lang code of '${messagesForLanguage.lang.code}'" - {

        implicit val msgs: Messages = messages(app, messagesForLanguage.lang)
        implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest())

        val view = app.injector.instanceOf[DestinationWarehouseExciseView]
        val form = app.injector.instanceOf[DestinationWarehouseExciseFormProvider].apply()

        implicit val doc: Document =
          Jsoup.parse(view(
            form,
            controllers.sections.destination.routes.DestinationWarehouseExciseController.onSubmit(request.ern, request.lrn, NormalMode)
          ).toString())

        behave like pageWithExpectedElementsAndMessages(Seq(
          Selectors.title -> messagesForLanguage.title,
          Selectors.h1 -> messagesForLanguage.heading,
          Selectors.h2(1) -> messagesForLanguage.destinationSection,
          Selectors.label("value") -> messagesForLanguage.text,
          Selectors.button -> messagesForLanguage.saveAndContinue,
          Selectors.link(1) -> messagesForLanguage.returnToDraft
        ))
      }
    }
  }
}
