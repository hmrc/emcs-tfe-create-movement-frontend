/*
 * Copyright 2024 HM Revenue & Customs
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

package utils

import base.SpecBase
import fixtures.MovementSubmissionFailureFixtures

class IndexedSubmissionFailureHelperSpec extends SpecBase with MovementSubmissionFailureFixtures {

  ".submissionHasErrorAtIndex" - {

    "should return true" - {

      "when an item submission failure exists at the specified (display) index" in {

        IndexedSubmissionFailureHelper
          .submissionHasItemErrorAtIndex(
            testIndex1,
            itemQuantityFailure(1)
          ) mustBe true
      }
    }

    "should return false" - {

      "when an item submission failure does not at the specified (display) index" in {

        IndexedSubmissionFailureHelper
          .submissionHasItemErrorAtIndex(
            testIndex2,
            itemQuantityFailure(1)
          ) mustBe false
      }

      "when an non-indexed item error exists" in {

        IndexedSubmissionFailureHelper.submissionHasItemErrorAtIndex(testIndex1, movementSubmissionFailure) mustBe false
      }
    }
  }
}
