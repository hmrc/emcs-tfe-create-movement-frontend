package controllers.sections.items

import controllers.actions._
import forms.CommercialDescriptionFormProvider
import javax.inject.Inject
import models.Mode
import navigation.Navigator
import pages.CommercialDescriptionPage
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.UserAnswersService
import views.html.CommercialDescriptionView

import scala.concurrent.Future

class CommercialDescriptionController @Inject()(
                                       override val messagesApi: MessagesApi,
                                       override val userAnswersService: UserAnswersService,
                                       override val userAllowList: UserAllowListAction,
                                       override val navigator: Navigator,
                                       override val auth: AuthAction,
                                       override val getData: DataRetrievalAction,
                                       override val requireData: DataRequiredAction,
                                       formProvider: CommercialDescriptionFormProvider,
                                       val controllerComponents: MessagesControllerComponents,
                                       view: CommercialDescriptionView
                                     ) extends BaseNavigationController with AuthActionHelper {

  def onPageLoad(ern: String, draftId: String, mode: Mode): Action[AnyContent] =
    authorisedDataRequest(ern, draftId) { implicit request =>
      Ok(view(fillForm(CommercialDescriptionPage, formProvider()), mode))
    }

  def onSubmit(ern: String, draftId: String, mode: Mode): Action[AnyContent] =
    authorisedDataRequestAsync(ern, draftId) { implicit request =>
      formProvider().bindFromRequest().fold(
        formWithErrors =>
          Future.successful(BadRequest(view(formWithErrors, mode))),
        value =>
          saveAndRedirect(CommercialDescriptionPage, value, mode)
      )
    }
}
