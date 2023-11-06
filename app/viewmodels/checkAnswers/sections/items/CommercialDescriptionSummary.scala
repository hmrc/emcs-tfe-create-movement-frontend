package viewmodels.checkAnswers.sections.items

import controllers.routes
import models.{CheckMode, UserAnswers}
import pages.CommercialDescriptionPage
import play.api.i18n.Messages
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import viewmodels.govuk.summarylist._
import viewmodels.implicits._

object CommercialDescriptionSummary  {

  def row(answers: UserAnswers)(implicit messages: Messages): Option[SummaryListRow] =
    answers.get(CommercialDescriptionPage).map {
      answer =>

        SummaryListRowViewModel(
          key     = "commercialDescription.checkYourAnswersLabel",
          value   = ValueViewModel(HtmlFormat.escape(answer).toString),
          actions = Seq(
            ActionItemViewModel("site.change", routes.CommercialDescriptionController.onPageLoad(answers.ern, answers.draftId, CheckMode).url)
              .withVisuallyHiddenText(messages("commercialDescription.change.hidden"))
          )
        )
    }
}
