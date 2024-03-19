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

import config.Constants.BODYEADESAD
import models.{Index, MovementSubmissionFailure}

object IndexedSubmissionFailureHelper {

  /**
   * Takes the index of an item (zero-indexed) and then attempts to find the existence of an error at
   * the index + 1. It adds one because CORE returns errors that are 1-indexed. For example, when the error messages (from CORE) looks like this:
   * <code>
   * <pre>
   * "errorLocation" : ".../BodyEadEsad[1]/DegreePlato[1]",
   * </pre>
   * </code>
   * This method would return true if Index(0) was passed in, but would return false if Index(1) is passed in.
   *
   * @param idx Index of item (zero-indexed)
   * @param movementSubmissionFailure submission failure from CORE (error location is 1-indexed)
   * @return boolean based on whether an item submission failure exists for this item
   */
  def submissionHasItemErrorAtIndex(idx: Index, movementSubmissionFailure: MovementSubmissionFailure): Boolean =
    movementSubmissionFailure.errorLocation.exists(_.contains(s"$BODYEADESAD[${idx.position + 1}]"))
}
