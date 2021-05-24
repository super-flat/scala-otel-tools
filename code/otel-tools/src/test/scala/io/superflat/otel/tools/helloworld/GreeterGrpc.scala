package io.superflat.otel.tools.helloworld

import io.grpc.{CallOptions, Channel, MethodDescriptor, ServerServiceDefinition, ServiceDescriptor}
import io.grpc.stub.{AbstractStub, ClientCalls, ServerCalls, StreamObserver}

import scala.concurrent.{ExecutionContext, Future}

object GreeterGrpc {
  val METHOD_SAY_HELLO: MethodDescriptor[HelloRequest, HelloReply] =
    MethodDescriptor
      .newBuilder()
      .setType(MethodDescriptor.MethodType.UNARY)
      .setFullMethodName(MethodDescriptor.generateFullMethodName("helloworld.Greeter", "SayHello"))
      .setSampledToLocalTracing(true)
      .setRequestMarshaller(HelloRequest)
      .setResponseMarshaller(HelloReply)
      .build()

  val SERVICE: ServiceDescriptor = ServiceDescriptor
    .newBuilder("helloworld.Greeter")
    .addMethod(METHOD_SAY_HELLO)
    .build()

  /**
   * The greeting service definition.
   */
  trait Greeter {

    /**
     * Sends a greeting
     */
    def sayHello(request: HelloRequest): scala.concurrent.Future[HelloReply]
  }

  object Greeter {
    def bindService(serviceImpl: Greeter,
                    executionContext: scala.concurrent.ExecutionContext
    ): ServerServiceDefinition =
      ServerServiceDefinition
        .builder(SERVICE)
        .addMethod(
          METHOD_SAY_HELLO,
          ServerCalls.asyncUnaryCall((request: HelloRequest, observer: StreamObserver[HelloReply]) =>
            serviceImpl
              .sayHello(request)
              .onComplete(GrpcHelpers.completeObserver(observer))(executionContext)
          )
        )
        .build()
  }

  /**
   * The greeting service definition.
   */
  trait GreeterBlockingClient {

    /**
     * Sends a greeting
     */
    def sayHello(request: HelloRequest): HelloReply
  }

  class GreeterBlockingStub(channel: Channel, options: CallOptions = CallOptions.DEFAULT)
      extends AbstractStub[GreeterBlockingStub](channel, options)
      with GreeterBlockingClient {

    /**
     * Sends a greeting
     */
    override def sayHello(request: HelloRequest): HelloReply = {
      ClientCalls.blockingUnaryCall(channel, METHOD_SAY_HELLO, options, request)
    }

    override def build(channel: Channel, options: CallOptions): GreeterBlockingStub =
      new GreeterBlockingStub(channel, options)
  }

  class GreeterStub(channel: Channel, options: CallOptions = CallOptions.DEFAULT)
      extends AbstractStub[GreeterStub](channel, options)
      with Greeter {

    /**
     * Sends a greeting
     */
    override def sayHello(request: HelloRequest): Future[HelloReply] = {
      GrpcHelpers.guavaFuture2ScalaFuture(
        ClientCalls.futureUnaryCall(channel.newCall(METHOD_SAY_HELLO, options), request)
      )
    }

    override def build(channel: Channel, options: CallOptions): GreeterStub =
      new GreeterStub(channel, options)
  }

  def bindService(serviceImpl: Greeter, executionContext: ExecutionContext): ServerServiceDefinition =
    Greeter.bindService(serviceImpl, executionContext)

  def blockingStub(channel: Channel): GreeterBlockingStub = new GreeterBlockingStub(channel)
}
