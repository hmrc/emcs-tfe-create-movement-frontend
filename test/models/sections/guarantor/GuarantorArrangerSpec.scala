/*
 * Copyright 2024 HM Revenue & Customs
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

package models.sections.guarantor

import models.sections.guarantor.GuarantorArranger._
import models.sections.info.movementScenario.MovementScenario
import models.sections.info.movementScenario.MovementScenario.{CertifiedConsignee, TemporaryCertifiedConsignee, UkTaxWarehouse}
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import play.api.i18n.Messages
import play.api.test.Helpers.stubMessages
import uk.gov.hmrc.govukfrontend.views.Aliases.Text
import uk.gov.hmrc.govukfrontend.views.viewmodels.radios.RadioItem

class GuarantorArrangerSpec extends AnyFreeSpec with Matchers {

  "GuarantorArranger" - {
    "should have the correct enum mappings and audit descriptions" in {

      Consignor.toString mustBe "1"
      Consignor.auditDescription mustBe "Consignor"

      Consignee.toString mustBe "4"
      Consignee.auditDescription mustBe "Consignee"

      GoodsOwner.toString mustBe "3"
      GoodsOwner.auditDescription mustBe "GoodsOwner"

      Transporter.toString mustBe "2"
      Transporter.auditDescription mustBe "Transporter"

      NoGuarantorRequiredUkToEu.toString mustBe "5"
      NoGuarantorRequiredUkToEu.auditDescription mustBe "NoGuarantorRequiredUkToEu"

      NoGuarantorRequired.toString mustBe "0"
      NoGuarantorRequired.auditDescription mustBe "NoGuarantorRequired"
    }
  }

  "options" - {
    implicit val msgs: Messages = stubMessages()

    val scenariosWithConsignee: Seq[MovementScenario] =
      UkTaxWarehouse.values ++ Seq(CertifiedConsignee, TemporaryCertifiedConsignee)

    scenariosWithConsignee.foreach {
      scenario =>
        s"should return all options, including Consignee, when the movement scenario is $scenario" in {
          GuarantorArranger.options(scenario) mustBe Seq(Consignor, Consignee, GoodsOwner, Transporter).map {
            value =>
              RadioItem(
                content = Text(msgs(s"guarantorArranger.${value.toString}")),
                value = Some(value.toString),
                id = Some(s"value_${value.toString}")
              )
          }
        }
    }

    MovementScenario.values.filterNot(scenariosWithConsignee.contains).foreach {
      scenario =>
        s"should return all options, excluding Consignee, when the movement scenario is $scenario" in {
          GuarantorArranger.options(scenario) mustBe Seq(Consignor, GoodsOwner, Transporter).map {
            value =>
              RadioItem(
                content = Text(msgs(s"guarantorArranger.${value.toString}")),
                value = Some(value.toString),
                id = Some(s"value_${value.toString}")
              )
          }
        }
    }
  }

}
