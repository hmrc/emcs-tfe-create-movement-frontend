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

package navigation

import controllers.routes
import models.sections.journeyType.HowMovementTransported.Other
import models.{CheckMode, Mode, NormalMode, ReviewMode, UserAnswers}
import pages.Page
import pages.sections.journeyType.{CheckYourAnswersJourneyTypePage, GiveInformationOtherTransportPage, HowMovementTransportedPage, JourneyTimeDaysPage, JourneyTimeHoursPage}
import play.api.mvc.Call

import javax.inject.{Inject, Singleton}

@Singleton
class JourneyTypeNavigator @Inject()() extends BaseNavigator {

  private val normalRoutes: Page => UserAnswers => Call = {
    case HowMovementTransportedPage =>
      (userAnswers: UserAnswers) =>
        userAnswers.get(HowMovementTransportedPage) match {
          case Some(Other) =>
            controllers.sections.journeyType.routes.GiveInformationOtherTransportController.onPageLoad(userAnswers.ern, userAnswers.draftId, NormalMode)
          case _ =>
            controllers.sections.journeyType.routes.JourneyTimeDaysController.onPageLoad(userAnswers.ern, userAnswers.draftId, NormalMode)
        }

    case GiveInformationOtherTransportPage => (userAnswers: UserAnswers) =>
      controllers.sections.journeyType.routes.JourneyTimeDaysController.onPageLoad(userAnswers.ern, userAnswers.draftId, NormalMode)

    case JourneyTimeDaysPage => (userAnswers: UserAnswers) =>
      controllers.sections.journeyType.routes.CheckYourAnswersJourneyTypeController.onPageLoad(userAnswers.ern, userAnswers.draftId)

    case JourneyTimeHoursPage => (userAnswers: UserAnswers) =>
      controllers.sections.journeyType.routes.CheckYourAnswersJourneyTypeController.onPageLoad(userAnswers.ern, userAnswers.draftId)

    case CheckYourAnswersJourneyTypePage =>
      //TODO update when next page is created
      (_: UserAnswers) => testOnly.controllers.routes.UnderConstructionController.onPageLoad()

    case _ => (userAnswers: UserAnswers) =>
      controllers.sections.journeyType.routes.CheckYourAnswersJourneyTypeController.onPageLoad(userAnswers.ern, userAnswers.draftId)
  }

  private[navigation] val checkRouteMap: Page => UserAnswers => Call = {
    case HowMovementTransportedPage =>
      (userAnswers: UserAnswers) =>
        userAnswers.get(HowMovementTransportedPage) match {
          case Some(Other) =>
            controllers.sections.journeyType.routes.GiveInformationOtherTransportController.onPageLoad(userAnswers.ern, userAnswers.draftId, CheckMode)
          case _ =>
            controllers.sections.journeyType.routes.JourneyTimeDaysController.onPageLoad(userAnswers.ern, userAnswers.draftId, CheckMode)
        }
    case _ => (userAnswers: UserAnswers) =>
      controllers.sections.journeyType.routes.CheckYourAnswersJourneyTypeController.onPageLoad(userAnswers.ern, userAnswers.draftId)
  }

  private[navigation] val reviewRouteMap: Page => UserAnswers => Call = {
    case _ =>
      (userAnswers: UserAnswers) => routes.CheckYourAnswersController.onPageLoad(userAnswers.ern, userAnswers.draftId)
  }

  override def nextPage(page: Page, mode: Mode, userAnswers: UserAnswers): Call = mode match {
    case NormalMode =>
      normalRoutes(page)(userAnswers)
    case CheckMode =>
      checkRouteMap(page)(userAnswers)
    case ReviewMode =>
      reviewRouteMap(page)(userAnswers)
  }
}
