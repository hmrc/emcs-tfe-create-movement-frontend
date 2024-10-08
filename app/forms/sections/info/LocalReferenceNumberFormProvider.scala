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

import forms.XSS_REGEX
import forms.mappings.Mappings
import forms.sections.info.LocalReferenceNumberFormProvider.LRN_REGEX
import models.requests.DataRequest
import pages.sections.info.LocalReferenceNumberPage
import play.api.data.Form

import javax.inject.Inject

class LocalReferenceNumberFormProvider @Inject() extends Mappings {

  def apply(isDeferred: Boolean)(implicit dataRequest: DataRequest[_]): Form[String] = {
    Form(
      "value" -> text(errMsgForKey("required")(isDeferred))
        .verifying(
          firstError(
            maxLength(22, errMsgForKey("length")(isDeferred)),
            isNotEqualToOptExistingAnswer(LocalReferenceNumberPage().getOriginalAttributeValue, "errors.704.lrn.input"),
            regexpUnlessEmpty(LRN_REGEX, errMsgForKey("invalid")(isDeferred))
          )
        )
    )
  }

  private def errMsgForKey(key: String)(isDeferred: Boolean): String = {
    val infix = if (isDeferred) "deferred" else "new"
    s"localReferenceNumber.$infix.error.$key"
  }
}

object LocalReferenceNumberFormProvider {
  private[info] val LRN_REGEX = XSS_REGEX.replaceAll(";:", "")
}
