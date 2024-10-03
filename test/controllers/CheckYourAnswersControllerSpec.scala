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

package controllers

import base.SpecBase
import controllers.actions.{DataRequiredAction, FakeAuthAction, FakeDataRetrievalAction}
import mocks.config.MockAppConfig
import mocks.services.MockMovementTemplatesService
import mocks.viewmodels.MockItemsAddToListHelper
import models.UserAnswers
import models.response.templates.MovementTemplates
import navigation.FakeNavigators.FakeNavigator
import pages.sections.info.DeferredMovementPage
import play.api.i18n.{Messages, MessagesApi}
import play.api.mvc.AnyContentAsEmpty
import play.api.test.Helpers._
import play.api.test.{FakeRequest, Helpers}
import viewmodels.govuk.SummaryListFluency
import views.html.CheckYourAnswersView

import scala.concurrent.Future

class CheckYourAnswersControllerSpec extends SpecBase with SummaryListFluency
  with MockItemsAddToListHelper
  with MockMovementTemplatesService
  with MockAppConfig {

  class Fixture(val userAnswers: Option[UserAnswers]) {
    implicit lazy val messagesApi: MessagesApi = app.injector.instanceOf[MessagesApi]

    lazy val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest()

    lazy val messages: Messages = messagesApi.preferred(request)

    lazy val view: CheckYourAnswersView = app.injector.instanceOf[CheckYourAnswersView]

    val controller = new CheckYourAnswersController(
      messagesApi,
      app.injector.instanceOf[FakeAuthAction],
      new FakeDataRetrievalAction(userAnswers, Some(testMinTraderKnownFacts)),
      app.injector.instanceOf[DataRequiredAction],
      Helpers.stubMessagesControllerComponents(),
      new FakeNavigator(testOnwardRoute),
      view,
      mockMovementTemplatesService,
      mockItemsAddToListHelper,
      mockAppConfig
    )
  }

  "Check Your Answers Controller" - {

    ".onPageLoad" - {

      "when there is an answer for deferred movement" - {

        "must return OK and the correct view for a GET" in new Fixture(Some(emptyUserAnswers
          .set(DeferredMovementPage(false), false)
        )) {

          MockItemsAddToListHelper.finalCyaSummary().returns(Future.successful(None))

          val result = controller.onPageLoad(testErn, testDraftId)(request)

          status(result) mustBe OK
          contentAsString(result) mustBe
            view(
              submitAction = routes.CheckYourAnswersController.onSubmit(testErn, testDraftId),
              deferredMovement = false,
              itemsSummary = None
            )(dataRequest(request, userAnswers.get), messages).toString
        }
      }

      "when there's no answer for deferred movement" - {

        "must redirect to Journey Recovery for a GET if no existing data is found" in new Fixture(None) {

          val result = controller.onPageLoad(testErn, testDraftId)(request)

          status(result) mustBe SEE_OTHER
          redirectLocation(result).value mustBe routes.JourneyRecoveryController.onPageLoad().url
        }
      }
    }

    ".onSubmit" - {

      "when the number of templates used >= maxTemplates" - {

        "must redirect directly to Declaration controller" in new Fixture(Some(emptyUserAnswers)) {

          MockAppConfig.maxTemplates.returns(30)
          MockMovementTemplatesService.getList(testErn).returns(Future.successful(MovementTemplates(Seq(), 30)))

          val result = controller.onSubmit(testErn, testDraftId)(request)

          status(result) mustBe SEE_OTHER
          redirectLocation(result).value mustBe controllers.routes.DeclarationController.onPageLoad(testErn, testDraftId).url
        }
      }

      "when the number of templates used < maxTemplates" - {

        "must redirect via the navigator to the next route" in new Fixture(Some(emptyUserAnswers)) {

          MockAppConfig.maxTemplates.returns(30)
          MockMovementTemplatesService.getList(testErn).returns(Future.successful(MovementTemplates(Seq(), 29)))

          val result = controller.onSubmit(testErn, testDraftId)(request)

          status(result) mustBe SEE_OTHER
          redirectLocation(result).value mustBe testOnwardRoute.url
        }
      }
    }
  }
}
