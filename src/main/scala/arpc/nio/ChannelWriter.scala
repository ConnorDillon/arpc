package nio

import java.io.IOException
import java.nio.ByteBuffer
import java.nio.channels.{SelectionKey, ClosedChannelException, CancelledKeyException, NotYetConnectedException}
import serialization.Serializer
import utils.execute

trait ChannelWriter[T] extends ChannelOps {
  this: Serializer[T] =>

  protected val writeBuffer: ByteBuffer

  def write(obj: T): Unit = execute(() => {
    serialize(obj, writeBuffer)
    flush()
    println(s"$name - sent: $obj")
  })

  protected def flush(): Unit = {
    writeBuffer.flip()
    try {
      var done = false
      while (writeBuffer.hasRemaining && !done) {
        val written = channel.write(writeBuffer)
        if (written == 0) {
          selectionKey.interestOps(selectionKey.interestOps() | SelectionKey.OP_WRITE)
          selectionKey.selector().wakeup()
          done = true
        }
      }
    } catch {
      //TODO: do something useful here
      case x: ClosedChannelException => x.printStackTrace()
      case x: IOException => x.printStackTrace()
      case x: NotYetConnectedException => x.printStackTrace()
      case x: CancelledKeyException => x.printStackTrace()
    } finally {
      writeBuffer.compact()
    }
  }

  def initWrite(): Unit = {
    try {
      selectionKey.interestOps(selectionKey.interestOps() - SelectionKey.OP_WRITE)
      execute(flush)
    } catch {
      //TODO: do something useful here
      case x: CancelledKeyException => x.printStackTrace()
    }
  }
}