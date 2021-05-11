import sbt.{Test, _}
import scalapb.compiler.Version.{grpcJavaVersion, scalapbVersion}

/**
 * Holds the list of dependencies used in the project and their various version
 * It gives a central place to quickly update dependencies
 */
object Dependencies {

  /**
   * Versions number
   */
  object Versions {
    val Scala213 = "2.13.5"
    val Scala212 = "2.12.13"
    val ScalaTestVersion = "3.2.8"
    val ScalaMockVersion = "5.1.0"
    val SilencerVersion = "1.7.3"

    val ScalapbCompilerVersion: String = scalapbVersion

    val SbtProtocVersion = "1.0.2"

    val OpenTelemetryVersion: String = "1.0.1"
    val OpenTelemetryGRPCVersion: String = "1.0.1-alpha"
    val OpenTelemetryMetricsVersion: String = "1.0.1-alpha"

    val JavaAgentVersion = "0.1.6"

    val TestContainers: String = "0.39.3"

    val AwaitilityVersion: String = "4.1.0"

    // The version used to build sbt plugin.
    val TargetSbt1 = "1.3.13"
  }

  import Dependencies.Versions._
  val excludeGRPC = ExclusionRule(organization = "io.grpc")

  val Jars = Seq(
    // Opentelemetry
    "io.opentelemetry" % "opentelemetry-api" % OpenTelemetryVersion,
    "io.opentelemetry" % "opentelemetry-sdk" % OpenTelemetryVersion,
    "io.opentelemetry.instrumentation" % "opentelemetry-grpc-1.5" % OpenTelemetryGRPCVersion,
    "io.opentelemetry" % "opentelemetry-extension-trace-propagators" % OpenTelemetryVersion,
    ("io.opentelemetry" % "opentelemetry-exporter-otlp-trace" % OpenTelemetryVersion).excludeAll(excludeGRPC),
    ("io.opentelemetry" % "opentelemetry-exporter-otlp-metrics" % OpenTelemetryMetricsVersion).excludeAll(excludeGRPC),
    "com.thesamet.scalapb" %% "scalapb-runtime-grpc" % scalapbVersion
  )

  /**
   * Test dependencies
   */
  val TestJars: Seq[ModuleID] = Seq(
    // general
    "org.scalatest" %% "scalatest" % Versions.ScalaTestVersion,
    "org.scalamock" %% "scalamock" % Versions.ScalaMockVersion,
    "io.grpc" % "grpc-testing" % grpcJavaVersion % Test,
    "io.opentelemetry" % "opentelemetry-sdk-testing" % OpenTelemetryVersion % Test,
    "org.awaitility" % "awaitility-scala" % AwaitilityVersion % Test
  )
}
