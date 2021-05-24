package io.superflat.otel.tools.helloworld

import io.grpc.MethodDescriptor.Marshaller

import java.io.{ByteArrayInputStream, InputStream}
import java.nio.charset.StandardCharsets

/**
 * The request message containing the user's name.
 */
final case class HelloRequest(name: String = "") {
  def withName(v: String): HelloRequest = copy(name = v)
}

object HelloRequest extends Marshaller[HelloRequest] {
  override def stream(value: HelloRequest): InputStream = new ByteArrayInputStream(value.name.getBytes())

  override def parse(stream: InputStream): HelloRequest = {
    val n: Int = stream.available
    val bytes: Array[Byte] = new Array[Byte](n)
    stream.read(bytes, 0, n)

    val s: String = new String(bytes, StandardCharsets.UTF_8)

    HelloRequest(s)
  }
}
