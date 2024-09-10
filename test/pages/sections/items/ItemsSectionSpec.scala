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

package pages.sections.items

import base.SpecBase
import fixtures.{ItemFixtures, MovementSubmissionFailureFixtures}
import models.requests.DataRequest
import models.sections.items.ItemsAddToList
import play.api.test.FakeRequest
import viewmodels.taskList._

class ItemsSectionSpec extends SpecBase with ItemFixtures with MovementSubmissionFailureFixtures {

  "status" - {

    "must return NotStarted" - {

      "when no items" in {

        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), emptyUserAnswers)

        ItemsSection.status mustBe NotStarted
      }
    }

    "must return Completed" - {

      "when all items are completed and ItemsAddToList.No" in {

        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), singleCompletedWineItem.set(ItemsAddToListPage, ItemsAddToList.No))

        ItemsSection.status mustBe Completed
      }
    }

    "must return InProgress" - {

      "when all items are completed and ItemsAddToList.Yes" in {

        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), singleCompletedWineItem.set(ItemsAddToListPage, ItemsAddToList.Yes))

        ItemsSection.status mustBe InProgress
      }

      "when all items are completed and ItemsAddToList.MoreLater" in {

        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), singleCompletedWineItem.set(ItemsAddToListPage, ItemsAddToList.MoreLater))

        ItemsSection.status mustBe InProgress
      }

      "when all items are completed and missing" in {

        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), singleCompletedWineItem)

        ItemsSection.status mustBe InProgress
      }

      "when an item exists which is not complete, even with ItemAddToList.No" in {

        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), singleCompletedWineItem
          .set(ItemExciseProductCodePage(testIndex2), testEpcTobacco)
          .set(ItemsAddToListPage, ItemsAddToList.No)
        )

        ItemsSection.status mustBe InProgress
      }
    }
  }

  "retrieveAllShippingMarks" - {
    "must return a non-empty Seq" - {
      "when all shipping marks are in one item" in {
        val userAnswers =
          emptyUserAnswers
            .set(ItemPackagingShippingMarksPage(testIndex1, testPackagingIndex1), "mark 1")
            .set(ItemPackagingShippingMarksPage(testIndex1, testPackagingIndex2), "mark 2")
            .set(ItemPackagingShippingMarksPage(testIndex1, testPackagingIndex3), "mark 3")

        ItemsSection.retrieveAllShippingMarks()(dataRequest(FakeRequest(), userAnswers)) mustBe Seq("mark 1", "mark 2", "mark 3")
      }
      "when shipping marks are split across multiple items" in {

        val userAnswers =
          emptyUserAnswers
            .set(ItemPackagingShippingMarksPage(testIndex1, testPackagingIndex1), "mark 1")
            .set(ItemPackagingShippingMarksPage(testIndex1, testPackagingIndex2), "mark 3")
            .set(ItemPackagingShippingMarksPage(testIndex2, testPackagingIndex1), "mark 2")

        ItemsSection.retrieveAllShippingMarks()(dataRequest(FakeRequest(), userAnswers)) mustBe Seq("mark 1", "mark 3", "mark 2")
      }
      "when there are multiple items, but not all items have ItemsPackagingCount" in {
        val userAnswers1 =
          emptyUserAnswers
            .set(ItemPackagingShippingMarksPage(testIndex1, testPackagingIndex1), "mark 1")
            .set(ItemPackagingShippingMarksPage(testIndex1, testPackagingIndex2), "mark 3")
            .set(ItemPackagingQuantityPage(testIndex2, testPackagingIndex1), "quantity")

        ItemsSection.retrieveAllShippingMarks()(dataRequest(FakeRequest(), userAnswers1)) mustBe Seq("mark 1", "mark 3")

        val userAnswers2 =
          emptyUserAnswers
            .set(ItemPackagingQuantityPage(testIndex1, testPackagingIndex1), "quantity")
            .set(ItemPackagingShippingMarksPage(testIndex2, testPackagingIndex1), "mark 1")
            .set(ItemPackagingShippingMarksPage(testIndex2, testPackagingIndex2), "mark 3")

        ItemsSection.retrieveAllShippingMarks()(dataRequest(FakeRequest(), userAnswers2)) mustBe Seq("mark 1", "mark 3")
      }
      "and handle duplicate shipping marks" in {

        val userAnswers =
          emptyUserAnswers
            .set(ItemPackagingShippingMarksPage(testIndex1, testPackagingIndex1), "mark 1")
            .set(ItemPackagingShippingMarksPage(testIndex1, testPackagingIndex2), "mark 1")
            .set(ItemPackagingShippingMarksPage(testIndex1, testPackagingIndex3), "mark 3")

        ItemsSection.retrieveAllShippingMarks()(dataRequest(FakeRequest(), userAnswers)) mustBe Seq("mark 1", "mark 3")
      }
    }

    "must return an empty Seq" - {
      "when ItemsCount is 0" in {
        val userAnswers =
          emptyUserAnswers

        ItemsSection.retrieveAllShippingMarks()(dataRequest(FakeRequest(), userAnswers)) mustBe Seq()
      }
      "when ItemsPackagingCount is 0 for every item" in {
        val userAnswers =
          emptyUserAnswers
            .set(ItemPackagingQuantityPage(testIndex1, testPackagingIndex1), "quantity")
            .set(ItemPackagingQuantityPage(testIndex1, testPackagingIndex2), "quantity 2")
            .set(ItemPackagingQuantityPage(testIndex2, testPackagingIndex1), "quantity 3")

        ItemsSection.retrieveAllShippingMarks()(dataRequest(FakeRequest(), userAnswers)) mustBe Seq()
      }
    }
  }

  "retrieveShippingMarkLocationsMatching" - {
    "must return a non-empty Seq" - {
      "when all shipping marks are in one item" in {
        val userAnswers =
          emptyUserAnswers
            .set(ItemPackagingShippingMarksPage(testIndex1, testPackagingIndex1), "mark 1")
            .set(ItemPackagingShippingMarksPage(testIndex1, testPackagingIndex2), "mark 2")
            .set(ItemPackagingShippingMarksPage(testIndex1, testPackagingIndex3), "mark 3")

        ItemsSection.retrieveShippingMarkLocationsMatching("mark 1")(dataRequest(FakeRequest(), userAnswers)) mustBe Seq((testIndex1, testPackagingIndex1))
      }
      "when shipping marks are split across multiple items" in {

        val userAnswers =
          emptyUserAnswers
            .set(ItemPackagingShippingMarksPage(testIndex1, testPackagingIndex1), "mark 1")
            .set(ItemPackagingShippingMarksPage(testIndex1, testPackagingIndex2), "mark 3")
            .set(ItemPackagingShippingMarksPage(testIndex2, testPackagingIndex1), "mark 2")

        ItemsSection.retrieveShippingMarkLocationsMatching("mark 2")(dataRequest(FakeRequest(), userAnswers)) mustBe Seq((testIndex2, testPackagingIndex1))
      }
      "when there are multiple items, but not all items have ItemsPackagingCount" in {
        val userAnswers1 =
          emptyUserAnswers
            .set(ItemPackagingShippingMarksPage(testIndex1, testPackagingIndex1), "mark 1")
            .set(ItemPackagingShippingMarksPage(testIndex1, testPackagingIndex2), "mark 3")
            .set(ItemPackagingQuantityPage(testIndex2, testPackagingIndex1), "quantity")

        ItemsSection.retrieveShippingMarkLocationsMatching("mark 1")(dataRequest(FakeRequest(), userAnswers1)) mustBe Seq((testIndex1, testPackagingIndex1))

        val userAnswers2 =
          emptyUserAnswers
            .set(ItemPackagingQuantityPage(testIndex1, testPackagingIndex1), "quantity")
            .set(ItemPackagingShippingMarksPage(testIndex2, testPackagingIndex1), "mark 1")
            .set(ItemPackagingShippingMarksPage(testIndex2, testPackagingIndex2), "mark 3")

        ItemsSection.retrieveShippingMarkLocationsMatching("mark 1")(dataRequest(FakeRequest(), userAnswers2)) mustBe Seq((testIndex2, testPackagingIndex1))
      }
      "and handle duplicate shipping marks" in {

        val userAnswers =
          emptyUserAnswers
            .set(ItemPackagingShippingMarksPage(testIndex1, testPackagingIndex1), "mark 1")
            .set(ItemPackagingShippingMarksPage(testIndex1, testPackagingIndex2), "mark 1")
            .set(ItemPackagingShippingMarksPage(testIndex1, testPackagingIndex3), "mark 3")

        ItemsSection.retrieveShippingMarkLocationsMatching("mark 1")(dataRequest(FakeRequest(), userAnswers)) mustBe Seq((testIndex1, testPackagingIndex1), (testIndex1, testPackagingIndex2))
      }
    }

    "must return an empty Seq" - {
      "when ItemsCount is 0" in {
        val userAnswers =
          emptyUserAnswers

        ItemsSection.retrieveShippingMarkLocationsMatching("mark 1")(dataRequest(FakeRequest(), userAnswers)) mustBe Seq()
      }
      "when ItemsPackagingCount is 0 for every item" in {
        val userAnswers =
          emptyUserAnswers
            .set(ItemPackagingQuantityPage(testIndex1, testPackagingIndex1), "quantity")
            .set(ItemPackagingQuantityPage(testIndex1, testPackagingIndex2), "quantity 2")
            .set(ItemPackagingQuantityPage(testIndex2, testPackagingIndex1), "quantity 3")

        ItemsSection.retrieveShippingMarkLocationsMatching("mark 1")(dataRequest(FakeRequest(), userAnswers)) mustBe Seq()
      }
      "when no packaging matches the value entered" in {
        val userAnswers =
          emptyUserAnswers
            .set(ItemPackagingShippingMarksPage(testIndex1, testPackagingIndex1), "mark 1")
            .set(ItemPackagingShippingMarksPage(testIndex1, testPackagingIndex2), "mark 3")
            .set(ItemPackagingShippingMarksPage(testIndex2, testPackagingIndex1), "mark 2")

        ItemsSection.retrieveShippingMarkLocationsMatching("mark 4")(dataRequest(FakeRequest(), userAnswers)) mustBe Seq()
      }
    }
  }

  "shippingMarkForItemIsUsedOnOtherItems" - {
    "must return true" - {
      "when items shipping mark is used on an item with package quantity == 0" in {
        val userAnswers =
          emptyUserAnswers
            .set(ItemPackagingShippingMarksPage(testIndex1, testPackagingIndex1), "Mark1")
            .set(ItemPackagingQuantityPage(testIndex1, testPackagingIndex1), "1")
            .set(ItemPackagingShippingMarksPage(testIndex2, testPackagingIndex1), "Mark1")
            .set(ItemPackagingQuantityPage(testIndex2, testPackagingIndex1), "0")

        ItemsSection.shippingMarkForItemIsUsedOnOtherItems(testIndex1, testPackagingIndex1)(dataRequest(FakeRequest(), userAnswers)) mustBe true
      }
    }

    "must return false" - {
      "when items shipping mark is used on an item with package quantity > 0" in {
        val userAnswers =
          emptyUserAnswers
            .set(ItemPackagingShippingMarksPage(testIndex1, testPackagingIndex1), "Mark1")
            .set(ItemPackagingQuantityPage(testIndex1, testPackagingIndex1), "1")
            .set(ItemPackagingShippingMarksPage(testIndex2, testPackagingIndex1), "Mark1")
            .set(ItemPackagingQuantityPage(testIndex2, testPackagingIndex1), "1")

        ItemsSection.shippingMarkForItemIsUsedOnOtherItems(testIndex1, testPackagingIndex1)(dataRequest(FakeRequest(), userAnswers)) mustBe false
      }

      "when items shipping mark does not match another packaging shipping mark" in {
        val userAnswers =
          emptyUserAnswers
            .set(ItemPackagingShippingMarksPage(testIndex1, testPackagingIndex1), "Mark1")
            .set(ItemPackagingQuantityPage(testIndex1, testPackagingIndex1), "1")
            .set(ItemPackagingShippingMarksPage(testIndex2, testPackagingIndex1), "Mark2")
            .set(ItemPackagingQuantityPage(testIndex2, testPackagingIndex1), "0")

        ItemsSection.shippingMarkForItemIsUsedOnOtherItems(testIndex1, testPackagingIndex1)(dataRequest(FakeRequest(), userAnswers)) mustBe false
      }

      "when item packaging quantity is zero" in {
        val userAnswers =
          emptyUserAnswers
            .set(ItemPackagingShippingMarksPage(testIndex1, testPackagingIndex1), "Mark1")
            .set(ItemPackagingQuantityPage(testIndex1, testPackagingIndex1), "1")
            .set(ItemPackagingShippingMarksPage(testIndex2, testPackagingIndex1), "Mark1")
            .set(ItemPackagingQuantityPage(testIndex2, testPackagingIndex1), "0")

        ItemsSection.shippingMarkForItemIsUsedOnOtherItems(testIndex2, testPackagingIndex1)(dataRequest(FakeRequest(), userAnswers)) mustBe false
      }

      "when shipping mark answer doesn't exist" in {
        val userAnswers =
          emptyUserAnswers
            .set(ItemPackagingQuantityPage(testIndex1, testPackagingIndex1), "1")
            .set(ItemPackagingQuantityPage(testIndex2, testPackagingIndex1), "0")

        ItemsSection.shippingMarkForItemIsUsedOnOtherItems(testIndex1, testPackagingIndex1)(dataRequest(FakeRequest(), userAnswers)) mustBe false
      }
    }
  }

  "removeAnyPackagingThatMatchesTheShippingMark" - {
    "when a specific indexes is told to be excluded from deletion" - {
      "when there are shipping marks linked to other packages" - {
        "must return a UserAnswers with the packaging removed" in {

          val userAnswers =
            emptyUserAnswers
              .set(ItemPackagingShippingMarksPage(testIndex1, testPackagingIndex1), "Mark1")
              .set(ItemPackagingQuantityPage(testIndex1, testPackagingIndex1), "1")
              .set(ItemPackagingShippingMarksPage(testIndex1, testPackagingIndex2), "Mark1")
              .set(ItemPackagingQuantityPage(testIndex1, testPackagingIndex2), "0")
              .set(ItemPackagingShippingMarksPage(testIndex2, testPackagingIndex1), "Mark1")
              .set(ItemPackagingQuantityPage(testIndex2, testPackagingIndex1), "0")
              .set(ItemPackagingShippingMarksPage(testIndex3, testPackagingIndex1), "Mark2")
              .set(ItemPackagingQuantityPage(testIndex3, testPackagingIndex1), "1")

          implicit val req = dataRequest(FakeRequest(), userAnswers)

          ItemsSection.removeAnyPackagingThatMatchesTheShippingMark("Mark1", Some(testIndex1 -> testPackagingIndex1)) mustBe userAnswers
            .remove(ItemsPackagingSectionItems(testIndex1, testPackagingIndex2))
            .remove(ItemsPackagingSectionItems(testIndex2, testPackagingIndex1))
        }
      }

      "when shipping mark exists, but only against the package which is excluded from being removed" - {
        "must return a UserAnswers unchanged" in {

          val userAnswers =
            emptyUserAnswers
              .set(ItemPackagingShippingMarksPage(testIndex1, testPackagingIndex1), "Mark1")
              .set(ItemPackagingQuantityPage(testIndex1, testPackagingIndex1), "1")
              .set(ItemPackagingShippingMarksPage(testIndex1, testPackagingIndex2), "Mark1")
              .set(ItemPackagingQuantityPage(testIndex1, testPackagingIndex2), "0")
              .set(ItemPackagingShippingMarksPage(testIndex2, testPackagingIndex1), "Mark1")
              .set(ItemPackagingQuantityPage(testIndex2, testPackagingIndex1), "0")
              .set(ItemPackagingShippingMarksPage(testIndex3, testPackagingIndex1), "Mark2")
              .set(ItemPackagingQuantityPage(testIndex3, testPackagingIndex1), "1")

          implicit val req = dataRequest(FakeRequest(), userAnswers)

          ItemsSection.removeAnyPackagingThatMatchesTheShippingMark("Mark2", Some(testIndex3 -> testPackagingIndex1)) mustBe userAnswers
        }
      }

      "when no shipping marks exist with the supplied shipping mark" - {
        "must return a UserAnswers unchanged" in {

          val userAnswers =
            emptyUserAnswers
              .set(ItemPackagingQuantityPage(testIndex1, testPackagingIndex1), "1")
              .set(ItemPackagingShippingMarksPage(testIndex1, testPackagingIndex2), "Mark1")
              .set(ItemPackagingQuantityPage(testIndex1, testPackagingIndex2), "0")
              .set(ItemPackagingShippingMarksPage(testIndex2, testPackagingIndex1), "Mark1")
              .set(ItemPackagingQuantityPage(testIndex2, testPackagingIndex1), "0")
              .set(ItemPackagingShippingMarksPage(testIndex3, testPackagingIndex1), "Mark2")
              .set(ItemPackagingQuantityPage(testIndex3, testPackagingIndex1), "1")

          implicit val req = dataRequest(FakeRequest(), userAnswers)

          ItemsSection.removeAnyPackagingThatMatchesTheShippingMark("NotExist") mustBe userAnswers
        }
      }
    }

    "when a NO indexes are told to be excluded from deletion" - {
      "when there are shipping marks linked to other packages" - {
        "must return a UserAnswers with any packaging removed which match the Mark" in {

          val userAnswers =
            emptyUserAnswers
              .set(ItemPackagingShippingMarksPage(testIndex1, testPackagingIndex1), "Mark1")
              .set(ItemPackagingQuantityPage(testIndex1, testPackagingIndex1), "1")
              .set(ItemPackagingShippingMarksPage(testIndex1, testPackagingIndex2), "Mark1")
              .set(ItemPackagingQuantityPage(testIndex1, testPackagingIndex2), "0")
              .set(ItemPackagingShippingMarksPage(testIndex2, testPackagingIndex1), "Mark1")
              .set(ItemPackagingQuantityPage(testIndex2, testPackagingIndex1), "0")
              .set(ItemPackagingShippingMarksPage(testIndex3, testPackagingIndex1), "Mark2")
              .set(ItemPackagingQuantityPage(testIndex3, testPackagingIndex1), "1")

          implicit val req = dataRequest(FakeRequest(), userAnswers)

          ItemsSection.removeAnyPackagingThatMatchesTheShippingMark("Mark1") mustBe userAnswers
            .remove(ItemsPackagingSectionItems(testIndex2, testPackagingIndex1))
            .remove(ItemsPackagingSectionItems(testIndex1, testPackagingIndex2))
            .remove(ItemsPackagingSectionItems(testIndex1, testPackagingIndex1))
        }
      }
    }
  }
}
