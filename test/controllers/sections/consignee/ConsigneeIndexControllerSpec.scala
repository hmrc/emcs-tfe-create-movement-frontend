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
import mocks.services.MockUserAnswersService
import models.sections.info.movementScenario.MovementScenario._
import models.{ExemptOrganisationDetailsModel, NormalMode, UserAddress, UserAnswers}
import navigation.FakeNavigators.FakeConsigneeNavigator
import pages.sections.consignee._
import pages.sections.info.DestinationTypePage
import play.api.test.FakeRequest
import play.api.test.Helpers._

class ConsigneeIndexControllerSpec extends SpecBase with MockUserAnswersService {

  class Fixture(optUserAnswers: Option[UserAnswers] = Some(emptyUserAnswers)) {

    val route = controllers.sections.consignee.routes.ConsigneeExemptOrganisationController.onPageLoad(testErn, testLrn, NormalMode).url
    val request = userRequest(FakeRequest(GET, route)).copy(ern = testErn)

    object TestController extends ConsigneeIndexController(
      messagesApi,
      fakeAuthAction,
      fakeUserAllowListAction,
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
          .set(ConsigneeBusinessNamePage, "")
          .set(ConsigneeAddressPage, UserAddress(None, "", "", ""))
        )) {

        val result = TestController.onPageLoad(testErn, testDraftId)(request)

        status(result) mustBe SEE_OTHER
        redirectLocation(result) mustBe
          Some(controllers.sections.consignee.routes.CheckYourAnswersConsigneeController.onPageLoad(testErn, testDraftId).url)
      }
    }
    "must redirect to ConsigneeExemptOrganisationController" - {
      s"when destination is $ExemptedOrganisation" in new Fixture(
        Some(emptyUserAnswers.set(DestinationTypePage, ExemptedOrganisation))) {

        val result = TestController.onPageLoad(testErn, testDraftId)(request)

        status(result) mustBe SEE_OTHER
        redirectLocation(result) mustBe
          Some(controllers.sections.consignee.routes.ConsigneeExemptOrganisationController.onPageLoad(testErn, testDraftId, NormalMode).url)
      }
    }
    "must redirect to ConsigneeExciseController" - {
      Seq(GbTaxWarehouse, EuTaxWarehouse, DirectDelivery).foreach(
        movementScenario =>
          s"when destination is $movementScenario" in new Fixture(
            Some(emptyUserAnswers.set(DestinationTypePage, movementScenario))) {

            val result = TestController.onPageLoad(testErn, testDraftId)(request)

            status(result) mustBe SEE_OTHER
            redirectLocation(result) mustBe
              Some(controllers.sections.consignee.routes.ConsigneeExciseController.onPageLoad(testErn, testDraftId, NormalMode).url)
          }
      )

      "when UserType is GBRC" - {
        val ern: String = "GBRC123"

        Seq(ExportWithCustomsDeclarationLodgedInTheUk).foreach(
          movementScenario =>
            s"and destination is $movementScenario" in new Fixture(
              Some(emptyUserAnswers.set(DestinationTypePage, movementScenario))) {

              val req = userRequest(FakeRequest(GET, route)).copy(ern = ern)
              val result = TestController.onPageLoad(ern, testDraftId)(req)

              status(result) mustBe SEE_OTHER
              redirectLocation(result) mustBe
                Some(controllers.sections.consignee.routes.ConsigneeExciseController.onPageLoad(ern, testDraftId, NormalMode).url)
            }
        )
      }
      "when UserType is XIRC" - {
        val ern: String = "XIRC123"

        Seq(
          RegisteredConsignee,
          TemporaryRegisteredConsignee,
          ExportWithCustomsDeclarationLodgedInTheUk,
          ExportWithCustomsDeclarationLodgedInTheEu
        ).foreach(
          movementScenario =>
            s"and destination is $movementScenario" in new Fixture(
              Some(emptyUserAnswers.set(DestinationTypePage, movementScenario))) {

              val req = userRequest(FakeRequest(GET, route)).copy(ern = ern)
              val result = TestController.onPageLoad(ern, testDraftId)(req)

              status(result) mustBe SEE_OTHER
              redirectLocation(result) mustBe
                Some(controllers.sections.consignee.routes.ConsigneeExciseController.onPageLoad(ern, testDraftId, NormalMode).url)
            }
        )
      }

      "when UserType is XIWK" - {
        val ern: String = "XIWK123"
        s"and destination is $TemporaryRegisteredConsignee" in new Fixture(
          Some(emptyUserAnswers.set(DestinationTypePage, TemporaryRegisteredConsignee))) {

          val req = userRequest(FakeRequest(GET, route)).copy(ern = ern)
          val result = TestController.onPageLoad(ern, testDraftId)(req)

          status(result) mustBe SEE_OTHER
          redirectLocation(result) mustBe
            Some(controllers.sections.consignee.routes.ConsigneeExciseController.onPageLoad(ern, testDraftId, NormalMode).url)
        }
      }
    }

    "must redirect to ConsigneeExportController" - {
      "when UserType is GBWK" - {
        val ern = "GBWK123"

        Seq(ExportWithCustomsDeclarationLodgedInTheUk).foreach(
          movementScenario =>
            s"and destination is $movementScenario" in new Fixture(
              Some(emptyUserAnswers.set(DestinationTypePage, movementScenario))) {

              val req = userRequest(FakeRequest(GET, route)).copy(ern = ern)
              val result = TestController.onPageLoad(ern, testDraftId)(req)

              status(result) mustBe SEE_OTHER
              redirectLocation(result) mustBe
                Some(controllers.sections.consignee.routes.ConsigneeExportController.onPageLoad(ern, testDraftId, NormalMode).url)
            }
        )
      }

      "when UserType is XIWK" - {
        val ern = "XIWK123"

        Seq(
          RegisteredConsignee,
          ExportWithCustomsDeclarationLodgedInTheUk,
          ExportWithCustomsDeclarationLodgedInTheEu
        ).foreach(
          movementScenario =>
            s"and destination is $movementScenario" in new Fixture(Some(emptyUserAnswers.set(DestinationTypePage, movementScenario))) {

              val req = userRequest(FakeRequest(GET, route)).copy(ern = ern)
              val result = TestController.onPageLoad(ern, testDraftId)(req)

              status(result) mustBe SEE_OTHER
              redirectLocation(result) mustBe
                Some(controllers.sections.consignee.routes.ConsigneeExportController.onPageLoad(ern, testDraftId, NormalMode).url)
            }
        )
      }
    }

    "must redirect to the tasklist" - {
      "when user isn't any of the above (they shouldn't be able to access the NEE pages)" in new Fixture(
        Some(emptyUserAnswers.set(DestinationTypePage, UnknownDestination))) {

        val req = userRequest(FakeRequest(GET, route)).copy(ern = testErn)
        val result = TestController.onPageLoad(testErn, testDraftId)(req)

        status(result) mustBe SEE_OTHER
        redirectLocation(result) mustBe
          Some(controllers.routes.DraftMovementController.onPageLoad(testErn, testDraftId).url)
      }
    }
  }
}
