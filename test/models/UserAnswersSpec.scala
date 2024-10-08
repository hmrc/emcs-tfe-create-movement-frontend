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

/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package models

import base.SpecBase
import fixtures.{ItemFixtures, MovementSubmissionFailureFixtures}
import models.response.templates.MovementTemplate
import org.scalamock.scalatest.MockFactory
import pages.QuestionPage
import pages.sections.documents.DocumentsSection
import pages.sections.info.{DeferredMovementPage, DispatchDetailsPage, InvoiceDetailsPage, LocalReferenceNumberPage}
import pages.sections.items.ItemsSection
import pages.sections.journeyType.JourneyTypeSection
import pages.sections.sad.SadSection
import pages.sections.transportUnit.TransportUnitsSection
import play.api.libs.json._
import play.api.test.FakeRequest
import queries.Derivable
import utils.{TimeMachine, UUIDGenerator}

import java.time.Instant


class UserAnswersSpec extends SpecBase with MovementSubmissionFailureFixtures with ItemFixtures with MockFactory {

  implicit lazy val mockUUIDGenerator: UUIDGenerator = mock[UUIDGenerator]
  implicit lazy val mockTimeMachine: TimeMachine = mock[TimeMachine]

  val now = Instant.now()

  case class TestPage(jsPath: JsPath = JsPath) extends QuestionPage[String] {
    override val toString: String = "TestPage"
    override val path: JsPath = jsPath \ toString
  }

  case class TestPage2(jsPath: JsPath = JsPath) extends QuestionPage[String] {
    override val toString: String = "TestPage2"
    override val path: JsPath = jsPath \ toString
  }

  case class TestModel(TestPage: String,
                       TestPage2: Option[String] = None)

  object TestModel {
    implicit val fmt: Format[TestModel] = Json.format[TestModel]
  }

