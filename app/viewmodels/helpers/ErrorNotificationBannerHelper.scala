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

import models.requests.DataRequest
import play.api.i18n.Messages
import play.twirl.api.{Html, HtmlFormat}
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.{HtmlContent, Text}
import uk.gov.hmrc.govukfrontend.views.viewmodels.notificationbanner.NotificationBanner
import utils.SubmissionError
import views.html.components._

import javax.inject.Inject

class ErrorNotificationBannerHelper @Inject()(list: list, p: p, link: link){

  def content(errors: Seq[SubmissionError], withLinks: Boolean = false)
             (implicit request: DataRequest[_], messages: Messages): Option[NotificationBanner] = {
    Option.when(errors.nonEmpty) {
      NotificationBanner(
        title = Text(messages("errors.704.notificationBanner.title")),
        content = if (errors.length == 1) {
          HtmlContent(p("govuk-notification-banner__heading")(singleErrorContent(errors.head, withLinks)))
        } else {
          HtmlContent(
            HtmlFormat.fill(Seq(
              p("govuk-notification-banner__heading")(Html(messages("errors.704.notificationBanner.p"))),
              list(errors.map(singleErrorContent(_, withLinks)))
            ))
          )
        }
      )
    }
  }

  private def singleErrorContent(error: SubmissionError, withLink: Boolean)
                                (implicit request: DataRequest[_], messages: Messages): Html =
    if(withLink) {
      link(error.route().url, error.messageKey, Some(error.id))
    } else {
      Html(messages(error.messageKey))
    }
}
