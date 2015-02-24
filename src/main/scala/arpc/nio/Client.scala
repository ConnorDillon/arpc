package arpc.nio

import java.net.InetSocketAddress
import java.nio.channels.{SelectionKey, SocketChannel}

class Client(val name: String, port: Int, chFactory: ChannelHandlerFactory) extends Selector {

  val address = new InetSocketAddress(port)

  def run(): Unit = {
    val selector = open()
    val socket = SocketChannel.open()
    socket.configureBlocking(false)
    socket.register(selector, SelectionKey.OP_CONNECT)

    while (true) select(selector)
  }

  protected def connect(key: SelectionKey): Unit = {
    println(s"$name - initiating connect")
    val socket = key.channel().asInstanceOf[SocketChannel]
    if (!socket.connect(address)) {
      while (socket.isConnectionPending) socket.finishConnect()
    }
    key.interestOps(SelectionKey.OP_READ)
    val handler = chFactory(key, new SingleThreadedExecutor)
    key.attach(handler)
  }

  protected def accept(key: SelectionKey): Unit = () // log error
}
