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

package fixtures.messages

object CheckYourAnswersMessages {

  sealed trait ViewMessages { _: i18n =>
    val title: String
    val heading: String
    val submitButton: String
  }

  object English extends ViewMessages with BaseEnglish {
    override val title: String = title("Check your answers before submitting your movement")
    override val heading = "Check your answers before submitting your movement"
    override val submitButton = "Submit movement"
  }

  object Welsh extends ViewMessages with BaseWelsh {
    override val title: String = title("Check your answers before submitting your movement")
    override val heading = "Check your answers before submitting your movement"
    override val submitButton = "Submit movement"
  }
}
