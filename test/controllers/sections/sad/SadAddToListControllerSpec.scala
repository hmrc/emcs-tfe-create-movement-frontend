package controllers.sections.sad

import base.SpecBase
import forms.SadAddToListFormProvider
import mocks.services.MockUserAnswersService
import models.{NormalMode, UserAnswers}
import navigation.FakeNavigators.FakeNavigator
import navigation.Navigator
import pages.SadAddToListPage
import play.api.inject.bind
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.UserAnswersService
import views.html.SadAddToListView

import scala.concurrent.Future

class SadAddToListControllerSpec extends SpecBase with MockUserAnswersService {

  def onwardRoute = Call("GET", "/foo")

  val formProvider = new SadAddToListFormProvider()
  val form = formProvider()

  lazy val sadAddToListRoute = routes.SadAddToListController.onPageLoad(testErn, testDraftId, NormalMode).url

  "SadAddToList Controller" - {

    "must return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, sadAddToListRoute)

        val result = route(application, request).value

        val view = application.injector.instanceOf[SadAddToListView]

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form, NormalMode)(dataRequest(request), messages(application)).toString
      }
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = emptyUserAnswers.set(SadAddToListPage, "answer")

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, sadAddToListRoute)

        val view = application.injector.instanceOf[SadAddToListView]

        val result = route(application, request).value

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form.fill("answer"), NormalMode)(dataRequest(request), messages(application)).toString
      }
    }

    "must redirect to the next page when valid data is submitted" in {

      MockUserAnswersService.set().returns(Future.successful(emptyUserAnswers))

      val application =
        applicationBuilder(userAnswers = Some(emptyUserAnswers))
          .overrides(
            bind[Navigator].toInstance(new FakeNavigator(onwardRoute)),
            bind[UserAnswersService].toInstance(mockUserAnswersService)
          )
          .build()

      running(application) {
        val request =
          FakeRequest(POST, sadAddToListRoute)
            .withFormUrlEncodedBody(("value", "answer"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual onwardRoute.url
      }
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      running(application) {
        val request =
          FakeRequest(POST, sadAddToListRoute)
            .withFormUrlEncodedBody(("value", ""))

        val boundForm = form.bind(Map("value" -> ""))

        val view = application.injector.instanceOf[SadAddToListView]

        val result = route(application, request).value

        status(result) mustEqual BAD_REQUEST
        contentAsString(result) mustEqual view(boundForm, NormalMode)(dataRequest(request), messages(application)).toString
      }
    }

    "must redirect to Journey Recovery for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      running(application) {
        val request = FakeRequest(GET, sadAddToListRoute)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad().url
      }
    }

    "must redirect to Journey Recovery for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      running(application) {
        val request =
          FakeRequest(POST, sadAddToListRoute)
            .withFormUrlEncodedBody(("value", "answer"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad().url
      }
    }
  }
}
