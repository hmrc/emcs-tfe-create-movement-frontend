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

import controllers.sections.items.routes
import models.requests.DataRequest
import models.response.referenceData.CnCodeInformation
import models.sections.items.ItemGeographicalIndicationType
import models.{CheckMode, Index, UnitOfMeasure}
import pages.sections.items._
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.Aliases.Key
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.{Content, HtmlContent}
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.{ActionItem, Actions, SummaryListRow, Value}
import viewmodels.checkAnswers.sections.items._
import viewmodels.implicits._
import views.html.components.p

import javax.inject.Inject

class ItemCheckAnswersHelper @Inject()(
                                        itemExciseProductCodeSummary: ItemExciseProductCodeSummary,
                                        itemCommodityCodeSummary: ItemCommodityCodeSummary,
                                        p: p
                                      ) {

  private def summaryListRowBuilder(key: Content, value: Content, changeLink: Option[ActionItem]) = SummaryListRow(
    Key(key),
    Value(value),
    classes = "govuk-summary-list__row",
    actions = changeLink.map(actionItem => Actions(items = Seq(actionItem)))
  )

  object ItemDetails {
    def constructCard(idx: Index, cnCodeInformation: CnCodeInformation)
                     (implicit request: DataRequest[_], messages: Messages): Seq[SummaryListRow] = {
      Seq(
        Some(itemExciseProductCodeSummary.row(idx, cnCodeInformation, CheckMode)),
        itemCommodityCodeSummary.row(idx, cnCodeInformation, CheckMode),
        ItemBrandNameSummary.row(idx),
        constructCommercialDescriptionRow(idx),
        constructAlcoholStrengthRow(idx),
        constructDegreesPlatoRow(idx),
        constructMaturationPeriodAgeRow(idx),
        constructDensityRow(idx, cnCodeInformation.unitOfMeasure),
        constructFiscalMarksChoiceRow(idx),
        constructFiscalMarksRow(idx),
        constructGeographicalIndicationChoiceRow(idx),
        constructGeographicalIndicationRow(idx),
        constructSmallIndependentProducerRow(idx),
        constructProducerSizeRow(idx),
      ).flatten
    }

    private[helpers] def constructCommercialDescriptionRow(idx: Index)
                                                          (implicit request: DataRequest[_], messages: Messages): Option[SummaryListRow] = {
      lazy val page = ItemCommercialDescriptionPage(idx)

      request.userAnswers.get(page).map {
        answer =>
          summaryListRowBuilder(
            key = s"$page.checkYourAnswersLabel",
            value = answer,
            changeLink = Some(ActionItem(
              href = routes.ItemCommercialDescriptionController.onPageLoad(request.ern, request.draftId, idx, CheckMode).url,
              content = "itemCheckAnswers.change",
              visuallyHiddenText = Some(messages(s"$page.change.hidden"))
            ))
          )
      }
    }

    private[helpers] def constructAlcoholStrengthRow(idx: Index)
                                                    (implicit request: DataRequest[_], messages: Messages): Option[SummaryListRow] = {
      lazy val page = ItemAlcoholStrengthPage(idx)

      request.userAnswers.get(page).map {
        answer =>
          summaryListRowBuilder(
            key = s"$page.checkYourAnswersLabel",
            value = messages("itemCheckAnswers.alcoholStrength.value", answer),
            changeLink = Some(ActionItem(
              href = routes.ItemAlcoholStrengthController.onPageLoad(request.ern, request.draftId, idx, CheckMode).url,
              content = "itemCheckAnswers.change",
              visuallyHiddenText = Some(messages(s"$page.change.hidden"))
            ))
          )
      }
    }

    private[helpers] def constructDegreesPlatoRow(idx: Index)
                                                 (implicit request: DataRequest[_], messages: Messages): Option[SummaryListRow] = {
      lazy val page = ItemDegreesPlatoPage(idx)

      def row(value: String = "site.no"): SummaryListRow = summaryListRowBuilder(
        key = s"$page.checkYourAnswersLabel",
        value = HtmlContent(messages(value)),
        changeLink = Some(ActionItem(
          href = routes.ItemDegreesPlatoController.onPageLoad(request.ern, request.draftId, idx, CheckMode).url,
          content = "itemCheckAnswers.change",
          visuallyHiddenText = Some(messages(s"$page.change.hidden"))
        ))
      )

      request.userAnswers.get(page).map {
        answer =>
          if (answer.hasDegreesPlato) {
            answer.degreesPlato
              .map(degreesPlato => row(messages("itemCheckAnswers.degreesPlato.value", degreesPlato)))
              .getOrElse(row())
          } else {
            row()
          }
      }
    }

    private[helpers] def constructMaturationPeriodAgeRow(idx: Index)
                                                        (implicit request: DataRequest[_], messages: Messages): Option[SummaryListRow] = {
      lazy val page = ItemMaturationPeriodAgePage(idx)

      def row(value: String = "itemCheckAnswers.notProvided"): SummaryListRow = summaryListRowBuilder(
        key = s"$page.checkYourAnswersLabel",
        value = value,
        changeLink = Some(ActionItem(
          href = routes.ItemMaturationPeriodAgeController.onPageLoad(request.ern, request.draftId, idx, CheckMode).url,
          content = "itemCheckAnswers.change",
          visuallyHiddenText = Some(messages(s"$page.change.hidden"))
        ))
      )

      request.userAnswers.get(page).map {
        answer =>
          if (answer.hasMaturationPeriodAge) {
            answer.maturationPeriodAge
              .map(maturationPeriodAge => row(maturationPeriodAge))
              .getOrElse(row())
          } else {
            row()
          }
      }
    }

    private[helpers] def constructDensityRow(idx: Index, unitOfMeasure: UnitOfMeasure)
                                            (implicit request: DataRequest[_], messages: Messages): Option[SummaryListRow] = {
      lazy val page = ItemDensityPage(idx)

      request.userAnswers.get(page).map {
        answer =>
          summaryListRowBuilder(
            key = HtmlContent(messages(s"$page.checkYourAnswersLabel")),
            value = HtmlContent(messages("itemCheckAnswers.density.value", answer, messages(s"itemCheckAnswers.density.value.$unitOfMeasure"))),
            changeLink = Some(ActionItem(
              href = routes.ItemDensityController.onPageLoad(request.ern, request.draftId, idx, CheckMode).url,
              content = "itemCheckAnswers.change",
              visuallyHiddenText = Some(messages(s"$page.change.hidden"))
            ))
          )
      }
    }

    private[helpers] def constructFiscalMarksChoiceRow(idx: Index)
                                                      (implicit request: DataRequest[_], messages: Messages): Option[SummaryListRow] = {
      lazy val page = ItemFiscalMarksChoicePage(idx)

      request.userAnswers.get(page).map {
        answer =>
          summaryListRowBuilder(
            key = s"$page.checkYourAnswersLabel",
            value = if (answer) "site.yes" else "site.no",
            changeLink = Some(ActionItem(
              href = routes.ItemFiscalMarksChoiceController.onPageLoad(request.ern, request.draftId, idx, CheckMode).url,
              content = "itemCheckAnswers.change",
              visuallyHiddenText = Some(messages(s"$page.change.hidden"))
            ))
          )
      }
    }

    private[helpers] def constructFiscalMarksRow(idx: Index)
                                                (implicit request: DataRequest[_], messages: Messages): Option[SummaryListRow] = {
      lazy val page = ItemFiscalMarksPage(idx)

      for {
        fiscalMarksChoiceAnswer <- request.userAnswers.get(ItemFiscalMarksChoicePage(idx))
        answer <- request.userAnswers.get(page)
        if fiscalMarksChoiceAnswer
      } yield {
        summaryListRowBuilder(
          key = s"$page.checkYourAnswersLabel",
          value = answer,
          changeLink = Some(ActionItem(
            href = routes.ItemFiscalMarksController.onPageLoad(request.ern, request.draftId, idx, CheckMode).url,
            content = "itemCheckAnswers.change",
            visuallyHiddenText = Some(messages(s"$page.change.hidden"))
          ))
        )
      }
    }

    private[helpers] def constructGeographicalIndicationChoiceRow(idx: Index)
                                                                 (implicit request: DataRequest[_], messages: Messages): Option[SummaryListRow] = {
      lazy val page = ItemGeographicalIndicationChoicePage(idx)

      request.userAnswers.get(page).map {
        answer =>
          summaryListRowBuilder(
            key = s"$page.checkYourAnswersLabel",
            value = s"$page.checkYourAnswers.value.$answer",
            changeLink = Some(ActionItem(
              href = routes.ItemGeographicalIndicationChoiceController.onPageLoad(request.ern, request.draftId, idx, CheckMode).url,
              content = "itemCheckAnswers.change",
              visuallyHiddenText = Some(messages(s"$page.change.hidden"))
            ))
          )
      }
    }

    private[helpers] def constructGeographicalIndicationRow(idx: Index)
                                                           (implicit request: DataRequest[_], messages: Messages): Option[SummaryListRow] = {
      lazy val page = ItemGeographicalIndicationPage(idx)

      for {
        itemGeographicalIndicationChoice <- request.userAnswers.get(ItemGeographicalIndicationChoicePage(idx))
        answer <- request.userAnswers.get(page)
        if itemGeographicalIndicationChoice != ItemGeographicalIndicationType.NoGeographicalIndication
      } yield {
        summaryListRowBuilder(
          key = s"$page.checkYourAnswersLabel",
          value = answer,
          changeLink = Some(ActionItem(
            href = routes.ItemGeographicalIndicationController.onPageLoad(request.ern, request.draftId, idx, CheckMode).url,
            content = "itemCheckAnswers.change",
            visuallyHiddenText = Some(messages(s"$page.change.hidden"))
          ))
        )
      }
    }

    private[helpers] def constructSmallIndependentProducerRow(idx: Index)
                                                             (implicit request: DataRequest[_], messages: Messages): Option[SummaryListRow] = {
      lazy val page = ItemSmallIndependentProducerPage(idx)

      request.userAnswers.get(page).map {
        answer =>
          summaryListRowBuilder(
            key = s"$page.checkYourAnswersLabel",
            value = if (answer) "site.yes" else "site.no",
            changeLink = Some(ActionItem(
              href = routes.ItemSmallIndependentProducerController.onPageLoad(request.ern, request.draftId, idx, CheckMode).url,
              content = "itemCheckAnswers.change",
              visuallyHiddenText = Some(messages(s"$page.change.hidden"))
            ))
          )
      }
    }

    private[helpers] def constructProducerSizeRow(idx: Index)
                                                 (implicit request: DataRequest[_], messages: Messages): Option[SummaryListRow] = {
      lazy val page = ItemProducerSizePage(idx)

      for {
        itemSmallIndependentProducer <- request.userAnswers.get(ItemSmallIndependentProducerPage(idx))
        answer <- request.userAnswers.get(page)
        if itemSmallIndependentProducer
      } yield {
        summaryListRowBuilder(
          key = s"$page.checkYourAnswersLabel",
          value = messages("itemCheckAnswers.producerSize.value", answer.toString()),
          changeLink = Some(ActionItem(
            href = routes.ItemProducerSizeController.onPageLoad(request.ern, request.draftId, idx, CheckMode).url,
            content = "itemCheckAnswers.change",
            visuallyHiddenText = Some(messages(s"$page.change.hidden"))
          ))
        )
      }
    }
  }

  object Quantity {
    def constructCard(idx: Index, cnCodeInformation: CnCodeInformation)
                     (implicit request: DataRequest[_], messages: Messages): Seq[SummaryListRow] = {
      Seq(
        constructQuantityRow(idx, cnCodeInformation.unitOfMeasure),
        constructNetMassRow(idx, cnCodeInformation.unitOfMeasure),
        constructGrossMassRow(idx, cnCodeInformation.unitOfMeasure),
      ).flatten
    }

    private[helpers] def constructQuantityRow(idx: Index, unitOfMeasure: UnitOfMeasure)
                                             (implicit request: DataRequest[_], messages: Messages): Option[SummaryListRow] = {
      lazy val page = ItemQuantityPage(idx)

      request.userAnswers.get(page).map {
        answer =>
          summaryListRowBuilder(
            key = s"$page.checkYourAnswersLabel",
            value = messages("itemCheckAnswers.quantity.value", answer, unitOfMeasure.toShortFormatMessage()),
            changeLink = Some(ActionItem(
              href = routes.ItemQuantityController.onPageLoad(request.ern, request.draftId, idx, CheckMode).url,
              content = "itemCheckAnswers.change",
              visuallyHiddenText = Some(messages(s"$page.change.hidden"))
            ))
          )
      }
    }

    private[helpers] def constructNetMassRow(idx: Index, unitOfMeasure: UnitOfMeasure)
                                            (implicit request: DataRequest[_], messages: Messages): Option[SummaryListRow] = {
      lazy val page = ItemNetGrossMassPage(idx)

      request.userAnswers.get(page).map {
        answer =>
          summaryListRowBuilder(
            key = s"$page.netMass.checkYourAnswersLabel",
            value = messages("itemCheckAnswers.netMass.value", answer.netMass, unitOfMeasure.toShortFormatMessage()),
            changeLink = Some(ActionItem(
              href = routes.ItemNetGrossMassController.onPageLoad(request.ern, request.draftId, idx, CheckMode).url,
              content = "itemCheckAnswers.change",
              visuallyHiddenText = Some(messages(s"$page.netMass.change.hidden"))
            ))
          )
      }
    }

    private[helpers] def constructGrossMassRow(idx: Index, unitOfMeasure: UnitOfMeasure)
                                              (implicit request: DataRequest[_], messages: Messages): Option[SummaryListRow] = {
      lazy val page = ItemNetGrossMassPage(idx)

      request.userAnswers.get(page).map {
        answer =>
          summaryListRowBuilder(
            key = s"$page.grossMass.checkYourAnswersLabel",
            value = messages("itemCheckAnswers.grossMass.value", answer.grossMass, unitOfMeasure.toShortFormatMessage()),
            changeLink = Some(ActionItem(
              href = routes.ItemNetGrossMassController.onPageLoad(request.ern, request.draftId, idx, CheckMode).url,
              content = "itemCheckAnswers.change",
              visuallyHiddenText = Some(messages(s"$page.grossMass.change.hidden"))
            ))
          )
      }
    }
  }
}
