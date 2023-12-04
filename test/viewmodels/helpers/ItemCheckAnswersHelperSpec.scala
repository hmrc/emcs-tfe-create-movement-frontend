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
import fixtures.messages.sections.items._
import models.requests.DataRequest
import models.sections.items._
import models.{CheckMode, UnitOfMeasure, UserAnswers}
import pages.sections.items._
import play.api.i18n.Messages
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import uk.gov.hmrc.govukfrontend.views.Aliases._
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import viewmodels.checkAnswers.sections.items.{ItemCommodityCodeSummary, ItemExciseProductCodeSummary}
import viewmodels.implicits._
import views.html.components.p

class ItemCheckAnswersHelperSpec extends SpecBase with ItemFixtures {
  val messagesForLanguage: ItemCheckAnswersMessages.English.type = ItemCheckAnswersMessages.English

  val baseUserAnswers: UserAnswers = emptyUserAnswers
    .set(ItemExciseProductCodePage(testIndex1), testEpcWine)

  class Test(val userAnswers: UserAnswers) {
    lazy implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(), userAnswers, testErn)
    lazy implicit val msgs: Messages = messages(Seq(messagesForLanguage.lang))
    lazy val p: p = app.injector.instanceOf[p]
    lazy val itemExciseProductCodeSummary: ItemExciseProductCodeSummary = app.injector.instanceOf[ItemExciseProductCodeSummary]
    lazy val itemCommodityCodeSummary: ItemCommodityCodeSummary = app.injector.instanceOf[ItemCommodityCodeSummary]

