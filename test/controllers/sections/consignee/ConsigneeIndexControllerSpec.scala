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

package controllers.sections.consignee

import base.SpecBase
import controllers.actions.FakeDataRetrievalAction
import fixtures.MovementSubmissionFailureFixtures
import mocks.services.MockUserAnswersService
import models.sections.info.movementScenario.MovementScenario._
import models.{ExemptOrganisationDetailsModel, NormalMode, UserAnswers}
import navigation.FakeNavigators.FakeConsigneeNavigator
import pages.sections.consignee._
import pages.sections.info.DestinationTypePage
import play.api.mvc.{AnyContentAsEmpty, Result}
import play.api.test.FakeRequest
import play.api.test.Helpers._

import scala.concurrent.Future

class ConsigneeIndexControllerSpec extends SpecBase with MockUserAnswersService with MovementSubmissionFailureFixtures {

  class Fixture(optUserAnswers: Option[UserAnswers] = Some(emptyUserAnswers)) {

    val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest()

    lazy val testController = new ConsigneeIndexController(
      messagesApi,
      fakeAuthAction,
      new FakeDataRetrievalAction(optUserAnswers, Some(testMinTraderKnownFacts)),
      dataRequiredAction,
      new FakeConsigneeNavigator(testOnwardRoute),
      mockUserAnswersService,
      messagesControllerComponents
    )
  }

  "ConsigneeIndexController" - {
    "must redirect to CheckYourAnswersConsigneeController" - {
      "when ConsigneeSection.isCompleted is true" in new Fixture(
        Some(emptyUserAnswers
          .set(DestinationTypePage, ExemptedOrganisation)
          .set(ConsigneeExemptOrganisationPage, ExemptOrganisationDetailsModel("", ""))
          .set(ConsigneeAddressPage, testUserAddress)
        )) {

        val result: Future[Result] = testController.onPageLoad(testErn, testDraftId)(request)

        status(result) mustBe SEE_OTHER
        redirectLocation(result) mustBe
          Some(controllers.sections.consignee.routes.CheckYourAnswersConsigneeController.onPageLoad(testErn, testDraftId).url)
      }
    }
    "must redirect to CheckYourAnswersConsigneeController" - {
      "when ConsigneeSection status is UpdateNeeded" in new Fixture(
        Some(emptyUserAnswers
          .set(DestinationTypePage, ExemptedOrganisation)
          .set(ConsigneeExemptOrganisationPage, ExemptOrganisationDetailsModel("", ""))
          .set(ConsigneeExcisePage, "AA12345678901")
          .set(ConsigneeAddressPage, testUserAddress)
          .copy(submissionFailures = Seq(consigneeExciseFailure))
        )) {

        val result: Future[Result] = testController.onPageLoad(testErn, testDraftId)(request)

        status(result) mustBe SEE_OTHER
        redirectLocation(result) mustBe
          Some(controllers.sections.consignee.routes.CheckYourAnswersConsigneeController.onPageLoad(testErn, testDraftId).url)
      }
    }
    "must redirect to ConsigneeExemptOrganisationController" - {
      s"when destination is $ExemptedOrganisation" in new Fixture(
        Some(emptyUserAnswers.set(DestinationTypePage, ExemptedOrganisation))) {

        val result: Future[Result] = testController.onPageLoad(testErn, testDraftId)(request)

        status(result) mustBe SEE_OTHER
        redirectLocation(result) mustBe
          Some(controllers.sections.consignee.routes.ConsigneeExemptOrganisationController.onPageLoad(testErn, testDraftId, NormalMode).url)
      }
    }
    "must redirect to ConsigneeExciseController" - {
      Seq(
        UkTaxWarehouse.GB,
        UkTaxWarehouse.NI,
        EuTaxWarehouse,
        DirectDelivery,
        RegisteredConsignee,
        TemporaryRegisteredConsignee,
        TemporaryCertifiedConsignee,
        CertifiedConsignee
      ).foreach(
        movementScenario =>
          s"when destination is $movementScenario" in new Fixture(
            Some(emptyUserAnswers.set(DestinationTypePage, movementScenario))) {

            val result: Future[Result] = testController.onPageLoad(testErn, testDraftId)(request)

            status(result) mustBe SEE_OTHER
            redirectLocation(result) mustBe
              Some(controllers.sections.consignee.routes.ConsigneeExciseController.onPageLoad(testErn, testDraftId, NormalMode).url)
          }
      )
    }

    "must redirect to ConsigneeExportInformationController" - {
      Seq(
        ExportWithCustomsDeclarationLodgedInTheUk,
        ExportWithCustomsDeclarationLodgedInTheEu
      ).foreach(
        movementScenario =>
          s"when destination is $movementScenario" in new Fixture(Some(emptyUserAnswers.set(DestinationTypePage, movementScenario))) {

            val result: Future[Result] = testController.onPageLoad(testErn, testDraftId)(request)

            status(result) mustBe SEE_OTHER
            redirectLocation(result) mustBe
              Some(controllers.sections.consignee.routes.ConsigneeExportInformationController.onPageLoad(testErn, testDraftId, NormalMode).url)
          }
      )
    }

    "must redirect to the tasklist" - {
      "when user isn't any of the above (they shouldn't be able to access the NEE pages)" in new Fixture(
        Some(emptyUserAnswers.set(DestinationTypePage, UnknownDestination))) {

        val result: Future[Result] = testController.onPageLoad(testErn, testDraftId)(request)

        status(result) mustBe SEE_OTHER
        redirectLocation(result) mustBe
          Some(controllers.routes.DraftMovementController.onPageLoad(testErn, testDraftId).url)
      }
    }
  }
}
