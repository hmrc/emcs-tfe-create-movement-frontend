package views.sections.consignor

import base.SpecBase
import fixtures.messages.sections.consignor.ConsignorPaidTemporaryAuthorisationMessages
import forms.sections.consignor.ConsignorPaidTemporaryAuthorisationCodeFormProvider
import models.requests.DataRequest
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.i18n.Messages
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import views.html.sections.consignor.ConsignorPaidTemporaryAuthorisationCodeView
import views.{BaseSelectors, ViewBehaviours}

class ConsignorPaidTemporaryAuthorisationCodeViewSpec extends SpecBase with ViewBehaviours {
  object Selectors extends BaseSelectors

  "ConsignorPaidTemporaryAuthorisationCodeView" - {

    Seq(ConsignorPaidTemporaryAuthorisationMessages.English).foreach { messagesForLanguage =>

      s"when being rendered in lang code of '${messagesForLanguage.lang.code}'" - {

        implicit val msgs: Messages = messages(Seq(messagesForLanguage.lang))
        implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(), emptyUserAnswers)

        lazy val view = app.injector.instanceOf[ConsignorPaidTemporaryAuthorisationCodeView]
        val form = app.injector.instanceOf[ConsignorPaidTemporaryAuthorisationCodeFormProvider].apply()

        implicit val doc: Document = Jsoup.parse(view(form, testOnwardRoute).toString())

        behave like pageWithExpectedElementsAndMessages(Seq(
          Selectors.title -> messagesForLanguage.title,
          Selectors.h1 -> messagesForLanguage.heading,
          Selectors.hint -> messagesForLanguage.hint,
          Selectors.button -> messagesForLanguage.saveAndContinue,
          Selectors.link(1) -> messagesForLanguage.returnToDraft
        ))
      }
    }
  }
}

