package nio

import scala.collection.mutable
import serialization.Deserializer
import Deserializer.{Incomplete, Invalid}
import serialization.Deserializer

trait StreamReader[T] extends ChannelReader[T] {
  this: Deserializer[T] =>

  protected def read(): List[T] = {
    val objects = mutable.ListBuffer[T]()
    var done = false
    var pos = readBuffer.position()
    while (readBuffer.hasRemaining && !done) {
      deserialize(readBuffer) match {
        case Right(obj) =>
          objects.append(obj)
        case Left(Incomplete) =>
          readBuffer.position(pos)
          done = true
        case Left(Invalid) => ()
      }
      pos = readBuffer.position()
    }
    objects.foreach(obj => println(s"$name - received $obj"))
    objects.toList
  }
}