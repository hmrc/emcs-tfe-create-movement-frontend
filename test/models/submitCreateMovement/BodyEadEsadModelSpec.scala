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
import fixtures.messages.sections.items.ItemSmallIndependentProducerMessages
import models.requests.DataRequest
import models.response.MissingMandatoryPage
import models.response.referenceData.{ItemPackaging, WineOperations}
import models.sections.info.movementScenario.MovementScenario.{EuTaxWarehouse, UkTaxWarehouse, UnknownDestination}
import models.sections.items.ItemGeographicalIndicationType.{NoGeographicalIndication, ProtectedDesignationOfOrigin, ProtectedGeographicalIndication}
import models.sections.items.ItemSmallIndependentProducerType.{CertifiedIndependentSmallProducer, NotApplicable, NotProvided, SelfCertifiedIndependentSmallProducerAndNotConsignor}
import models.sections.items.ItemWineProductCategory.ImportedWine
import models.sections.items._
import pages.sections.info.DestinationTypePage
import pages.sections.items._
import play.api.i18n.Messages
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest

class BodyEadEsadModelSpec extends SpecBase with ItemFixtures {

  val fakeRequest: FakeRequest[AnyContentAsEmpty.type] = FakeRequest()

  val messagesForLanguage = ItemSmallIndependentProducerMessages.English
  implicit val msgs: Messages = messages(Seq(messagesForLanguage.lang))

