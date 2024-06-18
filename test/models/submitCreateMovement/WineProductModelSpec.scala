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

package models.submitCreateMovement

import base.SpecBase
import fixtures.ItemFixtures
import models.requests.DataRequest
import models.sections.items.{ItemDesignationOfOriginModel, ItemGeographicalIndicationType, ItemWineGrowingZone, ItemWineProductCategory}
import models.{GoodsType, UserAnswers}
import pages.sections.items._
import play.api.test.FakeRequest

class WineProductModelSpec extends SpecBase with ItemFixtures {

  def userAnswers(
                   epc: String = testEpcWine,
                   cnCode: String = testCnCodeWine,
                   geographicalIndicationChoice: ItemGeographicalIndicationType = ItemGeographicalIndicationType.ProtectedDesignationOfOrigin,
                   importedChoice: ItemWineProductCategory = ItemWineProductCategory.Other
                 ): UserAnswers =
    emptyUserAnswers
      .set(ItemExciseProductCodePage(testIndex1), epc)
      .set(ItemCommodityCodePage(testIndex1), cnCode)
      .set(ItemDesignationOfOriginPage(testIndex1), ItemDesignationOfOriginModel(geographicalIndicationChoice, None, None))
      .set(ItemWineProductCategoryPage(testIndex1), importedChoice)
      .set(ItemWineGrowingZonePage(testIndex1), ItemWineGrowingZone.CI)
      .set(ItemWineOriginPage(testIndex1), countryModelGB)
      .set(ItemWineMoreInformationPage(testIndex1), Some("more info"))
      .set(ItemWineOperationsChoicePage(testIndex1), testWineOperations)

  "apply" - {
    "must return None" - {
      "if not Wine" in {
        GoodsType.values.filterNot(_ == GoodsType.Wine).map(gt => s"${gt.code}000").foreach {
          epc =>
            implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), userAnswers(epc = epc, cnCode = testCnCodeBeer))

            WineProductModel.applyAtIdx(testIndex1) mustBe None
        }
      }
    }

    "must return Some(WineProductModel)" - {
      "if commodity code is wine" in {
        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), userAnswers())

        val result = WineProductModel.applyAtIdx(testIndex1)

        // bodge the wine operations to be sorted so the comparison works
        result.map(_.copy(wineOperations = result.flatMap(_.wineOperations.map(_.sortBy(_.code))))) mustBe Some(WineProductModel(
          wineProductCategory = ItemWineProductCategory.Other,
          wineGrowingZoneCode = Some(ItemWineGrowingZone.CI),
          thirdCountryOfOrigin = Some(countryModelGB.code),
          otherInformation = Some("more info"),
          wineOperations = Some(testWineOperations.sortBy(_.code))
        ))
      }
    }
  }
}
