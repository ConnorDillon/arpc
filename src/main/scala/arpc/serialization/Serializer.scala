package serialization

import java.nio.ByteBuffer

trait Serializer[T] {
  def serialize(obj: T, buffer: ByteBuffer): Unit
}