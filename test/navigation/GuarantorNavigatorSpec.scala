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

package navigation

import base.SpecBase
import controllers.routes
import models.NormalMode
import pages.Page
import pages.sections.guarantor.GuarantorRequiredPage

class GuarantorNavigatorSpec extends SpecBase {
  val navigator = new GuarantorNavigator

  "GuarantorNavigator" - {

    "in Normal mode" - {

      "must go from a page that doesn't exist in the route map to Index" in {

        case object UnknownPage extends Page
        navigator.nextPage(UnknownPage, NormalMode, emptyUserAnswers) mustBe
          routes.IndexController.onPageLoad(testErn)
      }

      "for GuarantorRequiredPage" - {

        "when true" - {

          "must go to CAM-G02" in {

            val userAnswers = emptyUserAnswers.set(GuarantorRequiredPage, true)

            navigator.nextPage(GuarantorRequiredPage, NormalMode, userAnswers) mustBe
              controllers.sections.guarantor.routes.GuarantorArrangerController.onPageLoad(testErn, testLrn, NormalMode)
          }
        }
        "when false" - {
          // TODO redirect to CAM-G06
          "must go to CAM-G06" in {

            val userAnswers = emptyUserAnswers.set(GuarantorRequiredPage, false)

            navigator.nextPage(GuarantorRequiredPage, NormalMode, userAnswers) mustBe
              testOnly.controllers.routes.UnderConstructionController.onPageLoad()
          }
        }
      }
    }
  }
}