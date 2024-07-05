/*
 * Copyright 2024 HM Revenue & Customs
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

package viewmodels.helpers

import models.requests.DataRequest
import play.api.i18n.Messages

class ItemExciseProductCodeHelper {
  def insetText()(implicit request: DataRequest[_], messages: Messages): Option[String] = {
    if (request.isUkToUkAndNoGuarantor) {
      Some(messages("itemExciseProductCode.inset.GBNoGuarantor"))
    } else if (request.isUnknownDestination) {
      Some(messages("itemExciseProductCode.inset.unknownDestination"))
    } else if (request.isUkToEuAndNoGuarantor) {
      Some(messages("itemExciseProductCode.inset.EuNoGuarantor"))
    } else {
      None
    }
  }
}
