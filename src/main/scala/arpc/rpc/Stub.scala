package arpc.rpc

import scala.concurrent.{Future, Promise}

trait Stub {
  this: IDef =>

  protected var dispatcher: Option[Dispatcher] = None

  def setChannel(d: Dispatcher): Unit = dispatcher = Some(d)

  def disconnect(): Unit = dispatcher = None

  def isOnline: Boolean = dispatcher match {
    case Some(_) => true
    case None => false
  }

  def request[T](service: String, req: Any): Future[T] = dispatcher match {
    case Some(d) => d.request(s"$name.$service", req)
    case None => Promise().failure(new Exception("offline")).future
  }

  def notify(service: String, msg: Any): Unit = dispatcher match {
    case Some(d) => d.write(Notification(s"$name.$service", msg))
    case None => throw new Exception("offline")
  }
}