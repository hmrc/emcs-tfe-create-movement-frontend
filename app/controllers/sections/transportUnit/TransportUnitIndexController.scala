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
import controllers.actions._
import models.{Index, NormalMode}
import navigation.TransportUnitNavigator
import pages.sections.transportUnit.TransportUnitsSection
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.UserAnswersService

import javax.inject.Inject
import scala.concurrent.Future

class TransportUnitIndexController @Inject()(
                                              override val messagesApi: MessagesApi,
                                              override val userAnswersService: UserAnswersService,
                                              override val navigator: TransportUnitNavigator,
                                              override val auth: AuthAction,
                                              override val getData: DataRetrievalAction,
                                              override val requireData: DataRequiredAction,
                                              override val userAllowList: UserAllowListAction,
                                              val controllerComponents: MessagesControllerComponents
                                            ) extends BaseNavigationController with AuthActionHelper {

  def onPageLoad(ern: String, lrn: String): Action[AnyContent] =
    authorisedDataRequestAsync(ern, lrn) { implicit request =>
      request.userAnswers.get(TransportUnitsSection) match {
        case Some(_) =>
          // TODO redirect ot CAM-TU07 when page built
          Future.successful(Redirect(
            testOnly.controllers.routes.UnderConstructionController.onPageLoad()
          ))
        case None => Future.successful(Redirect(
          controllers.sections.transportUnit.routes.TransportUnitTypeController.onPageLoad(request.ern, request.draftId, Index(0), NormalMode)
        ))
      }
    }
}