  "UserAnswers" - {

    "when calling .set(page)" - {

      "when no data exists for that page" - {

        "must set the answer for the first userEnteredTime" in {
          emptyUserAnswers.set(TestPage(), "foo") mustBe emptyUserAnswers.copy(data = Json.obj(
            "TestPage" -> "foo"
          ))
        }
      }

      "when data exists for that page" - {

        "must change the answer" in {
          val withData = emptyUserAnswers.copy(data = Json.obj(
            "TestPage" -> "foo"
          ))
          withData.set(TestPage(), "bar") mustBe emptyUserAnswers.copy(data = Json.obj(
            "TestPage" -> "bar"
          ))
        }
      }

      "when setting at a subPath with indexes" - {

        "must store the answer at the subPath" in {
          val result =
            emptyUserAnswers
              .set(TestPage(__ \ "items" \ 0), "foo")
              .set(TestPage(__ \ "items" \ 1), "bar")
              .set(TestPage(__ \ "items" \ 2), "wizz")


          result.data mustBe Json.obj(
            "items" -> Json.arr(
              Json.obj("TestPage" -> "foo"),
              Json.obj("TestPage" -> "bar"),
              Json.obj("TestPage" -> "wizz")
            )
          )
        }
      }

      "when setting at a subPath which contains nested indexes" - {

        "must store the answer at the subPath" in {
          val result =
            emptyUserAnswers
              .set(TestPage(__ \ "items" \ 0 \ "subItems" \ 0), "foo")
              .set(TestPage(__ \ "items" \ 0 \ "subItems" \ 1), "bar")
              .set(TestPage(__ \ "items" \ 0 \ "subItems" \ 2), "wizz")

          result.data mustBe Json.obj(
            "items" -> Json.arr(
              Json.obj(
                "subItems" -> Json.arr(
                  Json.obj("TestPage" -> "foo"),
                  Json.obj("TestPage" -> "bar"),
                  Json.obj("TestPage" -> "wizz")
                )
              )
            )
          )
        }
      }
    }

    "when calling .get(page)" - {

      "when no data exists for that page" - {

        "must return None" in {
          emptyUserAnswers.get(TestPage()) mustBe None
        }
      }

      "when data exists for that page" - {

        "must Some(data)" in {
          val withData = emptyUserAnswers.copy(data = Json.obj(
            "TestPage" -> "foo"
          ))
          withData.get(TestPage()) mustBe Some("foo")
        }
      }

      "when getting data at a subPath with indexes" - {

        "must return the answer at the subPath" in {

          val withData = emptyUserAnswers.copy(data = Json.obj(
            "items" -> Json.arr(
              Json.obj("TestPage" -> "foo"),
              Json.obj("TestPage" -> "bar"),
              Json.obj("TestPage" -> "wizz")
            )
          ))
          withData.get(TestPage(__ \ "items" \ 0)) mustBe Some("foo")
        }
      }

      "when setting at a subPath which contains nested indexes" - {

        "must store the answer at the subPath" in {
          val withData = emptyUserAnswers.copy(data = Json.obj(
            "items" -> Json.arr(
              Json.obj(
                "subItems" -> Json.arr(
                  Json.obj("TestPage" -> "foo"),
                  Json.obj("TestPage" -> "bar"),
                  Json.obj("TestPage" -> "wizz")
                )
              )
            )
          ))
          withData.get(TestPage(__ \ "items" \ 0 \ "subItems" \ 0)) mustBe Some("foo")
        }
      }
    }

    "when calling .get(Derivable)" - {
      object TestDerivable extends Derivable[Seq[JsObject], Int] {
        override val path: JsPath = JsPath \ "items"

        override val derive: Seq[JsObject] => Int = _.size
      }

      "must return None if the data for the page doesnt exist" in {
        emptyUserAnswers.getCount(TestDerivable) mustBe None
      }

      "must perform the derive function if page exists" in {
        val withData = emptyUserAnswers.copy(data = Json.obj(
          "items" -> Json.arr(
            Json.obj("TestPage" -> "foo"),
            Json.obj("TestPage" -> "bar"),
            Json.obj("TestPage" -> "wizz")
          )
        ))
        withData.getCount(TestDerivable) mustBe Some(3)
      }
    }

    "when calling .remove(page)" - {

      "when no data exists for that page" - {

        "must return the emptyUserAnswers unchanged" in {
          val withData = emptyUserAnswers.copy(data = Json.obj(
            "AnotherPage" -> "foo"
          ))
          withData.remove(TestPage()) mustBe withData
        }
      }

      "when data exists for that page" - {

        "must remove the answer" in {
          val withData = emptyUserAnswers.copy(data = Json.obj(
            "TestPage" -> "foo"
          ))
          withData.remove(TestPage()) mustBe emptyUserAnswers
        }
      }

      "when removing data at a subPath with indexes" - {

        "when the page is the last page in the subObject" - {

          "must remove the entire object from the array at the subPath" in {

            val withData = emptyUserAnswers.copy(data = Json.obj(
              "items" -> Json.arr(
                Json.obj("TestPage" -> "foo"),
                Json.obj("TestPage" -> "bar"),
                Json.obj("TestPage" -> "wizz")
              )
            ))
            val result = withData.remove(TestPage(__ \ "items" \ 1))
            result.data mustBe Json.obj(
              "items" -> Json.arr(
                Json.obj("TestPage" -> "foo"),
                Json.obj("TestPage" -> "wizz")
              )
            )
          }
        }

        "when the page is NOT the last page in the subObject" - {

          "must remove just that page object key from the object in the array" in {

            val withData = emptyUserAnswers.copy(data = Json.obj(
              "items" -> Json.arr(
                Json.obj("TestPage" -> "foo"),
                Json.obj(
                  "TestPage" -> "bar",
                  "TestPage2" -> "bar2"
                ),
                Json.obj("TestPage" -> "wizz")
              )
            ))
            val result = withData.remove(TestPage(__ \ "items" \ 1))
            result.data mustBe Json.obj(
              "items" -> Json.arr(
                Json.obj("TestPage" -> "foo"),
                Json.obj("TestPage2" -> "bar2"),
                Json.obj("TestPage" -> "wizz")
              )
            )
          }
        }
      }

      "when removing at a subPath which contains nested indexes" - {

        "when the page is that last item in the arrays object" - {

          "must remove the object containing the answer from the array at the subPath" in {
            val withData = emptyUserAnswers.copy(data = Json.obj(
              "items" -> Json.arr(
                Json.obj(
                  "subItems" -> Json.arr(
                    Json.obj("TestPage" -> "foo"),
                    Json.obj("TestPage" -> "bar"),
                    Json.obj("TestPage" -> "wizz")
                  )
                )
              )
            ))
            val result = withData.remove(TestPage(__ \ "items" \ 0 \ "subItems" \ 1))
            result.data mustBe Json.obj(
              "items" -> Json.arr(
                Json.obj(
                  "subItems" -> Json.arr(
                    Json.obj("TestPage" -> "foo"),
                    Json.obj("TestPage" -> "wizz")
                  )
                )
              )
            )
          }
        }

        "when the page is NOT the last item in the arrays object" - {

          "must remove just that key from the object at the subPath" in {
            val withData = emptyUserAnswers.copy(data = Json.obj(
              "items" -> Json.arr(
                Json.obj(
                  "subItems" -> Json.arr(
                    Json.obj("TestPage" -> "foo"),
                    Json.obj(
                      "TestPage" -> "bar",
                      "TestPage2" -> "bar2"
                    ),
                    Json.obj("TestPage" -> "wizz")
                  )
                )
              )
            ))
            val result = withData.remove(TestPage(__ \ "items" \ 0 \ "subItems" \ 1))
            result.data mustBe Json.obj(
              "items" -> Json.arr(
                Json.obj(
                  "subItems" -> Json.arr(
                    Json.obj("TestPage" -> "foo"),
                    Json.obj(
                      "TestPage2" -> "bar2"
                    ),
                    Json.obj("TestPage" -> "wizz")
                  )
                )
              )
            )
          }
        }
      }
    }

    "when calling .handleResult" - {

      "when failed to update the UserAnswers" - {

        "must throw the exception" in {
          intercept[JsResultException](emptyUserAnswers.handleResult(JsError("OhNo")))
        }
      }

      "when updated UserAnswers successfully" - {

        "must return the user answers" in {
          emptyUserAnswers.handleResult(JsSuccess(emptyUserAnswers.data)) mustBe emptyUserAnswers
        }
      }
    }

    "when calling .filterForPages" - {

      val page1 = new QuestionPage[String] {
        override val toString: String = "page1"
        override val path: JsPath = __ \ toString
      }
      val page2 = new QuestionPage[String] {
        override val toString: String = "page2"
        override val path: JsPath = __ \ toString
      }
      val page3 = new QuestionPage[String] {
        override val toString: String = "page3"
        override val path: JsPath = __ \ toString
      }

      "must only return pages in the supplied Seq" in {
        val existingUserAnswers = emptyUserAnswers
          .set(page1, "foo")
          .set(page2, "bar")
          .set(page3, "wizz")

        existingUserAnswers.filterForPages(Seq(page1, page2)) mustBe {
          emptyUserAnswers.copy(data = Json.obj(
            page1.toString -> "foo",
            page2.toString -> "bar"
          ))
        }
      }

      "must return an empty Seq if none of the supplied pages are in UserAnswers" in {
        val existingUserAnswers = emptyUserAnswers
          .set(page1, "foo")
          .set(page3, "wizz")

        existingUserAnswers.filterForPages(Seq(page2)) mustBe emptyUserAnswers
      }
    }

    "when calling haveAllSubmissionErrorsBeenFixed" - {

      implicit val dr = dataRequest(FakeRequest())

      def userAnswers(hasBeenFixed: Boolean) = emptyUserAnswers.copy(
        submissionFailures = Seq(
          movementSubmissionFailure.copy(hasBeenFixed = hasBeenFixed),
          consignorNotApprovedToSendFailure //nonFixable failure (not linked to specific field)
        )
      )

      "return true" - {

        "when there are no errors" in {
          emptyUserAnswers.haveAllFixableSubmissionErrorsBeenFixed mustBe true
        }

        "when all the fixable errors have been fixed" in {
          userAnswers(hasBeenFixed = true).haveAllFixableSubmissionErrorsBeenFixed mustBe true
        }
      }

      "return false" - {

        "when a fixable error hasn't been fixed" in {
          userAnswers(hasBeenFixed = false).haveAllFixableSubmissionErrorsBeenFixed mustBe false
        }

        "when multiple errors exist and only one hasn't been fixed" in {
          emptyUserAnswers.copy(
            submissionFailures = Seq(
              movementSubmissionFailure.copy(hasBeenFixed = true),
              movementSubmissionFailure.copy(hasBeenFixed = false),
              movementSubmissionFailure.copy(hasBeenFixed = true)
            )
          ).haveAllFixableSubmissionErrorsBeenFixed mustBe false
        }
      }
    }

    "when calling .toTemplate" - {

      "when an existing ID is provided" - {

        "must remove sections that shouldn't be saved and create a MovementTemplate model, using supplied ID" in {

          (() => mockUUIDGenerator.randomUUID()).expects().never()
          (() => mockTimeMachine.instant()).expects().returns(now)

          baseFullUserAnswers.toTemplate(templateName, Some("existingId")) mustBe MovementTemplate(
            ern = baseFullUserAnswers.ern,
            templateId = "existingId",
            templateName = templateName,
            data = ItemsSection.removeCommercialSealFromPackaging(
                ItemsSection.removePackagingIfHasShippingMark(baseFullUserAnswers)
              )
              //Remove Info Section pages
              .remove(LocalReferenceNumberPage(isOnPreDraftFlow = false))
              .remove(DispatchDetailsPage(isOnPreDraftFlow = false))
              .remove(DispatchDetailsPage(isOnPreDraftFlow = false))
              .remove(InvoiceDetailsPage(isOnPreDraftFlow = false))
              .remove(DeferredMovementPage(isOnPreDraftFlow = false))
              //Remove other full sections
              .remove(JourneyTypeSection)
              .remove(TransportUnitsSection)
              .remove(SadSection)
              .remove(DocumentsSection).data,
            lastUpdated = now
          )
        }
      }

      "when an existing ID is NOT provided" - {

        "must remove sections that shouldn't be saved and create a MovementTemplate model, using a new ID" in {

          (() => mockUUIDGenerator.randomUUID()).expects().returns(templateId)
          (() => mockTimeMachine.instant()).expects().returns(now)

          baseFullUserAnswers.toTemplate(templateName, None) mustBe MovementTemplate(
            ern = baseFullUserAnswers.ern,
            templateId = templateId,
            templateName = templateName,
            data = ItemsSection.removeCommercialSealFromPackaging(
                ItemsSection.removePackagingIfHasShippingMark(baseFullUserAnswers)
              )
              //Remove Info Section pages
              .remove(LocalReferenceNumberPage(isOnPreDraftFlow = false))
              .remove(DispatchDetailsPage(isOnPreDraftFlow = false))
              .remove(DispatchDetailsPage(isOnPreDraftFlow = false))
              .remove(InvoiceDetailsPage(isOnPreDraftFlow = false))
              .remove(DeferredMovementPage(isOnPreDraftFlow = false))
              //Remove other full sections
              .remove(JourneyTypeSection)
              .remove(TransportUnitsSection)
              .remove(SadSection)
              .remove(DocumentsSection).data,
            lastUpdated = now
          )
        }
      }
    }
  }
}