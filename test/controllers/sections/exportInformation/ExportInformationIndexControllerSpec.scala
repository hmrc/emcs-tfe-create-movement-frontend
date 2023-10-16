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

package controllers.sections.exportInformation

import base.SpecBase
import models.NormalMode
import pages.sections.exportInformation.ExportCustomsOfficePage
import play.api.http.Status.SEE_OTHER
import play.api.test.FakeRequest
import play.api.test.Helpers._

class ExportInformationIndexControllerSpec extends SpecBase {
  "ExportInformationIndexController" - {

    "when ExportInformationSection.isCompleted" - {
      "must redirect to the CYA controller" in {
        val application = applicationBuilder(userAnswers = Some(emptyUserAnswers.set(ExportCustomsOfficePage, ""))).build()

        running(application) {

          val request = FakeRequest(GET, controllers.sections.exportInformation.routes.ExportInformationIndexController.onPageLoad(testErn, testDraftId).url)
          val result = route(application, request).value

          status(result) mustEqual SEE_OTHER
          redirectLocation(result) mustBe
            Some(controllers.sections.exportInformation.routes.ExportInformationCheckAnswersController.onPageLoad(testErn, testDraftId).url)
        }
      }
    }

    "must redirect to the export customs office controller" in {
      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      running(application) {

        val request = FakeRequest(GET, controllers.sections.exportInformation.routes.ExportInformationIndexController.onPageLoad(testErn, testDraftId).url)
        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result) mustBe
          Some(controllers.sections.exportInformation.routes.ExportCustomsOfficeController.onPageLoad(testErn, testDraftId, NormalMode).url)
      }
    }
  }
}