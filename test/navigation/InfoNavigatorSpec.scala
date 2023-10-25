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
import models._
import pages._
import pages.sections.info._

class InfoNavigatorSpec extends SpecBase {

  val navigator = new InfoNavigator

  "InfoNavigator" - {

    "in Normal mode" - {

      "must go from a page that doesn't exist in the route map to Index" in {

        case object UnknownPage extends Page
        navigator.nextPage(UnknownPage, NormalMode, testErn) mustBe
          testOnly.controllers.routes.UnderConstructionController.onPageLoad()
      }

      "for the DispatchPlace page" - {

        "must go to the Destination Type page" in {

          navigator.nextPage(DispatchPlacePage, NormalMode, testErn) mustBe
            controllers.sections.info.routes.DestinationTypeController.onPageLoad(testErn)
        }
      }

      "for the DestinationType page" - {

        "must go to the Deferred Movement page" in {

          navigator.nextPage(DestinationTypePage, NormalMode, testErn) mustBe
            controllers.sections.info.routes.DeferredMovementController.onPageLoad(testErn)
        }
      }

      "for the DeferredMovement page" - {

        "must go to the LocalReferenceNumber page" in {

          navigator.nextPage(DeferredMovementPage, NormalMode, testErn) mustBe
            controllers.sections.info.routes.LocalReferenceNumberController.onPageLoad(testErn)
        }
      }

      "for the LocalReferenceNumber page" - {

        "must go to the Invoice Details page" in {

          navigator.nextPage(LocalReferenceNumberPage, NormalMode, testErn) mustBe
            controllers.sections.info.routes.InvoiceDetailsController.onPageLoad(testErn)
        }
      }

      "for the Invoice Details page" - {

        //TODO update when CAMINFO006 is complete
        "must go to the Under Construction page" in {

          navigator.nextPage(InvoiceDetailsPage, NormalMode, testErn) mustBe
            testOnly.controllers.routes.UnderConstructionController.onPageLoad()
        }
      }
    }
  }
}
