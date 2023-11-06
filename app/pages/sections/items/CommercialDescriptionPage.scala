package pages.sections.items

import play.api.libs.json.JsPath

case object CommercialDescriptionPage extends QuestionPage[String] {
  override val toString: String = "commercialDescription"
  override val path: JsPath = JsPath \ toString
}
