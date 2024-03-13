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

package viewmodels.helpers

import models.requests.DataRequest
import play.api.i18n.Messages
import play.twirl.api.{Html, HtmlFormat}
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.{HtmlContent, Text}
import uk.gov.hmrc.govukfrontend.views.viewmodels.notificationbanner.NotificationBanner
import utils.SubmissionFailureErrorCodes.ErrorCode
import views.html.components._

import javax.inject.Inject

class ErrorNotificationBannerHelper @Inject()(list: list, p: p, link: link){

  def content(errors: Seq[ErrorCode], withLinks: Boolean = false)(implicit request: DataRequest[_], messages: Messages): NotificationBanner = {
    NotificationBanner(
      title = Text(messages("errors.704.notificationBanner.title")),
      content = if(withLinks) {
        HtmlContent(
          HtmlFormat.fill(Seq(
            p()(Html(messages("errors.704.notificationBanner.content"))),
            list(errors.map { error => link(
              link = error.route().url,
              messageKey = error.messageKey,
              id = Some(error.id)
            )})
          )))
      } else {
        HtmlContent(list(errors.map { error =>
          Html(messages(error.messageKey))
        }))
      }
    )
  }
}
