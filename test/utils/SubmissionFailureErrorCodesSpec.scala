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
import utils.SubmissionFailureErrorCodes.{ErrorCode, ItemDegreesPlatoError, ItemQuantityError}

class SubmissionFailureErrorCodesSpec extends SpecBase {

  "ErrorCode.apply" - {

    "should return ItemQuantityError for error code: 4407" - {
      Seq(true, false).foreach { isForAddToList =>

        s"when isForAddToList = $isForAddToList" in {
          ErrorCode.apply("4407", testIndex1, isForAddToList) mustBe ItemQuantityError(testIndex1, isForAddToList)
        }
      }
    }

    "should return ItemDegreesPlatoError for error code: 4445" - {

      Seq(true, false).foreach { isForAddToList =>

        s"when isForAddToList = $isForAddToList" in {
          ErrorCode.apply("4445", testIndex1, isForAddToList) mustBe ItemDegreesPlatoError(testIndex1, isForAddToList)
        }
      }
    }
  }
}
