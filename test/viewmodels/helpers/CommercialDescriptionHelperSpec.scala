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
import fixtures.messages.sections.items.CommercialDescriptionMessages
import forms.sections.items.CommercialDescriptionFormProvider
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.twirl.api.{Html, HtmlFormat}

class CommercialDescriptionHelperSpec extends SpecBase with GuiceOneAppPerSuite {

  lazy val helper = app.injector.instanceOf[CommercialDescriptionHelper]
  lazy val form = app.injector.instanceOf[CommercialDescriptionFormProvider]
  lazy val p = app.injector.instanceOf[views.html.components.p]
  lazy val details = app.injector.instanceOf[views.html.components.details]

  "CommercialDescriptionHelper" - {

    Seq(CommercialDescriptionMessages.English).foreach { langMessages =>

      implicit lazy val msgs = messages(app, langMessages.lang)

      s"when running for language code of '${langMessages.lang.code}'" - {

        "calling .content(goodsType : String)" - {

          "when the goodsType is beer" - {

            "must output the expected title" in {
              helper.content(goodsType = "Beer") mustBe
                details(langMessages.summary) {
                  HtmlFormat.fill(Seq(
                    p()(Html(langMessages.detailsBeer1)),
                    p()(Html(langMessages.detailsBeer2)),
                    p()(Html(langMessages.detailsBeer3))
                  ))
                }
            }
          }

          "when the goodsType is Wine" - {

            "must output the expected title" in {
              helper.content(goodsType = "Wine") mustBe
                details(langMessages.summary) {
                  HtmlFormat.fill(Seq(
                    p()(Html(langMessages.detailsWine1)),
                    p()(Html(langMessages.detailsWine2)),
                    p()(Html(langMessages.detailsWine3)),
                    p()(Html(langMessages.detailsWine4)),
                    p()(Html(langMessages.detailsWine5)),
                    p()(Html(langMessages.detailsWine6))
                  ))
                }
            }

            "when the goodsType is Spirits" - {

              "must output the expected title" in {
                helper.content(goodsType = "Spirits") mustBe
                  details(langMessages.summary) {
                    HtmlFormat.fill(Seq(
                      p()(Html(langMessages.detailsEthylAlcohol1)),
                      p()(Html(langMessages.detailsEthylAlcohol2)),
                      p()(Html(langMessages.detailsEthylAlcohol3)),
                      p()(Html(langMessages.detailsEthylAlcohol4)),
                      p()(Html(langMessages.detailsEthylAlcohol5))
                    ))
                  }
              }
            }

            "when the goodsType is Tobacco" - {

              "must output the expected title" in {
                helper.content(goodsType = "Tobacco") mustBe
                  details(langMessages.summary) {
                    HtmlFormat.fill(Seq(
                      p()(Html(langMessages.detailsTobacco1)),
                      p()(Html(langMessages.detailsTobacco2)),
                      p()(Html(langMessages.detailsTobacco3)),
                      p()(Html(langMessages.detailsTobacco4))
                    ))
                  }
              }
            }
          }
        }
      }
    }
  }
}
