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
import models.{CheckMode, ExciseProductCode, UnitOfMeasure, UserAnswers}
import pages.sections.items._
import play.api.i18n.Messages
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import uk.gov.hmrc.govukfrontend.views.Aliases._
import viewmodels.implicits._

class ItemCheckAnswersHelperSpec extends SpecBase with ItemFixtures {
  val messagesForLang: ItemCheckAnswersMessages.English.type = ItemCheckAnswersMessages.English

  val baseUserAnswers: UserAnswers = emptyUserAnswers
    .set(ItemExciseProductCodePage(testIndex1), testEpcWine)

  private def summaryListRowBuilder(key: Content, value: Content, changeLink: Option[ActionItem]) = SummaryListRow(
    Key(key),
    Value(value),
    classes = "govuk-summary-list__row",
    actions = changeLink.map(actionItem => Actions(items = Seq(actionItem)))
  )

  class Test(val userAnswers: UserAnswers) {
    lazy implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(), userAnswers, testErn)
    lazy implicit val msgs: Messages = messages(Seq(messagesForLang.lang))

    lazy val helper = new ItemCheckAnswersHelper()
  }

  "ItemCheckAnswersHelper" - {
    "for the ItemDetails section" - {
      "constructCard" - {
          "must return rows" in new Test(baseUserAnswers) {
            helper.ItemDetails.constructCard(testIndex1, testCommodityCodeWine) must not be empty
          }
      }

      "constructEpcRow" - {
        "must return a row" in new Test(baseUserAnswers) {
          helper.ItemDetails.constructEpcRow(
            idx = testIndex1,
            cnCodeInformation = testCommodityCodeWine
          ) mustBe
            summaryListRowBuilder(
              key = ItemExciseProductCodeMessages.English.cyaLabel,
              value = HtmlContent(s"${testCommodityCodeWine.exciseProductCode}<br>${testCommodityCodeWine.exciseProductCodeDescription}"),
              changeLink = Some(ActionItem(
                href = controllers.sections.items.routes.ItemExciseProductCodeController.onPageLoad(testErn, testDraftId, testIndex1, CheckMode).url,
                content = "itemCheckAnswers.change",
                visuallyHiddenText = Some(ItemExciseProductCodeMessages.English.cyaChangeHidden)
              ))
            )
        }
      }

      "constructCommodityCodeRow" - {
        "if EPC is not S500" - {
          "must return a row with no change link" - {
            ExciseProductCode.epcsOnlyOneCnCode.foreach(
              epc =>
                s"if EPC is $epc" in new Test(baseUserAnswers) {
                  helper.ItemDetails.constructCommodityCodeRow(
                    idx = testIndex1,
                    cnCodeInformation = testCommodityCodeWine.copy(exciseProductCode = epc)
                  ) mustBe
                    Some(summaryListRowBuilder(
                      key = ItemCommodityCodeMessages.English.cyaLabel,
                      value = HtmlContent(s"${testCommodityCodeWine.cnCode}<br>${testCommodityCodeWine.cnCodeDescription}"),
                      changeLink = None
                    ))
                }
            )
          }
          "must return a row with a change link" - {
            "if EPC has more than one CN Code" in new Test(baseUserAnswers) {
              helper.ItemDetails.constructCommodityCodeRow(
                idx = testIndex1,
                cnCodeInformation = testCommodityCodeWine
              ) mustBe
                Some(summaryListRowBuilder(
                  key = ItemCommodityCodeMessages.English.cyaLabel,
                  value = HtmlContent(s"${testCommodityCodeWine.cnCode}<br>${testCommodityCodeWine.cnCodeDescription}"),
                  changeLink = Some(ActionItem(
                    href = controllers.sections.items.routes.ItemCommodityCodeController.onPageLoad(testErn, testDraftId, testIndex1, CheckMode).url,
                    content = "itemCheckAnswers.change",
                    visuallyHiddenText = Some(ItemCommodityCodeMessages.English.cyaChangeHidden)
                  ))
                ))
            }
          }
        }
        "if EPC is S500" - {
          "must not return a row" in new Test(baseUserAnswers) {
            helper.ItemDetails.constructCommodityCodeRow(
              idx = testIndex1,
              cnCodeInformation = testCommodityCodeWine.copy(exciseProductCode = "S500")
            ) mustBe None
          }
        }
      }

      "constructBrandNameRow" - {
        "if ItemBrandNamePage hasBrandName is true" - {
          "must return a row with their answer if brandName is provided" in new Test(
            baseUserAnswers
              .set(ItemBrandNamePage(testIndex1), ItemBrandNameModel(hasBrandName = true, brandName = Some("test brand name")))
          ) {
            helper.ItemDetails.constructBrandNameRow(
              idx = testIndex1
            ) mustBe
              Some(summaryListRowBuilder(
                key = ItemBrandNameMessages.English.cyaLabel,
                value = "test brand name",
                changeLink = Some(ActionItem(
                  href = controllers.sections.items.routes.ItemBrandNameController.onPageLoad(testErn, testDraftId, testIndex1, CheckMode).url,
                  content = "itemCheckAnswers.change",
                  visuallyHiddenText = Some(ItemBrandNameMessages.English.cyaChangeHidden)
                ))
              ))
          }
          "must return a row with default answer if brandName is not provided" in new Test(
            baseUserAnswers
              .set(ItemBrandNamePage(testIndex1), ItemBrandNameModel(hasBrandName = true, brandName = None))
          ) {
            helper.ItemDetails.constructBrandNameRow(
              idx = testIndex1
            ) mustBe
              Some(summaryListRowBuilder(
                key = ItemBrandNameMessages.English.cyaLabel,
                value = messagesForLang.notProvided,
                changeLink = Some(ActionItem(
                  href = controllers.sections.items.routes.ItemBrandNameController.onPageLoad(testErn, testDraftId, testIndex1, CheckMode).url,
                  content = "itemCheckAnswers.change",
                  visuallyHiddenText = Some(ItemBrandNameMessages.English.cyaChangeHidden)
                ))
              ))
          }
        }
        "if ItemBrandNamePage hasBrandName is false" - {
          "must return a row with default answer even if brandName is provided" in new Test(
            baseUserAnswers
              .set(ItemBrandNamePage(testIndex1), ItemBrandNameModel(hasBrandName = false, brandName = Some("test brand name")))
          ) {
            helper.ItemDetails.constructBrandNameRow(
              idx = testIndex1
            ) mustBe
              Some(summaryListRowBuilder(
                key = ItemBrandNameMessages.English.cyaLabel,
                value = messagesForLang.notProvided,
                changeLink = Some(ActionItem(
                  href = controllers.sections.items.routes.ItemBrandNameController.onPageLoad(testErn, testDraftId, testIndex1, CheckMode).url,
                  content = "itemCheckAnswers.change",
                  visuallyHiddenText = Some(ItemBrandNameMessages.English.cyaChangeHidden)
                ))
              ))
          }
          "must return a row with default answer if brandName is not provided" in new Test(
            baseUserAnswers
              .set(ItemBrandNamePage(testIndex1), ItemBrandNameModel(hasBrandName = false, brandName = None))
          ) {
            helper.ItemDetails.constructBrandNameRow(
              idx = testIndex1
            ) mustBe
              Some(summaryListRowBuilder(
                key = ItemBrandNameMessages.English.cyaLabel,
                value = messagesForLang.notProvided,
                changeLink = Some(ActionItem(
                  href = controllers.sections.items.routes.ItemBrandNameController.onPageLoad(testErn, testDraftId, testIndex1, CheckMode).url,
                  content = "itemCheckAnswers.change",
                  visuallyHiddenText = Some(ItemBrandNameMessages.English.cyaChangeHidden)
                ))
              ))
          }
        }
      }

      "constructCommercialDescriptionRow" - {
        "if provided" - {
          "must return a row" in new Test(
            baseUserAnswers
              .set(CommercialDescriptionPage(testIndex1), "test commercial description")
          ) {
            helper.ItemDetails.constructCommercialDescriptionRow(
              idx = testIndex1
            ) mustBe
              Some(summaryListRowBuilder(
                key = CommercialDescriptionMessages.English.cyaLabel,
                value = "test commercial description",
                changeLink = Some(ActionItem(
                  href = controllers.sections.items.routes.CommercialDescriptionController.onPageLoad(testErn, testDraftId, testIndex1, CheckMode).url,
                  content = "itemCheckAnswers.change",
                  visuallyHiddenText = Some(CommercialDescriptionMessages.English.cyaChangeHidden)
                ))
              ))
          }
        }
        "if not provided" - {
          "must not return a row" in new Test(
            baseUserAnswers
              .set(ItemBrandNamePage(testIndex1), ItemBrandNameModel(hasBrandName = false, brandName = Some("test brand name")))
          ) {
            helper.ItemDetails.constructCommercialDescriptionRow(
              idx = testIndex1
            ) mustBe None
          }
        }
      }

      "constructAlcoholStrengthRow" - {
        "if provided" - {
          "must return a row" in new Test(
            baseUserAnswers
              .set(ItemAlcoholStrengthPage(testIndex1), BigDecimal(3.14))
          ) {
            helper.ItemDetails.constructAlcoholStrengthRow(
              idx = testIndex1
            ) mustBe
              Some(summaryListRowBuilder(
                key = ItemAlcoholStrengthMessages.English.cyaLabel,
                value = s"3.14 ${ItemAlcoholStrengthMessages.English.cyaSuffix}",
                changeLink = Some(ActionItem(
                  href = controllers.sections.items.routes.ItemAlcoholStrengthController.onPageLoad(testErn, testDraftId, testIndex1, CheckMode).url,
                  content = "itemCheckAnswers.change",
                  visuallyHiddenText = Some(ItemAlcoholStrengthMessages.English.cyaChangeHidden)
                ))
              ))
          }
        }
        "if not provided" - {
          "must not return a row" in new Test(
            baseUserAnswers
          ) {
            helper.ItemDetails.constructAlcoholStrengthRow(
              idx = testIndex1
            ) mustBe None
          }
        }
      }

      "constructDegreesPlatoRow" - {
        "if provided" - {
          "and hasDegreesPlato is true" - {
            "must return a row with their answer if degreesPlato is provided" in new Test(
              baseUserAnswers
                .set(ItemDegreesPlatoPage(testIndex1), ItemDegreesPlatoModel(hasDegreesPlato = true, degreesPlato = Some(BigDecimal(1.59))))
            ) {
              helper.ItemDetails.constructDegreesPlatoRow(idx = testIndex1) mustBe
                Some(summaryListRowBuilder(
                  key = ItemDegreesPlatoMessages.English.cyaLabel,
                  value = HtmlContent(s"1.59${ItemDegreesPlatoMessages.English.degreesPlatoSuffix}"),
                  changeLink = Some(ActionItem(
                    href = controllers.sections.items.routes.ItemDegreesPlatoController.onPageLoad(testErn, testDraftId, testIndex1, CheckMode).url,
                    content = "itemCheckAnswers.change",
                    visuallyHiddenText = Some(ItemDegreesPlatoMessages.English.cyaChangeHidden)
                  ))
                ))
            }
            "must return a row with default answer if degreesPlato is not provided" in new Test(
              baseUserAnswers
                .set(ItemDegreesPlatoPage(testIndex1), ItemDegreesPlatoModel(hasDegreesPlato = true, degreesPlato = None))
            ) {
              helper.ItemDetails.constructDegreesPlatoRow(idx = testIndex1) mustBe
                Some(summaryListRowBuilder(
                  key = ItemDegreesPlatoMessages.English.cyaLabel,
                  value = HtmlContent(messagesForLang.no),
                  changeLink = Some(ActionItem(
                    href = controllers.sections.items.routes.ItemDegreesPlatoController.onPageLoad(testErn, testDraftId, testIndex1, CheckMode).url,
                    content = "itemCheckAnswers.change",
                    visuallyHiddenText = Some(ItemDegreesPlatoMessages.English.cyaChangeHidden)
                  ))
                ))
            }
          }
          "and hasDegreesPlato is false" - {
            "must return a row with default answer even if degreesPlato is provided" in new Test(
              baseUserAnswers
                .set(ItemDegreesPlatoPage(testIndex1), ItemDegreesPlatoModel(hasDegreesPlato = false, degreesPlato = Some(BigDecimal(1.59))))
            ) {
              helper.ItemDetails.constructDegreesPlatoRow(idx = testIndex1) mustBe
                Some(summaryListRowBuilder(
                  key = ItemDegreesPlatoMessages.English.cyaLabel,
                  value = HtmlContent(messagesForLang.no),
                  changeLink = Some(ActionItem(
                    href = controllers.sections.items.routes.ItemDegreesPlatoController.onPageLoad(testErn, testDraftId, testIndex1, CheckMode).url,
                    content = "itemCheckAnswers.change",
                    visuallyHiddenText = Some(ItemDegreesPlatoMessages.English.cyaChangeHidden)
                  ))
                ))
            }
            "must return a row with default answer if degreesPlato is not provided" in new Test(
              baseUserAnswers
                .set(ItemDegreesPlatoPage(testIndex1), ItemDegreesPlatoModel(hasDegreesPlato = false, degreesPlato = None))
            ) {
              helper.ItemDetails.constructDegreesPlatoRow(idx = testIndex1) mustBe
                Some(summaryListRowBuilder(
                  key = ItemDegreesPlatoMessages.English.cyaLabel,
                  value = HtmlContent(messagesForLang.no),
                  changeLink = Some(ActionItem(
                    href = controllers.sections.items.routes.ItemDegreesPlatoController.onPageLoad(testErn, testDraftId, testIndex1, CheckMode).url,
                    content = "itemCheckAnswers.change",
                    visuallyHiddenText = Some(ItemDegreesPlatoMessages.English.cyaChangeHidden)
                  ))
                ))
            }
          }
        }
        "if not provided" - {
          "must not return a row" in new Test(baseUserAnswers) {
            helper.ItemDetails.constructDegreesPlatoRow(
              idx = testIndex1
            ) mustBe None
          }
        }
      }

      "constructMaturationPeriodAgeRow" - {
        "if provided" - {
          "and hasMaturationPeriodAge is true" - {
            "must return a row with their answer if maturationPeriodAge is provided" in new Test(
              baseUserAnswers
                .set(ItemMaturationPeriodAgePage(testIndex1), ItemMaturationPeriodAgeModel(
                  hasMaturationPeriodAge = true,
                  maturationPeriodAge = Some("test maturation period age")
                ))
            ) {
              helper.ItemDetails.constructMaturationPeriodAgeRow(idx = testIndex1) mustBe
                Some(summaryListRowBuilder(
                  key = ItemMaturationPeriodAgeMessages.English.cyaLabel,
                  value = "test maturation period age",
                  changeLink = Some(ActionItem(
                    href = controllers.sections.items.routes.ItemMaturationPeriodAgeController.onPageLoad(testErn, testDraftId, testIndex1, CheckMode).url,
                    content = "itemCheckAnswers.change",
                    visuallyHiddenText = Some(ItemMaturationPeriodAgeMessages.English.cyaChangeHidden)
                  ))
                ))
            }
            "must return a row with default answer if maturationPeriodAge is not provided" in new Test(
              baseUserAnswers
                .set(ItemMaturationPeriodAgePage(testIndex1), ItemMaturationPeriodAgeModel(
                  hasMaturationPeriodAge = true,
                  maturationPeriodAge = None
                ))
            ) {
              helper.ItemDetails.constructMaturationPeriodAgeRow(idx = testIndex1) mustBe
                Some(summaryListRowBuilder(
                  key = ItemMaturationPeriodAgeMessages.English.cyaLabel,
                  value = messagesForLang.notProvided,
                  changeLink = Some(ActionItem(
                    href = controllers.sections.items.routes.ItemMaturationPeriodAgeController.onPageLoad(testErn, testDraftId, testIndex1, CheckMode).url,
                    content = "itemCheckAnswers.change",
                    visuallyHiddenText = Some(ItemMaturationPeriodAgeMessages.English.cyaChangeHidden)
                  ))
                ))
            }
          }
          "and hasMaturationPeriodAge is false" - {
            "must return a row with default answer even if maturationPeriodAge is provided" in new Test(
              baseUserAnswers
                .set(ItemMaturationPeriodAgePage(testIndex1), ItemMaturationPeriodAgeModel(
                  hasMaturationPeriodAge = false,
                  maturationPeriodAge = Some("test maturation period age")
                ))
            ) {
              helper.ItemDetails.constructMaturationPeriodAgeRow(idx = testIndex1) mustBe
                Some(summaryListRowBuilder(
                  key = ItemMaturationPeriodAgeMessages.English.cyaLabel,
                  value = messagesForLang.notProvided,
                  changeLink = Some(ActionItem(
                    href = controllers.sections.items.routes.ItemMaturationPeriodAgeController.onPageLoad(testErn, testDraftId, testIndex1, CheckMode).url,
                    content = "itemCheckAnswers.change",
                    visuallyHiddenText = Some(ItemMaturationPeriodAgeMessages.English.cyaChangeHidden)
                  ))
                ))
            }
            "must return a row with default answer if maturationPeriodAge is not provided" in new Test(
              baseUserAnswers
                .set(ItemMaturationPeriodAgePage(testIndex1), ItemMaturationPeriodAgeModel(
                  hasMaturationPeriodAge = false,
                  maturationPeriodAge = None
                ))
            ) {
              helper.ItemDetails.constructMaturationPeriodAgeRow(idx = testIndex1) mustBe
                Some(summaryListRowBuilder(
                  key = ItemMaturationPeriodAgeMessages.English.cyaLabel,
                  value = messagesForLang.notProvided,
                  changeLink = Some(ActionItem(
                    href = controllers.sections.items.routes.ItemMaturationPeriodAgeController.onPageLoad(testErn, testDraftId, testIndex1, CheckMode).url,
                    content = "itemCheckAnswers.change",
                    visuallyHiddenText = Some(ItemMaturationPeriodAgeMessages.English.cyaChangeHidden)
                  ))
                ))
            }
          }
        }
        "if not provided" - {
          "must not return a row" in new Test(baseUserAnswers) {
            helper.ItemDetails.constructMaturationPeriodAgeRow(
              idx = testIndex1
            ) mustBe None
          }
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
                  content = "itemCheckAnswers.change",
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
                value = messagesForLang.yes,
                changeLink = Some(ActionItem(
                  href = controllers.sections.items.routes.ItemFiscalMarksChoiceController.onPageLoad(testErn, testDraftId, testIndex1, CheckMode).url,
                  content = "itemCheckAnswers.change",
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
                value = messagesForLang.no,
                changeLink = Some(ActionItem(
                  href = controllers.sections.items.routes.ItemFiscalMarksChoiceController.onPageLoad(testErn, testDraftId, testIndex1, CheckMode).url,
                  content = "itemCheckAnswers.change",
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
                  content = "itemCheckAnswers.change",
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
                      content = "itemCheckAnswers.change",
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
                      content = "itemCheckAnswers.change",
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
                value = messagesForLang.yes,
                changeLink = Some(ActionItem(
                  href = controllers.sections.items.routes.ItemSmallIndependentProducerController.onPageLoad(testErn, testDraftId, testIndex1, CheckMode).url,
                  content = "itemCheckAnswers.change",
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
                value = messagesForLang.no,
                changeLink = Some(ActionItem(
                  href = controllers.sections.items.routes.ItemSmallIndependentProducerController.onPageLoad(testErn, testDraftId, testIndex1, CheckMode).url,
                  content = "itemCheckAnswers.change",
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
                  content = "itemCheckAnswers.change",
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
  }
}
