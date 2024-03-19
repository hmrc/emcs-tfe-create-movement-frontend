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

package views.sections.items

import base.SpecBase
import fixtures.{ItemFixtures, MovementSubmissionFailureFixtures}
import fixtures.messages.sections.items.ItemExciseProductCodeMessages
import forms.sections.items.ItemExciseProductCodeFormProvider
import models.{CheckMode, Mode, NormalMode}
import models.requests.DataRequest
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.data.FormError
import play.api.i18n.Messages
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import utils.ItemExciseProductCodeDestinationNotApprovedToReceiveError
import utils._
import viewmodels.helpers.SelectItemHelper
import views.html.sections.items.ItemExciseProductCodeView
import views.{BaseSelectors, ViewBehaviours}

class ItemExciseProductCodeViewSpec extends SpecBase
  with ViewBehaviours
  with ItemFixtures
  with MovementSubmissionFailureFixtures {

  object Selectors extends BaseSelectors {
    def selectOption(nthChild: Int) = s"#excise-product-code > option:nth-child($nthChild)"

    val notificationBannerList: String = "#list-of-excise-product-code-submission-failures"

    val notificationBannerListElement: Int => String = index => s"$notificationBannerList > li:nth-of-type($index)"

    override val p: Int => String = index => s"main p.govuk-body:nth-of-type($index)"
  }

  "Item Excise Product Code view" - {

    Seq(ItemExciseProductCodeMessages.English).foreach { messagesForLanguage =>

      s"when being rendered in lang code of '${messagesForLanguage.lang.code}'" - {

        implicit val msgs: Messages = messages(Seq(messagesForLanguage.lang))
        implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest())

        lazy val view = app.injector.instanceOf[ItemExciseProductCodeView]
        val selectOptions = SelectItemHelper.constructSelectItems(
          selectOptions = Seq(beerExciseProductCode),
          defaultTextMessageKey = "itemExciseProductCode.select.defaultValue"
        )
        val form = app.injector.instanceOf[ItemExciseProductCodeFormProvider].apply(Seq(beerExciseProductCode), testIndex1)

        implicit def doc(isFormError: Boolean = false, mode: Mode = NormalMode)(implicit request: DataRequest[_]): Document = Jsoup.parse(view(
          if (isFormError) form.withError(FormError("key", "msg")) else form,
          testOnwardRoute,
          selectOptions,
          testIndex1,
          mode
        ).toString())

        behave like pageWithExpectedElementsAndMessages(Seq(
          Selectors.h2(1) -> messagesForLanguage.itemInformationSection,
          Selectors.hiddenText -> messagesForLanguage.hiddenSectionContent,
          Selectors.title -> messagesForLanguage.title(testIndex1),
          Selectors.h1 -> messagesForLanguage.heading(testIndex1),
          Selectors.p(1) -> messagesForLanguage.paragraph,
          Selectors.label("excise-product-code") -> messagesForLanguage.label,
          Selectors.selectOption(1) -> messagesForLanguage.defaultSelectOption,
          Selectors.selectOption(2) -> messagesForLanguage.beerSelectOption,
          Selectors.button -> messagesForLanguage.saveAndContinue,
          Selectors.link(1) -> messagesForLanguage.returnToDraft
        ))(doc())

        behave like pageWithElementsNotPresent(Seq(
          Selectors.notificationBannerTitle,
          Selectors.notificationBannerContent,
          Selectors.notificationBannerList
        ))(doc())

        "when in CheckMode" - {

          behave like pageWithExpectedElementsAndMessages(Seq(
            Selectors.h2(1) -> messagesForLanguage.itemInformationSection,
            Selectors.hiddenText -> messagesForLanguage.hiddenSectionContent,
            Selectors.title -> messagesForLanguage.title(testIndex1),
            Selectors.h1 -> messagesForLanguage.heading(testIndex1),
            Selectors.p(1) -> messagesForLanguage.paragraph,
            Selectors.label("excise-product-code") -> messagesForLanguage.label,
            Selectors.selectOption(1) -> messagesForLanguage.defaultSelectOption,
            Selectors.selectOption(2) -> messagesForLanguage.beerSelectOption,
            Selectors.warningText -> messagesForLanguage.warningText,
            Selectors.button -> messagesForLanguage.saveAndContinue,
            Selectors.link(1) -> messagesForLanguage.returnToDraft
          ))(doc(mode = CheckMode))
        }

        "when there is a single 704 error" - {

          Seq(
            ItemExciseProductCodeConsignorNotApprovedToSendError(testIndex1, isForAddToList = false) -> messagesForLanguage.itemExciseProductCodeConsignorNotApprovedToSendError,
            ItemExciseProductCodeConsigneeNotApprovedToReceiveError(testIndex1, isForAddToList = false) -> messagesForLanguage.itemExciseProductCodeConsigneeNotApprovedToReceiveError,
            ItemExciseProductCodeDestinationNotApprovedToReceiveError(testIndex1, isForAddToList = false) -> messagesForLanguage.itemExciseProductCodeDestinationNotApprovedToReceiveError,
            ItemExciseProductCodeDispatchPlaceNotAllowedError(testIndex1, isForAddToList = false) -> messagesForLanguage.itemExciseProductCodeDispatchPlaceNotAllowed
          ).foreach { exciseProductCodeErrorToErrorMessage =>

            s"for error code: ${exciseProductCodeErrorToErrorMessage._1.code}" - {
              implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(), emptyUserAnswers
                .copy(submissionFailures = Seq(itemExciseProductCodeFailure(exciseProductCodeErrorToErrorMessage._1, itemIndex = 1))))

              behave like pageWithExpectedElementsAndMessages(Seq(
                Selectors.notificationBannerTitle -> messagesForLanguage.updateNeeded,
                Selectors.notificationBannerContent -> exciseProductCodeErrorToErrorMessage._2,
                Selectors.subHeadingCaptionSelector -> messagesForLanguage.itemInformationSection,
                Selectors.hiddenText -> messagesForLanguage.hiddenSectionContent,
                Selectors.title -> messagesForLanguage.title(testIndex1),
                Selectors.h1 -> messagesForLanguage.heading(testIndex1),
                Selectors.p(1) -> messagesForLanguage.paragraph,
                Selectors.label("excise-product-code") -> messagesForLanguage.label,
                Selectors.selectOption(1) -> messagesForLanguage.defaultSelectOption,
                Selectors.selectOption(2) -> messagesForLanguage.beerSelectOption,
                Selectors.button -> messagesForLanguage.saveAndContinue,
                Selectors.link(1) -> messagesForLanguage.returnToDraft
              ))(doc())

              "not show the notification banner when there is an error" - {
                doc(isFormError = true).select(".govuk-error-summary").isEmpty mustBe false
                doc(isFormError = true).select(".govuk-notification-banner").isEmpty mustBe true
              }
            }
          }
        }

        "when there is multiple 704 errors" - {

          implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(), emptyUserAnswers
            .copy(submissionFailures = Seq(
              itemExciseProductCodeFailure(ItemExciseProductCodeConsignorNotApprovedToSendError(testIndex1, isForAddToList = false), itemIndex = 1),
              itemExciseProductCodeFailure(ItemExciseProductCodeConsigneeNotApprovedToReceiveError(testIndex1, isForAddToList = false), itemIndex = 1)
            )))

          behave like pageWithExpectedElementsAndMessages(Seq(
            Selectors.notificationBannerTitle -> messagesForLanguage.updateNeeded,
            Selectors.notificationBannerListElement(1) -> messagesForLanguage.itemExciseProductCodeConsignorNotApprovedToSendError,
            Selectors.notificationBannerListElement(2) -> messagesForLanguage.itemExciseProductCodeConsigneeNotApprovedToReceiveError,
            Selectors.subHeadingCaptionSelector -> messagesForLanguage.itemInformationSection,
            Selectors.hiddenText -> messagesForLanguage.hiddenSectionContent,
            Selectors.title -> messagesForLanguage.title(testIndex1),
            Selectors.h1 -> messagesForLanguage.heading(testIndex1),
            Selectors.p(1) -> messagesForLanguage.paragraph,
            Selectors.label("excise-product-code") -> messagesForLanguage.label,
            Selectors.selectOption(1) -> messagesForLanguage.defaultSelectOption,
            Selectors.selectOption(2) -> messagesForLanguage.beerSelectOption,
            Selectors.button -> messagesForLanguage.saveAndContinue,
            Selectors.link(1) -> messagesForLanguage.returnToDraft
          ))(doc())

          "not show the notification banner when there is an error" - {
            doc(isFormError = true).select(".govuk-error-summary").isEmpty mustBe false
            doc(isFormError = true).select(".govuk-notification-banner").isEmpty mustBe true
          }

        }
      }
    }
  }
}
