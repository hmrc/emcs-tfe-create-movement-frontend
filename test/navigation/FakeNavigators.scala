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

package navigation

import models.{Mode, UserAnswers}
import pages._
import play.api.mvc.Call

object FakeNavigators {

  class FakeNavigator(desiredRoute: Call) extends Navigator {
    override def nextPage(page: Page, mode: Mode, userAnswers: UserAnswers): Call =
      desiredRoute
  }

  class FakeJourneyTypeNavigator(desiredRoute: Call) extends JourneyTypeNavigator {
    override def nextPage(page: Page, mode: Mode, userAnswers: UserAnswers): Call =
      desiredRoute
  }

  class FakeConsigneeNavigator(desiredRoute: Call) extends ConsigneeNavigator {
    override def nextPage(page: Page, mode: Mode, userAnswers: UserAnswers): Call =
      desiredRoute
  }

  class FakeTransportArrangerNavigator(desiredRoute: Call) extends TransportArrangerNavigator {
    override def nextPage(page: Page, mode: Mode, userAnswers: UserAnswers): Call =
      desiredRoute
  }

  class FakeFirstTransporterNavigator(desiredRoute: Call) extends FirstTransporterNavigator {
    override def nextPage(page: Page, mode: Mode, userAnswers: UserAnswers): Call =
      desiredRoute
  }

  class FakeTransportUnitNavigator(desiredRoute: Call) extends TransportUnitNavigator {
    override def nextPage(page: Page, mode: Mode, userAnswers: UserAnswers): Call =
      desiredRoute
  }

  class FakeDispatchNavigator(desiredRoute: Call) extends DispatchNavigator {
    override def nextPage(page: Page, mode: Mode, userAnswers: UserAnswers): Call =
      desiredRoute
  }
}