  "apply" - {
    "must throw an error" - {
      "when no items" in {
        implicit val dr: DataRequest[_] = dataRequest(
          FakeRequest(),
          emptyUserAnswers
        )

        val result = intercept[MissingMandatoryPage](BodyEadEsadModel.apply)

        result.message mustBe "ItemsSection should contain at least one item"
      }
    }

    "must return a BodyEadEsadModel" - {
      "when one bulk item" in {
        implicit val dr: DataRequest[_] = dataRequest(
          FakeRequest(),
          emptyUserAnswers
            .set(DestinationTypePage, UkTaxWarehouse.GB)
            .set(ItemExciseProductCodePage(testIndex1), testEpcWine)
            .set(ItemCommodityCodePage(testIndex1), testCnCodeWine)
            .set(ItemQuantityPage(testIndex1), BigDecimal(1))
            .set(ItemNetGrossMassPage(testIndex1), ItemNetGrossMassModel(netMass = BigDecimal(2), grossMass = BigDecimal(3)))
            .set(ItemAlcoholStrengthPage(testIndex1), BigDecimal(1.23))
            .set(ItemDegreesPlatoPage(testIndex1), ItemDegreesPlatoModel(hasDegreesPlato = true, Some(4.56)))
            .set(ItemFiscalMarksPage(testIndex1), "fiscal marks")
            .set(ItemFiscalMarksChoicePage(testIndex1), true)
            .set(ItemDesignationOfOriginPage(testIndex1), ItemDesignationOfOriginModel(ProtectedDesignationOfOrigin, Some("talkin' 'bout my deeeeeesignation"), None))
            .set(ItemSmallIndependentProducerPage(testIndex1), ItemSmallIndependentProducerModel(CertifiedIndependentSmallProducer, None))
            .set(ItemProducerSizePage(testIndex1), BigInt(4))
            .set(ItemDensityPage(testIndex1), BigDecimal(7.89))
            .set(ItemCommercialDescriptionPage(testIndex1), "beans")
            .set(ItemBrandNamePage(testIndex1), ItemBrandNameModel(hasBrandName = true, Some("name")))
            .set(ItemMaturationPeriodAgePage(testIndex1), ItemMaturationPeriodAgeModel(hasMaturationPeriodAge = true, Some("really old")))
            .set(ItemBulkPackagingChoicePage(testIndex1), true)
            .set(ItemBulkPackagingSelectPage(testIndex1), bulkPackagingTypes.head)
            .set(ItemBulkPackagingSealTypePage(testIndex1), ItemPackagingSealTypeModel("seal type", Some("seal info")))
            .set(ItemWineProductCategoryPage(testIndex1), ImportedWine)
            .set(ItemWineGrowingZonePage(testIndex1), ItemWineGrowingZone.CII)
            .set(ItemWineOriginPage(testIndex1), countryModelGB)
            .set(ItemWineMoreInformationPage(testIndex1), Some("more wine info"))
            .set(ItemWineOperationsChoicePage(testIndex1), Seq(WineOperations("op code", "choice desc")))
        )

        BodyEadEsadModel.apply mustBe Seq(BodyEadEsadModel(
          bodyRecordUniqueReference = 1,
          exciseProductCode = testEpcWine,
          cnCode = testCnCodeWine,
          quantity = BigDecimal(1),
          grossMass = BigDecimal(3),
          netMass = BigDecimal(2),
          alcoholicStrengthByVolumeInPercentage = Some(BigDecimal(1.23)),
          degreePlato = Some(4.56),
          fiscalMark = Some("fiscal marks"),
          fiscalMarkUsedFlag = Some(true),
          designationOfOrigin = Some("The product has a Protected Designation of Origin (PDO). talkin' 'bout my deeeeeesignation"),
          independentSmallProducersDeclaration = Some("It is hereby certified that the alcoholic product described has been produced by an independent small producer. The producer is a certified independent small producer"),
          sizeOfProducer = Some(BigInt(4)),
          density = Some(BigDecimal(7.89)),
          commercialDescription = Some("beans"),
          brandNameOfProducts = Some("name"),
          maturationPeriodOrAgeOfProducts = Some("really old"),
          packages = Seq(
            PackageModel(
              kindOfPackages = bulkPackagingTypes.head.packagingType.toString,
              numberOfPackages = None,
              shippingMarks = None,
              commercialSealIdentification = Some("seal type"),
              sealInformation = Some("seal info")
            )
          ),
          wineProduct = Some(
            WineProductModel(
              wineProductCategory = ItemWineProductCategory.ImportedWine,
              wineGrowingZoneCode = Some(ItemWineGrowingZone.CII),
              thirdCountryOfOrigin = Some(countryModelGB.code),
              otherInformation = Some("more wine info"),
              wineOperations = Some(Seq(WineOperations("op code", "choice desc")))
            )
          )
        ))
      }
      "when one non bulk item" in {
        implicit val dr: DataRequest[_] = dataRequest(
          FakeRequest(),
          emptyUserAnswers
            .set(DestinationTypePage, EuTaxWarehouse)
            .set(ItemExciseProductCodePage(testIndex1), testEpcWine)
            .set(ItemCommodityCodePage(testIndex1), testCnCodeWine)
            .set(ItemQuantityPage(testIndex1), BigDecimal(1))
            .set(ItemNetGrossMassPage(testIndex1), ItemNetGrossMassModel(netMass = BigDecimal(2), grossMass = BigDecimal(3)))
            .set(ItemAlcoholStrengthPage(testIndex1), BigDecimal(1.23))
            .set(ItemDegreesPlatoPage(testIndex1), ItemDegreesPlatoModel(hasDegreesPlato = true, Some(4.56)))
            .set(ItemFiscalMarksPage(testIndex1), "fiscal marks")
            .set(ItemFiscalMarksChoicePage(testIndex1), true)
            .set(ItemDesignationOfOriginPage(testIndex1), ItemDesignationOfOriginModel(ProtectedDesignationOfOrigin, Some("talkin' 'bout my deeeeeesignation"), None))
            .set(ItemSmallIndependentProducerPage(testIndex1), ItemSmallIndependentProducerModel(SelfCertifiedIndependentSmallProducerAndNotConsignor, Some(testErn)))
            .set(ItemProducerSizePage(testIndex1), BigInt(4))
            .set(ItemDensityPage(testIndex1), BigDecimal(7.89))
            .set(ItemCommercialDescriptionPage(testIndex1), "beans")
            .set(ItemBrandNamePage(testIndex1), ItemBrandNameModel(hasBrandName = true, Some("name")))
            .set(ItemMaturationPeriodAgePage(testIndex1), ItemMaturationPeriodAgeModel(hasMaturationPeriodAge = true, Some("really old")))
            .set(ItemBulkPackagingChoicePage(testIndex1), false)
            .set(ItemSelectPackagingPage(testIndex1, testPackagingIndex1), ItemPackaging("BA", "Barrel"))
            .set(ItemPackagingQuantityPage(testIndex1, testPackagingIndex1), "3")
            .set(ItemPackagingShippingMarksPage(testIndex1, testPackagingIndex1), "marks")
            .set(ItemPackagingSealTypePage(testIndex1, testPackagingIndex1), ItemPackagingSealTypeModel("seal", None))
            .set(ItemSelectPackagingPage(testIndex1, testPackagingIndex2), ItemPackaging("JR", "Jar"))
            .set(ItemPackagingQuantityPage(testIndex1, testPackagingIndex2), "1")
            .set(ItemPackagingSealTypePage(testIndex1, testPackagingIndex2), ItemPackagingSealTypeModel("seal 2", Some("seal info")))
            .set(ItemWineProductCategoryPage(testIndex1), ImportedWine)
            .set(ItemWineGrowingZonePage(testIndex1), ItemWineGrowingZone.CII)
            .set(ItemWineOriginPage(testIndex1), countryModelGB)
            .set(ItemWineMoreInformationPage(testIndex1), Some("more wine info"))
            .set(ItemWineOperationsChoicePage(testIndex1), Seq(WineOperations("op code", "choice desc"))),
          ern = testNorthernIrelandErn
        )

        BodyEadEsadModel.apply mustBe Seq(BodyEadEsadModel(
          bodyRecordUniqueReference = 1,
          exciseProductCode = testEpcWine,
          cnCode = testCnCodeWine,
          quantity = BigDecimal(1),
          grossMass = BigDecimal(3),
          netMass = BigDecimal(2),
          alcoholicStrengthByVolumeInPercentage = Some(BigDecimal(1.23)),
          degreePlato = Some(4.56),
          fiscalMark = Some("fiscal marks"),
          fiscalMarkUsedFlag = Some(true),
          designationOfOrigin = Some("The product has a Protected Designation of Origin (PDO). talkin' 'bout my deeeeeesignation"),
          independentSmallProducersDeclaration = Some("It is hereby certified that the alcoholic product described has been produced by an independent wine producer. The producer is a self-certified independent small producer and not the consignor. Identification: XIRC123456789"),
          sizeOfProducer = Some(BigInt(4)),
          density = Some(BigDecimal(7.89)),
          commercialDescription = Some("beans"),
          brandNameOfProducts = Some("name"),
          maturationPeriodOrAgeOfProducts = Some("really old"),
          packages = Seq(
            PackageModel(
              kindOfPackages = "BA",
              numberOfPackages = Some(3),
              shippingMarks = Some("marks"),
              commercialSealIdentification = Some("seal"),
              sealInformation = None
            ),
            PackageModel(
              kindOfPackages = "JR",
              numberOfPackages = Some(1),
              shippingMarks = None,
              commercialSealIdentification = Some("seal 2"),
              sealInformation = Some("seal info")
            )
          ),
          wineProduct = Some(
            WineProductModel(
              wineProductCategory = ItemWineProductCategory.ImportedWine,
              wineGrowingZoneCode = Some(ItemWineGrowingZone.CII),
              thirdCountryOfOrigin = Some(countryModelGB.code),
              otherInformation = Some("more wine info"),
              wineOperations = Some(Seq(WineOperations("op code", "choice desc")))
            )
          )
        ))
      }
      "when a mix of bulk and non bulk items" in {
        implicit val dr: DataRequest[_] = dataRequest(
          FakeRequest(),
          emptyUserAnswers
            .set(DestinationTypePage, UkTaxWarehouse.GB)
            // index 1
            .set(ItemExciseProductCodePage(testIndex1), testEpcWine)
            .set(ItemCommodityCodePage(testIndex1), testCnCodeWine)
            .set(ItemQuantityPage(testIndex1), BigDecimal(1))
            .set(ItemNetGrossMassPage(testIndex1), ItemNetGrossMassModel(netMass = BigDecimal(2), grossMass = BigDecimal(3)))
            .set(ItemAlcoholStrengthPage(testIndex1), BigDecimal(1.23))
            .set(ItemDegreesPlatoPage(testIndex1), ItemDegreesPlatoModel(hasDegreesPlato = true, Some(4.56)))
            .set(ItemFiscalMarksPage(testIndex1), "fiscal marks")
            .set(ItemFiscalMarksChoicePage(testIndex1), true)
            .set(ItemDesignationOfOriginPage(testIndex1), ItemDesignationOfOriginModel(ProtectedDesignationOfOrigin, Some("talkin' 'bout my deeeeeesignation"), None))
            .set(ItemSmallIndependentProducerPage(testIndex1), ItemSmallIndependentProducerModel(NotProvided, None))
            .set(ItemProducerSizePage(testIndex1), BigInt(4))
            .set(ItemDensityPage(testIndex1), BigDecimal(7.89))
            .set(ItemCommercialDescriptionPage(testIndex1), "beans")
            .set(ItemBrandNamePage(testIndex1), ItemBrandNameModel(hasBrandName = true, Some("name")))
            .set(ItemMaturationPeriodAgePage(testIndex1), ItemMaturationPeriodAgeModel(hasMaturationPeriodAge = true, Some("really old")))
            .set(ItemBulkPackagingChoicePage(testIndex1), true)
            .set(ItemBulkPackagingSelectPage(testIndex1), bulkPackagingTypes.last)
            .set(ItemBulkPackagingSealTypePage(testIndex1), ItemPackagingSealTypeModel("seal type", Some("seal info")))
            .set(ItemWineProductCategoryPage(testIndex1), ImportedWine)
            .set(ItemWineGrowingZonePage(testIndex1), ItemWineGrowingZone.CII)
            .set(ItemWineOriginPage(testIndex1), countryModelGB)
            .set(ItemWineMoreInformationPage(testIndex1), Some("more wine info"))
            .set(ItemWineOperationsChoicePage(testIndex1), Seq(WineOperations("op code", "choice desc")))
            // index 2
            .set(ItemExciseProductCodePage(testIndex2), testEpcTobacco)
            .set(ItemCommodityCodePage(testIndex2), testCnCodeTobacco)
            .set(ItemQuantityPage(testIndex2), BigDecimal(1))
            .set(ItemNetGrossMassPage(testIndex2), ItemNetGrossMassModel(netMass = BigDecimal(2), grossMass = BigDecimal(3)))
            .set(ItemAlcoholStrengthPage(testIndex2), BigDecimal(1.23))
            .set(ItemDegreesPlatoPage(testIndex2), ItemDegreesPlatoModel(hasDegreesPlato = true, Some(4.56)))
            .set(ItemFiscalMarksPage(testIndex2), "fiscal marks")
            .set(ItemFiscalMarksChoicePage(testIndex2), true)
            .set(ItemDesignationOfOriginPage(testIndex2), ItemDesignationOfOriginModel(ProtectedDesignationOfOrigin, Some("talkin' 'bout my deeeeeesignation"), None))
            .set(ItemProducerSizePage(testIndex2), BigInt(4))
            .set(ItemDensityPage(testIndex2), BigDecimal(7.89))
            .set(ItemCommercialDescriptionPage(testIndex2), "beans")
            .set(ItemBrandNamePage(testIndex2), ItemBrandNameModel(hasBrandName = true, Some("name")))
            .set(ItemMaturationPeriodAgePage(testIndex2), ItemMaturationPeriodAgeModel(hasMaturationPeriodAge = true, Some("really old")))
            .set(ItemBulkPackagingChoicePage(testIndex2), false)
            .set(ItemSelectPackagingPage(testIndex2, testPackagingIndex1), ItemPackaging("BA", "Barrel"))
            .set(ItemPackagingQuantityPage(testIndex2, testPackagingIndex1), "3")
            .set(ItemPackagingShippingMarksPage(testIndex2, testPackagingIndex1), "marks")
            .set(ItemPackagingSealTypePage(testIndex2, testPackagingIndex1), ItemPackagingSealTypeModel("seal", None))
            .set(ItemSelectPackagingPage(testIndex2, testPackagingIndex2), ItemPackaging("JR", "Jar"))
            .set(ItemPackagingQuantityPage(testIndex2, testPackagingIndex2), "1")
            .set(ItemPackagingSealTypePage(testIndex2, testPackagingIndex2), ItemPackagingSealTypeModel("seal 2", Some("seal info")))
            .set(ItemWineProductCategoryPage(testIndex2), ImportedWine)
        )

        BodyEadEsadModel.apply mustBe Seq(
          BodyEadEsadModel(
            bodyRecordUniqueReference = 1,
            exciseProductCode = testEpcWine,
            cnCode = testCnCodeWine,
            quantity = BigDecimal(1),
            grossMass = BigDecimal(3),
            netMass = BigDecimal(2),
            alcoholicStrengthByVolumeInPercentage = Some(BigDecimal(1.23)),
            degreePlato = Some(4.56),
            fiscalMark = Some("fiscal marks"),
            fiscalMarkUsedFlag = Some(true),
            designationOfOrigin = Some("The product has a Protected Designation of Origin (PDO). talkin' 'bout my deeeeeesignation"),
            independentSmallProducersDeclaration = Some("I don't want to provide information about the producer"),
            sizeOfProducer = Some(BigInt(4)),
            density = Some(BigDecimal(7.89)),
            commercialDescription = Some("beans"),
            brandNameOfProducts = Some("name"),
            maturationPeriodOrAgeOfProducts = Some("really old"),
            packages = Seq(
              PackageModel(
                kindOfPackages = bulkPackagingTypes.last.packagingType.toString,
                numberOfPackages = None,
                shippingMarks = None,
                commercialSealIdentification = Some("seal type"),
                sealInformation = Some("seal info")
              )
            ),
            wineProduct = Some(
              WineProductModel(
                wineProductCategory = ItemWineProductCategory.ImportedWine,
                wineGrowingZoneCode = Some(ItemWineGrowingZone.CII),
                thirdCountryOfOrigin = Some(countryModelGB.code),
                otherInformation = Some("more wine info"),
                wineOperations = Some(Seq(WineOperations("op code", "choice desc")))
              )
            )
          ),
          BodyEadEsadModel(
            bodyRecordUniqueReference = 2,
            exciseProductCode = testEpcTobacco,
            cnCode = testCnCodeTobacco,
            quantity = BigDecimal(1),
            grossMass = BigDecimal(3),
            netMass = BigDecimal(2),
            alcoholicStrengthByVolumeInPercentage = Some(BigDecimal(1.23)),
            degreePlato = Some(4.56),
            fiscalMark = Some("fiscal marks"),
            fiscalMarkUsedFlag = Some(true),
            designationOfOrigin = Some("The product has a Protected Designation of Origin (PDO). talkin' 'bout my deeeeeesignation"),
            independentSmallProducersDeclaration = None,
            sizeOfProducer = Some(BigInt(4)),
            density = Some(BigDecimal(7.89)),
            commercialDescription = Some("beans"),
            brandNameOfProducts = Some("name"),
            maturationPeriodOrAgeOfProducts = Some("really old"),
            packages = Seq(
              PackageModel(
                kindOfPackages = "BA",
                numberOfPackages = Some(3),
                shippingMarks = Some("marks"),
                commercialSealIdentification = Some("seal"),
                sealInformation = None
              ),
              PackageModel(
                kindOfPackages = "JR",
                numberOfPackages = Some(1),
                shippingMarks = None,
                commercialSealIdentification = Some("seal 2"),
                sealInformation = Some("seal info")
              )
            ),
            wineProduct = None
          )
        )
      }
    }
  }

