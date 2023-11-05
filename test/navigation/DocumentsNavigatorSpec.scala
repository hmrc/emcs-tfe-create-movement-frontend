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
import controllers.sections.documents.routes
import models.{CheckMode, NormalMode, ReviewMode}
import pages.Page
import pages.sections.documents._

class DocumentsNavigatorSpec extends SpecBase {
  val navigator = new DocumentsNavigator

  "DocumentsNavigator" - {

    "in Normal mode" - {

      "must go from a page that doesn't exist in the route map to Documents CYA" in {

        case object UnknownPage extends Page
        navigator.nextPage(UnknownPage, NormalMode, emptyUserAnswers) mustBe
          testOnly.controllers.routes.UnderConstructionController.onPageLoad()
      }

      //TODO update to correct page when finished
      "must go from DocumentsCertificatesPage to UnderConstructionPage" in {

        "must go to CheckAnswersPage if no is selected" in {
          navigator.nextPage(DocumentsCertificatesPage, NormalMode, emptyUserAnswers) mustBe
            testOnly.controllers.routes.UnderConstructionController.onPageLoad()
        }

        "must go to CAM-DOC02 page if yes is selected" in {
          navigator.nextPage(DocumentsCertificatesPage, NormalMode, emptyUserAnswers) mustBe
            testOnly.controllers.routes.UnderConstructionController.onPageLoad()
        }
      }

      "for the DocumentsCheckAnswersPage" - {
        "must go to task list page when built" in {
          navigator.nextPage(DocumentsCheckAnswersPage, NormalMode, emptyUserAnswers) mustBe
            testOnly.controllers.routes.UnderConstructionController.onPageLoad()
        }
      }

      "for the ReferenceAvailablePage" - {

        "to DocumentReference when user selects yes" in {

          val userAnswers = emptyUserAnswers.set(ReferenceAvailablePage, true)

          navigator.nextPage(ReferenceAvailablePage, NormalMode, userAnswers) mustBe
            routes.DocumentReferenceController.onPageLoad(testErn, testDraftId, NormalMode)
        }

        "to DocumentDescription when user selects no" in {

          val userAnswers = emptyUserAnswers.set(ReferenceAvailablePage, false)

          navigator.nextPage(ReferenceAvailablePage, NormalMode, userAnswers) mustBe
            routes.DocumentDescriptionController.onPageLoad(testErn, testDraftId, NormalMode)
        }

        "to Journey Recovery if no answer is present" in {

          navigator.nextPage(ReferenceAvailablePage, NormalMode, emptyUserAnswers) mustBe
            controllers.routes.JourneyRecoveryController.onPageLoad()
        }
      }

      "must go from DocumentDescriptionPage to DocumentReferencePage" in {

        navigator.nextPage(DocumentDescriptionPage, NormalMode, emptyUserAnswers) mustBe
          testOnly.controllers.routes.UnderConstructionController.onPageLoad()
      }

      "must go from DocumentReferencePage to UnderConstruction" in {

        navigator.nextPage(DocumentReferencePage, NormalMode, emptyUserAnswers) mustBe
          testOnly.controllers.routes.UnderConstructionController.onPageLoad()
      }
    }

    "in Check mode" - {

      "must go to CheckYourAnswersDocumentsController if DocumentsCertificatesPage is no" in {
        navigator.nextPage(DocumentsCertificatesPage, CheckMode, emptyUserAnswers.set(DocumentsCertificatesPage, false)) mustBe
          routes.DocumentsCheckAnswersController.onPageLoad(testErn, testDraftId)
      }

      "must go to AddToListPage if DocumentsCertificatesPage is yes" in {
        //TODO redirect to AddToList CAM-DOC06 Page when built
        navigator.nextPage(DocumentsCertificatesPage, CheckMode, emptyUserAnswers.set(DocumentsCertificatesPage, true)) mustBe
          testOnly.controllers.routes.UnderConstructionController.onPageLoad()
      }

    }

    "in Review mode" - {

      "must go to CheckYourAnswers" in {
        case object UnknownPage extends Page
        navigator.nextPage(UnknownPage, ReviewMode, emptyUserAnswers) mustBe
          controllers.routes.CheckYourAnswersController.onPageLoad(testErn, testDraftId)
      }
    }
  }
}
