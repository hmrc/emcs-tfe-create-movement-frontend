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

package views.sections.items

import base.SpecBase
import fixtures.messages.UnitOfMeasureMessages
import fixtures.messages.sections.items.ItemQuantityMessages
import fixtures.{ItemFixtures, MovementSubmissionFailureFixtures}
import forms.sections.items.ItemQuantityFormProvider
import models.GoodsType.{Energy, Tobacco, Wine}
import models.requests.DataRequest
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.data.FormError
import play.api.i18n.Messages
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import views.html.sections.items.ItemQuantityView
import views.{BaseSelectors, ViewBehaviours}

class ItemQuantityViewSpec extends SpecBase with ViewBehaviours with MovementSubmissionFailureFixtures with ItemFixtures {

  object Selectors extends BaseSelectors

  val t200CommodityCode = testCommodityCodeTobacco.copy(exciseProductCode = "T200")
  val t300CommodityCode = testCommodityCodeTobacco.copy(exciseProductCode = "T300")
  val w200CommodityCode = testCommodityCodeWine
  val e500CommodityCode = testCommodityCodeEnergy


  "ItemQuantity view" - {

    Seq(ItemQuantityMessages.English -> UnitOfMeasureMessages.English).foreach { case (messagesForLanguage, unitOfMeasureMessages) =>

      s"when being rendered in lang code of '${messagesForLanguage.lang.code}'" - {
        implicit val msgs: Messages = messages(Seq(messagesForLanguage.lang))
        implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest())

        lazy val view = app.injector.instanceOf[ItemQuantityView]
        val form = app.injector.instanceOf[ItemQuantityFormProvider].apply(testIndex1)

        "for an T200 excise product code" - {
          implicit def doc(isFormError: Boolean = false)(implicit request: DataRequest[_]): Document = Jsoup.parse(view(
            if (isFormError) form.withError(FormError("key", "msg")) else form,
            testOnwardRoute,
            Tobacco,
            t200CommodityCode,
            testIndex1
          ).toString())

          behave like pageWithExpectedElementsAndMessages(Seq(
            Selectors.title -> messagesForLanguage.title(Tobacco.toSingularOutput(), t200CommodityCode),
            Selectors.h1 -> messagesForLanguage.headingT200,
            Selectors.subHeadingCaptionSelector -> messagesForLanguage.itemSection,
            Selectors.p(1) -> messagesForLanguage.paragraphT200,
            Selectors.hint -> messagesForLanguage.hintT200,
            Selectors.label("value") -> messagesForLanguage.labelT200,
            Selectors.inputSuffix -> unitOfMeasureMessages.kilogramsShort,
            Selectors.button -> messagesForLanguage.saveAndContinue,
            Selectors.link(1) -> messagesForLanguage.returnToDraft
          ))(doc())

          behave like pageWithElementsNotPresent(Seq(
            Selectors.notificationBannerTitle,
            Selectors.notificationBannerContent
          ))(doc())
        }

        "for an T300 excise product code" - {
          implicit def doc(isFormError: Boolean = false)(implicit request: DataRequest[_]): Document = Jsoup.parse(view(
            if (isFormError) form.withError(FormError("key", "msg")) else form,
            testOnwardRoute,
            Tobacco,
            t300CommodityCode,
            testIndex1
          ).toString())

          behave like pageWithExpectedElementsAndMessages(Seq(
            Selectors.title -> messagesForLanguage.title(Tobacco.toSingularOutput(), t300CommodityCode),
            Selectors.h1 -> messagesForLanguage.headingT300,
            Selectors.subHeadingCaptionSelector -> messagesForLanguage.itemSection,
            Selectors.p(1) -> messagesForLanguage.paragraphT300,
            Selectors.hint -> messagesForLanguage.hintT300,
            Selectors.label("value") -> messagesForLanguage.labelT300,
            Selectors.inputSuffix -> unitOfMeasureMessages.kilogramsShort,
            Selectors.button -> messagesForLanguage.saveAndContinue,
            Selectors.link(1) -> messagesForLanguage.returnToDraft
          ))(doc())

          behave like pageWithElementsNotPresent(Seq(
            Selectors.notificationBannerTitle,
            Selectors.notificationBannerContent
          ))(doc())
        }

        "for an W200 excise product code" - {
          implicit def doc(isFormError: Boolean = false)(implicit request: DataRequest[_]): Document = Jsoup.parse(view(
            if (isFormError) form.withError(FormError("key", "msg")) else form,
            testOnwardRoute,
            Wine,
            w200CommodityCode,
            testIndex1
          ).toString())

          behave like pageWithExpectedElementsAndMessages(Seq(
            Selectors.title -> messagesForLanguage.title(Wine.toSingularOutput(), w200CommodityCode),
            Selectors.h1 -> messagesForLanguage.heading(Wine.toSingularOutput()),
            Selectors.subHeadingCaptionSelector -> messagesForLanguage.itemSection,
            Selectors.hint -> messagesForLanguage.hintLiquid(unitOfMeasureMessages.litres20Long),
            Selectors.inputSuffix -> unitOfMeasureMessages.litres20Short,
            Selectors.button -> messagesForLanguage.saveAndContinue,
            Selectors.link(1) -> messagesForLanguage.returnToDraft
          ))(doc())

          behave like pageWithElementsNotPresent(Seq(
            Selectors.notificationBannerTitle,
            Selectors.notificationBannerContent
          ))(doc())
        }

        "for an E500 excise product code" - {
          implicit def doc(isFormError: Boolean = false)(implicit request: DataRequest[_]): Document = Jsoup.parse(view(
            if (isFormError) form.withError(FormError("key", "msg")) else form,
            testOnwardRoute,
            Energy,
            e500CommodityCode,
            testIndex1
          ).toString())

          behave like pageWithExpectedElementsAndMessages(Seq(
            Selectors.title -> messagesForLanguage.title(Energy.toSingularOutput(), e500CommodityCode),
            Selectors.h1 -> messagesForLanguage.heading(Energy.toSingularOutput()),
            Selectors.subHeadingCaptionSelector -> messagesForLanguage.itemSection,
            Selectors.inputSuffix -> unitOfMeasureMessages.kilogramsShort,
            Selectors.button -> messagesForLanguage.saveAndContinue,
            Selectors.link(1) -> messagesForLanguage.returnToDraft
          ))(doc())

          behave like pageWithElementsNotPresent(Seq(
            Selectors.notificationBannerTitle,
            Selectors.notificationBannerContent
          ))(doc())
        }
      }
    }
  }
}
