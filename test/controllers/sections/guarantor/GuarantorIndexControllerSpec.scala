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
import mocks.services.MockUserAnswersService
import models.sections.guarantor.GuarantorArranger.Consignor
import models.sections.info.movementScenario.MovementScenario.DirectDelivery
import models.sections.journeyType.HowMovementTransported.{FixedTransportInstallations, RoadTransport}
import models.{NormalMode, UserAddress, UserAnswers}
import navigation.FakeNavigators.FakeGuarantorNavigator
import pages.sections.consignor.ConsignorAddressPage
import pages.sections.guarantor.{GuarantorArrangerPage, GuarantorRequiredPage}
import pages.sections.info.DestinationTypePage
import pages.sections.journeyType.HowMovementTransportedPage
import play.api.http.Status.SEE_OTHER
import play.api.test.FakeRequest
import play.api.test.Helpers._

import scala.concurrent.Future

class GuarantorIndexControllerSpec extends SpecBase with MockUserAnswersService {

  class Fixture(val optUserAnswers: Option[UserAnswers] = Some(emptyUserAnswers), ern: String = testErn) {

    val request = FakeRequest(GET, routes.GuarantorIndexController.onPageLoad(ern, testDraftId).url)

    lazy val testController = new GuarantorIndexController(
      mockUserAnswersService,
      new FakeGuarantorNavigator(testOnwardRoute),
      fakeAuthAction,
      new FakeDataRetrievalAction(optUserAnswers, Some(testMinTraderKnownFacts)),
      dataRequiredAction,
      fakeUserAllowListAction,
      messagesControllerComponents
    )

  }


  "GuarantorIndexController" - {
    "when GuarantorSection.isCompleted" - {
      "must redirect to the CYA controller" in new Fixture(
        Some(emptyUserAnswers
          .set(GuarantorRequiredPage, true)
          .set(GuarantorArrangerPage, Consignor)
          .set(ConsignorAddressPage, UserAddress(None, "", "", "")))) {

        val result = testController.onPageLoad(testErn, testDraftId)(request)

        status(result) mustEqual SEE_OTHER
        redirectLocation(result) mustBe Some(routes.GuarantorCheckAnswersController.onPageLoad(testErn, testDraftId).url)
      }
    }

    "when GuarantorSection is not complete" - {
      "when the movement is UKtoEU" - {
        "when the Journey Type has not been answered" - {
          "must redirect to the guarantor required controller" in new Fixture(
            Some(emptyUserAnswers.set(DestinationTypePage, DirectDelivery)),
            testNorthernIrelandErn
          ) {

            val result = testController.onPageLoad(testNorthernIrelandErn, testDraftId)(request)

            status(result) mustEqual SEE_OTHER
            redirectLocation(result) mustBe Some(routes.GuarantorRequiredController.onPageLoad(testNorthernIrelandErn, testDraftId, NormalMode).url)
          }
        }

        "when the Journey Type has been answered as FixedTransportInstallations" - {
          "must redirect to the guarantor required controller" in new Fixture(
            Some(emptyUserAnswers
              .set(DestinationTypePage, DirectDelivery)
              .set(HowMovementTransportedPage, FixedTransportInstallations)
            ),
            testNorthernIrelandErn
          ) {

            val result = testController.onPageLoad(testNorthernIrelandErn, testDraftId)(request)

            status(result) mustEqual SEE_OTHER
            redirectLocation(result) mustBe Some(routes.GuarantorRequiredController.onPageLoad(testNorthernIrelandErn, testDraftId, NormalMode).url)
          }
        }

        "when the Journey Type has been answered as anything other than FixedTransportInstallations" - {
          "must save the GurantorRequired page as True and redirect to the onward route of that page" in new Fixture(
            Some(emptyUserAnswers
              .set(DestinationTypePage, DirectDelivery)
              .set(HowMovementTransportedPage, RoadTransport)
            ),
            testNorthernIrelandErn
          ) {

            val updatedAnswers = optUserAnswers.get.set(GuarantorRequiredPage, true)

            MockUserAnswersService.set(updatedAnswers).returns(Future.successful(updatedAnswers))

            val result = testController.onPageLoad(testNorthernIrelandErn, testDraftId)(request)

            status(result) mustEqual SEE_OTHER
            redirectLocation(result) mustBe Some(testOnwardRoute.url)
          }
        }
      }
      "when the movement is NOT UKtoEU" - {
        "must redirect to the guarantor required controller" in new Fixture() {

          val result = testController.onPageLoad(testErn, testDraftId)(request)

          status(result) mustEqual SEE_OTHER
          redirectLocation(result) mustBe Some(routes.GuarantorRequiredController.onPageLoad(testErn, testDraftId, NormalMode).url)
        }
      }
    }
  }
}
