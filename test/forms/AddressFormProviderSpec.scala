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

package forms

import base.SpecBase
import fixtures.UserAddressFixtures
import forms.behaviours.FieldBehaviours
import models.UserAddress
import models.requests.DataRequest
import pages.Page
import pages.sections.consignee.{ConsigneeAddressPage, ConsigneeExcisePage}
import pages.sections.consignor.ConsignorAddressPage
import pages.sections.dispatch.{DispatchAddressPage, DispatchWarehouseExcisePage}
import play.api.data.FormError
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest

class AddressFormProviderSpec extends SpecBase with FieldBehaviours with UserAddressFixtures {

  case class TestParameters(fieldName: String, fieldLength: Int, isMandatory: Boolean)

  implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(), emptyUserAnswers, ern = testGreatBritainErn)

  val form = new AddressFormProvider()(ConsignorAddressPage, false)

  val testBusinessNameParameters = TestParameters(fieldName = "businessName", fieldLength = 182, isMandatory = true)
  val testPropertyParameters = TestParameters(fieldName = "property", fieldLength = 11, isMandatory = false)
  val testStreetParameters = TestParameters(fieldName = "street", fieldLength = 65, isMandatory = true)
  val testTownParameters = TestParameters(fieldName = "town", fieldLength = 50, isMandatory = true)
  val testPostcodeParameters = TestParameters(fieldName = "postcode", fieldLength = 10, isMandatory = true)

  val postcodeField = testPostcodeParameters.fieldName

  def notBTPostcodeKey(page: Page): String = s"address.postcode.error.$page.mustNotStartWithBT"

  def mustBeBTPostcodeKey(page: Page): String = s"address.postcode.error.$page.mustStartWithBT"

  def formAnswersMap(updateField: Option[String] = None,
                     updateAnswer: Option[String] = None): Map[String, String] = {

    val validAnswers: Map[String, String] = Map(
      testBusinessNameParameters.fieldName -> userAddressModelMax.businessName.value,
      testPropertyParameters.fieldName -> userAddressModelMax.property.value,
      testStreetParameters.fieldName -> userAddressModelMax.street.value,
      testTownParameters.fieldName -> userAddressModelMax.town.value,
      testPostcodeParameters.fieldName -> userAddressModelMax.postcode.value
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

  def userAddressModel(updateField: Option[String] = None,
                       updateAnswer: Option[String] = None): UserAddress =
    (updateField, updateAnswer) match {
      case (Some(testBusinessNameParameters.fieldName), answer) => userAddressModelMax.copy(businessName = answer)
      case (Some(testPropertyParameters.fieldName), answer) => userAddressModelMax.copy(property = answer)
      case (Some(testStreetParameters.fieldName), answer) => userAddressModelMax.copy(street = answer)
      case (Some(testTownParameters.fieldName), answer) => userAddressModelMax.copy(town = answer)
      case (Some(testPostcodeParameters.fieldName), answer) => userAddressModelMax.copy(postcode = answer)
      case _ => userAddressModelMax
    }

  "all fields must bind when maximum valid data is entered" in {

    val actual = form.bind(formAnswersMap())
    actual.errors mustBe Seq()
    actual.value.value mustBe userAddressModelMax
  }

  "all fields must bind when maximum valid data is entered and isConsignorPageOrUsingConsignorDetails && request.traderKnownFacts.isDefined" in {

    val actual = new AddressFormProvider()(ConsignorAddressPage, true).bind(formAnswersMap())
    actual.errors mustBe Seq()
    actual.value.value mustBe userAddressModelMax.copy(businessName = Some(testMinTraderKnownFacts.traderName))
  }

  "all fields must bind when minimum valid data is entered" in {

    val actual = form.bind(formAnswersMap(Some(testPropertyParameters.fieldName), Some("")))
    actual.errors mustBe Seq()
    actual.value.value mustBe userAddressModel(Some(testPropertyParameters.fieldName), None)
  }

  Seq(
    testBusinessNameParameters, testPropertyParameters, testStreetParameters, testTownParameters, testPostcodeParameters
  ) foreach { testParameters =>

    val TestParameters(fieldName, fieldLength, isMandatory) = testParameters

    s".$fieldName" - {

      val requiredKey = s"address.$fieldName.error${if (testParameters == testBusinessNameParameters) ".consignorAddress" else ""}.required"
      val lengthKey = s"address.$fieldName.error${if (testParameters == testBusinessNameParameters) ".consignorAddress" else ""}.length"
      val invalidKey = s"address.$fieldName.error${if (testParameters == testBusinessNameParameters) ".consignorAddress" else ""}.invalid"
      val charactersKey = s"address.$fieldName.error${if (testParameters == testBusinessNameParameters) ".consignorAddress" else ""}.character"

      if (isMandatory) {

        "must error if no value is supplied" in {

          val answer = ""

          val expectedResult = Seq(FormError(fieldName, requiredKey, Seq()))

          val actualResult = form.bind(formAnswersMap(Some(fieldName), Some(answer)))

          actualResult.errors mustBe expectedResult
        }

        "must error if no value is all empty chars supplied" in {

          val answer = "     "

          val expectedResult = Seq(FormError(fieldName, requiredKey, Seq()))

          val actualResult = form.bind(formAnswersMap(Some(fieldName), Some(answer)))

          actualResult.errors mustBe expectedResult
        }

        "must bind successfully when value is supplied" in {

          val answer = "foo"

          val expectedResult = userAddressModel(Some(fieldName), Some("foo"))

          val actualResult = form.bind(formAnswersMap(Some(fieldName), Some(answer)))

          actualResult.errors mustBe Seq()
          actualResult.get mustBe expectedResult
        }
      } else {

        "must bind successfully if no value is supplied" in {

          val answer = ""

          val expectedResult = userAddressModel(Some(fieldName), None)

          val actualResult = form.bind(formAnswersMap(Some(fieldName), Some(answer)))

          actualResult.errors mustBe Seq()
          actualResult.get mustBe expectedResult
        }

        "must bind successfully when value is supplied" in {

          val answer = "foo"

          val expectedResult = userAddressModel(Some(fieldName), Some(answer))

          val actualResult = form.bind(formAnswersMap(Some(fieldName), Some(answer)))

          actualResult.errors mustBe Seq()
          actualResult.get mustBe expectedResult
        }
      }

      "must error when value exceeds max length" in {

        val invalidLengthAnswer = "A" * fieldLength + 1

        val expectedResult = Seq(FormError(fieldName, lengthKey, Seq(fieldLength)))

        val actualResult = form.bind(formAnswersMap(Some(fieldName), Some(invalidLengthAnswer)))

        actualResult.errors mustBe expectedResult
      }

      "must bind successfully when value equals max length" in {

        val validLengthAnswer = "A" * fieldLength

        val expectedResult = userAddressModel(Some(fieldName), Some("A" * fieldLength))

        val actualResult = form.bind(formAnswersMap(Some(fieldName), Some(validLengthAnswer)))

        actualResult.errors mustBe Seq()
        actualResult.get mustBe expectedResult
      }

      "must error when value contains invalid characters" in {

        val invalidAnswer = "aa>>aa"

        val expectedResult = Seq(FormError(fieldName, invalidKey, Seq(XSS_REGEX)))

        val actualResult = form.bind(formAnswersMap(Some(fieldName), Some(invalidAnswer)))

        actualResult.errors mustBe expectedResult
      }

      if (testParameters != testBusinessNameParameters) {
        "must error when value contains only non-alphanumerics" in {

          val nonAlphanumericAnswer = "."

          val expectedResult = Seq(FormError(fieldName, charactersKey, Seq(ALPHANUMERIC_REGEX)))

          val actualResult = form.bind(formAnswersMap(Some(fieldName), Some(nonAlphanumericAnswer)))

          actualResult.errors mustBe expectedResult
        }
      }
    }
  }

  "for the ConsignorAddress Page" - {

    lazy val gbForm = new AddressFormProvider()(ConsignorAddressPage, false)(dataRequest(FakeRequest(), emptyUserAnswers, ern = testGreatBritainErn))
    lazy val xiForm = new AddressFormProvider()(ConsignorAddressPage, false)(dataRequest(FakeRequest(), emptyUserAnswers, ern = testNorthernIrelandErn))

    "must not bind for 'BT' postcodes when the user is a GB trader" in {

      val expectedResult = Seq(FormError(postcodeField, notBTPostcodeKey(ConsignorAddressPage), Seq()))

      gbForm.bind(formAnswersMap(Some(postcodeField), Some("BT1 1AA"))).errors mustBe expectedResult
    }

    "must bind for postcodes not beginning with 'BT' for a GB trader" in {

      gbForm.bind(formAnswersMap(Some(postcodeField), Some("B1 1AA"))).errors mustBe empty
    }

    "must not bind for non-BT postcodes when the user is a XI trader" in {

      val expectedResult = Seq(FormError(postcodeField, mustBeBTPostcodeKey(ConsignorAddressPage), Seq()))

      xiForm.bind(formAnswersMap(Some(postcodeField), Some("B1 1AA"))).errors mustBe expectedResult
    }

    "must bind for postcodes beginning with 'BT' for a XI trader" in {

      xiForm.bind(formAnswersMap(Some(postcodeField), Some("BT1 1AA"))).errors mustBe empty
    }
  }

  "for the ConsigneeAddress Page" - {

    lazy val gbForm = new AddressFormProvider()(ConsigneeAddressPage, false)(dataRequest(FakeRequest(), emptyUserAnswers.set(ConsigneeExcisePage, testGreatBritainErn)))
    lazy val xiForm = new AddressFormProvider()(ConsigneeAddressPage, false)(dataRequest(FakeRequest(), emptyUserAnswers.set(ConsigneeExcisePage, testNorthernIrelandErn)))

    "must not bind for 'BT' postcodes when the user is a GB trader" in {

      val expectedResult = Seq(FormError(postcodeField, notBTPostcodeKey(ConsigneeAddressPage), Seq()))

      gbForm.bind(formAnswersMap(Some(postcodeField), Some("BT1 1AA"))).errors mustBe expectedResult
    }

    "must bind for postcodes not beginning with 'BT' for a GB trader" in {

      gbForm.bind(formAnswersMap(Some(postcodeField), Some("B1 1AA"))).errors mustBe empty
    }

    "must not bind for non-BT postcodes when the user is a XI trader" in {

      val expectedResult = Seq(FormError(postcodeField, mustBeBTPostcodeKey(ConsigneeAddressPage), Seq()))

      xiForm.bind(formAnswersMap(Some(postcodeField), Some("B1 1AA"))).errors mustBe expectedResult
    }

    "must bind for postcodes beginning with 'BT' for a XI trader" in {

      xiForm.bind(formAnswersMap(Some(postcodeField), Some("BT1 1AA"))).errors mustBe empty
    }

    "when the ERN is neither starting with GB nor XI" - {
      val testErn = "FRWK123456789"

      "must allow a postcode starting with GB" in {
        val form = new AddressFormProvider()(ConsigneeAddressPage, false)(dataRequest(FakeRequest(), emptyUserAnswers, ern = testErn))
        form.bind(formAnswersMap(Some(postcodeField), Some("GB1 1AA"))).errors mustBe empty
      }

      "must allow a postcode starting with XI" in {
        val form = new AddressFormProvider()(ConsigneeAddressPage, false)(dataRequest(FakeRequest(), emptyUserAnswers, ern = testErn))
        form.bind(formAnswersMap(Some(postcodeField), Some("XI1 1AA"))).errors mustBe empty
      }
    }
  }

  "for the DispatchAddress Page" - {

    "for a GB ERN" - {

      lazy val gbForm = new AddressFormProvider()(DispatchAddressPage, false)(dataRequest(FakeRequest(),
        emptyUserAnswers.set(DispatchWarehouseExcisePage, testGreatBritainErn)
      ))

      "must not bind for 'BT' postcode" in {
        val expectedResult = Seq(FormError(postcodeField, notBTPostcodeKey(DispatchAddressPage), Seq()))
        gbForm.bind(formAnswersMap(Some(postcodeField), Some("BT1 1AA"))).errors mustBe expectedResult
      }

      "must bind for postcode not beginning with 'BT'" in {
        gbForm.bind(formAnswersMap(Some(postcodeField), Some("B1 1AA"))).errors mustBe empty
      }
    }

    "for an XI ERN" - {

      lazy val xiForm = new AddressFormProvider()(DispatchAddressPage, false)(dataRequest(FakeRequest(),
        emptyUserAnswers.set(DispatchWarehouseExcisePage, testNorthernIrelandErn)
      ))

      "must not bind for non-BT postcode" in {
        val expectedResult = Seq(FormError(postcodeField, mustBeBTPostcodeKey(DispatchAddressPage), Seq()))
        xiForm.bind(formAnswersMap(Some(postcodeField), Some("B1 1AA"))).errors mustBe expectedResult
      }

      "must bind for postcode beginning with 'BT'" in {
        xiForm.bind(formAnswersMap(Some(postcodeField), Some("BT1 1AA"))).errors mustBe empty
      }
    }

    "where the DispatchWarehouseERN is not known" - {

      lazy val unknownErnForm = new AddressFormProvider()(DispatchAddressPage, false)(dataRequest(FakeRequest(), emptyUserAnswers))

      "must bind Non-BT postcode" in {
        unknownErnForm.bind(formAnswersMap(Some(postcodeField), Some("B1 1AA"))).errors mustBe empty
      }

      "must bind BT postcode" in {
        unknownErnForm.bind(formAnswersMap(Some(postcodeField), Some("BT1 1AA"))).errors mustBe empty
      }
    }

    "when the ERN is neither starting with GB nor XI" - {
      val testErn = "FRWK123456789"

      "must allow a postcode starting with GB" in {
        val form = new AddressFormProvider()(DispatchAddressPage, false)(dataRequest(FakeRequest(), emptyUserAnswers, ern = testErn))
        form.bind(formAnswersMap(Some(postcodeField), Some("GB1 1AA"))).errors mustBe empty
      }

      "must allow a postcode starting with XI" in {
        val form = new AddressFormProvider()(DispatchAddressPage, false)(dataRequest(FakeRequest(), emptyUserAnswers, ern = testErn))
        form.bind(formAnswersMap(Some(postcodeField), Some("XI1 1AA"))).errors mustBe empty
      }
    }
  }
}
