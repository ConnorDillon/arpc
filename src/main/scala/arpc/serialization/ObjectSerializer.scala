package serialization

import java.io.ObjectOutputStream
import java.nio.ByteBuffer
import nio.ByteBufferOutputStream

trait ObjectSerializer[T] extends Serializer[T] {
  def serialize(obj: T, buffer: ByteBuffer): Unit = {
    val bos = new ByteBufferOutputStream(buffer)
    val oos = new ObjectOutputStream(bos)
    oos.writeObject(obj)
  }
}