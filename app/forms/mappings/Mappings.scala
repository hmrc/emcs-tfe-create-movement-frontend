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
import play.api.data.{FieldMapping, Mapping}
import play.api.data.Forms.of
import uk.gov.voa.play.form.{Condition, ConditionalMapping, MandatoryOptionalMapping}

import java.time.{LocalDate, LocalTime}

trait Mappings extends Formatters with Constraints {

  protected def text(errorKey: String = "error.required", args: Seq[String] = Seq.empty): FieldMapping[String] =
    of(stringFormatter(errorKey, args))

  protected def normalisedSpaceText(errorKey: String = "error.required", args: Seq[String] = Seq.empty): Mapping[String] =
    text(errorKey, args).transform[String](normaliseSpacesAndControlCharacters, identity)

  protected def normaliseSpacesAndControlCharacters(raw: String): String = {
    raw.replace("\n", " ")
      .replace("\r", " ")
      .replaceAll(" +", " ")
      .trim
  }
  protected def int(requiredKey: String = "error.required",
                    wholeNumberKey: String = "error.wholeNumber",
                    nonNumericKey: String = "error.nonNumeric",
                    args: Seq[String] = Seq.empty): FieldMapping[Int] =
    of(intFormatter(requiredKey, wholeNumberKey, nonNumericKey, args))

  protected def bigInt(requiredKey: String = "error.required",
                       wholeNumberKey: String = "error.wholeNumber",
                       nonNumericKey: String = "error.nonNumeric",
                       args: Seq[String] = Seq.empty): FieldMapping[BigInt] =
    of(bigIntFormatter(requiredKey, wholeNumberKey, nonNumericKey, args))

  protected def bigDecimal(requiredKey: String = "error.required",
                           nonNumericKey: String = "error.nonNumeric",
                           args: Seq[String] = Seq.empty): FieldMapping[BigDecimal] =
    of(bigDecimalFormatter(requiredKey, nonNumericKey, args))

  protected def boolean(requiredKey: String = "error.required",
                        invalidKey: String = "error.boolean",
                        args: Seq[String] = Seq.empty): FieldMapping[Boolean] =
    of(booleanFormatter(requiredKey, invalidKey, args))


  protected def enumerable[A](requiredKey: String = "error.required",
                              invalidKey: String = "error.invalid",
                              args: Seq[String] = Seq.empty)(implicit ev: Enumerable[A]): FieldMapping[A] =
    of(enumerableFormatter[A](requiredKey, invalidKey, args))

  protected def localDate(
                           allRequiredKey: String,
                           oneRequiredKey: String,
                           twoRequiredKey: String,
                           oneInvalidKey: String,
                           twoInvalidKey: String,
                           notARealDateKey: String,
                           args: Seq[String] = Seq.empty): FieldMapping[LocalDate] =
    of(new LocalDateFormatter(allRequiredKey, oneRequiredKey, twoRequiredKey, oneInvalidKey, twoInvalidKey, notARealDateKey, args))


  protected def localTime(
                           invalidKey: String,
                           requiredKey: String,
                           args: Seq[String] = Seq.empty): FieldMapping[LocalTime] =
    of(new LocalTimeFormatter(invalidKey, requiredKey, args))

  /**
   * Useful for pages where the user has a series of options and within each option is an
   * optional field. This method first makes sure that the relevant option has been selected
   * (based on the `optionValue` parameter) and if the user has entered a value in the input field
   * then apply the supplied validation (or `mapping`)
   *
   * @param optionField The name of the option field
   * @param inputField The name of the input field
   * @param optionValue If the option field has a value, what that value should be in order to apply validation
   * @param mapping The validation to apply if this predicate holds true
   * @tparam T the data type that this field is
   * @return The mapping that should be applied based on the arguments and their values within the form
   */
  protected def mandatoryIfOptionSelectedAndInputNonEmpty[T](optionField: String,
                                                   optionValue: String,
                                                   inputField: String,
                                                   mapping: Mapping[T]
                                                  ): Mapping[Option[T]] =
    ConditionalMapping(
      formMapping => formMapping.get(optionField).contains(optionValue) && formMapping.get(inputField).exists(_.nonEmpty),
      MandatoryOptionalMapping(mapping, Nil),
      None,
      Seq.empty
    )

  protected def isOptionSelected(field: String, option: String): Condition =
    _.get(field).contains(option)
}
