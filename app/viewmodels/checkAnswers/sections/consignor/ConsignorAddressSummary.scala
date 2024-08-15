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

package viewmodels.checkAnswers.sections.consignor

import models.CheckMode
import models.requests.DataRequest
import pages.sections.consignor.ConsignorAddressPage
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import viewmodels.govuk.summarylist._
import viewmodels.implicits._

object ConsignorAddressSummary {

  def row()(implicit request: DataRequest[_], messages: Messages): Option[SummaryListRow] = {
    ConsignorAddressPage.value.map {
      address =>
        SummaryListRowViewModel(
          key = "checkYourAnswersConsignor.address",
          value = ValueViewModel(

            address.copy(businessName = ConsignorAddressPage.businessName).toCheckYourAnswersFormat
          ),
          actions = Seq(
            ActionItemViewModel(
              content = "site.change",
              href = controllers.sections.consignor.routes.ConsignorAddressController.onPageLoad(request.ern, request.draftId, CheckMode).url,
              id = "changeConsignorAddress"
            ).withVisuallyHiddenText(messages("checkYourAnswersConsignor.address.change.hidden"))
          )
        )
    }
  }

}
