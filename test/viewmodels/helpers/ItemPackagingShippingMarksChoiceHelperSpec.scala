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

import base.SpecBase
import fixtures.messages.sections.items.ItemPackagingShippingMarksChoiceMessages
import forms.sections.items.ItemPackagingShippingMarksChoiceFormProvider
import pages.sections.items.ItemPackagingQuantityPage
import play.api.i18n.Messages
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import uk.gov.hmrc.govukfrontend.views.Aliases.{Radios, Text}
import uk.gov.hmrc.govukfrontend.views.viewmodels.radios.RadioItem
import viewmodels.govuk.{FieldsetFluency, HintFluency}
import viewmodels.implicits._

class ItemPackagingShippingMarksChoiceHelperSpec extends SpecBase with FieldsetFluency with HintFluency {

  val form = new ItemPackagingShippingMarksChoiceFormProvider()()

  implicit val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest()

  implicit val msgs: Messages = messages(request)

  val messagesForLanguage = ItemPackagingShippingMarksChoiceMessages.English

  ".options" - {

    "should return the correct radio items" - {

      "when the quantity == 0" in {

        implicit val dr = dataRequest(FakeRequest(), emptyUserAnswers.set(ItemPackagingQuantityPage(testIndex1, testPackagingIndex1), "0"))

        ItemPackagingShippingMarksChoiceHelper.options(form, testIndex1, testPackagingIndex1) mustBe Radios(
          fieldset = Some(FieldsetViewModel(LegendViewModel(messagesForLanguage.legend).withCssClass("govuk-fieldset__legend--s"))),
          items = Seq(
            RadioItem(
              content = Text(messagesForLanguage.yesSelectExistingShippingMark),
              value = Some("true"),
              id = Some("value")
            ),
            RadioItem(
              content = Text(messagesForLanguage.no),
              value = Some("false"),
              id = Some("value-no"),
              hint = Some(HintViewModel(messagesForLanguage.noHint(testIndex1.displayIndex.toInt)))
            )
          ),
          name = "value",
          classes = " "
        )
      }

      "when the quantity > 0" in {

        implicit val dr = dataRequest(FakeRequest(), emptyUserAnswers.set(ItemPackagingQuantityPage(testIndex1, testPackagingIndex1), "1"))

        ItemPackagingShippingMarksChoiceHelper.options(form, testIndex1, testPackagingIndex1) mustBe Radios(
          fieldset = Some(FieldsetViewModel(LegendViewModel(messagesForLanguage.legend).withCssClass("govuk-fieldset__legend--s"))),
          items = Seq(
            RadioItem(
              content = Text(messagesForLanguage.yes),
              value = Some("true"),
              id = Some("value")
            ),
            RadioItem(
              content = Text(messagesForLanguage.no),
              value = Some("false"),
              id = Some("value-no"),
              hint = None
            )
          ),
          name = "value",
          classes = " govuk-radios--inline"
        )
      }
    }
  }
}
