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
import fixtures.{ItemFixtures, MovementSubmissionFailureFixtures}
import mocks.services.{MockGetCnCodeInformationService, MockUserAnswersService}
import models.{Index, UserAnswers}
import models.requests.DataRequest
import models.response.referenceData.{BulkPackagingType, CnCodeInformation, ItemPackaging}
import models.sections.items.ItemBulkPackagingCode.BulkSolidPowders
import navigation.BaseNavigator
import navigation.FakeNavigators.FakeNavigator
import pages.sections.items._
import play.api.mvc.{MessagesControllerComponents, Result, Results}
import play.api.test.Helpers._
import play.api.test.{FakeRequest, Helpers}
import services.UserAnswersService

import scala.concurrent.Future

class BaseItemsNavigationControllerSpec extends SpecBase
  with MockGetCnCodeInformationService
  with MockUserAnswersService
  with ItemFixtures
  with MovementSubmissionFailureFixtures {

  class Test(val userAnswers: UserAnswers) {
    implicit val request: DataRequest[_] = dataRequest(FakeRequest(), userAnswers)

    val controller: BaseItemsNavigationController = new BaseItemsNavigationController {
      override val userAnswersService: UserAnswersService = mockUserAnswersService
      override val navigator: BaseNavigator = new FakeNavigator(testOnwardRoute)

      override protected def controllerComponents: MessagesControllerComponents = Helpers.stubMessagesControllerComponents()
    }

    val cnCodeSuccessFunction: CnCodeInformation => Result = _ => Results.Ok
    val itemPackagingSuccessFunction: String => Future[Result] = _ => Future.successful(Results.Ok)
  }

  "withItemPackaging" - {
    "must return the item packaging description when both the item and packaging indexes are valid" in new Test(
      emptyUserAnswers
        .set(ItemExciseProductCodePage(testIndex1), testEpcTobacco)
        .set(ItemSelectPackagingPage(testIndex1, testPackagingIndex1), ItemPackaging("BG", "Bag"))
    ) {
      val result: Future[Result] = controller.withItemPackaging(testIndex1, testPackagingIndex1)(itemPackagingSuccessFunction)

      status(result) mustBe OK
    }

    "must redirect to the packaging index" - {
      "when the item index is invalid" in new Test(
        emptyUserAnswers
          .set(ItemExciseProductCodePage(testIndex1), testEpcTobacco)
          .set(ItemSelectPackagingPage(testIndex1, testPackagingIndex1), ItemPackaging("BG", "Bag"))
      ) {
        val result: Future[Result] = controller.withItemPackaging(testIndex2, testPackagingIndex1)(itemPackagingSuccessFunction)

        status(result) mustBe SEE_OTHER
        redirectLocation(result).value mustBe routes.ItemsPackagingIndexController.onPageLoad(request.ern, request.draftId, testIndex2).url
      }

      "when the packaging index is invalid" in new Test(
        emptyUserAnswers
          .set(ItemExciseProductCodePage(testIndex1), testEpcTobacco)
          .set(ItemSelectPackagingPage(testIndex1, testPackagingIndex1), ItemPackaging("BG", "Bag"))
      ) {
        val result: Future[Result] = controller.withItemPackaging(testIndex1, testPackagingIndex2)(itemPackagingSuccessFunction)

        status(result) mustBe SEE_OTHER
        redirectLocation(result).value mustBe routes.ItemsPackagingIndexController.onPageLoad(request.ern, request.draftId, testIndex1).url
      }
    }
  }

  "withBulkItemPackaging" - {
    "must return the item bulk packaging description when the item index is valid" in new Test(
      emptyUserAnswers
        .set(ItemExciseProductCodePage(testIndex1), testCnCodeTobacco)
        .set(ItemBulkPackagingSelectPage(testIndex1), BulkPackagingType(BulkSolidPowders, "Bulk, solid, fine (powders)"))
    ) {
      val result: Future[Result] = controller.withItemBulkPackaging(testIndex1)(itemPackagingSuccessFunction)

      status(result) mustBe OK
    }

    "must redirect to the items index" - {
      "when the index is invalid" in new Test(
        emptyUserAnswers
          .set(ItemExciseProductCodePage(testIndex1), testCnCodeTobacco)
          .set(ItemBulkPackagingSelectPage(testIndex1), BulkPackagingType(BulkSolidPowders, "Bulk, solid, fine (powders)"))
      ) {
        val result: Future[Result] = controller.withItemBulkPackaging(testIndex2)(itemPackagingSuccessFunction)

        status(result) mustBe SEE_OTHER
        redirectLocation(result).value mustBe routes.ItemsIndexController.onPageLoad(request.ern, request.draftId).url
      }
    }
  }

  "withItemPackagingQuantity" - {

    "must return the item packaging quantity when both the item and packaging indexes are valid" in new Test(
      emptyUserAnswers
        .set(ItemExciseProductCodePage(testIndex1), testEpcTobacco)
        .set(ItemPackagingQuantityPage(testIndex1, testPackagingIndex1), "2")
    ) {
      val result: Future[Result] = controller.withItemPackagingQuantity(testIndex1, testPackagingIndex1)(itemPackagingSuccessFunction)

      status(result) mustBe OK
    }

    "must redirect to the packaging index" - {
      "when the item index is invalid" in new Test(
        emptyUserAnswers
          .set(ItemExciseProductCodePage(testIndex1), testEpcTobacco)
          .set(ItemPackagingQuantityPage(testIndex1, testPackagingIndex1), "2")
      ) {
        val result: Future[Result] = controller.withItemPackagingQuantity(testIndex2, testPackagingIndex1)(itemPackagingSuccessFunction)

        status(result) mustBe SEE_OTHER
        redirectLocation(result).value mustBe routes.ItemsPackagingIndexController.onPageLoad(request.ern, request.draftId, testIndex2).url
      }

      "when the packaging index is invalid" in new Test(
        emptyUserAnswers
          .set(ItemExciseProductCodePage(testIndex1), testEpcTobacco)
          .set(ItemPackagingQuantityPage(testIndex1, testPackagingIndex1), "2")
      ) {
        val result: Future[Result] = controller.withItemPackagingQuantity(testIndex1, testPackagingIndex2)(itemPackagingSuccessFunction)

        status(result) mustBe SEE_OTHER
        redirectLocation(result).value mustBe routes.ItemsPackagingIndexController.onPageLoad(request.ern, request.draftId, testIndex1).url
      }
    }
  }

  //scalastyle:off
  ".updateItemSubmissionFailureIndexes" - {

    "when multiple errors for multiple items exists" - {

      val userAnswersWithFailures = emptyUserAnswers.copy(submissionFailures = Seq(
        movementSubmissionFailure,
        itemQuantityFailure(1),
        itemQuantityFailure(4),
        itemDegreesPlatoFailure(4),
        itemQuantityFailure(10),
        movementSubmissionFailure,
        itemQuantityFailure(20),
        itemQuantityFailure(11)
      ))

      "when removing an item at the start (first item with error)" - {

        "should remove the item failure AND reindex XPath for ErrorLocations to synchronise them with the new array order" in new Test(userAnswersWithFailures) {

          val result = controller.updateItemSubmissionFailureIndexes(Index(0), userAnswersWithFailures)

          result.submissionFailures mustBe Seq(
            movementSubmissionFailure,
            itemQuantityFailure(3),
            itemDegreesPlatoFailure(3),
            itemQuantityFailure(9),
            movementSubmissionFailure,
            itemQuantityFailure(19),
            itemQuantityFailure(10)
          )
        }
      }

      "when removing an item at a mid-point" - {

        "should remove the item failure AND reindex XPath for ErrorLocations to synchronise them with the new array order" in new Test(userAnswersWithFailures) {

          val result = controller.updateItemSubmissionFailureIndexes(Index(3), userAnswersWithFailures)

          result.submissionFailures mustBe Seq(
            movementSubmissionFailure,
            itemQuantityFailure(1),
            itemQuantityFailure(9),
            movementSubmissionFailure,
            itemQuantityFailure(19),
            itemQuantityFailure(10)
          )
        }
      }

      "when removing an item at the end (last item with error)" - {

        "should remove the item failure AND reindex XPath for ErrorLocations to synchronise them with the new array order" in new Test(userAnswersWithFailures) {

          val result = controller.updateItemSubmissionFailureIndexes(Index(19), userAnswersWithFailures)

          result.submissionFailures mustBe Seq(
            movementSubmissionFailure,
            itemQuantityFailure(1),
            itemQuantityFailure(4),
            itemDegreesPlatoFailure(4),
            itemQuantityFailure(10),
            movementSubmissionFailure,
            itemQuantityFailure(11)
          )
        }
      }

      "when removing an item that doesn't have an error against it" - {

        "should update all subsequent XPath for ErrorLocations to synchronise them with the new array order" in new Test(userAnswersWithFailures) {

          val result = controller.updateItemSubmissionFailureIndexes(Index(15), userAnswersWithFailures)

          result.submissionFailures mustBe Seq(
            movementSubmissionFailure,
            itemQuantityFailure(1),
            itemQuantityFailure(4),
            itemDegreesPlatoFailure(4),
            itemQuantityFailure(10),
            movementSubmissionFailure,
            itemQuantityFailure(19),
            itemQuantityFailure(11)
          )
        }
      }

      "when removing an item and not submission failures exist for any items" - {

        val userAnswersWithFailures = emptyUserAnswers.copy(submissionFailures = Seq(movementSubmissionFailure))

        "should return the submission failures unaffected" in new Test(userAnswersWithFailures) {

          val result = controller.updateItemSubmissionFailureIndexes(Index(15), userAnswersWithFailures)

          result.submissionFailures mustBe Seq(movementSubmissionFailure)
        }
      }

      "when removing an item before another item" - {

        val userAnswersWithFailures = emptyUserAnswers.copy(submissionFailures = Seq(
          itemQuantityFailure(1).copy(originalAttributeValue = Some("1")),
          itemQuantityFailure(2)
        ))

        "should remove the item failure AND reindex XPath for ErrorLocations to synchronise them with the new array order" in new Test(userAnswersWithFailures) {

          val result = controller.updateItemSubmissionFailureIndexes(Index(0), userAnswersWithFailures)

          result.submissionFailures mustBe Seq(itemQuantityFailure(1))
        }
      }
    }
  }

  ".removeItemSubmissionFailure" - {

    val userAnswersWithFailures = emptyUserAnswers.copy(submissionFailures = Seq(
      movementSubmissionFailure,
      itemQuantityFailure(1),
      itemQuantityFailure(2),
      movementSubmissionFailure
    ))

    "should remove the submission failure at the specified index" in new Test(userAnswersWithFailures) {

      val result = controller.removeItemSubmissionFailure(testIndex1, userAnswersWithFailures)

      result.submissionFailures mustBe Seq(movementSubmissionFailure, itemQuantityFailure(2), movementSubmissionFailure)
    }

    "return the original user answers" - {

      "when no submission failures exist" in new Test(emptyUserAnswers) {

        val result = controller.removeItemSubmissionFailure(testIndex1, emptyUserAnswers)

        result.submissionFailures mustBe Seq()
      }

      "when no submission failures exist at this index" in new Test(emptyUserAnswers.copy(submissionFailures = Seq(itemQuantityFailure(2), movementSubmissionFailure))) {

        val result = controller.removeItemSubmissionFailure(testIndex1, emptyUserAnswers.copy(submissionFailures = Seq(movementSubmissionFailure, itemQuantityFailure(2))))

        result.submissionFailures mustBe Seq(movementSubmissionFailure, itemQuantityFailure(2))
      }
    }
  }

  ".withExciseProductCode" - {

    "must return the item excise product code when both the item index is valid" in new Test(
      emptyUserAnswers.set(ItemExciseProductCodePage(testIndex1), testEpcTobacco)
    ) {
      val result: Future[Result] = controller.withExciseProductCode(testIndex1)(itemPackagingSuccessFunction)

      status(result) mustBe OK
    }

    "must redirect to the item index" - {

      "when the item index is invalid" in new Test(
        emptyUserAnswers.set(ItemExciseProductCodePage(testIndex1), testEpcTobacco)
      ) {
        val result: Future[Result] = controller.withExciseProductCode(testIndex2)(itemPackagingSuccessFunction)

        status(result) mustBe SEE_OTHER
        redirectLocation(result).value mustBe routes.ItemsIndexController.onPageLoad(request.ern, request.draftId).url
      }
    }
  }
}