  "designationOfOriginAnswer" - {

    s"when the $ItemDesignationOfOriginPage is PDO, with a name/register number and the EPC is S200 (marketed and labelled)" in {

      BodyEadEsadModel.designationOfOriginAnswer(ItemDesignationOfOriginModel(ProtectedDesignationOfOrigin, Some("name/register number"), Some(true))) mustBe
        "The product has a Protected Designation of Origin (PDO). name/register number. It is hereby certified that the product described is marketed and labelled in compliance with Regulation (EU) 2019/787"
    }

    s"when the $ItemDesignationOfOriginPage is PGI, with a name/register number and the EPC is S200 (marketed and labelled)" in {

      BodyEadEsadModel.designationOfOriginAnswer(ItemDesignationOfOriginModel(ProtectedGeographicalIndication, Some("name/register number"), Some(true))) mustBe
        "The product has a Protected Geographical Indication (PGI). name/register number. It is hereby certified that the product described is marketed and labelled in compliance with Regulation (EU) 2019/787"
    }

    s"when the $ItemDesignationOfOriginPage is None and the EPC is S200 (marketed and labelled)" in {

      BodyEadEsadModel.designationOfOriginAnswer(ItemDesignationOfOriginModel(NoGeographicalIndication, None, Some(true))) mustBe
        "I don't want to provide a statement about the designation of origin. It is hereby certified that the product described is marketed and labelled in compliance with Regulation (EU) 2019/787"
    }

    s"when the $ItemDesignationOfOriginPage is PDO, no name/register number and the EPC is S200 (NOT marked and labelled)" in {

      BodyEadEsadModel.designationOfOriginAnswer(ItemDesignationOfOriginModel(ProtectedDesignationOfOrigin, None, Some(false))) mustBe
        "The product has a Protected Designation of Origin (PDO). I don't want to provide a statement about the marketing and labelling of the spirit"
    }

    s"when the $ItemDesignationOfOriginPage is PGI, no name/register number and the EPC is S200 (NOT marked and labelled)" in {

      BodyEadEsadModel.designationOfOriginAnswer(ItemDesignationOfOriginModel(ProtectedGeographicalIndication, None, Some(false))) mustBe
        "The product has a Protected Geographical Indication (PGI). I don't want to provide a statement about the marketing and labelling of the spirit"
    }

    s"when the $ItemDesignationOfOriginPage is None and the EPC is S200 (NOT marketed and labelled)" in {

      BodyEadEsadModel.designationOfOriginAnswer(ItemDesignationOfOriginModel(NoGeographicalIndication, None, Some(false))) mustBe
        "I don't want to provide a statement about the designation of origin. I don't want to provide a statement about the marketing and labelling of the spirit"
    }

    s"when the $ItemDesignationOfOriginPage is PDO, with a name/register number and the EPC is NOT S200" in {

      BodyEadEsadModel.designationOfOriginAnswer(ItemDesignationOfOriginModel(ProtectedDesignationOfOrigin, Some("name/register number"), None)) mustBe
        "The product has a Protected Designation of Origin (PDO). name/register number"
    }

    s"when the $ItemDesignationOfOriginPage is PGI, with a name/register number and the EPC is NOT S200" in {

      BodyEadEsadModel.designationOfOriginAnswer(ItemDesignationOfOriginModel(ProtectedGeographicalIndication, Some("name/register number"), None)) mustBe
        "The product has a Protected Geographical Indication (PGI). name/register number"
    }

    s"when the $ItemDesignationOfOriginPage is PDO, no name/register number and the EPC is NOT S200" in {

      BodyEadEsadModel.designationOfOriginAnswer(ItemDesignationOfOriginModel(ProtectedDesignationOfOrigin, None, None)) mustBe
        "The product has a Protected Designation of Origin (PDO)"
    }

    s"when the $ItemDesignationOfOriginPage is PGI, no name/register number and the EPC is NOT S200" in {

      BodyEadEsadModel.designationOfOriginAnswer(ItemDesignationOfOriginModel(ProtectedGeographicalIndication, None, None)) mustBe
        "The product has a Protected Geographical Indication (PGI)"
    }

    s"when the $ItemDesignationOfOriginPage is None and the EPC is NOT S200" in {

      BodyEadEsadModel.designationOfOriginAnswer(ItemDesignationOfOriginModel(NoGeographicalIndication, None, None)) mustBe
        "I don't want to provide a statement about the designation of origin"
    }
  }

