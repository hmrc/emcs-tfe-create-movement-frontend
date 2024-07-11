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

package models.sections.items

import base.SpecBase
import models.sections.info.DispatchPlace
import models.sections.info.movementScenario.MovementScenario
import models.sections.items.ExciseProductCodeRules.{GBNoGuarantorRules, NINoGuarantorRules, UnknownDestinationRules}
import pages.sections.guarantor.GuarantorRequiredPage
import pages.sections.info.{DestinationTypePage, DispatchPlacePage}
import play.api.test.FakeRequest

class ExciseProductCodeRulesSpec extends SpecBase {

  "GBNoGuarantorRules" - {
    ".shouldDisplayInset" - {
      "must return true" - {
        "when GuarantorRequiredPage is false and isGreatBritainErn is true" in {
          Seq("GBWK123", "GBRC123").foreach { ern =>
            val request = dataRequest(FakeRequest(), ern = ern, answers = emptyUserAnswers.set(GuarantorRequiredPage, false))
            GBNoGuarantorRules.shouldDisplayInset()(request) mustBe true
          }
        }
      }

      "must return false" - {
        "when GuarantorRequiredPage is true and isGreatBritainErn is true" in {
          Seq("GBWK123", "GBRC123").foreach { ern =>
            val request = dataRequest(FakeRequest(), ern = ern, answers = emptyUserAnswers.set(GuarantorRequiredPage, true))
            GBNoGuarantorRules.shouldDisplayInset()(request) mustBe false
          }
        }
        "when isGreatBritainErn is false" in {
          Seq("XIWK123", "XIRC123").foreach { ern =>
            val request = dataRequest(FakeRequest(), ern = ern, answers = emptyUserAnswers.set(GuarantorRequiredPage, false))
            GBNoGuarantorRules.shouldDisplayInset()(request) mustBe false
          }
        }
        "when GuarantorRequredPage is missing" in {
          Seq("GBWK123", "GBRC123").foreach { ern =>
            val request = dataRequest(FakeRequest(), ern = ern)
            GBNoGuarantorRules.shouldDisplayInset()(request) mustBe false
          }
        }
      }
    }

    ".shouldResetGuarantorSectionOnSubmission" - {
      "must return true" - {
        "when shouldDisplayInset is true and exciseProductCode is not B000, W200 or W300" in {
          Seq("B001", "W201", "W301").foreach { exciseProductCode =>
            val request = dataRequest(FakeRequest(), ern = "GBWK123", answers = emptyUserAnswers.set(GuarantorRequiredPage, false))
            GBNoGuarantorRules.shouldResetGuarantorSectionOnSubmission(exciseProductCode)(request) mustBe true
          }
        }
      }

      "must return false" - {
        "when shouldDisplayInset is false" in {
          val request = dataRequest(FakeRequest(), ern = "GBWK123", answers = emptyUserAnswers.set(GuarantorRequiredPage, false))
          GBNoGuarantorRules.shouldResetGuarantorSectionOnSubmission("B000")(request) mustBe false
        }
        "when exciseProductCode is B000" in {
          val request = dataRequest(FakeRequest(), ern = "GBWK123", answers = emptyUserAnswers.set(GuarantorRequiredPage, false))
          GBNoGuarantorRules.shouldResetGuarantorSectionOnSubmission("B000")(request) mustBe false
        }
        "when exciseProductCode is W200" in {
          val request = dataRequest(FakeRequest(), ern = "GBWK123", answers = emptyUserAnswers.set(GuarantorRequiredPage, false))
          GBNoGuarantorRules.shouldResetGuarantorSectionOnSubmission("W200")(request) mustBe false
        }
        "when exciseProductCode is W300" in {
          val request = dataRequest(FakeRequest(), ern = "GBWK123", answers = emptyUserAnswers.set(GuarantorRequiredPage, false))
          GBNoGuarantorRules.shouldResetGuarantorSectionOnSubmission("W300")(request) mustBe false
        }
        "when shouldDisplayInset is true" in {
          val request = dataRequest(FakeRequest(), ern = "GBWK123", answers = emptyUserAnswers.set(GuarantorRequiredPage, true))
          GBNoGuarantorRules.shouldResetGuarantorSectionOnSubmission("B000")(request) mustBe false
        }
      }
    }
  }

