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

package viewmodels.helpers

import base.SpecBase
import fixtures.ItemFixtures
import fixtures.messages.sections.items.ItemSmallIndependentProducerMessages
import forms.sections.items.ItemSmallIndependentProducerFormProvider
import forms.sections.items.ItemSmallIndependentProducerFormProvider.{producerField, producerIdField}
import models.GoodsType._
import models.sections.info.movementScenario.MovementScenario._
import models.sections.items.ItemSmallIndependentProducerType._
import pages.sections.info.DestinationTypePage
import pages.sections.items.{ItemCommodityCodePage, ItemExciseProductCodePage}
import play.api.i18n.Messages
import play.api.test.FakeRequest
import uk.gov.hmrc.govukfrontend.views.Aliases._
import uk.gov.hmrc.govukfrontend.views.html.components.GovukInput
import viewmodels.govuk.LabelFluency

class ItemSmallIndependentProducerHelperSpec extends SpecBase with ItemFixtures with LabelFluency {

  val input: GovukInput = app.injector.instanceOf[GovukInput]

  val helper: ItemSmallIndependentProducerHelper = new ItemSmallIndependentProducerHelper(input)

  val form = new ItemSmallIndependentProducerFormProvider()()

  val messagesForLanguage: ItemSmallIndependentProducerMessages.English.type = ItemSmallIndependentProducerMessages.English

  implicit val messages: Messages = messages(FakeRequest())

  ".radios" - {

    "must return the correct radios" in {

      helper.radios(form) mustBe Radios(
        name = producerField,
        items = Seq(
          RadioItem(
            id = Some(s"${form(producerField).id}-$CertifiedIndependentSmallProducer"),
            value = Some(CertifiedIndependentSmallProducer.toString),
            content = Text(messagesForLanguage.certifiedIndependentSmallProducer),
            hint = Some(Hint(content = Text(messagesForLanguage.certifiedIndependentSmallProducerHint)))
          ),
          RadioItem(
            id = Some(s"${form(producerField).id}-$SelfCertifiedIndependentSmallProducerAndConsignor"),
            value = Some(SelfCertifiedIndependentSmallProducerAndConsignor.toString),
            content = Text(messagesForLanguage.selfCertifiedIndependentSmallProducerAndConsignor)
          ),
          RadioItem(
            id = Some(s"${form(producerField).id}-$SelfCertifiedIndependentSmallProducerAndNotConsignor"),
            value = Some(SelfCertifiedIndependentSmallProducerAndNotConsignor.toString),
            content = Text(messagesForLanguage.selfCertifiedIndependentSmallProducerNotConsignor),
            conditionalHtml = Some(
              input(Input(
                id = producerIdField,
                name = producerIdField,
                label = LabelViewModel(Text(messagesForLanguage.selfCertifiedIndependentSmallProducerNotConsignorInput)),
                value = form(producerIdField).value
              ))
            )
          ),
          RadioItem(
            divider = Some(messagesForLanguage.or)
          ),
          RadioItem(
            id = Some(s"${form(producerField).id}-$NotApplicable"),
            value = Some(NotApplicable.toString),
            content = Text(messagesForLanguage.notAIndependentSmallProducer)
          )
        ), fieldset = Some(Fieldset(legend = Some(Legend(Text(messagesForLanguage.legend), classes = " govuk-fieldset__legend--m")))))
    }
  }

