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

package forms.sections.consignee

import base.SpecBase
import fixtures.MovementSubmissionFailureFixtures
import forms.behaviours.StringFieldBehaviours
import models.requests.DataRequest
import models.sections.info.movementScenario.MovementScenario.{TemporaryCertifiedConsignee, TemporaryRegisteredConsignee}
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import pages.sections.info.DestinationTypePage
import play.api.data.FormError
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest

class ConsigneeExciseFormProviderSpec extends StringFieldBehaviours with GuiceOneAppPerSuite with SpecBase with MovementSubmissionFailureFixtures {

  val fieldName = "value"
  val fixedLength = 13

  val niTemporaryRegisteredConsignee: DataRequest[AnyContentAsEmpty.type] = dataRequest(
    request = FakeRequest(),
    answers = emptyUserAnswers
      .set(DestinationTypePage, TemporaryRegisteredConsignee),
    ern = testNorthernIrelandErn
  )

  val niTemporaryCertifiedConsignee: DataRequest[AnyContentAsEmpty.type] = dataRequest(
    request = FakeRequest(),
    answers = emptyUserAnswers
      .set(DestinationTypePage, TemporaryCertifiedConsignee),
    ern = testNorthernIrelandErn
  )

  "ConsigneeExciseFormProvider" - {

    "when a value is not provided" - {
      "for a Northern Ireland Temporary Registered Consignee" - {
        "must error with the expected msg key" in {
          val form = new ConsigneeExciseFormProvider().apply()(niTemporaryRegisteredConsignee)

          val boundForm = form.bind(Map(fieldName -> ""))
          boundForm.errors.headOption mustBe Some(FormError(fieldName, "consigneeExcise.temporaryRegisteredConsignee.error.noInput", Seq()))
        }
      }
      "for a Northern Ireland Temporary Certified Consignee" - {
        "must error with the expected msg key" in {
          val form = new ConsigneeExciseFormProvider().apply()(niTemporaryCertifiedConsignee)

          val boundForm = form.bind(Map(fieldName -> ""))
          boundForm.errors.headOption mustBe Some(FormError(fieldName, "consigneeExcise.temporaryCertifiedConsignee.error.noInput", Seq()))
        }
      }
      "for any other destination type" - {
        "must error with the expected msg key" in {
          val form = new ConsigneeExciseFormProvider().apply()(dataRequest(FakeRequest()))

          val boundForm = form.bind(Map(fieldName -> ""))
          boundForm.errors.headOption mustBe Some(FormError(fieldName, "consigneeExcise.error.noInput", Seq()))
        }
      }
    }

    "when the value entered is not the correct length" - {
      "for a Northern Ireland Temporary Registered Consignee" - {
        "must error with the expected msg key" in {
          val form = new ConsigneeExciseFormProvider().apply()(niTemporaryRegisteredConsignee)

          val boundForm = form.bind(Map(fieldName -> "12345678901234"))
          boundForm.errors.headOption mustBe Some(FormError(fieldName, "consigneeExcise.temporaryRegisteredConsignee.error.length", Seq(fixedLength)))
        }
      }
      "for a Northern Ireland Temporary Certified Consignee" - {
        "must error with the expected msg key" in {
          val form = new ConsigneeExciseFormProvider().apply()(niTemporaryCertifiedConsignee)

          val boundForm = form.bind(Map(fieldName -> "12345678901234"))
          boundForm.errors.headOption mustBe Some(FormError(fieldName, "consigneeExcise.temporaryCertifiedConsignee.error.length", Seq(fixedLength)))
        }
      }
      "for any other destination type" - {
        "must error with the expected msg key" in {
          val form = new ConsigneeExciseFormProvider().apply()(dataRequest(FakeRequest()))

          val boundForm = form.bind(Map(fieldName -> "12345678901234"))
          boundForm.errors.headOption mustBe Some(FormError(fieldName, "consigneeExcise.error.length", Seq(fixedLength)))
        }
      }
    }

    "when the value contains invalid characters" - {
      "for a Northern Ireland Temporary Registered Consignee" - {
        "must error with the expected msg key" in {
          val form = new ConsigneeExciseFormProvider().apply()(niTemporaryRegisteredConsignee)

          val boundForm = form.bind(Map(fieldName -> "!@£$%^&*()_+!"))
          boundForm.errors.headOption mustBe Some(FormError(fieldName, "consigneeExcise.temporaryRegisteredConsignee.error.invalidCharacters", Seq("^(?s)(?=.*[A-Za-z0-9]).{1,}$")))
        }
      }
      "for a Northern Ireland Temporary Certified Consignee" - {
        "must error with the expected msg key" in {
          val form = new ConsigneeExciseFormProvider().apply()(niTemporaryCertifiedConsignee)

          val boundForm = form.bind(Map(fieldName -> "!@£$%^&*()_+!"))
          boundForm.errors.headOption mustBe Some(FormError(fieldName, "consigneeExcise.temporaryCertifiedConsignee.error.invalidCharacters", Seq("^(?s)(?=.*[A-Za-z0-9]).{1,}$")))
        }
      }
      "for any other destination type" - {
        "must error with the expected msg key" in {
          val form = new ConsigneeExciseFormProvider().apply()(dataRequest(FakeRequest()))

          val boundForm = form.bind(Map(fieldName -> "!@£$%^&*()_+!"))
          boundForm.errors.headOption mustBe Some(FormError(fieldName, "consigneeExcise.error.invalidCharacters", Seq("^(?s)(?=.*[A-Za-z0-9]).{1,}$")))
        }
      }
    }

    "when the value is in the incorrect format" - {
      "for a Northern Ireland Temporary Registered Consignee" - {
        "must error with the expected msg key" in {
          val form = new ConsigneeExciseFormProvider().apply()(niTemporaryRegisteredConsignee)

          val boundForm = form.bind(Map(fieldName -> "0034567890123"))
          boundForm.errors.headOption mustBe Some(FormError(fieldName, "consigneeExcise.temporaryRegisteredConsignee.error.format", Seq("[A-Z]{2}[a-zA-Z0-9]{11}")))
        }
      }
      "for a Northern Ireland Temporary Certified Consignee" - {
        "must error with the expected msg key" in {
          val form = new ConsigneeExciseFormProvider().apply()(niTemporaryCertifiedConsignee)

          val boundForm = form.bind(Map(fieldName -> "0034567890123"))
          boundForm.errors.headOption mustBe Some(FormError(fieldName, "consigneeExcise.temporaryCertifiedConsignee.error.format", Seq("[A-Z]{2}[a-zA-Z0-9]{11}")))
        }
      }
      "for any other destination type" - {
        "must error with the expected msg key" in {
          val form = new ConsigneeExciseFormProvider().apply()(dataRequest(FakeRequest()))

          val boundForm = form.bind(Map(fieldName -> "0034567890123"))
          boundForm.errors.headOption mustBe Some(FormError(fieldName, "consigneeExcise.error.format", Seq("[A-Z]{2}[a-zA-Z0-9]{11}")))
        }
      }
    }

    "when a submission failure exists and the input is the same as the previous one" - {

      val form = new ConsigneeExciseFormProvider().apply()(dataRequest(FakeRequest(),
        answers = emptyUserAnswers.copy(submissionFailures = Seq(
          consigneeExciseFailure.copy(originalAttributeValue = Some(testErn))
        ))))

      "must error with the expected msg key" in {

        val boundForm = form.bind(Map(fieldName -> testErn))
        boundForm.errors.headOption mustBe Some(FormError(fieldName, "consigneeExcise.error.submissionError", Seq()))
      }
    }
  }
}
