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

package controllers.auth

import config.AppConfig
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import utils.Logging
import views.html.auth.SignedOutView

import javax.inject.Inject

class SignedOutController @Inject()(
                                     val controllerComponents: MessagesControllerComponents,
                                     appConfig: AppConfig,
                                     view: SignedOutView
                                   ) extends FrontendBaseController with I18nSupport with Logging {

  def signedOutSaved: Action[AnyContent] = Action { implicit request =>
    Ok(view("signedOut.guidance.saved"))
  }

  def signedOutNotSaved: Action[AnyContent] = Action { implicit request =>
    Ok(view("signedOut.guidance.notSaved"))
  }

  def signOut(): Action[AnyContent] = Action {
    Redirect(appConfig.signOutUrl, Map("continue" -> Seq(appConfig.feedbackFrontendSurveyUrl)))
  }

  def signOut(becauseOfTimeout: Boolean = false): Action[AnyContent] = Action { request =>
    val savablePage = request.uri.matches(".*/trader/.*/draft/.*")
    val continue = (becauseOfTimeout, savablePage) match {
      case (false, _) => appConfig.feedbackFrontendSurveyUrl
      case (_, true) => appConfig.host + controllers.auth.routes.SignedOutController.signedOutSaved().url
      case (_, false) => appConfig.host + controllers.auth.routes.SignedOutController.signedOutNotSaved().url
    }
    Redirect(appConfig.signOutUrl, Map("continue" -> Seq(continue)))
  }

}
