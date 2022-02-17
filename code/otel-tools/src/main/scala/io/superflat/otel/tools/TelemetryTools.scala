package io.superflat.otel.tools

import io.opentelemetry.api.common.{ Attributes, AttributesBuilder }
import io.opentelemetry.context.propagation.ContextPropagators
import io.opentelemetry.exporter.otlp.metrics.OtlpGrpcMetricExporter
import io.opentelemetry.exporter.otlp.trace.OtlpGrpcSpanExporter
import io.opentelemetry.sdk.OpenTelemetrySdk
import io.opentelemetry.sdk.metrics.SdkMeterProvider
import io.opentelemetry.sdk.metrics.`export`.PeriodicMetricReader
import io.opentelemetry.sdk.resources.Resource
import io.opentelemetry.sdk.trace.SdkTracerProvider
import io.opentelemetry.sdk.trace.`export`.BatchSpanProcessor
import io.opentelemetry.semconv.resource.attributes.ResourceAttributes

case class TelemetryTools(telemetryConfig: TelemetryConfig) {

  /**
    * Create an open telemetry SDK and configure it to instrument the service for trace and metrics
    * collection.
    * @return
    *   a OTEL sdk instance with reasonable settings
    */
  def start(): OpenTelemetrySdk = {
    val propagators: ContextPropagators = PropagatorConfig.configurePropagators(telemetryConfig)
    val resource                        = configureResource()
    configureMetricsExporter(resource)
    val tracerProvider: Option[SdkTracerProvider] = configureProvider(resource)
    val sdkBuilder = OpenTelemetrySdk.builder().setPropagators(propagators)
    tracerProvider.map(sdkBuilder.setTracerProvider)

    val sdk: OpenTelemetrySdk = sdkBuilder.build()

    sdk
  }

  /**
    * Configure a metrics exporter.
    * @param resource
    *   a Resource instance representing the instrumented target.
    */
  def configureMetricsExporter(resource: Resource): Unit = {
    val endpoint: String = telemetryConfig.otlpEndpoint
    if (endpoint.nonEmpty) {
      // set up the metric exporter and wire it into the SDK and a timed periodic reader.
      val metricExporter = OtlpGrpcMetricExporter.builder().setEndpoint(endpoint).build();

      val periodicReaderFactory =
        PeriodicMetricReader
          .builder(metricExporter)
          .newMetricReaderFactory();

      val meterProvider =
        SdkMeterProvider.builder
          .setResource(resource)
          .registerMetricReader(periodicReaderFactory)
          .build()

      sys.addShutdownHook {
        meterProvider.shutdown()
      }
    }
  }

  /**
    * Configure the Tracer Provider that will be used to process trace entries.
    *
    * @param resource
    *   a Resource instance representing the instrumented target
    * @return
    *   a Tracer provider configured to export metrics using OTLP
    */
  def configureProvider(resource: Resource): Option[SdkTracerProvider] =
    Option(telemetryConfig.otlpEndpoint)
      .filter(_.nonEmpty)
      .map { endpoint =>
        val providerBuilder = SdkTracerProvider.builder()
        providerBuilder.setResource(resource)

        val exporter = OtlpGrpcSpanExporter.builder.setEndpoint(endpoint).build()

        val processor = BatchSpanProcessor.builder(exporter).build()
        providerBuilder.addSpanProcessor(processor)

        val provider = providerBuilder.build()

        sys.addShutdownHook {
          provider.shutdown()
        }
        provider
      }

  /**
    * Create a resource instance that will hold details of the service being instrumented.
    *
    * @return
    *   a resource representing the current instrumentation target.
    */
  def configureResource(): Resource = {
    val resourceAttributes: AttributesBuilder = Attributes.builder()
    resourceAttributes.put(ResourceAttributes.SERVICE_NAME, telemetryConfig.serviceName)

    Option(telemetryConfig.namespace)
      .filter(_.nonEmpty)
      .foreach(resourceAttributes.put(ResourceAttributes.SERVICE_NAMESPACE, _))

    Resource.getDefault.merge(Resource.create(resourceAttributes.build()))
  }

}
