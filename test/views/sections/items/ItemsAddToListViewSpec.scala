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
import fixtures.messages.sections.items.ItemsAddToListMessages
import forms.sections.items.ItemsAddToListFormProvider
import mocks.services.MockGetCnCodeInformationService
import models.UnitOfMeasure.{Litres20, Thousands}
import models.requests.{CnCodeInformationItem, DataRequest}
import models.response.referenceData.CnCodeInformation
import models.sections.items.{ItemBrandNameModel, ItemNetGrossMassModel}
import models.sections.items.ItemGeographicalIndicationType.NoGeographicalIndication
import models.sections.items.ItemWineProductCategory.Other
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import pages.sections.items._
import play.api.i18n.Messages
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import uk.gov.hmrc.http.HeaderCarrier
import viewmodels.checkAnswers.sections.items.{ItemPackagingSummary, ItemQuantitySummary}
import viewmodels.helpers.{ItemsAddToListHelper, TagHelper}
import views.html.components._
import views.html.sections.items.ItemsAddToListView
import views.{BaseSelectors, ViewBehaviours}

import scala.concurrent.{ExecutionContext, Future}

class ItemsAddToListViewSpec extends SpecBase
  with ViewBehaviours
  with ItemFixtures
  with MockGetCnCodeInformationService
  with MovementSubmissionFailureFixtures {

  lazy val view = app.injector.instanceOf[ItemsAddToListView]
  lazy val form = app.injector.instanceOf[ItemsAddToListFormProvider].apply()

  implicit lazy val ec: ExecutionContext = app.injector.instanceOf[ExecutionContext]
  implicit lazy val hc: HeaderCarrier = HeaderCarrier()

  lazy val helper = new ItemsAddToListHelper(
    tagHelper = app.injector.instanceOf[TagHelper],
    span = app.injector.instanceOf[span],
    cnCodeInformationService = mockGetCnCodeInformationService,
    itemPackagingSummary = app.injector.instanceOf[ItemPackagingSummary],
    itemQuantitySummary = app.injector.instanceOf[ItemQuantitySummary],
    p = app.injector.instanceOf[p],
    list = app.injector.instanceOf[list],
    link = app.injector.instanceOf[link]
  )

  object Selectors extends BaseSelectors {
    val cardTitle: Int => String = index => s"div.govuk-summary-card:nth-of-type($index) .govuk-summary-card__title"
    val legendQuestion = ".govuk-fieldset__legend.govuk-fieldset__legend--m"
    val errorSummary: Int => String = index => s".govuk-error-summary__list > li:nth-child($index)"
    val removeItemLink: Int => String = index => s"#removeItem-$index"
    val changeItemLink: Int => String = index => s"#changeItem-$index"
    val editItemLink: Int => String = index => s"#editItem-$index"
    val notificationBannerList = s".govuk-notification-banner .govuk-list"
  }

  "ItemsAddToListView" - {

    Seq(ItemsAddToListMessages.English).foreach { messagesForLanguage =>

      s"when being rendered in language code of '${messagesForLanguage.lang.code}'" - {

        implicit val msgs: Messages = messages(Seq(messagesForLanguage.lang))

        s"when being rendered for singular item" - {

          val item = CnCodeInformationItem(testEpcWine, testCnCodeWine)

          MockGetCnCodeInformationService.getCnCodeInformationWithMovementItems(Seq(item))
            .returns(Future.successful(Seq(item -> CnCodeInformation(item.cnCode, "Sparkling Wine", item.productCode, "Wine", Litres20))))

          implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(), singleCompletedWineItem)

          implicit val doc: Document = Jsoup.parse(view(
            formOpt = Some(form),
            onSubmitCall = testOnwardRoute,
            items = helper.allItemsSummary.futureValue,
            showNoOption = true
          ).toString())

          behave like pageWithExpectedElementsAndMessages(Seq(
            Selectors.title -> messagesForLanguage.title(count = 1),
            Selectors.h2(1) -> messagesForLanguage.itemSection,
            Selectors.h1 -> messagesForLanguage.heading(count = 1),
            Selectors.cardTitle(1) -> messagesForLanguage.itemCardTitle(testIndex1),
            Selectors.removeItemLink(1) -> messagesForLanguage.removeItem(testIndex1),
            Selectors.legendQuestion -> messagesForLanguage.h2,
            Selectors.radioButton(1) -> messagesForLanguage.yes,
            Selectors.radioButton(2) -> messagesForLanguage.no1,
            Selectors.radioButton(4) -> messagesForLanguage.moreLater,
            Selectors.button -> messagesForLanguage.saveAndContinue,
            Selectors.saveAndExitLink -> messagesForLanguage.returnToDraft
          ))
        }

        s"when being rendered for multiple items (one incomplete and in progress)" - {

          val item1 = CnCodeInformationItem(testEpcWine, testCnCodeWine)
          val item2 = CnCodeInformationItem(testEpcTobacco, testCnCodeTobacco)

          MockGetCnCodeInformationService.getCnCodeInformationWithMovementItems(Seq(item1, item2))
            .returns(Future.successful(Seq(
              item1 -> CnCodeInformation(item1.cnCode, "Sparkling Wine", item1.productCode, "Wine", Litres20),
              item2 -> CnCodeInformation(item2.cnCode, "Cigars", item2.productCode, "Tobacco", Thousands)
            )))

          val userAnswers = singleCompletedWineItem
            .set(ItemExciseProductCodePage(testIndex2), testEpcTobacco)
            .set(ItemCommodityCodePage(testIndex2), testCnCodeTobacco)

          implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(), userAnswers)

          implicit val doc: Document = Jsoup.parse(view(
            formOpt = Some(form),
            onSubmitCall = testOnwardRoute,
            items = helper.allItemsSummary.futureValue,
            showNoOption = true
          ).toString())

          behave like pageWithExpectedElementsAndMessages(Seq(
            Selectors.title -> messagesForLanguage.title(count = 2),
            Selectors.h2(1) -> messagesForLanguage.itemSection,
            Selectors.h1 -> messagesForLanguage.heading(count = 2),
            Selectors.cardTitle(1) -> messagesForLanguage.itemCardTitle(testIndex1),
            Selectors.changeItemLink(1) -> messagesForLanguage.changeItem(testIndex1),
            Selectors.removeItemLink(1) -> messagesForLanguage.removeItem(testIndex1),
            Selectors.cardTitle(2) -> s"${messagesForLanguage.itemCardTitle(testIndex2)} ${messagesForLanguage.incomplete}",
            Selectors.editItemLink(2) -> messagesForLanguage.editItem(testIndex2),
            Selectors.removeItemLink(2) -> messagesForLanguage.removeItem(testIndex2),
            Selectors.legendQuestion -> messagesForLanguage.h2,
            Selectors.radioButton(1) -> messagesForLanguage.yes,
            Selectors.radioButton(2) -> messagesForLanguage.no2,
            Selectors.radioButton(4) -> messagesForLanguage.moreLater,
            Selectors.button -> messagesForLanguage.saveAndContinue,
            Selectors.saveAndExitLink -> messagesForLanguage.returnToDraft
          ))
        }

        s"when being rendered with no form" - {

          val item = CnCodeInformationItem(testEpcWine, testCnCodeWine)

          MockGetCnCodeInformationService.getCnCodeInformationWithMovementItems(Seq(item))
            .returns(Future.successful(Seq(item -> CnCodeInformation(item.cnCode, "Sparkling Wine", item.productCode, "Wine", Litres20))))

          implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(), singleCompletedWineItem)

          implicit val doc: Document = Jsoup.parse(view(
            formOpt = None,
            onSubmitCall = testOnwardRoute,
            items = helper.allItemsSummary.futureValue,
            showNoOption = true
          ).toString())

          behave like pageWithExpectedElementsAndMessages(Seq(
            Selectors.title -> messagesForLanguage.title(count = 1),
            Selectors.h2(1) -> messagesForLanguage.itemSection,
            Selectors.h1 -> messagesForLanguage.heading(count = 1),
            Selectors.cardTitle(1) -> messagesForLanguage.itemCardTitle(testIndex1),
            Selectors.removeItemLink(1) -> messagesForLanguage.removeItem(testIndex1),
            Selectors.button -> messagesForLanguage.saveAndContinue,
            Selectors.saveAndExitLink -> messagesForLanguage.returnToDraft
          ))

          behave like pageWithElementsNotPresent(Seq(
            Selectors.legendQuestion,
            Selectors.radioButton(1),
            Selectors.radioButton(2),
            Selectors.radioButton(4)
          ))
        }

        s"when being rendered for singular item (submission failure)" - {

          val item = CnCodeInformationItem(testEpcWine, testCnCodeWine)

          MockGetCnCodeInformationService.getCnCodeInformationWithMovementItems(Seq(item))
            .returns(Future.successful(Seq(item -> CnCodeInformation(item.cnCode, "Sparkling Wine", item.productCode, "Wine", Litres20))))

          implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(),
            singleCompletedWineItem.copy(submissionFailures = Seq(itemQuantityFailure(1))))

          implicit val doc: Document = Jsoup.parse(view(
            formOpt = Some(form),
            onSubmitCall = testOnwardRoute,
            items = helper.allItemsSummary.futureValue,
            showNoOption = true
          ).toString())

          behave like pageWithExpectedElementsAndMessages(Seq(
            Selectors.title -> messagesForLanguage.title(count = 1),
            Selectors.notificationBannerTitle -> messagesForLanguage.updateNeeded,
            Selectors.notificationBannerContent -> messagesForLanguage.notificationBannerParagraphForItems,
            Selectors.notificationBannerList -> messagesForLanguage.notificationBannerContentForQuantity(1),
            Selectors.subHeadingCaptionSelector -> messagesForLanguage.itemSection,
            Selectors.h1 -> messagesForLanguage.heading(count = 1),
            //TODO: not sure why this is index 2?
            Selectors.cardTitle(2) -> s"${messagesForLanguage.itemCardTitle(testIndex1)} ${messagesForLanguage.updateNeededTag}",
            Selectors.removeItemLink(1) -> messagesForLanguage.removeItem(testIndex1),
            Selectors.legendQuestion -> messagesForLanguage.h2,
            Selectors.radioButton(1) -> messagesForLanguage.yes,
            Selectors.radioButton(2) -> messagesForLanguage.no1,
            Selectors.radioButton(4) -> messagesForLanguage.moreLater,
            Selectors.button -> messagesForLanguage.saveAndContinue,
            Selectors.saveAndExitLink -> messagesForLanguage.returnToDraft
          ))
        }

        s"when being rendered for 2 items (both have submission failures)" - {

          val item = CnCodeInformationItem(testEpcWine, testCnCodeWine)

          MockGetCnCodeInformationService.getCnCodeInformationWithMovementItems(Seq(item))
            .returns(Future.successful(Seq(item -> CnCodeInformation(item.cnCode, "Sparkling Wine", item.productCode, "Wine", Litres20))))

          val userAnswers = singleCompletedWineItem
            .set(ItemExciseProductCodePage(testIndex2), testEpcWine)
            .set(ItemCommodityCodePage(testIndex2), testCnCodeWine)
            .set(ItemBrandNamePage(testIndex2), ItemBrandNameModel(hasBrandName = true, Some("brand")))
            .set(ItemCommercialDescriptionPage(testIndex2), "Wine from grapes")
            .set(ItemAlcoholStrengthPage(testIndex2), BigDecimal(12.5))
            .set(ItemGeographicalIndicationChoicePage(testIndex2), NoGeographicalIndication)
            .set(ItemQuantityPage(testIndex2), BigDecimal("1000"))
            .set(ItemNetGrossMassPage(testIndex2), ItemNetGrossMassModel(BigDecimal("2000"), BigDecimal("2105")))
            .set(ItemBulkPackagingChoicePage(testIndex2), false)
            .set(ItemWineProductCategoryPage(testIndex2), Other)
            .set(ItemWineMoreInformationChoicePage(testIndex2), false)
            .set(ItemSelectPackagingPage(testIndex2, testPackagingIndex1), testPackageBag)
            .set(ItemPackagingQuantityPage(testIndex2, testPackagingIndex1), "400")
            .set(ItemPackagingProductTypePage(testIndex2, testPackagingIndex1), true)
            .set(ItemPackagingSealChoicePage(testIndex2, testPackagingIndex1), false)

          implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(),
            userAnswers.copy(submissionFailures = Seq(itemQuantityFailure(1), itemQuantityFailure(2))))

          implicit val doc: Document = Jsoup.parse(view(
            formOpt = Some(form),
            onSubmitCall = testOnwardRoute,
            items = helper.allItemsSummary.futureValue,
            showNoOption = true
          ).toString())

          behave like pageWithExpectedElementsAndMessages(Seq(
            Selectors.title -> messagesForLanguage.title(count = 2),
            Selectors.notificationBannerTitle -> messagesForLanguage.updateNeeded,
            Selectors.notificationBannerContent -> messagesForLanguage.notificationBannerParagraphForItems,
            Selectors.notificationBannerList -> s"${messagesForLanguage.notificationBannerContentForQuantity(1)} ${messagesForLanguage.notificationBannerContentForQuantity(2)}",
            Selectors.subHeadingCaptionSelector -> messagesForLanguage.itemSection,
            Selectors.h1 -> messagesForLanguage.heading(count = 2),
            //TODO: not sure why this is index 2?
            Selectors.cardTitle(2) -> s"${messagesForLanguage.itemCardTitle(testIndex1)} ${messagesForLanguage.updateNeededTag}",
            Selectors.removeItemLink(1) -> messagesForLanguage.removeItem(testIndex1),
            Selectors.cardTitle(3) -> s"${messagesForLanguage.itemCardTitle(testIndex2)} ${messagesForLanguage.updateNeededTag}",
            Selectors.removeItemLink(2) -> messagesForLanguage.removeItem(testIndex2),
            Selectors.legendQuestion -> messagesForLanguage.h2,
            Selectors.radioButton(1) -> messagesForLanguage.yes,
            Selectors.radioButton(2) -> messagesForLanguage.no2,
            Selectors.radioButton(4) -> messagesForLanguage.moreLater,
            Selectors.button -> messagesForLanguage.saveAndContinue,
            Selectors.saveAndExitLink -> messagesForLanguage.returnToDraft
          ))
        }
      }
    }
  }
}

