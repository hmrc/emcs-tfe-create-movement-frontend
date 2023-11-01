package pages

import play.api.libs.json.JsPath

case object $className$Page extends QuestionPage[String] {
  
  override def path: JsPath = JsPath \ toString
  
  override val toString: String = "$className;format="decap"$"
}