    lazy val helper = new ItemCheckAnswersHelper(itemExciseProductCodeSummary, itemCommodityCodeSummary, p)
  }

  private def summaryListRowBuilder(key: Content, value: Content, changeLink: Option[ActionItem]) = SummaryListRow(
    Key(key),
    Value(value),
    classes = "govuk-summary-list__row",
    actions = changeLink.map(actionItem => Actions(items = Seq(actionItem)))
  )

  "ItemCheckAnswersHelper" - {
    "for the ItemDetails section" - {
      "constructCard" - {
        "must return rows" in new Test(baseUserAnswers) {
          helper.ItemDetails.constructCard(testIndex1, testCommodityCodeWine) must not be empty
        }
      }

      "constructDensityRow" - {
        "if provided" - {
          "must return a row" in new Test(
            baseUserAnswers
              .set(ItemDensityPage(testIndex1), BigDecimal(2.65))
          ) {
            helper.ItemDetails.constructDensityRow(
              idx = testIndex1,
              unitOfMeasure = UnitOfMeasure.Litres20
            ) mustBe
              Some(summaryListRowBuilder(
                key = HtmlContent(ItemDensityMessages.English.cyaLabel),
                value = HtmlContent(s"2.65${ItemDensityMessages.English.cyaSuffix}"),
                changeLink = Some(ActionItem(
                  href = controllers.sections.items.routes.ItemDensityController.onPageLoad(testErn, testDraftId, testIndex1, CheckMode).url,
                  content = messagesForLanguage.change,
                  visuallyHiddenText = Some(ItemDensityMessages.English.cyaChangeHidden)
                ))
              ))
          }
        }
        "if not provided" - {
          "must not return a row" in new Test(baseUserAnswers) {
            helper.ItemDetails.constructDensityRow(
              idx = testIndex1,
              unitOfMeasure = UnitOfMeasure.Litres15
            ) mustBe None
          }
        }
      }

      "constructFiscalMarksChoiceRow" - {
        "if true" - {
          "must return a row" in new Test(
            baseUserAnswers
              .set(ItemFiscalMarksChoicePage(testIndex1), true)
          ) {
            helper.ItemDetails.constructFiscalMarksChoiceRow(
              idx = testIndex1
            ) mustBe
              Some(summaryListRowBuilder(
                key = ItemFiscalMarksChoiceMessages.English.cyaLabel,
                value = messagesForLanguage.yes,
                changeLink = Some(ActionItem(
                  href = controllers.sections.items.routes.ItemFiscalMarksChoiceController.onPageLoad(testErn, testDraftId, testIndex1, CheckMode).url,
                  content = messagesForLanguage.change,
                  visuallyHiddenText = Some(ItemFiscalMarksChoiceMessages.English.cyaChangeHidden)
                ))
              ))
          }
        }
        "if false" - {
          "must return a row" in new Test(
            baseUserAnswers
              .set(ItemFiscalMarksChoicePage(testIndex1), false)
          ) {
            helper.ItemDetails.constructFiscalMarksChoiceRow(
              idx = testIndex1
            ) mustBe
              Some(summaryListRowBuilder(
                key = ItemFiscalMarksChoiceMessages.English.cyaLabel,
                value = messagesForLanguage.no,
                changeLink = Some(ActionItem(
                  href = controllers.sections.items.routes.ItemFiscalMarksChoiceController.onPageLoad(testErn, testDraftId, testIndex1, CheckMode).url,
                  content = messagesForLanguage.change,
                  visuallyHiddenText = Some(ItemFiscalMarksChoiceMessages.English.cyaChangeHidden)
                ))
              ))
          }
        }
        "if not provided" - {
          "must not return a row" in new Test(baseUserAnswers) {
            helper.ItemDetails.constructFiscalMarksChoiceRow(
              idx = testIndex1
            ) mustBe None
          }
        }
      }

      "constructFiscalMarksRow" - {
        "if ItemFiscalMarksChoicePage is true" - {
          "must return a row when there is an answer" in new Test(
            baseUserAnswers
              .set(ItemFiscalMarksChoicePage(testIndex1), true)
              .set(ItemFiscalMarksPage(testIndex1), "test fiscal marks")
          ) {
            helper.ItemDetails.constructFiscalMarksRow(
              idx = testIndex1
            ) mustBe
              Some(summaryListRowBuilder(
                key = ItemFiscalMarksMessages.English.cyaLabel,
                value = "test fiscal marks",
                changeLink = Some(ActionItem(
                  href = controllers.sections.items.routes.ItemFiscalMarksController.onPageLoad(testErn, testDraftId, testIndex1, CheckMode).url,
                  content = messagesForLanguage.change,
                  visuallyHiddenText = Some(ItemFiscalMarksMessages.English.cyaChangeHidden)
                ))
              ))
          }
        }
        "if ItemFiscalMarksChoicePage is false" - {
          "must not return a row" in new Test(
            baseUserAnswers
              .set(ItemFiscalMarksChoicePage(testIndex1), false)
              .set(ItemFiscalMarksPage(testIndex1), "test fiscal marks")
          ) {
            helper.ItemDetails.constructFiscalMarksRow(
              idx = testIndex1
            ) mustBe None
          }
        }
        "if not provided" - {
          "must not return a row" in new Test(baseUserAnswers.set(ItemFiscalMarksChoicePage(testIndex1), true)) {
            helper.ItemDetails.constructFiscalMarksRow(
              idx = testIndex1
            ) mustBe None
          }
        }
      }

      "constructGeographicalIndicationChoiceRow" - {
        ItemGeographicalIndicationType.values.foreach {
          answer =>
            s"if answer is $answer" - {
              "must return a row" in new Test(
                baseUserAnswers
                  .set(ItemGeographicalIndicationChoicePage(testIndex1), answer)
              ) {
                helper.ItemDetails.constructGeographicalIndicationChoiceRow(
                  idx = testIndex1
                ) mustBe
                  Some(summaryListRowBuilder(
                    key = ItemGeographicalIndicationChoiceMessages.English.cyaLabel,
                    value = msgs(s"${ItemGeographicalIndicationChoicePage(testIndex1)}.checkYourAnswers.value.$answer"),
                    changeLink = Some(ActionItem(
                      href = controllers.sections.items.routes.ItemGeographicalIndicationChoiceController.onPageLoad(testErn, testDraftId, testIndex1, CheckMode).url,
                      content = messagesForLanguage.change,
                      visuallyHiddenText = Some(ItemGeographicalIndicationChoiceMessages.English.cyaChangeHidden)
                    ))
                  ))
              }
            }
        }
        "if not provided" - {
          "must not return a row" in new Test(baseUserAnswers) {
            helper.ItemDetails.constructGeographicalIndicationChoiceRow(
              idx = testIndex1
            ) mustBe None
          }
        }
      }

      "constructGeographicalIndicationRow" - {
        ItemGeographicalIndicationType.values.filterNot(_ == ItemGeographicalIndicationType.NoGeographicalIndication).foreach {
          answer =>
            s"if ItemGeographicalIndicationChoicePage is $answer" - {
              "must return a row when there is an answer" in new Test(
                baseUserAnswers
                  .set(ItemGeographicalIndicationChoicePage(testIndex1), answer)
                  .set(ItemGeographicalIndicationPage(testIndex1), "test fiscal marks")
              ) {
                helper.ItemDetails.constructGeographicalIndicationRow(
                  idx = testIndex1
                ) mustBe
                  Some(summaryListRowBuilder(
                    key = ItemGeographicalIndicationMessages.English.cyaLabel,
                    value = "test fiscal marks",
                    changeLink = Some(ActionItem(
                      href = controllers.sections.items.routes.ItemGeographicalIndicationController.onPageLoad(testErn, testDraftId, testIndex1, CheckMode).url,
                      content = messagesForLanguage.change,
                      visuallyHiddenText = Some(ItemGeographicalIndicationMessages.English.cyaChangeHidden)
                    ))
                  ))
              }
            }
        }
        s"if ItemGeographicalIndicationChoicePage is ${ItemGeographicalIndicationType.NoGeographicalIndication}" - {
          "must not return a row" in new Test(
            baseUserAnswers
              .set(ItemGeographicalIndicationChoicePage(testIndex1), ItemGeographicalIndicationType.NoGeographicalIndication)
              .set(ItemGeographicalIndicationPage(testIndex1), "test fiscal marks")
          ) {
            helper.ItemDetails.constructGeographicalIndicationRow(
              idx = testIndex1
            ) mustBe None
          }
        }
        "if not provided" - {
          "must not return a row" in new Test(
            baseUserAnswers
              .set(ItemGeographicalIndicationChoicePage(testIndex1), ItemGeographicalIndicationType.GeographicalIndication)
          ) {
            helper.ItemDetails.constructGeographicalIndicationRow(
              idx = testIndex1
            ) mustBe None
          }
        }
      }

      "constructSmallIndependentProducerRow" - {
        "if true" - {
          "must return a row" in new Test(
            baseUserAnswers
              .set(ItemSmallIndependentProducerPage(testIndex1), true)
          ) {
            helper.ItemDetails.constructSmallIndependentProducerRow(
              idx = testIndex1
            ) mustBe
              Some(summaryListRowBuilder(
                key = ItemSmallIndependentProducerMessages.English.cyaLabel,
                value = messagesForLanguage.yes,
                changeLink = Some(ActionItem(
                  href = controllers.sections.items.routes.ItemSmallIndependentProducerController.onPageLoad(testErn, testDraftId, testIndex1, CheckMode).url,
                  content = messagesForLanguage.change,
                  visuallyHiddenText = Some(ItemSmallIndependentProducerMessages.English.cyaChangeHidden)
                ))
              ))
          }
        }
        "if false" - {
          "must return a row" in new Test(
            baseUserAnswers
              .set(ItemSmallIndependentProducerPage(testIndex1), false)
          ) {
            helper.ItemDetails.constructSmallIndependentProducerRow(
              idx = testIndex1
            ) mustBe
              Some(summaryListRowBuilder(
                key = ItemSmallIndependentProducerMessages.English.cyaLabel,
                value = messagesForLanguage.no,
                changeLink = Some(ActionItem(
                  href = controllers.sections.items.routes.ItemSmallIndependentProducerController.onPageLoad(testErn, testDraftId, testIndex1, CheckMode).url,
                  content = messagesForLanguage.change,
                  visuallyHiddenText = Some(ItemSmallIndependentProducerMessages.English.cyaChangeHidden)
                ))
              ))
          }
        }
        "if not provided" - {
          "must not return a row" in new Test(baseUserAnswers) {
            helper.ItemDetails.constructSmallIndependentProducerRow(
              idx = testIndex1
            ) mustBe None
          }
        }
      }

      "constructProducerSizeRow" - {
        "if ItemSmallIndependentProducerPage is true" - {
          "must return a row when there is an answer" in new Test(
            baseUserAnswers
              .set(ItemSmallIndependentProducerPage(testIndex1), true)
              .set(ItemProducerSizePage(testIndex1), BigInt(3))
          ) {
            helper.ItemDetails.constructProducerSizeRow(
              idx = testIndex1
            ) mustBe
              Some(summaryListRowBuilder(
                key = ItemProducerSizeMessages.English.cyaLabel,
                value = s"3 ${ItemProducerSizeMessages.English.inputSuffix}",
                changeLink = Some(ActionItem(
                  href = controllers.sections.items.routes.ItemProducerSizeController.onPageLoad(testErn, testDraftId, testIndex1, CheckMode).url,
                  content = messagesForLanguage.change,
                  visuallyHiddenText = Some(ItemProducerSizeMessages.English.cyaChangeHidden)
                ))
              ))
          }
        }
        "if ItemSmallIndependentProducerPage is false" - {
          "must not return a row" in new Test(
            baseUserAnswers
              .set(ItemSmallIndependentProducerPage(testIndex1), false)
              .set(ItemProducerSizePage(testIndex1), BigInt(3))
          ) {
            helper.ItemDetails.constructProducerSizeRow(
              idx = testIndex1
            ) mustBe None
          }
        }
        "if not provided" - {
          "must not return a row" in new Test(baseUserAnswers.set(ItemSmallIndependentProducerPage(testIndex1), true)) {
            helper.ItemDetails.constructProducerSizeRow(
              idx = testIndex1
            ) mustBe None
          }
        }
      }
    }

    "for the Quantity section" - {
      "constructCard" - {
        "must return all rows" in new Test(
          baseUserAnswers
            .set(ItemQuantityPage(testIndex1), BigDecimal(1.23))
            .set(ItemNetGrossMassPage(testIndex1), ItemNetGrossMassModel(BigDecimal(4.56), BigDecimal(7.89)))
        ) {
          helper.Quantity.constructCard(testIndex1, testCommodityCodeWine).length mustBe 3
        }
      }

      "constructQuantityRow" - {
        "if provided" - {
          "must return a row" in new Test(
            baseUserAnswers
              .set(ItemQuantityPage(testIndex1), BigDecimal(1.23))
          ) {
            helper.Quantity.constructQuantityRow(
              idx = testIndex1,
              unitOfMeasure = UnitOfMeasure.Kilograms
            ) mustBe
              Some(summaryListRowBuilder(
                key = ItemQuantityMessages.English.cyaLabel,
                value = s"1.23 ${UnitOfMeasure.Kilograms.toShortFormatMessage()}",
                changeLink = Some(ActionItem(
                  href = controllers.sections.items.routes.ItemQuantityController.onPageLoad(testErn, testDraftId, testIndex1, CheckMode).url,
                  content = messagesForLanguage.change,
                  visuallyHiddenText = Some(ItemQuantityMessages.English.cyaChangeHidden)
                ))
              ))
          }
        }
        "if not provided" - {
          "must not return a row" in new Test(baseUserAnswers) {
            helper.Quantity.constructQuantityRow(
              idx = testIndex1,
              unitOfMeasure = UnitOfMeasure.Kilograms
            ) mustBe None
          }
        }
      }

      "constructNetMassRow" - {
        "if provided" - {
          "must return a row" in new Test(
            baseUserAnswers
              .set(ItemNetGrossMassPage(testIndex1), ItemNetGrossMassModel(BigDecimal(4.56), BigDecimal(7.89)))
          ) {
            helper.Quantity.constructNetMassRow(
              idx = testIndex1,
              unitOfMeasure = UnitOfMeasure.Litres20
            ) mustBe
              Some(summaryListRowBuilder(
                key = ItemNetGrossMassMessages.English.cyaNetMassLabel,
                value = s"4.56 ${UnitOfMeasure.Litres20.toShortFormatMessage()}",
                changeLink = Some(ActionItem(
                  href = controllers.sections.items.routes.ItemNetGrossMassController.onPageLoad(testErn, testDraftId, testIndex1, CheckMode).url,
                  content = messagesForLanguage.change,
                  visuallyHiddenText = Some(ItemNetGrossMassMessages.English.cyaNetMassChangeHidden)
                ))
              ))
          }
        }
        "if not provided" - {
          "must not return a row" in new Test(baseUserAnswers) {
            helper.Quantity.constructNetMassRow(
              idx = testIndex1,
              unitOfMeasure = UnitOfMeasure.Litres20
            ) mustBe None
          }
        }
      }

      "constructGrossMassRow" - {
        "if provided" - {
          "must return a row" in new Test(
            baseUserAnswers
              .set(ItemNetGrossMassPage(testIndex1), ItemNetGrossMassModel(BigDecimal(4.56), BigDecimal(7.89)))
          ) {
            helper.Quantity.constructGrossMassRow(
              idx = testIndex1,
              unitOfMeasure = UnitOfMeasure.Thousands
            ) mustBe
              Some(summaryListRowBuilder(
                key = ItemNetGrossMassMessages.English.cyaGrossMassLabel,
                value = s"7.89 ${UnitOfMeasure.Thousands.toShortFormatMessage()}",
                changeLink = Some(ActionItem(
                  href = controllers.sections.items.routes.ItemNetGrossMassController.onPageLoad(testErn, testDraftId, testIndex1, CheckMode).url,
                  content = messagesForLanguage.change,
                  visuallyHiddenText = Some(ItemNetGrossMassMessages.English.cyaGrossMassChangeHidden)
                ))
              ))
          }
        }
        "if not provided" - {
          "must not return a row" in new Test(baseUserAnswers) {
            helper.Quantity.constructGrossMassRow(
              idx = testIndex1,
              unitOfMeasure = UnitOfMeasure.Thousands
            ) mustBe None
          }
        }
      }
    }
  }
}
