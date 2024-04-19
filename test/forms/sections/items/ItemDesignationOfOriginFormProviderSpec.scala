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

import fixtures.{BaseFixtures, ItemFixtures}
import forms.XSS_REGEX
import forms.behaviours.OptionFieldBehaviours
import models.sections.items.ItemDesignationOfOriginModel
import models.sections.items.ItemGeographicalIndicationType._
import play.api.data.FormError

class ItemDesignationOfOriginFormProviderSpec extends OptionFieldBehaviours with BaseFixtures with ItemFixtures {

  import forms.sections.items.ItemDesignationOfOriginFormProvider._

  val maxLengthForInput = 50

  val formProvider = new ItemDesignationOfOriginFormProvider()

  ".apply" - {

    "for a non-S200 EPC" - {

      val form = formProvider.apply(testEpcWine)

      "should bind successfully" - {

        s"when the $geographicalIndicationField field has a valid option selected" in {

          form.bind(Map(
            geographicalIndicationField -> NoGeographicalIndication.toString
          )).get mustBe ItemDesignationOfOriginModel(NoGeographicalIndication, None, None)
        }

        s"when the $geographicalIndicationField field has PDO selected and it takes the entry from the PDO entry box" in {

          form.bind(Map(
            geographicalIndicationField -> ProtectedDesignationOfOrigin.toString,
            protectedDesignationOfOriginTextField -> "value 1",
            //added this field to ensure that only the selected values entry box is captured in the model
            protectedGeographicalIndicationTextField -> "value 2"
          )).get mustBe ItemDesignationOfOriginModel(ProtectedDesignationOfOrigin, Some("value 1"), None)
        }

        s"when the $geographicalIndicationField field has PGI selected and it takes the entry from the PGI entry box" in {

          form.bind(Map(
            geographicalIndicationField -> ProtectedGeographicalIndication.toString,
            protectedDesignationOfOriginTextField -> "value 1",
            protectedGeographicalIndicationTextField -> "value 2"
          )).get mustBe ItemDesignationOfOriginModel(ProtectedGeographicalIndication, Some("value 2"), None)
        }

        s"when the $geographicalIndicationField field has PDO/PGI selected but the entry box is empty" in {

          form.bind(Map(
            geographicalIndicationField -> ProtectedDesignationOfOrigin.toString,
            protectedDesignationOfOriginTextField -> "",
            protectedGeographicalIndicationTextField -> "value 2"
          )).get mustBe ItemDesignationOfOriginModel(ProtectedDesignationOfOrigin, None, None)
        }

        s"when the $geographicalIndicationField field has PDO/PGI selected but the entry box only contains spaces (trimming down the value)" in {

          form.bind(Map(
            geographicalIndicationField -> ProtectedDesignationOfOrigin.toString,
            protectedDesignationOfOriginTextField -> "       ",
            protectedGeographicalIndicationTextField -> "value 2"
          )).get mustBe ItemDesignationOfOriginModel(ProtectedDesignationOfOrigin, Some(""), None)
        }
      }

      "should fail to bind" - {

        s"when the $geographicalIndicationField has no selection" in {

          form.bind(Map.empty[String, String]).errors mustBe Seq(FormError(geographicalIndicationField, geographicalIndicationChoiceRequiredError))
        }

        s"when the $geographicalIndicationField has no valid selection" in {

          form.bind(Map(
            geographicalIndicationField -> "mr worldwide"
          )).errors mustBe Seq(FormError(geographicalIndicationField, "error.invalid"))
        }

        s"when the $geographicalIndicationField is PDO and the PDO entry field has invalid characters " +
          s"(ignoring any value entered in the PGI entry field)" in {

          form.bind(Map(
            geographicalIndicationField -> ProtectedDesignationOfOrigin.toString,
            protectedDesignationOfOriginTextField -> "; DROP TABLE \"COMPANIES\";--",
            protectedGeographicalIndicationTextField -> "ignore this;;;;;;;;",
          )).errors mustBe Seq(FormError(protectedDesignationOfOriginTextField, geographicalIndicationIdentificationInvalidError, Seq(XSS_REGEX)))
        }

        s"when the $geographicalIndicationField is PDO and the PDO entry field is too long " +
          s"(ignoring any value entered in the PGI entry field)" in {

          form.bind(Map(
            geographicalIndicationField -> ProtectedDesignationOfOrigin.toString,
            protectedDesignationOfOriginTextField -> "a" * 51,
            protectedGeographicalIndicationTextField -> "ignore this;;;;;;;;",
          )).errors mustBe Seq(FormError(protectedDesignationOfOriginTextField, geographicalIndicationIdentificationLengthError, Seq(maxLengthForInput)))
        }

        s"when the $geographicalIndicationField is PGI and the PGI entry field has invalid characters " +
          s"(ignoring any value entered in the PDO entry field)" in {

          form.bind(Map(
            geographicalIndicationField -> ProtectedGeographicalIndication.toString,
            protectedGeographicalIndicationTextField -> "; DROP TABLE \"COMPANIES\";--",
            protectedDesignationOfOriginTextField -> "ignore this;;;;;;;;",
          )).errors mustBe Seq(FormError(protectedGeographicalIndicationTextField, geographicalIndicationIdentificationInvalidError, Seq(XSS_REGEX)))
        }

        s"when the $geographicalIndicationField is PGI and the PGI entry field is too long " +
          s"(ignoring any value entered in the PDO entry field)" in {

          form.bind(Map(
            geographicalIndicationField -> ProtectedGeographicalIndication.toString,
            protectedGeographicalIndicationTextField -> "a" * 51,
            protectedDesignationOfOriginTextField -> "ignore this;;;;;;;;",
          )).errors mustBe Seq(FormError(protectedGeographicalIndicationTextField, geographicalIndicationIdentificationLengthError, Seq(maxLengthForInput)))
        }
      }
    }

    "for an S200 EPC" - {

      val s200Form = formProvider.apply(testEpcSpirit)

      "should bind successfully" - {

        s"when both the $geographicalIndicationField and $isSpiritMarketedAndLabelledField fields have a valid option selected" in {

          s200Form.bind(Map(
            geographicalIndicationField -> NoGeographicalIndication.toString,
            isSpiritMarketedAndLabelledField -> "true"
          )).get mustBe ItemDesignationOfOriginModel(NoGeographicalIndication, None, Some(true))
        }
      }

      "should fail to bind" - {

        s"when the $isSpiritMarketedAndLabelledField has no selection" in {

          s200Form.bind(Map(
            geographicalIndicationField -> NoGeographicalIndication.toString
          )).errors mustBe Seq(FormError(isSpiritMarketedAndLabelledField, isSpiritMarketedAndLabelledRequiredError))
        }

        s"when the $isSpiritMarketedAndLabelledField has no valid selection" in {

          s200Form.bind(Map(
            geographicalIndicationField -> NoGeographicalIndication.toString,
            isSpiritMarketedAndLabelledField -> "inspector goole"
          )).errors mustBe Seq(FormError(isSpiritMarketedAndLabelledField, "error.boolean"))
        }

        s"when there is no selection for either $geographicalIndicationField or $isSpiritMarketedAndLabelledField" in {

          s200Form.bind(Map.empty[String, String]).errors mustBe Seq(
            FormError(geographicalIndicationField, geographicalIndicationChoiceRequiredError),
            FormError(isSpiritMarketedAndLabelledField, isSpiritMarketedAndLabelledRequiredError)
          )
        }

        s"when there is no valid selection for either $geographicalIndicationField or $isSpiritMarketedAndLabelledField" in {

          s200Form.bind(Map(
            geographicalIndicationField -> "mr worldwide",
            isSpiritMarketedAndLabelledField -> "inspector goole"
          )).errors mustBe Seq(
            FormError(geographicalIndicationField, "error.invalid"),
            FormError(isSpiritMarketedAndLabelledField, "error.boolean")
          )
        }
      }
    }
  }

