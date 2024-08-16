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

package forms.sections.destination

import base.SpecBase
import fixtures.UserAddressFixtures
import forms.behaviours.FieldBehaviours
import forms.{ALPHANUMERIC_REGEX, XSS_REGEX}
import models.UserAddress
import models.requests.DataRequest
import models.sections.info.movementScenario.MovementScenario
import pages.Page
import pages.sections.destination.{DestinationAddressPage, DestinationWarehouseExcisePage}
import pages.sections.info.DestinationTypePage
import play.api.data.FormError
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest

// scalastyle:off magic.number
class DestinationAddressFormProviderSpec extends SpecBase with FieldBehaviours with UserAddressFixtures {

  case class TestParameters(fieldName: String, fieldLength: Int, isMandatory: Boolean)

  def testBusinessNameParameters(isDirectDelivery: Boolean): TestParameters =
    TestParameters(fieldName = "businessName", fieldLength = 182, isMandatory = !isDirectDelivery)

  val testPropertyParameters: TestParameters =
    TestParameters(fieldName = "property", fieldLength = 11, isMandatory = false)

  def testStreetParameters(isDirectDelivery: Boolean): TestParameters =
    TestParameters(fieldName = "street", fieldLength = 65, isMandatory = isDirectDelivery)

  def testTownParameters(isDirectDelivery: Boolean): TestParameters =
    TestParameters(fieldName = "town", fieldLength = 50, isMandatory = isDirectDelivery)

  def testPostcodeParameters(isDirectDelivery: Boolean): TestParameters =
    TestParameters(fieldName = "postcode", fieldLength = 10, isMandatory = isDirectDelivery)

  def postcodeField(isDirectDelivery: Boolean): String = testPostcodeParameters(isDirectDelivery).fieldName

  def notBTPostcodeKey(page: Page): String = s"address.postcode.error.$page.mustNotStartWithBT"

  def mustBeBTPostcodeKey(page: Page): String = s"address.postcode.error.$page.mustStartWithBT"

  def formAnswersMap(
                      isDirectDelivery: Boolean,
                      updateField: Option[String] = None,
                      updateAnswer: Option[String] = None
                    ): Map[String, String] = {

    val validAnswers: Map[String, String] = Map(
      testBusinessNameParameters(isDirectDelivery).fieldName -> userAddressModelMax.businessName.value,
      testPropertyParameters.fieldName -> userAddressModelMax.property.value,
      testStreetParameters(isDirectDelivery).fieldName -> userAddressModelMax.street.value,
      testTownParameters(isDirectDelivery).fieldName -> userAddressModelMax.town.value,
      testPostcodeParameters(isDirectDelivery).fieldName -> userAddressModelMax.postcode.value
    )

    def update(fieldName: String, newAnswer: String): Map[String, String] = validAnswers.map {
      case (`fieldName`, _) => (fieldName, newAnswer)
      case fieldNameAndAnswer => fieldNameAndAnswer
    }

    (updateField, updateAnswer) match {
      case (Some(field), Some(answer)) => update(field, answer)
      case _ => validAnswers
    }
  }

  def userAddressModel(
                        isDirectDelivery: Boolean,
                        updateField: Option[String] = None,
                        updateAnswer: Option[String] = None
                      ): UserAddress =
    (updateField, updateAnswer) match {
      case (Some(fieldName), answer) if fieldName == testBusinessNameParameters(isDirectDelivery).fieldName => userAddressModelMax.copy(businessName = answer)
      case (Some(testPropertyParameters.fieldName), answer) => userAddressModelMax.copy(property = answer)
      case (Some(fieldName), answer) if fieldName == testStreetParameters(isDirectDelivery).fieldName => userAddressModelMax.copy(street = answer)
      case (Some(fieldName), answer) if fieldName == testTownParameters(isDirectDelivery).fieldName => userAddressModelMax.copy(town = answer)
      case (Some(fieldName), answer) if fieldName == testPostcodeParameters(isDirectDelivery).fieldName => userAddressModelMax.copy(postcode = answer)
      case _ => userAddressModelMax
    }

