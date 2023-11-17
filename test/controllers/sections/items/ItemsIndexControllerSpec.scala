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
import mocks.services.MockUserAnswersService
import models.{Index, NormalMode, UserAnswers}
import navigation.ItemsNavigator
import play.api.http.Status.SEE_OTHER
import play.api.mvc.AnyContentAsEmpty
import play.api.test.Helpers._
import play.api.test.{FakeRequest, Helpers}

class ItemsIndexControllerSpec extends SpecBase with MockUserAnswersService {

  class Test(val userAnswers: Option[UserAnswers] = Some(emptyUserAnswers)) {

    lazy val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest()

    lazy val controller = new ItemsIndexController(
      mockUserAnswersService,
      app.injector.instanceOf[ItemsNavigator],
      fakeAuthAction,
      new FakeDataRetrievalAction(userAnswers, Some(testMinTraderKnownFacts)),
      dataRequiredAction,
      fakeUserAllowListAction,
      Helpers.stubMessagesControllerComponents()
    )
  }

  "ItemsIndexController" - {

    "when ItemsSection.isCompleted" - {
      // TODO: remove ignore when CYA page is built
      "must redirect to the CYA controller" ignore new Test(Some(emptyUserAnswers)) {
        val result = controller.onPageLoad(testErn, testDraftId)(request)

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustBe testOnly.controllers.routes.UnderConstructionController.onPageLoad().url
      }
    }

    "must redirect to the items excise product code controller" in new Test(Some(emptyUserAnswers)) {
      val result = controller.onPageLoad(testErn, testDraftId)(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustBe
        controllers.sections.items.routes.ItemExciseProductCodeController.onPageLoad(testErn, testDraftId, Index(0), NormalMode).url
    }
  }
}