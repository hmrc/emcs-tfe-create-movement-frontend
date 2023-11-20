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
import config.SessionKeys
import handlers.ErrorHandler
import navigation.FakeNavigators.FakeNavigator
import navigation.Navigator
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import pages.DeclarationPage
import play.api.Application
import play.api.i18n.Messages
import play.api.inject.bind
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.ConfirmationView

class ConfirmationControllerSpec extends SpecBase with GuiceOneAppPerSuite {
  val testUserAnswers = emptyUserAnswers.set(DeclarationPage, testSubmissionDate)

  override lazy val app: Application =
    applicationBuilder(userAnswers = Some(testUserAnswers))
      .overrides(
        bind[Navigator].toInstance(new FakeNavigator(testOnwardRoute))
      ).build()

  val view: ConfirmationView = app.injector.instanceOf[ConfirmationView]
  val errorHandler: ErrorHandler = app.injector.instanceOf[ErrorHandler]
  val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest(GET, routes.ConfirmationController.onPageLoad(testErn, testDraftId).url)
  val controller: ConfirmationController = app.injector.instanceOf[ConfirmationController]
  implicit lazy val messagesInstance: Messages = messages(app)

  "Confirmation Controller" - {

    "when the confirmation receipt reference is held in session" - {

      "must return OK and the correct view for a GET" in {
        implicit val req = dataRequest(
          request = request.withSession(SessionKeys.SUBMISSION_RECEIPT_REFERENCE -> testConfirmationReference),
          answers = testUserAnswers
        )

        val result = controller.onPageLoad(testErn, testDraftId)(req)

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(testConfirmationReference, testSubmissionDate.toLocalDate).toString()
      }
    }

    "when NO confirmation receipt reference is held in session" - {

      "must return BadRequests" in {
        val req = dataRequest(request, emptyUserAnswers)

        val result = route(app, request).value

        status(result) mustEqual BAD_REQUEST
        contentAsString(result) mustEqual errorHandler.badRequestTemplate(req).toString
      }
    }
  }
}
