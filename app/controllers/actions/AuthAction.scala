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

package controllers.actions

import config.{AppConfig, EnrolmentKeys}
import connectors.emcsTfeFrontend.NavBarPartialConnector
import models.requests.UserRequest
import models.{GreatBritainWarehouse, NorthernIrelandTemporaryCertifiedConsignor, NorthernIrelandWarehouse, Unknown, UserType}
import play.api.mvc.Results._
import play.api.mvc._
import uk.gov.hmrc.auth.core.AffinityGroup.Organisation
import uk.gov.hmrc.auth.core._
import uk.gov.hmrc.auth.core.retrieve.v2.Retrievals
import uk.gov.hmrc.auth.core.retrieve.~
import uk.gov.hmrc.http.{HeaderCarrier, SessionId}
import uk.gov.hmrc.play.http.HeaderCarrierConverter
import utils.Logging

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

trait AuthAction {
  def apply(ern: String): ActionBuilder[UserRequest, AnyContent] with ActionFunction[Request, UserRequest]
}

@Singleton
class AuthActionImpl @Inject()(override val authConnector: AuthConnector,
                               navBarPartialConnector: NavBarPartialConnector,
                               config: AppConfig,
                               val bodyParser: BodyParsers.Default
                              )(implicit val ec: ExecutionContext) extends AuthAction with AuthorisedFunctions with Logging {

  def apply(ern: String): ActionBuilder[UserRequest, AnyContent] with ActionFunction[Request, UserRequest] =
    new ActionBuilder[UserRequest, AnyContent] with ActionFunction[Request, UserRequest] {

      override val parser = bodyParser
      override val executionContext = ec

      override def invokeBlock[A](request: Request[A], block: UserRequest[A] => Future[Result]): Future[Result] = {

        implicit val hc: HeaderCarrier = HeaderCarrierConverter.fromRequestAndSession(request, request.session)
        implicit val req = request

        authorised().retrieve(Retrievals.affinityGroup and Retrievals.allEnrolments and Retrievals.internalId and Retrievals.credentials) {

          case Some(Organisation) ~ enrolments ~ Some(internalId) ~ Some(credentials) =>
            checkOrganisationEMCSEnrolment(ern, enrolments, internalId, credentials.providerId, hc.sessionId)(block)

          case Some(Organisation) ~ _ ~ None ~ _ =>
            logger.warn("[invokeBlock] InternalId could not be retrieved from Auth")
            Future.successful(Redirect(controllers.error.routes.ErrorController.unauthorised()))

          case Some(Organisation) ~ _ ~ _ ~ None =>
            logger.warn("[invokeBlock] Credentials could not be retrieved from Auth")
            Future.successful(Redirect(controllers.error.routes.ErrorController.unauthorised()))

          case Some(affinityGroup) ~ _ ~ _ ~ _ =>
            logger.warn(s"[invokeBlock] User has incompatible AffinityGroup of '$affinityGroup'")
            Future.successful(Redirect(controllers.error.routes.ErrorController.notAnOrganisation()))

          case _ =>
            logger.warn(s"[invokeBlock] User has no AffinityGroup")
            Future.successful(Redirect(controllers.error.routes.ErrorController.unauthorised()))

        } recover {
          case _: NoActiveSession =>
            Redirect(config.loginUrl, Map("continue" -> Seq(config.loginContinueUrl(ern))))
          case x: AuthorisationException =>
            logger.debug(s"[invokeBlock] Authorisation Exception ${x.reason}")
            Redirect(controllers.error.routes.ErrorController.unauthorised())
        }
      }
    }

  private def checkOrganisationEMCSEnrolment[A](ernFromUrl: String,
                                                enrolments: Enrolments,
                                                internalId: String,
                                                credId: String,
                                                sessionId: Option[SessionId]
                                               )(block: UserRequest[A] => Future[Result])
                                               (implicit request: Request[A], hc: HeaderCarrier): Future[Result] =
    enrolments.enrolments.filter(enrolment => enrolment.key == EnrolmentKeys.EMCS_ENROLMENT) match {
      case emcsEnrolments if emcsEnrolments.isEmpty =>
        logger.debug(s"[checkOrganisationEMCSEnrolment] No ${EnrolmentKeys.EMCS_ENROLMENT} enrolment found")
        Future.successful(Redirect(controllers.error.routes.ErrorController.noEnrolment()))
      case emcsEnrolments =>
        emcsEnrolments.find(_.identifiers.exists(ident => ident.key == EnrolmentKeys.ERN && ident.value == ernFromUrl)) match {
          case Some(enrolment) if enrolment.isActivated =>
            checkIfUserErnCanAccessCaM(ernFromUrl, internalId, credId, sessionId, emcsEnrolments.size > 1)(block)
          case Some(_) =>
            logger.debug(s"[checkOrganisationEMCSEnrolment] ${EnrolmentKeys.EMCS_ENROLMENT} enrolment found but not activated")
            Future.successful(Redirect(controllers.error.routes.ErrorController.inactiveEnrolment()))
          case None =>
            logger.warn(s"[checkOrganisationEMCSEnrolment] User attempted to access ern: '$ernFromUrl' which they are not authorised to view")
            Future.successful(Redirect(controllers.error.routes.ErrorController.unauthorised()))
        }
    }

  private def checkIfUserErnCanAccessCaM[A](ernFromUrl: String,
                                            internalId: String,
                                            credId: String,
                                            sessionId: Option[SessionId],
                                            hasMultipleEnrolments: Boolean
                                           )(block: UserRequest[A] => Future[Result])
                                           (implicit request: Request[A], hc: HeaderCarrier): Future[Result] = {
    lazy val success = navBarPartialConnector.getNavBar(ernFromUrl).flatMap { navBar =>
      block(UserRequest(request, ernFromUrl, internalId, credId, sessionId.get.value, hasMultipleEnrolments, navBar))
    }
    val userType = UserType(ernFromUrl)

    if (Seq(Unknown, GreatBritainWarehouse, NorthernIrelandWarehouse).contains(userType)) {
      logger.warn(s"[checkIfUserErnCanAccessCaM] User attempted to access CaM with invalid ern: '$ernFromUrl'")
      Future.successful(Redirect(controllers.error.routes.ErrorController.unauthorised()))
    } else {
      if (userType != NorthernIrelandTemporaryCertifiedConsignor) {
        // all ERNs except XIPC are allowed through
        success
      } else {
        if (config.enableXIPCInCaM) {
          // if XIPC is allowed through
          success
        } else {
          // if XIPC is not allowed through
          logger.warn(s"[checkIfUserErnCanAccessCaM] User attempted to access CaM with Northern Ireland Temporary Certified Consignor ern: '$ernFromUrl' when enableXIPCInCaM is set to false")
          Future.successful(Redirect(controllers.error.routes.ErrorController.unauthorised()))
        }
      }
    }
  }
}
