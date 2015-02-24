package arpc.rpc

import scala.util.Try

sealed trait RPC

case class Request(id: Int, service: String, req: Any) extends RPC
case class Reply(id: Int, rep: Try[Any]) extends RPC
case class Notification(listner: String, msg: Any) extends RPC