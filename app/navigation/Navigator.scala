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

import config.AppConfig
import controllers.routes
import models._
import pages._
import play.api.mvc.Call

import javax.inject.{Inject, Singleton}

@Singleton
class Navigator @Inject()(appConfig: AppConfig) extends BaseNavigator {

  private val normalRoutes: Page => UserAnswers => Call = {
    case CheckAnswersPage =>
      (userAnswers: UserAnswers) =>
        if(appConfig.templatesFeatureEnabled) {
          if(userAnswers.createdFromTemplateId.isEmpty) {
            controllers.sections.templates.routes.SaveTemplateController.onPageLoad(userAnswers.ern, userAnswers.draftId)
          } else if (userAnswers.templateDataUnchanged) {
            routes.DeclarationController.onPageLoad(userAnswers.ern, userAnswers.draftId)
          } else {
            controllers.sections.templates.routes.UpdateTemplateController.onPageLoad(userAnswers.ern, userAnswers.draftId)
          }
        } else {
          routes.DeclarationController.onPageLoad(userAnswers.ern, userAnswers.draftId)
        }
    case DeclarationPage =>
      (userAnswers: UserAnswers) => routes.ConfirmationController.onPageLoad(userAnswers.ern, userAnswers.draftId)
    case _ =>
      (userAnswers: UserAnswers) => routes.IndexController.onPageLoad(userAnswers.ern)
  }

  private[navigation] val checkRouteMap: Page => UserAnswers => Call = {
    case _ =>
      (userAnswers: UserAnswers) => routes.CheckYourAnswersController.onPageLoad(userAnswers.ern, userAnswers.draftId)
  }

  private[navigation] val reviewRouteMap: Page => UserAnswers => Call = {
    case _ =>
      (userAnswers: UserAnswers) => routes.CheckYourAnswersController.onPageLoad(userAnswers.ern, userAnswers.draftId)
  }

  override def nextPage(page: Page, mode: Mode, userAnswers: UserAnswers): Call = mode match {
    case NormalMode =>
      normalRoutes(page)(userAnswers)
    case CheckMode =>
      checkRouteMap(page)(userAnswers)
    case ReviewMode =>
      reviewRouteMap(page)(userAnswers)
  }
}
