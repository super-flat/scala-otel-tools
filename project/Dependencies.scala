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
    val GrpcJavaVersion: String = grpcJavaVersion
    val ScalaPbVersion: String = scalapbVersion

    val Scala213: String = "2.13.6"
    val ScalaTestVersion: String = "3.2.10"
    val ScalaMockVersion: String = "5.1.0"

    val OpenTelemetryVersion: String = "1.6.0"
    val OpenTelemetryGRPCVersion: String = "1.0.1-alpha"
    val OpenTelemetryMetricsVersion: String = "1.0.1-alpha"

    val AwaitilityVersion: String = "4.1.0"
  }

  import Dependencies.Versions._
  val excludeGRPC = ExclusionRule(organization = "io.grpc")

  val Jars = Seq(
    // gRPC
    "io.grpc" % "grpc-stub" % GrpcJavaVersion % "provided",
    // Opentelemetry
    "io.opentelemetry" % "opentelemetry-api" % OpenTelemetryVersion,
    "io.opentelemetry" % "opentelemetry-sdk" % OpenTelemetryVersion,
    "io.opentelemetry.instrumentation" % "opentelemetry-grpc-1.5" % OpenTelemetryGRPCVersion,
    "io.opentelemetry" % "opentelemetry-extension-trace-propagators" % OpenTelemetryVersion,
    ("io.opentelemetry" % "opentelemetry-exporter-otlp-trace" % OpenTelemetryVersion).excludeAll(excludeGRPC),
    ("io.opentelemetry" % "opentelemetry-exporter-otlp-metrics" % OpenTelemetryMetricsVersion).excludeAll(excludeGRPC)
  )

  /**
   * Test dependencies
   */
  val TestJars: Seq[ModuleID] = Seq(
    // gRPC
    "io.grpc" % "grpc-testing" % GrpcJavaVersion % Test,
    // Protobuf
    "com.thesamet.scalapb" %% "scalapb-runtime-grpc" % ScalaPbVersion % Test,
    // Opentelemetry
    "io.opentelemetry" % "opentelemetry-sdk-testing" % OpenTelemetryVersion % Test,
    // general
    "org.scalatest" %% "scalatest" % Versions.ScalaTestVersion % Test,
    "org.scalamock" %% "scalamock" % Versions.ScalaMockVersion % Test,
    "org.awaitility" % "awaitility-scala" % AwaitilityVersion % Test
  )
}
