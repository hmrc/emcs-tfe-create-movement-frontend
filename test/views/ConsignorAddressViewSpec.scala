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

package views

import base.ViewSpecBase
import fixtures.messages.ConsignorAddressMessages
import forms.ConsignorAddressFormProvider
import models.NormalMode
import models.requests.DataRequest
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.i18n.Messages
import play.api.test.FakeRequest
import views.html.ConsignorAddressView

class ConsignorAddressViewSpec extends ViewSpecBase with ViewBehaviours {

  object Selectors extends BaseSelectors

  "Consignor Address View" - {

    Seq(ConsignorAddressMessages.English, ConsignorAddressMessages.Welsh).foreach { messagesForLanguage =>

      s"when being rendered in lang code of '${messagesForLanguage.lang.code}'" - {

        implicit val msgs: Messages = messages(app, messagesForLanguage.lang)
        implicit val request: DataRequest[_] = dataRequest(FakeRequest(), emptyUserAnswers)

        val view = app.injector.instanceOf[ConsignorAddressView]
        val form = app.injector.instanceOf[ConsignorAddressFormProvider].apply()

        implicit val doc: Document = Jsoup.parse(view(
          form = form,
          call = controllers.routes.ConsignorAddressController.onSubmit(request.ern, request.lrn, NormalMode)).toString()
        )

        behave like pageWithExpectedElementsAndMessages(Seq(
          Selectors.title -> messagesForLanguage.title,
          Selectors.h1 -> messagesForLanguage.heading,
          Selectors.h2(1) -> messagesForLanguage.subheading,
          Selectors.label("property") -> messagesForLanguage.property,
          Selectors.label("street") -> messagesForLanguage.street,
          Selectors.label("town") -> messagesForLanguage.town,
          Selectors.label("postcode") -> messagesForLanguage.postcode,
          Selectors.button -> messagesForLanguage.saveAndContinue,
          Selectors.link(1) -> messagesForLanguage.savePreviousAnswersAndExit
        ))
      }
    }
  }
}
