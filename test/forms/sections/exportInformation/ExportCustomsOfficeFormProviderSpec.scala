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

package forms.sections.exportInformation

import base.SpecBase
import fixtures.MovementSubmissionFailureFixtures
import fixtures.messages.sections.exportInformation.ExportCustomsOfficeMessages
import forms.behaviours.StringFieldBehaviours
import forms.{CUSTOMS_OFFICE_CODE_REGEX, XSS_REGEX}
import models.UserAnswers
import models.sections.info.DispatchPlace.{GreatBritain, NorthernIreland}
import pages.sections.info.DispatchPlacePage
import play.api.data.FormError
import play.api.i18n.Messages
import play.api.test.FakeRequest
import utils.ExportCustomsOfficeNumberError

class ExportCustomsOfficeFormProviderSpec extends SpecBase with StringFieldBehaviours with MovementSubmissionFailureFixtures {

  val requiredKey = "exportCustomsOffice.error.required"
  val lengthKey = "exportCustomsOffice.error.length"
  val xssKey = "exportCustomsOffice.error.invalidCharacter"
  val regexKey = "exportCustomsOffice.error.customOfficeRegex"
  val mustStartWithGBKey = "exportCustomsOffice.error.mustStartWithGB"
  val mustNotStartWithGBAsDispatchedFromNorthernIrelandKey = "exportCustomsOffice.error.mustNotStartWithGBAsDispatchedFromNorthernIreland"
  val mustNotStartWithGBAsNorthernIrelandRegisteredConsignorKey = "exportCustomsOffice.error.mustNotStartWithGBAsNorthernIrelandRegisteredConsignor"
  val requiredLength = 8

  class Test(consignorErn: String = testErn, userAnswers: UserAnswers = emptyUserAnswers) {
    val form = new ExportCustomsOfficeFormProvider()()(
      dataRequest(
        request = FakeRequest(),
        ern = consignorErn,
        answers = userAnswers.copy(ern = consignorErn)
      )
    )
  }

