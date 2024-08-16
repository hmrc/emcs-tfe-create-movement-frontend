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
import fixtures.messages.sections.guarantor.GuarantorAddressMessages
import fixtures.messages.sections.guarantor.GuarantorAddressMessages.ViewMessages
import models.CheckMode
import models.requests.DataRequest
import models.sections.guarantor.GuarantorArranger
import models.sections.guarantor.GuarantorArranger.{Consignee, Consignor, GoodsOwner, Transporter}
import models.sections.info.movementScenario.MovementScenario.{ExportWithCustomsDeclarationLodgedInTheUk, UkTaxWarehouse}
import pages.sections.consignee.ConsigneeAddressPage
import pages.sections.consignor.ConsignorAddressPage
import pages.sections.guarantor.{GuarantorAddressPage, GuarantorArrangerPage, GuarantorRequiredPage}
import pages.sections.info.DestinationTypePage
import play.api.i18n.Messages
import play.api.test.FakeRequest
import play.twirl.api.{Html, HtmlFormat}
import uk.gov.hmrc.govukfrontend.views.Aliases.Value
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.{Content, HtmlContent, Text}
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.{Key, SummaryListRow}
import viewmodels.govuk.summarylist._
import viewmodels.implicits._

class GuarantorAddressSummarySpec extends SpecBase {

  def expectedRow(arranger: GuarantorArranger, value: Content, showChangeLink: Boolean = true)(implicit messagesForLanguage: ViewMessages): Option[SummaryListRow] = {
    Some(
      SummaryListRowViewModel(
        key = Key(Text(messagesForLanguage.cyaLabel(arranger))),
        value = Value(value),
        actions = if (!showChangeLink) Seq() else Seq(ActionItemViewModel(
          content = Text(messagesForLanguage.change),
          href = controllers.sections.guarantor.routes.GuarantorAddressController.onPageLoad(testGreatBritainWarehouseKeeperErn, testDraftId, CheckMode).url,
          id = "changeGuarantorAddress"
        ).withVisuallyHiddenText(messagesForLanguage.cyaChangeHidden(arranger)))
      )
    )
  }

