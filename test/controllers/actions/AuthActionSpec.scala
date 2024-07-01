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

import base.SpecBase
import config.EnrolmentKeys
import fixtures.BaseFixtures
import mocks.config.MockAppConfig
import models.requests.UserRequest
import org.scalatest.BeforeAndAfterAll
import play.api.Play
import play.api.i18n.MessagesApi
import play.api.mvc._
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.auth.core.AffinityGroup.{Agent, Organisation}
import uk.gov.hmrc.auth.core._
import uk.gov.hmrc.auth.core.authorise.Predicate
import uk.gov.hmrc.auth.core.retrieve.{Credentials, Retrieval, ~}
import uk.gov.hmrc.http.{HeaderCarrier, SessionKeys}

import java.util.UUID
import javax.inject.Inject
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}

class AuthActionSpec extends SpecBase with BaseFixtures with BeforeAndAfterAll with MockAppConfig {

  type AuthRetrieval = ~[~[~[Option[AffinityGroup], Enrolments], Option[String]], Option[Credentials]]

  implicit val fakeRequest: FakeRequest[AnyContentAsEmpty.type] = FakeRequest().withSession(SessionKeys.sessionId -> UUID.randomUUID().toString)

  override def beforeAll(): Unit = {
    Play.start(app)
  }

  override def afterAll(): Unit = {
    Play.stop(app)
  }

  trait Harness {

    val ern: String = testErn

    lazy val bodyParsers = app.injector.instanceOf[BodyParsers.Default]
    implicit lazy val ec: ExecutionContext = app.injector.instanceOf[ExecutionContext]
    implicit lazy val messagesApi: MessagesApi = app.injector.instanceOf[MessagesApi]

    val authConnector: AuthConnector
    lazy val authAction = new AuthActionImpl(authConnector, mockAppConfig, bodyParsers)

    def onPageLoad(): Action[AnyContent] = authAction(ern) { _ => Results.Ok }

    lazy val result = onPageLoad()(fakeRequest)

    def testRequest(isOk: UserRequest[_] => Boolean): Boolean =
      Await.result(
        authAction(ern) { req =>
          if (isOk(req)) Results.Ok else Results.BadRequest
        }(fakeRequest).map(_.header.status == OK),
        Duration.Inf
      )
  }

  def authResponse(affinityGroup: Option[AffinityGroup] = Some(Organisation),
                   enrolments: Enrolments = Enrolments(Set.empty),
                   internalId: Option[String] = Some(testInternalId),
                   credId: Option[Credentials] = Some(Credentials(testCredId, "gg"))): AuthRetrieval =
    new~(new~(new~(affinityGroup, enrolments), internalId), credId)

