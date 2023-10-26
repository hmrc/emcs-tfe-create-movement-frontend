package pages

import models.$className$
import play.api.libs.json.JsPath

case object $className$Page extends QuestionPage[$className$] {
  
  override def path: JsPath = JsPath \ toString
  
  override val toString: String = "$className;format="decap"$"
}
