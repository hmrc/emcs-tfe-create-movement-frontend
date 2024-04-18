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
import uk.gov.voa.play.form.{ConditionalMapping, MandatoryOptionalMapping}

import java.time.{LocalDate, LocalTime}

trait Mappings extends Formatters with Constraints {

  protected def text(errorKey: String = "error.required", args: Seq[String] = Seq.empty): FieldMapping[String] =
    of(stringFormatter(errorKey, args))

  protected def normalisedSpaceText(errorKey: String = "error.required", args: Seq[String] = Seq.empty) =
    text(errorKey, args)
      .transform[String](
        _.replace("\n", " ")
          .replace("\r", " ")
          .replaceAll(" +", " ")
          .trim,
        identity
      )

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
                           invalidKey: String,
                           allRequiredKey: String,
                           twoRequiredKey: String,
                           requiredKey: String,
                           args: Seq[String] = Seq.empty): FieldMapping[LocalDate] =
    of(new LocalDateFormatter(invalidKey, allRequiredKey, twoRequiredKey, requiredKey, args))


  protected def localTime(
                           invalidKey: String,
                           requiredKey: String,
                           args: Seq[String] = Seq.empty): FieldMapping[LocalTime] =
    of(new LocalTimeFormatter(invalidKey, requiredKey, args))

  /**
   * Document
   *
   * @param optionField
   * @param inputField
   * @param optionValue
   * @param mapping
   * @tparam T
   * @return
   */
  def mandatoryIfOptionSelectedAndInputNonEmpty[T](optionField: String,
                                                   inputField: String,
                                                   optionValue: String,
                                                   mapping: Mapping[T]
                                                  ): Mapping[Option[T]] =
    ConditionalMapping(
      formMapping => formMapping.get(optionField).contains(optionValue) && formMapping.get(inputField).exists(_.nonEmpty),
      MandatoryOptionalMapping(mapping, Nil),
      None,
      Seq.empty
    )
}
