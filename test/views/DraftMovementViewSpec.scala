/*
 * Copyright 2024 HM Revenue & Customs
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
import fixtures.MovementSubmissionFailureFixtures
import fixtures.messages.DraftMovementMessages
import models.MovementValidationFailure
import models.requests.DataRequest
import models.sections.info.movementScenario.MovementScenario.UkTaxWarehouse
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import pages.sections.info.DestinationTypePage
import play.api.i18n.Messages
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import utils.LocalReferenceNumberError
import views.html.DraftMovementView

class DraftMovementViewSpec extends SpecBase with ViewBehaviours with MovementSubmissionFailureFixtures {

  object Selectors extends BaseSelectors

  lazy val view = app.injector.instanceOf[DraftMovementView]

  "DraftMovementView" - {

    Seq(DraftMovementMessages.English).foreach { messagesForLanguage =>

      implicit val msgs: Messages = messages(Seq(messagesForLanguage.lang))

      s"when being rendered in lang code of '${messagesForLanguage.lang.code}'" - {

        "for a 704 scenario" - {

          implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(),
            emptyUserAnswers.copy(submissionFailures = Seq(movementSubmissionFailure.copy(errorType = LocalReferenceNumberError.code, hasBeenFixed = false)))
          )

          implicit val doc: Document = Jsoup.parse(view().toString())

          behave like pageWithExpectedElementsAndMessages(Seq(
            Selectors.title -> messagesForLanguage.titleHelper(messagesForLanguage.headingWhen704ErrorsPresent),
            Selectors.notificationBannerTitle -> messagesForLanguage.updateNeeded,
            Selectors.notificationBannerContent -> messagesForLanguage.notificationBanner704Content,
            Selectors.subHeadingCaptionSelector -> messagesForLanguage.draftMovementSection,
            Selectors.h1 -> messagesForLanguage.headingWhen704ErrorsPresent
          ))
        }

        "for a validation failure scenario" - {

          val validationFailures: Seq[MovementValidationFailure] = Seq(
            //scalastyle:off magic.number
            MovementValidationFailure(Some(12), Some("This is an error.")),
            MovementValidationFailure(Some(13), Some("This is an error.")),
            MovementValidationFailure(Some(14), Some("This is an error."))
            //scalastyle:on magic.number
          )

          implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(),
            ern = testGreatBritainErn,
            answers = emptyUserAnswers.set(DestinationTypePage, UkTaxWarehouse.GB).copy(validationErrors = validationFailures)
          )

          implicit val doc: Document = Jsoup.parse(view().toString())

          behave like pageWithExpectedElementsAndMessages(Seq(
            Selectors.title -> messagesForLanguage.titleImportFor("Great Britain tax warehouse"),
            Selectors.notificationBannerTitle -> messagesForLanguage.important,
            Selectors.notificationBannerContent -> messagesForLanguage.notificationBannerValidationFailuresContent,
            Selectors.subHeadingCaptionSelector -> messagesForLanguage.draftMovementSection,
            // errorType: 12
            Selectors.notificationBannerError(1) -> "This is an error.",
            // errorType: 13
            Selectors.notificationBannerError(2) -> "This is an error.",
            // errorType: 14
            Selectors.notificationBannerError(3) -> "errors.validation.notificationBanner.14.content",
          ))
        }

        "for a draft movement (not in error)" - {

          implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(),
            ern = testGreatBritainErn,
            answers = emptyUserAnswers.set(DestinationTypePage, UkTaxWarehouse.GB)
          )

          implicit val doc: Document = Jsoup.parse(view().toString())

          behave like pageWithExpectedElementsAndMessages(Seq(
            Selectors.title -> messagesForLanguage.titleImportFor("Great Britain tax warehouse"),
            Selectors.subHeadingCaptionSelector -> messagesForLanguage.draftMovementSection,
            Selectors.h1 -> messagesForLanguage.headingImportFor("Great Britain tax warehouse")
          ))
        }
      }
    }

  }

}
