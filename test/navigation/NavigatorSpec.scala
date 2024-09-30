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
import config.AppConfig
import controllers.routes
import featureswitch.core.config.{FeatureSwitching, TemplatesLink}
import models._
import pages._

class NavigatorSpec extends SpecBase with FeatureSwitching {

  lazy val config = app.injector.instanceOf[AppConfig]
  lazy val navigator = app.injector.instanceOf[Navigator]

  "Navigator" - {

    "in Normal mode" - {

      "must go from a page that doesn't exist in the route map to Index" in {

        case object UnknownPage extends Page
        navigator.nextPage(UnknownPage, NormalMode, emptyUserAnswers) mustBe
          routes.IndexController.onPageLoad(testErn)
      }

      "for the CheckYourAnswers page" - {

        "when the Templates feature is enabled" - {

          "when movement was created from a template" - {

            "must go to the UpdateTemplate page" in {

              enable(TemplatesLink)

              navigator.nextPage(CheckAnswersPage, NormalMode, emptyUserAnswers.copy(createdFromTemplateId = Some(templateId))) mustBe
                controllers.sections.templates.routes.UpdateTemplateController.onPageLoad(testErn, testDraftId)
            }
          }

          "when movement was NOT created from a template" - {

            "must go to the SaveTemplate page" in {

              enable(TemplatesLink)

              navigator.nextPage(CheckAnswersPage, NormalMode, emptyUserAnswers.copy(createdFromTemplateId = None)) mustBe
                controllers.sections.templates.routes.SaveTemplateController.onPageLoad(testErn, testDraftId)
            }
          }
        }

        "when the Templates feature is disabled" - {

          "must go to the Declaration page" in {

            disable(TemplatesLink)

            navigator.nextPage(CheckAnswersPage, NormalMode, emptyUserAnswers) mustBe
              routes.DeclarationController.onPageLoad(testErn, testDraftId)
          }
        }
      }
      "for the Declaration page" - {

        "must go to the Confirmation page" in {

          navigator.nextPage(DeclarationPage, NormalMode, emptyUserAnswers) mustBe
            routes.ConfirmationController.onPageLoad(testErn, testDraftId)
        }
      }

    }

    "in Check mode" - {

      "must go from a page that doesn't exist in the edit route map to CheckYourAnswers" in {

        case object UnknownPage extends Page
        navigator.nextPage(UnknownPage, CheckMode, emptyUserAnswers) mustBe
          routes.CheckYourAnswersController.onPageLoad(testErn, testDraftId)
      }
    }

    "in review mode" - {

      "must go from a page that doesn't exist in the edit route map to CheckYourAnswers" in {

        case object UnknownPage extends Page
        navigator.nextPage(UnknownPage, ReviewMode, emptyUserAnswers) mustBe
          routes.CheckYourAnswersController.onPageLoad(testErn, testDraftId)
      }
    }
  }
}
