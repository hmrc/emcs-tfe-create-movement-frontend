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
import models.requests.DataRequest
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import play.api.test.Helpers._
import viewmodels.helpers.CheckYourAnswersDocumentsHelper
import views.html.sections.documents.DocumentsCheckAnswersView

class DocumentsCheckAnswersControllerSpec extends SpecBase {

  "DocumentsCheckAnswers Controller" - {

    "must return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, routes.DocumentsCheckAnswersController.onPageLoad(testErn, testDraftId).url)
        implicit val fakeDataRequest: DataRequest[AnyContentAsEmpty.type] = dataRequest(request)
        implicit val msgs = messages(request)

        val result = route(application, fakeDataRequest).value

        val view = application.injector.instanceOf[DocumentsCheckAnswersView]
        val summaryList = application.injector.instanceOf[CheckYourAnswersDocumentsHelper].summaryList()

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(summaryList).toString
      }
    }

    "must redirect to task list for POST" in {
      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      running(application) {
        val request = FakeRequest(POST, routes.DocumentsCheckAnswersController.onSubmit(testErn, testDraftId).url)
        implicit val fakeDataRequest: DataRequest[AnyContentAsEmpty.type] = dataRequest(request)

        val result = route(application, fakeDataRequest).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual
          controllers.routes.DraftMovementController.onPageLoad(testErn, testDraftId).url

      }
    }

    "must redirect to Journey Recovery for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      running(application) {
        val request = FakeRequest(GET, routes.DocumentsCheckAnswersController.onPageLoad(testErn, testDraftId).url)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual controllers.routes.JourneyRecoveryController.onPageLoad().url
      }
    }

    "must redirect to Journey Recovery for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      running(application) {
        val request =
          FakeRequest(POST, routes.DocumentsCheckAnswersController.onPageLoad(testErn, testDraftId).url)
            .withFormUrlEncodedBody(("value", "true"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual controllers.routes.JourneyRecoveryController.onPageLoad().url
      }
    }
  }
}
