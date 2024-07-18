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
import fixtures.messages.sections.guarantor.GuarantorRequiredMessages
import fixtures.messages.sections.guarantor.GuarantorRequiredMessages.ViewMessages
import models.CheckMode
import models.sections.info.movementScenario.MovementScenario.{EuTaxWarehouse, ExportWithCustomsDeclarationLodgedInTheUk, RegisteredConsignee}
import models.sections.journeyType.HowMovementTransported.{AirTransport, FixedTransportInstallations, InlandWaterwayTransport}
import models.sections.transportUnit.TransportUnitType.FixedTransport
import pages.sections.guarantor.GuarantorRequiredPage
import pages.sections.transportUnit.TransportUnitTypePage
import pages.sections.info.DestinationTypePage
import pages.sections.journeyType.HowMovementTransportedPage
import play.api.i18n.Messages
import play.api.test.FakeRequest
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.{Key, SummaryListRow, Value}
import viewmodels.govuk.summarylist._


class GuarantorRequiredSummarySpec extends SpecBase {

  private def expectedRow(value: String, ern: String = testErn)(implicit messagesForLanguage: ViewMessages): Option[SummaryListRow] = {
    Some(
      SummaryListRowViewModel(
        key = Key(Text(messagesForLanguage.cyaLabel)),
        value = Value(Text(value)),
        actions = Seq(ActionItemViewModel(
          content = Text(messagesForLanguage.change),
          href = controllers.sections.guarantor.routes.GuarantorRequiredController.onPageLoad(ern, testDraftId, CheckMode).url,
          id = "changeGuarantorRequired"
        ).withVisuallyHiddenText(messagesForLanguage.cyaChangeHidden))
      )
    )
  }

  Seq(GuarantorRequiredMessages.English).foreach { implicit messagesForLanguage =>

    s"when language is set to ${messagesForLanguage.lang.code}" - {

      implicit val msgs: Messages = messages(Seq(messagesForLanguage.lang))

      "and there is no answer for the GuarantorRequiredPage" - {

        "then must return a not provided row" in {

          implicit lazy val request = dataRequest(FakeRequest(), emptyUserAnswers, testErn)

          GuarantorRequiredSummary.row mustBe expectedRow(value = messagesForLanguage.notProvided)
        }
      }

      "and there is a GuarantorRequiredPage answer of yes" - {

        "then must return a row with the answer of yes " in {

          implicit lazy val request = dataRequest(FakeRequest(), emptyUserAnswers.set(GuarantorRequiredPage, true), testErn)

          GuarantorRequiredSummary.row mustBe expectedRow(value = messagesForLanguage.yes)
        }
      }

      "and there is a GuarantorRequiredPage answer of no" - {

        "then must return a row with the answer " in {

          implicit lazy val request = dataRequest(FakeRequest(), emptyUserAnswers.set(GuarantorRequiredPage, false), testErn)

          GuarantorRequiredSummary.row mustBe expectedRow(value = messagesForLanguage.no)
        }
      }

      "and guarantorAlwaysRequired is true" - {

        "then must return a row with the answer of yes " in {

          implicit lazy val request = dataRequest(FakeRequest(), emptyUserAnswers
            .set(DestinationTypePage, ExportWithCustomsDeclarationLodgedInTheUk)
            .set(GuarantorRequiredPage, true)
          )

          GuarantorRequiredSummary.row mustBe None
        }
      }

      "and guarantorAlwaysRequiredNIToEU is true" - {

        "then must return a row with the answer of yes " in {

          implicit lazy val request = dataRequest(FakeRequest(), emptyUserAnswers
            .set(DestinationTypePage, EuTaxWarehouse)
            .set(HowMovementTransportedPage, AirTransport)
          )

          GuarantorRequiredSummary.row mustBe None
        }
      }

      "and HowMovementTransportedPage is FixedTransportInstallations" - {

        "then must return a row with the answer of yes " in {

          implicit lazy val request = dataRequest(
            request = FakeRequest(),
            answers = emptyUserAnswers
              .set(GuarantorRequiredPage, true)
              .set(HowMovementTransportedPage, FixedTransportInstallations)
              .set(DestinationTypePage, RegisteredConsignee)
              .set(TransportUnitTypePage(0), FixedTransport),
            ern = testGreatBritainWarehouseKeeperErn
          )

          GuarantorRequiredSummary.row mustBe expectedRow(value = messagesForLanguage.yes, ern = testGreatBritainWarehouseKeeperErn)
        }
      }

      "and nonUkToEuMovement is true and HowMovementTransportedPage is NOT FixedTransportInstallations" - {

        "then must return a row with the answer of yes" in {

          implicit lazy val request = dataRequest(
            request = FakeRequest(),
            answers = emptyUserAnswers
              .set(GuarantorRequiredPage, true)
              .set(HowMovementTransportedPage, InlandWaterwayTransport)
              .set(DestinationTypePage, RegisteredConsignee),
            ern = testGreatBritainWarehouseKeeperErn
          )

          GuarantorRequiredSummary.row mustBe None
        }
      }
    }
  }
}
