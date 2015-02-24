package arpc.nio

import arpc.serialization.Deserializer
import arpc.serialization.Deserializer.{Incomplete, Invalid}

trait DatagramReader[T] extends ChannelReader[T] {
  this: Deserializer[T] =>

  protected def read(): Option[T] = {
    val prevPos = readBuffer.position()
    deserialize(readBuffer) match {
      case Right(x) =>  Some(x)
      case Left(Invalid) => None
      case Left(Incomplete) =>
        readBuffer.position(prevPos)
        None
    }
  }
}
