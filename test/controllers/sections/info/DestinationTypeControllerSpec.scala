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
import config.SessionKeys.DISPATCH_PLACE
import forms.sections.info.DestinationTypeFormProvider
import mocks.services.MockUserAnswersService
import models.DispatchPlace
import models.DispatchPlace.{GreatBritain, NorthernIreland}
import models.requests.UserRequest
import models.sections.info.movementScenario.MovementScenario
import play.api.Application
import play.api.data.Form
import play.api.inject.bind
import play.api.mvc.{AnyContentAsEmpty, AnyContentAsFormUrlEncoded}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.UserAnswersService
import views.html.sections.info.DestinationTypeView

class DestinationTypeControllerSpec extends SpecBase with MockUserAnswersService {

  class Fixture(dispatchPlace: Option[DispatchPlace], ern: String = testGreatBritainErn, value: String = "unknownDestination") {

    lazy val application: Application =
      applicationBuilder(userAnswers = None)
        .overrides(
          bind[UserAnswersService].toInstance(mockUserAnswersService)
        )
        .build()

    lazy val view: DestinationTypeView = application.injector.instanceOf[DestinationTypeView]

    lazy val destinationTypeGetRoute: String = controllers.sections.info.routes.DestinationTypeController.onPageLoad(ern).url
    lazy val destinationTypePostRoute: String = controllers.sections.info.routes.DestinationTypeController.onSubmit(ern).url

    implicit lazy val fakeGetRequest: FakeRequest[AnyContentAsEmpty.type] = dispatchPlace match {
      case Some(dp) => FakeRequest("GET", destinationTypeGetRoute).withSession(DISPATCH_PLACE -> dp.toString)
      case None => FakeRequest("GET", destinationTypeGetRoute)
    }
    implicit lazy val getRequest: UserRequest[_] = userRequest(fakeGetRequest).copy(ern = ern)

    implicit lazy val fakePostRequest: FakeRequest[AnyContentAsFormUrlEncoded] = dispatchPlace match {
      case Some(dp) => FakeRequest(POST, destinationTypePostRoute)
        .withFormUrlEncodedBody(("value", value))
        .withSession(DISPATCH_PLACE -> dp.toString)
      case None => FakeRequest(POST, destinationTypePostRoute)
        .withFormUrlEncodedBody(("value", value))
    }
    implicit lazy val postRequest: UserRequest[_] = userRequest(fakePostRequest).copy(ern = ern)

    lazy val formProvider: DestinationTypeFormProvider = new DestinationTypeFormProvider()
    lazy val form: Form[MovementScenario] = formProvider()(getRequest)


    lazy val getResult = route(application, fakeGetRequest).value
    lazy val postResult = route(application, fakePostRequest).value
  }

  "DestinationTypeController" - {
    "onPageLoad" - {
      "must render the view" - {
        "when the request contains a GBRC ERN" in new Fixture(None, ern = "GBRC123") {
          running(application) {
            status(getResult) mustEqual OK
            contentAsString(getResult) mustEqual view(GreatBritain, form, controllers.sections.info.routes.DestinationTypeController.onSubmit("GBRC123"))(getRequest, messages(application)).toString
          }
        }
        "when the request contains a GBWK ERN" in new Fixture(None, ern = "GBWK123") {
          running(application) {
            status(getResult) mustEqual OK
            contentAsString(getResult) mustEqual view(GreatBritain, form, controllers.sections.info.routes.DestinationTypeController.onSubmit("GBWK123"))(getRequest, messages(application)).toString
          }
        }
        "when the request contains a XIRC ERN" in new Fixture(None, ern = "XIRC123") {
          running(application) {
            status(getResult) mustEqual OK
            contentAsString(getResult) mustEqual view(GreatBritain, form, controllers.sections.info.routes.DestinationTypeController.onSubmit("XIRC123"))(getRequest, messages(application)).toString
          }
        }
        "when the request contains a XIWK ERN and dispatchPlace is GreatBritain" in new Fixture(Some(GreatBritain), ern = "XIWK123") {
          running(application) {
            status(getResult) mustEqual OK
            contentAsString(getResult) mustEqual view(GreatBritain, form, controllers.sections.info.routes.DestinationTypeController.onSubmit("XIWK123"))(getRequest, messages(application)).toString
          }
        }
        "when the request contains a XIWK ERN and dispatchPlace is NorthernIreland" in new Fixture(Some(NorthernIreland), ern = "XIWK123") {
          running(application) {
            status(getResult) mustEqual OK
            contentAsString(getResult) mustEqual view(NorthernIreland, form, controllers.sections.info.routes.DestinationTypeController.onSubmit("XIWK123"))(getRequest, messages(application)).toString
          }
        }
      }
      "must redirect to the DispatchPlace page" - {
        "when the request contains a XI ERN and dispatchPlace is not known" in new Fixture(None, ern = "XIWK123") {
          running(application) {
            status(getResult) mustEqual SEE_OTHER
            redirectLocation(getResult).value mustEqual controllers.sections.info.routes.DispatchPlaceController.onPageLoad("XIWK123").url
          }
        }
      }
    }

    "onSubmit" - {
      "must redirect to the next page when valid data is submitted" in new Fixture(None, value = "exportWithCustomsDeclarationLodgedInTheUk") {
        running(application) {
          status(postResult) mustEqual SEE_OTHER
          redirectLocation(postResult).value mustEqual controllers.sections.info.routes.DeferredMovementController.onPageLoad(testGreatBritainErn).url
        }
      }

      "must return a Bad Request and errors when invalid data is submitted" in new Fixture(None, value = "") {
        running(application) {
          val boundForm = form.bind(Map("value" -> ""))

          status(postResult) mustEqual BAD_REQUEST
          contentAsString(postResult) mustEqual view(GreatBritain, boundForm, controllers.sections.info.routes.DestinationTypeController.onSubmit(testGreatBritainErn))(postRequest, messages(application)).toString
        }
      }
    }
  }
}
