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

import com.google.inject.Inject
import models.Index
import models.requests.DataRequest
import pages.sections.items.{ItemsSection, ItemsSectionItem}
import play.api.i18n.Messages
import play.twirl.api.{Html, HtmlFormat}
import views.html.components.inset

class ShippingMarksPackagingHelper @Inject()(inset: inset) {

  def packagingRemoveFromListContent(itemIdx: Index, packageIdx: Index)(implicit request: DataRequest[_], messages: Messages): Option[HtmlFormat.Appendable] =
    Option.when(ItemsSection.shippingMarkForItemIsUsedOnOtherItems(itemIdx, packageIdx)) {
      inset(Html(messages("itemPackagingRemovePackage.inset", itemIdx.displayIndex)))
    }

  def itemRemoveFromListContent(itemIdx: Index)(implicit request: DataRequest[_], messages: Messages): Option[HtmlFormat.Appendable] =
    Option.when(ItemsSectionItem(itemIdx).packagingIndexes.exists(ItemsSection.shippingMarkForItemIsUsedOnOtherItems(itemIdx, _))) {
      inset(Html(messages("itemRemoveItem.inset", itemIdx.displayIndex)))
    }

}
