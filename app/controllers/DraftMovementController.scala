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

import controllers.actions._
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.ValidationService
import views.html.DraftMovementView

import javax.inject.Inject

class DraftMovementController @Inject()(override val messagesApi: MessagesApi,
                                        override val auth: AuthAction,
                                         override val getData: DataRetrievalAction,
                                        override val requireData: DataRequiredAction,
                                        val controllerComponents: MessagesControllerComponents,
                                        val validationService: ValidationService,
                                        view: DraftMovementView
                                       ) extends BaseController with AuthActionHelper {

  def onPageLoad(ern: String, draftId: String): Action[AnyContent] =
    authorisedDataRequestAsync(ern, draftId) { implicit request =>
      validationService.validate().map(updatedUserAnswers =>
        Ok(view()(request.copy(userAnswers = updatedUserAnswers), implicitly))
      )
    }
}
