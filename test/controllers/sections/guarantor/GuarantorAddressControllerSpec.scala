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

package controllers.sections.guarantor

import base.SpecBase
import controllers.actions.FakeDataRetrievalAction
import controllers.routes
import fixtures.UserAddressFixtures
import forms.AddressFormProvider
import mocks.services.MockUserAnswersService
import models.sections.guarantor.GuarantorArranger.{GoodsOwner, Transporter}
import models.{NormalMode, UserAnswers}
import navigation.FakeNavigators.FakeGuarantorNavigator
import pages.sections.guarantor.{GuarantorAddressPage, GuarantorArrangerPage, GuarantorRequiredPage}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.AddressView

import scala.concurrent.Future

class GuarantorAddressControllerSpec extends SpecBase with MockUserAnswersService with UserAddressFixtures {

  class Fixture(optUserAnswers: Option[UserAnswers] = Some(emptyUserAnswers)) {

    val formProvider = new AddressFormProvider()
    val form = formProvider()

    lazy val addressRoute = controllers.sections.guarantor.routes.GuarantorAddressController.onPageLoad(testErn, testDraftId, NormalMode).url
    lazy val addressOnSubmit = controllers.sections.guarantor.routes.GuarantorAddressController.onSubmit(testErn, testDraftId, NormalMode)

    val view = app.injector.instanceOf[AddressView]

    val request = FakeRequest(GET, addressRoute)

    object TestController extends GuarantorAddressController(
      messagesApi,
      mockUserAnswersService,
      new FakeGuarantorNavigator(testOnwardRoute),
      fakeAuthAction,
      new FakeDataRetrievalAction(optUserAnswers, Some(testMinTraderKnownFacts)),
      dataRequiredAction,
      fakeUserAllowListAction,
      new AddressFormProvider(),
      messagesControllerComponents,
      view
    )

  }

  Seq(GoodsOwner, Transporter).foreach(
    guarantorArranger => {

      val answersSoFar = emptyUserAnswers
        .set(GuarantorRequiredPage, true)
        .set(GuarantorArrangerPage, guarantorArranger)

      s"with a Guarantor Arranger of ${guarantorArranger.getClass.getSimpleName.stripSuffix("$")}" - {
        "must return OK and the correct view for a GET" in new Fixture(Some(answersSoFar)) {
          val result = TestController.onPageLoad(testErn, testDraftId, NormalMode)(request)

          status(result) mustEqual OK
          contentAsString(result) mustEqual view(
            form = form,
            addressPage = GuarantorAddressPage,
            call = addressOnSubmit,
            headingKey = Some(s"guarantorAddress.$guarantorArranger")
          )(dataRequest(request), messages(request)).toString
        }

        "must redirect to the next page when valid data is submitted" in new Fixture(Some(answersSoFar)) {
          MockUserAnswersService.set().returns(Future.successful(answersSoFar))

          val req = FakeRequest(POST, addressRoute).withFormUrlEncodedBody(
            ("property", userAddressModelMax.property.value),
            ("street", userAddressModelMax.street),
            ("town", userAddressModelMax.town),
            ("postcode", userAddressModelMax.postcode)
          )

          val result = TestController.onSubmit(testErn, testDraftId, NormalMode)(req)

          status(result) mustEqual SEE_OTHER
          redirectLocation(result).value mustEqual testOnwardRoute.url
        }

        "must return a Bad Request and errors when invalid data is submitted" in new Fixture(Some(answersSoFar)) {
          val req = FakeRequest(POST, addressRoute).withFormUrlEncodedBody(("value", ""))
          val boundForm = form.bind(Map("value" -> ""))

          val result = TestController.onSubmit(testErn, testDraftId, NormalMode)(req)

          status(result) mustEqual BAD_REQUEST
          contentAsString(result) mustEqual view(
            form = boundForm,
            addressPage = GuarantorAddressPage,
            call = addressOnSubmit,
            headingKey = Some(s"guarantorAddress.$guarantorArranger")
          )(dataRequest(request), messages(request)).toString
        }
      }
    }
  )

  "must redirect to the guarantor index if user hasn't answered that yet" in
    new Fixture(Some(emptyUserAnswers.set(GuarantorRequiredPage, true))) {
      val result = TestController.onPageLoad(testErn, testDraftId, NormalMode)(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.sections.guarantor.routes.GuarantorIndexController.onPageLoad(testErn, testDraftId).url
    }

  "must redirect to Journey Recovery for a GET if no existing data is found" in new Fixture(None) {
    val result = TestController.onPageLoad(testErn, testDraftId, NormalMode)(request)

    status(result) mustEqual SEE_OTHER
    redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad().url
  }

  "must redirect to Journey Recovery for a POST if no existing data is found" in new Fixture(None) {
    val req = FakeRequest(POST, addressRoute).withFormUrlEncodedBody(("value", "answer"))
    val result = TestController.onSubmit(testErn, testDraftId, NormalMode)(req)

    status(result) mustEqual SEE_OTHER
    redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad().url
  }

}
