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

import models._
import pages.Page
import pages.sections.info._
import play.api.mvc.Call

import javax.inject.Inject

class InfoNavigator @Inject() {

  private val normalRoutes: Page => String => Call = {
    case DispatchPlacePage =>
      (ern: String) => controllers.sections.info.routes.DestinationTypeController.onPageLoad(ern)
    case DestinationTypePage =>
      (ern: String) => controllers.sections.info.routes.DeferredMovementController.onPageLoad(ern)
    case DeferredMovementPage =>
      (ern: String) => controllers.sections.info.routes.LocalReferenceNumberController.onPageLoad(ern)
    case LocalReferenceNumberPage =>
      (ern: String) => controllers.sections.info.routes.InvoiceDetailsController.onPageLoad(ern)
    case InvoiceDetailsPage =>
      //TODO update when CAMINFO006 is complete
      (ern: String) => testOnly.controllers.routes.UnderConstructionController.onPageLoad()

    case _ =>
      (_: String) =>
        testOnly.controllers.routes.UnderConstructionController.onPageLoad()
  }

  def nextPage(page: Page, mode: Mode, ern: String): Call = normalRoutes(page)(ern)
}
