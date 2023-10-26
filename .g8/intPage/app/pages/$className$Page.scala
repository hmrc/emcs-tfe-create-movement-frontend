package pages

import play.api.libs.json.JsPath

case object $className$Page extends QuestionPage[Int] {
  
  override def path: JsPath = JsPath \ toString
  
  override val toString: String = "$className;format="decap"$"
}
