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
import mocks.services.MockPreDraftService
import navigation.FakeNavigators.FakeInfoNavigator
import navigation.InformationNavigator
import play.api.Application
import play.api.inject.bind
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.PreDraftService

import scala.concurrent.Future

class IndexControllerSpec extends SpecBase with MockPreDraftService {

  "Index Controller" - {

    lazy val application: Application =
      applicationBuilder(userAnswers = None)
        .overrides(
          bind[InformationNavigator].toInstance(new FakeInfoNavigator(testOnwardRoute)),
          bind[PreDraftService].toInstance(mockPreDraftService)
        )
        .build()

    "must redirect to the info Index controller" in {
      running(application) {

        MockPreDraftService.set(emptyUserAnswers).returns(Future.successful(true))

        val request = FakeRequest(GET, routes.IndexController.onPageLoad(testNorthernIrelandErn).url)
        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result) mustBe Some(controllers.sections.info.routes.InfoIndexController.onPageLoad(testNorthernIrelandErn).url)
      }
    }
  }
}