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
import pages.sections.firstTransporter._
import pages._

class FirstTransporterNavigatorSpec extends SpecBase {
  val navigator = new FirstTransporterNavigator

  "FirstTransporterNavigator" - {

    "in Normal mode" - {

      "must go from a page that doesn't exist in the route map to Index" in {

        case object UnknownPage extends Page
        navigator.nextPage(UnknownPage, NormalMode, emptyUserAnswers) mustBe
          routes.IndexController.onPageLoad(testErn)
      }
    }

    "for the FirstTransporterNamePage (CAM-FT01)" - {

      "must go to CAM-FT02" - {
        val userAnswers = emptyUserAnswers.set(FirstTransporterNamePage, "transporter name here")

        navigator.nextPage(FirstTransporterNamePage, NormalMode, userAnswers) mustBe
          controllers.sections.firstTransporter.routes.FirstTransporterVatController.onPageLoad(userAnswers.ern, userAnswers.lrn, NormalMode)
      }

    }

    "for the FirstTransporterVATPage (CAM-FT02)" - {

      "must go to CAM-FT03" - {
        val userAnswers = emptyUserAnswers.set(FirstTransporterVatPage, "123456789")

        // TODO redirect to CAM-TF03
        navigator.nextPage(FirstTransporterVatPage, NormalMode, userAnswers) mustBe
          testOnly.controllers.routes.UnderConstructionController.onPageLoad()
      }

    }

    "for the FirstTransporterAddressPage (CAM-FT03)" - {

      "must go to CAM-FT04" - {

        // TODO redirect to CAM-TF04
        navigator.nextPage(FirstTransporterAddressPage, NormalMode, emptyUserAnswers) mustBe
          testOnly.controllers.routes.UnderConstructionController.onPageLoad()
      }

    }

  }
}
