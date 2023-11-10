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
import models.{Index, NormalMode}
import play.api.http.Status.SEE_OTHER
import play.api.test.FakeRequest
import play.api.test.Helpers._

class ItemsIndexControllerSpec extends SpecBase {
  "ItemsIndexController" - {

    "when ItemsSection.isCompleted" - {
      // TODO: remove ignore when CYA page is built
      "must redirect to the CYA controller" ignore {
        val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

        running(application) {

          val request = FakeRequest(GET, controllers.sections.items.routes.ItemsIndexController.onPageLoad(testErn, testDraftId).url)
          val result = route(application, request).value

          status(result) mustEqual SEE_OTHER
          redirectLocation(result).value mustBe testOnly.controllers.routes.UnderConstructionController.onPageLoad().url
        }
      }
    }

    "must redirect to the items excise product code controller" in {
      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      running(application) {

        val request = FakeRequest(GET, controllers.sections.items.routes.ItemsIndexController.onPageLoad(testErn, testDraftId).url)
        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustBe
          controllers.sections.items.routes.ItemExciseProductCodeController.onPageLoad(testErn, testDraftId, Index(0), NormalMode).url
      }
    }
  }
}