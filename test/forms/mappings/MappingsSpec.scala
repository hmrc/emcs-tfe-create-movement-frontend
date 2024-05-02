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

package forms.mappings

import models.Enumerable
import org.scalatest.OptionValues
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import play.api.data.Forms.single
import play.api.data.{Form, FormError}

object MappingsSpec {

  sealed trait Foo

  case object Bar extends Foo

  case object Baz extends Foo

  object Foo {

    val values: Set[Foo] = Set(Bar, Baz)

    implicit val fooEnumerable: Enumerable[Foo] =
      Enumerable(values.toSeq.map(v => v.toString -> v): _*)
  }
}

class MappingsSpec extends AnyFreeSpec with Matchers with OptionValues with Mappings {

  import MappingsSpec._

  "text" - {

    val testForm: Form[String] =
      Form(
        "value" -> text()
      )

    "must bind a valid string" in {
      val result = testForm.bind(Map("value" -> "foobar"))
      result.get mustEqual "foobar"
    }

    "must not bind an empty string" in {
      val result = testForm.bind(Map("value" -> ""))
      result.errors must contain(FormError("value", "error.required"))
    }

    "must not bind a string of whitespace only" in {
      val result = testForm.bind(Map("value" -> " \t"))
      result.errors must contain(FormError("value", "error.required"))
    }

    "must not bind an empty map" in {
      val result = testForm.bind(Map.empty[String, String])
      result.errors must contain(FormError("value", "error.required"))
    }

    "must return a custom error message" in {
      val form = Form("value" -> text("custom.error"))
      val result = form.bind(Map("value" -> ""))
      result.errors must contain(FormError("value", "custom.error"))
    }

    "must unbind a valid value" in {
      val result = testForm.fill("foobar")
      result.apply("value").value.value mustEqual "foobar"
    }
  }

  "normalisedSpaceText" - {

    val testForm: Form[String] =
      Form(
        "value" -> normalisedSpaceText()
      )

    "must bind a valid string" in {
      val result = testForm.bind(Map("value" -> "foobar"))
      result.get mustEqual "foobar"
    }

    "must bind a valid string with multiple spaces" in {
      val result = testForm.bind(Map("value" -> "foo      bar"))
      result.get mustEqual "foo bar"
    }

    "must bind a valid string with \\n" in {
      val result = testForm.bind(Map("value" ->"foo\n\n\nbar".stripMargin
      ))
      result.get mustEqual "foo bar"
    }

    "must bind a valid string \\r" in {
      val result = testForm.bind(Map("value" ->"foo\r\r\rbar".stripMargin
      ))
      result.get mustEqual "foo bar"
    }

    "must bind a valid string with multiple spaces, \\n and \\r" in {
      val result = testForm.bind(Map("value" -> "foo   \n   \r   bar".stripMargin
      ))
      result.get mustEqual "foo bar"
    }

    "must not bind an empty string" in {
      val result = testForm.bind(Map("value" -> ""))
      result.errors must contain(FormError("value", "error.required"))
    }

    "must not bind a string of whitespace only" in {
      val result = testForm.bind(Map("value" -> " \t"))
      result.errors must contain(FormError("value", "error.required"))
    }

    "must not bind an empty map" in {
      val result = testForm.bind(Map.empty[String, String])
      result.errors must contain(FormError("value", "error.required"))
    }

    "must return a custom error message" in {
      val form = Form("value" -> text("custom.error"))
      val result = form.bind(Map("value" -> ""))
      result.errors must contain(FormError("value", "custom.error"))
    }

    "must unbind a valid value" in {
      val result = testForm.fill("foobar")
      result.apply("value").value.value mustEqual "foobar"
    }
  }

  "boolean" - {

    val testForm: Form[Boolean] =
      Form(
        "value" -> boolean()
      )

    "must bind true" in {
      val result = testForm.bind(Map("value" -> "true"))
      result.get mustEqual true
    }

    "must bind false" in {
      val result = testForm.bind(Map("value" -> "false"))
      result.get mustEqual false
    }

    "must not bind a non-boolean" in {
      val result = testForm.bind(Map("value" -> "not a boolean"))
      result.errors must contain(FormError("value", "error.boolean"))
    }

    "must not bind an empty value" in {
      val result = testForm.bind(Map("value" -> ""))
      result.errors must contain(FormError("value", "error.required"))
    }

    "must not bind an empty map" in {
      val result = testForm.bind(Map.empty[String, String])
      result.errors must contain(FormError("value", "error.required"))
    }

    "must unbind" in {
      val result = testForm.fill(true)
      result.apply("value").value.value mustEqual "true"
    }
  }

  "int" - {

    val testForm: Form[Int] =
      Form(
        "value" -> int()
      )

    "must bind a valid integer" in {
      val result = testForm.bind(Map("value" -> "1"))
      result.get mustEqual 1
    }

    "must bind a valid integer (with spaces)" in {
      val result = testForm.bind(Map("value" -> " 1        1             2  "))
      result.get mustEqual 112
    }

    "must not bind an empty value" in {
      val result = testForm.bind(Map("value" -> ""))
      result.errors must contain(FormError("value", "error.required"))
    }

    "must not bind an empty map" in {
      val result = testForm.bind(Map.empty[String, String])
      result.errors must contain(FormError("value", "error.required"))
    }

    "must not bind a decimal" in {
      val result = testForm.bind(Map("value" -> "1.1"))
      result.errors must contain(FormError("value", "error.wholeNumber"))
    }

    "must unbind a valid value" in {
      val result = testForm.fill(123)
      result.apply("value").value.value mustEqual "123"
    }
  }

