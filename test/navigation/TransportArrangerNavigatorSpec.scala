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
import models.sections.transportArranger.TransportArranger.{Consignee, Consignor, GoodsOwner, Other}
import pages.Page
import pages.sections.transportArranger.{TransportArrangerNamePage, TransportArrangerPage}

class TransportArrangerNavigatorSpec extends SpecBase {
  val navigator = new TransportArrangerNavigator

  "TransportArrangerNavigator" - {

    "in Normal mode" - {

      "must go from a page that doesn't exist in the route map to Index" in {

        case object UnknownPage extends Page
        navigator.nextPage(UnknownPage, NormalMode, emptyUserAnswers) mustBe
          routes.IndexController.onPageLoad(testErn)
      }
    }

    "for the TransportArrangerPage" - {

      "must go to CAM-TA02" - {

        // TODO redirect to CAM-TA02
        "when the answer is `Goods Owner`" in {
          val userAnswers = emptyUserAnswers.set(TransportArrangerPage, GoodsOwner)

          navigator.nextPage(TransportArrangerPage, NormalMode, userAnswers) mustBe
            testOnly.controllers.routes.UnderConstructionController.onPageLoad()
        }

        // TODO redirect to CAM-TA02
        "when the answer is `Other`" in {
          val userAnswers = emptyUserAnswers.set(TransportArrangerPage, Other)

          navigator.nextPage(TransportArrangerPage, NormalMode, userAnswers) mustBe
            testOnly.controllers.routes.UnderConstructionController.onPageLoad()
        }
      }

      "must go to CAM-TA05" - {

        // TODO redirect to CAM-TA05
        "when the answer is `Consignee`" in {
          val userAnswers = emptyUserAnswers.set(TransportArrangerPage, Consignee)

          navigator.nextPage(TransportArrangerPage, NormalMode, userAnswers) mustBe
            testOnly.controllers.routes.UnderConstructionController.onPageLoad()
        }

        // TODO redirect to CAM-TA05
        "when the answer is `Consignor`" in {
          val userAnswers = emptyUserAnswers.set(TransportArrangerPage, Consignor)

          navigator.nextPage(TransportArrangerPage, NormalMode, userAnswers) mustBe
            testOnly.controllers.routes.UnderConstructionController.onPageLoad()
        }
      }

    }

    "for the TransportArrangerNamePage" - {

      "must go to CAM-TA03" in {
        val userAnswers = emptyUserAnswers.set(TransportArrangerNamePage, "some name here")

        navigator.nextPage(TransportArrangerPage, NormalMode, userAnswers) mustBe
          testOnly.controllers.routes.UnderConstructionController.onPageLoad()
      }
    }

  }
}
