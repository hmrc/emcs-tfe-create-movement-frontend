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
import controllers.routes
import models.sections.guarantor.GuarantorArranger
import models.sections.guarantor.GuarantorArranger._
import models.{CheckMode, NormalMode, ReviewMode}
import pages.Page
import pages.sections.guarantor._

class GuarantorNavigatorSpec extends SpecBase {
  val navigator = new GuarantorNavigator

  "GuarantorNavigator" - {

    "in Normal mode" - {

      "must go from a page that doesn't exist in the route map to Guarantor CYA" in {
        case object UnknownPage extends Page
        navigator.nextPage(UnknownPage, NormalMode, emptyUserAnswers) mustBe
          controllers.sections.guarantor.routes.GuarantorCheckAnswersController.onPageLoad(testErn, testDraftId)
      }

      "for GuarantorRequiredPage" - {

        "when true" - {

          "must go to CAM-G02" in {

            val userAnswers = emptyUserAnswers.set(GuarantorRequiredPage, true)

            navigator.nextPage(GuarantorRequiredPage, NormalMode, userAnswers) mustBe
              controllers.sections.guarantor.routes.GuarantorArrangerController.onPageLoad(testErn, testDraftId, NormalMode)
          }
        }

        "when false" - {

          "must go to CAM-G06" in {

            val userAnswers = emptyUserAnswers.set(GuarantorRequiredPage, false)

            navigator.nextPage(GuarantorRequiredPage, NormalMode, userAnswers) mustBe
              controllers.sections.guarantor.routes.GuarantorCheckAnswersController.onPageLoad(testErn, testDraftId)
          }
        }

        "when NOT answered" - {

          "must go to CAM-G02" in {

            navigator.nextPage(GuarantorRequiredPage, NormalMode, emptyUserAnswers) mustBe
              controllers.sections.guarantor.routes.GuarantorArrangerController.onPageLoad(testErn, testDraftId, NormalMode)
          }
        }
      }

      "for GuarantorArrangerPage" - {

        GuarantorArranger.displayValues.foreach {
          case value@(GoodsOwner | Transporter) =>
            "must goto VAT page" - {
              s"when the arranger value is $value aka ${value.getClass.getSimpleName}" in {
                val userAnswers = emptyUserAnswers
                  .set(GuarantorRequiredPage, true)
                  .set(GuarantorArrangerPage, value)

                navigator.nextPage(GuarantorArrangerPage, NormalMode, userAnswers) mustBe
                  controllers.sections.guarantor.routes.GuarantorVatController.onPageLoad(testErn, testDraftId, NormalMode)
              }
            }
          case value =>
            "must goto CAM-G06" - {
              s"when the arranger value is $value aka ${value.getClass.getSimpleName}" in {
                val userAnswers = emptyUserAnswers
                  .set(GuarantorRequiredPage, true)
                  .set(GuarantorArrangerPage, value)

                navigator.nextPage(GuarantorArrangerPage, NormalMode, userAnswers) mustBe
                  controllers.sections.guarantor.routes.GuarantorCheckAnswersController.onPageLoad(testErn, testDraftId)
              }
            }
        }
      }

      "for GuarantorVATPage" - {
        "must goto CAM-G05" in {
          navigator.nextPage(GuarantorVatPage, NormalMode, emptyUserAnswers) mustBe
            controllers.sections.guarantor.routes.GuarantorAddressController.onPageLoad(testErn, testDraftId, NormalMode)
        }
      }

      "for GuarantorAddressPage" - {
        "must goto CAM-G06" in {
          navigator.nextPage(GuarantorAddressPage, NormalMode, emptyUserAnswers) mustBe
            controllers.sections.guarantor.routes.GuarantorCheckAnswersController.onPageLoad(testErn, testDraftId)
        }
      }

      "for GuarantorCheckAnswersPage" - {
        "must goto the tasklist page" in {
          navigator.nextPage(GuarantorCheckAnswersPage, NormalMode, emptyUserAnswers) mustBe
            routes.DraftMovementController.onPageLoad(testErn, testDraftId)
        }
      }
    }

    "in Check mode" - {
      "must go to Guarantor CYA" in {
        case object UnknownPage extends Page
        navigator.nextPage(UnknownPage, CheckMode, emptyUserAnswers) mustBe
          controllers.sections.guarantor.routes.GuarantorCheckAnswersController.onPageLoad(testErn, testDraftId)
      }
    }

    "in Review mode" - {
      "must go to CheckYourAnswers" in {
        case object UnknownPage extends Page
        navigator.nextPage(UnknownPage, ReviewMode, emptyUserAnswers) mustBe
          routes.CheckYourAnswersController.onPageLoad(testErn, testDraftId)
      }
    }
  }
}
