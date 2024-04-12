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

package controllers.sections.transportArranger

import controllers.BaseNavigationController
import controllers.actions._
import forms.sections.transportArranger.TransportArrangerVatFormProvider
import forms.sections.transportArranger.TransportArrangerVatFormProvider.{transportArrangerVatNumberField, transportArrangerVatNumberRequired}
import models.Mode
import models.requests.DataRequest
import models.sections.transportArranger.{TransportArranger, TransportArrangerVatModel}
import navigation.TransportArrangerNavigator
import pages.sections.transportArranger.{TransportArrangerPage, TransportArrangerVatPage}
import play.api.data.Form
import play.api.i18n.{Messages, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import services.UserAnswersService
import views.html.sections.transportArranger.TransportArrangerVatView

import javax.inject.Inject
import scala.concurrent.Future

class TransportArrangerVatController @Inject()(
                                                override val messagesApi: MessagesApi,
                                                override val userAnswersService: UserAnswersService,
                                                override val navigator: TransportArrangerNavigator,
                                                override val auth: AuthAction,
                                                override val getData: DataRetrievalAction,
                                                override val requireData: DataRequiredAction,
                                                override val betaAllowList: BetaAllowListAction,
                                                formProvider: TransportArrangerVatFormProvider,
                                                val controllerComponents: MessagesControllerComponents,
                                                view: TransportArrangerVatView
                                              ) extends BaseNavigationController with AuthActionHelper {

  def onPageLoad(ern: String, draftId: String, mode: Mode): Action[AnyContent] =
    authorisedDataRequestAsync(ern, draftId) { implicit request =>
      withAnswerAsync(
        page = TransportArrangerPage,
        redirectRoute = controllers.sections.transportArranger.routes.TransportArrangerIndexController.onPageLoad(request.ern, request.draftId)
      ) { arranger =>
        renderView(Ok, fillForm(TransportArrangerVatPage, formProvider(arranger)), arranger, mode)
      }
    }

  def onSubmit(ern: String, draftId: String, mode: Mode): Action[AnyContent] =
    authorisedDataRequestAsync(ern, draftId) { implicit request =>
      withAnswerAsync(
        page = TransportArrangerPage,
        redirectRoute = controllers.sections.transportArranger.routes.TransportArrangerIndexController.onPageLoad(request.ern, request.draftId)
      ) { arranger =>
        formProvider(arranger).bindFromRequest().fold(
          renderView(BadRequest, _, arranger, mode),
          handleSubmittedForm(_, arranger, mode)
        )
      }
    }

  private def renderView(status: Status, form: Form[_], arranger: TransportArranger, mode: Mode)(implicit request: DataRequest[_]): Future[Result] =
      Future.successful(status(view(
        form,
        routes.TransportArrangerVatController.onSubmit(request.ern, request.draftId, mode),
        arranger
      )))

  private def handleSubmittedForm(transportArrangerVatModel: TransportArrangerVatModel, arranger: TransportArranger, mode: Mode)
                                 (implicit request: DataRequest[_], messages: Messages): Future[Result] = {

    if (transportArrangerVatModel.hasTransportArrangerVatNumber && transportArrangerVatModel.transportArrangerVatNumber.isEmpty) {
      renderView(
        status = BadRequest,
        form =
          formProvider(arranger)
            .fill(transportArrangerVatModel)
            .withError(transportArrangerVatNumberField, messages(transportArrangerVatNumberRequired)),
        arranger = arranger,
        mode = mode
      )
    } else {
      saveAndRedirect(TransportArrangerVatPage, transportArrangerVatModel, mode)
    }
  }
}
