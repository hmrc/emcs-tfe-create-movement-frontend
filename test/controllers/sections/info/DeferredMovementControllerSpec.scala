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

package controllers.sections.info

import base.SpecBase
import forms.sections.info.DeferredMovementFormProvider
import mocks.services.{MockPreDraftService, MockUserAnswersService}
import models.{NormalMode, UserAnswers}
import navigation.FakeNavigators.FakeInfoNavigator
import navigation.InformationNavigator
import pages.sections.info.DeferredMovementPage
import play.api.inject.bind
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.{PreDraftService, UserAnswersService}
import views.html.sections.info.DeferredMovementView

import scala.concurrent.Future

class DeferredMovementControllerSpec extends SpecBase with MockUserAnswersService with MockPreDraftService {

  class Fixture(userAnswers: Option[UserAnswers]) {
    val application =
      applicationBuilder(userAnswers = userAnswers)
        .overrides(
          bind[InformationNavigator].toInstance(new FakeInfoNavigator(testOnwardRoute)),
          bind[UserAnswersService].toInstance(mockUserAnswersService),
          bind[PreDraftService].toInstance(mockPreDraftService)
        )
        .build()

    val view = application.injector.instanceOf[DeferredMovementView]
  }

  val formProvider = new DeferredMovementFormProvider()
  val form = formProvider()

  lazy val deferredMovementPreDraftRoute = controllers.sections.info.routes.DeferredMovementController.onPreDraftPageLoad(testErn, NormalMode).url
  lazy val deferredMovementPreDraftSubmitRoute = controllers.sections.info.routes.DeferredMovementController.onPreDraftSubmit(testErn, NormalMode)
  lazy val deferredMovementRoute = controllers.sections.info.routes.DeferredMovementController.onPageLoad(testErn, testDraftId).url
  lazy val deferredMovementSubmitRoute = controllers.sections.info.routes.DeferredMovementController.onSubmit(testErn, testDraftId)

  "DeferredMovement Controller" - {

    "pre-draft" - {

      "must return OK and the correct view for a GET" in new Fixture(Some(emptyUserAnswers)) {

        running(application) {
          val request = FakeRequest(GET, deferredMovementPreDraftRoute)

          val result = route(application, request).value

          status(result) mustEqual OK
          contentAsString(result) mustEqual view(form, deferredMovementPreDraftSubmitRoute)(dataRequest(request), messages(application)).toString
        }
      }

      "must redirect to the next page when valid data is submitted" in new Fixture(Some(emptyUserAnswers)) {

        running(application) {
          val request =
            FakeRequest(POST, deferredMovementPreDraftSubmitRoute.url)
              .withFormUrlEncodedBody(("value", "true"))

          MockPreDraftService.set(emptyUserAnswers.set(DeferredMovementPage(), true)).returns(Future.successful(true))

          val result = route(application, request).value

          status(result) mustEqual SEE_OTHER
          redirectLocation(result).value mustEqual testOnwardRoute.url
        }
      }

      "must return a Bad Request and errors when invalid data is submitted" in new Fixture(Some(emptyUserAnswers)) {

        running(application) {
          val request =
            FakeRequest(POST, deferredMovementPreDraftSubmitRoute.url)
              .withFormUrlEncodedBody(("value", ""))

          val boundForm = form.bind(Map("value" -> ""))

          val result = route(application, request).value

          status(result) mustEqual BAD_REQUEST
          contentAsString(result) mustEqual view(boundForm, deferredMovementPreDraftSubmitRoute)(dataRequest(request), messages(application)).toString
        }
      }
    }

    "post-draft" - {

      "must return OK and the correct view for a GET" in new Fixture(Some(emptyUserAnswers)) {

        running(application) {
          val request = FakeRequest(GET, deferredMovementRoute)

          val result = route(application, request).value

          status(result) mustEqual OK
          contentAsString(result) mustEqual view(form, deferredMovementSubmitRoute)(dataRequest(request), messages(application)).toString
        }
      }

      "must redirect to the next page when valid data is submitted" in new Fixture(Some(emptyUserAnswers)) {

        running(application) {
          val request =
            FakeRequest(POST, deferredMovementSubmitRoute.url)
              .withFormUrlEncodedBody(("value", "true"))

          MockUserAnswersService.set(emptyUserAnswers.set(DeferredMovementPage(), true)).returns(Future.successful(emptyUserAnswers))

          val result = route(application, request).value

          status(result) mustEqual SEE_OTHER
          redirectLocation(result).value mustEqual testOnwardRoute.url
        }
      }

      "must return a Bad Request and errors when invalid data is submitted" in new Fixture(Some(emptyUserAnswers)) {

        running(application) {
          val request =
            FakeRequest(POST, deferredMovementSubmitRoute.url)
              .withFormUrlEncodedBody(("value", ""))

          val boundForm = form.bind(Map("value" -> ""))

          val result = route(application, request).value

          status(result) mustEqual BAD_REQUEST
          contentAsString(result) mustEqual view(boundForm, deferredMovementSubmitRoute)(dataRequest(request), messages(application)).toString
        }
      }
    }
  }
}
