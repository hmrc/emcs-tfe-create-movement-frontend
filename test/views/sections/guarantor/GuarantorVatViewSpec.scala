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

package views.sections.guarantor

import base.SpecBase
import fixtures.messages.sections.guarantor.GuarantorVatMessages
import forms.sections.guarantor.GuarantorVatFormProvider
import models.NormalMode
import models.requests.DataRequest
import models.sections.guarantor.GuarantorArranger.{GoodsOwner, Transporter}
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.i18n.Messages
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import views.html.sections.guarantor.GuarantorVatView
import views.{BaseSelectors, ViewBehaviours}

class GuarantorVatViewSpec extends SpecBase with ViewBehaviours {
  object Selectors extends BaseSelectors

  "GuarantorVatView" - {

    Seq(GuarantorVatMessages.English).foreach { messagesForLanguage =>

      s"when being rendered in lang code of '${messagesForLanguage.lang.code}'" - {

        Seq(GoodsOwner, Transporter).foreach { implicit validGuarantorArranger =>
          s"with a transport arranger of `${validGuarantorArranger.getClass.getSimpleName}`" - {

            implicit val msgs: Messages = messages(Seq(messagesForLanguage.lang))
            implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(), emptyUserAnswers)

           lazy val view = app.injector.instanceOf[GuarantorVatView]
            val form = app.injector.instanceOf[GuarantorVatFormProvider].apply(validGuarantorArranger)

            implicit val doc: Document = Jsoup.parse(
              view(
                form = form,
                guarantorArranger = validGuarantorArranger,
                mode = NormalMode
              ).toString())

            behave like pageWithExpectedElementsAndMessages(Seq(
              Selectors.title -> messagesForLanguage.title,
              Selectors.h1 -> messagesForLanguage.heading,
              Selectors.hint -> messagesForLanguage.hint,
              Selectors.radioButton(1) -> messagesForLanguage.yes,
              Selectors.label(GuarantorVatFormProvider.vatNumberField) -> messagesForLanguage.label,
              //Note, this is radio button 2 but index is 3 due to hidden HTML conditional content for radio 1
              Selectors.radioButton(3) -> messagesForLanguage.no,
              Selectors.button -> messagesForLanguage.saveAndContinue,
              Selectors.saveAndExitLink -> messagesForLanguage.returnToDraft
            ))
          }
        }
      }
    }
  }
}

