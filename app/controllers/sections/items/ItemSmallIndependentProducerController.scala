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

package controllers.sections.items

import controllers.actions._
import forms.sections.items.ItemSmallIndependentProducerFormProvider
import models.requests.DataRequest
import models.{Index, Mode}
import navigation.ItemsNavigator
import pages.sections.items.{ItemProducerSizePage, ItemSmallIndependentProducerPage}
import play.api.data.Form
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import services.UserAnswersService
import views.html.sections.items.ItemSmallIndependentProducerView

import javax.inject.Inject
import scala.concurrent.Future

class ItemSmallIndependentProducerController @Inject()(
                                                        override val messagesApi: MessagesApi,
                                                        override val userAnswersService: UserAnswersService,
                                                        override val navigator: ItemsNavigator,
                                                        override val auth: AuthAction,
                                                        override val getData: DataRetrievalAction,
                                                        override val requireData: DataRequiredAction,
                                                        formProvider: ItemSmallIndependentProducerFormProvider,
                                                        val controllerComponents: MessagesControllerComponents,
                                                        view: ItemSmallIndependentProducerView
                                                      ) extends BaseItemsNavigationController with AuthActionHelper {

  def onPageLoad(ern: String, draftId: String, idx: Index, mode: Mode): Action[AnyContent] =
    authorisedDataRequestAsync(ern, draftId) { implicit request =>
      validateIndexAsync(idx) {
        renderView(Ok, fillForm(ItemSmallIndependentProducerPage(idx), formProvider()), idx, mode)
      }
    }

  def onSubmit(ern: String, draftId: String, idx: Index, mode: Mode): Action[AnyContent] =
    authorisedDataRequestAsync(ern, draftId) { implicit request =>
      validateIndexAsync(idx) {
        formProvider().bindFromRequest().fold(
          renderView(BadRequest, _, idx, mode),
          value => {
            val newUserAnswers = cleanseUserAnswersIfValueHasChanged(
              page = ItemSmallIndependentProducerPage(idx),
              newAnswer = value,
              cleansingFunction = request.userAnswers.remove(ItemProducerSizePage(idx))
            )
            saveAndRedirect(ItemSmallIndependentProducerPage(idx), value, newUserAnswers, mode)
          }
        )
      }
    }

  private def renderView(status: Status, form: Form[_], idx: Index, mode: Mode)(implicit request: DataRequest[_]): Future[Result] =
    withGoodsTypeAsync(idx) { _ =>
      Future.successful(status(view(
        form = form,
        action = routes.ItemSmallIndependentProducerController.onSubmit(request.ern, request.draftId, idx, mode),
        index = idx
      )))
    }

}
