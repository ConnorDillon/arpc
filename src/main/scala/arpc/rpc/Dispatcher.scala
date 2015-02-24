package arpc.rpc

import java.nio.ByteBuffer
import java.nio.channels.SelectionKey
import scala.concurrent.Future
import scala.collection.mutable
import arpc.nio.{SingleThreadedExecutor, TCPChannelHandler}
import arpc.serialization.{Deserializer, Serializer}

class Dispatcher(val name: String, stubs: List[Stub], impls: List[Impl],
                 val selectionKey: SelectionKey, val thread: SingleThreadedExecutor)
  extends TCPChannelHandler[RPC] {
  this: Serializer[RPC] with Deserializer[RPC] =>

  stubs foreach (_.setChannel(this))

  val readBuffer = ByteBuffer.allocate(8128)

  val writeBuffer = ByteBuffer.allocate(8128)

  val services: Map[String, (Any => Future[Any])] = impls match {
    case List() => Map()
    case x => x.map(_.services).reduceLeft(_ ++ _)
  }

  val listeners: Map[String, (Any => Unit)] = impls match {
    case List() => Map()
    case x => x.map(_.listeners).reduceLeft(_ ++ _)
  }
  
  protected val promises = mutable.Map[Int, PromisedRequest[_]]()
  
  protected var lastID = 0
  
  def genID: Int = {
    val id = lastID
    lastID += 1
    id
  }
  
  protected def dispatch(msg: RPC): Unit = msg match {
    case Request(id, service, req) =>
      val fut = services(service)(req)
      fut onComplete { rep => write(Reply(id, rep)) }
    case Notification(service, req) =>
      listeners(service)(req)
    case Reply(id, rep) =>
      promises remove id match {
        case Some(p) => p complete rep
        case None => println(s"no request for reply with id: $id")
      }
  }

  def request[T](service: String, req: Any): Future[T] = {
    val id = genID
    val pr = new PromisedRequest[T]
    Future {
      promises(id) = pr
    } onSuccess {
      case _ => write(Request(id, service, req))
    }
    pr.promise.future
  }

  def handleRead(): Unit = read() foreach dispatch

  override def handleClose(): Unit = {
    stubs foreach (_.disconnect())
    super.handleClose()
  }
}
