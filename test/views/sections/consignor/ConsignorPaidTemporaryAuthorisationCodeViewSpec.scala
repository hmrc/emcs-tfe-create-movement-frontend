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
  object Selectors extends BaseSelectors {
    val errorSummary: Int => String = index => s".govuk-error-summary__list > li:nth-child(${index})"
  }

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
          Selectors.saveAndExitLink -> messagesForLanguage.returnToDraft
        ))
      }

      s"when being rendered with required error in lang code of '${messagesForLanguage.lang.code}'" - {

        implicit val msgs: Messages = messages(Seq(messagesForLanguage.lang))
        implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(), emptyUserAnswers)

        lazy val view = app.injector.instanceOf[ConsignorPaidTemporaryAuthorisationCodeView]
        val form = app.injector.instanceOf[ConsignorPaidTemporaryAuthorisationCodeFormProvider].apply()

        implicit val doc: Document = Jsoup.parse(view(form.bind(Map("value" -> "")), testOnwardRoute).toString())

        behave like pageWithExpectedElementsAndMessages(Seq(
          Selectors.title -> messagesForLanguage.errorMessageHelper(messagesForLanguage.title),
          Selectors.errorSummary(1) -> messagesForLanguage.errorRequired,
          Selectors.h1 -> messagesForLanguage.heading,
          Selectors.hint -> messagesForLanguage.hint,
          Selectors.button -> messagesForLanguage.saveAndContinue,
          Selectors.saveAndExitLink -> messagesForLanguage.returnToDraft
        ))
      }

      s"when being rendered with length error in lang code of '${messagesForLanguage.lang.code}'" - {

        implicit val msgs: Messages = messages(Seq(messagesForLanguage.lang))
        implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(), emptyUserAnswers)

        lazy val view = app.injector.instanceOf[ConsignorPaidTemporaryAuthorisationCodeView]
        val form = app.injector.instanceOf[ConsignorPaidTemporaryAuthorisationCodeFormProvider].apply()

        implicit val doc: Document = Jsoup.parse(view(form.bind(Map("value" -> "12345678901234567890")), testOnwardRoute).toString())

        behave like pageWithExpectedElementsAndMessages(Seq(
          Selectors.title -> messagesForLanguage.errorMessageHelper(messagesForLanguage.title),
          Selectors.errorSummary(1) -> messagesForLanguage.errorLength,
          Selectors.h1 -> messagesForLanguage.heading,
          Selectors.hint -> messagesForLanguage.hint,
          Selectors.button -> messagesForLanguage.saveAndContinue,
          Selectors.saveAndExitLink -> messagesForLanguage.returnToDraft
        ))
      }


      s"when being rendered with invalid format error in lang code of '${messagesForLanguage.lang.code}'" - {

        implicit val msgs: Messages = messages(Seq(messagesForLanguage.lang))
        implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(), emptyUserAnswers)

        lazy val view = app.injector.instanceOf[ConsignorPaidTemporaryAuthorisationCodeView]
        val form = app.injector.instanceOf[ConsignorPaidTemporaryAuthorisationCodeFormProvider].apply()

        implicit val doc: Document = Jsoup.parse(view(form.bind(Map("value" -> testGreatBritainErn)), testOnwardRoute).toString())

        behave like pageWithExpectedElementsAndMessages(Seq(
          Selectors.title -> messagesForLanguage.errorMessageHelper(messagesForLanguage.title),
          Selectors.errorSummary(1) -> messagesForLanguage.errorInvalid,
          Selectors.h1 -> messagesForLanguage.heading,
          Selectors.hint -> messagesForLanguage.hint,
          Selectors.button -> messagesForLanguage.saveAndContinue,
          Selectors.saveAndExitLink -> messagesForLanguage.returnToDraft
        ))
      }
    }
  }
}

