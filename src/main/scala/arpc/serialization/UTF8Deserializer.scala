package arpc.serialization

import java.nio.ByteBuffer
import java.util
import arpc.serialization.Deserializer.Failure

trait UTF8Deserializer extends Deserializer[String] {
  def deserialize(buffer: ByteBuffer): Either[Failure, String] = {
    val slice = util.Arrays.copyOfRange(buffer.array(), buffer.position(), buffer.limit())
    buffer.position(buffer.limit())
    Right(new String(slice, "UTF-8"))
  }
}
