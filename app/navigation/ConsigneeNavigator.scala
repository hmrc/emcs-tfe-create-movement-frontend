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
import models.{Mode, NormalMode, UserAnswers}
import pages.Page
import pages.sections.consignee._
import play.api.mvc.Call

import javax.inject.Inject

class ConsigneeNavigator @Inject() extends BaseNavigator {

  private val normalRoutes: Page => UserAnswers => Call = {

    // if the [destinationType] was Exempted Organisation
    case ConsigneeExemptOrganisationPage => (userAnswers: UserAnswers) =>
      controllers.sections.consignee.routes.ConsigneeBusinessNameController.onPageLoad(userAnswers.ern, userAnswers.lrn, NormalMode)

    // else
    case ConsigneeExportPage => (userAnswers: UserAnswers) =>
      userAnswers.get(ConsigneeExportPage) match {
        case Some(true) =>
          controllers.sections.consignee.routes.ConsigneeExportVatController.onPageLoad(userAnswers.ern, userAnswers.lrn, NormalMode)
        case Some(false) =>
          controllers.sections.consignee.routes.ConsigneeExciseController.onPageLoad(userAnswers.ern, userAnswers.lrn, NormalMode)
        case _ =>
          controllers.routes.JourneyRecoveryController.onPageLoad()
      }

    case ConsigneeExportVatPage => (userAnswers: UserAnswers) =>
      controllers.sections.consignee.routes.ConsigneeBusinessNameController.onPageLoad(userAnswers.ern, userAnswers.lrn, NormalMode)

    case ConsigneeExcisePage => (userAnswers: UserAnswers) =>
      controllers.sections.consignee.routes.ConsigneeBusinessNameController.onPageLoad(userAnswers.ern, userAnswers.lrn, NormalMode)

    case ConsigneeBusinessNamePage => (userAnswers: UserAnswers) =>
      controllers.sections.consignee.routes.ConsigneeAddressController.onPageLoad(userAnswers.ern, userAnswers.lrn, NormalMode)

    case ConsigneeAddressPage =>
      //TODO update to next page when finished
      (_: UserAnswers) => testOnly.controllers.routes.UnderConstructionController.onPageLoad()

    case _ =>
      (userAnswers: UserAnswers) => routes.IndexController.onPageLoad(userAnswers.ern)
  }

  override def nextPage(page: Page, mode: Mode, userAnswers: UserAnswers): Call = mode match {
    case NormalMode =>
      normalRoutes(page)(userAnswers)
    //TODO update when other modes are added
    case _ =>
      normalRoutes(page)(userAnswers)
  }
}
