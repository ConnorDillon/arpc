package rpc

import scala.concurrent.Promise
import scala.util.Try

class PromisedRequest[T] {
  val promise = Promise[T]()

  def complete(rep: Try[Any]): Unit = {
    promise complete rep.asInstanceOf[Try[T]]
  }
}