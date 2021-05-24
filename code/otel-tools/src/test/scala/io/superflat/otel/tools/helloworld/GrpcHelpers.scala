package io.superflat.otel.tools.helloworld

import com.google.common.util.concurrent.{FutureCallback, Futures, ListenableFuture, MoreExecutors}
import io.grpc.stub.StreamObserver
import io.grpc.{Status, StatusException, StatusRuntimeException}

import scala.concurrent.{Future, Promise}
import scala.util.Try

object GrpcHelpers {
  def guavaFuture2ScalaFuture[A](guavaFuture: ListenableFuture[A]): Future[A] = {
    val p = Promise[A]()
    Futures.addCallback(
      guavaFuture,
      new FutureCallback[A] {
        override def onFailure(t: Throwable): Unit = p.failure(t)

        override def onSuccess(a: A): Unit = p.success(a)
      },
      MoreExecutors.directExecutor()
    )
    p.future
  }

  def completeObserver[T](observer: StreamObserver[T])(t: Try[T]): Unit =
    t.map(observer.onNext) match {
      case scala.util.Success(_) =>
        observer.onCompleted()
      case scala.util.Failure(s: StatusException) =>
        observer.onError(s)
      case scala.util.Failure(s: StatusRuntimeException) =>
        observer.onError(s)
      case scala.util.Failure(e) =>
        observer.onError(
          Status.INTERNAL.withDescription(e.getMessage).withCause(e).asException()
        )
    }
}
