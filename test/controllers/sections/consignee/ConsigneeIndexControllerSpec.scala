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

package controllers.sections.consignee

import base.SpecBase
import models.NormalMode
import models.requests.UserRequest
import models.sections.info.movementScenario.MovementScenario._
import pages.sections.info.DestinationTypePage
import play.api.test.FakeRequest
import play.api.test.Helpers.{GET, _}

class ConsigneeIndexControllerSpec extends SpecBase {

  implicit val ur: UserRequest[_] = userRequest(FakeRequest())

  "ConsigneeIndexController" - {
    "must redirect to ConsigneeExemptOrganisationController" - {
      s"when destination is ${ExemptedOrganisation()}" in {
        val ern: String = testErn

        lazy val application = applicationBuilder(
          userAnswers = Some(emptyUserAnswers.set(DestinationTypePage, ExemptedOrganisation()))
        ).build()

        running(application) {
          val request = userRequest(FakeRequest(GET, controllers.sections.consignee.routes.ConsigneeIndexController.onPageLoad(ern, testLrn).url))
            .copy(ern = ern)
          val result = route(application, request).value

          status(result) mustBe SEE_OTHER
          redirectLocation(result) mustBe
            Some(controllers.sections.consignee.routes.ConsigneeExemptOrganisationController.onPageLoad(ern, testLrn, NormalMode).url)
        }
      }
    }

    "must redirect to ConsigneeExciseController" - {
      Seq(GbTaxWarehouse(), EuTaxWarehouse(), DirectDelivery()).foreach(
        movementScenario =>
          s"when destination is $movementScenario" in {
            val ern: String = testErn

            lazy val application = applicationBuilder(
              userAnswers = Some(emptyUserAnswers.set(DestinationTypePage, movementScenario))
            ).build()

            running(application) {
              val request = userRequest(FakeRequest(GET, controllers.sections.consignee.routes.ConsigneeIndexController.onPageLoad(ern, testLrn).url))
                .copy(ern = ern)
              val result = route(application, request).value

              status(result) mustBe SEE_OTHER
              redirectLocation(result) mustBe
                Some(controllers.sections.consignee.routes.ConsigneeExciseController.onPageLoad(ern, testLrn, NormalMode).url)
            }
          }
      )

      "when UserType is GBRC" - {
        val ern: String = "GBRC123"

        Seq(
          RegisteredConsignee(),
          TemporaryRegisteredConsignee(),
          ExportWithCustomsDeclarationLodgedInTheUk(),
          ExportWithCustomsDeclarationLodgedInTheEu()
        ).foreach(
          movementScenario =>
            s"and destination is $movementScenario" in {

              lazy val application = applicationBuilder(
                userAnswers = Some(emptyUserAnswers.set(DestinationTypePage, movementScenario))
              ).build()

              running(application) {
                val request = userRequest(FakeRequest(GET, controllers.sections.consignee.routes.ConsigneeIndexController.onPageLoad(ern, testLrn).url))
                  .copy(ern = ern)
                val result = route(application, request).value

                status(result) mustBe SEE_OTHER
                redirectLocation(result) mustBe
                  Some(controllers.sections.consignee.routes.ConsigneeExciseController.onPageLoad(ern, testLrn, NormalMode).url)
              }
            }
        )
      }

      "when UserType is XIRC" - {
        val ern: String = "XIRC123"

        Seq(
          RegisteredConsignee(),
          TemporaryRegisteredConsignee(),
          ExportWithCustomsDeclarationLodgedInTheUk(),
          ExportWithCustomsDeclarationLodgedInTheEu()
        ).foreach(
          movementScenario =>
            s"and destination is $movementScenario" in {

              lazy val application = applicationBuilder(
                userAnswers = Some(emptyUserAnswers.set(DestinationTypePage, movementScenario))
              ).build()

              running(application) {
                val request = userRequest(FakeRequest(GET, controllers.sections.consignee.routes.ConsigneeIndexController.onPageLoad(ern, testLrn).url))
                  .copy(ern = ern)
                val result = route(application, request).value

                status(result) mustBe SEE_OTHER
                redirectLocation(result) mustBe
                  Some(controllers.sections.consignee.routes.ConsigneeExciseController.onPageLoad(ern, testLrn, NormalMode).url)
              }
            }
        )
      }

      "when UserType is XIWK" - {
        val ern: String = "XIWK123"
        s"and destination is ${TemporaryRegisteredConsignee()}" in {

          lazy val application = applicationBuilder(
            userAnswers = Some(emptyUserAnswers.set(DestinationTypePage, TemporaryRegisteredConsignee()))
          ).build()

          running(application) {
            val request = userRequest(FakeRequest(GET, controllers.sections.consignee.routes.ConsigneeIndexController.onPageLoad(ern, testLrn).url))
              .copy(ern = ern)
            val result = route(application, request).value

            status(result) mustBe SEE_OTHER
            redirectLocation(result) mustBe
              Some(controllers.sections.consignee.routes.ConsigneeExciseController.onPageLoad(ern, testLrn, NormalMode).url)
          }
        }
      }
    }

    "must redirect to ConsigneeExportController" - {
      "when UserType is GBWK" - {
        val ern = "GBWK123"

        Seq(
          RegisteredConsignee(),
          TemporaryRegisteredConsignee(),
          ExportWithCustomsDeclarationLodgedInTheUk(),
          ExportWithCustomsDeclarationLodgedInTheEu()
        ).foreach(
          movementScenario =>
            s"and destination is $movementScenario" in {

              lazy val application = applicationBuilder(
                userAnswers = Some(emptyUserAnswers.set(DestinationTypePage, movementScenario))
              ).build()

              running(application) {
                val request = userRequest(FakeRequest(GET, controllers.sections.consignee.routes.ConsigneeIndexController.onPageLoad(ern, testLrn).url))
                  .copy(ern = ern)
                val result = route(application, request).value

                status(result) mustBe SEE_OTHER
                redirectLocation(result) mustBe
                  Some(controllers.sections.consignee.routes.ConsigneeExportController.onPageLoad(ern, testLrn, NormalMode).url)
              }
            }
        )
      }

      "when UserType is XIWK" - {
        val ern = "XIWK123"

        Seq(
          RegisteredConsignee(),
          ExportWithCustomsDeclarationLodgedInTheUk(),
          ExportWithCustomsDeclarationLodgedInTheEu()
        ).foreach(
          movementScenario =>
            s"and destination is $movementScenario" in {

              lazy val application = applicationBuilder(
                userAnswers = Some(emptyUserAnswers.set(DestinationTypePage, movementScenario))
              ).build()

              running(application) {
                val request = userRequest(FakeRequest(GET, controllers.sections.consignee.routes.ConsigneeIndexController.onPageLoad(ern, testLrn).url))
                  .copy(ern = ern)
                val result = route(application, request).value

                status(result) mustBe SEE_OTHER
                redirectLocation(result) mustBe
                  Some(controllers.sections.consignee.routes.ConsigneeExportController.onPageLoad(ern, testLrn, NormalMode).url)
              }
            }
        )
      }
    }

    "must redirect to the tasklist" - {
      "when user isn't any of the above (they shouldn't be able to access the NEE pages)" in {
        val ern = testErn
        lazy val application = applicationBuilder(
          userAnswers = Some(emptyUserAnswers.set(DestinationTypePage, UnknownDestination()))
        ).build()

        running(application) {
          val request = userRequest(FakeRequest(GET, controllers.sections.consignee.routes.ConsigneeIndexController.onPageLoad(ern, testLrn).url))
            .copy(ern = ern)
          val result = route(application, request).value

          status(result) mustBe SEE_OTHER
          redirectLocation(result) mustBe
            Some(testOnly.controllers.routes.UnderConstructionController.onPageLoad().url)
        }
      }
    }
  }

}
