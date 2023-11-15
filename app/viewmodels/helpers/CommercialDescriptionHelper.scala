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
import play.twirl.api.{Html, HtmlFormat}

import javax.inject.Inject

class CommercialDescriptionHelper @Inject()(p: views.html.components.p,
                                            details: views.html.components.details,
                                            bullets: views.html.components.bullets
                                           ) {

  def content(goodsType: GoodsType)(implicit messages: Messages): Html =

    goodsType match {
      case Beer =>
        details("commercialDescription.summary") {
          HtmlFormat.fill(Seq(
            p()(Html(messages("commercialDescription.details.beer.p"))),
            bullets(Seq(
              p()(Html(messages("commercialDescription.details.beer.b1"))),
              p()(Html(messages("commercialDescription.details.beer.b2")))
            ))
          ))
        }
      case Wine =>
        details("commercialDescription.summary") {
          HtmlFormat.fill(Seq(
            p()(Html(messages("commercialDescription.details.wine.p"))),
            bullets(Seq(
              p()(Html(messages("commercialDescription.details.wine.b1"))),
              p()(Html(messages("commercialDescription.details.wine.b2"))),
              p()(Html(messages("commercialDescription.details.wine.b3"))),
              p()(Html(messages("commercialDescription.details.wine.b4"))),
              p()(Html(messages("commercialDescription.details.wine.b5")))
            ))
          ))
        }
      case Tobacco =>
        details("commercialDescription.summary") {
          HtmlFormat.fill(Seq(
            p()(Html(messages("commercialDescription.details.tobacco.p"))),
            bullets(Seq(
              p()(Html(messages("commercialDescription.details.tobacco.b1"))),
              p()(Html(messages("commercialDescription.details.tobacco.b2"))),
              p()(Html(messages("commercialDescription.details.tobacco.b3")))
            ))
          ))
        }
      case Spirits =>
        details("commercialDescription.summary") {
          HtmlFormat.fill(Seq(
            p()(Html(messages("commercialDescription.details.ethylAlcohol.p"))),
            bullets(Seq(
              p()(Html(messages("commercialDescription.details.ethylAlcohol.b1"))),
              p()(Html(messages("commercialDescription.details.ethylAlcohol.b2"))),
              p()(Html(messages("commercialDescription.details.ethylAlcohol.b3"))),
              p()(Html(messages("commercialDescription.details.ethylAlcohol.b4")))
            ))
          ))
        }
      case _ => Html("")
    }
}
