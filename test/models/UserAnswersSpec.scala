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
import fixtures.MovementSubmissionFailureFixtures
import pages.{DeclarationPage, QuestionPage}
import pages.sections.info.LocalReferenceNumberPage
import play.api.libs.json._
import queries.Derivable
import utils.SubmissionFailureErrorCodes.localReferenceNumberError


class UserAnswersSpec extends SpecBase with MovementSubmissionFailureFixtures {

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
        emptyUserAnswers.get(TestDerivable) mustBe None
      }

      "must perform the derive function if page exists" in {
        val withData = emptyUserAnswers.copy(data = Json.obj(
          "items" -> Json.arr(
            Json.obj("TestPage" -> "foo"),
            Json.obj("TestPage" -> "bar"),
            Json.obj("TestPage" -> "wizz")
          )
        ))
        withData.get(TestDerivable) mustBe Some(3)
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

      def userAnswers(hasFixed: Boolean) = emptyUserAnswers.copy(
        submissionFailures = Seq(movementSubmissionFailure.copy(hasFixed = hasFixed))
      )

      "return true" - {

        "when there are no errors" in {

          emptyUserAnswers.haveAllSubmissionErrorsBeenFixed mustBe true
        }

        "when all the errors have been fixed" in {

          userAnswers(hasFixed = true).haveAllSubmissionErrorsBeenFixed mustBe true
        }
      }

      "return false" - {

        "when an error hasn't been fixed" in {

          userAnswers(hasFixed = false).haveAllSubmissionErrorsBeenFixed mustBe false
        }

        "when multiple errors exist and only one hasn't been fixed" in {

          emptyUserAnswers.copy(
            submissionFailures = Seq(movementSubmissionFailure.copy(hasFixed = true),
              movementSubmissionFailure.copy(hasFixed = false),
              movementSubmissionFailure.copy(hasFixed = true))
          ).haveAllSubmissionErrorsBeenFixed mustBe false
        }
      }
    }

    "when calling isSubmissionErrorOnPage" - {

      "must return true" - {

        s"when the page is $LocalReferenceNumberPage and the error is $localReferenceNumberError and not fixed" in {
          emptyUserAnswers.copy(
            submissionFailures = Seq(movementSubmissionFailure.copy(errorType = localReferenceNumberError, hasFixed = false))
          ).isSubmissionErrorOnPage(LocalReferenceNumberPage()) mustBe true
        }
      }

      "must return false" - {

        s"for $LocalReferenceNumberPage" - {

          s"when the error is not a $localReferenceNumberError" in {
            emptyUserAnswers.copy(
              submissionFailures = Seq(movementSubmissionFailure.copy(errorType = "4403", hasFixed = false))
            ).isSubmissionErrorOnPage(LocalReferenceNumberPage()) mustBe false
          }

          s"when the error is $localReferenceNumberError but fixed" in {
            emptyUserAnswers.copy(
              submissionFailures = Seq(movementSubmissionFailure.copy(errorType = localReferenceNumberError, hasFixed = true))
            ).isSubmissionErrorOnPage(LocalReferenceNumberPage()) mustBe false
          }

          "no errors exist" in {
            emptyUserAnswers.isSubmissionErrorOnPage(LocalReferenceNumberPage()) mustBe false
          }
        }

        "the page is not matched" in {
          emptyUserAnswers.isSubmissionErrorOnPage(DeclarationPage) mustBe false
        }
      }
    }

    "when calling getOriginalAttributeValueForPage" - {

      "must return Some(_)" - {

        "for the LRN page" - {

          s"when the error type is $localReferenceNumberError and an original attribute value exists" in {
            emptyUserAnswers.copy(
              submissionFailures = Seq(movementSubmissionFailure.copy(errorType = localReferenceNumberError, hasFixed = false, originalAttributeValue = Some("LRN1")))
            ).getOriginalAttributeValueForPage(LocalReferenceNumberPage()) mustBe Some("LRN1")
          }
        }
      }

      "must return None" - {

        "for the LRN page" - {

          s"when the error type is not $localReferenceNumberError" in {
            emptyUserAnswers.copy(
              submissionFailures = Seq(movementSubmissionFailure.copy(errorType = "4403", hasFixed = false, originalAttributeValue = Some("LRN1")))
            ).getOriginalAttributeValueForPage(LocalReferenceNumberPage()) mustBe None
          }

          "when the original value is not defined" in {
            emptyUserAnswers.copy(
              submissionFailures = Seq(movementSubmissionFailure.copy(errorType = localReferenceNumberError, hasFixed = false, originalAttributeValue = None))
            ).getOriginalAttributeValueForPage(LocalReferenceNumberPage()) mustBe None
          }
        }

        "the page is not matched" in {
          emptyUserAnswers.getOriginalAttributeValueForPage(DeclarationPage) mustBe None
        }
      }
    }
  }
}