  ".isS200" - {

    "should return true" - {

      "when the EPC is S200" in {

        formProvider.isS200(testEpcSpirit).apply(Map.empty[String, String]) mustBe true
      }
    }

    "should return false" - {

      "when the EPC isn't S200" in {

        formProvider.isS200(testEpcWine).apply(Map.empty[String, String]) mustBe false
      }
    }
  }

  "applyModel" - {

    "should set the geographicalIndicationIdentification to the PDO input when the PDO option has been selected" in {

      formProvider.applyModel(ProtectedDesignationOfOrigin, Some("value 1"), Some("value 2"), Some(true)) mustBe
        ItemDesignationOfOriginModel(ProtectedDesignationOfOrigin, Some("value 1"), Some(true))
    }

    "should set the geographicalIndicationIdentification to the PGI input when the PGI option has been selected" in {

      formProvider.applyModel(ProtectedGeographicalIndication, Some("value 1"), Some("value 2"), Some(false)) mustBe
        ItemDesignationOfOriginModel(ProtectedGeographicalIndication, Some("value 2"), Some(false))
    }

    "should set the geographicalIndicationIdentification to None input when the None option has been selected" in {

      formProvider.applyModel(NoGeographicalIndication, Some("value 1"), Some("value 2"), None) mustBe
        ItemDesignationOfOriginModel(NoGeographicalIndication, None, None)
    }
  }

  "unapplyModel" - {

    "should populate the correct fields for PDO" in {

      formProvider.unapplyModel(ItemDesignationOfOriginModel(ProtectedDesignationOfOrigin, Some("value 1"), Some(true))) mustBe Some((
        ProtectedDesignationOfOrigin, Some("value 1"), None, Some(true)
      ))
    }

    "should populate the correct fields for PGI" in {

      formProvider.unapplyModel(ItemDesignationOfOriginModel(ProtectedGeographicalIndication, Some("value 1"), None)) mustBe Some((
        ProtectedGeographicalIndication, None, Some("value 1"), None
      ))
    }

    "should populate the correct fields for None" in {

      formProvider.unapplyModel(ItemDesignationOfOriginModel(NoGeographicalIndication, None, None)) mustBe Some((
        NoGeographicalIndication, None, None, None
      ))
    }
  }
}
