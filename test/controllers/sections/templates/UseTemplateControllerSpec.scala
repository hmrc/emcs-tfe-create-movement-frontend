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

package controllers.sections.templates

import base.SpecBase
import controllers.actions.predraft.FakePreDraftRetrievalAction
import forms.sections.templates.UseTemplateFormProvider
import mocks.services.{MockPreDraftService, MockUserAnswersService}
import models.UserAnswers
import play.api.data.Form
import play.api.mvc.AnyContentAsEmpty
import play.api.test.Helpers._
import play.api.test.{FakeRequest, Helpers}
import views.html.sections.templates.UseTemplateView

class UseTemplateControllerSpec extends SpecBase with MockUserAnswersService with MockPreDraftService {

  lazy val formProvider: UseTemplateFormProvider = new UseTemplateFormProvider()
  lazy val form: Form[Boolean] = formProvider()
  lazy val view: UseTemplateView = app.injector.instanceOf[UseTemplateView]

  class Fixture(val userAnswers: Option[UserAnswers]) {
    lazy val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest()

    lazy val controller = new UseTemplateController(
      messagesApi = messagesApi,
      auth = fakeAuthAction,
      getPreDraftData = new FakePreDraftRetrievalAction(userAnswers, Some(testMinTraderKnownFacts)),
      requirePreDraftData = preDraftDataRequiredAction,
      formProvider = formProvider,
      controllerComponents = Helpers.stubMessagesControllerComponents(),
      view = view,
      appConfig = appConfig
    )
  }

  lazy val useTemplateSubmitRoute = controllers.sections.templates.routes.UseTemplateController.onSubmit(testErn)

  "UseTemplate Controller" - {

    "must return OK and the correct view for a GET" in new Fixture(Some(emptyUserAnswers)) {
      val result = controller.onPageLoad(testErn)(request)

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(form, useTemplateSubmitRoute)(dataRequest(request), messages(request)).toString
    }

    "must redirect to the Account FE templates page when (UseTemplate - True)" in new Fixture(Some(emptyUserAnswers)) {

      val result = controller.onSubmit(testErn)(request.withFormUrlEncodedBody(("useTemplate", "true")))

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual appConfig.emcsTfeTemplatesUrl(testErn)
    }

    "must redirect to the Info Index Controller when (UseTemplate - False)" in new Fixture(Some(emptyUserAnswers)) {

      val result = controller.onSubmit(testErn)(request.withFormUrlEncodedBody(("useTemplate", "false")))

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.sections.info.routes.InfoIndexController.onPreDraftPageLoad(testErn).url
    }

    "must return a Bad Request and errors when invalid data is submitted" in new Fixture(Some(emptyUserAnswers)) {

      val boundForm = form.bind(Map("useTemplate" -> ""))
      val result = controller.onSubmit(testErn)(request.withFormUrlEncodedBody(("value", "")))

      status(result) mustEqual BAD_REQUEST
      contentAsString(result) mustEqual view(boundForm, useTemplateSubmitRoute)(dataRequest(request), messages(request)).toString
    }
  }
}
