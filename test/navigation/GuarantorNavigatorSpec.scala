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
import models.NormalMode
import models.sections.guarantor.GuarantorArranger
import models.sections.guarantor.GuarantorArranger._
import pages.sections.guarantor._
import pages.{GuarantorArrangerPage, Page}

class GuarantorNavigatorSpec extends SpecBase {
  val navigator = new GuarantorNavigator

  "GuarantorNavigator" - {

    "in Normal mode" - {

      "must go from a page that doesn't exist in the route map to Index" in {

        case object UnknownPage extends Page
        navigator.nextPage(UnknownPage, NormalMode, emptyUserAnswers) mustBe
          routes.IndexController.onPageLoad(testErn)
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
      }

      "for GuarantorArrangerPage" - {

        GuarantorArranger.values.foreach {
          case value@(GoodsOwner | Transporter) =>
            "must goto CAM-G03" - {
              s"when the arranger value is $value aka ${value.getClass.getSimpleName}" in {
                val userAnswers = emptyUserAnswers
                  .set(GuarantorRequiredPage, true)
                  .set(GuarantorArrangerPage, value)

                navigator.nextPage(GuarantorArrangerPage, NormalMode, userAnswers) mustBe
                  controllers.sections.guarantor.routes.GuarantorNameController.onPageLoad(testErn, testDraftId, NormalMode)
              }
            }
          case value@(Consignee | Consignor) =>
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

      "for GuarantorNamePage" - {
        "must goto CAM-G04" in {
          navigator.nextPage(GuarantorNamePage, NormalMode, emptyUserAnswers) mustBe
            controllers.sections.guarantor.routes.GuarantorVatController.onPageLoad(testErn, testDraftId, NormalMode)
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
        //TODO: Update to route to next section when built
        "must goto under construction page" in {
          navigator.nextPage(GuarantorCheckAnswersPage, NormalMode, emptyUserAnswers) mustBe
            testOnly.controllers.routes.UnderConstructionController.onPageLoad()
        }
      }
    }
  }
}
