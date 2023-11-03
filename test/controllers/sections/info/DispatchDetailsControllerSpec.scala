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
import forms.sections.info.DispatchDetailsFormProvider
import mocks.services.{MockPreDraftService, MockUserAnswersService}
import models.sections.info.DispatchDetailsModel
import models.{NormalMode, UserAnswers}
import navigation.FakeNavigators.FakeInfoNavigator
import navigation.InformationNavigator
import pages.sections.info.{DeferredMovementPage, DispatchDetailsPage}
import play.api.inject.bind
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.{PreDraftService, UserAnswersService}
import utils.DateTimeUtils
import views.html.sections.info.DispatchDetailsView

import java.time.{LocalDate, LocalTime}
import scala.concurrent.Future

class DispatchDetailsControllerSpec extends SpecBase with MockUserAnswersService with MockPreDraftService with DateTimeUtils {

  val testLocalDate = LocalDate.of(2023, 2, 9)


  class Fixture(val userAnswers: Option[UserAnswers] = Some(emptyUserAnswers)) {

    val formProvider = new DispatchDetailsFormProvider()
    val form = formProvider()

    lazy val dispatchDetailsRoute = controllers.sections.info.routes.DispatchDetailsController.onPreDraftPageLoad(testErn, NormalMode).url
    lazy val dispatchDetailsOnSubmit = controllers.sections.info.routes.DispatchDetailsController.onPreDraftSubmit(testErn, NormalMode)

    val application = applicationBuilder(userAnswers)
      .overrides(
        bind[InformationNavigator].toInstance(new FakeInfoNavigator(testOnwardRoute)),
        bind[UserAnswersService].toInstance(mockUserAnswersService),
        bind[PreDraftService].toInstance(mockPreDraftService)
      )
      .build()

    val view = application.injector.instanceOf[DispatchDetailsView]
  }

  "DispatchDetails Controller" - {

    "onPreDraftPageLoad" - {

      "must redirect when there is no deferred movement answer" in new Fixture(Some(emptyUserAnswers)) {
        running(application) {
          val request = FakeRequest(GET, dispatchDetailsRoute)
          val result = route(application, request).value

          status(result) mustEqual SEE_OTHER
          redirectLocation(result).value mustEqual controllers.sections.info.routes.DeferredMovementController.onPreDraftPageLoad(testErn, NormalMode).url
        }
      }

      Seq(
        ("is a deferred movement" -> true),
        ("is not a deferred movement" -> false)
      ).foreach {
        case (action, deferredMovement) =>

          s"when this $action" - {

            "must return OK and the correct view" in new Fixture(Some(emptyUserAnswers.set(DeferredMovementPage, deferredMovement))) {
              running(application) {

                val request = FakeRequest(GET, dispatchDetailsRoute)

                val result = route(application, request).value

                status(result) mustEqual OK
                contentAsString(result) mustEqual view(
                  form = form,
                  deferredMovement = deferredMovement,
                  onSubmitCall = dispatchDetailsOnSubmit,
                  skipQuestionCall = testOnwardRoute
                )(dataRequest(request), messages(application)).toString
              }
            }
          }
      }


    }

    "onPreDraftSubmit" - {

      "must redirect when there is no deferred movement answer" in new Fixture(Some(emptyUserAnswers)) {
        running(application) {
          val request = FakeRequest(POST, dispatchDetailsOnSubmit.url)
          val result = route(application, request).value

          status(result) mustEqual SEE_OTHER
          redirectLocation(result).value mustEqual controllers.sections.info.routes.DeferredMovementController.onPreDraftPageLoad(testErn, NormalMode).url
        }
      }

      Seq(
        ("is a deferred movement" -> true),
        ("is not a deferred movement" -> false)
      ).foreach {
        case (action, deferredMovement) =>

          s"when this $action" - {

            val userAnswersSoFar = emptyUserAnswers.set(DeferredMovementPage, deferredMovement)

            "must redirect to the next page when valid data is submitted" in new Fixture(Some(userAnswersSoFar)) {
              running(application) {

                val dispatchDetailsModel = DispatchDetailsModel(
                  date = LocalDate.of(2022,12,31),
                  time = LocalTime.of(6,0)
                )

                MockPreDraftService.set(userAnswersSoFar.set(DispatchDetailsPage, dispatchDetailsModel)).returns(Future.successful(true))

                val request =
                  FakeRequest(POST, dispatchDetailsRoute)
                    .withFormUrlEncodedBody(
                      ("value.day", "31"),
                      ("value.month", "12"),
                      ("value.year", "2022"),
                      ("time", "6am")
                    )

                val result = route(application, request).value

                status(result) mustEqual SEE_OTHER
                redirectLocation(result).value mustEqual testOnwardRoute.url
              }
            }

            "must return a Bad Request and errors when invalid data is submitted" in new Fixture(Some(userAnswersSoFar)) {
              running(application) {
                val request = FakeRequest(POST, dispatchDetailsOnSubmit.url)
                  .withFormUrlEncodedBody(
                    ("time", "ten past twelve")
                  )

                val boundForm = form.bind(Map("time" -> "ten past twelve"))

                val result = route(application, request).value

                status(result) mustEqual BAD_REQUEST
                contentAsString(result) mustEqual view(
                  form = boundForm,
                  deferredMovement = deferredMovement,
                  onSubmitCall = dispatchDetailsOnSubmit,
                  skipQuestionCall = testOnwardRoute
                )(dataRequest(request), messages(application)).toString
              }
            }

          }
      }
    }

  }
}
