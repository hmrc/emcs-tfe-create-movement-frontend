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

package controllers

import base.SpecBase
import controllers.actions.FakeDataRetrievalAction
import controllers.sections.templates.SaveTemplateController
import featureswitch.core.config.{FeatureSwitching, TemplatesLink}
import forms.SaveTemplateFormProvider
import mocks.services.MockUserAnswersService
import models.{NormalMode, UserAnswers}
import navigation.FakeNavigators.FakeNavigator
import pages.SaveTemplatePage
import play.api.i18n.{Messages, MessagesApi}
import play.api.mvc.AnyContentAsEmpty
import play.api.test.Helpers._
import play.api.test.{FakeRequest, Helpers}
import views.html.SaveTemplateView

class SaveTemplateControllerSpec extends SpecBase with MockUserAnswersService with FeatureSwitching {

  lazy val config = appConfig
  lazy val formProvider = new SaveTemplateFormProvider()
  lazy val form = formProvider()
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
      view
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
      val result = controller.onPageLoad(testErn, testDraftId, NormalMode)(request)

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(form, controllers.sections.templates.routes.SaveTemplateController.onSubmit(testErn, testDraftId),NormalMode)(dataRequest(request, userAnswers.get), messages).toString
    }

    "must populate the view correctly on a GET when the question has previously been answered" in new Test(Some(
      emptyUserAnswers.set(SaveTemplatePage, true)
    )) {
      enable(TemplatesLink)
      val result = controller.onPageLoad(testErn, testDraftId, NormalMode)(request)

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(form.fill(true), controllers.sections.templates.routes.SaveTemplateController.onSubmit(testErn, testDraftId), NormalMode)(dataRequest(request, userAnswers.get), messages).toString
    }

    "must redirect to the name template page when valid data is submitted and value is true" in new Test(Some(emptyUserAnswers)) {
      enable(TemplatesLink)
      val result = controller.onSubmit(testErn, testDraftId, NormalMode)(request.withFormUrlEncodedBody(("value", "true")))

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual testOnly.controllers.routes.UnderConstructionController.onPageLoad().url
    }

    "must redirect to the declaration page when valid data is submitted and value is false" in new Test(Some(emptyUserAnswers)) {
      enable(TemplatesLink)
      val result = controller.onSubmit(testErn, testDraftId, NormalMode)(request.withFormUrlEncodedBody(("value", "false")))

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.DeclarationController.onPageLoad(testErn, testDraftId).url
    }

    "must return a Bad Request and errors when invalid data is submitted" in new Test(Some(emptyUserAnswers)) {
      enable(TemplatesLink)
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
