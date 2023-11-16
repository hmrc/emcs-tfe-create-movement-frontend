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

package viewmodels.helpers

import base.SpecBase
import fixtures.BaseFixtures
import fixtures.messages.sections.items.ItemSmallIndependentProducerMessages
import forms.sections.items.ItemSmallIndependentProducerFormProvider
import models.GoodsTypeModel._
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.i18n.{Messages, MessagesApi}
import uk.gov.hmrc.govukfrontend.views.Aliases.RadioItem
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text
import viewmodels.LegendSize
import viewmodels.govuk.all._

class ItemSmallIndependentProducerHelperSpec extends SpecBase with BaseFixtures with GuiceOneAppPerSuite {

  val form = new ItemSmallIndependentProducerFormProvider()()

  ".radio" - {

    Seq(ItemSmallIndependentProducerMessages.English).foreach { langMessages =>

      implicit val msgs: Messages = app.injector.instanceOf[MessagesApi].preferred(Seq(langMessages.lang))

      s"when running for language code of '${langMessages.lang.code}'" - {

        "should return the expected radio options with correct wording" - {

          Seq(Beer, Spirits, Wine, Energy, Tobacco, Intermediate).foreach { goodsType =>

            s"when GoodsType is $goodsType" in {

              val yesWording = goodsType match {
                case Beer => langMessages.yesBeer
                case Spirits => langMessages.yesSpirits
                case _ => langMessages.yesOther
              }

              ItemSmallIndependentProducerHelper.radios(form, goodsType) mustBe
                RadiosViewModel.apply(
                  form("value"),
                  items = Seq(
                    RadioItem(
                      id = Some(form("value").id),
                      value = Some("true"),
                      content = Text(yesWording)
                    ),
                    RadioItem(
                      id = Some(s"${form("value").id}-no"),
                      value = Some("false"),
                      content = Text(langMessages.no)
                    )
                  ),
                  LegendViewModel(Text(langMessages.heading(goodsType.toSingularOutput()))).asPageHeading(LegendSize.Large)
                )
            }
          }
        }
      }
    }
  }
}
