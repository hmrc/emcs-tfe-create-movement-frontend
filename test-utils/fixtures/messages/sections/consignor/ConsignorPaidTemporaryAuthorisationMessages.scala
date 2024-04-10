package fixtures.messages.sections.consignor

import fixtures.messages.{BaseEnglish, BaseMessages, i18n}

object ConsignorPaidTemporaryAuthorisationMessages {

  sealed trait ViewMessages extends BaseMessages { _: i18n =>
    val heading = "Enter the consignor’s Paid Temporary Authorisation (PTA) code"
    val title = titleHelper(heading)
    val hint = "The PTA contains 13 characters starting with XIPTA. It will be different to the Excise Registration Number you signed in to EMCS with."
    val errorMessageHelper: String => String = s"Error: " + _

    val errorRequired = "Enter the Paid Temporary Authorisation (PTA) code"
    val errorLength = "Paid Temporary Authorisation (PTA) must be 13 characters or less"
    val errorInvalid = "Paid Temporary Authorisation (PTA) must start with XIPTA followed by 8 numbers or mixed letters and numbers"

  }

  object English extends ViewMessages with BaseEnglish
}

