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

package controllers.actions

import config.AppConfig
import controllers.routes
import models.requests.{DataRequest, OptionalDataRequest}
import play.api.mvc.Results.Redirect
import play.api.mvc.{ActionRefiner, Result}
import utils.Logging

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class DataRequiredActionImpl @Inject()(appConfig: AppConfig)(implicit val executionContext: ExecutionContext) extends DataRequiredAction with Logging {

  override protected def refine[A](request: OptionalDataRequest[A]): Future[Either[Result, DataRequest[A]]] = {

    request.userAnswers match {
      case None =>
        Future.successful(Left(Redirect(routes.JourneyRecoveryController.onPageLoad())))
      case Some(userAnswers) =>
        lazy val isOnConfirmationScreen =
          request.uri == routes.ConfirmationController.onPageLoad(request.ern, request.draftId).url

        if(userAnswers.hasBeenSubmitted && !isOnConfirmationScreen) {
          // hasBeenSubmitted is set to `true` on successful CaM submission
          // if an IE704 is generated, hasBeenSubmitted is set back to false so they should be able to access the draft again
          logger.debug(s"[refine] User with ERN: ${request.ern} has already submitted the movement, redirecting to account home page")
          Future.successful(Left(Redirect(appConfig.emcsTfeFrontendHomeUrl(request.ern))))
        } else {
          Future.successful(Right(DataRequest(request.request, request.draftId, userAnswers, request.traderKnownFacts)))
        }
    }
  }
}

trait DataRequiredAction extends ActionRefiner[OptionalDataRequest, DataRequest]
