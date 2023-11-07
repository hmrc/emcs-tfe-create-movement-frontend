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
<<<<<<< HEAD
import models.sections.documents.DocumentType
=======
import models.sections.documents.DocumentsAddToList
>>>>>>> 70381a6f ([ETFE-2479] updated navigator)
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

      "must go from DocumentsCertificatesPage" - {

        "to CheckAnswersPage if no is selected" in {
          val userAnswers = emptyUserAnswers.set(DocumentsCertificatesPage, false)

          navigator.nextPage(DocumentsCertificatesPage, NormalMode, userAnswers) mustBe
            controllers.sections.documents.routes.DocumentsCheckAnswersController.onPageLoad(testErn, testDraftId)
        }

        "to CAM-DOC02 page if yes is selected" in {
          val userAnswers = emptyUserAnswers.set(DocumentsCertificatesPage, true)

          navigator.nextPage(DocumentsCertificatesPage, NormalMode, userAnswers) mustBe
            controllers.sections.documents.routes.DocumentTypeController.onPageLoad(testErn, testDraftId, NormalMode)
        }
      }

      "must go from DocumentsTypePage" - {

        s"to CAM-DOC03 ReferenceAvailable if ${DocumentType.OtherCode} is selected" in {
          val userAnswers = emptyUserAnswers.set(DocumentTypePage, DocumentType.OtherCode)

          navigator.nextPage(DocumentTypePage, NormalMode, userAnswers) mustBe
            controllers.sections.documents.routes.ReferenceAvailableController.onPageLoad(testErn, testDraftId, NormalMode)
        }

        "to CAM-DOC05 DocumentReference page if yes is selected" in {
          val userAnswers = emptyUserAnswers.set(DocumentTypePage, "testCode")

          navigator.nextPage(DocumentTypePage, NormalMode, userAnswers) mustBe
            controllers.sections.documents.routes.DocumentReferenceController.onPageLoad(testErn, testDraftId, NormalMode)
        }
      }

      "for the DocumentsCheckAnswersPage" - {

        "must go to task list page when built" in {

          navigator.nextPage(DocumentsCheckAnswersPage, NormalMode, emptyUserAnswers) mustBe
            controllers.routes.DraftMovementController.onPageLoad(testErn, testDraftId)
        }
      }

      "for the ReferenceAvailablePage" - {

        "to DocumentReference when user selects yes" in {

          val userAnswers = emptyUserAnswers.set(ReferenceAvailablePage(0), true)

          navigator.nextPage(ReferenceAvailablePage(0), NormalMode, userAnswers) mustBe
            routes.DocumentReferenceController.onPageLoad(testErn, testDraftId, 0, NormalMode)
        }

        "to DocumentDescription when user selects no" in {

          val userAnswers = emptyUserAnswers.set(ReferenceAvailablePage(0), false)

          navigator.nextPage(ReferenceAvailablePage(0), NormalMode, userAnswers) mustBe
            routes.DocumentDescriptionController.onPageLoad(testErn, testDraftId, 0, NormalMode)
        }

        "to Journey Recovery if no answer is present" in {

          navigator.nextPage(ReferenceAvailablePage(0), NormalMode, emptyUserAnswers) mustBe
            controllers.routes.JourneyRecoveryController.onPageLoad()
        }
      }

      "must go from DocumentDescriptionPage to DocumentReferencePage" in {

        navigator.nextPage(DocumentDescriptionPage(0), NormalMode, emptyUserAnswers) mustBe
          routes.DocumentsAddToListController.onPageLoad(testErn, testDraftId, NormalMode)
      }

      "must go from DocumentReferencePage to UnderConstruction" in {

        navigator.nextPage(DocumentReferencePage(0), NormalMode, emptyUserAnswers) mustBe
          routes.DocumentsAddToListController.onPageLoad(testErn, testDraftId, NormalMode)
      }

      "must go from DocumentsAddToListPage" - {

        "to DocumentReference when user selects Yes" in {

          val userAnswers = emptyUserAnswers.set(DocumentsAddToListPage, DocumentsAddToList.Yes)

          navigator.nextPage(DocumentsAddToListPage, NormalMode, userAnswers) mustBe
            routes.ReferenceAvailableController.onPageLoad(testErn, testDraftId, 0, NormalMode)
        }

        "to DocumentDescription when user selects No" in {

          val userAnswers = emptyUserAnswers.set(DocumentsAddToListPage, DocumentsAddToList.No)

          navigator.nextPage(DocumentsAddToListPage, NormalMode, userAnswers) mustBe
            routes.DocumentsCheckAnswersController.onPageLoad(testErn, testDraftId)
        }

        "to DocumentDescription when user selects MoreLater" in {

          val userAnswers = emptyUserAnswers.set(DocumentsAddToListPage, DocumentsAddToList.MoreLater)

          navigator.nextPage(DocumentsAddToListPage, NormalMode, userAnswers) mustBe
            routes.DocumentsCheckAnswersController.onPageLoad(testErn, testDraftId)
        }
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
