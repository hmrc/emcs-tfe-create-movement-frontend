package pages

import models.$className$
import play.api.libs.json.JsPath

case object $className$Page extends QuestionPage[$className$] {
  override val toString: String = "$className;format="decap"$"
  override val path: JsPath = JsPath \ toString
}
