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

import base.ViewSpecBase
import fixtures.messages.sections.info.DestinationTypeMessages
import forms.sections.info.DestinationTypeFormProvider
import models.DispatchPlace.GreatBritain
import models.requests.UserRequest
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.i18n.Messages
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import views.html.sections.info.DestinationTypeView
import views.{BaseSelectors, ViewBehaviours}

class DestinationTypeViewSpec extends ViewSpecBase with ViewBehaviours {

  object Selectors extends BaseSelectors

  "Destination Type view" - {

    Seq(DestinationTypeMessages.English, DestinationTypeMessages.Welsh).foreach { messagesForLanguage =>

      s"when being rendered in lang code of '${messagesForLanguage.lang.code}'" - {
        Seq("GBWK", "XIWK").foreach {
          ern =>
            s"for ERN starting with $ern" - {
              implicit val msgs: Messages = messages(app, messagesForLanguage.lang)
              implicit val request: UserRequest[AnyContentAsEmpty.type] = userRequest(FakeRequest()).copy(ern = s"${ern}123")

              val view = app.injector.instanceOf[DestinationTypeView]
              val form = app.injector.instanceOf[DestinationTypeFormProvider].apply()

              implicit val doc: Document = Jsoup.parse(view(GreatBritain, form, controllers.sections.info.routes.DestinationTypeController.onSubmit(request.ern)).toString())

              behave like pageWithExpectedElementsAndMessages(Seq(
                Selectors.title -> messagesForLanguage.titleMovement,
                Selectors.h1 -> messagesForLanguage.headingMovement,
                Selectors.h2(1) -> messagesForLanguage.caption,
                Selectors.button -> messagesForLanguage.continue
              ))
            }
        }
        Seq("GBRC", "XIRC").foreach {
          ern =>
            s"for ERN starting with $ern" - {
              implicit val msgs: Messages = messages(app, messagesForLanguage.lang)
              implicit val request: UserRequest[AnyContentAsEmpty.type] = userRequest(FakeRequest()).copy(ern = s"${ern}123")

              val view = app.injector.instanceOf[DestinationTypeView]
              val form = app.injector.instanceOf[DestinationTypeFormProvider].apply()

              implicit val doc: Document = Jsoup.parse(view(GreatBritain, form, controllers.sections.info.routes.DestinationTypeController.onSubmit(request.ern)).toString())

              behave like pageWithExpectedElementsAndMessages(Seq(
                Selectors.title -> messagesForLanguage.titleImport,
                Selectors.h1 -> messagesForLanguage.headingImport,
                Selectors.h2(1) -> messagesForLanguage.caption,
                Selectors.button -> messagesForLanguage.continue
              ))
            }
        }
      }
    }
  }
}