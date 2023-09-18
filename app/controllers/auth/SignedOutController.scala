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

import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import utils.Logging
import views.html.auth.SignedOutView

import javax.inject.Inject

class SignedOutController @Inject()(
                                     val controllerComponents: MessagesControllerComponents,
                                     view: SignedOutView
                                   ) extends FrontendBaseController with I18nSupport with Logging {

  def onPageLoad: Action[AnyContent] = Action { implicit request =>

    val referer: Option[String] = request.session.get(REFERER_SESSION_KEY).flatMap(_.split("/").lastOption)

    logger.debug(s"referer from session: [${request.session.get(REFERER_SESSION_KEY)}]")
    logger.debug(s"referer variable: [$referer]")

    val infoRoutes: Seq[String] = Seq(
      //INFO01
      controllers.routes.LocalReferenceNumberController.onPageLoad("ern").url,
      //INFO03
      //INFO04
      controllers.routes.DeferredMovementController.onPageLoad("ern").url,
      //INFO06
      //INFO07
      //INFO08
      // TODO: INFO routes should not be saved. Add all INFO routes to this as they are created.
      testOnly.controllers.routes.UnderConstructionController.onPageLoad().url
    ).flatMap(_.split("/").lastOption)


    val guidance: String = referer match {
      case Some(value) if infoRoutes.exists(value.contains) => "signedOut.guidance.notSaved"
      case _ => "signedOut.guidance.saved"
    }

    Ok(view(guidance)).removingFromSession(REFERER_SESSION_KEY)
  }
}
