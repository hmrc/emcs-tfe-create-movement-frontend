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

package pages.sections.items

import base.SpecBase
import fixtures.MovementSubmissionFailureFixtures

class ItemQuantityPageSpec extends SpecBase with MovementSubmissionFailureFixtures {

  val page: ItemQuantityPage = ItemQuantityPage(testIndex2)

  "should have the correct page path" in {
    page.path mustBe ItemsSectionItem(testIndex2).path \ "itemQuantity"
  }

}
