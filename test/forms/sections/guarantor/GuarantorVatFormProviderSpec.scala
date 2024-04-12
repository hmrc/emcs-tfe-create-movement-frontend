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

package forms.sections.guarantor

import fixtures.BaseFixtures
import forms.ONLY_ALPHANUMERIC_REGEX
import forms.behaviours.StringFieldBehaviours
import models.VatNumberModel
import models.sections.guarantor.GuarantorArranger
import models.sections.guarantor.GuarantorArranger.{GoodsOwner, Transporter}
import play.api.data.FormError

class GuarantorVatFormProviderSpec extends StringFieldBehaviours with BaseFixtures {

  val lengthKey = "guarantorVat.error.input.length"
  val inputRequiredKey = "guarantorVat.error.input.required"
  val alphanumericKey = "guarantorVat.error.input.alphanumeric"
  val maxLength = 14

  class Test(guarantorArranger: GuarantorArranger = GoodsOwner) {
    val radioRequiredKey: String = s"guarantorVat.$guarantorArranger.error.radio.required"
    val form = new GuarantorVatFormProvider()(guarantorArranger)
  }

  "when binding 'Yes'" - {

    "when VAT number is not provided" - {

      "must error when binding the form" in new Test() {

        val boundForm = form.bind(Map(GuarantorVatFormProvider.hasVatNumberField -> "true"))

        boundForm.errors mustBe Seq(FormError(GuarantorVatFormProvider.vatNumberField, inputRequiredKey, Seq()))
      }
    }

    "when VAT number contains invalid characters" - {

      "must error when binding the form" in new Test() {

        val boundForm = form.bind(Map(
          GuarantorVatFormProvider.hasVatNumberField -> "true",
          GuarantorVatFormProvider.vatNumberField -> "<"
        ))

        boundForm.errors mustBe Seq(FormError(GuarantorVatFormProvider.vatNumberField, alphanumericKey, Seq(ONLY_ALPHANUMERIC_REGEX)))
      }
    }

    "when VAT number is too long" - {

      "must error when binding the form" in new Test() {

        val boundForm = form.bind(Map(
          GuarantorVatFormProvider.hasVatNumberField -> "true",
          GuarantorVatFormProvider.vatNumberField -> "a" * (maxLength + 1)
        ))

        boundForm.errors mustBe Seq(FormError(GuarantorVatFormProvider.vatNumberField, lengthKey, Seq(maxLength)))
      }
    }

    "when VAT number is valid" - {

      "must bind the form successfully when true with value (spaces exist but trim them out)" in new Test() {

        val boundForm = form.bind(Map(
          GuarantorVatFormProvider.hasVatNumberField -> "true",
          GuarantorVatFormProvider.vatNumberField -> "GB123 456-178"
        ))

        boundForm.value mustBe Some(VatNumberModel(hasVatNumber = true, Some("GB123456178")))
      }

      "must bind the form successfully when true with value" in new Test() {

        val boundForm = form.bind(Map(
          GuarantorVatFormProvider.hasVatNumberField -> "true",
          GuarantorVatFormProvider.vatNumberField -> testVatNumber
        ))

        boundForm.value mustBe Some(VatNumberModel(hasVatNumber = true, Some(testVatNumber)))
      }
    }
  }

  "when binding 'No'" - {

    "must bind the form successfully when false with value (should be transformed to None on bind)" in new Test() {

      val boundForm = form.bind(Map(
        GuarantorVatFormProvider.hasVatNumberField -> "false",
        GuarantorVatFormProvider.vatNumberField -> "brand"
      ))

      boundForm.value mustBe Some(VatNumberModel(hasVatNumber = false, None))
    }

    "must bind the form successfully when false with NO value" in new Test() {

      val boundForm = form.bind(Map(GuarantorVatFormProvider.hasVatNumberField -> "false"))

      boundForm.value mustBe Some(VatNumberModel(hasVatNumber = false, None))
    }
  }


  Seq(Transporter, GoodsOwner).foreach { arrangerType =>

    s"when an option hasn't been selected for arranger type: $arrangerType" - {

      "must error with correct error message when binding the form" in new Test(arrangerType) {

        val boundForm = form.bind(Map(GuarantorVatFormProvider.hasVatNumberField -> ""))

        boundForm.errors mustBe Seq(FormError(GuarantorVatFormProvider.hasVatNumberField, radioRequiredKey, Seq()))
      }
    }
  }
}
