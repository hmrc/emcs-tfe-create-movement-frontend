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
import models.requests.DataRequest
import play.api.mvc.Results.Redirect
import play.api.mvc.{Action, ActionBuilder, AnyContent, Result}
import utils.Logging

import scala.concurrent.Future

trait AuthActionHelper extends Logging {

  val auth: AuthAction
  val getData: DataRetrievalAction
  val requireData: DataRequiredAction

  private def authorisedWithData(ern: String, draftId: String): ActionBuilder[DataRequest, AnyContent] =
    auth(ern) andThen getData(draftId) andThen requireData

  def authorisedDataRequest(ern: String, draftId: String)(block: DataRequest[_] => Result): Action[AnyContent] =
    authorisedWithData(ern, draftId)(block)

  def authorisedDataRequestAsync(ern: String, draftId: String)(block: DataRequest[_] => Future[Result]): Action[AnyContent] =
    authorisedWithData(ern, draftId).async(block)

  def ifCanUseDraftTemplates(ern: String, draftId: String)(block: => Future[Result])(implicit appConfig: AppConfig): Future[Result] = {
    if (appConfig.templatesFeatureEnabled) {
      block
    } else {
      logger.warn(s"[ifCanAccessDraftTemplates] User with ERN: $ern is not allowed to use draft templates")
      Future.successful(Redirect(controllers.routes.DeclarationController.onPageLoad(ern, draftId)))
    }
  }

}
