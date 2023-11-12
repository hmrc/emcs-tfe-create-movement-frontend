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
import models.NormalMode
import pages.sections.sad.ImportNumberPage
import play.api.http.Status.SEE_OTHER
import play.api.test.FakeRequest
import play.api.test.Helpers._

class SadIndexControllerSpec extends SpecBase {
  "SadIndexController" - {

    "for XIRC traders" - {
      val ern = "XIRC123"
      "when SadSectionItem.isCompleted is true" - {
        "must redirect to the CYA controller" in {
          val application = applicationBuilder(userAnswers = Some(emptyUserAnswers.set(ImportNumberPage(testIndex1), "beans"))).build()

          running(application) {

            val request = FakeRequest(GET, controllers.sections.sad.routes.SadIndexController.onPageLoad(ern, testDraftId).url)
            val result = route(application, request).value

            status(result) mustEqual SEE_OTHER
            redirectLocation(result) mustBe Some(controllers.sections.sad.routes.SadAddToListController.onPageLoad(ern, testDraftId).url)
          }
        }
      }

      "when SadSectionItem.isCompleted is false" - {
        "must redirect to the sad import number controller" in {
          val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

          running(application) {

            val request = FakeRequest(GET, controllers.sections.sad.routes.SadIndexController.onPageLoad(ern, testDraftId).url)
            val result = route(application, request).value

            status(result) mustEqual SEE_OTHER
            redirectLocation(result) mustBe Some(controllers.sections.sad.routes.ImportNumberController.onPageLoad(ern, testDraftId, testIndex1, NormalMode).url)
          }
        }
      }

    }
    "for GBRC traders" - {
      val ern = "GBRC123"
      "when SadSectionItem.isCompleted is true" - {
        "must redirect to the CYA controller" in {
          val application = applicationBuilder(userAnswers = Some(emptyUserAnswers.set(ImportNumberPage(testIndex1), "beans"))).build()

          running(application) {

            val request = FakeRequest(GET, controllers.sections.sad.routes.SadIndexController.onPageLoad(ern, testDraftId).url)
            val result = route(application, request).value

            status(result) mustEqual SEE_OTHER
            redirectLocation(result) mustBe Some(controllers.sections.sad.routes.SadAddToListController.onPageLoad(ern, testDraftId).url)
          }
        }
      }

      "when SadSectionItem.isCompleted is false" - {
        "must redirect to the sad import number controller" in {
          val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

          running(application) {

            val request = FakeRequest(GET, controllers.sections.sad.routes.SadIndexController.onPageLoad(ern, testDraftId).url)
            val result = route(application, request).value

            status(result) mustEqual SEE_OTHER
            redirectLocation(result) mustBe Some(controllers.sections.sad.routes.ImportNumberController.onPageLoad(ern, testDraftId, testIndex1, NormalMode).url)
          }
        }
      }

    }
    "for any other traders" - {
      Seq("XIWK123", "GBWK123").foreach {
        ern =>
          s"must redirect to the tasklist for traders starting with ${ern.take(4)}" in {
            val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

            running(application) {

              val request = FakeRequest(GET, controllers.sections.sad.routes.SadIndexController.onPageLoad(ern, testDraftId).url)
              val result = route(application, request).value

              status(result) mustEqual SEE_OTHER
              redirectLocation(result) mustBe Some(controllers.routes.DraftMovementController.onPageLoad(ern, testDraftId).url)
            }
          }
      }
    }
  }
}