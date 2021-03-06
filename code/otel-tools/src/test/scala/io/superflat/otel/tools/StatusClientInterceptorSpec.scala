package io.superflat.otel.tools

import io.grpc.{ ManagedChannel, ServerServiceDefinition, Status }
import io.grpc.inprocess.{ InProcessChannelBuilder, InProcessServerBuilder }
import io.opentelemetry.api.OpenTelemetry
import io.opentelemetry.api.common.{ AttributeKey, Attributes }
import io.opentelemetry.api.trace.Span
import io.opentelemetry.api.trace.propagation.W3CTraceContextPropagator
import io.opentelemetry.context.propagation.ContextPropagators
import io.opentelemetry.instrumentation.grpc.v1_5.GrpcTracing
import io.opentelemetry.sdk.OpenTelemetrySdk
import io.opentelemetry.sdk.testing.exporter.InMemorySpanExporter
import io.opentelemetry.sdk.trace.SdkTracerProvider
import io.opentelemetry.sdk.trace.data.SpanData
import io.opentelemetry.sdk.trace.export.SimpleSpanProcessor
import io.superflat.otel.mixins.BaseSpec
import io.superflat.otel.tools.helloworld.{ GreeterGrpc, HelloReply, HelloRequest }
import io.superflat.otel.tools.helloworld.GreeterGrpc.Greeter
import org.awaitility.Awaitility.await

import java.util.concurrent.TimeUnit
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.jdk.CollectionConverters.ListHasAsScala
import scala.util.Try

class StatusClientInterceptorSpec extends BaseSpec {

  var testExporter: InMemorySpanExporter = _
  var openTelemetry: OpenTelemetry       = _

  override def beforeEach(): Unit = {
    testExporter = InMemorySpanExporter.create
    openTelemetry = OpenTelemetrySdk.builder
      .setTracerProvider(
        SdkTracerProvider.builder.addSpanProcessor(SimpleSpanProcessor.create(testExporter)).build
      )
      .setPropagators(ContextPropagators.create(W3CTraceContextPropagator.getInstance))
      .build()
  }

  "interceptor" should {
    "add gRPC status code to span" in {
      // create a mock server that returns an error status
      val serverName: String   = InProcessServerBuilder.generateName
      val serviceImpl: Greeter = mock[Greeter]
      val errStatus            = Status.NOT_FOUND.withDescription("not found")
      val err: Throwable       = errStatus.asException()
      (serviceImpl.sayHello _).expects(*).returning(Future.failed(err))

      val service: ServerServiceDefinition = Greeter.bindService(serviceImpl, global)

      closeables.register(
        InProcessServerBuilder
          .forName(serverName)
          .directExecutor()
          .addService(service)
          .build()
          .start()
      )

      // create generic opentelemetry gRPC interceptor
      val grpcInterceptor = GrpcTracing.create(openTelemetry).newClientInterceptor()

      // create custom status client interceptor
      val statusInterceptor = new StatusClientInterceptor()

      // create
      val channel: ManagedChannel =
        InProcessChannelBuilder
          .forName(serverName)
          .directExecutor()
          .intercept(grpcInterceptor, statusInterceptor)
          .build()

      closeables.register(channel)

      val stub: GreeterGrpc.GreeterBlockingStub = GreeterGrpc.blockingStub(channel)

      val span: Span = openTelemetry.getTracer("test").spanBuilder("foo").startSpan()

      val scope = span.makeCurrent()

      val response: Try[HelloReply] = Try(stub.sayHello(HelloRequest("foo")))

      scope.close()
      span.end()

      response.isFailure shouldBe true

      testExporter.flush()

      // awaits for the child span
      await()
        .atMost(10, TimeUnit.SECONDS)
        .until(() =>
          testExporter.getFinishedSpanItems.asScala
            .exists(_.getParentSpanId == span.getSpanContext.getSpanId)
        )

      val spans: List[SpanData] = testExporter.getFinishedSpanItems.asScala.toList

      val attributeData: Attributes =
        spans.find(_.getParentSpanId == span.getSpanContext.getSpanId).map(_.getAttributes).get
      attributeData.get(AttributeKey.stringKey("grpc.kind")) shouldBe "client"
      attributeData.get(AttributeKey.stringKey("grpc.status_code")) shouldBe errStatus.getCode
        .name()
      attributeData.get(AttributeKey.stringKey("grpc.ok")) shouldBe "false"

    }
  }
}
