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

package controllers.sections.sad

import base.SpecBase
import play.api.http.Status.SEE_OTHER
import play.api.test.FakeRequest
import play.api.test.Helpers._

class SadIndexControllerSpec extends SpecBase {
  "SadIndexController" - {

    "for XIRC traders" - {
      val ern = "XIWK123"
      "when SadSection.isCompleted" - {
        // TODO: remove ignore when CYA page is built
        "must redirect to the CYA controller" ignore {
          val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

          running(application) {

            val request = FakeRequest(GET, controllers.sections.sad.routes.SadIndexController.onPageLoad(ern, testDraftId).url)
            val result = route(application, request).value

            status(result) mustEqual SEE_OTHER
            redirectLocation(result) mustBe Some(testOnly.controllers.routes.UnderConstructionController.onPageLoad().url)
          }
        }
      }

      "must redirect to the sad import number controller" in {
        val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

        running(application) {

          val request = FakeRequest(GET, controllers.sections.sad.routes.SadIndexController.onPageLoad(ern, testDraftId).url)
          val result = route(application, request).value

          status(result) mustEqual SEE_OTHER
          redirectLocation(result) mustBe Some(testOnly.controllers.routes.UnderConstructionController.onPageLoad().url)
        }
      }
    }

    "for any other traders" - {
      Seq("XIWK123", "GBRC123", "GBWK123").foreach {
        ern =>
          s"must redirect to the tasklist for traders starting with ${ern.take(4)}" in {
            val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

            running(application) {

              val request = FakeRequest(GET, controllers.sections.sad.routes.SadIndexController.onPageLoad(ern, testDraftId).url)
              val result = route(application, request).value

              status(result) mustEqual SEE_OTHER
              redirectLocation(result) mustBe Some(testOnly.controllers.routes.UnderConstructionController.onPageLoad().url)
            }
          }
      }
    }
  }
}