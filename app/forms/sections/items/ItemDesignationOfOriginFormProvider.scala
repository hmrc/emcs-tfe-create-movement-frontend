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

package forms.sections.items

import forms.XSS_REGEX
import forms.mappings.Mappings
import forms.sections.items.ItemDesignationOfOriginFormProvider._
import models.{ExciseProductCode, GoodsType}
import models.GoodsType.Spirits
import models.sections.items.ItemGeographicalIndicationType.{ProtectedDesignationOfOrigin, ProtectedGeographicalIndication}
import models.sections.items.{ItemDesignationOfOriginModel, ItemGeographicalIndicationType}
import play.api.data.Form
import play.api.data.Forms.{mapping, optional, text => playText}
import uk.gov.voa.play.form.Condition
import uk.gov.voa.play.form.ConditionalMappings.{mandatoryIf, mandatoryIfEqual}

import javax.inject.Inject

class ItemDesignationOfOriginFormProvider @Inject() extends Mappings {

  //scalastyle:off
  def apply(epc: String): Form[ItemDesignationOfOriginModel] =
    Form(
      mapping(
        geographicalIndicationField -> enumerable[ItemGeographicalIndicationType]("itemDesignationOfOrigin.error.geographicalIndication.choice.required"),

        protectedDesignationOfOriginTextField -> mandatoryIfOptionSelectedAndInputNonEmpty(
          geographicalIndicationField,
          protectedDesignationOfOriginTextField,
          ProtectedDesignationOfOrigin.toString,
          playText()
            .verifying(maxLength(geographicalIndicationIdentificationMaxLength, geographicalIndicationIdentificationLengthError))
            .verifying(regexp(XSS_REGEX, geographicalIndicationIdentificationInvalidError))
        ),

        protectedGeographicalIndicationTextField -> mandatoryIfOptionSelectedAndInputNonEmpty(
          geographicalIndicationField,
          protectedGeographicalIndicationTextField,
          ProtectedGeographicalIndication.toString,
          playText()
            .verifying(maxLength(geographicalIndicationIdentificationMaxLength, geographicalIndicationIdentificationLengthError))
            .verifying(regexp(XSS_REGEX, geographicalIndicationIdentificationInvalidError))
        ),

        isSpiritMarketedAndLabelledField -> mandatoryIf(isS200(epc), boolean("itemDesignationOfOrigin.error.spiritMarketedAndLabelled.choice.required"))
      )(applyModel)(unapplyModel)
    )

  private def isS200(epc: String): Condition = _ => epc == "S200"

  private def applyModel(geographicalIndication: ItemGeographicalIndicationType, pdoTextField: Option[String], pgiTextField: Option[String], isSpiritMarketedAndLabelled: Option[Boolean]) = {
    val geographicalIndicationIdentification = geographicalIndication match {
      case ProtectedDesignationOfOrigin => pdoTextField
      case ProtectedGeographicalIndication => pgiTextField
      case _ => None
    }
    ItemDesignationOfOriginModel(geographicalIndication, geographicalIndicationIdentification, isSpiritMarketedAndLabelled)
  }

  private def unapplyModel(model: ItemDesignationOfOriginModel): Option[(ItemGeographicalIndicationType, Option[String], Option[String], Option[Boolean])] = model match {

    case ItemDesignationOfOriginModel(identification@ProtectedDesignationOfOrigin, geographicalIndicationIdentification, isSpiritMarketedAndLabelledField) =>
      Some(identification, geographicalIndicationIdentification, None, isSpiritMarketedAndLabelledField)

    case ItemDesignationOfOriginModel(identification@ProtectedGeographicalIndication, geographicalIndicationIdentification, isSpiritMarketedAndLabelledField) =>
      Some(identification, None, geographicalIndicationIdentification, isSpiritMarketedAndLabelledField)

    case model => Some(model.geographicalIndication, None, None, model.isSpiritMarketedAndLabelled)
  }

}

object ItemDesignationOfOriginFormProvider {

  val geographicalIndicationField = "geographicalIndication"
  val protectedDesignationOfOriginTextField = "protectedDesignationOfOriginText"
  val protectedGeographicalIndicationTextField = "protectedGeographicalIndicationText"
  val isSpiritMarketedAndLabelledField = "isSpiritMarketedAndLabelled"

  val geographicalIndicationIdentificationMaxLength = 50

  val geographicalIndicationIdentificationLengthError = "itemDesignationOfOrigin.error.geographicalIndication.input.length"
  val geographicalIndicationIdentificationInvalidError = "itemDesignationOfOrigin.error.geographicalIndication.input.invalid"

}