  "AuthAction" - {

    "calling .apply(ern)" - {

      "User is not logged in" - {

        "redirect to the sign-in URL with the ContinueURL set" in new Harness {

          override val authConnector = new FakeFailingAuthConnector(new BearerTokenExpired)

          MockAppConfig.loginUrl.returns(testUrl)
          MockAppConfig.loginContinueUrl.returns(testUrl)

          status(result) mustBe SEE_OTHER
          redirectLocation(result) mustBe Some(s"$testUrl?continue=$testUrl")
        }
      }

      "An unexpected Authorisation exception is returned from the Auth library" - {

        "redirect to unauthorised" in new Harness {

          override val authConnector = new FakeFailingAuthConnector(new InsufficientConfidenceLevel)

          status(result) mustBe SEE_OTHER
          redirectLocation(result) mustBe Some(controllers.error.routes.ErrorController.unauthorised().url)
        }
      }

      "User is logged in" - {

        "Affinity Group of user does not exist" - {

          "redirect to unauthorised" in new Harness {

            override val authConnector = new FakeSuccessAuthConnector(authResponse(affinityGroup = None))

            status(result) mustBe SEE_OTHER
            redirectLocation(result) mustBe Some(controllers.error.routes.ErrorController.unauthorised().url)
          }
        }

        "Affinity Group of user is not Organisation" - {

          "redirect to unauthorised" in new Harness {

            override val authConnector = new FakeSuccessAuthConnector(authResponse(affinityGroup = Some(Agent)))

            status(result) mustBe SEE_OTHER
            redirectLocation(result) mustBe Some(controllers.error.routes.ErrorController.notAnOrganisation().url)
          }
        }

        "Affinity Group of user is Organisation" - {

          "internalId is not retrieved from Auth" - {

            "redirect to unauthorised" in new Harness {

              override val authConnector = new FakeSuccessAuthConnector(authResponse(internalId = None))

              status(result) mustBe SEE_OTHER
              redirectLocation(result) mustBe Some(controllers.error.routes.ErrorController.unauthorised().url)
            }
          }

          "internalId is retrieved from Auth" - {

            "credential is not retrieved from Auth" - {

              "redirect to unauthorised" in new Harness {

                override val authConnector = new FakeSuccessAuthConnector(authResponse(credId = None))

                status(result) mustBe SEE_OTHER
                redirectLocation(result) mustBe Some(controllers.error.routes.ErrorController.unauthorised().url)
              }
            }

            "credential is retrieved from Auth" - {

              s"Enrolments is missing the ${EnrolmentKeys.EMCS_ENROLMENT}" - {

                "redirect to unauthorised" in new Harness {

                  override val authConnector = new FakeSuccessAuthConnector(authResponse())

                  status(result) mustBe SEE_OTHER
                  redirectLocation(result) mustBe Some(controllers.error.routes.ErrorController.noEnrolment().url)
                }
              }

              s"Enrolments exists for ${EnrolmentKeys.EMCS_ENROLMENT} but is NOT activated" - {

                "redirect to unauthorised" in new Harness {

                  override val authConnector = new FakeSuccessAuthConnector(authResponse(enrolments = Enrolments(Set(
                    Enrolment(
                      key = EnrolmentKeys.EMCS_ENROLMENT,
                      identifiers = Seq(EnrolmentIdentifier(EnrolmentKeys.ERN, testErn)),
                      state = EnrolmentKeys.INACTIVE
                    )
                  ))))

                  status(result) mustBe SEE_OTHER
                  redirectLocation(result) mustBe Some(controllers.error.routes.ErrorController.inactiveEnrolment().url)
                }
              }

              s"Enrolments exists for ${EnrolmentKeys.EMCS_ENROLMENT} AND is activated" - {

                s"the ${EnrolmentKeys.ERN} identifier is missing (should be impossible)" - {

                  "redirect to unauthorised" in new Harness {

                    override val authConnector = new FakeSuccessAuthConnector(authResponse(enrolments = Enrolments(Set(
                      Enrolment(
                        key = EnrolmentKeys.EMCS_ENROLMENT,
                        identifiers = Seq(),
                        state = EnrolmentKeys.ACTIVATED
                      )
                    ))))

                    status(result) mustBe SEE_OTHER
                    redirectLocation(result) mustBe Some(controllers.error.routes.ErrorController.unauthorised().url)
                  }
                }

                s"the ${EnrolmentKeys.ERN} identifier is present" - {

                  "the user is invalid" - {
                    Seq("GB00", "XI00", "ABCD").foreach {
                      ernPrefix =>
                        val badErn = s"${ernPrefix}1234"
                        s"redirect to unauthorised for ern $badErn" in new Harness {
                          override val ern: String = badErn
                          private val singleEnrolment = Enrolments(Set(
                            Enrolment(
                              key = EnrolmentKeys.EMCS_ENROLMENT,
                              identifiers = Seq(EnrolmentIdentifier(EnrolmentKeys.ERN, ern)),
                              state = EnrolmentKeys.ACTIVATED
                            )
                          ))
                          override val authConnector = new FakeSuccessAuthConnector(authResponse(enrolments = singleEnrolment))

                          status(result) mustBe SEE_OTHER
                          redirectLocation(result) mustBe Some(controllers.error.routes.ErrorController.unauthorised().url)
                        }
                    }
                  }

                  "the user is XIPC" - {
                    "enableXIPCInCaM is true" - {
                      "allow the User through, returning a 200 (OK)" in new Harness {
                        override val ern: String = testNITemporaryCertifiedConsignorErn
                        private val singleEnrolment = Enrolments(Set(
                          Enrolment(
                            key = EnrolmentKeys.EMCS_ENROLMENT,
                            identifiers = Seq(EnrolmentIdentifier(EnrolmentKeys.ERN, ern)),
                            state = EnrolmentKeys.ACTIVATED
                          )
                        ))
                        override val authConnector = new FakeSuccessAuthConnector(authResponse(enrolments = singleEnrolment))

                        MockAppConfig.enableXIPCInCaM.returns(true)

                        status(result) mustBe OK
                      }
                    }
                    "enableXIPCInCaM is false" - {
                      "redirect to unauthorised" in new Harness {
                        override val ern: String = testNITemporaryCertifiedConsignorErn
                        private val singleEnrolment = Enrolments(Set(
                          Enrolment(
                            key = EnrolmentKeys.EMCS_ENROLMENT,
                            identifiers = Seq(EnrolmentIdentifier(EnrolmentKeys.ERN, ern)),
                            state = EnrolmentKeys.ACTIVATED
                          )
                        ))
                        override val authConnector = new FakeSuccessAuthConnector(authResponse(enrolments = singleEnrolment))

                        MockAppConfig.enableXIPCInCaM.returns(false)

                        status(result) mustBe SEE_OTHER
                        redirectLocation(result) mustBe Some(controllers.error.routes.ErrorController.unauthorised().url)
                      }
                    }
                  }

                  "the user is not XIPC or invalid" - {
                    val singleEnrolment = Enrolments(Set(
                      Enrolment(
                        key = EnrolmentKeys.EMCS_ENROLMENT,
                        identifiers = Seq(EnrolmentIdentifier(EnrolmentKeys.ERN, testErn)),
                        state = EnrolmentKeys.ACTIVATED
                      )
                    ))

                    "allow the User through, returning a 200 (OK)" in new Harness {
                      override val authConnector = new FakeSuccessAuthConnector(authResponse(enrolments = singleEnrolment))

                      status(result) mustBe OK
                    }

                    "set UserRequest.hasMultipleErns to false" in new Harness {
                      override val authConnector = new FakeSuccessAuthConnector(authResponse(enrolments = singleEnrolment))
                      val hasMultipleErns = testRequest(req => req.hasMultipleErns)

                      hasMultipleErns mustBe false
                    }
                  }

                  s"there are multiple Enrolments with ${EnrolmentKeys.ERN}'s present and ERN matches one" - {
                    val multipleEnrolements = Enrolments(Set(
                      Enrolment(
                        key = EnrolmentKeys.EMCS_ENROLMENT,
                        identifiers = Seq(EnrolmentIdentifier(EnrolmentKeys.ERN, "OTHER_1")),
                        state = EnrolmentKeys.INACTIVE
                      ),
                      Enrolment(
                        key = EnrolmentKeys.EMCS_ENROLMENT,
                        identifiers = Seq(EnrolmentIdentifier(EnrolmentKeys.ERN, testErn)),
                        state = EnrolmentKeys.ACTIVATED
                      ),
                      Enrolment(
                        key = EnrolmentKeys.EMCS_ENROLMENT,
                        identifiers = Seq(EnrolmentIdentifier(EnrolmentKeys.ERN, "OTHER_2")),
                        state = EnrolmentKeys.ACTIVATED
                      )
                    ))

                    "allow the User through, returning a 200 (OK)" in new Harness {
                      override val authConnector = new FakeSuccessAuthConnector(authResponse(enrolments = multipleEnrolements))

                      status(result) mustBe OK
                    }

                    "set UserRequest.hasMultipleErns to true" in new Harness {
                      override val authConnector = new FakeSuccessAuthConnector(authResponse(enrolments = multipleEnrolements))
                      val hasMultipleErns = testRequest(req => req.hasMultipleErns)

                      hasMultipleErns mustBe true
                    }
                  }
                }
              }
            }
          }
        }
      }
    }
  }
}

class FakeSuccessAuthConnector[B] @Inject()(response: B) extends AuthConnector {
  override def authorise[A](predicate: Predicate, retrieval: Retrieval[A])(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[A] =
    Future.successful(response.asInstanceOf[A])
}

class FakeFailingAuthConnector @Inject()(exceptionToReturn: Throwable) extends AuthConnector {
  override def authorise[A](predicate: Predicate, retrieval: Retrieval[A])(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[A] =
    Future.failed(exceptionToReturn)
}
