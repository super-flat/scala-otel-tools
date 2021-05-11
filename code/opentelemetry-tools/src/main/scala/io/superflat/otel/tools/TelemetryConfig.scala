package io.superflat.otel.tools

case class TelemetryConfig(propagators: Seq[String], otlpEndpoint: String, namespace: String, serviceName: String)
