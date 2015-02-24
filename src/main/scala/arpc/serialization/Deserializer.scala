package arpc.serialization

import java.nio.ByteBuffer

trait Deserializer[T] {
  import Deserializer._

  def deserialize(buffer: ByteBuffer): Either[Failure, T]
}

object Deserializer {
  trait Failure
  case object Incomplete extends Failure
  case object Invalid extends Failure
}