  "designationOfOrigin" - {

    s"when the $ItemDesignationOfOriginPage has an answer, return the generated answer" in {

      BodyEadEsadModel.designationOfOrigin(testIndex1)(dataRequest(FakeRequest(),
        emptyUserAnswers.set(ItemDesignationOfOriginPage(testIndex1), ItemDesignationOfOriginModel(ProtectedDesignationOfOrigin, None, None))
      ), implicitly) mustBe Some("The product has a Protected Designation of Origin (PDO)")
    }

    s"when the $ItemDesignationOfOriginPage has no answer at the specified index, return None" in {

      BodyEadEsadModel.designationOfOrigin(testIndex2)(dataRequest(FakeRequest(),
        emptyUserAnswers.set(ItemDesignationOfOriginPage(testIndex1), ItemDesignationOfOriginModel(ProtectedDesignationOfOrigin, None, None))
      ), implicitly) mustBe None
    }

    s"when the $ItemDesignationOfOriginPage has no answer, return None" in {

      BodyEadEsadModel.designationOfOrigin(testIndex1)(dataRequest(FakeRequest()), implicitly) mustBe None
    }
  }

  "smallIndependentProducer" - {

    s"when the $ItemSmallIndependentProducerPage answer is present, return the generated answer" in {

      BodyEadEsadModel.smallIndependentProducer(testIndex1)(dataRequest(FakeRequest(),
        emptyUserAnswers
          .set(DestinationTypePage, UkTaxWarehouse.GB)
          .set(ItemExciseProductCodePage(testIndex1), testEpcWine)
          .set(ItemCommodityCodePage(testIndex1), testCnCodeWine)
          .set(ItemSmallIndependentProducerPage(testIndex1), ItemSmallIndependentProducerModel(SelfCertifiedIndependentSmallProducerAndNotConsignor, Some(testErn)))
      ), implicitly) mustBe Some("It is hereby certified that the alcoholic product described has been produced by an independent small producer. The producer is a self-certified independent small producer and not the consignor. Identification: XIRC123456789")
    }

    s"when the $ItemSmallIndependentProducerPage has no answer at the specified index, return None" in {

      BodyEadEsadModel.smallIndependentProducer(testIndex2)(dataRequest(FakeRequest(),
        emptyUserAnswers.set(ItemSmallIndependentProducerPage(testIndex1), ItemSmallIndependentProducerModel(SelfCertifiedIndependentSmallProducerAndNotConsignor, Some(testErn)))
      ), implicitly) mustBe None
    }

    s"when the $ItemSmallIndependentProducerPage has no answer, return None" in {

      BodyEadEsadModel.smallIndependentProducer(testIndex1)(dataRequest(FakeRequest()), implicitly) mustBe None
    }
  }

