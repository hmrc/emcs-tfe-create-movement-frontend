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

package viewmodels.helpers

import base.SpecBase
import fixtures.messages.sections.consignee.ConsigneeExciseMessages.English
import models.UserAnswers
import models.requests.DataRequest
import play.api.i18n.Messages
import play.api.test.FakeRequest
import play.twirl.api.{Html, HtmlFormat}
import uk.gov.hmrc.govukfrontend.views.Aliases.Text
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.HtmlContent
import uk.gov.hmrc.govukfrontend.views.viewmodels.notificationbanner.NotificationBanner
import utils.{InvalidOrMissingConsigneeError, LinkIsPendingError}
import views.html.components.{link, list, p}

class ErrorNotificationBannerHelperSpec extends SpecBase {

  lazy val link: link = app.injector.instanceOf[link]
  lazy val list: list = app.injector.instanceOf[list]
  lazy val p: p = app.injector.instanceOf[p]
  lazy val helper: ErrorNotificationBannerHelper = app.injector.instanceOf[ErrorNotificationBannerHelper]

  class Setup(userAnswers: UserAnswers = emptyUserAnswers) {
    implicit lazy val request: DataRequest[_] = dataRequest(FakeRequest(), userAnswers)
    implicit lazy val msg: Messages = messages(request)
  }

  ".content" - {

    "when given NO errors" in new Setup {

      val expectedResult = None

      val actualResult = helper.content(errors = Seq.empty)

      actualResult mustBe expectedResult
    }

    "when given a single error" - {

      "with NO links" in new Setup {

        val expectedResult = Some(NotificationBanner(
          title = Text(English.updateNeeded),
          content = HtmlContent(
            p("govuk-notification-banner__heading")(Html(English.invalidOrMissingConsignee))
          )
        ))

        val actualResult = helper.content(
          errors = Seq(InvalidOrMissingConsigneeError)
        )

        actualResult mustBe expectedResult
      }

      "with links" in new Setup {

        val expectedResult = Some(NotificationBanner(
          title = Text(English.updateNeeded),
          content = HtmlContent(
            p("govuk-notification-banner__heading")(link(
              InvalidOrMissingConsigneeError.route().url,
              English.invalidOrMissingConsignee,
              Some(InvalidOrMissingConsigneeError.id)
            )
            ))
        ))

        val actualResult = helper.content(
          errors = Seq(InvalidOrMissingConsigneeError),
          withLinks = true
        )

        actualResult mustBe expectedResult
      }
    }

    "when given multiple errors" - {

      "with NO links" in new Setup {

        val expectedResult = Some(NotificationBanner(
          title = Text(English.updateNeeded),
          content = HtmlContent(
            HtmlFormat.fill(Seq(
              p("govuk-notification-banner__heading")(Html(English.notificationBannerParagraph)),
              list(Seq(
                Html(English.invalidOrMissingConsignee),
                Html(English.linkIsPending),
              ))
            ))
          )
        ))

        val actualResult = helper.content(
          errors = Seq(InvalidOrMissingConsigneeError, LinkIsPendingError)
        )

        actualResult mustBe expectedResult
      }

      "with links" in new Setup {

        val expectedResult = Some(NotificationBanner(
          title = Text(English.updateNeeded),
          content = HtmlContent(
            HtmlFormat.fill(Seq(
              p("govuk-notification-banner__heading")(Html(English.notificationBannerParagraph)),
              list(Seq(
                link(
                  InvalidOrMissingConsigneeError.route().url,
                  English.invalidOrMissingConsignee,
                  Some(InvalidOrMissingConsigneeError.id)
                ),
                link(
                  LinkIsPendingError.route().url,
                  English.linkIsPending,
                  Some(LinkIsPendingError.id)
                )
              ))
            ))
          )
        ))

        val actualResult = helper.content(
          errors = Seq(InvalidOrMissingConsigneeError, LinkIsPendingError),
          withLinks = true
        )

        actualResult mustBe expectedResult
      }
    }
  }
}
