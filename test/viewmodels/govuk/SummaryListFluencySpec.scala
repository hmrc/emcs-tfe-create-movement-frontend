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

package viewmodels.govuk

import base.SpecBase
import uk.gov.hmrc.govukfrontend.views.Aliases.{Empty, Text}
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.HtmlContent

class SummaryListFluencySpec extends SpecBase {

  "ValueViewModel" - {
    ".apply" - {
      "must remove any escape characters and sanitise the input string" - {
        "when the content is Text" in {
          summarylist.ValueViewModel.apply(Text("This is a &lsquo;test', it's a \'good\' 'test&rsquo; and it \"will\" be 'tested'")).content mustBe Text("This is a ‘test’, it’s a ‘good’ ‘test’ and it \"will\" be ‘tested’")
        }
      }

      "must not do anything when the content is HtmlContent" in {
        summarylist.ValueViewModel.apply(HtmlContent("This is a &lsquo;test', it's a \'good\' 'test&rsquo; and it \"will\" be 'tested'")).content mustBe HtmlContent("This is a &lsquo;test', it's a \'good\' 'test&rsquo; and it \"will\" be 'tested'")
      }

      "must not do anything when the content is Empty" in {
        summarylist.ValueViewModel.apply(Empty).content mustBe Empty
      }
    }
  }
}
