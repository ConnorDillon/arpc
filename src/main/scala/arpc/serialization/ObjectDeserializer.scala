package serialization

import java.io.{EOFException, ObjectInputStream}
import java.nio.ByteBuffer
import nio.ByteBufferInputStream
import Deserializer.{Failure, Incomplete, Invalid}

trait ObjectDeserializer[T] extends Deserializer[T] {
  def deserialize(buffer: ByteBuffer): Either[Failure, T] = {
    val bis = new ByteBufferInputStream(buffer)
    val ois = new ObjectInputStream(bis)
    try {
      Right(ois.readObject().asInstanceOf[T])
    } catch {
      case _: EOFException => Left(Incomplete)
      case _: Throwable => Left(Invalid)
    }
  }
}