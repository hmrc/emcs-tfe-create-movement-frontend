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
import controllers.sections.items.routes
import fixtures.{ItemFixtures, MovementSubmissionFailureFixtures}
import fixtures.messages.sections.items.ItemCheckAnswersMessages
import models.{CheckMode, NormalMode}
import models.requests.DataRequest
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import pages.sections.items.{ItemBulkPackagingChoicePage, ItemExciseProductCodePage, ItemPackagingQuantityPage, ItemPackagingSealChoicePage, ItemPackagingShippingMarksChoicePage, ItemSelectPackagingPage, ItemsPackagingSection}
import play.api.i18n.Messages
import play.api.mvc.{AnyContentAsEmpty, Call}
import play.api.test.FakeRequest
import views.html.sections.items.ItemCheckAnswersView
import views.{BaseSelectors, ViewBehaviours}

class ItemCheckAnswersViewSpec extends SpecBase with ViewBehaviours with ItemFixtures with MovementSubmissionFailureFixtures {

  object Selectors extends BaseSelectors {
    val fixQuantityErrorAtIndex: Int => String = index => s"#fix-item-$index-quantity"
    val notificationBannerList = "#list-of-submission-failures"
    val notificationBannerListElement: Int => String = index => s"#list-of-submission-failures > li:nth-of-type($index)"

    val addMorePackagingButton: String = "#add-more-packaging"

    override val button: String = ".govuk-button-group > .govuk-button"
  }

