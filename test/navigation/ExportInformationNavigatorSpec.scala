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
import pages.sections.exportInformation.{ExportCustomsOfficePage, ExportInformationCheckAnswersPage}

class ExportInformationNavigatorSpec extends SpecBase {

  val navigator = new ExportInformationNavigator

  "ExportInformationNavigator" - {

    "in Normal mode" - {

      "must go from a page that doesn't exist in the route map to Index" in {

        case object UnknownPage extends Page
        navigator.nextPage(UnknownPage, NormalMode, emptyUserAnswers) mustBe
          routes.IndexController.onPageLoad(testErn)
      }
    }

    "for the ExportCustomsOfficePage" - {

      "must go to ExportInformationCheckAnswersPage" in {

        val userAnswers = emptyUserAnswers.set(ExportCustomsOfficePage, "AB123456")

        navigator.nextPage(ExportCustomsOfficePage, NormalMode, userAnswers) mustBe
          controllers.sections.exportInformation.routes.ExportInformationCheckAnswersController.onPageLoad(testErn, testDraftId)
      }
    }

    "for the ExportInformationCheckAnswersPage" - {

      "must go to the next section" in {

        navigator.nextPage(ExportInformationCheckAnswersPage, NormalMode, emptyUserAnswers) mustBe
          testOnly.controllers.routes.UnderConstructionController.onPageLoad()
      }
    }
  }
}
