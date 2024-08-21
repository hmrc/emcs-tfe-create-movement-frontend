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
import config.Constants.BODYEADESAD
import fixtures.{ItemFixtures, MovementSubmissionFailureFixtures}
import models.GoodsType._
import models.requests.DataRequest
import models.response.InvalidRegexException
import models.sections.items.ItemGeographicalIndicationType.NoGeographicalIndication
import models.sections.items.ItemSmallIndependentProducerType.{CertifiedIndependentSmallProducer, NotAIndependentSmallProducer, NotProvided}
import models.sections.items.ItemWineProductCategory.Other
import models.sections.items.{ItemBrandNameModel, ItemDesignationOfOriginModel, ItemNetGrossMassModel, ItemSmallIndependentProducerModel}
import play.api.test.FakeRequest
import utils.{ItemDegreesPlatoError, ItemQuantityError}
import viewmodels.taskList.{NotStarted, UpdateNeeded}

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

  "status" - {

    "should return UpdateNeeded" - {

      "when a submission failure exists for this item" in {

        val answers = singleCompletedWineItem.copy(submissionFailures = Seq(itemQuantityFailure(1)))
        ItemsSectionItems.status(dataRequest(FakeRequest(), answers)) mustBe UpdateNeeded
      }
    }

    "should return NotStarted" - {

      "when there are no items" in {

        ItemsSectionItems.status(dataRequest(FakeRequest(), emptyUserAnswers)) mustBe NotStarted
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
            .set(ItemDesignationOfOriginPage(testIndex1), ItemDesignationOfOriginModel(NoGeographicalIndication, None, None))
            .set(ItemQuantityPage(index), BigDecimal("1000"))
            .set(ItemNetGrossMassPage(index), ItemNetGrossMassModel(BigDecimal("2000"), BigDecimal("2105")))
            .set(ItemBulkPackagingChoicePage(index), false)
            .set(ItemWineProductCategoryPage(index), Other)
            .set(ItemWineMoreInformationChoicePage(index), false)
            .set(ItemSelectPackagingPage(index, testPackagingIndex1), testPackageBag)
            .set(ItemPackagingQuantityPage(index, testPackagingIndex1), "400")
            .set(ItemPackagingShippingMarksChoicePage(index, testPackagingIndex1), true)
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
            .set(ItemDesignationOfOriginPage(index), ItemDesignationOfOriginModel(NoGeographicalIndication, None, None))
            .set(ItemQuantityPage(index), BigDecimal("1000"))
            .set(ItemNetGrossMassPage(index), ItemNetGrossMassModel(BigDecimal("2000"), BigDecimal("2105")))
            .set(ItemBulkPackagingChoicePage(index), false)
            .set(ItemWineProductCategoryPage(index), Other)
            .set(ItemWineMoreInformationChoicePage(index), false)
            .set(ItemSelectPackagingPage(index, testPackagingIndex1), testPackageBag)
            .set(ItemPackagingQuantityPage(index, testPackagingIndex1), "400")
            .set(ItemPackagingShippingMarksChoicePage(index, testPackagingIndex1), true)
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

      "when multiple errors exist for the same item - returning only distinct values" in {

        val userAnswersWithFailures = emptyUserAnswers.copy(submissionFailures = Seq(
          movementSubmissionFailure,
          itemQuantityFailure(1),
          itemDegreesPlatoFailure(1),
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
        ) mustBe Seq()
      }
    }

    "should throw an exception" - {

      s"when the errorLocation does not have a $BODYEADESAD" in {

        intercept[InvalidRegexException](ItemsSectionItems.indexesOfItemsWithSubmissionFailures(
          emptyUserAnswers.copy(submissionFailures = Seq(itemQuantityFailure(1).copy(
            errorLocation = Some(s"$BODYEADESAD[]")
          )))
        )).getMessage mustBe s"[indexesOfItemsWithSubmissionFailures] Invalid item error location received: Some($BODYEADESAD[])"
      }
    }
  }

  "getSubmissionFailuresForItems" - {

    val twoCompletedItems = singleCompletedWineItem
      .set(ItemExciseProductCodePage(testIndex2), testEpcWine)
      .set(ItemCommodityCodePage(testIndex2), testCnCodeWine)
      .set(ItemBrandNamePage(testIndex2), ItemBrandNameModel(hasBrandName = true, Some("brand")))
      .set(ItemCommercialDescriptionPage(testIndex2), "Wine from grapes")
      .set(ItemAlcoholStrengthPage(testIndex2), BigDecimal(12.5))
      .set(ItemDesignationOfOriginPage(testIndex2), ItemDesignationOfOriginModel(NoGeographicalIndication, None, None))
      .set(ItemQuantityPage(testIndex2), BigDecimal("1000"))
      .set(ItemNetGrossMassPage(testIndex2), ItemNetGrossMassModel(BigDecimal("2000"), BigDecimal("2105")))
      .set(ItemBulkPackagingChoicePage(testIndex2), false)
      .set(ItemWineProductCategoryPage(testIndex2), Other)
      .set(ItemWineMoreInformationChoicePage(testIndex2), false)
      .set(ItemSelectPackagingPage(testIndex2, testPackagingIndex1), testPackageBag)
      .set(ItemPackagingQuantityPage(testIndex2, testPackagingIndex1), "400")
      .set(ItemPackagingShippingMarksChoicePage(testIndex2, testPackagingIndex1), true)
      .set(ItemPackagingSealChoicePage(testIndex2, testPackagingIndex1), false)

    "return an empty list" - {

      "when no items exist" in {

        ItemsSectionItems.getSubmissionFailuresForItems()(dataRequest(FakeRequest(), emptyUserAnswers.copy(submissionFailures = Seq()))) mustBe Seq.empty
      }

      "when no submission failures exist" in {

        ItemsSectionItems.getSubmissionFailuresForItems()(dataRequest(FakeRequest(), singleCompletedWineItem.copy(submissionFailures = Seq()))) mustBe Seq.empty
      }

      "when submission failures exist but all are fixed" in {
        ItemsSectionItems.getSubmissionFailuresForItems()(dataRequest(FakeRequest(), twoCompletedItems.copy(
          submissionFailures = Seq(
            itemQuantityFailure(1).copy(hasBeenFixed = true),
            itemDegreesPlatoFailure(1).copy(hasBeenFixed = true),
            itemQuantityFailure(2).copy(hasBeenFixed = true),
            itemDegreesPlatoFailure(2).copy(hasBeenFixed = true)
          )
        ))) mustBe Seq.empty
      }
    }

    "return the submission failures that exist" - {
      "returning only the number of errors that haven't been fixed" in {
        ItemsSectionItems.getSubmissionFailuresForItems()(dataRequest(FakeRequest(), twoCompletedItems.copy(
          submissionFailures = Seq(
            itemQuantityFailure(1).copy(hasBeenFixed = false),
            itemDegreesPlatoFailure(1).copy(hasBeenFixed = true),
            itemQuantityFailure(2).copy(hasBeenFixed = true),
            itemDegreesPlatoFailure(2).copy(hasBeenFixed = false)
          )
        ))) mustBe Seq(
          ItemQuantityError(testIndex1, isForAddToList = false),
          ItemDegreesPlatoError(testIndex2, isForAddToList = false),
        )
      }

      "when all errors haven't been fixed" in {
        ItemsSectionItems.getSubmissionFailuresForItems()(dataRequest(FakeRequest(), twoCompletedItems.copy(
          submissionFailures = Seq(
            itemQuantityFailure(1),
            itemDegreesPlatoFailure(1),
            itemQuantityFailure(2),
            itemDegreesPlatoFailure(2)
          )
        ))) mustBe Seq(
          ItemQuantityError(testIndex1, isForAddToList = false),
          ItemDegreesPlatoError(testIndex1, isForAddToList = false),
          ItemQuantityError(testIndex2, isForAddToList = false),
          ItemDegreesPlatoError(testIndex2, isForAddToList = false)
        )
      }

      "when the user is on the add to list page" in {
        ItemsSectionItems.getSubmissionFailuresForItems(isOnAddToList = true)(dataRequest(FakeRequest(), twoCompletedItems.copy(
          submissionFailures = Seq(
            itemQuantityFailure(1),
            itemDegreesPlatoFailure(1),
            itemQuantityFailure(2),
            itemDegreesPlatoFailure(2)
          )
        ))) mustBe Seq(
          ItemQuantityError(testIndex1, isForAddToList = true),
          ItemDegreesPlatoError(testIndex1, isForAddToList = true),
          ItemQuantityError(testIndex2, isForAddToList = true),
          ItemDegreesPlatoError(testIndex2, isForAddToList = true)
        )
      }
    }
  }

  "onlyContainsOrIsEmpty" - {

    "when give a list of one" - {

      "when there are NO GoodsTypes" - {

        "return true" in {
          implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), emptyUserAnswers)
          ItemsSectionItems.onlyContainsOrIsEmpty(Energy) mustBe true
        }
      }

      "when there is one GoodsType" - {

        "when the the goods type matches" - {

          "return true" in {
            implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), emptyUserAnswers
              .set(ItemExciseProductCodePage(0), Energy.code)
            )
            ItemsSectionItems.onlyContainsOrIsEmpty(Energy) mustBe true
          }
        }

        "when the the goods type does NOT match" - {

          "return false" in {
            implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), emptyUserAnswers
              .set(ItemExciseProductCodePage(0), Energy.code)
            )
            ItemsSectionItems.onlyContainsOrIsEmpty(Beer) mustBe false
          }
        }
      }

      "when there are multiple GoodsTypes" - {

        "when none match" - {

          "return false" in {
            implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), emptyUserAnswers
              .set(ItemExciseProductCodePage(0), Energy.code)
              .set(ItemExciseProductCodePage(1), Beer.code)
              .set(ItemExciseProductCodePage(2), Intermediate.code)
            )
            ItemsSectionItems.onlyContainsOrIsEmpty(Wine) mustBe false
          }
        }

        "when one matches" - {

          "return false" in {
            implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), emptyUserAnswers
              .set(ItemExciseProductCodePage(0), Energy.code)
              .set(ItemExciseProductCodePage(1), Beer.code)
              .set(ItemExciseProductCodePage(2), Intermediate.code)
            )
            ItemsSectionItems.onlyContainsOrIsEmpty(Energy) mustBe false
          }
        }

        "when multiple match (but not all)" - {

          "return false" in {
            implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), emptyUserAnswers
              .set(ItemExciseProductCodePage(0), Energy.code)
              .set(ItemExciseProductCodePage(1), Beer.code)
              .set(ItemExciseProductCodePage(2), Energy.code)
            )
            ItemsSectionItems.onlyContainsOrIsEmpty(Energy) mustBe false
          }
        }

        "when multiple match (all)" - {

          "return true" in {
            implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), emptyUserAnswers
              .set(ItemExciseProductCodePage(0), Energy.code)
              .set(ItemExciseProductCodePage(1), Energy.code)
              .set(ItemExciseProductCodePage(2), Energy.code)
            )
            ItemsSectionItems.onlyContainsOrIsEmpty(Energy) mustBe true
          }
        }
      }
    }

    "when give a list of multiple goods types" - {

      "when there are NO GoodsTypes" - {

        "return true" in {
          implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), emptyUserAnswers)
          ItemsSectionItems.onlyContainsOrIsEmpty(Energy, Beer) mustBe true
        }
      }

      "when there is one GoodsType" - {

        "when the the goods type matches" - {

          "return true" in {
            implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), emptyUserAnswers
              .set(ItemExciseProductCodePage(0), Energy.code)
            )
            ItemsSectionItems.onlyContainsOrIsEmpty(Energy, Beer) mustBe true
          }
        }

        "when the the goods type does NOT match" - {

          "return false" in {

            implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), emptyUserAnswers
              .set(ItemExciseProductCodePage(0), Energy.code)
            )
            ItemsSectionItems.onlyContainsOrIsEmpty(Wine, Beer) mustBe false
          }
        }
      }

      "when there are multiple GoodsTypes" - {

        "when none match" - {

          "return false" in {
            implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), emptyUserAnswers
              .set(ItemExciseProductCodePage(0), Energy.code)
              .set(ItemExciseProductCodePage(1), Beer.code)
              .set(ItemExciseProductCodePage(2), Intermediate.code)
            )
            ItemsSectionItems.onlyContainsOrIsEmpty(Spirits, Wine) mustBe false
          }
        }

        "when one matches" - {

          "return false" in {
            implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), emptyUserAnswers
              .set(ItemExciseProductCodePage(0), Energy.code)
              .set(ItemExciseProductCodePage(1), Beer.code)
              .set(ItemExciseProductCodePage(2), Intermediate.code)
            )
            ItemsSectionItems.onlyContainsOrIsEmpty(Energy, Wine) mustBe false
          }
        }

        "when multiple match (but not all)" - {

          "return false" in {
            implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), emptyUserAnswers
              .set(ItemExciseProductCodePage(0), Energy.code)
              .set(ItemExciseProductCodePage(1), Beer.code)
              .set(ItemExciseProductCodePage(2), Wine.code)
            )
            ItemsSectionItems.onlyContainsOrIsEmpty(Energy, Beer) mustBe false
          }
        }

        "when multiple match (all)" - {

          "return true" in {
            implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), emptyUserAnswers
              .set(ItemExciseProductCodePage(0), Energy.code)
              .set(ItemExciseProductCodePage(1), Beer.code)
              .set(ItemExciseProductCodePage(2), Energy.code)
            )
            ItemsSectionItems.onlyContainsOrIsEmpty(Energy, Beer) mustBe true
          }
        }
      }
    }
  }

  "containsItemFromCertifiedIndependentSmallProducer" - {

    "must return true when there exists an item that's an independent small producer" in {

      implicit val request = dataRequest(FakeRequest(), emptyUserAnswers
        .set(ItemSmallIndependentProducerPage(testIndex1), ItemSmallIndependentProducerModel(NotAIndependentSmallProducer, None))
        .set(ItemSmallIndependentProducerPage(testIndex2), ItemSmallIndependentProducerModel(CertifiedIndependentSmallProducer, None))
        .set(ItemSmallIndependentProducerPage(testIndex3), ItemSmallIndependentProducerModel(NotProvided, None))
      )

      ItemsSectionItems.containsItemFromCertifiedIndependentSmallProducer mustBe true
    }

    "must return false when there DOES NOT exist an item that's an independent small producer" in {

      implicit val request = dataRequest(FakeRequest(), emptyUserAnswers
        .set(ItemSmallIndependentProducerPage(testIndex1), ItemSmallIndependentProducerModel(NotAIndependentSmallProducer, None))
        .set(ItemSmallIndependentProducerPage(testIndex2), ItemSmallIndependentProducerModel(NotAIndependentSmallProducer, None))
        .set(ItemSmallIndependentProducerPage(testIndex3), ItemSmallIndependentProducerModel(NotProvided, None))
      )

      ItemsSectionItems.containsItemFromCertifiedIndependentSmallProducer mustBe false
    }

    "must return false when no info exists" in {
      implicit val request = dataRequest(FakeRequest(), emptyUserAnswers)
      ItemsSectionItems.containsItemFromCertifiedIndependentSmallProducer mustBe false
    }
  }
}
