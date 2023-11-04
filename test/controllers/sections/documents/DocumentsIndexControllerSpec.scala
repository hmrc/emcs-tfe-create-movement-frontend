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
import play.api.http.Status.SEE_OTHER
import play.api.test.FakeRequest
import play.api.test.Helpers._
import models.NormalMode
import pages.sections.documents.DocumentsCertificatesPage

class DocumentsIndexControllerSpec extends SpecBase {
  "DocumentsIndexController" - {

    "when DocumentsSection.isCompleted" - {
      // TODO: remove ignore when CAM-DOC02 page is built
      "must redirect to the Add To List controller" ignore {
        val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

        running(application) {

          val request = FakeRequest(GET, controllers.sections.documents.routes.DocumentsIndexController.onPageLoad(testErn, testDraftId).url)
          val result = route(application, request).value

          status(result) mustEqual SEE_OTHER
          redirectLocation(result) mustBe Some(testOnly.controllers.routes.UnderConstructionController.onPageLoad().url)
        }
      }
    }

    "must redirect to check answers page when Document Certificates page is false" in {
      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers.set(DocumentsCertificatesPage, false))).build()

      running(application) {

        val request = FakeRequest(GET, controllers.sections.documents.routes.DocumentsIndexController.onPageLoad(testErn, testDraftId).url)
        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result) mustBe
          Some(controllers.sections.documents.routes.DocumentsCheckAnswersController.onPageLoad(testErn, testDraftId).url)
      }
    }

    "must redirect to the documents required controller" in {
      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      running(application) {

        val request = FakeRequest(GET, controllers.sections.documents.routes.DocumentsIndexController.onPageLoad(testErn, testDraftId).url)
        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result) mustBe Some(controllers.sections.documents.routes.DocumentsCertificatesController.onPageLoad(testErn, testDraftId, NormalMode).url)
      }
    }
  }
}
