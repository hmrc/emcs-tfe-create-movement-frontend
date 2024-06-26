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

package controllers

import base.SpecBase
import mocks.services.{MockPreDraftService, MockUserAnswersService}
import models.UserAnswers
import play.api.test.FakeRequest
import play.api.test.Helpers._

import scala.concurrent.Future

class IndexControllerSpec extends SpecBase with MockPreDraftService with MockUserAnswersService {

  "Index Controller" - {
    "must redirect to the info Index controller" in {
      lazy val testController = new IndexController(
        messagesApi,
        mockPreDraftService,
        mockUserAnswersService,
        fakeAuthAction,
        fakeBetaAllowListAction,
        messagesControllerComponents
      )

      MockPreDraftService.set(UserAnswers(
          testNorthernIrelandErn,
          testSessionId,
          hasBeenSubmitted = false,
          submissionFailures = Seq.empty,
          validationErrors = Seq.empty,
          submittedDraftId = None
        )).returns(Future.successful(true))

      val request = FakeRequest()
      val result = testController.onPageLoad(testNorthernIrelandErn)(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result) mustBe Some(controllers.sections.info.routes.InfoIndexController.onPreDraftPageLoad(testNorthernIrelandErn).url)
    }
  }
}
