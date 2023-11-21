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

package controllers.sections.items

import base.SpecBase
import fixtures.ItemFixtures
import mocks.services.{MockGetCnCodeInformationService, MockUserAnswersService}
import models.UserAnswers
import models.requests.{CnCodeInformationItem, DataRequest}
import models.response.referenceData.CnCodeInformation
import navigation.BaseNavigator
import navigation.FakeNavigators.FakeNavigator
import pages.sections.items.{ItemCommodityCodePage, ItemExciseProductCodePage}
import play.api.mvc.{MessagesControllerComponents, Result, Results}
import play.api.test.Helpers._
import play.api.test.{FakeRequest, Helpers}
import services.{GetCnCodeInformationService, UserAnswersService}

import scala.concurrent.Future

class BaseItemsNavigationControllerSpec extends SpecBase
  with MockGetCnCodeInformationService
  with MockUserAnswersService
  with ItemFixtures {

  class Test(val userAnswers: UserAnswers) {
    implicit val request: DataRequest[_] = dataRequest(FakeRequest(), userAnswers)

    val controller: BaseItemsNavigationController = new BaseItemsNavigationController {
      override val cnCodeInformationService: GetCnCodeInformationService = mockGetCnCodeInformationService
      override val userAnswersService: UserAnswersService = mockUserAnswersService
      override val navigator: BaseNavigator = new FakeNavigator(testOnwardRoute)

      override protected def controllerComponents: MessagesControllerComponents = Helpers.stubMessagesControllerComponents()
    }

    val successFunction: CnCodeInformation => Result = _ => Results.Ok
  }

  "withCnCodeInformation" - {
    "must redirect to the index controller" - {
      "when EPC is missing" in new Test(emptyUserAnswers.set(ItemCommodityCodePage(testIndex1), testCnCodeTobacco)) {
        val result: Future[Result] = controller.withCnCodeInformation(testIndex1)(successFunction)

        status(result) mustBe SEE_OTHER
        redirectLocation(result).value mustBe routes.ItemsIndexController.onPageLoad(testErn, testDraftId).url
      }
      "when CN Code is missing" in new Test(emptyUserAnswers.set(ItemExciseProductCodePage(testIndex1), testEpcTobacco)) {
        val result: Future[Result] = controller.withCnCodeInformation(testIndex1)(successFunction)

        status(result) mustBe SEE_OTHER
        redirectLocation(result).value mustBe routes.ItemsIndexController.onPageLoad(testErn, testDraftId).url
      }
    }
    "must redirect to JourneyRecovery" - {
      "when both EPC and CN Code are in userAnswers and service returns an empty list" in new Test(
        emptyUserAnswers
          .set(ItemExciseProductCodePage(testIndex1), testEpcTobacco)
          .set(ItemCommodityCodePage(testIndex1), testCnCodeTobacco)
      ) {
        MockGetCnCodeInformationService
          .getCnCodeInformationWithMovementItems(Seq(CnCodeInformationItem(testEpcTobacco, testCnCodeTobacco)))
          .returns(Future.successful(Nil))

        val result: Future[Result] = controller.withCnCodeInformation(testIndex1)(successFunction)

        status(result) mustBe SEE_OTHER
        redirectLocation(result).value mustBe controllers.routes.JourneyRecoveryController.onPageLoad().url
      }
    }
    "must load the success function" - {
      "when both EPC and CN Code are in userAnswers and service returns a list with one item" in new Test(
        emptyUserAnswers
          .set(ItemExciseProductCodePage(testIndex1), testEpcTobacco)
          .set(ItemCommodityCodePage(testIndex1), testCnCodeTobacco)
      ) {
        val item: CnCodeInformationItem = CnCodeInformationItem(testEpcTobacco, testCnCodeTobacco)

        MockGetCnCodeInformationService
          .getCnCodeInformationWithMovementItems(Seq(CnCodeInformationItem(testEpcTobacco, testCnCodeTobacco)))
          .returns(Future.successful(Seq(item -> testCommodityCodeTobacco)))

        val result: Future[Result] = controller.withCnCodeInformation(testIndex1)(successFunction)

        status(result) mustBe OK
      }
      "when both EPC and CN Code are in userAnswers and service returns a list with multiple items" in new Test(
        emptyUserAnswers
          .set(ItemExciseProductCodePage(testIndex1), testEpcTobacco)
          .set(ItemCommodityCodePage(testIndex1), testCnCodeTobacco)
      ) {
        val item: CnCodeInformationItem = CnCodeInformationItem(testEpcTobacco, testCnCodeTobacco)

        MockGetCnCodeInformationService
          .getCnCodeInformationWithMovementItems(Seq(CnCodeInformationItem(testEpcTobacco, testCnCodeTobacco)))
          .returns(Future.successful(Seq(
            item -> testCommodityCodeTobacco,
            item -> testCommodityCodeWine
          )))

        val result: Future[Result] = controller.withCnCodeInformation(testIndex1)(successFunction)

        status(result) mustBe OK
      }
    }
  }
}
