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

import base.SpecBase
import fixtures.messages.DeclarationMessages
import models.UserAnswers
import models.requests.DataRequest
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.i18n.Messages
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import views.html.DeclarationView

import java.time.Instant
import java.time.temporal.ChronoUnit

class DeclarationViewSpec extends SpecBase with ViewBehaviours {

  object Selectors extends BaseSelectors {
    val exitToTemplatesLink = "#save-and-exit-to-templates"
    val content = "#declaration-content"
    val insetP1 = "#max-templates-reached-p1"
    val insetP2 = "#max-templates-reached-p2"
  }

  "DeclarationView" - {

    Seq(DeclarationMessages.English).foreach { messagesForLanguage =>

      s"when being rendered in lang code of '${messagesForLanguage.lang.code}'" - {

        "when movement was Satisfactory and has not used a template (total templates < max)" - {

          implicit val msgs: Messages = messages(Seq(messagesForLanguage.lang))
          implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(), emptyUserAnswers)

          val view = app.injector.instanceOf[DeclarationView]

          implicit val doc: Document = Jsoup.parse(view(
            submitAction = testOnwardRoute,
            countOfTemplates = appConfig.maxTemplates - 1
          ).toString())

          behave like pageWithExpectedElementsAndMessages(Seq(
            Selectors.title -> messagesForLanguage.title,
            Selectors.subHeadingCaptionSelector -> messagesForLanguage.draftMovementSection,
            Selectors.h1 -> messagesForLanguage.heading,
            Selectors.content -> messagesForLanguage.content,
            Selectors.button -> messagesForLanguage.submit
          ))

          "submit button should have prevent double click" in {
            doc.select(Selectors.button).attr("data-prevent-double-click") mustBe "true"
          }
        }

        "when movement was Satisfactory and has not used a template (total templates >= max)" - {

          implicit val msgs: Messages = messages(Seq(messagesForLanguage.lang))
          implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(), emptyUserAnswers)

          val view = app.injector.instanceOf[DeclarationView]

          implicit val doc: Document = Jsoup.parse(view(
            submitAction = testOnwardRoute,
            countOfTemplates = appConfig.maxTemplates
          ).toString())

          behave like pageWithExpectedElementsAndMessages(Seq(
            Selectors.title -> messagesForLanguage.title,
            Selectors.subHeadingCaptionSelector -> messagesForLanguage.draftMovementSection,
            Selectors.insetP1 -> messagesForLanguage.maxTemplatesReachedP1(appConfig.maxTemplates),
            Selectors.insetP2 -> messagesForLanguage.maxTemplatesReachedP2(appConfig.maxTemplates),
            Selectors.h1 -> messagesForLanguage.heading,
            Selectors.content -> messagesForLanguage.content,
            Selectors.button -> messagesForLanguage.submit
          ))

          "submit button should have prevent double click" in {
            doc.select(Selectors.button).attr("data-prevent-double-click") mustBe "true"
          }

          "should include a link to save and exit to templates" in {
            val link = doc.select(Selectors.exitToTemplatesLink)
            link.text mustBe messagesForLanguage.saveAndExitToTemplates
            link.attr("href") mustBe appConfig.emcsTfeTemplatesUrl(testErn)
          }
        }

        "when movement was Satisfactory and has used a template" - {

          implicit val msgs: Messages = messages(Seq(messagesForLanguage.lang))
          implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(), UserAnswers(
            ern = testErn,
            draftId = testDraftId,
            lastUpdated = Instant.now().truncatedTo(ChronoUnit.MILLIS),
            submissionFailures = Seq.empty,
            validationErrors = Seq.empty,
            submittedDraftId = None,
            hasBeenSubmitted = false,
            createdFromTemplateId = Some("1"),
            createdFromTemplateName = Some("1")
          ))

          val view = app.injector.instanceOf[DeclarationView]

          implicit val doc: Document = Jsoup.parse(view(
            submitAction = testOnwardRoute,
            countOfTemplates = appConfig.maxTemplates
          ).toString())

          behave like pageWithExpectedElementsAndMessages(Seq(
            Selectors.title -> messagesForLanguage.title,
            Selectors.subHeadingCaptionSelector -> messagesForLanguage.draftMovementSection,
            Selectors.h1 -> messagesForLanguage.heading,
            Selectors.p(1) -> messagesForLanguage.templateContent,
            Selectors.content -> messagesForLanguage.content,
            Selectors.button -> messagesForLanguage.submit
          ))

          "submit button should have prevent double click" in {
            doc.select(Selectors.button).attr("data-prevent-double-click") mustBe "true"
          }
        }
      }
    }
  }
}
