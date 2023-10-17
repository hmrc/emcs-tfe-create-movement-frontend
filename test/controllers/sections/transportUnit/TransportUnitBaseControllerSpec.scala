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

package controllers.sections.transportUnit

import base.SpecBase
import mocks.services.MockUserAnswersService
import models.TransportUnitType.Tractor
import models.{NormalMode, TransportUnitType}
import navigation.{BaseNavigator, TransportUnitNavigator}
import pages.sections.transportUnit.TransportUnitTypePage
import play.api.http.Status
import play.api.mvc.Results.Ok
import play.api.mvc.{MessagesControllerComponents, Result}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.UserAnswersService

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class TransportUnitBaseControllerSpec extends SpecBase with MockUserAnswersService {
  val mockNavigator: TransportUnitNavigator = mock[TransportUnitNavigator]

  val controller: TransportUnitBaseController = new TransportUnitBaseController {
    override val userAnswersService: UserAnswersService = mockUserAnswersService
    override val navigator: BaseNavigator = mockNavigator

    override protected def controllerComponents: MessagesControllerComponents = stubMessagesControllerComponents()
  }

  val fakeBlock: TransportUnitType => Future[Result] = _ => Future(Ok("success :)"))

  ".withTransportUnitTypeAnswer" - {
    "run the block when the 'TransportUnitTypePage' answer is present" in {
      val result = controller.withTransportUnitTypeAnswer(fakeBlock)(dataRequest(FakeRequest(), emptyUserAnswers.set(TransportUnitTypePage, Tractor)))
      status(result) mustBe Status.OK
      contentAsString(result) mustBe "success :)"
    }

    "redirect back to TU01 when the 'TransportUnitTypePage' answer is not present" in {
      val result = controller.withTransportUnitTypeAnswer(fakeBlock)(dataRequest(FakeRequest()))
      status(result) mustBe Status.SEE_OTHER
      redirectLocation(result).get mustBe controllers.sections.transportUnit.routes.TransportUnitTypeController.onPageLoad(testErn, testLrn, NormalMode).url
    }
  }
}
