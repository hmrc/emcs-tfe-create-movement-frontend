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
import models.sections.transportUnit.TransportUnitType.Tractor
import models.{CheckMode, Index, NormalMode, ReviewMode}
import pages.Page
import pages.sections.transportUnit._

class TransportUnitNavigatorSpec extends SpecBase with TransportUnitFixtures {
  val navigator = new TransportUnitNavigator

  "in Normal mode" - {

    "must go from a page that doesn't exist in the route map to Transport Unit CYA" in {
      //TODO: update to Transport Unit CYA when built
      case object UnknownPage extends Page
      navigator.nextPage(UnknownPage, NormalMode, emptyUserAnswers) mustBe
        testOnly.controllers.routes.UnderConstructionController.onPageLoad()
    }

    "for the TransportUnitType (CAM-TU01)" - {

      "must go to CAM-TU02" in {
        val userAnswers = emptyUserAnswers.set(TransportUnitTypePage(testIndex1), Tractor)

        navigator.nextPage(TransportUnitTypePage(testIndex1), NormalMode, userAnswers) mustBe
          controllers.sections.transportUnit.routes.TransportUnitIdentityController.onPageLoad(testErn, testDraftId, Index(0), NormalMode)
      }
    }

    "for the TransportUnitIdentity (CAM-TU02)" - {

      "must go to CAM-TU03" in {

        val userAnswers = emptyUserAnswers
          .set(TransportUnitTypePage(testIndex1), Tractor)
          .set(TransportUnitIdentityPage(testIndex1), "weee")

        navigator.nextPage(TransportUnitIdentityPage(testIndex1), NormalMode, userAnswers) mustBe
          controllers.sections.transportUnit.routes.TransportSealChoiceController.onPageLoad(userAnswers.ern, userAnswers.draftId, testIndex1, NormalMode)
      }
    }

    "for the TransportSealChoicePage (CAM-TU03)" - {

      "must go to CAM-TU04 when TransportSealChoice is true" in {

        val userAnswers = emptyUserAnswers
          .set(TransportUnitTypePage(testIndex1), Tractor)
          .set(TransportSealChoicePage(testIndex1), true)

        navigator.nextPage(TransportSealChoicePage(testIndex1), NormalMode, userAnswers) mustBe
          controllers.sections.transportUnit.routes.TransportSealTypeController.onPageLoad(testErn, testDraftId, testIndex1, NormalMode)
      }

      "must go to CAM-TU05 when TransportSealChoice is false" in {

        val userAnswers = emptyUserAnswers
          .set(TransportUnitTypePage(testIndex1), Tractor)
          .set(TransportSealChoicePage(testIndex1), false)

        navigator.nextPage(TransportSealChoicePage(testIndex1), NormalMode, userAnswers) mustBe
          controllers.sections.transportUnit.routes.TransportUnitGiveMoreInformationChoiceController.onPageLoad(testErn, testDraftId, testIndex1, NormalMode)
      }
    }

    "for the TransportSealType (CAM-TU04)" - {

      "must go to CAM-TU05" in {

        val userAnswers = emptyUserAnswers.set(TransportSealTypePage(testIndex1), transportSealTypeModelMax)

        navigator.nextPage(TransportSealTypePage(testIndex1), NormalMode, userAnswers) mustBe
          controllers.sections.transportUnit.routes.TransportUnitGiveMoreInformationChoiceController.onPageLoad(testErn, testDraftId, testIndex1, NormalMode)
      }

    }

    "for the TransportUnitGiveMoreInformationChoice (CAM-TU05)" - {

      "must go to CAM-TU06" - {

        "when the answer is Yes" in {
          val userAnswers = emptyUserAnswers.set(TransportUnitGiveMoreInformationChoicePage(testIndex1), true)

          navigator.nextPage(TransportUnitGiveMoreInformationChoicePage(testIndex1), NormalMode, userAnswers) mustBe
            controllers.sections.transportUnit.routes.TransportUnitGiveMoreInformationController.onPageLoad(testErn, testDraftId, testIndex1, NormalMode)
        }
      }

      // TODO redirect to CAM-TU07
      "must go to CAM-TU07" - {

        "when the answer is No" in {
          val userAnswers = emptyUserAnswers.set(TransportUnitGiveMoreInformationChoicePage(testIndex1), false)

          navigator.nextPage(TransportUnitGiveMoreInformationChoicePage(testIndex1), NormalMode, userAnswers) mustBe
            testOnly.controllers.routes.UnderConstructionController.onPageLoad()
        }
      }
    }

    "for the TransportUnitGiveMoreInformation (CAM-TU06)" - {
      // TODO redirect to CAM-TU07
      "must go to CAM-TU07" in {

        val userAnswers = emptyUserAnswers
          .set(TransportUnitTypePage(testIndex1), Tractor)
          .set(TransportUnitGiveMoreInformationPage(testIndex1), "answer")

        navigator.nextPage(TransportUnitGiveMoreInformationPage(testIndex1), NormalMode, userAnswers) mustBe
          testOnly.controllers.routes.UnderConstructionController.onPageLoad()
      }
    }
  }

  "in Check mode" - {
    "must go to CheckYourAnswersTransportUnitController" in {
      //TODO: update to Transport Unit CYA when built
      case object UnknownPage extends Page
      navigator.nextPage(UnknownPage, CheckMode, emptyUserAnswers) mustBe
        testOnly.controllers.routes.UnderConstructionController.onPageLoad()
    }
  }

  "in Review mode" - {
    "must go to CheckYourAnswers" in {
      case object UnknownPage extends Page
      navigator.nextPage(UnknownPage, ReviewMode, emptyUserAnswers) mustBe
        routes.CheckYourAnswersController.onPageLoad(testErn, testDraftId)
    }
  }
}
