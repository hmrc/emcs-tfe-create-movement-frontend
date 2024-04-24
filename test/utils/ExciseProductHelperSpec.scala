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
        ExciseProductCodeHelper.isSpirituousBeverages("S201") mustBe false
      }
    }
  }
}
