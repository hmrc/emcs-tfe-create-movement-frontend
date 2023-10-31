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

package controllers.sections.info

import base.SpecBase
import mocks.services.{MockPreDraftService, MockUserAnswersService}
import mocks.viewmodels.MockInformationCheckAnswersHelper
import models.UserAnswers
import models.sections.info.movementScenario.MovementScenario.GbTaxWarehouse
import navigation.FakeNavigators.FakeInfoNavigator
import navigation.InformationNavigator
import pages.sections.info.{DeferredMovementPage, DestinationTypePage}
import play.api.inject.bind
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.{PreDraftService, UserAnswersService}
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryList
import viewmodels.checkAnswers.sections.info.InformationCheckAnswersHelper
import viewmodels.govuk.SummaryListFluency
import views.html.sections.info.InformationCheckAnswersView

import scala.concurrent.Future

class InformationCheckAnswersControllerSpec extends SpecBase
  with SummaryListFluency
  with MockInformationCheckAnswersHelper
  with MockUserAnswersService
  with MockPreDraftService {

  class Fixtures(userAnswers: Option[UserAnswers]) {

    lazy val checkYourAnswersRoute = controllers.sections.info.routes.InformationCheckAnswersController.onPageLoad(testErn).url
    lazy val checkYourAnswersSubmitRoute = controllers.sections.info.routes.InformationCheckAnswersController.onSubmit(testErn).url

    lazy val view = application.injector.instanceOf[InformationCheckAnswersView]

    val list: SummaryList = SummaryListViewModel(Seq.empty).withCssClass("govuk-!-margin-bottom-9")

    val application = applicationBuilder(userAnswers)
      .overrides(
        bind[InformationNavigator].toInstance(new FakeInfoNavigator(testOnwardRoute)),
        bind[InformationCheckAnswersHelper].toInstance(MockInformationCheckAnswersHelper),
        bind[UserAnswersService].toInstance(mockUserAnswersService),
        bind[PreDraftService].toInstance(mockPreDraftService)
      )
      .build()
  }

  "InformationCheckAnswers Controller" - {

    val userAnswers = emptyUserAnswers.set(DestinationTypePage, GbTaxWarehouse).set(DeferredMovementPage, true)
    "must return OK and the correct view for a GET" in new Fixtures(Some(userAnswers)) {
      running(application) {

        MockCheckAnswersJourneyTypeHelper.summaryList(deferredMovement = true).returns(list)

        implicit val request = dataRequest(FakeRequest(GET, checkYourAnswersRoute))

        val result = route(application, request).value

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(
          list = list,
          submitAction = controllers.sections.info.routes.InformationCheckAnswersController.onSubmit(testErn)
        )(dataRequest(request), messages(application)).toString
      }
    }

    "must redirect to the next page when submitting the page" in new Fixtures(Some(userAnswers)) {
        running(application) {

          MockUserAnswersService.set(userAnswers).returns(Future.successful(userAnswers))
          MockPreDraftService.clear(testErn, testSessionId).returns(Future.successful(true))

          val request = FakeRequest(POST, checkYourAnswersSubmitRoute)

          val result = route(application, request).value

          status(result) mustEqual SEE_OTHER
          redirectLocation(result).value mustEqual testOnwardRoute.url
        }
      }

  }
}
