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

import config.AppConfig
import controllers.actions._
import pages.DeclarationPage
import pages.sections.info.LocalReferenceNumberPage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import utils.Logging
import views.html.ConfirmationView

import javax.inject.Inject

class ConfirmationController @Inject()(
                                        override val messagesApi: MessagesApi,
                                        override val auth: AuthAction,
                                        override val betaAllowList: BetaAllowListAction,
                                        override val getData: DataRetrievalAction,
                                        override val requireData: DataRequiredAction,
                                        val controllerComponents: MessagesControllerComponents,
                                        config: AppConfig,
                                        view: ConfirmationView
                                      ) extends FrontendBaseController with I18nSupport with AuthActionHelper with Logging {

  def onPageLoad(ern: String, draftId: String): Action[AnyContent] =
    authorisedDataRequest(ern, draftId) { implicit request =>
      (request.userAnswers.get(LocalReferenceNumberPage()), request.userAnswers.get(DeclarationPage)) match {
        case (Some(submissionReference), Some(submissionTimestamp)) =>
          Ok(view(
            reference = submissionReference,
            dateOfSubmission = submissionTimestamp.toLocalDate,
            exciseEnquiriesLink = config.exciseGuidance,
            returnToAccountLink = config.emcsTfeHomeUrl,
            feedbackLink = config.feedbackFrontendSurveyUrl
          ))
        case _ =>
          logger.warn("[onPageLoad] Could not retrieve submission receipt reference or submission timestamp from user answers")
          Redirect(routes.JourneyRecoveryController.onPageLoad())
      }
    }
}
