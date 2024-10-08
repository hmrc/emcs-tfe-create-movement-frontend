import sbt.*

object AppDependencies {

  val playSuffix = "-play-30"
  val hmrcBootstrapVersion = "8.6.0"
  val hmrcMongoVersion = "1.9.0"
  val jsoupVersion = "1.17.2"
  val catsCoreVersion   =  "2.9.0"

  private val compile = Seq(
    play.sbt.PlayImport.ws,
    "uk.gov.hmrc"             %% s"play-frontend-hmrc$playSuffix"               %  "8.5.0",
    "uk.gov.hmrc"             %% s"bootstrap-frontend$playSuffix"               %  hmrcBootstrapVersion,
    "uk.gov.hmrc.mongo"       %% s"hmrc-mongo$playSuffix"                       %  hmrcMongoVersion,
    "org.jsoup"               %   "jsoup"                                       % jsoupVersion,
    "uk.gov.hmrc"             %% s"play-conditional-form-mapping$playSuffix"    %  "2.0.0",
    "org.typelevel"           %%  "cats-core"                                   % catsCoreVersion
  )

  private val test = Seq(
    "uk.gov.hmrc"             %% s"bootstrap-test$playSuffix"         % hmrcBootstrapVersion,
    "uk.gov.hmrc.mongo"       %% s"hmrc-mongo-test$playSuffix"        % hmrcMongoVersion,
    "org.scalamock"           %%  "scalamock"                         % "5.2.0",
    "org.jsoup"               %   "jsoup"                             % "1.17.2"
  ).map(_ % "test, it")

  private val overrides = Seq(
    "com.google.inject" % "guice" % "5.1.0"
  )

  def apply(): Seq[ModuleID] = compile ++ test ++ overrides
}
