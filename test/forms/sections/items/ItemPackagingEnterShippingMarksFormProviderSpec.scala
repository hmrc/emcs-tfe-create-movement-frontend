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

package forms.sections.items

import base.SpecBase
import fixtures.ItemFixtures
import fixtures.messages.sections.items.ItemPackagingEnterShippingMarksMessages
import fixtures.messages.sections.items.ItemPackagingEnterShippingMarksMessages.English.errorShippingMarkNotUnique
import forms.behaviours.StringFieldBehaviours
import models.requests.DataRequest
import models.response.referenceData.ItemPackaging
import pages.sections.items.{ItemExciseProductCodePage, ItemPackagingQuantityPage, ItemPackagingShippingMarksPage, ItemSelectPackagingPage}
import play.api.data.FormError
import play.api.i18n.Messages
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest

class ItemPackagingEnterShippingMarksFormProviderSpec extends SpecBase with StringFieldBehaviours with ItemFixtures {

  implicit val msgs: Messages = messages(Seq(ItemPackagingEnterShippingMarksMessages.English.lang))

  val requiredKey = "itemPackagingEnterShippingMarks.error.required"
  val lengthKey = "itemPackagingEnterShippingMarks.error.length"
  val notUniqueKey = "itemPackagingEnterShippingMarks.error.not.unique"
  val maxLength = 999

  implicit val dr: DataRequest[AnyContentAsEmpty.type] = dataRequest(
    FakeRequest(),
    emptyUserAnswers
      .set(ItemExciseProductCodePage(testIndex1), testEpcWine)
      .set(ItemSelectPackagingPage(testIndex1, testPackagingIndex1), ItemPackaging("BO", "Box"))
      .set(ItemPackagingShippingMarksPage(testIndex1, testPackagingIndex1), "mark 1")
      .set(ItemPackagingShippingMarksPage(testIndex1, testPackagingIndex2), "mark 2")
      .set(ItemPackagingShippingMarksPage(testIndex1, testPackagingIndex3), "mark 3")
      .set(ItemExciseProductCodePage(testIndex2), testEpcWine)
      .set(ItemSelectPackagingPage(testIndex2, testPackagingIndex1), ItemPackaging("BO", "Box"))
      .set(ItemPackagingShippingMarksPage(testIndex2, testPackagingIndex1), "mark 4")
      .set(ItemPackagingShippingMarksPage(testIndex2, testPackagingIndex2), "mark 5")
      .set(ItemPackagingShippingMarksPage(testIndex2, testPackagingIndex3), "mark 6")
  )

  val form = new ItemPackagingEnterShippingMarksFormProvider()(testIndex2, testPackagingIndex2)

  ".value" - {

    val fieldName = "value"

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      "0" * maxLength
    )

    behave like fieldWithMaxLength(
      form,
      fieldName,
      maxLength = maxLength,
      lengthError = FormError(fieldName, lengthKey, Seq(maxLength))
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )

    "display an error when the shipping mark value has been used elsewhere" in {
      form.bind(Map(fieldName -> "mark 2")).errors must contain(FormError(fieldName, errorShippingMarkNotUnique(testPackagingIndex2)))
    }

    "not display an error when the shipping mark value is the same as the one we are modifying" in {
      form.bind(Map(fieldName -> "mark 5")).hasErrors mustBe false
    }

    "not display an error when the shipping mark value is changed to something not used before" in {
      form.bind(Map(fieldName -> "mark 5555555555555555")).hasErrors mustBe false
    }

    "not display an error when a shipping mark value is added and there are no other shipping marks present" in {
      val noOtherShippingMarksDataRequest = dataRequest(
        FakeRequest(),
        emptyUserAnswers
          .set(ItemExciseProductCodePage(testIndex1), testEpcWine)
          .set(ItemSelectPackagingPage(testIndex1, testPackagingIndex1), ItemPackaging("BO", "Box"))
          .set(ItemExciseProductCodePage(testIndex2), testEpcWine)
          .set(ItemSelectPackagingPage(testIndex2, testPackagingIndex1), ItemPackaging("BO", "Box"))
      )

      new ItemPackagingEnterShippingMarksFormProvider()(testIndex2, testPackagingIndex1)(noOtherShippingMarksDataRequest, msgs)
        .bind(Map(fieldName -> "shipping mark 1")).hasErrors mustBe false
    }

    "not display an error when a shipping mark value is not changed from it's current value" in {
      val noOtherShippingMarksDataRequest = dataRequest(
        FakeRequest(),
        emptyUserAnswers
          .set(ItemExciseProductCodePage(testIndex1), testEpcWine)

          .set(ItemSelectPackagingPage(testIndex1, testPackagingIndex1), ItemPackaging("BO", "Box"))
          .set(ItemPackagingQuantityPage(testIndex1, testPackagingIndex1), "0")
          .set(ItemPackagingShippingMarksPage(testIndex1, testPackagingIndex1), "mark 1")

          .set(ItemSelectPackagingPage(testIndex1, testPackagingIndex2), ItemPackaging("BO", "Box"))
          .set(ItemPackagingQuantityPage(testIndex1, testPackagingIndex2), "10")
          .set(ItemPackagingShippingMarksPage(testIndex1, testPackagingIndex2), "mark 1")
      )

      new ItemPackagingEnterShippingMarksFormProvider()(testIndex1, testPackagingIndex1)(noOtherShippingMarksDataRequest, msgs)
        .bind(Map(fieldName -> "mark 1")).hasErrors mustBe false
    }

  }

}
