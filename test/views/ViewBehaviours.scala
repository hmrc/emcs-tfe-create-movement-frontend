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
import org.jsoup.nodes.Document
import utils.Logging

trait ViewBehaviours extends Logging { _: SpecBase =>

  def pageWithExpectedElementsAndMessages(checks: Seq[(String, String)])(implicit document: Document): Unit = checks foreach {
    case (selector, message) =>
      s"element with selector '$selector'" - {
        s"must have the message '$message'" in {
          document.select(selector) match {
            case elements if elements.size() == 0 =>
              fail(s"Could not find element with CSS selector: '$selector'")
            case elements =>
              if (elements.size() > 1) logger.warn(s"More than one element found with selector '$selector', will check first element found - count of them was: '${elements.size()}'")
              elements.first().text() mustBe message
          }
        }
      }
  }

  def pageWithElementsNotPresent(checks: Seq[String])(implicit document: Document): Unit = checks foreach {
    case (selector) =>
      s"element with selector '$selector' must not be found" - {
          document.select(selector) match {
            case elements if elements.size() == 0 =>

            case elements =>
              fail(s"Found element with CSS selector: '$selector'")
          }
      }
  }
}

