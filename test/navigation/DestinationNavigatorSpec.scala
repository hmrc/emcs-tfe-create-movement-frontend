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

package navigation

import base.SpecBase
import controllers.sections.destination.routes
import models.sections.info.movementScenario.MovementScenario.{CertifiedConsignee, TemporaryCertifiedConsignee}
import models.{CheckMode, NormalMode, ReviewMode}
import pages.Page
import pages.sections.consignee.{ConsigneeAddressPage, ConsigneeBusinessNamePage}
import pages.sections.destination._
import pages.sections.info.DestinationTypePage

class DestinationNavigatorSpec extends SpecBase {
  val navigator = new DestinationNavigator

  "DestinationNavigator" - {

    "in Normal mode" - {

      "must go from a page that doesn't exist in the route map to Destination CYA" in {

        case object UnknownPage extends Page
        navigator.nextPage(UnknownPage, NormalMode, emptyUserAnswers) mustBe
          routes.DestinationCheckAnswersController.onPageLoad(testErn, testDraftId)
      }

      "for the DestinationWarehouseExcisePage" - {

        "when ConsigneeDetails exist" - {

          "must go to Destination Consignee details page" in {

            val userAnswers = emptyUserAnswers
              .set(ConsigneeAddressPage, testUserAddress)
              .set(ConsigneeBusinessNamePage, testBusinessName)

            navigator.nextPage(DestinationWarehouseExcisePage, NormalMode, userAnswers) mustBe
              routes.DestinationConsigneeDetailsController.onPageLoad(testErn, testDraftId, NormalMode)
          }
        }

        "when ConsigneeDetails DO NOT exist" - {

          "must go to Destination Business Name page" in {

            navigator.nextPage(DestinationWarehouseExcisePage, NormalMode, emptyUserAnswers) mustBe
              routes.DestinationBusinessNameController.onPageLoad(testErn, testDraftId, NormalMode)
          }
        }
      }

      "for the DestinationWarehouseVatPage" - {

        Seq(CertifiedConsignee, TemporaryCertifiedConsignee).foreach { destinationType =>

          "when Consignee Details exist" - {

            s"must go to DestinationConsigneeDetails page when DestinationType is $destinationType" in {

              val userAnswers = emptyUserAnswers
                .set(ConsigneeAddressPage, testUserAddress)
                .set(ConsigneeBusinessNamePage, testBusinessName)
                .set(DestinationTypePage, destinationType)

              navigator.nextPage(DestinationWarehouseVatPage, NormalMode, userAnswers) mustBe
                routes.DestinationConsigneeDetailsController.onPageLoad(testErn, testDraftId, NormalMode)
            }
          }

          "when Consignee Details DO NOT exist" - {

            s"must go to DestinationBusinessName page when DestinationType is $destinationType" in {

              val userAnswers = emptyUserAnswers.set(DestinationTypePage, destinationType)

              navigator.nextPage(DestinationWarehouseVatPage, NormalMode, userAnswers) mustBe
                routes.DestinationBusinessNameController.onPageLoad(testErn, testDraftId, NormalMode)
            }
          }
        }

        "must go to DestinationDetailsChoice page when DestinationType is NOT CertifiedConsignee or TemporaryCertifiedConsignee" in {

          navigator.nextPage(DestinationWarehouseVatPage, NormalMode, emptyUserAnswers) mustBe
            routes.DestinationDetailsChoiceController.onPageLoad(testErn, testDraftId, NormalMode)
        }
      }

      "for the DestinationDetailsChoicePage" - {

        "when ConsigneeDetails exist" - {

          "must go to DestinationConsigneeDetails (CAM-DES03) when user selects yes" in {

            val userAnswers = emptyUserAnswers
              .set(ConsigneeBusinessNamePage, testBusinessName)
              .set(ConsigneeAddressPage, testUserAddress)
              .set(DestinationDetailsChoicePage, true)

            navigator.nextPage(DestinationDetailsChoicePage, NormalMode, userAnswers) mustBe
              routes.DestinationConsigneeDetailsController.onPageLoad(testErn, testDraftId, NormalMode)
          }
        }

        "when ConsigneeDetails DO NOT exist" - {

          "must go to DestinationBusinessName when user selects yes" in {

            val userAnswers = emptyUserAnswers.set(DestinationDetailsChoicePage, true)

            navigator.nextPage(DestinationDetailsChoicePage, NormalMode, userAnswers) mustBe
              routes.DestinationBusinessNameController.onPageLoad(testErn, testDraftId, NormalMode)
          }
        }

        "must go to DestinationCheckAnswersPage when user selects no" in {

          val userAnswers = emptyUserAnswers.set(DestinationDetailsChoicePage, false)

          navigator.nextPage(DestinationDetailsChoicePage, NormalMode, userAnswers) mustBe
            routes.DestinationCheckAnswersController.onPageLoad(testErn, testDraftId)
        }

        "must go to Journey Recovery if no answer is present" in {

          navigator.nextPage(DestinationDetailsChoicePage, NormalMode, emptyUserAnswers) mustBe
            controllers.routes.JourneyRecoveryController.onPageLoad()
        }
      }

      "for the DestinationConsigneeDetailsPage" - {

        "must go to Destination Business Name page (CAM-DES04)" in {

          navigator.nextPage(DestinationConsigneeDetailsPage, NormalMode, emptyUserAnswers) mustBe
            routes.DestinationBusinessNameController.onPageLoad(testErn, testDraftId, NormalMode)
        }
      }

      "for the DestinationBusinessNamePage" - {

        "must go to Destination Address page" in {

          navigator.nextPage(DestinationBusinessNamePage, NormalMode, emptyUserAnswers) mustBe
            routes.DestinationAddressController.onPageLoad(testErn, testDraftId, NormalMode)
        }
      }

      "for the DestinationAddressPage" - {

        "must go to DestinationCheckAnswersPage page" in {

          navigator.nextPage(DestinationAddressPage, NormalMode, emptyUserAnswers) mustBe
            routes.DestinationCheckAnswersController.onPageLoad(testErn, testDraftId)
        }
      }

      "for the DestinationCheckAnswersPage" - {

        "must go to tasklist page" in {

          navigator.nextPage(DestinationCheckAnswersPage, NormalMode, emptyUserAnswers) mustBe
            controllers.routes.DraftMovementController.onPageLoad(testErn, testDraftId)
        }
      }
    }

    "in Check mode" - {

      "must go to CheckYourAnswersDestinationController" in {

        case object UnknownPage extends Page
        navigator.nextPage(UnknownPage, CheckMode, emptyUserAnswers) mustBe
          routes.DestinationCheckAnswersController.onPageLoad(testErn, testDraftId)
      }
    }

    "in Review mode" - {

      "must go to CheckYourAnswers" in {

        case object UnknownPage extends Page
        navigator.nextPage(UnknownPage, ReviewMode, emptyUserAnswers) mustBe
          controllers.routes.CheckYourAnswersController.onPageLoad(testErn, testDraftId)
      }
    }
  }
}
