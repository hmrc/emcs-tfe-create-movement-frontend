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
import forms.sections.items.ItemProducerSizeFormProvider
import models.requests.DataRequest
import models.sections.info.movementScenario.MovementScenario.UkTaxWarehouse
import models.{Index, Mode}
import navigation.ItemsNavigator
import pages.sections.info.DestinationTypePage
import pages.sections.items.{ItemExciseProductCodePage, ItemProducerSizePage}
import play.api.data.Form
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import services.UserAnswersService
import utils.TimeMachine
import views.html.sections.items.ItemProducerSizeView

import java.time.Month
import javax.inject.Inject
import scala.concurrent.Future

class ItemProducerSizeController @Inject()(
                                       override val messagesApi: MessagesApi,
                                       override val userAnswersService: UserAnswersService,
                                       override val navigator: ItemsNavigator,
                                       override val auth: AuthAction,
                                       override val getData: DataRetrievalAction,
                                       override val requireData: DataRequiredAction,
                                       override val betaAllowList: BetaAllowListAction,
                                       formProvider: ItemProducerSizeFormProvider,
                                       val controllerComponents: MessagesControllerComponents,
                                       view: ItemProducerSizeView,
                                       timeMachine: TimeMachine
                                     ) extends BaseItemsNavigationController with AuthActionHelper {

  def onPageLoad(ern: String, draftId: String, idx: Index, mode: Mode): Action[AnyContent] =
    authorisedDataRequest(ern, draftId) { implicit request =>
      renderView(Ok, fillForm(ItemProducerSizePage(idx), formProvider()), idx, mode)
    }

  def onSubmit(ern: String, draftId: String, idx: Index, mode: Mode): Action[AnyContent] =
    authorisedDataRequestAsync(ern, draftId) { implicit request =>
      formProvider().bindFromRequest().fold(
        formWithErrors => Future(renderView(BadRequest, formWithErrors, idx, mode)),
        saveAndRedirect(ItemProducerSizePage(idx), _, mode)
      )
    }

  def unableToProvideInformation(ern: String, draftId: String, idx: Index, mode: Mode): Action[AnyContent] =
    authorisedDataRequestAsync(ern, draftId) { implicit request =>
      val updated = request.userAnswers.remove(ItemProducerSizePage(idx))
      saveAndRedirect(ItemProducerSizePage(idx), updated, mode)
    }

  private def renderView(status: Status, form: Form[_], idx: Index, mode: Mode)
                        (implicit request: DataRequest[_]): Result = withGoodsType(idx) { goodsType =>

    val destinationType = request.userAnswers.get(DestinationTypePage)
    val itemExciseProductCode = request.userAnswers.get(ItemExciseProductCodePage(idx))

    val showAlcoholProductionContent = destinationType.contains(UkTaxWarehouse.GB) || itemExciseProductCode.exists(Seq("S300", "S500").contains(_))

    status(view(
      form = form,
      onSubmitAction = routes.ItemProducerSizeController.onSubmit(request.ern, request.draftId, idx, mode),
      skipQuestionAction = routes.ItemProducerSizeController.unableToProvideInformation(request.ern, request.draftId, idx, mode),
      goodsType = goodsType,
      startYear = yearStart().toString,
      endYear = yearEnd().toString,
      index = idx,
      showAlcoholProductionContent = showAlcoholProductionContent
    ))
  }

  private def yearEnd(): Int = timeMachine.now().getMonth match {
    case Month.JANUARY => timeMachine.now().getYear - 1
    case _ => timeMachine.now().getYear
  }

  private def yearStart(): Int = yearEnd() - 1
}
