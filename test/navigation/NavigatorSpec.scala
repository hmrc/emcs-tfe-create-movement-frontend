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
import models._
import pages._
import pages.sections.consignor._
import pages.sections.info._

class NavigatorSpec extends SpecBase {

  val navigator = new Navigator

  "Navigator" - {

    "in Normal mode" - {

      "must go from a page that doesn't exist in the route map to Index" in {

        case object UnknownPage extends Page
        navigator.nextPage(UnknownPage, NormalMode, emptyUserAnswers) mustBe
          routes.IndexController.onPageLoad(testErn)
      }

      "for the LocalReferenceNumber page" - {

        "must go to the Invoice Details page" in {

          navigator.nextPage(LocalReferenceNumberPage, NormalMode, emptyUserAnswers) mustBe
            controllers.sections.info.routes.InvoiceDetailsController.onPageLoad(testErn, testLrn)
        }
      }

      "for the Invoice Detailspage" - {

        //TODO update when CAMINFO006 is complete
        "must go to the Under Construction page" in {

          navigator.nextPage(InvoiceDetailsPage, NormalMode, emptyUserAnswers) mustBe
            testOnly.controllers.routes.UnderConstructionController.onPageLoad()
        }
      }

      "for the ConsignorAddress page" - {

        "must go to the CheckYourAnswersConsignor page" in {

          navigator.nextPage(ConsignorAddressPage, NormalMode, emptyUserAnswers) mustBe
            controllers.sections.consignor.routes.CheckYourAnswersConsignorController.onPageLoad(testErn, testLrn)
        }
      }

      "for the CheckYourAnswersConsignor page" - {

        "must go to the UnderConstruction page" in {

          navigator.nextPage(CheckAnswersConsignorPage, NormalMode, emptyUserAnswers) mustBe
            testOnly.controllers.routes.UnderConstructionController.onPageLoad()
        }
      }

      "for the CheckYourAnswers page" - {

        "must go to the Confirmation page" in {

          navigator.nextPage(CheckAnswersPage, NormalMode, emptyUserAnswers) mustBe
            routes.ConfirmationController.onPageLoad(testErn, testLrn)
        }
      }
    }

    "in Check mode" - {

      "must go from a page that doesn't exist in the edit route map to CheckYourAnswers" in {

        case object UnknownPage extends Page
        navigator.nextPage(UnknownPage, CheckMode, emptyUserAnswers) mustBe
          routes.CheckYourAnswersController.onPageLoad(testErn, testLrn)
      }

      "must go from the ConsignorAddressPage to CheckYourAnswersConsignor" in {

        navigator.nextPage(ConsignorAddressPage, CheckMode, emptyUserAnswers) mustBe
          controllers.sections.consignor.routes.CheckYourAnswersConsignorController.onPageLoad(testErn, testLrn)
      }
    }

    "in review mode" - {

      "must go from a page that doesn't exist in the edit route map to CheckYourAnswers" in {

        case object UnknownPage extends Page
        navigator.nextPage(UnknownPage, ReviewMode, emptyUserAnswers) mustBe
          routes.CheckYourAnswersController.onPageLoad(testErn, testLrn)
      }
    }
  }
}
