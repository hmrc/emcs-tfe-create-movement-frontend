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

import controllers.actions.{AuthAction, DataRetrievalAction}
import models.UserAnswers
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.{AddressLookupFrontendService, UserAnswersService}

import javax.inject.Inject

class IndexController @Inject()(override val messagesApi: MessagesApi,
                                val userAnswersService: UserAnswersService,
                                addressLookupFrontendService: AddressLookupFrontendService,
                                authAction: AuthAction,
                                getData: DataRetrievalAction,
                                val controllerComponents: MessagesControllerComponents) extends BaseController {

  def onPageLoad(ern: String, lrn: String): Action[AnyContent] = (authAction(ern) andThen getData(lrn)).async { implicit request =>
    val userAnswers = request.userAnswers match {
      case Some(answers) => answers
      case _ => UserAnswers(request.internalId, request.ern, request.lrn)
    }
    userAnswersService.set(userAnswers).flatMap { _ =>
      addressLookupFrontendService.initialiseJourney(controllers.routes.CheckYourAnswersController.onPageLoad(ern, lrn)).map {
        case Right(url) => SeeOther(url)
        case _ => Redirect(routes.CheckYourAnswersController.onPageLoad(ern, lrn))
      }
      //TODO: Update to route to first page of journey
    }
  }

}
