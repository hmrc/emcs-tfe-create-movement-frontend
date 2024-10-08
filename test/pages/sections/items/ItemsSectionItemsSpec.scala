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
import models.GoodsType._
import models.requests.DataRequest
import models.sections.items.ItemSmallIndependentProducerModel
import models.sections.items.ItemSmallIndependentProducerType.{CertifiedIndependentSmallProducer, NotApplicable, NotProvided}
import play.api.test.FakeRequest
import viewmodels.taskList.NotStarted

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

    "should return NotStarted" - {

      "when there are no items" in {

        ItemsSectionItems.status(dataRequest(FakeRequest(), emptyUserAnswers)) mustBe NotStarted
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
        .set(ItemSmallIndependentProducerPage(testIndex1), ItemSmallIndependentProducerModel(NotApplicable, None))
        .set(ItemSmallIndependentProducerPage(testIndex2), ItemSmallIndependentProducerModel(CertifiedIndependentSmallProducer, None))
        .set(ItemSmallIndependentProducerPage(testIndex3), ItemSmallIndependentProducerModel(NotProvided, None))
      )

      ItemsSectionItems.containsItemFromCertifiedIndependentSmallProducer mustBe true
    }

    "must return false when there DOES NOT exist an item that's an independent small producer" in {

      implicit val request = dataRequest(FakeRequest(), emptyUserAnswers
        .set(ItemSmallIndependentProducerPage(testIndex1), ItemSmallIndependentProducerModel(NotApplicable, None))
        .set(ItemSmallIndependentProducerPage(testIndex2), ItemSmallIndependentProducerModel(NotApplicable, None))
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
