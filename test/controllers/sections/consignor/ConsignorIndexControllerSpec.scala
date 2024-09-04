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

package controllers.sections.consignor

import base.SpecBase
import controllers.actions.FakeDataRetrievalAction
import mocks.services.MockUserAnswersService
import models.{NormalMode, UserAnswers}
import navigation.FakeNavigators.FakeConsignorNavigator
import pages.sections.consignor.ConsignorAddressPage
import play.api.http.Status.SEE_OTHER
import play.api.test.FakeRequest
import play.api.test.Helpers._

class ConsignorIndexControllerSpec extends SpecBase with MockUserAnswersService {

  class Fixture(optUserAnswers: Option[UserAnswers] = Some(emptyUserAnswers)) {
    val request = FakeRequest(GET, controllers.sections.consignor.routes.ConsignorIndexController.onPageLoad(testErn, testDraftId).url)

    lazy val testController = new ConsignorIndexController(
      mockUserAnswersService,
      new FakeConsignorNavigator(testOnwardRoute),
      fakeAuthAction,
      new FakeDataRetrievalAction(optUserAnswers, Some(testMinTraderKnownFacts)),
      dataRequiredAction,
      messagesControllerComponents
    )
  }

  "ConsignorIndexController" - {
    "when ConsignorSection.isCompleted is true" - {
      "must redirect to the consignor CYA controller" in new Fixture(Some(emptyUserAnswers.set(ConsignorAddressPage, testUserAddress))) {

        val result = testController.onPageLoad(testErn, testDraftId)(request)

        status(result) mustEqual SEE_OTHER
        redirectLocation(result) mustBe Some(controllers.sections.consignor.routes.CheckYourAnswersConsignorController.onPageLoad(testErn, testDraftId).url)
      }
    }

    "when ConsignorSection.isCompleted is false" - {
      "and logged in as a NorthernIrelandTemporaryCertifiedConsignor" - {
        "must redirect to ConsignorPaidTemporaryAuthorisationCodeController" in
          new Fixture(Some(emptyUserAnswers.copy(ern = testNITemporaryCertifiedConsignorErn))) {
            val result = testController.onPageLoad(testNITemporaryCertifiedConsignorErn, testDraftId)(request)

            status(result) mustEqual SEE_OTHER
            redirectLocation(result) mustBe
              Some(controllers.sections.consignor.routes.ConsignorPaidTemporaryAuthorisationCodeController.onPageLoad(testNITemporaryCertifiedConsignorErn, testDraftId, NormalMode).url)
          }

      }
      "and NOT logged in as a NorthernIrelandTemporaryCertifiedConsignor" - {
        "must redirect to ConsignorAddressController" in
          new Fixture(Some(emptyUserAnswers.copy(ern = testNICertifiedConsignorErn))) {
            val result = testController.onPageLoad(testNICertifiedConsignorErn, testDraftId)(request)

            status(result) mustEqual SEE_OTHER
            redirectLocation(result) mustBe
              Some(controllers.sections.consignor.routes.ConsignorAddressController.onPageLoad(testNICertifiedConsignorErn, testDraftId, NormalMode).url)
          }
      }
    }
  }

}
