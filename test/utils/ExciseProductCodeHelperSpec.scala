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

class ExciseProductCodeHelperSpec extends SpecBase {

  ".isSpirituousBeverages" - {
    "must return true" - {
      "when the epc is S200" in {
        ExciseProductCodeHelper.isSpirituousBeverages("S200") mustBe true
      }
    }

    "must return false" - {
      "when the epc is NOT S200" in {
        ExciseProductCodeHelper.isSpirituousBeverages("S300") mustBe false
      }
    }
  }

  ".isSpiritAndNotSpirituousBeverages" - {
    "must return true" - {

      Seq("S300", "S400", "S500", "S600").foreach { epc =>
        s"when the epc is $epc" in {
          ExciseProductCodeHelper.isSpiritAndNotSpirituousBeverages(epc) mustBe true
        }
      }
      
    }

    "must return false" - {
      "when the epc is NOT S300 / S400 / S500 / S600" in {
        ExciseProductCodeHelper.isSpiritAndNotSpirituousBeverages("S200") mustBe false
      }
    }
  }
}
