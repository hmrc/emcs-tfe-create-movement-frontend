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

package views

import base.ViewSpecBase
import models.requests.DataRequest
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.safety.Safelist
import play.api.i18n.Messages
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import uk.gov.hmrc.govukfrontend.views.Aliases.{SelectItem, Text}
import uk.gov.hmrc.govukfrontend.views.html.{components => govComponents}
import uk.gov.hmrc.govukfrontend.views.viewmodels.label.Label
class AutocompleteSpec extends ViewSpecBase {

  object Selectors extends BaseSelectors

  implicit lazy val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(), emptyUserAnswers)
  implicit lazy val msgs: Messages = messages(app)

  lazy val autocomplete = app.injector.instanceOf[views.html.components.autocomplete]

  "Autcomplete" - {

    val html = autocomplete(govComponents.Select(
      id = "testId",
      name = "testName",
      label = Label(content = Text("Test Label")),
      errorMessage = None,
      items = Seq(
        SelectItem(value = Some("a"), text = "Test A"),
        SelectItem(value = Some("b"), text = "Test B"),
        SelectItem(value = Some("c"), text = "Test C"),
        SelectItem(value = Some("d"), text = "Test D")
      )),
      "Not found message"
    )

    implicit val doc: Document = Jsoup.parse(html.toString())

    "must contain a label for the dropdown" in {
      doc.select("label").text() mustBe "Test Label"
    }

    "must contain a select with the expected options" in {
      doc.select("select > option:nth-of-type(1)").text() mustBe "Test A"
      doc.select("select > option:nth-of-type(2)").text() mustBe "Test B"
      doc.select("select > option:nth-of-type(3)").text() mustBe "Test C"
      doc.select("select > option:nth-of-type(4)").text() mustBe "Test D"
    }

    s"must render with the correct reference to the autocomplete.min.js" in {
      html.toString() must include(s"<script  src='${controllers.routes.Assets.versioned("javascripts/autocomplete.min.js")}'></script>")
    }

    s"must generate the correct Javascript" in {

      val actualJs = Jsoup.clean(doc.select("#accessibilityAutocomplete").html(), Safelist.relaxed())
      val expectedJs = Jsoup.clean(
        """        accessibleAutocomplete.enhanceSelectElement({
          |         selectElement: document.getElementById("testId"),
          |         name: 'testNameAutocomplete',
          |         defaultValue: '',
          |         tNoResults: () => 'Not found message' ,
          |         onConfirm: () => {
          |          const matchingOption = Array.from(document.querySelectorAll("#testId-select > option")).find(function (c) {
          |           return c.text === document.getElementById("testId").value;
          |          });
          |          const selectedValue = matchingOption ? matchingOption.value : undefined;
          |          document.getElementById("testId-select").value = selectedValue;
          |         }
          |        })""".stripMargin, Safelist.relaxed())

      actualJs mustBe expectedJs
    }
  }
}