  ".constructDeclarationPrefix" - {

    "should return the correct string" - {

      Seq(
        UkTaxWarehouse.GB,
        UkTaxWarehouse.NI,
        ExportWithCustomsDeclarationLodgedInTheUk,
        ExportWithCustomsDeclarationLodgedInTheEu
      ).foreach { movementScenario =>

        s"for movement scenario: $movementScenario" in {

          implicit val request = dataRequest(FakeRequest(), emptyUserAnswers.set(DestinationTypePage, movementScenario))

          ItemSmallIndependentProducerHelper.constructDeclarationPrefix(testIndex1) mustBe messagesForLanguage.producedByIndependentSmallProducer
        }
      }
    }

    "should throw an exception" - {

      "when movement scenario is not defined" in {

        implicit val request = dataRequest(FakeRequest(),
          emptyUserAnswers
            .set(ItemExciseProductCodePage(testIndex1), testEpcSpirit)
            .set(ItemCommodityCodePage(testIndex1), testCnCodeSpirit)
        )
        intercept[IllegalStateException](ItemSmallIndependentProducerHelper.constructDeclarationPrefix(testIndex1
        )).getMessage mustBe s"Invalid scenario for small independent producer. Destination type: None & EPC: Some($testEpcSpirit) & CN code: Some($testCnCodeSpirit)"
      }

      "when EPC is not defined " +
        "(and movement scenario not UkTaxWarehouse | ExportWithCustomsDeclarationLodgedInTheUk | ExportWithCustomsDeclarationLodgedInTheEu)" in {

        implicit val request = dataRequest(FakeRequest(),
          emptyUserAnswers
            .set(DestinationTypePage, EuTaxWarehouse)
            .set(ItemCommodityCodePage(testIndex1), testCnCodeSpirit)
        )
        intercept[IllegalStateException](ItemSmallIndependentProducerHelper.constructDeclarationPrefix(testIndex1
        )).getMessage mustBe s"Invalid scenario for small independent producer. Destination type: Some($EuTaxWarehouse) & EPC: None & CN code: Some($testCnCodeSpirit)"
      }

      "when CN code is not defined " +
        "(and movement scenario not UkTaxWarehouse | ExportWithCustomsDeclarationLodgedInTheUk | ExportWithCustomsDeclarationLodgedInTheEu)" in {

        implicit val request = dataRequest(FakeRequest(),
          emptyUserAnswers
            .set(DestinationTypePage, EuTaxWarehouse)
            .set(ItemExciseProductCodePage(testIndex1), testEpcSpirit)
        )
        intercept[IllegalStateException](ItemSmallIndependentProducerHelper.constructDeclarationPrefix(testIndex1
        )).getMessage mustBe s"Invalid scenario for small independent producer. Destination type: Some($EuTaxWarehouse) & EPC: Some($testEpcSpirit) & CN code: None"
      }

      "when it's not an intra-UK movement or NI -> EU" in {

        implicit val request = dataRequest(FakeRequest(),
          emptyUserAnswers
            .set(DestinationTypePage, UnknownDestination)
            .set(ItemExciseProductCodePage(testIndex1), testEpcSpirit)
            .set(ItemCommodityCodePage(testIndex1), testCnCodeSpirit)
        )
        intercept[IllegalStateException](ItemSmallIndependentProducerHelper.constructDeclarationPrefix(testIndex1
        )).getMessage mustBe s"Invalid scenario for small independent producer. Destination type: Some($UnknownDestination) & EPC: Some($testEpcSpirit) & CN code: Some($testCnCodeSpirit)"
      }
    }
  }


  ".handleNiToEuMovementDeclaration" - {

    "should return the correct string" - {

      Seq(
        Beer -> messagesForLanguage.producedByIndependentSmallBrewery,
        Spirits -> messagesForLanguage.producedByIndependentSmallDistillery,
        Wine -> messagesForLanguage.producedByIndependentWineProducer,
        Intermediate -> messagesForLanguage.producedByIndependentIntermediateProductsProducer
      ).foreach { goodsTypeAndExpectedMessage =>

        s"for goods type: ${goodsTypeAndExpectedMessage._1}" in {

          ItemSmallIndependentProducerHelper.handleNiToEuMovementDeclaration(
            goodsTypeAndExpectedMessage._1.code, testCnCodeWine
          ) mustBe goodsTypeAndExpectedMessage._2
        }

      }

      s"for goods type: $Fermented" in {

        ItemSmallIndependentProducerHelper.handleNiToEuMovementDeclaration(
          testEpcSpirit, testCnCodeSpirit
        ) mustBe messagesForLanguage.producedByIndependentFermentedBeveragesProducer
      }
    }

    "should throw an exception" - {

      Seq(Energy, Tobacco).foreach { goodsType =>

        s"for goods type: $goodsType" in {

          intercept[IllegalStateException](ItemSmallIndependentProducerHelper.handleNiToEuMovementDeclaration(
            goodsType.code, testCnCodeEnergy
          )).getMessage mustBe s"Invalid goods type for small independent producer: $goodsType"
        }
      }
    }

  }

  ".isNiToEUMovement" - {

    "should return true" - {

      "when the movement scenario is EU-related and the consignor is an XI trader" in {

        ItemSmallIndependentProducerHelper.isNiToEUMovement(EuTaxWarehouse)(dataRequest(FakeRequest(), ern = testNorthernIrelandErn)) mustBe true
      }
    }

    "should return false" - {

      "when the movement scenario is EU-related but the consignor is not an XI trader" in {

        ItemSmallIndependentProducerHelper.isNiToEUMovement(EuTaxWarehouse)(dataRequest(FakeRequest(), ern = testGreatBritainErn)) mustBe false
      }

      "when the consignor is an XI trader but movement scenario is not EU-related" in {

        ItemSmallIndependentProducerHelper.isNiToEUMovement(UkTaxWarehouse.GB)(dataRequest(FakeRequest(), ern = testNorthernIrelandErn)) mustBe false
      }
    }

  }
}
