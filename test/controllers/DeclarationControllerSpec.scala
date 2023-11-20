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
import navigation.FakeNavigators.FakeNavigator
import navigation.Navigator
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.Application
import play.api.inject.bind
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.DeclarationView


class DeclarationControllerSpec extends SpecBase with GuiceOneAppPerSuite {
  override lazy val app: Application =
    applicationBuilder(userAnswers = Some(emptyUserAnswers))
      .overrides(
        bind[Navigator].toInstance(new FakeNavigator(testOnwardRoute))
      ).build()

  lazy val controller: DeclarationController = app.injector.instanceOf[DeclarationController]
  lazy val view: DeclarationView = app.injector.instanceOf[DeclarationView]
  lazy val getRoute = routes.DeclarationController.onPageLoad(testErn, testDraftId)
  lazy val submitRoute = routes.DeclarationController.onSubmit(testErn, testDraftId)
  implicit lazy val messagesInstance = messages(app)

  "DeclarationController" - {
    "for GET onPageLoad" - {
      "must return the declaration page" in {
        implicit val request = dataRequest(
          FakeRequest(),
          emptyUserAnswers
        )

        val res = controller.onPageLoad(testErn, testDraftId)(request)

        status(res) mustBe OK
        contentAsString(res) mustBe view(submitRoute).toString()
      }
    }

    "for POST submit" - {
      "must save the timestamp and redirect" in {
        implicit val request = dataRequest(
          FakeRequest(),
          emptyUserAnswers
        )

        val res = controller.onSubmit(testErn, testDraftId)(request)

        status(res) mustBe SEE_OTHER
        redirectLocation(res) must contain(testOnwardRoute.url)
      }
    }
  }
}
