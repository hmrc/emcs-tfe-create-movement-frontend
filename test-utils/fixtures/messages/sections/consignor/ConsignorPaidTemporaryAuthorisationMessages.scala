package fixtures.messages.sections.consignor

import fixtures.messages.{BaseEnglish, BaseMessages, i18n}

object ConsignorPaidTemporaryAuthorisationMessages {

  sealed trait ViewMessages extends BaseMessages { _: i18n =>
    val heading = "Enter the consignor’s Paid Temporary Authorisation (PTA) code"
    val title = titleHelper(heading)
    val hint = "The PTA contains 13 characters starting with XIPTA. It will be different to the Excise Registration Number you signed in to EMCS with."
  }

  object English extends ViewMessages with BaseEnglish
}

