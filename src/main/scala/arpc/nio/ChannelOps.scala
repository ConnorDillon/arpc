package nio

import java.nio.channels.{SelectionKey, SocketChannel}
import scala.concurrent.ExecutionContext

trait ChannelOps {
  val name: String

  protected val thread: SingleThreadedExecutor

  protected val selectionKey: SelectionKey

  protected val channel = selectionKey.channel().asInstanceOf[SocketChannel]

  protected implicit val ec = ExecutionContext.fromExecutorService(thread)

  protected def handleClose(): Unit = println(s"$name - connection closed by peer")

  def close(): Unit = {
    if (channel.isOpen) channel.close()
    handleClose()
  }
}