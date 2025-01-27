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

package forms.sections.info

import base.SpecBase
import fixtures.messages.sections.info.DispatchDetailsMessages
import forms.behaviours.StringFieldBehaviours
import play.api.data.FormError
import play.api.i18n.Messages
import utils.{DateTimeUtils, TimeMachine}

import java.time.{Instant, LocalDateTime}

class DispatchDetailsFormProviderSpec extends SpecBase with StringFieldBehaviours with DateTimeUtils {

  val dateTimeNow = LocalDateTime.now()
  val timeMachine: TimeMachine = new TimeMachine {
    override def now(): LocalDateTime = dateTimeNow
    override def instant(): Instant = Instant.now
  }

  val dateField = "value"
  val dayField = "value.day"
  val monthField = "value.month"
  val yearField = "value.year"
  val timeField = "time"

  def form(isDeferred: Boolean = false) = new DispatchDetailsFormProvider(appConfig, timeMachine)(isDeferred)

  def formAnswersMap(day: String = dispatchDetailsModel(dateTimeNow.toLocalDate).date.getDayOfMonth.toString,
                     month: String = dispatchDetailsModel(dateTimeNow.toLocalDate).date.getMonthValue.toString,
                     year: String = dispatchDetailsModel(dateTimeNow.toLocalDate).date.getYear.toString,
                     time: String = dispatchDetailsModel(dateTimeNow.toLocalDate).time.formatTimeForUIOutput()
                    ): Map[String, String] = {
    Map(
      dayField -> day,
      monthField -> month,
      yearField -> year,
      timeField -> time
    )
  }

