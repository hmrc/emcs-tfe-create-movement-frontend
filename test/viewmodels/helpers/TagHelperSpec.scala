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

import base.SpecBase
import fixtures.messages.TaskListStatusMessages
import play.api.i18n.Messages
import play.api.test.FakeRequest

class TagHelperSpec extends SpecBase {

  implicit lazy val msgs: Messages = messages(FakeRequest())

  lazy val tagHelper = app.injector.instanceOf[TagHelper]
  lazy val tag = app.injector.instanceOf[views.html.components.tag]

  ".updateNeededTag" - {

    Seq(TaskListStatusMessages.English).foreach { messagesForLang =>

      "should render an UpdateNeededTag with correct data" in {

        val result = tagHelper.updateNeededTag()

        result mustBe tag(
          message = messagesForLang.updateNeeded,
          colour = "orange",
          extraClasses = "float-none govuk-!-margin-left-1"
        )
      }
    }
  }
}
