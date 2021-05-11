import Dependencies.Versions
import org.scalafmt.sbt.ScalafmtPlugin.autoImport.scalafmtOnCompile
import sbt.Keys._
import sbt._

object CommonSettings extends AutoPlugin {

  override def trigger = allRequirements

  override def globalSettings =
    Seq(
      scalaVersion := Versions.Scala213,
      organization := "io.superflat",
      organizationName := "Super Flat",
      startYear := Some(2020),
      organizationHomepage := Some(url("https://superflat.io/")),
      homepage := Some(url("https://github.com/super-flat/scala-opentelemetry-tools")),
      scmInfo := Some(ScmInfo(url("https://github.com/super-flat/scala-opentelemetry-tools"), "git@github.com:super-flat/scala-opentelemetry-tools.git")),
      licenses := List("Apache-2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0")),
      developers += Developer(
        "contributors",
        "Contributors",
        "",
        url("https://github.com/super-flat/scala-opentelemetry-tools/graphs/contributors")),
      description := "scala-opentelemetry-tools - Scala shared code for telemetry.\n")

  override def projectSettings =
    Seq(
      scalacOptions ++= Seq(
        "-Xfatal-warnings",
        "-deprecation",
        "-Xlint"
      ),
      resolvers ++= Seq(Resolver.jcenterRepo, Resolver.sonatypeRepo("public"), Resolver.sonatypeRepo("snapshots")),
      scalafmtOnCompile := true,
      // show full stack traces and test case durations
      Test / testOptions += Tests.Argument("-oDF"),
      Test / logBuffered := false,
      Test / fork := true
    )
}
