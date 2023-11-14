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

package mocks.viewmodels

import models.GoodsTypeModel.GoodsType
import models.requests.DataRequest
import models.{Index, UserAnswers}
import org.scalamock.handlers.CallHandler5
import org.scalamock.scalatest.MockFactory
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryList
import viewmodels.helpers.ConfirmCommodityCodeHelper

trait MockConfirmCommodityCodeHelper extends MockFactory {

  lazy val mockConfirmCommodityCodeHelper: ConfirmCommodityCodeHelper = mock[ConfirmCommodityCodeHelper]

  object MockConfirmCommodityCodeHelper {

    def summaryList(idx: Index, goodsType: GoodsType, userAnswers: UserAnswers): CallHandler5[Index, GoodsType, UserAnswers, DataRequest[_], Messages, SummaryList] =
      (mockConfirmCommodityCodeHelper.summaryList(_: Index, _: GoodsType, _: UserAnswers)(_: DataRequest[_], _: Messages)).expects(*, *, *, *, *)
  }

}
