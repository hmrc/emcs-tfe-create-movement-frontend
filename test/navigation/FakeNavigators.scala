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

import config.AppConfig
import models.{Mode, UserAnswers}
import org.scalamock.scalatest.MockFactory
import pages._
import play.api.mvc.Call

object FakeNavigators extends MockFactory {

  class FakeNavigator(desiredRoute: Call) extends Navigator(mock[AppConfig]) {
    override def nextPage(page: Page, mode: Mode, userAnswers: UserAnswers): Call =
      desiredRoute
  }

  class FakeJourneyTypeNavigator(desiredRoute: Call) extends JourneyTypeNavigator {
    override def nextPage(page: Page, mode: Mode, userAnswers: UserAnswers): Call =
      desiredRoute
  }

  class FakeConsignorNavigator(desiredRoute: Call) extends ConsignorNavigator {
    override def nextPage(page: Page, mode: Mode, userAnswers: UserAnswers): Call =
      desiredRoute
  }

  class FakeItemsNavigator(desiredRoute: Call) extends ItemsNavigator {
    override def nextPage(page: Page, mode: Mode, userAnswers: UserAnswers): Call =
      desiredRoute
  }

  class FakeInfoNavigator(desiredRoute: Call) extends InformationNavigator {
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

  class FakeDestinationNavigator(desiredRoute: Call) extends DestinationNavigator {
    override def nextPage(page: Page, mode: Mode, userAnswers: UserAnswers): Call =
      desiredRoute
  }

  class FakeGuarantorNavigator(desiredRoute: Call) extends GuarantorNavigator {
    override def nextPage(page: Page, mode: Mode, userAnswers: UserAnswers): Call =
      desiredRoute
  }

  class FakeExportInformationNavigator(desiredRoute: Call) extends ExportInformationNavigator {
    override def nextPage(page: Page, mode: Mode, userAnswers: UserAnswers): Call =
      desiredRoute
  }

  class FakeImportInformationNavigator(desiredRoute: Call) extends ImportInformationNavigator {
    override def nextPage(page: Page, mode: Mode, userAnswers: UserAnswers): Call =
      desiredRoute
  }

  class FakeDocumentsNavigator(desiredRoute: Call) extends DocumentsNavigator {
    override def nextPage(page: Page, mode: Mode, userAnswers: UserAnswers): Call =
      desiredRoute
  }

  class FakeSadNavigator(desiredRoute: Call) extends SadNavigator {
    override def nextPage(page: Page, mode: Mode, userAnswers: UserAnswers): Call =
      desiredRoute
  }
}
