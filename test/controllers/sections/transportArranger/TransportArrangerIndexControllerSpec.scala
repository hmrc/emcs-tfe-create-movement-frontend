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

package controllers.sections.transportArranger

import base.SpecBase
import models.sections.transportArranger.TransportArranger.Consignor
import models.{NormalMode, UserAddress}
import pages.sections.consignor.ConsignorAddressPage
import pages.sections.transportArranger._
import play.api.http.Status.SEE_OTHER
import play.api.test.FakeRequest
import play.api.test.Helpers._

class TransportArrangerIndexControllerSpec extends SpecBase {

  "TransportArrangerIndexController" - {
    "when TransportArrangerSection.isCompleted" - {
      "must redirect to the CYA controller" in {
        val application = applicationBuilder(userAnswers = Some(
          emptyUserAnswers
          .set(TransportArrangerPage, Consignor)
          .set(ConsignorAddressPage, UserAddress(None, "", "", ""))
        )).build()

        running(application) {

          val request = FakeRequest(GET, controllers.sections.transportArranger.routes.TransportArrangerIndexController.onPageLoad(testErn, testDraftId).url)
          val result = route(application, request).value

          status(result) mustEqual SEE_OTHER
          redirectLocation(result) mustBe
            Some(controllers.sections.transportArranger.routes.TransportArrangerCheckAnswersController.onPageLoad(testErn, testDraftId).url)
        }
      }
    }
    "must redirect to the transportArranger controller" in {
      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      running(application) {

        val request = FakeRequest(GET, controllers.sections.transportArranger.routes.TransportArrangerIndexController.onPageLoad(testErn, testDraftId).url)
        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result) mustBe
          Some(controllers.sections.transportArranger.routes.TransportArrangerController.onPageLoad(testErn, testDraftId, NormalMode).url)
      }
    }

  }

}
