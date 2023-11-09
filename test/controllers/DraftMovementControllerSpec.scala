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
import models.sections.info.movementScenario.MovementScenario
import pages.sections.info.DestinationTypePage
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.DraftMovementView

class DraftMovementControllerSpec extends SpecBase {
  "onPageLoad" - {
    "must render the page" in {
      val userAnswers = emptyUserAnswers.set(DestinationTypePage, MovementScenario.GbTaxWarehouse)
      val application = applicationBuilder(Some(userAnswers)).build()

      running(application) {
        lazy val request = FakeRequest(GET, routes.DraftMovementController.onPageLoad(testErn, testDraftId).url)

        lazy val result = route(application, request).value

        lazy val view = application.injector.instanceOf[DraftMovementView]

        status(result) mustEqual OK
        contentAsString(result) mustEqual
          view()(dataRequest(request, userAnswers), messages(application)).toString
      }
    }
  }
}
