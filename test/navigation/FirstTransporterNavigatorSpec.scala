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
import models.{CheckMode, NormalMode, ReviewMode, VatNumberModel}
import pages._
import pages.sections.firstTransporter._

class FirstTransporterNavigatorSpec extends SpecBase {
  val navigator = new FirstTransporterNavigator

  "in Normal mode" - {

    "must go from a page that doesn't exist in the route map to First Transporter CYA" in {

      case object UnknownPage extends Page
      navigator.nextPage(UnknownPage, NormalMode, emptyUserAnswers) mustBe
        controllers.sections.firstTransporter.routes.FirstTransporterCheckAnswersController.onPageLoad(testErn, testDraftId)
    }

    "for the FirstTransporterVATPage (CAM-FT02)" - {

      "must go to CAM-FT03" in {
        navigator.nextPage(FirstTransporterVatPage, NormalMode, emptyUserAnswers) mustBe
          controllers.sections.firstTransporter.routes.FirstTransporterAddressController.onPageLoad(emptyUserAnswers.ern, emptyUserAnswers.draftId, NormalMode)
      }

    }

    "for the FirstTransporterAddressPage (CAM-FT03)" - {

      "must go to CAM-FT04" in {
        navigator.nextPage(FirstTransporterAddressPage, NormalMode, emptyUserAnswers) mustBe
          controllers.sections.firstTransporter.routes.FirstTransporterCheckAnswersController.onPageLoad(emptyUserAnswers.ern, emptyUserAnswers.draftId)
      }

    }

    "for the FirstTransporterCheckAnswersPage" - {

      "must go to tasklist" in {
        navigator.nextPage(FirstTransporterCheckAnswersPage, NormalMode, emptyUserAnswers) mustBe
          routes.DraftMovementController.onPageLoad(testErn, testDraftId)
      }

    }

  }

  "in Check mode" - {
    "must go from a page that doesn't exist in the route map to CYA" in {

      case object UnknownPage extends Page
      navigator.nextPage(UnknownPage, CheckMode, emptyUserAnswers) mustBe
        controllers.sections.firstTransporter.routes.FirstTransporterCheckAnswersController.onPageLoad(emptyUserAnswers.ern, emptyUserAnswers.draftId)
    }

    "for the FirstTransporterVatPage" - {
      "must redirect to FirstTransporterAddressController" - {
        "when FirstTransporterVatPage is empty" in {
          val userAnswers = emptyUserAnswers
            .set(FirstTransporterAddressPage, testUserAddress)
          navigator.nextPage(FirstTransporterVatPage, CheckMode, userAnswers) mustBe
            controllers.sections.firstTransporter.routes.FirstTransporterAddressController.onPageLoad(userAnswers.ern, userAnswers.draftId, NormalMode)
        }
        "when FirstTransporterAddressPage is empty" in {
          val userAnswers = emptyUserAnswers
            .set(FirstTransporterVatPage, VatNumberModel(false, None))
          navigator.nextPage(FirstTransporterVatPage, CheckMode, userAnswers) mustBe
            controllers.sections.firstTransporter.routes.FirstTransporterAddressController.onPageLoad(userAnswers.ern, userAnswers.draftId, NormalMode)
        }
      }
      "must redirect to CYA" - {
        "when FirstTransporterVatPage and FirstTransporterAddressPage are non-empty" in {
          val userAnswers = emptyUserAnswers
            .set(FirstTransporterVatPage, VatNumberModel(false, None))
            .set(FirstTransporterAddressPage, testUserAddress)
          navigator.nextPage(FirstTransporterVatPage, CheckMode, userAnswers) mustBe
            controllers.sections.firstTransporter.routes.FirstTransporterCheckAnswersController.onPageLoad(userAnswers.ern, userAnswers.draftId)
        }
      }
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
