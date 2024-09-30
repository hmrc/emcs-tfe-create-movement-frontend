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
import fixtures.messages.sections.items.ItemsAddToListMessages
import fixtures.{ItemFixtures, MovementSubmissionFailureFixtures}
import mocks.services.MockGetCnCodeInformationService
import models.UserAnswers
import models.requests.DataRequest
import models.sections.info.movementScenario.MovementScenario
import models.sections.info.movementScenario.MovementScenario.ExportWithCustomsDeclarationLodgedInTheUk
import pages.sections.info.DestinationTypePage
import play.api.i18n.Messages
import play.api.test.FakeRequest
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.govukfrontend.views.html.components.GovukSummaryList
import uk.gov.hmrc.http.HeaderCarrier
import viewmodels.checkAnswers.sections.consignee.ConsigneeCheckAnswersHelper
import viewmodels.checkAnswers.sections.consignor.ConsignorCheckAnswersHelper
import viewmodels.checkAnswers.sections.destination.DestinationCheckAnswersHelper
import viewmodels.checkAnswers.sections.dispatch.DispatchCheckAnswersHelper
import viewmodels.checkAnswers.sections.exportInformation.ExportInformationCheckAnswersHelper
import viewmodels.checkAnswers.sections.firstTransporter.FirstTransporterCheckAnswersHelper
import viewmodels.checkAnswers.sections.guarantor.GuarantorCheckAnswersHelper
import viewmodels.checkAnswers.sections.info.InformationCheckAnswersHelper
import viewmodels.checkAnswers.sections.transportArranger.TransportArrangerCheckAnswersHelper

import scala.concurrent.ExecutionContext

