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

package controllers.sections.documents

import base.SpecBase
import models.NormalMode
import pages.sections.documents.{DocumentReferencePage, DocumentsCertificatesPage, ReferenceAvailablePage}
import play.api.http.Status.SEE_OTHER
import play.api.test.FakeRequest
import play.api.test.Helpers._

class DocumentsIndexControllerSpec extends SpecBase {

  "DocumentsIndexController" - {

    "when DocumentsCertificate is false" - {

      "must redirect to the DocumentsCheckAnswers page" in {

        val application = applicationBuilder(userAnswers = Some(
          emptyUserAnswers.set(DocumentsCertificatesPage, false)
        )).build()

        running(application) {

          val request = FakeRequest(GET, routes.DocumentsIndexController.onPageLoad(testErn, testDraftId).url)
          val result = route(application, request).value

          status(result) mustEqual SEE_OTHER
          redirectLocation(result) mustBe Some(routes.DocumentsCheckAnswersController.onPageLoad(testErn, testDraftId).url)
        }
      }
    }

    "when DocumentsCertificate is true" - {

      "when DocumentsCount is > 0" - {

        "must redirect to the DocumentReference page" in {

          val application = applicationBuilder(userAnswers = Some(
            emptyUserAnswers
              .set(DocumentsCertificatesPage, true)
              .set(ReferenceAvailablePage(0), true)
              .set(DocumentReferencePage(0), "reference")
          )).build()

          running(application) {

            val request = FakeRequest(GET, routes.DocumentsIndexController.onPageLoad(testErn, testDraftId).url)
            val result = route(application, request).value

            status(result) mustEqual SEE_OTHER
            redirectLocation(result) mustBe Some(routes.DocumentsAddToListController.onPageLoad(testErn, testDraftId, NormalMode).url)
          }
        }
      }

      "when DocumentsCount is 0" - {

        "must redirect to the DocumentReference page" in {

          val application = applicationBuilder(userAnswers = Some(
            emptyUserAnswers.set(DocumentsCertificatesPage, true)
          )).build()

          running(application) {

            val request = FakeRequest(GET, routes.DocumentsIndexController.onPageLoad(testErn, testDraftId).url)
            val result = route(application, request).value

            status(result) mustEqual SEE_OTHER
            redirectLocation(result) mustBe Some(routes.DocumentsCertificatesController.onPageLoad(testErn, testDraftId, NormalMode).url)
          }
        }
      }
    }

    "when DocumentsCertificate is not answered" - {

      "must redirect to the DocumentsCheckAnswers page" in {

        val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

        running(application) {

          val request = FakeRequest(GET, routes.DocumentsIndexController.onPageLoad(testErn, testDraftId).url)
          val result = route(application, request).value

          status(result) mustEqual SEE_OTHER
          redirectLocation(result) mustBe Some(routes.DocumentsCertificatesController.onPageLoad(testErn, testDraftId, NormalMode).url)
        }
      }
    }
  }
}
