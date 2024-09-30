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
import forms.sections.templates.SaveTemplateFormProvider
import mocks.services.{MockMovementTemplatesService, MockUserAnswersService}
import models.{NormalMode, UserAnswers}
import navigation.FakeNavigators.FakeNavigator
import play.api.i18n.{Messages, MessagesApi}
import play.api.mvc.AnyContentAsEmpty
import play.api.test.Helpers._
import play.api.test.{FakeRequest, Helpers}
import views.html.sections.templates.SaveTemplateView

import scala.concurrent.Future

class SaveTemplateControllerSpec extends SpecBase with FeatureSwitching
  with MockUserAnswersService
  with MockMovementTemplatesService {

  lazy val config = appConfig
  lazy val formProvider = new SaveTemplateFormProvider()
  lazy val form = formProvider(Seq())
  lazy val view = app.injector.instanceOf[SaveTemplateView]


  class Test(val userAnswers: Option[UserAnswers]) {
    implicit lazy val messagesApi: MessagesApi = app.injector.instanceOf[MessagesApi]

    lazy val messages: Messages = messagesApi.preferred(request)
    lazy val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest()

    lazy val controller = new SaveTemplateController(
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

  "SaveTemplate Controller" - {

    "redirect when templates disabled" in new Test(Some(emptyUserAnswers)) {
      disable(TemplatesLink)
      val result = controller.onPageLoad(testErn, testDraftId, NormalMode)(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result) mustBe Some(controllers.routes.DeclarationController.onPageLoad(testErn, testDraftId).url)
    }

    "must return OK and the correct view for a GET" in new Test(Some(emptyUserAnswers)) {
      enable(TemplatesLink)

      MockMovementTemplatesService.getExistingTemplateNames(testErn).returns(Future.successful(Seq()))

      val result = controller.onPageLoad(testErn, testDraftId, NormalMode)(request)

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(form, controllers.sections.templates.routes.SaveTemplateController.onSubmit(testErn, testDraftId),NormalMode)(dataRequest(request, userAnswers.get), messages).toString
    }

    "must save a template when valid data is submitted and value is true" in new Test(Some(emptyUserAnswers)) {
      enable(TemplatesLink)

      MockMovementTemplatesService.getExistingTemplateNames(testErn).returns(Future.successful(Seq()))
      MockMovementTemplatesService.saveTemplate("testTemplateName").returns(Future.successful(Right(true)))

      val result = controller.onSubmit(testErn, testDraftId, NormalMode)(request.withFormUrlEncodedBody(
        "value" -> "true",
        "name" -> "testTemplateName"
      ))

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.DeclarationController.onPageLoad(testErn, testDraftId).url
    }

    "must redirect to the declaration page without saving a template when value is false" in new Test(Some(emptyUserAnswers)) {
      enable(TemplatesLink)

      MockMovementTemplatesService.getExistingTemplateNames(testErn).returns(Future.successful(Seq()))

      val result = controller.onSubmit(testErn, testDraftId, NormalMode)(request.withFormUrlEncodedBody(("value", "false")))

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.DeclarationController.onPageLoad(testErn, testDraftId).url
    }

    "must return a Bad Request and errors when invalid data is submitted" in new Test(Some(emptyUserAnswers)) {
      enable(TemplatesLink)

      MockMovementTemplatesService.getExistingTemplateNames(testErn).returns(Future.successful(Seq()))

      val boundForm = form.bind(Map("value" -> ""))
      val result = controller.onSubmit(testErn, testDraftId, NormalMode)(request.withFormUrlEncodedBody(("value", "")))

      status(result) mustEqual BAD_REQUEST
      contentAsString(result) mustEqual view(boundForm, controllers.sections.templates.routes.SaveTemplateController.onSubmit(testErn, testDraftId), NormalMode)(dataRequest(request, userAnswers.get), messages).toString
    }

    "must redirect to Journey Recovery for a GET if no existing data is found" in new Test(None) {
      enable(TemplatesLink)
      val result = controller.onPageLoad(testErn, testDraftId, NormalMode)(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.JourneyRecoveryController.onPageLoad().url
    }

    "must redirect to Journey Recovery for a POST if no existing data is found" in new Test(None) {
      enable(TemplatesLink)
      val result = controller.onSubmit(testErn, testDraftId, NormalMode)(request.withFormUrlEncodedBody(("value", "true")))

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.JourneyRecoveryController.onPageLoad().url
    }
  }
}