  //Each scenario is independently tested within ItemSmallIndependentProducerHelperSpec so only smoke test
  "smallIndependentProducerAnswer" - {

    "generate the correct answer for GB / Exports" in {

      implicit val dr = dataRequest(FakeRequest(),
        emptyUserAnswers
          .set(DestinationTypePage, UkTaxWarehouse.GB)
          .set(ItemExciseProductCodePage(testIndex1), testEpcWine)
          .set(ItemCommodityCodePage(testIndex1), testCnCodeWine)
          .set(ItemSmallIndependentProducerPage(testIndex1), ItemSmallIndependentProducerModel(SelfCertifiedIndependentSmallProducerAndNotConsignor, Some(testErn)))
      )

      BodyEadEsadModel.smallIndependentProducerAnswer(
        testIndex1, ItemSmallIndependentProducerModel(SelfCertifiedIndependentSmallProducerAndNotConsignor, Some(testErn))
      ) mustBe s"It is hereby certified that the alcoholic product described has been produced by an independent small producer. The producer is a self-certified independent small producer and not the consignor. Identification: $testErn"
    }

    Seq(
      NotApplicable -> "Not applicable",
    ).foreach { optionAndAnswer =>
      s"generate the correct answer when the user selects: ${optionAndAnswer._1}" in {

        implicit val dr = dataRequest(FakeRequest(),
          emptyUserAnswers
            .set(DestinationTypePage, UkTaxWarehouse.GB)
            .set(ItemExciseProductCodePage(testIndex1), testEpcWine)
            .set(ItemCommodityCodePage(testIndex1), testCnCodeWine)
            .set(ItemSmallIndependentProducerPage(testIndex1), ItemSmallIndependentProducerModel(optionAndAnswer._1, None))
        )

        BodyEadEsadModel.smallIndependentProducerAnswer(
          testIndex1, ItemSmallIndependentProducerModel(optionAndAnswer._1, None)
        ) mustBe optionAndAnswer._2
      }
    }


    "generate the correct answer for NI -> EU" in {

      implicit val dr = dataRequest(FakeRequest(),
        emptyUserAnswers
          .set(DestinationTypePage, EuTaxWarehouse)
          .set(ItemExciseProductCodePage(testIndex1), testEpcWine)
          .set(ItemCommodityCodePage(testIndex1), testCnCodeWine)
          .set(ItemSmallIndependentProducerPage(testIndex1), ItemSmallIndependentProducerModel(SelfCertifiedIndependentSmallProducerAndNotConsignor, Some(testErn))),
        ern = testNorthernIrelandErn
      )

      BodyEadEsadModel.smallIndependentProducerAnswer(
        testIndex1, ItemSmallIndependentProducerModel(SelfCertifiedIndependentSmallProducerAndNotConsignor, Some(testErn))
      ) mustBe s"It is hereby certified that the alcoholic product described has been produced by an independent wine producer. The producer is a self-certified independent small producer and not the consignor. Identification: $testErn"
    }

    "throw an exception on an invalid scenario" in {

      implicit val dr = dataRequest(FakeRequest(),
        emptyUserAnswers
          .set(DestinationTypePage, UnknownDestination)
          .set(ItemExciseProductCodePage(testIndex1), testEpcWine)
          .set(ItemCommodityCodePage(testIndex1), testCnCodeWine)
          .set(ItemSmallIndependentProducerPage(testIndex1), ItemSmallIndependentProducerModel(SelfCertifiedIndependentSmallProducerAndNotConsignor, Some(testErn)))
      )

      intercept[IllegalStateException](
        BodyEadEsadModel.smallIndependentProducerAnswer(testIndex1, ItemSmallIndependentProducerModel(SelfCertifiedIndependentSmallProducerAndNotConsignor, Some(testErn)))
      ).getMessage mustBe s"Invalid scenario for small independent producer. Destination type: Some($UnknownDestination) & EPC: Some($testEpcWine) & CN code: Some($testCnCodeWine)"
    }
  }
}