  "bigInt" - {

    val testForm: Form[BigInt] =
      Form(
        "value" -> bigInt()
      )

    "must bind a valid integer" in {
      val result = testForm.bind(Map("value" -> "1"))
      result.get mustEqual 1
    }

    "must bind a valid integer (with spaces)" in {
      val result = testForm.bind(Map("value" -> " 1        1             2  "))
      result.get mustEqual 112
    }

    s"must bind an integer larger than ${Int.MaxValue}" in {
      val result = testForm.bind(Map("value" -> s"${Int.MaxValue + 1}"))
      result.get mustEqual Int.MaxValue + 1
    }

    s"must bind an integer smaller than ${Int.MinValue}" in {
      val result = testForm.bind(Map("value" -> s"${Int.MinValue - 1}"))
      result.get mustEqual Int.MinValue - 1
    }

    "must not bind an empty value" in {
      val result = testForm.bind(Map("value" -> ""))
      result.errors must contain(FormError("value", "error.required"))
    }

    "must not bind an empty map" in {
      val result = testForm.bind(Map.empty[String, String])
      result.errors must contain(FormError("value", "error.required"))
    }

    "must not bind a decimal" in {
      val result = testForm.bind(Map("value" -> "1.1"))
      result.errors must contain(FormError("value", "error.wholeNumber"))
    }

    "must unbind a valid value" in {
      val result = testForm.fill(123)
      result.apply("value").value.value mustEqual "123"
    }
  }

  "enumerable" - {

    val testForm = Form(
      "value" -> enumerable[Foo]()
    )

    "must bind a valid option" in {
      val result = testForm.bind(Map("value" -> "Bar"))
      result.get mustEqual Bar
    }

    "must not bind an invalid option" in {
      val result = testForm.bind(Map("value" -> "Not Bar"))
      result.errors must contain(FormError("value", "error.invalid"))
    }

    "must not bind an empty map" in {
      val result = testForm.bind(Map.empty[String, String])
      result.errors must contain(FormError("value", "error.required"))
    }
  }

  "mandatoryIfOptionSelectedAndInputNonEmpty" - {

    "must run the provided validation" - {

      "when the option field has been selected and the specified input field has a value" in {

        mandatoryIfOptionSelectedAndInputNonEmpty(
          optionField = "option1",
          optionValue = Bar.toString,
          inputField = "input1",
          mapping = single("input1" -> text().verifying(maxLength(1, "error.length")))
        ).bind(
          Map("option1" -> Bar.toString, "input1" -> "non empty")
        ).swap.toOption.get mustBe Seq(FormError("input1", List("error.length"), Seq(1)))
      }
    }

    "must not run the provided validation" - {

      "when the option field is not selected" in {

        mandatoryIfOptionSelectedAndInputNonEmpty(
          optionField = "option1",
          optionValue = Bar.toString,
          inputField = "input1",
          mapping = single("input1" -> text().verifying(maxLength(1, "error.length")))
        ).bind(
          Map("option2" -> Bar.toString, "input1" -> "non empty")
        ).isRight mustBe true
      }

      "when the input field is empty" in {

        mandatoryIfOptionSelectedAndInputNonEmpty(
          optionField = "option1",
          optionValue = Bar.toString,
          inputField = "input1",
          mapping = single("input1" -> text().verifying(maxLength(1, "error.length")))
        ).bind(
          Map("option1" -> Bar.toString, "input1" -> "")
        ).isRight mustBe true
      }
    }
  }

  "normaliseSpacesAndControlCharacters" - {

    "must replace a string with multiple spaces" in {
      normaliseSpacesAndControlCharacters("foo      bar") mustBe "foo bar"
    }

    "must replace a string with \\n" in {
      normaliseSpacesAndControlCharacters("foo\n\n\nbar") mustBe "foo bar"
    }

    "must replace a string with \\r" in {
      normaliseSpacesAndControlCharacters("foo\r\r\rbar") mustBe "foo bar"
    }

    "must replace string with multiple spaces, \\n and \\r" in {
      normaliseSpacesAndControlCharacters("foo   \n   \r   bar") mustBe "foo bar"
    }
  }

  "isOptionSelected" - {

    val optionField = "optionField"

    val optionValue = "optionValue"

    "must return true" - {

      "when the option has been selected" in {

        isOptionSelected(optionField, optionValue).apply(Map(optionField -> optionValue)) mustBe true
      }
    }

    "must return false" - {

      "when the option has not been selected" in {

        isOptionSelected(optionField, "blah").apply(Map(optionField -> optionValue)) mustBe false
      }

      "when the option value doesn't exist in the form data" in {

        isOptionSelected(optionField, optionValue).apply(Map.empty) mustBe false
      }
    }
  }
}
