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
import fixtures.{BaseFixtures, ItemFixtures, MovementSubmissionFailureFixtures}
import forms.behaviours.StringFieldBehaviours
import play.api.data.FormError
import play.api.test.FakeRequest
import utils._

class ItemExciseProductCodeFormProviderSpec extends SpecBase
  with StringFieldBehaviours
  with BaseFixtures
  with ItemFixtures
  with MovementSubmissionFailureFixtures {

  val requiredKey = "itemExciseProductCode.error.required"
  val maxLength = 100

  val form = new ItemExciseProductCodeFormProvider().apply(Seq(beerExciseProductCode), testIndex1)(dataRequest(FakeRequest()))

  ".value" - {

    val fieldName = "excise-product-code"

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      "B000"
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )

    "not bind when the value provided is not in the excise product code list" in {
      val result = form.bind(Map(fieldName -> "B001")).apply(fieldName)
      result.errors mustEqual Seq(FormError(fieldName, requiredKey, Seq()))
    }

    "when a submission failure exists and the input is the same as the previous one" - {

      val form = new ItemExciseProductCodeFormProvider()(Seq(beerExciseProductCode), testIndex2)(dataRequest(FakeRequest(),
        answers = emptyUserAnswers.copy(submissionFailures = Seq(
          itemExciseProductCodeFailure(ItemExciseProductCodeConsignorNotApprovedToSendError(testIndex1, isForAddToList = false), itemIndex = 2),
          itemExciseProductCodeFailure(ItemExciseProductCodeConsigneeNotApprovedToReceiveError(testIndex1, isForAddToList = false), itemIndex = 2),
          itemExciseProductCodeFailure(ItemExciseProductCodeDestinationNotApprovedToReceiveError(testIndex1, isForAddToList = false), itemIndex = 2),
          itemExciseProductCodeFailure(ItemExciseProductCodeDispatchPlaceNotAllowedError(testIndex1, isForAddToList = false), itemIndex = 2)
        ).map(_.copy(originalAttributeValue = Some("B000"))))
      ))

      "must error with the expected msg key" in {

        val result = form.bind(Map(fieldName -> "B000")).apply(fieldName)
        result.errors.headOption mustBe Some(FormError(fieldName, "itemExciseProductCode.error.submissionFailure", Seq()))
      }
    }
  }
}
