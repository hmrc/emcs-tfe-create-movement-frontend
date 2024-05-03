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
import fixtures.messages.DraftMovementMessages.English.destination
import fixtures.messages.sections.consignee.ConsigneeExciseMessages.English
import forms.ALPHANUMERIC_REGEX
import forms.behaviours.StringFieldBehaviours
import models.sections.info.DispatchPlace.{GreatBritain, NorthernIreland}
import models.sections.info.movementScenario.MovementScenario.{EuTaxWarehouse, RegisteredConsignee, TemporaryRegisteredConsignee, UkTaxWarehouse}
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import pages.sections.info.{DestinationTypePage, DispatchPlacePage}
import play.api.data.FormError
import play.api.i18n.Messages
import play.api.test.FakeRequest

class ConsigneeExciseFormProviderSpec extends StringFieldBehaviours with GuiceOneAppPerSuite with SpecBase with MovementSubmissionFailureFixtures {

  val fieldName = "value"
  val fixedLength = 13

  implicit val msgs: Messages = messages(Seq(English.lang))

  "ConsigneeExciseFormProvider" - {

    lazy val form = new ConsigneeExciseFormProvider().apply(isNorthernIrishTemporaryRegisteredConsignee = false)(dataRequest(FakeRequest()), messages(FakeRequest()))
    lazy val dynamicForm = new ConsigneeExciseFormProvider().apply(isNorthernIrishTemporaryRegisteredConsignee = true)(dataRequest(FakeRequest()), messages(FakeRequest()))

    "when a value is not provided" - {
      "must error with the expected msg key" in {
        val boundForm = form.bind(Map(fieldName -> ""))
        boundForm.errors.headOption mustBe Some(FormError(fieldName, "consigneeExcise.error.noInput", Seq()))
      }
      "must error with the expected msg key for the dynamic form" in {
        val boundForm = dynamicForm.bind(Map(fieldName -> ""))
        boundForm.errors.headOption mustBe Some(FormError(fieldName, "consigneeExcise.temporaryConsignee.error.noInput", Seq()))
      }
    }

    "when the value contains invalid characters" - {
      "must error with the expected msg key" in {
        val boundForm = form.bind(Map(fieldName -> "!@£$%^&*()_+!"))
        boundForm.errors.headOption mustBe Some(FormError(fieldName, "consigneeExcise.error.invalidCharacters", Seq(ALPHANUMERIC_REGEX)))
      }
      "must error with the expected msg key for the dynamic form" in {
        val boundForm = dynamicForm.bind(Map(fieldName -> "!@£$%^&*()_+!"))
        boundForm.errors.headOption mustBe Some(FormError(fieldName, "consigneeExcise.temporaryConsignee.error.invalidCharacters", Seq(ALPHANUMERIC_REGEX)))
      }
    }

    "for the ERN form" - {
      behave like fieldWithERN(
        form = form,
        fieldName = fieldName,
        formatError = FormError(fieldName, "consigneeExcise.error.format", Seq("[A-Z]{2}[a-zA-Z0-9]{11}"))
      )

      behave like fieldWithFixedLength(
        form,
        fieldName,
        lengthError = FormError(fieldName, "consigneeExcise.error.length", Seq(fixedLength)),
        fixedLength
      )
    }

    "for the temporary authorisation reference form" - {
      behave like fieldWithERN(
        form = dynamicForm,
        fieldName = fieldName,
        formatError = FormError(fieldName, "consigneeExcise.temporaryConsignee.error.format", Seq("[A-Z]{2}[a-zA-Z0-9]{11}"))
      )

      behave like fieldWithFixedLength(
        dynamicForm,
        fieldName,
        lengthError = FormError(fieldName, "consigneeExcise.temporaryConsignee.error.length", Seq(fixedLength)),
        fixedLength
      )
    }

    "when a submission failure exists and the input is the same as the previous one" - {

      val form = new ConsigneeExciseFormProvider().apply(false)(dataRequest(FakeRequest(),
        answers = emptyUserAnswers.copy(submissionFailures = Seq(
          consigneeExciseFailure.copy(originalAttributeValue = Some(testErn))
        ))), messages(FakeRequest()))

      "must error with the expected msg key" in {

        val boundForm = form.bind(Map(fieldName -> testErn))
        boundForm.errors.headOption mustBe Some(FormError(fieldName, "consigneeExcise.error.submissionError", Seq()))
      }
    }

    "when applying RIM validations" - {

      // ERN must not start with XI or GB because the movement destination is a {0}
      "rim rule 1" - {

        "must fail validation" - {
          Seq(EuTaxWarehouse, RegisteredConsignee, TemporaryRegisteredConsignee).foreach { destination =>

            s"when the destination type is ${destination.stringValue}" - {
              Seq("GB", "XI").foreach { ernPrefix =>

                s"and the consignee ERN starts with `$ernPrefix`" in {
                  val form = new ConsigneeExciseFormProvider().apply(false)(dataRequest(FakeRequest(),
                    answers = emptyUserAnswers
                      .set(DestinationTypePage, destination)
                  ), messages(FakeRequest()))

                  val boundForm = form.bind(Map(fieldName -> s"${ernPrefix}12345678912"))
                  boundForm.errors.headOption mustBe Some(
                    FormError(fieldName, s"ERN must not start with XI or GB because the movement destination is a ${destination.stringValue}", Seq("(?!(GB|XI)).*"))
                  )
                }
              }
            }
          }
        }

      }

      // ERN must start with GB or XI followed by 11 numbers or mixed letters and numbers
      "rim rule 2" - {

        "must pass validation" - {

          Seq(GreatBritain, NorthernIreland).foreach { dispatch =>

            s"when the dispatch place is $dispatch" - {

              Seq("GB", "XI").foreach { ernPrefix =>

                s"and the consignee ERN starts with `$ernPrefix`" in {
                  val form = new ConsigneeExciseFormProvider().apply(false)(dataRequest(FakeRequest(),
                    answers = emptyUserAnswers
                      .set(DispatchPlacePage, dispatch)
                  ), messages(FakeRequest()))

                  val boundForm = form.bind(Map(fieldName -> s"${ernPrefix}12345678912"))
                  boundForm.hasErrors mustBe false
                }
              }
            }
          }

        }

        "must fail validation" - {

          Seq(GreatBritain, NorthernIreland).foreach { dispatch =>

            s"when the dispatch place is $dispatch" - {

              "and the consignee ERN does not start with either GB or XI" in {
                val form = new ConsigneeExciseFormProvider().apply(false)(dataRequest(FakeRequest(),
                  answers = emptyUserAnswers
                    .set(DispatchPlacePage, dispatch)
                ), messages(FakeRequest()))

                val boundForm = form.bind(Map(fieldName -> s"FR12345678912"))
                boundForm.errors.headOption mustBe Some(
                  FormError(fieldName, s"consigneeExcise.error.rimRule2", Seq("(GB|XI).*"))
                )
              }

            }

          }

        }

      }

      // ERN must start with GB or XI followed by 11 numbers or mixed letters and numbers
      "rim rule 3" - {
        val consignorErn = "GBRC123456789"

        "must pass validation" - {
          "when the consignor ERN starts with GBRC" - {
            Seq("GB", "XI").foreach { ernPrefix =>
              s"and the consignee ERN value entered starts with $ernPrefix" in {
                val form = new ConsigneeExciseFormProvider().apply(false)(
                  dataRequest(
                    request = FakeRequest(),
                    answers = emptyUserAnswers.copy(ern = consignorErn),
                    ern = consignorErn
                  ),
                  messages(FakeRequest()))

                val boundForm = form.bind(Map(fieldName -> s"${ernPrefix}12345678912"))
                boundForm.hasErrors mustBe false
              }
            }
          }
        }

        "must fail validation" - {
          "when the consignor ERN starts with GBRC" - {
            "and the consignee ERN value starts with FR" in {
              val form = new ConsigneeExciseFormProvider().apply(false)(
                dataRequest(
                  request = FakeRequest(),
                  answers = emptyUserAnswers.copy(ern = consignorErn),
                  ern = consignorErn
                ),
                messages(FakeRequest()))

              val boundForm = form.bind(Map(fieldName -> "FR12345678912"))
              boundForm.errors.headOption mustBe Some(
                FormError(fieldName, s"consigneeExcise.error.rimRule3", Seq("(GB|XI).*"))
              )
            }
          }
        }
      }

      // ERN must start with GBWK or XIWK because the destination is a UK tax warehouse
      "rim rule 4" - {

        "must pass validation" - {
          s"when the destination type is ${UkTaxWarehouse.GB.stringValue}" - {
            Seq("GBWK", "XIWK").foreach { ernPrefix =>

              s"and the consignee ERN starts with `$ernPrefix`" in {
                val form = new ConsigneeExciseFormProvider().apply(false)(dataRequest(FakeRequest(),
                  answers = emptyUserAnswers
                    .set(DestinationTypePage, UkTaxWarehouse.GB)
                ), messages(FakeRequest()))

                val boundForm = form.bind(Map(fieldName -> s"${ernPrefix}123456789"))
                boundForm.hasErrors mustBe false
              }
            }
          }
        }

        "must fail validation" - {
          s"when the destination type is ${UkTaxWarehouse.GB.stringValue}" - {
            "and the consignee ERN does not start with GBWK or XIWK" in {
              val form = new ConsigneeExciseFormProvider().apply(false)(dataRequest(FakeRequest(),
                answers = emptyUserAnswers
                  .set(DestinationTypePage, UkTaxWarehouse.GB)
              ), messages(FakeRequest()))

              val boundForm = form.bind(Map(fieldName -> s"FRWK123456789"))
              boundForm.errors.headOption mustBe Some(
                FormError(fieldName, s"consigneeExcise.error.rimRule4", Seq("(GBWK|XIWK).*"))
              )
            }
          }
        }
      }

      // ERN must not start with GBWK or XIWK because the destination is not a UK tax warehouse
      "rim rule 5" - {

        "must pass validation" - {
          s"when the destination type is not a ${UkTaxWarehouse.GB.stringValue}" - {
            Seq("FRWK").foreach { ernPrefix =>
              s"and the consignee ERN starts with `$ernPrefix`" in {
                val form = new ConsigneeExciseFormProvider().apply(false)(dataRequest(FakeRequest(),
                  answers = emptyUserAnswers
                    .set(DestinationTypePage, UkTaxWarehouse.NI)
                ), messages(FakeRequest()))

                val boundForm = form.bind(Map(fieldName -> s"${ernPrefix}123456789"))
                boundForm.hasErrors mustBe false
              }
            }
          }
        }

        "must fail validation" - {
          s"when the destination type is not a ${UkTaxWarehouse.GB.stringValue}" - {
            Seq("GBWK", "XIWK").foreach { ernPrefix =>

              s"and the consignee ERN starts with `$ernPrefix`" in {
                val form = new ConsigneeExciseFormProvider().apply(false)(dataRequest(FakeRequest(),
                  answers = emptyUserAnswers
                    .set(DestinationTypePage, UkTaxWarehouse.NI)
                ), messages(FakeRequest()))

                val boundForm = form.bind(Map(fieldName -> s"${ernPrefix}123456789"))
                boundForm.errors.headOption mustBe Some(
                  FormError(fieldName, s"consigneeExcise.error.rimRule5", Seq("(?!(GBWK|XIWK)).*"))
                )
              }
            }
          }
        }

      }

      // If ERN starts with XI it must be followed by the capital letters WK, and 9 numbers
      "rim rule 6" - {

      }
    }
  }
}
