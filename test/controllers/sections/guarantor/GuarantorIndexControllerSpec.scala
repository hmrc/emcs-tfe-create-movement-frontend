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
import models.{NormalMode, UserAnswers}
import navigation.FakeNavigators.FakeGuarantorNavigator
import pages.sections.consignor.ConsignorAddressPage
import pages.sections.guarantor.{GuarantorArrangerPage, GuarantorRequiredPage}
import play.api.http.Status.SEE_OTHER
import play.api.test.FakeRequest
import play.api.test.Helpers._

class GuarantorIndexControllerSpec extends SpecBase with MockUserAnswersService {

  class Fixture(val optUserAnswers: Option[UserAnswers] = Some(emptyUserAnswers), ern: String = testErn) {

    val request = FakeRequest(GET, routes.GuarantorIndexController.onPageLoad(ern, testDraftId).url)

    lazy val testController = new GuarantorIndexController(
      mockUserAnswersService,
      new FakeGuarantorNavigator(testOnwardRoute),
      fakeAuthAction,
      new FakeDataRetrievalAction(optUserAnswers, Some(testMinTraderKnownFacts)),
      dataRequiredAction,
      messagesControllerComponents
    )
  }

  "GuarantorIndexController" - {

    "when GuarantorSection.isCompleted" - {

      "must redirect to the CYA controller" in new Fixture(
        Some(emptyUserAnswers
          .set(GuarantorRequiredPage, true)
          .set(GuarantorArrangerPage, Consignor)
          .set(ConsignorAddressPage, testUserAddress))) {

        val result = testController.onPageLoad(testErn, testDraftId)(request)

        status(result) mustEqual SEE_OTHER
        redirectLocation(result) mustBe Some(routes.GuarantorCheckAnswersController.onPageLoad(testErn, testDraftId).url)
      }
    }

    "when GuarantorSection is not complete" - {

      "must redirect to the guarantor required controller" in new Fixture() {

        val result = testController.onPageLoad(testErn, testDraftId)(request)

        status(result) mustEqual SEE_OTHER
        redirectLocation(result) mustBe Some(routes.GuarantorRequiredController.onPageLoad(testErn, testDraftId, NormalMode).url)
      }
    }
  }
}
