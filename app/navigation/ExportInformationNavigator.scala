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

import controllers.routes
import models.{Mode, UserAnswers}
import pages.Page
import pages.sections.exportInformation.{ExportCustomsOfficePage, ExportInformationCheckAnswersPage}
import play.api.mvc.Call

import javax.inject.Inject

class ExportInformationNavigator @Inject() extends BaseNavigator {

  private val normalRoutes: Page => UserAnswers => Call = {

    case ExportCustomsOfficePage => (userAnswers: UserAnswers) =>
      controllers.sections.exportInformation.routes.ExportInformationCheckAnswersController.onPageLoad(userAnswers.ern, userAnswers.lrn)

    //TODO: Route to next section when available
    case ExportInformationCheckAnswersPage => (_: UserAnswers) =>
      testOnly.controllers.routes.UnderConstructionController.onPageLoad()

    case _ => (userAnswers: UserAnswers) =>
      routes.IndexController.onPageLoad(userAnswers.ern)
  }

  override def nextPage(page: Page, mode: Mode, userAnswers: UserAnswers): Call =
    normalRoutes(page)(userAnswers)
}
