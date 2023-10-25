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

package controllers.sections.firstTransporter

import base.SpecBase
import models.{NormalMode, UserAddress}
import pages.sections.firstTransporter.{FirstTransporterAddressPage, FirstTransporterNamePage, FirstTransporterVatPage}
import play.api.http.Status.SEE_OTHER
import play.api.test.FakeRequest
import play.api.test.Helpers._

class FirstTransporterIndexControllerSpec extends SpecBase {
  "FirstTransporterIndexController" - {

    "when FirstTransporterSection.isCompleted" - {
      "must redirect to the CYA controller" in {
        val application = applicationBuilder(userAnswers = Some(
          emptyUserAnswers
            .set(FirstTransporterNamePage, "")
            .set(FirstTransporterVatPage, "")
            .set(FirstTransporterAddressPage, UserAddress(None, "", "", ""))
        )).build()

        running(application) {

          val request = FakeRequest(GET, controllers.sections.firstTransporter.routes.FirstTransporterIndexController.onPageLoad(testErn, testDraftId).url)
          val result = route(application, request).value

          status(result) mustEqual SEE_OTHER
          redirectLocation(result) mustBe
            Some(controllers.sections.firstTransporter.routes.FirstTransporterCheckAnswersController.onPageLoad(testErn, testDraftId).url)
        }
      }
    }

    "must redirect to the first transporter name controller" in {
      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      running(application) {

        val request = FakeRequest(GET, controllers.sections.firstTransporter.routes.FirstTransporterIndexController.onPageLoad(testErn, testDraftId).url)
        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result) mustBe
          Some(controllers.sections.firstTransporter.routes.FirstTransporterNameController.onPageLoad(testErn, testDraftId, NormalMode).url)
      }
    }
  }
}