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

package models.sections.info.movementScenario

import models.requests.UserRequest
import models.response.InvalidUserTypeException
import models.{Enumerable, WithName}
import utils.Logging

sealed trait MovementScenario {
  implicit val request: UserRequest[_]
  val originType: OriginType
  val destinationType: DestinationType
  val movementType: MovementType
}

object MovementScenario extends Enumerable.Implicits with Logging {

  private def getOriginType()(implicit request: UserRequest[_]): OriginType = (request.isWarehouseKeeper, request.isRegisteredConsignor) match {
    case (true, _) => OriginType.TaxWarehouse
    case (_, true) => OriginType.Imports
    case _ =>
      logger.error(s"[getOriginType] invalid UserType for CAM journey: ${request.userTypeFromErn}")
      throw InvalidUserTypeException(s"[MovementScenario][getOriginType] invalid UserType for CAM journey: ${request.userTypeFromErn}")
  }

  /**
   * emcs: direct_export / import_for_direct_export
   */
  case class ExportWithCustomsDeclarationLodgedInTheUk()(implicit val request: UserRequest[_])
    extends WithName("exportWithCustomsDeclarationLodgedInTheUk") with MovementScenario {

    val originType: OriginType = getOriginType()

    val destinationType: DestinationType = DestinationType.Export

    val movementType: MovementType = (request.isWarehouseKeeper, request.isRegisteredConsignor) match {
      case (true, _) => MovementType.DirectExport
      case (_, true) => MovementType.ImportDirectExport
      case _ =>
        logger.error(s"[movementType] invalid UserType for CAM journey: ${request.userTypeFromErn}")
        throw InvalidUserTypeException(s"[MovementScenario][movementType] invalid UserType for CAM journey: ${request.userTypeFromErn}")
    }
  }

  /**
   * emcs: tax_warehouse_uk_to_uk / import_for_taxwarehouse_uk
   */
  case class GbTaxWarehouse()(implicit val request: UserRequest[_])
    extends WithName("gbTaxWarehouse") with MovementScenario {

    val originType: OriginType = getOriginType()

    val destinationType: DestinationType = DestinationType.TaxWarehouse

    val movementType: MovementType = (request.isWarehouseKeeper, request.isRegisteredConsignor) match {
      case (true, _) => MovementType.UkToUk
      case (_, true) => MovementType.ImportUk
      case _ =>
        logger.error(s"[movementType] invalid UserType for CAM journey: ${request.userTypeFromErn}")
        throw InvalidUserTypeException(s"[MovementScenario][movementType] invalid UserType for CAM journey: ${request.userTypeFromErn}")
    }
  }

  /**
   * emcs: direct_delivery / import_for_direct_delivery
   */
  case class DirectDelivery()(implicit val request: UserRequest[_])
    extends WithName("directDelivery") with MovementScenario {

    val originType: OriginType = getOriginType()

    val destinationType: DestinationType = DestinationType.DirectDelivery

    val movementType: MovementType = (request.isWarehouseKeeper, request.isRegisteredConsignor) match {
      case (true, _) => MovementType.UkToEu
      case (_, true) => MovementType.ImportEu
      case _ =>
        logger.error(s"[movementType] invalid UserType for CAM journey: ${request.userTypeFromErn}")
        throw InvalidUserTypeException(s"[MovementScenario][movementType] invalid UserType for CAM journey: ${request.userTypeFromErn}")
    }
  }

  /**
   * emcs: tax_warehouse_uk_to_eu / import_for_taxwarehouse_eu
   */
  case class EuTaxWarehouse()(implicit val request: UserRequest[_])
    extends WithName("euTaxWarehouse") with MovementScenario {

    val originType: OriginType = getOriginType()

    val destinationType: DestinationType = DestinationType.TaxWarehouse

    val movementType: MovementType = (request.isWarehouseKeeper, request.isRegisteredConsignor) match {
      case (true, _) => MovementType.UkToEu
      case (_, true) => MovementType.ImportEu
      case _ =>
        logger.error(s"[movementType] invalid UserType for CAM journey: ${request.userTypeFromErn}")
        throw InvalidUserTypeException(s"[MovementScenario][movementType] invalid UserType for CAM journey: ${request.userTypeFromErn}")
    }
  }

  /**
   * emcs: exempted_organisation / import_for_exempted_organisation
   */
  case class ExemptedOrganisation()(implicit val request: UserRequest[_])
    extends WithName("exemptedOrganisation") with MovementScenario {

    val originType: OriginType = getOriginType()

    val destinationType: DestinationType = DestinationType.ExemptedOrganisation

    val movementType: MovementType = (request.isWarehouseKeeper, request.isRegisteredConsignor) match {
      case (true, _) => MovementType.UkToEu
      case (_, true) => MovementType.ImportEu
      case _ =>
        logger.error(s"[movementType] invalid UserType for CAM journey: ${request.userTypeFromErn}")
        throw InvalidUserTypeException(s"[MovementScenario][movementType] invalid UserType for CAM journey: ${request.userTypeFromErn}")
    }
  }

