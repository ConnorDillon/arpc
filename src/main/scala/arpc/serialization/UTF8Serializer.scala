package arpc.serialization

import java.nio.ByteBuffer

trait UTF8Serializer extends Serializer[String] {
  def serialize(str: String, buffer: ByteBuffer): Unit = str.getBytes("UTF-8") foreach buffer.put
}