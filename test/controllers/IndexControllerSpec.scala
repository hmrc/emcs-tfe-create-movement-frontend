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
import play.api.test.FakeRequest
import play.api.test.Helpers._

class IndexControllerSpec extends SpecBase {

  "Index Controller" - {

    "with a Northern Ireland Warehouse Keeper ERN" - {

      "must redirect to the Dispatch place page (CAM-INFO01)" in {

        val application = applicationBuilder(userAnswers = None).build()

        running(application) {

          val request = FakeRequest(GET, routes.IndexController.onPageLoad(testNorthernIrelandErn).url)
          val result = route(application, request).value

          status(result) mustEqual SEE_OTHER
          redirectLocation(result) mustBe Some(controllers.sections.info.routes.DispatchPlaceController.onPageLoad(testNorthernIrelandErn).url)
        }
      }
    }

    "with any other ERN" - {

      "must redirect to the Destination Type page (CAM-INFO08)" in {

        val application = applicationBuilder(userAnswers = None).build()

        running(application) {

          val request = FakeRequest(GET, routes.IndexController.onPageLoad(testGreatBritainErn).url)
          val result = route(application, request).value

          status(result) mustEqual SEE_OTHER
          redirectLocation(result) mustBe Some(controllers.sections.info.routes.DestinationTypeController.onPageLoad(testGreatBritainErn).url)
        }
      }
    }
  }
}