class FinalCheckYourAnswersHelperSpec extends SpecBase
  with ItemFixtures
  with MockGetCnCodeInformationService
  with MovementSubmissionFailureFixtures {

  implicit lazy val ec: ExecutionContext = app.injector.instanceOf[ExecutionContext]
  implicit lazy val hc: HeaderCarrier = HeaderCarrier()

  implicit lazy val informationCheckAnswersHelper: InformationCheckAnswersHelper = app.injector.instanceOf[InformationCheckAnswersHelper]
  implicit lazy val consignorCheckAnswersHelper: ConsignorCheckAnswersHelper = app.injector.instanceOf[ConsignorCheckAnswersHelper]
  implicit lazy val dispatchCheckAnswersHelper: DispatchCheckAnswersHelper = app.injector.instanceOf[DispatchCheckAnswersHelper]
  implicit lazy val consigneeCheckAnswersHelper: ConsigneeCheckAnswersHelper = app.injector.instanceOf[ConsigneeCheckAnswersHelper]
  implicit lazy val destinationCheckAnswersHelper: DestinationCheckAnswersHelper = app.injector.instanceOf[DestinationCheckAnswersHelper]
  implicit lazy val importHelper: CheckYourAnswersImportHelper = app.injector.instanceOf[CheckYourAnswersImportHelper]
  implicit lazy val exportInformationCheckAnswersHelper: ExportInformationCheckAnswersHelper = app.injector.instanceOf[ExportInformationCheckAnswersHelper]
  implicit lazy val guarantorCheckAnswersHelper: GuarantorCheckAnswersHelper = app.injector.instanceOf[GuarantorCheckAnswersHelper]
  implicit lazy val journeyTypeHelper: CheckYourAnswersJourneyTypeHelper = app.injector.instanceOf[CheckYourAnswersJourneyTypeHelper]
  implicit lazy val transportArrangerCheckAnswersHelper: TransportArrangerCheckAnswersHelper = app.injector.instanceOf[TransportArrangerCheckAnswersHelper]
  implicit lazy val transportUnitsAddToListHelper: TransportUnitsAddToListHelper = app.injector.instanceOf[TransportUnitsAddToListHelper]
  implicit lazy val firstTransporterCheckAnswersHelper: FirstTransporterCheckAnswersHelper = app.injector.instanceOf[FirstTransporterCheckAnswersHelper]
  implicit lazy val sadAddToListHelper: SadAddToListHelper = app.injector.instanceOf[SadAddToListHelper]
  implicit lazy val documentsAddToListHelper: DocumentsAddToListHelper = app.injector.instanceOf[DocumentsAddToListHelper]
  implicit lazy val govukSummary: GovukSummaryList = app.injector.instanceOf[GovukSummaryList]

  lazy val helper = app.injector.instanceOf[FinalCheckYourAnswersHelper]

  val headingLevel = 2

  class Setup(userAnswers: UserAnswers = emptyUserAnswers,
              ern: String = testGreatBritainWarehouseKeeperErn) {
    implicit lazy val request: DataRequest[_] = dataRequest(FakeRequest(), userAnswers, ern)
  }

  "FinalCheckYourAnswersHelper" - {

    Seq(ItemsAddToListMessages.English).foreach { messagesForLanguage =>

      implicit lazy val msgs: Messages = messages(Seq(messagesForLanguage.lang))

      s"when rendered for language of '${messagesForLanguage.lang.code}'" - {

        ".cya" - {

          "render the correct data when is a deferred movement" in new Setup(baseFullUserAnswers) {

            helper.cya(deferredMovement = true, None) mustBe HtmlFormat.fill(Seq(
              govukSummary(informationCheckAnswersHelper.summaryList(deferredMovement = true, asCard = true)),
              govukSummary(consignorCheckAnswersHelper.summaryList(asCard = true)),
              govukSummary(dispatchCheckAnswersHelper.summaryList(asCard = true)),
              govukSummary(consigneeCheckAnswersHelper.summaryList(asCard = true)),
              govukSummary(destinationCheckAnswersHelper.summaryList(asCard = true)),
              govukSummary(guarantorCheckAnswersHelper.summaryList(asCard = true)),
              govukSummary(journeyTypeHelper.summaryList(asCard = true)),
              govukSummary(transportArrangerCheckAnswersHelper.summaryList(asCard = true)),
              govukSummary(transportUnitsAddToListHelper.finalCyaSummary().get),
              govukSummary(firstTransporterCheckAnswersHelper.summaryList(asCard = true)),
              govukSummary(documentsAddToListHelper.finalCyaSummary())
            ))
          }

          "render the correct data when NOT a deferred movement" in new Setup(baseFullUserAnswers) {

            helper.cya(deferredMovement = false, None) mustBe HtmlFormat.fill(Seq(
              govukSummary(informationCheckAnswersHelper.summaryList(deferredMovement = false, asCard = true)),
              govukSummary(consignorCheckAnswersHelper.summaryList(asCard = true)),
              govukSummary(dispatchCheckAnswersHelper.summaryList(asCard = true)),
              govukSummary(consigneeCheckAnswersHelper.summaryList(asCard = true)),
              govukSummary(destinationCheckAnswersHelper.summaryList(asCard = true)),
              govukSummary(guarantorCheckAnswersHelper.summaryList(asCard = true)),
              govukSummary(journeyTypeHelper.summaryList(asCard = true)),
              govukSummary(transportArrangerCheckAnswersHelper.summaryList(asCard = true)),
              govukSummary(transportUnitsAddToListHelper.finalCyaSummary().get),
              govukSummary(firstTransporterCheckAnswersHelper.summaryList(asCard = true)),
              govukSummary(documentsAddToListHelper.finalCyaSummary())
            ))
          }

          "render the correct data for Registered Consignor (Import variant)" in new Setup(
            baseFullUserAnswers,
            testNIRegisteredConsignorErn
          ) {

            helper.cya(deferredMovement = false, None) mustBe HtmlFormat.fill(Seq(
              govukSummary(informationCheckAnswersHelper.summaryList(deferredMovement = false, asCard = true)),
              govukSummary(consignorCheckAnswersHelper.summaryList(asCard = true)),
              govukSummary(importHelper.summaryList(asCard = true)),
              govukSummary(consigneeCheckAnswersHelper.summaryList(asCard = true)),
              govukSummary(destinationCheckAnswersHelper.summaryList(asCard = true)),
              govukSummary(guarantorCheckAnswersHelper.summaryList(asCard = true)),
              govukSummary(journeyTypeHelper.summaryList(asCard = true)),
              govukSummary(transportArrangerCheckAnswersHelper.summaryList(asCard = true)),
              govukSummary(transportUnitsAddToListHelper.finalCyaSummary().get),
              govukSummary(firstTransporterCheckAnswersHelper.summaryList(asCard = true)),
              govukSummary(sadAddToListHelper.finalCyaSummary().get),
              govukSummary(documentsAddToListHelper.finalCyaSummary())
            ))
          }

          "render the correct data for Unknown Destination (no Consignee or Destination)" in new Setup(
            baseFullUserAnswers.set(DestinationTypePage, MovementScenario.UnknownDestination)
          ) {

            helper.cya(deferredMovement = false, None) mustBe HtmlFormat.fill(Seq(
              govukSummary(informationCheckAnswersHelper.summaryList(deferredMovement = false, asCard = true)),
              govukSummary(consignorCheckAnswersHelper.summaryList(asCard = true)),
              govukSummary(dispatchCheckAnswersHelper.summaryList(asCard = true)),
              govukSummary(guarantorCheckAnswersHelper.summaryList(asCard = true)),
              govukSummary(journeyTypeHelper.summaryList(asCard = true)),
              govukSummary(transportArrangerCheckAnswersHelper.summaryList(asCard = true)),
              govukSummary(transportUnitsAddToListHelper.finalCyaSummary().get),
              govukSummary(firstTransporterCheckAnswersHelper.summaryList(asCard = true)),
              govukSummary(documentsAddToListHelper.finalCyaSummary())
            ))
          }

          "render the correct data for an Export" in new Setup(
            baseFullUserAnswers.set(DestinationTypePage, ExportWithCustomsDeclarationLodgedInTheUk)
          ) {

            helper.cya(deferredMovement = false, None) mustBe HtmlFormat.fill(Seq(
              govukSummary(informationCheckAnswersHelper.summaryList(deferredMovement = false, asCard = true)),
              govukSummary(consignorCheckAnswersHelper.summaryList(asCard = true)),
              govukSummary(dispatchCheckAnswersHelper.summaryList(asCard = true)),
              govukSummary(consigneeCheckAnswersHelper.summaryList(asCard = true)),
              govukSummary(exportInformationCheckAnswersHelper.summaryList(asCard = true)),
              govukSummary(guarantorCheckAnswersHelper.summaryList(asCard = true)),
              govukSummary(journeyTypeHelper.summaryList(asCard = true)),
              govukSummary(transportArrangerCheckAnswersHelper.summaryList(asCard = true)),
              govukSummary(transportUnitsAddToListHelper.finalCyaSummary().get),
              govukSummary(firstTransporterCheckAnswersHelper.summaryList(asCard = true)),
              govukSummary(documentsAddToListHelper.finalCyaSummary())
            ))
          }
        }
      }
    }
  }
}