  ".value" - {

    val fieldName = "value"

    behave like mandatoryField(
      new ExportCustomsOfficeFormProvider()()(dataRequest(FakeRequest())),
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )

    behave like fieldWithFixedLength(
      new ExportCustomsOfficeFormProvider()()(dataRequest(FakeRequest())),
      fieldName,
      lengthError = FormError(fieldName, lengthKey, Seq(requiredLength)),
      requiredLength = requiredLength
    )

    "not bind a value that contains XSS chars" in new Test {

      val boundForm = form.bind(Map(fieldName -> "<1234567"))
      boundForm.errors mustBe Seq(FormError(fieldName, xssKey, Seq(XSS_REGEX)))
    }

    "not bind a value that doesn't start with two alpha chars" in new Test {

      val boundForm = form.bind(Map(fieldName -> "12345678"))
      boundForm.errors mustBe Seq(FormError(fieldName, regexKey, Seq(CUSTOMS_OFFICE_CODE_REGEX)))
    }

    "bind a value that meets the expected regex" in new Test(testGreatBritainErn) {

      val boundForm = form.bind(Map(fieldName -> "GB345678"))
      boundForm.errors mustBe Seq()
      boundForm.value mustBe Some("GB345678")
    }

    "when a submission failure exists and the input is the same as the previous one" - {

      val form = new ExportCustomsOfficeFormProvider()()(dataRequest(FakeRequest(),
        answers = emptyUserAnswers.copy(submissionFailures = Seq(
          movementSubmissionFailure.copy(errorType = ExportCustomsOfficeNumberError.code, hasBeenFixed = false, originalAttributeValue = Some(testExportCustomsOffice))
        ))))

      "must error with the expected msg key" in {

        val boundForm = form.bind(Map(fieldName -> testExportCustomsOffice))
        boundForm.errors.headOption mustBe Some(FormError(fieldName, "errors.704.exportOffice.input", Seq()))
      }
    }

    "when the consignor ERN starts with GB" - {
      "binds a valid value starting with GB" in new Test(testGreatBritainErn) {
        val boundForm = form.bind(Map(fieldName -> "GB123456"))
        boundForm.errors mustBe Seq()
        boundForm.value mustBe Some("GB123456")
      }

      "not bind a value if it does not start with GB" in new Test(testGreatBritainErn) {
        val boundForm = form.bind(Map(fieldName -> "XI123456"))
        boundForm.errors mustBe Seq(FormError(fieldName, mustStartWithGBKey))
      }
    }

    "when the consignor ERN starts with XI" - {
      "and dispatched place is Great Britain" - {
        val userAnswers = emptyUserAnswers.set(DispatchPlacePage, GreatBritain)

        "binds a valid value starting with GB" in new Test(testNorthernIrelandErn, userAnswers) {
          val boundForm = form.bind(Map(fieldName -> "GB123456"))
          boundForm.errors mustBe Seq()
          boundForm.value mustBe Some("GB123456")
        }

        "not bind a value that doesn't start with GB" in new Test(testNorthernIrelandErn, userAnswers) {
          val boundForm = form.bind(Map(fieldName -> "XI123456"))
          boundForm.errors mustBe Seq(FormError(fieldName, mustStartWithGBKey))
        }
      }

      "and dispatched place is Northern Ireland" - {
        val userAnswers = emptyUserAnswers.set(DispatchPlacePage, NorthernIreland)

        "binds a valid value starting with XI" in new Test(testNorthernIrelandErn, userAnswers) {
          val boundForm = form.bind(Map(fieldName -> "XI123456"))
          boundForm.errors mustBe Seq()
          boundForm.value mustBe Some("XI123456")
        }

        "not bind a value if it starts with GB" in new Test(testNorthernIrelandErn, userAnswers) {
          val boundForm = form.bind(Map(fieldName -> "GB123456"))
          boundForm.errors mustBe Seq(FormError(fieldName, mustNotStartWithGBAsDispatchedFromNorthernIrelandKey))
        }
      }
    }

    "when the consignor is a Northern Ireland Registered Consignor" - {
      "binds a valid value starting with XI" in new Test(testNIRegisteredConsignorErn) {
        val boundForm = form.bind(Map(fieldName -> "XI123456"))
        boundForm.errors mustBe Seq()
        boundForm.value mustBe Some("XI123456")
      }

      "not bind a value if it starts with GB" in new Test(testNIRegisteredConsignorErn) {
        val boundForm = form.bind(Map(fieldName -> "GB123456"))
        boundForm.errors mustBe Seq(FormError(fieldName, mustNotStartWithGBAsNorthernIrelandRegisteredConsignorKey))
      }
    }
  }

  "Error Messages" - {

    Seq(ExportCustomsOfficeMessages.English) foreach { messagesForLanguage =>

      implicit val msgs: Messages = messages(Seq(messagesForLanguage.lang))

      s"when output for language code '${messagesForLanguage.lang.code}'" - {

        "have the correct required error message" in {

          msgs("exportCustomsOffice.error.required") mustBe
            messagesForLanguage.errorRequired
        }

        "have the correct length error message" in {

          msgs("exportCustomsOffice.error.length", requiredLength) mustBe
            messagesForLanguage.errorLength(requiredLength)
        }

        "have the correct invalidCharacter error message" in {

          msgs("exportCustomsOffice.error.invalidCharacter") mustBe
            messagesForLanguage.errorInvalidCharacter
        }

        "have the correct customsOfficeRegex error message" in {

          msgs("exportCustomsOffice.error.customOfficeRegex") mustBe
            messagesForLanguage.errorCustomOfficeRegex
        }

        "have the correct error message for a submission failure" in {
          msgs("errors.704.exportOffice.input") mustBe
            messagesForLanguage.submissionFailureErrorInput
        }

        "have the correct must start with GB error message" in {
          msgs(mustStartWithGBKey) mustBe messagesForLanguage.errorMustStartWithGB
        }

        "have the correct must not start with GB as dispatched from Northern Ireland error message" in {
          msgs(mustNotStartWithGBAsDispatchedFromNorthernIrelandKey) mustBe messagesForLanguage.errorMustNotStartWithGBAsDispatchedFromNorthernIreland
        }

        "have the correct must not start with GB error message as Northern Ireland Registsred Consignor" in {
          msgs(mustNotStartWithGBAsNorthernIrelandRegisteredConsignorKey) mustBe messagesForLanguage.errorMustNotStartWithGBAsNorthernIrelandRegisteredConsignor
        }
      }
    }
  }
}
