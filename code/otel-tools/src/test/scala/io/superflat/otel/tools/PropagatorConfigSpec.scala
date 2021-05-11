package io.superflat.otel.tools

import io.opentelemetry.api.baggage.propagation.W3CBaggagePropagator
import io.opentelemetry.api.trace.propagation.W3CTraceContextPropagator
import io.opentelemetry.context.propagation.{ContextPropagators, TextMapPropagator}
import io.opentelemetry.extension.trace.propagation.{B3Propagator, JaegerPropagator, OtTracePropagator}
import io.superflat.otel.mixins.BaseSpec

class PropagatorConfigSpec extends BaseSpec {
  "getPropagator" should {
    "handle tracecontext" in {
      val actual: TextMapPropagator = PropagatorConfig.getPropagator("tracecontext")
      val expected: TextMapPropagator = W3CTraceContextPropagator.getInstance

      expected shouldBe actual
    }
    "handle baggage" in {
      val actual: TextMapPropagator = PropagatorConfig.getPropagator("baggage")
      val expected: TextMapPropagator = W3CBaggagePropagator.getInstance

      expected shouldBe actual
    }
    "handle b3" in {
      val actual: TextMapPropagator = PropagatorConfig.getPropagator("b3")
      val expected: TextMapPropagator = B3Propagator.injectingSingleHeader()

      expected shouldBe actual
    }
    "handle b3multi" in {
      val actual: TextMapPropagator = PropagatorConfig.getPropagator("b3multi")
      val expected: TextMapPropagator = B3Propagator.injectingMultiHeaders()

      expected shouldBe actual
    }
    "handle jaeger" in {
      val actual: TextMapPropagator = PropagatorConfig.getPropagator("jaeger")
      val expected: TextMapPropagator = JaegerPropagator.getInstance

      expected shouldBe actual
    }
    "handle ottracer" in {
      val actual: TextMapPropagator = PropagatorConfig.getPropagator("ottracer")
      val expected: TextMapPropagator = OtTracePropagator.getInstance

      expected shouldBe actual
    }
    "throw a runtime exception on an unrecognized propagator" in {
      intercept[RuntimeException](PropagatorConfig.getPropagator("not-a-propagator"))
    }
  }

  "configurePropagators" should {
    "return a ContextPropagators instance" in {
      val propagators: Seq[String] = Seq("tracecontext")
      val telemetryConfig: TelemetryConfig = TelemetryConfig(propagators, "", "", "")
      val actual: ContextPropagators = PropagatorConfig.configurePropagators(telemetryConfig)
      val expected: ContextPropagators = ContextPropagators.create(PropagatorConfig.getPropagator("tracecontext"))

      expected.getTextMapPropagator shouldBe actual.getTextMapPropagator
    }
  }
}
