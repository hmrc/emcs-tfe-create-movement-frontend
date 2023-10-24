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
import forms.sections.info.LocalReferenceNumberFormProvider
import mocks.services.{MockPreDraftService, MockUserAnswersService}
import models.sections.info.movementScenario.MovementScenario.{GbTaxWarehouse, UnknownDestination}
import models.{NormalMode, UserAnswers}
import navigation.FakeNavigators.FakeInfoNavigator
import navigation.InformationNavigator
import pages.sections.info.{DeferredMovementPage, DestinationTypePage, LocalReferenceNumberPage}
import play.api.Application
import play.api.inject.bind
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.{PreDraftService, UserAnswersService}
import views.html.sections.info.LocalReferenceNumberView

import scala.concurrent.Future

class LocalReferenceNumberControllerSpec extends SpecBase with MockUserAnswersService with MockPreDraftService {

  class Fixture(val userAnswers: Option[UserAnswers] = Some(emptyUserAnswers)) {

    val application: Application =
      applicationBuilder(userAnswers)
        .overrides(
          bind[InformationNavigator].toInstance(new FakeInfoNavigator(testOnwardRoute)),
          bind[UserAnswersService].toInstance(mockUserAnswersService),
          bind[PreDraftService].toInstance(mockPreDraftService)
        )
        .build()

    val view = application.injector.instanceOf[LocalReferenceNumberView]
  }

  val formProvider = new LocalReferenceNumberFormProvider()
  val form = formProvider(isDeferred = false)

  lazy val localReferenceNumberRoute = controllers.sections.info.routes.LocalReferenceNumberController.onPreDraftPageLoad(testErn, NormalMode).url
  lazy val localReferenceNumberSubmitAction = controllers.sections.info.routes.LocalReferenceNumberController.onPreDraftSubmit(testErn, NormalMode)

  "LocalReferenceNumberController" - {

    ".onPageLoad()" - {

      "when the Destination Type Page answer exists in session" - {

        "when the Deferred Movement answer exists in session" - {

          val answersSoFar = emptyUserAnswers
            .set(DestinationTypePage, GbTaxWarehouse)
            .set(DeferredMovementPage, false)

          "must return OK and the correct view for a GET" in new Fixture(userAnswers = Some(answersSoFar)) {
            running(application) {

              val request = FakeRequest(GET, localReferenceNumberRoute)
              val result = route(application, request).value

              status(result) mustEqual OK
              contentAsString(result) mustEqual view(isDeferred = false, form, localReferenceNumberSubmitAction)(dataRequest(request), messages(application)).toString
            }
          }
        }

        "when the Deferred Movement answer DOES NOT exist in session" - {

          val answersSoFar = emptyUserAnswers
            .set(DestinationTypePage, UnknownDestination)

          "must return SEE_OTHER and redirect to the Deferred Movement page" in new Fixture(userAnswers = Some(answersSoFar)) {
            running(application) {

              val request = FakeRequest(GET, localReferenceNumberRoute)
              val result = route(application, request).value

              status(result) mustEqual SEE_OTHER
              redirectLocation(result) mustBe Some(controllers.sections.info.routes.DeferredMovementController.onPreDraftPageLoad(testErn, NormalMode).url)
            }
          }
        }
      }

      "when the Destination Type Page answer does not exist in session" - {

        val answersSoFar = emptyUserAnswers

        "must return SEE_OTHER and redirect to the Destination Type page" in new Fixture(userAnswers = Some(answersSoFar)) {
          running(application) {

            val request =
              FakeRequest(POST, localReferenceNumberSubmitAction.url)
                .withFormUrlEncodedBody(("value", testDraftId))
            val result = route(application, request).value

            status(result) mustEqual SEE_OTHER
            redirectLocation(result) mustBe Some(controllers.sections.info.routes.DestinationTypeController.onPreDraftPageLoad(testErn, NormalMode).url)
          }
        }
      }
    }

    ".onSubmit()" - {

      "when the Destination Type Page answer exists" - {

        "when the Deferred Movement answer exists" - {

          val answersSoFar =
            emptyUserAnswers
              .set(DestinationTypePage, GbTaxWarehouse)
              .set(DeferredMovementPage, false)

          "must redirect to the next page when valid data is submitted" in new Fixture(Some(answersSoFar)) {
            running(application) {

              val expectedSavedAnswers = answersSoFar.set(LocalReferenceNumberPage, testLrn)

              MockPreDraftService.set(expectedSavedAnswers).returns(Future.successful(true))

              val request =
                FakeRequest(POST, localReferenceNumberSubmitAction.url)
                  .withFormUrlEncodedBody(("value", testLrn))

              val result = route(application, request).value

              status(result) mustEqual SEE_OTHER
              redirectLocation(result).value mustEqual testOnwardRoute.url
            }
          }

          "must return a Bad Request and errors when invalid data is submitted" in
            new Fixture(
              userAnswers = Some(
                emptyUserAnswers
                  .set(DestinationTypePage, UnknownDestination)
                  .set(DeferredMovementPage, false)
              )) {

              running(application) {

                val request =
                  FakeRequest(POST, localReferenceNumberSubmitAction.url)
                    .withFormUrlEncodedBody(("value", ""))

                val boundForm = form.bind(Map("value" -> ""))
                val result = route(application, request).value

                status(result) mustEqual BAD_REQUEST
                contentAsString(result) mustEqual view(isDeferred = false, boundForm, localReferenceNumberSubmitAction)(dataRequest(request), messages(application)).toString
              }
            }
        }


        "when the Deferred Movement answer DOES NOT exist in session" - {

          "must return SEE_OTHER and redirect to the Deferred Movement page" in
            new Fixture(
              userAnswers = Some(
                emptyUserAnswers
                  .set(DestinationTypePage, UnknownDestination)
              )) {
            running(application) {

              val request =
                FakeRequest(POST, localReferenceNumberSubmitAction.url)
                  .withFormUrlEncodedBody(("value", testDraftId))

              val result = route(application, request).value

              status(result) mustEqual SEE_OTHER
              redirectLocation(result) mustBe Some(controllers.sections.info.routes.DeferredMovementController.onPreDraftPageLoad(testErn, NormalMode).url)
            }
          }
        }

      }

      "when the Destination Type Page answer does not exist in session" - {

        "must return SEE_OTHER and redirect to the Destination Type page" in new Fixture() {
          running(application) {

            val request =
              FakeRequest(POST, localReferenceNumberSubmitAction.url)
                .withFormUrlEncodedBody(("value", testDraftId))
            val result = route(application, request).value

            status(result) mustEqual SEE_OTHER
            redirectLocation(result) mustBe Some(controllers.sections.info.routes.DestinationTypeController.onPreDraftPageLoad(testErn, NormalMode).url)
          }
        }
      }

      "when the Destination Type Page answer exists in session but is an invalid value" - {

        "must return SEE_OTHER and redirect to the Destination Type page" in new Fixture() {
          running(application) {

            val request =
              FakeRequest(POST, localReferenceNumberRoute)
                .withFormUrlEncodedBody(("value", testDraftId))
            //.withSession(SessionKeys.DESTINATION_TYPE -> "beans")
            val result = route(application, request).value

            status(result) mustEqual SEE_OTHER
            redirectLocation(result) mustBe Some(controllers.sections.info.routes.DestinationTypeController.onPreDraftPageLoad(testErn, NormalMode).url)
          }
        }

      }
    }
  }
}
