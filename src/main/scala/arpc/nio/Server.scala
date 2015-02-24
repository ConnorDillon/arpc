package arpc.nio

import java.net.InetSocketAddress
import java.nio.channels.{SelectionKey, ServerSocketChannel}
import scala.collection.JavaConversions._

class Server(val name: String, port: Int, poolSize: Int, chFactory: ChannelHandlerFactory) extends Selector {

  val address = new InetSocketAddress(port)

  val threadPool = new SingleThreadedExecutorPool(poolSize)

  protected var stop = false

  def run(): Unit = {
    val selector = open()
    val serverSocketChannel = ServerSocketChannel.open()

    serverSocketChannel.configureBlocking(false)
    serverSocketChannel.bind(address)
    serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT)

    while (!stop) select(selector)

    selector.keys().foreach(_.channel().close())
  }

  def close(): Unit = stop = true

  def accept(key: SelectionKey): Unit = {
    println(s"$name - initiating accept")
    val channel = key.channel().asInstanceOf[ServerSocketChannel]
    val selector = key.selector()
    val socketChannel = channel.accept()
    if (socketChannel != null) {
      println(s"$name - accepted connection from ${socketChannel.getRemoteAddress}")
      socketChannel.configureBlocking(false)
      val key = socketChannel.register(selector, SelectionKey.OP_READ)
      val handler = chFactory(key, threadPool.get)
      key.attach(handler)
    }
  }

  def connect(key: SelectionKey): Unit = ()
}
