package io.superflat.otel.tools.helloworld

object GreeterGrpc {
  val METHOD_SAY_HELLO: _root_.io.grpc.MethodDescriptor[io.superflat.otel.tools.helloworld.HelloRequest,
                                                        io.superflat.otel.tools.helloworld.HelloReply
  ] =
    _root_.io.grpc.MethodDescriptor
      .newBuilder()
      .setType(_root_.io.grpc.MethodDescriptor.MethodType.UNARY)
      .setFullMethodName(_root_.io.grpc.MethodDescriptor.generateFullMethodName("helloworld.Greeter", "SayHello"))
      .setSampledToLocalTracing(true)
      .setRequestMarshaller(_root_.scalapb.grpc.Marshaller.forMessage[io.superflat.otel.tools.helloworld.HelloRequest])
      .setResponseMarshaller(_root_.scalapb.grpc.Marshaller.forMessage[io.superflat.otel.tools.helloworld.HelloReply])
      .setSchemaDescriptor(
        _root_.scalapb.grpc.ConcreteProtoMethodDescriptorSupplier.fromMethodDescriptor(
          io.superflat.otel.tools.helloworld.HelloworldProto.javaDescriptor.getServices().get(0).getMethods().get(0)
        )
      )
      .build()

  val SERVICE: _root_.io.grpc.ServiceDescriptor =
    _root_.io.grpc.ServiceDescriptor
      .newBuilder("helloworld.Greeter")
      .setSchemaDescriptor(
        new _root_.scalapb.grpc.ConcreteProtoFileDescriptorSupplier(
          io.superflat.otel.tools.helloworld.HelloworldProto.javaDescriptor
        )
      )
      .addMethod(METHOD_SAY_HELLO)
      .build()

  /**
   * The greeting service definition.
   */
  trait Greeter extends _root_.scalapb.grpc.AbstractService {
    override def serviceCompanion = Greeter

    /**
     * Sends a greeting
     */
    def sayHello(
      request: io.superflat.otel.tools.helloworld.HelloRequest
    ): scala.concurrent.Future[io.superflat.otel.tools.helloworld.HelloReply]
  }

  object Greeter extends _root_.scalapb.grpc.ServiceCompanion[Greeter] {
    implicit def serviceCompanion: _root_.scalapb.grpc.ServiceCompanion[Greeter] = this
    def javaDescriptor: _root_.com.google.protobuf.Descriptors.ServiceDescriptor =
      io.superflat.otel.tools.helloworld.HelloworldProto.javaDescriptor.getServices().get(0)
    def scalaDescriptor: _root_.scalapb.descriptors.ServiceDescriptor =
      io.superflat.otel.tools.helloworld.HelloworldProto.scalaDescriptor.services(0)
    def bindService(serviceImpl: Greeter,
                    executionContext: scala.concurrent.ExecutionContext
    ): _root_.io.grpc.ServerServiceDefinition =
      _root_.io.grpc.ServerServiceDefinition
        .builder(SERVICE)
        .addMethod(
          METHOD_SAY_HELLO,
          _root_.io.grpc.stub.ServerCalls.asyncUnaryCall(
            new _root_.io.grpc.stub.ServerCalls.UnaryMethod[io.superflat.otel.tools.helloworld.HelloRequest,
                                                            io.superflat.otel.tools.helloworld.HelloReply
            ] {
              override def invoke(
                request: io.superflat.otel.tools.helloworld.HelloRequest,
                observer: _root_.io.grpc.stub.StreamObserver[io.superflat.otel.tools.helloworld.HelloReply]
              ): _root_.scala.Unit =
                serviceImpl.sayHello(request).onComplete(scalapb.grpc.Grpc.completeObserver(observer))(executionContext)
            }
          )
        )
        .build()
  }

  /**
   * The greeting service definition.
   */
  trait GreeterBlockingClient {
    def serviceCompanion = Greeter

    /**
     * Sends a greeting
     */
    def sayHello(
      request: io.superflat.otel.tools.helloworld.HelloRequest
    ): io.superflat.otel.tools.helloworld.HelloReply
  }

  class GreeterBlockingStub(channel: _root_.io.grpc.Channel,
                            options: _root_.io.grpc.CallOptions = _root_.io.grpc.CallOptions.DEFAULT
  ) extends _root_.io.grpc.stub.AbstractStub[GreeterBlockingStub](channel, options)
      with GreeterBlockingClient {

    /**
     * Sends a greeting
     */
    override def sayHello(
      request: io.superflat.otel.tools.helloworld.HelloRequest
    ): io.superflat.otel.tools.helloworld.HelloReply = {
      _root_.scalapb.grpc.ClientCalls.blockingUnaryCall(channel, METHOD_SAY_HELLO, options, request)
    }

    override def build(channel: _root_.io.grpc.Channel, options: _root_.io.grpc.CallOptions): GreeterBlockingStub =
      new GreeterBlockingStub(channel, options)
  }

  class GreeterStub(channel: _root_.io.grpc.Channel,
                    options: _root_.io.grpc.CallOptions = _root_.io.grpc.CallOptions.DEFAULT
  ) extends _root_.io.grpc.stub.AbstractStub[GreeterStub](channel, options)
      with Greeter {

    /**
     * Sends a greeting
     */
    override def sayHello(
      request: io.superflat.otel.tools.helloworld.HelloRequest
    ): scala.concurrent.Future[io.superflat.otel.tools.helloworld.HelloReply] = {
      _root_.scalapb.grpc.ClientCalls.asyncUnaryCall(channel, METHOD_SAY_HELLO, options, request)
    }

    override def build(channel: _root_.io.grpc.Channel, options: _root_.io.grpc.CallOptions): GreeterStub =
      new GreeterStub(channel, options)
  }

  def bindService(serviceImpl: Greeter,
                  executionContext: scala.concurrent.ExecutionContext
  ): _root_.io.grpc.ServerServiceDefinition = Greeter.bindService(serviceImpl, executionContext)

  def blockingStub(channel: _root_.io.grpc.Channel): GreeterBlockingStub = new GreeterBlockingStub(channel)

  def stub(channel: _root_.io.grpc.Channel): GreeterStub = new GreeterStub(channel)

  def javaDescriptor: _root_.com.google.protobuf.Descriptors.ServiceDescriptor =
    io.superflat.otel.tools.helloworld.HelloworldProto.javaDescriptor.getServices().get(0)

}
