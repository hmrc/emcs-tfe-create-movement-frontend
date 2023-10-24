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

package utils

import base.SpecBase
import fixtures.messages.MonthMessages
import play.api.i18n.Messages

import java.time.LocalDate

class DateUtilsSpec extends SpecBase with DateUtils {

  lazy val app = applicationBuilder().build()

  ".formatDateForUIOutput()" - {

    Seq(MonthMessages.English, MonthMessages.Welsh).foreach { messagesForLanguage =>
      1 to 12 foreach { month =>

        s"for month: `$month and language: '${messagesForLanguage.lang.code}'" - {

          "must format it correctly" in {
            implicit val msgs: Messages = messages(app, messagesForLanguage.lang)
            LocalDate.of(2023, month, 1).formatDateForUIOutput() mustBe
              s"1 ${messagesForLanguage.month(month)} 2023"
          }
        }
      }
    }
  }

  ".formatDateNumbersOnly" - {

    "must format it correctly" in {

      val expectedResult = s"1 1 2023"

      val actualResult = LocalDate.of(2023, 1, 1).formatDateNumbersOnly()

      actualResult mustBe expectedResult
    }
  }
}
