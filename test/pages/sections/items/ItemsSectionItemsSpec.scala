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
import models.sections.items.ItemGeographicalIndicationType.NoGeographicalIndication
import models.sections.items.ItemWineProductCategory.Other
import models.sections.items.{ItemBrandNameModel, ItemNetGrossMassModel}
import play.api.test.FakeRequest

class ItemsSectionItemsSpec extends SpecBase with ItemFixtures with MovementSubmissionFailureFixtures {

  "isCompleted" - {

    "must return true" - {

      "when all items are completed" in {

        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), singleCompletedWineItem)

        ItemsSectionItems.isCompleted mustBe true
      }
    }

    "must return false" - {

      "when no items added" in {

        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), emptyUserAnswers)

        ItemsSectionItems.isCompleted mustBe false
      }

      "when an item exists which is not complete" in {

        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), singleCompletedWineItem
          .set(ItemExciseProductCodePage(testIndex2), testEpcTobacco)
        )

        ItemsSectionItems.isCompleted mustBe false
      }
    }
  }

  "isMovementSubmissionError" - {

    "should return false" - {

      "when there are no items" in {

        ItemsSectionItems.isMovementSubmissionError(dataRequest(FakeRequest(), emptyUserAnswers)) mustBe false
      }

      "when there are items but none of them have submission errors" in {

        val answers = Seq(testIndex2, testIndex3).foldLeft(singleCompletedWineItem) { (userAnswers, index) =>
          userAnswers
            .set(ItemExciseProductCodePage(index), testEpcWine)
            .set(ItemCommodityCodePage(index), testCnCodeWine)
            .set(ItemBrandNamePage(index), ItemBrandNameModel(hasBrandName = true, Some("brand")))
            .set(ItemCommercialDescriptionPage(index), "Wine from grapes")
            .set(ItemAlcoholStrengthPage(index), BigDecimal(12.5))
            .set(ItemGeographicalIndicationChoicePage(index), NoGeographicalIndication)
            .set(ItemQuantityPage(index), BigDecimal("1000"))
            .set(ItemNetGrossMassPage(index), ItemNetGrossMassModel(BigDecimal("2000"), BigDecimal("2105")))
            .set(ItemBulkPackagingChoicePage(index), false)
            .set(ItemWineProductCategoryPage(index), Other)
            .set(ItemWineMoreInformationChoicePage(index), false)
            .set(ItemSelectPackagingPage(index, testPackagingIndex1), testPackageBag)
            .set(ItemPackagingQuantityPage(index, testPackagingIndex1), "400")
            .set(ItemPackagingProductTypePage(index, testPackagingIndex1), true)
            .set(ItemPackagingSealChoicePage(index, testPackagingIndex1), false)
        }
        ItemsSectionItems.isMovementSubmissionError(dataRequest(FakeRequest(), answers)) mustBe false
      }
    }

    "should return true" - {

      "when an error exists within the items section" in {

        val answers = Seq(testIndex2, testIndex3).foldLeft(singleCompletedWineItem) { (userAnswers, index) =>
          userAnswers
            .set(ItemExciseProductCodePage(index), testEpcWine)
            .set(ItemCommodityCodePage(index), testCnCodeWine)
            .set(ItemBrandNamePage(index), ItemBrandNameModel(hasBrandName = true, Some("brand")))
            .set(ItemCommercialDescriptionPage(index), "Wine from grapes")
            .set(ItemAlcoholStrengthPage(index), BigDecimal(12.5))
            .set(ItemGeographicalIndicationChoicePage(index), NoGeographicalIndication)
            .set(ItemQuantityPage(index), BigDecimal("1000"))
            .set(ItemNetGrossMassPage(index), ItemNetGrossMassModel(BigDecimal("2000"), BigDecimal("2105")))
            .set(ItemBulkPackagingChoicePage(index), false)
            .set(ItemWineProductCategoryPage(index), Other)
            .set(ItemWineMoreInformationChoicePage(index), false)
            .set(ItemSelectPackagingPage(index, testPackagingIndex1), testPackageBag)
            .set(ItemPackagingQuantityPage(index, testPackagingIndex1), "400")
            .set(ItemPackagingProductTypePage(index, testPackagingIndex1), true)
            .set(ItemPackagingSealChoicePage(index, testPackagingIndex1), false)
        }.copy(
          submissionFailures = Seq(itemQuantityFailure(2))
        )
        ItemsSectionItems.isMovementSubmissionError(dataRequest(FakeRequest(), answers)) mustBe true
      }
    }
  }

  "indexesOfItemsWithSubmissionFailures" - {

    "should return all the submission indexes of items with submission failures" - {

      //scalastyle:off
      "when some exist" in {

        val userAnswersWithFailures = emptyUserAnswers.copy(submissionFailures = Seq(
          movementSubmissionFailure,
          itemQuantityFailure(1),
          itemQuantityFailure(4),
          itemQuantityFailure(10),
          movementSubmissionFailure,
          itemQuantityFailure(20),
          itemQuantityFailure(11)
        ))

        ItemsSectionItems.indexesOfItemsWithSubmissionFailures(userAnswersWithFailures) mustBe Seq(1, 4, 10, 20, 11)
      }
    }

    "should return an empty list" - {

      "when there are no submission failures" - {
        ItemsSectionItems.indexesOfItemsWithSubmissionFailures(emptyUserAnswers) mustBe Seq()
      }

      "when there are no item submission failures" - {
        ItemsSectionItems.indexesOfItemsWithSubmissionFailures(
          emptyUserAnswers.copy(submissionFailures = Seq(movementSubmissionFailure))
        )mustBe Seq()
      }
    }
  }
}
