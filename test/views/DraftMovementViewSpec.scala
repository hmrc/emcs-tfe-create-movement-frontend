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
import models.requests.DataRequest
import models.sections.info.movementScenario.MovementScenario.GbTaxWarehouse
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import pages.sections.info.DestinationTypePage
import play.api.i18n.Messages
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import utils.SubmissionFailureErrorCodes.localReferenceNumberError
import views.html.DraftMovementView

class DraftMovementViewSpec extends SpecBase with ViewBehaviours with MovementSubmissionFailureFixtures {

  object Selectors extends BaseSelectors

  lazy val view = app.injector.instanceOf[DraftMovementView]

  "DraftMovementView" - {

    Seq(DraftMovementMessages.English).foreach { messagesForLanguage =>

      s"when being rendered in lang code of '${messagesForLanguage.lang.code}'" - {

        "for a 704 scenario" - {

          implicit val msgs: Messages = messages(Seq(messagesForLanguage.lang))
          implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(),
            emptyUserAnswers.copy(submissionFailures = Seq(movementSubmissionFailure.copy(errorType = localReferenceNumberError, hasBeenFixed = false)))
          )

          implicit val doc: Document = Jsoup.parse(view().toString())

          behave like pageWithExpectedElementsAndMessages(Seq(
            Selectors.title -> messagesForLanguage.titleHelper(messagesForLanguage.headingWhen704ErrorsPresent),
            Selectors.notificationBannerTitle -> messagesForLanguage.notificationBannerTitle,
            Selectors.notificationBannerContent -> messagesForLanguage.notificationBanner704Content,
            Selectors.subHeadingCaptionSelector -> messagesForLanguage.draftMovementSection,
            Selectors.h1 -> messagesForLanguage.headingWhen704ErrorsPresent
          ))
        }

        "for a draft movement (not in error)" - {

          implicit val msgs: Messages = messages(Seq(messagesForLanguage.lang))
          implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(),
            ern = testGreatBritainErn,
            answers = emptyUserAnswers.set(DestinationTypePage, GbTaxWarehouse)
          )

          implicit val doc: Document = Jsoup.parse(view().toString())

          behave like pageWithExpectedElementsAndMessages(Seq(
            Selectors.title -> messagesForLanguage.titleImportFor("Tax warehouse in Great Britain"),
            Selectors.subHeadingCaptionSelector -> messagesForLanguage.draftMovementSection,
            Selectors.h1 -> messagesForLanguage.headingImportFor("Tax warehouse in Great Britain")
          ))
        }
      }
    }

  }

}
