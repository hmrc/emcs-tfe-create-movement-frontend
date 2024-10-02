/*
 * Copyright 2024 HM Revenue & Customs
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

package controllers.sections.templates

import base.SpecBase
import controllers.actions.FakeDataRetrievalAction
import featureswitch.core.config.{FeatureSwitching, TemplatesLink}
import forms.sections.templates.UpdateTemplateFormProvider
import mocks.services.{MockMovementTemplatesService, MockUserAnswersService}
import models.UserAnswers
import navigation.FakeNavigators.FakeNavigator
import play.api.i18n.{Messages, MessagesApi}
import play.api.mvc.AnyContentAsEmpty
import play.api.test.Helpers._
import play.api.test.{FakeRequest, Helpers}
import views.html.sections.templates.UpdateTemplateView

import scala.concurrent.Future

class UpdateTemplateControllerSpec extends SpecBase with FeatureSwitching
  with MockUserAnswersService
  with MockMovementTemplatesService {

  lazy val config = appConfig
  lazy val formProvider = new UpdateTemplateFormProvider()
  lazy val form = formProvider()
  lazy val view = app.injector.instanceOf[UpdateTemplateView]

  class Test(val userAnswers: Option[UserAnswers], templatesEnabled: Boolean = true) {
    implicit lazy val messagesApi: MessagesApi = app.injector.instanceOf[MessagesApi]

    lazy val messages: Messages = messagesApi.preferred(request)
    lazy val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest()

    if(templatesEnabled) enable(TemplatesLink) else disable(TemplatesLink)

    lazy val controller = new UpdateTemplateController(
      messagesApi,
      mockUserAnswersService,
      new FakeNavigator(testOnwardRoute),
      fakeAuthAction,
      new FakeDataRetrievalAction(userAnswers, Some(testMinTraderKnownFacts)),
      dataRequiredAction,
      formProvider,
      Helpers.stubMessagesControllerComponents(),
      view,
      mockMovementTemplatesService
    )(config)
  }

  "UpdateTemplate Controller" - {

    "redirect when templates disabled" in new Test(Some(emptyUserAnswersFromTemplate), templatesEnabled = false) {

      val result = controller.onPageLoad(testErn, testDraftId)(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result) mustBe Some(controllers.routes.DeclarationController.onPageLoad(testErn, testDraftId).url)
    }

    "must return OK and the correct view for a GET" in new Test(Some(emptyUserAnswersFromTemplate)) {

      val result = controller.onPageLoad(testErn, testDraftId)(request)

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(
        form = form,
        submitAction = controllers.sections.templates.routes.UpdateTemplateController.onSubmit(testErn, testDraftId)
      )(dataRequest(request, userAnswers.get), messages).toString
    }

    "must save a template when valid data is submitted and value is true" in new Test(Some(emptyUserAnswersFromTemplate)) {

      MockMovementTemplatesService.saveTemplate(templateName, Some(templateId)).returns(Future.successful(Right(true)))

      val result = controller.onSubmit(testErn, testDraftId)(request.withFormUrlEncodedBody("value" -> "true"))

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.DeclarationController.onPageLoad(testErn, testDraftId).url
    }

    "must redirect to the declaration page without saving a template when value is false" in new Test(Some(emptyUserAnswersFromTemplate)) {

      val result = controller.onSubmit(testErn, testDraftId)(request.withFormUrlEncodedBody(("value", "false")))

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.DeclarationController.onPageLoad(testErn, testDraftId).url
    }

    "must return a Bad Request and errors when invalid data is submitted" in new Test(Some(emptyUserAnswersFromTemplate)) {

      val boundForm = form.bind(Map("value" -> ""))
      val result = controller.onSubmit(testErn, testDraftId)(request.withFormUrlEncodedBody(("value", "")))

      status(result) mustEqual BAD_REQUEST
      contentAsString(result) mustEqual view(
        form = boundForm,
        submitAction = controllers.sections.templates.routes.UpdateTemplateController.onSubmit(testErn, testDraftId)
      )(dataRequest(request, userAnswers.get), messages).toString
    }

    "must redirect to Journey Recovery for a GET if no existing data is found" in new Test(None) {

      val result = controller.onPageLoad(testErn, testDraftId)(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.JourneyRecoveryController.onPageLoad().url
    }

    "must redirect to Journey Recovery for a POST if no existing data is found" in new Test(None) {

      val result = controller.onSubmit(testErn, testDraftId)(request.withFormUrlEncodedBody(("value", "true")))

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.JourneyRecoveryController.onPageLoad().url
    }
  }
}
