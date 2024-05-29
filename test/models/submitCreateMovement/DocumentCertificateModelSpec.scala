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
import models.sections.documents.DocumentType
import pages.sections.documents._
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest

class DocumentCertificateModelSpec extends SpecBase {

  val fakeRequest: FakeRequest[AnyContentAsEmpty.type] = FakeRequest()

  "apply" - {
    "should return Some(Seq(_))" - {
      "when all pages have been answered" in {
        implicit val dr: DataRequest[_] = dataRequest(
          fakeRequest,
          emptyUserAnswers
            .set(DocumentsCertificatesPage, true)
            .set(DocumentTypePage(testIndex1), DocumentType("1", "1 type desc"))
            .set(DocumentReferencePage(testIndex1), "1 reference")
            .set(DocumentTypePage(testIndex2), DocumentType("2", "2 type desc"))
            .set(DocumentReferencePage(testIndex2), "2 reference")
        )

        DocumentCertificateModel.applyFromRequest mustBe Some(Seq(
          DocumentCertificateModel(
            documentType = Some(DocumentType("1", "1 type desc")),
            documentReference = Some("1 reference"),
            documentDescription = None,
            referenceOfDocument = None
          ),
          DocumentCertificateModel(
            documentType = Some(DocumentType("2", "2 type desc")),
            documentReference = Some("2 reference"),
            documentDescription = None,
            referenceOfDocument = None
          )
        ))
      }
    }
    "should return None" - {
      "when DocumentsCertificatesPage is false" in {
        implicit val dr: DataRequest[_] = dataRequest(
          fakeRequest,
          emptyUserAnswers
            .set(DocumentsCertificatesPage, false)
        )

        DocumentCertificateModel.applyFromRequest mustBe None
      }
      "when DocumentsCertificatesPage is true but DocumentsCount is < 1" in {
        implicit val dr: DataRequest[_] = dataRequest(
          fakeRequest,
          emptyUserAnswers
            .set(DocumentsCertificatesPage, true)
        )

        DocumentCertificateModel.applyFromRequest mustBe None
      }
    }
  }
}
