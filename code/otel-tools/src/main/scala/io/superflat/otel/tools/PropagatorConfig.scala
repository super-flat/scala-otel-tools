package io.superflat.otel.tools

import io.opentelemetry.api.baggage.propagation.W3CBaggagePropagator
import io.opentelemetry.api.trace.propagation.W3CTraceContextPropagator
import io.opentelemetry.context.propagation.{ContextPropagators, TextMapPropagator}
import io.opentelemetry.extension.trace.propagation.{B3Propagator, JaegerPropagator, OtTracePropagator}

import scala.jdk.CollectionConverters.SeqHasAsJava

/**
 * PropagatorConfiguration is a helper object that provides for a way to select the trace propagators
 * to be used in the service.
 */
object PropagatorConfig {
  def configurePropagators(config: TelemetryConfig): ContextPropagators = {
    val propagators: Seq[TextMapPropagator] = config.propagators.map(getPropagator)
    ContextPropagators.create(TextMapPropagator.composite(propagators.asJava))
  }

  def getPropagator(name: String): TextMapPropagator = {
    name match {
      case "tracecontext" => W3CTraceContextPropagator.getInstance
      case "baggage"      => W3CBaggagePropagator.getInstance
      case "b3"           => B3Propagator.injectingSingleHeader()
      case "b3multi"      => B3Propagator.injectingMultiHeaders()
      case "jaeger"       => JaegerPropagator.getInstance
      case "ottracer"     => OtTracePropagator.getInstance
      case _ =>
        throw new RuntimeException(s"Unrecognized value for trace propagators: $name")
    }
  }
}
