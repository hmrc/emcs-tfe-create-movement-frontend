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

import models.GoodsTypeModel._
import play.api.i18n.Messages
import play.twirl.api.Html

import javax.inject.Inject

class CommercialDescriptionHelper @Inject()(p: views.html.components.p,
                                            details: views.html.components.details,
                                            bullets: views.html.components.bullets
                                           ) {

  def content(goodsType: GoodsType)(implicit messages: Messages): Html =

    goodsType match {
      case Beer =>
        details("commercialDescription.summary") {
          bullets(Seq(
            p()(Html(messages("commercialDescription.details.beer1"))),
            p()(Html(messages("commercialDescription.details.beer2"))),
            p()(Html(messages("commercialDescription.details.beer3")))
          ))
        }
      case Wine =>
        details("commercialDescription.summary") {
          bullets(Seq(
            p()(Html(messages("commercialDescription.details.wine1"))),
            p()(Html(messages("commercialDescription.details.wine2"))),
            p()(Html(messages("commercialDescription.details.wine3"))),
            p()(Html(messages("commercialDescription.details.wine4"))),
            p()(Html(messages("commercialDescription.details.wine5"))),
            p()(Html(messages("commercialDescription.details.wine6")))
          ))
        }
      case Tobacco =>
        details("commercialDescription.summary") {
          bullets(Seq(
            p()(Html(messages("commercialDescription.details.tobacco1"))),
            p()(Html(messages("commercialDescription.details.tobacco2"))),
            p()(Html(messages("commercialDescription.details.tobacco3"))),
            p()(Html(messages("commercialDescription.details.tobacco4")))
          ))
        }
      case Spirits =>
        details("commercialDescription.summary") {
          bullets(Seq(
            p()(Html(messages("commercialDescription.details.ethylAlcohol1"))),
            p()(Html(messages("commercialDescription.details.ethylAlcohol2"))),
            p()(Html(messages("commercialDescription.details.ethylAlcohol3"))),
            p()(Html(messages("commercialDescription.details.ethylAlcohol4"))),
            p()(Html(messages("commercialDescription.details.ethylAlcohol5")))
          ))
        }
      case _ => Html("")
    }
}
