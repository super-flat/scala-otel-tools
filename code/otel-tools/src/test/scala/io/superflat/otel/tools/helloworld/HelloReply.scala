package io.superflat.otel.tools.helloworld

import io.grpc.MethodDescriptor.Marshaller

import java.io.{ByteArrayInputStream, InputStream}
import java.nio.charset.StandardCharsets

/**
 * The response message containing the greetings
 */
final case class HelloReply(message: String = "") {
  def withMessage(v: String): HelloReply = copy(message = v)
}

object HelloReply extends Marshaller[HelloReply] {
  override def stream(value: HelloReply): InputStream = new ByteArrayInputStream(value.message.getBytes())

  override def parse(stream: InputStream): HelloReply = {
    val n: Int = stream.available
    val bytes: Array[Byte] = new Array[Byte](n)
    stream.read(bytes, 0, n)

    val s: String = new String(bytes, StandardCharsets.UTF_8)

    HelloReply(s)
  }
}
