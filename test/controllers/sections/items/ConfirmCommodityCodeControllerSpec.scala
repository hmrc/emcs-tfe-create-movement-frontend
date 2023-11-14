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

package controllers.sections.items

import base.SpecBase
import controllers.actions.FakeDataRetrievalAction
import controllers.routes
import handlers.ErrorHandler
import mocks.services.{MockGetCnCodeInformationService, MockUserAnswersService}
import mocks.viewmodels.MockConfirmCommodityCodeHelper
import models.requests.DataRequest
import models.{GoodsTypeModel, NormalMode, UserAnswers}
import navigation.FakeNavigators.FakeItemsNavigator
import pages.sections.items.{ItemCommodityCodePage, ItemExciseProductCodePage}
import play.api.i18n.Messages
import play.api.mvc.AnyContentAsEmpty
import play.api.test.Helpers._
import play.api.test.{FakeRequest, Helpers}
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.{SummaryList, SummaryListRow}
import viewmodels.checkAnswers.sections.items.{ItemCommodityCodeSummary, ItemExciseProductCodeSummary}
import viewmodels.govuk.SummaryListFluency
import views.html.sections.items.ConfirmCommodityCodeView

class ConfirmCommodityCodeControllerSpec extends SpecBase with SummaryListFluency
  with MockConfirmCommodityCodeHelper with MockUserAnswersService with MockGetCnCodeInformationService {
  def request: FakeRequest[AnyContentAsEmpty.type] =
    FakeRequest(GET, controllers.sections.items.routes.ConfirmCommodityCodeController.onPageLoad(testErn, testDraftId, testIndex1, NormalMode).url)

  implicit val testDataRequest: DataRequest[AnyContentAsEmpty.type] = dataRequest(request)

  class Fixture(userAnswers: Option[UserAnswers] = Some(emptyUserAnswers)) {


    lazy val controller = new ConfirmCommodityCodeController(
      messagesApi,
      fakeAuthAction,
      fakeUserAllowListAction,
      new FakeDataRetrievalAction(userAnswers, Some(testMinTraderKnownFacts)),
      mockUserAnswersService,
      dataRequiredAction,
      mockGetCnCodeInformationService,
      Helpers.stubMessagesControllerComponents(),
      new FakeItemsNavigator(testOnwardRoute),
      mockConfirmCommodityCodeHelper,
      view
    )

    implicit val msgs: Messages = messages(request)

    val filledUserAnswers: UserAnswers = emptyUserAnswers
      .set(ItemExciseProductCodePage(testIndex1), testExciseProductCodeB000)
      .set(ItemCommodityCodePage(testIndex1), testCommodityCodeBeer)

    val confirmCodesList: Seq[SummaryListRow] = Seq(
      ItemExciseProductCodeSummary.row(idx = testIndex1),
      ItemCommodityCodeSummary.row(idx = testIndex1, goodsType = GoodsTypeModel(testExciseProductCodeB000.code), filledUserAnswers)).flatten

    val confirmCodesSummaryList: SummaryList = SummaryListViewModel(
      rows = confirmCodesList
    ).withCssClass("govuk-!-margin-bottom-9")


    lazy val errorHandler: ErrorHandler = app.injector.instanceOf[ErrorHandler]
    val view: ConfirmCommodityCodeView = app.injector.instanceOf[ConfirmCommodityCodeView]
  }

  "ConfirmCommodityCode Controller" - {
    ".onPageLoad" - {

      "must return OK and the correct view when supplied EPC and Commodity Code" in new Fixture(
        Some(emptyUserAnswers
          .set(ItemExciseProductCodePage(testIndex1), testExciseProductCodeB000)
          .set(ItemCommodityCodePage(testIndex1), testCommodityCodeBeer))) {

          MockConfirmCommodityCodeHelper.summaryList(testIndex1, GoodsTypeModel(testExciseProductCodeB000.code),
            emptyUserAnswers
              .set(ItemExciseProductCodePage(testIndex1), testExciseProductCodeB000)
              .set(ItemCommodityCodePage(testIndex1), testCommodityCodeBeer)
          ).returns(confirmCodesSummaryList)

          val result = controller.onPageLoad(testErn, testDraftId, testIndex1, NormalMode)(request)

          val viewAsString = view(controllers.sections.items.routes.ConfirmCommodityCodeController.onSubmit(testErn, testDraftId, testIndex1, NormalMode),
            confirmCodesSummaryList
          )(dataRequest(request), messages(request)).toString

          status(result) mustBe OK
          contentAsString(result) mustBe viewAsString
      }
      "must redirect to Journey Recovery if no existing data is found" in new Fixture(None) {

          val result = controller.onPageLoad(testErn, testDraftId, testIndex1, NormalMode)(request)

          status(result) mustBe SEE_OTHER
          redirectLocation(result).value mustBe routes.JourneyRecoveryController.onPageLoad().url

      }
    }

    ".onSubmit" - {
      "must redirect to the onward route" in new Fixture(Some(emptyUserAnswers
        .set(ItemExciseProductCodePage(testIndex1), testExciseProductCodeB000)
        .set(ItemCommodityCodePage(testIndex1), testCommodityCodeBeer))) {

        def request: FakeRequest[AnyContentAsEmpty.type] =
          FakeRequest(POST, controllers.sections.items.routes.ConfirmCommodityCodeController.onSubmit(testErn, testDraftId, testIndex1, NormalMode).url)


          val result = controller.onSubmit(testErn, testDraftId, testIndex1, NormalMode)(request)

          status(result) mustBe SEE_OTHER
          redirectLocation(result).value mustBe testOnwardRoute.url

      }
    }
  }
}