  Seq(true, false).foreach { isDirectDelivery =>

    implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(
      FakeRequest(),
      emptyUserAnswers.set(DestinationTypePage, if(isDirectDelivery) MovementScenario.DirectDelivery else MovementScenario.UkTaxWarehouse.GB),
      ern = testGreatBritainErn
    )

    val form = new DestinationAddressFormProvider()()

    s"when isDirectDelivery = $isDirectDelivery" - {

      "all fields must bind when maximum valid data is entered" in {

        val actual = form.bind(formAnswersMap(isDirectDelivery))
        actual.errors mustBe Seq()
        actual.value.value mustBe userAddressModelMax
      }

      "all fields must bind when minimum valid data is entered" in {

        val actual = form.bind(formAnswersMap(isDirectDelivery, Some(testPropertyParameters.fieldName), Some("")))
        actual.errors mustBe Seq()
        actual.value.value mustBe userAddressModel(isDirectDelivery, Some(testPropertyParameters.fieldName), None)
      }

      Seq(
        testBusinessNameParameters(isDirectDelivery),
        testPropertyParameters,
        testStreetParameters(isDirectDelivery),
        testTownParameters(isDirectDelivery),
        testPostcodeParameters(isDirectDelivery)
      ) foreach { testParameters =>

        val TestParameters(fieldName, fieldLength, isMandatory) = testParameters

        s".$fieldName" - {

          val requiredKey = s"address.$fieldName.error${if (testParameters == testBusinessNameParameters(isDirectDelivery)) ".destinationAddress" else ""}.required"
          val lengthKey = s"address.$fieldName.error${if (testParameters == testBusinessNameParameters(isDirectDelivery)) ".destinationAddress" else ""}.length"
          val invalidKey = s"address.$fieldName.error${if (testParameters == testBusinessNameParameters(isDirectDelivery)) ".destinationAddress" else ""}.invalid"
          val charactersKey = s"address.$fieldName.error${if (testParameters == testBusinessNameParameters(isDirectDelivery)) ".destinationAddress" else ""}.character"

          if (isMandatory) {

            "must error if no value is supplied" in {

              val answer = ""

              val expectedResult = Seq(FormError(fieldName, requiredKey, Seq()))

              val actualResult = form.bind(formAnswersMap(isDirectDelivery, Some(fieldName), Some(answer)))

              actualResult.errors mustBe expectedResult
            }

            "must error if no value is all empty chars supplied" in {

              val answer = "     "

              val expectedResult = Seq(FormError(fieldName, requiredKey, Seq()))

              val actualResult = form.bind(formAnswersMap(isDirectDelivery, Some(fieldName), Some(answer)))

              actualResult.errors mustBe expectedResult
            }

            "must bind successfully when value is supplied" in {

              val answer = "foo"

              val expectedResult = userAddressModel(isDirectDelivery, Some(fieldName), Some("foo"))

              val actualResult = form.bind(formAnswersMap(isDirectDelivery, Some(fieldName), Some(answer)))

              actualResult.errors mustBe Seq()
              actualResult.get mustBe expectedResult
            }
          } else {

            "must bind successfully if no value is supplied" in {

              val answer = ""

              val expectedResult = userAddressModel(isDirectDelivery, Some(fieldName), None)

              val actualResult = form.bind(formAnswersMap(isDirectDelivery, Some(fieldName), Some(answer)))

              actualResult.errors mustBe Seq()
              actualResult.get mustBe expectedResult
            }

            "must bind successfully when value is supplied" in {

              val answer = "foo"

              val expectedResult = userAddressModel(isDirectDelivery, Some(fieldName), Some(answer))

              val actualResult = form.bind(formAnswersMap(isDirectDelivery, Some(fieldName), Some(answer)))

              actualResult.errors mustBe Seq()
              actualResult.get mustBe expectedResult
            }
          }

          "must error when value exceeds max length" in {

            val invalidLengthAnswer = "A" * fieldLength + 1

            val expectedResult = Seq(FormError(fieldName, lengthKey, Seq(fieldLength)))

            val actualResult = form.bind(formAnswersMap(isDirectDelivery, Some(fieldName), Some(invalidLengthAnswer)))

            actualResult.errors mustBe expectedResult
          }

          "must bind successfully when value equals max length" in {

            val validLengthAnswer = "A" * fieldLength

            val expectedResult = userAddressModel(isDirectDelivery, Some(fieldName), Some("A" * fieldLength))

            val actualResult = form.bind(formAnswersMap(isDirectDelivery, Some(fieldName), Some(validLengthAnswer)))

            actualResult.errors mustBe Seq()
            actualResult.get mustBe expectedResult
          }

          "must error when value contains invalid characters" in {

            val invalidAnswer = "aa>>aa"

            val expectedResult = Seq(FormError(fieldName, invalidKey, Seq(XSS_REGEX)))

            val actualResult = form.bind(formAnswersMap(isDirectDelivery, Some(fieldName), Some(invalidAnswer)))

            actualResult.errors mustBe expectedResult
          }

          if (testParameters != testBusinessNameParameters(isDirectDelivery)) {
            "must error when value contains only non-alphanumerics" in {

              val nonAlphanumericAnswer = "."

              val expectedResult = Seq(FormError(fieldName, charactersKey, Seq(ALPHANUMERIC_REGEX)))

              val actualResult = form.bind(formAnswersMap(isDirectDelivery, Some(fieldName), Some(nonAlphanumericAnswer)))

              actualResult.errors mustBe expectedResult
            }
          }
        }
      }

      "for a GB ERN" - {

        lazy val gbForm = new DestinationAddressFormProvider()()(dataRequest(FakeRequest(),
          emptyUserAnswers.set(DestinationWarehouseExcisePage, testGreatBritainErn)
        ))

        "must not bind for 'BT' postcode" in {
          val expectedResult = Seq(FormError(postcodeField(isDirectDelivery), notBTPostcodeKey(DestinationAddressPage), Seq()))
          gbForm.bind(formAnswersMap(isDirectDelivery, Some(postcodeField(isDirectDelivery)), Some("BT1 1AA"))).errors mustBe expectedResult
        }

        "must bind for postcode not beginning with 'BT'" in {
          gbForm.bind(formAnswersMap(isDirectDelivery, Some(postcodeField(isDirectDelivery)), Some("B1 1AA"))).errors mustBe empty
        }
      }

      "for an XI ERN" - {

        lazy val xiForm = new DestinationAddressFormProvider()()(dataRequest(FakeRequest(),
          emptyUserAnswers.set(DestinationWarehouseExcisePage, testNorthernIrelandErn)
        ))

        "must not bind for non-BT postcode" in {
          val expectedResult = Seq(FormError(postcodeField(isDirectDelivery), mustBeBTPostcodeKey(DestinationAddressPage), Seq()))
          xiForm.bind(formAnswersMap(isDirectDelivery, Some(postcodeField(isDirectDelivery)), Some("B1 1AA"))).errors mustBe expectedResult
        }

        "must bind for postcode beginning with 'BT'" in {
          xiForm.bind(formAnswersMap(isDirectDelivery, Some(postcodeField(isDirectDelivery)), Some("BT1 1AA"))).errors mustBe empty
        }
      }

      "where the DispatchWarehouseERN is not known" - {

        lazy val unknownErnForm = new DestinationAddressFormProvider()()(dataRequest(FakeRequest(), emptyUserAnswers))

        "must bind Non-BT postcode" in {
          unknownErnForm.bind(formAnswersMap(isDirectDelivery, Some(postcodeField(isDirectDelivery)), Some("B1 1AA"))).errors mustBe empty
        }

        "must bind BT postcode" in {
          unknownErnForm.bind(formAnswersMap(isDirectDelivery, Some(postcodeField(isDirectDelivery)), Some("BT1 1AA"))).errors mustBe empty
        }
      }

      "when the ERN is neither starting with GB nor XI" - {
        val testErn = "FRWK123456789"

        "must allow a postcode starting with GB" in {
          val form = new DestinationAddressFormProvider()()(dataRequest(FakeRequest(), emptyUserAnswers, ern = testErn))
          form.bind(formAnswersMap(isDirectDelivery, Some(postcodeField(isDirectDelivery)), Some("GB1 1AA"))).errors mustBe empty
        }

        "must allow a postcode starting with XI" in {
          val form = new DestinationAddressFormProvider()()(dataRequest(FakeRequest(), emptyUserAnswers, ern = testErn))
          form.bind(formAnswersMap(isDirectDelivery, Some(postcodeField(isDirectDelivery)), Some("XI1 1AA"))).errors mustBe empty
        }
      }
    }
  }
}
