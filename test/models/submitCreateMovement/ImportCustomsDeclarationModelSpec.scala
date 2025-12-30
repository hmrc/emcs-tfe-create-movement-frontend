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

package models.submitCreateMovement

import base.SpecBase
import models.requests.DataRequest
import models.response.MissingMandatoryPage
import pages.sections.sad.ImportNumberPage
import play.api.test.FakeRequest

class ImportCustomsDeclarationModelSpec extends SpecBase {
  "apply" - {
    "must throw an error" - {
      "when no ICD" in {
        implicit val dr: DataRequest[_] = dataRequest(
          FakeRequest(),
          emptyUserAnswers
        )

        val result = intercept[MissingMandatoryPage](ImportCustomsDeclarationModel.apply)

        result.message mustBe "SadSection should contain at least one item"
      }
    }

    "must return a Seq(ImportCustomsDeclarationModel)" - {
      "when one ICD" in {
        implicit val dr: DataRequest[_] = dataRequest(
          FakeRequest(),
          emptyUserAnswers
            .set(ImportNumberPage(testIndex1), "icd 1")
        )

        ImportCustomsDeclarationModel.apply mustBe Seq(ImportCustomsDeclarationModel("icd 1"))
      }
      "when more than one ICD" in {
        implicit val dr: DataRequest[_] = dataRequest(
          FakeRequest(),
          emptyUserAnswers
            .set(ImportNumberPage(testIndex1), "icd 1")
            .set(ImportNumberPage(testIndex2), "icd 2")
            .set(ImportNumberPage(testIndex3), "icd 3")
        )

        ImportCustomsDeclarationModel.apply mustBe Seq(ImportCustomsDeclarationModel("icd 1"), ImportCustomsDeclarationModel("icd 2"), ImportCustomsDeclarationModel("icd 3"))
      }
    }
  }
}
