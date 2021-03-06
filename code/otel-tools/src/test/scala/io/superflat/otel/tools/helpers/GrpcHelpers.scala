package io.superflat.otel.tools.helpers

import io.grpc.{ ManagedChannel, Metadata }

import java.io.Closeable
import java.util.concurrent.TimeUnit
import scala.collection.mutable

object GrpcHelpers {

  def getHeaders(headers: (String, String)*): Metadata = {
    val metadata: Metadata = new Metadata()
    headers.foreach {
      case (k, v) =>
        metadata.put(Metadata.Key.of(k, Metadata.ASCII_STRING_MARSHALLER), v)
    }
    metadata
  }

  def getStringHeader(headers: Metadata, key: String): String =
    headers.get(Metadata.Key.of(key, Metadata.ASCII_STRING_MARSHALLER))

  class Closeables() {
    val resources: mutable.ListBuffer[Closeable] = mutable.ListBuffer.empty[Closeable]

    def registerChannel(channel: ManagedChannel): ManagedChannel =
      register(channel)

    def register(channel: ManagedChannel): ManagedChannel = {
      val closeable: Closeable = () => {
        channel.shutdownNow()
        channel.awaitTermination(10000, TimeUnit.MILLISECONDS)
      }

      registerCloseable(closeable)

      channel
    }

    def register(server: io.grpc.Server): io.grpc.Server = {
      val closeable: Closeable = () => {
        server.shutdownNow()
        server.awaitTermination(10000, TimeUnit.MILLISECONDS)
      }

      registerCloseable(closeable)

      server
    }

    def registerCloseable[T <: Closeable](closeable: T): T = {
      resources.append(closeable)
      closeable
    }

    def closeAll(): Unit = {
      resources.foreach(_.close())
      resources.clear()
    }
  }

}
