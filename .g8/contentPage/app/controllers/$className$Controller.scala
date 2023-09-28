package controllers

import javax.inject.Inject
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.$className$View

class $className$Controller @Inject()(
                                       override val messagesApi: MessagesApi,
                                       val controllerComponents: MessagesControllerComponents,
                                       view: $className$View
                                     ) extends FrontendBaseController with I18nSupport {

  def onPageLoad: Action[AnyContent] = Action { implicit request =>
    Ok(view())
  }
}