  "GuarantorAddressSummary" - {

    Seq(GuarantorAddressMessages.English).foreach { implicit messagesForLanguage =>

      implicit val msgs: Messages = messages(Seq(messagesForLanguage.lang))

      s"when being rendered in lang code of '${messagesForLanguage.lang.code}'" - {

        "when Guarantor is optional" - {

          "when GuarantorRequired is true" - {

            Seq(GoodsOwner, Transporter).foreach { arranger =>

              s"when the Guarantor is ${arranger.getClass.getSimpleName.stripSuffix("$")}" - {

                "when there's no answer" - {

                  "must output the expected data" in {

                    implicit lazy val request: DataRequest[_] = dataRequest(
                      FakeRequest(),
                      emptyUserAnswers
                        .set(DestinationTypePage, UkTaxWarehouse.GB)
                        .set(GuarantorRequiredPage, true)
                        .set(GuarantorArrangerPage, arranger),
                      testGreatBritainWarehouseKeeperErn
                    )

                    GuarantorAddressSummary.row() mustBe expectedRow(arranger, messagesForLanguage.notProvided, true)
                  }
                }

                "when there's an answer" - {

                  "must output the expected row" in {

                    implicit lazy val request: DataRequest[_] = dataRequest(FakeRequest(), emptyUserAnswers
                      .set(DestinationTypePage, UkTaxWarehouse.GB)
                      .set(GuarantorRequiredPage, true)
                      .set(GuarantorArrangerPage, arranger)
                      .set(GuarantorAddressPage, testUserAddress),
                      testGreatBritainWarehouseKeeperErn
                    )

                    val expectedValue = HtmlContent(
                      HtmlFormat.fill(
                        Seq(
                          Html(testUserAddress.businessName.value + "<br>"),
                          Html(testUserAddress.property.fold("")(_ + " ") + testUserAddress.street.value + "<br>"),
                          Html(testUserAddress.town.value + "<br>"),
                          Html(testUserAddress.postcode.value),
                        )
                      )
                    )

                    GuarantorAddressSummary.row() mustBe expectedRow(arranger, expectedValue, true)
                  }
                }
              }
            }

            "when the Guarantor is Consignor" - {

              "when there's no answer for the ConsignorAddressPage" - {

                "must output the expected data" in {

                  implicit lazy val request: DataRequest[_] = dataRequest(
                    FakeRequest(),
                    emptyUserAnswers
                      .set(DestinationTypePage, UkTaxWarehouse.GB)
                      .set(GuarantorRequiredPage, true)
                      .set(GuarantorArrangerPage, Consignor),
                    testGreatBritainWarehouseKeeperErn
                  )

                  GuarantorAddressSummary.row() mustBe expectedRow(GuarantorArranger.Consignor, Text(messagesForLanguage.sectionNotComplete("Consignor")), false)
                }
              }

              "when there's an answer for the ConsignorAddressPage" - {

                "must output the expected row" in {

                  implicit lazy val request: DataRequest[_] = dataRequest(FakeRequest(), emptyUserAnswers
                    .set(DestinationTypePage, UkTaxWarehouse.GB)
                    .set(GuarantorRequiredPage, true)
                    .set(GuarantorArrangerPage, Consignor)
                    .set(ConsignorAddressPage, testUserAddress),
                    testGreatBritainWarehouseKeeperErn
                  )

                  val expectedValue = HtmlContent(
                    HtmlFormat.fill(
                      Seq(
                        Html(testMinTraderKnownFacts.traderName + "<br>"),
                        Html(testUserAddress.property.fold("")(_ + " ") + testUserAddress.street.value + "<br>"),
                        Html(testUserAddress.town.value + "<br>"),
                        Html(testUserAddress.postcode.value),
                      )
                    )
                  )

                  GuarantorAddressSummary.row() mustBe expectedRow(GuarantorArranger.Consignor, expectedValue, false)
                }
              }
            }

            "when the Guarantor is Consignee" - {

              "when there's no answer for the ConsigneeAddressPage" - {

                "must output the expected data" in {

                  implicit lazy val request: DataRequest[_] = dataRequest(
                    FakeRequest(),
                    emptyUserAnswers
                      .set(DestinationTypePage, UkTaxWarehouse.GB)
                      .set(GuarantorRequiredPage, true)
                      .set(GuarantorArrangerPage, Consignee),
                    testGreatBritainWarehouseKeeperErn
                  )

                  GuarantorAddressSummary.row() mustBe expectedRow(GuarantorArranger.Consignee, Text(messagesForLanguage.sectionNotComplete("Consignee")), false)
                }
              }

              "when there's an answer for the ConsigneeAddressPage" - {

                "must output the expected row" in {

                  implicit lazy val request: DataRequest[_] = dataRequest(FakeRequest(), emptyUserAnswers
                    .set(DestinationTypePage, UkTaxWarehouse.GB)
                    .set(GuarantorRequiredPage, true)
                    .set(GuarantorArrangerPage, Consignee)
                    .set(ConsigneeAddressPage, testUserAddress),
                    testGreatBritainWarehouseKeeperErn
                  )

                  val expectedValue = HtmlContent(
                    HtmlFormat.fill(
                      Seq(
                        Html(testUserAddress.businessName.value + "<br>"),
                        Html(testUserAddress.property.fold("")(_ + " ") + testUserAddress.street.value + "<br>"),
                        Html(testUserAddress.town.value + "<br>"),
                        Html(testUserAddress.postcode.value),
                      )
                    )
                  )

                  GuarantorAddressSummary.row() mustBe expectedRow(GuarantorArranger.Consignee, expectedValue, false)
                }
              }
            }
          }

          "when GuarantorRequired is false" - {

            "must output the expected data" in {

              implicit lazy val request: DataRequest[_] = dataRequest(
                FakeRequest(),
                emptyUserAnswers
                  .set(DestinationTypePage, UkTaxWarehouse.GB)
                  .set(GuarantorRequiredPage, false)
                  .set(GuarantorArrangerPage, Consignor),
                testGreatBritainWarehouseKeeperErn
              )

              GuarantorAddressSummary.row() mustBe None
            }
          }
        }

        "when Guarantor is always required " - {

          "when GuarantorRequired is unanswered" - {

            "must output section incomplete" in {

              implicit lazy val request: DataRequest[_] = dataRequest(
                FakeRequest(),
                emptyUserAnswers
                  .set(GuarantorArrangerPage, Consignor)
                  .set(DestinationTypePage, ExportWithCustomsDeclarationLodgedInTheUk),
                testGreatBritainWarehouseKeeperErn
              )

              GuarantorAddressSummary.row() mustBe expectedRow(GuarantorArranger.Consignor, Text(messagesForLanguage.sectionNotComplete("Consignor")), false)
            }
          }
        }
      }
    }
  }
}
