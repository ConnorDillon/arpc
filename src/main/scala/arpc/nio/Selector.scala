package nio

import java.nio.channels.{SelectionKey, Selector=>JavaSelector}

trait Selector extends Runnable {
  val name: String

  val thread = new SingleThreadedExecutor

  def runInThread(): Thread = {
    val t = new Thread(this)
    t.start()
    t
  }

  protected def open(): JavaSelector = JavaSelector.open()

  protected def select(selector: JavaSelector): Unit = {
    val numReady = selector.select()
    println(s"$name - selecting $numReady")
    if (numReady > 0) {
      val selectedKeys = selector.selectedKeys()
      val keyIterator = selectedKeys.iterator()
      while (keyIterator.hasNext) {
        val selectionKey = keyIterator.next()
        if (selectionKey.isValid) {
          if (selectionKey.isReadable) read(selectionKey)
          else if (selectionKey.isWritable) write(selectionKey)
          else if (selectionKey.isAcceptable) accept(selectionKey)
          else if (selectionKey.isConnectable) connect(selectionKey)
        }
        keyIterator.remove()
      }
    }
  }

  protected def accept(key: SelectionKey): Unit

  protected def connect(key: SelectionKey): Unit

  protected def write(key: SelectionKey): Unit = {
    val handler = key.attachment().asInstanceOf[ChannelWriter[_]]
    println(s"$name - initiating write")
    handler.initWrite()
  }

  protected def read(key: SelectionKey): Unit = {
    println(s"$name - initiating read")
    val handler = key.attachment().asInstanceOf[ChannelReader[_]]
    handler.initRead()
  }
}