  val view: ItemCheckAnswersView = app.injector.instanceOf[ItemCheckAnswersView]
  implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest())
  val addMorePackagingCall: Call = routes.ItemSelectPackagingController.onPageLoad(testErn, testDraftId, testIndex1, testPackagingIndex1, NormalMode)

  val itemDetailsIndex = 1
  val quantityIndex = 2
  val wineDetailsIndex = 3
  val packagingTypeIndex = 4
  val packagingIndex = 5

  "Item Check Answers view" - {
    Seq(ItemCheckAnswersMessages.English).foreach { messagesForLanguage =>

      implicit val msgs: Messages = messages(Seq(messagesForLanguage.lang))

      s"when being rendered in lang code of '${messagesForLanguage.lang.code}'" - {

        "for the non-bulk view" - {

          implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(),
            emptyUserAnswers
              .set(ItemExciseProductCodePage(testIndex1), testEpcWine)
              .set(ItemBulkPackagingChoicePage(testIndex1), false)
              .set(ItemSelectPackagingPage(testIndex1, testPackagingIndex1), testPackageAerosol)
              .set(ItemPackagingQuantityPage(testIndex1, testPackagingIndex1), "2")
              .set(ItemPackagingShippingMarksChoicePage(testIndex1, testPackagingIndex1), false)
              .set(ItemPackagingSealChoicePage(testIndex1, testPackagingIndex1), false)
          )

          implicit val doc: Document = Jsoup.parse(view(testIndex1, testCommodityCodeWine, testOnwardRoute, addMorePackagingCall, isBulk = false, packagingCount = Some(1)).toString())

          behave like pageWithExpectedElementsAndMessages(Seq(
            Selectors.title -> messagesForLanguage.title,
            Selectors.h1 -> messagesForLanguage.heading,
            Selectors.subHeadingCaptionSelector -> messagesForLanguage.itemSection,
            Selectors.summaryCardHeading(itemDetailsIndex) -> messagesForLanguage.cardTitleItemDetails(testIndex1),
            Selectors.summaryCardHeading(quantityIndex) -> messagesForLanguage.cardTitleQuantity(testIndex1),
            Selectors.summaryCardHeading(wineDetailsIndex) -> messagesForLanguage.cardTitleWineDetails(testIndex1),
            Selectors.summaryCardHeading(packagingTypeIndex) -> messagesForLanguage.cardTitlePackagingType(testIndex1),
            Selectors.summaryCardHeading(packagingIndex) -> messagesForLanguage.cardTitleIndividualPackaging(testIndex1, testPackagingIndex1),
            Selectors.addMorePackagingButton -> messagesForLanguage.addMorePackaging,
            Selectors.button -> messagesForLanguage.confirmAnswers
          ))

          "not show the add another packaging button when the max packages have been entered" - {

            implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(),
              emptyUserAnswers
                .set(ItemExciseProductCodePage(testIndex1), testEpcWine)
                .set(ItemBulkPackagingChoicePage(testIndex1), false)
                .set(ItemSelectPackagingPage(testIndex1, testPackagingIndex1), testPackageAerosol)
                .set(ItemPackagingQuantityPage(testIndex1, testPackagingIndex1), "2")
                .set(ItemPackagingShippingMarksChoicePage(testIndex1, testPackagingIndex1), false)
                .set(ItemPackagingSealChoicePage(testIndex1, testPackagingIndex1), false)
            )

            implicit val doc: Document = Jsoup.parse(view(testIndex1, testCommodityCodeWine, testOnwardRoute, addMorePackagingCall, isBulk = false, packagingCount = Some(ItemsPackagingSection(testIndex1).MAX)).toString())

            behave like pageWithElementsNotPresent(Seq(
              Selectors.addMorePackagingButton
            ))
          }
        }

        "for the bulk view" - {
          implicit val doc: Document = Jsoup.parse(view(testIndex1, testCommodityCodeWine, testOnwardRoute, addMorePackagingCall, isBulk = true, packagingCount = None).toString())

          behave like pageWithExpectedElementsAndMessages(Seq(
            Selectors.title -> messagesForLanguage.title,
            Selectors.h1 -> messagesForLanguage.heading,
            Selectors.subHeadingCaptionSelector -> messagesForLanguage.itemSection,
            Selectors.summaryCardHeading(itemDetailsIndex) -> messagesForLanguage.cardTitleItemDetails(testIndex1),
            Selectors.summaryCardHeading(quantityIndex) -> messagesForLanguage.cardTitleQuantity(testIndex1),
            Selectors.summaryCardHeading(wineDetailsIndex) -> messagesForLanguage.cardTitleWineDetails(testIndex1),
            Selectors.summaryCardHeading(packagingTypeIndex) -> messagesForLanguage.cardTitlePackagingType(testIndex1),
            Selectors.button -> messagesForLanguage.confirmAnswers
          ))

          behave like pageWithElementsNotPresent(Seq(
            Selectors.addMorePackagingButton
          ))
        }
      }

      "must not render the Wine card" - {
        s"when the commodity code is not 22060010 or begins 2204 (except 22043096 / 22043098)" in {
          implicit val msgs: Messages = messages(Seq(messagesForLanguage.lang))
          implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest())

          implicit val doc: Document = Jsoup.parse(view(testIndex1, testCommodityCodeWine.copy(cnCode = testCnCodeSpirit), testOnwardRoute, addMorePackagingCall, isBulk = false, packagingCount = Some(1)).toString())

          doc.selectFirst(Selectors.summaryCardHeading(wineDetailsIndex)).text() must not be messagesForLanguage.cardTitleWineDetails(testIndex1)
        }
      }

      "when there is one 704 error" - {

        implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(), singleCompletedWineItem
          .copy(submissionFailures = Seq(itemQuantityFailure(1))))

        implicit val doc: Document = Jsoup.parse(view(testIndex1, testCommodityCodeWine, testOnwardRoute, addMorePackagingCall, isBulk = false, packagingCount = Some(1)).toString())

        behave like pageWithExpectedElementsAndMessages(Seq(
          Selectors.title -> messagesForLanguage.title,
          Selectors.subHeadingCaptionSelector -> messagesForLanguage.itemSection,
          Selectors.h1 -> messagesForLanguage.heading,
          Selectors.notificationBannerTitle -> messagesForLanguage.updateNeeded,
          Selectors.notificationBannerContent -> messagesForLanguage.notificationBannerContentForQuantity,
          Selectors.addMorePackagingButton -> messagesForLanguage.addMorePackaging,
          Selectors.button -> messagesForLanguage.confirmAnswers
        ))

        "link to the item Quantity page" in {
          doc.select(Selectors.fixQuantityErrorAtIndex(1)).attr("href") mustBe controllers.sections.items.routes.ItemQuantityController.onPageLoad(testErn, testDraftId, testIndex1, CheckMode).url
        }
      }

      "when there are multiple 704 errors" - {

        implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(), singleCompletedWineItem
          .copy(submissionFailures = Seq(itemQuantityFailure(1), itemDegreesPlatoFailure(1))))

        implicit val doc: Document = Jsoup.parse(view(testIndex1, testCommodityCodeWine, testOnwardRoute, addMorePackagingCall, isBulk = false, packagingCount = Some(1)).toString())

        "have the correct banner content - rendering a list of errors" in {
          doc.select(Selectors.notificationBannerList).isEmpty mustBe false
          doc.select(Selectors.notificationBannerContent).first().ownText() mustBe messagesForLanguage.notificationBannerParagraphForItems
          doc.select(Selectors.notificationBannerListElement(1)).text mustBe messagesForLanguage.notificationBannerContentForQuantity
          doc.select(Selectors.notificationBannerListElement(2)).text mustBe messagesForLanguage.notificationBannerContentForDegreesPlato
        }
      }

      "when there is a 704 error (in another item)" - {

        "don't show the notification banner" - {

          implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(), singleCompletedWineItem
            .copy(submissionFailures = Seq(itemQuantityFailure(2))))

          implicit val doc: Document = Jsoup.parse(view(testIndex1, testCommodityCodeWine, testOnwardRoute, addMorePackagingCall, isBulk = false, packagingCount = Some(1)).toString())

          behave like pageWithElementsNotPresent(Seq(
            Selectors.notificationBannerList,
            Selectors.notificationBannerContent
          ))
        }
      }
    }
  }
}
