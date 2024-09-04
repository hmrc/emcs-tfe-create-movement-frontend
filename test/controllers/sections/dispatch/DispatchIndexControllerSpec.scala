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

package controllers.sections.dispatch

import base.SpecBase
import controllers.actions.FakeDataRetrievalAction
import fixtures.MovementSubmissionFailureFixtures
import mocks.services.MockUserAnswersService
import models.sections.info.movementScenario.MovementScenario.{CertifiedConsignee, ExemptedOrganisation, UkTaxWarehouse, TemporaryCertifiedConsignee}
import models.{NormalMode, UserAnswers}
import navigation.FakeNavigators.FakeDispatchNavigator
import pages.sections.consignor.ConsignorAddressPage
import pages.sections.dispatch.{DispatchAddressPage, DispatchUseConsignorDetailsPage, DispatchWarehouseExcisePage}
import pages.sections.info.DestinationTypePage
import play.api.http.Status.SEE_OTHER
import play.api.mvc.Result
import play.api.test.FakeRequest
import play.api.test.Helpers._

import scala.concurrent.Future

class DispatchIndexControllerSpec extends SpecBase with MockUserAnswersService with MovementSubmissionFailureFixtures {

  class Fixture(optUserAnswers: Option[UserAnswers] = Some(emptyUserAnswers)) {
    val request = FakeRequest(GET, controllers.sections.dispatch.routes.DispatchIndexController.onPageLoad(testErn, testDraftId).url)

    lazy val testController = new DispatchIndexController(
      userAnswersService = mockUserAnswersService,
      navigator = new FakeDispatchNavigator(testOnwardRoute),
      auth = fakeAuthAction,
      getData = new FakeDataRetrievalAction(optUserAnswers, Some(testMinTraderKnownFacts)),
      requireData = dataRequiredAction,
      controllerComponents = messagesControllerComponents
    )
  }

  "DispatchIndexController" - {

    "when DispatchSection.status is UpdateNeeded" -{

      "must redirect to DispatchCheckAnswersController" in new Fixture(Some(emptyUserAnswers
        .set(DispatchWarehouseExcisePage, "beans")
        .set(DispatchUseConsignorDetailsPage, true)
        .set(DispatchAddressPage, testUserAddress)
        .copy(submissionFailures = Seq(dispatchWarehouseInvalidOrMissingOnSeedError))
      )) {

        val result: Future[Result] = testController.onPageLoad(testErn, testDraftId)(request)

        status(result) mustBe SEE_OTHER
        redirectLocation(result) mustBe
          Some(controllers.sections.dispatch.routes.DispatchCheckAnswersController.onPageLoad(testErn, testDraftId).url)
      }
    }

    "when DispatchSection.isCompleted" - {

      "must redirect to the CYA controller" in new Fixture(Some(emptyUserAnswers
        .set(DispatchWarehouseExcisePage, "beans")
        .set(DispatchUseConsignorDetailsPage, true)
        .set(DispatchAddressPage, testUserAddress)
      )) {

        val result = testController.onPageLoad(testErn, testDraftId)(request)

        status(result) mustEqual SEE_OTHER
        redirectLocation(result) mustBe Some(controllers.sections.dispatch.routes.DispatchCheckAnswersController.onPageLoad(testErn, testDraftId).url)
      }
    }

    "when DispatchSetion is NOT complete" - {

      Seq(CertifiedConsignee, TemporaryCertifiedConsignee).foreach { destinationType =>

        "when ConsignorAddress exists" - {

          s"when $destinationType must redirect to the DispatchUseConsignorDetailsController" in new Fixture(Some(emptyUserAnswers
            .set(DestinationTypePage, destinationType)
            .set(ConsignorAddressPage, testUserAddress)
          )) {

            val result = testController.onPageLoad(testErn, testDraftId)(request)

            status(result) mustEqual SEE_OTHER
            redirectLocation(result) mustBe
              Some(controllers.sections.dispatch.routes.DispatchUseConsignorDetailsController.onPageLoad(testErn, testDraftId, NormalMode).url)
          }
        }

        "when ConsignorAddress DOES NOT exist" - {

          s"when $destinationType must redirect to the DispatchAddressController" in new Fixture(Some(emptyUserAnswers
            .set(DestinationTypePage, destinationType)
          )) {

            val result = testController.onPageLoad(testErn, testDraftId)(request)

            status(result) mustEqual SEE_OTHER
            redirectLocation(result) mustBe
              Some(controllers.sections.dispatch.routes.DispatchAddressController.onPageLoad(testErn, testDraftId, NormalMode).url)
          }
        }
      }

      Seq(UkTaxWarehouse.GB, UkTaxWarehouse.NI, ExemptedOrganisation).foreach { destinationType =>

        s"when $destinationType must redirect to the DispatchWarehouseExciseController" in new Fixture(Some(emptyUserAnswers
          .set(DestinationTypePage, destinationType)
        )) {

          val result = testController.onPageLoad(testErn, testDraftId)(request)

          status(result) mustEqual SEE_OTHER
          redirectLocation(result) mustBe
            Some(controllers.sections.dispatch.routes.DispatchWarehouseExciseController.onPageLoad(testErn, testDraftId, NormalMode).url)
        }
      }
    }
  }
}
