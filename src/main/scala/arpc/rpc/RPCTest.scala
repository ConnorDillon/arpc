package rpc

import java.nio.channels.SelectionKey
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}
import nio.{SingleThreadedExecutor, Server, Client}
import serialization.{ObjectSerializer, ObjectDeserializer}
import utils.execute

object RPCTest {
  def apply(): Unit = {
    val pingerStub = new PingerStub
    val pinger = new PingerImpl

    def dispatcherFactory(name: String, stubs: List[Stub], impls: List[Impl])
                         (key: SelectionKey, thread: SingleThreadedExecutor): Dispatcher = {
      new Dispatcher(name, stubs, impls, key, thread)
        with ObjectSerializer[RPC] with ObjectDeserializer[RPC]
    }

    val server = new Server("server", 9999, 4, dispatcherFactory("server", List(), List(pinger)))
    val client = new Client("client", 9999, dispatcherFactory("client", List(pingerStub), List()))
    server.runInThread()
    client.runInThread()

    Thread.sleep(500)

    new Thread(new Runnable {
      def run(): Unit = {
        while (true) {
          val futPong = pingerStub.ping(Ping())

          futPong onComplete {
            case Success(x) => println(s"Ping() - $x")
            case Failure(x) => println(s"error: $x")
          }
          Thread.sleep(1000)
        }
      }
    }).start()

    Thread.sleep(5000)

    server.close()
  }

  trait Pinger extends IDef {
    val name = "Pinger"

    def ping(p: Ping): Future[Pong]

    def tell(msg: Ping): Unit
  }

  class PingerStub extends Pinger with Stub {
    def ping(p: Ping): Future[Pong] = request("ping", p)

    def tell(msg: Ping): Unit = notify("tell", msg)
  }

  class PingerImpl(implicit val ec: ExecutionContext) extends Pinger with Impl {
    serve("ping" -> ping)

    listen("tell" -> tell)

    def ping(p: Ping): Future[Pong] = Future(Pong())

    def tell(msg: Ping): Unit = execute(() => println(msg))
  }

  case class Ping()
  case class Pong()
}