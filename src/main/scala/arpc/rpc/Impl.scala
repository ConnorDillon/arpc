package arpc.rpc

import scala.language.implicitConversions
import scala.concurrent.{ExecutionContext, Future}

//TODO: think of a better name for an implementation
trait Impl {
  this: IDef =>

  type Service = (Any) => Future[Any]

  type Listener = (Any) => Unit

  implicit val ec: ExecutionContext

  protected implicit def serviceConv[Req, Rep](fn: (Req) => Future[Rep]): Service = {
    (rep: Any) => fn(rep.asInstanceOf[Req])
  }

  protected implicit def listnerConv[T](fn: (T) => Unit): Listener = {
    (msg: Any) => fn(msg.asInstanceOf[T])
  }

  protected def serve(services: (String, Service)*): Unit = {
    for ((n, s) <- services) srvs = srvs + (s"$name.$n" -> s)
  }

  protected def listen(listeners: (String, Listener)*): Unit = {
    for ((n, l) <- listeners) lstnrs = lstnrs + (s"$name.$n" -> l)
  }

  private var srvs = Map[String, Service]()

  private var lstnrs = Map[String, Listener]()

  def services = srvs

  def listeners = lstnrs
}