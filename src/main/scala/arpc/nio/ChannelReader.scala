package nio

import java.io.IOException
import java.nio.ByteBuffer
import java.nio.channels.{CancelledKeyException, ClosedChannelException, SelectionKey, NotYetConnectedException}
import serialization.Deserializer
import utils.execute

trait ChannelReader[T] extends ChannelOps {
  this: Deserializer[T] =>

  protected val readBuffer: ByteBuffer

  protected def handleRead(): Unit

  def initRead(): Unit = {
    try {
      selectionKey.interestOps(selectionKey.interestOps() - SelectionKey.OP_READ)
      execute(() => {
        try {
          val bytesRead = channel.read(readBuffer)
          selectionKey.interestOps(selectionKey.interestOps() | SelectionKey.OP_READ)
          selectionKey.selector().wakeup()
          if (bytesRead > 0) {
            readBuffer.flip()
            try {
              handleRead()
            } finally {
              readBuffer.compact()
            }
          } else if (bytesRead == -1) {
            close()
          }
        } catch {
          case _: ClosedChannelException => close()
          case _: IOException => close()
          case _: NotYetConnectedException => ()
          case _: CancelledKeyException => close()
        }
      })
    } catch {
      case _: CancelledKeyException => close()
    }
  }
}