  "the dispatch details form" - {

    "when all filled in are valid " in {

      val data = formAnswersMap()

      val expectedResult = Some(dispatchDetailsModel(dateTimeNow.toLocalDate))
      val expectedErrors = Seq.empty

      val actualResult = form().bind(data)

      actualResult.errors mustBe expectedErrors
      actualResult.value mustBe expectedResult
    }

    "the date fields" - {

      "should give an error" - {

        "when not entered" in {

          val data = formAnswersMap(day = "", month = "", year = "")

          val expectedResult = Seq(FormError(dateField, s"dispatchDetails.$dateField.error.required.all"))

          val actualResult = form().bind(data)

          actualResult.errors mustBe expectedResult
        }

        "when the date is invalid" in {

          val data = formAnswersMap(day = "1000", month = "1000", year = "a")

          val expectedResult = Seq(FormError(dateField, s"dispatchDetails.$dateField.error.notARealDate"))

          val actualResult = form().bind(data)

          actualResult.errors mustBe expectedResult
        }

        "when is a deferred movement" - {

          "when the date is before the earliest allowed date" in {
            val data = formAnswersMap(day = "31", month = "12", year = "1999")
            val expectedResult = Seq(FormError(dateField, s"dispatchDetails.$dateField.error.earliestDate.deferred"))
            val actualResult = form(isDeferred = true).bind(data)

            actualResult.errors mustBe expectedResult
          }

          "when the date is equal to the earliest allowed date" in {
            val data = formAnswersMap(day = "1", month = "1", year = "2000")
            val actualResult = form(isDeferred = true).bind(data)

            actualResult.errors mustBe Seq()
          }

          "when the date is equal to todays date" in {
            val data = formAnswersMap(day = dateTimeNow.getDayOfMonth.toString, month = dateTimeNow.getMonthValue.toString, year = dateTimeNow.getYear.toString)
            val actualResult = form(isDeferred = true).bind(data)

            actualResult.errors mustBe Seq()
          }

          "when the date is after todays date" in {
            val tomorrow = dateTimeNow.plusDays(1)
            val data = formAnswersMap(day = tomorrow.getDayOfMonth.toString, month = tomorrow.getMonthValue.toString, year = tomorrow.getYear.toString)
            val expectedResult = Seq(FormError(dateField, s"dispatchDetails.$dateField.error.latestDate.deferred", Seq(appConfig.maxDispatchDateFutureDays)))
            val actualResult = form(isDeferred = true).bind(data)

            actualResult.errors mustBe expectedResult
          }
        }

        "when is NOT a deferred movement" - {

          "when the date is before today" in {
            val yesterday = dateTimeNow.minusDays(1)
            val data = formAnswersMap(day = yesterday.getDayOfMonth.toString, month = yesterday.getMonthValue.toString, year = yesterday.getYear.toString)
            val expectedResult = Seq(FormError(dateField, s"dispatchDetails.$dateField.error.earliestDate"))
            val actualResult = form().bind(data)

            actualResult.errors mustBe expectedResult
          }

          "when the date is equal to today" in {
            val data = formAnswersMap(day = dateTimeNow.getDayOfMonth.toString, month = dateTimeNow.getMonthValue.toString, year = dateTimeNow.getYear.toString)
            val actualResult = form().bind(data)

            actualResult.errors mustBe Seq()
          }

          s"when the date is equal to todays date + max days in future" in {
            val maxDate = dateTimeNow.plusDays(appConfig.maxDispatchDateFutureDays)
            val data = formAnswersMap(day = maxDate.getDayOfMonth.toString, month = maxDate.getMonthValue.toString, year = maxDate.getYear.toString)
            val actualResult = form().bind(data)

            actualResult.errors mustBe Seq()
          }

          "when the date is after todays date + max days in future" in {
            val tomorrow = dateTimeNow.plusDays(appConfig.maxDispatchDateFutureDays + 1)
            val data = formAnswersMap(day = tomorrow.getDayOfMonth.toString, month = tomorrow.getMonthValue.toString, year = tomorrow.getYear.toString)
            val expectedResult = Seq(FormError(dateField, s"dispatchDetails.$dateField.error.latestDate", Seq(appConfig.maxDispatchDateFutureDays)))
            val actualResult = form().bind(data)

            actualResult.errors mustBe expectedResult
          }
        }
      }

      "the day field" - {

        "should give an error" - {

          "when not supplied" in {

            val data = formAnswersMap(day = "")

            val expectedResult = Seq(FormError(dateField, s"dispatchDetails.$dateField.error.required.one", List("day")))

            val actualResult = form().bind(data)

            actualResult.errors mustBe expectedResult
          }
        }
      }

      "the month field" - {

        "should give an error" - {

          "when not supplied" in {

            val data = formAnswersMap(month = "")

            val expectedResult = Seq(FormError(dateField, s"dispatchDetails.$dateField.error.required.one", List("month")))

            val actualResult = form().bind(data)

            actualResult.errors mustBe expectedResult
          }
        }
      }

      "the year field" - {

        "should give an error" - {

          "when not entered" in {

            val data = formAnswersMap(year = "")

            val expectedResult = Seq(FormError(dateField, s"dispatchDetails.$dateField.error.required.one", List("year")))

            val actualResult = form().bind(data)

            actualResult.errors mustBe expectedResult
          }

          "when it's not four digits long" in {
            val data = formAnswersMap(year = "23")

            val expectedResult = Seq(FormError(dateField, s"dispatchDetails.$dateField.error.yearNotFourDigits"))

            val actualResult = form().bind(data)

            actualResult.errors mustBe expectedResult
          }
        }
      }

      "the day and month fields" - {

        "should give an error" - {

          "when not entered" in {

            val data = formAnswersMap(day = "", month = "")

            val expectedResult = Seq(FormError(dateField, s"dispatchDetails.$dateField.error.required.two", List("day", "month")))

            val actualResult = form().bind(data)

            actualResult.errors mustBe expectedResult
          }
        }
      }

      "the day and year fields" - {

        "should give an error" - {

          "when not entered" in {
            val data = formAnswersMap(day = "", year = "")

            val expectedResult = Seq(FormError(dateField, s"dispatchDetails.$dateField.error.required.two", List("day", "year")))

            val actualResult = form().bind(data)

            actualResult.errors mustBe expectedResult
          }
        }
      }

      "the month and year" - {

        "should give an error" - {

          "when not entered" in {

            val data = formAnswersMap(month = "", year = "")

            val expectedResult = Seq(FormError(dateField, s"dispatchDetails.$dateField.error.required.two", List("month", "year")))

            val actualResult = form().bind(data)

            actualResult.errors mustBe expectedResult
          }
        }
      }
    }

    "the time field" - {

      "should error" - {

        "when not entered" in {

          val data = formAnswersMap(time = "")

          val expectedResult = Seq(FormError(timeField, s"dispatchDetails.$timeField.error.required"))

          val actualResult = form().bind(data)

          actualResult.errors mustBe expectedResult
        }

        "when an incorrect formatted time is entered" in {

          val data = formAnswersMap(time = "Ten past six")

          val expectedResult = Seq(FormError(timeField, s"dispatchDetails.$timeField.error.invalid"))

          val actualResult = form().bind(data)

          actualResult.errors mustBe expectedResult
        }
      }

    }
  }

  "Error Messages" - {

    Seq(DispatchDetailsMessages.English).foreach { messagesForLanguage =>

      s"when rendering the form in language code of '${messagesForLanguage.lang}'" - {

        implicit val msgs: Messages = messages(Seq(messagesForLanguage.lang))

        "must have the correct message content for deferred movement too far in future" in {
          msgs(s"dispatchDetails.$dateField.error.latestDate.deferred") mustBe messagesForLanguage.deferredTooFarFutureError
        }

        "must have the correct message content for deferred movement too far in past" in {
          msgs(s"dispatchDetails.$dateField.error.earliestDate.deferred") mustBe messagesForLanguage.deferredTooFarInPastError
        }

        "must have the correct message content for non-deferred movement too far in future" in {
          msgs(s"dispatchDetails.$dateField.error.latestDate", appConfig.maxDispatchDateFutureDays) mustBe
            messagesForLanguage.tooFarFutureError(appConfig.maxDispatchDateFutureDays)
        }

        "must have the correct message content for non-deferred movement too far in past" in {
          msgs(s"dispatchDetails.$dateField.error.earliestDate") mustBe messagesForLanguage.tooFarInPastError
        }
      }
    }
  }
}
