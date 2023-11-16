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

package base

import config.AppConfig
import connectors.emcsTfe.{FakeUserAnswersConnector, UserAnswersConnector}
import connectors.referenceData._
import connectors.userAllowList.{FakeUserAllowListConnector, UserAllowListConnector}
import controllers.actions._
import controllers.actions.predraft.{FakePreDraftRetrievalAction, PreDraftDataRetrievalAction}
import fixtures.BaseFixtures
import models.requests.{DataRequest, UserRequest}
import models.{TraderKnownFacts, UserAnswers}
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatest.{OptionValues, TryValues}
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.Play.materializer
import play.api.i18n.{Messages, MessagesApi}
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.mvc.{MessagesControllerComponents, Request}
import play.api.test.Helpers.stubPlayBodyParsers
import repositories.SessionRepository
import repository.{FakePlayMongoComponent, FakeSessionRepository}
import uk.gov.hmrc.mongo.MongoComponent

trait SpecBase extends AnyFreeSpec with Matchers with TryValues with OptionValues with ScalaFutures with BaseFixtures with GuiceOneAppPerSuite {

  lazy val appConfig: AppConfig = app.injector.instanceOf[AppConfig]
  lazy val messagesApi: MessagesApi = app.injector.instanceOf[MessagesApi]
  lazy val messagesControllerComponents: MessagesControllerComponents = app.injector.instanceOf[MessagesControllerComponents]
  lazy val dataRequiredAction: DataRequiredAction = app.injector.instanceOf[DataRequiredAction]

  def messages(request: Request[_]): Messages = app.injector.instanceOf[MessagesApi].preferred(request)

  val fakeAuthAction = new FakeAuthAction(stubPlayBodyParsers)
  val fakeUserAllowListAction = new FakeUserAllowListAction()

  def userRequest[A](request: Request[A], ern: String = testErn): UserRequest[A] =
    UserRequest(request, ern, testInternalId, testCredId, testSessionId, hasMultipleErns = false)

  def dataRequest[A](request: Request[A], answers: UserAnswers = emptyUserAnswers, ern: String = testErn): DataRequest[A] =
    DataRequest(userRequest(request, ern), testDraftId, answers, testMinTraderKnownFacts)

  protected def applicationBuilder(userAnswers: Option[UserAnswers] = None,
                                   optTraderKnownFacts: Option[TraderKnownFacts] = Some(testMinTraderKnownFacts)): GuiceApplicationBuilder =
    new GuiceApplicationBuilder()
      .configure(
        "play.filters.csp.nonce.enabled" -> false
      )
      .overrides(
        bind[AuthAction].to[FakeAuthAction],
        bind[UserAllowListAction].to[FakeUserAllowListAction],
        bind[DataRetrievalAction].toInstance(new FakeDataRetrievalAction(userAnswers, optTraderKnownFacts)),
        bind[PreDraftDataRetrievalAction].toInstance(new FakePreDraftRetrievalAction(userAnswers, optTraderKnownFacts)),
        bind[UserAnswersConnector].toInstance(new FakeUserAnswersConnector()),
        bind[GetCnCodeInformationConnector].toInstance(new FakeGetCnCodeInformationConnector()),
        bind[GetDocumentTypesConnector].toInstance(new FakeGetDocumentTypesConnector()),
        bind[GetTraderKnownFactsConnector].toInstance(new FakeGetTraderKnownFactsConnector()),
        bind[GetExciseProductCodesConnector].toInstance(new FakeGetExciseProductCodesConnector()),
        bind[GetBulkPackagingTypesConnector].toInstance(new FakeGetBulkPackagingTypesConnector()),
        bind[GetMemberStatesConnector].toInstance(new FakeGetMemberStatesConnector()),
        bind[UserAllowListConnector].toInstance(new FakeUserAllowListConnector()),
        bind[SessionRepository].toInstance(new FakeSessionRepository()),
        bind[GetCommodityCodesConnector].toInstance(new FakeGetCommodityCodesConnector()),
        bind[MongoComponent].to[FakePlayMongoComponent].eagerly()
      )
}
