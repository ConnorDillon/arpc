package nio

import serialization.Deserializer
import Deserializer.{Incomplete, Invalid}
import serialization.Deserializer

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