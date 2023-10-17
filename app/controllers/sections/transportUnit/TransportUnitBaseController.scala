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

package controllers.sections.transportUnit

import controllers.BaseNavigationController
import models.{NormalMode, TransportUnitType}
import models.requests.DataRequest
import pages.sections.transportUnit.TransportUnitTypePage
import play.api.mvc.Result

import scala.concurrent.Future

trait TransportUnitBaseController extends BaseNavigationController {

  def withTransportUnitTypeAnswer(f: TransportUnitType => Future[Result])(implicit request: DataRequest[_]): Future[Result] = {
    request.userAnswers.get(TransportUnitTypePage) match {
      case Some(transportUnitType) => f(transportUnitType)
      case None =>
        logger.warn(s"[withTransportUnitTypeAnswer] No answer, redirecting to get the answer")
        Future.successful(
          Redirect(
            controllers.sections.transportUnit.routes.TransportUnitTypeController.onPageLoad(request.ern, request.lrn, NormalMode)
          )
        )
    }
  }

}
