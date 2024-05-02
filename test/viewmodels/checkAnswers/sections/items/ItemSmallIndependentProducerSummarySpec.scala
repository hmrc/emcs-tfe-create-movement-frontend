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

package viewmodels.checkAnswers.sections.items

import base.SpecBase
import fixtures.ItemFixtures
import fixtures.messages.sections.items.ItemSmallIndependentProducerMessages
import models.GoodsType._
import models.requests.DataRequest
import models.sections.info.movementScenario.MovementScenario.{EuTaxWarehouse, ExportWithCustomsDeclarationLodgedInTheEu, ExportWithCustomsDeclarationLodgedInTheUk, UkTaxWarehouse}
import models.sections.items.ItemSmallIndependentProducerModel
import models.sections.items.ItemSmallIndependentProducerType.{CertifiedIndependentSmallProducer, SelfCertifiedIndependentSmallProducerAndNotConsignor}
import models.{CheckMode, UserAnswers}
import org.scalatest.matchers.must.Matchers
import pages.sections.info.DestinationTypePage
import pages.sections.items.{ItemCommodityCodePage, ItemExciseProductCodePage, ItemSmallIndependentProducerPage}
import play.api.i18n.Messages
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import uk.gov.hmrc.govukfrontend.views.Aliases.{HtmlContent, Text, Value}
import viewmodels.govuk.summarylist._
import viewmodels.implicits._
import views.html.components.p

class ItemSmallIndependentProducerSummarySpec extends SpecBase with Matchers with ItemFixtures {

  val p: p = app.injector.instanceOf[p]

  val summary: ItemSmallIndependentProducerSummary = app.injector.instanceOf[ItemSmallIndependentProducerSummary]

  class Test(val userAnswers: UserAnswers, ern: String = testErn) {
    implicit lazy val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(), userAnswers, ern = ern)
  }

  "ItemSmallIndependentProducerSummary" - {

    Seq(ItemSmallIndependentProducerMessages.English).foreach { messagesForLanguage =>

      s"when being rendered in lang code of '${messagesForLanguage.lang.code}'" - {

        implicit val msgs: Messages = messages(Seq(messagesForLanguage.lang))

        "return the correct row for" - {

          Seq(
            UkTaxWarehouse.GB,
            UkTaxWarehouse.NI,
            ExportWithCustomsDeclarationLodgedInTheUk,
            ExportWithCustomsDeclarationLodgedInTheEu
          ).foreach { movementScenario =>

            s"intra-UK / export movement: $movementScenario" in new Test(emptyUserAnswers
              .set(DestinationTypePage, movementScenario)
              .set(ItemSmallIndependentProducerPage(testIndex1), ItemSmallIndependentProducerModel(SelfCertifiedIndependentSmallProducerAndNotConsignor, Some(testErn)))
            ) {

              summary.row(
                idx = testIndex1
              ) mustBe
                Some(SummaryListRowViewModel(
                  key = messagesForLanguage.cyaLabel,
                  value = Value(HtmlContent(Seq(
                    p()(Text(messagesForLanguage.producedByIndependentSmallProducer.dropRight(1)).asHtml).toString(),
                    p()(Text(messagesForLanguage.selfCertifiedIndependentSmallProducerNotConsignor).asHtml).toString(),
                    p()(Text(messagesForLanguage.seedNumber(testErn)).asHtml).toString()
                  ).mkString)),
                  actions = Seq(ActionItemViewModel(
                    href = controllers.sections.items.routes.ItemSmallIndependentProducerController.onPageLoad(testErn, testDraftId, testIndex1, CheckMode).url,
                    content = messagesForLanguage.change,
                    id = s"changeItemSmallIndependentProducer${testIndex1.displayIndex}"
                  ).withVisuallyHiddenText(messagesForLanguage.cyaChangeHidden))
                ))
            }

          }

          Seq(
            Beer -> messagesForLanguage.producedByIndependentSmallBrewery,
            Spirits -> messagesForLanguage.producedByIndependentSmallDistillery,
            Wine -> messagesForLanguage.producedByIndependentWineProducer,
            Intermediate -> messagesForLanguage.producedByIndependentIntermediateProductsProducer
          ).foreach { goodsTypeAndDeclaration =>

            s"NI -> EU movement and goods type: ${goodsTypeAndDeclaration._1}" in new Test(emptyUserAnswers
              .set(DestinationTypePage, EuTaxWarehouse)
              .set(ItemExciseProductCodePage(testIndex1), goodsTypeAndDeclaration._1.code)
              .set(ItemCommodityCodePage(testIndex1), testCnCodeBeer) //Irrelevant for these scenarios (but logically required)
              .set(ItemSmallIndependentProducerPage(testIndex1), ItemSmallIndependentProducerModel(CertifiedIndependentSmallProducer, None)
              ), ern = testNorthernIrelandErn
            ) {

              summary.row(
                idx = testIndex1
              ) mustBe
                Some(SummaryListRowViewModel(
                  key = messagesForLanguage.cyaLabel,
                  value = Value(HtmlContent(Seq(
                    p()(Text(goodsTypeAndDeclaration._2.dropRight(1)).asHtml).toString(),
                    p()(Text(messagesForLanguage.certifiedIndependentSmallProducer).asHtml).toString()
                  ).mkString)),
                  actions = Seq(ActionItemViewModel(
                    href = controllers.sections.items.routes.ItemSmallIndependentProducerController.onPageLoad(testNorthernIrelandErn, testDraftId, testIndex1, CheckMode).url,
                    content = messagesForLanguage.change,
                    id = s"changeItemSmallIndependentProducer${testIndex1.displayIndex}"
                  ).withVisuallyHiddenText(messagesForLanguage.cyaChangeHidden))
                ))
            }
          }

          s"NI -> EU movement and goods type: $Fermented" in new Test(emptyUserAnswers
            .set(DestinationTypePage, EuTaxWarehouse)
            .set(ItemExciseProductCodePage(testIndex1), Fermented(testEpcWine).code)
            .set(ItemCommodityCodePage(testIndex1), testCnCodeSpirit)
            .set(ItemSmallIndependentProducerPage(testIndex1), ItemSmallIndependentProducerModel(CertifiedIndependentSmallProducer, None)
            ), ern = testNorthernIrelandErn
          ) {

            summary.row(
              idx = testIndex1
            ) mustBe
              Some(SummaryListRowViewModel(
                key = messagesForLanguage.cyaLabel,
                value = Value(HtmlContent(Seq(
                  p()(Text(messagesForLanguage.producedByIndependentFermentedBeveragesProducer.dropRight(1)).asHtml).toString(),
                  p()(Text(messagesForLanguage.certifiedIndependentSmallProducer).asHtml).toString()
                ).mkString)),
                actions = Seq(ActionItemViewModel(
                  href = controllers.sections.items.routes.ItemSmallIndependentProducerController.onPageLoad(testNorthernIrelandErn, testDraftId, testIndex1, CheckMode).url,
                  content = messagesForLanguage.change,
                  id = s"changeItemSmallIndependentProducer${testIndex1.displayIndex}"
                ).withVisuallyHiddenText(messagesForLanguage.cyaChangeHidden))
              ))
          }
        }

        "if not provided" - {
          "must not return a row" in new Test(emptyUserAnswers) {
            summary.row(
              idx = testIndex1
            ) mustBe None
          }
        }
      }
    }
  }
}
