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
import fixtures.messages.AddressMessages
import forms.AddressFormProvider
import models.NormalMode
import models.requests.DataRequest
import models.sections.transportArranger.TransportArranger.{GoodsOwner, Other}
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import pages.sections.consignee.ConsigneeAddressPage
import pages.sections.consignor.ConsignorAddressPage
import pages.sections.dispatch.DispatchAddressPage
import pages.sections.firstTransporter.FirstTransporterAddressPage
import pages.sections.transportArranger.TransportArrangerAddressPage
import play.api.i18n.{Lang, Messages}
import play.api.test.FakeRequest
import views.html.AddressView

class AddressViewSpec extends ViewSpecBase with ViewBehaviours {

  class Fixture(lang: Lang) {
    implicit val msgs: Messages = messages(app, lang)
    implicit val request: DataRequest[_] = dataRequest(FakeRequest(), emptyUserAnswers)

    val view = app.injector.instanceOf[AddressView]
    val form = app.injector.instanceOf[AddressFormProvider].apply()
  }

  object Selectors extends BaseSelectors

  Seq(AddressMessages.English, AddressMessages.Welsh).foreach { messagesForLanguage =>

    s"when being rendered in lang code of '${messagesForLanguage.lang.code}'" - new Fixture(messagesForLanguage.lang) {

      Seq(ConsignorAddressPage, ConsigneeAddressPage) foreach { addressPage =>

        s"$addressPage View" - {

          implicit val doc: Document = Jsoup.parse(view(
            form = form,
            addressPage = addressPage,
            call = controllers.sections.consignor.routes.ConsignorAddressController.onSubmit(request.ern, request.lrn, NormalMode)).toString()
          )

          behave like pageWithExpectedElementsAndMessages(Seq(
            Selectors.title -> messagesForLanguage.title(addressPage),
            Selectors.h1 -> messagesForLanguage.heading(addressPage),
            Selectors.h2(1) -> messagesForLanguage.subheading(addressPage),
            Selectors.label("property") -> messagesForLanguage.property,
            Selectors.label("street") -> messagesForLanguage.street,
            Selectors.label("town") -> messagesForLanguage.town,
            Selectors.label("postcode") -> messagesForLanguage.postcode,
            Selectors.button -> messagesForLanguage.saveAndContinue,
            Selectors.link(1) -> messagesForLanguage.returnToDraft
          ))
        }
      }

      "when rendered for TransportArranger page" - {

        "when the Arranger is the GoodsOwner" - new Fixture(messagesForLanguage.lang) {

          implicit val doc: Document = Jsoup.parse(view(
            form = form,
            addressPage = TransportArrangerAddressPage,
            call = controllers.sections.consignor.routes.ConsignorAddressController.onSubmit(request.ern, request.lrn, NormalMode),
            headingKey = Some(s"$TransportArrangerAddressPage.$GoodsOwner")
          ).toString())

          behave like pageWithExpectedElementsAndMessages(Seq(
            Selectors.title -> messagesForLanguage.transportArrangerAddressGoodsOwnerTitle,
            Selectors.h1 -> messagesForLanguage.transportArrangerAddressGoodsOwnerHeading,
            Selectors.h2(1) -> messagesForLanguage.subheading(TransportArrangerAddressPage),
            Selectors.label("property") -> messagesForLanguage.property,
            Selectors.label("street") -> messagesForLanguage.street,
            Selectors.label("town") -> messagesForLanguage.town,
            Selectors.label("postcode") -> messagesForLanguage.postcode,
            Selectors.button -> messagesForLanguage.saveAndContinue,
            Selectors.link(1) -> messagesForLanguage.returnToDraft
          ))
        }

        "when the Arranger is Other" - new Fixture(messagesForLanguage.lang) {

          implicit val doc: Document = Jsoup.parse(view(
            form = form,
            addressPage = TransportArrangerAddressPage,
            call = controllers.sections.consignor.routes.ConsignorAddressController.onSubmit(request.ern, request.lrn, NormalMode),
            headingKey = Some(s"$TransportArrangerAddressPage.$Other")
          ).toString())

          behave like pageWithExpectedElementsAndMessages(Seq(
            Selectors.title -> messagesForLanguage.transportArrangerAddressOtherTitle,
            Selectors.h1 -> messagesForLanguage.transportArrangerAddressOtherHeading,
            Selectors.h2(1) -> messagesForLanguage.subheading(TransportArrangerAddressPage)
          ))
        }
      }

      "when rendered for FirstTransporterAddress page" - new Fixture(messagesForLanguage.lang) {

        implicit val doc: Document = Jsoup.parse(view(
          form = form,
          addressPage = FirstTransporterAddressPage,
          call = controllers.sections.firstTransporter.routes.FirstTransporterAddressController.onSubmit(request.ern, request.lrn, NormalMode),
          headingKey = Some("firstTransporterAddress")
        ).toString())

        behave like pageWithExpectedElementsAndMessages(Seq(
          Selectors.title -> messagesForLanguage.firstTransporterAddressTitle,
          Selectors.h1 -> messagesForLanguage.firstTransporterAddressHeading,
          Selectors.h2(1) -> messagesForLanguage.subheading(FirstTransporterAddressPage),
          Selectors.label("property") -> messagesForLanguage.property,
          Selectors.label("street") -> messagesForLanguage.street,
          Selectors.label("town") -> messagesForLanguage.town,
          Selectors.label("postcode") -> messagesForLanguage.postcode,
          Selectors.button -> messagesForLanguage.saveAndContinue,
          Selectors.link(1) -> messagesForLanguage.returnToDraft
        ))
      }

      "when rendered for DispatchAddress page" - new Fixture(messagesForLanguage.lang) {

        implicit val doc: Document = Jsoup.parse(view(
          form = form,
          addressPage = DispatchAddressPage,
          call = controllers.sections.dispatch.routes.DispatchAddressController.onSubmit(request.ern, request.lrn, NormalMode),
          headingKey = Some("dispatchAddress")
        ).toString())

        behave like pageWithExpectedElementsAndMessages(Seq(
          Selectors.title -> messagesForLanguage.dispatchAddressTitle,
          Selectors.h1 -> messagesForLanguage.dispatchAddressHeading,
          Selectors.h2(1) -> messagesForLanguage.subheading(DispatchAddressPage),
          Selectors.label("property") -> messagesForLanguage.property,
          Selectors.label("street") -> messagesForLanguage.street,
          Selectors.label("town") -> messagesForLanguage.town,
          Selectors.label("postcode") -> messagesForLanguage.postcode,
          Selectors.button -> messagesForLanguage.saveAndContinue,
          Selectors.link(1) -> messagesForLanguage.returnToDraft
        ))
      }


    }
  }
}