  /**
   * emcs: indirect_export / import_for_indirect_export
   */
  case class ExportWithCustomsDeclarationLodgedInTheEu()(implicit val request: UserRequest[_])
    extends WithName("exportWithCustomsDeclarationLodgedInTheEu") with MovementScenario {

    val originType: OriginType = getOriginType()

    val destinationType: DestinationType = DestinationType.Export

    val movementType: MovementType = (request.isWarehouseKeeper, request.isRegisteredConsignor) match {
      case (true, _) => MovementType.IndirectExport
      case (_, true) => MovementType.ImportIndirectExport
      case _ =>
        logger.error(s"[movementType] invalid UserType for CAM journey: ${request.userTypeFromErn}")
        throw InvalidUserTypeException(s"[MovementScenario][movementType] invalid UserType for CAM journey: ${request.userTypeFromErn}")
    }
  }

  /**
   * emcs: registered_consignee / import_for_registered_consignee
   */
  case class RegisteredConsignee()(implicit val request: UserRequest[_])
    extends WithName("registeredConsignee") with MovementScenario {

    val originType: OriginType = getOriginType()

    val destinationType: DestinationType = DestinationType.RegisteredConsignee

    val movementType: MovementType = (request.isWarehouseKeeper, request.isRegisteredConsignor) match {
      case (true, _) => MovementType.UkToEu
      case (_, true) => MovementType.ImportEu
      case _ =>
        logger.error(s"[movementType] invalid UserType for CAM journey: ${request.userTypeFromErn}")
        throw InvalidUserTypeException(s"[MovementScenario][movementType] invalid UserType for CAM journey: ${request.userTypeFromErn}")
    }
  }

  /**
   * emcs: temp_registered_consignee / import_for_temp_registered_consignee
   */
  case class TemporaryRegisteredConsignee()(implicit val request: UserRequest[_])
    extends WithName("temporaryRegisteredConsignee") with MovementScenario {

    val originType: OriginType = getOriginType()

    val destinationType: DestinationType = DestinationType.TemporaryRegisteredConsignee

    val movementType: MovementType = (request.isWarehouseKeeper, request.isRegisteredConsignor) match {
      case (true, _) => MovementType.UkToEu
      case (_, true) => MovementType.ImportEu
      case _ =>
        logger.error(s"[movementType] invalid UserType for CAM journey: ${request.userTypeFromErn}")
        throw InvalidUserTypeException(s"[MovementScenario][movementType] invalid UserType for CAM journey: ${request.userTypeFromErn}")
    }
  }

  /**
   * emcs: unknown_destination / import_for_unknown_destination
   */
  case class UnknownDestination()(implicit val request: UserRequest[_])
    extends WithName("unknownDestination") with MovementScenario {

    val originType: OriginType = getOriginType()

    val destinationType: DestinationType = DestinationType.UnknownDestination

    val movementType: MovementType = (request.isWarehouseKeeper, request.isRegisteredConsignor) match {
      case (true, _) => MovementType.UkToEu
      case (_, true) => MovementType.ImportUnknownDestination
      case _ =>
        logger.error(s"[movementType] invalid UserType for CAM journey: ${request.userTypeFromErn}")
        throw InvalidUserTypeException(s"[MovementScenario][movementType] invalid UserType for CAM journey: ${request.userTypeFromErn}")
    }
  }

  def valuesUk(implicit request: UserRequest[_]): Seq[MovementScenario] = Seq(
    ExportWithCustomsDeclarationLodgedInTheUk(),
    GbTaxWarehouse()
  )

  def valuesEu(implicit request: UserRequest[_]): Seq[MovementScenario] = Seq(
    DirectDelivery(),
    ExemptedOrganisation(),
    ExportWithCustomsDeclarationLodgedInTheEu(),
    ExportWithCustomsDeclarationLodgedInTheUk(),
    RegisteredConsignee(),
    EuTaxWarehouse(),
    GbTaxWarehouse(),
    TemporaryRegisteredConsignee(),
    UnknownDestination()
  )

  def values(implicit request: UserRequest[_]): Seq[MovementScenario] = (valuesUk ++ valuesEu).distinct

  implicit def enumerable(implicit request: UserRequest[_]): Enumerable[MovementScenario] =
    Enumerable(values.map(v => v.toString -> v): _*)
}
