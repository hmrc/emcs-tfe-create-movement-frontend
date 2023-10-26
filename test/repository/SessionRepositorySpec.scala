package repository

import base.SpecBase
import config.AppConfig
import models.UserAnswers
import repositories.SessionRepository
import uk.gov.hmrc.mongo.test.{CleanMongoCollectionSupport, PlayMongoRepositorySupport}

import scala.concurrent.ExecutionContext

class SessionRepositorySpec extends SpecBase with PlayMongoRepositorySupport[UserAnswers] with CleanMongoCollectionSupport {

  implicit val ec: ExecutionContext = applicationBuilder().injector.instanceOf[ExecutionContext]

  lazy val repository: SessionRepository = new SessionRepository(
    mongoComponent = mongoComponent,
    appConfig = applicationBuilder().injector.instanceOf[AppConfig]
  )


  ".get" - {
    "return None when the repository is empty" in {
      repository.get("ern", "sessionId").futureValue mustBe None
    }

    "return the correct record from the repository" in {
      repository.set(emptyUserAnswers).futureValue mustBe true
      repository.get(testErn, testDraftId).futureValue.isDefined mustBe true
    }
  }

  ".set" - {
    "populate the repository correctly" in {
      repository.set(emptyUserAnswers).futureValue mustBe true
      repository.get(testErn, testDraftId).futureValue.isDefined mustBe true
    }
  }

  ".clear" - {
    "clear the repository correctly" in {
      repository.set(emptyUserAnswers).futureValue mustBe true
      repository.clear(testErn, testDraftId).futureValue mustBe true
      repository.get(testErn, testDraftId).futureValue.isDefined mustBe false

    }
  }

}
