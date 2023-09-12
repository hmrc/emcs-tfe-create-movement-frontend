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

package pages

import models.UserAddress
import play.api.libs.json.JsPath

<<<<<<<< HEAD:app/pages/ConsignorAddressPage.scala
case object ConsignorAddressPage extends QuestionPage[UserAddress] {

  override def path: JsPath = JsPath \ toString

  override def toString: String = "consignorAddress"
========
object FakeNavigators {

  class FakeNavigator(desiredRoute: Call) extends Navigator {
    override def nextPage(page: Page, mode: Mode, userAnswers: UserAnswers): Call =
      desiredRoute
  }

  class FakeJourneyTypeNavigator(desiredRoute: Call) extends JourneyTypeNavigator {
    override def nextPage(page: Page, mode: Mode, userAnswers: UserAnswers): Call =
      desiredRoute
  }


>>>>>>>> 6de1d49 (ETFE-1930 CAM-JT01:how-movement-transported):test/navigation/FakeNavigators.scala
}
