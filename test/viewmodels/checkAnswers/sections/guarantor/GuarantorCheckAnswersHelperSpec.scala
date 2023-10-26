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

package viewmodels.checkAnswers.sections.guarantor

import base.SpecBase
import fixtures.messages.sections.guarantor.GuarantorArrangerMessages
import models.requests.DataRequest
import models.sections.guarantor.GuarantorArranger
import models.sections.guarantor.GuarantorArranger.{GoodsOwner, Transporter}
import org.scalamock.scalatest.MockFactory
import pages.sections.consignee.{ConsigneeAddressPage, ConsigneeBusinessNamePage}
import pages.sections.guarantor._
import play.api.i18n.Messages
import play.api.test.FakeRequest

class GuarantorCheckAnswersHelperSpec extends SpecBase with MockFactory {
  trait Test {
    lazy val app = applicationBuilder().build()
    implicit lazy val msgs: Messages = messages(app, GuarantorArrangerMessages.English.lang)
    val helper = new GuarantorCheckAnswersHelper()
  }

  "summaryList" - {
    GuarantorArranger.values.foreach {
      case value@(GoodsOwner | Transporter) =>

        "must render five rows" - {
          s"when GuarantorArranger value is ${value.getClass.getSimpleName.stripSuffix("$")}" in new Test {
            implicit val request: DataRequest[_] = dataRequest(
              FakeRequest(),
              emptyUserAnswers
                .set(GuarantorRequiredPage, true)
                .set(GuarantorArrangerPage, value)
                .set(GuarantorNamePage, "guarantor name")
                .set(GuarantorVatPage, "gurantor123")
                .set(GuarantorAddressPage, testUserAddress)
            )
            helper.summaryList()(request, msgs).rows.length mustBe 5
          }
        }
      case value =>
        "must render four rows" - {
          s"when GuarantorArranger value is ${value.getClass.getSimpleName.stripSuffix("$")}" in new Test {
            implicit val request: DataRequest[_] = dataRequest(
              FakeRequest(),
              emptyUserAnswers
                .set(GuarantorRequiredPage, true)
                .set(GuarantorArrangerPage, value)
                .set(ConsigneeBusinessNamePage, s"$value name")
                .set(ConsigneeAddressPage, testUserAddress)
            )
            helper.summaryList()(request, msgs).rows.length mustBe 4
          }
        }
    }

    "must render one row" - {
      "when no answers for the guarantor section" in new Test {
        implicit val request: DataRequest[_] = dataRequest(
          FakeRequest(),
          emptyUserAnswers
            .set(GuarantorRequiredPage, false)
        )
        helper.summaryList()(request, msgs).rows.length mustBe 1
      }
    }

  }
}
