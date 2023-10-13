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

package controllers.sections.guarantor

import controllers.BaseNavigationController
import models.NormalMode
import models.requests.DataRequest
import models.sections.guarantor.GuarantorArranger
import models.sections.guarantor.GuarantorArranger.{GoodsOwner, Transporter}
import pages.GuarantorArrangerPage
import pages.sections.guarantor.GuarantorRequiredPage
import play.api.mvc.Result

import scala.concurrent.Future

trait GuarantorBaseController extends BaseNavigationController {

  def withGuarantorRequiredAnswer(f: Future[Result]) (implicit request: DataRequest[_]): Future[Result] = {
    request.userAnswers.get(GuarantorRequiredPage) match {
      case Some(true) => f
      case _ =>
        logger.warn(s"[withGuarantorRequiredAnswer] No answer, redirecting to get the answer")
        Future.successful(
          Redirect(
            controllers.sections.guarantor.routes.GuarantorRequiredController.onPageLoad(request.ern, request.lrn, NormalMode)
          )
        )
    }
  }


  def withGuarantorArrangerAnswer(f: GuarantorArranger => Future[Result])(implicit request: DataRequest[_]): Future[Result] = {
    request.userAnswers.get(GuarantorArrangerPage) match {
      case Some(guarantorArranger) if guarantorArranger == GoodsOwner | guarantorArranger == Transporter => f(guarantorArranger)
      case Some(guarantorArranger) =>
        logger.warn(s"[withGuarantorArrangerAnswer] Invalid answer of $guarantorArranger for this controller/page")
        Future.successful(
          Redirect(
            controllers.routes.JourneyRecoveryController.onPageLoad()
          )
        )
      case None =>
        logger.warn(s"[withGuarantorArrangerAnswer] No answer, redirecting to get the answer")
        Future.successful(
          Redirect(
            controllers.sections.guarantor.routes.GuarantorArrangerController.onPageLoad(request.ern, request.lrn, NormalMode)
          )
        )
    }
  }

}