  "NINoGuarantorRules" - {
    ".shouldDisplayInset" - {
      "must return true" - {
        "when GuarantorRequiredPage is false and DispatchPlacePage is NorthernIreland and isNorthernIrelandErn is true" in {
          Seq("XIWK123", "XIRC123").foreach { ern =>
            val request = dataRequest(FakeRequest(), ern = ern, answers = emptyUserAnswers.set(GuarantorRequiredPage, false).set(DispatchPlacePage, DispatchPlace.NorthernIreland))
            NINoGuarantorRules.shouldDisplayInset()(request) mustBe true
          }
        }
      }

      "must return false" - {
        "when GuarantorRequiredPage is true and DispatchPlacePage is NorthernIreland and isNorthernIrelandErn is true" in {
          Seq("XIWK123", "XIRC123").foreach { ern =>
            val request = dataRequest(FakeRequest(), ern = ern, answers = emptyUserAnswers.set(GuarantorRequiredPage, true).set(DispatchPlacePage, DispatchPlace.NorthernIreland))
            NINoGuarantorRules.shouldDisplayInset()(request) mustBe false
          }
        }
        "when DispatchPlacePage is not NorthernIreland" in {
          Seq("XIWK123", "XIRC123").foreach { ern =>
            val request = dataRequest(FakeRequest(), ern = ern, answers = emptyUserAnswers.set(GuarantorRequiredPage, false).set(DispatchPlacePage, DispatchPlace.GreatBritain))
            NINoGuarantorRules.shouldDisplayInset()(request) mustBe false
          }
        }
        "when DispatchPlacePage is missing" in {
          Seq("XIWK123", "XIRC123").foreach { ern =>
            val request = dataRequest(FakeRequest(), ern = ern, answers = emptyUserAnswers.set(GuarantorRequiredPage, false))
            NINoGuarantorRules.shouldDisplayInset()(request) mustBe false
          }
        }
        "when GuarantorRequiredPage is missing" in {
          Seq("XIWK123", "XIRC123").foreach { ern =>
            val request = dataRequest(FakeRequest(), ern = ern, answers = emptyUserAnswers.set(DispatchPlacePage, DispatchPlace.NorthernIreland))
            NINoGuarantorRules.shouldDisplayInset()(request) mustBe false
          }
        }
        "when isNorthernIrelandErn is false" in {
          Seq("GBWK123", "GBRC123").foreach { ern =>
            val request = dataRequest(FakeRequest(), ern = ern, answers = emptyUserAnswers.set(GuarantorRequiredPage, false).set(DispatchPlacePage, DispatchPlace.NorthernIreland))
            NINoGuarantorRules.shouldDisplayInset()(request) mustBe false
          }
        }
      }
    }

    ".shouldResetGuarantorSectionOnSubmission" - {
      "must return true" - {
        "when shouldDisplayInset is true and exciseProductCode doesn't start with E" in {
          val request = dataRequest(FakeRequest(), ern = "XIWK123", answers = emptyUserAnswers.set(GuarantorRequiredPage, false).set(DispatchPlacePage, DispatchPlace.NorthernIreland))
          NINoGuarantorRules.shouldResetGuarantorSectionOnSubmission("B123")(request) mustBe true
        }
      }

      "must return false" - {
        "when shouldDisplayInset is false" in {
          val request = dataRequest(FakeRequest(), ern = "XIWK123")
          NINoGuarantorRules.shouldResetGuarantorSectionOnSubmission("B123")(request) mustBe false
        }
        "when exciseProductCode starts with E" in {
          val request = dataRequest(FakeRequest(), ern = "XIWK123", answers = emptyUserAnswers.set(GuarantorRequiredPage, false).set(DispatchPlacePage, DispatchPlace.NorthernIreland))
          NINoGuarantorRules.shouldResetGuarantorSectionOnSubmission("E123")(request) mustBe false
        }
      }
    }
  }

  "UnknownDestinationRules" - {
    ".shouldDisplayInset" - {
      "must return true" - {
        "when DestinationTypePage is UnknownDestination" in {
          val request = dataRequest(FakeRequest(), answers = emptyUserAnswers.set(DestinationTypePage, MovementScenario.UnknownDestination))
          UnknownDestinationRules.shouldDisplayInset()(request) mustBe true
        }
      }

      "must return false" - {
        "when DestinationTypePage is not UnknownDestination" in {
          MovementScenario.values.filterNot(_ == MovementScenario.UnknownDestination).foreach { scenario =>
            val request = dataRequest(FakeRequest(), answers = emptyUserAnswers.set(DestinationTypePage, scenario))
            UnknownDestinationRules.shouldDisplayInset()(request) mustBe false
          }
        }
        "when DestinationTypePage is missing" in {
          val request = dataRequest(FakeRequest())
          UnknownDestinationRules.shouldDisplayInset()(request) mustBe false
        }
      }
    }

    ".shouldResetGuarantorSectionOnSubmission" - {
      "must return false" in {
        val request = dataRequest(FakeRequest())
        UnknownDestinationRules.shouldResetGuarantorSectionOnSubmission("B123")(request) mustBe false
      }
    }
  }
}