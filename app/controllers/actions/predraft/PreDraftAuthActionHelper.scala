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

package controllers.actions.predraft

import controllers.actions.AuthAction
import models.requests.DataRequest
import play.api.mvc.{Action, ActionBuilder, AnyContent, Result}

import scala.concurrent.Future

trait PreDraftAuthActionHelper {

  val auth: AuthAction
  val getPreDraftData: PreDraftDataRetrievalAction
  val requirePreDraftData: PreDraftDataRequiredAction

  def authorisedWithPreDraftData(ern: String): ActionBuilder[DataRequest, AnyContent] =
    auth(ern) andThen getPreDraftData() andThen requirePreDraftData

  def authorisedPreDraftDataRequestAsync(ern: String)(block: DataRequest[_] => Future[Result]): Action[AnyContent] =
    authorisedWithPreDraftData(ern).async(block)

}
