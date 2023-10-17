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
import fixtures.TransportUnitFixtures
import models.NormalMode
import models.TransportUnitType.Tractor
import pages.sections.transportUnit.TransportSealChoicePage
import pages.sections.transportUnit.TransportSealTypePage
import pages.{Page, TransportUnitIdentityPage, TransportUnitTypePage}

class TransportUnitNavigatorSpec extends SpecBase with TransportUnitFixtures {
  val navigator = new TransportUnitNavigator

  "TransportUnitNavigator" - {

    "in Normal mode" - {

      "must go from a page that doesn't exist in the route map to Index" in {

        case object UnknownPage extends Page
        navigator.nextPage(UnknownPage, NormalMode, emptyUserAnswers) mustBe
          routes.IndexController.onPageLoad(testErn)
      }
    }

    "for the TransportUnitType (CAM-TU01)" - {

      "must go to CAM-TU02" in {
        val userAnswers = emptyUserAnswers.set(TransportUnitTypePage, Tractor)

        navigator.nextPage(TransportUnitTypePage, NormalMode, userAnswers) mustBe
          controllers.sections.transportUnit.routes.TransportUnitIdentityController.onPageLoad(userAnswers.ern, userAnswers.lrn, NormalMode)
      }
    }

    "for the TransportSealType (CAM-TU04)" - {

      // TODO redirect to CAM-TU05
      "must go to CAM-TU05" in {

        val userAnswers = emptyUserAnswers.set(TransportSealTypePage, transportSealTypeModelMax)

        navigator.nextPage(TransportUnitTypePage, NormalMode, userAnswers) mustBe
          controllers.sections.transportUnit.routes.TransportUnitIdentityController.onPageLoad(testErn, testLrn, NormalMode)
      }
    }

    "for the TransportUnitIdentity (CAM-TU02)" - {

      "must go to CAM-TU03" in {

        val userAnswers = emptyUserAnswers
          .set(TransportUnitTypePage, Tractor)
          .set(TransportUnitIdentityPage, "weee")

        navigator.nextPage(TransportUnitIdentityPage, NormalMode, userAnswers) mustBe
          controllers.sections.transportUnit.routes.TransportSealChoiceController.onPageLoad(userAnswers.ern, userAnswers.lrn, NormalMode)
      }
    }

    "for the TransportSealChoicePage (CAM-TU03)" - {

      // TODO redirect to CAM-TU04
      "must go to CAM-TU04" in {

        val userAnswers = emptyUserAnswers
          .set(TransportUnitTypePage, Tractor)
          .set(TransportSealChoicePage, false)

        navigator.nextPage(TransportSealChoicePage, NormalMode, userAnswers) mustBe
          testOnly.controllers.routes.UnderConstructionController.onPageLoad()
      }

      // TODO redirect to CAM-TU05
      "must go to CAM-TU05" in {

        val userAnswers = emptyUserAnswers
          .set(TransportUnitTypePage, Tractor)
          .set(TransportSealChoicePage, false)

        navigator.nextPage(TransportSealChoicePage, NormalMode, userAnswers) mustBe
          testOnly.controllers.routes.UnderConstructionController.onPageLoad()
      }

    }
  }
}
